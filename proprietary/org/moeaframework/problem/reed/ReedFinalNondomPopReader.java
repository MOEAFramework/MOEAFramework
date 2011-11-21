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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.EpsilonBoxDominanceComparator;

public class ReedFinalNondomPopReader implements Closeable {

	private final BufferedReader reader;

	private int numberOfObjectives;

	private int numberOfVariables;

	private String line;

	public ReedFinalNondomPopReader(File file) throws IOException {
		reader = new BufferedReader(new FileReader(file));

		readHeader();
	}

	private final void readHeader() throws IOException {
		line = reader.readLine();

		if ((line == null)
				|| (!line
						.equals("# This file contains the final non-dominated population."))) {
			throw new IOException("header not formatted correctly: " + line);
		}

		line = reader.readLine();

		Pattern pattern = Pattern
				.compile("# Format: RS# \\| Cum. Gen \\| Index \\| Objectives = (\\d+) \\|  Real Variables = (\\d+) \\| Rank \\| Crowding Distance");
		Matcher matcher = pattern.matcher(line);

		if (!matcher.matches()) {
			throw new IOException("header not formatted correctly: " + line);
		}

		numberOfObjectives = Integer.parseInt(matcher.group(1));
		numberOfVariables = Integer.parseInt(matcher.group(2));

		// read the first line of data to initialize the readNextPopulation
		// method
		line = reader.readLine();

		if (line == null) {
			throw new IOException("file contains no data");
		}
	}

	public boolean hasNext() {
		return line != null;
	}

	private NondominatedPopulation readNextPopulation() throws IOException {
		NondominatedPopulation population = new NondominatedPopulation();

		Solution solution = parseLine();
		int seed = (Integer)solution.getAttribute("seed");

		population.add(solution);

		while ((line = reader.readLine()) != null) {
			solution = parseLine();

			if (seed == (Integer)solution.getAttribute("seed")) {
				population.add(solution);
			} else {
				break;
			}
		}

		return population;
	}

	public NondominatedPopulation next() throws IOException {
		if (hasNext()) {
			return readNextPopulation();
		} else {
			throw new NoSuchElementException();
		}
	}

	private Solution parseLine() throws IOException {
		String[] tokens = line.split("\\s+");

		if (tokens.length != numberOfObjectives + numberOfVariables + 5) {
			throw new IOException("line not formatted correctly");
		}

		Solution solution = new Solution(0, numberOfObjectives);

		solution.setAttribute("seed", Integer.parseInt(tokens[0]));

		for (int i = 0; i < numberOfObjectives; i++) {
			solution.setObjective(i, Double.parseDouble(tokens[i + 3]));
		}

		solution.setAttribute("testCrowding", Double.parseDouble(tokens[4
				+ numberOfObjectives + numberOfVariables]));

		return solution;
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	public static void main(String[] args) throws IOException {
		EpsilonBoxDominanceArchive archive = new EpsilonBoxDominanceArchive(
				new EpsilonBoxDominanceComparator(new double[] { 0.01, 0.001,
						0.0001, 0.0001 }));

		ReedFinalNondomPopReader reader = new ReedFinalNondomPopReader(
				new File(args[0]));

		while (reader.hasNext()) {
			archive.addAll(reader.next());
		}

		PopulationIO.writeObjectives(new File(args[1]), archive);
	}

}
