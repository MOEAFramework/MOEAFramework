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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assume;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;
import org.moeaframework.TempFiles;
import org.moeaframework.core.Settings;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Variable;

public class TabularDataTest {
	
	private TabularData<Triple<String, Integer, Variable>> data;
	
	private TabularData<String> escapedData;
	
	private String expectedOutput;
	
	private String expectedCsv;
	
	private String expectedMarkdown;
	
	private String expectedLatex;
	
	private String expectedJson;
	
	private String expectedARFF;
		
	@Before
	public void setUp() {
		List<Triple<String, Integer, Variable>> rawData = new ArrayList<>();
		rawData.add(Triple.of("foo", 1, new RealVariable(0.5, 0.0, 1.0)));
		rawData.add(Triple.of("bar", Integer.MAX_VALUE, new BinaryIntegerVariable(5, 0, 10)));
		
		data = new TabularData<>(rawData);
		data.addColumn(new Column<>("String", Triple::getLeft));
		data.addColumn(new Column<>("Integer", Triple::getMiddle));
		data.addColumn(new Column<>("Variable", Triple::getRight));
		
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
		
		expectedJson = String.join("", new String[] {
				"[",
				"{",
				"\"String\":\"foo\",",
				"\"Integer\":1,",
				"\"Variable\":0.500000",
				"},",
				"{",
				"\"String\":\"bar\",",
				"\"Integer\":2147483647,",
				"\"Variable\":5",
				"}",
				"]"});
		
		expectedARFF = String.join(System.lineSeparator(), new String[] {
				"@RELATION \"MOEA Framework Dataset\"",
				"@ATTRIBUTE String STRING",
				"@ATTRIBUTE Integer NUMERIC",
				"@ATTRIBUTE Variable NUMERIC",
				"@DATA",
				"foo, 1, 0.500000",
				"bar, 2147483647, 5" });
		
		List<String> rawEscapedData = new ArrayList<>();
		rawEscapedData.add("foo\"\\\t\r\n|&bar");
			
		escapedData = new TabularData<>(rawEscapedData);
		escapedData.addColumn(new Column<>("key", x -> x));
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
		
		Capture.stream(ps -> data.display(ps)).assertEqualsNormalized(expectedOutput);
	}
	
	@Test
	public void testPlaintext() throws IOException {
		Capture.stream(ps -> data.display(ps)).assertEqualsNormalized(expectedOutput);
	}
	
	@Test
	public void testCsv() throws IOException {
		Capture.stream(ps -> data.display(TableFormat.CSV, ps)).assertEqualsNormalized(expectedCsv);
	}
	
	@Test
	public void testMarkdown() throws IOException {
		Capture.stream(ps -> data.display(TableFormat.Markdown, ps)).assertEqualsNormalized(expectedMarkdown);
	}
	
	@Test
	public void testLatex() throws IOException {
		Capture.stream(ps -> data.display(TableFormat.Latex, ps)).assertEqualsNormalized(expectedLatex);
	}
	
	@Test
	public void testJson() throws IOException {
		Capture.stream(ps -> data.display(TableFormat.Json, ps)).assertEqualsNormalized(expectedJson);
	}
	
	@Test
	public void testARFF() throws IOException {
		Capture.stream(ps -> data.display(TableFormat.ARFF, ps)).assertEqualsNormalized(expectedARFF);
	}
	
	@Test
	public void testSavePlaintext() throws IOException {
		Capture.file(f -> data.save(TableFormat.Plaintext, f)).assertEqualsNormalized(expectedOutput);
	}
	
	@Test
	public void testSaveCsv() throws IOException {
		Capture.file(f -> data.save(TableFormat.CSV, f)).assertEqualsNormalized(expectedCsv);
	}
	
	@Test
	public void testSaveMarkdown() throws IOException {
		Capture.file(f -> data.save(TableFormat.Markdown, f)).assertEqualsNormalized(expectedMarkdown);
	}
	
	@Test
	public void testSaveLatex() throws IOException {
		Capture.file(f -> data.save(TableFormat.Latex, f)).assertEqualsNormalized(expectedLatex);
	}
	
	@Test
	public void testSaveJson() throws IOException {
		Capture.file(f -> data.save(TableFormat.Json, f)).assertEqualsNormalized(expectedJson);
	}
	
	@Test
	public void testSaveARFF() throws IOException {
		Capture.file(f -> data.save(TableFormat.ARFF, f)).assertEqualsNormalized(expectedARFF);
	}
	
	@Test
	public void testJsonUsingPython() throws IOException {
		Assume.assumePythonExists();
		
		File tempFile = TempFiles.createFile();
		data.save(TableFormat.Json, tempFile);
		
		ProcessBuilder processBuilder = new ProcessBuilder()
				.command(Settings.getPythonCommand(), "-c", "import json; f=open(r'" + tempFile.getAbsolutePath() +
						"'); df = json.load(f); print(df); f.close();");
		
		CaptureResult result = Capture.output(processBuilder);
		
		System.out.println(result.toString());
		result.assertSuccessful();
	}
	
	@Test
	public void testJsonUsingPandas() throws IOException, InterruptedException {
		Assume.assumePythonExists();
		
		File tempFile = TempFiles.createFile();
		data.save(TableFormat.Json, tempFile);
		
		ProcessBuilder processBuilder = new ProcessBuilder()
				.command(Settings.getPythonCommand(), "-c", "import pandas; df = pandas.read_json(r'" +
						tempFile.getAbsolutePath() + "'); print(df);");
		
		CaptureResult result = Capture.output(processBuilder);
		
		System.out.println(result.toString());
		result.assertSuccessful();
	}
	
	@Test
	public void testPlaintextEscaping() throws IOException {
		Capture.stream(ps -> escapedData.display(ps)).assertEqualsNormalized("key\n----------\nfoo\"\\|&bar");
	}
	
	@Test
	public void testCsvEscaping() throws IOException {
		Capture.stream(ps -> escapedData.display(TableFormat.CSV, ps)).assertEqualsNormalized("key\n\"foo\"\"\\\t\r\n|&bar\"");
	}
	
	@Test
	public void testMarkdownEscaping() throws IOException {
		Capture.stream(ps -> escapedData.display(TableFormat.Markdown, ps)).assertEqualsNormalized("key\n----------------\nfoo\"\\\\&#124;&bar");
	}
	
	@Test
	public void testLatexEscaping() throws IOException {
		Capture.stream(ps -> escapedData.display(TableFormat.Latex, ps)).assertEqualsNormalized("\\begin{tabular}{|l|}\n\\hline\nkey \\\\\n\\hline\nfoo\"\\|\\&bar \\\\\n\\hline\n\\end{tabular}");
	}
	
	@Test
	public void testJsonEscaping() throws IOException {
		Capture.stream(ps -> escapedData.display(TableFormat.Json, ps)).assertEqualsNormalized("[{\"key\":\"foo\\\"\\\\\\t\\r\\n|&bar\"}]");
	}

}
