package org.moeaframework.analysis.sensitivity;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.comparator.EpsilonBoxConstraintComparator;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

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

	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(OptionBuilder
				.withLongOpt("epsilon")
				.hasArg()
				.withArgName("e1,e2,...")
				.withDescription("Epsilon values for epsilon-dominance")
				.create('e'));
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		for (String filename : commandLine.getArgs()) {
			NondominatedPopulation set = new NondominatedPopulation(
					PopulationIO.readObjectives(new File(filename)));
			
			if (commandLine.hasOption("epsilon")) {
				TypedProperties typedProperties = TypedProperties.withProperty(
						"epsilon", commandLine.getOptionValue("epsilon"));
				
				set = new EpsilonBoxDominanceArchive(
						new EpsilonBoxConstraintComparator(
								typedProperties.getDoubleArray("epsilon", 
										null)), set);
			}
			
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
