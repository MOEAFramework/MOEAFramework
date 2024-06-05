# File Formats

This document details the file formats that are used by the MOEA Framework.

## Tabular Data

Classes that represent data, such as `Solution`, `Population`, `TypedProperties`, etc. and their subclasses
all share a common trait that the data is "tabular".  This is akin to a spreadsheet with data organized in rows and
columns.  These classes all implement the `Formattable` interface, which defines the `display(...)` and
`save(...)` methods for formatting and outputting the data in a variety of formats.

For instance, calling `display()` will print the data to standard output:

<!-- java:examples/org/moeaframework/examples/misc/SaveAndFormatResultsExample.java [48:48] -->

```java
algorithm.getResult().display();
```

Or we can save to a file as CSV, Json, or Markdown and Latex tables:

<!-- java:examples/org/moeaframework/examples/misc/SaveAndFormatResultsExample.java [41:44] -->

```java
algorithm.getResult().save(TableFormat.CSV, new File("solutions.csv"));
algorithm.getResult().save(TableFormat.Markdown, new File("solutions.md"));
algorithm.getResult().save(TableFormat.Latex, new File("solutions.tex"));
algorithm.getResult().save(TableFormat.Json, new File("solution.json"));
```

Note that these file formats are only intended to produce output.  They can not be read or loaded back into the MOEA
Framework.  Prefer using one of the other options documented here for storage purposes.

## Population Contents

The `Population` and `NondominatedPopulation` classes define methods for reading and writing the contents
of the population.

First, we can use `saveObjectives` and `loadObjectives` to read and write the objective values to a file.
Each row contains the objective values, separated by spaces, for a solution in the population.

<!-- java:test/org/moeaframework/snippet/FileFormatSnippet.java [49:50] -->

```java
population.saveObjectives(new File("population.dat"));
Population.loadObjectives(new File("population.dat"));
```

Reference sets, including those defined in the `pf/` folder, are also stored in this manner.  However, for convenience,
we recommend using the `loadReferenceSet` method, which loads the solutions into a `NondominatedPopulation`:

<!-- java:test/org/moeaframework/snippet/FileFormatSnippet.java [55:55] -->

```java
NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet("pf/DTLZ2.2D.pf");
```

However, note that only saving objective values is lossy, as the decision variables and other attributes of the
solution are not included.  If instead you need to store the entire contents of a solution, use the binary format:

<!-- java:test/org/moeaframework/snippet/FileFormatSnippet.java [63:64] -->

```java
population.saveBinary(new File("population.bin"));
Population.loadBinary(new File("population.bin"));
```

## Result File

Whereas the above examples show how to store a single population, a "result file" is another file format for storing
a collection of populations along with any associated metadata.  Many of the command-line tools and the `Analyzer`
use this file format when saving output.

This format is useful for storing runtime dynamics, where we snapshot the population every few iterations.
Additionally, it supports appending to an existing file.  When appending, the contents of the file are validated and
any incomplete entries are cleaned up.

Here is an example where we store the approximation set after each iteration of the algorithm:

<!-- java:test/org/moeaframework/snippet/FileFormatSnippet.java [72:81] -->

```java
try (ResultFileWriter writer = ResultFileWriter.overwrite(problem, new File("result.dat"))) {
    for (int i = 0; i < 1000; i++) {
        algorithm.step();

        TypedProperties properties = new TypedProperties();
        properties.setInt("NFE", algorithm.getNumberOfEvaluations());

        writer.append(new ResultEntry(algorithm.getResult(), properties));
    }
}
```

To resume or append to an existing file, replace `overwrite` with `append`.  Note that we can query the number of
existing entries to determine where to resume.

<!-- java:test/org/moeaframework/snippet/FileFormatSnippet.java [83:87] {KeepComments} -->

```java
try (ResultFileWriter writer = ResultFileWriter.append(problem, new File("result.dat"))) {
    int existingEntries = writer.getNumberOfEntries();

    // if existingEntries > 0, we are appending to an existing file
}
```

Use the reader to validate and load the contents of a results file:

<!-- java:test/org/moeaframework/snippet/FileFormatSnippet.java [89:96] -->

```java
try (ResultFileReader reader = ResultFileReader.open(problem, new File("result.dat"))) {
    while (reader.hasNext()) {
        ResultEntry entry = reader.next();

        TypedProperties metadata = entry.getProperties();
        NondominatedPopulation set = entry.getPopulation();
    }
}
```

> [!IMPORTANT]  
> Since result files are automatically validated, both when reading or appending, any incomplete entries or invalid
> data will discard any remaining content in the file.  Check the number of entries in the file to validate all content
> was read.

### File Format

This section provides a brief overview of the structure of a result file.  The file starts with a header section
followed by the body.  Each header line starts with `#` and contains the following information about the problem:

```
# Problem = Schaffer
# Variables = 1
# Objectives = 2
```

The header is immediately followed by the body, which contains one or more entries.  Each entry consists of:

1. The metadata, stored as key-value pairs in the format `//<key>=<value>`
2. The solutions, storing the decision variables and objective values separated by whitespace
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

Note that because the result file only stores `NondominatedPopulation`s, any infeasible solutions are automatically
discarded and no constraint values are included.

### Command Line Tools

Additionally, several command-line tools exist for working with result files, including:

1. [`ResultFileEvaluator`](commandLineTools.md#resultfileevaluator) - To evaluate performance indicators on a result
   file and output a metric file.
2. [`ResultFileInfo`](commandLineTools.md#resultfileinfo) - To count the display the number of valid entries in a
   result file.
3. [`ResultFileMerger`](commandLineTools.md#resultfilemerger) - To merge each population in a result file to create a
   combined approximation set.
4. [`ResultFileSeedMerger`](commandLineTools.md#resultfileseedmerger) - To merge results produced by different seeds,
   combining each entry across multiple result files.
5. [`ExtractData`](commandLineTools.md#extractdata) - To read specific metadata from a result file.
6. [`ARFFConverter`](commandLineTools.md#arffconverter) - To convert a result file into the ARFF file format used by
   data mining software.

## Metric File

The last file to discuss is the metric file.  Metric files contain the metrics or performance indicators.  When
generated from a result file, each row in the metric file corresponds to an entry in the result file.  For example:

```
#Hypervolume GenerationalDistance InvertedGenerationalDistance Spacing EpsilonIndicator MaximumParetoFrontError
0.7500358780000573 0.010035587845798506 0.03531768229428171 0.30988709824546057 0.06142454424660952 0.1886973093535054
0.769919280895597 0.006152896795104611 0.015283663427573918 0.10885841307576978 0.04402598508933901 0.1886973093535054
```

We generally recommend using `ResultFileEvaluator` to convert a result file into a metric file, however, one can
also read and write to metric files programmatically.  The procedure is almost identical to result files, except we use
`MetricFileReader` and `MetricFileWriter`.
