@echo off
REM Build Data Visualizer as Windows Executable
REM Requires: JDK 17+, Maven, jpackage (included in JDK 14+)

echo ================================================
echo  Building King Data Visualizer
echo ================================================
echo.

REM Step 1: Build the fat JAR with Maven
echo [1/2] Compiling and packaging with Maven...
call mvn clean package -q

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Maven build failed! Run "mvn clean package" to see details.
    pause
    exit /b 1
)

echo [1/2] Maven build successful.
echo.

REM Step 2: Package into a Windows installer with jpackage
echo [2/2] Creating Windows executable with jpackage...

REM Locate jpackage via JAVA_HOME, or fall back to PATH
if defined JAVA_HOME (
    set JPACKAGE="%JAVA_HOME%\bin\jpackage"
) else (
    set JPACKAGE=jpackage
)

REM Verify jpackage is accessible
%JPACKAGE% --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] jpackage not found!
    echo   JAVA_HOME is currently: %JAVA_HOME%
    echo.
    echo   Fix options:
    echo   1. Set JAVA_HOME to your JDK 17+ folder, e.g.:
    echo      set JAVA_HOME=C:\Program Files\Java\jdk-17
    echo   2. Or add your JDK bin folder to PATH
    echo   3. Or run: where java  -- to find your JDK location
    pause
    exit /b 1
)

REM Output goes into target\installer to keep things tidy
if not exist target\installer mkdir target\installer

%JPACKAGE% ^
    --input target ^
    --name "King_Data_Visualizer" ^
    --main-jar data-visualizer.jar ^
    --main-class com.datavisualizer.Main ^
    --type exe ^
    --icon assets\Data-Visual-Icon.ico ^
    --vendor "Jeme Beseka" ^
    --description "Transform Your Data Into Beautiful Charts" ^
    --app-version 3.3 ^
    --dest target\installer ^
    --win-shortcut ^
    --win-shortcut-prompt ^
    --win-menu ^
    --win-menu-group "KJS Data Visualizer" ^
    --win-dir-chooser ^
    --java-options "--module-path \"C:\javafx-sdk-17.0.18\lib\" --add-modules javafx.controls,javafx.fxml,javafx.swing"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] jpackage failed!
    echo   - Make sure JDK 17+ is installed (jpackage is included in the JDK)
    echo   - Make sure assets\Data-Visual-Icon.ico exists
    echo   - Make sure target\data-visualizer.jar was created by Maven
    pause
    exit /b 1
)

echo.
echo ================================================
echo  Build complete!
echo  Installer: target\installer\
echo ================================================
pause