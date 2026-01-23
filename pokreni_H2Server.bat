@echo off
chcp 65001 >nul
setlocal

set H2_JAR=%USERPROFILE%\.m2\repository\com\h2database\h2\2.4.240\h2-2.4.240.jar
set PROJECT_DIR=%~dp0
set DB_DIR=%PROJECT_DIR%bazaPodataka

echo Zaustavi s CTRL+C

java -cp "%H2_JAR%" org.h2.tools.Server -tcp -tcpPort 9092 -baseDir "%DB_DIR%"