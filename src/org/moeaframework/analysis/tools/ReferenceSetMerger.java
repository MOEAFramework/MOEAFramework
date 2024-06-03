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
package org.moeaframework.analysis.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.validate.Validate;

/**
 * Utility for merging two or more populations identified by unique sources and determining the contribution of each
 * source to the combined non-dominated population. A command line interface is also provided.
 */
public class ReferenceSetMerger extends CommandLineUtility {

	/**
	 * The combined population, used to discard dominated solutions.
	 */
	private NondominatedPopulation combinedPopulation;

	/**
	 * Map storing the original source populations.
	 */
	private final Map<String, Population> originalPopulations;
	
	/**
	 * Map storing the solutions contributed by each source.
	 */
	private final Map<String, Population> contributedPopulations;
	
	/**
	 * Flag indicating the source populations changed and we must call {@link #update()}.
	 */
	private boolean modified;

	/**
	 * Class constructor for merging populations and determining which contributed to the resulting non-dominated
	 * population.
	 */
	public ReferenceSetMerger() {
		this(new NondominatedPopulation());
	}

	/**
	 * Class constructor for merging population and determining which contributed to the resulting non-dominated
	 * population.
	 * 
	 * @param combinedPopulation an (empty) population for maintaining non-dominated solutions
	 */
	public ReferenceSetMerger(NondominatedPopulation combinedPopulation) {
		super();
		this.combinedPopulation = combinedPopulation;

		originalPopulations = new HashMap<String, Population>();
		contributedPopulations = new HashMap<String, Population>();
	}

	/**
	 * Adds the population from the specified source.
	 * 
	 * @param source the source of the population
	 * @param population the population
	 * @throws IllegalArgumentException if a population has been added previously with the specified source
	 */
	public void add(String source, Population population) {
		if (originalPopulations.containsKey(source)) {
			Validate.that("source", source).fails("A population with the same source has already been added");
		}

		originalPopulations.put(source, population);
		modified = true;
	}
	
	/**
	 * Merges the populations and determines the contribution from each source.
	 */
	void update() {
		if (!modified) {
			return;
		}
		
		combinedPopulation.clear();
		contributedPopulations.clear();
		
		// first pass combines all the solutions to determine the non-dominated front
		for (Population population : originalPopulations.values()) {
			combinedPopulation.addAll(population);
		}
		
		// second pass determine the contribution from each source
		for (String source : originalPopulations.keySet()) {
			Population population = originalPopulations.get(source);
			Population contribution = new Population();
			
			for (Solution solution : population) {
				for (Solution s : combinedPopulation) {
					if (solution.euclideanDistance(s) < Settings.EPS) {
						contribution.add(solution);
						break;
					}
				}
			}
			
			contributedPopulations.put(source, contribution);
		}
		
		modified = false;
	}

	/**
	 * Returns the combined non-dominated population.
	 * 
	 * @return the combined non-dominated population
	 */
	public NondominatedPopulation getCombinedPopulation() {
		update();
		return combinedPopulation;
	}

	/**
	 * Returns the sources that have been added to the combined non-dominated population.
	 * 
	 * @return the sources that have been added to the combined non-dominated population
	 */
	public Set<String> getSources() {
		return originalPopulations.keySet();
	}

	/**
	 * Returns the original population associated with the specified source.
	 * 
	 * @param source the source
	 * @return the original population associated with the specified source
	 */
	public Population getPopulation(String source) {
		Population population = originalPopulations.get(source);
		
		if (population == null) {
			Validate.that("source", source).fails("No source population with the given name found");
		}
		
		return population;
	}

	/**
	 * Returns the solutions in the combined non-dominated population originating from the specified source.
	 * 
	 * @param source the source whose solutions in the combined non-dominated population are returned
	 * @return the solutions in the combined non-dominated population originating from the specified source
	 */
	public Population getContributionFrom(String source) {
		update();
		
		Population contribution = contributedPopulations.get(source);
		
		if (contribution == null) {
			Validate.that("source", source).fails("No source population with the given name found");
		}
		
		return contribution;
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());
		options.addOption(Option.builder("d")
				.longOpt("diff")
				.build());
		
		OptionUtils.addEpsilonOption(options);

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		//use an epsilon-dominance archive if necessary
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);
		
		if (epsilons != null) {
			combinedPopulation = new EpsilonBoxDominanceArchive(epsilons);
		}

		//read the population files
		for (String filename : commandLine.getArgs()) {
			add(filename, Population.loadObjectives(new File(filename)));
		}

		//write combined set to the output file
		if (commandLine.hasOption("output")) {
			getCombinedPopulation().saveObjectives(new File(commandLine.getOptionValue("output")));
		}

		//write diff files
		if (commandLine.hasOption("diff")) {
			for (String filename : commandLine.getArgs()) {
				getContributionFrom(filename).saveObjectives(new File(filename + ".diff"));
			}
		}

		//display contribution of each input to the combined set
		for (String filename : commandLine.getArgs()) {
			System.out.print(filename);
			System.out.print(": ");
			System.out.print(getContributionFrom(filename).size());
			System.out.print(" / ");
			System.out.println(getPopulation(filename).size());
		}
	}
	
	/**
	 * Starts the command line utility for merging two or more sets.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ReferenceSetMerger().start(args);
	}

}
