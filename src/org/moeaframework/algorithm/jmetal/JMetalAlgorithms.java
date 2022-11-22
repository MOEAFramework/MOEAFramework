/* Copyright 2009-2022 David Hadka
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

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.core.spi.RegisteredAlgorithmProvider;
import org.moeaframework.util.TypedProperties;
import org.uma.jmetal.algorithm.multiobjective.abyss.ABYSSBuilder;
import org.uma.jmetal.algorithm.multiobjective.cdg.CDGBuilder;
import org.uma.jmetal.algorithm.multiobjective.cellde.CellDE45;
import org.uma.jmetal.algorithm.multiobjective.espea.ESPEABuilder;
import org.uma.jmetal.algorithm.multiobjective.espea.util.EnergyArchive.ReplacementStrategy;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.mochc.MOCHC;
import org.uma.jmetal.algorithm.multiobjective.mochc.MOCHCBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD.FunctionType;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.omopso.OMOPSO;
import org.uma.jmetal.algorithm.multiobjective.omopso.OMOPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.paes.PAES;
import org.uma.jmetal.algorithm.multiobjective.paes.PAESBuilder;
import org.uma.jmetal.algorithm.multiobjective.pesa2.PESA2;
import org.uma.jmetal.algorithm.multiobjective.pesa2.PESA2Builder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSO;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOA;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOABuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.mutation.NonUniformMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.mutation.UniformMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.impl.C9;

/**
 * Algorithm provider for JMetal algorithms.  Refer to {@code docs/jmetal.md} for the specifics
 * of parameterizing the algorithms.
 */
public class JMetalAlgorithms extends RegisteredAlgorithmProvider {

	/**
	 * Constructs a JMetal algorithm provider.
	 */
	public JMetalAlgorithms() {
		super();
		
		register(this::newAbYSS, "AbYSS", "AbYSS-JMetal");
		register(this::newCDG, "CDG", "CDG-JMetal");
		register(this::newCellDE, "CellDE", "CellDE-JMetal");
		register(this::newESPEA, "ESPEA", "ESPEA-JMetal");
		register(this::newGDE3, "GDE3", "GDE3-JMetal");
		register(this::newIBEA, "IBEA", "IBEA-JMetal");
		register(this::newMOCell, "MOCell", "MOCell-JMetal");
		register(this::newMOCHC, "MOCHC", "MOCHC-JMetal");
		register(this::newMOEAD, "MOEAD", "MOEAD-JMetal");
		register(this::newNSGAII, "NSGAII", "NSGAII-JMetal");
		register(this::newNSGAIII, "NSGAIII", "NSGAIII-JMetal");
		register(this::newOMOPSO, "OMOPSO", "OMOPSO-JMetal");
		register(this::newPAES, "PAES", "PAES-JMetal");
		register(this::newPESA2, "PESA2", "PESA2-JMetal");
		register(this::newSMPSO, "SMPSO", "SMPSO-JMetal");
		register(this::newSMSEMOA, "SMSEMOA", "SMSEMOA-JMetal");
		register(this::newSPEA2, "SPEA2", "SPEA2-JMetal");
	}

	@Override
	public Algorithm getAlgorithm(String name, TypedProperties properties,Problem problem) {
		try  {
			return super.getAlgorithm(name, properties, problem);
		} catch (JMetalException e) {
			throw new ProviderNotFoundException(name, e);
		}
	}

	/**
	 * Returns a new {@link AbYSS} instance. Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code AbYSS} instance
	 * @param problem the problem
	 * @return a new {@code AbYSS} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Algorithm newAbYSS(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		if (!(adapter instanceof DoubleProblemAdapter)) {
			throw new JMetalException("AbYSS only supports problems with real decision variables");
		}
		
		Archive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(
				(int)properties.getDouble("archiveSize", 100));
		
		CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(
				properties.getDouble("sbx.rate", 1.0),
				properties.getDouble("sbx.distributionIndex", 15.0));
		
		MutationOperator<DoubleSolution> mutation = new PolynomialMutation(
				properties.getDouble("pm.rate", 1.0 / problem.getNumberOfVariables()),
				properties.getDouble("pm.distributionIndex", 20.0));

	    ABYSSBuilder builder = new ABYSSBuilder((DoubleProblemAdapter)adapter, archive)
	        .setMaxEvaluations((int)properties.getDouble("maxEvaluations", 25000))
	        .setPopulationSize((int)properties.getDouble("populationSize", 20))
	        .setArchiveSize((int)properties.getDouble("archiveSize", 100))
	        .setRefSet1Size((int)properties.getDouble("refSet1Size", 10))
	        .setRefSet2Size((int)properties.getDouble("refSet2Size", 10))
	        .setCrossoverOperator(crossover)
	        .setMutationOperator(mutation)
	        .setNumberOfSubranges(properties.getInt("numberOfSubRanges", 4));
	        
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}
	
	/**
	 * Returns a new {@link CDG} instance. Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code CDG} instance
	 * @param problem the problem
	 * @return a new {@code CDG} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newCDG(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		if (!(adapter instanceof DoubleProblemAdapter)) {
			throw new JMetalException("CDG only supports problems with real decision variables");
		}
		
		DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover(
				properties.getDouble("de.crossoverRate", 0.1),
				properties.getDouble("de.stepSize", 0.5),
				properties.getString("de.variant", "rand/1/bin"));

		CDGBuilder builder = new CDGBuilder((DoubleProblemAdapter)adapter)
	                .setCrossover(crossover)
	                .setMaxEvaluations((int)properties.getDouble("maxEvaluations", 25000))
	                .setPopulationSize((int)properties.getDouble("populationSize", 100))
	                .setResultPopulationSize((int)properties.getDouble("archiveSize", 100))
	                .setNeighborhoodSelectionProbability(properties.getDouble("neighborhoodSelectionProbability", 0.9));
	            
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

	/**
	 * Returns a new {@link CellDE} instance.  Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code CellDE} instance
	 * @param problem the problem
	 * @return a new {@code CellDE} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newCellDE(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		if (!(adapter instanceof DoubleProblemAdapter)) {
			throw new JMetalException("CellDE only supports problems with real decision variables");
		}
		
		DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover(
				properties.getDouble("de.crossoverRate", 0.1),
				properties.getDouble("de.stepSize", 0.5),
				properties.getString("de.variant", "rand/1/bin"));

		BinaryTournamentSelection<DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(
				new RankingAndCrowdingDistanceComparator<DoubleSolution>());

		SolutionListEvaluator<DoubleSolution> evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();
	    
		CellDE45 algorithm = new CellDE45((DoubleProblemAdapter)adapter,
				(int)properties.getDouble("maxEvaluations", 25000),
				(int)properties.getDouble("populationSize", 100),
		        new CrowdingDistanceArchive<DoubleSolution>((int)properties.getDouble("archiveSize", 100)),
		        new C9<DoubleSolution>((int)Math.sqrt(100), (int)Math.sqrt(100)),
				selection,
				crossover,
				(int)properties.getDouble("feedBack", 20),
				evaluator);
		
	    return new JMetalAlgorithmAdapter(algorithm,
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

//	/**
//	 * Returns a new {@link DENSEA} instance.
//	 * 
//	 * @param properties the properties for customizing the new {@code DENSEA}
//	 *        instance
//	 * @param problem the problem adapter
//	 * @return a new {@code DENSEA} instance
//	 * @throws JMException if an error occurred when constructing the algorithm
//	 */
//	private DENSEA newDENSEA(TypedProperties properties,
//			JMetalProblemAdapter problem) throws JMException {
//		DENSEA algorithm = new DENSEA(problem);
//
//		algorithm.setInputParameter("populationSize", 
//				(int)properties.getDouble("populationSize", 100));
//		algorithm.setInputParameter("maxEvaluations", 
//				(int)properties.getDouble("maxEvaluations", 25000));
//
//		setupVariationOperators(algorithm, properties, problem);
//
//		Operator selection = new BinaryTournament(null);
//		algorithm.addOperator("selection", selection);
//
//		return algorithm;
//	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newESPEA(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		CrossoverOperator<?> crossover = JMetalFactory.getInstance().createCrossoverOperator(adapter, properties);
		MutationOperator<?> mutation = JMetalFactory.getInstance().createMutationOperator(adapter, properties);

	    ESPEABuilder builder = new ESPEABuilder(adapter, crossover, mutation);
	    builder.setMaxEvaluations((int)properties.getDouble("maxEvaluations", 25000));
	    builder.setPopulationSize((int)properties.getDouble("populationSize", 100));
	    
	    String replacementStrategy = properties.getString("replacementStrategy", ReplacementStrategy.WORST_IN_ARCHIVE.name());
	    builder.setReplacementStrategy(ReplacementStrategy.valueOf(replacementStrategy));
	    
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

//	/**
//	 * Returns a new {@link FastPGA} instance.
//	 * 
//	 * @param properties the properties for customizing the new {@code FastPGA} instance
//	 * @param problem the problem adapter
//	 * @return a new {@code FastPGA} instance
//	 * @throws JMException if an error occurred when constructing the algorithm
//	 */
//	private FastPGA newFastPGA(TypedProperties properties,
//			JMetalProblemAdapter problem) throws JMException {
//		FastPGA algorithm = new FastPGA(problem);
//
//		algorithm.setInputParameter("maxPopSize", 
//				(int)properties.getDouble("maxPopSize", 100));
//		algorithm.setInputParameter("initialPopulationSize", 
//				(int)properties.getDouble("initialPopulationSize", 100));
//		algorithm.setInputParameter("maxEvaluations", 
//				(int)properties.getDouble("maxEvaluations", 25000));
//		algorithm.setInputParameter("a", properties.getDouble("a", 20.0));
//		algorithm.setInputParameter("b", properties.getDouble("b", 1.0));
//		algorithm.setInputParameter("c", properties.getDouble("c", 20.0));
//		algorithm.setInputParameter("d", properties.getDouble("d", 0.0));
//		algorithm.setInputParameter("termination", 
//				(int)properties.getDouble("termination", 1));
//
//		setupVariationOperators(algorithm, properties, problem);
//
//		HashMap<String, Object> parameters = new HashMap<String, Object>();
//		parameters.put("comparator", new FPGAFitnessComparator());
//		Operator selection = new BinaryTournament(parameters);
//		
//		algorithm.addOperator("selection", selection);
//
//		return algorithm;
//	}
	
	/**
	 * Returns a new {@link GDE3} instance.  Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code GDE3} instance
	 * @param problem the problem
	 * @return a new {@code GDE3} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newGDE3(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		if (!(adapter instanceof DoubleProblemAdapter)) {
			throw new JMetalException("GDE3 only supports problems with real decision variables");
		}
		
		DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover(
				properties.getDouble("de.crossoverRate", 0.1),
				properties.getDouble("de.stepSize", 0.5),
				properties.getString("de.variant", "rand/1/bin"));
				
		GDE3Builder builder = new GDE3Builder((DoubleProblemAdapter)adapter)
		        .setCrossover(crossover)
		        .setSelection(new DifferentialEvolutionSelection())
		        .setMaxEvaluations((int)properties.getDouble("maxEvaluations", 25000))
		        .setPopulationSize((int)properties.getDouble("populationSize", 100));
		
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

	/**
	 * Returns a new {@link IBEA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code IBEA} instance
	 * @param problem the problem
	 * @return a new {@code IBEA} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newIBEA(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		CrossoverOperator<?> crossover = JMetalFactory.getInstance().createCrossoverOperator(adapter, properties);
		MutationOperator<?> mutation = JMetalFactory.getInstance().createMutationOperator(adapter, properties);
	    SelectionOperator selection = new BinaryTournamentSelection();
	    
		IBEA algorithm = new IBEA(adapter,
				(int)properties.getDouble("populationSize", 100),
				(int)properties.getDouble("archiveSize", 100),
				(int)properties.getDouble("maxEvaluations", 25000),
				selection,
				crossover,
				mutation);
		
	    return new JMetalAlgorithmAdapter(algorithm,
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

	/**
	 * Returns a new {@code MOCell} instance.
	 * 
	 * @param properties the properties for customizing the new {@code MOCell} instance
	 * @param problem the problem
	 * @return a new {@code MOCell} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newMOCell(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		if (properties.contains("feedback")) {
			System.err.println("Warning: Parameter 'feedback' is no longer supported in MOCell (JMetal)");
		}
		
		CrossoverOperator<?> crossover = JMetalFactory.getInstance().createCrossoverOperator(adapter, properties);
		MutationOperator<?> mutation = JMetalFactory.getInstance().createMutationOperator(adapter, properties);
	    SelectionOperator selection = new BinaryTournamentSelection(new RankingAndCrowdingDistanceComparator());
		
	    int maxEvaluations = (int)properties.getDouble("maxEvaluations", 25000);
		int populationSize = (int)properties.getDouble("populationSize", 100);
		int archiveSize = (int)properties.getDouble("archiveSize", 100);
		
		MOCellBuilder builder = new MOCellBuilder(adapter, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxEvaluations(maxEvaluations)
		        .setPopulationSize(populationSize)
		        .setArchive(new CrowdingDistanceArchive(archiveSize));
        
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

	/**
	 * Returns a new {@link MOCHC} instance. Only binary encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code MOCHC} instance
	 * @param problem the problem
	 * @return a new {@code MOCHC} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newMOCHC(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		if (!(adapter instanceof BinaryProblemAdapter)) {
			throw new JMetalException("MOCHC only supports problems with binary decision variables");
		}
		
		int populationSize = (int)properties.getDouble("populationSize", 100);
		
		HUXCrossover crossover = new HUXCrossover(properties.getDouble("hux.rate", 1.0));
	    SelectionOperator parentSelection = new RandomSelection<BinarySolution>();
	    SelectionOperator newGenerationSelection = new RankingAndCrowdingSelection<BinarySolution>(populationSize);
	    BitFlipMutation mutation = new BitFlipMutation(properties.getDouble("bf.rate", 0.35));

	    MOCHCBuilder builder = new MOCHCBuilder((BinaryProblemAdapter)adapter)
	            .setInitialConvergenceCount(properties.getDouble("initialConvergenceCount", 0.25))
	            .setConvergenceValue((int)properties.getDouble("convergenceValue", 3))
	            .setPreservedPopulation(properties.getDouble("preservedPopulation", 0.05))
	            .setMaxEvaluations((int)properties.getDouble("maxEvaluations", 25000))
	            .setPopulationSize(populationSize)
	            .setCrossover(crossover)
	            .setNewGenerationSelection(newGenerationSelection)
	            .setCataclysmicMutation(mutation)
	            .setParentSelection(parentSelection);
	    
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}
	
	/**
	 * Returns a new {@link MOEAD} instance. Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code MOEAD} instance
	 * @param problem the problem
	 * @return a new {@code MOEAD} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Algorithm newMOEAD(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		if (!(adapter instanceof DoubleProblemAdapter)) {
			throw new JMetalException("MOEAD only supports problems with real decision variables");
		}
		
		MOEADBuilder.Variant variant = MOEADBuilder.Variant.MOEAD;
		
		if (properties.contains("variant")) {
			variant = MOEADBuilder.Variant.valueOf(properties.getString("variant", null));
		}
		
		FunctionType functionType = FunctionType.valueOf(properties.getString("functionType", "TCHE"));
		
		if (variant == MOEADBuilder.Variant.MOEADD && !properties.contains("functionType")) {
			functionType = FunctionType.PBI;
		}
		
		DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover(
				properties.getDouble("de.crossoverRate", 0.1),
				properties.getDouble("de.stepSize", 0.5),
				properties.getString("de.variant", "rand/1/bin"));
		
		MutationOperator mutation = JMetalFactory.getInstance().createMutationOperator(adapter, properties);

		MOEADBuilder builder = new MOEADBuilder((DoubleProblemAdapter)adapter, MOEADBuilder.Variant.MOEAD)
	            .setCrossover(crossover)
	            .setMutation(mutation)
	            .setMaxEvaluations((int)properties.getDouble("maxEvaluations", 25000))
	            .setPopulationSize((int)properties.getDouble("populationSize", 100))
	            .setResultPopulationSize((int)properties.getDouble("archiveSize", 100))
	            .setNeighborhoodSelectionProbability(properties.getDouble("delta", 0.9))
	            .setMaximumNumberOfReplacedSolutions(properties.getInt("eta", 2))
	            .setNeighborSize((int)properties.getDouble("neighborhoodSize", 20))
	            .setFunctionType(functionType)
				.setDataDirectory(properties.getString("dataDirectory", "MOEAD_Weights"));
	    
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}
	
	/**
	 * Returns a new {@link NSGAII} instance.
	 * 
	 * @param properties the properties for customizing the new {@code NSGAII} instance
	 * @param problem the problem
	 * @return a new {@code NSGAII} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Algorithm newNSGAII(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		CrossoverOperator<?> crossover = JMetalFactory.getInstance().createCrossoverOperator(adapter, properties);
		MutationOperator<?> mutation = JMetalFactory.getInstance().createMutationOperator(adapter, properties);
		SelectionOperator selection = new BinaryTournamentSelection();
		
		int maxEvaluations = (int)properties.getDouble("maxEvaluations", 25000);
		int populationSize = (int)properties.getDouble("populationSize", 100);
		
		NSGAIIBuilder builder = new NSGAIIBuilder(adapter, crossover, mutation, populationSize)
				.setSelectionOperator(selection)
				.setMaxEvaluations(maxEvaluations);
        
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}
	
	/**
	 * Returns a new {@link NSGAIII} instance.
	 * 
	 * @param properties the properties for customizing the new {@code NSGAIII} instance
	 * @param problem the problem
	 * @return a new {@code NSGAIII} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newNSGAIII(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		CrossoverOperator<?> crossover = JMetalFactory.getInstance().createCrossoverOperator(adapter, properties);
		MutationOperator<?> mutation = JMetalFactory.getInstance().createMutationOperator(adapter, properties);
		SelectionOperator selection = new BinaryTournamentSelection();

	    int maxEvaluations = (int)properties.getDouble("maxEvaluations", 25000);
	    int populationSize = (int)properties.getDouble("populationSize", 100);

		NSGAIIIBuilder builder = new NSGAIIIBuilder(adapter)
				.setCrossoverOperator(crossover)
				.setMutationOperator(mutation)
				.setSelectionOperator(selection)
				.setPopulationSize(populationSize)
				.setMaxIterations(maxEvaluations / populationSize);

	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}
	
	/**
	 * Returns a new {@link OMOPSO} instance. Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code OMOPSO} instance
	 * @param problem the problem
	 * @return a new {@code OMOPSO} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newOMOPSO(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		if (!(adapter instanceof DoubleProblemAdapter)) {
			throw new JMetalException("OMOPSO only supports problems with real decision variables");
		}
		
		if (properties.contains("epsilon")) {
			System.err.println("Warning: Parameter 'epsilon' is no longer supported in OMOPSO (JMetal)");
		}
		
		int populationSize = (int)properties.getDouble("populationSize", 100);
		int archiveSize = (int)properties.getDouble("archiveSize", 100);
		int maxIterations = (int)properties.getDouble("maxEvaluations", 25000) / populationSize;
		double mutationProbability = 1.0 / problem.getNumberOfVariables();
		
		UniformMutation uniformMutation = new UniformMutation(
				properties.getDouble("mutationProbability", mutationProbability),
				properties.getDouble("perturbationIndex", 0.5));
		
		NonUniformMutation nonUniformMutation = new NonUniformMutation(
				properties.getDouble("mutationProbability", mutationProbability),
				properties.getDouble("perturbationIndex", 0.5),
				maxIterations);
		
		SolutionListEvaluator evaluator = new SequentialSolutionListEvaluator();
		
		OMOPSOBuilder builder = new OMOPSOBuilder((DoubleProblemAdapter)adapter, evaluator)
        		.setMaxIterations(maxIterations)
        		.setSwarmSize(populationSize)
        		.setArchiveSize(archiveSize)
        		.setUniformMutation(uniformMutation)
        		.setNonUniformMutation(nonUniformMutation);
		
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

	/**
	 * Returns a new {@link PAES} instance.
	 * 
	 * @param properties the properties for customizing the new {@code PAES} instance
	 * @param problem the problem
	 * @return a new {@code PAES} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Algorithm newPAES(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		MutationOperator<?> mutation = JMetalFactory.getInstance().createMutationOperator(adapter, properties);
		
		PAESBuilder builder = new PAESBuilder(adapter)
		        .setMutationOperator(mutation)
		        .setMaxEvaluations((int)properties.getDouble("maxEvaluations", 25000))
		        .setArchiveSize((int)properties.getDouble("archiveSize", 100))
		        .setBiSections((int)properties.getDouble("bisections", 8));
		
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

	/**
	 * Returns a new {@link PESA2} instance.
	 * 
	 * @param properties the properties for customizing the new {@code PESA2} instance
	 * @param problem the problem
	 * @return a new {@code PESA2} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newPESA2(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		CrossoverOperator<?> crossover = JMetalFactory.getInstance().createCrossoverOperator(adapter, properties);
		MutationOperator<?> mutation = JMetalFactory.getInstance().createMutationOperator(adapter, properties);
		
        PESA2Builder builder = new PESA2Builder(adapter, crossover, mutation)
		        .setMaxEvaluations((int)properties.getDouble("maxEvaluations", 25000))
		        .setPopulationSize((int)properties.getDouble("populationSize", 10))
		        .setArchiveSize((int)properties.getDouble("archiveSize", 100))
		        .setBisections((int)properties.getDouble("bisections", 8));
        
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

	/**
	 * Returns a new {@link SMPSO} instance. Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code SMPSO} instance
	 * @param problem the problem
	 * @return a new {@code SMPSO} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Algorithm newSMPSO(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		if (!(adapter instanceof DoubleProblemAdapter)) {
			throw new JMetalException("SMPSO only supports problems with real decision variables");
		}
		
		MutationOperator<DoubleSolution> mutation = (MutationOperator<DoubleSolution>)
				JMetalFactory.getInstance().createMutationOperator(adapter, properties);
		
		int populationSize = (int)properties.getDouble("populationSize", 100);
		int archiveSize = (int)properties.getDouble("archiveSize", 100);
		int maxIterations = (int)properties.getDouble("maxEvaluations", 25000) / populationSize;

	    BoundedArchive archive = new CrowdingDistanceArchive(archiveSize);
		
		SMPSOBuilder builder = new SMPSOBuilder((DoubleProblemAdapter)adapter, archive)
		        .setMutation(mutation)
		        .setMaxIterations(maxIterations)
		        .setSwarmSize(populationSize);
		
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

	/**
	 * Returns a new {@link SPEA2} instance.
	 * 
	 * @param properties the properties for customizing the new {@code SPEA2} instance
	 * @param problem the problem
	 * @return a new {@code SPEA2} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Algorithm newSPEA2(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		CrossoverOperator<?> crossover = JMetalFactory.getInstance().createCrossoverOperator(adapter, properties);
		MutationOperator<?> mutation = JMetalFactory.getInstance().createMutationOperator(adapter, properties);
		SelectionOperator selection = new BinaryTournamentSelection();
		
		int maxEvaluations = (int)properties.getDouble("maxEvaluations", 25000);
		int populationSize = (int)properties.getDouble("populationSize", 100);
		
		SPEA2Builder builder = new SPEA2Builder(adapter, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxIterations(maxEvaluations / populationSize)
		        .setPopulationSize(populationSize)
		        .setK(properties.getInt("k", 1));
		
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

	/**
	 * Returns a new {@link SMSEMOA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code SMSEMOA} instance
	 * @param problem the problem
	 * @return a new {@code SMSEMOA} instance
	 * @throws JMetalException if an error occurred when constructing the algorithm
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Algorithm newSMSEMOA(TypedProperties properties, Problem problem) throws JMetalException {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(problem);
		
		CrossoverOperator<?> crossover = JMetalFactory.getInstance().createCrossoverOperator(adapter, properties);
		MutationOperator<?> mutation = JMetalFactory.getInstance().createMutationOperator(adapter, properties);
		SelectionOperator selection = new RandomSelection();
		
	    Hypervolume hypervolume = new PISAHypervolume();
	    hypervolume.setOffset(properties.getDouble("offset", 100.0));
	    
	    SMSEMOABuilder builder = new SMSEMOABuilder(adapter, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxEvaluations((int)properties.getDouble("maxEvaluations", 25000))
		        .setPopulationSize((int)properties.getDouble("populationSize", 100))
		        .setHypervolumeImplementation(hypervolume);
        
	    return new JMetalAlgorithmAdapter(builder.build(),
				(int)properties.getDouble("maxEvaluations", 25000),
				adapter);
	}

}
