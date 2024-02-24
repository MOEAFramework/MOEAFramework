# Miscellaneous Features

## Checkpoints

Checkpoints are useful when performing expensive or long-running optimizations.  This will periodically save
the state of the optimization to a file, which can be resumed at a later point if the run is interrupted.

```java
File checkpointFile = new File("checkpoint.dat");

NSGAII algorithm = new NSGAII(new SrinivasProblem());
Checkpoints checkpoints = new Checkpoints(algorithm, checkpointFile, 1000);

if (checkpointFile.exists()) {
    System.out.println("Checkpoint file exists, resuming previous run at " +
        checkpoints.getNumberOfEvaluations() + " evaluations!");
} else {
    System.out.println("No checkpoint file, starting new run!");
}

checkpoints.run(1000000);
```

If we run this example a few times, you will see the output:

```
No checkpoint file, starting new run!
Checkpoint file exists, resuming previous run at 1000000 evaluations!
Checkpoint file exists, resuming previous run at 2000000 evaluations!
```

## Saving and Formatting Results

In many of the examples, you likely noticed we displayed results using `algorithm.getResult().display()`.
Alternatively, we can save the output to a file using:

```java
algorithm.getResult().asTabularData().saveCSV(new File("solutions.dat"));
```

We can also customize how the output is formatted.  Say we want to use ten digits of precision
for the output:

```java
NumberFormatter numberFormat = new NumberFormatter();
numberFormat.setPrecision(10);
		
TabularData<Solution> results = algorithm.getResult().asTabularData();
results.addFormatter(numberFormat);
results.display();
```

## Injecting Initial Solutions

By default, algorithms initialize the population using randomly-generated solutions.  It's also possible to
inject pre-defined solutions into the initial population:

```java
Problem problem = new DTLZ2(2);
		
Solution solutionA = problem.newSolution();
EncodingUtils.setReal(solutionA, new double[] { 0.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 });
		
Solution solutionB = problem.newSolution();
EncodingUtils.setReal(solutionB, new double[] { 1.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 });
		
NSGAII algorithm = new NSGAII(problem);	
algorithm.setInitialization(new InjectedInitialization(problem, solutionA, solutionB));
algorithm.run(10000);
		
algorithm.getResult().display();
```
