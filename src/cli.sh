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

java -classpath "${CLASSPATH}" org.moeaframework.analysis.tools.Main "$@"
