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

Otherwise, download the following JMetal packages and add to the classpath:

1. `jmetal-core` - [Download Jar](https://repo1.maven.org/maven2/org/uma/jmetal/jmetal-core/5.9/jmetal-core-5.9.jar)
2. `jmetal-algorithm` - [Download Jar](https://repo1.maven.org/maven2/org/uma/jmetal/jmetal-algorithm/5.9/jmetal-algorithm-5.9.jar)

## Algorithms

Our interface with JMetal only supports problems with real-valued, binary, and permutation decision variables.

### AbYSS

AbYSS is a hybrid scatter search algorithm that uses genetic algorithm operators \citep{nebro08}.   Only real-valued decision variables are supported.  The following parameters are available:

Parameter            | Default Value | Description
:------------------- | :------------ | :----------
  populationSize & The size of the population & $20$ \\
  archiveSize & The size of the archive & $100$ \\
  refSet1Size & The size of the first reference set & $10$ \\
  refSet2Size & The size of the second reference set & $10$ \\
  improvementRounds & The number of iterations that the local search operator is applied & $1$ \\

### CellDE

CellDE is a hybrid cellular genetic algorithm (meaning mating only occurs among neighbors) combined with differential evolution \citep{durillo08}.  Use the string \java{"CellDE"} when creating instances of this algorithm with the \java{Executor}.  CellDE defines its own parameters for its real-valued operators as listed below:

Parameter            | Default Value | Description
:------------------- | :------------ | :----------
`populationSize`     | 100           | The size of the population
`archiveSize`        | 100           | The size of the archive
`feedBack`           | 20            | Controls the number of solutions from the archive that are fed back into the population
`de.crossoverRate`   | 0.1           | The crossover rate for differential evolution
`de.stepSize`        | 0.5           | Control the size of each step taken by differential evolution

### MOCell

MOCell is the multiobjective version of a cellular genetic algorithm \citep{nebro07}.  Use the string \java{"MOCell"} when creating instances of this algorithm with the \java{Executor}.  MOCell supports real-valued, binary, and permutation encodings.  The following parameters are available:

Parameter            | Default Value | Description
:------------------- | :------------ | :----------
`populationSize`     | 100           | The size of the population
`archiveSize`        | 100           | The size of the archive
`feedBack`           | 20            | Controls the number of solutions from the archive that are fed back into the population

### MOCHC
MOCHC is a genetic algorithm that combines a conservative selection strategy with highly disruptive recombination, which unlike traditional MOEAs aims to produce offspring that are maximally different from both parents \citep{nebro07b}.  Use the string \java{"MOCHC"} when creating instances of this algorithm with the \java{Executor}.  MOCHC defines its own parameters for its search operators as listed below:

Parameter            | Default Value | Description
:------------------- | :------------ | :----------
`initialConvergenceCount` | 0.25     | The threshold (as a percent of the number of bits in the encoding) used to determine similarity between solutions & $0.25$ \\
`preservedPopulation` & The percentage of the population that does not undergo cataclysmic mutation & $0.05$ \\
`convergenceValue & The convergence threshold that determines when cataclysmic mutation is applied & $3$ \\
`populationSize & The size of the population & $100$ \\
`hux.rate & The crossover rate for the highly disruptive recombination operator & $1.0$ \\
`bf.rate & The mutation rate for bit-flip mutation & $0.35$ \\
