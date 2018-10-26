# Processes .mp3 and .m4a audio files to convert them to .wav, mono channel, 16 bit samples

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
    $wavFile = "$outputFolder/$($file.BaseName).wav";

    $overwriteSwitch = $( If($overwrite) {"-y"} Else {"-n"});
    $ffmpegCmd = ".\ffmpeg.exe $overwriteSwitch -i `"$sourceFile`" -sample_fmt s16 -ac 1 `"$wavFile`"";
    If($suppressOutput) {
        Invoke-Expression $ffmpegCmd 2>&1 | Out-Null;
    }
    else {
        Invoke-Expression $ffmpegCmd;
    }
}

Write-Host "Finished."