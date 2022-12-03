# List of Algorithms

## Contents

Below lists the optimization algorithms provided by the MOEA Framework organized into categories.

* Multi-objective
  * Classical - [NSGA-II](#nsga-ii), [MOEA/D](#moead), [GDE3](#gde3), [PAES](#paes), [PESA2](#pesa2), [SPEA2](#spea2), [VEGA](#vega)
  * Epsilon-dominance - [e-MOEA](#e-moea), [e-NSGA-II](#e-nsga-ii)
  * Reference point / vector - [NSGA-III](#nsga-iii), [DBEA](#dbea), [RVEA](#rvea)
  * Particle swarm - [OMOPSO](#omopso), [SMPSO](#smpso)
  * Indicator based - [IBEA](#ibea), [SMS-EMOA](#sms-emoa)
  * Other - [CMA-ES](#cma-es), [MSOPS](#msops), [RSO](#rso), [Random](#random)
* Single-objective - [GA](#ga), [ES](#es), [DE](#de), [CMA-ES](#cma-es)

## Instantiating an Algorithm

Each algorithm has a collection of parameters (called properties) used to configure specific details, such as their population size, mutation and
crossover rates, etc.  Using the `Executor` class, we can quickly create, configure, and execute an algorithm on a given problem:

```java

NondominatedPopulation results = new Executor()
    .withProblem("UF1")
    .withAlgorithm("NSGA-II")
    .withProperty("populationSize", 250)
    .withMaxEvaluations(10000)
    .run();
```

Many of these algorithms support any decision variable type, and thus the selected mutation and/or crossover operators will depend on the problem
type.  A default operators is used unless explicitly overridden by the `operator` parameter, where supported.  See the [List of Operators](operators.md)
for specifics.

## Multiobjective Optimizers

### CMA-ES

CMA-ES is a sophisticated covariance matrix adaptation evolution strategy algorithm for real-valued global optimization[^hansen04][^igel07].  CMA-ES produces 
offspring by sampling a distribution formed by a covariance matrix, hence the name, and updating the covariance matrix based on the surviving offspring.
Single and multi-objective variants exist in the literature and both are supported by the MOEA Framework.  

**Algorithm Name:** `"CMA-ES"`  
**Supported Types:** Real  
**Supported Operators:** None (provides its own variation and mutation)

Parameter            | Default Value | Description
:------------------- | :------------ | :----------
`lambda`             | `100`         | The offspring population size
`cc`                 | Derived       | The cumulation parameter
`cs`                 | Derived       | The step size of the cumulation parameter
`damps`              | Derived       | The damping factor for the step size
`ccov`               | Derived       | The learning rate
`ccovsep`            | Derived       | The learning rate when in diagonal-only mode
`sigma`              | `0.5`         | The initial standard deviation
`diagonalIterations` | `0`           | The number of iterations in which only the covariance diagonal is used
`indicator`          | `crowding`    | The fitness indicator - `hypervolume`, `epsilon`, or `crowding`
`initialSearchPoint` | Unset         | Initial guess at the starting location (comma-separated values).  If unset, a random initial guess is used

Derived means the default values are calculated from other settings, but can be overridden if explicitly set.  See Igel., C et al. for details.

### DBEA

DBEA, or I-DBEA, is the Improved Decomposition-Based Evolutionary Algorithm.  DBEA uses the same systematic sampling of reference points as NSGA-III,
but utilizes distance along each reference vector to measure convergence and the perpendicular distance to reference vectors to measure diversity
[^asafuddoula15].  DBEA also proposes corner-sort as a means to identify exteme points for normalization.

**Algorithm Name:** `"DBEA"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator
`divisions`          | Problem dependent | The number of divisions
`divisionsInner`     | Unset             | The number of inner divisions when using the two-layer approach
`divisionsOuter`     | Unset             | The number of outer divisions when using the two-layer approach

See the [NSGA-III](#nsga-iii) documentation for details on generating reference vectors and the two-layer approach.

### e-MOEA
$\epsilon$-MOEA is a steady-state MOEA that uses $\epsilon$-dominance archiving to record a diverse set of Pareto optimal solutions [^deb03].
The term steady-state means that the algorithm evolves one solution at a time.  This is in contrast to generational algorithms, which evolve the
entire population every iteration.  $\epsilon$-dominance archives are useful since they ensure convergence and diversity throughout search [^laumanns02].
However, the algorithm requires an additional $\epsilon$ parameter which is problem dependent.  The $\epsilon$ parameter controls the granularity or
resolution of the solutions in objective space.  Smaller values produce larger, more dense sets while larger values produce smaller sets.  In general,
the $\epsilon$ values should be chosen to yield a moderately-sized Pareto approximate set.

**Algorithm Name:** `"eMOEA"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator
`epsilon`            | Problem dependent | The $\epsilon$ values used by the $\epsilon$-dominance archive, which can either be a single value or a comma-separated array

### e-NSGA-II

$\epsilon$-NSGA-II combines the generational search of NSGA-II with the guaranteed convergence provided by an $\epsilon$-dominance archive [^kollat06].  It also features randomized restarts to enhance search and find a diverse set of Pareto optimal solutions.  During a random restart, the algorithm empties the current population and fills it with new, randomly-generated solutions.

**Algorithm Name:** `"e-NSGA-II"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator
`epsilon`            | Problem dependent | The $\epsilon$ values used by the $\epsilon$-dominance archive, which can either be a single value or a comma-separated array
`injectionRate`      | `0.25`            | Controls the percentage of the population after a restart this is "injected", or copied, from the $\epsilon$-dominance archive
`windowSize`         | `100`             | Frequency of checking if a randomized restart should be triggered (number of iterations)
`maxWindowSize`      | `100`             | The maximum number of iterations between successive randomized restarts
`minimumPopulationSize` | `100`          | The smallest possible population size when injecting new solutions after a randomized restart
`maximumPopulationSize` | `10000`        | The largest possible population size when injecting new solutions after a randomized restart

### GDE3

GDE3 is the third version of the generalized differential evolution algorithm [^kukkonen05].  The name differential evolution comes from how the algorithm
evolves offspring.  It randomly selects three parents.  Next, it computes the difference (the differential) between two of the parents.  Finally, it offsets
the remaining parent by this differential.

**Algorithm Name:** `"GDE3"`  
**Supported Types:** Real  
**Supported Operators:** DE

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`de.crossoverRate`   | `0.1`             | The crossover rate for differential evolution
`de.stepSize`        | `0.5`             | Control the size of each step taken by differential evolution

### IBEA

IBEA is a indicator-based MOEA that uses the hypervolume performance indicator as a means to rank solutions [^zitzler04].  Indicator-based algorithms
are based on the idea that a performance indicator, such as hypervolume or additive $\epsilon$-indicator, highlight solutions with desirable qualities.
The primary disadvantage of indicator-based methods is that the calculation of the performance indicator can become computationally expensive, particularly
as the number of objectives increases.

**Algorithm Name:** `"IBEA"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator
`indicator`          | `hypervolume`     | The indicator function - `hypervolume`, `epsilon

### MOEA/D

MOEA/D is a relatively new optimization algorithm based on the concept of decomposing the problem into many single-objective formulations .  Several versions
of MOEA/D exist in the literature.  The most common variant seen in the literature, MOEA/D-DE [^li09], is the default implementation in the MOEA Framework.

An extension to MOEA/D-DE variant called MOEA/D-DRA introduced a utility function that aimed to reduce the amount of "wasted" effort by the algorithm
[^zhang09].  This variant is enabled by setting the `updateUtility` parameter to a non-zero value.

**Algorithm Name:** `"MOEA/D"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator
`de.crossoverRate`   | `0.1`             | The crossover rate for differential evolution
`de.stepSize`        | `0.5`             | Control the size of each step taken by differential evolution
`pm.rate`            | `1/N`             | The mutation rate for polynomial mutation
`pm.distributionIndex` | `20.0`          | The distribution index for polynomial mutation
`neighborhoodSize`   | `0.1`             | The size of the neighborhood used for mating, given as a percentage of the population size
`delta`              | `0.9`             | The probability of mating with an individual from the neighborhood versus the entire population
`eta`                | `0.01`            | The maximum number of spots in the population that an offspring can replace, given as a percentage of the population size
`updateUtility`      | Unset             | The frequency, in generations, at which utility values are updated.  If set, this uses the MOEA/D-DRA variant; if unset, then then MOEA/D-DE variant is used

### MSOPS

MSOPS is the Multiple Single-Objective Pareto Search algorithm [^hughes03].  MSOPS works by enumerating $k$ reference vectors and applying a rank ordering
based on two aggregate functions: weighted min-max and vector angle distance scaling (VADS).  Solutions with higher rankings with respect to both metrics are
preferred.  MSOPS only supports real-valued solutions using differential evolution.

**Algorithm Name:** `"MSOPS"`  
**Supported Types:** Real  
**Supported Operators:** DE

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`numberOfWeights`    | `50`              | The number of weight vectors
`de.crossoverRate`   | `0.1`             | The crossover rate for differential evolution
`de.stepSize`        | `0.5`             | Control the size of each step taken by differential evolution

### NSGA-II

NSGA-II is one of the first and most widely used MOEAs [^deb00].  It enhanced it predecessor, NSGA, by introducing fast non-dominated sorting and using
the more computationally efficient crowding distance metric during survival selection.

**Algorithm Name:** `"NSGA-II"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator
`withReplacement`    | `true`            | Uses binary tournament selection with (`true`) or without (`false`) replacement

### NSGA-III

NSGA-III is the many-objective successor to NSGA-II, using reference points to direct solutions towards a diverse set [^deb14].  The number of reference
points is controlled by the number of objectives and the `divisions` parameter.  For an $M$-objective problem, the number of reference points is:

$$ H = {M+divisions-1 \choose divisions} $$

The authors also propose a two-layer approach for divisions for many-objective problems where an outer and inner division number is specified.  To use the
two-layer approach, replace the `divisions` parameter with `divisionsOuter` and `divisionsInner`.  `divisionsOuter` controls the number of reference points
on the boundary of the objective space; `divisionsInner` controls the number of reference points on the interior of the objective space.  Please refer
to the cited paper for more details.

**Algorithm Name:** `"NSGA-III"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | Unset             | The size of the population.  If unset, the population size is equal to the number of reference points
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator
`divisions`          | Problem dependent | The number of divisions
`divisionsInner`     | Unset             | The number of inner divisions when using the two-layer approach
`divisionsOuter`     | Unset             | The number of outer divisions when using the two-layer approach

### OMOPSO

OMOPSO is a multiobjective particle swarm optimization algorithm that includes an $\epsilon$-dominance archive to discover a diverse set of Pareto optimal solutions [^sierra05].  This implementation of OMOPSO differs slightly from the original author's implementation in JMetal due to a discrepancy between the author's code and the paper.  The paper returns the $\epsilon$-dominance archive while the code returns the leaders.  This discrepancy causes a small difference in performance. 

**Algorithm Name:** `"OMOPSO"`  
**Supported Types:** Any  
**Supported Operators:** None (provides its own variation and mutation)

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`archiveSize`        | `100`             | The size of the archive
`maxEvaluations`     | `25000`           | The maximum number of evaluations for adapting non-uniform mutation
`mutationProbability` | `1/N`            | The mutation probability for uniform and non-uniform mutation
`perturbationIndex`  | `0.5`             | Controls the shape of the distribution for uniform and non-uniform mutation
`epsilon`            | Problem dependent | The $\epsilon$ values used by the $\epsilon$-dominance archive

### PAES

PAES is a multiobjective version of evolution strategy [^knowles99].  PAES tends to underperform when compared to other MOEAs, but it is often used as a baseline algorithm for comparisons.  Like PESA-II, PAES uses the adaptive grid archive to maintain a fixed-size archive of solutions.

**Algorithm Name:** `"PAES"`  
**Supported Types:** Any  
**Supported Operators:** Any *mutation* operator

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`archiveSize`        | `100`             | The size of the archive
`bisections`         | `8`               | The number of bisections in the adaptive grid archive

### PESA2

PESA2 is another multiobjective evolutionary algorithm that tends to underperform other MOEAs but is often used as a baseline algorithm in comparative studies [^corne01].  It is the successor to PESA [^corne00].  Like PAES, PESA2 uses the adaptive grid archive to maintain a fixed-size archive of solutions.

**Algorithm Name:** `"PESA2"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `10`              | The size of the population
`archiveSize`        | `100`             | The size of the archive
`bisections`         | `8`               | The number of bisections in the adaptive grid archive
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator

### Random

The random search algorithm simply randomly generates new solutions uniformly throughout the search space.  It is not intended as an "optimization algorithm" *per se*, but as a way to compare the performance of other MOEAs against random search.  If an optimization algorithm can not beat random search, then continued use of that optimization algorithm should be questioned.

**Algorithm Name:** `"Random"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | This parameter only has a use when parallelizing evaluations; it controls the number of solutions that are generated and evaluated in parallel
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator
`epsilon`            | Unset             | The $\epsilon$ values used by the $\epsilon$-dominance archive, which can either be a single value or a comma-separated array (this parameter is optional)

### RSO

The repeated single objectives (RSO) algorithm solves multiobjective problems by running several single-objective optimizers independently with varying weights [^hughes05].  Any of the single-objective optimizers supported by the MOEA Framework can be utilized, and any properties supported by that optimizer can be defined.  RSO is a useful tool for comparing single and multiobjective optimizers.  The maximum number of evaluations is spread evenly across each single-objective instance.

**Algorithm Name:** `"RSO"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`algorithm`          | `GA`              | The single-objective optimizer - `GA`, `ES`, `DE`
`method`             | `min-max`         | The scalarizing method - `min-max`, `linear`
`instances`          | `100`             | The number of single-objective optimizers

### RVEA

The reference vector guided evolutionary algorithm (RVEA) has many similarities with NSGA-III, but avoids use of Pareto dominance and uses an angle-penalized distance function for survival selection [^cheng16].  RVEA only works on problems with at least two objectives and can only use genetic operators requiring two parents.

**Algorithm Name:** `"RVEA"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | Unset             | The size of the population.  If unset, the population size is equal to the number of reference vectors
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator
`divisions`          | Problem dependent | The number of divisions
`divisionsInner`     | Unset             | The number of inner divisions when using the two-layer approach
`divisionsOuter`     | Unset             | The number of outer divisions when using the two-layer approach
`alpha`              | `2`               | Controls the rate of change in the angle-penalized distance function
`adaptFrequency`     | $\texttt{maxEvaluations / (populationSize * 10)}$ | The frequency (in generations) in which the weights are adapted / scaled

See the [NSGA-III](#nsga-iii) documentation for details on generating reference vectors and the two-layer approach.

### SMPSO

SMPSO is a multiobjective particle swarm optimization algorithm [^nebro09].

**Algorithm Name:** `"SMPSO"`  
**Supported Types:** Real  
**Supported Operators:** PM

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`archiveSize`        | `100`             | The size of the archive
`pm.rate`            | `1/N`             | The mutation rate for polynomial mutation
`pm.distributionIndex` | `20.0`          | The distribution index for polynomial mutation

### SMS-EMOA

SMS-EMOA is an indicator-based MOEA that uses the volume of the dominated hypervolume to rank individuals [^beume07].

**Algorithm Name:** `"SMS-EMOA"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`offset`             | `100`             | The reference point offset for computing hypervolume
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator

### SPEA2

SPEA2 is an older but popular benchmark MOEA that uses the so-called ``strength-based'' method for ranking solutions [^zitzler02].  The general idea is that the strength or quality of a solution is related to the strength of solutions it dominates.

**Algorithm Name:** `"SPEA2"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`offspringSize`      | `100`             | The number of offspring generated every iteration
`k`                  | `1`               | Crowding is based on the distance to the $k$-th nearest neighbor
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator

### VEGA

VEGA is considered the earliest documented MOEA.  While we provide support for VEGA, other MOEAs should be preferred as they exhibit better performance.  VEGA is provided for its historical significance [^schaffer85].

**Algorithm Name:** `"VEGA"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator

## Single-Objective Optimizers

In addition to the multiobjective optimizers listed above, the MOEA Framework supports several single-objective optimizers.  These single-objective optimizers can be used to solve both single and multiobjective problems.  For multiobjective problems, additional properties are supported to scalarize the objectives into a single value.

### GA

GA is the standard genetic algorithm with elitism[^holland75].  A single elite individual is guaranteed to survive between generations.

**Algorithm Name:** `"GA"`  
**Supported Types:** Any  
**Supported Operators:** Any

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`operator`           | Problem dependent | The variation (crossover and/or mutation) operator
`method`             | `linear`          | The scalarization method - `linear`, `min-max`
`weights`            | `1.0,1.0,...`     | The scalarization weights

### ES

ES is the standard $(1+1)$ evolution strategies algorithm[^rechenberg71].  ES only supports real-valued variables.  This means the population is size `1` and only `1` offspring is generated each iteration.  The fittest solution survives to the next iteration.  Additionally, ES uses a self-adaptive variation operator.

**Algorithm Name:** `"ES"`  
**Supported Types:** Real  
**Supported Operators:** None (uses self-adaptive variation)

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`method`             | `linear`          | The scalarization method - `linear`, `min-max`
`weights`            | `1.0,1.0,...`     | The scalarization weights

### DE

DE is the standard differential evolution algorithm[^storn97], also known as `DE/rand/1/bin`.  DE only supports real-valued variables using the differential evolution operator.  DE works by calculating the difference between two randomly-selected points and applying that difference to a third point.

**Algorithm Name:** `"DE"`  
**Supported Types:** Real  
**Supported Operators:** DE

Parameter            | Default Value     | Description
:------------------- | :---------------- | :----------
`populationSize`     | `100`             | The size of the population
`method`             | `linear`          | The scalarization method - `linear`, `min-max`
`weights`            | `1.0,1.0,...`     | The scalarization weights
`de.crossoverRate`   | `0.1`             | The crossover rate for differential evolution
`de.stepSize`        | `0.5`             | Control the size of each step taken by differential evolution

## References

[^asafuddoula15]: Asafuddoula, M., Ray, T., and Sarker, R. (2015). A decomposition-based evolutionary algorithm for many-objective optimization. IEEE Transactions on Evolutionary Computation, 19:445–460.
[^beume07]: Beume, N., Naujoks, B., and Emmerich, M. (2007). Sms-emoa: Multiobjective selection based on dominated hypervolume. European Journal of Operational Research, 181(3):1653–1669.
[^cheng16]: Cheng, R., Jin, Y., Olhofer, M., and Sendhoff, B. (2016). A reference vector guided evolutionary algorithm for many-objective optimization. IEEE Transactions on Evolutionary Computation, 99.
[^corne00]: Corne, D. W. and Knowles, J. D. (2000). The Pareto envelope-based selection algorithm for multiobjective optimization. In Proceedings of the 6th International Conference on Parallel Problem Solving from Nature (PPSN VI), pages 839–848, Paris, France.
[^corne01]: Corne, D. W., Jerram, N. R., Knowles, J. D., and Oates, M. J. (2001). PESA-II: Region based selection in evolutionary multiobjective optimization. In Proceedings of the Genetic and Evolutionary Computation Conference (GECCO 2001), pages 283–290, San Francisco, CA.
[^deb00]: Deb, K., Pratap, A., Agarwal, S., and Meyarivan, T. (2000). A fast elitist multi-objective genetic algorithm: NSGA-II. IEEE Transactions on Evolutionary Computation, 6(2):182–197.
[^deb03]: Deb, K., Mohan, M., and Mishra, S. (2003). A fast multi-objective evolutionary algorithm for finding well-spread Pareto-optimal solutions. KanGAL Report No. 2003002, Kanpur Genetic Algorithms Laboratory (KanGAL), Indian Institute of Technology, Kanpur, India.
[^deb14]: Deb, K. and Jain, H. (2014). An evolutionary many-objective optimization algorithm using reference-point-based nondominated sorting approach, part i: Solving problems with box constraints. IEEE Transactions on Evolutionary Computation, 18(4):577–601.
[^hansen04]: Hansen and Kern (2004). Evaluating the cma evolution strategy on multimodal test functions.  In Eighth International Conference on Parallel Problem Solving from Nature PPSN VIII, pages 282–291.
[^holland75]: Holland, J. H. (1975). Adaptation in Natural and Artificial Systems. University of Michigan Press, Ann Arbor, MI.
[^hughes03]: Hughes, E. J. (2003). Multiple single objective pareto sampling. In Congress on Evolutionary Computation, pages 2678–2684.
[^hughes05]: Hughes, E. J. (2005). Evolutionary many-objective optimisation: Many once or one many? In The 2005 IEEE Congress on Evolutionary Computation (CEC 2005), pages 222–227, Edinburgh, UK.
[^igel07]: Igel, C., Hansen, N., and Roth, S. (2007). Covariance matrix adaptation for multi-objective optimization. Evolutionary Computation, 15:1–28.
[^knowles99]: Knowles, J. D. and Corne, D. W. (1999). Approximating the nondominated front using the Pareto Archived Evolution Strategy. Evolutionary Computation, 8:149–172.
[^kollat06]: Kollat, J. B. and Reed, P. M. (2006). Comparison of multi-objective evolutionary algorithms for long-term monitoring design. Advances in Water Resources, 29(6):792–807.
[^kukkonen05]: Kukkonen, S. and Lampinen, J. (2005). GDE3: The third evolution step of generalized differential evolution. In The 2005 IEEE Congress on Evolutionary Computation (CEC 2005), pages 443–450, Guanajuato, Mexico.
[^laumanns02]: Laumanns, M., Thiele, L., Deb, K., and Zitzler, E. (2002). Combining convergence and diversity in evolutionary multi-objective optimization. Evolutionary Computation, 10(3):263–282.
[^li09]: Li, H. and Zhang, Q. (2009). Multiobjective optimization problems with complicated Pareto sets, MOEA/D and NSGA-II. IEEE Transactions on Evolutionary Computation, 13(2):284–302.
[^nebro09]: Nebro, A. J., Durillo, J. J., Garc´ıa-Nieto, J., Coello Coello, C. A., Luna, F., and Alba, E. (2009). SMPSO: A new PSO-based metaheuristic for multi-objective optimization. In IEEE Symposium on Computational Intelligence in Multicriteria Decision-Making
(MCDM 2009), pages 66–73, Nashville, TN.
[^rechenberg71]: Rechenberg, I. (1971). Evolutionsstrategie: Optimierung technischer Systeme nach Prinzipiender biologischen Evolution. PhD thesis, Fromman-Holzboog.
[^schaffer85]: Schaffer, D. J. (1985). Multiple objective optimization with vector evaluated genetic algorithms. In 1st International Conference on Genetic Algorithms, pages 93–100.
[^sierra05]: Sierra, M. R. and Coello Coello, C. A. (2005). Improving PSO-based multi-objective optimization using crowding, mutation and ϵ-dominance. In Evolutionary Multi-Criterion Optimization (EMO 2005), pages 505–519, Guanajuato, Mexico.
[^storn97]: Storn, R. and Price, K. (1997). Differential evolution — a simple and efficient heuristic for global optimization over continuous spaces. Journal of Global Optimization, 11(4):341–359.
[^zhang09]: Zhang, Q., Liu, W., and Li, H. (2009). The performance of a new version of MOEA/D on CEC09 unconstrained MOP test instances. In Congress on Evolutionary Computation (CEC 2009), pages 203–208, Trondheim, Norway.
[^zitzler02]: Zitzler, E., Laumanns, M., and Thiele, L. (2002a). SPEA2: Improving the Strength Pareto Evolutionary Algorithm For Multiobjective Optimization. International Center for Numerical Methods in Engineering (CIMNE), Barcelona, Spain.
[^zitzler04]: Zitzler, E. and K¨unzli, S. (2004). Indicator-based selection in multiobjective search. In Parallel Problem Solving from Nature (PPSN VIII), pages 832–842, Birmingham, UK.
