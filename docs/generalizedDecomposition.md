# Generalized Decomposition

Some optimization algorithms, including MOEA/D, use the Chebyshev (aka Tchebycheff) scalarizing function to compute
the utility of solutions:

$$ g(x) = \text{max}_i \left( w_i \left| f_i(x) - z_i^* \right| \right) $$

where $$x$$ is the solution, $$f_i(x)$$ is the i-th objective function, $$w$$ is the weight vector, and $$z_i^*$$ is
the ideal objective vector.  Solutions with smaller values for $$g(x)$$ are selected for survival.

One limitation of using this scalarizing function is that a uniformly-distributed set of weight vectors does not
necessarily produce a uniformly-distributed set of points on the Pareto front.  Generalized decomposition (GD) was
introduced as a method of producing weights for the Chebyshev scalarizing function to overcome this limitation.

## What is Generalized Decomposition?

For a given target point $$x$$, generalized decomposition (GD) finds the weights that minimizes the value of $$g(x)$$.
That is, if we assume that $$f(x) \geq 0$$ and eliminate the $$z_i^*$$ term, GD derives the optimal weight vector by
solving:

$$
\begin{align}
\text{Minimize } &c(w) = \lVert w * f(x) \rVert_\infty \\\
\text{Subject to } &\text{sum}(w) = 1 \\\
&0 \leq w \leq 1
\end{align}
$$

where $$c(w)$$ is the cost function we are minimizing.  We can convert $$\lVert w * f(x) \rVert_\infty$$ into linear
constraints by introducing the slack variable $$t$$ as follows:

$$
\begin{align}
\text{Minimize } &c(w) = t \\\
\text{Subject to } &\text{sum}(w) = 1 \\\
&0 \leq w \leq 1 \\\
&w * f(x) <= t \\\
&w * f(x) >= -t
\end{align}
$$

This formulation is a linear program (LP) which can be solved using LP algorithms.  In practice, we use the
primal-dual interior point method.

For more details on the GD procedure, please refer to:

> Giagkiozis, I., R. C. Purshouse, and P. J. Fleming (2013).  "Generalized Decomposition."  Evolutionary Multi-Criterion Optimization, 7th International Conference, pp. 428-442.

## Prerequisites

Since GD involves solving a convex optimization problem, we require the Python `cvxopt` library.  Therefore, please
ensure Python 3 and `cvxopt` are installed:

```
pip install cvxopt
```

## Example

We begin by generating the set of target points.  Here, we use the Normal Boundary Intersection (NBI) method to produce
a set of well-distributed points.  Note that the `--dimensions` parameter must match the number of objectives:

<!-- bash:examples/org/moeaframework/examples/generalizedDecomposition/generateWeights.sh [5:5] -->

```bash
./cli SequenceGenerator --weights normalboundary --dimension 3 --divisions 12 > nbi_weights.txt
```

Then, for each target point we use GD to generate the corresponding weights:

<!-- bash:examples/org/moeaframework/examples/generalizedDecomposition/generateWeights.sh [6:6] -->

```bash
cat nbi_weights.txt | python3 "${ROOT}/generalizedDecomposition.py" > gd_weights.txt
```

Finally, we can supply these weights to MOEA/D:

<!-- java:examples/org/moeaframework/examples/generalizedDecomposition/GeneralizedDecompositionExample.java [62:67] -->

```java
FixedWeights weights = FixedWeights.load(GD_WEIGHTS);

MOEAD algorithm = new MOEAD(problem);
algorithm.setWeightGenerator(weights);
algorithm.setInitialPopulationSize(weights.size());
algorithm.run(10000);
```

Comparing the resulting hypervolume when using the original NBI weights versus those used by GD, we observe GD often
outperforms.

<!-- output:examples/org/moeaframework/examples/generalizedDecomposition/GeneralizedDecompositionExample.java -->

```
Name         Min      Median   Max      IQR (+/-) Count Statistically Similar (a=0.05)
------------ -------- -------- -------- --------- ----- ------------------------------
MOEA/D (GD)  0.377867 0.387469 0.393323 0.004907  50
MOEA/D (NBI) 0.366972 0.372217 0.378778 0.004183  50
```
