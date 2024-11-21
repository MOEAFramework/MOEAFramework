# Writing Problems in Java

Now let's explore how to solve your own optimization problems.

## Defining New Problems

To define a new problem, we need to create a new class implementing the `Problem` interface.  The `AbstractProblem`
class provides a lot of common functionality, so most of the time we only need to extend `AbstractProblem`.  In
doing so, we will need to specify:

1. The constructor, which specifies the number of variables, objectives, and constraints;
2. The `evaluate` method, which reads the decision variables, evaluates the problem, and sets the
   objectives / constraints; and
3. The `newSolution` method, which defines a solution to the problem.

Here we construct the Srinivas problem, defined as:

$$ \begin{align} \text{Minimize } &f_1 = (x - 2)^2 + (y - 1)^2 + 2 \\\ &f_2 = 9x - (y - 1)^2 \\\ \text{Subject to } &x^2 + y^2 \leq 225 \\\ &x - 3y \leq -10 \\\ \text{Where } &-20 \leq x, y \leq 20 \end{align}$$

which has two decision variables, `x` and `y`, two objectives, `f1` and `f2`, and two constraints, `c1` and `c2`.
Programming this using the MOEA Framework would look something like:

<!-- java:examples/org/moeaframework/examples/srinivas/Srinivas.java [30:79] -->

```java
public class Srinivas extends AbstractProblem {

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

        solution.setObjectiveValue(0, f1);
        solution.setObjectiveValue(1, f2);

        solution.setConstraintValue(0, c1);
        solution.setConstraintValue(1, c2);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(2, 2, 2);

        solution.setVariable(0, new RealVariable(-20.0, 20.0));
        solution.setVariable(1, new RealVariable(-20.0, 20.0));

        solution.setObjective(0, new Minimize());
        solution.setObjective(1, new Minimize());

        solution.setConstraint(0, LessThanOrEqual.to(225.0));
        solution.setConstraint(1, LessThanOrEqual.to(-10.0));

        return solution;
    }

}
```

We can solve this problem by passing it directly to the constructor of an optimization algorithm, such as NSGA-II:

<!-- java:examples/org/moeaframework/examples/srinivas/SrinivasExample.java [29:34] -->

```java
Problem problem = new Srinivas();

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

algorithm.getResult().display();
```

## Types

Observe above that we define the types of the decision variables, objectives, and constraints in the `newSolution`
method.

### Decision Variables

Refer to the [List of Decision Variables](listOfDecisionVariables.md) for details on each decision variable type and
their usage.

### Objectives

Objectives derive from the `Objective` interface.  We can define the objective direction, potentially mixing the two
types, as demonstrated below:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [objective-definition] {KeepComments} -->

```java
solution.setObjective(0, new Minimize());
solution.setObjective(1, new Maximize());
```

Then, in the `evaluate` method, we would assign a value to each objective:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [objective-values] {KeepComments} -->

```java
// Get or set the objective value directly
solution.getObjective(0).setValue(100.0);
solution.getObjective(0).getValue();

// Alternative way to get or set the objective value
solution.setObjectiveValue(0, 100.0);
solution.getObjectiveValue(0);
```

The `Objective` type provides several built-in methods for comparing and modifying the objective values, accounting for
the different directions.

### Constraints

Constraints derive from the `Constraint` interface.  Below we demonstrate constructing each of the constraint types,
which defines the range of values that are considered feasible or in violation.

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [constraint-definition] {KeepComments} -->

```java
// Require the constraint to be less than (or equal) to a given value
solution.setConstraint(0, LessThan.value(10.0));
solution.setConstraint(1, LessThanOrEqual.to(10.0));

// Require the constraint to be greater than (or equal) to a given value
solution.setConstraint(0, GreaterThan.value(10.0));
solution.setConstraint(1, GreaterThanOrEqual.to(10.0));

// Require the constraint to be equal or not equal to a given value
solution.setConstraint(0, Equal.to(10.0));
solution.setConstraint(1, NotEqual.to(10.0));

// Require the constraint to be between or outside some lower and upper bounds
solution.setConstraint(0, Between.values(-10.0, 10.0));
solution.setConstraint(1, Outside.values(-10.0, 10.0));
```

We then typically would set the value of a constraint in the `evaluate` method of our problem:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [constraint-values] {KeepComments} -->

```java
// Get or set the constraint value directly
solution.getConstraint(0).setValue(100.0);
solution.getConstraint(0).getValue();

// Alternative way to get or set the constraint value
solution.setConstraintValue(0, 100.0);
solution.getConstraintValue(0);
```

Lastly, we can check if a constraint is feasible or in violation, or measure the magnitude of said violation, as
follows:

<!-- java:test/org/moeaframework/snippet/ProblemSnippet.java [constraint-violation] {KeepComments} -->

```java
// Checking if a single constraint is feasible or violated
solution.getConstraint(0).isViolation();
solution.getConstraint(0).getMagnitudeOfViolation();

// Checking all constraints of a solution
solution.isFeasible();
solution.getSumOfConstraintViolations();
```


