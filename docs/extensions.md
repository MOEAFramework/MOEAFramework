# Extensions

Extensions were introduced in version 4.5.  Please update to this version to use extensions!

## What are Extensions?

Extensions provide a way to extend or augment the functionality of an algorithm by plugging into specific
"extension points".  There are a number of built-in extensions that are detailed below, but we can also write custom
extensions.

For example, let's write an extension that displays the current NFE and the number of non-dominated points after each
iteration.  We define the extension by implementing the `Extension` interface and overriding one or more of its methods.
Here, we override `onStep` to run after each iteration.

<!-- java:examples/org/moeaframework/examples/extensions/CustomExtensionExample.java [32:39] -->

```java
public static class CustomExtension implements Extension {

    public void onStep(Algorithm algorithm) {
        System.out.println("NFE: " + algorithm.getNumberOfEvaluations() +
                ", Nondominated Solutions: " + algorithm.getResult().size());
    }

}
```

Then, we simply register the extension with the algorithm:

<!-- java:examples/org/moeaframework/examples/extensions/CustomExtensionExample.java [42:44] -->

```java
NSGAII algorithm = new NSGAII(new Srinivas());
algorithm.addExtension(new CustomExtension());
algorithm.run(10000);
```

Running this code produces the following output:

<!-- output:examples/org/moeaframework/examples/extensions/CustomExtensionExample.java [:10] -->

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
```

## Extension Points

The following extension points are provided:

* `onRegister` - Called when registering the extension.  This can be used to perform any type checking, validation,
  or initial setup of the algorithm.
* `onInitialize` - Called during initialization.  For instance, this can be used to modify the initial population or
  state of the algorithm.
* `onStep` - Called after each step (or iteration) of the algorithm.
* `onTerminate` - Called when the termination conditions are reached at the end of a run.

## Built-in Extensions

### Logging

The logging extension periodically displays the NFE and elapsed time:

<!-- java:examples/org/moeaframework/examples/extensions/LoggingExample.java [32:34] -->

```java
NSGAII algorithm = new NSGAII(new Srinivas());
algorithm.addExtension(new LoggingExtension());
algorithm.run(100000);
```

### Checkpoints

Checkpoints are useful when performing expensive or long-running optimizations.  This will periodically save
the state of the optimization to a file, which can be resumed at a later point if the run is interrupted.

<!-- java:examples/org/moeaframework/examples/extensions/CheckpointExample.java [35:48] -->

```java
File checkpointFile = new File("checkpoint.dat");
checkpointFile.delete();

NSGAII algorithm = new NSGAII(new Srinivas());
algorithm.addExtension(new LoggingExtension());
algorithm.addExtension(new CheckpointExtension(checkpointFile, 1000));
algorithm.run(500000);

System.out.println("========== End of first run ==========");

algorithm = new NSGAII(new Srinivas());
algorithm.addExtension(new LoggingExtension());
algorithm.addExtension(new CheckpointExtension(checkpointFile, 1000));
algorithm.run(1000000 - algorithm.getNumberOfEvaluations());
```

### Runtime Collector

Records the intermediate approximation sets as an algorithm runs, saving them to a result file.

<!-- java:examples/org/moeaframework/examples/extensions/RuntimeCollectorExample.java [36:43] -->

```java
Problem problem = new Srinivas();
File file = new File("runtime.dat");

try (ResultFileWriter writer = ResultFileWriter.overwrite(problem, file)) {
    NSGAII algorithm = new NSGAII(problem);
    algorithm.addExtension(new RuntimeCollectorExtension(writer, 1000, FrequencyType.EVALUATIONS));
    algorithm.run(100000);
}
```

