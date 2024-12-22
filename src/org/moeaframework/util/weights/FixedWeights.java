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

import org.moeaframework.util.io.MatrixIO;

// TODO: add tests for FixedWeights

/**
 * Produces a fixed set of weights, typically loaded from a file.
 */
public class FixedWeights implements WeightGenerator {
	
	/**
	 * The weights.
	 */
	private final List<double[]> weights;
	
	public FixedWeights(double[][] weights) {
		this(Arrays.asList(weights));
	}
	
	/**
	 * Constructs a new weight generator that generates randomly-sampled weights.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param numberOfPoints the number of weights to generate
	 */
	public FixedWeights(List<double[]> weights) {
		super();
		this.weights = weights;
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
	
	public void save(File file) throws IOException {
		MatrixIO.save(file, weights);
	}
	
	public void save(Writer writer) throws IOException {
		MatrixIO.save(writer, weights);
	}
	
	public static FixedWeights load(File file) throws IOException {
		return new FixedWeights(MatrixIO.load(file));
	}
	
	public static FixedWeights load(Reader reader) throws IOException {
		return new FixedWeights(MatrixIO.load(reader));
	}

}
