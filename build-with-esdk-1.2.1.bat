@echo off
setlocal

rem
rem The following batch file assumes the following environment:
rem 
rem		JAVA_HOME - Root directory of your JDK 8 environment
rem 	ELEKTRON_JAVA_HOME - Root directory of your (EMA) Elektron Java API installation
rem	

set ELEKTRON_SDK_VERSION=1.2.1

.\build-generic.bat %ELEKTRON_SDK_VERSION%