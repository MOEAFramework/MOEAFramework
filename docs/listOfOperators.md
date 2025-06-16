# List of Variation Operators

## Overview

This page details mutation and crossover operators built into the MOEA Framework.  When constructing a new algorithm,
default operators for each built-in type are automatically selected and configured.  Mixed types are also supported
by combining multiple operators.

The selected operator and its configuration can be changed.  First, we can call the setter with the operator, passing
any parameters into the constructor.

<!-- :code: src=examples/Example5.java lines=36:36 -->

```java
algorithm.setVariation(new PCX(5, 2));
```

Alternatively, we can apply a configuration using the properties:

<!-- :code: src=examples/Example6.java lines=36:42 -->

```java
TypedProperties properties = new TypedProperties();
properties.setInt("populationSize", 250);
properties.setString("operator", "pcx");
properties.setInt("pcx.parents", 10);
properties.setInt("pcx.offspring", 2);

algorithm.applyConfiguration(properties);
```

The two examples above produce the same configuration, using parent-centric crossover (PCX) with 10 parents and 2
offspring.  Observe how we defined the `"operator"` property.  It's common to combine crossover and mutation
operators using `+`.  For example, for simulated binary crossover (SBX) with polynomial mutation (PM), we would use
the value `"sbx+pm"`.

## Real-Valued Operators

Real-valued decision variable store floating-point numbers ranging from $\pm \inf$.

### Simulated Binary Crossover (SBX)

SBX attempts to simulate the offspring distribution of binary-encoded single-point crossover on real-valued decision
variables [^deb94].  It accepts two parents and produces two offspring.  An example of this distribution, which favors
offspring nearer to the two parents, is shown below. 

<p align="center">
	<img src="../src/org/moeaframework/core/operator/real/doc-files/SBX.png" />
</p>

The distribution index controls the shape of the offspring distribution. Larger values for the distribution index
generates offspring closer to the parents. 

Parameter               | Default Value     | Description
:---------------------- | :---------------- | :----------
`sbx.rate`              | `1.0`             | The probability that the SBX operator is applied to a decision variable
`sbx.distributionIndex` | `15.0`            | The shape of the offspring distribution

### Polynomial Mutation (PM)

PM attempts to simulate the offspring distribution of binary-encoded bit-flip mutation on real-valued decision
variables [^deb96]. Similar to SBX, PM favors offspring nearer to the parent.  It is recommended each decision
variable is mutated with a probability of $1 / N$, where $N$ is the number of decision variables. This results in
one mutation per offspring on average. 

The distribution index controls the shape of the offspring distribution. Larger values for the distribution index
generates offspring closer to the parents.

Parameter              | Default Value     | Description
:--------------------- | :---------------- | :----------
`pm.rate`              | `1/N`             | The probability that the PM operator is applied to a decision variable
`pm.distributionIndex` | `20.0`            | The shape of the offspring distribution (larger values produce offspring closer to the parent)

### Differential Evolution (DE)

Differential evolution works by randomly selecting three distinct individuals from a population. A difference vector
is calculated between the first two individuals (shown as the left-most arrow in the figure below), which is
subsequently applied to the third individual (shown as the right-most arrow in the figure below). 

<p align="center">
	<img src="../src/org/moeaframework/core/operator/real/doc-files/DifferentialEvolution.png" />
</p>

The scaling factor parameter adjusts the magnitude of the difference vector, allowing the user to decrease or increase
the magnitude in relation to the actual difference between the individuals \citep{storn97}. The crossover rate
parameter controls the fraction of decision variables which are modified by the DE operator. 

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`de.crossoverRate`   | `0.1`             | The fraction of decision variables modified by the DE operator
`de.stepSize`        | `0.5`             | The scaling factor or step size used to adjust the length of each step taken by the DE operator

### Parent Centric Crossover (PCX)

PCX is a multiparent operator, allowing a user-defined number of parents and offspring [^deb02d]. Offspring are
clustered around the parents, as depicted in the figure below.

<p align="center">
	<img src="../src/org/moeaframework/core/operator/real/doc-files/PCX.png" />
</p>

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`pcx.parents`        | `10`              | The number of parents
`pcx.offspring`      | `2`               | The number of offspring generated by PCX
`pcx.eta`            | `0.1`             | The standard deviation of the normal distribution controlling the spread of solutions in the direction of the selected parent
`pcx.zeta`           | `0.1`             | The standard deviation of the normal distribution controlling the spread of solutions in the directions defined by the remaining parents

### Unimodal Distribution Crossover (UNDX)

UNDX is a multiparent operator, allowing a user-defined number of parents and offspring [^kita99] [^deb02]. Offspring
are centered around the centroid, forming a normal distribution whose shape is controlled by the positions of the
parents, as depicted in the figure below.

<p align="center">
	<img src="../src/org/moeaframework/core/operator/real/doc-files/UNDX.png" />
</p>

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`undx.parents`       | `10`              | The number of parents
`undx.offspring`     | `2`               | The number of offspring generated by UNDX
`undx.zeta`          | `0.5`             | The standard deviation of the normal distribution controlling the spread of solutions in the orthogonal directions defined by the parents
`undx.eta`           | `0.35`            | The standard deviation of the normal distribution controlling the spread of solutions in the remaining orthogonal directions not defined by the parents. This value is divided by $\sqrt{N}$ prior to use, where $N$ is the number of decision variables

### Simplex Crossover (SPX)

SPX is a multiparent operator, allowing a user-defined number of parents and offspring [^tsutsui99] [^higuchi00]. The
parents form a convex hull, called a simplex. Offspring are generated uniformly at random from within the simplex.
The expansion rate parameter can be used to expand the size of the simplex beyond the bounds of the parents. For
example, the figure below shows three parent points and the offspring distribution, clearly filling an expanded
triangular simplex. 

<p align="center">
	<img src="../src/org/moeaframework/core/operator/real/doc-files/SPX.png" />
</p>

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`spx.parents`        | `10`              | The number of parents
`spx.offspring`      | `2`               | The number of offspring generated by UNDX
`spx.epsilon`        | `3`               | The expansion rate

### Uniform Mutation (UM)

Each decision variable is mutated by selecting a new value within its bounds uniformly at random. The figure below
depicts the offspring distribution. It is recommended each decision variable is mutated with a probability of $1/N$,
where $N$ is the number of decision variables. This results in one mutation per offspring on average. 

<p align="center">
	<img src="../src/org/moeaframework/core/operator/real/doc-files/UM.png" />
</p>

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`um.rate`            | `1/N`             | The probability that the UM operator is applied to a decision variable

### Adaptive Metropolis (AM)

AM is a multiparent operator, allowing a user-defined number of parents and offspring [^vrugt07] [^vrugt09]. AM
produces normally-distributed clusters around each parent, where the shape of the distribution is controlled by the
covariance of the parents.

Internally, the Cholesky decomposition is used to update the resulting offspring distribution. Cholesky decomposition
requires that its input be positive definite. In order to guarantee this condition is satisfied, all parents must be
unique. In the event that the positive definite condition is not satisfied, no offspring are produced and an empty
array is returned.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`am.parents`         | `10`              | The number of parents
`am.offspring`       | `2`               | The number of offspring generated by AM
`am.coefficient`     | `2.4`             | The jump rate coefficient, controlling the standard deviation of the  covariance matrix.  The actual jump rate is calculated as $(am.coefficient / \sqrt{n})^2$

## Binary / Bit String Operators

### Half Uniform Crossover (HUX)

Half-uniform crossover (HUX) operator. Half of the non-matching bits are swapped between the two parents.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`hux.rate`           | `1.0`             | The probability that the UM operator is applied to a binary decision variable

### Bit Flip Mutation (BF)

Each bit is flipped (switched from a $0$ to a $1$, or vice versa) using the specified probability.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`bf.rate`            | `0.01`            | The probability that a bit is flipped

## Permutations

### Partially Mapped Crossover (PMX)

PMX is similar to two-point crossover, but includes a repair operator to ensure the offspring are valid permutations [^goldberg85].

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`pmx.rate`           | `1.0`             | The probability that the PMX operator is applied to a permutation decision variable

### Insertion Mutation

Randomly selects an entry in the permutation and inserts it at some other position in the permutation.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`insertion.rate`     | `0.3`             | The probability that the insertion operator is applied to a permutation decision variable

### Swap Mutation

Randomly selects two entries in the permutation and swaps their position.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`swap.rate`          | `0.3`             | The probability that the swap operator is applied to a permutation decision variable

## Subsets

### Subset Crossover (SSX)

SSX is similar to HUX crossover for binary strings, where half of the non-matching members are swapped between the two subsets.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`ssx.rate`           | `0.9`             | The probability that the SSX operator is applied to a subset decision variable

### Replace Mutation

Randomly replaces one of the members in the subset with a non-member.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`replace.rate`       | `0.3`             | The probability that the replace operator is applied to a subset decision variable

## Grammars

### Grammar Crossover (GX)

Single-point crossover for grammars. A crossover point is selected in both parents with the tail portions swapped.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`gx.rate`            | `1.0`             | The probability that the GX operator is applied to a grammar decision variable

### Grammar Mutation (GM)

Uniform mutation for grammars. Each integer codon in the grammar representation is uniformly mutated with a specified
probability.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`gm.rate`            | `1.0`             | The probability that the GM operator is applied to a grammar decision variable

## Program Tree

### Subtree Crossover (BX)

Exchanges a randomly-selected subtree from one program with a compatible, randomly-selected subtree from another program.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`bx.rate`            | `1.0`             | The probability that the BX operator is applied to a program tree decision variable

### Point Mutation (PTM)

Mutates a program by randomly selecting nodes in the expression tree and replacing the node with a new, compatible,
randomly-selected node.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`ptm.rate`            | `1.0`             | The probability that the PTM operator is applied to a program tree decision variable

## Crossover Operators

These crossover operators can be applied to any decision variable type.  They work by swapping decision variables
between solutions.  They do not change the actual value of the decision variable.

### One-Point Crossover (1X)

A crossover point is selected and all decision variables to the left/right are swapped between the two parents. The
two children resulting from this swapping are returned.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`1x.rate`            | `1.0`             | The probability that one-point crossover is applied to produce offspring

### Two-Point Crossover (2X)

Two crossover points are selected and all decision variables between the two points are swapped between the two
parents. The two children resulting from this swapping are returned.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`2x.rate`            | `1.0`             | The probability that two-point crossover is applied to produce offspring

### Uniform Crossover (UX)

Crossover operator where each index is swapped with a specified probability.

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`ux.rate`            | `1.0`             | The probability that uniform crossover is applied to produce offspring
 
## References

[^deb94]: Deb, K. and Agrawal, R. B. (1994). Simulated binary crossover for continuous search space. Technical Report No. IITK/ME/SMD-94027, Indian Institute of Technology, Kanpur, India.
[^deb96]: Deb, K. and Goyal, M. (1996). A combined genetic adaptive search (geneas) for engineering design. Computer Science and Informatics, 26(4):30–45.
[^deb02]: Deb, K., Anand, A., and Joshi, D. (2002). A computationally efficient evolutionary algorithm for real-parameter optimization. Evolutionary Computation, 10:371–395.
[^goldberg85]: Goldberg, D. E. and Jr., R. L. (1985). Alleles, loci, and the traveling salesman problem. In 1st International Conference on Genetic Algorithms and Their Applications.
[^higuchi00]: Higuchi, T., Tsutsui, S., and Yamamura, M. (2000). Theoretical analysis of simplex crossover for real-coded genetic algorithms. In Parallel Problem Solving from Nature (PPSN VI), pages 365–374.
[^kita99]: Kita, H., Ono, I., and Kobayashi, S. (1999). Multi-parental extension of the unimodal normal distribution crossover for real-coded genetic algorithms. In Proceedings of the 1999 Congress on Evolutionary Computation (CEC 1999), pages 1581–1588, Washington, DC.
[^tsutsui99]: Tsutsui, S., Yamamura, M., and Higuchi, T. (1999). Multi-parent recombination with simplex crossover in real coded genetic algorithms. In Genetic and Evolutionary Computation Conference (GECCO 1999), pages 657–664, Orlando, FL.
[^vrugt07]: Vrugt, J. A. and Robinson, B. A. (2007). Improved evolutionary optimization from genetically adaptive multimethod search. Proceedings of the National Academy of Sciences, 104(3):708–711.
[^vrugt09]: Vrugt, J. A., Robinson, B. A., and Hyman, J. M. (2009). Self-adaptive multimethod search for global optimization in real-parameter spaces. IEEE Transactions on Evolutionary Computation, 13(2):243–259.
