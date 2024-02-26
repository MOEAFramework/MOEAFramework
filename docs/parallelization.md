# Parallelization

By default, the MOEA Framework is single-threaded.  Solving a problem in the manner below will use a single core on your computer:

```java

Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

algorithm.getResult().display();
```

This is typically fine for test problems, but custom problems can quickly become computationally expensive.  Let's explore how we can
improve evaluation times.

## Distributing Function Evaluations

Perhaps the most straightforward approach to speeding up evaluations is to distribute or parallelize the function evaluations across
multiple cores.  Most consumer CPUs today have multiple cores (and even multiple threads per core!).  We can distributed function
evaluations using the `DistributedProblem` class:

```java
try (Problem problem = DistributedProblem.from(new DTLZ2(2))) {
    NSGAII algorithm = new NSGAII(problem);
    algorithm.run(10000);		
    algorithm.getResult().display();
}
```

There are a few key points to call out:

1. We create a distributed version of the problem by calling `DistributedProblem.from(...)`.  By default, this will distribute across
   all available processors on the local machine.

2. Be sure to close the the problem when finished to ensure all underlying resources are cleaned up.  The easiest way is using a
   try-with-resources block as demonstrated in this example.
   
3. When distributing function evaluations, we must balance the speedup provided by parallelization against communication and overhead costs.
   If each function evaluation only takes milliseconds, parallelization is unlikely to provide any performance benefit (or could
   even make it slower!).

## Distributing on Remote Machines

The above example distributed the function evaluations on the local computer.  But we can also distribute across
remote machines using a library like [Apache Ignite](https://ignite.apache.org/).  In fact, any library that
provides an `ExecutorService` implementation is supported!

First, the problem class must implement the `Serializable` interface.  This allows Java to transmit the problem
definition to the remote machines.  Without this, you will likely see a `NotSerializableException`.

```java
public class MyDistributedProblem extends AbstractProblem implements Serializable {
    // ... implement problem ...
}
```

Then, use the `ExecutorService` provided by your library with the `DistributedProblem`.  For Apache Ignite,
it would look something like:

```java
try (Ignite ignite = Ignition.start("config/ignite-config.xml")) {
    ExecutorService executor = ignite.executorService();
			
    try (Problem problem = DistributedProblem.from(new MyDistributedProblem(), executor)) {
        NSGAII algorithm = new NSGAII(problem);
        algorithm.run(10000);		
        algorithm.getResult().display();
    }
}
```

## Island Model Parallelization

Another common approach is island-model parallelization.  Instead of distributing the work of a single algorithm, island-model
parallelization runs multiple algorithm instances in parallel.  The example below demonstrates a simple island-model using
NSGA-II.  More advanced configurations can be used, altering the island topology and migration strategy, or even using different
optimization algorithms on each island.

```java

Problem problem = new UF1();
		
PRNG.setRandom(ThreadLocalMersenneTwister.getInstance());
		
Selection migrationSelection = new TournamentSelection(2, new ChainedComparator(
	new ParetoDominanceComparator(), new CrowdingComparator()));
		
Migration migration = new SingleNeighborMigration(1, migrationSelection);
Topology topology = new FullyConnectedTopology();
IslandModel model = new IslandModel(1000, migration, topology);
		
for (int i = 0; i < 8; i++) {
	NSGAII algorithm = new NSGAII(problem);
	model.addIsland(new Island(algorithm, algorithm.getPopulation()));
}
		
try (ThreadedIslandExecutor executor = new ThreadedIslandExecutor(model)) {
	NondominatedPopulation result = executor.run(100000);
	result.display();
}
```
