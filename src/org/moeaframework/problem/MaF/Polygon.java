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
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.moeaframework.core.Settings;

/**
 * An immutable polygon, represented by an ordered list of vertices and the line segments formed between those vertices.
 */
class Polygon {
	
	private final Vector2D[] vertices;
	
	private final Line[] lines;
	
	private Region<Euclidean2D> region;
	
	public Polygon(Collection<Vector2D> vertices) {
		this(vertices.toArray(Vector2D[]::new));
	}
	
	public Polygon(Vector2D[] vertices) {
		super();
		this.vertices = vertices.clone();
		this.lines = new Line[vertices.length];
		
		for (int i = 0; i < vertices.length; i++) {
			lines[i] = new Line(vertices[i], vertices[(i + 1) % vertices.length], Settings.EPS);
		}
	}
	
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
	
	public List<Vector2D> getVertices() {
		return List.of(vertices);
	}
	
	/**
	 * Returns the edge / line at the given index.  The modulus of the index is taken, so the indices will "wrap"
	 * around the polygon.
	 * 
	 * @param i the index
	 * @return the vertex
	 */
	public Line getLine(int i) {
		return lines[Math.floorMod(i, lines.length)];
	}
	
	public List<Line> getLines() {
		return List.of(lines);
	}
	
	public Region<Euclidean2D> toRegion() {
		if (region == null) {
			region = new MonotoneChain(false, Settings.EPS).generate(getVertices()).createRegion();
		}
		
		return region;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append(vertices).build();
	}
	
}