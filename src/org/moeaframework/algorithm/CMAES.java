/* Copyright 2009-2018 David Hadka
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

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.core.FastNondominatedSorting;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.FitnessComparator;
import org.moeaframework.core.comparator.NondominatedSortingComparator;
import org.moeaframework.core.comparator.ObjectiveComparator;
import org.moeaframework.core.comparator.RankComparator;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

/**
 * The Covariance Matrix Adaption Evolution Strategy (CMA-ES) algorithm for
 * single and multi-objective problems.  For multi-objective problems,
 * individuals are compared using Pareto ranking and crowding distance to break
 * ties.  An optional {@code fitnessEvaluator} parameter can be specified to
 * replace the crowding distance calculation with, for example, the
 * hypervolume indicator.
 * <p>
 * This file is based on the Java implementation of CMA-ES by Nikolaus Hansen
 * available at https://www.lri.fr/~hansen/cmaes_inmatlab.html#java,
 * originally licensed under the GNU LGPLv3.
 * <p>
 * References:
 * <ol>
 *   <li>Hansen and Kern (2004). Evaluating the CMA Evolution Strategy on
 *       Multimodal Test Functions. In Proceedings of the Eighth International
 *       Conference on Parallel Problem Solving from Nature PPSN VIII,
 *       pp. 282-291, Berlin: Springer.
 *   <li>Hansen, N. (2011).  The CMA Evolution Strategy: A Tutorial.
 *       Available at https://www.lri.fr/~hansen/cmatutorial.pdf.
 *   <li>Igel, C., N. Hansen, and S. Roth (2007).  Covariance Matrix Adaptation
 *       for Multi-objective Optimization.  Evolutionary Computation,
 *       15(1):1-28.
 * </ol>
 */
public class CMAES extends AbstractAlgorithm {
	
	/**
	 * An initial search point to start searching from, or {@code null} if no
	 * initial search point is specified.
	 */
	private final double[] initialSearchPoint;
	
	/**
	 * If {@code true}, perform consistency checks to ensure CMA-ES remains
	 * numerically stable.
	 */
	private final boolean checkConsistency;
	
	/**
	 * Secondary comparison criteria for comparing population individuals
	 * with the same rank.  If {@code null}, the default crowding distance
	 * metric is used.
	 */
	private final FitnessEvaluator fitnessEvaluator;
	
	/**
	 * Nondominated archive of the best solutions found.
	 */
	private final NondominatedPopulation archive;

	/**
	 * The number of iterations already performed.
	 */
	private int iteration;

	/**
	 * The number of iterations in which only the covariance diagonal is used.
	 * This enhancement helps speed up the algorithm when there are many
	 * decision variables.  Set to {@code 0} to always use the full covariance
	 * matrix.
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
	 * Constructs a new CMA-ES instance using default parameters.
	 *
	 * @param problem the problem to optimize
	 * @param lambda the offspring population size
	 */
	public CMAES(Problem problem, int lambda) {
		this(problem, lambda, null, new NondominatedPopulation());
	}
	
	/**
	 * Constructs a new CMA-ES instance using default parameters.
	 *
	 * @param problem the problem to optimize
	 * @param lambda the offspring population size
	 * @param fitnessEvaluator secondary comparison criteria for comparing
	 *        population individuals with the same rank, or {@code null} to use
	 *        the default crowding distance metric
	 * @param archive the nondominated archive for storing the elite individuals
	 */
	public CMAES(Problem problem, int lambda, FitnessEvaluator fitnessEvaluator,
			NondominatedPopulation archive) {
		this(problem, lambda, fitnessEvaluator, archive, null, false,
				-1, -1, -1, -1, -1, -1, -1);
	}

	/**
	 * Constructs a new CMA-ES instance with the given parameters.
	 * <p>
	 * If the parameters {@code cc}, {@code cs}, {@code damps}, {@code ccov},
	 * {@code ccovsep}, {@code sigma}, and {@code diagonalIterations} are set
	 * to any negative number, then the default parameter will be used.
	 * 
	 * @param problem the problem to optimize
	 * @param lambda the offspring population size
	 * @param fitnessEvaluator secondary comparison criteria for comparing
	 *        population individuals with the same rank, or {@code null} to use
	 *        the default crowding distance metric
	 * @param archive the nondominated archive for storing the elite individuals
	 * @param initialSearchPoint an initial search point, or {@code null} if
	 *        no initial search point is specified
	 * @param checkConsistency if {@code true}, performs checks to ensure
	 *        CMA-ES remains numerically stable
	 * @param cc the cumulation parameter
	 * @param cs the step size of the cumulation parameter
	 * @param damps the damping factor for the step size
	 * @param ccov the learning rate
	 * @param ccovsep the learning rate when in diagonal-only mode
	 * @param sigma the initial standard deviation
	 * @param diagonalIterations the number of iterations in which only the
	 *        covariance diagonal is used
	 */
	public CMAES(Problem problem, int lambda, FitnessEvaluator fitnessEvaluator,
			NondominatedPopulation archive, double[] initialSearchPoint,
			boolean checkConsistency, double cc, double cs, double damps,
			double ccov, double ccovsep, double sigma, int diagonalIterations) {
		super(problem);
		this.lambda = lambda;
		this.initialSearchPoint = initialSearchPoint;
		this.checkConsistency = checkConsistency;
		this.fitnessEvaluator = fitnessEvaluator;
		this.archive = archive;
		this.cc = cc;
		this.cs = cs;
		this.damps = damps;
		this.ccov = ccov;
		this.ccovsep = ccovsep;
		this.sigma = sigma;
		this.diagonalIterations = diagonalIterations;
		
		population = new Population();
	}
	
	/**
	 * Validates parameters prior to calling the {@link #initialize()} method.
	 * Checks include ensuring the initial search point is valid and ensures
	 * each devision variable is real-valued.
	 * 
	 * @param prototypeSolution an example solution for retrieving variable
	 *        types and bounds
	 * @throws IllegalArgumentException if any of the checks fail
	 */
	private void preInitChecks(Solution prototypeSolution) {
		if (initialSearchPoint != null) {
			if (initialSearchPoint.length != prototypeSolution.getNumberOfVariables()) {
				throw new IllegalArgumentException("initial search point is not the correct length (expected=" + prototypeSolution.getNumberOfVariables() + ", actual=" + initialSearchPoint.length + ")");
			}
		}
		
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			if (prototypeSolution.getVariable(i) instanceof RealVariable) {
				RealVariable variable = (RealVariable)prototypeSolution.getVariable(i);
				
				if (initialSearchPoint != null) {
					if (initialSearchPoint[i] > variable.getUpperBound()) {
						throw new IllegalArgumentException("initial search point is out of bounds (index=" + i + ", value=" + initialSearchPoint[i] + ", ub=" + variable.getUpperBound() + ")");
					} else if (initialSearchPoint[i] < variable.getLowerBound()) {
						throw new IllegalArgumentException("initial search point is out of bounds (index=" + i + ", value=" + initialSearchPoint[i] + ", lb=" + variable.getLowerBound() + ")");
					}
				}
			} else {
				throw new IllegalArgumentException("CMA-ES is only applicable to real-valued decision variables");
			}
		}
	}
	
	/**
	 * Validates parameters after calling the {@link #initialize()} method.
	 * 
	 * @throws IllegalArgumentException if any of the checks fail
	 */
	private void postInitChecks() {
		if (problem.getNumberOfVariables() == 0) {
			throw new IllegalArgumentException("dimension must be greater than zero");
		}
		
		if (lambda <= 1) {
			throw new IllegalArgumentException("offspring population size, lambda, must be greater than one (lambda=" + lambda + ")");
		}
		
		if (mu < 1) {
			throw new IllegalArgumentException("number of parents used in recombination, mu, must be smaller or equal to lambda (lambda=" + lambda + ", mu=" + mu + ")");
		}
		
		if ((cs <= 0) || (cs > 1)) {
			throw new IllegalArgumentException("0 < cs <= 1 must hold for step-size cumulation parameter (cs=" + cs + ")");
		}
		
		if (damps <= 0) {
			throw new IllegalArgumentException("step size damping parameter, damps, must be greater than zero (damps=" + damps + ")");
		}
		
		if ((cc <= 0) || (cc > 1)) {
			throw new IllegalArgumentException("0 < cc <= 1 must hold for cumulation parameter (cc=" + cc + ")");
		}
		
		if (mueff < 0) {
			throw new IllegalArgumentException("mueff >= 0 must hold (mueff=" + mueff + ")");
		}
		
		if (ccov < 0) {
			throw new IllegalArgumentException("ccov >= 0 must hold (ccov=" + ccov + ")");
		}
		
		if (ccovsep < 0) {
			throw new IllegalArgumentException("ccovsep >= 0 must hold (ccovsep=" + ccovsep + ")");
		}
		
		if (sigma <= 0) {
			throw new IllegalArgumentException("initial standard deviation, sigma, must be positive (sigma=" + sigma + ")");
		}
		
		if (StatUtils.min(diagD) <= 0) {
			throw new IllegalArgumentException("initial standard deviations, diagD, must be positive");
		}
	}

	@Override
	public void initialize() {
		super.initialize();
		
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
					B[i][j] = B[j][i] = C[i][j];
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
					System.err.println("an eigenvalue has become negative");
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
				System.err.println("flat fitness landscape, consider reformulation of fitness, step size increased");
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
	private class NondominatedFitnessComparator extends ChainedComparator implements Comparator<Solution>, Serializable {

		private static final long serialVersionUID = -4088873047790962685L;

		public NondominatedFitnessComparator() {
			super(new RankComparator(), new FitnessComparator(fitnessEvaluator.areLargerValuesPreferred()));
		}

	}
	
	/**
	 * Comparator for single-objective problems using aggregate constraint violations to handle constrained optimization problems.
	 */
	private class SingleObjectiveComparator extends ChainedComparator implements Comparator<Solution>, Serializable {

		private static final long serialVersionUID = 6182830776461513578L;

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
				xmean[i] += weights[j] * EncodingUtils.getReal(population.get(j).getVariable(i));
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
					C[i][j] += ccov * (1 - 1.0 / mueff) * weights[k] * (EncodingUtils.getReal(population.get(k).getVariable(i)) - xold[i]) * (EncodingUtils.getReal(population.get(k).getVariable(j)) - xold[j]) / sigma / sigma;
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
	public void step() {
		// Since unlike other algorithms, the initialize() method does not
		// produce an initial population.  To remain consistent, we override
		// the step() method so that iterate() is called after initialize().
		if (isTerminated()) {
			throw new AlgorithmTerminationException(this, 
					"algorithm already terminated");
		} else if (!isInitialized()) {
			initialize();
			iterate();
		} else {
			iterate();
		}
	}
	
	@Override
	public NondominatedPopulation getResult() {
		return archive;
	}
	
	// The remaining functions in this file are copied almost verbatim from
	// Nikolaus Hansen's Java implementation.
	
	/**
	 * Symmetric Householder reduction to tridiagonal form, taken from JAMA
	 * package.
	 * 
	 * This is derived from the Algol procedures tred2 by Bowdler, Martin,
	 * Reinsch, and Wilkinson, Handbook for Auto. Comp., Vol.ii-Linear Algebra,
	 * and the corresponding Fortran subroutine in EISPACK.
	 */
	public static void tred2(int n, double[][] V, double[] d, double[] e) {
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
	 * This is derived from the Algol procedures tql2, by Bowdler, Martin,
	 * Reinsch, and Wilkinson, Handbook for Auto. Comp., Vol.ii-Linear Algebra,
	 * and the corresponding Fortran subroutine in EISPACK.
	 */
	public static void tql2(int n, double[] d, double[] e, double[][] V) {
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

			// If m == l, d[l] is an eigenvalue,
			// otherwise, iterate.
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
	 * Exhaustive test of the output of the eigendecomposition.  Needs O(n^3)
	 * operations.
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
					System.err.println("imprecise result detected " + i + " " + j + " " + cc + " " + C[i>j?i:j][i>j?j:i] + " " + (cc-C[i>j?i:j][i>j?j:i]));
					++res;
				}
				if (Math.abs(dd - (i==j?1:0)) > 1e-10) {
					System.err.println("imprecise result detected (Q not orthog.) " + i + " " + j + " " + dd);
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
	
	/**
	 * Proxy for serializing and deserializing the state of a {@code CMAES}
	 * instance.
	 */
	private static class CMAESState implements Serializable {

		private static final long serialVersionUID = 2634186176589891715L;

		/**
		 * The {@code population} from the {@code MOEAD} instance.
		 */
		private final List<Solution> population;
		
		/**
		 * The archive stored in a serializable list.
		 */
		private final List<Solution> archive;
		
		/**
		 * The value of {@code iteration} from the {@code CMAES} instance.
		 */
		private int iteration;

		/**
		 * The value of {@code sigma} from the {@code CMAES} instance.
		 */
		private double sigma;

		/**
		 * The value of {@code diagD} from the {@code CMAES} instance.
		 */
		private double[] diagD;

		/**
		 * The value of {@code xmean} from the {@code CMAES} instance.
		 */
		private double[] xmean;

		/**
		 * The value of {@code pc} from the {@code CMAES} instance.
		 */
		private double[] pc;

		/**
		 * The value of {@code ps} from the {@code CMAES} instance.
		 */
		private double[] ps;

		/**
		 * The value of {@code B} from the {@code CMAES} instance.
		 */
		private double[][] B;

		/**
		 * The value of {@code C} from the {@code CMAES} instance.
		 */
		private double[][] C;

		/**
		 * The value of {@code lastEigenupdate} from the {@code CMAES} instance.
		 */
		private int lastEigenupdate;
		
		/**
		 * Constructs a proxy for serializing and deserializing the state of a
		 * {@code CMAES} instance.
		 * 
		 * @param population the value of {@code population} from the {@code CMAES} instance
		 * @param archive the value of {@code archive} from the {@code CMAES} instance
		 * @param iteration the value of {@code iteration} from the {@code CMAES} instance
		 * @param sigma the value of {@code sigma} from the {@code CMAES} instance
		 * @param diagD the value of {@code diagD} from the {@code CMAES} instance
		 * @param xmean the value of {@code xmean} from the {@code CMAES} instance
		 * @param pc the value of {@code pc} from the {@code CMAES} instance
		 * @param ps the value of {@code ps} from the {@code CMAES} instance
		 * @param B the value of {@code B} from the {@code CMAES} instance
		 * @param C the value of {@code C} from the {@code CMAES} instance
		 * @param lastDigenupdate the value of {@code lastEigenupdate} from the {@code CMAES} instance
		 */
		public CMAESState(List<Solution> population, List<Solution> archive,
				int iteration, double sigma, double[] diagD, double[] xmean,
				double[] pc, double[] ps, double[][] B, double[][] C,
				int lastEigenupdate) {
			super();
			this.population = population;
			this.archive = archive;
			this.iteration = iteration;
			this.sigma = sigma;
			this.diagD = diagD;
			this.xmean = xmean;
			this.pc = pc;
			this.ps = ps;
			this.B = B;
			this.C = C;
			this.lastEigenupdate = lastEigenupdate;
		}

	}

	@Override
	public Serializable getState() throws NotSerializableException {
		if (!isInitialized()) {
			throw new AlgorithmInitializationException(this, 
					"algorithm not initialized");
		}

		List<Solution> populationList = new ArrayList<Solution>();
		List<Solution> archiveList = new ArrayList<Solution>();

		for (Solution solution : population) {
			populationList.add(solution);
		}

		if (archive != null) {
			for (Solution solution : archive) {
				archiveList.add(solution);
			}
		}

		return new CMAESState(populationList, archiveList, iteration, sigma,
				diagD.clone(), xmean.clone(), pc.clone(), ps.clone(),
				B.clone(), C.clone(), lastEigenupdate);
	}

	@Override
	public void setState(Object objState) throws NotSerializableException {
		CMAESState state = (CMAESState)objState;
		
		xmean = state.xmean.clone();
		initialize();
		
		population.addAll(state.population);
		
		if (archive != null) {
			archive.addAll(state.archive);
		}
		
		iteration = state.iteration;
		sigma = state.sigma;
		diagD = state.diagD.clone();
		pc = state.pc.clone();
		ps = state.ps.clone();
		B = state.B.clone();
		C = state.C.clone();
		lastEigenupdate = state.lastEigenupdate;
	}

}
