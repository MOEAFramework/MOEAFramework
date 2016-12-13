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
package org.moeaframework.algorithm.single;

import java.util.Comparator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;

/**
 * Compares solutions based on a computed aggregate fitness from the objective
 * values.  Examples could include weighted linear, weighted min-max, or
 * vector angle distance scaling (VADS).
 * <p>
 * Implementation Note: This interface extends both the
 * {@link DominanceComparator} and {@code Comparator<Solution>} interfaces.
 * Since both of these interfaces provide identical
 * {@code int compare(Solution, Solution)} methods, you may encounter a
 * compilation error indicating the use of the {@code compare} method is
 * ambiguous.  This is the result of an important distinction between
 * {@code DominanceComparator} and {@code Comparator<Solution>}.
 * {@link DominanceComparator} induces a <emph>partial</emph> ordering while
 * {@code Comparator<Solution>} provides a <emph>total</emph> ordering.  In
 * general, the two can not be interchanged except in cases where the
 * {@code DominanceComparator} produces a total ordering, which is the case
 * here.  However, you will need to cast to one of these two interfaces in order
 * to invoke the {@code compare} method and avoid the compilation error.
 */
public interface AggregateObjectiveComparator extends DominanceComparator, Comparator<Solution> {

}
