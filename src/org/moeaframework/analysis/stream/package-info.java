/* Copyright 2009-2025 David Hadka
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
 * This package provides several data structures for streams, including:
 * <ol>
 *   <li>{@link DataStream} - A stream of values,
 *   <li>{@link Partition} - A stream of key-value pairs, and
 *   <li>{@link Groups} - A stream of keys mapped to a collection of values.
 * </ol>
 * Additionally, this provides static methods for creating {@link Groupings} and aggregating data using
 * {@link Measures}.  While these methods are intended to be used with the data structures, they may also be used
 * directly on the underlying stream.
 * <p>
 * This API is inspired by Java's {@link java.util.stream.Stream} API, and uses it under the covers, with a few
 * modifications:
 * <ul>
 *   <li>Unlike Java streams, multiple operations are permitted on the same data stream.  This is allowed since
 *       intermediate results are materialized after each operation, at the cost of performance.
 *   <li>The original data source can be modified after constructing a data stream, again because intermediate results
 *       are materialized.
 *   <li>Parallel streams are not supported, since this library is not designed to be thread-safe.
 * </ul>
 */
package org.moeaframework.analysis.stream;