# SmartPresentation
Smart presentation est un sujet de T.E.R. (travaux encadrés de recherche) du master 1 Tronc commun informatique
 à la faculté des sciences de Luminy(Aix-Marseille) proposé par M. Frédéric Béchet pour l'année 2015-2016.
Le nom du sujet est Moteur de présentations interactives guidées par la voix.
Ce logiciel permet de faire des présentations à l'aide de la voix.
Vous pouvez changer de diapositives selon certains mots-clefs :
En français :
	passer a la diapositive suivante/precedante
	aller a la diapositive <titre de la diapositive>
En anglais :
	go to the next/previous slide
	go to the slide <titre de la diapositive>
Ces mots-clefs peuvent être modifiés dans le fichier src/server/Server.java   
Git du projet : https://gitlab.com/thomasRambaldi/TER-MPIGV

# Logiciels requis
Kaldi : https://github.com/kaldi-asr/
Gst-kaldi : https://github.com/alumae/gst-kaldi-nnet2-online
Interface d'homeostasis : https://gitlab.lif.univ-mrs.fr/HOMEOSTASIS/interface-rocio
asr-fr : outils de création de modeles de langages français de Benoit Favre - http://pageperso.lif.univ-mrs.fr/~benoit.favre/ 

# Utiliser LaTex pour SmartPresentation 
	Pour l'instant les seules présentations possibles avec SmartPresentation sont celles créées avec LaTeX.
	Il faut tout d'abord créer son fichier LaTeX(.tex) en prenant soin de mettre en commentaire ce que vous allez dire pour chaque diapositive
	Exemple de fichier tex :
	  
        \begin{frame}{Titre}
	     %texte en commentaire qui sera lu par l'application

	     texte d'illustration\\
	     qui ne sera pas lu par le système
        \end{frame}

	Dans cet exemple le titre de la diapositive est "Titre"
	Si on ajoute des sous-titres à la diapositive, (\begin{frame}{Titre}{soustitre1}{soustitre2})
	l'application prendra comme titre de la diapositive le dernier sous-titre (soit soustitre2)
	Cela servira à la navigation vocale (on dira donc pour aller à cette diapositive:"aller à la diapositive soustitre2")

	Les commentaires (tout ce qui suit le '%' sur une ligne) situés entre \begin{frame}... et \end{frame} représenteront le texte
	que vous énoncerez lors de votre passage sur la diapositive concernée. Lorsque vous serez sur cette diapositive si vous énoncez
	entierement votre texte (ce qu'il y a en commentaire) le système passera a la diapositive suivante.

	Une fois votre fichier .tex terminé, vous pourrez le compiler afin d'en créer un fichier pdf avec la commande :
		pdflatex votre_fichier.tex 
	Vous placerez ensuite votre fichier tex et votre fichier pdf généré dans la racine du projet (soit au même emplacement que ce README).
	Les deux fichiers doivent aussi avoir le même nom (votre_fichier.tex / votre_fichier.pdf)
	Il est donc évident que le fichier pdf que vous placerez devra correspondre à votre fichier tex, sinon l'application ne fonctionnera
	pas sur la présentation illustrée par le pdf.

	Il est possible que vous décidiez d'ajouter des animations simples à votre présentation comme
	des ajouts successifs de blocs textuels.
	Le système prendra chaque animation comme une page (comme le pdf).
	Exemple de fichier tex avec animation :
	
	\begin{frame}{SmartPresentation}{Animations avec apparitions de blocs textuels}
		%Texte de l'utilisateur pour l'animation une
		\begin{block}{Animation une}<1->
		  	\begin{itemize}
		  		\item {\tt bloc textuel 1}   		
			\end{itemize}
		\end{block}
		%Texte de l'utilisateur pour l'animation deux
		\begin{block}{Animation deux}<2->
		  	\begin{itemize}
		  		\item {\tt bloc textuel 2}
			\end{itemize}
		\end{block}
		%Texte de l'utilisateur pour l'animation trois
		\begin{block}{Animation trois}<3->
		  	\begin{itemize}
		  		\item {\tt bloc textuel 3}   		
	    		\end{itemize}
		\end{block}
	\end{frame}

	La syntaxe importante ici est <1-> <2-> <3-> (resp l 44, 50, 56) qui déclenche le système d'animation
	et qui est également détecté par notre système.

	Vous pouvez regarder les fichiers exemple-latex-anglais.tex et exemple-latex-français.tex 
	qui disposent d'une bonne syntaxe pour l'application.
	Une fois votre fichier tex et votre fichier pdf prêts, vous pouvez passer à l'étape suivante qui est la compilation.

# Pré-requis une fois Kaldi et gst-kaldi installés
	Déplacer le fichier gui-demo-en.py dans le dossier gst-kaldi-nnet2-online/demo
	Déplacer le fichier gui-demo-fr.py dans le dossier asr-fr/ 

# Compilation et lancement de l'application
	Vous pouvez choisir de faire des présentations anglaises ou françaises.
	Version française:
	Ouvrir le fichier startfr.sh et modifier tous les chemins en début de ce fichier
	pour que cela corresponde à ce que vous avez sur votre ordinateur. 
	Modifier également la variable FILENAME en votre nom de fichier tex sans l'extension (sans le .tex)	 
	Ouvrir un terminal puis taper 
			./startfr.sh 
	(si le terminal ne reconnait pas ce fichier, taper "chmod a+x startfr.sh" dans le terminal
	puis réessayer).

	Attention pour l'instant la version française ne reconnait pas les accents en raison de la bibliothèque
	JavaOSC qui ne reconnaît aucun accent envoyé par le client. Si vous mettez des accents dans les commentaires
	de votre fichier tex ils seront retirés lors de la création du fichier xml ou du fichier texte pour les modèles de langage.	 

	Version anglaise:
	Même déroulement que la version française sauf qu'il faut le faire avec un script différent
			./starten.sh

	Si vous regardez le contenu des scripts, vous pourrez voir que nous utilisons ant pour lancer notre projet.
	Ant utilise le fichier build.xml situé à la racine.

# Utilisation de l'interface
	Pour changer de diapositive vous pouvez utiliser les flèches droites et gauche du clavier
	Vous pouvez mettre aussi en plein écran avec F5
	Vous pouvez également zoomer avec la molette de la souris mais ce n'est pas encore très fonctionnel (voir src/server/Main.java)	

# Problèmes possibles et comment les résoudre
	Il se peut que certains logiciels ne puissent pas se lancer (message d'erreur core dumped et d'autres).
	Peut être qu'il manque des bibliothèques pour que cela puisse bien fonctionner.
	Lors de notre projet il nous manquait :
		MITLM : https://github.com/mitlm/mitlm/
		CRF : 	https://taku910.github.io/crfpp/#download
	Une fois ces deux librairies installés, il faut ensuite donner leur emplacement aux logiciels.
	Interface d'homeostasis (soit interface-rocio) : vous pouvez modifier le fichier asr/tools/path.sh
	et lui mettre les bon emplacements des librairies. 
	Si besoin le notre est situé dans EXEMPLE_PATH/path-interface-rocio.sh.
	
	Logiciel de modèles de langages français (soit asr-fr): Même manipulation, modifier le fichier tools/path.sh
	Si besoin le notre est situé dans EXEMPLE_PATH/path-asr-fr.sh

	La version française peut avoir des dysfonctionnement en raison de la bibliothèque JavaOSC
	qui n'arrive pas à recevoir les caractères spéciaux (accents, ç, etc..) ce qui peut provoquer des erreurs
	comme une diapositive qui ne passe pas à la suivante.
	Si vous utilisez des apostrophes, le logiciel risque de ne pas bien les détecter. 
	Plus de précisions dans le fichier src/server/Server.java

	Il est possible de voir des erreurs java s'afficher dans le terminal mais qui ne perturbent pas le logiciel.
	Elle peuvent se provoquer à cause des deux librairies : PDFRenderer et JavaOSC
	PDFRenderer : Si le fichier pdf de votre présentation est très complexe (beaucoup de thèmes, de couleurs...)
	il se peut que la bibliothèque génère des erreurs car elle n'est pas forcement complète c'est à dire que certains 
	pdf ne seront pas très bien gérés. Les consequences sont un affichage qui n'est pas 100% conforme au pdf.
	JavaOSC : Lorsque que l'on quitte l'application nous fermons brutalement le serveur OSC, ce qui génere une erreur
	de la classe Socket de Java. Nous n'avons pas trouvé d'autres solutions pour fermer le serveur. 

	Il est possible aussi que le logiciel ne se lance pas suite à des erreurs java du type JavaClassNotFoundException
	Cela est surement dù au fichier build.xml qui ne correspond pas exactement a votre version du projet.
	Faites une sauvegarde du fichier build.xml puis lancer le logiciel eclipse.
	Dans eclipse faites un clic droit sur notre projet puis exporter puis Ant build file.
	Normalement cela devrait créer un nouveau fichier build.xml qui correspond bien au projet.
	Ensuite ajouter à ce nouveau build.xml ce qu'il y avait dans l'ancien soit à la fin :
	<target name="Start Presentation">
        <java classname="server.Main" failonerror="true" fork="yes">
            <arg value="${englishOrFrench}"/>
            <arg value="${fileTex}"/>
            <classpath refid="SmartPresentation.classpath"/>
        </java>
    	</target>
    	<target name="Construct File For Language Model">
        <java classname="tools.Main" failonerror="true" fork="yes">
            <arg value="${xmlOrText}"/>
            <arg value="${fileTex}"/>
            <classpath refid="SmartPresentation.classpath"/>
        </java>


	En cas d'autres problèmes, contacter les étudiants de ce projet ou bien les encadrants:
	Encadrants :  (frederic.bechet,benoit.favre)[at]lif.univ-mrs.fr
	Etudiants  :  Kévin Lebreton          - K.kev542[at]laposte.net
	              Thomas Rambaldi         - t.rambaldi13[at]gmail.com
	              Yemouna Manel Chikbouni - ym.chikbouni[at]gmail.com

# Utiliser SmartPresentation avec Eclipse
	Il est possible de modifier le code de l'application pour de futures améliorations.
	Le projet ayant été fait avec le logiciel Eclipse (car en java) vous pouvez l'ouvrir via ce logiciel.
	Une fois dans eclipse vous pouvez importer le projet en sélectionnant le dossier SmartPresentation.
	Puis il faut ensuite ajouter toutes les bibliothèques externes (JDom, PDFRenderer, JavaOSC).
	Pour cela il suffit de faire un clic droit sur le projet
		> Propriétés > Onglet java build path > onglet Libraries > Add External JARs 
	puis sélectionner les trois bibliothèques présentes à la racine du projet (tous les .jar).
	S'il se trouve qu'il y a encore des erreurs suite à javafx, vous pouvez les corriger 
	en restant dans l'onglet Libraries. Il vous suffit de dérouler la librairie système que vous avez
	(la mienne est JRE System Library[JavaSE-1.8])
	Puis cliquer sur Access rules, ensuite Edit et enfin Add et taper javafx/** et le mettre en accessible.
	Cela devrait résoudre les derniers problèmes de compilation.


# Perspectives pour le futur de ce projet
	- Afin de régler le problème des accents pour la version française il serait peut être mieux de changer 
	de bibliothèque pour communiquer en OSC.
	Autre solution, nous avons remarqué qu'avec un serveur en python il n'y a aucun souci au niveau des accents.
	Un transfert de toute l'application de Java à Python résoudrait le problème.
	C'est une possiblité, sachant qu'en python la manipulation de pdf est plus facile.  	  

	- Ajouter la création du fichier pdf à partir du fichier tex dans les scripts de lancements.

	- Ajouter un mini-compilateur de fichier tex pour être sûr que l'utilisateur a bien mis les commentaires où 
	il fallait etc...

	- Rendre l'application encore facile d'utilisation grâce à un apprentissage automatique.
	Pendant que l'utilisateur répète sa présentation l'application enregistrerait tous les mots et repérerait les 
	moments où la diapositive est changée. Ainsi, après plusieurs répétitions, l'utilisateur n'aurait même plus besoin
	d'écrire de commentaires dans le fichier tex pour que la présentation défile automatiquement.
	Ce qui ouvrirait également l'application à tous les autres formats de présentations
	autres que pdf (PowerPoint, Keynote, Open Office ...).	 

# Contact
	Encadrants du projet :  (frederic.bechet,benoit.favre)[at]lif.univ-mrs.fr
	Etudiants du projets :  Kévin Lebreton          - K.kev542[at]laposte.net
	              		Thomas Rambaldi         - t.rambaldi13[at]gmail.com
	              		Yemouna Manel Chikbouni - ym.chikbouni[at]gmail.com

