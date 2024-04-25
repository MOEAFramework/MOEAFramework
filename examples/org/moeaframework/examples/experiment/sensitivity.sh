#!/bin/bash

# Demonstrates a simple sensitivity analysis study to measure the first, second, and total-order
# effects of NSGA-II's parameters with respect to the Hypervolume metric.  For demonstration
# purposes, this uses '--numberOfSamples 500', but real experiments should consider larger values.

if ! [ -f lib/MOEAFramework-*.jar -o -f dist/MOEAFramework-*.jar ]; then
    echo "Please build the MOEA Framework using 'ant build-binary' before running this example"
    exit -1
fi

set -ex

if [ ! -f NSGAII_Samples.txt ]; then
    java -classpath "lib/*:dist/*" org.moeaframework.analysis.tools.SampleGenerator \
        --parameterFile examples/org/moeaframework/examples/experiment/NSGAII_Params.txt \
        --method saltelli \
        --numberOfSamples 500 \
        --output NSGAII_Samples.txt
fi

java -classpath "lib/*:dist/*" org.moeaframework.analysis.tools.Evaluator \
    --parameterFile examples/org/moeaframework/examples/experiment/NSGAII_Params.txt \
    --input NSGAII_Samples.txt \
    --output NSGAII_DTLZ2_Results.txt \
    --problem DTLZ2 \
    --algorithm NSGAII \
    --epsilon 0.01

java -classpath "lib/*:dist/*" org.moeaframework.analysis.tools.ResultFileEvaluator \
    --input NSGAII_DTLZ2_Results.txt \
    --output NSGAII_DTLZ2_Metrics.txt \
    --problem DTLZ2 \
    --epsilon 0.01
    
java -classpath "lib/*:dist/*" org.moeaframework.analysis.tools.ResultFileInfo \
    --problem DTLZ2 \
    NSGAII_DTLZ2_Results.txt

java -classpath "lib/*:dist/*" org.moeaframework.analysis.tools.SobolAnalysis \
    --parameterFile examples/org/moeaframework/examples/experiment/NSGAII_Params.txt \
    --input NSGAII_DTLZ2_Metrics.txt \
    --metric hypervolume
