The MOEA Framework is a free and open source Java library for developing and experimenting with multiobjective
evolutionary algorithms (MOEAs) and other metaheuristics.

![GitHub Actions status](https://github.com/MOEAFramework/MOEAFramework/workflows/Tests/badge.svg?branch=master&event=push)
![Maven Central](https://img.shields.io/maven-central/v/org.moeaframework/moeaframework)
![GitHub Downloads](https://img.shields.io/github/downloads/MOEAFramework/MOEAFramework/total?label=GitHub%20Downloads)


### Features

* Fast, reliable implementations of over 25 MOEAs, including NSGA-III and MOEA/D.
* Suite of tools for comparing and statistically testing algorithms.
* Master-slave, island-model, and hybrid parallelization.
* Permissive open source license (LGPL)


### Getting Started

Download the latest release from the [GitHub Releases](https://github.com/MOEAFramework/MOEAFramework/releases) page.  Maven users can add our
dependency to their `pom.xml` file:

```xml
<dependency>
    <groupId>org.moeaframework</groupId>
    <artifactId>moeaframework</artifactId>
    <version>5.0</version>
</dependency>
```

Check out our [examples](https://github.com/MOEAFramework/MOEAFramework/tree/master/examples),
[online documentation](https://github.com/MOEAFramework/MOEAFramework/blob/master/docs/README.md), and
[API Specification (Javadoc)](https://moeaframework.org//javadoc/index.html) to get started.


### Example

Below we solve the 2-objective DTLZ2 problem using NSGA-II:

<!-- java:examples/Example1.java [29:34] -->

```java
Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

algorithm.getResult().display();
```


### Citation

If you use this software in your work, please cite it as follows (APA style):

> Hadka, D. (2025). MOEA Framework A Free and Open Source Java Framework for Multiobjective Optimization (Version 5.0) [Computer software]. Retrieved from https://github.com/MOEAFramework/MOEAFramework.
