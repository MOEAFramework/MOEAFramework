/* Copyright 2009-2019 David Hadka
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

public class JMetalUtils {
	
	private JMetalUtils() {
		// private constructor to prevent instantiation
	}

	public static Set<Class<?>> getTypes(Problem problem) {
		Set<Class<?>> types = new HashSet<Class<?>>();
		Solution schema = problem.newSolution();
		
		for (int i=0; i<schema.getNumberOfVariables(); i++) {
			types.add(schema.getVariable(i).getClass());
		}
		
		return types;
	}
	
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
	
	public static <T extends org.uma.jmetal.solution.Solution<?>> void copyObjectivesAndConstraints(T from, Solution to) {
		for (int i = 0; i < from.getNumberOfObjectives(); i++) {
			to.setObjective(i, from.getObjective(i));
		}
		
		// TODO: Will need to be updated when switching to JMetal 6
		if (to.getNumberOfConstraints() > 0) {
			to.setConstraint(0, getOverallConstraintViolation(from));
		}
	}
	
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
	
	public static <T extends org.uma.jmetal.solution.Solution<?>> double getOverallConstraintViolation(T solution) {
		Double value = new OverallConstraintViolation<T>().getAttribute(solution);
		
		if (value == null) {
			return 0.0;
		} else {
			return value;
		}
	}
	
	public static <T extends org.uma.jmetal.solution.Solution<?>> int getNumberOfViolatedConstraints(T solution) {
		Integer value = new NumberOfViolatedConstraints<T>().getAttribute(solution);
		
		if (value == null) {
			return 0;
		} else {
			return value;
		}
	}
	
	public static <T extends org.uma.jmetal.solution.Solution<?>> void setOverallConstraintViolation(T solution, double value) {
		new OverallConstraintViolation<T>().setAttribute(solution, value);
	}
	
	public static <T extends org.uma.jmetal.solution.Solution<?>> void setNumberOfViolatedConstraints(T solution, int value) {
		new NumberOfViolatedConstraints<T>().setAttribute(solution, value);
	}
	
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
	
	public static <T extends org.uma.jmetal.solution.Solution<?>> Front toFront(
			ProblemAdapter<T> adapter, Population population) {
		return toFront(adapter, toSolutionSet(adapter, population));
	}

}
