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
package org.moeaframework;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

public class Assume extends org.junit.Assume {
	
	@SuppressWarnings("deprecation")
	private Assume() {
		super();
	}
	
	public static void assumeFileExists(File file) {
		if (!file.exists()) {
			assumeTrue(file + " does not exist, skipping test", false);
		}
	}
	
	public static void assumePOSIX() {
		if (!SystemUtils.IS_OS_UNIX) {
			assumeTrue("System is not POSIX-compliant, skipping test", false);
		}
	}
	
	public static void assumeMakeExists() {
		assumeTrue("Make is not available on this system, skipping test", Make.verifyMakeExists());
	}

}
