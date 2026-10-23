@echo off
title MySQL 8.0 Command Line Client - Root
cd /d "C:\Program Files\MySQL\MySQL Server 8.0\bin"
echo ==========================================================
echo  MySQL 8.0 Command Line Client
echo ==========================================================
echo Connecting to localhost:3306 as user 'root'...
echo Tip: Enter password '0000' when prompted.
echo.
mysql.exe --defaults-file="C:\ProgramData\MySQL\MySQL Server 8.0\my.ini" -u root -p
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [!] MySQL session ended.
    pause
)

