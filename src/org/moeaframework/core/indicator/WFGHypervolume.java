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
package org.moeaframework.core.indicator;

import java.util.Comparator;
import java.util.Iterator;
import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Fast hypervolume calculation published by the Walking Fish Group (WFG).  This implementation includes all
 * optimizations discussed in the paper, including: (1) sorting the solutions by an objective, (2) slicing, and
 * (3) an exact method to compute the 2D hypervolume case.
 * <p>
 * This version is not normalized!  See {@link WFGNormalizedHypervolume} for the normalized version.
 * <p>
 * References:
 * <ol>
 *   <li>While, Ronald Lyndon et al. “A Fast Way of Calculating Exact Hypervolumes.” IEEE Transactions on Evolutionary
 *       Computation 16 (2012): 86-95.
 * </ol>
 */
public class WFGHypervolume implements Indicator {

	/**
	 * The problem.
	 */
	protected final Problem problem;
	
	/**
	 * The reference point, which is typically the worst value in each objective.
	 */
	private double[] referencePoint;
	
	/**
	 * Constructs a new WFG hypervolume instance with the given reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set used to derive the reference point
	 */
	public WFGHypervolume(Problem problem, NondominatedPopulation referenceSet) {
		this(problem, referenceSet.getUpperBounds());
	}
	
	/**
	 * Constructs a new WFG hypervolume instance with the given reference point.
	 * 
	 * @param problem the problem
	 * @param referencePoint the reference point
	 */
	public WFGHypervolume(Problem problem, double[] referencePoint) {
		super();
		this.problem = problem;
		this.referencePoint = referencePoint;
	}
	
	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		Iterator<Solution> iterator = approximationSet.iterator();
		
		while (iterator.hasNext()) {
			Solution solution = iterator.next();
			
			//prune any solutions which exceed the reference point
			for (int i=0; i<solution.getNumberOfObjectives(); i++) {
				if (solution.getObjective(i) > referencePoint[i]) {
					iterator.remove();
					break;
				}
			}
		}
		
		return wfg(approximationSet, problem.getNumberOfObjectives());
	}
	
	/**
	 * Recursive hypervolume calculation using slices.
	 * 
	 * @param pointList the list of points being considered in the hypervolume calculation
	 * @param slice the current slice (dimension) being computed
	 * @return the hypervolume of the points
	 */
	private double wfg(NondominatedPopulation pointList, int slice) {
		double volume = 0.0;

		if (pointList.size() == 0) {
			return volume;
		}
		
		pointList.sort(new WFGObjectiveComparator(slice));
		
		if (slice == 1) {
			// special case - O(1) calculation for the 1-dimension case
			volume = referencePoint[0] - pointList.get(0).getObjective(0);
		} else if (slice == 2) {
			// special case - O(n) calculation for the 2-dimension case
			volume = (referencePoint[0] - pointList.get(0).getObjective(0)) *
					(referencePoint[1] - pointList.get(0).getObjective(1));
			
			for (int i = 1; i < pointList.size(); i++) {
				volume += (referencePoint[0] - pointList.get(i).getObjective(0)) *
						(pointList.get(i-1).getObjective(1) - pointList.get(i).getObjective(1));
			}
		} else {
			// recursive case for 3+ dimensions
			for (int i = pointList.size() - 1; i >= 0; i--) {
				volume += (referencePoint[slice-1] - pointList.get(i).getObjective(slice-1)) * exclhv(pointList, i, slice-1);
			}
		}
		
		return volume;
	}
	
	/**
	 * Returns the inclusive hypervolume for the given point.  Inclusive means it measures the entire volume
	 * dominated by this point.
	 * 
	 * @param point the current point
	 * @param slice the current slice (dimension) being computed
	 * @return the inclusive hypervolume of the solution
	 */
	private double inclhv(Solution point, int slice) {
		double volume = 1.0;
		
		for (int i = 0; i < slice; i++) {
			volume *= (referencePoint[i] - point.getObjective(i));
		}
		
		return volume;
	}
	
	/**
	 * Returns the exclusive hypervolume of the current (contributing) point.  Any volume overlapping with outer points
	 * is excluded.
	 * 
	 * @param pointList the list of points being considered in the hypervolume calculation
	 * @param k the index of the current (contributing) point
	 * @param slice the current slice (dimension) being computed
	 * @return the exclusive hypervolume of the current solution
	 */
	private double exclhv(NondominatedPopulation pointList, int k, int slice) {
		return inclhv(pointList.get(k), slice) - wfg(limitset(pointList, k, slice), slice);
	}
	
	/**
	 * Returns a modified list of the points {k+1 .. |pointList|} updated with the larger value taken from the point
	 * and the contributing point k.  Additionally, any dominated points in this modified list are removed.
	 * <p>
	 * Note: This method is equivalent to {@code nds(limitset(...))} in Figure 5 from the referenced paper.
	 * 
	 * @param pointList the list of points being considered in the hypervolume calculation
	 * @param k the index of the current (contributing) point
	 * @param slice the current slice (dimension) being computed
	 * @return a new list of points with updated objective values
	 */
	private NondominatedPopulation limitset(NondominatedPopulation pointList, int k, int slice) {
		NondominatedPopulation result = new NondominatedPopulation();
		
		for (int i = 0; i < pointList.size() - k - 1; i++) {
			Solution slicedSolution = new Solution(0, slice);
			
			for (int j = 0; j < slice; j++) {
				slicedSolution.setObjective(j, worse(pointList.get(k), pointList.get(k + i + 1), j));
			}
			
			result.add(slicedSolution);
		}
		
		return result;
	}
	
	/**
	 * Returns the worse objective value between the two points.  Since all objectives are minimized, this returns
	 * the larger value.
	 * 
	 * @param point1 the first point
	 * @param point2 the second point
	 * @param objective the objective to compare
	 * @return the worse (larger) objective value
	 */
	private double worse(Solution point1, Solution point2, int objective) {
		double objective1 = point1.getObjective(objective);
		double objective2 = point2.getObjective(objective);
		
		return objective1 > objective2 ? objective1 : objective2;
	}
	
	/**
	 * Comparator to sort solutions so they are monotonically improving (decreasing) in the last objective.
	 * Ties are broken by considering other objectives.
	 */
	private class WFGObjectiveComparator implements Comparator<Solution> {
		
		private int slice;
		
		public WFGObjectiveComparator(int slice) {
			super();
			this.slice = slice;
		}

		@Override
		public int compare(Solution solution1, Solution solution2) {
			for (int i = slice - 1; i >= 0; i--) {
				int flag = Double.compare(solution1.getObjective(i), solution2.getObjective(i));

				if (flag != 0) {
					return -flag;
				}
			}
			
			return 0;
		}
		
	}

}
