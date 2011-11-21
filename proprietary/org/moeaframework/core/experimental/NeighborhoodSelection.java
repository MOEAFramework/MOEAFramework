/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.core.experimental;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.math.util.MathUtils;
import org.moeaframework.core.Population;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;

public class NeighborhoodSelection implements Selection {

	private final Selection selection;

	private final int neighborhoodSize;

	public NeighborhoodSelection(Selection selection, int neighborhoodSize) {
		this.selection = selection;
		this.neighborhoodSize = neighborhoodSize;
	}

	@Override
	public Solution[] select(int arity, Population population) {
		Solution[] result = new Solution[arity];

		result[0] = selection.select(1, population)[0];

		for (int i = 1; i < arity; i++) {
			Solution[] neighborhood = selection.select(neighborhoodSize,
					population);
			Arrays.sort(neighborhood,
					new ObjectiveDistanceComparator(result[0]));
			result[i] = neighborhood[0];
		}

		return result;
	}

	/*
	 * @Override public Solution[] select(int arity, Population population) {
	 * Solution[] result = new Solution[arity]; result[0] = selection.select(1,
	 * population)[0]; Population neighborhood = new Population(population);
	 * neighborhood.truncate(neighborhoodSize, new
	 * ObjectiveDistanceComparator(result[0])); for (int i=1; i<arity; i++) {
	 * result[i] = selection.select(1, neighborhood)[0]; } return result; }
	 */

	private static class ObjectiveDistanceComparator implements
	Comparator<Solution>, Serializable {

		private static final long serialVersionUID = -187896834188576361L;
		
		private final Solution solution;

		public ObjectiveDistanceComparator(Solution solution) {
			this.solution = solution;
		}

		@Override
		public int compare(Solution o1, Solution o2) {
			double d1 = MathUtils.distance(solution.getObjectives(), o1
					.getObjectives());
			double d2 = MathUtils.distance(solution.getObjectives(), o2
					.getObjectives());

			return Double.compare(d1, d2);
		}

	}

}
