package org.moeaframework.core.operator;

import org.moeaframework.core.Detection;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class StandardDetection implements Detection {

    private final double proportion;

    public StandardDetection(double proportion){
        this.proportion = proportion;
    }

    public StandardDetection(){
        this(0.1d);
    }

    @Override
    public double getProportionOfExperimentalSolutions() {
        return proportion;
    }

    @Override
    public boolean isEnvironmentChanged(Problem problem, Solution[] solutions) {
        int[] indexes = ThreadLocalRandom.current().ints(0, solutions.length).distinct().limit((int)(solutions.length*this.proportion)).toArray();
        for(int index : indexes) {
            double[] objectives = solutions[index].getObjectives().clone();
            problem.evaluate(solutions[index]);
            if (!Arrays.equals(objectives, solutions[index].getObjectives())) {
                return true;
            }
        }
        return false;
    }
}
