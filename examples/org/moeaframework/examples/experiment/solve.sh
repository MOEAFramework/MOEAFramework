#!/bin/bash

# Demonstrates using the CLI tools to solve the 2-objective DTLZ2 problem using NSGA-II and
# computing the Hypervolume and Generational Distance metrics.

if ! [ -f lib/MOEAFramework-*.jar -o -f dist/MOEAFramework-*.jar ]; then
    echo "Please build the MOEA Framework using 'ant build-binary' before running this example"
    exit -1
fi

set -ex

java -classpath "lib/*:dist/*" org.moeaframework.analysis.tools.Solve \
    --problem DTLZ2 \
    --algorithm NSGAII \
    --epsilon 0.01 \
    --runtimeFrequency 100 \
    --numberOfEvaluations 10000 \
    --output NSGAII_DTLZ2_Runtime.txt
    
java -classpath "lib/*:dist/*" org.moeaframework.analysis.tools.ExtractData \
    --problem DTLZ2 \
    --epsilon 0.01 \
    --input NSGAII_DTLZ2_Runtime.txt \
    NFE ElapsedTime Hypervolume GenerationalDistance
