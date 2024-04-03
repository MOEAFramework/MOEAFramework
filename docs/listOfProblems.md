# List of Problems

## Contents

* [ZDT](#zdt)
* [DTLZ](#dtlz)
* [LZ](#lz)
* [CEC2009](#cec2009)
* [WFG](#wfg)
* [CDTLZ](#cdtlz)
* [LSMOP](#lsmop)
* [ZCAT](#zcat)
* [BBOB 2016](#bbob-2016)
* [Individual Problems](#individual-problems)
* [Problem Wrappers](#problem-wrappers)

## Instantiating a Problem

We can create an instance of any problem by calling its constructor:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [39:39] -->

```java
Problem problem = new UF1();
```

Several of these problems can be scaled in terms of the number of decision variables or objectives.  We can call the
relevant constructor to configure the problem.  For example, here we create the three-objective DTLZ2 problem:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [44:44] -->

```java
Problem problem = new DTLZ2(3);
```

We can also construct problems by name using the `ProblemFactory`.  This is primarily used when
[running large-scale experiments](runningExperiments.md).  Here we create the same three-objective DTLZ2 problem:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [49:49] -->

```java
Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_3");
```

## Test Suites

### ZDT

Contains five real-valued and one binary problem [^zitzler00].

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
`ZDT1` | 30 | 2 | 0 | Real
`ZDT2` | 30 | 2 | 0 | Real
`ZDT3` | 30 | 2 | 0 | Real
`ZDT4` | 10 | 2 | 0 | Real
`ZDT5` | 80 | 2 | 0 | Binary
`ZDT6` | 10 | 2 | 0 | Real

### DTLZ

Contains five unconstrained real-valued problems [^deb01] [^deb02].  These problems are scalable in the number of
objectives.  Control this by replacing `N` in the name.  For example, `DTLZ2_2` creates the 2-objective DTLZ2
instance.

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
`DTLZ1_N` | `4+N` | N | 0 | Real
`DTLZ2_N` | `9+N` | N | 0 | Real
`DTLZ3_N` | `9+N` | N | 0 | Real
`DTLZ4_N` | `9+N` | N | 0 | Real
`DTLZ5_N` | `9+N` | N | 0 | Real
`DTLZ6_N` | `9+N` | N | 0 | Real
`DTLZ7_N` | `19+N` | N | 0 | Real

### LZ

Contains nine real-valued test problems designed to have complicated Pareto sets [^li09].

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
`LZ1` | 30 | 2 | 0 | Real
`LZ2` | 30 | 2 | 0 | Real
`LZ3` | 30 | 2 | 0 | Real
`LZ4` | 30 | 2 | 0 | Real
`LZ5` | 30 | 2 | 0 | Real
`LZ6` | 10 | 3 | 0 | Real
`LZ7` | 10 | 2 | 0 | Real
`LZ8` | 10 | 2 | 0 | Real
`LZ9` | 30 | 2 | 0 | Real

### CEC2009

Constrained (CF) and unconstrained (UF) test problems used for the CEC 2009 special session and competition [^zhang09].

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
`CF1` | 10 | 2 | 1 | Real
`CF2` | 10 | 2 | 1 | Real
`CF3` | 10 | 2 | 1 | Real
`CF4` | 10 | 2 | 1 | Real
`CF5` | 10 | 2 | 1 | Real
`CF6` | 10 | 2 | 2 | Real
`CF7` | 10 | 2 | 2 | Real
`CF8` | 10 | 3 | 1 | Real
`CF9` | 10 | 3 | 1 | Real
`CF10` | 10 | 3 | 1 | Real
`UF1` | 30 | 2 | 0 | Real
`UF2` | 30 | 2 | 0 | Real
`UF3` | 30 | 2 | 0 | Real
`UF4` | 30 | 2 | 0 | Real
`UF5` | 30 | 2 | 0 | Real
`UF6` | 30 | 2 | 0 | Real
`UF7` | 30 | 2 | 0 | Real
`UF8` | 30 | 3 | 0 | Real
`UF9` | 30 | 3 | 0 | Real
`UF10` | 30 | 3 | 0 | Real
`UF11` | 30 | 5 | 0 | Real
`UF12` | 30 | 5 | 0 | Real
`UF13` | 30 | 5 | 0 | Real

### WFG

Contains nine scalable, real-valued problems by the walking fish group (WFG) [^huband05] [^huband07].

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
`WFG1_N` | `9+N` | N | 0 | Real
`WFG2_N` | `9+N` | N | 0 | Real
`WFG3_N` | `9+N` | N | 0 | Real
`WFG4_N` | `9+N` | N | 0 | Real
`WFG5_N` | `9+N` | N | 0 | Real
`WFG6_N` | `9+N` | N | 0 | Real
`WFG7_N` | `9+N` | N | 0 | Real
`WFG8_N` | `9+N` | N | 0 | Real
`WFG9_N` | `9+N` | N | 0 | Real

### CDTLZ

A constrained version of the DTLZ problem suite [^deb14] [^jain14].  These problems are scalable in the number of
objectives.  Control this by replacing `N` in the name.

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
`C1_DTLZ1_N` | `4+N` | N | 1 | Real
`C1_DTLZ3_N` | `9+N` | N | 1 | Real
`C2_DTLZ2_N` | `9+N` | N | 1 | Real
`C3_DTLZ1_N` | `4+N` | N | N | Real
`C3_DTLZ4_N` | `9+N` | N | N | Real
`Convex_C2_DTLZ2_N` | `9+N` | N | 1 | Real

### LSMOP

Large-scale multi- and many-objective problem test suite [^cheng17].  These problems are scalable in the number of
objectives.  Control this by replacing `N` in the name.

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
`LSMOP1_N` | ??? | N | 0 | Real
`LSMOP2_N` | ??? | N | 0 | Real
`LSMOP3_N` | ??? | N | 0 | Real
`LSMOP4_N` | ??? | N | 0 | Real
`LSMOP5_N` | ??? | N | 0 | Real
`LSMOP6_N` | ??? | N | 0 | Real
`LSMOP7_N` | ??? | N | 0 | Real
`LSMOP8_N` | ??? | N | 0 | Real
`LSMOP9_N` | ??? | N | 0 | Real

The number of decision variables depends on the how the problem is configured.  Use
`problem.getNumberOfVariables()` to lookup the exact values.

### ZCAT

Set of challenging test problems for multi- and many-objective optimization [^zapotecas23].  These problems are
scalable in the number of objectives.  Control this by replacing `N` in the name.

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
`ZCAT1_N` | 10*N | N | 0 | Real
`ZCAT2_N` | 10*N | N | 0 | Real
`ZCAT3_N` | 10*N | N | 0 | Real
`ZCAT4_N` | 10*N | N | 0 | Real
`ZCAT5_N` | 10*N | N | 0 | Real
`ZCAT6_N` | 10*N | N | 0 | Real
`ZCAT7_N` | 10*N | N | 0 | Real
`ZCAT8_N` | 10*N | N | 0 | Real
`ZCAT9_N` | 10*N | N | 0 | Real
`ZCAT10_N` | 10*N | N | 0 | Real
`ZCAT11_N` | 10*N | N | 0 | Real
`ZCAT12_N` | 10*N | N | 0 | Real
`ZCAT13_N` | 10*N | N | 0 | Real
`ZCAT14_N` | 10*N | N | 0 | Real
`ZCAT15_N` | 10*N | N | 0 | Real
`ZCAT16_N` | 10*N | N | 0 | Real
`ZCAT17_N` | 10*N | N | 0 | Real
`ZCAT18_N` | 10*N | N | 0 | Real
`ZCAT19_N` | 10*N | N | 0 | Real
`ZCAT20_N` | 10*N | N | 0 | Real

### BBOB-2016

Contains the 55 bi-objective problems as part of the "bbob-biobj" test suite along with the extended "bbob-biobj-ext"
problems from the BBOB workshop hosted at GECCO 2016 [^finck15].  These bi-objective problems are formed by combining
two single-objective functions.

The easiest way to construct a BBOB 2016 problem instance is from its name.  Each single-objective function is defined
by its (1) test function number, (2) instance number, and (3) dimension, given as:

```
bbob_f<val>_i<val>_d<val>
```

For example, `bbob_f1_i2_d5` would use the function `1` (Sphere), instance `2`, and `5` decision variables.  Then,
to construct the two-objective version, we simply combine two of these single-objective functions with a comma.
Here's an example:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [55:55] -->

```java
Problem problem = ProblemFactory.getInstance().getProblem("bbob-biobj(bbob_f1_i2_d5,bbob_f21_i2_d5)");
```

## Individual Problems

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
Belegundu | 2 | 2 | 2 | Real
Binh | 2 | 2 | 0 | Real
Binh2 | 2 | 2 | 2 | Real
Binh3 | 2 | 3 | 0 | Real
Binh4 | 2 | 3 | 2 | Real
Fonseca | 2 | 2 | 0 | Real
Fonseca2 | 3 | 2 | 0 | Real
Jimenez $\dagger$ | 2 | 2 | 4 | Real
Kita $\dagger$ | 2 | 2 | 3 | Real
Kursawe | 3 | 2 | 0 | Real
Laumanns | 2 | 2 | 0 | Real
Lis | 2 | 2 | 0 | Real
Murata | 2 | 2 | 0 | Real
Obayashi $\dagger$ | 2 | 2 | 1 | Real
OKA1 | 2 | 2 | 0 | Real
OKA2 | 3 | 2 | 0 | Real
Osyczka | 2 | 2 | 2 | Real
Osyczka2 | 6 | 2 | 6 | Real
Poloni $\dagger$ | 2 | 2 | 0 | Real
Quagliarella | 16 | 2 | 0 | Real
Rendon | 2 | 2 | 0 | Real
Rendon2 | 2 | 2 | 0 | Real
Schaffer | 1 | 2 | 0 | Real
Schaffer2 | 1 | 2 | 0 | Real
Srinivas | 2 | 2 | 2 | Real
Tamaki $\dagger$ | 3 | 3 | 1 | Real
Tanaka | 2 | 2 | 2 | Real
Viennet | 2 | 3 | 0 | Real
Viennet2 | 2 | 3 | 0 | Real
Viennet3 | 2 | 3 | 0 | Real
Viennet4 | 2 | 3 | 3 | Real

Problems marked with $\dagger$ have maximized objectives.  Since the MOEA Framework only works with minimized
objectives, the objective values are negated.

## Problem Wrappers

These wrappers add to or modify existing problems:

### Scaled Problems

Many test problems are defined with similar ranges for objective values.  To counteract any bias, we can apply a
scaling factor to each objective.  In this example, we will scale the i-th objective by $2^i$:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [61:61] -->

```java
Problem problem = new ScaledProblem(new DTLZ2(2), 2.0);
```

### Rotated Problems

Algorithms can also take advantage when each decision variable is independent.  We can counteract this by rotating
the problem in decision variable space, creating a linear relationship between the variables.  We first define a
rotation matrix using the `RotationMatrixBuilder`.  We can fully customize the rotation matrix, but here we
demonstrate applying a 45-degree rotation to each axis:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [66:69] -->


```java
RotationMatrixBuilder builder = new RotationMatrixBuilder(11);
builder.rotateAll().withThetas(Math.toRadians(45));

Problem problem = new RotatedProblem(new DTLZ2(2), builder.create());
```

### Timings

The `TimingProblem` wrapper is used to measure the total time spent performing function evaluations:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [74:76] -->

```java
TimingProblem problem = new TimingProblem(new DTLZ2(2));

System.out.println(problem.getNFE() + " evaluations took " + problem.getSeconds() + " sec.");
```

[^cheng17]: Cheng et al. "Test problems for large-scale multiobjective and many-objective optimization." IEEE Transactions on Cybernetics, 7(12): 4108-4121, 2017.
[^deb01]: Deb et al.  "Scalable Test Problems for Evolutionary Multi-Objective Optimization."  TIK-Technical Report No 112, 2001.
[^deb02]: Deb et al. "Scalable Multi-Objective Optimization Test Problems." Congress on Evolutionary Computation. pp 825-830, 2002.
[^deb14]: Deb, K. and H. Jain.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based Nondominated Sorting Approach, Part I: Solving Problems With Box Constraints."  IEEE Transactions on Evolutionary Computation, 18(4):577-601, 2014.
[^jain14]: Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach." IEEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
[^finck15]: Finck, S., N. Hansen, R. Ros, and A. Auger.  "Real-Parameter Black-Box Optimization Benchmarking 2010: Presentation of the Noiseless Functions."  Working Paper 2009/20, compiled November 17, 2015.
[^huband07]: Huband et al. "A Review of Multi-Objective Test Problems and a Scalable Test Problem Toolkit." IEEE Transactions on Evolutionary Computation. 10(5):477-506, 2007.
[^huband05]: Huband et al. "A Scalable Multi-Objective Test Problem Toolkit." 3rd International Conference on Evolutionary Multi-Criterion Optimization. pp 280-294, 2005.
[^li09]: Li and Zhang (2009). "Multiobjective Optimization Problems with Complicated Pareto Sets, MOEA/D and NSGA-II."  IEEE Transactions on Evolutionary Computation.  13(2):284-302.
[^zapotecas23]: Zapotecas-Martínez et al.  "Challenging test problems for multi-and many-objective optimization." Swarm and Evolutionary Computation, Volume 81, 101350, ISSN 2210-6502, 2023.
[^zhang09]: Zhang et al (2009).  "Multiobjective Optimization Test Instances for the CEC 2009 Special Session and Competition."  Technical Report CES-487, The School of Computer Science and Electronic Engineering, University of Essex.
[^zitzler00]: Zitzler et al.  "Comparison of Multiobjective Evolutionary Algorithms: Empirical Results."  Evolutionary Computation Journal.  8(2):125-148, 2000.
