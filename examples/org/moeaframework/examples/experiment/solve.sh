#!/bin/bash

java -classpath "lib/*:dist:bin" org.moeaframework.analysis.tools.Solve \
    --problem DTLZ2 \
    --algorithm NSGAII \
    --epsilon 0.01 \
    --runtimeFrequency 100 \
    --numberOfEvaluations 10000 \
    --output NSGAII_DTLZ2_Runtime.txt
    
java -classpath "lib/*:dist:bin" org.moeaframework.analysis.tools.ExtractData \
    --problem DTLZ2 \
    --epsilon 0.01 \
    --input NSGAII_DTLZ2_Runtime.txt \
    NFE ElapsedTime Hypervolume GenerationalDistance
