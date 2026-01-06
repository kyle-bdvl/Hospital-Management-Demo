@echo off
echo ========================================
echo Hospital Management System - Setup
echo ========================================

echo.
echo Step 1: Creating build directory...
if not exist build mkdir build

echo.
echo Step 2: Checking for MySQL JDBC driver...
set MYSQL_CONNECTOR="C:\Users\Himanshu\OneDrive\Documents\java\mysql-connector-j-9.4.0\mysql-connector-j-9.4.0.jar"
if not exist %MYSQL_CONNECTOR% (
    echo ERROR: MySQL JDBC driver not found at expected location!
    echo Expected: %MYSQL_CONNECTOR%
    echo Please verify the file exists at this path
    echo.
    pause
    exit /b 1
)

echo MySQL JDBC driver found: %MYSQL_CONNECTOR%

echo.
echo Step 3: Compiling Java files...
javac -cp %MYSQL_CONNECTOR% -d build src/main/java/com/hospital/*.java src/main/java/com/hospital/dao/*.java src/main/java/com/hospital/model/*.java src/main/java/com/hospital/ui/*.java src/main/java/com/hospital/util/*.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Make sure JDK is properly installed and JAVA_HOME is set
    pause
    exit /b 1
)

echo.
echo Step 4: Compilation successful! Starting application...
echo.
echo Make sure XAMPP MySQL is running before proceeding...
echo Default login: admin / admin123
echo.
pause

java -cp "build;%MYSQL_CONNECTOR%" com.hospital.Main

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Application failed to start!
    echo Check if XAMPP MySQL is running and database is set up
    pause
)

echo.
echo Application closed.
pause
