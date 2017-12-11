@echo off
setlocal

rem
rem The following batch file assumes the following environment:
rem 
rem		JAVA_HOME - Root directory of your JDK 8 environment
rem	

set JAVA="%JAVA_HOME%\bin\java"
set DISTDIR=./dist
set VALUE_ADD_OBJECTS_FOR_EMA_HOME=./ValueAddObjectsForEMA
set LOGGINGCONFIGPATH=-Djava.util.logging.config.file=.\logging.properties
set CLASSPATH=%DISTDIR%/MarketPriceSubscriber.jar;%VALUE_ADD_OBJECTS_FOR_EMA_HOME%/dist/ValueAddObjectsForEMA.jar;lib/commons-cli-1.4.jar;lib/json-20160810.jar

%JAVA%  %LOGGINGCONFIGPATH% -cp %CLASSPATH% com.thomsonreuters.platformservices.elektron.tools.marketprice.MarketPriceSubscriber %*
