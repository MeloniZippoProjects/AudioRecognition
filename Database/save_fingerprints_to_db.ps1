# Creates and populates the SQLLite database with song fingerprints and metadata 

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
        Remove-Item $databasePath;
        Write-Host "Already existing database removed"
    } 
    New-Item $databasePath
    
    $query = "CREATE TABLE song(
        song_id INTEGER PRIMARY KEY,
        fingerprint BLOB,
        title VARCHAR(255),
        artist VARCHAR(255),
        album VARCHAR(255),
        date VARCHAR(255),
        genre VARCHAR(255),
        cover BLOB);"
    Invoke-SqliteQuery -Query $query -DataSource $databasePath

    Write-Host "Database created"
}

$fingerprints = Get-ChildItem -Path $fingerprintsDirectory -Filter "*.fing.txt";

$total = $fingerprints.Length;
$current = 1;
foreach($fingerprint in $fingerprints)
{
    Write-Host "$current out of $total";
    $current++;

    $basename = ($fingerprint.Basename) -replace ".fing" , ""
    $fingerprint_text = Get-Content $fingerprint.FullName;

    #Default metadata values
    $title = $basename;
    $artist = $null;
    $album = $null;
    $date = $null;
    $genre = $null;
    $cover = $null;

    $metaFile = "$metadataDirectory/$basename.meta.txt";
    if(Test-Path $metaFile)
    {
        $metadata = ConvertFrom-StringData (Get-Content $metaFile -Encoding UTF8 | Select-Object -skip 1 | Out-String);
        if($null -ne $metadata['title']) {$title = $metadata['title'] }
        $artist = $metadata['artist'];
        $album = $metadata['album'];
        $date = $metadata['date'];
        $genre = $metadata['genre'];
    }

    $coverFile = "$coversDirectory/$basename.cover.jpg";
    if(Test-Path $coverFile)
    {
        $cover = (Format-Hex $coverFile | Select-Object -Expand Bytes | ForEach-Object { '{0:x2}' -f $_ }) -join '';
    }

    $insertQuery = "INSERT INTO song (
        fingerprint,
        title,
        artist,
        album,
        date,
        genre,
        cover)
    VALUES
        (
            X'$fingerprint_text',
            @title,
            @artist,
            @album,
            @date,
            @genre,
            X'$cover'
        ) "

    $record = @{
        title = $title
        artist = $artist
        album = $album
        date = $date
        genre = $genre
    }

    if(-not $suppressOutput)
    {
        Write-Host $basename
        Write-Host ($record | Out-String)
    }

    Invoke-SqliteQuery -Query $insertQuery -DataSource $databasePath -SqlParameters $record
}

Write-Host "Finished."