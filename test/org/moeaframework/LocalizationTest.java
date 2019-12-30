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
package org.moeaframework;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.util.Localization;

/**
 * Tests the {@link Localization} class.  If errors occur when running tests
 * within Eclipse, please ensure the text file encoding is UTF-8.
 */
public class LocalizationTest {

	@Test
	public void testInstanceMethods() {
		Localization english = Localization.getLocalization(
				LocalizationTest.class);
		Localization spanish = Localization.getLocalization(
				LocalizationTest.class,
				new Locale("ES"));
		Localization german = Localization.getLocalization(
				LocalizationTest.class,
				new Locale("DE"));

		//test without arguments
		Assert.assertEquals("hello, world", english.getString("test"));
		Assert.assertEquals("hola, mundo", spanish.getString("test"));
		Assert.assertEquals("hello, world", german.getString("test"));
		Assert.assertTrue(english.containsKey("test"));
		Assert.assertTrue(spanish.containsKey("test"));
		Assert.assertTrue(german.containsKey("test"));

		//test with arguments
		Assert.assertEquals("hello, Foo",
				english.getString("testArgument", "Foo"));
		Assert.assertEquals("hola, Foo",
				spanish.getString("testArgument", "Foo"));
		Assert.assertEquals("hello, Foo",
				german.getString("testArgument", "Foo"));

		//test if missing key returns the key
		Assert.assertEquals("missing.key", english.getString("missing.key"));
		Assert.assertEquals("missing.key", english.getString("missing.key",
				"Foo"));
		Assert.assertFalse(english.containsKey("missing.key"));
		
		//test with missing bundle
		Localization missing = Localization.getLocalization(
				"org.moeaframework.util.missing");
		Assert.assertEquals("missing.key", missing.getString("missing.key"));
		Assert.assertFalse(missing.containsKey("missing.key"));
	}

	@Test
	public void testStaticMethods() {
		Locale defaultLocale = Locale.getDefault();

		Assert.assertEquals("hello, static world", 
				Localization.getString(LocalizationTest.class, "test"));
		Assert.assertEquals("hello, static Foo", 
				Localization.getString(LocalizationTest.class, "testArgument",
						"Foo"));
		Assert.assertTrue(Localization.containsKey(LocalizationTest.class, 
				"test"));

		Locale.setDefault(new Locale("ES"));
		Assert.assertEquals("hola, estático mundo", 
				Localization.getString(LocalizationTest.class, "test"));
		Assert.assertEquals("hola, estático Foo", 
				Localization.getString(LocalizationTest.class, "testArgument",
						"Foo"));
		Assert.assertTrue(Localization.containsKey(LocalizationTest.class, 
				"test"));

		Locale.setDefault(new Locale("DE"));
		Assert.assertEquals("hello, static world", 
				Localization.getString(LocalizationTest.class, "test"));
		Assert.assertEquals("hello, static Foo", 
				Localization.getString(LocalizationTest.class, "testArgument",
						"Foo"));
		Assert.assertTrue(Localization.containsKey(LocalizationTest.class, 
				"test"));

		Locale.setDefault(defaultLocale);

		Assert.assertEquals("LocalizationTest.missing.key",
				Localization.getString(LocalizationTest.class, "missing.key"));
		Assert.assertEquals("LocalizationTest.missing.key",
				Localization.getString(LocalizationTest.class, "missing.key",
						"Foo"));
		Assert.assertFalse(Localization.containsKey(LocalizationTest.class, 
						"missing.key"));
	}

}
