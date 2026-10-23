$WshShell = New-Object -ComObject WScript.Shell

$mysqlExe = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
$myIni = "C:\ProgramData\MySQL\MySQL Server 8.0\my.ini"
$workDir = "C:\Program Files\MySQL\MySQL Server 8.0\bin\"

# 1. Desktop Shortcut
$desktopPath = [Environment]::GetFolderPath('Desktop')
$desktopShortcut = Join-Path -Path $desktopPath -ChildPath "MySQL 8.0 Command Line Client.lnk"

$sc1 = $WshShell.CreateShortcut($desktopShortcut)
$sc1.TargetPath = $mysqlExe
$sc1.Arguments = """--defaults-file=$myIni"" ""-uroot"" ""-p"""
$sc1.WorkingDirectory = $workDir
$sc1.IconLocation = "$mysqlExe,0"
$sc1.Description = "MySQL 8.0 Command Line Client (Root)"
$sc1.Save()
Write-Output "Created Desktop Shortcut: $desktopShortcut"

# 2. User Start Menu Shortcut
$userStartMenu = [Environment]::GetFolderPath('StartMenu')
$userPrograms = Join-Path -Path $userStartMenu -ChildPath "Programs\MySQL\MySQL Server 8.0"
if (-not (Test-Path $userPrograms)) {
    New-Item -ItemType Directory -Path $userPrograms -Force | Out-Null
}

$startMenuShortcut = Join-Path -Path $userPrograms -ChildPath "MySQL 8.0 Command Line Client.lnk"
$sc2 = $WshShell.CreateShortcut($startMenuShortcut)
$sc2.TargetPath = $mysqlExe
$sc2.Arguments = """--defaults-file=$myIni"" ""-uroot"" ""-p"""
$sc2.WorkingDirectory = $workDir
$sc2.IconLocation = "$mysqlExe,0"
$sc2.Description = "MySQL 8.0 Command Line Client (Root)"
$sc2.Save()
Write-Output "Created Start Menu Shortcut: $startMenuShortcut"

$startMenuUnicode = Join-Path -Path $userPrograms -ChildPath "MySQL 8.0 Command Line Client - Unicode.lnk"
$sc3 = $WshShell.CreateShortcut($startMenuUnicode)
$sc3.TargetPath = $mysqlExe
$sc3.Arguments = """--defaults-file=$myIni"" ""-uroot"" ""-p"" ""--default-character-set=utf8mb4"""
$sc3.WorkingDirectory = $workDir
$sc3.IconLocation = "$mysqlExe,0"
$sc3.Description = "MySQL 8.0 Command Line Client - Unicode (Root)"
$sc3.Save()
Write-Output "Created Start Menu Unicode Shortcut: $startMenuUnicode"

