@echo off
title KEC (R2024) Student Academic ^& Continuous Evaluation System
cd /d "%~dp0"

echo =====================================================================
echo  Kongu Engineering College (Autonomous) - Regulation 2024
echo  Student Academic ^& Continuous Evaluation System (Java JDBC)
echo =====================================================================
echo.

:: Compile source files if needed
echo Compiling source files...
javac -cp ".;src;lib\mysql-connector-j-8.x.jar" src\DBConnection.java src\AcademicDAO.java src\StudentDAO.java src\WebDashboard.java src\MainApp.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)
echo Compilation successful.
echo.

:: Handle command-line arguments when run from Command Prompt
if /i "%~1"=="web" goto run_web
if /i "%~1"=="1" goto run_web
if /i "%~1"=="gui" goto run_web
if /i "%~1"=="dashboard" goto run_web

if /i "%~1"=="console" goto run_console
if /i "%~1"=="2" goto run_console
if /i "%~1"=="cli" goto run_console
if /i "%~1"=="cmd" goto run_console

if /i "%~1"=="demo" goto run_demo
if /i "%~1"=="3" goto run_demo

:: If flags like --report, --defaulters, etc. are passed
if not "%~1"=="" (
    java -cp ".;src;lib\mysql-connector-j-8.x.jar" MainApp %*
    pause
    exit /b 0
)

:interactive_menu
echo Select how you want to run the system:
echo  [1] Launch Interactive Web Dashboard (Browser GUI at http://localhost:8080)
echo  [2] Launch Command Prompt Console Menu (Interactive CLI)
echo  [3] Run Automated Demonstration Proof
echo  [4] Exit
echo.
set /p opt="Enter your choice (1-4) [default: 1]: "

if "%opt%"=="2" goto run_console
if "%opt%"=="3" goto run_demo
if "%opt%"=="4" exit /b 0
goto run_web

:run_console
echo.
echo Launching Interactive Command Prompt Console...
echo.
java -cp ".;src;lib\mysql-connector-j-8.x.jar" MainApp
pause
exit /b 0

:run_demo
echo.
echo Running Automated Demonstration Proof in Command Prompt...
echo.
java -cp ".;src;lib\mysql-connector-j-8.x.jar" MainApp --demo
pause
exit /b 0

:run_web
echo.
echo Starting Interactive Web Dashboard at http://localhost:8080 ...
java -cp ".;src;lib\mysql-connector-j-8.x.jar" WebDashboard
pause
exit /b 0
