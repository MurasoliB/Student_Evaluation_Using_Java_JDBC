$WshShell = New-Object -ComObject WScript.Shell
$DesktopPath = [Environment]::GetFolderPath('Desktop')
$ShortcutPath = Join-Path -Path $DesktopPath -ChildPath "KEC Academic Evaluation.lnk"

if (Test-Path $ShortcutPath) {
    $Shortcut = $WshShell.CreateShortcut($ShortcutPath)
    Write-Output "File exists: True"
    Write-Output "Target: $($Shortcut.TargetPath)"
    Write-Output "WorkingDirectory: $($Shortcut.WorkingDirectory)"
    Write-Output "IconLocation: $($Shortcut.IconLocation)"
    Write-Output "Description: $($Shortcut.Description)"
} else {
    Write-Output "Shortcut not found at $ShortcutPath"
}

