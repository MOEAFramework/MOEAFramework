package org.moeaframework.problem;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public abstract class AbstractDynamicProblem implements Problem {
    protected final double severityOfChange;
    private final int frequencyOfChange;
    private int counter = 0;

    public AbstractDynamicProblem(double severityOfChange, int frequencyOfChange) {
        this.severityOfChange = severityOfChange;
        this.frequencyOfChange = frequencyOfChange;
    }

    @Override
    public void evaluate(Solution solution){
        if(++counter>frequencyOfChange) {
            counter = 0;
            changeEnvironment();
        }
    }

    protected abstract void changeEnvironment();
}
