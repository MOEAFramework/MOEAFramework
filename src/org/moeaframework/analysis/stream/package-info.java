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

/**
 * Streaming API for manipulating and analyzing data.
 * <p>
 * This API is inspired by Java's {@link java.util.stream.Stream} API, and uses it under the covers, with a few
 * modifications.  First and foremost, Java streams only allow one sequence of operations per stream, as it only
 * evaluates the results when reaching a terminal operation.  This code, instead, materializes the intermediate result
 * after each operation.
 */
package org.moeaframework.analysis.stream;