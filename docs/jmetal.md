# JMetal

JMetal is another popular Java library for metaheuristic algorithms.  We use it extensively for testing purposes,
but it also includes several additional algorithms that can be incorporated into the MOEA Framework.

⚠️ Only **JMetal 5.9** is supported.  Attempting to use a newer version will not work! ⚠️

## Setup

### Maven

If using Maven, add the following dependences to your `pom.xml`:

```xml
<dependency>
    <groupId>org.uma.jmetal</groupId>
    <artifactId>jmetal-algorithm</artifactId>
    <version>5.9</version>
</dependency>
<dependency>
    <groupId>org.uma.jmetal</groupId>
    <artifactId>jmetal-core</artifactId>
    <version>5.9</version>
</dependency>
```

### Manually

Otherwise, download the following JMetal packages and add to the classpath.  Typically, this just means placing these files in the `lib/` folder.

1. `jmetal-core` - [Download Jar](https://repo1.maven.org/maven2/org/uma/jmetal/jmetal-core/5.9/jmetal-core-5.9.jar)
2. `jmetal-algorithm` - [Download Jar](https://repo1.maven.org/maven2/org/uma/jmetal/jmetal-algorithm/5.9/jmetal-algorithm-5.9.jar)

## Algorithms

Our interface with JMetal only supports problems with real-valued, binary, and permutation decision variables.

### AbYSS

AbYSS is a hybrid scatter search algorithm that uses genetic algorithm operators [^nebro08].

**Algorithm Name:** `"AbYSS"`  
**Supported Types:** Real  
**Supported Operators:** `sbx+pm`

Parameter            | Default Value | Description
:------------------- | :------------ | :----------
`populationSize`     | 20            | The size of the population
`archiveSize`        | 100           | The size of the archive
`refSet1Size`        | 10            | The size of the first reference set
`refSet2Size`        | 10            | The size of the second reference set
`improvementRounds`  | 1             | The number of iterations that the local search operator is applied

### CDG

CDG is a "constrained decomposition with grid" algorithm with differential evolution [^cai18].

**Algorithm Name:** `"CDG"`  
**Supported Types:** Real  
**Supported Operators:** `de`

Parameter            | Default Value | Description
:------------------- | :------------ | :----------
`populationSize`     | 100           | The size of the population
`archiveSize`        | 100           | The size of the archive
`neighborhoodSelectionProbability`   | 0.9            | The probability of selecting a neighboring solution
`de.crossoverRate`   | 0.1           | The crossover rate for differential evolution
`de.stepSize`        | 0.5           | Control the size of each step taken by differential evolution
`de.variant`         | `rand/1/bin`  | The DE variant

The following DE variants are supported: `rand/1/bin`, `rand/1/exp`, `best/1/bin`, `best/1/exp`, `current-to-rand/1`, `current-to-rand/1/bin`,
`current-to-rand/1/exp`, `current-to-best/1`, `current-to-best/1/bin`, and `current-to-best/1/exp`.

### CellDE

CellDE is a hybrid cellular genetic algorithm (meaning mating only occurs among neighbors) combined with differential evolution [^durillo08].

**Algorithm Name:** `"CellDE"`  
**Supported Types:** Real  
**Supported Operators:** `de`

Parameter            | Default Value | Description
:------------------- | :------------ | :----------
`populationSize`     | 100           | The size of the population
`archiveSize`        | 100           | The size of the archive
`feedBack`           | 20            | Controls the number of solutions from the archive that are fed back into the population
`de.crossoverRate`   | 0.1           | The crossover rate for differential evolution
`de.stepSize`        | 0.5           | Control the size of each step taken by differential evolution
`de.variant`         | `rand/1/bin`  | The DE variant

### MOCell

MOCell is the multiobjective version of a cellular genetic algorithm [^nebro07a].

**Algorithm Name:** `"MOCell"`  
**Supported Types:** Real, Binary, Permutation  
**Supported Operators:** `sbx+pm`, `1x+pf`, `pmx+swap`

Parameter            | Default Value | Description
:------------------- | :------------ | :----------
`populationSize`     | 100           | The size of the population
`archiveSize`        | 100           | The size of the archive

### MOCHC

MOCHC is a genetic algorithm that combines a conservative selection strategy with highly disruptive recombination, which unlike traditional MOEAs aims to produce offspring that are maximally different from both parents [^nebro07b].

**Algorithm Name:** `"MOCHC"`  
**Supported Types:** Real  
**Supported Operators:** `hux+bf`

Parameter                 | Default Value | Description
:------------------------ | :------------ | :----------
`initialConvergenceCount` | 0.25          | The threshold (as a percent of the number of bits in the encoding) used to determine similarity between solutions
`preservedPopulation`     | 0.05          | The percentage of the population that does not undergo cataclysmic mutation
`convergenceValue`        | 3             | The convergence threshold that determines when cataclysmic mutation is applied
`populationSize`          | 100           | The size of the population
`hux.rate`                | 1.0           | The crossover rate for the highly disruptive recombination operator
`bf.rate`                 | 0.35          | The mutation rate for bit-flip mutation

### JMetal Variants

In many cases, both the MOEA Framework and JMetal provide the same optimization algorithm.  In such cases, you can explicitly reference the JMetal
version by appending `-JMetal` to the name.  For example, `NSGAII-JMetal`.

[^cai18]: X. Cai, Z. Mei, Z. Fan and Q. Zhang, "A Constrained Decomposition Approach With Grids for Evolutionary Multiobjective Optimization," in IEEE Transactions on Evolutionary Computation, vol. 22, no. 4, pp. 564-577, Aug. 2018, doi: 10.1109/TEVC.2017.2744674.
[^durillo08]: Durillo, J. J., Nebro, A. J., Luna, F., and Alba, E. (2008). Solving three-objective optimization problems using a new hybrid cellular genetic algorithm. In Parallel Problem Solving form Nature - PPSN X, pages 661–670. Springer.
[^nebro07a]: Nebro, A. J., Durillo, J. J., Luna, F., and Dorronsoro, B. (2007). MOCell: A cellular genetic algorithm for multiobjective optimization. International Journal of Intelligent Systems, pages 25–36.
[^nebro07b]: Nebro, A. J., Alba, E., Molina, G., Chicano, F., Luna, F., and Durillo, J. J. (2007). Optimal antenna placement using a new multi-objective chc algorithm. In Proceedings of the 9th Annual Conference on Genetic and Evolutionary Computation, pages 876–883.
[^nebro08]: Nebro, A. J., Luna, F., Alba, E., Dorronsoro, B., Durillo, J. J., and Beham, A. (2008). AbYSS: Adapting scatter search to multiobjective optimization. IEEE Transactions on Evolutionary Computation, 12(4):439–457.
