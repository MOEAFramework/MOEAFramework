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
package org.moeaframework.snippet;

import org.junit.Test;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.analysis.stream.DataStream;
import org.moeaframework.analysis.stream.Groupings;
import org.moeaframework.analysis.stream.Measures;
import org.moeaframework.analysis.stream.Partition;
import org.moeaframework.core.PRNG;

public class MigrationSnippet {
	
	@Test
	public void example() {
		// begin-example:stream
		// Generate 1000 random numbers, group into buckets of 0.1, and count the frequency
		Partition<Double, Integer> counts = DataStream.repeat(1000, PRNG::nextDouble)
			.groupBy(Groupings.bucket(0.1))
			.measureEach(Measures.count())
			.sorted();
		
		// Display as a table
		counts.display();
				
		// Render line plot
		new Plot()
			.histogram("frequency", counts)
			.show();
		// end-example:stream
		
		Plot.disposeAll();
	}
	
}
