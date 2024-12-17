@echo off
setlocal enabledelayedexpansion

set "dossier_principal=.\"
set "dossier_temp=temp"

if not exist "%dossier_temp%" mkdir "%dossier_temp%"

for /r "%dossier_principal%" %%f in (*.java) do (
    set "chemin_complet=%%~f"
    set "nom_fichier=%%~nf.java"
    
    copy "!chemin_complet!" "%dossier_temp%\!nom_fichier!" > nul
)

set /p projet="Librairie Name : "
set src=temp\*.java
set mainPkg=.\mg
set archive=%projet%.jar

javac -g -d %dossier_temp% %src%
cd %dossier_temp%
jar -cf %archive% .\mg
cd ..
xcopy "temp\%archive%" "%dossier_principal%" /y

rmdir /S /Q %dossier_temp%

endlocal