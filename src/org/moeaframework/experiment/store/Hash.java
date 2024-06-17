package org.moeaframework.experiment.store;

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
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.TypedProperties;

public class Hash {
	
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
		return new HashCodeBuilder(17, 37)
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
	
	public static Hash of(Key key, DataType dataType) {
		return of(key + "->" + dataType);
	}
	
	public static Hash of(TypedProperties properties) {
		StringBuilder sb = new StringBuilder();
		
		for (String key : properties.keySet()) {
			sb.append(key);
			sb.append("=");
			sb.append(properties.getString(key));
			sb.append("\n");
		}
		
		return of(sb.toString());
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