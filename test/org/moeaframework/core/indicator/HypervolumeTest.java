/* Copyright 2009-2022 David Hadka
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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.Timing;

public class HypervolumeTest extends IndicatorTest {
	
	@Test
	public void collectTimings() {
		compare("DTLZ2_2");
		compare("DTLZ2_3");
		compare("DTLZ2_4");
		compare("DTLZ2_6");
		compare("DTLZ2_8");
	}
	
	public void compare(String problemName) {
		Problem problem = ProblemFactory.getInstance().getProblem(problemName);
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet(problemName);

		PISAHypervolume pisa = new PISAHypervolume(problem, referenceSet);
		WFGNormalizedHypervolume wfg = new WFGNormalizedHypervolume(problem, referenceSet);
		
		System.out.println(problemName);
		
		for (int i = 0; i < 1; i++) {
			//Timing.startTimer("PISA");
			//double v2 = pisa.evaluate(referenceSet);
			//Timing.stopTimer("PISA");
			
			Timing.startTimer("WFG");
			double v1 = wfg.evaluate(referenceSet);
			Timing.stopTimer("WFG");
			
			//System.out.println("    Iteration " + (i+1) + ": " + v1 + " " + v2);
		}
		
		//System.out.println("    PISA: " + Timing.getStatistics("PISA").getMean() / 1000000000.0 + " sec");
		System.out.println("    WFG:  " + Timing.getStatistics("WFG").getMean() / 1000000000.0 + " sec");
		System.out.println();
	}

}
