# Third-Party Algorithm Providers

In addition to the [algorithms built into the MOEA Framework](algorithms.md), we have integrated several
third-party providers.

## JMetal

JMetal is another Java library for metaheuristic algorithms.  We use it extensively for testing purposes,
but it also includes several additional algorithms that can be incorporated into the MOEA Framework.

In order to use any JMetal algorithm, one must first setup the JMetal dependencies.  We have built and
tested against JMetal version **5.9**.

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

### Supported Algorithms

Refer to [src/org/moeaframework/algorithm/jmetal/JMetalAlgorithms.java] for the full list and available parameters,
but at the time of writing we support:

1. AbYSS - Hybrid scatter search
2. CDG - Constrained decomposition MOEA with grid
3. CellDE - Hybrid cellular genetic algorithm with differential evolution
4. MOCell - Multi-objective cellular genetic algorithm
5. MOCHC - Conservative selection combined with a highly-disruptive recombination operator