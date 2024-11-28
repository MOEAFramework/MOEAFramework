# Demonstrates a simple sensitivity analysis study to measure the first, second, and total-order
# effects of NSGA-II's parameters with respect to the Hypervolume metric.  For demonstration
# purposes, this uses '--numberOfSamples 500', but real experiments should consider larger values.

./cli SampleGenerator \
    --parameterFile examples/org/moeaframework/examples/sensitivity/NSGAII_Params.txt \
    --method saltelli \
    --numberOfSamples 500 \
    --output NSGAII_Samples.txt

./cli EndOfRunEvaluator \
    --parameterFile examples/org/moeaframework/examples/sensitivity/NSGAII_Params.txt \
    --input NSGAII_Samples.txt \
    --output NSGAII_DTLZ2_Results.txt \
    --problem DTLZ2 \
    --algorithm NSGAII \
    --epsilon 0.01

./cli MetricsEvaluator \
    --input NSGAII_DTLZ2_Results.txt \
    --output NSGAII_DTLZ2_Metrics.txt \
    --problem DTLZ2 \
    --epsilon 0.01
    
./cli MetricsAnalysis \
    --controllability \
    --efficiency \
    --band 100 \
    --parameterFile examples/org/moeaframework/examples/sensitivity/NSGAII_Params.txt \
    --parameters NSGAII_Samples.txt \
    --metric InvertedGenerationalDistance \
    NSGAII_DTLZ2_Metrics.txt

./cli SobolAnalysis \
    --parameterFile examples/org/moeaframework/examples/sensitivity/NSGAII_Params.txt \
    --input NSGAII_DTLZ2_Metrics.txt \
    --metric Hypervolume
