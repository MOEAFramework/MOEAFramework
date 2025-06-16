# Creating and Using Extensions

## What are Extensions?

Extensions provide a way to extend or augment the functionality of an algorithm by plugging into specific
"extension points".  There are a number of built-in extensions that are detailed below, but we can also write custom
extensions.

For example, let's write an extension that displays the current NFE and the number of non-dominated points after each
iteration.  We define the extension by implementing the `Extension` interface and overriding one or more of its methods.
Here, we override `onStep` to run after each iteration.

<!-- :code: src=examples/org/moeaframework/examples/extensions/CustomExtensionExample.java lines=32:40 -->

```java
public static class CustomExtension implements Extension {

    @Override
    public void onStep(Algorithm algorithm) {
        System.out.println("NFE: " + algorithm.getNumberOfEvaluations() +
                ", Nondominated Solutions: " + algorithm.getResult().size());
    }

}
```

Then, we simply register the extension with the algorithm:

<!-- :code: src=examples/org/moeaframework/examples/extensions/CustomExtensionExample.java lines=43:45 -->

```java
NSGAII algorithm = new NSGAII(new Srinivas());
algorithm.addExtension(new CustomExtension());
algorithm.run(10000);
```

Running this code produces the following output:

<!-- :exec: src=examples/org/moeaframework/examples/extensions/CustomExtensionExample.java lines=:10 showEllipsis -->

```
NFE: 100, Nondominated Solutions: 11
NFE: 200, Nondominated Solutions: 15
NFE: 300, Nondominated Solutions: 26
NFE: 400, Nondominated Solutions: 37
NFE: 500, Nondominated Solutions: 58
NFE: 600, Nondominated Solutions: 87
NFE: 700, Nondominated Solutions: 100
NFE: 800, Nondominated Solutions: 100
NFE: 900, Nondominated Solutions: 100
NFE: 1000, Nondominated Solutions: 100
...
```

## Built-in Extensions

### Logging

The logging extension periodically displays the NFE and elapsed time:

<!-- :code: src=examples/org/moeaframework/examples/extensions/LoggingExample.java lines=32:34 -->

```java
NSGAII algorithm = new NSGAII(new Srinivas());
algorithm.addExtension(new LoggingExtension());
algorithm.run(100000);
```

### Progress

The progress extension allows for tracking the progress of a run, including the elapsed time and estimated time
remaining.

<!-- :code: src=examples/org/moeaframework/examples/extensions/ProgressExtensionExample.java lines=33:35 -->

```java
NSGAII algorithm = new NSGAII(new Srinivas());
algorithm.addExtension(new ProgressExtension().withListener(new DefaultProgressListener()));
algorithm.run(100000);
```

When using the default listener, as the example above demonstrates, the elapsed time, remaining time, and progress
is displayed to the console:

```
E: 00:01:25, R: 00:00:18 [===============================>        ] 81%
```

### Callback Function

The callback extension allows running arbitrary commands after each step.  Observe in this example how we can write
custom log messages:

<!-- :code: src=examples/org/moeaframework/examples/extensions/CallbackExtensionExample.java lines=33:41 -->

```java
NSGAII algorithm = new NSGAII(new Srinivas());

algorithm.addExtension(new LoggingExtension());
algorithm.addExtension(new CallbackExtension(a -> {
    LoggingExtension.info(a, "NFE: {0}, Nondominated Solutions: {1}",
            algorithm.getNumberOfEvaluations(), algorithm.getResult().size());
}));

algorithm.run(10000);
```

### Checkpoints

Checkpoints are useful when performing expensive or long-running optimizations.  This will periodically save
the state of the optimization to a file, which can be resumed at a later point if the run is interrupted.

<!-- :code: src=examples/org/moeaframework/examples/extensions/CheckpointExample.java lines=36:49 -->

```java
File checkpointFile = new File("checkpoint.dat");
checkpointFile.delete();

NSGAII algorithm = new NSGAII(new Srinivas());
algorithm.addExtension(new LoggingExtension());
algorithm.addExtension(new CheckpointExtension(checkpointFile, Frequency.ofEvaluations(1000)));
algorithm.run(500000);

System.out.println("========== End of first run ==========");

algorithm = new NSGAII(new Srinivas());
algorithm.addExtension(new LoggingExtension());
algorithm.addExtension(new CheckpointExtension(checkpointFile, Frequency.ofEvaluations(1000)));
algorithm.run(1000000 - algorithm.getNumberOfEvaluations());
```

### Runtime Collector

Records the intermediate approximation sets as an algorithm runs, saving them to a result file.

<!-- :code: src=examples/org/moeaframework/examples/extensions/RuntimeCollectorExample.java lines=36:43 -->

```java
Problem problem = new Srinivas();
File file = new File("runtime.dat");

try (ResultFileWriter writer = ResultFileWriter.open(problem, file)) {
    NSGAII algorithm = new NSGAII(problem);
    algorithm.addExtension(new RuntimeCollectorExtension(writer, Frequency.ofEvaluations(1000)));
    algorithm.run(100000);
}
```

### Time Continuation

Adaptive time continuation (along with epsilon-progress continuation) adds dynamic restarts, wherein we periodically
resize and inject random solutions into the population to avoid or escape local optima.  The epsilon-progress version
also triggers restarts if no improvements are detected after some time.

<!-- :code: src=examples/org/moeaframework/examples/extensions/AdaptiveTimeContinuationExample.java lines=36:42 -->

```java
NSGAII algorithm = new NSGAII(new DTLZ2(2));
algorithm.setArchive(new EpsilonBoxDominanceArchive(0.01));

algorithm.addExtension(new LoggingExtension());
algorithm.addExtension(new AdaptiveTimeContinuationExtension());

algorithm.run(100000);
```

