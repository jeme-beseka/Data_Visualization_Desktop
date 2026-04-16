@echo off
REM Build Data Visualizer as Windows Executable

echo Building Data Visualizer...
call mvn clean package

if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Creating Windows executable with jpackage...

REM Get the JAR file path
set JAR_FILE=target\data-visualizer.jar

REM Create the executable
jpackage ^
    --input target ^
    --name "King Data Visualizer" ^
    --main-jar data-visualizer.jar ^
    --main-class com.datavisualizer.Main ^
    --type exe ^
    --icon assets\Data-Visual-Icon.ico ^
    --vendor "Jeme Beseka" ^
    --description "Transform Your Data Into Beautiful Charts" ^
    --app-version 3.3 ^
    --win-console

if %ERRORLEVEL% NEQ 0 (
    echo jpackage failed!
    pause
    exit /b 1
)

echo.
echo Build complete! Executable created in the current directory.
pause
