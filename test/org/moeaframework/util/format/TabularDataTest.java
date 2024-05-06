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
package org.moeaframework.util.format;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.RealVariable;

public class TabularDataTest {
	
	private TabularData<Triple<String, Integer, Variable>> data;
	
	private String expectedOutput;
	
	private String expectedCsv;
	
	private String expectedMarkdown;
	
	private String expectedLatex;
	
	@Before
	public void setUp() {
		List<Triple<String, Integer, Variable>> rawData = new ArrayList<Triple<String, Integer, Variable>>();
		rawData.add(Triple.of("foo", 1, new RealVariable(0.5, 0.0, 1.0)));
		rawData.add(Triple.of("bar", Integer.MAX_VALUE, new BinaryIntegerVariable(5, 0, 10)));
		
		data = new TabularData<>(rawData);
		data.addColumn(new Column<Triple<String, Integer, Variable>, String>("String", d -> d.getLeft()));
		data.addColumn(new Column<Triple<String, Integer, Variable>, Integer>("Integer", d -> d.getMiddle()));
		data.addColumn(new Column<Triple<String, Integer, Variable>, Variable>("Variable", d -> d.getRight()));
		
		expectedOutput = String.join(System.lineSeparator(), new String[] {
				"String Integer    Variable ",
				"------ ---------- -------- ",
				"foo    1          0.500000 ",
				"bar    2147483647 5        " });
		
		expectedCsv = String.join(System.lineSeparator(), new String[] {
				"String, Integer, Variable",
				"foo, 1, 0.500000",
				"bar, 2147483647, 5" });
		
		expectedMarkdown = String.join(System.lineSeparator(), new String[] {
				"String | Integer    | Variable",
				"------ | ---------- | --------",
				"foo    | 1          | 0.500000",
				"bar    | 2147483647 | 5" });
		
		expectedLatex = String.join(System.lineSeparator(), new String[] {
				"\\begin{tabular}{|lll|}",
				"  \\hline",
				"  String & Integer    & Variable \\\\",
				"  \\hline",
				"  foo    & 1          & 0.500000 \\\\",
				"  bar    & 2147483647 & 5        \\\\",
				"  \\hline",
				"\\end{tabular}"});
	}
	
	@Test
	public void testDisplay() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 PrintStream ps = new PrintStream(baos)) {
			data.display(ps);
			Assert.assertEqualsNormalized(expectedOutput, baos.toString());
		}
	}
	
	@Test
	public void testCustomFormatter() throws IOException {
		data.addFormatter(new Formatter<String>() {

			@Override
			public Class<String> getType() {
				return String.class;
			}

			@Override
			public String format(Object value) {
				return value.toString().toUpperCase();
			}
			
		});
		
		expectedOutput = expectedOutput.replace("foo", "FOO").replace("bar", "BAR");
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 PrintStream ps = new PrintStream(baos)) {
			data.display(ps);
			Assert.assertEqualsNormalized(expectedOutput, baos.toString());
		}
	}
	
	@Test
	public void testCsv() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 PrintStream ps = new PrintStream(baos)) {
			data.toCSV(ps);
			Assert.assertEqualsNormalized(expectedCsv, baos.toString());
		}
	}
	
	@Test
	public void testMarkdown() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 PrintStream ps = new PrintStream(baos)) {
			data.toMarkdown(ps);
			Assert.assertEqualsNormalized(expectedMarkdown, baos.toString());
		}
	}
	
	@Test
	public void testLatex() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 PrintStream ps = new PrintStream(baos)) {
			data.toLatex(ps);
			Assert.assertEqualsNormalized(expectedLatex, baos.toString());
			data.toLatex(System.out);
		}
	}
	
	@Test
	public void testSavePlaintext() throws IOException {
		File tempFile = TempFiles.createFile();
		data.save(TableFormat.Plaintext, tempFile);
		Assert.assertEqualsNormalized(expectedOutput, Files.readString(tempFile.toPath(), StandardCharsets.UTF_8));
	}
	
	@Test
	public void testSaveCsv() throws IOException {
		File tempFile = TempFiles.createFile();
		data.save(TableFormat.CSV, tempFile);
		Assert.assertEqualsNormalized(expectedCsv, Files.readString(tempFile.toPath(), StandardCharsets.UTF_8));
	}
	
	@Test
	public void testSaveMarkdown() throws IOException {
		File tempFile = TempFiles.createFile();
		data.save(TableFormat.Markdown, tempFile);
		Assert.assertEqualsNormalized(expectedMarkdown, Files.readString(tempFile.toPath(), StandardCharsets.UTF_8));
	}
	
	@Test
	public void testSaveLatex() throws IOException {
		File tempFile = TempFiles.createFile();
		data.save(TableFormat.Latex, tempFile);
		Assert.assertEqualsNormalized(expectedLatex, Files.readString(tempFile.toPath(), StandardCharsets.UTF_8));
	}

}
