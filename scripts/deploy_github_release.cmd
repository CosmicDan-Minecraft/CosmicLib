@echo off
SETLOCAL
pushd %~dp0\..
CALL :GET_PROP "build.properties" "mod_version" "modver"
CALL :GET_PROP "build.properties" "mod_revision" "modrev"
SET MOD_VERSION=%modver%.%modrev%
echo [i] Detected current mod version: %MOD_VERSION%
echo This script is for deploying source to GitHub. Full details:
echo 	- Merge commits from master to the github_master branch;
echo 	- Squash all these new commits on github_master with commit message of '%MOD_VERSION%' (current detected version from build.properties);
echo 	- Create a tag of '%MOD_VERSION%';
echo 	- Push to 'github' remote;
echo 	- Merge these changes back to the BitBucket origin.
echo.
echo [#] Press any key three times to go!

pause>nul && pause>nul && pause>nul


git checkout github_master
git merge --squash master
git commit -m "%MOD_VERSION%"
git tag 1.0.0 -m "1.0.0"
git push github HEAD:master

git push origin github_master
git checkout master
git merge github_master
git push origin master

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