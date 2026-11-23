#!/bin/sh
# Fabrique FallingSnake.jar, le fichier à télécharger pour jouer (java -jar FallingSnake.jar)
# et le copie dans web/ pour la version jouable dans le navigateur (CheerpJ).
set -e
cd "$(dirname "$0")"

rm -rf build
mkdir -p build/classes

# --release 8 : le jeu tourne avec Java 8 ou plus récent, et avec le Java 8 de CheerpJ
javac --release 8 -nowarn -Xlint:-options -d build/classes *.java

printf 'Main-Class: App\n' > build/manifest.txt
jar cfm build/FallingSnake.jar build/manifest.txt -C build/classes .

cp build/FallingSnake.jar web/FallingSnake.jar
echo "OK : build/FallingSnake.jar et web/FallingSnake.jar"
