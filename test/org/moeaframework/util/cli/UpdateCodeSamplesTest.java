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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Capture;
import org.moeaframework.TempFiles;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.cli.UpdateCodeSamples.BlockMatcher;
import org.moeaframework.util.cli.UpdateCodeSamples.CodeProcessor;
import org.moeaframework.util.cli.UpdateCodeSamples.Document;
import org.moeaframework.util.cli.UpdateCodeSamples.ExecProcessor;
import org.moeaframework.util.cli.UpdateCodeSamples.Java;
import org.moeaframework.util.cli.UpdateCodeSamples.Language;
import org.moeaframework.util.cli.UpdateCodeSamples.MarkdownFormatter;
import org.moeaframework.util.cli.UpdateCodeSamples.MethodMatcher;
import org.moeaframework.util.cli.UpdateCodeSamples.ParsingException;
import org.moeaframework.util.cli.UpdateCodeSamples.Plaintext;
import org.moeaframework.util.cli.UpdateCodeSamples.PlotProcessor;
import org.moeaframework.util.cli.UpdateCodeSamples.ShellScript;
import org.moeaframework.util.cli.UpdateCodeSamples.Slice;

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
		Assert.assertInstanceOf(MarkdownFormatter.class, utility.getFileFormatter(new File("INTRO.MD")));
		Assert.assertNull(utility.getFileFormatter(new File("intro.foo")));
	}
	
	@Test
	public void testGetProcesssor() {
		Assert.assertInstanceOf(CodeProcessor.class, utility.getProcessor("code"));
		Assert.assertInstanceOf(ExecProcessor.class, utility.getProcessor("exec"));
		Assert.assertInstanceOf(PlotProcessor.class, utility.getProcessor("plot"));
		Assert.assertThrows(IllegalArgumentException.class, () -> utility.getProcessor("foo"));
	}
	
	public static class BlockMatcherTest {
		
		private BlockMatcher matcher = new BlockMatcher(s -> s.startsWith("```"), s -> s.endsWith("```"));
	
		@Test
		public void testMatch() {
			Document document = new Document("""
					foo
					
					```
					bar
					```
					
					baz
					""");
			
			Slice slice = matcher.scan(1, document);
			
			Assert.assertEquals(3, slice.getStart());
			Assert.assertEquals(5, slice.getEnd());
		}
		
		@Test
		public void testNoMatch() {
			Document document = new Document("""
					foo
					
					baz
					""");
			
			Assert.assertNull(matcher.scan(1, document));
		}
		
		@Test(expected = ParsingException.class)
		public void testNoEnd() {
			Document document = new Document("""
					foo
					
					```
					
					baz
					""");
			
			matcher.scan(1, document);
		}
	
	}
	
	public static class MethodMatcherTest {
		
		private MethodMatcher matcher = new MethodMatcher("foo");
	
		@Test
		public void testMatch() {
			Document document = new Document("""
					import xyz;
					
					public class Foo {
					
						public void foo() {
							bar;
						}
					
					}
					""");
			
			Slice slice = matcher.scan(1, document);
			
			Assert.assertEquals(5, slice.getStart());
			Assert.assertEquals(7, slice.getEnd());
		}
		
		@Test
		public void testComplexSignature() {
			Document document = new Document("""
					import xyz;
					
					public class Foo {
					
						public <T extends Bar> T foo(T[] array, List<T> list) {
							bar;
						}
					
					}
					""");
			
			Slice slice = matcher.scan(1, document);
			
			Assert.assertEquals(5, slice.getStart());
			Assert.assertEquals(7, slice.getEnd());
		}
		
		@Test
		public void testNestedParenthesiss() {
			Document document = new Document("""
					import xyz;
					
					public class Foo {
					
						public void foo() {
							if (bar) {
								baz;
							}
						}
					
					}
					""");
			
			Slice slice = matcher.scan(1, document);
			
			Assert.assertEquals(5, slice.getStart());
			Assert.assertEquals(9, slice.getEnd());
		}
		
		@Test
		public void testNoMatch() {
			Document document = new Document("""
					import xyz;
					
					public class Foo {
					
						public void bar() {
							bar;
						}
					
					}
					""");
			
			Assert.assertNull(matcher.scan(1, document));
		}
		
		@Test(expected = ParsingException.class)
		public void testNoEnd() {
			Document document = new Document("""
					import xyz;
					
					public class Foo {
					
						public void foo() {
							bar;
					""");
			
			System.out.println(matcher.scan(1, document));
		}
	
	}
	
	public static class SliceTest {
		
		@Test
		public void test() {
			Assert.assertEquals(":", Slice.fromString("").toString());
			Assert.assertEquals(":", Slice.fromString(":").toString());
			Assert.assertEquals("1:3", Slice.fromString("1:3").toString());
			Assert.assertEquals("2", Slice.fromString("2").toString());
			Assert.assertEquals(":2", Slice.fromString(":2").toString());
			Assert.assertEquals("2:", Slice.fromString("2:").toString());
			Assert.assertEquals(":-1", Slice.fromString(":-1").toString());
			Assert.assertEquals("-1:", Slice.fromString("-1:").toString());
			Assert.assertEquals("-1", Slice.fromString("-1").toString());
		}
		
		@Test
		public void testResolve() {
			Document document = new Document("\n\n\n\n\n");
			
			Assert.assertEquals("1:5", Slice.fromString(":").resolve(document).toString());
			Assert.assertEquals("1:3", Slice.fromString("1:3").resolve(document).toString());
			Assert.assertEquals("2", Slice.fromString("2").resolve(document).toString());
			Assert.assertEquals("1:2", Slice.fromString(":2").resolve(document).toString());
			Assert.assertEquals("2:5", Slice.fromString("2:").resolve(document).toString());
			Assert.assertEquals("1:4", Slice.fromString(":-1").resolve(document).toString());
			Assert.assertEquals("5", Slice.fromString("-1:").resolve(document).toString());
			Assert.assertEquals("5", Slice.fromString("-1").resolve(document).toString());
		}
		
	}
	
	public static class DocumentTest {
		
		@Test
		public void testEdit() {
			Document document = new Document();
			
			document.insert(1, List.of("bar"));
			Assert.assertEquals("bar\n", document.toString());
			
			document.prepend("foo");
			Assert.assertEquals("foo\nbar\n", document.toString());
			
			document.append("baz");
			Assert.assertEquals("foo\nbar\nbaz\n", document.toString());
			
			document.remove(2);
			Assert.assertEquals("foo\nbaz\n", document.toString());
			
			document.replace(Slice.fromString("1"), List.of("bar"));
			Assert.assertEquals("bar\nbaz\n", document.toString());
		}
		
		@Test
		public void testExtract() {
			Document document = new Document("foo\nbar\nbaz");
			
			Assert.assertEquals("bar\nbaz\n", document.extract(Slice.fromString("2:")).toString());
			Assert.assertEquals("foo\nbar\n", document.extract(Slice.fromString(":-1")).toString());
			Assert.assertEquals("bar\n", document.extract(Slice.fromString("2")).toString());
		}
		
		@Test
		public void testCopy() {
			Document document = new Document("foo\nbar\nbaz");
			Document copy = document.copy();
			
			Assert.assertEquals("foo\nbar\nbaz\n", copy.toString());
			
			document.remove(2);
			copy.remove(3);
			
			Assert.assertEquals("foo\nbaz\n", document.toString());
			Assert.assertEquals("foo\nbar\n", copy.toString());
		}
		
		@Test
		public void testRemoveIndent() {
			Document document1 = new Document("\t  foo\n\t  bar\n");
			document1.removeIndentation();
			Assert.assertEquals("foo\nbar\n", document1.toString());
			
			Document document2 = new Document("\t  foo\n\tbar\n");
			document2.removeIndentation();
			Assert.assertEquals("  foo\nbar\n", document2.toString());
			
			Document document3 = new Document("\t  foo\nbar\n");
			document3.removeIndentation();
			Assert.assertEquals("\t  foo\nbar\n", document3.toString());
		}
		
		@Test
		public void testRemoveLeadingAndTrailingBlankLines() {
			Document document1 = new Document("\n\nfoo\nbar\n\n");
			document1.removeLeadingAndTrailingBlankLines();
			Assert.assertEquals("foo\nbar\n", document1.toString());
			
			Document document2 = new Document("\n  \nfoo\nbar\n\t\n");
			document2.removeLeadingAndTrailingBlankLines();
			Assert.assertEquals("foo\nbar\n", document2.toString());
			
			Document document3 = new Document("foo\nbar");
			document3.removeLeadingAndTrailingBlankLines();
			Assert.assertEquals("foo\nbar\n", document3.toString());
		}
		
		@Test
		public void testReplaceTabsWithSpaces() {
			Document document1 = new Document("\tfoo\n\tbar");
			document1.replaceTabsWithSpaces();
			Assert.assertEquals("    foo\n    bar\n", document1.toString());
			
			Document document2 = new Document("    foo\n\tbar\n\t");
			document2.replaceTabsWithSpaces();
			Assert.assertEquals("    foo\n    bar\n    \n", document2.toString());
			
			Document document3 = new Document("foo\nbar");
			document3.replaceTabsWithSpaces();
			Assert.assertEquals("foo\nbar\n", document3.toString());
		}
		
		@Test
		public void testDiff() throws IOException {
			Document document1 = new Document("foo\nbar");
			Document document2 = new Document("foo\nbar\nbaz");
			
			
			Capture.stream((out) -> {
				Assert.assertFalse(document1.diff(document1.copy(), out));
			}).assertEqualsNormalized("");
			
			Capture.stream((out) -> {
				Assert.assertTrue(document1.diff(document2.copy(), out));
			}).assertEqualsNormalized("      ! ++ baz\n");
			
			Capture.stream((out) -> {
				Assert.assertTrue(document2.diff(document1.copy(), out));
			}).assertEqualsNormalized("      ! -- baz\n");
		}
		
		@Test
		public void testLineSeparator() {
			Assert.assertEquals("foo\nbar\n", new Document("foo\nbar").toString());
			Assert.assertEquals("foo\r\nbar\r\n", new Document("foo\r\nbar").toString());
			Assert.assertEquals("foo\rbar\r", new Document("foo\rbar").toString());
		}
		
	}
	
	public static class JavaTest {
		
		@Test
		public void testStripComments() {
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
					public class TestClass {

						private void test() {
							int x = 5;
							int y = 10;
							
							invoke(x, y);
						}
					
					}
					""";
			
			Document document = new Document(input);
			UpdateCodeSamples utility = new UpdateCodeSamples();
			Language language = utility.getLanguageForExtension("java");
			
			document.transform(language::stripComments);

			Assert.assertEqualsNormalized(expected, document.toString());
		}
		
	}
	
	@Test
	public void testE2EWithLineNumbers() throws Exception {
		String template = """
				<!-- :code: src=${source} lines=3:5 -->
				
				```
				```
				""";
		
		String source = """
				public class Foo {
					
					public void test() {
						int x = 5;
					}
					
				}
				""";
		
		String expected = """
				<!-- :code: src=${source} lines=3:5 -->
				
				```java
				public void test() {
					int x = 5;
				}
				```
				""";
		
		runE2E(template, source, true, expected);
	}
	
	@Test
	public void testE2EWithIdentifier() throws Exception {
		String template = """
				<!-- :code: src=${source} id=foo -->
				
				```
				```
				""";
		
		String source = """
				public class Foo {
					// begin-example: foo
					public void test() {
						int x = 5;
					}
					// end-example: foo
				}
				""";
		
		String expected = """
				<!-- :code: src=${source} id=foo -->
				
				```java
				public void test() {
					int x = 5;
				}
				```
				""";
		
		runE2E(template, source, true, expected);
	}
	
	@Test
	public void testE2EUnmodified() throws Exception {
		String template = """
				```java
				public void test() {
					int x = 5;
				}
				```
				""";
		
		String source = """
				public class Foo {
					
					public void test() {
						int x = 5;
					}
					
				}
				""";
		
		runE2E(template, source, true, template);
	}
	
	@Test
	public void testE2EValidateNoChanges() throws Exception {
		String template = """
				<!-- :code: src=${source} lines=3:5 preserveTabs -->
				
				```java
				public void test() {
					int x = 5;
				}
				```
				""";
		
		String source = """
				public class Foo {
					
					public void test() {
						int x = 5;
					}
					
				}
				""";
		
		runE2E(template, source, false, template);
	}
	
	@Test(expected = FrameworkException.class)
	public void testE2EValidateWithChanges() throws Exception {
		String template = """
				<!-- :code: src=${source} lines=3:5 -->
				
				```java
				public void test() {
					int x = 10;
				}
				```
				""";
		
		String source = """
				public class Foo {
					
					public void test() {
						int x = 5;
					}
					
				}
				""";
		
		runE2E(template, source, false, template);
	}
	
	@Test
	@Ignore("Unable to find class when testing through Maven")
	public void testE2EExecute() throws Exception {
		String template = """
				<!-- :exec: src=examples/Example1.java lines=1 -->
				
				```
				```
				""";
		
		String expected = """
				<!-- :exec: src=examples/Example1.java lines=1 -->
				
				```
				Var1     Var2     Var3     Var4     Var5     Var6     Var7     Var8     Var9     Var10    Var11    Obj1     Obj2
				```
				""";

		runE2E(template, "", true, expected);
	}
	
	@Test(expected = ParsingException.class)
	public void testMalformedInstruction() throws Exception {
		String template = """
				<!-- :code: src=${source} id= -->
				
				```java
				```
				""";
		
		runE2E(template, "", false, template);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInstructionType() throws Exception {
		String template = """
				<!-- :foo: src=${source} id=foo -->
				
				```java
				```
				""";
		
		runE2E(template, "", false, template);
	}
	
	@Test(expected = ParsingException.class)
	public void testMissingIdentifier() throws Exception {
		String template = """
				<!-- :code: src=${source} id=foo -->
				
				```java
				```
				""";
		
		String source = """
				public class Foo {
					// begin-example: bar
					public void test() {
						int x = 5;
					}
					// end-example: bar
				}
				""";
		
		runE2E(template, source, false, template);
	}
	
	public void runE2E(String template, String source, boolean update, String expected) throws Exception {
		File sourceFile = TempFiles.createFileWithExtension(".java").withContent(source);

		Map<String, String> replacements = new HashMap<>();
		replacements.put("source", sourceFile.getAbsolutePath());
		
		StringSubstitutor substitutor = new StringSubstitutor(replacements);
		template = substitutor.replace(template);
		expected = substitutor.replace(expected);
		
		File templateFile = TempFiles.createFileWithExtension(".md").withContent(template);
		
		List<String> args = new ArrayList<>();
		args.add(templateFile.getAbsolutePath());
		
		if (update) {
			args.add(0, "--update");
		}
		
		Exception thrownException = null;
		
		try {
			utility.start(args.toArray(new String[0]));
		} catch (FrameworkException e) {
			thrownException = e;
		}
		
		Assert.assertEqualsNormalized(expected, Files.readString(templateFile.toPath()));
		
		if (thrownException != null) {
			throw thrownException;
		}
	}

}
