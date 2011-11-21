/* Copyright 2009-2011 David Hadka
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

import java.io.NotSerializableException;
import java.io.Serializable;

import org.moeaframework.core.Algorithm;

/**
 * Interface for algorithms whose internal state can be saved at regular
 * checkpoints and resumed later.
 */
public interface Resumable extends Algorithm {

	/**
	 * Returns a {@code Serializable} object representing the internal state of
	 * this algorithm.
	 * 
	 * @return a {@code Serializable} object representing the internal state of
	 *         this algorithm
	 * @throws NotSerializableException if this algorithm does not support
	 *         serialization
	 * @throws AlgorithmException if this algorithm has not yet been
	 *         initialized
	 */
	public Serializable getState() throws NotSerializableException;

	/**
	 * Sets the internal state of of this algorithm.
	 * 
	 * @param state the internal state of this algorithm
	 * @throws NotSerializableException if this algorithm does not support
	 *         serialization
	 * @throws AlgorithmException if this algorithm has already been
	 *         initialized
	 */
	public void setState(Object state) throws NotSerializableException;

}
