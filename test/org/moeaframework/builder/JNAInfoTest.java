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
package org.moeaframework.builder;

import java.io.IOException;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Capture;
import org.moeaframework.core.FrameworkException;

public class JNAInfoTest {
	
	@Test(expected = MissingOptionException.class)
	public void testMissingOption() throws Exception {
		JNAInfo.main(new String[] { });
	}
	
	@Test(expected = MissingArgumentException.class)
	public void testFindLibMissingArg() throws Exception {
		JNAInfo.main(new String[] { "--findLib" });
	}

	@Test(expected = MissingArgumentException.class)
	public void testTestProblemMissingArg() throws Exception {
		JNAInfo.main(new String[] { "--testProblem" });
	}
	
	@Test(expected = MissingArgumentException.class)
	public void testLibNameMissingArg() throws Exception {
		JNAInfo.main(new String[] { "--libName" });
	}
	
	@Test(expected = FrameworkException.class)
	public void testFindLibMissingLibrary() throws Exception {
		JNAInfo.main(new String[] { "--findLib", "foo" });
	}
	
	@Test(expected = FrameworkException.class)
	public void testTestProblemMissingLibrary() throws Exception {
		JNAInfo.main(new String[] { "--testProblem", "foo" });
	}
	
	@Test
	public void testLibName() throws IOException {
		String actual = Capture.output(JNAInfo.class, "--libName", "TestProblem").toString().trim();
		String expected = SystemUtils.IS_OS_WINDOWS ? "TestProblem.dll" :
			SystemUtils.IS_OS_MAC ? "libTestProblem.dylib" :
			"libTestProblem.so";
		
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testSysArch() throws IOException {
		String actual = Capture.output(JNAInfo.class, "--sysArch").toString().trim();
		String expected = SystemUtils.IS_OS_WINDOWS ? "win32-x86-64" :
				SystemUtils.IS_OS_MAC ? "darwin-x86-64" :
				"linux-x86-64";
		
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testLegacy() throws IOException {
		String actual = Capture.output(JNAInfo.class, "--libName", "--problem", "TestProblem").toString().trim();
		String expected = SystemUtils.IS_OS_WINDOWS ? "TestProblem.dll" :
			SystemUtils.IS_OS_MAC ? "libTestProblem.dylib" :
			"libTestProblem.so";
		
		Assert.assertEquals(expected, actual);
	}

}
