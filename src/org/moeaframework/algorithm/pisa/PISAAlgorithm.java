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
package org.moeaframework.algorithm.pisa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.algorithm.AlgorithmException;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.io.FileUtils;
import org.moeaframework.util.io.RedirectStream;

/**
 * Algorithm for interfacing with an external PISA selector. The PISA
 * framework is a platform and programming language independent interface for
 * search algorithms. PISA separates search algorithms into <em>selector</em>s,
 * describing the optimization algorithm, and <em>variator</em>s, describing
 * the optimization problem. PISA uses a file-based communication channel
 * between selectors and variators, which may result in excessive
 * communication costs, file system bottlenecks and file name collisions.
 * See the PISA homepage for detailed instructions.
 * <p>
 * Note that some PISA selectors parse the command line arguments using sscanf
 * <pre>
 *   sscanf(argv[2], "%s", filenamebase);
 * </pre>
 * On some operating systems, this will not work if the files used by PISA
 * contain whitespace in the filename.  It may be necessary to set the JVM
 * property {@code java.io.tmpdir} to a folder with no whitespace in the
 * filename.
 * 
 * @see <a href="http://www.tik.ee.ethz.ch/pisa/">PISA Homepage</a>
 */
public class PISAAlgorithm extends AbstractAlgorithm {

	/**
	 * The file prefix used when creating the PISA communication files.
	 */
	private final String filePrefix;

	/**
	 * The {@link ProcessBuilder} to start the selector process.
	 */
	private final ProcessBuilder selector;

	/**
	 * The shared state.
	 */
	private final State state;

	/**
	 * The population size.
	 */
	private final int alpha;

	/**
	 * The number of parents.
	 */
	private final int mu;

	/**
	 * The number of offspring.
	 */
	private final int lambda;

	/**
	 * Mapping from identifiers to solutions.
	 */
	private final Map<Integer, Solution> solutions;

	/**
	 * The variation operator.
	 */
	private final Variation variation;

	/**
	 * Constructs an adapter for a PISA selector.
	 * 
	 * @param name the name of the PISA selector
	 * @param problem the problem being solved
	 * @param variation the variation operator
	 * @param properties additional properties for the PISA selector
	 *        configuration file
	 * @throws IOException if an I/O error occurred
	 */
	public PISAAlgorithm(String name, Problem problem, Variation variation,
			Properties properties) throws IOException {
		super(problem);
		this.variation = variation;
		
		TypedProperties typedProperties = new TypedProperties(properties);
		String command = Settings.getPISACommand(name);
		String configuration = Settings.getPISAConfiguration(name);
		int pollRate = Settings.getPISAPollRate();
		
		if (command == null) {
			throw new IllegalArgumentException("missing command");
		}
		
		//This is slightly unsafe since the actual files used by 
		//PISA add the arc, cfg, ini, sel and sta extensions.  This
		//dependency on files for communication is part of PISA's 
		//design.
		filePrefix = File.createTempFile("pisa", "").getCanonicalPath();
		
		//ensure the seed property is set
		if (!properties.containsKey("seed")) {
			properties.setProperty("seed", Integer.toString(PRNG.nextInt()));
		}
		
		//write the configuration file if one is not specified
		if (configuration == null) {
			PrintWriter writer = null;
			configuration = new File(filePrefix + "par").getCanonicalPath();

			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(
						configuration)));
				
				for (String parameter : Settings.getPISAParameters(name)) {
					writer.print(parameter);
					writer.print(' ');
					writer.println(typedProperties.getString(parameter,
							Settings.getPISAParameterDefaultValue(name,
									parameter)));
				}
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		
		//construct the command line call to start the PISA selector
		selector = new ProcessBuilder(ArrayUtils.addAll(
				Settings.parseCommand(command), 
				configuration,
				filePrefix, 
				Double.toString(pollRate/(double)1000)));
		
		//ensure population size is a multiple of the # of parents
		int populationSize = (int)typedProperties.getDouble("populationSize",
				100);
		
		while (populationSize % variation.getArity() != 0) {
			populationSize++;
		}
		
		//configure the remaining options
		alpha = populationSize;
		mu = (int)typedProperties.getDouble("mu", alpha);
		lambda = (int)typedProperties.getDouble("lambda", alpha);
		state = new State(new File(filePrefix + "sta"));
		solutions = new HashMap<Integer, Solution>();
	}

	@Override
	public void initialize() {
		super.initialize();

		try {
			configure();
			state.set(0);
			state0();
			state.set(1);

			Process process = selector.start();
			RedirectStream.redirect(process.getInputStream(), System.out);
			RedirectStream.redirect(process.getErrorStream(), System.err);
		} catch (Exception e) {
			throw new AlgorithmException(this, e);
		}
	}

	@Override
	public void terminate() {
		super.terminate();
		
		//guard against attempting to access the non-existent state file if 
		//this algorithm is not yet initialized
		if (!isInitialized()) {
			return;
		}

		try {
			int currentState = state.get();

			while (true) {
				if (currentState == 2) {
					state.set(4);
					state4();
					state.set(5);
					break;
				} else if ((currentState == 4) || (currentState == 7)) {
					break;
				} else if (currentState < 8) {
					currentState = state.waitWhile(currentState);
				} else {
					throw new AlgorithmException(this, "restart not supported");
				}
			}
		} catch (Exception e) {
			throw new AlgorithmException(this, e);
		}
	}

	@Override
	public void iterate() {
		try {
			int currentState = state.get();

			while (true) {
				if (currentState == 2) {
					state2();
					state.set(3);
					break;
				} else if ((currentState == 4) || (currentState == 7)) {
					terminate();
					break;
				} else if (currentState < 8) {
					currentState = state.waitWhile(currentState);
				} else {
					throw new AlgorithmException(this, "restart not supported");
				}
			}
		} catch (Exception e) {
			throw new AlgorithmException(this, e);
		}
	}

	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation result = new NondominatedPopulation();
		result.addAll(solutions.values());
		return result;
	}

	/**
	 * Clears the specified file. Some selector implementations may block until
	 * the {@code sel} and {@code arc} files are cleared.
	 * 
	 * @param file the file to clear
	 * @throws IOException if an I/O error occurred
	 */
	private void clearFile(File file) throws IOException {
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			writer.println('0');
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Updates the population, retaining only those solutions with the
	 * specified identifiers.
	 * 
	 * @param ids the identifiers to retain
	 */
	private void updatePopulation(int[] ids) {
		List<Integer> archivedIds = new ArrayList<Integer>();

		for (int id : ids) {
			archivedIds.add(id);
		}

		solutions.keySet().retainAll(archivedIds);
	}

	/**
	 * Adds the specified solution to the population, returning its assigned
	 * identifier.
	 * 
	 * @param solution the solution
	 * @return the assigned identifier for the solution
	 */
	private int addToPopulation(Solution solution) {
		int id = nextFreeId();
		solutions.put(id, solution);
		return id;
	}

	/**
	 * Returns the next available identifier.
	 * 
	 * @return the next available identifier
	 */
	private int nextFreeId() {
		int id = 0;
		Set<Integer> ids = solutions.keySet();

		while (ids.contains(id)) {
			id++;
		}

		return id;
	}

	/**
	 * The commands to execute when in state 0.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	private void state0() throws IOException {
		Initialization initialization = new RandomInitialization(problem, alpha);
		Solution[] initialPopulation = initialization.initialize();
		int[] initialIds = new int[alpha];

		evaluateAll(initialPopulation);

		for (int i = 0; i < alpha; i++) {
			initialIds[i] = addToPopulation(initialPopulation[i]);
		}

		writePopulation(new File(filePrefix + "ini"), initialIds);
	}

	/**
	 * The commands to execute when in state 4.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	private void state4() throws IOException {
		int[] archivedIds = readList(new File(filePrefix + "arc"));

//		if (archivedIds.length != alpha) {
//			throw new IOException("invalid archive length");
//		}

		updatePopulation(archivedIds);
	}

	/**
	 * The commands to execute when in state 2.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	private void state2() throws IOException {
		int[] selectionIds = readList(new File(filePrefix + "sel"));
		int[] archivedIds = readList(new File(filePrefix + "arc"));
		int[] variationIds = new int[lambda];

		if (selectionIds.length != mu) {
			throw new IOException("invalid selection length");
		}

		//if (archivedIds.length != alpha) {
		//	System.out.println(archivedIds.length + " " + alpha);
		//	throw new IOException("invalid archive length");
		//}

		updatePopulation(archivedIds);
		clearFile(new File(filePrefix + "sel"));
		clearFile(new File(filePrefix + "arc"));

		List<Solution> offspring = new ArrayList<Solution>();

		for (int i = 0; i < mu; i += variation.getArity()) {
			Solution[] parents = new Solution[variation.getArity()];
			
			for (int j = 0; j < variation.getArity(); j++) {
				parents[j] = solutions.get(selectionIds[i+j]);
			}

			offspring.addAll(Arrays.asList(variation.evolve(parents)));
		}

		if (offspring.size() != lambda) {
			throw new IOException("invalid variation length");
		}

		evaluateAll(offspring);

		for (int i = 0; i < lambda; i++) {
			variationIds[i] = addToPopulation(offspring.get(i));
		}

		writePopulation(new File(filePrefix + "var"), variationIds);
	}

	/**
	 * Reads either the {@code sel} or {@code arc} files, returning the list
	 * of identifiers contained in the file.
	 * 
	 * @param file the {@code sel} or {@code arc} file
	 * @return the list of identifiers contained in the file
	 * @throws IOException if an I/O error occurred
	 */
	private int[] readList(File file) throws IOException {
		BufferedReader reader = null;
		String line = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			line = reader.readLine();

			if (line == null) {
				throw new IOException("unexpected end of file");
			}

			int size = Integer.parseInt(line);
			int[] result = new int[size];

			for (int i = 0; i < size; i++) {
				line = reader.readLine();

				if (line == null) {
					throw new IOException("unexpected end of file");
				}

				result[i] = Integer.parseInt(line);
			}

			// sanity check
			if (!"END".equals(reader.readLine())) {
				throw new IOException("expected END on last line");
			}

			return result;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Writes either the {@code ini} or {@code var} file with the specified
	 * identifiers.
	 * 
	 * @param file the {@code ini} or {@code var} file
	 * @param ids the identifiers of solutions written to the file
	 * @throws IOException if an I/O error occurred
	 */
	private void writePopulation(File file, int[] ids) throws IOException {
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			writer.println(ids.length * (problem.getNumberOfObjectives() + 1));

			for (int i = 0; i < ids.length; i++) {
				writer.print(ids[i]);

				for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
					writer.print(' ');
					writer.print(solutions.get(ids[i]).getObjective(j));
				}

				writer.println();
			}

			writer.println("END");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Removes any existing PISA communication files, creating a new {@code cfg}
	 * file with the appropriate settings.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	private void configure() throws IOException {
		FileUtils.delete(new File(filePrefix + "arc"));
		FileUtils.delete(new File(filePrefix + "cfg"));
		FileUtils.delete(new File(filePrefix + "ini"));
		FileUtils.delete(new File(filePrefix + "sel"));
		FileUtils.delete(new File(filePrefix + "sta"));

		PrintWriter writer = null;

		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(filePrefix + "cfg"))));

			writer.print("alpha ");
			writer.println(alpha);
			writer.print("mu ");
			writer.println(mu);
			writer.print("lambda ");
			writer.println(lambda);
			writer.print("dim ");
			writer.print(problem.getNumberOfObjectives());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
