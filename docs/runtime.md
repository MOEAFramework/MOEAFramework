# Collecting Runtime Data

In the examples thus far, we demonstrated running an algorithm for a given number of function evaluations (NFE) and
processing the end-of-run result.  Here we introduce the `Instrumenter` class as a means to collect runtime data from
algorithms.

## Convergence of Performance Indicators

As demonstrated below, we configure the `Instrumenter` with the reference set, collection frequency (every 100 NFE),
and specify what to collect.  Here, we collect the hypervolume and generational distance metrics:

<!-- java:examples/org/moeaframework/examples/runtime/PrintRuntimeDynamics.java [34:49] -->

```java
Problem problem = new DTLZ2(2);
NSGAII algorithm = new NSGAII(problem);

Instrumenter instrumenter = new Instrumenter()
        .withReferenceSet("pf/DTLZ2.2D.pf")
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

<!-- output:examples/org/moeaframework/examples/runtime/PrintRuntimeDynamics.java [:12] -->

```
NFE   GenerationalDistance Hypervolume
----- -------------------- -----------
100   0.151459             0.000000
200   0.137644             0.000000
300   0.122702             0.000000
400   0.102731             0.003740
500   0.083064             0.003740
600   0.070741             0.010701
700   0.055458             0.022142
800   0.033471             0.062235
900   0.028945             0.072104
1000  0.023840             0.080322
```

Alternatively, we can pass the observations to the `Plot` class to produce a line graph of the runtime data:

<!-- java:examples/org/moeaframework/examples/runtime/PlotRuntimeDynamics.java [50:53] -->

```java
new Plot()
    .add(instrumentedAlgorithm.getObservations())
    .show();
```

![Performance Indicator Plot](imgs/runtime-lingegraph.png)

## Visualizing Approximation Set Convergence

We can also collect the approximation set throughout a run, using the built-in `RuntimeViewer` to display an interactive
plot showing the convergence of the approximation set.

<!-- java:examples/org/moeaframework/examples/runtime/RuntimeViewerExample.java [35:51] -->

```java
Problem problem = new DTLZ2(2);
NSGAII algorithm = new NSGAII(problem);

Instrumenter instrumenter = new Instrumenter()
        .withReferenceSet("pf/DTLZ2.2D.pf")
        .withFrequency(100)
        .attachApproximationSetCollector();

InstrumentedAlgorithm<NSGAII> instrumentedAlgorithm = instrumenter.instrument(algorithm);
instrumentedAlgorithm.run(10000);

RuntimeViewer.show("NSGAII on UF1",
        instrumenter.getReferenceSet(),
        instrumenter.getObservations());
```

![Runtime Viewer](imgs/runtime-viewer.png)
