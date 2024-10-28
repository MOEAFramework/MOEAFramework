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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.io.OutputWriter;
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.spi.ProblemProvider;
import org.moeaframework.core.spi.RegisteredProblemProvider;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.Problem;

public class ParetoFrontConverter<T> {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, IllegalArgumentException, IllegalAccessException {
		ServiceLoader<ProblemProvider> providers = ServiceLoader.load(ProblemProvider.class);
		
		for (ProblemProvider provider : providers) {
			if (provider instanceof RegisteredProblemProvider registeredProvider) {
				Field referenceSetField = FieldUtils.getField(registeredProvider.getClass(), "referenceSetMap", true);
				Map<String, String> referenceSetMap = (Map<String, String>)referenceSetField.get(registeredProvider);
				
				for (String problemName : referenceSetMap.keySet()) {
					System.out.println("Converting " + problemName + "...");
					
					Problem problem = registeredProvider.getProblem(problemName);
					String referenceSet = referenceSetMap.get(problemName);
					
					if (referenceSet == null) {
						continue;
					}
					
					File referenceSetFile = new File(referenceSet);
					File tempFile = TempFiles.createFile();
					
					try (ResultFileReader reader = ResultFileReader.openLegacy(problem, referenceSetFile);
							ResultFileWriter writer = ResultFileWriter.open(problem, tempFile)) {
						ResultEntry entry = reader.next();
						writer.write(new ResultEntry(stripConstraints(entry.getPopulation())));
					}
					
					OutputWriter.replace(tempFile, referenceSetFile);
				}
			}
		}
	}
	
	private static Population stripConstraints(Population population) {
		Population result = new Population();
		
		for (Solution solution : population) {
			result.add(MockSolution.of().withObjectives(solution.getObjectiveValues()));
		}
		
		return result;
	}

}
