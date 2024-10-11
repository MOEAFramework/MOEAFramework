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
package org.moeaframework.examples.timing;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.Timing;

/**
 * Simplest way to solve a multi-objective optimization problem.  Here, we solve
 * the bi-objective DTLZ2 test problem using NSGA-II and display the Pareto front.
 */
public class TimingTest {

	public static void main(String[] args) {
		int seeds = 20;
		int nfe = 1000000;
		String[] problems = new String[] { "DTLZ2_2" }; //, "DTLZ2_3", "UF1", "CF1" };
		
		for (String problem : problems) {
			for (int i = 0; i < seeds; i++) {
				Timing.startTimer(problem);
				
				NSGAII algorithm = new NSGAII(ProblemFactory.getInstance().getProblem(problem));
				algorithm.run(nfe);
				
				Timing.stopTimer(problem);
			}
		}
		
		Timing.display();
	}
	
//	Timer   Min      Mean     Max       Count 
//	------- -------- -------- --------- ----- 
//	DTLZ2_2 9.707216 9.851910 10.438754 20    


//	Timer   Min       Mean      Max       Count 
//	------- --------- --------- --------- ----- 
//	DTLZ2_2 10.856589 11.068600 12.436745 20    


	
// Original (with double[] for objectives and constraints)
//
//	Timer   Min      Mean      Max       Count 
//	------- -------- --------- --------- ----- 
//	DTLZ2_2 6.948848 7.783355  9.825456  20    
//	DTLZ2_3 9.362743 10.263943 11.996527 20    
//	UF1     6.741221 7.195890  8.103312  20    
//	CF1     4.257259 4.446232  5.706008  20    
//
//
// Objective and Constraint classes
//
//	Timer   Min      Mean      Max       Count 
//	------- -------- --------- --------- ----- 
//	DTLZ2_2 7.118115 7.765839  8.351056  20    
//	DTLZ2_3 9.994453 10.617336 11.654345 20    
//	UF1     6.925423 7.217929  7.659797  20    
//	CF1     4.410762 4.705057  5.148216  20    
//   
//
//
// Objective and Constraint classes (with type checking on comparison)
//
//	Timer   Min       Mean      Max       Count 
//	------- --------- --------- --------- ----- 
//	DTLZ2_2 8.863778  9.532351  11.548204 20    
//	DTLZ2_3 12.920419 13.619143 15.087924 20    
//	UF1     8.831276  9.690442  11.857773 20    
//	CF1     4.475237  5.075467  6.416722  20    

	
}
