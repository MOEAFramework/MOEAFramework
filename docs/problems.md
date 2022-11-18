# List of Problems

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

Contains five unconstrained real-valued problems [^deb01] [^deb02].  These problems are scalable in the number of objectives.  Control this by replacing
`N` in the name.  For example, `DTLZ2_2` creates the 2-objective DTLZ2 instance.

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
`DTLZ1_N` | `4+N` | N | 0 | Real
`DTLZ2_N` | `9+N` | N | 0 | Real
`DTLZ3_N` | `9+N` | N | 0 | Real
`DTLZ4_N` | `9+N` | N | 0 | Real
`DTLZ7_N` | `19+N` | N | 0 | Real

Note that `DTLZ5` and `DTLZ6` are not included as they are degenerate at higher dimensions.

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

A constrained version of the DTLZ problem suite [^deb14] [^jain14].  These problems are scalable in the number of objectives.  Control this by replacing
`N` in the name.

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
`C1_DTLZ1_N` | `4+N` | N | 1 | Real
`C1_DTLZ3_N` | `9+N` | N | 1 | Real
`C2_DTLZ2_N` | `9+N` | N | 1 | Real
`C3_DTLZ1_N` | `4+N` | N | N | Real
`C3_DTLZ4_N` | `9+N` | N | N | Real
`Convex_C2_DTLZ2_N` | `9+N` | N | 1 | Real

### BBOB-2016

Contains the 55 bi-objective problems as part of the "bbob-biobj" test suite from the BBOB workshop hosted at GECCO 2016 [^finck15].
These bi-objective problems are formed by combining two single-objective functions.  Additionally, each problem is further customizable
by the dimension (number of decision variables) and instance number (which varies the location of the optimum point).

The MOEA Framework uses a special name format for these problems.  Each single-objective function has a unique name in the form
`bbob_f<N>_i<N>_d<N>` where `f`, `i`, and `d` represent the test function number, instance, and dimension, respectively.  The `<N>` is
replaced by a specific value for each.  For example, `bbob_f1_i2_d5` represents the first BBOB function (the Sphere function), instance `2`, with 
five decision variables.

Next, two of these single-objective functions are combined to get the bi-objective problem name, separating the names by a comma.
For example, `bbob_f1_i2_d5,bbob_f21_i2_d5` would produce the $(f_1, f_{21})$ bi-objective problem with five decision variables.

To recreate the bbob-biobj test suite from the BBOB workshop, we can enumerate the set of problems using:

```java

int[] functions = new int[] { 1, 2, 6, 8, 13, 14, 15, 17, 20, 21 };
int[] instances = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
int[] dimensions = new int[] { 2, 3, 5, 10, 20, 40 };

List<String> bbob_biobj_names = new ArrayList<String>();

for (int i = 0; i < functions.length; i++) {
    for (int j = i; j < functions.length; j++) {
        for (int instance : instances) {
            for (int dimension : dimensions) {
            	bbob_biobj_names.add(String.format("bbob_f%d_i%d_d%d,bbob_f%d_i%d_d%d",
            			functions[i], instance, dimension, functions[j], instance, dimension));
            }
        }
    }
}
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

Problems marked with $\dagger$ have maximized objectives.  Since the MOEA Framework only works with minimized objectives, the objective values are negated.

## Special Problem Classes

### Rotated Problems

In many test problems, there is a direct relationship between decision variables and objectives, often to the point where once can tweak each variable
independently to find the optimum.  Any real-valued problem can be rotated in decision variable space to remove this independence and create a linear
relationship between the variables.  This is accomplished by prepending one of the following to the problem name:

* `ROT_` - Rotates all decision variables by 45 degrees
* `ROT(X)_` - Rotates all decision variables by `X` degrees.  Replace `X` with `RAND` to randomly rotated each axis.
* `ROT(K,X)_` - Makes K rotatations along random planes.  Replace `K` with `ALL` to rotate all axes.

For example:

```java
NondominatedPopulation results = new Executor()
    .withProblem("ROT_DTLZ2_2")
    .withAlgorithm("NSGA-II")
    .withProperty("populationSize", 250)
    .withMaxEvaluations(10000)
    .run();
```

### Scripted or External Problems

The MOEA Framework also has the capability to execute problems written in a scripting language (using the appropriate Java plugin) or external, compiled
programs.  Refer to `ScriptedProblem` and `ExternalProblem` for details.

[^deb01]: Deb et al.  "Scalable Test Problems for Evolutionary Multi-Objective Optimization."  TIK-Technical Report No 112, 2001.
[^deb02]: Deb et al. "Scalable Multi-Objective Optimization Test Problems." Congress on Evolutionary Computation. pp 825-830, 2002.
[^deb14]: Deb, K. and H. Jain.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based Nondominated Sorting Approach, Part I: Solving Problems With Box Constraints."  IEEE Transactions on Evolutionary Computation, 18(4):577-601, 2014.
[^jain14]: Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach." IEEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
[^finck15]: Finck, S., N. Hansen, R. Ros, and A. Auger.  "Real-Parameter Black-Box Optimization Benchmarking 2010: Presentation of the Noiseless Functions."  Working Paper 2009/20, compiled November 17, 2015.
[^huband07]: Huband et al. "A Review of Multi-Objective Test Problems and a Scalable Test Problem Toolkit." IEEE Transactions on Evolutionary Computation. 10(5):477-506, 2007.
[^huband05]: Huband et al. "A Scalable Multi-Objective Test Problem Toolkit." 3rd International Conference on Evolutionary Multi-Criterion Optimization. pp 280-294, 2005.
[^li09]: Li and Zhang (2009). "Multiobjective Optimization Problems with Complicated Pareto Sets, MOEA/D and NSGA-II."  IEEE Transactions on Evolutionary Computation.  13(2):284-302.
[^zhang09]: Zhang et al (2009).  "Multiobjective Optimization Test Instances for the CEC 2009 Special Session and Competition."  Technical Report CES-487, The School of Computer Science and Electronic Engineering, University of Essex.
[^zitzler00]: Zitzler et al.  "Comparison of Multiobjective Evolutionary Algorithms: Empirical Results."  Evolutionary Computation Journal.  8(2):125-148, 2000.
