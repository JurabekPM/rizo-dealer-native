param([string]$ProjectRoot = (Split-Path -Parent $PSScriptRoot))

$ErrorActionPreference = 'Stop'
$manifestPath = Join-Path $ProjectRoot 'docs\HANDOFF_BASELINE_SHA256.json'
$manifest = Get-Content -LiteralPath $manifestPath -Raw | ConvertFrom-Json
$differences = @()
foreach ($entry in $manifest.files) {
    $target = Join-Path $ProjectRoot $entry.path
    if (-not (Test-Path -LiteralPath $target -PathType Leaf)) {
        $differences += [pscustomobject]@{ State = 'Missing'; Path = $entry.path }
    } elseif ((Get-FileHash -LiteralPath $target -Algorithm SHA256).Hash.ToLowerInvariant() -ne $entry.sha256) {
        $differences += [pscustomobject]@{ State = 'Changed'; Path = $entry.path }
    }
}
# Report newly added source/config files as well as changed/missing baseline files.
$knownPaths = @{}
foreach ($entry in $manifest.files) { $knownPaths[$entry.path] = $true }
$sourceRoots = @('app\src', 'gradle')
foreach ($relativeRoot in $sourceRoots) {
    $directory = Join-Path $ProjectRoot $relativeRoot
    if (Test-Path -LiteralPath $directory) {
        Get-ChildItem -LiteralPath $directory -File -Recurse | ForEach-Object {
            $relative = $_.FullName.Substring($ProjectRoot.TrimEnd('\').Length + 1).Replace('\', '/')
            if (-not $knownPaths.ContainsKey($relative)) {
                $differences += [pscustomobject]@{ State = 'Added'; Path = $relative }
            }
        }
    }
}
if ($differences.Count -eq 0) {
    Write-Output "Source matches handoff baseline ($($manifest.date)); $($manifest.files.Count) files checked."
} else {
    Write-Output "$($differences.Count) differences from $($manifest.date). Review their task scope; do not blindly restore."
    $differences | Format-Table -AutoSize
}
