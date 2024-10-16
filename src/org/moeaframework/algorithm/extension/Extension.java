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
package org.moeaframework.algorithm.extension;

import org.moeaframework.algorithm.Algorithm;

/**
 * Extensions are a flexible alternative to wrappers when extending or augmenting the functionality of an algorithm.
 * <p>
 * A "wrapper" is a class that encloses an object, typically by passing the nested object in the constructor.  For
 * example, here we wrap the NSGAII algorithm:
 * <pre>
 *     Algorithm algorithm = new NSGAII();
 *     Wrapper wrapper = new Wrapper(algorithm);
 *     wrapper.run(10000);
 * <pre>
 * On the other hand, an extension adds new functionality by plugging into specific "extension points":
 * <pre>
 *     Algorithm algorithm = new NSGAII();
 *     algorithm.addExtension(new Extension());
 *     algorithm.run(10000);
 * </pre>
 * There are uses for both, as highlighted below:
 * <ol>
 *   <li>Wrappers can extend or replace any non-final method of the nested object, whereas extensions can only extend
 *       functionality at specific locations.  However, this also means extensions provide a well-defined "contract"
 *       for how they can modify an algorithm.
 *   <li>Extensions can be added dynamically without impacting user code.  For example, we could add a default logging
 *       extension to all algorithms.
 *   <li>Extensions leave the original type unchanged, whereas with wrappers we need to work with the wrapper type.
 *       The wrapper often needs to either re-implement methods to call the underlying class, or expose the nested
 *       instance (e.g., <code>algorithm.getNestedAlgorithm()</code>).
 *   <li>Extensions offer better encapsulation.  With a wrapper, we potentially reference and call methods directly on
 *       the nested instance, bypassing the wrapper and leading to unexpected results.
 * </ol>
 * All of the methods in this interface have default implementations that no-op.  An extension can simply override the
 * specific methods required.
 */
public interface Extension {
	
	/**
	 * Called when this extension is registered with an algorithm.  This can be used to perform any type checking or
	 * initialization.
	 * 
	 * @param algorithm the algorithm associated with this extension
	 */
	public default void onRegister(Algorithm algorithm) {
		
	}
	
	/**
	 * Called after each step of the algorithm.
	 * 
	 * @param algorithm the algorithm associated with this extension
	 */
	public default void onStep(Algorithm algorithm) {
		
	}
	
	/**
	 * Called when an algorithm is being initialized.
	 * 
	 * @param algorithm the algorithm associated with this extension
	 */
	public default void onInitialize(Algorithm algorithm) {
		
	}
	
	/**
	 * Called after the termination of an algorithm.
	 * 
	 * @param algorithm the algorithm associated with this extension
	 */
	public default void onTerminate(Algorithm algorithm) {
		
	}

}
