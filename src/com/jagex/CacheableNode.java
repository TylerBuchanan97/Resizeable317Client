package com.jagex;

public class CacheableNode extends Node {

	// Class30_Sub2
	public CacheableNode nextCacheable;
	CacheableNode previousCacheable;

	public void unlinkCacheable() {
		if (previousCacheable == null) {
			return;
		}

		previousCacheable.nextCacheable = nextCacheable;
		nextCacheable.previousCacheable = previousCacheable;
		nextCacheable = null;
		previousCacheable = null;
	}

}