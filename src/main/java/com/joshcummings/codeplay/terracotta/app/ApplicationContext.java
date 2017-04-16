package com.joshcummings.codeplay.terracotta.app;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
	private final Map<Class<?>, Object> registry = new HashMap<>();
	
	public <T> T get(Class<T> clazz) {
		return (T)registry.get(clazz);
	}
	
	public void set(Object obj) {
		put(obj.getClass(), obj);
	}

	public void put(Class<?> clazz, Object obj) {
		registry.put(clazz, obj);
	}
	
	public void clear() {
		registry.clear();
	}
}
