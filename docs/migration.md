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

## File Formats

With the addition of the objective and constraint types, the "result file" format has been updated to record this
information:

```
# Version=5
# Problem=TestProblem
# NumberOfVariables=2
# NumberOfObjectives=2
# NumberOfConstraints=0
# Variable.1.Definition=RealVariable(0.0,1.0)
# Variable.2.Definition=RealVariable(0.0,1.0)
# Objective.1.Definition=Minimize
# Objective.2.Definition=Minimize
```

This new format contains all the necessary information to process and interpret the solutions contained in the file.
Consequently, the "result file" is now the preferred and only supported file format.  This also means:

1. The supplied Pareto front files in `./pf/` are updated to use the new format
2. The `saveObjectives` / `loadObjectives` and `saveBinary` and `loadBinary` methods for saving populations are removed.
   Instead, simply use `save` and `load`.
3. Maximized objectives no longer need to be negated.  The `Negater` CLI tools is also removed.
4. CLI tools that process result files no longer accept the `--dimension` argument, as the dimension and type
   information is available in the header.
   
## Class Organization

Some interfaces and classes have been moved to follow a more standard organization.  If you find some imports failing
due to the class not being found, simply remove that import and use your IDE to find the new location.  The new layout
places the interface, abstract classes, and implementations all in the same package.  For example, `Population`,
`NondominatedPopulation`, and all related population and archive classes can be found in the new
`org.moeaframework.core.population` package.

## Miscellaneous Changes

* Genetic programming's `Problem` class hierarchy changed.  Previously, the `Program` object served as the root node
  in the program tree.  Now, the root node is accessed by calling `getRoot()` or `setRoot(...)`.

  
  
## New Features

### Streams


### Data Store

