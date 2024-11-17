---
carousels:
  - images: 
    - image: assets/carousel/diagnosticTool.png
    - image: assets/carousel/runtimeViewer.png
    - image: assets/carousel/tsp.png
    - image: assets/carousel/symbolicRegression.png
---

The MOEA Framework is a free and open source Java library for developing and experimenting with multiobjective
evolutionary algorithms (MOEAs) and other metaheuristics.

### Features

* Fast, reliable implementations of over 25 MOEAs, including NSGA-III and MOEA/D.
* Suite of tools for comparing and statistically testing algorithms.
* Master-slave, island-model, and hybrid parallelization.
* Permissive open source license (LGPL)

### Getting Started

Visit us on [GitHub]({{ site.github.repository_url }}) to download the latest release.  Maven users can add our
dependency to their `pom.xml` file:

```xml
<dependency>
    <groupId>org.moeaframework</groupId>
    <artifactId>moeaframework</artifactId>
    <version>{{ site.version }}</version>
</dependency>
```

Check out our [examples]({{ site.github.repository_url }}/tree/master/examples),
[online documentation]({{ site.github.repository_url }}/blob/master/docs/README.md), and
[API Specification (Javadoc)](javadoc/index.html) to get started.

### Example

Below we solve the 2-objective DTLZ2 problem using NSGA-II:

<!-- java:examples/Example1.java [29:34] -->

```java
Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

algorithm.getResult().display();
```

In addition, the MOEA Framework includes a suite of diagnostic and visualization tools:

{% include carousel.html height="500" unit="px" duration="7" number="1" %}

### Citation

If you use this software in your work, please cite it as follows (APA style):

> Hadka, D. (2024). MOEA Framework: A Free and Open Source Java Framework for Multiobjective Optimization (Version {{ site.version }}) [Computer software].  Retrieved from {{ site.github.repository_url }}.
