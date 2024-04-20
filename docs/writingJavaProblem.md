# Writing Problems in Java

Now let's explore how to solve your own optimization problems.

## Defining New Problems

To define a new problem, we need to create a new class implementing the `Problem` interface.  The `AbstractProblem`
class provides a lot of common functionality, so most of the time we only need to extend `AbstractProblem`.  In
doing so, we will need to specify:

1. The constructor, which specifies the number of variables, objectives, and constraints;
2. The `evaluate` method, which reads the decision variables, evaluates the problem, and sets the
   objectives / constraints; and
3. The `newSolution` method, which defines a solution to the problem, specifically setting the types and bounds of
   decision variables.

Here we construct the Srinivas problem, defined as:

$$ \begin{align} \text{Minimize } &f_1 = (x - 2)^2 + (y - 1)^2 + 2 \\\ &f_2 = 9x - (y - 1)^2 \\\ \text{Subject to } &x^2 + y^2 \leq 225 \\\ &x - 3y \leq -10 \\\ \text{Where } &-20 \leq x, y \leq 20 \end{align}$$

which has two decision variables, `x` and `y`, two objectives, `f1` and `f2`, and two constraints, `c1` and `c2`.
Programming this using the MOEA Framework would look something like:

<!-- java:examples/Example6.java [32:78] -->

```java
public static class Srinivas extends AbstractProblem {

    public Srinivas() {
        super(2, 2, 2);
    }

    @Override
    public void evaluate(Solution solution) {
        double x = EncodingUtils.getReal(solution.getVariable(0));
        double y = EncodingUtils.getReal(solution.getVariable(1));

        double f1 = Math.pow(x - 2.0, 2.0) + Math.pow(y - 1.0, 2.0) + 2.0;
        double f2 = 9.0*x - Math.pow(y - 1.0, 2.0);
        double c1 = Math.pow(x, 2.0) + Math.pow(y, 2.0);
        double c2 = x - 3.0*y;

        solution.setObjective(0, f1);
        solution.setObjective(1, f2);

        solution.setConstraint(0, Constraint.lessThanOrEqual(c1, 225.0));
        solution.setConstraint(1, Constraint.lessThanOrEqual(c2, -10.0));
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(2, 2, 2);

        solution.setVariable(0, new RealVariable(-20.0, 20.0));
        solution.setVariable(1, new RealVariable(-20.0, 20.0));

        return solution;
    }

}
```

We can solve this problem by passing it directly to the constructor of an optimization algorithm, such as NSGA-II:

<!-- java:examples/Example6.java [81:86] -->

```java
Problem problem = new Srinivas();

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

algorithm.getResult().display();
```
