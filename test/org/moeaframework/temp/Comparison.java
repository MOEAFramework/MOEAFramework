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
package org.moeaframework.temp;

import java.io.IOException;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.analysis.IndicatorStatistics;
import org.moeaframework.core.indicator.AdditiveEpsilonIndicator;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;

public class Comparison {

	public static void main(String[] args) throws IOException {
		int seeds = 10;
		int maxEvaluations = 10000;
		String problemName = "Poloni";		
		String[] algorithmNames = { "AGE-MOEA-II", "AMOSA", "CMA-ES", "DBEA", "e-MOEA", "e-NSGA-II", "GDE3", "IBEA",
				"MOEA/D", "NSGA-II", "NSGA-III", "OMOPSO", "PAES", "PESA2", "Random", "RVEA", "SPEA2", "SMPSO",
				"SMS-EMOA", "U-NSGA-III", "VEGA", "DifferentialEvolution", "EvolutionStrategy", "SimulatedAnnealing",
				"RSO" };
		
		Problem problem = ProblemFactory.getInstance().getProblem(problemName);
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet(problemName);
		
		AdditiveEpsilonIndicator indicator = new AdditiveEpsilonIndicator(problem, referenceSet);
		IndicatorStatistics statistics = new IndicatorStatistics(indicator);
		
		for (String algorithmName : algorithmNames) {
			for (int seed = 0; seed < seeds; seed++) {
				Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, problem);
				algorithm.run(maxEvaluations);
				statistics.add(algorithmName, algorithm.getResult());
			}
		}

		statistics.display();
	}
	
//	Algorithm             Indicator                Min      Median   Max      IQR (+/-) Count Statistically Similar (a=0.05)                                                                  
//	--------------------- ------------------------ -------- -------- -------- --------- ----- ----------------------------------------------------------------------------------------------- 
//	AGE-MOEA-II           AdditiveEpsilonIndicator 0.000578 0.001202 0.004205 0.001893  10    e-MOEA, e-NSGA-II, NSGA-II, OMOPSO, PESA2, Random, SMPSO                                        
//	AMOSA                 AdditiveEpsilonIndicator 0.001046 0.007595 0.026087 0.013987  10    CMA-ES, DBEA, NSGA-II, NSGA-III, OMOPSO, RSO, RVEA, U-NSGA-III                                  
//	CMA-ES                AdditiveEpsilonIndicator 0.001233 0.024014 0.108544 0.083702  10    AMOSA, DBEA, GDE3, IBEA, MOEA/D, NSGA-II, NSGA-III, PAES, RSO, RVEA, SMS-EMOA, U-NSGA-III, VEGA 
//	DBEA                  AdditiveEpsilonIndicator 0.007256 0.008202 0.008505 0.000284  10    AMOSA, CMA-ES, NSGA-III, RSO, U-NSGA-III                                                        
//	DifferentialEvolution AdditiveEpsilonIndicator 0.102042 0.102042 0.102042 0.000000  10                                                                                                    
//	e-MOEA                AdditiveEpsilonIndicator 0.000018 0.000974 0.002748 0.001964  10    AGE-MOEA-II, e-NSGA-II, NSGA-II, PESA2, Random, SMPSO, SPEA2                                    
//	e-NSGA-II             AdditiveEpsilonIndicator 0.000245 0.001533 0.002986 0.001819  10    AGE-MOEA-II, e-MOEA, NSGA-II, OMOPSO, Random, SMPSO                                             
//	EvolutionStrategy     AdditiveEpsilonIndicator 0.102042 0.102042 0.102042 0.000000  10                                                                                                    
//	GDE3                  AdditiveEpsilonIndicator 0.077859 0.079228 0.166984 0.007173  10    CMA-ES, IBEA, VEGA                                                                              
//	IBEA                  AdditiveEpsilonIndicator 0.003493 0.081373 0.097810 0.007014  10    CMA-ES, GDE3, VEGA                                                                              
//	MOEA/D                AdditiveEpsilonIndicator 0.019061 0.061202 0.115419 0.040559  10    CMA-ES, PAES, SMS-EMOA                                                                          
//	NSGA-II               AdditiveEpsilonIndicator 0.000130 0.002924 0.029171 0.006813  10    AGE-MOEA-II, AMOSA, CMA-ES, e-MOEA, e-NSGA-II, OMOPSO, PESA2, Random, RSO, SMPSO, SPEA2         
//	NSGA-III              AdditiveEpsilonIndicator 0.005846 0.008084 0.008432 0.001143  10    AMOSA, CMA-ES, DBEA, RSO, RVEA, U-NSGA-III                                                      
//	OMOPSO                AdditiveEpsilonIndicator 0.000959 0.002236 0.007983 0.001761  10    AGE-MOEA-II, AMOSA, e-NSGA-II, NSGA-II, SMPSO                                                   
//	PAES                  AdditiveEpsilonIndicator 0.002725 0.077744 0.078585 0.064103  10    CMA-ES, MOEA/D, SMS-EMOA                                                                        
//	PESA2                 AdditiveEpsilonIndicator 0.000116 0.000939 0.001807 0.000947  10    AGE-MOEA-II, e-MOEA, NSGA-II, Random, SMPSO, SPEA2                                              
//	Random                AdditiveEpsilonIndicator 0.000464 0.001024 0.003340 0.000978  10    AGE-MOEA-II, e-MOEA, e-NSGA-II, NSGA-II, PESA2, SMPSO, SPEA2                                    
//	RSO                   AdditiveEpsilonIndicator 0.000791 0.008101 0.025420 0.009097  10    AMOSA, CMA-ES, DBEA, NSGA-II, NSGA-III, RVEA, U-NSGA-III                                        
//	RVEA                  AdditiveEpsilonIndicator 0.007490 0.007665 0.008160 0.000521  10    AMOSA, CMA-ES, NSGA-III, RSO                                                                    
//	SimulatedAnnealing    AdditiveEpsilonIndicator 0.102315 0.107177 0.113022 0.004890  10                                                                                                    
//	SMPSO                 AdditiveEpsilonIndicator 0.000278 0.001683 0.011350 0.004341  10    AGE-MOEA-II, e-MOEA, e-NSGA-II, NSGA-II, OMOPSO, PESA2, Random                                  
//	SMS-EMOA              AdditiveEpsilonIndicator 0.002195 0.032023 0.078914 0.044210  10    CMA-ES, MOEA/D, PAES                                                                            
//	SPEA2                 AdditiveEpsilonIndicator 0.000233 0.000648 0.001327 0.000686  10    e-MOEA, NSGA-II, PESA2, Random                                                                  
//	U-NSGA-III            AdditiveEpsilonIndicator 0.004187 0.008350 0.009122 0.000529  10    AMOSA, CMA-ES, DBEA, NSGA-III, RSO                                                              
//	VEGA                  AdditiveEpsilonIndicator 0.078173 0.082342 0.089916 0.009903  10    CMA-ES, GDE3, IBEA                                                                              

	
}
