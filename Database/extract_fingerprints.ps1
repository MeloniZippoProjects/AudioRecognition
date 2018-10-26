param(
	[parameter(Mandatory=$true)][String]$sourceFolder,
	[parameter(Mandatory=$true)][String]$outputFolder	
)

$files = Get-ChildItem -Path $sourceFolder -Recurse -Filter '*.wav';

$total = $files.Length;
$current = 1;
foreach($file in $files)
{
    Write-Host "$current out of $total";
    $current++;

    $fingerprintGeneratorPath = "./fingerprint_generator.jar";
    $fingerprintCmd = "java -jar `"$fingerprintGeneratorPath`" `"$($file.FullName)`"";
    $outfile = "$outputFolder/$($file.BaseName).fing.txt";
    Invoke-Expression $fingerprintCmd | Out-File $outfile;
}

Write-Host "Finished."