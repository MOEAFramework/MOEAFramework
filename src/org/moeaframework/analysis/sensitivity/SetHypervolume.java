package org.moeaframework.analysis.sensitivity;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for calculating the hypervolume of approximation sets.
 */
public class SetHypervolume extends CommandLineUtility {
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private SetHypervolume() {
		super();
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		for (String filename : commandLine.getArgs()) {
			NondominatedPopulation set = new NondominatedPopulation(
					PopulationIO.readObjectives(new File(filename)));
			
			System.out.print(filename);
			System.out.print(' ');
			System.out.println(new Hypervolume(new ProblemStub(
					set.get(0).getNumberOfObjectives()), set).evaluate(set));
		}
	}
	
	/**
	 * Starts the command line utility for calculating the hypervolume of 
	 * approximation sets.
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new SetHypervolume().start(args);
	}

}
