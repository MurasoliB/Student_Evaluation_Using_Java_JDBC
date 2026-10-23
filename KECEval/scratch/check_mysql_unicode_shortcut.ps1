$WshShell = New-Object -ComObject WScript.Shell
$lnkPath = "C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0\MySQL 8.0 Command Line Client - Unicode.lnk"
if (Test-Path $lnkPath) {
    $sc = $WshShell.CreateShortcut($lnkPath)
    Write-Output "Target: $($sc.TargetPath)"
    Write-Output "Arguments: $($sc.Arguments)"
    Write-Output "WorkingDir: $($sc.WorkingDirectory)"
}

