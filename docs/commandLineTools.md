# Command Line Tools

## Usage

To assist in running these command line tools, we provide an "all in one" script.  The syntax is:

```bash
./cli [command]
```

where `[command]` is one of the tool names.  To view the available commands, run:

<!-- bash:.github/workflows/ci.yml [cli-help] -->

```bash
./cli --help
```

For example, here we run `solve` on the 2-objective DTLZ2 problem using NSGA-II, followed by `calc` (abbreviation of
`CalculateIndicators`) to compute the hypervolume metric:

<!-- bash:.github/workflows/ci.yml [cli-solve] -->

```bash
./cli solve --problem DTLZ2 --algorithm NSGAII --numberOfEvaluations 10000 --output NSGAII_DTLZ2_Runtime.txt
./cli calc --problem DTLZ2 --indicator hypervolume NSGAII_DTLZ2_Runtime.txt
```

## Installing CLI Tool

If you plan to use these tools often, you can optionally add the MOEA Framework directory to your system's `PATH` so it
is always available.  To see the recommended commands needed to register the tool, run:

```bash
./cli init
```

## Available Tools

### BuildProblem

<!-- help:src/org/moeaframework/builder/BuildProblem.java [:-2] -->

```
Constructs the scaffolding for a natively-compiled problem in a target language.

Usage: ./cli BuildProblem [options]

The following options are available:

  -c,--numberOfConstraints <arg>   The number of constraints (default: 0)
     --classpath <arg>             If set, sets the classpath used for compiling and running Java programs
  -d,--directory <arg>             Changes the directory where the files are generated (default: native/)
  -f,--functionName <arg>          The name of the native function (must be a valid function name)
  -h,--help                        Display help information
  -l,--language <arg>              The target language (supports "c", "cpp", "fortran", "java", "python", and
                                   "external")
     --lowerBound <arg>            Sets the lower bound for each real-valued decision variable (default: 0.0)
  -n,--numberOfVariables <arg>     The number of real-valued decision variables
  -o,--numberOfObjectives <arg>    The number of objectives
     --overwrite                   If set, overwrites any existing content in the directory
  -p,--problemName <arg>           The name of the problem (must be a valid Java identifier)
     --package <arg>               If set, sets the Java package where the classes are created
     --upperBound <arg>            Sets the upper bound for each real-valued decision variable (default: 1.0)
```

### CalculateIndicator

<!-- help:src/org/moeaframework/analysis/tools/CalculateIndicator.java [:-2] -->

```
Calculates the indicator value for an approximation set.

Usage: ./cli CalculateIndicator [options] <file...>

The following options are available:

  -b,--problem <name>        Problem name
  -e,--epsilon <e1,e2,...>   Epsilon values for epsilon-dominance
  -h,--help                  Display help information
  -i,--indicator <name>      The name of the indicator (e.g., hypervolume)
  -o,--output <file>         Output file
  -r,--reference <file>      Reference set file
```

### DataStoreTool

<!-- help:src/org/moeaframework/analysis/tools/DataStoreTool.java [:-2] -->

```
Access and manage the content of a data store.

Usage: ./cli DataStoreTool [options] <command>

Select one of the available commands:

  copy      Copies the contents of one data store to another.
  create    Creates the data store if one doesn't already exist.
  delete    Deletes the data store, container, or blob.
  details   Show details of data store, container, or blob.
  exists    Checks if the data store, container, or blob exists.
  get       Gets the content of the blob.
  list      List the contents of the data store or container.
  lock      Locks the data store, allowing read-only operations.
  server    Starts a web server providing read-only access.
  set       Sets the content of the blob.
  type      Display the type of the given URI.
  unlock    Unlocks the data store, allowing read and write operations.

The following options are available:

  -h,--help   Display help information
```

### EndOfRunEvaluator

<!-- help:src/org/moeaframework/analysis/tools/EndOfRunEvaluator.java [:-2] -->

```
Evaluates an optimization algorithm on the specified problem, storing end-of-run approximation sets.

Usage: ./cli EndOfRunEvaluator [options]

The following options are available:

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

### LaunchDiagnosticTool

<!-- help:src/org/moeaframework/analysis/diagnostics/LaunchDiagnosticTool.java [:-2] -->

```
Launches the diagnostic tool GUI.

Usage: ./cli LaunchDiagnosticTool [options]

The following options are available:

  -h,--help   Display help information
```

### MetricsAnalysis

<!-- help:src/org/moeaframework/analysis/tools/MetricsAnalysis.java [:-2] -->

```
Calculates the best, attainment, efficiency and controllability metrics.

Usage: ./cli MetricsAnalysis [options] <file...>

The following options are available:

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

### MetricsEvaluator

<!-- help:src/org/moeaframework/analysis/tools/MetricsEvaluator.java [:-2] -->

```
Evaluates the approximation sets stored in a result file.

Usage: ./cli MetricsEvaluator [options]

Outputs a metric file containing the hypervolume, generational distance, inverted generational distance, spacing,
additive epsilon-indicator, and maximum Pareto front error performance indicators.

The following options are available:

  -b,--problem <name>        Problem name
  -e,--epsilon <e1,e2,...>   Epsilon values for epsilon-dominance
     --force                 Continue processing if the file timestamp check fails
  -h,--help                  Display help information
  -i,--input <file>          Input result file
  -o,--output <file>         Output metric file
     --overwrite             Overwrite the output file if it exists
  -r,--reference <file>      Reference set file
```

### MetricsValidator

<!-- help:src/org/moeaframework/analysis/tools/MetricsValidator.java [:-2] -->

```
Validates the number of rows stored in a metrics file.

Usage: ./cli MetricsValidator [options] <file...>

The following options are available:

  -c,--count <N>       The expected number of rows
  -h,--help            Display help information
  -o,--output <file>   Output file
```

### ReferenceSetGenerator

<!-- help:src/org/moeaframework/analysis/tools/ReferenceSetGenerator.java [:-2] -->

```
Generates a reference set for any problem whose analytical solution is known.

Usage: ./cli ReferenceSetGenerator [options]

The following options are available:

  -b,--problem <name>           Problem name
  -e,--epsilon <e1,e2,...>      Epsilon values for epsilon-dominance
  -h,--help                     Display help information
  -n,--numberOfPoints <value>   Number of points to generate
  -o,--output <file>            Output file
  -s,--seed <value>             Random number seed
```

### ResultFileConverter

<!-- help:src/org/moeaframework/analysis/tools/ResultFileConverter.java [:-2] -->

```
Converts a result file into a different file format.

Usage: ./cli ResultFileConverter [options]

The following options are available:

  -b,--problem <name>   Problem name
  -f,--format <fmt>     The output file format (Plaintext, Markdown, Latex, CSV, ARFF)
  -h,--help             Display help information
  -i,--input <file>     Input file
  -o,--output <file>    Output file
```

### ResultFileMerger

<!-- help:src/org/moeaframework/analysis/tools/ResultFileMerger.java [:-2] -->

```
Merges the approximation sets contained in one or more result files to produce the combined reference set.

Usage: ./cli ResultFileMerger [options] <file...>

The following options are available:

  -b,--problem <name>        Problem name
  -e,--epsilon <e1,e2,...>   Epsilon values for epsilon-dominance
  -h,--help                  Display help information
  -o,--output <file>         Output file containing the merged set
```

### ResultFileMetadata

<!-- help:src/org/moeaframework/analysis/tools/ResultFileMetadata.java [:-2] -->

```
Extracts metadata and/or performance metrics from a result file.

Usage: ./cli ResultFileMetadata [options] <field...>

The following options are available:

  -b,--problem <name>        Problem name
  -e,--epsilon <e1,e2,...>   Epsilon values for epsilon-dominance
  -f,--format <fmt>          The output file format (Plaintext, Markdown, Latex, CSV, ARFF)
  -h,--help                  Display help information
  -i,--input <file>          Input file
  -o,--output <file>         Output file
  -r,--reference <file>      Reference set file
```

### ResultFileSeedMerger

<!-- help:src/org/moeaframework/analysis/tools/ResultFileSeedMerger.java [:-2] -->

```
Merges the approximation sets contained in one or more result files across each seed.

Usage: ./cli ResultFileSeedMerger [options] <file...>

Unlike ResultFileMerger that merges all approximation sets into one reference set, this utility merges each entry across
its seeds.  The output will contain N approximation sets if the inputs all contain N approximation sets.

The following options are available:

  -b,--problem <name>        Problem name
  -e,--epsilon <e1,e2,...>   Epsilon values for epsilon-dominance
  -h,--help                  Display help information
  -o,--output <file>         Output file
```

### ResultFileValidator

<!-- help:src/org/moeaframework/analysis/tools/ResultFileValidator.java [:-2] -->

```
Validates the number of approximation sets stored in a result file.

Usage: ./cli ResultFileValidator [options] <file...>

The following options are available:

  -b,--problem <name>   Problem name
  -c,--count <N>        The expected number of entries
  -h,--help             Display help information
  -o,--output <file>    Output file
```

### ResultFileViewer

<!-- help:src/org/moeaframework/analysis/tools/ResultFileViewer.java [:-2] -->

```
Visualizes the contents of a result file.

Usage: ./cli ResultFileViewer [options] <file...>

The following options are available:

  -b,--problem <name>     Problem name
  -h,--help               Display help information
  -r,--reference <file>   Reference set file
```

### RuntimeEvaluator

<!-- help:src/org/moeaframework/analysis/tools/RuntimeEvaluator.java [:-2] -->

```
Records the approximation set at a fixed sampling frequency from each parameterization.

Usage: ./cli RuntimeEvaluator [options]

The following options are available:

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

### SampleGenerator

<!-- help:src/org/moeaframework/analysis/tools/SampleGenerator.java [:-2] -->

```
Generates parameter samples for running the Evaluator.

Usage: ./cli SampleGenerator [options]

The following options are available:

  -h,--help                      Display help information
  -m,--method <name>             Sample generation method (uniform, latin, sobol, saltelli)
  -n,--numberOfSamples <value>   Number of samples
  -o,--output <file>             Output file
     --overwrite                 Overwrite the output file if it exists
  -p,--parameterFile <file>      Parameter file
  -s,--seed <value>              Random number generator seed
```

### SobolAnalysis

<!-- help:src/org/moeaframework/analysis/tools/SobolAnalysis.java [:-2] -->

```
Performs Sobol' global variance analysis.

Usage: ./cli SobolAnalysis [options]

The following options are available:

  -h,--help                   Display help information
  -i,--input <file>           Model output file
  -m,--metric <value>         Column in model output to evaluate
  -o,--output <file>          Output file
  -p,--parameterFile <file>   Parameter description file
  -r,--resamples <number>     Number of resamples when computing bootstrap confidence intervals
```

### Solve

<!-- help:src/org/moeaframework/analysis/tools/Solve.java [:-2] -->

```
Solves an optimization problem.

Usage: ./cli Solve [options] -- <executable>

Supports solving any built-in problem given its name or by starting a external process.  When using an external process,
the decision variables must be defined using the syntax "R(<lb>,<ub>)" for reals, "B(<length>)" for binary,
"I(<lb>,<ub>)" for integers, "P(<length>)" for permutations.

The following options are available:

  -a,--algorithm <name>               Algorithm name
  -b,--problem <name>                 Problem name
  -c,--constraints <spec>             Number of constraints (default 0)
  -e,--epsilon <e1,e2,...>            Epsilon values for epsilon-dominance
  -f,--output <file>                  Output file
  -F,--runtimeFrequency <value>       Output population every N evaluations (default 100)
  -h,--help                           Display help information
  -H,--hostname <value>               Hostname used when using sockets (default localhost)
  -l,--lowerBounds <l1,l2,...>        Lower bounds of real-valued decision variables
  -n,--numberOfEvaluations <value>    Maximum number of evaluations
  -o,--objectives <spec>              Number of objectives
  -P,--port <value>                   Port used when using sockets (default 16801)
  -r,--retries <value>                The number of retries when establishing a socket connection (default 5)
  -s,--seed <value>                   Random number seed
  -S,--useSocket                      Communicate with external problem using sockets
  -t,--test <trials>                  Runs a few trials to test the connection with the external problem
  -u,--upperBounds <u1,u2,...>        Upper bounds of real-valued decision variables
  -v,--variables <spec>               Semicolon-separated list of decision variable specifications
  -X,--properties <p1=v1;p2=v2;...>   Algorithm properties
```

### WeightGenerator

<!-- help:src/org/moeaframework/analysis/tools/WeightGenerator.java [:-2] -->

```
Outputs randomly-generated weights.

Usage: ./cli WeightGenerator [options]

The following options are available:

  -d,--dimension <value>         Dimension (typically the number of objectives)
     --divisions <value>         Number of divisions used for NBI weights (for single layer)
     --divisionsInner <value>    Number of inner divisions used for NBI weights (for two layers)
     --divisionsOuter <value>    Number of outer divisions used for NBI weights (for two layers)
  -g,--generalized               Convert the weights using Generalized Decomposition (GD)
  -h,--help                      Display help information
  -m,--method <name>             The sampling method (random, uniformdesign, normalboundaryintersection)
  -n,--numberOfSamples <value>   Number of samples
  -o,--output <file>             Output file
```

