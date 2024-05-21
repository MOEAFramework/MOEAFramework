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
 * Marker interface for accessing attributes.  Attributes are metadata associated with a specific solution.  While one
 * can call the {@link org.moeaframework.core.Solution} methods for working with attributes directly, such as
 * {@link org.moeaframework.core.Solution#getAttribute(String)}, these attribute classes provide a standardized and
 * type-safe way to access the attributes.
 * <p>
 * These classes are implemented with {@code static} methods.  This design decision was made for performance and
 * simplicity, as an actual class would require extra overhead from calling the constructor and methods.  Static (and
 * final) methods can be resolved at compile-time and often in-lined.  Additionally, these static methods can be used
 * with Java's functional interfaces (see {@code java.util.function}) if more flexibility is required.
 * <p>
 * While this interface does not mandate any particular implementation, for standardization we recommend the following:
 * <ol>
 *   <li>The class should be {@code final}
 *   <li>Define {@code public static final String ATTRIBUTE_NAME} that identifies the key / name of the attribute
 *   <li>Define the following {@code static final} methods:
 *     <ol>
 *       <li>{@code boolean hasAttribute(Solution)}
 *       <li>{@code void setAttribute(Solution, T)}
 *       <li>{@code T getAttribute(Solution)}
 *       <li>{@code void removeAttribute(Solution)} (optional)
 *     </ol>
 * </ol>
 * Where {@code T} is the type of the attribute.  Prefer using primitive types and implement any boxing / un-boxing
 * within the getter and setter.
 */
public interface Attribute {

}
