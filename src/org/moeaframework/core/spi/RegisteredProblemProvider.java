package org.moeaframework.core.spi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.util.io.CommentedLineReader;

/**
 * Problem provider that lets callers register problems by name.  For example:
 * <pre>
 *    RegisteredProblemProvider myProvider = new RegisteredProblemProvider();
 *    myProvider.register("MyProblem", () -> new MyProblem(), "pf/myProbem.pf");
 *    
 *    ProblemFactory.getInstance().addProvider(myProvider);
 * </pre>
 */
public class RegisteredProblemProvider extends ProblemProvider {
	
	/**
	 * Mapping of problem names to a constructor function.
	 */
	private final TreeMap<String, Supplier<Problem>> constructorMap;
	
	/**
	 * Mapping of problem names to their reference set.
	 */
	private final TreeMap<String, String> referenceSetMap;
	
	public RegisteredProblemProvider() {
		super();
		constructorMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		referenceSetMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Registers a new problem with this provider.
	 * 
	 * @param name the problem name
	 * @param constructor the function that creates a new instance of the problem
	 * @param referenceSet the path of the file containing the reference set
	 */
	public final void register(String name, Supplier<Problem> constructor, String referenceSet) {
		constructorMap.put(name, constructor);
		referenceSetMap.put(name, referenceSet);
	}
	
	/**
	 * For testing only.  Returns the names of all testable problems.
	 * 
	 * @return the problem names
	 */
	public Set<String> getTestableProblems() {
		return constructorMap.keySet();
	}

	@Override
	public Problem getProblem(String name) {
		Supplier<Problem> constructor = constructorMap.get(name);
		
		if (constructor != null) {
			return constructor.get();
		}
		
		return null;
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		String referenceSet = referenceSetMap.get(name);
		
		if (referenceSet != null) {
			try {
				return loadReferenceSet(referenceSet);
			} catch (IOException e) {
				return null;
			}
		}
		
		return null;
	}
	
	/**
	 * Loads a reference set either from a file on disk or from a resource bundled in
	 * the JAR file.
	 * 
	 * @param resource the path of the file or resource
	 * @return the reference set, or {@code null} if the file or resource was not found
	 * @throws IOException if an I/O error occurred
	 */
	private NondominatedPopulation loadReferenceSet(String resource) throws IOException {
		File file = new File(resource);
		
		if (file.exists()) {
			return new NondominatedPopulation(PopulationIO.readObjectives(file));
		} else {
			try (InputStream input = getClass().getResourceAsStream("/" + resource)) {
				if (input == null) {
					throw new FileNotFoundException(resource);
				} else {
					return new NondominatedPopulation(PopulationIO.readObjectives(
							new CommentedLineReader(new InputStreamReader(input))));
				}
			}
		}
	}

}
