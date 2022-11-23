# Extensions

The MOEA Framework is extensible, allowing third-party plugins to define their own optimization algorithms, test problems,
and mutation / crossover operators.  Refer to the individual repositories for installation and usage instructions.

## JMetal-Plugin

**Repository:** https://github.com/MOEAFramework/JMetal-Plugin

The JMetal-Plugin extension adds most of the optimization algorithms from JMetal, another open source Java library
for metaheuristic algorithms.  This includes AbYSS, CDG, DMOPSO, ESPEA, FAME, GWASFGA, MOCell, MOCHC, MOMBI,
MOMBI2, MOSA, RNSGAII, and WASFGA. 

## Generalized Decomposition

**Repository:** https://github.com/MOEAFramework/GeneralizedDecomposition

Adds the `GD-MOEA/D` algorithm, which is `MOEA/D` using weights produced by Generalized Decomposition (GD).  Unlike
regular MOEA/D that uses randomly-generated weights, the weights produced by GD are typically more uniformly-distributed
or can be used to target specific regions in space.

## Hypervolume

**Repository:** https://github.com/MOEAFramework/Hypervolume

If working on many-objective problems, you may find the hypervolume calculation built into the MOEA Framework is time
consuming.  This extension provides faster, compiled hypervolume implementations.
