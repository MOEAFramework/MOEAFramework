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
package org.moeaframework.examples.temp;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.Timing;

//Timer   Min      Mean     Max       Count 
//------- -------- -------- --------- ----- 
//DTLZ2_2 9.707216 9.851910 10.438754 20    


//Timer   Min       Mean      Max       Count 
//------- --------- --------- --------- ----- 
//DTLZ2_2 10.856589 11.068600 12.436745 20  

public class Performance {

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
	
}
