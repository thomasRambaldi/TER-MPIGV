package server;


import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;


import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tools.Slide;

/**
 * Le slide handler va permettre la manipulation de plusieurs diapositives
 * Disposant de toutes les diapositives il a des fonctions qui lui permette de passer à la diapositive suivante  
 * ou à une diapositive particulière 
 * Il a une fonction qui utilise la bibliothèque PDFRenderer afin de découper un fichier pdf en plusieurs images
 */
public class SlideHandler {
	/*	Le numéro de  la diapositive courante*/
	private int numberOfCurrentSlide;
	/* La liste de toutes les diapositives */
	private ArrayList<Slide> slides;
	/* L'imageView de javafx qui permet d'afficher une diapositive sur l'interface graphique*/
	private ImageView imageView;

	
	/**
	 * Le constructeur de la classe SlideHandler
	 * @param slides : la liste de diapositives(ou slide)
	 * @param imageView : l'imageView utilisée avec javafx
	 */
	public SlideHandler(ArrayList<Slide> slides, ImageView imageView){
		numberOfCurrentSlide = 0;
		this.slides = slides;
		this.imageView = imageView;
	}

	/**
	 * Permet de transformer un fichier pdf en plusieurs images et de les stocker dans chaque slide 
	 * de la liste de slides
	 * @param fileName : le fichier pdf à découper
	 * @throws IOException
	 */
	public void pdfToImages(String fileName) throws IOException {
		/*Load a pdf from a byte buffer*/
		RandomAccessFile raf = new RandomAccessFile(new File(fileName), "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		PDFFile pdffile = new PDFFile(buf);
		
		for(int i=1; i <= pdffile.getNumPages(); i++)
			addPDFpageToSlideArray(pdffile, i);
		raf.close();
	}

	private void addPDFpageToSlideArray(PDFFile pdffile, int index) {
		PDFPage page = pdffile.getPage(index);

		//get the width and height for the doc at the default zoom 
		Rectangle rect = new Rectangle(0,0,
				(int) page.getBBox().getWidth(),
				(int) page.getBBox().getHeight());
		
		/*  C'est ici que s'effectue la redimension de l'image, ici elle est a 1920 par 1080*/
		double redim = 1.5;
		double width = 1280*redim;
		double height = 720*redim;
		BufferedImage img = (BufferedImage) page.getImage(
				(int) width, (int) height, 
				rect, // clip rect
				null, // null for the ImageObserver
				false, // fill background with white
				true  // block until drawing is done
				);
		slides.get(index-1).setImage(SwingFXUtils.toFXImage(img, null));
	}

	/**
	 * Permet de passer à la diapo suivante
	 */
	public void nextSlide(){
		incrementNumberOfCurrentSlide();
		System.out.println(numberOfCurrentSlide);
		imageView.setImage(slides.get(numberOfCurrentSlide).getImage());
	}

	/**
	 * Permet de passer à la diapo précédente
	 */
	public void prevSlide(){
		decrementNumberOfCurrentSlide();
		imageView.setImage(slides.get(numberOfCurrentSlide).getImage());
	}
	
	/**
	 * Permet d'aller à une diapositive particulière via son numéro
	 * @param numberOfSlide
	 */
	public void goToSlide(int numberOfSlide){
		if(numberOfSlide < 0 || numberOfSlide >= slides.size()){
			System.err.println("Number of diapo is out of bounds (0, "+(slides.size()-1)+") : "+numberOfSlide);
			return;
		}	
		numberOfCurrentSlide = numberOfSlide;
		imageView.setImage(slides.get(numberOfCurrentSlide).getImage());
	}
	
	/**
	 * Charge le pdf et s'occupe de le découper en plusieurs images 
	 * @param filename : le nom du fichier pdf
	 */
	public void loadPdf(String filename){
		try {
			pdfToImages(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retourne la liste de diapositive de la présentation
	 * @return la liste d'image de la présentation
	 */
	public ArrayList<Slide> getSlides(){
		return slides;
	}
	
	/**
	 * Retourne une diapositive particulière
	 * @param index : le numéro de la diapositive
	 * @return la slide correspondant à l'index
	 */
	public Slide getSlide(int index){
		if(index < 0 || index > slides.size()) return null ;
		
		for(int i = 0 ; i < slides.size() ; i++)
			if( i == index ) return slides.get(i);
		
		return null;
	}
	
	/**
	 * Retourne l'image correspondant à la slide numéro index
	 * @param index : le numéro de la slide
	 * @return l'image de la slide numéro index
	 */
	public Image getImage(int index){
		if(index < 0 || index > slides.size()) return null ;
		
		for(int i = 0 ; i < slides.size() ; i++)
			if( i == index ) return slides.get(i).getImage();
		
		return null;
	}
	
	/**
	 * 
	 * @return l'imageView de l'interface graphique
	 */
	public ImageView getImageView(){
		return imageView;
	}
	
	/**
	 * Change l'imageView en une nouvelle
	 * @param imageView
	 */
	public void setImageView(ImageView imageView){
		this.imageView = imageView;
	}
	
	
	/**
	 * Remplace la liste de diapositive 
	 * @param images nouvelle liste d'image
	 */
	public void setSlides(ArrayList<Slide> slides){
		this.slides = slides;
	}
	
	/**
	 * Remplace le numéro de slide courante, mais ne change pas la slide
	 * @param numberImage
	 */
	public void setNumberOfCurrentSlide(int numberImage){
		this.numberOfCurrentSlide = numberImage;
	}
	
	public int getNumberOfCurrentSlide(){
		return numberOfCurrentSlide;
	}
	
	/**
	 * Retourne la diapositive courante
	 * @return
	 */
	public Slide getCurrentSlide(){
		return slides.get(numberOfCurrentSlide);
	}
	
	/**
	 * Incremente le nombre de la diapositive courante de manière sécurisée (pas de out of bounds)
	 */
	public void incrementNumberOfCurrentSlide(){
		numberOfCurrentSlide = (numberOfCurrentSlide+1) == slides.size() ? 0 : numberOfCurrentSlide+1;
	}
	
	/**
	 * Décremente le nombre de la diapositive courante de manière sécurisée (pas de out of bounds)
	 */
	public void decrementNumberOfCurrentSlide(){
		numberOfCurrentSlide = (numberOfCurrentSlide-1) == -1 ? slides.size()-1 : numberOfCurrentSlide-1;
	}
}
