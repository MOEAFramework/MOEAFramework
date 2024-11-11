# Command Line Tools

This document details the available command line tools.  See the usage for how to construct the command
and arguments.  Adding the `-h` / `--help` flag to any command will display this usage information.

## BuildProblem

<!-- help:src/org/moeaframework/builder/BuildProblem.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.builder.BuildProblem [-c <arg>] [--classpath <arg>] [-d <arg>] [-f
       <arg>] [-h] [-l <arg>] -n <arg> -o <arg> [--overwrite] -p <arg> [--package <arg>] [-u <arg>]

Constructs the scaffolding for a natively-compiled problem in a target language.  The following options are available:

 -c,--numberOfConstraints <arg>   The number of constraints (default: 0)
    --classpath <arg>             If set, sets the classpath used for compiling and running Java programs
 -d,--directory <arg>             Changes the directory where the files are generated (default: native/)
 -f,--functionName <arg>          The name of the native function (must be a valid function name)
 -h,--help                        Display help information
 -l,--lowerBound <arg>            Sets the lower bound for each real-valued decision variable (default: 0.0)
 -n,--numberOfVariables <arg>     The number of real-valued decision variables
 -o,--numberOfObjectives <arg>    The number of objectives
    --overwrite                   If set, overwrites any existing content in the directory
 -p,--problemName <arg>           The name of the problem (must be a valid Java identifier)
    --package <arg>               If set, sets the Java package where the classes are created
 -u,--upperBound <arg>            Sets the upper bound for each real-valued decision variable (default: 1.0)
```

## CalculateIndicator

<!-- help:src/org/moeaframework/analysis/tools/CalculateIndicator.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.CalculateIndicator [-b <name>] [-e <e1,e2,...>] [-h] -i
       <name> [-o <file>] [-r <file>]

Calculates the indicator value for an approximation set.  The following options are available:

 -b,--problem <name>        Problem name
 -e,--epsilon <e1,e2,...>   Epsilon values for epsilon-dominance
 -h,--help                  Display help information
 -i,--indicator <name>      The name of the indicator (e.g., hypervolume)
 -o,--output <file>         Output file
 -r,--reference <file>      Reference set file
```

## EndOfRunEvaluator

<!-- help:src/org/moeaframework/analysis/tools/EndOfRunEvaluator.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.EndOfRunEvaluator -a <name> [-b <name>] [-e <e1,e2,...>]
       [--force] [-h] -i <file> -o <file> [--overwrite] -p <file> [-s <value>] [-X <p1=v1;p2=v2;...>]

Evaluates an optimization algorithm on the specified problem, storing end-of-run approximation sets.  The following
options are available:

 -a,--algorithm <name>               Algorithm name
 -b,--problem <name>                 Problem name
 -e,--epsilon <e1,e2,...>            Epsilon values for epsilon-dominance
    --force                          Continue processing if the file timestamp check fails
 -h,--help                           Display help information
 -i,--input <file>                   Parameter samples
 -o,--output <file>                  Output file
    --overwrite                      Overwrite the output file if it exists
 -p,--parameterFile <file>           Parameter description file
 -s,--seed <value>                   Random number seed
 -X,--properties <p1=v1;p2=v2;...>   Fixed algorithm properties
```

## ExtractData

<!-- help:src/org/moeaframework/analysis/tools/ExtractData.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.ExtractData [-b <name>] [-e <e1,e2,...>] [-h] -i <file>
       [-n] [-o <file>] [-r <file>] [-s <value>]

Extracts metadata and/or performance metrics from a result file, storing the data in a spreadsheet-like format.  The
following options are available:

 -b,--problem <name>        Problem name
 -e,--epsilon <e1,e2,...>   Epsilon values for epsilon-dominance
 -h,--help                  Display help information
 -i,--input <file>          Input file
 -n,--noheader              Do not print header line
 -o,--output <file>         Output file
 -r,--reference <file>      Reference set file
 -s,--separator <value>     Separator between entries
```

## MetricsAnalysis

<!-- help:src/org/moeaframework/analysis/tools/MetricsAnalysis.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.MetricsAnalysis [-b <width>] [-c] [-e] [-h] -i <file> -m
       <value> [-o <file>] -p <file> [-t <percent>]

Calculates the best, attainment, efficiency and controllability metrics.  The following options are available:

 -b,--band <width>           NFE band width for calculating efficiency
 -c,--controllability        Include controllability calculation
 -e,--efficiency             Include efficiency calculation
 -h,--help                   Display help information
 -i,--parameters <file>      Parameter samples
 -m,--metric <value>         Column in metric file to evaluate
 -o,--output <file>          Output file
 -p,--parameterFile <file>   Parameter file
 -t,--threshold <percent>    Attainment threshold
```

## MetricsEvaluator

<!-- help:src/org/moeaframework/analysis/tools/MetricsEvaluator.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.MetricsEvaluator [-b <name>] [-e <e1,e2,...>] [--force]
       [-h] -i <file> -o <file> [--overwrite] [-r <file>]

Evaluates the approximation sets stored in a result file, outputting a metric file containing the hypervolume,
generational distance, inverted generational distance, spacing, additive epsilon-indicator, and maximum Pareto front
error performance indicators.  The following options are available:

 -b,--problem <name>        Problem name
 -e,--epsilon <e1,e2,...>   Epsilon values for epsilon-dominance
    --force                 Continue processing if the file timestamp check fails
 -h,--help                  Display help information
 -i,--input <file>          Input result file
 -o,--output <file>         Output metric file
    --overwrite             Overwrite the output file if it exists
 -r,--reference <file>      Reference set file
```

## ReferenceSetGenerator

<!-- help:src/org/moeaframework/analysis/tools/ReferenceSetGenerator.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.ReferenceSetGenerator [-b <name>] [-e <e1,e2,...>] [-h]
       -n <value> [-o <file>] [-s <value>]

Generates a reference set for any problem whose analytical solution is known.  The following options are available:

 -b,--problem <name>           Problem name
 -e,--epsilon <e1,e2,...>      Epsilon values for epsilon-dominance
 -h,--help                     Display help information
 -n,--numberOfPoints <value>   Number of points to generate
 -o,--output <file>            Output file
 -s,--seed <value>             Random number seed
```

## ResultFileConverter

<!-- help:src/org/moeaframework/analysis/tools/ResultFileConverter.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.ResultFileConverter [-b <name>] -f <type> [-h] -i <file>
       [-o <file>]

Converts a result file into a different file format.  The following options are available:

 -b,--problem <name>   Problem name
 -f,--format <type>    The output file format (Plaintext, Markdown, Latex, CSV, ARFF)
 -h,--help             Display help information
 -i,--input <file>     Input file
 -o,--output <file>    Output file
```

## ResultFileMerger

<!-- help:src/org/moeaframework/analysis/tools/ResultFileMerger.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.ResultFileMerger [-b <name>] [-e <e1,e2,...>] [-h] -o
       <file>

Merges the approximation sets contained in one or more result files to produce the combined reference set.  The
following options are available:

 -b,--problem <name>        Problem name
 -e,--epsilon <e1,e2,...>   Epsilon values for epsilon-dominance
 -h,--help                  Display help information
 -o,--output <file>         Output file containing the merged set
```

## ResultFileSeedMerger

<!-- help:src/org/moeaframework/analysis/tools/ResultFileSeedMerger.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.ResultFileSeedMerger [-b <name>] [-e <e1,e2,...>] [-h]
       -o <file>

Merges the approximation sets contained in one or more result files across each seed, where each result file is
generated by a different seed.  Unlike ResultFileMerger that merges all approximation sets into one reference set, this
utility merges each entry across its seeds.  The output will contain N approximation sets if the inputs all contain N
approximation sets.  The following options are available:

 -b,--problem <name>        Problem name
 -e,--epsilon <e1,e2,...>   Epsilon values for epsilon-dominance
 -h,--help                  Display help information
 -o,--output <file>         Output file
```

## ResultFileValidator

<!-- help:src/org/moeaframework/analysis/tools/ResultFileValidator.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.ResultFileValidator [-b <name>] -c <N> [-h] [-o <file>]

Validates the number of approximation sets stored in a result file.  The following options are available:

 -b,--problem <name>   Problem name
 -c,--count <N>        The expected number of entries
 -h,--help             Display help information
 -o,--output <file>    Output file
```

## RuntimeEvaluator

<!-- help:src/org/moeaframework/analysis/tools/RuntimeEvaluator.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.RuntimeEvaluator -a <name> [-b <name>] [-e <e1,e2,...>]
       [-f <nfe>] [-h] -i <file> -o <file> -p <file> [-s <value>] [-X <p1=v1;p2=v2;...>]

Records the approximation set at a fixed sampling frequency from each parameterization.  The following options are
available:

 -a,--algorithm <name>               Algorithm name
 -b,--problem <name>                 Problem name
 -e,--epsilon <e1,e2,...>            Epsilon values for epsilon-dominance
 -f,--frequency <nfe>                The sampling frequency in function evaluations
 -h,--help                           Display help information
 -i,--input <file>                   Parameter samples
 -o,--output <file>                  Output file name format with %d replaced by the run index (e.g., result_%d.dat)
 -p,--parameterFile <file>           Parameter description file
 -s,--seed <value>                   Random number seed
 -X,--properties <p1=v1;p2=v2;...>   Fixed algorithm properties
```

## SampleGenerator

<!-- help:src/org/moeaframework/analysis/tools/SampleGenerator.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.SampleGenerator [-h] -m <name> -n <value> [-o <file>] -p
       <file> [-s <value>]

Generates parameter samples for running the Evaluator.  The following options are available:

 -h,--help                      Display help information
 -m,--method <name>             Sample generation method (uniform, latin, sobol, saltelli)
 -n,--numberOfSamples <value>   Number of samples
 -o,--output <file>             Output file
 -p,--parameterFile <file>      Parameter file
 -s,--seed <value>              Random number generator seed
```

## SimpleStatistics

<!-- help:src/org/moeaframework/analysis/tools/SimpleStatistics.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.SimpleStatistics [-h] [-i] [-m <arg>] [-o <file>] [-x
       <value>]

Calculates statistics on a metric file produced by Evaluator or ResultFileEvaluator.  The following options are
available:

 -h,--help              Display help information
 -i,--ignore            Ignore infinity and NaN values
 -m,--mode <arg>        Either minimum, maximum, average, stdev, count
 -o,--output <file>     Output file
 -x,--maximum <value>   Replaces infinity values with the given value
```

## SobolAnalysis

<!-- help:src/org/moeaframework/analysis/tools/SobolAnalysis.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.SobolAnalysis [-h] -i <file> -m <value> [-o <file>] -p
       <file> [-r <number>] [-s]

Performs Sobol' global variance analysis.  The following options are available:

 -h,--help                   Display help information
 -i,--input <file>           Model output file
 -m,--metric <value>         Column in model output to evaluate
 -o,--output <file>          Output file
 -p,--parameterFile <file>   Parameter description file
 -r,--resamples <number>     Number of resamples when computing bootstrap confidence intervals
 -s,--simple                 Simple output format
```

## Solve

<!-- help:src/org/moeaframework/analysis/tools/Solve.java [:-2] -->

```
usage: java -classpath "lib/*" org.moeaframework.analysis.tools.Solve -a <name> [-b <name>] [-c <value>] [-e
       <e1,e2,...>] -f <file> [-F <value>] [-h] [-H <value>] [-l <v1,v2,...>] -n <value> [-o <value>] [-P <value>] [-r
       <value>] [-s <value>] [-S] [-t <trials>] [-u <v1,v2,...>] [-v <v1,v2,...>] [-x <p1=v1;p2=v2;...>]

Solves an optimization problem using any optimization algorithm supported by the MOEA Framework.  The following options
are available:

 -a,--algorithm <name>               Algorithm name
 -b,--problem <name>                 Problem name
 -c,--constraints <value>            Number of constraints (default 0)
 -e,--epsilon <e1,e2,...>            Epsilon values for epsilon-dominance
 -f,--output <file>                  Output file
 -F,--runtimeFrequency <value>       Output population every N evaluations (default 100)
 -h,--help                           Display help information
 -H,--hostname <value>               Hostname used when using sockets (default localhost)
 -l,--lowerBounds <v1,v2,...>        Lower bounds of real-valued decision variables
 -n,--numberOfEvaluations <value>    Maximum number of evaluations
 -o,--objectives <value>             Number of objectives
 -P,--port <value>                   Port used when using sockets (default 16801)
 -r,--retries <value>                The number of retries when establishing a socket connection (default 5)
 -s,--seed <value>                   Random number seed
 -S,--useSocket                      Communicate with external problem using sockets
 -t,--test <trials>                  Runs a few trials to test the connection with the external problem
 -u,--upperBounds <v1,v2,...>        Upper bounds of real-valued decision variables
 -v,--variables <v1,v2,...>          Comma-separated list of decision variable specifications.  Use "R(<lb>:<ub>)" for
                                     real-valued, "B(<length>)" for binary, "I(<lb>:<ub>)" for integer-valued, and
                                     "P(<length>)" for permutations
 -x,--properties <p1=v1;p2=v2;...>   Algorithm properties
```
