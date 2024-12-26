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
package org.moeaframework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

public class CallCounter<T> {

	private final T proxy;
	
	private final ProxyInvocationHandler<T> handler;
	
	public CallCounter(T proxy, ProxyInvocationHandler<T> handler) {
		super();
		this.proxy = proxy;
		this.handler = handler;
	}
	
	public T getProxy() {
		return proxy;
	}
	
	public int getExactCallCount(Method method, Object... args) {
		return handler.getExactCallCount(method, args);
	}
	
	public int getExactCallCount(String methodName, Object... args) {
		return handler.getExactCallCount(methodName, args);
	}
	
	public int getTotalCallCount(Method method) {
		return handler.getTotalCallCount(method);
	}
	
	public int getTotalCallCount(String methodName) {
		return handler.getTotalCallCount(methodName);
	}
	
	public int getTotalCallCount() {
		return handler.getTotalCallCount();
	}
	
	public static class ProxyInvocationHandler<T> implements InvocationHandler {
		
		private final T instance;
		
		private final Counter<Pair<Method, List<Object>>> invocations;
		
		public ProxyInvocationHandler(T instance) {
			super();
			this.instance = instance;
			this.invocations = new Counter<>();
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			invocations.incrementAndGet(Pair.of(method, args == null ? List.of() : List.of(args)));
			return method.invoke(instance, args);
		}
		
		public int getExactCallCount(Method method, Object... args) {
			return invocations.get(Pair.of(method, args == null ? List.of() : List.of(args)));
		}
		
		public int getExactCallCount(String methodName, Object... args) {
			List<Object> argList = args == null ? List.of() : List.of(args);
			
			for (Pair<Method, List<Object>> invocation : invocations.values()) {
				if (invocation.getKey().getName().equals(methodName) && argList.equals(invocation.getValue())) {
					return invocations.get(invocation);
				}
			}
			
			return 0;
		}
		
		public int getTotalCallCount(Method method) {
			return getTotalCallCount(method.getName());
		}
		
		public int getTotalCallCount(String methodName) {
			int sum = 0;
			
			for (Pair<Method, List<Object>> invocation : invocations.values()) {
				if (invocation.getKey().getName().equals(methodName)) {
					sum += invocations.get(invocation);
				}
			}
			
			return sum;
		}
		
		public int getTotalCallCount() {
			int sum = 0;
			
			for (Pair<Method, List<Object>> invocation : invocations.values()) {
				sum += invocations.get(invocation);
			}
			
			return sum;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> CallCounter<T> of(T instance) {
		ProxyInvocationHandler<T> handler = new ProxyInvocationHandler<>(instance);
		T proxy = (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), instance.getClass().getInterfaces(), handler);
		return new CallCounter<>(proxy, handler);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> CallCounter<Consumer<T>> mockConsumer() {
		Consumer<T> mock = t -> {
			// do nothing
		};
		
		ProxyInvocationHandler<Consumer<T>> handler = new ProxyInvocationHandler<>(mock);
		Consumer<T> proxy = (Consumer<T>)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), mock.getClass().getInterfaces(), handler);
		return new CallCounter<>(proxy, handler);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, U> CallCounter<BiConsumer<T, U>> mockBiConsumer() {
		BiConsumer<T, U> mock = (t, u) -> {
			// do nothing
		};
		
		ProxyInvocationHandler<BiConsumer<T, U>> handler = new ProxyInvocationHandler<>(mock);
		BiConsumer<T, U> proxy = (BiConsumer<T, U>)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), mock.getClass().getInterfaces(), handler);
		return new CallCounter<>(proxy, handler);
	}

}
