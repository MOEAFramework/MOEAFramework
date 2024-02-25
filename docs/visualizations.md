# Visualizations

## Simple Plots

The `Plot` class can generate simple, 2D plots from results produced by the MOEA Framework.  For instance,
plotting the Pareto approximation set produced by an algorithm:

```java
NSGAII algorithm = new NSGAII(new DTLZ2(2));
algorithm.run(10000);

new Plot()
    .add("NSGA-II", algorithm.getResult());
    .show();
```

or the runtime dynamics of an algorithm, such as Hypervolume and Generational Distance:

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
		
Observations observations = instrumentedAlgorithm.getObservations();
		
new Plot()
    .add(observations)
    .show();
```

## Diagnostic Tool

Instead of manually generating plots as shown above, the Diagnostic Tool is a convenient way to run and visualize optimization algorithms.
However, it is limited in that it can only run with default settings.

To start the Diagnostic Tool, run `launch-diagnostic-tool.bat` on Windows.  On other systems, run the following command from the terminal:

```
java -classpath "lib/*" org.moeaframework.analysis.diagnostics.LaunchDiagnosticTool
```

Below is an example comparing NSGA-II and $\epsilon$-MOEA on the UF1 test problem.  Note how the controls on the left-hand side allow you
to select and view different problems, algorithms, and quality indicators.

![image](https://user-images.githubusercontent.com/2496211/202853310-2e41b809-7997-4b30-865a-cd4fce2ed36f.png)

## J3

For higher-dimensional visualizations of Pareto approximation sets, we also developed a desktop application called J3.
This tool can be downloaded and installed from https://github.com/Project-Platypus/J3.

As an example, let's solve the 3-objective DLTZ2 problem using NSGA-II and save the approximation set to a CSV file:

```java

Problem problem = new DTLZ2(3);
		
NSGAII algorithm = new NSGAII(problem);
algorithm.setInitialPopulationSize(500);
algorithm.run(100000);
		
algorithm.getResult().asTabularData().saveCSV(new File("output.csv"));
```

Then, launch J3 and open `output.csv`.  The initial 3D plot will look random due to how the axes are configured.
Click the options button and set the X / Y / Z axes to display `Obj1`, `Obj2`, and `Obj3`:

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/b0872934-c7b7-4ca3-9925-e168e6d86383)

You should then see a 3D scatter plot similar to:

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/4cd9fb04-1ea6-4cc1-88b7-c71f7a0549e5)

From the Widgets button, you can add additional visualizations, such as a parallel coordinates plot:

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/4671727f-d966-4749-9e41-735d579ebf20)

Lastly, all of these plots are interactive.  For example, clicking on a point will highlight that record:

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/a93b8612-bbcb-41c8-ba98-f5df9e1ef845)
