@echo off
setlocal

rem
rem The following batch file assumes the following environment:
rem 
rem		JAVA_HOME - Root directory of your JDK 8 environment
rem 	ELEKTRON_JAVA_HOME - Root directory of your (EMA) Elektron Java API installation
rem		

set SCRIPT=%0
set BINDIR=build\classes
set DISTDIR=dist
set ELEKTRON_SDK_VERSION=%1
set VALUE_ADD_OBJECTS_FOR_EMA_HOME=./ValueAddObjectsForEMA

set JAVAC="%JAVA_HOME%\bin\javac"
set JAR="%JAVA_HOME%\bin\jar"

set CLASSPATH=%BINDIR%;%VALUE_ADD_OBJECTS_FOR_EMA_HOME%/dist/ValueAddObjectsForEMA.jar;lib/commons-cli-1.4.jar;lib/json-20160810.jar

if not exist %VALUE_ADD_OBJECTS_FOR_EMA_HOME% goto :NO_SUBMODULE
if not exist %VALUE_ADD_OBJECTS_FOR_EMA_HOME%\build-with-esdk-%ELEKTRON_SDK_VERSION%.bat goto :NO_SUBMODULE

cd ValueAddObjectsForEMA
call build-with-esdk-%ELEKTRON_SDK_VERSION%.bat
cd ..

if not exist %BINDIR% (mkdir %BINDIR%)
if not exist %DISTDIR% (mkdir %DISTDIR%)
echo Building the MarketPriceSubscriber application...
%JAVAC% -Xlint -d %BINDIR% src\com\thomsonreuters\platformservices\elektron\tools\marketprice\*.java
if %errorlevel% neq 0 goto :ERROR

echo Building the jar file...
%JAR% cfm %DISTDIR%\MarketPriceSubscriber.jar Manifest.txt -C build\classes .
if %errorlevel% neq 0 goto :ERROR

goto :DONE

:NO_SUBMODULE
echo.
echo The %VALUE_ADD_OBJECTS_FOR_EMA_HOME% submodule is missing or incomplete.
goto :ERROR

:ERROR
echo.
echo Build failed.  Exiting.
goto :EOF

:DONE
echo ** MarketPriceSubscriber project successfully built **
