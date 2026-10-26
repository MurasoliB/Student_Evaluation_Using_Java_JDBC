$WshShell = New-Object -ComObject WScript.Shell
$DesktopPath = [Environment]::GetFolderPath('Desktop')

# 1. Main System Shortcuts
$shortcuts = @(
    "KEC Academic Evaluation.lnk",
    "run.bat - Shortcut.lnk"
)

foreach ($name in $shortcuts) {
    $ShortcutPath = Join-Path -Path $DesktopPath -ChildPath $name
    $Shortcut = $WshShell.CreateShortcut($ShortcutPath)
    $Shortcut.TargetPath = "C:\MY DRIVE\KECEval\run.bat"
    $Shortcut.WorkingDirectory = "C:\MY DRIVE\KECEval"
    $Shortcut.IconLocation = "C:\MY DRIVE\KECEval\assets\kec_icon.ico,0"
    $Shortcut.Description = "Kongu Engineering College - Continuous Evaluation System (R2024)"
    $Shortcut.Save()
    Write-Output "Created: $ShortcutPath"
}

# 2. Command Prompt Console Direct Shortcut
$ConsoleShortcutPath = Join-Path -Path $DesktopPath -ChildPath "KEC Console (Command Prompt).lnk"
$ConsoleShortcut = $WshShell.CreateShortcut($ConsoleShortcutPath)
$ConsoleShortcut.TargetPath = "C:\MY DRIVE\KECEval\console.bat"
$ConsoleShortcut.WorkingDirectory = "C:\MY DRIVE\KECEval"
$ConsoleShortcut.IconLocation = "C:\MY DRIVE\KECEval\assets\kec_icon.ico,0"
$ConsoleShortcut.Description = "KEC (R2024) Interactive Command Prompt Console Menu"
$ConsoleShortcut.Save()
Write-Output "Created: $ConsoleShortcutPath"
