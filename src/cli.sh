#!/usr/bin/env bash

set -e

ROOT="$(dirname -- "${BASH_SOURCE[0]}")"
CLASSPATH="${ROOT}/lib/*:${ROOT}/examples"
ARGS=("-Dorg.moeaframework.util.cli.executable=$0")

if [ -d "${ROOT}/lib" ]; then
	MOEAFRAMEWORK_LIB="$(find "${ROOT}/lib" -type f -name "MOEAFramework-*.jar" | head -n 1)"
fi

if [ -z "${MOEAFRAMEWORK_LIB}" ]; then
	if [ -d "${ROOT}/dist" ]; then
		MOEAFRAMEWORK_LIB="$(find "${ROOT}/dist" -type f -name "MOEAFramework-*.jar" | \
			grep -v "MOEAFramework-.*-Test.jar" | sort --version-sort --reverse | head -n 1)"
	fi
	
	if [ -n "${MOEAFRAMEWORK_LIB}" ]; then
		CLASSPATH="${CLASSPATH}:${MOEAFRAMEWORK_LIB}"
	elif [ -d "${ROOT}/bin" ]; then
		CLASSPATH="${CLASSPATH}:${ROOT}/bin"
	elif [ -d "${ROOT}/build" ]; then
		CLASSPATH="${CLASSPATH}:${ROOT}/build"
	fi
fi

if [ "$(uname -s)" == "Darwin" ]; then
	ARGS+=("-Xdock:name=MOEA Framework")
fi

if ! java -classpath "${CLASSPATH}" "${ARGS[@]}" org.moeaframework.analysis.tools.Main --version >/dev/null 2>&1; then
	echo "Unable to run MOEA Framework command line tools!" >&2
	echo "If building from source code, please run 'ant package-binary' first." >&2
	exit -1
fi

java -classpath "${CLASSPATH}" "${ARGS[@]}" org.moeaframework.analysis.tools.Main "$@"
