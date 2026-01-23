@echo off
chcp 65001 >nul
setlocal

set PROJECT_DIR=%~dp0
set H2_JAR=%USERPROFILE%\.m2\repository\com\h2database\h2\2.4.240\h2-2.4.240.jar
set DB_PATH=%PROJECT_DIR%bazaPodataka\studentski_sustav

java -cp "%H2_JAR%" org.h2.tools.RunScript -url "jdbc:h2:file:%DB_PATH%" -user "sa" -password "" -script "%PROJECT_DIR%kreiranje_tablica.sql" -showResults

echo.
echo Baza inicijalizirana.
pause


