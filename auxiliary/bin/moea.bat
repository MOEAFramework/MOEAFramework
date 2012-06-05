@echo off
set SCRIPTPATH=%~dp0
set MOEAFRAMEWORK_HOME=%SCRIPTPATH%..
set ARGUMENTS=-Xmx1g -server -Djava.ext.dirs=%MOEAFRAMEWORK_HOME%\lib -classpath .

java %ARGUMENTS% org.moeaframework.util.Frontend %*
