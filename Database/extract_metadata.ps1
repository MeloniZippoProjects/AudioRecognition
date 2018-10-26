# Processes .mp3 and .m4a audio files to extract metadata, that is title, artist, etc.

param(
	[parameter(Mandatory=$true)][String]$sourceFolder,
    [parameter(Mandatory=$true)][String]$outputFolder,
    [bool]$overwrite=$true,
    [bool]$suppressOutput=$true	
)

$files = Get-ChildItem -Path $sourceFolder -Recurse -Include ('*.mp3', '*.m4a');

$total = $files.Length;
$current = 1;
foreach($file in $files)
{
    Write-Host "$current out of $total";
    $current++;

    $sourceFile = $file.FullName;
    $metaFile = "$outputFolder/$($file.BaseName).meta.txt";

    $overwriteSwitch = $( If($overwrite) {"-y"} Else {"-n"});
    $ffmpegCmd = ".\ffmpeg.exe $overwriteSwitch -i `"$sourceFile`" -f ffmetadata `"$metaFile`"";
    If($suppressOutput) {
        Invoke-Expression $ffmpegCmd 2>&1 | Out-Null;
    }
    else {
        Invoke-Expression $ffmpegCmd;
    }
}

Write-Host "Finished."