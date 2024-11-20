# File Formats

This document details the file formats that are used by the MOEA Framework.

## Tabular Data

Classes that represent data, such as `Solution`, `Population`, `TypedProperties`, etc. and their subclasses
all share a common trait that the data is "tabular".  This is akin to a spreadsheet with data organized in rows and
columns.  These classes all implement the `Formattable` interface, which defines the `display(...)` and
`save(...)` methods for formatting and outputting the data in a variety of formats.

For instance, calling `display()` will print the data to standard output:

<!-- java:examples/Example1.java [34:34] -->

```java
algorithm.getResult().display();
```

Or we can save to a file as CSV, Markdown, Latex, Json, or ARFF:

<!-- java:examples/org/moeaframework/examples/io/SaveLoadPopulationExample.java [41:45] -->

```java
algorithm.getResult().save(TableFormat.CSV, new File("NSGAII_DTLZ2.csv"));
algorithm.getResult().save(TableFormat.Markdown, new File("NSGAII_DTLZ2.md"));
algorithm.getResult().save(TableFormat.Latex, new File("NSGAII_DTLZ2.tex"));
algorithm.getResult().save(TableFormat.Json, new File("NSGAII_DTLZ2.json"));
algorithm.getResult().save(TableFormat.ARFF, new File("NSGAII_DTLZ2.arff"));
```

Note that these file formats are only intended to produce output.  They can not be read or loaded back into the MOEA
Framework.  Prefer using one of the other options documented below for storage purposes.

## Populations

Populations define `save` and `load` methods for reading and writing the contents of a population:

<!-- java:examples/org/moeaframework/examples/io/SaveLoadPopulationExample.java [47:51] {KeepComments} -->

```java
// Save the population to a result file
algorithm.getResult().save(new File("NSGAII_DTLZ2.res"));

// Load the population from the result file
Population result = Population.load(new File("NSGAII_DTLZ2.res"));
```

Reference sets, including those defined in the `pf/` folder, are also stored in this manner.  Since reference sets
contain non-dominated solutions, use the `NondominatedPopulation` class to load the set:

<!-- java:examples/org/moeaframework/examples/io/LoadAndEvaluateReferenceSet.java [34:34] {KeepComments} -->

```java
NondominatedPopulation referenceSet = NondominatedPopulation.load("pf/DTLZ2.2D.pf");
```

## Result File

This section details the format of the "result file".  This is the format used by the `save` and `load` methods above
to store individual populations, but the file format also allows storing a collection of populations.  This is
especially useful when storing the result from multiple samples or runtime data:

<!-- java:examples/org/moeaframework/examples/io/ResultFileExample.java [54:64] -->

```java
try (ResultFileWriter writer = ResultFileWriter.open(problem, resultFile)) {
    for (Sample sample : samples) {
        System.out.println("Solving UF1 using NSGA-II with populationSize=" + populationSize.readValue(sample));

        NSGAII algorithm = new NSGAII(problem);
        algorithm.applyConfiguration(sample);
        algorithm.run(10000);

        writer.write(new ResultEntry(algorithm.getResult(), algorithm.getConfiguration()));
    }
}
```

One of the design considerations with result files is having the ability to resume or append data to an existing file.
When opening a result file in append mode, as demonstrated below, we automatically validate and repair any invalid
or incomplete entries.  Observe how we can query the number of entries to determine where to resume a previous run:

<!-- java:examples/org/moeaframework/examples/io/AppendingResultFileExample.java [52:68] -->

```java
try (ResultFileWriter writer = ResultFileWriter.append(problem, resultFile)) {
    int existingEntries = writer.getNumberOfEntries();

    if (existingEntries > 0) {
        System.out.println("Appending to " + resultFile + ", resuming after " + existingEntries + " entries!");
    }

    for (Sample sample : samples.skip(existingEntries)) {
        System.out.println("Solving UF1 using NSGA-II with populationSize=" + populationSize.readValue(sample));

        NSGAII algorithm = new NSGAII(problem);
        algorithm.applyConfiguration(sample);
        algorithm.run(10000);

        writer.write(new ResultEntry(algorithm.getResult(), algorithm.getConfiguration()));
    }
}
```

Use the reader to validate and load the contents of a results file:

<!-- java:examples/org/moeaframework/examples/io/ResultFileExample.java [67:77] -->

```java
try (ResultFileReader reader = ResultFileReader.open(problem, resultFile)) {
    Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.load("pf/UF1.pf"));

    while (reader.hasNext()) {
        ResultEntry entry = reader.next();

        double value = hypervolume.evaluate(new NondominatedPopulation(entry.getPopulation()));
        System.out.println("Hypervolume for populationSize=" + populationSize.readValue(entry.getProperties()) +
                " => " + value);
    }
}
```

> [!IMPORTANT]  
> Since result files are automatically validated, both when reading or appending, any incomplete entries or invalid
> data will be discarded.  Always validate the number of entries in the file match what is expected.

### File Format

This section provides a brief overview of the structure of a result file.  The file starts with a header section
followed by the body.  Each header line starts with `#` and defines the problem:

<!-- text:pf/Schaffer.pf [1-8] -->

```text
# Version=5
# Problem=Schaffer
# NumberOfVariables=1
# NumberOfObjectives=2
# NumberOfConstraints=0
# Variable.1.Definition=RealVariable(-10.0,10.0)
# Objective.1.Definition=Minimize
# Objective.2.Definition=Minimize
```

The header is immediately followed by the body, which contains one or more entries.  Each entry consists of:

1. The metadata, stored as key-value pairs in the format `//<key>=<value>`
2. The solutions, storing the decision variables, objectives, and constraint values separated by whitespace
3. A single line containing `#` to indicate the end of the entry

For example:

```
//NFE=100
1.630351879805879 2.658047251986563 0.1366397327630474
2.1516551041075296 4.629619687031984 0.022999270601865636
1.0045683929351448 1.0091576560842994 0.9908840843437203
-0.1909582824678946 0.03646506564308822 4.800298195514666
0.6809966029697812 0.46375637325638175 1.739769961377257
1.4254386488355273 2.031875341594054 0.33012074625194454
1.10304267509877 1.216703143089051 0.8045324426939705
#
//NFE=200
0.10815038674844235 0.011696506153837655 3.5790949591600683
2.0436877897785832 4.176659782090071 0.0019086229757376814
0.3574769258473287 0.12778975251325655 2.6978820491239417
1.183922905118636 1.4016734452645507 0.6659818247900069
0.6151852026366633 0.3784528335431125 1.9177120229964593
1.8695903665403146 3.495368138660348 0.01700667249908949
1.3921848202273965 1.9381785736715884 0.36943929276200227
0.4333809185738704 0.18781902058393168 2.45429534628845
#
```

## Metric File

The last file to discuss is the metric file.  Metric files contain the metrics or performance indicators.  When
generated from a result file, each row in the metric file corresponds to an entry in the result file.  For example:

```
#Hypervolume GenerationalDistance InvertedGenerationalDistance Spacing EpsilonIndicator MaximumParetoFrontError
0.7500358780000573 0.010035587845798506 0.03531768229428171 0.30988709824546057 0.06142454424660952 0.1886973093535054
0.769919280895597 0.006152896795104611 0.015283663427573918 0.10885841307576978 0.04402598508933901 0.1886973093535054
```

We generally recommend using `MetricsEvaluator` to convert a result file into a metric file, however, one can
also read and write to metric files programmatically.  The procedure is almost identical to result files, except we use
`MetricFileReader` and `MetricFileWriter`.
