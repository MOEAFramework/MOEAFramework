package org.moeaframework.analysis.sensitivity;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.TypedProperties;

public class OptionUtils {
	
	private OptionUtils() {
		super();
	}
	
	public static void addProblemOptionGroup(Options options) {
		OptionGroup group = new OptionGroup();
		group.setRequired(true);
		group.addOption(Option.builder("b")
				.longOpt("problem")
				.hasArg()
				.argName("name")
				.build());
		group.addOption(Option.builder("d")
				.longOpt("dimension")
				.hasArg()
				.argName("number")
				.build());
		options.addOptionGroup(group);
	}
	
	public static void addReferenceSetOption(Options options) {
		options.addOption(Option.builder("r")
				.longOpt("reference")
				.hasArg()
				.argName("file")
				.build());
	}
	
	public static void addEpsilonOption(Options options) {
		options.addOption(Option.builder("e")
				.longOpt("epsilon")
				.hasArg()
				.argName("e1,e2,...")
				.build());
	}
	
	public static Problem getProblemInstance(CommandLine commandLine) {
		if (commandLine.hasOption("problem")) {
			return ProblemFactory.getInstance().getProblem(
					commandLine.getOptionValue("problem"));
		} else {
			return new ProblemStub(Integer.parseInt(
					commandLine.getOptionValue("dimension")));
		}
	}
	
	public static NondominatedPopulation getReferenceSet(CommandLine commandLine) throws IOException {
		NondominatedPopulation referenceSet = null;
		
		if (commandLine.hasOption("reference")) {
			referenceSet = new NondominatedPopulation(PopulationIO.readObjectives(
					new File(commandLine.getOptionValue("reference"))));
		} else {
			referenceSet = ProblemFactory.getInstance().getReferenceSet(
					commandLine.getOptionValue("problem"));
		}

		if (referenceSet == null) {
			throw new FrameworkException("no reference set available");
		}
		
		return referenceSet;
	}
	
	public static double[] getEpsilon(CommandLine commandLine) {
		if (commandLine.hasOption("epsilon")) {
			TypedProperties properties = TypedProperties.withProperty("epsilon",
					commandLine.getOptionValue("epsilon"));
			
			return properties.getDoubleArray("epsilon", null);
		}
		
		return null;
	}
	
	public static NondominatedPopulation getArchive(CommandLine commandLine) {
		double[] epsilon = getEpsilon(commandLine);
		
		if (epsilon != null) {
			return new EpsilonBoxDominanceArchive(epsilon);
		} else {
			return new NondominatedPopulation();
		}
	}

}
