package tools;

import java.io.FileNotFoundException;

public class Main {
	/**
	 * Le main qui va créer un fichier xml (version anglause) ou un fichier txt(version française)
	 * pour ensuite permettre la création d'un modèle de langage avec ce fichier
	 * @param args
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public static void main(String[] args) throws FileNotFoundException, Exception {
		switch(args[0]){
		case ".xml" : CreationFileTools.createXmlFile(args[1]+".xml", CreationFileTools.constructDocumentXMLForHomeostatis(LatexTools.latexToSlide(args[1]+".tex"))); break;
		case ".txt" : CreationFileTools.createTextFileForFrench(args[1]+".txt", LatexTools.latexToSlide(args[1]+".tex")); break;
		default : System.err.println("Usage : ant -DxmlOrText=<\".xml\" or \".tex\"> -DfileTex=<your filename without extension> \"Construct File For Language Model\"");
		}
	}
}
