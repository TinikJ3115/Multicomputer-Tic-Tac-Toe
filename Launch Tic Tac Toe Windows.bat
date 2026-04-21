@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0"

if not exist out mkdir out

echo Compiling project...
set "JAVA_FILES="
for /r src %%f in (*.java) do (
    set "JAVA_FILES=!JAVA_FILES! "%%f""
)

javac -d out !JAVA_FILES!
if errorlevel 1 (
    echo.
    echo Compilation failed.
    pause
    exit /b 1
)

echo Launching game...
java -cp out ui.MainMenu

if errorlevel 1 (
    echo.
    echo Launch failed.
    pause
    exit /b 1
)
endlocal
