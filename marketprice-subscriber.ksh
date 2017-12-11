#!/bin/ksh

#
# The following batch file assumes the following environment:
# 
#	JAVA_HOME - Root directory of your JDK 8 environment
#

JAVA=$JAVA_HOME/bin/java
DISTDIR=./dist
VALUE_ADD_OBJECTS_FOR_EMA_HOME=./ValueAddObjectsForEMA
LOGGINGCONFIGPATH=-Djava.util.logging.config.file=./logging.properties
CLASSPATH=$DISTDIR/MarketPriceSubscriber.jar:$VALUE_ADD_OBJECTS_FOR_EMA_HOME/dist/ValueAddObjectsForEMA.jar:lib/commons-cli-1.4.jar:lib/json-20160810.jar

function run
{
   $JAVA $LOGGINGCONFIGPATH -cp $CLASSPATH com.thomsonreuters.platformservices.elektron.tools.marketprice.MarketPriceSubscriber $*
}

run

