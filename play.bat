@echo off
setlocal
pushd "%~dp0"
if errorlevel 1 exit /b 1
call gradlew.bat installDist --console=plain --quiet
if errorlevel 1 (
    popd
    exit /b 1
)
if not exist "build\install\prison-game\bin\prison-game.bat" (
    echo The game launcher was not created by Gradle. 1>&2
    popd
    exit /b 1
)
call "build\install\prison-game\bin\prison-game.bat" %*
set "gameExitCode=%errorlevel%"
popd
exit /b %gameExitCode%
