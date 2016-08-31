package server;

import java.net.SocketException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tools.LatexTools;
import tools.Slide;

/**
 * Le main qui va lancer la présentation et le serveur à partir du fichier tex en anglais ou en français
 * les paramètres sont en premier "english" ou "french" puis le nom de votre fichier sans l'extension
 *
 */
public class Main extends Application{
	private Server s;
	
	/**
	 * Le main qui va lancer javafx avec launch qui va avoir pour effet de lancer la fonction start()
	 * @param args <"english" | "french"> <"votre_fichier_sans_lextension">
	 */
	public static void main(String[] args){
		launch(args);
	}

	/**
	 * La fonction start() va créer un slide handler (manipulateur de diapositive) et un serveur
	 * Puis va lancer ce serveur et enfin va lancer javafx pour afficher la première diapositive 
	 * a partir du slide handler
	 * Pour récupérer les arguments on peut utiliser la fonction String getParameters().getRaw().get(number);
	 */
	@Override
	public void start(final Stage primaryStage) throws Exception {
		String fileName = getParameters().getRaw().get(1);
		ArrayList<Slide> slides = LatexTools.latexToSlide(fileName+".tex");
		
		final SlideHandler sh = new SlideHandler(slides, null);
		sh.loadPdf(fileName+".pdf");
		initImageView(primaryStage, sh);

		final VBox vbox = new VBox();
		vbox.getChildren().add(sh.getImageView());
		initServer(sh, getParameters().getRaw().get(0));
		
		Scene scene = new Scene(vbox, 1280, 720);
		addAllEventsHandler(primaryStage, scene, vbox, sh);
		primaryStage.setTitle(fileName+".pdf");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Cette fonction va avoir pour effet d'initialiser le serveur, selon le string envoyé 
	 * elle va lancer un serveur de présentation française ou bien anglaise 
	 * @param sh : le slide handler de la présentation
	 * @param englishOrFrench : "english" ou "french" afin de choisir la langue de la présentation
	 */
	private void initServer(SlideHandler sh, String englishOrFrench) {
		switch(englishOrFrench){
		case "english": s = new Server(sh, true); break;
		case "french" : s = new Server(sh, false); break;
		default : System.err.println("Usage : ant -DenglishOrFrench=<\"english\" or \"french\"> -DfileTex=<your filename without extension> \"Start Presentation\"");
		}
		try {
			s.receive();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * La fonction appelée par javafx lorsque l'on ferme l'application
	 * C'est la que nous fermons le serveur brutalement, ce qui a pour effet de générer une erreur. 
	 */
	public void stop(){
		s.close();
	}
	
	/**
	 * Cette fonction va initialiser une imageView aux longueurs et largeurs de la fenêtre,
	 * puis va la stocker dans le slide handler passé en paramètre 
	 * @param primaryStage : le stage de la fonction start() de javafx
	 * @param sh : le slide handler de la présentation
	 */
	private void initImageView(Stage primaryStage, SlideHandler sh){
		final ImageView imageView = new ImageView(sh.getImage(0));
		imageView.setPreserveRatio(true);
		imageView.fitWidthProperty().bind(primaryStage.widthProperty()); 
		imageView.fitHeightProperty().bind(primaryStage.heightProperty());
		sh.setImageView(imageView);
	}
	
	/**
	 * Cette fonction va ajouter tous les évenements (souris, clavier, molette..) à gérer  
	 * @param primaryStage : le stage de la fonction start() de javafx
	 * @param scene : la scene de l'interface 
	 * @param vbox : la vbox utilisée par l'application
	 * @param sh : le slide handler de la présentation
	 */
	private void addAllEventsHandler(Stage primaryStage, Scene scene, VBox vbox, SlideHandler sh){
		addKeyEventsHandler(primaryStage, scene, sh);
		addMouseEventsHandler(primaryStage, scene, vbox);
	}
	
	/**
	 * Cette fonction va ajouter tous les évenements clavier 
	 * @param primaryStage : le stage de la fonction start() de javafx
	 * @param scene : la scene de l'interface 
	 * @param sh : le slide handler de la présentation
	 */
	private void addKeyEventsHandler(Stage primaryStage, Scene scene, SlideHandler sh){
		scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
			switch(key.getCode()){
			case LEFT  : sh.prevSlide();break;
			case RIGHT : sh.nextSlide();break;
			case F5    : primaryStage.setFullScreen(true); break;
			default:   break;
			}
		});
	}
	
	/**
	 * Cette fonction va ajouter tous les évenements de la souris(clic, dragged, molette)
	 * @param primaryStage : le stage de la fonction start() de javafx
	 * @param scene : la scene de l'interface 
	 * @param vbox : la vbox utilisée par l'application
	 */
	private void addMouseEventsHandler(Stage primaryStage, Scene scene, VBox vbox){
		addMouseClickedEvents (primaryStage, scene, vbox);
		//addMouseDraggedEvents (primaryStage, scene, vbox);
		addMouseScrolledEvents(primaryStage, scene, vbox);
	}
	
	/**
	 * Cette fonction va ajouter tous les évenements clics de la souris
	 * C'est ici que nous gérons le déplacement de l'image (case PRIMARY)
	 * mais ce n'est pas encore tout a fait fonctionnel c'est pourquoi c'est en commentaire
	 * 
	 * @param primaryStage : le stage de la fonction start() de javafx
	 * @param scene : la scene de l'interface 
	 * @param vbox : la vbox utilisée par l'application
	 */
	private void addMouseClickedEvents(Stage primaryStage, Scene scene, VBox vbox) {
		scene.addEventHandler(MouseEvent.MOUSE_CLICKED, (buttonPressed) -> {
			switch(buttonPressed.getButton()){
			case PRIMARY : 
				/*vbox.setTranslateX(buttonPressed.getSceneX() - vbox.getWidth()/2); 
				vbox.setTranslateY(buttonPressed.getSceneY() - vbox.getHeight()/2); */break;
			case MIDDLE  : vbox.setScaleX(1);
			vbox.setScaleY(1);
			break;
			default:   break;
			}
		});
		
	}
	
	/**
	 * Cette fonction va ajouter tous les évenements drag de la souris (souris cliquée puis déplacée tout en maintenant le clic)
	 * 
	 * @param primaryStage : le stage de la fonction start() de javafx
	 * @param scene : la scene de l'interface 
	 * @param vbox : la vbox utilisée par l'application
	 */
	private void addMouseDraggedEvents(Stage primaryStage, Scene scene, VBox vbox) {
		scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, (buttonPressed) -> {
			switch(buttonPressed.getButton()){
			case PRIMARY : System.out.println(vbox.getBoundsInParent().getMinX()+ " "+vbox.getBoundsInParent().getMinY());
			vbox.setTranslateX(buttonPressed.getSceneX() - vbox.getWidth()/2); 
			vbox.setTranslateY(buttonPressed.getSceneY() - vbox.getHeight()/2); break;
			default:   break;
			}
		});
		
	}

	/**
	 * Cette fonction va ajouter tous les évenements molette de la souris
	 * C'est ici qu'est géré le zoom 	 
	 * @param primaryStage : le stage de la fonction start() de javafx
	 * @param scene : la scene de l'interface 
	 * @param vbox : la vbox utilisée par l'application
	 */
	private void addMouseScrolledEvents(Stage primaryStage, Scene scene, VBox vbox) {
		scene.addEventHandler(ScrollEvent.ANY, (scrollEvent) -> {
			double deltaY = scrollEvent.getDeltaY();
			double zoomFactor = deltaY > 0 ? 1.05 : 0.95;
			vbox.setScaleX(vbox.getScaleX() * zoomFactor);
			vbox.setScaleY(vbox.getScaleY() * zoomFactor);
			scrollEvent.consume();
		});
		
	}
}
