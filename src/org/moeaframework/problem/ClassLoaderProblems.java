package org.moeaframework.problem;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemProvider;
import org.moeaframework.core.spi.ProviderNotFoundException;

/**
 * Problem provider for problems accessible through the current class loader.
 * The name of the problem should be the fully-qualified class name (including
 * the containing package, if any).  Problems instantiated this way must provide
 * an empty constructor.  No reference sets are provided; see 
 * {@link PropertiesProblems} for a way to define problems with reference sets.
 */
public class ClassLoaderProblems extends ProblemProvider {

	@Override
	public Problem getProblem(String name) {
		try {
			return (Problem)Class.forName(name).newInstance();
		} catch (ClassNotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new ProviderNotFoundException(name, e);
		}
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		return null;
	}

}
