param(
	[parameter(Mandatory=$true)][String]$sourceFolder,
	[parameter(Mandatory=$true)][String]$outputFolder	
)

$files = Get-ChildItem -Path $sourceFolder -Recurse -Include ('*.mp3', '*.m4a');

$total = $files.Length;
$current = 1;
foreach($file in $files)
{
    Write-Host "$current out of $total";
    $current++;

    $sourceFilepath = $file.FullName;
    $outFilepath = "$outputFolder/$($file.BaseName).wav";

    $ffmpegCmd = ".\ffmpeg.exe -y -i `"$sourceFilepath`" -sample_fmt s16 -ac 1 `"$outFilepath`"";
    Invoke-Expression $ffmpegCmd 2>&1 | Out-Null;
}

Write-Host "Finished."