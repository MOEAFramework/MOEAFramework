# MOEA/D and Generalized Decomposition

## MOEA/D

MOEA/D is a popular multi-objective optimization algorithm that decomposes a multi-objective problem into many
single-objective subproblems, optimizing these subproblems simultaneously to approximate the Pareto front.  Commonly,
the Chebyshev (aka Tchebycheff) scalarizing function is used:

$$ g(x) = \lVert w \left| f(x) - z^* \right| \rVert_\infty = \max_i \left( w_i \left| f_i(x) - z_i^* \right| \right) $$

This is also known as the L-inf norm, where $$x$$ represents the decision variables, $$f(x)$$ is the objective
function, $$w$$ is the weight vector, and $$z^*$$ is the ideal objective vector.  Each subproblem uses a different set
of weights, such that solutions with smaller values for $$g(x)$$ are selected for survival.

## What is Generalized Decomposition?

One limitation of such scalarizing functions is the weights do not directly correspond to locations on the Pareto
front.  In practice, using uniformly-distributed weights does not typically produce a uniformly-distributed Pareto
front, instead resulting in some regions with a higher concentration of points and others lacking coverage.

Generalized decomposition (GD) is a method to overcome this limitation.  We start with a set of target points that
define the ideal distribution of points on the Pareto front, then solve the inverse of the Chebychev scalarizing
function to find the weights corresponding to those points.  Without loss of generality, let's assume that
$$f(x) \geq 0$$ and set $$z^* = 0$$.  Then, we find the weights, $$w$$, by solving:

$$
\begin{align}
\text{Minimize } &\lVert w * f(x) \rVert_\infty \\\
\text{Subject to } &\text{sum}(w) = 1 \\\
&0 \leq w \leq 1
\end{align}
$$

We can convert $$\lVert w * f(x) \rVert_\infty$$ into linear constraints by introducing the slack variable $$t$$ as
follows:

$$
\begin{align}
\text{Minimize } &t \\\
\text{Subject to } &\text{sum}(w) = 1 \\\
&0 \leq w \leq 1 \\\
&-t \leq w * f(x) \leq t \\\
\end{align}
$$

This formulation is a linear program (LP) which can be solved efficiently using the primal-dual method.  For more
details on the GD procedure, please refer to:

> Giagkiozis, I., R. C. Purshouse, and P. J. Fleming (2013).  "Generalized Decomposition."  Evolutionary Multi-Criterion Optimization, 7th International Conference, pp. 428-442.


## Example

Since GD involves solving a convex optimization problem, we require the Python `cvxopt` library.  Therefore, please
ensure Python 3 and `cvxopt` are installed:

```
pip install cvxopt
```

Next, we generate the weights we will use with MOEA/D.  For comparison, we will generate two sets of weights: (1) the
first using the Normal Boundary Intersection (NBI) method, and (2) the second using Generalized Decomposition.  Note
the GD weights are derived from the NBI weights.

<!-- :code: src=examples/org/moeaframework/examples/generalizedDecomposition/generateWeights.sh lines=3:4 -->

```bash
./cli WeightGenerator --method normalboundary --dimension 3 --divisions 20 > nbi_weights.txt
./cli WeightGenerator --method normalboundary --dimension 3 --divisions 20 --generalized > gd_weights.txt
```

Finally, we can configure MOEA/D to use these weights loaded from the files:

<!-- :code: src=examples/org/moeaframework/examples/generalizedDecomposition/GeneralizedDecompositionExample.java lines=59:64 -->

```java
FixedWeights weights = FixedWeights.load(new File("gd_weights.txt"));

MOEAD algorithm = new MOEAD(problem);
algorithm.setWeightGenerator(weights);
algorithm.setInitialPopulationSize(weights.size());
algorithm.run(10000);
```

Comparing the hypervolume, we see GD outperform NBI weights:

```
Name         Min      Median   Max      IQR (+/-) Count Statistically Similar (a=0.05)
------------ -------- -------- -------- --------- ----- ------------------------------
MOEA/D (GD)  0.409812 0.413551 0.417779 0.002444  50
MOEA/D (NBI) 0.401969 0.405872 0.410604 0.002854  50
```

The difference is also visually striking when plotting the results.  Observe how NBI weights (left) tend to form
clusters near the "steeper" parts of the Pareto front, whereas the GD weights (right) are more uniformly distributed.

<p align="center">
	<img src="imgs/moead-weights.png" />
</p>
