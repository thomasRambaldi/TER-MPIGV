package tools;

import javafx.scene.image.Image;

/**
 * La classe Slide représente une diapositive de la présentation
 * Elle contient les commentaires associés à celle-ci (ceux situés entre le begin{frame} et le end{frame} du fichier tex
 * Elle contient le texte affiché sur la diapositive 
 * Elle contient le titre de la diapositive
 * Et elle contient également l'iamge de la diapositive que l'on peut voir sur le pdf
 */

public class Slide {

	private String commentaires;
	private String texte;
	private String titre;
	private Image image;

	public Slide(String commentaires, String texte,String titre ,Image image ){
		this.commentaires = trim(commentaires);
		this.texte = texte;
		this.titre = titre;
		this.image = image;
	}
	
	/**
	 * Ajout d'un commentaire
	 * le commentaire va subir une modification, tous les symboles interdits seront enlevés
	 * voir la fonction trim() pour voir les symboles enlevés
	 * @param commentaires : commentaires à ajouter
	 */
	public void addCommentaires(String commentaires) {
		this.commentaires += trim(" "+commentaires);
	}

	/**
	 * Ajouter du texte
	 * @param texte à ajouter
	 */
	public void addTexte(String texte) {
		this.texte += texte;
	}
	
	/**
	 * Retourne le titre de la diapositive
	 * @return le titre de la diapositive
	 */
	public String getTitre() {
		return titre;
	}

	/**
	 * Remplace le titre de la diapositive.
	 * @param titre : le nouveau nom de la frame qui sera mis en minuscule et ne disposera pas d'accents
	 */
	public void setTitre(String titre) {
		this.titre = titre.toLowerCase();
		this.titre = this.titre.replace('é', 'e');
		this.titre = this.titre.replace('è', 'e');
		this.titre = this.titre.replace('à', 'a');
		this.titre = this.titre.replace('ê', 'e');
		this.titre = this.titre.replace('â', 'a');
	}

	/**
	 * Retourne les commentaires
	 * @return les commentaires
	 */
	public String getCommentaires() {
		return commentaires;
	}

	/**
	 * Remplaces les commentaires par celui du paramètres
	 * @param commentaires nouveau commentaire
	 */
	public void setCommentaires(String commentaires) {
		this.commentaires = commentaires;
	}

	/**
	 * Retourne le texte
	 * @return le texte
	 */
	public String getTexte() {
		return texte;
	}

	/**
	 * Remplace le texte par celui en paramètre
	 * @param texte nouveau texte
	 */
	public void setTexte(String texte) {
		this.texte = texte;
	}

	/**
	 * Retourne l'image de la diapositive
	 * @return l'image de la diapositive
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Remplace l'image par celle qui est en paramètre
	 * @param image : nouvelle image
	 */
	public void setImage(Image image) {
		this.image = image;
	}
	
	/**
	 * Cette fonction va enlevé tous les caractères interdits commes les virgules, les points...
	 * Car le créateur de modèles de langages ne peut parfois pas fonctionner avec ces caractères
	 * Les accents sont également remplacé mais ceci est temporaire car cest a cause de JavaOSC qui ne permet 
	 * pas de recevoir les accents, cela complique donc le changement de diapositive avec les titres ou le texte
	 * car les caractères avec accents ne pourront êtres détectés
	 * @param commentaires : le commentaire qu'il faut modifier
	 * @return le commentaire modifié
	 */
	private String trim(String commentaires){
		String newComment = commentaires;
	    newComment = newComment.replace(".", "");
	    newComment = newComment.replace(",", "");
	    newComment = newComment.replace("/", " ");
	    newComment = newComment.replace("(", " ");
	    newComment = newComment.replace(")", " ");
	    newComment = newComment.replace("[", "");
	    newComment = newComment.replace("<", "");
	    newComment = newComment.replace("+", "");
	    newComment = newComment.replace("-", "");
	    newComment = newComment.replace(">", "");
	    newComment = newComment.replace("]", "");
	    newComment = newComment.replace("?", "");
	    newComment = newComment.replace(":", "");
	    newComment = newComment.replace("!", "");
	    newComment = newComment.replace('é', 'e');
	    newComment = newComment.replace('è', 'e');
	    newComment = newComment.replace('à', 'a');
	    newComment = newComment.replace('ê', 'e');
	    newComment = newComment.replace('â', 'a');
		return newComment;
	}
}