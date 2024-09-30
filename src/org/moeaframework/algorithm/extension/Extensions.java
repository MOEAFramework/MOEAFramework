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
package org.moeaframework.algorithm.extension;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Stateful;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.util.TypedProperties;

/**
 * A collection of {@link Extension} associated with an {@link Algorithm}.
 */
public class Extensions implements Iterable<Extension>, Stateful, Configurable {
	
	private final Algorithm algorithm;
	
	private final List<Extension> extensions;
	
	public Extensions(Algorithm algorithm) {
		super();
		this.algorithm = algorithm;
		this.extensions = new ArrayList<Extension>();
	}
	
	public void add(Extension extension) {
		extensions.add(extension);
		extension.onRegister(algorithm);
	}
	
	public void remove(Extension extension) {
		extensions.remove(extension);
	}
	
	public void remove(Class<? extends Extension> extensionType) {
		removeIf((e) -> extensionType.isInstance(e));
	}
	
	public void removeIf(Predicate<? super Extension> filter) {
		extensions.removeIf(filter);
	}
	
	public <T extends Extension> T get(Class<T> extensionType) {
		for (Extension extension : extensions) {
			if (extensionType.isInstance(extension)) {
				return extensionType.cast(extension);
			}
		}
		
		return null;
	}
	
	public void onStep() {
		for (Extension extension : extensions) {
			extension.onStep(algorithm);
		}
	}
	
	public void onInitialize() {
		for (Extension extension : extensions) {
			extension.onInitialize(algorithm);
		}
	}
	
	public void onTerminate() {
		for (Extension extension : extensions) {
			extension.onTerminate(algorithm);
		}
	}

	@Override
	public Iterator<Extension> iterator() {
		return extensions.iterator();
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		for (Extension extension : extensions) {
			if (extension instanceof Stateful) {
				((Stateful)extension).saveState(stream);
			}
		}
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		for (Extension extension : extensions) {
			if (extension instanceof Stateful) {
				((Stateful)extension).loadState(stream);
			}
		}
	}
	
	@Override
	public void applyConfiguration(TypedProperties properties) {
		for (Extension extension : extensions) {
			if (extension instanceof Configurable) {
				((Configurable)extension).applyConfiguration(properties);
			}
		}
	}
	
	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = new TypedProperties();
		
		for (Extension extension : extensions) {
			if (extension instanceof Configurable) {
				properties.addAll(((Configurable)extension).getConfiguration());
			}
		}
		
		return properties;
	}

}
