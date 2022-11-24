package org.moeaframework.core.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.moeaframework.util.Concaterator;

/**
 * Abstract factory for service providers.  This contains convenience methods
 * for enumerating the providers using a {@link ServiceLoader} along with
 * explicit providers registered with {@link #addProvider}.
 *
 * @param <T> the generic type of the provider
 */
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
	 * 
	 * @param <T> the generic type of the provider
	 * @param type the class of the provider
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
	
	/**
	 * Returns {@code true} if this factory contains a provider with the given name.
	 * This will match either the class' fully-qualified name or simple name.
	 * 
	 * @param name the class name, either simple or including the package
	 * @return {@code true} if this factory contains the provider; {@code false} otherwise
	 */
	public boolean hasProvider(String name) {
		for (T provider : this) {
			Class<?> providerType = provider.getClass();
			
			if (name.equalsIgnoreCase(providerType.getSimpleName()) ||
					name.equalsIgnoreCase(providerType.getName())) {
				return true;
			}
		}
		
		return false;
	}
	
}
