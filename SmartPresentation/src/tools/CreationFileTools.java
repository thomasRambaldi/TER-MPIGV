package tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * La classe CreationFileTools est une classe statique qui permet de créer des fichiers xml spécifique 
 * pour homeostatis ou bien des fichiers textes pour l'outil de création de modèle de langage de Benoit Favre
 * Elle permet aussi de lire des fichiers xml spécifiques à homeostasis afin d'en récupérer les actions et les mots-clefs
 * @author kevin
 *
 */
public class CreationFileTools {
	
	private static String [] numbersEnglish = {"zero","one","two","three","four","five","six","seven","eight","nine","ten",
			"eleven","twelve", "thirteen","fourteen","fifteen", "sixteen", "seventeen","eighteen", "nineteen", "twenty",
			"twenty one", "twenty two", "twenty three", "twenty four", "twenty five", "twenty six", "twenty seven"};
	
	private static String [] numbersFrench = {"zero","un","deux","trois","quatre","cinq","six","sept","huit","neuf","dix",
			"onze","douze", "treize","quatorze","quinze", "seize", "dix-sept", "dix-huit", "dix-neuf", "vingt",	
			"vingt-et-un","vingt-deux", "vingt-trois", "vingt-quatre", "vingt-cinq", "vingt-six", "vingt-sept"};
	
	/**
	 * Construit un document xml spécifique à homeostasis
	 * Ce document va permettre ensuite la création d'un fichier xml 
	 * @param slideArray
	 * @return
	 */
	public static Document constructDocumentXMLForHomeostatis(ArrayList<Slide> slideArray) {
		Document documentXml = DocumentHelper.createDocument();
		Element root = documentXml.addElement( "homeostasis" );
		root.addAttribute("version", "25-11-2014");
		Element liste_section = null;
		Element sequence = null;
		Element section = null;
		try {
			liste_section = createSectionList(liste_section, root, slideArray);
			createSections(section, liste_section, sequence, slideArray);
		} catch (Exception e){
			e.printStackTrace();
		}
		return  documentXml;
	}
	
	/**
	 * Crée les balises représentant les sections
	 * @param section est un Element(org.dom4j.Element) 
	 * @param liste_section est un Element(org.dom4j.Element) 
	 * @param sequence est un Element(org.dom4j.Element) 
	 * @param slideArray la liste de slides contenant les commentaire, texte, titres de chaque slide
	 */
	private static void createSections(Element section, Element liste_section, Element sequence, ArrayList<Slide> slideArray) {
		StringBuilder sentenceComment = new StringBuilder("");
		
		for (int i = 0; i < slideArray.size(); i++) {
			// Récupérations des commentaire et du texte
			sentenceComment.append(slideArray.get(i).getCommentaires() + " ");
			// TODO : Attribut de section ATTENTION A VERIFIER
			section = liste_section.addElement("section").addAttribute("id", String.valueOf(i+1));
			section.addAttribute("start", "go to slide "+ numbersEnglish[i+1]).addAttribute("end","exit section");
			section.addAttribute("action_start","slide"+ numbersEnglish[i+1]);
			section.addAttribute("action_end","nextslide");
			createSequences(sequence, section, i, slideArray, false);
		}
		createSequences(sequence, section, 0, slideArray, true);
	}
	
	/**
	 * Sert à créer les balises représentant les sequences
	 * @param sequence est un Element(org.dom4j.Element) 
	 * @param section est un Element(org.dom4j.Element)
	 * @param index : L'index de la slide courante
	 * @param slideArray la liste de slides contenant les commentaire, texte, titres de chaque diapositives 
	 * @param isHomeostatisVersion 
	 */
	private static void createSequences(Element sequence, Element section, int index, ArrayList<Slide> slideArray, boolean isEnd) {
		if(!isEnd){
			sequence = section.addElement("sequence").addAttribute("ordre","strict");
			sequence.addAttribute("repetition","non").addAttribute("action","");
			sequence.addAttribute("action", "nextslide").addText(slideArray.get(index).getCommentaires());
			sequence.addElement("keyword").addAttribute("action", "nextslide")
			.addText("the slide about the "+slideArray.get(index).getTitre());
		}
		else{
			sequence = section.addElement("sequence").addAttribute("ordre","strict");
			sequence.addAttribute("repetition","non").addAttribute("action","");
			sequence.addAttribute("action", "noaction");
			String concatTitles="";
			for (int i = 0; i < slideArray.size(); i++)
				concatTitles+="go to the slide "+slideArray.get(i).getTitre()+" ";
			sequence.addText("go to the next slide go to the previous slide "+printStringArray(numbersEnglish)+" "+concatTitles);
		}
	}
	
	/**
	 * Cette fonction à transformer un tableau de string en string
	 * @param array : le tableau de string
	 * @return
	 */
	private static String printStringArray(String [] array){
		String concat ="";
		for(int i=0;i<array.length;i++)
			concat+=array[i]+" ";
		return concat;
	}

	/**
	 * Crée les balises représentant la liste des sections
	 * @param liste_section est un Element(org.dom4j.Element)
	 * @param root est un Element(org.dom4j.Element) il représente la racine du fichier XML
	 * @param slideArray : La liste de slides contenant les commentaire, texte, titres de chaque slide
	 * @param b 
	 * @return la liste des sections
	 */
	private static Element createSectionList(Element liste_section, Element root, ArrayList<Slide> slideArray) {
		liste_section = root.addElement("liste_section").addAttribute("sequences",getListOfSections(slideArray) );
		liste_section.addAttribute("ordre", "variable").addAttribute("repetition","oui");
		liste_section.addAttribute("action","exclusive");

		return liste_section;
		
	}
	
	/** 
	 * Construit un string qui contient tous les numéros des diapos
	 *  des slides séparé par un espace
	 * @param slideArray: La liste de slides contenant les commentaire, texte, titres de chaque slide
	 * @return un string contenant les ID des slides séparé par un espace
	 */
	public static String getListOfSections (ArrayList<Slide> slideArray) {
		String listeSections = "";
		for (int i = 0; i < slideArray.size() ; i++) {
			listeSections += String.valueOf(i+1);
			if(i+1 != slideArray.size()){
				listeSections += " ";
			}	
		}
		return listeSections;
	}


	/**
	 * Crée un fichier .xml  ou est inscrit tout le document XML
	 * @param fileXmlName: Le nom du fichier xml que vous voulez créer
	 * @param documentXml: Le document crée avec la méthode "constructDocumentXMLForHomeostasis"
	 * @throws Exception
	 */
	// Ecrit en sortie le fichier XML en format XML
	public static void createXmlFile(String fileXmlName,Document documentXml) throws Exception {
		FileOutputStream out = new FileOutputStream(fileXmlName);
		OutputFormat outformat = OutputFormat.createPrettyPrint();
		outformat.setEncoding("utf-8");
		XMLWriter writer = new XMLWriter(out, outformat);
		writer.write(documentXml);
		writer.flush();
	}
	
	/**
	 * * Lit le fichier XML spécifique à Homeostasis et récupère toutes les actions et les keywords
	 * @param XMLfileName: Fichier XML à lire
	 * @return une liste contenant quatres listes de string, la 1ere correspond aux mots-clefs que l'on peut trouver au debut, 
	 * la 2eme correspond aux actions de ces mots-clefs de début, la 3eme et la 4eme correspondent aux mots-clefs 
	 * permettant le changement de diapos et leur actions 
	 */
	public static ArrayList<ArrayList<String>> lectureXML(String XMLfileName){
		ArrayList<ArrayList<String>> keyWordsAndActions = new ArrayList<>();
		for(int i=0; i<4;i++)
			keyWordsAndActions.add(new ArrayList<String>());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = null;
		org.w3c.dom.Document doc = null;
		org.w3c.dom.Element racine = null;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(XMLfileName);
			racine = doc.getDocumentElement();
			NodeList list = racine.getChildNodes();
		    for (int i=0; i < list.getLength(); i++) {
		        Node subnode = list.item(i);
		        if (subnode.getNodeType() == Node.ELEMENT_NODE) 
		               getListSectionInfos(subnode, keyWordsAndActions);
		    }
		} catch (Exception e) {	e.printStackTrace();}
		return keyWordsAndActions;
	}

	/**
	 * Récupère les informations de la balise liste_section
	 * @param listSection Le noeud liste section du fichier XML
	 * @param keyWordsAndActions  La liste de liste de string contenant les 4 listes (mots-clef-debut, actions, mot-clefs, actions)
	 */
	private static void getListSectionInfos(Node listSection, ArrayList<ArrayList<String>> keyWordsAndActions) {
		/*System.out.print("<"+listSection.getNodeName());
		NamedNodeMap attr = listSection.getAttributes();
	    for (int i=attr.getLength()-1; i > -1; i--)
	        System.out.print(" "+attr.item(i));
	    System.out.println(">");*/
	    getGlobalKeywordsAndSections(listSection, keyWordsAndActions);
	}

	/**
	 * Récupère les mots-clés en début de fichiers
	 * @param listSection Le noeud liste section du fichier XML
	 * @param keyWordsAndActions  La liste de liste de string contenant les 4 listes (mots-clef-debut, actions, mot-clefs, actions)
	 */
	private static void getGlobalKeywordsAndSections(Node listSection, ArrayList<ArrayList<String>> keyWordsAndActions) {
		NodeList list = listSection.getChildNodes();
		for (int i=0; i < list.getLength(); i++) {
			Node subnode = list.item(i);
			if (subnode.getNodeType() == Node.ELEMENT_NODE) {
				if(subnode.getNodeName().equals("keyword_begin"))	
					getKeywordInfos(subnode, keyWordsAndActions);
				else if(subnode.getNodeName().equals("section"))
					getSectionInfos(subnode, keyWordsAndActions);
			}
		}
	}

	/**
	 * Récupère les informations d'une section
	 * @param section Le noeud section du fichier XML
	 * @param keyWordsAndActions  La liste de liste de string contenant les 4 listes (mots-clef-debut, actions, mot-clefs, actions)
	 */
	private static void getSectionInfos(Node section, ArrayList<ArrayList<String>> keyWordsAndActions) {
		/*System.out.print("<"+section.getNodeName());
		NamedNodeMap attr = section.getAttributes();
	    for (int i=attr.getLength()-1; i > -1; i--)
	        System.out.print(" "+attr.item(i));
	    System.out.println(">");*/
	    NodeList list = section.getChildNodes();
		for (int i=0; i < list.getLength(); i++) {
			Node subnode = list.item(i);
			if (subnode.getNodeType() == Node.ELEMENT_NODE)
				getSequenceInfos(subnode, keyWordsAndActions);
		}
	}

	/**
	 * Récupère les informations des noeuds sequence
	 * @param sequence Le noeud sequence du fichier XML
	 * @param keyWordsAndActions  La liste de liste de string contenant les 4 listes (mots-clef-debut, actions, mot-clefs, actions)
	 */
	private static void getSequenceInfos(Node sequence, ArrayList<ArrayList<String>> keyWordsAndActions) {
		/*System.out.print("<"+sequence.getNodeName());
		NamedNodeMap attr = sequence.getAttributes();
	    for (int i=attr.getLength()-1; i > -1; i--)
	        System.out.print(" "+attr.item(i));
	    System.out.println(">");*/
	    NodeList list = sequence.getChildNodes();
		for (int i=0; i < list.getLength(); i++) {
			Node subnode = list.item(i);
			if (subnode.getNodeType() == Node.ELEMENT_NODE)
				getKeywordInfos(subnode, keyWordsAndActions);
		}
	}

	/**
	 * Récupère les informations du noeud KeyWord
	 * @param keyword le noeud keyword ou keyword_begin du fichier XML
	 * @param keyWordsAndActions  La liste de liste de string contenant les 4 listes (mots-clef-debut, actions, mot-clefs, actions)
	 */
	private static void getKeywordInfos(Node keyword, ArrayList<ArrayList<String>> keyWordsAndActions) {
		/*System.out.print("<"+keyword.getNodeName());
		NamedNodeMap attr = keyword.getAttributes();
	    for (int i=attr.getLength()-1; i > -1; i--)
	        System.out.print(" "+attr.item(i));
	    System.out.println(">");
	    System.out.println("	"+keyword.getTextContent());*/
	    NamedNodeMap attr = keyword.getAttributes();
	    if(keyword.getNodeName().equals("keyword_begin")){
	    	keyWordsAndActions.get(0).add(attr.item(0).getNodeValue());
	    	keyWordsAndActions.get(1).add(keyword.getTextContent().trim());
	    }
	    else if(keyword.getNodeName().equals("keyword")){
	    	keyWordsAndActions.get(2).add(attr.item(0).getNodeValue());
	    	keyWordsAndActions.get(3).add(keyword.getTextContent().trim());
	    }
	}
	
	/**
	 * Crée un fichier txt pour la création de modèle de langage français
	 * @param fileName le nom de votre fichier txt
	 * @param slides la liste de slides contenant toutes les diapositives dela présentation
	 */
	public static void createTextFileForFrench(String fileName, ArrayList<Slide> slides) {
		try
		{
			File fileTxt = new File(fileName);
			fileTxt.createNewFile(); 
			FileWriter fw = new FileWriter (fileTxt);
			for(Slide s : slides){
				fw.write("aller a la diapositive "+s.getTitre());
				fw.write(s.getCommentaires());
				fw.write('\n');
				fw.write('\n');
			}
			fw.write(printStringArray(numbersFrench));
			fw.write("passer a la diapositive suivante passer a la diapositive precedente prochaine va a la diapo diapositive jarvis ");
			fw.close();
		}
		catch (Exception e)	{e.printStackTrace();} 
	}

}


