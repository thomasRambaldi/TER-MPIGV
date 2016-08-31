#/bin/bash

# Chemin du logiciel SmartPresentation 
SMART_PRESENTATION_ROOT="/home/kevin/Programmation/TER/SmartPresentation"

# Chemin du dossier demo se situant dans le dossier où est installé gst-kaldi-nnet2-online 
KALDI_DEMO_ROOT="/home/kevin/Programmation/TER/gst-kaldi-nnet2-online-master/demo"

# Chemin de l'interface d'homeostasis, interface-rocio
KALDI_ROCIO_INTERFACE_ROOT="/home/kevin/Programmation/TER/modified-kaldi/interface-rocio-master"

# Nom de votre fichier LaTeX sans le .tex
FILENAME="exemple-latex-anglais"

xml=".xml"
FILENAME_XML="$FILENAME$xml"

# Couleurs pour l'affichage
RED='\033[0;31m' # Couleur Rouge
NC='\033[0m'     # Aucune couleur


cd $SMART_PRESENTATION_ROOT
printf "${RED} Création du fichier xml ..${NC} \n"
ant -DxmlOrText=".xml" -DfileTex=$FILENAME "Construct File For Language Model"
printf "${RED} Done${NC} \n\n"
cp $FILENAME_XML $KALDI_ROCIO_INTERFACE_ROOT/data 

printf "${RED} Création du modèle de langage ..${NC} \n"
cd $KALDI_ROCIO_INTERFACE_ROOT
./asr/tools/create-asr-models.sh data/$FILENAME_XML asr/models/$FILENAME
printf "${RED} Done${NC} \n\n"

printf "${RED} Copie des fichiers de modèles de langages dans le dossier $KALDi_DEMO_ROOT ..${NC} \n"
cd $KALDI_DEMO_ROOT
cp -L -r $KALDI_ROCIO_INTERFACE_ROOT/asr/models/$FILENAME/* .

cd conf
echo "--splice-config=conf/splice.conf
--cmvn-config=conf/online_cmvn.conf
--lda-matrix=ivector_extractor/final.mat
--global-cmvn-stats=ivector_extractor/global_cmvn.stats
--diag-ubm=ivector_extractor/final.dubm
--ivector-extractor=ivector_extractor/final.ie
--num-gselect=5
--min-post=0.025
--posterior-scale=0.1
--use-most-recent-ivector=true
--max-remembered-frames=1000" > ivector_extractor.fixed.conf
printf "${RED} Done${NC} \n"
cd ..
LANG=C GST_PLUGIN_PATH=../src ./gui-demo-en.py &

cd $SMART_PRESENTATION_ROOT
ant -DenglishOrFrench="english" -DfileTex=$FILENAME "Start Presentation"  
