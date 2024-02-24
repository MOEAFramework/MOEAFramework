# Extending with New Problems and Algorithms

Now let's explore how to solve your own optimization problems or develop new optimization algorithms.

## Custom Problems

To define a new problem, typically you would start by extending the `AbstractProblem` class.  In doing so, we will
need to specify:

1. The constructor, which specifies the number of variables, objectives, and constraints;
2. The `evaluate` method, which reads the decision variables, evaluates the problem, and sets the objectives / constraints; and
3. The `newSolution` method, which defines a solution to the problem, specifically setting the types and bounds of decision variables.

Here we construct the Srinivas problem, with two real-valued decision variables, two objectives, and two constraints:

```java

public class SrinivasProblem extends AbstractProblem {

	public SrinivasProblem() {
		super(2, 2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		double f1 = Math.pow(x - 2.0, 2.0) + Math.pow(y - 1.0, 2.0) + 2.0;
		double f2 = 9.0*x - Math.pow(y - 1.0, 2.0);
		double c1 = Math.pow(x, 2.0) + Math.pow(y, 2.0) - 225.0;
		double c2 = x - 3.0*y + 10.0;
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
		solution.setConstraint(0, c1 <= 0.0 ? 0.0 : c1);
		solution.setConstraint(1, c2 <= 0.0 ? 0.0 : c2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 2);
		
		solution.setVariable(0, EncodingUtils.newReal(-20.0, 20.0));
		solution.setVariable(1, EncodingUtils.newReal(-20.0, 20.0));
		
		return solution;
	}

}
```



