#!/bin/bash
SCRIPT=$(readlink -f "$0")
SCRIPTPATH=$(dirname "$SCRIPT")
MOEAFRAMEWORK_HOME=$SCRIPTPATH/..
CLASSPATH=$(echo $MOEAFRAMEWORK_HOME/lib/*.jar | sed 's/ /:/g'):.
ARGUMENTS="-Xmx1g -server -classpath $CLASSPATH"

java $ARGUMENTS org.moeaframework.util.Frontend $*
