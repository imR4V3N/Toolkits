#!/bin/bash

# filepath: /home/raven/Workspace/Perso/Script/Jar.sh

set -e  # Arrête le script en cas d'erreur
set -u  # Erreur si une variable non définie est utilisée

dossier_principal="./"
dossier_temp="temp"

# Création du dossier temporaire s'il n'existe pas
if [ ! -d "$dossier_temp" ]; then
    mkdir "$dossier_temp"
fi

# Copie des fichiers .java dans le dossier temporaire
find "$dossier_principal" -type f -name "*.java" | while read -r chemin_complet; do
    nom_fichier=$(basename "$chemin_complet")
    cp "$chemin_complet" "$dossier_temp/$nom_fichier"
done

# Demande du nom de la librairie
read -p "Librairie Name: " projet
src="$dossier_temp/*.java"
mainPkg="./mg"
archive="$projet.jar"

# Compilation des fichiers Java
javac -g -d "$dossier_temp" $src

# Création de l'archive JAR
cd "$dossier_temp"
jar -cf "$archive" "$mainPkg"
cd ..

# Copie de l'archive JAR dans le répertoire principal
cp "$dossier_temp/$archive" "$dossier_principal"

# Copie de l'archive JAR dans le répertoire de test
destination="../Test/lib/"
if [ -f "$destination$archive" ]; then
    rm -f "$destination$archive"
fi
cp "$dossier_temp/$archive" "$destination"

# Nettoyage du dossier temporaire
rm -rf "$dossier_temp"