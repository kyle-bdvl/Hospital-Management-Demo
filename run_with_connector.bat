@echo off
echo ========================================
echo Hospital Management System - Quick Run
echo ========================================

echo.
echo Using MySQL Connector: mysql-connector-j-9.4.0.jar
echo.

set MYSQL_CONNECTOR="C:\Users\Himanshu\OneDrive\Documents\java\mysql-connector-j-9.4.0\mysql-connector-j-9.4.0.jar"

echo Step 1: Creating build directory...
if not exist build mkdir build

echo.
echo Step 2: Compiling Java files...
javac -cp %MYSQL_CONNECTOR% -d build src/main/java/com/hospital/*.java src/main/java/com/hospital/dao/*.java src/main/java/com/hospital/model/*.java src/main/java/com/hospital/ui/*.java src/main/java/com/hospital/util/*.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Make sure JDK 25 is properly installed
    pause
    exit /b 1
)

echo.
echo Step 3: Starting Hospital Management System...
echo.
echo IMPORTANT: Make sure XAMPP MySQL is running!
echo Default login: admin / admin123
echo.
pause

java -cp "build;%MYSQL_CONNECTOR%" com.hospital.Main

echo.
echo Application closed.
pause
