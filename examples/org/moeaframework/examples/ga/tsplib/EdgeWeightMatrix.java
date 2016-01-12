/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.examples.ga.tsplib;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Stores the edge weight matrix from a TSPLIB problem instance.
 */
public class EdgeWeightMatrix extends DistanceTable {
	
	/**
	 * The number of nodes represented in this edge weight matrix.
	 */
	private final int size;
	
	/**
	 * The format of this edge weight matrix.  This defines the format stored
	 * in the TSPLIB problem instance.  This implementation converts from this
	 * format to a full matrix.
	 */
	private final EdgeWeightFormat format;
	
	/**
	 * The edge weight matrix.  This edge weights are always stored in a full
	 * matrix, regardless of the input edge weight format.
	 */
	private final double[][] matrix;
	
	/**
	 * Constructs a new, empty edge weight matrix.
	 * 
	 * @param size the number of nodes represented in this edge weight matrix
	 * @param format the format of this edge weight matrix
	 */
	public EdgeWeightMatrix(int size, EdgeWeightFormat format) {
		super();
		this.size = size;
		this.format = format;
		
		matrix = new double[size][size];
	}
	
	/**
	 * Reads the next line from the reader, parses out one or more weights, and
	 * appends the weights to the given queue.
	 * 
	 * @param reader the reader containing the edge weights
	 * @param entries the queue of read but unprocessed edge weights
	 * @throws IOException if an I/O error occurred while reading the edge
	 *         weights
	 */
	private void readNextLine(BufferedReader reader, Queue<Double> entries)
			throws IOException {
		String line = null;
		
		do {
			line = reader.readLine();
			
			if (line == null) {
				throw new EOFException("unexpectedly reached EOF");
			}
		} while ((line = line.trim()).isEmpty());
		
		String[] tokens = line.split("\\s+");
		
		for (int i = 0; i < tokens.length; i++) {
			entries.offer(Double.parseDouble(tokens[i]));
		}
	}
	
	@Override
	public void load(BufferedReader reader) throws IOException {
		Queue<Double> entries = new LinkedList<Double>();
		
		switch (format) {
		case FULL_MATRIX:
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (entries.isEmpty()) {
						readNextLine(reader, entries);
					}
					
					matrix[i][j] = entries.poll();
				}
			}
			
			break;
		case UPPER_ROW:
			for (int i = 0; i < size-1; i++) {
				for (int j = i+1; j < size; j++) {
					if (entries.isEmpty()) {
						readNextLine(reader, entries);
					}

					matrix[i][j] = entries.poll();
					matrix[j][i] = matrix[i][j];
				}
			}
			
			break;
		case UPPER_DIAG_ROW:
			for (int i = 0; i < size; i++) {
				for (int j = i; j < size; j++) {
					if (entries.isEmpty()) {
						readNextLine(reader, entries);
					}

					matrix[i][j] = entries.poll();
					matrix[j][i] = matrix[i][j];
				}
			}
			
			break;
		case LOWER_ROW:
			for (int i = 1; i<size; i++) {
				for (int j = 0; j < i; j++) {
					if (entries.isEmpty()) {
						readNextLine(reader, entries);
					}

					matrix[i][j] = entries.poll();
					matrix[j][i] = matrix[i][j];
				}
			}
			
			break;
		case LOWER_DIAG_ROW:
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < i+1; j++) {
					if (entries.isEmpty()) {
						readNextLine(reader, entries);
					}

					matrix[i][j] = entries.poll();
					matrix[j][i] = matrix[i][j];
				}
			}
			
			break;
		case UPPER_COL:
			for (int j = 1; j < size; j++) {
				for (int i = 0; i < j; i++) {
					if (entries.isEmpty()) {
						readNextLine(reader, entries);
					}

					matrix[i][j] = entries.poll();
					matrix[j][i] = matrix[i][j];
				}
			}
			
			break;
		case UPPER_DIAG_COL:
			for (int j = 0; j < size; j++) {
				for (int i = 0; i < j+1; i++) {
					if (entries.isEmpty()) {
						readNextLine(reader, entries);
					}

					matrix[i][j] = entries.poll();
					matrix[j][i] = matrix[i][j];
				}
			}
			
			break;
		case LOWER_COL:
			for (int j = 0; j < size-1; j++) {
				for (int i = j+1; i < size; i++) {
					if (entries.isEmpty()) {
						readNextLine(reader, entries);
					}

					matrix[i][j] = entries.poll();
					matrix[j][i] = matrix[i][j];
				}
			}
			
			break;
		case LOWER_DIAG_COL:
			for (int j = 0; j < size; j++) {
				for (int i = j; i < size; i++) {
					if (entries.isEmpty()) {
						readNextLine(reader, entries);
					}

					matrix[i][j] = entries.poll();
					matrix[j][i] = matrix[i][j];
				}
			}
			
			break;
		default:
			throw new IllegalArgumentException("unsupported matrix type");
		}
		
		// sanity check
		if (!entries.isEmpty()) {
			throw new IOException("edge weight matrix is longer than expected");
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (j > 0) {
					sb.append(' ');
				}
				
				sb.append(matrix[i][j]);
			}
			
			sb.append('\n');
		}
		
		return sb.toString();
	}
	
	@Override
	public int[] listNodes() {
		int[] nodes = new int[size];
		
		for (int i = 1; i <= size; i++) {
			nodes[i-1] = i;
		}
		
		return nodes;
	}

	@Override
	public int[] getNeighborsOf(int id) {
		int index = 0;
		int[] neighbors = new int[size-1];
		
		if ((id < 1) || (id > size)) {
			throw new IllegalArgumentException("no node with identifier " + id);
		}
		
		for (int i = 1; i <= size; i++) {
			if (i != id) {
				neighbors[index++] = i;
			}
		}
		
		return neighbors;
	}

	@Override
	public double getDistanceBetween(int id1, int id2) {
		if ((id1 < 1) || (id1 > size)) {
			throw new IllegalArgumentException("no node with identifier " +
					id1);
		}
		
		if ((id2 < 1) || (id2 > size)) {
			throw new IllegalArgumentException("no node with identifier " +
					id2);
		}
		
		return matrix[id1-1][id2-1];
	}

}
