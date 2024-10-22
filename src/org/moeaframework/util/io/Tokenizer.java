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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;
import org.apache.commons.text.translate.OctalUnescaper;
import org.apache.commons.text.translate.UnicodeEscaper;
import org.apache.commons.text.translate.UnicodeUnescaper;
import org.moeaframework.core.FrameworkException;

/**
 * Tokenizer for encoding and decoding content on a line, escaping any special characters.
 */
public class Tokenizer {
		
	private final Map<CharSequence, CharSequence> specialCharacterMap;
	
	private final Map<CharSequence, CharSequence> escapeMap;
	
	private final Map<CharSequence, CharSequence> unescapeMap;
	
	private final Map<CharSequence, CharSequence> customMap;
	
	private final Set<CharSequence> delimiters;
	
	private CharSequence outputDelimiter;
	
	private CharSequenceTranslator escapeTranslator;
	
	private CharSequenceTranslator unescapeTranslator;
	
	/**
	 * Constructs a new tokenizer with default settings.
	 */
	public Tokenizer() {
		super();
		specialCharacterMap = new HashMap<>();
		escapeMap = new HashMap<>();
		unescapeMap = new HashMap<>();
		customMap = new HashMap<>();
		delimiters = new HashSet<>();
		
		defaults();
	}
	
	/**
	 * Initializes this tokenizer with default settings.
	 */
	private void defaults() {
		specialCharacterMap.put("\b", "\\b");
		specialCharacterMap.put("\n", "\\n");
		specialCharacterMap.put("\t", "\\t");
		specialCharacterMap.put("\f", "\\f");
		specialCharacterMap.put("\r", "\\r");
		
		escapeMap.put("\"", "\\\"");
		escapeMap.put("\\", "\\\\");
		
		unescapeMap.put("\\\\", "\\");
		unescapeMap.put("\\\"", "\"");
		unescapeMap.put("\\'", "'");
		unescapeMap.put("\\", "");
		
		reset();
	}
	
	/**
	 * Resets this tokenizer back to its default state, removing any custom settings.
	 */
	public void reset() {
		customMap.clear();
		delimiters.clear();
		outputDelimiter = null;
		
		addDelimiter(" ");
		requireUpdate();
	}
	
	/**
	 * Rebuilds the internal translators for escaping text, if required.
	 */
	private void update() {
		if (escapeTranslator == null) {
			escapeTranslator = new AggregateTranslator(
				new LookupTranslator(Collections.unmodifiableMap(escapeMap)),
				new LookupTranslator(Collections.unmodifiableMap(specialCharacterMap)),
				new LookupTranslator(Collections.unmodifiableMap(customMap)),
				UnicodeEscaper.outsideOf(32, 0x7f));
		}

		if (unescapeTranslator == null) {
			unescapeTranslator = new AggregateTranslator(
				new OctalUnescaper(),
				new UnicodeUnescaper(),
				new LookupTranslator(EntityArrays.invert(customMap)),
				new LookupTranslator(EntityArrays.invert(specialCharacterMap)),
				new LookupTranslator(Collections.unmodifiableMap(unescapeMap)));
		}
	}
	
	/**
	 * Force this tokenizer to rebuild the internal translators for escaping characters.
	 */
	private void requireUpdate() {
		escapeTranslator = null;
		unescapeTranslator = null;
	}
	
	/**
	 * Registers an escaped character by specifying the original character and its escaped representation.  Note that
	 * whitespace, control characters, unicode, and {@code '\'} are escaped by default.
	 * 
	 * @param original the original character
	 * @param replacement the replacement string, which must start with {@code '\'}
	 */
	public void escapeChar(CharSequence original, CharSequence replacement) {
		if (original.length() != 1) {
			throw new IllegalArgumentException("original must be a single character, given '" + original + "'");
		}
		
		if (replacement.length() != 2 && replacement.charAt(0) != '\\') {
			throw new IllegalArgumentException("replacement must be a two-character string starting with \\, given '" +
					replacement + "'");
		}
		
		if (specialCharacterMap.containsKey(original) || escapeMap.containsKey(original)) {
			throw new IllegalArgumentException("original is already a default escape character, given '" + original +
					"'");
		}
		
		customMap.put(original, replacement);
		requireUpdate();
	}
	
	/**
	 * Adds a new delimiter in addition to any already configured.
	 * 
	 * @param delimiter the delimiter character
	 */
	public void addDelimiter(CharSequence delimiter) {
		if (delimiter.length() != 1) {
			throw new IllegalArgumentException("delimiter must be single character, given '" + delimiter + "'");
		}
		
		escapeChar(delimiter, "\\" + delimiter);
		delimiters.add(delimiter);
		
		if (outputDelimiter == null) {
			outputDelimiter = delimiter;
		}
	}
	
	/**
	 */
	public void clearDelimiters() {
		delimiters.clear();
	}
	
	/**
	 * Sets the delimiter to output when encoding tokens into a string.  The delimiter is also included for decoding,
	 * if not already configured.
	 * 
	 * @param delimiter the delimiter character
	 */
	public void setOutputDelimiter(CharSequence delimiter) {
		addDelimiter(delimiter);
		outputDelimiter = delimiter;
	}
	
	/**
	 * Returns the output delimiter used by this tokenizer.
	 * 
	 * @return the delimiter character
	 */
	public String getOutputDelimiter() {
		return outputDelimiter.toString();
	}
	
	/**
	 * Returns {@code true} if the given character is a delimiter, {@code false} otherwise.
	 * 
	 * @param c the character
	 * @return {@code true} if the given character is a delimiter, {@code false} otherwise
	 */
	private boolean isDelimiter(char c) {
		return delimiters.contains(String.valueOf(c));
	}
	
	/**
	 * Unescape the string without splitting into tokens.
	 * 
	 * @param str the string
	 * @return the unescaped string
	 */
	public String unescape(String str) {
		update();
		return unescapeTranslator.translate(str);
	}
	
	/**
	 * Decodes or parses the string into individual tokens, converting any escaped characters back to their original.
	 * 
	 * @param line the line to decode
	 * @return the tokens
	 */
	public String[] decodeToArray(String line) {
		return decode(line).toArray(String[]::new);
	}
	
	/**
	 * Decodes or parses the string into individual tokens, converting any escaped characters back to their original.
	 * 
	 * @param line the line to decode
	 * @return the tokens
	 */
	public List<String> decode(String line) {
		update();
		
		List<String> tokens = new ArrayList<>();
		boolean precedingBackslash = false;
		int lastDelimiter = -1;
			
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
				
			if (isDelimiter(c) && !precedingBackslash) {
				String token = line.substring(lastDelimiter + 1, i);
				
				if (!token.isBlank()) {
					token = unescapeTranslator.translate(token);
					tokens.add(token);
				}
				
				lastDelimiter = i;
			}
				
			precedingBackslash = c == '\\' ? !precedingBackslash : false;
		}
		
		if (lastDelimiter < line.length() - 1) {
			String token = line.substring(lastDelimiter + 1);
			
			if (!token.isBlank()) {
				token = unescapeTranslator.translate(token);
				tokens.add(token);
			}
		}
		
		return tokens;
	}
	
	public <T> List<T> decode(String line, Function<String, T> conversion) {
		List<T> result = new ArrayList<T>();
		
		for (String token : decode(line)) {
			result.add(conversion.apply(token));
		}
		
		return result;
	}
	
	/**
	 * Escapes the characters in a string.
	 * 
	 * @param str the string
	 * @return the escaped string
	 */
	public String escape(String str) {
		update();
		return escapeTranslator.translate(str);
	}
	
	/**
	 * Encodes the tokens into a string, potentially replacing characters with escaped variants.
	 * 
	 * @param tokens the tokens to encode
	 * @return the string
	 */
	public String encode(String[] tokens) {
		return encode(List.of(tokens));
	}
	
	/**
	 * Encodes the tokens into a string, potentially replacing characters with escaped variants.
	 * 
	 * @param tokens the tokens to encode
	 * @return the string
	 */
	public String encode(List<String> tokens) {
		update();
		
		StringBuilder sb = new StringBuilder();
		
		for (String token : tokens) {
			if (sb.length() > 0) {
				if (outputDelimiter == null) {
					throw new FrameworkException("outputDelimiter not set");
				}
				
				sb.append(outputDelimiter);
			}
			
			token = escapeTranslator.translate(token);
			sb.append(token);
		}
		
		return sb.toString();
	}

}
