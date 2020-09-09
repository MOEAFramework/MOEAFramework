package org.moeaframework.algorithm;

import org.moeaframework.core.Problem;

public abstract class AbstractDynamicAlgorithm extends AbstractAlgorithm{

    /**
     * Constructs an abstract algorithm for solving the specified problem.
     *
     * @param problem the problem being solved
     */
    public AbstractDynamicAlgorithm(Problem problem) {
        super(problem);
    }
    /**
     * Detects change in the problem.
     */
    public abstract boolean detectChange();

    /**
     * steps required to take on the change of environment.
     */
    public abstract void stepOnChange();
}
