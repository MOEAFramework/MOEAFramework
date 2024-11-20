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
package org.moeaframework.snippet;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.RotatedProblem;
import org.moeaframework.problem.ScaledProblem;
import org.moeaframework.problem.TimingProblem;
import org.moeaframework.util.RotationMatrixBuilder;

public class ProblemSnippet {

	@Test
	public void UF1() {
		// begin-example: problem-no-args
		Problem problem = new UF1();
		// end-example: problem-no-args
		
		Assert.assertNotNull(problem);
	}
	
	@Test
	public void DTLZ2_3() {
		// begin-example: problem-with-args
		Problem problem = new DTLZ2(3);
		// end-example: problem-with-args
		
		Assert.assertNotNull(problem);
	}
	
	@Test
	public void BBOB2016() {
		// begin-example: bbob-2016-problem
		Problem problem = ProblemFactory.getInstance().getProblem("bbob-biobj(bbob_f1_i2_d5,bbob_f21_i2_d5)");
		// end-example: bbob-2016-problem
		
		Assert.assertNotNull(problem);
	}
	
	@Test
	public void scaledProblem() {
		// begin-example: scaled-problem
		Problem problem = new ScaledProblem(new DTLZ2(2), 2.0);
		// end-example: scaled-problem
		
		Assert.assertNotNull(problem);
	}
	
	@Test
	public void rotatedProblem() {
		// begin-example: rotated-problem
		RotationMatrixBuilder builder = new RotationMatrixBuilder(11);
		builder.rotateAll().withThetas(Math.toRadians(45));
		
		Problem problem = new RotatedProblem(new DTLZ2(2), builder.create());
		// end-example: rotated-problem
		
		Assert.assertNotNull(problem);
	}
	
	@Test
	public void timingProblem() {
		// begin-example: timing-problem
		TimingProblem problem = new TimingProblem(new DTLZ2(2));
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(10000);
		
		System.out.println(problem.getTotalNFE() + " evaluations took " + problem.getTotalSeconds() + " sec.");
		// end-example: timing-problem
		
		Assert.assertNotNull(problem);
	}
	
}
