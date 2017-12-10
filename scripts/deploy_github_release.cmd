@echo off
SETLOCAL
pushd %~dp0\..
CALL :GET_PROP "build.properties" "mod_version" "modver"
CALL :GET_PROP "build.properties" "mod_revision" "modrev"
SET MOD_VERSION=%modver%.%modrev%
echo [i] Detected current mod version: %MOD_VERSION%
echo This script is for deploying source to GitHub. Full details:
echo 	- Merge commits from master to the release branch;
echo 	- Squash all these new commits on github_master with commit message of '%MOD_VERSION%' (current detected version from build.properties);
echo 	- Create a tag of '%MOD_VERSION%';
echo 	- Push to 'github' remote;
echo 	- Merge these changes back to the BitBucket origin.
echo.
echo [#] Press any key three times to go!

pause>nul && pause>nul && pause>nul

@echo on
:: checkout release and merge our changes from master...
git checkout release
git merge master

:: checkout github_master and merge our changes from release...
git checkout github_master
git merge --squash release
git commit -m "%MOD_VERSION%"
git tag %MOD_VERSION% -m "%MOD_VERSION%"

:: push github_master to github origin
git push github HEAD:master

:: push changes back to release and master
git push origin github_master

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