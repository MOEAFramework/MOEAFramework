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
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;
import org.apache.commons.text.translate.OctalUnescaper;
import org.apache.commons.text.translate.UnicodeEscaper;
import org.apache.commons.text.translate.UnicodeUnescaper;

/**
 * Tokenizer for encoding and decoding content on a line, escaping any special characters.
 */
public class Tokenizer {
		
	private final Map<CharSequence, CharSequence> specialCharacterMap;
	
	private final Map<CharSequence, CharSequence> escapeMap;
	
	private final Map<CharSequence, CharSequence> unescapeMap;
	
	private final Map<CharSequence, CharSequence> customMap;
	
	private char delimiter;
		
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
		
		setDelimiter(' ');
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
	 * Sets the delimiter, adding it as an escape character if not already configured.
	 * 
	 * @param delimiter the delimiter character
	 */
	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
		escapeChar(String.valueOf(delimiter), "\\" + delimiter);
	}
	
	/**
	 * Returns the delimiter used by this tokenizer.
	 * 
	 * @return the delimiter character
	 */
	public String getDelimiter() {
		return String.valueOf(delimiter);
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
	 * Decodes or parses the string into individual tokens.  See {@link #decode(String)} for details.
	 * 
	 * @param line the line to decode
	 * @return the tokens
	 */
	public String[] decodeToArray(String line) {
		return decode(line).toArray(String[]::new);
	}
	
	/**
	 * Decodes or parses the string into individual tokens, converting any escaped characters back to their original.
	 * <p>
	 * Leading and trailing whitespace are trimmed from the tokens.  Any such whitespace that is part of the token
	 * needs to be escaped.  For example, {@code "  foo  "} becomes {@code ["foo"]}, but the whitespace can be escaped
	 * with {@code "\ \ foo\ \ "}.
	 * <p>
	 * If the delimiter is a whitespace character, multiple adjacent whitespace are treated as one delimiter.
	 * However, if the delimiter is a non-whitespace character, each delimiter denotes a new token.  This leads to
	 * slightly different behavior when dealing with delimiters.  For instance, {@code "foo  bar"} becomes
	 * {@code ["foo", "bar"]}, but {@code "foo,,bar"} becomes {@code ["foo", "", "bar"]}.
	 * 
	 * @param line the line to decode
	 * @return the tokens
	 */
	public List<String> decode(String line) {
		update();
		
		List<String> tokens = new ArrayList<>();
		boolean precedingBackslash = false;
		boolean isToken = false;
		int startIndex = 0;
		int endIndex = 0;
			
		while (endIndex < line.length()) {
			char c = line.charAt(endIndex);
			
			if (c != ' ' && c != '\t' && c != '\f') {
				isToken = true;
			}
			
			if ((c == delimiter) && !precedingBackslash) {
				if (isToken) {
					String token = line.substring(startIndex, endIndex);
					token = stripUnescapedWhitespace(token);
					token = unescapeTranslator.translate(token);
					tokens.add(token);
					
					// trailing non-whitespace delimiters start next token
					isToken = !Character.isWhitespace(c);
				}
				
				startIndex = endIndex + 1;
			}
				
			precedingBackslash = c == '\\' ? !precedingBackslash : false;
			endIndex += 1;
		}
		
		if (isToken) {
			String token = line.substring(startIndex);
			token = stripUnescapedWhitespace(token);
			token = unescapeTranslator.translate(token);
			tokens.add(token);
		}
		
		return tokens;
	}

	/**
	 * Strips any leading and trailing whitespace that is not escaped.
	 * 
	 * @param str the string
	 * @return the stripped string
	 */
	private String stripUnescapedWhitespace(String str) {
		int start = 0;
		int end = str.length();

		while (start < end) {
			char c = str.charAt(start);

			if (c != ' ' && c != '\t' && c != '\f') {
				break;
			}
			
			start += 1;
		}

		while (end > start) {
			char c = str.charAt(end - 1);
					
			if ((c != ' ' && c != '\t' && c != '\f') || (end - 2 >= start && str.charAt(end - 2) == '\\')) {
				break;
			}

			end -= 1;
		}

		return str.substring(start, end);
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
	 * Encodes the tokens into a string.  See {@link #encode(Iterable)} for details.
	 * 
	 * @param tokens the tokens to encode
	 * @return the encoded string
	 */
	public String encode(String[] tokens) {
		return encode(List.of(tokens));
	}
	
	/**
	 * Encodes the tokens into a string.  See {@link #encode(Iterable)} for details.
	 * 
	 * @param tokens the tokens to encode
	 * @return the encoded string
	 */
	public String encode(Stream<String> tokens) {
		return encode(tokens.toList());
	}
	
	/**
	 * Encodes the tokens into a string.  Each token will be escaped following the rules of this tokenizer and joined
	 * into a string separated by the delimiter.
	 * 
	 * @param tokens the tokens to encode
	 * @return the encoded string
	 */
	public String encode(Iterable<String> tokens) {
		update();
		
		StringBuilder sb = new StringBuilder();
		
		for (String token : tokens) {
			if (sb.length() > 0) {
				sb.append(delimiter);
			}
			
			sb.append(escapeTranslator.translate(token));
		}
		
		return sb.toString();
	}

}
