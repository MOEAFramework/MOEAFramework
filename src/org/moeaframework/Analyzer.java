/* Copyright 2009-2024 David Hadka
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
package org.moeaframework;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Displayable;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.NumberFormatter;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.statistics.KruskalWallisTest;
import org.moeaframework.util.statistics.MannWhitneyUTest;

/**
 * Performs basic end-of-run analysis.  This includes evaluating the selected performance indicators, summarizing the
 * indicator values with descriptive statistics (min, median, max, inter-quartile range, etc.), and determining if
 * the results of each algorithm are statistically similar.
 * <p>
 * For example, the following demonstrates its typical use.  First construct and configure the analyzer:
 * <pre>
 *   Analyzer analyzer = new Analyzer()
 *       .withProblem("DTLZ2_2")
 *       .includeGenerationalDistance()
 *       .includeInvertedGenerationalDistance()
 *       .includeAdditiveEpsilonIndicator()
 *       .includeContribution()
 *       .showAggregate()
 *       .showStatisticalSignificance();
 * </pre>
 * The problem must always be specified.  Next, add the data to be analyzed:
 * <pre>
 *   Executor executor = new Executor().withProblem("DTLZ2_2");
 *   analyzer.add("NSGAII", executor.withAlgorithm("NSGAII").run());
 *   analyzer.add("eMOEA", executor.withAlgorithm("eMOEA").run());
 * </pre>
 * Lastly, print the results of the analysis:
 * <pre>
 *   analyzer.display();
 * </pre>
 */
public class Analyzer extends ProblemBuilder implements Displayable {

	/**
	 * The indicators that have been selected.
	 */
	private final EnumSet<StandardIndicator> selectedIndicators;
	
	/**
	 * The {@link UnivariateStatistic}s used during the analysis.  If none are specified by the user, then the default
	 * statistics are displayed.
	 */
	private final List<UnivariateStatistic> selectedStatistics;

	/**
	 * {@code true} if the individual values for each seed are shown; {@code false} otherwise.
	 */
	private boolean showIndividualValues;

	/**
	 * {@code true} if the metric values for the aggregate approximation set (across all seeds) is to be calculated;
	 * {@code false} otherwise.
	 */
	private boolean showAggregate;

	/**
	 * {@code true} if the statistical significance of all metrics is to be calculated; {@code false} otherwise.
	 * If {@code true}, it is necessary to record multiple seeds for each entry.
	 */
	private boolean showStatisticalSignificance;

	/**
	 * The level of significance used when testing the statistical significance of observed differences in the medians.
	 */
	private double significanceLevel;

	/**
	 * The ideal point to use when normalizing the data; or {@code null} if the ideal point should be derived from the
	 * reference set.
	 */
	private double[] idealPoint;

	/**
	 * The reference point to use when computing the hypervolume metric; or {@code null} if the reference point should
	 * be derived from the reference set.
	 */
	private double[] referencePoint;

	/**
	 * The collection of end-of-run approximation sets.
	 */
	private Map<String, EndOfRunResults> data;

	/**
	 * Constructs a new analyzer initialized with default settings.
	 */
	public Analyzer() {
		super();

		significanceLevel = 0.05;
		selectedIndicators = EnumSet.noneOf(StandardIndicator.class);
		selectedStatistics = new ArrayList<>();
		data = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}

	@Override
	public Analyzer withSameProblemAs(ProblemBuilder builder) {
		return (Analyzer)super.withSameProblemAs(builder);
	}

	@Override
	public Analyzer usingProblemFactory(ProblemFactory problemFactory) {
		return (Analyzer)super.usingProblemFactory(problemFactory);
	}

	@Override
	public Analyzer withProblem(String problemName) {
		return (Analyzer)super.withProblem(problemName);
	}

	@Override
	public Analyzer withProblem(Problem problemInstance) {
		return (Analyzer)super.withProblem(problemInstance);
	}

	@Override
	public Analyzer withProblemClass(Class<?> problemClass, Object... problemArguments) {
		return (Analyzer)super.withProblemClass(problemClass, problemArguments);
	}

	@Override
	public Analyzer withProblemClass(String problemClassName, Object... problemArguments)
			throws ClassNotFoundException {
		return (Analyzer)super.withProblemClass(problemClassName, problemArguments);
	}

	@Override
	public Analyzer withEpsilon(double... epsilon) {
		return (Analyzer)super.withEpsilon(epsilon);
	}

	@Override
	public Analyzer withEpsilons(Epsilons epsilons) {
		return (Analyzer)super.withEpsilons(epsilons);
	}

	@Override
	public Analyzer withReferenceSet(File referenceSetFile) {
		return (Analyzer)super.withReferenceSet(referenceSetFile);
	}
	
	@Override
	public Analyzer withReferenceSet(NondominatedPopulation referenceSet) {
		return (Analyzer)super.withReferenceSet(referenceSet);
	}

	/**
	 * Enables the evaluation of the hypervolume metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeHypervolume() {
		selectedIndicators.add(StandardIndicator.Hypervolume);
		return this;
	}

	/**
	 * Enables the evaluation of the generational distance metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeGenerationalDistance() {
		selectedIndicators.add(StandardIndicator.GenerationalDistance);
		return this;
	}
	
	/**
	 * Enables the evaluation of the generational distance plus metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeGenerationalDistancePlus() {
		selectedIndicators.add(StandardIndicator.GenerationalDistancePlus);
		return this;
	}

	/**
	 * Enables the evaluation of the inverted generational distance metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeInvertedGenerationalDistance() {
		selectedIndicators.add(StandardIndicator.InvertedGenerationalDistance);
		return this;
	}
	
	/**
	 * Enables the evaluation of the inverted generational distance plus metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeInvertedGenerationalDistancePlus() {
		selectedIndicators.add(StandardIndicator.InvertedGenerationalDistancePlus);
		return this;
	}

	/**
	 * Enables the evaluation of the additive &epsilon;-indicator metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeAdditiveEpsilonIndicator() {
		selectedIndicators.add(StandardIndicator.AdditiveEpsilonIndicator);
		return this;
	}

	/**
	 * Enables the evaluation of the maximum Pareto front error metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeMaximumParetoFrontError() {
		selectedIndicators.add(StandardIndicator.MaximumParetoFrontError);
		return this;
	}

	/**
	 * Enables the evaluation of the spacing metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeSpacing() {
		selectedIndicators.add(StandardIndicator.Spacing);
		return this;
	}

	/**
	 * Enables the evaluation of the contribution metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeContribution() {
		selectedIndicators.add(StandardIndicator.Contribution);
		return this;
	}

	/**
	 * Enables the evaluation of the R1 indicator.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeR1() {
		selectedIndicators.add(StandardIndicator.R1Indicator);
		return this;
	}

	/**
	 * Enables the evaluation of the R2 indicator.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeR2() {
		selectedIndicators.add(StandardIndicator.R2Indicator);
		return this;
	}

	/**
	 * Enables the evaluation of the R3 indicator.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeR3() {
		selectedIndicators.add(StandardIndicator.R3Indicator);
		return this;
	}

	/**
	 * Enables the evaluation of all metrics.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeAllMetrics() {
		includeHypervolume();
		includeGenerationalDistance();
		includeGenerationalDistancePlus();
		includeInvertedGenerationalDistance();
		includeInvertedGenerationalDistancePlus();
		includeAdditiveEpsilonIndicator();
		includeMaximumParetoFrontError();
		includeSpacing();
		includeContribution();
		includeR1();
		includeR2();
		includeR3();

		return this;
	}

	/**
	 * Enables the output of all analysis results.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer showAll() {
		showIndividualValues();
		showAggregate();
		showStatisticalSignificance();

		return this;
	}

	/**
	 * Enables the output of individual metric values for each seed.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer showIndividualValues() {
		showIndividualValues = true;
		return this;
	}

	/**
	 * Enables the output of the metric value of the aggregate approximation set, produced by merging all individual
	 * seeds.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer showAggregate() {
		showAggregate = true;
		return this;
	}

	/**
	 * Enables the output of statistical significance tests.  If enabled, it is necessary to record multiple seeds
	 * for each entry.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer showStatisticalSignificance() {
		showStatisticalSignificance = true;
		return this;
	}

	/**
	 * Specifies the {@link UnivariateStatistic}s calculated during the analysis.  If none are specified by the user,
	 * then the default statistics are displayed.
	 * 
	 * @param statistic the statistic to calculate
	 * @return a reference to this analyzer
	 */
	public Analyzer showStatistic(UnivariateStatistic statistic) {
		selectedStatistics.add(statistic);
		return this;
	}

	/**
	 * Sets the level of significance used when testing the statistical significance of observed differences in the
	 * medians.  Commonly used levels of significance are {@code 0.05} and {@code 0.01}.
	 * 
	 * @param significanceLevel the level of significance
	 * @return a reference to this analyzer
	 */
	public Analyzer withSignifianceLevel(double significanceLevel) {
		this.significanceLevel = significanceLevel;
		return this;
	}

	/**
	 * Sets the ideal point used for computing the hypervolume metric.
	 * 
	 * @param idealPoint the ideal point
	 * @return a reference to this analyzer
	 */
	public Analyzer withIdealPoint(double... idealPoint) {
		this.idealPoint = idealPoint;
		return this;
	}

	/**
	 * Sets the reference point used for computing the hypervolume metric.
	 * 
	 * @param referencePoint the reference point
	 * @return a reference to this analyzer
	 */
	public Analyzer withReferencePoint(double... referencePoint) {
		this.referencePoint = referencePoint;
		return this;
	}

	/**
	 * Adds the collection of new samples with the specified name.
	 * 
	 * @param name the name of these samples
	 * @param approximationSets the approximation sets
	 * @return a reference to this analyzer
	 */
	public Analyzer addAll(String name, Collection<NondominatedPopulation> approximationSets) {
		for (NondominatedPopulation approximationSet : approximationSets) {
			add(name, approximationSet);
		}

		return this;
	}

	/**
	 * Adds a new sample with the specified name.  If multiple samples are added using the same name, each sample is
	 * treated as an individual seed.  Analyses can be performed on both the individual seeds and aggregates of the
	 * seeds.
	 * 
	 * @param name the name of this sample
	 * @param approximationSet the approximation set
	 * @return a reference to this analyzer
	 */
	public Analyzer add(String name, NondominatedPopulation approximationSet) {
		EndOfRunResults result = data.get(name);

		if (result == null) {
			result = new EndOfRunResults(name);
			data.put(name, result);
		}

		result.addApproximationSet(approximationSet);

		return this;
	}

	/**
	 * Saves all data stored in this analyzer, which can subsequently be read using
	 * {@link #loadData(File, String, String)} with matching arguments.
	 * 
	 * @param directory the directory in which the data is stored
	 * @param prefix the prefix for filenames
	 * @param suffix the suffix (extension) for filenames
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer saveData(File directory, String prefix, String suffix) throws IOException {
		Files.createDirectories(directory.toPath());

		for (String algorithm : data.keySet()) {
			saveAs(algorithm, new File(directory, prefix + algorithm + suffix));
		}

		return this;
	}

	/**
	 * Loads data into this analyzer, which was previously saved using {@link #saveData(File, String, String)} with
	 * matching arguments.
	 * 
	 * @param directory the directory in which the data is stored
	 * @param prefix the prefix for filenames
	 * @param suffix the suffix (extension) for filenames
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer loadData(File directory, String prefix, String suffix) throws IOException {
		for (File file : directory.listFiles()) {
			String filename = file.getName();

			if (filename.startsWith(prefix) && filename.endsWith(suffix)) {
				String name = filename.substring(prefix.length(), filename.length()-suffix.length());
				loadAs(name, file);
			}
		}

		return this;
	}

	/**
	 * Loads the samples stored in a result file using {@link ResultFileReader}.
	 * 
	 * @param name the name of the samples
	 * @param resultFile the result file to load
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer loadAs(String name, File resultFile) throws IOException {
		try (Problem problem = getProblemInstance()) {
			try (ResultFileReader reader = new ResultFileReader(problem, resultFile)) {	
				while (reader.hasNext()) {
					add(name, reader.next().getPopulation());
				}
			}
		}

		return this;
	}

	/**
	 * Saves the samples to a result file using {@link ResultFileWriter}.  If {@code name} is {@code null}, the
	 * reference set is saved.  Otherwise, the approximation sets for the named entries are saved.
	 * 
	 * @param name the name of the samples
	 * @param resultFile the result file to which the data is saved
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer saveAs(String name, File resultFile) throws IOException {
		try (Problem problem = getProblemInstance()) {	
			try (ResultFileWriter writer = ResultFileWriter.overwrite(problem, resultFile)) {
				if (name == null) {
					writer.append(new ResultEntry(getReferenceSet()));
				} else {
					EndOfRunResults result = data.get(name);

					for (NondominatedPopulation approximationSet : result.getApproximationSets()) {
						writer.append(new ResultEntry(approximationSet));
					}
				}
			}
		}

		return this;
	}

	/**
	 * Saves the analysis of all data recorded in this analyzer to the specified file.
	 * 
	 * @param file the file to which the analysis is saved
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer saveAnalysis(File file) throws IOException {
		try (PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			printAnalysis(ps);
		}

		return this;
	}

	/**
	 * Prints the analysis of all data recorded in this analyzer to standard output.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer printAnalysis() {
		printAnalysis(System.out);
		return this;
	}

	/**
	 * Saves the reference set to the specified file.
	 * 
	 * @param file the file to which the reference set is saved
	 * @return a reference to this analyzer
	 * @see #getReferenceSet()
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer saveReferenceSet(File file) throws IOException {
		getReferenceSet().saveObjectives(file);
		return this;
	}

	/**
	 * Returns the reference set used by this analyzer.  The reference set is generated as follows:
	 * <ol>
	 *   <li>If {@link #withReferenceSet(File)} has been set, the contents of the reference set file are returned;
	 *   <li>If the problem factory provides a reference set via the {@link ProblemFactory#getReferenceSet(String)}
	 *       method, this reference set is returned;
	 *   <li>Otherwise, the reference set is aggregated from all individual approximation sets.
	 * </ol>
	 * 
	 * @return the reference set used by this analyzer
	 * @throws IllegalArgumentException if the reference set could not be loaded
	 */
	public NondominatedPopulation getReferenceSet() {
		try {
			return super.getReferenceSet();
		} catch (IllegalArgumentException e) {
			if (referenceSet == null) {
				//return the combination of all approximation sets
				NondominatedPopulation referenceSet = newArchive();

				for (EndOfRunResults result : data.values()) {
					for (NondominatedPopulation approximationSet : result.getApproximationSets()) {
						referenceSet.addAll(approximationSet);
					}
				}

				return referenceSet;
			} else {
				throw e;
			}
		}
	}

	/**
	 * Generates the analysis of all data recorded in this analyzer.  
	 * 
	 * @return an object storing the results of the analysis
	 */
	public AnalyzerResults getAnalysis() {
		if (data.isEmpty()) {
			return new AnalyzerResults();
		}

		if (!isProblemConfigured()) {
			System.err.println("no problem configured");
			return new AnalyzerResults();
		}

		try (Problem problem = getProblemInstance()) {
			//instantiate the reference set
			NondominatedPopulation referenceSet = getReferenceSet();

			//construct the result object
			AnalyzerResults analyzerResults = new AnalyzerResults(problem, referenceSet, epsilons, selectedIndicators);
			analyzerResults.addAll(data.values());

			//perform the analysis
			analyzerResults.calculateIndicators(showAggregate);

			if (showStatisticalSignificance && data.size() >= 2) {
				analyzerResults.calculateStatisticalSignificance(significanceLevel);
			}

			return analyzerResults;
		}
	}

	/**
	 * Prints the analysis of all data recorded in this analyzer.  
	 * 
	 * @param ps the stream to which the analysis is written
	 * @return a reference to this analyzer
	 */
	public Analyzer printAnalysis(PrintStream ps) {
		display(ps);
		return this;
	}

	@Override
	public void display(PrintStream ps) {
		if (data.isEmpty()) {
			ps.println("-- no data --");
		} else {
			getAnalysis().display(ps);
		}
	}

	/**
	 * Clears all data stored in this analyzer.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer clear() {
		data.clear();
		return this;
	}

	/**
	 * Stores the results produced by this analyzer.
	 */
	public class AnalyzerResults implements Formattable<Pair<String, StandardIndicator>> {
		
		/**
		 * The problem.
		 */
		private final Problem problem;
		
		/**
		 * The reference set used to compute performance indicators.
		 */
		private final NondominatedPopulation referenceSet;
		
		/**
		 * The epsilons used to compute performance indicators, or {@code null} if no epsilon values are configured.
		 */
		private final Epsilons epsilons;
		
		/**
		 * The indicators that were computed in these results.
		 */
		private final EnumSet<StandardIndicator> selectedIndicators;

		/**
		 * The results for each algorithm.
		 */
		private final Map<String, EndOfRunResults> data;
		
		/**
		 * Records the descriptive statistics associated with each end-of-run result.
		 */
		private final Map<String, Map<StandardIndicator, DescriptiveStatistics>> statistics;
		
		/**
		 * Records the indicator values for the aggregate of all end-of-run reslts.
		 */
		private final Map<String, IndicatorValues> aggregates;

		/**
		 * Records which algorithms produced statistically similar results.
		 */
		private final Map<String, Map<StandardIndicator, Set<String>>> similarities;
		
		/**
		 * Constructs a results object indicating no data available.
		 */
		AnalyzerResults() {
			this(null, null, null, EnumSet.noneOf(StandardIndicator.class));
		}

		/**
		 * Constructs a results object for the given problem.  Before returning the constructed object to the caller,
		 * be sure to add data and perform the appropriate calculations.
		 */
		AnalyzerResults(Problem problem, NondominatedPopulation referenceSet, Epsilons epsilons,
				EnumSet<StandardIndicator> selectedIndicators) {
			super();
			this.problem = problem;
			this.referenceSet = referenceSet;
			this.epsilons = epsilons;
			this.selectedIndicators = selectedIndicators;
			
			data = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			statistics = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			aggregates = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			similarities = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		}
		
		/**
		 * Adds a collection of results.  Each result should have a unique algorithm name.
		 * 
		 * @param results the collection of results
		 */
		void addAll(Collection<EndOfRunResults> results) {
			for (EndOfRunResults result : results) {
				add(result);
			}
		}

		/**
		 * Adds the results for an algorithm.  Each result should have a unique algorithm name.  The results are
		 * copied to keep these results distinct from the {@link Analyzer}.
		 * 
		 * @param result the results for the algorithm
		 */
		void add(EndOfRunResults result) {
			data.put(result.getAlgorithmName(), result.copy());
		}
		
		Indicators createIndicators() {
			Indicators indicators = Indicators.of(problem, referenceSet);
			indicators.include(selectedIndicators);

			if (selectedIndicators.contains(StandardIndicator.Hypervolume)) {
				if ((idealPoint != null) && (referencePoint != null)) {
					indicators.withHypervolume(idealPoint, referencePoint);
				} else if (referencePoint != null) {
					indicators.withHypervolume(referencePoint);
				}
			}

			if (epsilons != null) {
				indicators.withEpsilons(epsilons);
			}

			return indicators;
		}
		
		void calculateIndicators(boolean includeAggregate) {
			statistics.clear();
			aggregates.clear();
			
			// Initialize the data structure
			for (String algorithmName : data.keySet()) {
				Map<StandardIndicator, DescriptiveStatistics> map = new HashMap<>();
				
				for (StandardIndicator indicator : selectedIndicators) {
					map.put(indicator, new DescriptiveStatistics());
				}
				
				statistics.put(algorithmName, map);
			}
			
			// Evaluate the performance indicators
			Indicators indicators = createIndicators();
			
			for (String algorithmName : data.keySet()) {
				EndOfRunResults results = data.get(algorithmName);
				
				for (NondominatedPopulation approximationSet : results.getApproximationSets()) {
					IndicatorValues indicatorValues = indicators.apply(approximationSet);
					
					for (StandardIndicator indicator : selectedIndicators) {
						statistics.get(algorithmName).get(indicator).addValue(indicatorValues.get(indicator));
					}
				}
				
				if (includeAggregate) {
					NondominatedPopulation aggregate = results.getAggregate(epsilons);
					aggregates.put(algorithmName, indicators.apply(aggregate));
				}
			}
		}
		
		void calculateStatisticalSignificance(double significanceLevel) {
			similarities.clear();
			
			List<String> algorithms = new ArrayList<String>(getAlgorithms());
			EnumSet<StandardIndicator> indicators = getIndicators();
						
			// Initialize the data structure
			for (String algorithmName : algorithms) {
				Map<StandardIndicator, Set<String>> map = new HashMap<>();
				
				for (StandardIndicator indicator : selectedIndicators) {
					map.put(indicator, new TreeSet<String>(String.CASE_INSENSITIVE_ORDER));
				}
				
				similarities.put(algorithmName, map);
			}
			
			// Perform the statistical tests
			for (StandardIndicator indicator : indicators) {
				try {
					// First use the non-parametric Kruskal-Wallis test to determine if the medians are the same
					// between N groups (the null hypothesis).  Using this single test first results in less error than
					// performing all pairwise tests.
					KruskalWallisTest kwTest = new KruskalWallisTest(algorithms.size());
		
					for (int i = 0; i < algorithms.size(); i++) {
						kwTest.addAll(statistics.get(algorithms.get(i)).get(indicator).getValues(), i);
					}
				
					if (!kwTest.test(significanceLevel)) {
						// No difference detected, add all pairs as similar
						for (int i=0; i<algorithms.size()-1; i++) {
							for (int j=i+1; j<algorithms.size(); j++) {
								similarities.get(algorithms.get(i)).get(indicator).add(algorithms.get(j));
								similarities.get(algorithms.get(j)).get(indicator).add(algorithms.get(i));
							}
						}
					} else {
						// Difference detected, test each pair of algorithms
						for (int i = 0; i < algorithms.size() - 1; i++) {
							for (int j = i + 1; j < algorithms.size(); j++) {
								MannWhitneyUTest mwTest = new MannWhitneyUTest();
								mwTest.addAll(statistics.get(algorithms.get(i)).get(indicator).getValues(), 0);
								mwTest.addAll(statistics.get(algorithms.get(j)).get(indicator).getValues(), 1);

								if (!mwTest.test(significanceLevel)) {
									similarities.get(algorithms.get(i)).get(indicator).add(algorithms.get(j));
									similarities.get(algorithms.get(j)).get(indicator).add(algorithms.get(i));
								}
							}
						}
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Returns the algorithms processed using this analyzer.
		 * 
		 * @return the algorithm names
		 */
		public Set<String> getAlgorithms() {
			return data.keySet();
		}
		
		/**
		 * Returns the indicators that are included in these results.
		 * 
		 * @return the indicators
		 */
		public EnumSet<StandardIndicator> getIndicators() {
			return selectedIndicators.clone();
		}

		/**
		 * Returns the end-of-run results for an algorithm.
		 * 
		 * @param algorithmName the name of the algorithm
		 * @return the results for the given algorithm
		 */
		public EndOfRunResults get(String algorithmName) {
			return data.get(algorithmName);
		}
		
		/**
		 * Returns the descriptive statistics for an algorithm.
		 * 
		 * @param algorithmName the name of the algorithm
		 * @param indicator the indicator
		 * @return the descriptive statistics
		 */
		public DescriptiveStatistics getStatistics(String algorithmName, StandardIndicator indicator) {
			return statistics.get(algorithmName).get(indicator);
		}
		
		/**
		 * Returns a set of algorithm names that produced statistically similar results as the given algorithm.
		 * 
		 * @param algorithmName the name of the algorithm
		 * @param indicator the indicator
		 * @return the algorithms with statistically similar results
		 */
		public Set<String> getSimilarAlgorithms(String algorithmName, StandardIndicator indicator) {
			return Collections.unmodifiableSet(similarities.get(algorithmName).get(indicator));
		}

		@Override
		public TabularData<Pair<String, StandardIndicator>> asTabularData() {
			List<Pair<String, StandardIndicator>> keys = new ArrayList<>();
			
			for (String algorithm : data.keySet()) {
				for (StandardIndicator indicator : selectedIndicators) {
					keys.add(Pair.of(algorithm, indicator));
				}
			}
			
			TabularData<Pair<String, StandardIndicator>> table = new TabularData<>(keys);
			table.addColumn(new Column<Pair<String, StandardIndicator>, String>("Algorithm", x -> x.getKey()));
			table.addColumn(new Column<Pair<String, StandardIndicator>, String>("Indicator", x -> x.getValue().name()));
			
			if (selectedStatistics.isEmpty()) {
				table.addColumn(new Column<Pair<String, StandardIndicator>, Double>("Min",
						x -> getStatistics(x.getKey(), x.getValue()).getMin()));
				table.addColumn(new Column<Pair<String, StandardIndicator>, Double>("Median",
						x -> getStatistics(x.getKey(), x.getValue()).getPercentile(50)));
				table.addColumn(new Column<Pair<String, StandardIndicator>, Double>("Max",
						x -> getStatistics(x.getKey(), x.getValue()).getMax()));
				table.addColumn(new Column<Pair<String, StandardIndicator>, Double>("IQR (+/-)",
						x -> getStatistics(x.getKey(), x.getValue()).getPercentile(75) -
						getStatistics(x.getKey(), x.getValue()).getPercentile(25)));
				table.addColumn(new Column<Pair<String, StandardIndicator>, Long>("Count",
						x -> getStatistics(x.getKey(), x.getValue()).getN()));
			} else {
				for (UnivariateStatistic statistic : selectedStatistics) {
					table.addColumn(new Column<Pair<String, StandardIndicator>, Double>(
							statistic.getClass().getSimpleName(),
							x -> getStatistics(x.getKey(), x.getValue()).apply(statistic)));
				}
			}
			
			if (aggregates.size() > 0) {
				table.addColumn(new Column<Pair<String, StandardIndicator>, Double>("Aggregate",
						x -> aggregates.get(x.getKey()).get(x.getValue())));
			}
			
			if (similarities.size() > 0) {
				table.addColumn(new Column<Pair<String, StandardIndicator>, String>(
						"Statistically Similar (a=" + significanceLevel + ")",
						x -> String.join(", ", getSimilarAlgorithms(x.getKey(), x.getValue()))));
			}
			
			if (showIndividualValues) {
				table.addColumn(new Column<Pair<String, StandardIndicator>, String>("Values",
						x -> DoubleStream.of(getStatistics(x.getKey(), x.getValue()).getValues())
							.mapToObj((double v) -> NumberFormatter.getDefault().format(v))
							.collect(Collectors.joining(", "))));
			}
			
			return table;
		}

	}

	/**
	 * Collection of end-of-run results (i.e., the approximation set) produced by an algorithm across multiple seeds.
	 */
	public class EndOfRunResults {

		private final String algorithmName;

		private final List<NondominatedPopulation> approximationSets;

		/**
		 * Constructs a new end-of-run results object for the given algorithm.
		 * 
		 * @param algorithmName the name of the algorithm
		 */
		public EndOfRunResults(String algorithmName) {
			super();
			this.algorithmName = algorithmName;

			approximationSets = new ArrayList<>();
		}

		/**
		 * Returns the algorithm name.
		 * 
		 * @return the algorithm name
		 */
		public String getAlgorithmName() {
			return algorithmName;
		}

		/**
		 * Adds an end-of-run approximation set to these results.
		 * 
		 * @param approximationSet the end-of-run approximation set
		 */
		public void addApproximationSet(NondominatedPopulation approximationSet) {
			approximationSets.add(approximationSet);
		}

		/**
		 * Returns all end-of-run approximation sets contained within these results.
		 * 
		 * @return the list of end-of-run approximation sets
		 */
		public List<NondominatedPopulation> getApproximationSets() {
			return approximationSets;
		}
		
		/**
		 * Calculates and returns the end-of-run aggregate set for these results.  This aggregate set combines the
		 * individual end-of-run results.
		 * 
		 * @param epsilons the epsilon values, or {@code null} if no epsilons are specified
		 * @return the aggregate set
		 */
		public NondominatedPopulation getAggregate(Epsilons epsilons) {
			NondominatedPopulation aggregate = epsilons == null ?
					new NondominatedPopulation(new ParetoDominanceComparator()) :
					new EpsilonBoxDominanceArchive(epsilons);
			
			for (NondominatedPopulation approximationSet : getApproximationSets()) {
				aggregate.addAll(approximationSet);
			}
			
			return aggregate;
		}
		
		/**
		 * Returns a shallow copy of these results.  Changes made to the {@link Analyzer} will not be reflected in the
		 * returned copy.
		 * 
		 * @return the copy
		 */
		public EndOfRunResults copy() {
			EndOfRunResults copy = new EndOfRunResults(getAlgorithmName());
			
			for (NondominatedPopulation approximationSet : getApproximationSets()) {
				copy.addApproximationSet(approximationSet.copy());
			}
			
			return copy;
		}
		
	}

}
