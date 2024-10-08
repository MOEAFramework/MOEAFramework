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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Region.Location;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.util.validate.Validate;

/**
 * The MaF9 test problem.  This problem exhibits the following properties:
 * <ul>
 *   <li>Linear Pareto front
 *   <li>Degenerate
 * </ul>
 * <p>
 * MaF9 is unique in that it features a repair operator.  As discussed in the cited paper, for polygons with 5 or more
 * vertices, the lines forming the polygon can intersect outside the polygon, causing additional points to be
 * non-dominated with respect to points inside the polygon.  The repair operator simply resamples the point at a new,
 * random location.
 */
public class MaF9 extends AbstractProblem implements AnalyticalProblem {

	final Polygon polygon;

	final List<Polygon> invalidRegions;

	/**
	 * Constructs an MaF9 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF9(int numberOfObjectives) {
		super(2, numberOfObjectives);
		Validate.that("numberOfObjectives", numberOfObjectives).isGreaterThanOrEqualTo(3);

		polygon = Polygon.createStandardPolygon(numberOfObjectives);

		// Construct the invalid regions
		int iterations = (int)Math.ceil(numberOfObjectives / 2.0 - 2.0);
		invalidRegions = new ArrayList<Polygon>(iterations * numberOfObjectives);

		for (int i = 0; i < iterations * numberOfObjectives; i++) {
			int head = i % numberOfObjectives;
			int tail = head + i / numberOfObjectives + 1;
			
			Vector2D v1 = polygon.getVertex(head - 1);
			Vector2D v2 = polygon.getVertex(head);
			Vector2D v3 = polygon.getVertex(tail);
			Vector2D v4 = polygon.getVertex(tail + 1);

			Line l1 = new Line(v1, v2, Polygon.TOLERANCE);
			Line l2 = new Line(v3, v4, Polygon.TOLERANCE);

			Vector2D intersection = l1.intersection(l2);

			if (intersection == null) {
				throw new IllegalStateException("Lines do not intersect: " + l1 + " " + l2);
			}

			List<Vector2D> vertices = new ArrayList<Vector2D>();

			for (int j = head; j <= tail; j++) {
				vertices.add(polygon.getVertex(j));
			}

			for (int j = head; j <= tail; j++) {
				vertices.add(new Vector2D(
						2 * intersection.getX() - polygon.getVertex(j).getX(),
						2 * intersection.getY() - polygon.getVertex(j).getY()));
			}

			invalidRegions.add(new Polygon(vertices));
		}
	}

	@Override
	public void evaluate(Solution solution) {
		Vector2D point = repair(solution);
		double[] f = new double[numberOfObjectives];
		
		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = polygon.getLine(i).distance(point);
		}
		
		solution.setObjectiveValues(f);
	}
	
	Vector2D repair(Solution solution) {
		Vector2D point = new Vector2D(EncodingUtils.getReal(solution));
		
		while (isInvalid(point)) {
			for (int i = 0; i < solution.getNumberOfVariables(); i++) {
				solution.getVariable(i).randomize();
			}
			
			point = new Vector2D(EncodingUtils.getReal(solution));
		}
		
		return point;
	}
	
	boolean isInvalid(Vector2D point) {
		for (Polygon invalidRegion : invalidRegions) {
			if (invalidRegion.checkPoint(point) == Location.INSIDE) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(-10000, 10000));
		}

		return solution;
	}
	
	@Override
	public Solution generate() {
		Vector2D point = new Vector2D(PRNG.nextDouble(-1.0, 1.0), PRNG.nextDouble(-1.0, 1.0));
		
		while (polygon.checkPoint(point) == Location.OUTSIDE) {
			point = new Vector2D(PRNG.nextDouble(-1.0, 1.0), PRNG.nextDouble(-1.0, 1.0));
		}
		
		Solution solution = newSolution();
		EncodingUtils.setReal(solution, point.toArray());
		evaluate(solution);
		
		return solution;
	}

}
