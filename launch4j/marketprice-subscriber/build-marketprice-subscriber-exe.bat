@echo off
setlocal

rem
rem The following batch file assumes the following environment:
rem 
rem		LAUNCH4J_HOME - Root directory of the Launch4j tool
rem		

set SCRIPT=%0
set DISTDIR=..\..\dist\marketprice-subscriber

rem Launch4J Compiler.
set LAUNCH4JC="%LAUNCH4J_HOME%\launch4jc"

if not exist %DISTDIR% (mkdir %DISTDIR%)

echo Building marketprice-subscriber.exe...
%LAUNCH4JC%  config.xml
if %errorlevel% neq 0 goto :ERROR

echo Copying configuration files and README.txt...
copy ..\..\EmaConfig.xml %DISTDIR%
copy ..\..\logging.properties %DISTDIR%
copy README.txt %DISTDIR%
if %errorlevel% neq 0 goto :ERROR

goto :DONE

:ERROR
echo.
echo Build failed.  Exiting.
goto :EOF

:DONE
echo.
echo Done.
