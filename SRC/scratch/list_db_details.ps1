$userPath = [Environment]::GetEnvironmentVariable('Path', 'User')
$machinePath = [Environment]::GetEnvironmentVariable('Path', 'Machine')
$env:Path = "$userPath;$machinePath"

& mysql -u root -p0000 -e "
SHOW DATABASES;
SELECT table_schema AS 'Database', table_name AS 'Table', table_rows AS 'Approx Rows' 
FROM information_schema.tables 
WHERE table_schema NOT IN ('information_schema', 'mysql', 'performance_schema', 'sys') 
ORDER BY table_schema, table_name;
"

