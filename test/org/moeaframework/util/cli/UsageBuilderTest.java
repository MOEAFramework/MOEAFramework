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
package org.moeaframework.util.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Settings;

public class UsageBuilderTest {

	@Test
	public void testUtility() throws Exception {
		String usage = new UsageBuilder()
			.executable(Settings.getCLIExecutable())
			.mainClass(TestExamples.class)
			.positionalArgs("src", "dest")
			.build();
		
		Assert.assertEquals("java -classpath \"lib/*\" " + TestExamples.class.getName() + " <src> <dest>", usage);
	}
	
	@Test
	public void testCommand() throws Exception {
		String usage = new UsageBuilder()
			.executable("./cli")
			.command("TestExamples")
			.command("command")
			.positionalArgs("src", "dest")
			.build();
		
		Assert.assertEquals("./cli TestExamples command <src> <dest>", usage);
	}
	
	@Test
	public void testAllOptions() throws Exception {
		Options options = new Options();
		options.addOption(Option.builder("f").build());
		options.addOption(Option.builder("b").hasArg().argName("val").build());
		options.addOption(Option.builder("r").hasArg().argName("val").required().build());
		
		String usage = new UsageBuilder()
			.executable("./cli")
			.command("TestExamples")
			.options(options)
			.positionalArgs("src", "dest")
			.optionStyle(OptionStyle.ALL)
			.build();
		
		Assert.assertEquals("./cli TestExamples [-b <val>] [-f] -r <val> <src> <dest>", usage);
	}
	
	@Test
	public void testRequiredOnlyOptions() throws Exception {
		Options options = new Options();
		options.addOption(Option.builder("f").build());
		options.addOption(Option.builder("b").hasArg().argName("val").build());
		options.addOption(Option.builder("r").hasArg().argName("val").required().build());
		
		String usage = new UsageBuilder()
			.executable("./cli")
			.command("TestExamples")
			.options(options)
			.positionalArgs("src", "dest")
			.optionStyle(OptionStyle.REQUIRED_ONLY)
			.build();
		
		Assert.assertEquals("./cli TestExamples -r <val> <src> <dest>", usage);
	}
	
	@Test
	public void testDefaultOptions() throws Exception {
		Options options = new Options();
		options.addOption(Option.builder("f").build());
		options.addOption(Option.builder("b").hasArg().argName("val").build());
		options.addOption(Option.builder("r").hasArg().argName("val").required().build());
		
		String usage = new UsageBuilder()
			.executable("./cli")
			.command("TestExamples")
			.options(options)
			.positionalArgs("src", "dest")
			.build();
		
		Assert.assertEquals("./cli TestExamples [options] <src> <dest>", usage);
	}
	
	@Test
	public void testGroup() throws Exception {
		Options options = new Options();
		
		OptionGroup group = new OptionGroup();
		group.addOption(Option.builder("f").build());
		group.addOption(Option.builder("b").hasArg().argName("val").build());
		group.addOption(Option.builder("r").hasArg().argName("val").required().build());
		
		options.addOptionGroup(group);
		
		String usage = new UsageBuilder()
			.executable("./cli")
			.command("TestExamples")
			.options(options)
			.positionalArgs("src", "dest")
			.optionStyle(OptionStyle.ALL)
			.build();
		
		Assert.assertEquals("./cli TestExamples [-f | -b <val> | -r <val>] <src> <dest>", usage);
	}

}
