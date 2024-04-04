# List of Problems

## Instantiating a Problem

We can create an instance of any problem by calling its constructor:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [36:36] -->

```java
Problem problem = new UF1();
```

Several of these problems can be scaled in terms of the number of decision variables or objectives.  We can call the
relevant constructor to configure the problem.  For example, here we create the three-objective DTLZ2 problem:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [41:41] -->

```java
Problem problem = new DTLZ2(3);
```

## Test Suites

### ZDT

Contains five real-valued and one binary problem [^zitzler00].

Problem | # of Vars | # of Objs | # of Constrs | Type | Constructor | Pareto Front
:------ | :-------: | :-------: | :----------: | :--- | :---------- | ------------
ZDT1 | 30 | 2 | 0 | Real | `new ZDT1()` | <img src="imgs/ZDT1.png" width="100" />
ZDT2 | 30 | 2 | 0 | Real | `new ZDT2()` | <img src="imgs/ZDT2.png" width="100" />
ZDT3 | 30 | 2 | 0 | Real | `new ZDT3()` | <img src="imgs/ZDT3.png" width="100" />
ZDT4 | 10 | 2 | 0 | Real | `new ZDT4()` | <img src="imgs/ZDT4.png" width="100" />
ZDT5 | 80 | 2 | 0 | Binary | `new ZDT5()` | <img src="imgs/ZDT5.png" width="100" />
ZDT6 | 10 | 2 | 0 | Real | `new ZDT6()` | <img src="imgs/ZDT6.png" width="100" />

### DTLZ

Contains unconstrained real-valued problems that are scalable in the number of objectives [^deb01] [^deb02].
These problems are scalable in the number of objectives, controlled by passing the value `N` to the constructor.

Problem | # of Vars | # of Objs | # of Constrs | Type | Constructor | Pareto Front
:------ | :-------: | :-------: | :----------: | :--- | :---------- | ------------
DTLZ1 | `4+N` | `N` | 0 | Real | `new DTLZ1(N)` | <img src="imgs/DTLZ1.2D.png" width="100" /> <img src="imgs/DTLZ1.3D.png" width="100" />
DTLZ2 | `9+N` | `N` | 0 | Real | `new DTLZ2(N)` | <img src="imgs/DTLZ2.2D.png" width="100" /> <img src="imgs/DTLZ2.3D.png" width="100" />
DTLZ3 | `9+N` | `N` | 0 | Real | `new DTLZ3(N)` | <img src="imgs/DTLZ3.2D.png" width="100" /> <img src="imgs/DTLZ3.3D.png" width="100" />
DTLZ4 | `9+N` | `N` | 0 | Real | `new DTLZ4(N)` | <img src="imgs/DTLZ4.2D.png" width="100" /> <img src="imgs/DTLZ4.3D.png" width="100" />
DTLZ5 | `9+N` | `N` | 0 | Real | `new DTLZ5(N)` | <img src="imgs/DTLZ5.2D.png" width="100" /> <img src="imgs/DTLZ5.3D.png" width="100" />
DTLZ6 | `9+N` | `N` | 0 | Real | `new DTLZ6(N)` | <img src="imgs/DTLZ6.2D.png" width="100" /> <img src="imgs/DTLZ6.3D.png" width="100" />
DTLZ7 | `19+N` | `N` | 0 | Real | `new DTLZ7(N)` | <img src="imgs/DTLZ7.2D.png" width="100" /> <img src="imgs/DTLZ7.3D.png" width="100" />

### LZ

Contains nine real-valued test problems designed to have complicated Pareto sets [^li09].

Problem | # of Vars | # of Objs | # of Constrs | Type | Constructor | Pareto Front
:------ | :-------: | :-------: | :----------: | :--- | :---------- | ------------
LZ1 | 30 | 2 | 0 | Real | `new LZ1()` | <img src="imgs/LZ09_F1.png" width="100" />
LZ2 | 30 | 2 | 0 | Real | `new LZ2()` | <img src="imgs/LZ09_F2.png" width="100" />
LZ3 | 30 | 2 | 0 | Real | `new LZ3()` | <img src="imgs/LZ09_F3.png" width="100" />
LZ4 | 30 | 2 | 0 | Real | `new LZ4()` | <img src="imgs/LZ09_F4.png" width="100" />
LZ5 | 30 | 2 | 0 | Real | `new LZ5()` | <img src="imgs/LZ09_F5.png" width="100" />
LZ6 | 10 | 3 | 0 | Real | `new LZ6()` | <img src="imgs/LZ09_F6.png" width="100" />
LZ7 | 10 | 2 | 0 | Real | `new LZ7()` | <img src="imgs/LZ09_F7.png" width="100" />
LZ8 | 10 | 2 | 0 | Real | `new LZ8()` | <img src="imgs/LZ09_F8.png" width="100" />
LZ9 | 30 | 2 | 0 | Real | `new LZ9()` | <img src="imgs/LZ09_F9.png" width="100" />

### CEC2009

Constrained (CF) and unconstrained (UF) test problems used for the CEC 2009 special session and competition [^zhang09].

Problem | # of Vars | # of Objs | # of Constrs | Type | Constructor | Pareto Front
:------ | :-------: | :-------: | :----------: | :--- | :---------- | ------------
CF1 | 10 | 2 | 1 | Real | `new CF1()` | <img src="imgs/CF1.png" width="100" />
CF2 | 10 | 2 | 1 | Real | `new CF2()` | <img src="imgs/CF2.png" width="100" />
CF3 | 10 | 2 | 1 | Real | `new CF3()` | <img src="imgs/CF3.png" width="100" />
CF4 | 10 | 2 | 1 | Real | `new CF4()` | <img src="imgs/CF4.png" width="100" />
CF5 | 10 | 2 | 1 | Real | `new CF5()` | <img src="imgs/CF5.png" width="100" />
CF6 | 10 | 2 | 2 | Real | `new CF6()` | <img src="imgs/CF6.png" width="100" />
CF7 | 10 | 2 | 2 | Real | `new CF7()` | <img src="imgs/CF7.png" width="100" />
CF8 | 10 | 3 | 1 | Real | `new CF8()` | <img src="imgs/CF8.png" width="100" />
CF9 | 10 | 3 | 1 | Real | `new CF9()` | <img src="imgs/CF9.png" width="100" />
CF10 | 10 | 3 | 1 | Real | `new CF10()` | <img src="imgs/CF10.png" width="100" />
UF1 | 30 | 2 | 0 | Real | `new UF1()` | <img src="imgs/UF1.png" width="100" />
UF2 | 30 | 2 | 0 | Real | `new UF2()` | <img src="imgs/UF2.png" width="100" />
UF3 | 30 | 2 | 0 | Real | `new UF3()` | <img src="imgs/UF3.png" width="100" />
UF4 | 30 | 2 | 0 | Real | `new UF4()` | <img src="imgs/UF4.png" width="100" />
UF5 | 30 | 2 | 0 | Real | `new UF5()` | <img src="imgs/UF5.png" width="100" />
UF6 | 30 | 2 | 0 | Real | `new UF6()` | <img src="imgs/UF6.png" width="100" />
UF7 | 30 | 2 | 0 | Real | `new UF7()` | <img src="imgs/UF7.png" width="100" />
UF8 | 30 | 3 | 0 | Real | `new UF8()` | <img src="imgs/UF8.png" width="100" />
UF9 | 30 | 3 | 0 | Real | `new UF9()` | <img src="imgs/UF9.png" width="100" />
UF10 | 30 | 3 | 0 | Real | `new UF10()` | <img src="imgs/UF10.png" width="100" />
UF11 | 30 | 5 | 0 | Real | `new UF11()` | Not Available
UF12 | 30 | 5 | 0 | Real | `new UF12()` | Not Available
UF13 | 30 | 5 | 0 | Real | `new UF13()` | Not Available

### WFG

Contains nine scalable, real-valued problems by the walking fish group (WFG) [^huband05] [^huband07].

Problem | # of Vars | # of Objs | # of Constrs | Type | Constructor | Pareto Front
:------ | :-------: | :-------: | :----------: | :--- | :---------- | ------------
WFG1 | `9+N` | `N` | 0 | Real | `new WFG1(N)` | <img src="imgs/WFG1.2D.png" width="100" /> <img src="imgs/WFG1.3D.png" width="100" />
WFG2 | `9+N` | `N` | 0 | Real | `new WFG2(N)` | <img src="imgs/WFG2.2D.png" width="100" /> <img src="imgs/WFG2.3D.png" width="100" />
WFG3 | `9+N` | `N` | 0 | Real | `new WFG3(N)` | <img src="imgs/WFG3.2D.png" width="100" /> <img src="imgs/WFG3.3D.png" width="100" />
WFG4 | `9+N` | `N` | 0 | Real | `new WFG4(N)` | <img src="imgs/WFG4.2D.png" width="100" /> <img src="imgs/WFG4.3D.png" width="100" />
WFG5 | `9+N` | `N` | 0 | Real | `new WFG5(N)` | <img src="imgs/WFG5.2D.png" width="100" /> <img src="imgs/WFG5.3D.png" width="100" />
WFG6 | `9+N` | `N` | 0 | Real | `new WFG6(N)` | <img src="imgs/WFG6.2D.png" width="100" /> <img src="imgs/WFG6.3D.png" width="100" />
WFG7 | `9+N` | `N` | 0 | Real | `new WFG7(N)` | <img src="imgs/WFG7.2D.png" width="100" /> <img src="imgs/WFG7.3D.png" width="100" />
WFG8 | `9+N` | `N` | 0 | Real | `new WFG8(N)` | <img src="imgs/WFG8.2D.png" width="100" /> <img src="imgs/WFG8.3D.png" width="100" />
WFG9 | `9+N` | `N` | 0 | Real | `new WFG9(N)` | <img src="imgs/WFG9.2D.png" width="100" /> <img src="imgs/WFG9.3D.png" width="100" />

### CDTLZ

A constrained version of the DTLZ problem suite [^deb14] [^jain14].  These problems are scalable in the number of objectives,
controlled by passing the value `N` to the constructor.

Problem | # of Vars | # of Objs | # of Constrs | Type | Constructor | Pareto Front
:------ | :-------: | :-------: | :----------: | :--- | :---------- | ------------
C1_DTLZ1 | `4+N` | `N` | 1 | Real | `new C1_DTLZ1(N)` | <img src="imgs/C1_DTLZ1.2D.png" width="100" /> <img src="imgs/C1_DTLZ1.3D.png" width="100" />
C1_DTLZ3 | `9+N` | `N` | 1 | Real | `new C1_DTLZ3(N)` | <img src="imgs/C1_DTLZ3.2D.png" width="100" /> <img src="imgs/C1_DTLZ3.3D.png" width="100" />
C2_DTLZ2 | `9+N` | `N` | 1 | Real | `new C2_DTLZ2(N)` | <img src="imgs/C2_DTLZ2.2D.png" width="100" /> <img src="imgs/C2_DTLZ2.3D.png" width="100" />
C3_DTLZ1 | `4+N` | `N` | N | Real | `new C3_DTLZ1(N)` | <img src="imgs/C3_DTLZ1.2D.png" width="100" /> <img src="imgs/C3_DTLZ1.3D.png" width="100" />
C3_DTLZ4 | `9+N` | `N` | N | Real | `new C3_DTLZ4(N)` | <img src="imgs/C3_DTLZ4.2D.png" width="100" /> <img src="imgs/C3_DTLZ4.3D.png" width="100" />
Convex_C2_DTLZ2 | `9+N` | `N` | 1 | Real | `new ConvexC2_DTLZ2(N)` | <img src="imgs/Convex_C2_DTLZ2.2D.png" width="100" /> <img src="imgs/Convex_C2_DTLZ2.3D.png" width="100" />

### LSMOP

Large-scale multi- and many-objective problem test suite [^cheng17].  These problems are scalable in the number of objectives,
controlled by passing the value `N` to the constructor.

Problem | # of Vars | # of Objs | # of Constrs | Type | Constructor | Pareto Front
:------ | :-------: | :-------: | :----------: | :--- | :---------- | ------------
LSMOP1 | ??? | `N` | 0 | Real | `new LSMOP1(N)` | <img src="imgs/LSMOP1.2D.png" width="100" />
LSMOP2 | ??? | `N` | 0 | Real | `new LSMOP2(N)` | <img src="imgs/LSMOP2.2D.png" width="100" />
LSMOP3 | ??? | `N` | 0 | Real | `new LSMOP3(N)` | <img src="imgs/LSMOP3.2D.png" width="100" />
LSMOP4 | ??? | `N` | 0 | Real | `new LSMOP4(N)` | <img src="imgs/LSMOP4.2D.png" width="100" />
LSMOP5 | ??? | `N` | 0 | Real | `new LSMOP5(N)` | <img src="imgs/LSMOP5.2D.png" width="100" />
LSMOP6 | ??? | `N` | 0 | Real | `new LSMOP6(N)` | <img src="imgs/LSMOP6.2D.png" width="100" />
LSMOP7 | ??? | `N` | 0 | Real | `new LSMOP7(N)` | <img src="imgs/LSMOP7.2D.png" width="100" />
LSMOP8 | ??? | `N` | 0 | Real | `new LSMOP8(N)` | <img src="imgs/LSMOP8.2D.png" width="100" />
LSMOP9 | ??? | `N` | 0 | Real | `new LSMOP9(N)` | <img src="imgs/LSMOP9.2D.png" width="100" />

The number of decision variables depends on the how the problem is configured.  Use
`problem.getNumberOfVariables()` to lookup the exact values.

### ZCAT

Set of challenging test problems for multi- and many-objective optimization [^zapotecas23].  These problems are
scalable in the number of objectives, controlled by passing the value `N` to the constructor.

Problem | # of Vars | # of Objs | # of Constrs | Type | Constructor | Pareto Front
:------ | :-------: | :-------: | :----------: | :--- | :---------- | ------------
ZCAT1 | `10*N` | `N` | 0 | Real | `new ZCAT1(N)` | <img src="imgs/ZCAT1.2D.png" width="100" /> <img src="imgs/ZCAT1.3D.png" width="100" />
ZCAT2 | `10*N` | `N` | 0 | Real | `new ZCAT2(N)` | <img src="imgs/ZCAT2.2D.png" width="100" /> <img src="imgs/ZCAT2.3D.png" width="100" />
ZCAT3 | `10*N` | `N` | 0 | Real | `new ZCAT3(N)` | <img src="imgs/ZCAT3.2D.png" width="100" /> <img src="imgs/ZCAT3.3D.png" width="100" />
ZCAT4 | `10*N` | `N` | 0 | Real | `new ZCAT4(N)` | <img src="imgs/ZCAT4.2D.png" width="100" /> <img src="imgs/ZCAT4.3D.png" width="100" />
ZCAT5 | `10*N` | `N` | 0 | Real | `new ZCAT5(N)` | <img src="imgs/ZCAT5.2D.png" width="100" /> <img src="imgs/ZCAT5.3D.png" width="100" />
ZCAT6 | `10*N` | `N` | 0 | Real | `new ZCAT6(N)` | <img src="imgs/ZCAT6.2D.png" width="100" /> <img src="imgs/ZCAT6.3D.png" width="100" />
ZCAT7 | `10*N` | `N` | 0 | Real | `new ZCAT7(N)` | <img src="imgs/ZCAT7.2D.png" width="100" /> <img src="imgs/ZCAT7.3D.png" width="100" />
ZCAT8 | `10*N` | `N` | 0 | Real | `new ZCAT8(N)` | <img src="imgs/ZCAT8.2D.png" width="100" /> <img src="imgs/ZCAT8.3D.png" width="100" />
ZCAT9 | `10*N` | `N` | 0 | Real | `new ZCAT9(N)` | <img src="imgs/ZCAT9.2D.png" width="100" /> <img src="imgs/ZCAT9.3D.png" width="100" />
ZCAT10 | `10*N` | `N` | 0 | Real | `new ZCAT10(N)` | <img src="imgs/ZCAT10.2D.png" width="100" /> <img src="imgs/ZCAT10.3D.png" width="100" />
ZCAT11 | `10*N` | `N` | 0 | Real | `new ZCAT11(N)` | <img src="imgs/ZCAT11.2D.png" width="100" /> <img src="imgs/ZCAT11.3D.png" width="100" />
ZCAT12 | `10*N` | `N` | 0 | Real | `new ZCAT12(N)` | <img src="imgs/ZCAT12.2D.png" width="100" /> <img src="imgs/ZCAT12.3D.png" width="100" />
ZCAT13 | `10*N` | `N` | 0 | Real | `new ZCAT13(N)` | <img src="imgs/ZCAT13.2D.png" width="100" /> <img src="imgs/ZCAT13.3D.png" width="100" />
ZCAT14 | `10*N` | `N` | 0 | Real | `new ZCAT14(N)` | <img src="imgs/ZCAT14.2D.png" width="100" /> <img src="imgs/ZCAT14.3D.png" width="100" />
ZCAT15 | `10*N` | `N` | 0 | Real | `new ZCAT15(N)` | <img src="imgs/ZCAT15.2D.png" width="100" /> <img src="imgs/ZCAT15.3D.png" width="100" />
ZCAT16 | `10*N` | `N` | 0 | Real | `new ZCAT16(N)` | <img src="imgs/ZCAT16.2D.png" width="100" /> <img src="imgs/ZCAT16.3D.png" width="100" />
ZCAT17 | `10*N` | `N` | 0 | Real | `new ZCAT17(N)` | <img src="imgs/ZCAT17.2D.png" width="100" /> <img src="imgs/ZCAT17.3D.png" width="100" />
ZCAT18 | `10*N` | `N` | 0 | Real | `new ZCAT18(N)` | <img src="imgs/ZCAT18.2D.png" width="100" /> <img src="imgs/ZCAT18.3D.png" width="100" />
ZCAT19 | `10*N` | `N` | 0 | Real | `new ZCAT19(N)` | <img src="imgs/ZCAT19.2D.png" width="100" /> <img src="imgs/ZCAT19.3D.png" width="100" />
ZCAT20 | `10*N` | `N` | 0 | Real | `new ZCAT20(N)` | <img src="imgs/ZCAT20.2D.png" width="100" /> <img src="imgs/ZCAT20.3D.png" width="100" />

## Miscellaneous Problems

Individual problems found throughout the literature that do not belong to a specific test suite.

Problem | # of Vars | # of Objs | # of Constrs | Type | Constructor | Pareto Front
:------ | :-------: | :-------: | :----------: | :--- | :---------- | ------------
Belegundu | 2 | 2 | 2 | Real | `new Belegundu()` | <img src="imgs/Belegundu.png" width="100" />
Binh | 2 | 2 | 0 | Real | `new Binh()` | <img src="imgs/Binh.png" width="100" />
Binh2 | 2 | 2 | 2 | Real | `new Binh2()` | <img src="imgs/Binh2.png" width="100" />
Binh3 | 2 | 3 | 0 | Real | `new Binh3()` | <img src="imgs/Binh3.png" width="100" />
Binh4 | 2 | 3 | 2 | Real | `new Binh4()` | <img src="imgs/Binh4.png" width="100" />
Fonseca | 2 | 2 | 0 | Real | `new Fonseca()` | <img src="imgs/Fonseca.png" width="100" />
Fonseca2 | 3 | 2 | 0 | Real | `new Fonseca2()` | <img src="imgs/Fonseca2.png" width="100" />
Jimenez (Maximized) | 2 | 2 | 4 | Real | `new Jimenez()` | <img src="imgs/Jimenez.png" width="100" />
Kita (Maximized) | 2 | 2 | 3 | Real | `new Kita()` | <img src="imgs/Kita.png" width="100" />
Kursawe | 3 | 2 | 0 | Real | `new Kursawe()` | <img src="imgs/Kursawe.png" width="100" />
Laumanns | 2 | 2 | 0 | Real | `new Laumanns()` | <img src="imgs/Laumanns.png" width="100" />
Lis | 2 | 2 | 0 | Real | `new Lis()` | <img src="imgs/Lis.png" width="100" />
Murata | 2 | 2 | 0 | Real | `new Murata()` | <img src="imgs/Murata.png" width="100" />
Obayashi (Maximized) | 2 | 2 | 1 | Real | `new Obayashi()` | <img src="imgs/Obayashi.png" width="100" />
OKA1 | 2 | 2 | 0 | Real | `new OKA1()` | <img src="imgs/OKA1.png" width="100" />
OKA2 | 3 | 2 | 0 | Real | `new OKA2()` | <img src="imgs/OKA2.png" width="100" />
Osyczka | 2 | 2 | 2 | Real | `new Osyczka()` | <img src="imgs/Osyczka.png" width="100" />
Osyczka2 | 6 | 2 | 6 | Real | `new Osyczka2()` | <img src="imgs/Osyczka2.png" width="100" />
Poloni (Maximized) | 2 | 2 | 0 | Real | `new Poloni()` | <img src="imgs/Poloni.png" width="100" />
Quagliarella | 16 | 2 | 0 | Real | `new Quagliarella()` | <img src="imgs/Quagliarella.png" width="100" />
Rendon | 2 | 2 | 0 | Real | `new Rendon()` | <img src="imgs/Rendon.png" width="100" />
Rendon2 | 2 | 2 | 0 | Real | `new Rendon2()` | <img src="imgs/Rendon2.png" width="100" />
Schaffer | 1 | 2 | 0 | Real | `new Schaffer()` | <img src="imgs/Schaffer.png" width="100" />
Schaffer2 | 1 | 2 | 0 | Real | `new Schaffer2()` | <img src="imgs/Schaffer2.png" width="100" />
Srinivas | 2 | 2 | 2 | Real | `new Srinivas()` | <img src="imgs/Srinivas.png" width="100" />
Tamaki (Maximized) | 3 | 3 | 1 | Real | `new Tamaki()` | <img src="imgs/Tamaki.png" width="100" />
Tanaka | 2 | 2 | 2 | Real | `new Tanaka()` | <img src="imgs/Tanaka.png" width="100" />
Viennet | 2 | 3 | 0 | Real | `new Viennet()` | <img src="imgs/Viennet.png" width="100" />
Viennet2 | 2 | 3 | 0 | Real | `new Viennet2()` | <img src="imgs/Viennet2.png" width="100" />
Viennet3 | 2 | 3 | 0 | Real | `new Viennet3()` | <img src="imgs/Viennet3.png" width="100" />
Viennet4 | 2 | 3 | 3 | Real | `new Viennet4()` | <img src="imgs/Viennet4.png" width="100" />

Note that several of these problems are maximized.  Since the MOEA Framework only supports minimization, the
objective values are negated!

## BBOB-2016

Contains the 55 bi-objective problems from the "bbob-biobj" test suite presented at the GECCO 2016 BBOB
workshop [^finck15].  These bi-objective problems are formed by combining two single-objective functions.  The easiest
way to construct a BBOB 2016 problem instance is from its name.  Each single-objective function is defined
by its (1) test function number, (2) instance number, and (3) dimension, given as:

```
bbob_f<val>_i<val>_d<val>
```

For example, `bbob_f1_i2_d5` would use function `1` (Sphere), instance `2`, and `5` decision variables.  Then,
to construct the two-objective version, we simply combine two of these single-objective functions with a comma.
Here's an example:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [52:52] -->

```java
Problem problem = ProblemFactory.getInstance().getProblem("bbob-biobj(bbob_f1_i2_d5,bbob_f21_i2_d5)");
```

For more details on the specific problem instances, see http://numbbo.github.io/coco-doc/bbob-biobj/functions/.

## Problem Wrappers

Problem wrappers modify or extend existing problems, typically in an effort to make the problem more challenging.

### Scaling Objectives

The `ScaledProblem` wrapper applies a scaling factor to each objective by multipling the i-th objecive
value by $b^i$, where $b=2$ in the example below.  This helps avoid any bias caused by assuming all objectives
have similar ranges.

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [58:58] -->

```java
Problem problem = new ScaledProblem(new DTLZ2(2), 2.0);
```

### Rotating Decision Variables

The `RotatedProblem` wrapper applies a rotation to the decision variables.  This is beneficial as it converts what
would be independent decision variables (which is typically easier to optimize) into a version with linear relationships
between the variables.  We can customize the rotation matrix, selecting all or a subset of decision variables, by
constructing the `RotationMatrixBuilder`.  The example below demonstrates applying a 45 degree rotation to each axis.

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [63:66] -->

```java
RotationMatrixBuilder builder = new RotationMatrixBuilder(11);
builder.rotateAll().withThetas(Math.toRadians(45));

Problem problem = new RotatedProblem(new DTLZ2(2), builder.create());
```

### Timings

The `TimingProblem` wrapper is used to measure the total time spent performing function evaluations.  This can
capture up to a nanoseconds resolution, as long as the system supports that level of accuracy.

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [71:73] -->

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
