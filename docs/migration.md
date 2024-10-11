# Migrating to Version 5

## Objectives and Constraints

In previous versions, all objectives were minimized and any non-zero constraint value was considered infeasible.
Starting in version 5, the type of objective and constraint can be specified using one of the `Objective` and
`Constraint` classes.

For consistency with older versions, if you do not explicitly set the type, it defaults to the older style.
However, these new classes provide more flexibility in defining problems.  To change the objective or constraint
type, simply use:

```java
solution.setObjective(i, new Maximize());
solution.setConstraint(i, LessThanOrEqual.to(0.0));
```

However, this means the types changed.  The getters and setters now return their class:

```java
Objective objective = solution.getObjective(i);
double objective_value = objective.getValue();

Constraint constraint = solution.getConstraint(i);
double constraint_value = constraint.getValue();
```

You can also use the `get*Value()` method to get the value directly:

```java
double objective_value = solution.getObjectiveValue(i);
double constraint_value = solution.getConstraintValue(i);
```

Another consequence of this change is that, previously, any non-zero constraint value was a violation.  Now, the value
of the constraint no longer indicates if it is feasible, as it depends on the type of constraint.  Use the available
methods:

```java
solution.getConstraint(i).isViolation();
solution.getConstraint(i).getMagnitudeOfViolation();
```

## File Format

Along with the added objective and constraint types, all output formats changed to also store this information, as it
is now required to correctly load and interpret the stored values.

