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
package org.moeaframework.analysis.store.schema;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.core.TypedProperties;

public class SchemaTest {
	
	@Test
	public void testSchemaless() throws IOException {
		Schema schema = Schema.schemaless();
		Assert.assertTrue(schema.isSchemaless());
		Assert.assertSize(0, schema.getFields());
		
		TypedProperties properties = new TypedProperties();
		
		// Empty
		List<Pair<Field<?>, String>> resolvedFields = schema.resolve(Reference.of(properties));
		Assert.assertSize(0, resolvedFields);
		
		// Single field
		properties.setString("foo", "bar");
		
		resolvedFields = schema.resolve(Reference.of(properties));
		Assert.assertSize(1, resolvedFields);
		Assert.assertEquals("foo", resolvedFields.get(0).getKey().getName());
		Assert.assertEquals("bar", resolvedFields.get(0).getValue());
		
		// Multiple fields
		properties.setString("aaa", "bbb");
		
		resolvedFields = schema.resolve(Reference.of(properties));
		Assert.assertSize(2, resolvedFields);
		Assert.assertEquals("aaa", resolvedFields.get(0).getKey().getName());
		Assert.assertEquals("bbb", resolvedFields.get(0).getValue());
		Assert.assertEquals("foo", resolvedFields.get(1).getKey().getName());
		Assert.assertEquals("bar", resolvedFields.get(1).getValue());
	}
	
	@Test
	public void testSchema() throws IOException {
		Schema schema = Schema.of(Field.named("foo").asString());
		Assert.assertFalse(schema.isSchemaless());
		Assert.assertSize(1, schema.getFields());

		List<Pair<Field<?>, String>> resolvedFields = schema.resolve(Reference.of(TypedProperties.of("foo", "bar")));
		Assert.assertSize(1, resolvedFields);
		Assert.assertEquals("foo", resolvedFields.get(0).getKey().getName());
		Assert.assertEquals("bar", resolvedFields.get(0).getValue());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSchemaMissingField() throws IOException {
		Schema schema = Schema.of(Field.named("foo").asString());
		schema.resolve(Reference.of(TypedProperties.of()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSchemaExtraField() throws IOException {
		Schema schema = Schema.of(Field.named("foo").asString());
		schema.resolve(Reference.of(TypedProperties.of("aaa", "bbb")));
	}

}
