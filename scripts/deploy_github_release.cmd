@echo off
SETLOCAL
pushd %~dp0\..
CALL :GET_PROP "version.properties" "major" "ver_major"
CALL :GET_PROP "version.properties" "minor" "ver_minor"
CALL :GET_PROP "version.properties" "revision" "ver_revision"
SET MOD_VERSION=%ver_major%.%ver_minor%.%ver_revision%
echo [i] Detected current mod version: %MOD_VERSION%
echo This script is for deploying source to GitHub. Full details:
echo 	- Create a tag of '%MOD_VERSION%';
echo 	- Push to 'github' remote;
echo 	- Merge these changes back to the BitBucket origin branches.
echo.
echo [!] Make SURE you are currently on the release branch and that you have rebased/squashed history to however you want it to appear on GitHub!
echo [#] Press any key three times to go!

pause>nul && pause>nul && pause>nul

@echo on
git commit -m "%MOD_VERSION%"
git tag %MOD_VERSION% -m "%MOD_VERSION%"

:: push github_master to github origin
git push github HEAD:master
git push github %MOD_VERSION%

:: push changes back to release and master
git push origin github_master
git push github %MOD_VERSION%

git checkout release
git merge github_master
git push origin release

git checkout master
git merge release
git push origin master

@echo off
echo.
echo.
echo.
echo Done! Press any key to close.
pause>nul

:: SOURCE: http://almanachackers.com/blog/2009/12/31/reading-an-ini-config-file-from-a-batch-file/
:GET_PROP
:: %1 = name of ini file to search in.
:: %2 = search term to look for
:: %3 = variable to place search result
FOR /F "eol=; eol=# tokens=1,2* delims==" %%i in ('findstr /b /l /i %~2= %1') DO set %~3=%%~j
GOTO :EOF