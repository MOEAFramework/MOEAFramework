/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.core.operator.real;

import java.util.List;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentCentricVariationTest;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.clustering.Cluster;
import org.moeaframework.util.clustering.Clustering;

@RunWith(CIRunner.class)
public class AdaptiveMetropolisTest extends ParentCentricVariationTest<AdaptiveMetropolis> {
	
	@Override
	public AdaptiveMetropolis createInstance() {
		return new AdaptiveMetropolis(3, TestThresholds.SAMPLES, 1.0);
	}
	
	/**
	 * This is sample output from running Jasper Vrugt's AMALGAM codes in MATLAB that is positive definite.
	 */
	@Test
	public void testVrugtSample() {
		RealMatrix matrix = MatrixUtils.createRealMatrix(new double[][] {
				{ 0.0000,  0.0002, -0.0006,  0.0008,  0.0001,  0.0018,  0.0001,  0.0003,  0.0006, -0.0035 },
				{ 0.0934, -0.0010, -0.0062,  0.0001,  0.0024,  0.0017,  0.0019,  0.0004,  0.0008, -0.0067 },
				{ 0.1435, -0.0047,  0.0007, -0.0051, -0.0049,  0.0054,  0.0052,  0.0018,  0.0016,  0.0014 },
				{ 0.0026,  0.0001, -0.0001,  0.0002,  0.0001,  0.0004, -0.0002, -0.0002,  0.0000, -0.0010 },
				{ 0.0262,  0.0028, -0.0003,  0.0019, -0.0013,  0.0002, -0.0009, -0.0004, -0.0012, -0.0021 },
				{ 0.0432,  0.0005, -0.0006,  0.0008,  0.0001,  0.0018, -0.0015,  0.0003,  0.0006, -0.0035 },
				{ 0.1220, -0.0028, -0.0082,  0.0006,  0.0033, -0.0044, -0.0019, -0.0033, -0.0029,  0.0010 },
				{ 0.0657, -0.0049, -0.0005,  0.0004,  0.0016,  0.0020, -0.0010,  0.0047, -0.0047, -0.0033 },
				{ 0.1594,  0.0001, -0.0001,  0.0026,  0.0058,  0.0004, -0.0001, -0.0002,  0.0000, -0.0018 },
				{ 0.1329,  0.0057, -0.0010, -0.0053,  0.0026,  0.0048,  0.0061,  0.0019,  0.0023, -0.0020 },
				{ 0.0998,  0.0029,  0.0036, -0.0014,  0.0015,  0.0018, -0.0035,  0.0006, -0.0013,  0.0083 },
				{ 0.0160, -0.0003, -0.0009,  0.0008,  0.0006, -0.0003, -0.0010,  0.0008, -0.0005, -0.0033 },
				{ 0.0061, -0.0001, -0.0001, -0.0001,  0.0001,  0.0001,  0.0001,  0.0000,  0.0000, -0.0003 }});
		
		double jumpRate = Math.pow(2.4 / Math.sqrt(10), 2.0);

		RealMatrix actual = new CholeskyDecomposition(
					new Covariance(matrix.scalarMultiply(jumpRate)).getCovarianceMatrix()).getLT();
		
		RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {
				{ 0.0335, -0.0001, -0.0003, -0.0006,  0.0005,  0.0003,  0.0006,  0.0000,  0.0001,  0.0005 },
				{ 0.0000,  0.0017,  0.0005, -0.0002,  0.0004,  0.0002,  0.0001, -0.0002,  0.0005,  0.0003 },
				{ 0.0000,  0.0000,  0.0016, -0.0003, -0.0006,  0.0008, -0.0000,  0.0006,  0.0001,  0.0010 },
				{ 0.0000,  0.0000,  0.0000,  0.0012,  0.0008, -0.0007, -0.0010, -0.0003, -0.0004, -0.0001 },
				{ 0.0000,  0.0000,  0.0000,  0.0000,  0.0009, -0.0001, -0.0001,  0.0003, -0.0002, -0.0001 },
				{ 0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0008,  0.0006,  0.0005,  0.0004, -0.0015 },
				{ 0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0007, -0.0001,  0.0004, -0.0006 },
				{ 0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0005, -0.0005, -0.0003 },
				{ 0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0003, -0.0003 },
				{ 0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0003 }});

		Assert.assertEquals(expected, actual, 0.0001);
	}

	@Test
	public void testFullDistribution() {
		AdaptiveMetropolis am = createInstance();

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0), newSolution(0.0, 1.0), newSolution(1.0, 0.0) };
		Solution[] offspring = am.evolve(parents);

		checkDistribution(parents, offspring);
	}

	@Test
	public void testPartialDistribution() {
		AdaptiveMetropolis am = createInstance();

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0), newSolution(0.0, 1.0), newSolution(0.0, 2.0) };

		Assert.assertEquals(0, am.evolve(parents).length);
	}
	
	/**
	 * Returns the covariance matrix for the specified cluster.
	 * 
	 * @param cluster the cluster
	 * @return the covariance matrix for the specified cluster
	 */
	private RealMatrix getCovariance(Cluster cluster) {
		RealMatrix rm = MatrixUtils.createRealMatrix(cluster.size(), 2);
		
		for (int i=0; i < cluster.size(); i++) {
			rm.setRow(i, cluster.get(i).getPoint());
		}
		
		return new Covariance(rm).getCovarianceMatrix();
	}
	
	/**
	 * Returns the covariance matrix for the specified solutions.
	 * 
	 * @param cluster the cluster
	 * @param jumpRateCoefficient the jump rate coefficient
	 * @return the covariance matrix for the specified solutions
	 */
	private RealMatrix getCovariance(Solution[] parents, double jumpRateCoefficient) {
		RealMatrix rm = MatrixUtils.createRealMatrix(parents.length, 2);
		
		for (int i=0; i<parents.length; i++) {
			rm.setRow(i, RealVariable.getReal(parents[i]));
		}
		
		rm = rm.scalarMultiply(Math.pow(jumpRateCoefficient / Math.sqrt(2), 2.0));
		
		return new Covariance(rm).getCovarianceMatrix();
	}
	
	/**
	 * Tests if each cluster formed around the parents exhibits the same covariance as the parent solutions.
	 */
	@Test
	@Retryable
	public void testCovariance() {
		//the smaller jump rate is used to ensure separation between clusters
		double jumpRateCoefficient = 0.5;
		
		AdaptiveMetropolis am = new AdaptiveMetropolis(3, TestThresholds.SAMPLES, jumpRateCoefficient);

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0), newSolution(0.0, 5.0), newSolution(2.0, 0.0) };
		Solution[] offspring = am.evolve(parents);
		
		List<Cluster> clusters = Clustering.kMeansPlusPlus().clusterVariables(parents.length, offspring);

		for (Cluster cluster : clusters) {
			Assert.assertEquals(getCovariance(cluster), getCovariance(parents, jumpRateCoefficient),
					TestThresholds.LOW_PRECISION);
		}
	}

}
