/* Copyright 2009-2015 David Hadka
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.moeaframework.analysis.sensitivity.ResultEntry;
import org.moeaframework.analysis.sensitivity.ResultFileReader;
import org.moeaframework.analysis.sensitivity.ResultFileWriter;
import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.AdditiveEpsilonIndicator;
import org.moeaframework.core.indicator.Contribution;
import org.moeaframework.core.indicator.GenerationalDistance;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.indicator.InvertedGenerationalDistance;
import org.moeaframework.core.indicator.MaximumParetoFrontError;
import org.moeaframework.core.indicator.Spacing;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.io.FileUtils;
import org.moeaframework.util.statistics.KruskalWallisTest;
import org.moeaframework.util.statistics.MannWhitneyUTest;

/**
 * Performs basic end-of-run analysis.  For example, the following demonstrates
 * its typical use.  First construct and configure the analyzer:
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
 *   add("NSGAII", executor.withAlgorithm("NSGAII").run());
 *   add("eMOEA", executor.withAlgorithm("eMOEA").run());
 * </pre>
 * Lastly, print the results of the analysis:
 * <pre>
 *   analyzer.printAnalysis();
 * </pre>
 * The output produced is compatible with the 
 * <a href="http://yaml.org/">YAML</a> format, and thus can be postprocessed
 * easily with any YAML parser.
 */
public class Analyzer extends ProblemBuilder {
	
	/**
	 * {@code true} if the hypervolume metric is to be computed; {@code false}
	 * otherwise.
	 */
	private boolean includeHypervolume;
	
	/**
	 * {@code true} if the generational distance metric is to be computed; 
	 * {@code false} otherwise.
	 */
	private boolean includeGenerationalDistance;
	
	/**
	 * {@code true} if the inverted generational distance metric is to be 
	 * computed; {@code false} otherwise.
	 */
	private boolean includeInvertedGenerationalDistance;
	
	/**
	 * {@code true} if the additive &epsilon;-indicator metric is to be 
	 * computed; {@code false} otherwise.
	 */
	private boolean includeAdditiveEpsilonIndicator;
	
	/**
	 * {@code true} if the spacing metric is to be computed; {@code false}
	 * otherwise.
	 */
	private boolean includeSpacing;
	
	/**
	 * {@code true} if the maximum Pareto front error metric is to be 
	 * computed; {@code false} otherwise.
	 */
	private boolean includeMaximumParetoFrontError;
	
	/**
	 * {@code true} if the contribution of each approximation set to the
	 * reference set is to be computed; {@code false} otherwise.
	 */
	private boolean includeContribution;
	
	/**
	 * {@code true} if the individual values for each seed are shown;
	 * {@code false} otherwise.
	 */
	private boolean showIndividualValues;
	
	/**
	 * {@code true} if the metric values for the aggregate approximation set 
	 * (across all seeds) is to be calculated; {@code false} otherwise.
	 */
	private boolean showAggregate;
	
	/**
	 * {@code true} if the statistical significance of all metrics is to be
	 * calculated; {@code false} otherwise.  If {@code true}, it is necessary
	 * to record multiple seeds for each entry.
	 */
	private boolean showStatisticalSignificance;
	
	/**
	 * The level of significance used when testing the statistical significance
	 * of observed differences in the medians.
	 */
	private double significanceLevel;
	
	/**
	 * The {@link UnivariateStatistic}s used during the analysis.  If none are
	 * specified by the user, then {@link Min}, {@link Median} and {@link Max}
	 * are used.
	 */
	private List<UnivariateStatistic> statistics;
	
	/**
	 * The collection of end-of-run approximation sets.
	 */
	private Map<String, List<NondominatedPopulation>> data;
	
	/**
	 * Constructs a new analyzer initialized with default settings.
	 */
	public Analyzer() {
		super();
		
		significanceLevel = 0.05;
		statistics = new ArrayList<UnivariateStatistic>();
		data = new HashMap<String, List<NondominatedPopulation>>();
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
	public Analyzer withProblemClass(Class<?> problemClass, 
			Object... problemArguments) {
		return (Analyzer)super.withProblemClass(problemClass, problemArguments);
	}

	@Override
	public Analyzer withProblemClass(String problemClassName, 
			Object... problemArguments) throws ClassNotFoundException {
		return (Analyzer)super.withProblemClass(problemClassName,
				problemArguments);
	}
	
	@Override
	public Analyzer withEpsilon(double... epsilon) {
		return (Analyzer)super.withEpsilon(epsilon);
	}
	
	@Override
	public Analyzer withReferenceSet(File referenceSetFile) {
		return (Analyzer)super.withReferenceSet(referenceSetFile);
	}
	
	/**
	 * Enables the evaluation of the hypervolume metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeHypervolume() {
		includeHypervolume = true;
		
		return this;
	}
	
	/**
	 * Enables the evaluation of the generational distance metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeGenerationalDistance() {
		includeGenerationalDistance = true;
		
		return this;
	}
	
	/**
	 * Enables the evaluation of the inverted generational distance metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeInvertedGenerationalDistance() {
		includeInvertedGenerationalDistance = true;
		
		return this;
	}
	
	/**
	 * Enables the evaluation of the additive &epsilon;-indicator metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeAdditiveEpsilonIndicator() {
		includeAdditiveEpsilonIndicator = true;
		
		return this;
	}

	/**
	 * Enables the evaluation of the maximum Pareto front error metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeMaximumParetoFrontError() {
		includeMaximumParetoFrontError = true;
		
		return this;
	}
	
	/**
	 * Enables the evaluation of the spacing metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeSpacing() {
		includeSpacing = true;
		
		return this;
	}
	
	/**
	 * Enables the evaluation of the contribution metric.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer includeContribution() {
		includeContribution = true;
		
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
		includeInvertedGenerationalDistance();
		includeAdditiveEpsilonIndicator();
		includeMaximumParetoFrontError();
		includeSpacing();
		includeContribution();
		
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
	 * Enables the output of the metric value of the aggregate approximation
	 * set, produced by merging all individual seeds.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer showAggregate() {
		showAggregate = true;
		
		return this;
	}
	
	/**
	 * Enables the output of statistical significance tests.  If enabled, it is
	 * necessary to record multiple seeds for each entry.
	 * 
	 * @return a reference to this analyzer
	 */
	public Analyzer showStatisticalSignificance() {
		showStatisticalSignificance = true;
		
		return this;
	}
	
	/**
	 * Specifies the {@link UnivariateStatistic}s calculated during the 
	 * analysis.  If none are specified by the user, then {@link Min}, 
	 * {@link Median} and {@link Max} are used.
	 * 
	 * @param statistic the statistic to calculate
	 * @return a reference to this analyzer
	 */
	public Analyzer showStatistic(UnivariateStatistic statistic) {
		statistics.add(statistic);
		
		return this;
	}
	
	/**
	 * Sets the level of significance used when testing the statistical 
	 * significance of observed differences in the medians.  Commonly used
	 * levels of significance are {@code 0.05} and {@code 0.01}.
	 * 
	 * @param significanceLevel the level of significance
	 * @return a reference to this analyzer
	 */
	public Analyzer withSignifianceLevel(double significanceLevel) {
		this.significanceLevel = significanceLevel;
		
		return this;
	}
	
	/**
	 * Adds the collection of new samples with the specified name.
	 * 
	 * @param name the name of these samples
	 * @param results the approximation sets
	 * @return a reference to this analyzer
	 */
	public Analyzer addAll(String name, Collection<NondominatedPopulation> results) {
		for (NondominatedPopulation result : results) {
			add(name, result);
		}
		
		return this;
	}
	
	/**
	 * Adds a new sample with the specified name.  If multiple samples are
	 * added using the same name, each sample is treated as an individual
	 * seed.  Analyses can be performed on both the individual seeds and
	 * aggregates of the seeds.
	 * 
	 * @param name the name of this sample
	 * @param result the approximation set
	 * @return a reference to this analyzer
	 */
	public Analyzer add(String name, NondominatedPopulation result) {
		List<NondominatedPopulation> list = data.get(name);
		
		if (list == null) {
			list = new ArrayList<NondominatedPopulation>();
			data.put(name, list);
		}
		
		list.add(result);
		
		return this;
	}
	
	/**
	 * Saves all data stored in this analyzer, which can subsequently be read
	 * using {@link #loadData(File, String, String)} with matching arguments.
	 * 
	 * @param directory the directory in which the data is stored
	 * @param prefix the prefix for filenames
	 * @param suffix the suffix (extension) for filenames
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer saveData(File directory, String prefix, String suffix) 
	throws IOException {
		FileUtils.mkdir(directory);

		for (String algorithm : data.keySet()) {
			saveAs(algorithm, new File(directory, prefix + algorithm + 
					suffix));
		}
		
		return this;
	}
	
	/**
	 * Loads data into this analyzer, which was previously saved using
	 * {@link #saveData(File, String, String)} with matching arguments.
	 * 
	 * @param directory the directory in which the data is stored
	 * @param prefix the prefix for filenames
	 * @param suffix the suffix (extension) for filenames
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer loadData(File directory, String prefix, String suffix) 
	throws IOException {
		for (File file : directory.listFiles()) {
			String filename = file.getName();

			if (filename.startsWith(prefix) && filename.endsWith(suffix)) {
				String name = filename.substring(prefix.length(), 
						filename.length()-suffix.length());

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
		Problem problem = null;
		ResultFileReader reader = null;
		
		try {
			problem = getProblemInstance();

			try {
				reader = new ResultFileReader(problem, resultFile);
						
				while (reader.hasNext()) {
					add(name, reader.next().getPopulation());
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
		
		return this;
	}
	
	/**
	 * Saves the samples to a result file using {@link ResultFileWriter}.  If
	 * {@code name} is {@code null}, the reference set is saved.  Otherwise,
	 * the approximation sets for the named entries are saved.
	 * 
	 * @param name the name of the samples
	 * @param resultFile the result file to which the data is saved
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer saveAs(String name, File resultFile) throws IOException {
		Problem problem = null;
		ResultFileWriter writer = null;
		
		try {
			problem = getProblemInstance();
			
			//delete the file to avoid appending
			FileUtils.delete(resultFile);

			try {
				writer = new ResultFileWriter(problem, resultFile);
				
				if (name == null) {
					writer.append(new ResultEntry(getReferenceSet()));
				} else {
					for (NondominatedPopulation result : data.get(name)) {
						writer.append(new ResultEntry(result));
					}
				}
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
		
		return this;
	}
	
	/**
	 * Saves the analysis of all data recorded in this analyzer to the
	 * specified file.
	 * 
	 * @param file the file to which the analysis is saved
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer saveAnalysis(File file) throws IOException {
		PrintStream ps = null;
		
		try {
			ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(
					file)));
			
			printAnalysis(ps);
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
		
		return this;
	}
	
	/**
	 * Prints the analysis of all data recorded in this analyzer to standard
	 * output.
	 * 
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer printAnalysis() throws IOException {
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
		PopulationIO.writeObjectives(file, getReferenceSet());
		
		return this;
	}
	
	/**
	 * Returns the reference set used by this analyzer.  The reference set is
	 * generated as follows:
	 * <ol>
	 *   <li>If {@link #withReferenceSet(File)} has been set, the contents of 
	 *       the reference set file are returned;
	 *   <li>If the problem factory provides a reference set via the
	 *       {@link ProblemFactory#getReferenceSet(String)} method, this
	 *       reference set is returned;
	 *   <li>Otherwise, the reference set is aggregated from all individual 
	 *       approximation sets.
	 * </ol>
	 * 
	 * @return the reference set used by this analyzer
	 * @throws IllegalArgumentException if the reference set could not be loaded
	 */
	public NondominatedPopulation getReferenceSet() {
		try {
			return super.getReferenceSet();
		} catch (IllegalArgumentException e) {
			if (referenceSetFile == null) {
				//return the combination of all approximation sets
				NondominatedPopulation referenceSet = newArchive();
				
				for (List<NondominatedPopulation> entry : data.values()) {
					for (NondominatedPopulation set : entry) {
						referenceSet.addAll(set);
					}
				}
				
				return referenceSet;
			} else {
				throw e;
			}
		}
	}
	
	/**
	 * Prints the analysis of all data recorded in this analyzer.  
	 * 
	 * @param ps the stream to which the analysis is written
	 * @return a reference to this analyzer
	 * @throws IOException if an I/O error occurred
	 */
	public Analyzer printAnalysis(PrintStream ps) throws IOException {
		if (data.isEmpty()) {
			return this;
		}
		
		Problem problem = null;
		
		try {
			problem = getProblemInstance();
			
			//instantiate the reference set
			NondominatedPopulation referenceSet = getReferenceSet();
			
			//setup the quality indicators
			List<Indicator> indicators = new ArrayList<Indicator>();
			
			if (includeHypervolume) {
				indicators.add(new Hypervolume(problem, referenceSet));
			}
			
			if (includeGenerationalDistance) {
				indicators.add(new GenerationalDistance(problem, referenceSet));
			}
			
			if (includeInvertedGenerationalDistance) {
				indicators.add(new InvertedGenerationalDistance(problem, 
						referenceSet));
			}
			
			if (includeAdditiveEpsilonIndicator) {
				indicators.add(new AdditiveEpsilonIndicator(problem, 
						referenceSet));
			}
			
			if (includeMaximumParetoFrontError) {
				indicators.add(new MaximumParetoFrontError(problem, 
						referenceSet));
			}
			
			if (includeSpacing) {
				indicators.add(new Spacing(problem));
			}
			
			if (includeContribution) {
				if (epsilon == null) {
					indicators.add(new Contribution(referenceSet));
				} else {
					indicators.add(new Contribution(referenceSet, epsilon));
				}
			}
			
			if (indicators.isEmpty()) {
				System.err.println("no indicators selected");
				return this;
			}
			
			//generate the aggregate sets
			Map<String, NondominatedPopulation> aggregateSets =
					new HashMap<String, NondominatedPopulation>();
			
			if (showAggregate) {
				for (String algorithm : data.keySet()) {
					NondominatedPopulation aggregateSet = newArchive();
					
					for (NondominatedPopulation set : data.get(algorithm)) {
						aggregateSet.addAll(set);
					}
					
					aggregateSets.put(algorithm, aggregateSet);
				}
			}
			
			//precompute the individual seed metrics, as they are used both
			//for descriptive statistics and statistical significance tests
			Map<String, Map<Indicator, double[]>> metrics = 
					new HashMap<String, Map<Indicator, double[]>>();
			
			for (String algorithm : data.keySet()) {
				Map<Indicator, double[]> entry = 
						new HashMap<Indicator, double[]>();
				
				for (Indicator indicator : indicators) {
					List<NondominatedPopulation> sets = data.get(algorithm);
					double[] values = new double[sets.size()];
					
					for (int i=0; i<sets.size(); i++) {
						values[i] = indicator.evaluate(sets.get(i));
					}
					
					entry.put(indicator, values);
				}
				
				metrics.put(algorithm, entry);
			}
			
			//precompute the statistical significance of the medians
			Map<Indicator, Map<String, List<String>>> indifferences =
					new HashMap<Indicator, Map<String, List<String>>>();
			
			if (showStatisticalSignificance) {
				List<String> algorithms = new ArrayList<String>(
						metrics.keySet());
				
				//initialize the storage
				for (Indicator indicator : indicators) {
					HashMap<String, List<String>> entry = 
							new HashMap<String, List<String>>();
					
					for (String algorithm : algorithms) {
						entry.put(algorithm, new ArrayList<String>());
					}
					
					indifferences.put(indicator, entry);
				}
				
				for (Indicator indicator : indicators) {
					//insufficient number of samples, skip test
					if (algorithms.size() < 2) {
						continue;
					}
					
					KruskalWallisTest kwTest = new KruskalWallisTest(
							algorithms.size());
					
					for (int i=0; i<algorithms.size(); i++) {
						kwTest.addAll(metrics.get(algorithms.get(i))
								.get(indicator), i);
					}
					
					try {
						if (!kwTest.test(significanceLevel)) {
							for (int i=0; i<algorithms.size()-1; i++) {
								for (int j=i+1; j<algorithms.size(); j++) {
									indifferences.get(indicator)
											.get(algorithms.get(i))
											.add(algorithms.get(j));
									indifferences.get(indicator)
											.get(algorithms.get(j))
											.add(algorithms.get(i));
								}
							}
						} else {
							for (int i=0; i<algorithms.size()-1; i++) {
								for (int j=i+1; j<algorithms.size(); j++) {
									MannWhitneyUTest mwTest = 
											new MannWhitneyUTest();
									
									mwTest.addAll(metrics.get(algorithms.get(i))
											.get(indicator), 0);
									mwTest.addAll(metrics.get(algorithms.get(j))
											.get(indicator), 1);
									
									if (!mwTest.test(significanceLevel)) {
										indifferences.get(indicator)
												.get(algorithms.get(i))
												.add(algorithms.get(j));
										indifferences.get(indicator)
												.get(algorithms.get(j))
												.add(algorithms.get(i));
									}
								}
							}
						}
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			}
			
			//print the results
			Min min = new Min();
			Max max = new Max();
			Median median = new Median();
			
			for (String algorithm : metrics.keySet()) {
				ps.print(algorithm);
				ps.println(':');
				
				for (Indicator indicator : indicators) {
					double[] values = metrics.get(algorithm).get(indicator);
					
					ps.print("    ");
					ps.print(indicator.getClass().getSimpleName());
					ps.print(": ");
					
					if (values.length == 0) {
						ps.print("null");
					} else if (values.length == 1) {
						ps.print(values[0]);
					} else {
						ps.println();
						
						if (showAggregate) {
							ps.print("        Aggregate: ");
							ps.println(indicator.evaluate(
									aggregateSets.get(algorithm)));
						}
						
						if (statistics.isEmpty()) {
							ps.print("        Min: ");
							ps.println(min.evaluate(values));
							ps.print("        Median: ");
							ps.println(median.evaluate(values));
							ps.print("        Max: ");
							ps.println(max.evaluate(values));
						} else {
							for (UnivariateStatistic statistic : statistics) {
								ps.print("        ");
								ps.print(statistic.getClass().getSimpleName());
								ps.print(": ");
								ps.println(statistic.evaluate(values));
							}
						}
						
						ps.print("        Count: ");
						ps.print(values.length);
						
						if (showStatisticalSignificance) {
							ps.println();
							ps.print("        Indifferent: ");
							ps.print(indifferences.get(indicator)
									.get(algorithm));
						}
						
						if (showIndividualValues) {
							ps.println();
							ps.print("        Values: ");
							ps.print(Arrays.toString(values));
						}
					}
					
					ps.println();
				}
			}
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
		
		return this;
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
	
}
