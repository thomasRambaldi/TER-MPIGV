#/bin/bash

# Chemin du logiciel SmartPresentation 
SMART_PRESENTATION_ROOT="/home/kevin/Programmation/TER/SmartPresentation"

# Chemin de gst-kaldi-nnet2-online
GST_KALDI_ROOT="/home/kevin/Programmation/TER/gst-kaldi-nnet2-online-master"

# Chemin de asr-fr
ASR_FR_ROOT="/home/kevin/Programmation/TER/asr-fr"

# Nom de votre fichier LaTeX sans le .tex
# Pour tester vous pouvez mettre exemple-latex-français ou bien presentation-ter
FILENAME="presentation-ter"

txt=".txt"
FILENAME_TXT="$FILENAME$txt"

# Couleurs pour l'affichage
RED='\033[0;31m' # Couleur Rouge
NC='\033[0m'     # Aucune couleur


cd $SMART_PRESENTATION_ROOT
printf "${RED} Création du fichier texte pour le modèle de langage ..${NC} \n"
ant -DxmlOrText=".txt" -DfileTex=$FILENAME "Construct File For Language Model"
printf "${RED} Done${NC} \n\n"
cp $FILENAME_TXT $ASR_FR_ROOT 

printf "${RED} Création du modèle de langage français..${NC} \n"
cd $ASR_FR_ROOT
./tools/compile.sh $FILENAME_TXT $ASR_FR_ROOT/models/$FILENAME
printf "${RED} Done${NC} \n\n"

printf "${RED} Lancement de kaldi ..${NC} \n"
LANG=C GST_PLUGIN_PATH=$GST_KALDI_ROOT/src ./gui-demo-fr.py $FILENAME &
printf "${RED} Done${NC} \n\n"

cd $SMART_PRESENTATION_ROOT
ant -DenglishOrFrench="french" -DfileTex=$FILENAME "Start Presentation"  
