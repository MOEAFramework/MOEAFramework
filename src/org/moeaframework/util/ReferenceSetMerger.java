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
package org.moeaframework.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.math3.util.MathArrays;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;

/**
 * Utility for merging two or more populations identified by unique sources and
 * determining the contribution of each source to the combined non-dominated
 * population. A command line interface is also provided.
 */
public class ReferenceSetMerger extends CommandLineUtility {

	/**
	 * The attribute key used to store the file from which each solution
	 * originated.
	 */
	public static final String SOURCE_ATTRIBUTE = "source";

	/**
	 * The combined non-dominated population.
	 */
	private NondominatedPopulation combinedPopulation;

	/**
	 * Map storing the original populations.
	 */
	private Map<String, Population> populations;

	/**
	 * Class constructor for merging populations and determining which
	 * contributed to the resulting non-dominated population.
	 */
	public ReferenceSetMerger() {
		this(new NondominatedPopulation());
	}

	/**
	 * Class constructor for merging population and determining which
	 * contributed to the resulting non-dominated population.
	 * 
	 * @param combinedPopulation an (empty) population for maintaining
	 *        non-dominated solutions
	 */
	public ReferenceSetMerger(NondominatedPopulation combinedPopulation) {
		super();
		this.combinedPopulation = combinedPopulation;

		populations = new HashMap<String, Population>();
	}

	/**
	 * Adds the population from the specified source.
	 * 
	 * @param source the source of the population
	 * @param population the population
	 * @throws IllegalArgumentException if a population has been added
	 *         previously with the specified source
	 */
	public void add(String source, Population population) {
		if (populations.containsKey(source)) {
			throw new IllegalArgumentException("source already exists");
		}

		populations.put(source, population);

		for (Solution solution : population) {
			solution.setAttribute(SOURCE_ATTRIBUTE, source);
			
			//print warning if duplicate solutions found
			for (Solution s : combinedPopulation) {
				if (MathArrays.distance(s.getObjectives(), 
						solution.getObjectives()) < Settings.EPS) {
					System.err.println("duplicate solution found");
				}
			}
			
			combinedPopulation.add(solution);
		}
	}

	/**
	 * Returns the combined non-dominated population.
	 * 
	 * @return the combined non-dominated population
	 */
	public NondominatedPopulation getCombinedPopulation() {
		return combinedPopulation;
	}

	/**
	 * Returns the sources that have been added to the combined non-dominated
	 * population.
	 * 
	 * @return the sources that have been added to the combined non-dominated
	 *         population
	 */
	public Set<String> getSources() {
		return populations.keySet();
	}

	/**
	 * Returns the original population associated with the specified source.
	 * 
	 * @param source the source
	 * @return the original population associated with the specified source
	 */
	public Population getPopulation(String source) {
		return populations.get(source);
	}

	/**
	 * Returns the solutions in the combined non-dominated population
	 * originating from the specified source.
	 * 
	 * @param source the source whose solutions in the combined non-dominated
	 *        population are returned
	 * @return the solutions in the combined non-dominated population
	 *         originating from the specified source
	 */
	public NondominatedPopulation getContributionFrom(String source) {
		NondominatedPopulation result = new NondominatedPopulation();

		for (Solution solution : combinedPopulation) {
			if (solution.getAttribute(SOURCE_ATTRIBUTE).equals(source)) {
				result.add(solution);
			}
		}

		return result;
	}

	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(OptionBuilder
				.withLongOpt("output")
				.hasArg()
				.withArgName("file")
				.create('o'));
		options.addOption(OptionBuilder
				.withLongOpt("epsilon")
				.hasArg()
				.withArgName("e1,e2,...")
				.create('e'));
		options.addOption(OptionBuilder
				.withLongOpt("diff")
				.create('d'));

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		//use an epsilon-dominance archive if necessary
		if (commandLine.hasOption("epsilon")) {
			TypedProperties properties = TypedProperties.withProperty(
					"epsilon", commandLine.getOptionValue("epsilon"));
			
			combinedPopulation = new EpsilonBoxDominanceArchive(
					properties.getDoubleArray("epsilon", null));
		}

		//read the population files
		for (String filename : commandLine.getArgs()) {
			add(filename, PopulationIO.readObjectives(new File(filename)));
		}

		//write combined set to the output file
		if (commandLine.hasOption("output")) {
			PopulationIO.writeObjectives(new File(commandLine
					.getOptionValue("output")), getCombinedPopulation());
		}

		//write diff files
		if (commandLine.hasOption("diff")) {
			for (String filename : commandLine.getArgs()) {
				PopulationIO.writeObjectives(new File(filename + ".diff"), 
						getContributionFrom(filename));
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
