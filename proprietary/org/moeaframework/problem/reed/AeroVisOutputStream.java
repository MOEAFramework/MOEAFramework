/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.problem.reed;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Class for writing the population and archive from an evolutionary algorithm
 * to Aerovis-compatible files. The following files are created:
 * <ul>
 * <li>{@code &lt;prefix&gt;.par} - Aerovis parameter file
 * <li>{@code &lt;prefix&gt;.pop} - Population data
 * <li>{@code &lt;prefix&gt;.arc} - Archive data
 * <li>{@code &lt;prefix&gt;.ref} - Reference set data
 * </ul>
 */
public class AeroVisOutputStream implements Closeable {

	/**
	 * The filename prefix.
	 */
	private final String name;

	/**
	 * The problem being solved.
	 */
	private final Problem problem;

	/**
	 * The output stream for writing population data.
	 */
	private final PrintStream population;

	/**
	 * The output stream for writing archive data.
	 */
	private final PrintStream archive;

	/**
	 * The output stream for writing reference set data.
	 */
	private final PrintStream reference;

	/**
	 * The reference set for the specified problem.
	 */
	private final NondominatedPopulation referenceSet;

	/**
	 * Creates a new Aerivis output stream with the specified filename prefix
	 * and the specified problem.
	 * 
	 * @param name the filename prefix
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @throws IOException if an I/O exception of some sort has occurred
	 */
	public AeroVisOutputStream(String name, Problem problem,
			NondominatedPopulation referenceSet) throws IOException {
		this.name = name;
		this.problem = problem;
		this.referenceSet = referenceSet;

		writeParameterFile();

		population = new PrintStream(new FileOutputStream(name + ".pop"));
		archive = new PrintStream(new FileOutputStream(name + ".arc"));
		reference = new PrintStream(new FileOutputStream(name + ".ref"));

		writeHeader(population);
		writeHeader(archive);
		writeHeader(reference);
	}

	/**
	 * Writes Aerovis header lines to the specified stream.
	 * 
	 * @param stream the stream to which the header lines are written
	 */
	private void writeHeader(PrintStream stream) {
		stream.println("# Format: RS# | Run | Gen | Popsize | Archive | NFE |"
				+ " Eperf | Eind | EXE Time |Objectives |");
		stream.println("# Real Variables | Rank | Crowding Distance");
		stream.println("# <GEN_HEADER> RS, Run, Gen, Popsize, Archive, NFE, "
				+ "Eperf, Eind, Time");

		stream.print("# <DATA_HEADER> f1");
		for (int i = 1; i < problem.getNumberOfObjectives(); i++) {
			stream.print(", f" + (i + 1));
		}
		stream.println();

		stream.println('#');
	}

	/**
	 * Creates and writes the Aerovis parameter file.
	 * 
	 * @throws FileNotFoundException if the parameter file could not be created
	 */
	private void writeParameterFile() throws FileNotFoundException {
		PrintStream ps = new PrintStream(new FileOutputStream(name + ".par"));

		ps.println("<DATA_FILE_FORMAT> multiple-simultaneous");

		ps.println("<DATA_FILES_BEGIN>");
		ps.println(name + ".pop, Population");
		ps.println(name + ".arc, Archive");
		ps.println(name + ".ref, Reference");
		ps.println("<DATA_FILES_END>");

		ps.println("<NOBJ> " + problem.getNumberOfObjectives());

		// TODO: don't really have good values to put here
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			ps.println("<OBJ" + (i + 1) + "_LOW> 0.0");
			ps.println("<OBJ" + (i + 1) + "_HIGH> 6.0");
		}

		ps.println("<OBJ_START> 1");

		ps.println("-- the following isn't used but needed to bypass a file "
				+ "load bug");
		ps.println("<SOAP_TEMPLATE> ./SOAP_AeroVis_base_4p_example.orb");
		ps.println("<NSAT> 0");
		ps.println("<SAT_START> 0");

		ps.close();
	}

	/**
	 * Appends the specified stream with the objective vectors contained in the
	 * specified population
	 * 
	 * @param stream the stream to which the objective vectors are written
	 * @param population the population containing the objective vectors
	 * @param numberOfEvaluations the number of evaluations
	 * @param populationSize the size of the current population
	 * @param archiveSize the size of the current archive
	 */
	private void write(PrintStream stream, Population population,
			int numberOfEvaluations, int populationSize, int archiveSize) {
		stream.printf("1\t1\t%d\t%d\t%d\t%d\t0.0\t0.0\t0.0\n",
				numberOfEvaluations, populationSize, archiveSize,
				numberOfEvaluations);

		stream.println('#');

		for (Solution solution : population) {
			stream.print(solution.getObjective(0));

			for (int i = 1; i < solution.getNumberOfObjectives(); i++) {
				stream.print('\t');
				stream.print(solution.getObjective(i));
			}

			stream.print("\t1\t0.0"); // rank and crowding distance

			stream.println();
		}

		stream.println('#');
	}

	/**
	 * Appends the current population and archive from the specified algorithm
	 * to the Aerovis data files.
	 * 
	 * @param algorithm the algorithm
	 */
	public void write(EvolutionaryAlgorithm algorithm) {
		write(population, algorithm.getPopulation(), algorithm
				.getNumberOfEvaluations(), algorithm.getPopulation().size(),
				algorithm.getArchive().size());
		write(archive, algorithm.getArchive(), algorithm
				.getNumberOfEvaluations(), algorithm.getPopulation().size(),
				algorithm.getArchive().size());
		write(reference, referenceSet, algorithm.getNumberOfEvaluations(),
				algorithm.getPopulation().size(), algorithm.getArchive().size());
	}

	@Override
	public void close() {
		population.close();
		archive.close();
		reference.close();
	}

}
