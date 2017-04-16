package com.joshcummings.codeplay.terracotta.app;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class RedirectCache {
	private Cache<String, String> cache = 
				CacheBuilder.newBuilder()
					.expireAfterWrite(2, TimeUnit.MINUTES)
					.build();
	
	public String key(String url) {
		String key = UUID.randomUUID().toString();
		cache.put(key, url);
		return key;
	}
	
	public String url(String key) {
		return cache.getIfPresent(key);
	}
}
