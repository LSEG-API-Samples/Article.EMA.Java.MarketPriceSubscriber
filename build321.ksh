#!/bin/ksh

#
# The following batch file assumes the following environment:
# 
#	JAVA_HOME - Root directory of your JDK 8 environment
# ELEKTRON_JAVA_HOME - Root directory of your (EMA) Elektron Java API installation
#

BINDIR=build/classes
DISTDIR=dist
VALUE_ADD_OBJECTS_FOR_EMA_HOME=./ValueAddObjectsForEMA

JAVAC=$JAVA_HOME/bin/javac
JAR=$JAVA_HOME/bin/jar

CLASSPATH=$BINDIR:$VALUE_ADD_OBJECTS_FOR_EMA_HOME/dist/ValueAddObjectsForEMA.jar:lib/commons-cli-1.4.jar:lib/json-20160810.jar

if [ ! -d "$VALUE_ADD_OBJECTS_FOR_EMA_HOME" ]; then
  printf "The $VALUE_ADD_OBJECTS_FOR_EMA_HOME submodule is missing or incomplete.\n"
  exit -1
fi

cd $VALUE_ADD_OBJECTS_FOR_EMA_HOME
./build321.ksh
cd ..

if [ ! -d $BINDIR ]; then
  mkdir -p $BINDIR
fi
if [ ! -d $DISTDIR ]; then
  mkdir -p $DISTDIR
fi

function build
{
   printf "Building the ChainExpander application...\n"
   $JAVAC -Xlint -cp $CLASSPATH -d $BINDIR src/com/thomsonreuters/platformservices/elektron/tools/chain/*.java; ret=$?
   if [ $ret != 0 ]; then
      printf "Build failed.  Exiting\n"
      exit $ret
   fi

   printf "Building jar file...\n"
   $JAR cfm $DISTDIR/ChainExpander.jar Manifest.txt -C build/classes .; ret=$?
   if [ $ret != 0 ]; then
      printf "Build failed.  Exiting\n"
      exit $ret
   fi
}

build

printf "\nDone.\n"
