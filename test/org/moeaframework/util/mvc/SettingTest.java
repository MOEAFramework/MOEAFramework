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
package org.moeaframework.util.mvc;

import java.lang.reflect.Proxy;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.CallCounter;

public class SettingTest {
	
	@Test
	public void test() {
		final Setting<Integer> setting = new Setting<>(5);

		SettingChangedListener listener = new SettingChangedListener() {

			@Override
			public void settingChanged(SettingChangedEvent event) {
				Assert.assertNotNull(event);
				Assert.assertSame(setting, event.getSource());
			}
			
			@Override
			public boolean equals(Object obj) {
				if (obj instanceof Proxy) {
					return toString().equals(obj.toString());
				} else {
					return super.equals(obj);
				}
			}
			
			// Required for Checkstyle since it expects both equals and hashCode to be overridden
			@Override
			public int hashCode() {
				return super.hashCode();
			}
			
		};
		
		CallCounter<SettingChangedListener> counter = CallCounter.of(listener);		
		setting.addSettingChangedListener(counter.getProxy());
		
		Assert.assertEquals(5, setting.get());
		Assert.assertEquals(5, setting.getDefaultValue());
		Assert.assertEquals(0, counter.getTotalCallCount("settingChanged"));
		
		setting.set(10);
		Assert.assertEquals(10, setting.get());
		Assert.assertEquals(5, setting.getDefaultValue());
		Assert.assertEquals(1, counter.getTotalCallCount("settingChanged"));
		
		setting.set(10);
		Assert.assertEquals(10, setting.get());
		Assert.assertEquals(5, setting.getDefaultValue());
		Assert.assertEquals(1, counter.getTotalCallCount("settingChanged"));
		
		setting.removeSettingChangedListener(counter.getProxy());
		
		setting.set(15);
		Assert.assertEquals(15, setting.get());
		Assert.assertEquals(5, setting.getDefaultValue());
		Assert.assertEquals(1, counter.getTotalCallCount("settingChanged"));
	}

}
