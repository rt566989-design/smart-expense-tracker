@echo off
REM ====================================================================
REM Smart Expense Tracker - Automated Build & Packaging Script
REM Advanced Internet Programming Mini Project
REM ====================================================================

echo ===================================================
echo   Building Smart Expense Tracker Web Application
echo ===================================================
echo.

REM 1. Check if Maven is available in PATH
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    echo [INFO] Apache Maven detected. Building WAR via Maven...
    call mvn clean package
    if %ERRORLEVEL% equ 0 (
        echo.
        echo [SUCCESS] Build completed! WAR artifact generated at:
        echo   target\smart-expense-tracker.war
        echo.
        echo Copy target\smart-expense-tracker.war into your Tomcat 'webapps' folder.
        goto end
    ) else (
        echo [WARNING] Maven build failed. Falling back to JDK javac compiler...
    )
)

REM 2. Fallback to direct JDK compilation if Maven is not configured
echo [INFO] Compiling directly using JDK...

set "OUTPUT_CLASSES=src\main\webapp\WEB-INF\classes"
if not exist "%OUTPUT_CLASSES%" mkdir "%OUTPUT_CLASSES%"

REM Copy db.properties into classes directory
copy /Y "src\main\resources\db.properties" "%OUTPUT_CLASSES%\" >nul

REM Find servlet-api.jar
set "SERVLET_JAR="
if exist "%CATALINA_HOME%\lib\servlet-api.jar" set "SERVLET_JAR=%CATALINA_HOME%\lib\servlet-api.jar"
if not defined SERVLET_JAR (
    if exist "%USERPROFILE%\.m2\repository\javax\servlet\javax.servlet-api\4.0.1\javax.servlet-api-4.0.1.jar" (
        set "SERVLET_JAR=%USERPROFILE%\.m2\repository\javax\servlet\javax.servlet-api\4.0.1\javax.servlet-api-4.0.1.jar"
    )
)

if not defined SERVLET_JAR (
    echo [ERROR] servlet-api.jar not found!
    echo Please set CATALINA_HOME pointing to your Tomcat installation folder, e.g.:
    echo   set CATALINA_HOME=C:\apache-tomcat-9.0.xx
    goto end
)

echo [INFO] Using Servlet API: %SERVLET_JAR%

REM Compile all java source files
set "CP=%SERVLET_JAR%;src\main\webapp\WEB-INF\lib\mysql-connector-j-8.3.0.jar;src\main\webapp\WEB-INF\lib\jstl-1.2.jar"
dir /s /b src\main\java\*.java > sources.txt

javac -encoding UTF-8 -cp "%CP%" -d "%OUTPUT_CLASSES%" @sources.txt
del sources.txt

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java compilation failed!
    goto end
)

echo [INFO] Packaging into smart-expense-tracker.war...
if not exist "target" mkdir "target"
jar -cvf target\smart-expense-tracker.war -C src\main\webapp . >nul

echo.
echo [SUCCESS] Direct build succeeded!
echo WAR file created: target\smart-expense-tracker.war
echo.
echo Deployment Instructions:
echo 1. Copy 'target\smart-expense-tracker.war' to '%CATALINA_HOME%\webapps\'
echo 2. Start Tomcat via '%CATALINA_HOME%\bin\startup.bat'
echo 3. Open browser: http://localhost:8080/smart-expense-tracker/
echo ===================================================

:end
pause

