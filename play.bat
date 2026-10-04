@echo off
setlocal
pushd "%~dp0"
call gradlew.bat installDist --console=plain
if errorlevel 1 (
    popd
    exit /b 1
)
call "build\install\comp2120-wed10_a3_u7842952_u7922002_u7986490_u8001214\bin\comp2120-wed10_a3_u7842952_u7922002_u7986490_u8001214.bat"
popd
