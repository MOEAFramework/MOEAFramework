## MOEA Framework ##

**Visit our website at [http://moeaframework.org](http://moeaframework.org)
for downloads, documentation, and examples.**

﻿<a href="https://github.com/MOEAFramework/MOEAFramework"><img alt="GitHub Actions status" src="https://github.com/MOEAFramework/MOEAFramework/workflows/Tests/badge.svg?branch=master&event=push"></a>
![Maven Central](https://img.shields.io/maven-central/v/org.moeaframework/moeaframework)
![GitHub all releases](https://img.shields.io/github/downloads/MOEAFramework/MOEAFramework/total?label=GitHub)
![SourceForge](https://img.shields.io/sourceforge/dt/moeaframework?label=SourceForge)

The MOEA Framework is a free and open source Java library for developing and
experimenting with multiobjective evolutionary algorithms (MOEAs) and other
general-purpose multiobjective optimization algorithms. The MOEA Framework
supports genetic algorithms, differential evolution, particle swarm
optimization, genetic programming, grammatical evolution, and more. A number of
algorithms are provided out-of-the-box, including NSGA-II, NSGA-III, ε-MOEA,
GDE3, PAES, PESA2, SPEA2, IBEA, SMS-EMOA, SMPSO, OMOPSO, CMA-ES, and MOEA/D.
In addition, the MOEA Framework provides the tools necessary
to rapidly design, develop, execute and statistically test optimization
algorithms.

Its key features includes:
  * Fast, reliable implementations of many state-of-the-art algorithms
  * Extensible with custom algorithms, problems and operators
  * Supports master-slave, island-model, and hybrid parallelization
  * Tools for building and statistically testing new optimization algorithms
  * Permissive open source license
  * Fully documented and tested source code

### Download ###

The latest release can be downloaded from our [website](http://moeaframework.org)
or from the [releases page](https://github.com/MOEAFramework/MOEAFramework/releases).
Maven users can add our dependency to their `pom.xml` file:

```xml
<dependency>
    <groupId>org.moeaframework</groupId>
    <artifactId>moeaframework</artifactId>
    <version>3.3</version>
</dependency>
```

Visit the [Maven distribution page](https://search.maven.org/artifact/org.moeaframework/moeaframework/3.3/jar)
for setup instructions for other package management tools.

### Documentation ###

Find our [documentation](docs/README.md), [examples](examples/), and online resources at [moeaframework.org](http://moeaframework.org)
for instructions for using the MOEA Framework.

### License ###

Copyright 2009-2022 David Hadka and other contributors.  All rights reserved.

The MOEA Framework is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or (at your
option) any later version.

The MOEA Framework is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
