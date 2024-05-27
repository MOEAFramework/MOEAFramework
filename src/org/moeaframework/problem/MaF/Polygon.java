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

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Region;

/**
 * An immutable polygon, represented by an ordered list of vertices and the line segments formed between those vertices.
 */
class Polygon {
	
	private static final double TOLERANCE = 0.00001;

	private final Vector2D[] vertices;

	private final Line[] lines;

	private Region<Euclidean2D> region;

	/**
	 * Constructs a new polygon with the given vertices.
	 * 
	 * @param vertices the vertices, in order
	 */
	public Polygon(Collection<Vector2D> vertices) {
		this(vertices.toArray(Vector2D[]::new));
	}

	/**
	 * Constructs a new polygon with the given vertices.
	 * 
	 * @param vertices the vertices, in order
	 */
	public Polygon(Vector2D[] vertices) {
		super();
		this.vertices = vertices.clone();
		this.lines = new Line[vertices.length];

		for (int i = 0; i < vertices.length; i++) {
			lines[i] = new Line(vertices[i], vertices[(i + 1) % vertices.length], TOLERANCE);
		}
	}

	/**
	 * Creates a polygon with {@code M} vertices with a radial distance of {@code 1.0}.
	 * 
	 * @param M the number of vertices
	 * @return the polygon
	 */
	public static Polygon createStandardPolygon(int M) {
		// use polar coordinates to create evenly-spaced vertices in Cartesian coordinates
		double rho = Math.sqrt(1.0);
		double theta = Math.atan2(1.0, 0.0);

		Vector2D[] vertices = new Vector2D[M];

		for (int i = 0; i < M; i++) {
			double t = theta - 2.0 * (i+1) * Math.PI / M;
			vertices[i] = new Vector2D(rho * Math.cos(t), rho * Math.sin(t));
		}

		return new Polygon(vertices);
	}

	/**
	 * Returns the size of this polygon (i.e., the number of vertices or edges).
	 * 
	 * @return the size of this polygon
	 */
	public int size() {
		return vertices.length;
	}

	/**
	 * Returns the vertex at the given index.  The modulus of the index is taken, so the indices will "wrap" around
	 * the polygon.
	 * 
	 * @param i the index
	 * @return the vertex
	 */
	public Vector2D getVertex(int i) {
		return vertices[Math.floorMod(i, vertices.length)];
	}

	/**
	 * Returns the vertices that define this polygon.
	 * 
	 * @return the vertices
	 */
	public List<Vector2D> getVertices() {
		return List.of(vertices);
	}

	/**
	 * Returns the edge / line at the given index.  The modulus of the index is taken, so the indices will "wrap"
	 * around the polygon.
	 * 
	 * @param i the index
	 * @return the edge / line
	 */
	public Line getLine(int i) {
		return lines[Math.floorMod(i, lines.length)];
	}

	/**
	 * Returns the edges / lines of this polygon.
	 * 
	 * @return the edges / lines
	 */
	public List<Line> getLines() {
		return List.of(lines);
	}

	/**
	 * Returns the region defined by this polygon, useful for determining if points lie inside, on the boundary, or
	 * outside this polygon.
	 * 
	 * @param tolerance the tolerance / thickness of the region
	 * @return the region
	 */
	public Region<Euclidean2D> toRegion() {
		if (region == null) {
			// Per this constructor, the vertices must be ordered in a counter-clockwise manner so that the inside
			// is to the "left" of the lines.
			region = new PolygonsSet(TOLERANCE, vertices);
		}

		return region;
	}
	
	/**
	 * Checks the location of the point in relation to this polygon, determining if it is inside, on the boundary, or
	 * outside this polygon.
	 * 
	 * @param point the point to check
	 * @return the location of the point in relation to this polygon
	 */
	public Region.Location checkPoint(Vector2D point) {
		return toRegion().checkPoint(point);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(vertices).build();
	}

}