package org.moeaframework.core;

public interface Detection {
    /**
     * Returns the proportion of solutions that is going to be controlled to detect change in the environment with {@code isEnvironmentChanged}
     * method.
     *
     * @return the proportion of solutions that is going to be controlled to detect change in the environment with {@code isEnvironmentChanged}
     * method.
     */
    public double getProportionOfExperimentalSolutions();

    /**
     * Evaluates the n (specified by {@code getProportionOfExperimentalSolutions} * size of solution set) subset of solution set again and checks
     * whether the environment is modified.
     *
     * @param problem the array of parent solutions
     * @param solutions the array of parent solutions
     * @return {@code true} if environment is changed, {@code false} otherwise
     */
    public boolean isEnvironmentChanged(Problem problem, Solution[] solutions);
}
