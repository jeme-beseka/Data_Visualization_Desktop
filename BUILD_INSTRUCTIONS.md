# Data Visualizer - Build Instructions

## System Requirements

- **Java Development Kit (JDK) 17 or higher**
  - Download from: https://www.oracle.com/java/technologies/downloads/
  - Or use OpenJDK: https://adoptopenjdk.net/

- **Maven 3.6 or higher**
  - Download from: https://maven.apache.org/download.cgi
  - Installation guide: https://maven.apache.org/install.html

- **Windows 10/11** (for .exe creation)

## Installation Steps

### 1. Install Java 17+
```bash
# Verify installation
java -version
# Should show: java version "17.x.x" or higher
```

### 2. Install Maven
```bash
# Verify installation
mvn -version
# Should show Maven 3.6.0 or higher
```

### 3. Add to System PATH (if not already done)
- Java bin directory: `C:\Program Files\Java\jdk-17\bin`
- Maven bin directory: `C:\Program Files\apache-maven-3.x.x\bin`

## Building the Application

### Option 1: Quick Build (Recommended)

Navigate to project directory and run:
```bash
build-exe.bat
```

This script will:
1. Clean previous builds
2. Compile the source code
3. Download dependencies
4. Package the application
5. Create the Windows executable

**Result:** `KJS Data Visualizer.exe` in the project root

### Option 2: Step-by-Step Manual Build

#### Step 1: Clean and Compile
```bash
mvn clean compile
```

#### Step 2: Run Tests (Optional)
```bash
mvn test
```

#### Step 3: Package the Application
```bash
mvn package
```

This creates `target\data-visualizer.jar`

#### Step 4: Create Windows Executable
```bash
jpackage --input target ^
    --name "KJS Data Visualizer" ^
    --main-jar data-visualizer.jar ^
    --main-class com.datavisualizer.Main ^
    --type exe ^
    --icon assets\Data-Visual-Icon.ico ^
    --vendor "Jeme Beseka" ^
    --description "Transform Your Data Into Beautiful Charts" ^
    --app-version 3.3
```

**Result:** `KJS Data Visualizer.exe` in the current directory

### Option 3: Development Mode (No Executable)

For development and testing without creating an executable:

```bash
mvn javafx:run
```

This runs the application directly from source code.

## Troubleshooting Build Issues

### Issue: "mvn: command not found"
**Solution:**
1. Verify Maven is installed: `mvn -version`
2. Add Maven bin to PATH environment variable
3. Restart Command Prompt/PowerShell after PATH changes

### Issue: "java: command not found"
**Solution:**
1. Verify Java is installed: `java -version`
2. Add Java bin to PATH environment variable
3. Ensure JDK (not JRE) is installed

### Issue: "jpackage: command not found"
**Solution:**
- jpackage comes with Java 17+
- Verify Java version: `java -version` (must be 17+)
- jpackage is located in: `JAVA_HOME\bin\jpackage.exe`

### Issue: Build fails with "Cannot find symbol"
**Solution:**
1. Run `mvn clean install` to download dependencies
2. Check internet connection
3. Verify pom.xml is not corrupted

### Issue: "Icon file not found"
**Solution:**
- Ensure `assets\Data-Visual-Icon.ico` exists in project root
- Check file path in jpackage command
- Use absolute path if relative path doesn't work

### Issue: jpackage fails with "Invalid input"
**Solution:**
1. Verify JAR file exists: `target\data-visualizer.jar`
2. Check main class is correct: `com.datavisualizer.Main`
3. Ensure all paths use backslashes: `\` (not forward slashes)

## Build Artifacts

After successful build, you'll have:

```
project-root/
├── target/
│   ├── data-visualizer.jar          # Packaged application
│   ├── classes/                     # Compiled classes
│   └── dependency/                  # Downloaded dependencies
├── KJS Data Visualizer.exe              # Windows executable
└── KJS Data Visualizer/                 # Installation directory (optional)
```

## Verifying the Build

### Test the JAR File
```bash
java -jar target\data-visualizer.jar
```

### Test the Executable
```bash
"KJS Data Visualizer.exe"
```

Or simply double-click the executable file.

## Distribution

### For End Users
1. Distribute `KJS Data Visualizer.exe` directly
2. No installation required - it's portable
3. Users can place it anywhere on their system

### For Developers
1. Share the entire project folder
2. Users run: `mvn javafx:run`
3. Or build their own executable

### For Installers
Use tools like:
- **NSIS** (Nullsoft Scriptable Install System)
- **Inno Setup**
- **Advanced Installer**

To create a professional installer for the executable.

## Build Customization

### Change Application Name
Edit `build-exe.bat` or jpackage command:
```bash
--name "Your App Name"
```

### Change Version
Edit `build-exe.bat` or jpackage command:
```bash
--app-version 2.1
```

### Change Icon
Replace `assets\Data-Visual-Icon.ico` with your icon file

### Change Vendor Name
Edit `build-exe.bat` or jpackage command:
```bash
--vendor "Your Company"
```

## Performance Optimization

### Reduce Build Time
```bash
mvn -T 1C package  # Use single thread
mvn -o package     # Offline mode (if dependencies cached)
```

### Reduce Executable Size
- Remove unused dependencies from pom.xml
- Use jlink to create minimal JVM
- Strip debug information

## Advanced Options

### Build with Custom JVM
```bash
mvn package
jlink --module-path target/modules \
      --add-modules java.base,javafx.controls,javafx.fxml \
      --output custom-jvm
```

### Sign the Executable (Optional)
```bash
signtool sign /f certificate.pfx /p password "Data Visualizer.exe"
```

## Continuous Integration

For automated builds, use:
- **GitHub Actions**
- **GitLab CI**
- **Jenkins**
- **Azure Pipelines**

Example GitHub Actions workflow:
```yaml
name: Build
on: [push]
jobs:
  build:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: mvn package
```

## Support & Documentation

- **README.md** - Complete feature documentation
- **QUICK_START.md** - User guide
- **IMPROVEMENTS.md** - Feature details
- **build-windows-exe.md** - Workflow guide

## Version Information

- **Application Version:** 2.0
- **Java Target:** 17+
- **JavaFX Version:** 17.0.2
- **Maven Version:** 3.6+
- **Build Date:** 2026

---

**Need Help?** Check the troubleshooting section or review the workflow documentation.
