SETLOCAL
pushd %~dp0
SET RELEASEMODE=true
call gradlew build
pause