#!/bin/ksh

#
# The following batch file assumes the following environment:
# 
#	JAVA_HOME - Root directory of your JDK 8 environment
# 	ELEKTRON_JAVA_HOME - Root directory of your (EMA) Elektron Java API installation
#

ELEKTRON_SDK_VERSION=1.1.x

function run
{
   ./build-generic.bat $ELEKTRON_SDK_VERSION
}

run

