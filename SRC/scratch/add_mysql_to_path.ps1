$mysqlBin = "C:\Program Files\MySQL\MySQL Server 8.0\bin"
$currentPath = [Environment]::GetEnvironmentVariable('Path', 'User')

if ($currentPath -notlike "*$mysqlBin*") {
    $newPath = "$mysqlBin;$currentPath"
    [Environment]::SetEnvironmentVariable('Path', $newPath, 'User')
    Write-Output "Added MySQL bin to User PATH: $newPath"
} else {
    Write-Output "MySQL bin is already in User PATH."
}

