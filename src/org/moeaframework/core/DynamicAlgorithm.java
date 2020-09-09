package org.moeaframework.core;

public interface DynamicAlgorithm extends Algorithm{
    /**
     * steps required to take on the change of environment.
     */
    public void stepOnChange();
}
