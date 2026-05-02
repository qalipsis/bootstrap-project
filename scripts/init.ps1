# Initialize the bootstrap project for your own use.
#
# Usage:
#   .\scripts\init.ps1                                # interactive
#   .\scripts\init.ps1 -Name acme-tests `             # non-interactive
#                      -Package com.acme.loadtest `
#                      -Image acme/loadtest `
#                      -Group com.acme `
#                      -Version 0.1.0-SNAPSHOT
[CmdletBinding()]
param(
    [string] $Name,
    [string] $Package,
    [string] $Image,
    [string] $Group,
    [string] $Version
)

$ErrorActionPreference = 'Stop'
$root = Resolve-Path (Join-Path $PSScriptRoot '..')
Set-Location $root

function Read-Default([string] $promptText, [string] $default) {
    $answer = Read-Host "$promptText [$default]"
    if ([string]::IsNullOrWhiteSpace($answer)) { return $default } else { return $answer }
}

if (-not $Name)    { $Name    = Read-Default 'Project name (Gradle rootProject.name)' 'my-load-tests' }
if (-not $Package) { $Package = Read-Default 'Base Kotlin package'                     'com.example.loadtest' }
if (-not $Image)   { $Image   = Read-Default 'Docker image name'                       ("$($Name -split '-' | Select-Object -First 1)/$Name") }
if (-not $Group)   { $Group   = Read-Default 'Maven/Gradle group'                      $Package }
if (-not $Version) { $Version = Read-Default 'Initial version'                         '0.1.0-SNAPSHOT' }

Write-Host ''
Write-Host 'About to apply:'
Write-Host "  rootProject.name = $Name"
Write-Host "  group            = $Group"
Write-Host "  version          = $Version"
Write-Host "  base package     = $Package"
Write-Host "  Docker image     = $Image"
Write-Host ''
$confirm = Read-Host 'Continue? [y/N]'
if ($confirm -notmatch '^[Yy]$') { Write-Host 'Aborted.'; exit 1 }

function Replace-InFile([string] $path, [string] $pattern, [string] $replacement) {
    $content = Get-Content -Raw -Encoding UTF8 $path
    $updated = [regex]::Replace($content, $pattern, $replacement)
    Set-Content -Encoding UTF8 -Path $path -Value $updated -NoNewline
}

# 1. settings.gradle.kts
Replace-InFile 'settings.gradle.kts' '(?m)^rootProject\.name = ".*"' "rootProject.name = `"$Name`""

# 2. build.gradle.kts
Replace-InFile 'build.gradle.kts' '(?m)^group = ".*"'      "group = `"$Group`""
Replace-InFile 'build.gradle.kts' '(?m)^version = ".*"'    "version = `"$Version`""
Replace-InFile 'build.gradle.kts' '(?m)^(\s*name = )".*"'  "`$1`"$Image`""

# 3. Move Kotlin sources.
$oldPackage = 'my.bootstrap'
$oldPath    = Join-Path 'src/main/kotlin' ($oldPackage -replace '\.', '/')
$newPath    = Join-Path 'src/main/kotlin' ($Package    -replace '\.', '/')
$inGit      = (git rev-parse --is-inside-work-tree 2>$null) -eq 'true'

function Move-Package([string] $from, [string] $to) {
    if ((Test-Path $from) -and ($from -ne $to)) {
        New-Item -ItemType Directory -Force (Split-Path $to) | Out-Null
        if ($inGit) { git mv $from $to } else { Move-Item $from $to }
    }
}

Move-Package $oldPath $newPath
Move-Package (Join-Path 'src/test/kotlin' ($oldPackage -replace '\.', '/')) `
             (Join-Path 'src/test/kotlin' ($Package    -replace '\.', '/'))

# 4. Rewrite package declarations and FQN references.
Get-ChildItem -Recurse src -Filter *.kt | ForEach-Object {
    Replace-InFile $_.FullName "(?m)^package $oldPackage" "package $Package"
    Replace-InFile $_.FullName "\b$oldPackage\b"          $Package
}

Write-Host ''
Write-Host 'Done. Verify with:'
Write-Host '    ./gradlew clean build qalipsisRunAllScenarios'
Write-Host ''

$del = Read-Host 'Delete this init script (and scripts/init.sh)? [Y/n]'
if ($del -notmatch '^[Nn]$') {
    Remove-Item -Force 'scripts/init.sh', 'scripts/init.ps1' -ErrorAction SilentlyContinue
    if ((Get-ChildItem 'scripts' -ErrorAction SilentlyContinue).Count -eq 0) {
        Remove-Item -Force 'scripts'
    }
    Write-Host 'Removed scripts/.'
}
