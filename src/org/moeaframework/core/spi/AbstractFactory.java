package org.moeaframework.core.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.moeaframework.util.Concaterator;

public class AbstractFactory<T> implements Iterable<T> {
	
	/**
	 * The static service loader for loading algorithm providers.
	 */
	private final ServiceLoader<T> providers;
	
	/**
	 * Collection of providers that have been manually added.
	 */
	private List<T> customProviders;
	
	/**
	 * Constructs a new factory for the given type.
	 */
	public AbstractFactory(Class<T> type) {
		super();
		providers = ServiceLoader.load(type);
		customProviders = new ArrayList<T>();
	}
	
	/**
	 * Adds a provider to this factory.  Subsequent calls to enumerate the providers
	 * will include this provider.
	 * 
	 * @param provider the new provider
	 */
	public void addProvider(T provider) {
		customProviders.add(provider);
	}
	
	/**
	 * Returns an iterator of all registered providers.
	 * 
	 * @return the iterator of all registered providers
	 */
	public Iterator<T> iterator() {
		return new Concaterator<T>(customProviders.iterator(), providers.iterator());
	}
	
}
