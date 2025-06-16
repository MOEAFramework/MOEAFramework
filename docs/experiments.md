# Experiments

The MOEA Framework is intended to facilitate experimentation with MOEAs.  While the design of experiments can vary
in procedure, scope, and scale, we have built in a number of tools to aid in performing experiments.

## Parameter Specification

Parameters refer to the inputs used to configure an experiment.  We can identify several classes of parameters:

1. **Algorithm Configuration** - Determines the design of and parameters used by an MOEA.  Examples include the
   population size, the crossover and mutation operators, and their parameters.

2. **Run Settings** - Controls the run itself.  Examples include the PRNG seed, termination conditions (NFE), etc.

3. **Experiment Settings** - Other settings related to the design of the experiment.  Examples include the collection of
   algorithm(s) and problem(s) being studied.

These parameters can be defined in a programmatic way using the `Parameter` class.  This class uses a **builder**
pattern, which exposes methods appropriate to that parameter as it's constructed.  For example, here we define the
parameter `populationSize` to be an integer with values between `10` and `100` with a step size of `10`.  This results
in the values `10, 20, ..., 100`.

<!-- :code: src=examples/org/moeaframework/examples/experiment/ParameterSampleExample.java lines=47 -->

```java
Enumeration<Integer> populationSize = Parameter.named("populationSize").asInt().range(10, 100, 10);
```

Next, we define a parameter for the PRNG seed and select 10 random values:

<!-- :code: src=examples/org/moeaframework/examples/experiment/ParameterSampleExample.java lines=48 -->

```java
Enumeration<Long> seed = Parameter.named("seed").asLong().random(0, Long.MAX_VALUE, 10);
```

We then create a `ParameterSet` with the parameters we have defined.  We can optionally save this data to a file so we
have a record of how the experiment was configured.

<!-- :code: src=examples/org/moeaframework/examples/experiment/ParameterSampleExample.java lines=51:52 -->

```java
ParameterSet parameters = new ParameterSet(populationSize, seed);
parameters.save(new File("parameters.txt"));
```

## Sampling Parameters

With the parameters defined, we can now generate the samples.  You might have noticed above that the type of each
parameter was an `Enumeration`.  This simply means the parameter has a fixed set of possible values.  Consequently, when
we enumerate the samples, we create the "cross product" containing all possible combinations of these values.

<!-- :code: src=examples/org/moeaframework/examples/experiment/ParameterSampleExample.java lines=54:55 -->

```java
Samples samples = parameters.enumerate();
samples.save(new File("samples.txt"));
```

## Evaluating Samples

Finally, we use the `evaluateAll` method to configure each run, collect the result, and store it in a `SampledResults`
object.  Observe how we call `applyConfiguration` to configure the algorithm, but must also set up the PRNG seed
separately.

<!-- :code: src=examples/org/moeaframework/examples/experiment/ParameterSampleExample.java lines=57:66 -->

```java
SampledResults<NondominatedPopulation> results = samples.evaluateAll(sample -> {
    PRNG.setSeed(sample.getLong("seed"));

    NSGAII algorithm = new NSGAII(problem);
    algorithm.applyConfiguration(sample);
    algorithm.run(10000);

    return algorithm.getResult();
});
```

`SampledResults` is a mapping between each `Sample` and the result type, in this case a `NondominatedPopulation`.

## Analyzing Samples

With the data collected, we turn to analyzing the data.  Here, we measure the average hypervolume for each population
size and display the results:

<!-- :code: src=examples/org/moeaframework/examples/experiment/ParameterSampleExample.java lines=68:77 -->

```java
Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.load("./pf/DTLZ2.2D.pf"));

Partition<Integer, Double> avgHypervolume = results
        .map(hypervolume::evaluate)
        .groupBy(Groupings.exactValue(populationSize))
        .measureEach(Measures.average())
        .sorted();

avgHypervolume.display();
```

We can see below how the hypervolume value increases as we increase the population size (indicated by the `Key` column),
but there are diminishing returns as we get above `50+`:

<!-- :exec: src=examples/org/moeaframework/examples/experiment/ParameterSampleExample.java -->

```
Key Value
--- --------
10  0.166628
20  0.190002
30  0.197874
40  0.202048
50  0.204128
60  0.206022
70  0.207098
80  0.208162
90  0.208612
100 0.209286
```

