#!/usr/bin/env bash

if [ -f "moeaframework.properties" ]; then
	ROOT="$(pwd)"
elif [ -n "${MOEAFRAMEWORK_ROOT}" ]; then
	ROOT="${MOEAFRAMEWORK_ROOT}"
fi

if [ -z "${ROOT}" ]; then
	echo "Unable to locate MOEA Framework installation directory!"
	echo "Set the MOEAFRAMEWORK_ROOT environment variable."
	return -1
fi

if [ ! -d "lib/" ]; then
	echo "Unable to locate MOEA Framework lib directory!"
	return -1
fi

CLASSPATH="lib/*"

if [ -d "bin/" ]; then
	CLASSPATH="${CLASSPATH}:bin"
elif [ -d "build/" ]; then
	CLASSPATH="${CLASSPATH}:build"
fi

if ! java -classpath "${CLASSPATH}" org.moeaframework.analysis.tools.Main --version > /dev/null; then
	echo "Unable to run MOEA Framework command line tools!"
	echo "If building from source code, please run 'ant package-binary' first."
	return -1
fi

java -classpath "${CLASSPATH}" org.moeaframework.analysis.tools.Main "$@"
