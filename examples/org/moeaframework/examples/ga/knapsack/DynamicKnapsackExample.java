package org.moeaframework.examples.ga.knapsack;

import org.moeaframework.Executor;
import org.moeaframework.algorithm.StandardDynamicAlgorithms;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.util.Vector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Example of binary optimization using the {@link Knapsack} problem on the
 * {@code knapsack.100.2} instance.
 */
public class DynamicKnapsackExample {

    /**
     * Starts the example running the knapsack problem.
     *
     * @param args
     *            the command line arguments
     * @throws IOException
     *             if an I/O error occurred
     */
    public static void main(String[] args) throws IOException {
        // open the file containing the knapsack problem instance
        InputStream input = Knapsack.class.getResourceAsStream("knapsack.100.2");

        if (input == null) {
            System.err.println("Unable to find the file knapsack.100.2");
            System.exit(-1);
        }

        Properties properties = new Properties();
        properties.put("dataset", input);
        properties.put("frequencyOfChange",5000);
        properties.put("severityOfChange",1d);

        AlgorithmFactory.getInstance().addProvider(new StandardDynamicAlgorithms());
        // solve using NSGA-II
        NondominatedPopulation result = new Executor().withProblemClass(DynamicKnapsack.class, properties).withAlgorithm("DNSGAIIA")
                .withMaxEvaluations(500000).distributeOnAllCores().run();
        // solve using AMOSA
//		OperatorFactory.getInstance().addProvider(new HLPProvider());
//		TemperatureTerminationCondition temperatureTerminationCondition = new TemperatureTerminationCondition();
//		NondominatedPopulation result = new Executor().withProblemClass(Knapsack.class, input).withAlgorithm("AMOSA")
//				.withMaxEvaluations(50000).distributeOnAllCores().withTerminationCondition(temperatureTerminationCondition).run();
//


        // print the results
        for (int i = 0; i < result.size(); i++) {
            Solution solution = result.get(i);
            double[] objectives = solution.getObjectives();

            // negate objectives to return them to their maximized form
            objectives = Vector.negate(objectives);

            System.out.println("Solution " + (i + 1) + ":");
            System.out.println("    Sack 1 Profit: " + objectives[0]);
            System.out.println("    Sack 2 Profit: " + objectives[1]);
            System.out.println("    Binary String: " + solution.getVariable(0));
        }
    }

}
