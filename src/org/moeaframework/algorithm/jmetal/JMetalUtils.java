/* Copyright 2009-2020 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.algorithm.jmetal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.ProblemException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

/**
 * Utility methods for interfacing with JMetal.
 */
public class JMetalUtils {
	
	private JMetalUtils() {
		// private constructor to prevent instantiation
	}

	/**
	 * Returns the decision variables types used by the given problem.
	 * 
	 * @param problem the problem
	 * @return the set of decision variable types
	 */
	public static Set<Class<?>> getTypes(Problem problem) {
		Set<Class<?>> types = new HashSet<Class<?>>();
		Solution schema = problem.newSolution();
		
		for (int i=0; i<schema.getNumberOfVariables(); i++) {
			types.add(schema.getVariable(i).getClass());
		}
		
		return types;
	}
	
	/**
	 * Returns the single decision variable type used by the given problem, or
	 * throws an exception if the problem uses more than one type.
	 * 
	 * @param problem the problem
	 * @return the decision variable type
	 * @throws ProblemException when the problem has more than one type
	 */
	public static Class<?> getSingleType(Problem problem) {
		Set<Class<?>> types = getTypes(problem);
		
		if (types.isEmpty()) {
			throw new ProblemException(problem, "Problem has no defined types");
		} else if (types.size() > 1) {
			throw new ProblemException(problem, "Problem has multiple types defined, expected only one: " +
					Arrays.toString(types.toArray()));
		} else {
			return types.iterator().next();
		}
	}
	
	/**
	 * Creates a {@link ProblemAdapter} for the given problem.  Currently only supports
	 * problems with a single decision variable type of {@code RealVariable},
	 * {@code BinaryVariable}, or {@code Permutation}.
	 * 
	 * @param problem the problem
	 * @return the problem adapter appropriate for the given problem
	 */
	public static ProblemAdapter<? extends org.uma.jmetal.solution.Solution<?>> createProblemAdapter(Problem problem) {
		Class<?> type = JMetalUtils.getSingleType(problem);

		if (RealVariable.class.isAssignableFrom(type)) {
			return new DoubleProblemAdapter(problem);
		} else if (BinaryVariable.class.isAssignableFrom(type)) {
			return new BinaryProblemAdapter(problem);
		} else if (Permutation.class.isAssignableFrom(type)) {
			return new PermutationProblemAdapter(problem);
		} else {
			throw new ProblemException(problem, "Problems with type " + type.getSimpleName() + 
					" are not currently supported by JMetal");
		}
	}
	
	/**
	 * Copies all objective and constraint values from a JMetal solution to a
	 * MOEA Framework solution.  Since JMetal currently aggregates all constraints
	 * into an overall value, this overall value is copied to the first constraint.
	 * 
	 * @param from the JMetal solution
	 * @param to the MOEA Framework solution
	 */
	public static <T extends org.uma.jmetal.solution.Solution<?>> void copyObjectivesAndConstraints(T from, Solution to) {
		for (int i = 0; i < from.getNumberOfObjectives(); i++) {
			to.setObjective(i, from.getObjective(i));
		}
		
		// TODO: Will need to be updated when switching to JMetal 6
		if (to.getNumberOfConstraints() > 0) {
			to.setConstraint(0, getOverallConstraintViolation(from));
		}
	}
	
	/**
	 * Copies all objective and constraint values from a MOEA Framework solution to
	 * a JMetal solution.  Note that JMetal currently aggregates all constraints into an
	 * overall value.  Problems with multiple constraints will be aggregated into this one
	 * value.
	 * 
	 * @param from the MOEA Framework solution
	 * @param to the JMetal solution
	 */
	public static <T extends org.uma.jmetal.solution.Solution<?>> void copyObjectivesAndConstraints(Solution from, T to) {
		for (int i = 0; i < from.getNumberOfObjectives(); i++) {
			to.setObjective(i, from.getObjective(i));
		}
		
		// TODO: Will need to be updated when switching to JMetal 6
		if (from.getNumberOfConstraints() > 0) {
			double overallConstraintViolation = 0.0;
			int numberOfViolatedConstraints = 0;
			
			for (int i = 0; i < from.getNumberOfConstraints(); i++) {
				if (from.getConstraint(i) != 0.0) {
					overallConstraintViolation -= Math.abs(from.getConstraint(i));
					numberOfViolatedConstraints++;
				}
			}
			
			setOverallConstraintViolation(to, overallConstraintViolation);
			setNumberOfViolatedConstraints(to, numberOfViolatedConstraints);
		}
	}
	
	/**
	 * Returns the overall constraint violation of the JMetal solution.
	 * 
	 * @param solution the JMetal solution
	 * @return the overall constraint violation
	 */
	public static <T extends org.uma.jmetal.solution.Solution<?>> double getOverallConstraintViolation(T solution) {
		Double value = new OverallConstraintViolation<T>().getAttribute(solution);
		
		if (value == null) {
			return 0.0;
		} else {
			return value;
		}
	}
	
	/**
	 * Returns the number of violated constraints of the JMetal solution.
	 * 
	 * @param solution the JMetal solution
	 * @return the number of violated constraints
	 */
	public static <T extends org.uma.jmetal.solution.Solution<?>> int getNumberOfViolatedConstraints(T solution) {
		Integer value = new NumberOfViolatedConstraints<T>().getAttribute(solution);
		
		if (value == null) {
			return 0;
		} else {
			return value;
		}
	}
	
	/**
	 * Sets the overall constraint violation of the JMetal solution.
	 * 
	 * @param solution the JMetal solution
	 * @param value the overall constraint violation to set
	 */
	public static <T extends org.uma.jmetal.solution.Solution<?>> void setOverallConstraintViolation(T solution, double value) {
		new OverallConstraintViolation<T>().setAttribute(solution, value);
	}
	
	/**
	 * Sets the number of violated constraints of the JMetal solution.
	 * 
	 * @param solution the JMetal solution
	 * @param value the number of violated constraints to set
	 */
	public static <T extends org.uma.jmetal.solution.Solution<?>> void setNumberOfViolatedConstraints(T solution, int value) {
		new NumberOfViolatedConstraints<T>().setAttribute(solution, value);
	}
	
	/**
	 * Converts the given MOEA Framework population to a JMetal solution set.  Only
	 * the objectives and constraints will be copied.
	 * 
	 * @param adapter the problem adapter for converting solutions
	 * @param population the MOEA Framework population
	 * @return the JMetal solution set
	 */
	public static <T extends org.uma.jmetal.solution.Solution<?>> List<T> toSolutionSet(
			ProblemAdapter<T> adapter, Population population) {
		List<T> result = new ArrayList<T>();
		
		for (Solution solution : population) {
			T newSolution = adapter.createSolution();
			JMetalUtils.copyObjectivesAndConstraints(solution, newSolution);
			result.add(newSolution);
		}
		
		return result;
	}
	
	/**
	 * Converts a JMetal solution set to a JMetal {@link Front}.  The {@code Front}
	 * will only contain feasible solutions.
	 * 
	 * @param adapter the problem adapter for converting solutions
	 * @param solutionSet the JMetal solution set
	 * @return the JMetal {@code Front}
	 */
	public static <T extends org.uma.jmetal.solution.Solution<?>> Front toFront(
			ProblemAdapter<T> adapter, List<T> solutionSet) {
		List<Point> points = new ArrayList<Point>();
		
		for (T solution : solutionSet) {
			if (JMetalUtils.getOverallConstraintViolation(solution) >= 0.0) {
				Point point = new ArrayPoint(solution.getObjectives());
				points.add(point);
			}
		}
		
		Front front = new ArrayFront(points.size(), adapter.getNumberOfObjectives());
		
		for (int i = 0; i < points.size(); i++) {
			front.setPoint(i, points.get(i));
		}
		
		return front;
	}
	
	/**
	 * Converts a MOEA Framework population to a JMetal {@link Front}.  The {@code Front}
	 * will only contain feasible solutions. 
	 * 
	 * @param adapter the problem adapter for converting solutions
	 * @param population the MOEA Framework population
	 * @return the JMetal {@code Front}
	 */
	public static <T extends org.uma.jmetal.solution.Solution<?>> Front toFront(
			ProblemAdapter<T> adapter, Population population) {
		return toFront(adapter, toSolutionSet(adapter, population));
	}

}
