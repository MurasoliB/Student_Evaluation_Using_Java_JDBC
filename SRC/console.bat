@echo off
title KEC (R2024) Academic Evaluation - Command Prompt Console
cd /d "%~dp0"

echo =====================================================================
echo  Kongu Engineering College (Autonomous) - Regulation 2024
echo  Student Academic ^& Continuous Evaluation System (Command Prompt)
echo =====================================================================
echo.

:: Ensure sources are compiled
if not exist "src\MainApp.class" (
    echo Compiling source files...
    javac -cp ".;src;lib\mysql-connector-j-8.x.jar" src\DBConnection.java src\AcademicDAO.java src\StudentDAO.java src\WebDashboard.java src\MainApp.java
    if %ERRORLEVEL% NEQ 0 (
        echo [ERROR] Compilation failed.
        pause
        exit /b %ERRORLEVEL%
    )
)

:: Launch the Interactive Console System
java -cp ".;src;lib\mysql-connector-j-8.x.jar" MainApp %*

if %ERRORLEVEL% NEQ 0 (
    echo.
    pause
)

