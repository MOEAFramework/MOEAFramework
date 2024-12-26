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
package org.moeaframework.analysis.store.fs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.schema.Field;
import org.moeaframework.analysis.store.schema.Schema;
import org.moeaframework.core.FrameworkException;

class Hash {
	
	private static final Charset UTF8 = StandardCharsets.UTF_8;
	
	private static final HexFormat HEX = HexFormat.of();
	
	private final String hashValue;
		
	Hash(MessageDigest messageDigest) {
		super();
		this.hashValue = HEX.formatHex(messageDigest.digest());
	}
	
	@Override
	public String toString() {
		return hashValue;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(hashValue)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj.getClass() != getClass()) {
			return false;
		}

		Hash rhs = (Hash)obj;
		return new EqualsBuilder()
				.append(hashValue, rhs.hashValue)
				.isEquals();
	}
	
	private static MessageDigest newMessageDigest() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// This should never fail given SHA-256 is required in all Java implementations...
			throw new FrameworkException("Failed to create SHA-256 message digest", e);
		}
	}
	
	public static Hash of(byte[] data) {
		MessageDigest digest = newMessageDigest();
		digest.update(data);
		return new Hash(digest);
	}
	
	public static Hash of(String data) {
		return of(data.getBytes(UTF8));
	}
	
	public static Hash of(Schema schema, Reference reference) {
		MessageDigest digest = newMessageDigest();
		
		for (Pair<Field<?>, String> entries : schema.resolve(reference)) {
			digest.update(entries.getKey().getNormalizedName().getBytes(UTF8));
			digest.update(entries.getKey().getNormalizedValue(entries.getValue()).getBytes(UTF8));
		}
		
		return new Hash(digest);
	}
	
	public static Hash of(Serializable serializable) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				oos.writeObject(serializable);
			}
			
			return of(baos.toByteArray());
		}
	}
}