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
package org.moeaframework.core.attribute;

/**
 * Marker interface for attributes, which is metadata associated with a specific solution.  Classes implementing this
 * interface are simply for convenience when reading or writing these attributes.  While this interface does not
 * mandate any particular implementation, for standardization we recommend each subclass should:
 * <ol>
 *   <li>Specify the class and methods are {@code static} and {@code final}.
 *   <li>Define the following methods:
 *     <ol>
 *       <li>{@code boolean hasAttribute(Solution)}
 *       <li>{@code void setAttribute(Solution, T)}
 *       <li>{@code T getAttribute(Solution)}
 *       <li>{@code void removeAttribute(Solution)} (optional)
 *     </ol>
 * </ol>
 * <p>
 * By flagging the class and methods as {@code static} and {@code final}, we help the JIT compiler optimize the code,
 * possibly in-lining the function calls.  Additionally, we can call these methods directly or pass them as arguments
 * using Java's functional interfaces.
 */
public interface Attribute {

}
