/* Copyright 2009-2025 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.algorithm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.FitnessComparator;
import org.moeaframework.core.comparator.NondominatedSortingComparator;
import org.moeaframework.core.comparator.ObjectiveComparator;
import org.moeaframework.core.comparator.RankComparator;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.fitness.AdditiveEpsilonIndicatorFitnessEvaluator;
import org.moeaframework.core.fitness.HypervolumeFitnessEvaluator;
import org.moeaframework.core.fitness.IndicatorFitnessEvaluator;
import org.moeaframework.core.population.FastNondominatedSorting;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.validate.Validate;

/**
 * The Covariance Matrix Adaption Evolution Strategy (CMA-ES) algorithm for single and multi-objective problems.
 * For multi-objective problems, individuals are compared using Pareto ranking and crowding distance to break
 * ties.  An optional {@code fitnessEvaluator} parameter can be specified to replace the crowding distance calculation
 * with, for example, the hypervolume indicator.
 * <p>
 * This file is based on the Java implementation of CMA-ES by Nikolaus Hansen available at
 * {@literal https://www.lri.fr/~hansen/cmaes_inmatlab.html#java}, originally licensed under the GNU LGPLv3.
 * <p>
 * References:
 * <ol>
 *   <li>Hansen and Kern (2004).  Evaluating the CMA Evolution Strategy on Multimodal Test Functions.  In Proceedings
 *       of the Eighth International Conference on Parallel Problem Solving from Nature PPSN VIII, pp. 282-291,
 *       Berlin: Springer.
 *   <li>Hansen, N. (2011).  The CMA Evolution Strategy: A Tutorial.  Available at
 *       https://www.lri.fr/~hansen/cmatutorial.pdf.
 *   <li>Igel, C., N. Hansen, and S. Roth (2007).  Covariance Matrix Adaptation for Multi-objective Optimization.
 *       Evolutionary Computation, 15(1):1-28.
 * </ol>
 */
public class CMAES extends AbstractAlgorithm implements Configurable {
	
	/**
	 * An initial search point to start searching from, or {@code null} if no initial search point is specified.
	 */
	private final double[] initialSearchPoint;
	
	/**
	 * Secondary comparison criteria for comparing population individuals with the same rank.  If {@code null}, the
	 * default crowding distance metric is used.
	 */
	private IndicatorFitnessEvaluator fitnessEvaluator;
	
	/**
	 * Nondominated archive of the best solutions found.
	 */
	private NondominatedPopulation archive;

	/**
	 * The number of iterations already performed.
	 */
	private int iteration;

	/**
	 * The number of iterations in which only the covariance diagonal is used.  This enhancement helps speed up the
	 * algorithm when there are many decision variables.  Set to {@code 0} to always use the full covariance matrix.
	 */
	private int diagonalIterations;

	/**
	 * Number of offspring generated each iteration.
	 */
	private int lambda;

	/**
	 * Number of offspring selected for recombination.
	 */
	private int mu;

	/**
	 * Overall standard deviation.
	 */
	private double sigma;

	/**
	 * Variance-effectiveness.
	 */
	private double mueff;

	/**
	 * Learning rate.
	 */
	private double ccov;

	/**
	 * Learning rate when diagonal mode is active.
	 */
	private double ccovsep;

	/**
	 * Expectation of ||N(0, I)||.
	 */
	private double chiN;

	/**
	 * Step size cumulation parameter.
	 */
	private double cs;

	/**
	 * Cumulation parameter.
	 */
	private double cc;

	/**
	 * Damping for step size.
	 */
	private double damps;

	/**
	 * Weights for recombination.
	 */
	private double[] weights;

	/**
	 * Scaling factors.
	 */
	private double[] diagD;

	/**
	 * Current centroid of the distribution.
	 */
	private double[] xmean;

	/**
	 * Evolution path.
	 */
	private double[] pc;

	/**
	 * Evolution path for sigma.
	 */
	private double[] ps;

	/**
	 * Coordinate system.
	 */
	private double[][] B;

	/**
	 * Current covariance matrix.
	 */
	private double[][] C;
	
	/**
	 * The current population.
	 */
	private Population population;

	/**
	 * Last iteration were the eigenvalue decomposition was calculated.
	 */
	private int lastEigenupdate;
	
	/**
	 * If {@code true}, perform consistency checks to ensure CMA-ES remains numerically stable.
	 */
	private boolean checkConsistency;
	
	/**
	 * Constructs a new CMA-ES intance using default parameters.
	 * 
	 * @param problem the problem to optimize
	 */
	public CMAES(Problem problem) {
		this(problem, Settings.DEFAULT_POPULATION_SIZE, null, new NondominatedPopulation());
	}
	
	/**
	 * Constructs a new CMA-ES instance using default parameters.
	 *
	 * @param problem the problem to optimize
	 * @param lambda the offspring population size
	 * @param fitnessEvaluator secondary comparison criteria for comparing population individuals with the same rank,
	 *        or {@code null} to use the default crowding distance metric
	 * @param archive the nondominated archive for storing the elite individuals
	 */
	public CMAES(Problem problem, int lambda, IndicatorFitnessEvaluator fitnessEvaluator,
			NondominatedPopulation archive) {
		this(problem, lambda, fitnessEvaluator, archive, null, Settings.isCMAESConsistencyCheckingEnabled(),
				-1, -1, -1, -1, -1, -1, -1);
	}

	/**
	 * Constructs a new CMA-ES instance with the given parameters.
	 * <p>
	 * If the parameters {@code cc}, {@code cs}, {@code damps}, {@code ccov}, {@code ccovsep}, {@code sigma}, and
	 * {@code diagonalIterations} are set to any negative number, then the default parameter will be used.
	 * 
	 * @param problem the problem to optimize
	 * @param lambda the offspring population size
	 * @param fitnessEvaluator secondary comparison criteria for comparing population individuals with the same rank,
	 *        or {@code null} to use the default crowding distance metric
	 * @param archive the nondominated archive for storing the elite individuals
	 * @param initialSearchPoint an initial search point, or {@code null} if no initial search point is specified
	 * @param checkConsistency if {@code true}, performs checks to ensure CMA-ES remains numerically stable
	 * @param cc the cumulation parameter
	 * @param cs the step size of the cumulation parameter
	 * @param damps the damping factor for the step size
	 * @param ccov the learning rate
	 * @param ccovsep the learning rate when in diagonal-only mode
	 * @param sigma the initial standard deviation
	 * @param diagonalIterations the number of iterations in which only the covariance diagonal is used
	 */
	public CMAES(Problem problem, int lambda, IndicatorFitnessEvaluator fitnessEvaluator,
			NondominatedPopulation archive, double[] initialSearchPoint, boolean checkConsistency, double cc,
			double cs, double damps, double ccov, double ccovsep, double sigma, int diagonalIterations) {
		super(problem);
		setLambda(lambda);
		setArchive(archive);
		setCc(cc);
		setCs(cs);
		setDamps(damps);
		setCcov(ccov);
		setCcovsep(ccovsep);
		setSigma(sigma);
		setDiagonalIterations(diagonalIterations);
		setFitnessEvaluator(fitnessEvaluator);
		setCheckConsistency(checkConsistency);
		
		Validate.that("problem", problem).isType(RealVariable.class);
		
		this.initialSearchPoint = initialSearchPoint;
		this.population = new Population();
	}
	
	@Override
	public String getName() {
		return "CMA-ES";
	}

	/**
	 * Returns the number of iterations in which only the covariance diagonal is used.
	 * 
	 * @return the number of iterations in which only the covariance diagonal is used
	 */
	public int getDiagonalIterations() {
		return diagonalIterations;
	}

	/**
	 * Sets the number of iterations in which only the covariance diagonal is used.  If set to {@code -1}, a default
	 * value will be provided during initialization.  This property can only be configured before initialization.
	 * 
	 * @param diagonalIterations the number of iterations in which only the covariance diagonal is used
	 */
	@Property
	public void setDiagonalIterations(int diagonalIterations) {
		assertNotInitialized();
		this.diagonalIterations = diagonalIterations;
	}

	/**
	 * Returns the number of offspring generated each iteration.
	 * 
	 * @return the number of offspring generated each iteration
	 */
	public int getLambda() {
		return lambda;
	}

	/**
	 * Sets the number of offspring generated each iteration.  If set to {@code -1}, a default value will be provided
	 * during initialization.  This property can only be configured before initialization.
	 * 
	 * @param lambda the number of offspring generated each iteration
	 */
	@Property
	public void setLambda(int lambda) {
		assertNotInitialized();
		this.lambda = lambda;
	}

	/**
	 * Returns the overall standard deviation.
	 * 
	 * @return the overall standard deviation
	 */
	public double getSigma() {
		return sigma;
	}

	/**
	 * Sets the overall standard deviation.  If set to {@code -1}, a default value will be provided during
	 * initialization.  This property can only be configured before initialization.
	 * 
	 * @param sigma the overall standard deviation
	 */
	@Property
	public void setSigma(double sigma) {
		assertNotInitialized();
		this.sigma = sigma;
	}

	/**
	 * Returns the learning rate.
	 * 
	 * @return the learning rate
	 */
	public double getCcov() {
		return ccov;
	}

	/**
	 * Sets the learning rate.  If set to {@code -1}, a default value will be provided during initialization.  This
	 * property can only be configured before initialization.
	 * 
	 * @param ccov the learning rate
	 */
	@Property
	public void setCcov(double ccov) {
		assertNotInitialized();
		this.ccov = ccov;
	}

	/**
	 * Returns the learning rate when diagonal mode is active.
	 * 
	 * @return the learning rate when diagonal mode is active
	 */
	public double getCcovsep() {
		return ccovsep;
	}

	/**
	 * Sets the learning rate when diagonal mode is active.  If set to {@code -1}, a default value will be provided
	 * during initialization.  This property can only be configured before initialization.
	 * 
	 * @param ccovsep the learning rate when diagonal mode is active
	 */
	@Property
	public void setCcovsep(double ccovsep) {
		assertNotInitialized();
		this.ccovsep = ccovsep;
	}

	/**
	 * Returns the step size of the cumulation parameter.
	 * 
	 * @return the step size of the cumulation parameter
	 */
	public double getCs() {
		return cs;
	}

	/**
	 * Sets the step size of the cumulation parameter.  If set to {@code -1}, a default value will be provided during
	 * initialization.  This property can only be configured before initialization.
	 * 
	 * @param cs the step size of the cumulation parameter
	 */
	@Property
	public void setCs(double cs) {
		assertNotInitialized();
		this.cs = cs;
	}

	/**
	 * Returns the cumulation parameter.
	 * 
	 * @return the cumulation parameter
	 */
	public double getCc() {
		return cc;
	}

	/**
	 * Sets the cumulation parameter.  If set to {@code -1}, a default value will be provided during initialization.
	 * This property can only be configured before initialization.
	 * 
	 * @param cc the cumulation parameter
	 */
	@Property
	public void setCc(double cc) {
		assertNotInitialized();
		this.cc = cc;
	}

	/**
	 * Returns the damping for step size.
	 * 
	 * @return the damping for step size
	 */
	public double getDamps() {
		return damps;
	}

	/**
	 * Sets the damping for step size.  If set to {@code -1}, a default value will be provided during initialization.
	 * This property can only be configured before initialization.
	 * 
	 * @param damps the damping for step size
	 */
	@Property
	public void setDamps(double damps) {
		assertNotInitialized();
		this.damps = damps;
	}

	/**
	 * Returns the initial search point to start searching from, or {@code null} if no initial search point was
	 * specified.
	 * 
	 * @return the initial search point to start searching from, or {@code null} if no initial search point was
	 *         specified
	 */
	public double[] getInitialSearchPoint() {
		return initialSearchPoint;
	}
	
	/**
	 * Returns the non-dominated archive of the best solutions found.
	 * 
	 * @return the non-dominated archive of the best solutions found
	 */
	public NondominatedPopulation getArchive() {
		return archive;
	}
	
	/**
	 * Sets the non-dominated archive of the best solutions found.  This property can only be configured before
	 * initialization.
	 * 
	 * @param archive the non-dominated archive of the best solutions found
	 */
	public void setArchive(NondominatedPopulation archive) {
		assertNotInitialized();
		this.archive = archive;
	}
	
	/**
	 * Returns the indicator-based fitness evaluator.
	 * 
	 * @return the indicator-based fitness evaluator
	 */
	public IndicatorFitnessEvaluator getFitnessEvaluator() {
		return fitnessEvaluator;
	}
	
	/**
	 * Sets the indicator-based fitness evaluator used as a secondary comparison criteria for comparing population
	 * individuals with the same rank, or {@code null} to use the default crowding distance metric
	 * 
	 * @param fitnessEvaluator the indicator-based fitness evaluator
	 */
	public void setFitnessEvaluator(IndicatorFitnessEvaluator fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}
	
	/**
	 * Returns {@code true} if consistency checks are enabled; {@code false} otherwise.
	 * 
	 * @return {@code true} if consistency checks are enabled; {@code false} otherwise
	 */
	public boolean isCheckConsistency() {
		return checkConsistency;
	}
	
	/**
	 * Enables or disables consistency checks to ensure CMA-ES remains numerically stable.  This property can only be
	 * configured before initialization.
	 * 
	 * @param checkConsistency {@code true} if consistency checks are enabled; {@code false} otherwise
	 */
	@Property
	public void setCheckConsistency(boolean checkConsistency) {
		assertNotInitialized();
		this.checkConsistency = checkConsistency;
	}

	@Override
	public void applyConfiguration(TypedProperties properties) {
		if (properties.contains("indicator")) {
			String indicator = properties.getString("indicator");
			
			if ("hypervolume".equalsIgnoreCase(indicator)) {
				setFitnessEvaluator(new HypervolumeFitnessEvaluator(problem));
			} else if ("epsilon".equalsIgnoreCase(indicator)) {
				setFitnessEvaluator(new AdditiveEpsilonIndicatorFitnessEvaluator(problem));
			} else if ("crowding".equalsIgnoreCase(indicator)) {
				setFitnessEvaluator(null);
			} else {
				throw new ConfigurationException("Invalid indicator: " + indicator);
			}
		}
		
		Configurable.super.applyConfiguration(properties);
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = Configurable.super.getConfiguration();
		
		if (fitnessEvaluator == null) {
			properties.setString("indicator", "crowding");
		} else if (fitnessEvaluator instanceof HypervolumeFitnessEvaluator) {
			properties.setString("indicator", "hypervolume");
		} else if (fitnessEvaluator instanceof AdditiveEpsilonIndicatorFitnessEvaluator) {
			properties.setString("indicator", "epsilon");
		}
		
		return properties;
	}

	/**
	 * Validates parameters prior to calling the {@link #initialize()} method.  Checks include ensuring the initial
	 * search point is valid.
	 * 
	 * @param prototypeSolution an example solution for retrieving variable bounds
	 * @throws IllegalArgumentException if any of the checks fail
	 */
	private void preInitChecks(Solution prototypeSolution) {
		if (initialSearchPoint == null) {
			return;
		}
		
		if (initialSearchPoint.length != prototypeSolution.getNumberOfVariables()) {
			Validate.that("initialSearchPoint", initialSearchPoint)
				.fails("must match the number of decision variables");
		}
		
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			RealVariable realVariable = (RealVariable)prototypeSolution.getVariable(i);
			Validate.that("initialSearchPoint", initialSearchPoint[i])
				.isBetween(realVariable.getLowerBound(), realVariable.getUpperBound());
		}
	}
	
	/**
	 * Validates parameters after calling the {@link #initialize()} method.
	 * 
	 * @throws IllegalArgumentException if any of the checks fail
	 */
	private void postInitChecks() {
		Validate.that("number of variables", problem.getNumberOfVariables()).isGreaterThan(0);
		Validate.that("lambda (offspring population size)", lambda).isGreaterThanOrEqualTo(1);
		Validate.that("mu (number of parents selected for recombination)", mu).isLessThanOrEqualTo("lambda", lambda);
		Validate.that("cs (step-size cumulation parameter)", cs).isBetween(0.0, 1.0);
		Validate.that("damps (step-size damping parameter)", damps).isGreaterThan(0.0);
		Validate.that("cc (cumulation parameter)", cc).isBetween(0.0, 1.0);
		Validate.that("mueff (variance effectiveness)", mueff).isGreaterThanOrEqualTo(0.0);
		Validate.that("ccov (learning rate)", ccov).isGreaterThanOrEqualTo(0.0);
		Validate.that("ccovsep (learning rate when diagonal mode is active)", ccovsep).isGreaterThanOrEqualTo(0.0);
		Validate.that("sigma (initial standard deviation)", sigma).isGreaterThan(0.0);
		Validate.that("diagD (initial standard deviations)", StatUtils.min(diagD)).isGreaterThan(0.0);
	}

	/**
	 * Initializes the internal state of the algorithm.
	 */
	protected void initializeState() {
		int N = problem.getNumberOfVariables();
		Solution prototypeSolution = problem.newSolution();
		
		preInitChecks(prototypeSolution);

		// initialization
		if (sigma < 0) {
			sigma = 0.5;
		}
		
		if (diagonalIterations < 0) {
			diagonalIterations = 150 * N / lambda;
		}
		
		diagD = new double[N];
		pc = new double[N];
		ps = new double[N];
		B = new double[N][N];
		C = new double[N][N];

		for (int i = 0; i < N; i++) {
			pc[i] = 0;
			ps[i] = 0;
			diagD[i] = 1;

			for (int j = 0; j < N; j++) {
				B[i][j] = 0;
			}

			for (int j = 0; j < i; j++) {
				C[i][j] = 0;
			}

			B[i][i] = 1;
			C[i][i] = diagD[i] * diagD[i];
		}
		
		// initialization of xmean
		if (xmean == null) {
			xmean = new double[N];
			
			if (initialSearchPoint == null) {
				for (int i = 0; i < N; i++) {
					RealVariable variable = (RealVariable)prototypeSolution.getVariable(i);
					double offset = sigma * diagD[i];
					double range = (variable.getUpperBound() - variable.getLowerBound() - 2*sigma*diagD[i]);
					
					if (offset > 0.4 * (variable.getUpperBound() - variable.getLowerBound())) {
						offset = 0.4 * (variable.getUpperBound() - variable.getLowerBound());
						range = 0.2 * (variable.getUpperBound() - variable.getLowerBound());
					}
					
					xmean[i] = variable.getLowerBound() + offset + PRNG.nextDouble() * range;
				}
			} else {
				for (int i = 0; i < N; i++) {
					xmean[i] = initialSearchPoint[i] + sigma * diagD[i] * PRNG.nextGaussian();
				}
			}
		}

		// initialization of other parameters
		chiN = Math.sqrt(N) * (1.0 - 1.0 / (4.0 * N) + 1.0 / (21.0 * N * N));
		mu = (int)Math.floor(lambda / 2.0);
		weights = new double[mu];

		for (int i = 0; i < mu; i++) {
			weights[i] = Math.log(mu + 1) - Math.log(i + 1);
		}

		double sum = StatUtils.sum(weights);

		for (int i = 0; i < mu; i++) {
			weights[i] /= sum;
		}

		double sumSq = StatUtils.sumSq(weights);

		mueff = 1.0 / sumSq; // also called mucov
		
		if (cs < 0) {
			cs = (mueff + 2) / (N + mueff + 3);
		}
		
		if (damps < 0) {
			damps = (1 + 2 * Math.max(0, Math.sqrt((mueff - 1.0) / (N + 1)) - 1)) + cs;
		}
		
		if (cc < 0) {
			cc = 4.0 / (N + 4.0);
		}
		
		if (ccov < 0) {
			ccov = 2.0 / (N + 1.41) / (N + 1.41) / mueff + (1 - (1.0 / mueff)) * Math.min(1, (2 * mueff - 1) / (mueff + (N + 2) * (N + 2)));
		}
		
		if (ccovsep < 0) {
			ccovsep = Math.min(1, ccov * (N + 1.5) / 3.0);
		}
		
		postInitChecks();
	}
	
	@Override
	public void initialize() {
		super.initialize();
		initializeState();
		
		// Run one iteration to produce the initial population.
		iterate();
	}

	/**
	 * Performs eigenvalue decomposition to update B and diagD.
	 */
	private void eigendecomposition() {
		int N = problem.getNumberOfVariables();

		lastEigenupdate = iteration;

		if (diagonalIterations >= iteration) {
			for (int i = 0; i < N; i++) {
				diagD[i] = Math.sqrt(C[i][i]);
			}
		} else {
			// set B <- C
			for (int i = 0; i < N; i++) {
				for (int j = 0; j <= i; j++) {
					B[i][j] = C[i][j];
					B[j][i] = C[i][j];
				}
			}

			// eigenvalue decomposition
			double[] offdiag = new double[N];
			tred2(N, B, diagD, offdiag);
			tql2(N, diagD, offdiag, B);

			if (checkConsistency) {
				checkEigenSystem(N, C, diagD, B);
			}

			// assign diagD to eigenvalue square roots
			for (int i = 0; i < N; i++) {
				if (diagD[i] < 0) { // numerical problem?
					System.err.println("WARNING: An eigenvalue has become negative, setting to 0!");
					diagD[i] = 0;
				}

				diagD[i] = Math.sqrt(diagD[i]);
			}
		}
	}
	
	/**
	 * Test and correct any numerical issues.
	 */
	private void testAndCorrectNumerics() {
		// flat fitness, test is function values are identical
		if (population.size() > 0) {
			population.sort(new ObjectiveComparator(0));
			
			if (population.get(0).getObjective(0) == population.get(Math.min(lambda-1, lambda/2 + 1) - 1).getObjective(0)) {
				System.err.println("WARNING: Flat fitness landscape, consider reformulation of fitness, step size increased");
				sigma *= Math.exp(0.2 + cs/damps);
			}
		}
		
		// align (renormalize) scale C (and consequently sigma)
		double fac = 1.0;
		
		if (StatUtils.max(diagD) < 1e-6) {
			fac = 1.0 / StatUtils.max(diagD);
		} else if (StatUtils.min(diagD) > 1e4) {
			fac = 1.0 / StatUtils.min(diagD);
		}
		
		if (fac != 1.0) {
			sigma /= fac;
			
			for (int i = 0; i < problem.getNumberOfVariables(); i++) {
				pc[i] *= fac;
				diagD[i] *= fac;
				
				for (int j = 0; j <= i; j++) {
					C[i][j] *= fac*fac;
				}
			}
		}
	}

	/**
	 * Samples a new population.
	 */
	private void samplePopulation() {
		boolean feasible = true;
		int N = problem.getNumberOfVariables();

		if ((iteration - lastEigenupdate) > 1.0 / ccov / N / 5.0) {
			eigendecomposition();
		}
		
		if (checkConsistency) {
			testAndCorrectNumerics();
		}
		
		population.clear();

		// sample the distribution
		for (int i = 0; i < lambda; i++) {
			Solution solution = problem.newSolution();

			if (diagonalIterations >= iteration) {
				// loop until a feasible solution is generated
				do {
					feasible = true;

					for (int j = 0; j < N; j++) {
						RealVariable variable = (RealVariable)solution.getVariable(j);
						double value = xmean[j] + sigma * diagD[j] * PRNG.nextGaussian();

						if (value < variable.getLowerBound() || value > variable.getUpperBound()) {
							feasible = false;
							break;
						}

						variable.setValue(value);
					}
				} while (!feasible);
			} else {
				double[] artmp = new double[N];
				
				// loop until a feasible solution is generated
				do {
					feasible = true;

					for (int j = 0; j < N; j++) {
						artmp[j] = diagD[j] * PRNG.nextGaussian();
					}

					// add mutation (sigma * B * (D*z))
					for (int j = 0; j < N; j++) {
						RealVariable variable = (RealVariable)solution.getVariable(j);
						double sum = 0.0;

						for (int k = 0; k < N; k++) {
							sum += B[j][k] * artmp[k];
						}

						double value = xmean[j] + sigma * sum;

						if (value < variable.getLowerBound() || value > variable.getUpperBound()) {
							feasible = false;
							break;
						}

						variable.setValue(value);
					}
				} while (!feasible);
			}
			
			population.add(solution);
		}

		iteration++;
	}
	
	/**
	 * Comparator using indicator-based fitness to break ties.
	 */
	private class NondominatedFitnessComparator extends ChainedComparator implements Comparator<Solution> {

		public NondominatedFitnessComparator() {
			super(new RankComparator(), new FitnessComparator(fitnessEvaluator.areLargerValuesPreferred()));
		}

	}
	
	/**
	 * Comparator for single-objective problems using aggregate constraint violations to handle constrained
	 * optimization problems.
	 */
	private static class SingleObjectiveComparator extends ChainedComparator implements Comparator<Solution> {

		public SingleObjectiveComparator() {
			super(new AggregateConstraintComparator(), new ObjectiveComparator(0));
		}
		
	}

	/**
	 * Updates the internal parameters given the evaluated population.
	 */
	private void updateDistribution() {
		int N = problem.getNumberOfVariables();
		double[] xold = Arrays.copyOf(xmean, xmean.length);
		double[] BDz = new double[N];
		double[] artmp = new double[N];

		// sort function values
		if (problem.getNumberOfObjectives() == 1) {
			population.sort(new SingleObjectiveComparator());
		} else {
			if (fitnessEvaluator == null) {
				population.sort(new NondominatedSortingComparator());
			} else {
				population.sort(new NondominatedFitnessComparator());
			}
		}

		// calculate xmean and BDz
		for (int i = 0; i < N; i++) {
			xmean[i] = 0;

			for (int j = 0; j < mu; j++) {
				xmean[i] += weights[j] * RealVariable.getReal(population.get(j).getVariable(i));
			}

			BDz[i] = Math.sqrt(mueff) * (xmean[i] - xold[i]) / sigma;
		}

		// cumulation for sigma (ps) using B*z
		if (diagonalIterations >= iteration) {
			// given B=I we have B*z = z = D^-1 BDz
			for (int i = 0; i < N; i++) {
				ps[i] = (1.0 - cs) * ps[i] + Math.sqrt(cs * (2.0 - cs)) * BDz[i] / diagD[i];
			}
		} else {
			for (int i = 0; i < N; i++) {
				double sum = 0.0;

				for (int j = 0; j < N; j++) {
					sum += B[j][i] * BDz[j];
				}

				artmp[i] = sum / diagD[i];
			}

			for (int i = 0; i < N; i++) {
				double sum = 0.0;

				for (int j = 0; j < N; j++) {
					sum += B[i][j] * artmp[j];
				}

				ps[i] = (1.0 - cs) * ps[i] + Math.sqrt(cs * (2.0 - cs)) * sum;
			}
		}

		// calculate norm(ps)^2
		double psxps = 0;

		for (int i = 0; i < N; i++) {
			psxps += ps[i] * ps[i];
		}

		// cumulation for covariance matrix (pc) using B*D*z
		int hsig = 0;

		if (Math.sqrt(psxps) / Math.sqrt(1.0 - Math.pow(1.0 - cs, 2.0 * iteration)) / chiN < 1.4 + 2.0 / (N+1)) {
			hsig = 1;
		}

		for (int i = 0; i < N; i++) {
			pc[i] = (1.0 - cc) * pc[i] + hsig * Math.sqrt(cc * (2.0 - cc)) * BDz[i];
		}

		// update of C
		for (int i = 0; i < N; i++) {
			for (int j = (diagonalIterations >= iteration ? i : 0); j <= i; j++) {
				C[i][j] = (1.0 - (diagonalIterations >= iteration ? ccovsep : ccov)) * C[i][j] + ccov * (1.0 / mueff) * (pc[i] * pc[j] + (1 - hsig) * cc * (2.0 - cc) * C[i][j]);

				for (int k = 0; k < mu; k++) {
					C[i][j] += ccov * (1 - 1.0 / mueff) * weights[k] * (RealVariable.getReal(population.get(k).getVariable(i)) - xold[i]) * (RealVariable.getReal(population.get(k).getVariable(j)) - xold[j]) / sigma / sigma;
				}
			}
		}

		// update of sigma
		sigma *= Math.exp(((Math.sqrt(psxps) / chiN) - 1) * cs / damps);
	}
	
	@Override
	protected void iterate() {
		samplePopulation();
		evaluateAll(population);
		
		// extension for multiple objectives
		if (problem.getNumberOfObjectives() > 1) {
			new FastNondominatedSorting().evaluate(population);
			
			if (fitnessEvaluator != null) {
				fitnessEvaluator.evaluate(population);
			}
		}

		archive.addAll(population);
		updateDistribution();
	}
	
	@Override
	public NondominatedPopulation getResult() {
		return archive;
	}
	
	// The remaining functions in this file are copied almost verbatim from Nikolaus Hansen's Java implementation.
	
	/**
	 * Symmetric Householder reduction to tridiagonal form, taken from JAMA package.
	 * 
	 * This is derived from the Algol procedures tred2 by Bowdler, Martin, Reinsch, and Wilkinson, Handbook for Auto.
	 * Comp., Vol.ii-Linear Algebra, and the corresponding Fortran subroutine in EISPACK.
	 */
	private static void tred2(int n, double[][] V, double[] d, double[] e) {
		for (int j = 0; j < n; j++) {
			d[j] = V[n-1][j];
		}

		// Householder reduction to tridiagonal form.
		for (int i = n-1; i > 0; i--) {

			// Scale to avoid under/overflow.
			double scale = 0.0;
			double h = 0.0;
			for (int k = 0; k < i; k++) {
				scale = scale + Math.abs(d[k]);
			}
			if (scale == 0.0) {
				e[i] = d[i-1];
				for (int j = 0; j < i; j++) {
					d[j] = V[i-1][j];
					V[i][j] = 0.0;
					V[j][i] = 0.0;
				}
			} else {
				// Generate Householder vector.
				for (int k = 0; k < i; k++) {
					d[k] /= scale;
					h += d[k] * d[k];
				}
				double f = d[i-1];
				double g = Math.sqrt(h);
				if (f > 0) {
					g = -g;
				}
				e[i] = scale * g;
				h = h - f * g;
				d[i-1] = f - g;
				for (int j = 0; j < i; j++) {
					e[j] = 0.0;
				}

				// Apply similarity transformation to remaining columns.
				for (int j = 0; j < i; j++) {
					f = d[j];
					V[j][i] = f;
					g = e[j] + V[j][j] * f;
					for (int k = j+1; k <= i-1; k++) {
						g += V[k][j] * d[k];
						e[k] += V[k][j] * f;
					}
					e[j] = g;
				}
				f = 0.0;
				for (int j = 0; j < i; j++) {
					e[j] /= h;
					f += e[j] * d[j];
				}
				double hh = f / (h + h);
				for (int j = 0; j < i; j++) {
					e[j] -= hh * d[j];
				}
				for (int j = 0; j < i; j++) {
					f = d[j];
					g = e[j];
					for (int k = j; k <= i-1; k++) {
						V[k][j] -= (f * e[k] + g * d[k]);
					}
					d[j] = V[i-1][j];
					V[i][j] = 0.0;
				}
			}
			d[i] = h;
		}

		// Accumulate transformations.
		for (int i = 0; i < n-1; i++) {
			V[n-1][i] = V[i][i];
			V[i][i] = 1.0;
			double h = d[i+1];
			if (h != 0.0) {
				for (int k = 0; k <= i; k++) {
					d[k] = V[k][i+1] / h;
				}
				for (int j = 0; j <= i; j++) {
					double g = 0.0;
					for (int k = 0; k <= i; k++) {
						g += V[k][i+1] * V[k][j];
					}
					for (int k = 0; k <= i; k++) {
						V[k][j] -= g * d[k];
					}
				}
			}
			for (int k = 0; k <= i; k++) {
				V[k][i+1] = 0.0;
			}
		}
		for (int j = 0; j < n; j++) {
			d[j] = V[n-1][j];
			V[n-1][j] = 0.0;
		}
		V[n-1][n-1] = 1.0;
		e[0] = 0.0;
	}
	
	/**
	 * Symmetric tridiagonal QL algorithm, taken from JAMA package.
	 * 
	 * This is derived from the Algol procedures tql2, by Bowdler, Martin, Reinsch, and Wilkinson, Handbook for Auto.
	 * Comp., Vol.ii-Linear Algebra, and the corresponding Fortran subroutine in EISPACK.
	 */
	private static void tql2(int n, double[] d, double[] e, double[][] V) {
		for (int i = 1; i < n; i++) {
			e[i-1] = e[i];
		}
		e[n-1] = 0.0;

		double f = 0.0;
		double tst1 = 0.0;
		double eps = Math.pow(2.0,-52.0);
		for (int l = 0; l < n; l++) {
			// Find small subdiagonal element
			tst1 = Math.max(tst1,Math.abs(d[l]) + Math.abs(e[l]));
			int m = l;
			while (m < n) {
				if (Math.abs(e[m]) <= eps*tst1) {
					break;
				}
				m++;
			}

			// If m == l, d[l] is an eigenvalue, otherwise, iterate.
			if (m > l) {
				int iter = 0;
				do {
					iter = iter + 1;  // (Could check iteration count here.)

					// Compute implicit shift
					double g = d[l];
					double p = (d[l+1] - g) / (2.0 * e[l]);
					double r = hypot(p,1.0);
					if (p < 0) {
						r = -r;
					}
					d[l] = e[l] / (p + r);
					d[l+1] = e[l] * (p + r);
					double dl1 = d[l+1];
					double h = g - d[l];
					for (int i = l+2; i < n; i++) {
						d[i] -= h;
					}
					f = f + h;

					// Implicit QL transformation.
					p = d[m];
					double c = 1.0;
					double c2 = c;
					double c3 = c;
					double el1 = e[l+1];
					double s = 0.0;
					double s2 = 0.0;
					for (int i = m-1; i >= l; i--) {
						c3 = c2;
						c2 = c;
						s2 = s;
						g = c * e[i];
						h = c * p;
						r = hypot(p,e[i]);
						e[i+1] = s * r;
						s = e[i] / r;
						c = p / r;
						p = c * d[i] - s * g;
						d[i+1] = h + s * (c * g + s * d[i]);

						// Accumulate transformation.
						for (int k = 0; k < n; k++) {
							h = V[k][i+1];
							V[k][i+1] = s * V[k][i] + c * h;
							V[k][i] = c * V[k][i] - s * h;
						}
					}
					p = -s * s2 * c3 * el1 * e[l] / dl1;
					e[l] = s * p;
					d[l] = c * p;

					// Check for convergence.
				} while (Math.abs(e[l]) > eps*tst1);
			}
			d[l] = d[l] + f;
			e[l] = 0.0;
		}

		// Sort eigenvalues and corresponding vectors.
		for (int i = 0; i < n-1; i++) {
			int k = i;
			double p = d[i];
			for (int j = i+1; j < n; j++) {
				if (d[j] < p) { // NH find smallest k>i
					k = j;
					p = d[j];
				}
			}
			if (k != i) {
				d[k] = d[i]; // swap k and i
				d[i] = p;
				for (int j = 0; j < n; j++) {
					p = V[j][i];
					V[j][i] = V[j][k];
					V[j][k] = p;
				}
			}
		}
	}

	/**
	 * Exhaustive test of the output of the eigendecomposition.  Needs O(n^3) operations.
	 * 
	 * @return the number of detected inaccuracies
	 */
	private static int checkEigenSystem(int N, double[][] C, double[] diag, double[][] Q) {
		/* compute Q diag Q^T and Q Q^T to check */
		int i;
		int j;
		int k;
		int res = 0;
		double cc;
		double dd;

		for (i=0; i < N; ++i) {
			for (j=0; j < N; ++j) {
				for (cc=0.,dd=0., k=0; k < N; ++k) {
					cc += diag[k] * Q[i][k] * Q[j][k];
					dd += Q[i][k] * Q[j][k];
				}
				/* check here, is the normalization the right one? */
				if (Math.abs(cc - C[i>j?i:j][i>j?j:i])/Math.sqrt(C[i][i]*C[j][j]) > 1e-10
						&& Math.abs(cc - C[i>j?i:j][i>j?j:i]) > 1e-9) { /* quite large */
					System.err.println("WARNING: Imprecise result detected " + i + " " + j + " " + cc + " " + C[i>j?i:j][i>j?j:i] + " " + (cc-C[i>j?i:j][i>j?j:i]));
					++res;
				}
				if (Math.abs(dd - (i==j?1:0)) > 1e-10) {
					System.err.println("WARNING: Imprecise result detected (Q not orthog.) " + i + " " + j + " " + dd);
					++res;
				}
			}
		}
		return res;
	}

	/**
	 * Compute sqrt(a^2 + b^2) without under/overflow.
	 */
	private static double hypot(double a, double b) {
		double r  = 0;
		if (Math.abs(a) > Math.abs(b)) {
			r = b/a;
			r = Math.abs(a)*Math.sqrt(1+r*r);
		} else if (b != 0) {
			r = a/b;
			r = Math.abs(b)*Math.sqrt(1+r*r);
		}
		return r;
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);

		stream.writeObject(xmean);
		stream.writeInt(iteration);
		stream.writeDouble(sigma);
		stream.writeObject(diagD);
		stream.writeObject(pc);
		stream.writeObject(ps);
		stream.writeObject(B);
		stream.writeObject(C);
		stream.writeInt(lastEigenupdate);
		population.saveState(stream);
		
		if (archive != null) {
			archive.saveState(stream);
		}
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);

		xmean = (double[])stream.readObject();
		
		initializeState();
		
		iteration = stream.readInt();
		sigma = stream.readDouble();
		diagD = (double[])stream.readObject();
		pc = (double[])stream.readObject();
		ps = (double[])stream.readObject();
		B = (double[][])stream.readObject();
		C = (double[][])stream.readObject();
		lastEigenupdate = stream.readInt();
		population.loadState(stream);

		if (archive != null) {
			archive.loadState(stream);
		}
	}

}
