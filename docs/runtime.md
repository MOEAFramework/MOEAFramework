# Collecting Runtime Data

In the examples thus far, we demonstrated running an algorithm for a given number of function evaluations (NFE) and
processing the end-of-run result.  Here we introduce the `Instrumenter` class as a means to collect runtime data from
algorithms.

## Convergence of Performance Indicators

As demonstrated below, we configure the `Instrumenter` with the reference set, collection frequency (every 100 NFE),
and specify what to collect.  Here, we collect the hypervolume and generational distance metrics:

<!-- java:examples/org/moeaframework/examples/runtime/PrintRuntimeDynamics.java [34:49] -->

```java
Problem problem = new UF1();
NSGAII algorithm = new NSGAII(problem);

Instrumenter instrumenter = new Instrumenter()
        .withReferenceSet("pf/UF1.pf")
        .withFrequency(100)
        .attachHypervolumeCollector()
        .attachGenerationalDistanceCollector();

InstrumentedAlgorithm<NSGAII> instrumentedAlgorithm = instrumenter.instrument(algorithm);
instrumentedAlgorithm.run(10000);

instrumentedAlgorithm.getObservations().display();
```

Observe how we use the `Instrumenter` to instrument the algorithm, receiving back an `InstrumentedAlgorithm`.  This class
handles collecting the data as the algorithm runs, returning the data as `Observations`.  Calling `display()` prints
a table with the data:

<!-- output:examples/org/moeaframework/examples/runtime/PrintRuntimeDynamics.java [:12] {Truncated} -->

```
NFE   GenerationalDistance Hypervolume
----- -------------------- -----------
100   0.799030             0.000000
200   0.707753             0.000000
300   0.438113             0.000000
400   0.383873             0.000000
500   0.431799             0.000000
600   0.372148             0.000000
700   0.344861             0.000000
800   0.294252             0.000000
900   0.294386             0.000000
1000  0.293309             0.000000
...
```

Alternatively, we can pass the observations to the `Plot` class to produce a line graph of the runtime data:

<!-- java:examples/org/moeaframework/examples/runtime/PlotRuntimeDynamics.java [50:53] -->

```java
new Plot()
    .add(instrumentedAlgorithm.getObservations())
    .show();
```

![Performance Indicator Plot](imgs/runtime-linegraph.png)

## Visualizing Approximation Set Convergence

We can also collect the approximation set throughout a run, using the built-in `RuntimeViewer` to display an interactive
plot showing the convergence of the approximation set.

<!-- java:examples/org/moeaframework/examples/runtime/RuntimeViewerExample.java [35:51] -->

```java
Problem problem = new UF1();
NSGAII algorithm = new NSGAII(problem);

Instrumenter instrumenter = new Instrumenter()
        .withReferenceSet("pf/UF1.pf")
        .withFrequency(100)
        .attachApproximationSetCollector();

InstrumentedAlgorithm<NSGAII> instrumentedAlgorithm = instrumenter.instrument(algorithm);
instrumentedAlgorithm.run(10000);

RuntimeViewer.show("NSGAII on UF1",
        instrumenter.getReferenceSet(),
        instrumenter.getObservations());
```

![Runtime Viewer](imgs/runtimeViewer.png)

## Diagnostic Tool

The Diagnostic Tool is a GUI that provides a convenient way to run and visualize the algorithms.  To start the
Diagnostic Tool, run:

```bash
# Windows
cli LaunchDiagnosticTool

# Linux / Mac
cli.sh LaunchDiagnosticTool
```

Below is an example comparing NSGA-II and $\epsilon$-MOEA on the UF1 test problem.  Note how the controls on the
left-hand side allow us to select and view different problems, algorithms, and quality indicators.

![Runtime Viewer](imgs/diagnosticTool.png)
