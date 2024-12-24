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
package org.moeaframework.util.weights;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.moeaframework.core.Settings;
import org.moeaframework.util.io.MatrixIO;

/**
 * Produces a fixed set of weights, typically loaded from a file.
 */
public class FixedWeights implements WeightGenerator {
	
	/**
	 * The weights.
	 */
	private final List<double[]> weights;
	
	/**
	 * Constructs a new weight using a fixed set of weights.
	 * 
	 * @param weights the fixed set of weights
	 * @throws IllegalArgumentException if the weights are not valid
	 */
	public FixedWeights(double[][] weights) {
		this(Arrays.asList(weights));
	}
	
	/**
	 * Constructs a new weight using a fixed set of weights.
	 * 
	 * @param weights the fixed set of weights
	 * @throws IllegalArgumentException if the weights are not valid
	 */
	public FixedWeights(List<double[]> weights) {
		super();
		this.weights = weights;
		
		if (weights.size() > 0) {
			int expectedLength = weights.get(0).length;
			
			for (double[] weight : weights) {
				double sum = 0.0;
				
				if (weight.length != expectedLength) {
					throw new IllegalArgumentException("Invalid weight vector (incorrect length): " +
							Arrays.toString(weight));
				}
				
				for (int i = 0; i < weight.length; i++) {
					if (weight[i] < 0.0 || weight[i] > 1.0) {
						throw new IllegalArgumentException("Invalid weight vector (values must be between 0.0 and 1.0): " +
								Arrays.toString(weight));
					}
					
					sum += weight[i];
				}
				
				if (Math.abs(sum - 1.0) > Settings.EPS) {
					throw new IllegalArgumentException("Invalid weight vector (values must sum to 1.0): " +
							Arrays.toString(weight));
				}
			}
		}
	}

	@Override
	public int size() {
		return weights.size();
	}

	@Override
	public List<double[]> generate() {
		List<double[]> result = new ArrayList<>();
		
		for (double[] weight : weights) {
			result.add(weight.clone());
		}
		
		return result;
	}
	
	/**
	 * Saves the weights to a file.
	 * 
	 * @param file the file
	 * @throws IOException if an I/O error occurred
	 */
	public void save(File file) throws IOException {
		MatrixIO.save(file, weights);
	}
	
	/**
	 * Saves the weights to a writer.
	 * 
	 * @param writer the writer
	 */
	public void save(Writer writer) {
		MatrixIO.save(writer, weights);
	}
	
	/**
	 * Loads the weights from a file.
	 * 
	 * @param file the file
	 * @return the loaded weights
	 * @throws IllegalArgumentException if the weights are not valid
	 * @throws IOException if an I/O error occurred
	 */
	public static FixedWeights load(File file) throws IOException {
		return new FixedWeights(MatrixIO.load(file));
	}
	
	/**
	 * Loads the weights from a reader.
	 * 
	 * @param reader the reader
	 * @return the loaded weights
	 * @throws IllegalArgumentException if the weights are not valid
	 * @throws IOException if an I/O error occurred
	 */
	public static FixedWeights load(Reader reader) throws IOException {
		return new FixedWeights(MatrixIO.load(reader));
	}

}
