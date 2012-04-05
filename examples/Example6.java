import java.io.IOException;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.ExternalProblem;


public class Example6 {
	
	public static class DTLZ2Remote extends ExternalProblem {

		public DTLZ2Remote() throws IOException {
			super((String)null, DEFAULT_PORT);
		}

		@Override
		public String getName() {
			return "DTLZ2";
		}

		@Override
		public int getNumberOfVariables() {
			return 11;
		}

		@Override
		public int getNumberOfObjectives() {
			return 2;
		}

		@Override
		public int getNumberOfConstraints() {
			return 0;
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(getNumberOfVariables(), 
					getNumberOfObjectives());

			for (int i = 0; i < getNumberOfVariables(); i++) {
				solution.setVariable(i, new RealVariable(0.0, 1.0));
			}

			return solution;
		}
		
	}
	
	public static void main(String[] args) {
		//configure and run the Rosenbrock function
		NondominatedPopulation result = new Executor()
				.withProblemClass(DTLZ2Remote.class)
				.withAlgorithm("GDE3")
				.withMaxEvaluations(1000000)
				.run();
				
		//display the results
		for (Solution solution : result) {
			System.out.print(solution.getVariable(0));
			System.out.print(" ");
			System.out.print(solution.getVariable(1));
			System.out.print(" => ");
			System.out.println(solution.getObjective(0));
		}
	}

}
