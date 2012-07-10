/* Copyright 2009-2012 David Hadka
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
package org.moeaframework.core;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link CoreUtils} class.
 */
public class CoreUtilsTest {
	
	@Test
	@Deprecated
	public void testAssertNotNullWithObject() {
		CoreUtils.assertNotNull("test", 0.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	@Deprecated
	public void testAssertNotNullWithNull() {
		CoreUtils.assertNotNull("test", null);
	}
	
	@Test
	@Deprecated
	public void testMerge1() {
		Solution s1 = new Solution(0, 0);
		Solution s2 = new Solution(0, 0);
		Solution s3 = new Solution(0, 0);
		
		Assert.assertArrayEquals(new Solution[] { s1, s2, s3 },
				CoreUtils.merge(s1, new Solution[] { s2, s3 }));
	}
	
	@Test
	@Deprecated
	public void testMerge1Empty() {
		Solution s1 = new Solution(0, 0);
		
		Assert.assertArrayEquals(new Solution[] { s1 },
				CoreUtils.merge(s1, new Solution[] { }));
	}
	
	@Test(expected = NullPointerException.class)
	@Deprecated
	public void testMerge1Null() {
		Solution s1 = new Solution(0, 0);
		CoreUtils.merge(s1, null);
	}
	
	@Test
	@Deprecated
	public void testMerge2() {
		Solution s1 = new Solution(0, 0);
		Solution s2 = new Solution(0, 0);
		Solution s3 = new Solution(0, 0);
		
		Assert.assertArrayEquals(new Solution[] { s1, s2, s3 },
				CoreUtils.merge(new Solution[] { s1 }, 
						new Solution[] { s2, s3 }));
	}
	
	@Test
	@Deprecated
	public void testMerge2Empty1() {
		Solution s1 = new Solution(0, 0);
		
		Assert.assertArrayEquals(new Solution[] { s1 },
				CoreUtils.merge(new Solution[] { }, new Solution[] { s1 }));
	}
	
	@Test
	@Deprecated
	public void testMerge2Empty2() {
		Assert.assertArrayEquals(new Solution[] { },
				CoreUtils.merge(new Solution[] { }, new Solution[] { }));
	}
	
	@Test(expected = NullPointerException.class)
	@Deprecated
	public void testMerge2Null() {
		Solution s1 = new Solution(0, 0);
		CoreUtils.merge((Solution[])null, new Solution[] { s1 });
	}
	
	public void testParseCommand() throws IOException {
		String command = "java -jar \"C:\\Program Files\\Test\\test.jar\" \"\"";
		String[] expected = new String[] { "java", "-jar", 
				"C:\\Program Files\\Test\\test.jar", "\"" };
		String[] actual = CoreUtils.parseCommand(command);
		
		Assert.assertArrayEquals(expected, actual);
	}

}
