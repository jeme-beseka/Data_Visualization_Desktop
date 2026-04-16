---
description: Build Data Visualizer as a Windows executable (.exe)
---

# Building Data Visualizer as Windows Executable

This workflow guides you through creating a standalone Windows executable for the Data Visualizer application.

## Prerequisites

Before starting, ensure you have:
- Java 17 or higher installed
- Maven 3.6 or higher installed
- Both Java and Maven added to your system PATH
- The project properly set up with all source files

## Quick Build (Recommended)

### Step 1: Navigate to Project Directory
Open Command Prompt or PowerShell and navigate to the project root:
```
cd C:\Users\Jeme Beseka\Desktop\School\DataVisualizerJava\Data_Visualization_Desktop
```

### Step 2: Run the Build Script
// turbo
Execute the build script:
```
build-exe.bat
```

The script will:
1. Clean and compile the project
2. Package all dependencies into a JAR file
3. Create a Windows executable using jpackage
4. Place the executable in the project root directory

The executable will be named `Data Visualizer.exe`

## Manual Build Process

If you prefer to build manually or the script doesn't work:

### Step 1: Build the JAR File
```
mvn clean package
```

This creates `target\data-visualizer.jar`

### Step 2: Create the Executable
// turbo
Run jpackage with the following command:
```
jpackage --input target --name "Data Visualizer" --main-jar data-visualizer.jar --main-class com.datavisualizer.Main --type exe --icon assets\Data-Visual-Icon.ico --vendor "Data Visualizer" --description "Transform Your Data Into Beautiful Charts" --app-version 2.0
```

### Step 3: Locate the Executable
The executable will be created in the current directory as `Data Visualizer.exe`

## Troubleshooting

### Issue: "jpackage not found"
**Solution:** Ensure Java 17+ is installed and in your PATH. jpackage is included with Java 17+.

### Issue: "mvn not found"
**Solution:** Ensure Maven is installed and added to your system PATH.

### Issue: Build fails with dependency errors
**Solution:** Run `mvn clean install` first to download all dependencies.

### Issue: Icon not appearing in executable
**Solution:** Ensure the `assets\Data-Visual-Icon.ico` file exists in the project root.

## Distribution

Once the executable is created:

1. **Standalone Distribution:** The `.exe` file can be distributed directly to users
2. **Installer Creation:** Use tools like NSIS or Inno Setup to create an installer
3. **Portable Version:** The executable is already portable and doesn't require installation

## Customization

To customize the executable build:

- **App Name:** Change `--name "Data Visualizer"` parameter
- **Version:** Modify `--app-version 2.0` parameter
- **Icon:** Replace `assets\Data-Visual-Icon.ico` with your icon
- **Vendor:** Update `--vendor "Data Visualizer"` parameter
- **Description:** Change `--description` parameter

## Verification

After building, verify the executable:

1. Double-click `Data Visualizer.exe` to launch
2. Confirm the app icon appears in the window title bar
3. Test loading a CSV file
4. Test creating a chart
5. Test exporting a chart as PNG
6. Test the dark mode toggle

## Next Steps

- Create an installer using NSIS or Inno Setup
- Distribute the executable to users
- Set up auto-update mechanism (optional)
- Create user documentation
