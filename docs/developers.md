# Developer's Guide

## Versioning

We use [semantic versioning](https://semver.org/) following the pattern `{major}.{minor}`.  Two versions with the same
`{major}` number are expected to be backwards compatible, for example allowing one to upgrade from `3.0` to `3.2`
without difficulty.

### Preview Code

In some instances, especially when working directly from the Git default branch, new code is being actively developed.  These
packages and classes should include the `@preview` tag to indicate they are subject to change.

## Java Versions

Each release of the MOEA Framework targets a specific version of Java:

* `3.x` - Java 8+
* `2.x` - Java 6+ (some features are deprecated in Java 16+ and no longer work)

To determine if and when to update which Java version we target, we generally look at the
[support roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) and favor LTS releases.
While supporting earlier versions limits our use of newer language features, the tradeoff is supporting
the widest possible audience.

## Service Providers

This library uses Java's Service Provider Interface (SPI) to support extensibility.  The idea is that we can reference any
supported algorithm, operator, or problem by name and load it dynamically.  Furthermore, the providers automatically inspect
the problem, such as looking at the decision variable types, to select the appropriate default operators.

With this approach, one can quickly construct an algorithm appropriate for a given problem:

```java

DTLZ2 problem = new DTLZ2(2);
Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("NSGAII", problem);
```

Furthermore, we can provide properties to customize the algorithm:

```java

DTLZ2 problem = new DTLZ2(2);

TypedProperties properties = new TypedProperties();
properties.withInt("populationSize", 200);

Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("NSGAII", properties, problem);
```

Even going so far to let one dynamically change the operators, in this case using a combination of Parent Centric Crossover (PCX)
and Uniform Mutation (UM):

```java

DTLZ2 problem = new DTLZ2(2);

TypedProperties properties = new TypedProperties();
properties.withString("operator", "pcx+um");

Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("NSGAII", properties, problem);
```

We have providers for algorithms (see `AlgorithmProvider` and `AlgorithmFactory`), operators (see `OperatorProvider`
and `OperatorFactory`), and problems (see `ProblemProvider` and `ProblemFactory`).  Additionally, these are used by
the `Executor` and `Analyzer` classes to further simplify the creation and use of optimization algorithms.

The following steps are needed to add a new algorithm, operator, and problem using service providers:

1. First decide if the new algorithm, operator, or problem belongs in the MOEA Framework or a separate library.  The required
   changes are identical, the only difference is whether you compile it as part of the `MOEAFramework-X.X.jar` or a
   separate jar.
   
2. Create a new provider class, implementing one of the three interfaces (`AlgorithmProvider`, `OperatorProvider`,
   or `ProblemProvider`).  
   
3. Open the corresponding file in `META-INF/services` and add a new line with the fully-qualified class name for the new
   provider.

4. Try it out! Try creating and using your new code through one of the factories or through the `Executor`.



