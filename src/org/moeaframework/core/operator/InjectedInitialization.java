package org.moeaframework.core.operator;

import java.util.List;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * This file has been moved to the initialization package.  Please update your imports accordingly.
 * 
 * @deprecated Moved to {@link org.moeaframework.core.initialization.InjectedInitialization}
 */
@Deprecated
public class InjectedInitialization extends org.moeaframework.core.initialization.InjectedInitialization {

	public InjectedInitialization(Problem problem, List<Solution> injectedSolutions) {
		super(problem, injectedSolutions);
	}

	public InjectedInitialization(Problem problem, Solution... injectedSolutions) {
		super(problem, injectedSolutions);
	}


}
