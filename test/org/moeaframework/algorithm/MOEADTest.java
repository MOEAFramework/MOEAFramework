/* Copyright 2009-2012 David Hadka
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
package org.moeaframework.algorithm;

import java.io.IOException;
import java.util.Properties;
import jmetal.base.Operator;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.mutation.MutationFactory;
import jmetal.metaheuristics.moead.MOEAD;
import jmetal.util.JMException;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.jmetal.JMetalAlgorithmAdapter;
import org.moeaframework.algorithm.jmetal.JMetalProblemAdapter;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.util.TypedProperties;

/**
 * 
 */
@Ignore
public class MOEADTest {
	
	private static class MOEADFactory extends AlgorithmFactory {

		@Override
		public synchronized Algorithm getAlgorithm(String name,
				Properties properties, Problem problem) {
			if (name.equalsIgnoreCase("MOEAD-JMetal")) {
				try {
				TypedProperties tp = new TypedProperties(properties);
				JMetalProblemAdapter adapter = new JMetalProblemAdapter(problem);
				MOEAD algorithm = new MOEAD(adapter);
			    
			    // Algorithm parameters
			    algorithm.setInputParameter("populationSize",tp.getInt("populationSize", 100));
			    algorithm.setInputParameter("maxEvaluations",tp.getInt("maxEvaluations", 10000));
			    
			    // Directory with the files containing the weight vectors used in 
			    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
			    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
			    // of CS & EE, University of Essex, 02/2009.
			    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
			    algorithm.setInputParameter("dataDirectory",
			    "C:\\Users\\David\\Desktop\\Weight");
			    
			    // Crossover operator 
			    Operator crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover");                   
			    crossover.setParameter("CR", 0.1);                   
			    crossover.setParameter("F", 0.5);
			    
			    // Mutation operator
			    Operator mutation = MutationFactory.getMutationOperator("PolynomialMutation");                    
			    mutation.setParameter("probability",1.0/problem.getNumberOfVariables());
			    mutation.setParameter("distributionIndex",20.0);  
			    
			    algorithm.addOperator("crossover",crossover);
			    algorithm.addOperator("mutation",mutation);
			    
			    return new JMetalAlgorithmAdapter(algorithm, adapter);
				} catch (JMException e) {
					throw new ProviderNotFoundException(name, e);
				}
			} else {
				return super.getAlgorithm(name, properties, problem);
			}
		}
		
	}
	
	@Test
	public void test() throws IOException {
		String problem = "UF1";
		
		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeAllMetrics()
				.showAggregate()
				.showStatisticalSignificance();
		
		Executor executor = new Executor()
				.withProblem(problem)
				.usingAlgorithmFactory(new MOEADFactory())
				.withProperty("populationSize", 300)
				.withMaxEvaluations(300*33)
				.distributeOnAllCores();
		
		analyzer.addAll("MOEAD", 
				executor.withAlgorithm("MOEAD").runSeeds(50));
		analyzer.addAll("MOEAD-JMetal", 
				executor.withAlgorithm("MOEAD-JMetal").runSeeds(50));
		
		analyzer.printAnalysis();
	}

}
