package tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Cette classe va permettre la lecture d'un fichier latex en stockant les informations nécessaire à notre application
 */

public class LatexTools {
	
	
	/**
	 * Lit un fichier latex et en récupère toutes les info des diapositives soit le titre et les commentaires
	 * @param latexFileName : le nom du fichier latex
	 * @return la liste de diapositives lue
	 * @throws IOException
	 */
	public static ArrayList<Slide> latexToSlide(String latexFileName) throws IOException{
		BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(latexFileName)));
		ArrayList<Slide> slideArray = new ArrayList<Slide>();
		String line;
		boolean isAnimation=false;
			
		while ((line = file.readLine()) != null) {
			if (line.contains("begin{frame}") /*|| line.equals("{")*/) {
				Slide slide = new Slide("", "","", null);

				if ( (line.contains("begin{frame}{")))
					slide.setTitre(getTitleFromBeginFrame(line));
				else
					slide.setTitre("Frame Without Title");
				
				while(! (line = file.readLine()).contains("end{frame}") /*&& !(line.equals("}") )*/){
					
					if(lineContainsAnimation(line)){
						slideArray.add(slide);
						slide= new Slide("", "",slide.getTitre(), null);
						isAnimation=true;
					}
						
					if ( line.contains("frametitle") )
						slide.setTitre(getTitleFromFrameTitle(line));
					if(line.contains("titlepage"))
						continue;
					slide = getTextAndCommentFromFrame(line, slide);
				}
				if(!isAnimation){
					slideArray.add(slide);
				}
				isAnimation=false;
			}
		}
		file.close();
		return slideArray;
	}

	/**
	 * Détecte si une ligne contient une instruction d'animation
	 * @param line : la ligne en train d'être lue
	 * @return true si c'est une animation à été detecté false sinon
	 */
	private static boolean lineContainsAnimation(String line) {
		for(int i=0; i<line.length()-3;i++)
			if(line.charAt(i)=='<' && line.charAt(i+2)=='-' && line.charAt(i+3)=='>')
				return true;
		return false;
	}

	/**
	 * Récupère tous les commentaires et le texte d'une diapositive
	 * @param line : La ligne courante lors de la lecture du fichier latex
	 * @param slide: Le slide courant lors de la lecture du fichier latex
	 * @return le slide mis à jour avec le texte et les commentaires
	 */
	private static Slide getTextAndCommentFromFrame(String line, Slide slide) {
		StringBuilder  commentaire = new StringBuilder("");
		StringBuilder texte = new StringBuilder("");
		boolean isText = true;
		
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i)== '%'){
				isText = false;
				continue;
			}	
			if(isText)
				texte.append(line.charAt(i));
			else
				commentaire.append(line.charAt(i));		
		}
		if(! commentaire.toString().equals("")){
			slide.addCommentaires(commentaire.toString());
		}	
		if(! texte.toString().equals("")){
			slide.addTexte(texte.toString());
		}
		return slide;
	}
	
	/**
	 * getTitleFromFrameTitle: Récupère le titre d'une frame comportant l'instruction /frametitle
	 * @param line: La ligne courante lors de la lecture du fichier latex
	 * @return le titre récupéré
 	 */

	private static String getTitleFromFrameTitle(String line) {
		StringBuilder titre = new StringBuilder("");
		boolean isTitle = false;
		for (int i = 0; i < line.length()-2; i++) {
			if ((line.charAt(i)== '{'))
				isTitle=true;
			if (isTitle)
				titre.append(line.charAt(i+1));
		}
		return titre.toString();
	}

	/**
	 * getTitleFromBeginFrame: Récupère le dernier titre de la frame
	 * syntaxe:  begin{frame}{TITRE}{Titredeux}{DernierTitre}
	 * @param line : La ligne courante lors de la lecture du fichier
	 * @return le titre de la frame courante
	 */
	private static String getTitleFromBeginFrame(String line) {
		StringBuilder titre = new StringBuilder("");
		int indexOfLastOpenAccolades = 0;
		int indexOfLastClosedAccolades = 0;
		
		for (int i = 0; i < line.length(); i++){ 
			if ((line.charAt(i)== '{'))
				indexOfLastOpenAccolades=i;
			if ((line.charAt(i)== '}'))
				indexOfLastClosedAccolades=i;
		}	
				
		for(int i=indexOfLastOpenAccolades+1; i<indexOfLastClosedAccolades;i++)
			titre.append(line.charAt(i));
		return titre.toString();
	}
}

