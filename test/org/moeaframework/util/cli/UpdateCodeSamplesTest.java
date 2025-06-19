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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.cli.UpdateCodeSamples.CodeProcessor;
import org.moeaframework.util.cli.UpdateCodeSamples.Document;
import org.moeaframework.util.cli.UpdateCodeSamples.ExecProcessor;
import org.moeaframework.util.cli.UpdateCodeSamples.FileFormatter;
import org.moeaframework.util.cli.UpdateCodeSamples.Java;
import org.moeaframework.util.cli.UpdateCodeSamples.MarkdownFormatter;
import org.moeaframework.util.cli.UpdateCodeSamples.ParsingException;
import org.moeaframework.util.cli.UpdateCodeSamples.Plaintext;
import org.moeaframework.util.cli.UpdateCodeSamples.PlotProcessor;
import org.moeaframework.util.cli.UpdateCodeSamples.ProcessorInstruction;
import org.moeaframework.util.cli.UpdateCodeSamples.ShellScript;

public class UpdateCodeSamplesTest {
	
	private UpdateCodeSamples utility;
	
	@Before
	public void setUp() {
		utility = new UpdateCodeSamples();
	}
	
	@After
	public void tearDown() {
		utility = null;
	}
	
	@Test
	public void testGetLanguage() {
		Assert.assertInstanceOf(Java.class, utility.getLanguage(new File("Example1.java")));
		Assert.assertInstanceOf(ShellScript.class, utility.getLanguage(new File("Example1.sh")));
		Assert.assertInstanceOf(Plaintext.class, utility.getLanguage(new File("Example1.foo")));
	}
	
	@Test
	public void testGetLanguageForExtension() {
		Assert.assertInstanceOf(Java.class, utility.getLanguageForExtension("java"));
		Assert.assertInstanceOf(Java.class, utility.getLanguageForExtension("JAVA"));
		Assert.assertInstanceOf(Plaintext.class, utility.getLanguageForExtension("foo"));
	}
	
	@Test
	public void testGetFileFormatter() {
		Assert.assertInstanceOf(MarkdownFormatter.class, utility.getFileFormatter(new File("intro.md")));
		Assert.assertNull(utility.getFileFormatter(new File("intro.foo")));
	}
	
	@Test
	public void testGetProcesssor() {
		Assert.assertInstanceOf(CodeProcessor.class, utility.getProcessor("code"));
		Assert.assertInstanceOf(ExecProcessor.class, utility.getProcessor("exec"));
		Assert.assertInstanceOf(PlotProcessor.class, utility.getProcessor("plot"));
		Assert.assertThrows(IllegalArgumentException.class, () -> utility.getProcessor("foo"));
	}

	@Test
	public void testStripCommentsAndLines() throws IOException {
		String input = """
				/**
				 * Test multi-line Javadoc comment.
				 * @see Foo
				 */
				public class TestClass {
				
					/*
					 * C style block comment.
					 */
					private void test() {
						// C style single-line comment on its own line
						int x = 5;
						int y = 10; // C style comment at end of line
						
						invoke(x, /* inline block comments */ y);
					}
				
				}
				
				
				""";
		
		String expected = """
				```java
				public class TestClass {

					private void test() {
						int x = 5;
						int y = 10;
						
						invoke(x, y);
					}
				
				}
				```
				""";
		
		Assert.assertEqualsNormalized(expected, format("<!-- :code: src=Example.java -->", input));
	}
	
	@Test
	public void testReplaceTabsWithSpaces() throws IOException {
		String input = """
				public void test() {
				\tint x = 5;
				}
				""";
		
		String expected = """
				```java
				public void test() {
				    int x = 5;
				}
				```
				""";
		
		Assert.assertEqualsNormalized(expected, format("<!-- :code: src=Example.java -->", input));
	}
	
	@Test
	public void testStripIndentation() throws IOException {
		String input = """
				\t    public void test() {
				\t        int x = 5;
				\t    }
				""";
		
		String expected = """
				```java
				public void test() {
				    int x = 5;
				}
				```
				""";
		
		Assert.assertEqualsNormalized(expected, format("<!-- :code: src=Example.java -->", input));
	}
	
	@Test
	public void testLineNumbers() throws IOException {
		String input = """
				public void test() {
				    int x = 5;
				}
				""";
		
		String allLines = """
				```java
				public void test() {
				    int x = 5;
				}
				```
				""";
		
		String secondLine = """
				```java
				int x = 5;
				```
				""";
		
		String firstTwoLines = """
				```java
				public void test() {
				    int x = 5;
				```
				""";
		
		String lastTwoLines = """
				```java
				    int x = 5;
				}
				```
				""";
		
		Assert.assertEqualsNormalized(allLines, format("<!-- :code: src=Example.java -->", input));
		Assert.assertEqualsNormalized(allLines, format("<!-- :code: src=Example.java lines=: -->", input));
		Assert.assertEqualsNormalized(allLines, format("<!-- :code: src=Example.java lines=1:3 -->", input));
		Assert.assertEqualsNormalized(secondLine, format("<!-- :code: src=Example.java lines=2 -->", input));
		Assert.assertEqualsNormalized(firstTwoLines, format("<!-- :code: src=Example.java lines=:2 -->", input));
		Assert.assertEqualsNormalized(lastTwoLines, format("<!-- :code: src=Example.java lines=-2: -->", input));
		Assert.assertEqualsNormalized(firstTwoLines, format("<!-- :code: src=Example.java lines=:-1 -->", input));
	}
	
	@Test
	public void testIdentifier() throws IOException {
		String input = """
				public class Foo {
				    // begin-example: foo
					public void test() {
					    int x = 5;
					}
					// end-example: foo
				}
				""";
		
		String expected = """
				```java
				public void test() {
				    int x = 5;
				}
				```
				""";
		
		Assert.assertEqualsNormalized(expected, format("<!-- :code: src=Example.java id=foo -->", input));
	}
	
	@Test(expected = ParsingException.class)
	public void testMissingIdentifier() throws IOException {
		String input = """
				public class Foo {
				    // begin-example: foo
					public void test() {
					    int x = 5;
					}
					// end-example: foo
				}
				""";
		
		format("<!-- :code: src=Example.java id=bar -->", input);
	}
	
//	@Test
//	public void testHtml() throws IOException {
//		String input = """
//				public void test() {
//				    int x = 5;
//				}
//				""";
//		
//		String expected = """
//				<pre class="brush: java; toolbar: false;">
//				<![CDATA[
//				public void test() {
//				    int x = 5;
//				}
//				]]>
//				</pre>
//				""";
//		
//		Assert.assertEqualsNormalized(expected, format("test.html", "<!-- :code: src=Example.java -->", input));
//	}
		
	@Test
	public void fullTestWithUpdate() throws Exception {
		fullTest(true);
	}
	
	@Test(expected = FrameworkException.class)
	public void fullTestWithValidateOnly() throws Exception {
		fullTest(false);
	}
	
	public void fullTest(boolean update) throws Exception {
		File codeFile = TempFiles.createFileWithExtension(".java").withContent("""
				public void test() {
				    int x = 5;
				}
				""");
		
		File markdownFile = TempFiles.createFileWithExtension(".md").withContent("""
				# Test
				This is a test.
				
				""" +
				"<!-- :code: src=" + codeFile.getAbsolutePath() + " -->" +
				"""


				```java
				old content
				```
				""");
		
		String expected = """
				# Test
				This is a test.
				
				""" +
				"<!-- :code: src=" + codeFile.getAbsolutePath() + " -->" +
				"""


				```java
				public void test() {
				    int x = 5;
				}
				```
				""";
		
		List<String> args = new ArrayList<>();
		args.add(markdownFile.getAbsolutePath());
		
		if (update) {
			args.add(0, "--update");
		}
		
		utility.start(args.toArray(new String[0]));
		
		Assert.assertEqualsNormalized(expected, Files.readString(markdownFile.toPath()));
	}
	
	private String format(String instruction, String content) throws IOException {
		return format("test.md", instruction, content);
	}
	
	private String format(String templateFilename, String instruction, String content) throws IOException {
		File templateFile = new File(templateFilename);
		FileFormatter formatter = utility.getFileFormatter(templateFile);
		
		ProcessorInstruction options = formatter.tryParseProcessorInstruction(templateFile, new Document(instruction), 1);
		Assert.assertNotNull(options);
		
		Document document = new Document(content);
		options.formatCode(document);
		return document.toString();
	}

}
