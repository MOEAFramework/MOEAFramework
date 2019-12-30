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
package org.moeaframework.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CommandLineUtilityTest {
	
	private boolean invoked;
	
	public class MockCommandLineUtility extends CommandLineUtility {

		@SuppressWarnings("static-access")
		@Override
		public Options getOptions() {
			Options options = super.getOptions();
			
			options.addOption(OptionBuilder
					.isRequired()
					.withLongOpt("test")
					.create('t'));
			
			return options;
		}

		@Override
		public void run(CommandLine commandLine) throws Exception {
			invoked = true;
		}
		
	}
	
	@Before
	public void setUp() {
		invoked = false;
	}
	
	@Test
	public void testHelp() throws Exception {
		try {
			new MockCommandLineUtility().start(new String[] { "--help" });
		} catch (ParseException e) {
			// this throws a parse exception due to the missing required option
		}
		
		Assert.assertFalse(invoked);
	}
	
	@Test
	public void testHelpWithValidOption() throws Exception {
		new MockCommandLineUtility().start(new String[] { "--test",
				"--help" });
		Assert.assertFalse(invoked);
	}
	
	@Test
	public void testInvalidOption() throws Exception {
		try {
			new MockCommandLineUtility().start(new String[] { "--invalid" });
		} catch (ParseException e) {
			// this always throws a parse exception due to missing required
			// option and invalid option
		}
		
		Assert.assertFalse(invoked);
	}
	
	@Test
	public void testMissingOption() throws Exception {
		try {
			new MockCommandLineUtility().start(new String[] {});
		} catch (ParseException e) {
			// this always throws a parse exception due to missing required
			// option
		}
		
		Assert.assertFalse(invoked);
	}
	
	@Test
	public void testNormal() throws Exception {
		new MockCommandLineUtility().start(new String[] { "--test" });
		Assert.assertTrue(invoked);
	}

}
