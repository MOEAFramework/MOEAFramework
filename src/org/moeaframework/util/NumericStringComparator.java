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

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sorts strings containing numeric values.  The string is split into sections that contain numeric and non-numeric
 * characters.  The numbers are sorted according to their natural order, whereas non-numeric sections are sorted
 * lexicographically.
 * <p>
 * For example, the default string comparator sorts {@code "Val15"} before {@code "Val2"} because the two strings
 * differ at position 3 and compare {@code "1"} to {@code "2"}.  On the other hand, this comparator identifies the
 * two strings contain {@code 15} and {@code 2}, producing a more natural ordering.
 */
public class NumericStringComparator implements Comparator<String> {
	
	private static final Pattern INTEGER_PATTERN = Pattern.compile("([0-9]+)");
	
	@Override
	public int compare(String str1, String str2) {
		Matcher matcher1 = INTEGER_PATTERN.matcher(str1);
		Matcher matcher2 = INTEGER_PATTERN.matcher(str2);		
		int start1 = 0;
		int start2 = 0;
		
		while (matcher1.find() && matcher2.find()) {
			String substr1 = str1.substring(start1, matcher1.start());
			String substr2 = str2.substring(start2, matcher2.start());
			int cmp = String.CASE_INSENSITIVE_ORDER.compare(substr1, substr2);
			
			if (cmp != 0) {
				return cmp;
			}
			
			int val1 = Integer.parseInt(matcher1.group());
			int val2 = Integer.parseInt(matcher2.group());
			cmp = Integer.compare(val1, val2);
			
			if (cmp != 0) { 
				return cmp;
			}
			
			start1 = matcher1.end();
			start2 = matcher2.end();
		}
		
		return String.CASE_INSENSITIVE_ORDER.compare(str1.substring(start1), str2.substring(start2));
	}

}
