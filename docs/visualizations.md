# Visualizations

## Simple Plots

The `Plot` class can generate simple, 2D plots from results produced by the MOEA Framework.

### Pareto Set

Here we plot the result, a Pareto approximation set, of NSGA-II solving the 2-objective DTLZ2 problem:

<!-- java:examples/org/moeaframework/examples/plots/PlotApproximationSet.java [31:38] -->

```java
Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

new Plot()
    .add("NSGA-II", algorithm.getResult())
    .show();
```

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/8c622569-07d6-4e0d-8b04-6663caf2c21a)

### Runtime Dynamics

Here we plot the runtime dynamics showing the hypervolume and generational distance metrics as NSGA-II solves the
2-objective DTLZ2 problem:

<!-- java:examples/org/moeaframework/examples/plots/PlotRuntimeDynamics.java [36:54] -->

```java
Problem problem = new DTLZ2(2);

Instrumenter instrumenter = new Instrumenter()
    .withProblem(problem)
    .withReferenceSet(new File("./pf/DTLZ2.2D.pf"))
    .withFrequency(100)
    .attachHypervolumeCollector()
    .attachGenerationalDistanceCollector();

NSGAII algorithm = new NSGAII(problem);

InstrumentedAlgorithm instrumentedAlgorithm = instrumenter.instrument(algorithm);
instrumentedAlgorithm.run(10000);

Observations observations = instrumenter.getObservations();

new Plot()
    .add(observations)
    .show();
```

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/e7a5f079-b44d-434b-a359-5c8744e5cc6b)

### Control Maps

Finally, here is a control map plot showing how two parameters, `maxEvaluations` and `populationSize`, affect the
performance of the algorithm (lighter-colored areas indicate better results).

<!-- java:examples/org/moeaframework/examples/plots/PlotControlMap.java [36:59] -->

```java
Problem problem = new DTLZ2(2);
Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.loadReferenceSet("./pf/DTLZ2.2D.pf"));

double[] x = IntStream.range(0, 50).mapToDouble(i -> 100 * (i+1)).toArray();
double[] y = IntStream.range(0, 50).mapToDouble(i -> 4 * (i+1)).toArray();
double[][] z = new double[x.length][y.length];

for (int i = 0; i < x.length; i++) {
    for (int j = 0; j < y.length; j++) {
        System.out.println("Evaluating run " + (i * y.length + j + 1) + " of " + (x.length * y.length));

        NSGAII algorithm = new NSGAII(problem);
        algorithm.setInitialPopulationSize((int)y[j]);
        algorithm.run((int)x[i]);

        z[i][j] = hypervolume.evaluate(algorithm.getResult());
    }
}

new Plot()
    .heatMap("Hypervolume", x, y, z)
    .setXLabel("Max Evaluations")
    .setYLabel("Population Size")
    .show();
```

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/a60c9866-6d94-4b3e-ac20-d2318d0f6c75)

## Diagnostic Tool

Instead of manually generating plots as shown above, the Diagnostic Tool is a convenient way to run and visualize optimization algorithms.
However, it is limited in that it can only run with default settings.

To start the Diagnostic Tool, run `launch-diagnostic-tool.bat` on Windows.  On other systems, run the following command from the terminal:

<!-- bash:src/launch-diagnostic-tool.bat [2:2] -->

```bash
java -classpath "lib/*" org.moeaframework.analysis.diagnostics.LaunchDiagnosticTool
```

Below is an example comparing NSGA-II and $\epsilon$-MOEA on the UF1 test problem.  Note how the controls on the left-hand side allow you
to select and view different problems, algorithms, and quality indicators.

![image](https://user-images.githubusercontent.com/2496211/202853310-2e41b809-7997-4b30-865a-cd4fce2ed36f.png)

## J3 - Java 3D Visualization Tool

For higher-dimensional visualizations of Pareto approximation sets, we also developed a desktop application called J3.
This tool can be downloaded and installed from https://github.com/Project-Platypus/J3.

First, we need to export the results into a CSV file:

<!-- java:examples/org/moeaframework/examples/misc/SaveAndFormatResultsExample.java [41:41] -->

```java
algorithm.getResult().save(TableFormat.CSV, new File("solutions.csv"));
```

Then, launch J3 and open `solutions.csv`.  The initial 3D plot will look random due to how the axes are configured.
Click the options button and set the X / Y / Z axes to display `Obj1`, `Obj2`, and `Obj3`:

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/b0872934-c7b7-4ca3-9925-e168e6d86383)

You should then see a 3D scatter plot similar to:

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/4cd9fb04-1ea6-4cc1-88b7-c71f7a0549e5)

From the Widgets button, you can add additional visualizations, such as a parallel coordinates plot:

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/4671727f-d966-4749-9e41-735d579ebf20)

Lastly, all of these plots are interactive.  For example, clicking on a point will highlight that record:

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/a93b8612-bbcb-41c8-ba98-f5df9e1ef845)
