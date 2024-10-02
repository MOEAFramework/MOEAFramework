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
package org.moeaframework.util.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.io.UpdateCodeSamples.FileType;
import org.moeaframework.util.io.UpdateCodeSamples.FormatFlag;
import org.moeaframework.util.io.UpdateCodeSamples.FormattingOptions;
import org.moeaframework.util.io.UpdateCodeSamples.Language;

public class UpdateCodeSamplesTest {
	
	@Test
	public void testLanguage() {
		Assert.assertEquals(Language.Java, Language.fromString("Java"));
		Assert.assertEquals(Language.Java, Language.fromString("java"));
	}
	
	
	@Test
	public void testFormatFlag() {
		EnumSet<FormatFlag> flags = FormatFlag.fromFormatString("{KeepIndentation,KeepTabs}");
		Assert.assertTrue(flags.contains(FormatFlag.KeepIndentation));
		Assert.assertTrue(flags.contains(FormatFlag.KeepTabs));
		Assert.assertFalse(flags.contains(FormatFlag.KeepComments));
	}
	
	@Test
	public void testFileType() {
		Assert.assertEquals(FileType.Markdown, FileType.fromExtension("md"));
		Assert.assertEquals(FileType.Html, FileType.fromExtension("HTML"));
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
		
		Assert.assertEqualsNormalized(expected, format(Language.Java, FileType.Markdown, null, input));
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
		
		Assert.assertEqualsNormalized(expected, format(Language.Java, FileType.Markdown, null, input));
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
		
		Assert.assertEqualsNormalized(expected, format(Language.Java, FileType.Markdown, null, input));
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
		
		Assert.assertEqualsNormalized(allLines, format(Language.Java, FileType.Markdown, "[:]", input));
		Assert.assertEqualsNormalized(allLines, format(Language.Java, FileType.Markdown, "[1:3]", input));
		Assert.assertEqualsNormalized(secondLine, format(Language.Java, FileType.Markdown, "[2:2]", input));
		Assert.assertEqualsNormalized(firstTwoLines, format(Language.Java, FileType.Markdown, "[:2]", input));
		Assert.assertEqualsNormalized(lastTwoLines, format(Language.Java, FileType.Markdown, "[-2:]", input));
		Assert.assertEqualsNormalized(firstTwoLines, format(Language.Java, FileType.Markdown, "[:-1]", input));
	}
	
	@Test
	public void testIdentifier() throws IOException {
		String input = """
				public class Foo {
				    //begin-example:foo
					public void test() {
					    int x = 5;
					}
					//end-example:foo
				}
				""";
		
		String expected = """
				```java
				public void test() {
				    int x = 5;
				}
				```
				""";
		
		Assert.assertEqualsNormalized(expected, format(Language.Java, FileType.Markdown, "[foo]", input));
	}
	
	@Test(expected = IOException.class)
	public void testMissingIdentifier() throws IOException {
		String input = """
				public class Foo {
				    //begin-example:foo
					public void test() {
					    int x = 5;
					}
					//end-example:foo
				}
				""";
		
		format(Language.Java, FileType.Markdown, "[bar]", input);
	}
	
	@Test
	public void testHtml() throws IOException {
		String input = """
				public void test() {
				    int x = 5;
				}
				""";
		
		String expected = """
				<pre class="brush: java; toolbar: false;">
				<![CDATA[
				public void test() {
				    int x = 5;
				}
				]]>
				</pre>
				""";
		
		Assert.assertEqualsNormalized(expected, format(Language.Java, FileType.Html, null, input));
	}
	
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
				"<!-- java:" + codeFile.getAbsolutePath() + " [:] -->" +
				"""


				```java
				old content
				```
				""");
		
		String expected = """
				# Test
				This is a test.
				
				""" +
				"<!-- java:" + codeFile.getAbsolutePath() + " [:] -->" +
				"""


				```java
				public void test() {
				    int x = 5;
				}
				```
				""";
		
		List<String> args = new ArrayList<String>();
		args.add(markdownFile.getAbsolutePath());
		
		if (update) {
			args.add(0, "--update");
		}
		
		UpdateCodeSamples updater = new UpdateCodeSamples();
		updater.start(args.toArray(new String[0]));
		
		Assert.assertEqualsNormalized(expected, Files.readString(markdownFile.toPath()));
	}
	
	private String format(Language language, FileType fileType, String lineNumbers, String input) throws IOException {
		FormattingOptions options = new FormattingOptions(language);
		
		if (lineNumbers != null) {
			options.parseLineNumbers(lineNumbers);
		}
		
		List<String> lines = options.format(input, fileType);
		return lines.stream().collect(Collectors.joining(System.lineSeparator()));
	}

}
