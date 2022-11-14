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
```

Test suites exhibit certain problem characteristics and are useful for comparing the performance of different algorithms across a range of problem.

1. **ZDT** - 
2. **DTLZ** - Unconstrained, scalable real-valued problems
3. **LZ

ZDT	6 real-valued problems from Zitzler et al. (2000)
DTLZ	5 unconstrained, scalable real-valued problems from Deb et al. (2001)
LZ	9 real-valued problems from Hui Li and Qingfu Zhang (2009)
CEC2009	13 unconstrained and 10 constrained real-valued problems from the CEC2009 competition
WFG	9 scalable, real-valued problems by Huband et al. (2005)
BBOB-2016	55 bi-objective problems from the BBOB workshop hosted at GECCO 2016
Miscellaneous	28 real-valued, binary, permutation, and program-based test problems from the literature (e.g., knapsack, NK-landscapes)

## Problems

Problem | # of Vars | # of Objs | # of Constrs | Type 
:------ | :-------: | :-------: | :----------: | :---
Belegundu | 2 | 2 | 2 | Real
Binh | 2 | 2 | 0 | Real
Binh2 | 2 | 2 | 2 | Real
Binh3 | 2 | 3 | 0 | Real
Binh4 | 2 | 3 | 2 | Real
CF1 | 10 | 2 | 1 | Real
CF2 | 10 | 2 | 1 | Real
CF3 | 10 | 2 | 1 | Real
CF4 | 10 | 2 | 1 | Real
CF5 | 10 | 2 | 1 | Real
CF6 | 10 | 2 | 2 | Real
CF7 | 10 | 2 | 2 | Real
CF8 | 10 | 3 | 1 | Real
CF9 | 10 | 3 | 1 | Real
CF10 | 10 | 3 | 1 | Real
Fonseca | 2 | 2 | 0 | Real
Fonseca2 | 3 | 2 | 0 | Real
Jimenez $\dagger$ | 2 | 2 | 4 | Real
Kita $\dagger$ | 2 | 2 | 3 | Real
Kursawe | 3 | 2 | 0 | Real
Laumanns | 2 | 2 | 0 | Real
Lis | 2 | 2 | 0 | Real
LZ1 | 30 | 2 | 0 | Real
LZ2 | 30 | 2 | 0 | Real
LZ3 | 30 | 2 | 0 | Real
LZ4 | 30 | 2 | 0 | Real
LZ5 | 30 | 2 | 0 | Real
LZ6 | 10 | 3 | 0 | Real
LZ7 | 10 | 2 | 0 | Real
LZ8 | 10 | 2 | 0 | Real
LZ9 | 30 | 2 | 0 | Real
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
UF1 | 30 | 2 | 0 | Real
UF2 | 30 | 2 | 0 | Real
UF3 | 30 | 2 | 0 | Real
UF4 | 30 | 2 | 0 | Real
UF5 | 30 | 2 | 0 | Real
UF6 | 30 | 2 | 0 | Real
UF7 | 30 | 2 | 0 | Real
UF8 | 30 | 3 | 0 | Real
UF9 | 30 | 3 | 0 | Real
UF10 | 30 | 3 | 0 | Real
UF11 | 30 | 5 | 0 | Real
UF12 | 30 | 5 | 0 | Real
UF13 | 30 | 5 | 0 | Real
Viennet | 2 | 3 | 0 | Real
Viennet2 | 2 | 3 | 0 | Real
Viennet3 | 2 | 3 | 0 | Real
Viennet4 | 2 | 3 | 3 | Real
WFG1\_N | $9+N$ | N | 0 | Real
WFG2\_N | $9+N$ | N | 0 | Real
WFG3\_N | $9+N$ | N | 0 | Real
WFG4\_N | $9+N$ | N | 0 | Real
WFG5\_N | $9+N$ | N | 0 | Real
WFG6\_N | $9+N$ | N | 0 | Real
WFG7\_N | $9+N$ | N | 0 | Real
WFG8\_N | $9+N$ | N | 0 | Real
WFG9\_N | $9+N$ | N | 0 | Real


## Notes

Problems marked with $\dagger$ have maximized objectives.  The MOEA Framework negates the values of maximized objectives.

Problems with `_N` in the name are scalable to any number of objectives.  Replace `N` with the objective count.  For example, `DTLZ_2` references the 2-objective
DTLZ2 problem.

## References

[^deb01]: Deb et al.  "Scalable Test Problems for Evolutionary Multi-Objective Optimization."  TIK-Technical Report No 112, 2001.
[^deb02]: Deb et al. "Scalable Multi-Objective Optimization Test Problems." Congress on Evolutionary Computation. pp 825-830, 2002.
[^zhang09]: Zhang et al (2009).  "Multiobjective Optimization Test Instances for the CEC 2009 Special Session and Competition."  Technical Report CES-487, The School of Computer Science and Electronic Engineering, University of Essex.
[^zitzler00]: Zitzler et al.  "Comparison of Multiobjective Evolutionary Algorithms: Empirical Results."  Evolutionary Computation Journal.  8(2):125-148, 2000.
