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
package org.moeaframework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.moeaframework.algorithm.Checkpoints;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.TerminationCondition;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.termination.CompoundTerminationCondition;
import org.moeaframework.core.termination.MaxElapsedTime;
import org.moeaframework.core.termination.MaxFunctionEvaluations;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.distributed.DistributedProblem;
import org.moeaframework.util.io.FileUtils;
import org.moeaframework.util.progress.ProgressHelper;
import org.moeaframework.util.progress.ProgressListener;

/**
 * Configures and executes algorithms while hiding the underlying boilerplate 
 * code needed to setup and safely execute an algorithm.  For example, the 
 * following demonstrates its typical use:
 * <p>
 * <pre>
 *   NondominatedPopulation result = new Executor()
 *       .withAlgorithm("NSGAII")
 *       .withProblem("DTLZ2_2")
 *       .withMaxEvaluations(10000)
 *       .run();
 * </pre>
 * <p>
 * The problem and algorithm must be specified prior to calling {@link #run()}.
 * Additional parameters for each algorithm can be assigned using the
 * {@code withProperty} methods:
 * <p>
 * <pre>
 *   NondominatedPopulation result = new Executor()
 *       .withAlgorithm("NSGAII")
 *       .withProblem("DTLZ2_2")
 *       .withMaxEvaluations(10000)
 *       .withProperty("populationSize", 100)
 *       .withProperty("sbx.rate", 1.0)
 *       .withProperty("sbx.distributionIndex", 15.0)
 *       .withProperty("pm.rate", 0.05)
 *       .withProperty("pm.distributionIndex", 20.0)
 *       .run();
 * </pre>
 * <p>
 * The evaluation of function evaluations can be distributed across multiple
 * cores or computers by using {@link #distributeOnAllCores()},
 * {@link #distributeOn(int)}, or {@link #distributeWith(ExecutorService)}.
 * Checkpoint files can be saved in order to resume interrupted runs using the
 * {@link #withCheckpointFrequency(int)} and {@link #withCheckpointFile(File)}
 * methods.  For example:
 * <p>
 * <pre>
 *   NondominatedPopulation result = new Executor()
 *       .withAlgorithm("NSGAII")
 *       .withProblem("DTLZ2_2")
 *       .withMaxEvaluations(100000)
 *       .distributeOnAllCores()
 *       .withCheckpointFrequency(1000)
 *       .withCheckpointFile(new File("example.state"))
 *       .run();
 * </pre>
 */
public class Executor extends ProblemBuilder {
	
	/**
	 * The algorithm name.
	 */
	private String algorithmName;
	
	/**
	 * The algorithm properties.
	 */
	private TypedProperties properties;
	
	/**
	 * The number of threads for local execution.
	 */
	private int numberOfThreads;
	
	/**
	 * The executor service for distributing jobs; or {@code null} if
	 * distribution is local.
	 */
	private ExecutorService executorService;
	
	/**
	 * The checkpoint file for storing the algorithm state; or {@code null} if
	 * checkpoints are not used.
	 */
	private File checkpointFile;
	
	/**
	 * The checkpoint frequency, specifying the number of evaluations between
	 * checkpoints.
	 */
	private int checkpointFrequency;
	
	/**
	 * The algorithm provider for creating algorithm instances; or {@code null}
	 * if the default algorithm factory should be used.
	 */
	private AlgorithmFactory algorithmFactory;
	
	/**
	 * The instrumenter used to record information about the runtime behavior
	 * of algorithms executed by this executor; or {@code null} if no 
	 * instrumentation is used.
	 */
	private Instrumenter instrumenter;
	
	/**
	 * Manages reporting progress, elapsed time, and time remaining to
	 * {@link ProgressListener}s.
	 */
	private ProgressHelper progress;
	
	/**
	 * Indicates that this executor should stop processing and return any
	 * results collected thus far.
	 */
	private AtomicBoolean isCanceled;
	
	/**
	 * The termination conditions.
	 */
	private List<TerminationCondition> terminationConditions;
	
	/**
	 * Constructs a new executor initialized with default settings.
	 */
	public Executor() {
		super();
		
		isCanceled = new AtomicBoolean();
		progress = new ProgressHelper(this);
		properties = new TypedProperties();
		numberOfThreads = 1;
		terminationConditions = new ArrayList<TerminationCondition>();
	}
	
	/**
	 * Informs this executor to stop processing and returns any results
	 * collected thus far.  This method is thread-safe.
	 */
	public void cancel() {
		isCanceled.set(true);
	}
	
	/**
	 * Returns {@code true} if the canceled flag is set; {@code false}
	 * otherwise.  After canceling a run, the flag will remain set to
	 * {@code true} until another run is started.  This method is thread-safe.
	 * 
	 * @return {@code true} if the canceled flag is set; {@code false}
	 *         otherwise
	 */
	public boolean isCanceled() {
		return isCanceled.get();
	}
	
	/**
	 * Sets the instrumenter used to record information about the runtime
	 * behavior of algorithms executed by this executor.
	 * 
	 * @param instrumenter the instrumeter
	 * @return a reference to this executor
	 */
	public Executor withInstrumenter(Instrumenter instrumenter) {
		this.instrumenter = instrumenter;
		
		return this;
	}
	
	/**
	 * Returns the instrumenter used by this executor; or {@code null} if no
	 * instrumenter has been assigned.
	 * 
	 * @return the instrumenter used by this executor; or {@code null} if no
	 *         instrumenter has been assigned
	 */
	public Instrumenter getInstrumenter() {
		return instrumenter;
	}
	
	/**
	 * Sets the algorithm factory used by this executor.
	 * 
	 * @param algorithmFactory the algorithm factory
	 * @return a reference to this executor
	 */
	public Executor usingAlgorithmFactory(AlgorithmFactory algorithmFactory) {
		this.algorithmFactory = algorithmFactory;
		
		return this;
	}
	
	@Override
	public Executor withSameProblemAs(ProblemBuilder builder) {
		return (Executor)super.withSameProblemAs(builder);
	}
	
	@Override
	public Executor usingProblemFactory(ProblemFactory problemFactory) {
		return (Executor)super.usingProblemFactory(problemFactory);
	}
	
	@Override
	public Executor withProblem(String problemName) {
		return (Executor)super.withProblem(problemName);
	}
	
	@Override
	public Executor withProblem(Problem problemInstance) {
		return (Executor)super.withProblem(problemInstance);
	}
	
	@Override
	public Executor withProblemClass(Class<?> problemClass, 
			Object... problemArguments) {
		return (Executor)super.withProblemClass(problemClass, problemArguments);
	}

	@Override
	public Executor withProblemClass(String problemClassName, 
			Object... problemArguments) throws ClassNotFoundException {
		return (Executor)super.withProblemClass(problemClassName,
				problemArguments);
	}
	
	public Executor withTerminationCondition(TerminationCondition condition) {
		terminationConditions.add(condition);
		
		return this;
	}
	
	/**
	 * Sets the algorithm used by this executor.
	 * 
	 * @param algorithmName the algorithm name
	 * @return a reference to this executor
	 */
	public Executor withAlgorithm(String algorithmName) {
		this.algorithmName = algorithmName;
		
		return this;
	}
	
	/**
	 * Sets the {@link ExecutorService} used by this executor to distribute
	 * solution evaluations.  The caller is responsible for ensuring the 
	 * executor service is shutdown after use.
	 * 
	 * @param executorService the executor service
	 * @return a reference to this executor
	 */
	public Executor distributeWith(ExecutorService executorService) {
		this.executorService = executorService;
		
		return this;
	}
	
	/**
	 * Enables this executor to distribute solution evaluations across the
	 * specified number of threads.
	 * 
	 * @param numberOfThreads the number of threads
	 * @return a reference to this executor
	 * @throws IllegalArgumentException if {@code numberOfThreads <= 0}
	 */
	public Executor distributeOn(int numberOfThreads) {
		if (numberOfThreads <= 0) {
			throw new IllegalArgumentException("invalid number of threads");
		}
		
		this.numberOfThreads = numberOfThreads;
		
		return this;
	}
	
	/**
	 * Enables this executor to distribute solution evaluations across all
	 * processors on the local host.
	 * 
	 * @return a reference to this executor
	 */
	public Executor distributeOnAllCores() {
		return distributeOn(Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * Sets the checkpoint file where the algorithm state is stored.  This
	 * method must be invoked in order to enable checkpoints.
	 * 
	 * @param checkpointFile the checkpoint file
	 * @return a reference to this executor
	 */
	public Executor withCheckpointFile(File checkpointFile) {
		this.checkpointFile = checkpointFile;
		
		return this;
	}
	
	/**
	 * Sets the frequency at which this executor saves checkpoints.
	 * 
	 * @param checkpointFrequency the checkpoint frequency, specifying the
	 *        number of evaluations between checkpoints
	 * @return a reference to this executor
	 */
	public Executor withCheckpointFrequency(int checkpointFrequency) {
		this.checkpointFrequency = checkpointFrequency;
		
		return this;
	}
	
	/**
	 * Enables this executor to save checkpoints after every iteration of the
	 * algorithm.
	 * 
	 * @return a reference to this executor
	 */
	public Executor checkpointEveryIteration() {
		return withCheckpointFrequency(1);
	}
	
	/**
	 * Deletes the checkpoint file if it exists.
	 * 
	 * @return a reference to this executor
	 * @throws IOException if the checkpoint file could not be deleted
	 */
	public Executor resetCheckpointFile() throws IOException {
		if (checkpointFile != null) {
			FileUtils.delete(checkpointFile);
		}
		
		return this;
	}
	
	/**
	 * Sets the &epsilon; values; equivalent to setting the property
	 * {@code epsilon}.
	 * 
	 * @param epsilon the &epsilon; values
	 * @return a reference to this executor
	 */
	@Override
	public Executor withEpsilon(double... epsilon) {
		super.withEpsilon(epsilon);
		
		if ((epsilon == null) || (epsilon.length == 0)) {
			properties.remove("epsilon");
		} else {
			withProperty("epsilon", epsilon);
		}
		
		return this;
	}
	
	/**
	 * Sets the maximum number of evaluations; equivalent to setting the
	 * property {@code maxEvaluations}.
	 * 
	 * @param maxEvaluations the maximum number of evaluations
	 * @return a reference to this executor
	 */
	public Executor withMaxEvaluations(int maxEvaluations) {
		withProperty("maxEvaluations", maxEvaluations);
		
		return this;
	}
	
	/**
	 * Sets the maximum elapsed time in milliseconds; equivalent to setting the
	 * property {@code maxTime}.
	 * 
	 * @param maxTime the maximum elapsed time in milliseconds
	 * @return a reference to this executor
	 */
	public Executor withMaxTime(long maxTime) {
		withProperty("maxTime", maxTime);
		
		return this;
	}
	
	/**
	 * Unsets a property.
	 * 
	 * @param key the property key
	 * @return a reference to this executor
	 */
	public Executor removeProperty(String key) {
		properties.remove(key);
		
		return this;
	}
	
	/**
	 * Sets a property.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, String value) {
		properties.setString(key, value);
		
		return this;
	}
	
	/**
	 * Sets a {@code float} property.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, float value) {
		properties.setFloat(key, value);
		
		return this;
	}
	
	/**
	 * Sets a {@code double} property.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, double value) {
		properties.setDouble(key, value);
		
		return this;
	}
	
	/**
	 * Sets a {@code byte} property.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, byte value) {
		properties.setByte(key, value);
		
		return this;
	}
	
	/**
	 * Sets a {@code short} property.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, short value) {
		properties.setShort(key, value);
		
		return this;
	}
	
	/**
	 * Sets an {@code int} property.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, int value) {
		properties.setInt(key, value);
		
		return this;
	}
	
	/**
	 * Sets a {@code long} property.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, long value) {
		properties.setLong(key, value);
		
		return this;
	}
	
	/**
	 * Sets a {@code boolean} property.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, boolean value) {
		properties.setBoolean(key, value);
		
		return this;
	}
	
	/**
	 * Sets a {@code String} array property.
	 * 
	 * @param key the property key
	 * @param values the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, String[] values) {
		properties.setStringArray(key, values);
		
		return this;
	}
	
	/**
	 * Sets a {@code float} array property.
	 * 
	 * @param key the property key
	 * @param values the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, float[] values) {
		properties.setFloatArray(key, values);
		
		return this;
	}
	
	/**
	 * Sets a {@code double} array property.
	 * 
	 * @param key the property key
	 * @param values the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, double[] values) {
		properties.setDoubleArray(key, values);
		
		return this;
	}
	
	/**
	 * Sets a {@code byte} array property.
	 * 
	 * @param key the property key
	 * @param values the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, byte[] values) {
		properties.setByteArray(key, values);
		
		return this;
	}
	
	/**
	 * Sets a {@code short} array property.
	 * 
	 * @param key the property key
	 * @param values the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, short[] values) {
		properties.setShortArray(key, values);
		
		return this;
	}
	
	/**
	 * Sets an {@code int} array property.
	 * 
	 * @param key the property key
	 * @param values the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, int[] values) {
		properties.setIntArray(key, values);
		
		return this;
	}
	
	/**
	 * Sets a {@code long} array property.
	 * 
	 * @param key the property key
	 * @param values the property value
	 * @return a reference to this executor
	 */
	public Executor withProperty(String key, long[] values) {
		properties.setLongArray(key, values);
		
		return this;
	}
	
	/**
	 * Clears the properties.
	 * 
	 * @return a reference to this executor
	 */
	public Executor clearProperties() {
		properties.clear();
		
		return this;
	}
	
	/**
	 * Sets all properties.  This will clear any existing properties, including
	 * the {@code maxEvaluations} or {@code maxTime}.
	 * 
	 * @param properties the properties
	 * @return a reference to this executor
	 */
	public Executor withProperties(Properties properties) {
		this.properties.clear();
		this.properties.addAll(properties);
		
		return this;
	}
	
	/**
	 * Adds the given progress listener to receive periodic progress reports.
	 * 
	 * @param listener the progress listener to add
	 * @return a reference to this executor
	 */
	public Executor withProgressListener(ProgressListener listener) {
		progress.addProgressListener(listener);
		
		return this;
	}
	
	/**
	 * Returns the termination condition for this executor.
	 * 
	 * @return the termination condition
	 */
	protected TerminationCondition createTerminationCondition() {
		int maxEvaluations = (int)properties.getDouble("maxEvaluations", -1);
		long maxTime = (long)properties.getDouble("maxTime", -1);
		
		// create a list of the termination conditions
		List<TerminationCondition> conditions = new ArrayList<TerminationCondition>();
		
		if (maxEvaluations >= 0) {
			conditions.add(new MaxFunctionEvaluations(maxEvaluations));
		}
		
		if (maxTime >= 0) {
			conditions.add(new MaxElapsedTime(maxTime));
		}
		
		conditions.addAll(terminationConditions);
		
		// return the termination conditions
		if (conditions.size() == 0) {
			System.err.println("no termination conditions set, setting to " +
					"25,000 max evaluations");
			
			return new MaxFunctionEvaluations(25000);
		} else if (conditions.size() == 1) {
			return conditions.get(0);
		} else {
			return new CompoundTerminationCondition(conditions.toArray(
					new TerminationCondition[conditions.size()]));
		}
	}
	
	/**
	 * Runs this executor with its configured settings multiple times,
	 * returning the individual end-of-run approximation sets.  If the run
	 * is canceled, the list contains any complete seeds that finished prior
	 * to cancellation.
	 * 
	 * @param numberOfSeeds the number of seeds to run
	 * @return the individual end-of-run approximation sets
	 */
	public List<NondominatedPopulation> runSeeds(int numberOfSeeds) {
		isCanceled.set(false);
		
		if ((checkpointFile != null) && (numberOfSeeds > 1)) {
			System.err.println(
					"checkpoints not supported when running multiple seeds");
			checkpointFile = null;
		}
		
		int maxEvaluations = properties.getInt("maxEvaluations", -1);
		long maxTime = properties.getLong("maxTime", -1);
		List<NondominatedPopulation> results =
				new ArrayList<NondominatedPopulation>();
		
		progress.start(numberOfSeeds, maxEvaluations, maxTime);
		
		for (int i = 0; i < numberOfSeeds && !isCanceled.get(); i++) {
			NondominatedPopulation result = runSingleSeed(i+1, numberOfSeeds,
					createTerminationCondition());
			
			results.add(result);
				
			progress.nextSeed();
		}
		
		progress.stop();
		
		return results;
	}
	
	/**
	 * Runs this executor with its configured settings.
	 * 
	 * @return the end-of-run approximation set; or {@code null} if canceled
	 */
	public NondominatedPopulation run() {
		isCanceled.set(false);
		
		int maxEvaluations = properties.getInt("maxEvaluations", -1);
		long maxTime = properties.getLong("maxTime", -1);
		
		progress.start(1, maxEvaluations, maxTime);
		
		NondominatedPopulation result = runSingleSeed(1, 1, createTerminationCondition());
		
		progress.nextSeed();
		progress.stop();
		
		return result;
	}

	/**
	 * Runs this executor with its configured settings.
	 * 
	 * @param seed the current seed being run, such that
	 *        {@code 1 <= seed <= numberOfSeeds}
	 * @param numberOfSeeds to total number of seeds being run
	 * @param terminationCondition the termination conditions for the run
	 * 
	 * @return the end-of-run approximation set; or {@code null} if canceled
	 */
	protected NondominatedPopulation runSingleSeed(int seed, int numberOfSeeds,
			TerminationCondition terminationCondition) {
		if (algorithmName == null) {
			throw new IllegalArgumentException("no algorithm specified");
		}
		
		if ((problemName == null) && (problemClass == null) && (problemInstance == null)) {
			throw new IllegalArgumentException("no problem specified");
		}
		
		Problem problem = null;
		Algorithm algorithm = null;
		ExecutorService executor = null;
		
		try {
			problem = getProblemInstance();
			
			try {
				if (executorService != null) {
					problem = new DistributedProblem(problem, executorService);
				} else if (numberOfThreads > 1) {
					executor = Executors.newFixedThreadPool(numberOfThreads);
					problem = new DistributedProblem(problem, executor);
				}
				
				NondominatedPopulation result = newArchive();
				
				try {
					if (algorithmFactory == null) {
						algorithm = AlgorithmFactory.getInstance().getAlgorithm(
								algorithmName, 
								properties.getProperties(), 
								problem);
					} else {
						algorithm = algorithmFactory.getAlgorithm(
								algorithmName, 
								properties.getProperties(), 
								problem);
					}

					if (checkpointFile != null) {
						algorithm = new Checkpoints(
								algorithm, 
								checkpointFile,
								checkpointFrequency);
					}
					
					if (instrumenter != null) {
						algorithm = instrumenter.instrument(algorithm);
					}
					
					terminationCondition.initialize(algorithm);
					progress.setCurrentAlgorithm(algorithm);

					while (!algorithm.isTerminated() &&
							!terminationCondition.shouldTerminate(algorithm)) {
						// stop and return null if canceled and not yet complete
						if (isCanceled.get()) {
							return null;
						}
						
						algorithm.step();
						progress.setCurrentNFE(algorithm.getNumberOfEvaluations());
					}

					result.addAll(algorithm.getResult());
					
					progress.setCurrentAlgorithm(null);
				} finally {
					if (algorithm != null) {
						algorithm.terminate();
					}
				}
				
				return result;
			} finally {
				if (executor != null) {
					executor.shutdown();
				}
			}
		} finally {
			if ((problem != null) && (problem != this.problemInstance)) {
				problem.close();
			}
		}
	}

}
