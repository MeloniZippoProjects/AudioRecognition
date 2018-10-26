param(
    [parameter(Mandatory=$true)][String]$databasePath,
	[parameter(Mandatory=$true)][String]$fingerprintsDirectory,
    [parameter(Mandatory=$true)][String]$metadataDirectory,
    [parameter(Mandatory=$true)][String]$coversDirectory,
    [bool]$overwrite=$true,
    [bool]$suppressOutput=$true	
)

Import-Module PSSQLite

$dbExists = Test-Path $databasePath
if(-Not $dbExists -Or $overwrite) 
{
    # Create the database and the needed tables in it
    if($dbExists)
    {
        Remove-Item $databasePath
    } 
    New-Item $databasePath
    
    $query = "CREATE TABLE fingerprint(song_id BIGINT PRIMARY KEY, data BLOB, title VARCHAR(255), artist VARCHAR(255), album VARCHAR(255), genre VARCHAR(255))"
    Invoke-SqliteQuery -Query $query -DataSource $databasePath

    Write-Host "Database created"
}