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
package org.moeaframework.algorithm.jmetal;

import java.util.HashMap;
import java.util.Properties;

import jmetal.core.Operator;
import jmetal.core.SolutionType;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.metaheuristics.abyss.AbYSS;
import jmetal.metaheuristics.cellde.CellDE;
import jmetal.metaheuristics.densea.DENSEA;
import jmetal.metaheuristics.fastPGA.FastPGA;
import jmetal.metaheuristics.gde3.GDE3;
import jmetal.metaheuristics.ibea.IBEA;
import jmetal.metaheuristics.mocell.MOCell;
import jmetal.metaheuristics.mochc.MOCHC;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.omopso.OMOPSO;
import jmetal.metaheuristics.paes.PAES;
import jmetal.metaheuristics.pesa2.PESA2;
import jmetal.metaheuristics.smpso.SMPSO;
import jmetal.metaheuristics.smsemoa.SMSEMOA;
import jmetal.metaheuristics.spea2.SPEA2;
import jmetal.operators.crossover.DifferentialEvolutionCrossover;
import jmetal.operators.crossover.HUXCrossover;
import jmetal.operators.crossover.PMXCrossover;
import jmetal.operators.crossover.SBXCrossover;
import jmetal.operators.crossover.SinglePointCrossover;
import jmetal.operators.localSearch.MutationLocalSearch;
import jmetal.operators.mutation.BitFlipMutation;
import jmetal.operators.mutation.NonUniformMutation;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.operators.mutation.SwapMutation;
import jmetal.operators.mutation.UniformMutation;
import jmetal.operators.selection.BinaryTournament;
import jmetal.operators.selection.BinaryTournament2;
import jmetal.operators.selection.DifferentialEvolutionSelection;
import jmetal.operators.selection.RandomSelection;
import jmetal.operators.selection.RankingAndCrowdingSelection;
import jmetal.util.JMException;
import jmetal.util.comparators.FPGAFitnessComparator;
import jmetal.util.comparators.FitnessComparator;

import org.moeaframework.analysis.sensitivity.EpsilonHelper;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.util.TypedProperties;

/**
 * Algorithm provider for JMetal algorithms. Supports the following algorithms:
 * <p>
 * <table width="100%" border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="10%" align="left">Name</th>
 *     <th width="10%" align="left">Type</th>
 *     <th width="80%" align="left">Properties</th>
 *   </tr>
 *   <tr>
 *     <td>AbYSS</td>
 *     <td>Real</td>
 *     <td>{@code populationSize, refSet1Size, refSet2Size, archiveSize,
 *         maxEvaluations, improvementRounds}</td>
 *   </tr>
 *   <tr>
 *     <td>CellDE</td>
 *     <td>Real*</td>
 *     <td>{@code populationSize, archiveSize, maxEvaluations, feedBack,
 *         de.crossoverRate, de.stepSize}</td>
 *   </tr>
 *   <tr>
 *     <td>DENSEA</td>
 *     <td>Any</td>
 *     <td>{@code populationSize, maxEvaluations}</td>
 *   </tr>
 *   <tr>
 *     <td>FastPGA</td>
 *     <td>Any</td>
 *     <td>{@code maxPopSize, initialPopulationSize, maxEvaluations, a, b, c, d,
 *         termination}</td>
 *   </tr>
 *   <tr>
 *     <td>GDE3</td>
 *     <td>Real*</td>
 *     <td>{@code populationSize, maxEvaluations, de.crossoverRate, 
 *         de.stepSize}</td>
 *   </tr>
 *   <tr>
 *     <td>IBEA</td>
 *     <td>Any</td>
 *     <td>{@code populationSize, archiveSize, maxEvaluations}</td>
 *   </tr>
 *   <tr>
 *     <td>MOCell</td>
 *     <td>Any</td>
 *     <td>{@code populationSize, archiveSize, maxEvaluations, feedBack}</td>
 *   </tr>
 *   <tr>
 *     <td>MOCHC</td>
 *     <td>Binary*</td>
 *     <td>{@code initialConvergenceCount, preservedPopulation, 
 *         convergenceValue, populationSize, maxEvaluations, hux.rate, 
 *         bf.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>NSGAII</td>
 *     <td>Any</td>
 *     <td>{@code populationSize, maxEvaluations}</td>
 *   </tr>
 *   <tr>
 *     <td>OMOPSO</td>
 *     <td>Real*</td>
 *     <td>{@code populationSize, archiveSize, maxEvaluations, 
 *         mutationProbability, perturbationIndex, epsilon}</td>
 *   </tr>
 *   <tr>
 *     <td>PAES</td>
 *     <td>Any</td>
 *     <td>{@code archiveSize, bisections, maxEvaluations}</td>
 *   </tr>
 *   <tr>
 *     <td>PESA2</td>
 *     <td>Any</td>
 *     <td>{@code populationSize, archiveSize, bisections, maxEvaluations}</td>
 *   </tr>
 *   <tr>
 *     <td>SMPSO</td>
 *     <td>Real*</td>
 *     <td>{@code populationSize, archiveSize, maxEvaluations, pm.rate,
 *         pm.distributionIndex}</td>
 *   </tr>
 *   <tr>
 *     <td>SMSEMOA</td>
 *     <td>Any</td>
 *     <td>{@code populationSize, maxEvaluations, offset}</td>
 *   </tr>
 *   <tr>
 *     <td>SPEA2</td>
 *     <td>Any</td>
 *     <td>{@code populationSize, archiveSize, maxEvaluations}</td>
 *   </tr>
 * </table>
 * <p>
 * You may also append {@code "-JMetal"} to any algorithm name to use the
 * JMetal implementation incase it is overridden by another implementation.
 * For example, use {@code "NSGAII-JMetal"}.
 * <p>
 * Unless the type is marked with *, the algorithm uses one of the types listed
 * below.  Note that only the types below are supported.  Algorithms marked 
 * with * define operators specific to that algorithm.  See the JMetal 
 * documentation for additional details.
 * <p>
 * <table width="100%" border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="10%" align="left">Type</th>
 *     <th width="20%" align="left">Operators</th>
 *     <th width="70%" align="left">Parameters</th>
 *   </tr>
 *   <tr>
 *     <td>Real</td>
 *     <td>SBX, PM</td>
 *     <td>{@code sbx.rate, sbx.distributionIndex, pm.rate, 
 *         pm.distributionIndex}</td>
 *   </tr>
 *   <tr>
 *     <td>Binary</td>
 *     <td>Single-Point, Bit Flip</td>
 *     <td>{@code 1x.rate, bf.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>Permutation</td>
 *     <td>PMX, Swap</td>
 *     <td>{@code pmx.rate, swap.rate}</td>
 *   </tr>
 * </table>
 */
public class JMetalAlgorithms extends AlgorithmProvider {

	/**
	 * Constructs a JMetal algorithm provider.
	 */
	public JMetalAlgorithms() {
		super();
	}

	@Override
	public Algorithm getAlgorithm(String name, Properties properties,
			Problem problem) {
		TypedProperties typedProperties = new TypedProperties(properties);
		JMetalProblemAdapter adapter = new JMetalProblemAdapter(problem);
		jmetal.core.Algorithm algorithm = null;

		try {
			if (name.equalsIgnoreCase("AbYSS") ||
					name.equalsIgnoreCase("AbYSS-JMetal")) {
				algorithm = newAbYSS(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("CellDE") ||
					name.equalsIgnoreCase("CellDE-JMetal")) {
				algorithm = newCellDE(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("DENSEA") ||
					name.equalsIgnoreCase("DENSEA-JMetal")) {
				algorithm = newDENSEA(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("FastPGA") ||
					name.equalsIgnoreCase("FastPGA-JMetal")) {
				algorithm = newFastPGA(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("GDE3") ||
					name.equalsIgnoreCase("GDE3-JMetal")) {
				algorithm = newGDE3(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("IBEA") ||
					name.equalsIgnoreCase("IBEA-JMetal")) {
				algorithm = newIBEA(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("MOCell") ||
					name.equalsIgnoreCase("MOCell-JMetal")) {
				algorithm = newMOCell(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("MOCHC") ||
					name.equalsIgnoreCase("MOCHC-JMetal")) {
				algorithm = newMOCHC(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("NSGAII") ||
					name.equalsIgnoreCase("NSGAII-JMetal")) {
				algorithm = newNSGAII(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("OMOPSO") ||
					name.equalsIgnoreCase("OMOPSO-JMetal")) {
				algorithm = newOMOPSO(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("PAES") ||
					name.equalsIgnoreCase("PAES-JMetal")) {
				algorithm = newPAES(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("PESA2") ||
					name.equalsIgnoreCase("PESA2-JMetal")) {
				algorithm = newPESA2(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("SMPSO") ||
					name.equalsIgnoreCase("SMPSO-JMetal")) {
				algorithm = newSMPSO(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("SMSEMOA") ||
					name.equalsIgnoreCase("SMSEMOA-JMetal")) {
				algorithm = newSMSEMOA(typedProperties, adapter);
			} else if (name.equalsIgnoreCase("SPEA2") ||
					name.equalsIgnoreCase("SPEA2-JMetal")) {
				algorithm = newSPEA2(typedProperties, adapter);
			}
		} catch (JMException e) {
			throw new ProviderNotFoundException(name, e);
		}

		if (algorithm == null) {
			return null;
		} else {
			return new JMetalAlgorithmAdapter(algorithm, adapter);
		}
	}

	/**
	 * Adds the appropriate variation operators to the algorithm for the
	 * specified problem. Currently supports {@code Binary}, {@code BinaryReal},
	 * {@code Real} and {@code Permutation} encodings.
	 * 
	 * @param algorithm the algorithm which will use the operators
	 * @param properties the properties for customizing the operators
	 * @param problem the problem adapter
	 * @throws JMException if an error occurred when constructing the operators
	 */
	private void setupVariationOperators(jmetal.core.Algorithm algorithm,
			TypedProperties properties, JMetalProblemAdapter problem)
			throws JMException {
		Operator crossover = null;
		Operator mutation = null;
		HashMap<String, Object> parameters = null;
		SolutionType solutionType = problem.getSolutionType();

		if ((solutionType instanceof BinarySolutionType)
				|| (solutionType instanceof BinaryRealSolutionType)) {
			parameters = new HashMap<String, Object>();
			parameters.put("probability", properties.getDouble("1x.rate", 0.9));
			crossover = new SinglePointCrossover(parameters);

			parameters = new HashMap<String, Object>();
			parameters.put("probability", properties.getDouble("bf.rate", 1.0 / problem.getLength(0)));
			mutation = new BitFlipMutation(parameters);
		} else if (solutionType instanceof RealSolutionType) {
			parameters = new HashMap<String, Object>();
			parameters.put("probability", properties.getDouble("sbx.rate", 1.0));
			parameters.put("distributionIndex", properties.getDouble("sbx.distributionIndex", 15.0));
			crossover = new SBXCrossover(parameters);

			parameters = new HashMap<String, Object>();
			parameters.put("probability", properties.getDouble("pm.rate", 1.0 / problem.getNumberOfVariables()));
			parameters.put("distributionIndex", properties.getDouble("pm.distributionIndex", 20.0));
			mutation = new PolynomialMutation(parameters);
		} else if (solutionType instanceof PermutationSolutionType) {
			parameters = new HashMap<String, Object>();
			parameters.put("probability", properties.getDouble("pmx.rate", 1.0));
			crossover = new PMXCrossover(parameters);

			parameters = new HashMap<String, Object>();
			parameters.put("probability", properties.getDouble("swap.rate", 0.35));
			mutation = new SwapMutation(parameters);
		} else {
			throw new JMException("solution type not supported");
		}

		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
	}

	/**
	 * Returns a new {@link AbYSS} instance. Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code AbYSS}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code AbYSS} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private AbYSS newAbYSS(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		if (!(problem.getSolutionType() instanceof RealSolutionType)) {
			throw new JMException("unsupported solution type");
		}

		AbYSS algorithm = new AbYSS(problem);

		algorithm.setInputParameter("populationSize", 
				(int)properties.getDouble("populationSize", 20));
		algorithm.setInputParameter("refSet1Size", 
				(int)properties.getDouble("refSet1Size", 10));
		algorithm.setInputParameter("refSet2Size", 
				(int)properties.getDouble("refSet2Size", 10));
		algorithm.setInputParameter("archiveSize", 
				(int)properties.getDouble("archiveSize", 100));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("probability", properties.getDouble("sbx.rate", 1.0));
		parameters.put("distributionIndex", properties.getDouble("sbx.distributionIndex", 15.0));
		Operator crossover = new SBXCrossover(parameters);

		parameters = new HashMap<String, Object>();
		parameters.put("probability", properties.getDouble("pm.rate", 1.0 / problem.getNumberOfVariables()));
		parameters.put("distributionIndex", properties.getDouble("pm.distributionIndex", 20.0));
		Operator mutation = new PolynomialMutation(parameters);

		parameters = new HashMap<String, Object>();
		parameters.put("problem", problem);
		parameters.put("improvementRounds", (int)properties.getDouble("improvementRounds", 1));
		parameters.put("mutation", mutation);
		Operator improvement = new MutationLocalSearch(parameters);

		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("improvement", improvement);

		return algorithm;
	}

	/**
	 * Returns a new {@link CellDE} instance.  Only real encodings are 
	 * supported.
	 * 
	 * @param properties the properties for customizing the new {@code CellDE}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code CellDE} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private CellDE newCellDE(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		if (!(problem.getSolutionType() instanceof RealSolutionType)) {
			throw new JMException("unsupported solution type");
		}

		CellDE algorithm = new CellDE(problem);

		algorithm.setInputParameter("populationSize", 
				(int)properties.getDouble("populationSize", 100));
		algorithm.setInputParameter("archiveSize", 
				(int)properties.getDouble("archiveSize", 100));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));
		algorithm.setInputParameter("feedBack", 
				(int)properties.getDouble("feedBack", 20));

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("CR", properties.getDouble("de.crossoverRate", 0.1));
		parameters.put("F", properties.getDouble("de.stepSize", 0.5));
		Operator crossover = new DifferentialEvolutionCrossover(parameters);
		
		Operator selection = new BinaryTournament(null);

		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("selection", selection);

		return algorithm;
	}

	/**
	 * Returns a new {@link DENSEA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code DENSEA}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code DENSEA} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private DENSEA newDENSEA(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		DENSEA algorithm = new DENSEA(problem);

		algorithm.setInputParameter("populationSize", 
				(int)properties.getDouble("populationSize", 100));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));

		setupVariationOperators(algorithm, properties, problem);

		Operator selection = new BinaryTournament(null);
		algorithm.addOperator("selection", selection);

		return algorithm;
	}

	/**
	 * Returns a new {@link FastPGA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code FastPGA}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code FastPGA} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private FastPGA newFastPGA(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		FastPGA algorithm = new FastPGA(problem);

		algorithm.setInputParameter("maxPopSize", 
				(int)properties.getDouble("maxPopSize", 100));
		algorithm.setInputParameter("initialPopulationSize", 
				(int)properties.getDouble("initialPopulationSize", 100));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));
		algorithm.setInputParameter("a", properties.getDouble("a", 20.0));
		algorithm.setInputParameter("b", properties.getDouble("b", 1.0));
		algorithm.setInputParameter("c", properties.getDouble("c", 20.0));
		algorithm.setInputParameter("d", properties.getDouble("d", 0.0));
		algorithm.setInputParameter("termination", 
				(int)properties.getDouble("termination", 1));

		setupVariationOperators(algorithm, properties, problem);

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("comparator", new FPGAFitnessComparator());
		Operator selection = new BinaryTournament(parameters);
		
		algorithm.addOperator("selection", selection);

		return algorithm;
	}
	
	/**
	 * Returns a new {@link GDE3} instance.  Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code GDE3}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code GDE3} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private GDE3 newGDE3(TypedProperties properties, 
			JMetalProblemAdapter problem) throws JMException {
		if (!(problem.getSolutionType() instanceof RealSolutionType)) {
			throw new JMException("unsupported solution type");
		}

		GDE3 algorithm = new GDE3(problem);
		int populationSize = (int)properties.getDouble("populationSize", 100);

		algorithm.setInputParameter("populationSize", populationSize);
		algorithm.setInputParameter("maxIterations", (int)properties.getDouble(
				"maxEvaluations", 25000) / populationSize);

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("CR", properties.getDouble("de.crossoverRate", 0.1));
		parameters.put("F", properties.getDouble("de.stepSize", 0.5));
		Operator crossover = new DifferentialEvolutionCrossover(parameters);

		Operator selection = new DifferentialEvolutionSelection(null);

		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("selection", selection);

		return algorithm;
	}

	/**
	 * Returns a new {@link IBEA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code IBEA}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code IBEA} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private IBEA newIBEA(TypedProperties properties, JMetalProblemAdapter problem)
			throws JMException {
		IBEA algorithm = new IBEA(problem);

		algorithm.setInputParameter("populationSize", 
				(int)properties.getDouble("populationSize", 100));
		algorithm.setInputParameter("archiveSize", 
				(int)properties.getDouble("archiveSize", 100));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));

		setupVariationOperators(algorithm, properties, problem);

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("comparator", new FitnessComparator());
		Operator selection = new BinaryTournament(parameters);
		
		algorithm.addOperator("selection", selection);

		return algorithm;
	}

	/**
	 * Returns a new {@code MOCell} instance.
	 * 
	 * @param properties the properties for customizing the new {@code MOCell}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code MOCell} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private MOCell newMOCell(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		MOCell algorithm = new MOCell(problem);

		algorithm.setInputParameter("populationSize", 
				(int)properties.getDouble("populationSize", 100));
		algorithm.setInputParameter("archiveSize", 
				(int)properties.getDouble("archiveSize", 100));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));
		algorithm.setInputParameter("feedBack", 
				(int)properties.getDouble("feedBack", 20));

		setupVariationOperators(algorithm, properties, problem);

		Operator selection = new BinaryTournament(null);
		algorithm.addOperator("selection", selection);

		return algorithm;
	}

	/**
	 * Returns a new {@link MOCHC} instance. Only binary encodings are 
	 * supported.
	 * 
	 * @param properties the properties for customizing the new {@code MOCHC}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code MOCHC} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private MOCHC newMOCHC(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		if (!(problem.getSolutionType() instanceof BinarySolutionType)) {
			throw new JMException("unsupported solution type");
		}

		MOCHC algorithm = new MOCHC(problem);

		algorithm.setInputParameter("initialConvergenceCount", 
				properties.getDouble("initialConvergenceCount", 0.25));
		algorithm.setInputParameter("preservedPopulation", 
				properties.getDouble("preservedPopulation", 0.05));
		algorithm.setInputParameter("convergenceValue", 
				(int)properties.getDouble("convergenceValue", 3));
		algorithm.setInputParameter("populationSize", 
				(int)properties.getDouble("populationSize", 100));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("probability", properties.getDouble("hux.rate", 1.0));
		Operator crossoverOperator = new HUXCrossover(parameters);

		parameters = new HashMap<String, Object>();
		parameters.put("probability", properties.getDouble("bf.rate", 0.35));
		Operator mutationOperator = new BitFlipMutation(parameters);

		Operator parentsSelection = new RandomSelection(null);

		parameters = new HashMap<String, Object>();
		parameters.put("problem", problem);
		Operator newGenerationSelection = new RankingAndCrowdingSelection(parameters);

		algorithm.addOperator("crossover", crossoverOperator);
		algorithm.addOperator("cataclysmicMutation", mutationOperator);
		algorithm.addOperator("parentSelection", parentsSelection);
		algorithm.addOperator("newGenerationSelection", newGenerationSelection);

		return algorithm;
	}
	
	private NSGAII newNSGAII(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		NSGAII algorithm = new NSGAII(problem);

		algorithm.setInputParameter("populationSize", 
				(int)properties.getDouble("populationSize", 100));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));

		setupVariationOperators(algorithm, properties, problem);

	    Operator selection = new BinaryTournament2(null);                           
	    algorithm.addOperator("selection", selection);
	    
	    return algorithm;
	}

	/**
	 * Returns a new {@link OMOPSO} instance. Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code OMOPSO}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code OMOPSO} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private OMOPSO newOMOPSO(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		if (!(problem.getSolutionType() instanceof RealSolutionType)) {
			throw new JMException("unsupported solution type");
		}

		OMOPSO algorithm = new OMOPSO(problem);
		int populationSize = (int)properties.getDouble("populationSize", 100);
		int maxIterations = (int)properties.getDouble("maxEvaluations", 25000) /
				populationSize;
		double mutationProbability = 1.0 / problem.getNumberOfVariables();

		algorithm.setInputParameter("swarmSize", populationSize);
		algorithm.setInputParameter("archiveSize", 
				(int)properties.getDouble("archiveSize", 100));
		algorithm.setInputParameter("maxIterations", maxIterations);
		algorithm.setInputParameter("epsilon",
				properties.getDoubleArray("epsilon", new double[] {
						EpsilonHelper.getEpsilon(problem.getProblem()) }));
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("probability", properties.getDouble("mutationProbability", mutationProbability));
		parameters.put("perturbation", properties.getDouble("perturbationIndex", 0.5));
		Operator uniformMutation = new UniformMutation(parameters);
		
		parameters = new HashMap<String, Object>();
		parameters.put("probability", properties.getDouble("mutationProbability", mutationProbability));
		parameters.put("perturbation", properties.getDouble("perturbationIndex", 0.5));
		parameters.put("maxIterations", maxIterations);
		Operator nonUniformMutation = new NonUniformMutation(parameters);
		
		algorithm.addOperator("uniformMutation", uniformMutation);
	    algorithm.addOperator("nonUniformMutation", nonUniformMutation);

		return algorithm;
	}

	/**
	 * Returns a new {@link PAES} instanc.
	 * 
	 * @param properties the properties for customizing the new {@code PAES}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code PAES} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private PAES newPAES(TypedProperties properties, JMetalProblemAdapter problem)
			throws JMException {
		PAES algorithm = new PAES(problem);

		algorithm.setInputParameter("archiveSize", 
				(int)properties.getDouble("archiveSize", 100));
		algorithm.setInputParameter("biSections", 
				(int)properties.getDouble("bisections", 8));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));

		// only the mutation operator is used
		setupVariationOperators(algorithm, properties, problem);

		return algorithm;
	}

	/**
	 * Returns a new {@link PESA2} instance.
	 * 
	 * @param properties the properties for customizing the new {@code PESA2}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code PESA2} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private PESA2 newPESA2(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		PESA2 algorithm = new PESA2(problem);

		algorithm.setInputParameter("populationSize", 
				(int)properties.getDouble("populationSize", 10));
		algorithm.setInputParameter("archiveSize", 
				(int)properties.getDouble("archiveSize", 100));
		algorithm.setInputParameter("bisections", 
				(int)properties.getDouble("bisections", 8));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));

		setupVariationOperators(algorithm, properties, problem);

		return algorithm;
	}

	/**
	 * Returns a new {@link SMPSO} instance. Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code SMPSO}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code SMPSO} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private SMPSO newSMPSO(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		if (!(problem.getSolutionType() instanceof RealSolutionType)) {
			throw new JMException("unsupported solution type");
		}

		SMPSO algorithm = new SMPSO(problem);
		int populationSize = (int)properties.getDouble("populationSize", 100);
		int maxIterations = (int)properties.getDouble("maxEvaluations", 25000) /
				populationSize;

		algorithm.setInputParameter("swarmSize", populationSize);
		algorithm.setInputParameter("archiveSize", 
				(int)properties.getDouble("archiveSize", 100));
		algorithm.setInputParameter("maxIterations", maxIterations);
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("probability", properties.getDouble("pm.rate", 1.0 / problem.getNumberOfVariables()));
		parameters.put("distributionIndex", properties.getDouble("pm.distributionIndex", 20.0));
		Operator mutation = new PolynomialMutation(parameters);

		algorithm.addOperator("mutation", mutation);

		return algorithm;
	}

	/**
	 * Returns a new {@link SPEA2} instance.
	 * 
	 * @param properties the properties for customizing the new {@code SPEA2}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code SPEA2} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private SPEA2 newSPEA2(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		SPEA2 algorithm = new SPEA2(problem);

		algorithm.setInputParameter("populationSize", 
				(int)properties.getDouble("populationSize", 100));
		algorithm.setInputParameter("archiveSize", 
				(int)properties.getDouble("archiveSize", 100));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));

		setupVariationOperators(algorithm, properties, problem);

		Operator selection = new BinaryTournament(null);
		algorithm.addOperator("selection", selection);

		return algorithm;
	}

	/**
	 * Returns a new {@link SMSEMOA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code SMSEMOA}
	 *        instance
	 * @param problem the problem adapter
	 * @return a new {@code SMSEMOA} instance
	 * @throws JMException if an error occurred when constructing the algorithm
	 */
	private SMSEMOA newSMSEMOA(TypedProperties properties,
			JMetalProblemAdapter problem) throws JMException {
		SMSEMOA algorithm = new SMSEMOA(problem);

		algorithm.setInputParameter("populationSize", 
				(int)properties.getDouble("populationSize", 100));
		algorithm.setInputParameter("maxEvaluations", 
				(int)properties.getDouble("maxEvaluations", 25000));
		algorithm.setInputParameter("offset", 
				properties.getDouble("offset", 100.0));

		setupVariationOperators(algorithm, properties, problem);

		Operator selection = new RandomSelection(null);
		algorithm.addOperator("selection", selection);

		return algorithm;
	}

}
