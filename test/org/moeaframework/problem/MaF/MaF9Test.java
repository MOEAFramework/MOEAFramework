/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.problem.MaF;

import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class MaF9Test extends ProblemTest {
	
	@Test
	public void test3D() {
		Problem problem = new MaF9(3);
		
		Assert.assertArrayEquals(new double[] { 9999.5, 3659.75404, 13660.75404 }, 
				evaluateAtLowerBounds(problem).getObjectives(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 10000.5, 3660.75404, 13659.75404 }, 
				evaluateAtUpperBounds(problem).getObjectives(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 0.5, 0.5, 0.5 }, 
				evaluateAt(problem, Vector.of(2, 0.0)).getObjectives(),
				0.00001);
	}
	
	@Test
	public void test10D() {
		MaF9 problem = new MaF9(10);
		
		Assert.assertTrue(problem.isInvalid(new Vector2D(1, 1)));
		Assert.assertFalse(problem.isInvalid(new Vector2D(10, 10)));
		
		Assert.assertArrayEquals(new double[] { 13.017, 9.0489, 1.2613, 7.3715, 13.5518, 14.9191, 10.9511, 3.1634, 5.4693, 11.6497}, 
				evaluateAt(problem, Vector.of(2, 10.0)).getObjectives(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("MaF9_3", 3, false);
	}
	
	@Test
	public void testAgainstJMetal3D() {
		testAgainstJMetal("MaF9_3");
	}
	
	@Test
	@Ignore("graphical test")
	public void testSolve() {
		MaF9 problem = new MaF9(10);
		
		MOEAD algorithm = new MOEAD(problem);
		algorithm.run(100000);
		
		List<Solution> solutions = algorithm.getResult().asList();

		Plot plot = new Plot();
		plot.scatter("Solutions",
				solutions.stream().mapToDouble(x -> EncodingUtils.getReal(x.getVariable(0))).toArray(),
				solutions.stream().mapToDouble(x -> EncodingUtils.getReal(x.getVariable(1))).toArray());
		plot.scatter("Vertices",
				problem.getPolygon().getVertices().stream().mapToDouble(x -> x.getX()).toArray(),
				problem.getPolygon().getVertices().stream().mapToDouble(x -> x.getY()).toArray());
		plot.show();
	}

}
