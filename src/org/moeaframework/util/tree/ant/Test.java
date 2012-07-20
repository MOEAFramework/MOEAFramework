package org.moeaframework.util.tree.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class Test {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		int maxMoves = 500;
		File file = new File("src/org/moeaframework/util/tree/ant/santafe.trail");
		
		NondominatedPopulation results = new Executor()
				.withProblemClass(AntProblem.class, file, maxMoves)
				.withAlgorithm("NSGAII")
				.withProperty("populationSize", 500)
				.withMaxEvaluations(500000)
				.run();
		
		for (Solution solution : results) {
			System.out.println(solution.getObjective(0));
			System.out.println(solution.getVariable(0));
			
			AntProblem problem = new AntProblem(file, maxMoves);
			problem.evaluate(solution);
			problem.displayLastEvaluation();
		}
	}

}
