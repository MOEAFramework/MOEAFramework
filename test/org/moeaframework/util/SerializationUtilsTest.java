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
package org.moeaframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.moeaframework.Assert;

public class SerializationUtilsTest {
	
	@Test
	public void testList() throws IOException, ClassNotFoundException {
		List<String> expected = List.of("foo");
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				SerializationUtils.writeList(expected, oos);
			}
			
			try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					ObjectInputStream ois = new ObjectInputStream(bais)) {
				List<String> actual = SerializationUtils.readList(String.class, ois);
				Assert.assertEquals(expected, actual);
			}
		}
	}
	
	@Test
	public void testMap() throws IOException, ClassNotFoundException {
		Map<String, Integer> expected = Map.of("foo", 1);
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				SerializationUtils.writeMap(expected, oos);
			}
			
			try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					ObjectInputStream ois = new ObjectInputStream(bais)) {
				Map<String, Integer> actual = SerializationUtils.readMap(String.class, Integer.class, ois);
				Assert.assertEquals(expected, actual);
			}
		}
	}
	
	@Test(expected = ClassCastException.class)
	public void testInvalidType() throws IOException, ClassNotFoundException {
		List<String> expected = List.of("foo");
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				SerializationUtils.writeList(expected, oos);
			}
			
			try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					ObjectInputStream ois = new ObjectInputStream(bais)) {
				SerializationUtils.readList(Integer.class, ois);
			}
		}
	}
	
}
