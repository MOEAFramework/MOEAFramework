@echo off

java -classpath "%~dp0/lib/*;%~dp0/examples" -D"cli.executable=%~n" org.moeaframework.analysis.tools.Main %*