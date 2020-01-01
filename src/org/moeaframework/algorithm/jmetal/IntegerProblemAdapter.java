package org.moeaframework.algorithm.jmetal;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerSolution;

public class IntegerProblemAdapter extends ProblemAdapter<IntegerSolution> implements IntegerProblem {

	private static final long serialVersionUID = -9218462589427024057L;
	
	private Solution schema;
	
	public IntegerProblemAdapter(Problem problem) {
		super(problem);
		schema = problem.newSolution();
	}
	
	@Override
	public Integer getLowerBound(int index) {
		Variable variable = schema.getVariable(index);
		
		if (variable instanceof RealVariable) {
			return (int)((RealVariable)variable).getLowerBound();
		} else if (variable instanceof BinaryIntegerVariable) {
			return ((BinaryIntegerVariable)variable).getLowerBound();
		} else {
			throw new FrameworkException("Variable of type " + variable.getClass() +
					" not supported in IntegerProblemAdapter");
		}
	}

	@Override
	public Integer getUpperBound(int index) {
		Variable variable = schema.getVariable(index);
		
		if (variable instanceof RealVariable) {
			return (int)((RealVariable)variable).getUpperBound();
		} else if (variable instanceof BinaryIntegerVariable) {
			return ((BinaryIntegerVariable)variable).getUpperBound();
		} else {
			throw new FrameworkException("Variable of type " + variable.getClass() +
					" not supported in IntegerProblemAdapter");
		}
	}

	@Override
	public IntegerSolution createSolution() {
		return new DefaultIntegerSolution(this);
	}
	
	@Override
	public Solution convert(IntegerSolution solution) {
		Solution result = getProblem().newSolution();
		
		for (int i = 0; i < getNumberOfVariables(); i++) {
			EncodingUtils.setInt(result.getVariable(i), solution.getVariableValue(i));
		}
		
		return result;
	}

}
