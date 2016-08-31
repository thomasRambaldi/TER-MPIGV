package server;

import java.net.SocketException;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

/**
 * Voici le serveur de la présentation qui va s'occuper de changer de diapositive selon les 
 * informations reçues par le client de la démo de kaldi
 * Le serveur utilise le protocole OSC et reçoit sur le port 9001 à l'adresse IP 127.0.0.1
 */
public class Server{
	private SlideHandler slideHandler;
	private String[] commentsOfCurrentSlide; 
	private String entireMessage;
	private OSCPortIn receiver;
	private boolean isEnglishPresentation;
	
	/**
	 * Le constructeur de la classe serveur
	 * @param slideHandler : le slide handler de la présentation
	 * @param isEnglishPresentation : c'est une présentation anglaise ? Si non c'est une française
	 */
	public Server(SlideHandler slideHandler, boolean isEnglishPresentation){
		this.slideHandler = slideHandler;
		entireMessage = "";
		commentsOfCurrentSlide = this.slideHandler.getCurrentSlide().getCommentaires().split(" ");
		this.isEnglishPresentation=isEnglishPresentation;
	}

	/**
	 * La fonction qui va lancer le serveur sur le port 9001 à l'adresse IP 127.0.0.1
	 * Elle va créer un listener dans lequel sera ajouter tout le code du serveur c'est à dire 
	 * tous ce qu'il derva faire quand il sera en marche soit analyser les données du client 
	 * afin de décider de l'action à faire (changer de diapo etc...)
	 * @throws SocketException
	 */
	public void receive() throws SocketException{
		receiver = new OSCPortIn(9001);
		OSCListener listener = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				String messageReceived = (String)message.getArguments()[0];
				if(isEnglishPresentation)
					globalEnglishKeyWord(messageReceived);
				else
					globalFrenchKeyWord(messageReceived);
				version3(messageReceived);
				System.out.println(messageReceived);
			}
		};
		receiver.addListener("/test", listener);
		receiver.startListening();
	}
	
	/**
	 * La fonction qui ferme brutalement le serveur
	 * Nous n'avons pas trouvé d'autres alternatives pour le fermer sans générer d'erreurs
	 */
	public void close(){
		if (! receiver.equals(null)) {
			receiver.stopListening();
			receiver.close();
			System.out.println("Le serveur a été fermé");
		}
	}
	
	/**
	 * La fonction qui va appliquer la version 3 de notre projet soit essayer passer à la diapositive suivante en analysant 
	 * le texte que l'utilisateur énonce, soit sans mots-clefs.
	 * 
	 * Pour cela nous utilisons une variable entireMessage qui contient tout ce qu'a dis l'orateur sur la 
	 * diapositive courante
	 * Nous utilisons aussi une variable commentsOfCurrentSlide qui correspond à un tableau de string
	 * qui contient tous les commentaires de la diapo courante (le string contenant tous les commentaires est splité
	 * par les espaces, ce qui peut provoquer des dysfonctionnement notamment au niveau des apostrophes car par exemple
	 * "l'animation" sera considéré comme un seul mot, or quand kaldi détecte ce mot il l'écrit "l' animation" soit en deux mots
	 * c'est une erreur à savoir pour de futures améliorations)
	 * 
	 * Tous ce que nous faisons c'est regarder combien de mots de la diapo courante sont contenus dans le message entier
	 * puis si on trouve qu'il y a 75% de mots qui sont contenus alors nous passons à la slide suivante   
	 * @param messageReceived : le message reçu par le serveur
	 */
	public void version3(String messageReceived){
		//-- Detecter si les 3/4 des mots de la diapo courante ont été dis, si oui on passe a la diapo suivante
		entireMessage+=messageReceived;
		int numberOfCorrespondance = 0;
		
		for(String comment : commentsOfCurrentSlide)
			if(entireMessage.contains(comment)){
				numberOfCorrespondance++;	
			}	
		
		if(numberOfCorrespondance > 3*commentsOfCurrentSlide.length/4 && commentsOfCurrentSlide.length!=0){ 
			slideHandler.nextSlide();
			initVersion3();
		}
		
		//System.out.println(numberOfCorrespondance+" < "+3*commentsOfCurrentSlide.length/4);
	}
	
	/**
	 * Cette fonction va réinitialiser les variables que l'on utilise pour la version3
	 */
	private void initVersion3(){
		entireMessage = "";
		if(slideHandler.getCurrentSlide().getCommentaires().equals(""))
			commentsOfCurrentSlide= new String[]{};
		else commentsOfCurrentSlide = slideHandler.getCurrentSlide().getCommentaires().split(" ");
	}
	
	/**
	 * Cette fonction va détecter les mots-clef permettant d'aller à la diapositive suivante ou précédante
	 * ou a une diapositive particulière via son titre pour une présentation française 
	 * @param messageReceived : le message reçu par le serveur
	 */
	private void globalFrenchKeyWord(String messageReceived){
		if(messageReceived.contains("passer a la diapositive suivante")){
			slideHandler.nextSlide();
			initVersion3();
			return;
		}else if(messageReceived.contains("passer a la diapositive precedente")){
			slideHandler.prevSlide();
			initVersion3();
			return;
		}
		
		for(int i=0; i<slideHandler.getSlides().size(); i++){
			if(messageReceived.contains("aller a la diapositive "+slideHandler.getSlides().get(i).getTitre())){
				slideHandler.goToSlide(i);
				initVersion3();
				return;
			}	
		}	
	}
	
	/**
	 * Cette fonction va détecter les mots-clef permettant d'aller à la diapositive suivante ou précédante
	 * ou a une diapositive particulière via son titre pour une présentation anglaise  
	 * @param messageReceived : le message reçu par le serveur
	 */
	private void globalEnglishKeyWord(String messageReceived){
		if(messageReceived.contains("go to the next slide")){
			slideHandler.nextSlide();
			initVersion3();
			return;
		}else if(messageReceived.contains("go to the previous slide")){
			slideHandler.prevSlide();
			initVersion3();
			return;
		}
		
		for(int i=0; i<slideHandler.getSlides().size(); i++){
			if(messageReceived.contains("go to the slide "+slideHandler.getSlides().get(i).getTitre())){
				slideHandler.goToSlide(i);
				initVersion3();
				return;
			}	
		}	
	} 
	
	
	/*
	 * PROBLEME pour le francais
	 * le message recu par javaosc ne detecte pas les arguments à accents comme èéà...
	 * à la place il met le même caracatère : ￃﾩ
	 * Donc il faut résoudre ce problème en utilisant peut être une autre librairie osc pour java
	 * En attendant tous les accents sont remplacés : é -> e à -> a  etc...
	 */
}
