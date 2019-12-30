/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.util.io;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link CommentedLineReader} class.
 */
public class CommentedLineReaderTest {

	/**
	 * Tests if the {@code CommentedLineReader} correctly handles a normal
	 * input.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testExample() throws IOException {
		CommentedLineReader reader = new CommentedLineReader(new StringReader(
				"#comment line\nnon-comment line\n# comment line"));

		Assert.assertEquals("non-comment line", reader.readLine());
		Assert.assertNull(reader.readLine());

		reader.close();
	}
	
	/**
	 * Tests if the {@code CommentedLineReader} correctly handles empty files.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testEmpty1() throws IOException {
		CommentedLineReader reader = new CommentedLineReader(new StringReader(
				""));

		Assert.assertNull(reader.readLine());

		reader.close();
	}
	
	/**
	 * Tests if the {@code CommentedLineReader} correctly handles an input file
	 * containing all commented lines.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testEmpty2() throws IOException {
		CommentedLineReader reader = new CommentedLineReader(new StringReader(
				"#comment line\n# comment line"));

		Assert.assertNull(reader.readLine());

		reader.close();
	}

}
