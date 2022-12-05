package org.moeaframework.core.spi;

import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;

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
	
	/**
	 * Creates a new, empty problem provider.
	 */
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
	protected final void register(String name, Supplier<Problem> constructor, String referenceSet) {
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
				return PopulationIO.readReferenceSet(referenceSet);
			} catch (IOException e) {
				return null;
			}
		}
		
		return null;
	}

}
