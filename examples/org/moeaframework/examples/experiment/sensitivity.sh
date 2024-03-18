if [ ! -f NSGAII_Samples.txt ]; then
    java -classpath "lib/*:dist:bin" org.moeaframework.analysis.tools.SampleGenerator \
        --parameterFile examples/org/moeaframework/examples/experiment/NSGAII_Params.txt \
        --method saltelli \
        --numberOfSamples 1000 \
        --output NSGAII_Samples.txt
fi

java -classpath "lib/*:dist:bin" org.moeaframework.analysis.tools.Evaluator \
    --parameterFile examples/org/moeaframework/examples/experiment/NSGAII_Params.txt \
    --input NSGAII_Samples.txt \
    --output NSGAII_DTLZ2_Results.txt \
    --problem DTLZ2 \
    --algorithm NSGAII \
    --epsilon 0.01

java -classpath "lib/*:dist:bin" org.moeaframework.analysis.tools.ResultFileEvaluator \
    --input NSGAII_DTLZ2_Results.txt \
    --output NSGAII_DTLZ2_Metrics.txt \
    --problem DTLZ2 \
    --epsilon 0.01 \
    --force
    
java -classpath "lib/*:dist:bin" org.moeaframework.analysis.tools.ResultFileInfo \
    --problem DTLZ2 \
    NSGAII_DTLZ2_Results.txt

java -classpath "lib/*:dist:bin" org.moeaframework.analysis.tools.SobolAnalysis \
    --parameterFile examples/org/moeaframework/examples/experiment/NSGAII_Params.txt \
    --input NSGAII_DTLZ2_Metrics.txt \
    --metric hypervolume
