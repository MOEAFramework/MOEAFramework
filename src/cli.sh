#!/usr/bin/env bash

ROOT="$(dirname -- "${BASH_SOURCE[0]}")"

if [ ! -d "${ROOT}/lib/" ]; then
	echo "Unable to locate MOEA Framework root directory at ${ROOT}"
	exit -1
fi

CLASSPATH="${ROOT}/lib/*"

if [ -d "${ROOT}/bin/" ]; then
	CLASSPATH="${CLASSPATH}:${ROOT}/bin"
elif [ -d "${ROOT}/build/" ]; then
	CLASSPATH="${CLASSPATH}:${ROOT}/build"
fi

if ! java -classpath "${CLASSPATH}" org.moeaframework.analysis.tools.Main --version > /dev/null 2&>1; then
	echo "Unable to run MOEA Framework command line tools!"
	echo "If building from source code, please run 'ant build-binary' first."
	exit -1
fi

java -classpath "${CLASSPATH}" org.moeaframework.analysis.tools.Main "$@"
