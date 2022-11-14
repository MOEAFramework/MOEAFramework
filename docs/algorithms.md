# List of Algorithms

## Multiobjective Optimizers

### CMA-ES

CMA-ES is a sophisticated covariance matrix adaptation evolution strategy algorithm for real-valued global optimization.  CMA-ES produces offspring by
sampling a distribution formed by a covariance matrix, hence the name, and updating the covariance matrix based on the surviving offspring.  Single and
multi-objective variants exist in the literature and both are supported by the MOEA Framework.  

> Hansen and Kern (2004). Evaluating the cma evolution strategy on multimodal test functions.  In Eighth International Conference on Parallel Problem Solving
> from Nature PPSN VIII, pages 282–291.
> 
> Igel, C., Hansen, N., and Roth, S. (2007). Covariance matrix adaptation for multi-objective optimization. Evolutionary Computation, 15:1–28.

**Supported Types:** Real

**Supported Operators:** None (provides its own variation and mutation)

Parameters           | Default Value | Description
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
\citep{asafuddoula15}.  DBEA also proposes corner-sort as a means to identify exteme points for normalization. For an $M$-objective problem, the number
of reference points is:

$H = {M+divisions-1 \choose divisions}$

To use the two-layer approach also used by NSGA-III, replace the \java{divisions} parameter with \java{divisionsOuter} and \java{divisionsInner}.

Parameters           | Default Value | Description
:------------------- | :------------ | :----------
divisions            | Derived       | The number of divisions

# $\epsilon$-MOEA

$\epsilon$-MOEA is a steady-state MOEA that uses $\epsilon$-dominance archiving to record a diverse set of Pareto optimal solutions \cite{deb03}.  The term steady-state means that the algorithm evolves one solution at a time.  This is in contrast to generational algorithms, which evolve the entire population every iteration.  $\epsilon$-dominance archives are useful since they ensure convergence and diversity throughout search \cite{laumanns02}.  However, the algorithm requires an additional $\epsilon$ parameter which is problem dependent.  The $\epsilon$ parameter controls the granularity or resolution of the solutions in objective space.  Smaller values produce larger, more dense sets while larger values produce smaller sets.  In general, the $\epsilon$ values should be chosen to yield a moderately-sized Pareto approximate set.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
	\hline
	Parameter & Description & Default Value \\
	\hline
	populationSize & The size of the population & $100$ \\
	epsilon & The $\epsilon$ values used by the $\epsilon$-dominance archive, which can either be a single value or a comma-separated array & Problem dependent \\
	\hline
\end{tabularx}

\subsection{$\epsilon$-NSGA-II}

$\epsilon$-NSGA-II combines the generational search of NSGA-II with the guaranteed convergence provided by an $\epsilon$-dominance archive \cite{kollat06}.  It also features randomized restarts to enhance search and find a diverse set of Pareto optimal solutions.  During a random restart, the algorithm empties the current population and fills it with new, randomly-generated solutions.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=7.5cm}XX}
	\hline
	Parameter & Description & Default Values \\
	\hline
	populationSize & The size of the population & $100$ \\
	epsilon & The $\epsilon$ values used by the $\epsilon$-dominance archive, which can either be a single value or a comma-separated array & Problem dependent \\
	injectionRate & Controls the percentage of the population after a restart this is ``injected'', or copied, from the $\epsilon$-dominance archive & $0.25$ \\
	windowSize & Frequency of checking if a randomized restart should be triggered (number of iterations) & $100$ \\
	maxWindowSize & The maximum number of iterations between successive randomized restarts & $100$ \\
	minimumPopulationSize & The smallest possible population size when injecting new solutions after a randomized restart & $100$ \\
	maximumPopulationSize & The largest possible population size when injecting new solutions after a randomized restart & $10000$ \\
	\hline
\end{tabularx}

\subsection{GDE3}

GDE3 is the third version of the generalized differential evolution algorithm \cite{kukkonen05}.  The name differential evolution comes from how the algorithm evolves offspring.  It randomly selects three parents.  Next, it computes the difference (the differential) between two of the parents.  Finally, it offsets the remaining parent by this differential.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Values \\
  \hline
  populationSize & The size of the population & $100$ \\
  de.crossoverRate & The crossover rate for differential evolution & $0.1$ \\
  de.stepSize & Control the size of each step taken by differential evolution & $0.5$ \\
  \hline
\end{tabularx}

\subsection{IBEA}

IBEA is a indicator-based MOEA that uses the hypervolume performance indicator as a means to rank solutions \cite{zitzler04}.  Indicator-based algorithms are based on the idea that a performance indicator, such as hypervolume or additive $\epsilon$-indicator, highlight solutions with desirable qualities.  The primary disadvantage of indicator-based methods is that the calculation of the performance indicator can become computationally expensive, particularly as the number of objectives increases.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
  indicator & The indicator function (e.g., \java{"hypervolume"}, \java{"epsilon"}) & \java{"hypervolume"} \\
  \hline
\end{tabularx}

\subsection{MOEA/D}

MOEA/D is a relatively new optimization algorithm based on the concept of decomposing the problem into many single-objective formulations .  Several version of MOEA/D exist in the literature.  The most common variant seen in the literature, MOEA/D-DE \citep{li09}, is the default implementation in the MOEA Framework.

An extension to MOEA/D-DE variant called MOEA/D-DRA introduced a utility function that aimed to reduce the amount of ``wasted'' effort by the algorithm \citep{zhang09}.  This variant is enabled by setting the \plaintext{updateUtility} parameter to a non-zero value.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
  de.crossoverRate & The crossover rate for differential evolution & $0.1$ \\
  de.stepSize & Control the size of each step taken by differential evolution & $0.5$ \\
  pm.rate & The mutation rate for polynomial mutation & $1/N$ \\
  pm.distributionIndex & The distribution index for polynomial mutation & $20.0$ \\
  neighborhoodSize & The size of the neighborhood used for mating, given as a percentage of the population size & $0.1$ \\
  delta & The probability of mating with an individual from the neighborhood versus the entire population & $0.9$ \\
  eta & The maximum number of spots in the population that an offspring can replace, given as a percentage of the population size & $0.01$ \\
  updateUtility & The frequency, in generations, at which utility values are updated.  If set, this uses the MOEA/D-DRA variant; if unset, then then MOEA/D-DE variant is used & Unset \\
  \hline
\end{tabularx}

\subsection{MSOPS}

MSOPS is the Multiple Single-Objective Pareto Search algorithm \citep{hughes03}.  MSOPS works by enumerating $k$ reference vectors and applying a rank ordering based on two aggregate functions: weighted min-max and vector angle distance scaling (VADS).  Solutions with higher rankings with respect to both metrics are preferred.  MSOPS only supports real-valued solutions using differential evolution.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
	numberOfWeights & The number of weight vectors & $50$ \\
  de.crossoverRate & The crossover rate for differential evolution & $0.1$ \\
  de.stepSize & Control the size of each step taken by differential evolution & $0.5$ \\
  \hline
\end{tabularx}

\subsection{NSGA-II}

NSGA-II is one of the first and most widely used MOEAs \citep{deb00}.  It enhanced it predecessor, NSGA, by introducing fast non-dominated sorting and using the more computationally efficient crowding distance metric during survival selection.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
	withReplacement & Uses binary tournament selection with (\texttt{true}) or without (\texttt{false}) replacement & \texttt{true} \\
  \hline
\end{tabularx}

\subsection{NSGA-III}

NSGA-III is the many-objective successor to NSGA-II, using reference points to direct solutions towards a diverse set \citep{deb14}.  The number of reference points is controlled by the number of objectives and the \java{divisions} parameter.  For an $M$-objective problem, the number of reference points is:
\begin{equation}
  H = {M+divisions-1 \choose divisions}
\end{equation}
The authors also propose a two-layer approach for divisions for many-objective problems where an outer and inner division number is specified.  To use the two-layer approach, replace the \java{divisions} parameter with \java{divisionsOuter} and \java{divisionsInner}.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population.  If unset, the population size is equal to the number of reference points & Unset \\
  divisions & The number of divisions & Problem dependent \\
  \hline
\end{tabularx}

\subsection{OMOPSO}

OMOPSO is a multiobjective particle swarm optimization algorithm that includes an $\epsilon$-dominance archive to discover a diverse set of Pareto optimal solutions \citep{sierra05}.  This implementation of OMOPSO differs slightly from the original author's implementation in JMetal due to a discrepancy between the author's code and the paper.  The paper returns the $\epsilon$-dominance archive while the code returns the leaders.  This discrepancy causes a small difference in performance. 
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=7.5cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
  archiveSize & The size of the archive & $100$ \\
  maxEvaluations & The maximum number of evaluations for adapting non-uniform mutation & $25000$ \\
  mutationProbability & The mutation probability for uniform and non-uniform mutation & $1/N$ \\
  perturbationIndex & Controls the shape of the distribution for uniform and non-uniform mutation & $0.5$ \\
  epsilon & The $\epsilon$ values used by the $\epsilon$-dominance archive & Problem dependent \\
  \hline
\end{tabularx}

\subsection{PAES}

PAES is a multiobjective version of evolution strategy \citep{knowles99}.  PAES tends to underperform when compared to other MOEAs, but it is often used as a baseline algorithm for comparisons.  Like PESA-II, PAES uses the adaptive grid archive to maintain a fixed-size archive of solutions.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  archiveSize & The size of the archive & $100$ \\
  bisections & The number of bisections in the adaptive grid archive & $8$ \\
  pm.rate & The mutation rate for polynomial mutation & $1/N$ \\
  pm.distributionIndex & The distribution index for polynomial mutation & $20.0$ \\
  \hline
\end{tabularx}

\subsection{PESA-II}

PESA-II is another multiobjective evolutionary algorithm that tends to underperform other MOEAs but is often used as a baseline algorithm in comparative studies \citep{corne01}.  It is the successor to PESA \citep{corne00}.  Like PAES, PESA-II uses the adaptive grid archive to maintain a fixed-size archive of solutions.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $10$ \\
  archiveSize & The size of the archive & $100$ \\
  bisections & The number of bisections in the adaptive grid archive & $8$ \\
  \hline
\end{tabularx}

\subsection{Random}

The random search algorithm simply randomly generates new solutions uniformly throughout the search space.  It is not intended as an ``optimization algorithm'' \emph{per se}, but as a way to compare the performance of other MOEAs against random search.  If an optimization algorithm can not beat random search, then continued use of that optimization algorithm should be questioned.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & This parameter only has a use when parallelizing evaluations; it controls the number of solutions that are generated and evaluated in parallel & $100$ \\
  epsilon & The $\epsilon$ values used by the $\epsilon$-dominance archive, which can either be a single value or a comma-separated array (this parameter is optional) & Unset \\
  \hline
\end{tabularx}

\subsection{RSO}

The repeated single objectives (RSO) algorithm solves multiobjective problems by running several single-objective optimizers independently with varying weights \citep{hughes05}.  Any of the single-objective optimizers supported by the MOEA Framework can be utilized, and any properties supported by that optimizer can be defined.  RSO is a useful tool for comparing single and multiobjective optimizers.  The maximum number of evaluations is spread evenly across each single-objective instance.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  algorithm & The single-objective optimizer & \java{"GA"} \\
  method & The scalarizing method & \java{"min-max"} \\
	instances & The number of single-objective optimizers & $100$ \\
  \hline
\end{tabularx}

\subsection{RVEA}

The reference vector guided evolutionary algorithm (RVEA) has many similarities with NSGA-III, but avoids use of Pareto dominance and uses an angle-penalized distance function for survival selection \citep{cheng16}.  RVEA only works on problems with at least two objectives and can only use genetic operators requiring two parents.  Like NSGA-III, the number of reference vectors is controlled by the number of objectives and the \java{divisions} parameter.  For an $M$-objective problem, the number of reference vectors is:
\begin{equation}
  H = {M+divisions-1 \choose divisions}
\end{equation}
To use the two-layer approach, replace the \java{divisions} parameter with \java{divisionsOuter} and \java{divisionsInner}.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=8cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population.  If unset, the population size is equal to the number of reference vectors & Unset \\
  divisions & The number of divisions & Problem dependent \\
	alpha & Controls the rate of change in the angle-penalized distance function & 2 \\
	adaptFrequency & The frequency (in generations) in which the weights are adapted / scaled & \texttt{maxEvaluations / (populationSize * 10)} \\
  \hline
\end{tabularx}

\subsection{SMPSO}

SMPSO is a multiobjective particle swarm optimization algorithm \citep{nebro09}.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
  archiveSize & The size of the archive & $100$ \\
  pm.rate & The mutation rate for polynomial mutation & $1/N$ \\
  pm.distributionIndex & The distribution index for polynomial mutation & $20.0$ \\
  \hline
\end{tabularx}

\subsection{SMS-EMOA}

SMS-EMOA is an indicator-based MOEA that uses the volume of the dominated hypervolume to rank individuals \citep{beume07}.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
  offset & The reference point offset for computing hypervolume & $100$ \\
  \hline
\end{tabularx}

\subsection{SPEA2}

SPEA2 is an older but popular benchmark MOEA that uses the so-called ``strength-based'' method for ranking solutions \citep{zitzler02}.  The general idea is that the strength or quality of a solution is related to the strength of solutions it dominates.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
  offspringSize & The number of offspring generated every iteration & $100$ \\
  k & Crowding is based on the distance to the $k$-th nearest neighbor & $1$ \\
  \hline
\end{tabularx}

\subsection{VEGA}

VEGA is considered the earliest documented MOEA.  While we provide support for VEGA, other MOEAs should be preferred as they exhibit better performance.  VEGA is provided for its historical significance \citep{schaffer85}.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
  \hline
\end{tabularx}

\section{Single-Objective Optimizers}

In addition to the multiobjective optimizers listed above, the MOEA Framework supports several single-objective optimizers.  These single-objective optimizers can be used to solve both single and multiobjective problems.  For multiobjective problems, additional weighting properties are provided.

All single objective algorithms support the \java{"weights"} and \java{"method"} properties.  Both are optional.  If not weights are given, the default is equal weights.  \java{"method"} can either be \java{"linear"} or \java{"min-max"}.

\subsection{GA}

GA is the standard genetic algorithm with elitism \citep{holland75}.  A single elite individual is guaranteed to survive between generations.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
	method & The scalarization method & \java{"linear"} \\
	weights & The scalarization weights & Equal weights \\
  \hline
\end{tabularx}

\subsection{ES}

ES is the standard $(1+1)$ evolution strategies algorithm \citep{rechenberg71}.  ES only supports real-valued variables.  This means the population is size $1$ and only $1$ offspring is generated each iteration.  The fittest solution survives to the next iteration.  Additionally, ES uses a self-adaptive variation operator.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
	method & The scalarization method & \java{"linear"} \\
	weights & The scalarization weights & Equal weights \\
  \hline
\end{tabularx}

\subsection{DE}

DE is the standard differential evolution algorithm \citep{storn97}.  DE only supports real-valued variables using the differential evolution operator.  DE works by calculating the difference between two randomly-selected points and applying that difference to a third point.
\newline
\newline
\noindent
\begin{tabularx}{\linewidth}{l>{\hsize=9cm}XX}
  \hline
  Parameter & Description & Default Value \\
  \hline
  populationSize & The size of the population & $100$ \\
	method & The scalarization method & \java{"linear"} \\
	weights & The scalarization weights & Equal weights \\
	de.crossoverRate & The DE crossover rate & See documentation \\
	de.stepSize & The DE step size & See documentation \\
  \hline
\end{tabularx}
