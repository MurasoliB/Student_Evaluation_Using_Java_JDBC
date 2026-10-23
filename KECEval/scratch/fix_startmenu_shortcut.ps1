$WshShell = New-Object -ComObject WScript.Shell
$lnkPath = "C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0\MySQL 8.0 Command Line Client.lnk"
try {
    $sc = $WshShell.CreateShortcut($lnkPath)
    $sc.Arguments = '"--defaults-file=C:\ProgramData\MySQL\MySQL Server 8.0\my.ini" "-uroot" "-p"'
    $sc.Save()
    Write-Output "Successfully updated Start Menu shortcut!"
} catch {
    Write-Output "Error: $_"
}

