package com.jagex;

public final class Cache {

	private int capacity;
	private CacheableNode empty = new CacheableNode();
	private Queue references = new Queue();
	private HashTable table = new HashTable(1024);
	private int unused;

	public Cache(int capacity) {
		this.capacity = capacity;
		unused = capacity;
	}

	public CacheableNode get(long key) {
		CacheableNode node = (CacheableNode) table.get(key);
		if (node != null) {
			references.push(node);
		}
		return node;
	}

	public void put(CacheableNode node, long key) {
		if (unused == 0) {
			CacheableNode front = references.pop();
			front.unlink();
			front.unlinkCacheable();
			if (front == empty) {
				front = references.pop();
				front.unlink();
				front.unlinkCacheable();
			}
		} else {
			unused--;
		}
		table.put(node, key);
		references.push(node);
	}

	public void unlink() {
		do {
			CacheableNode front = references.pop();
			if (front != null) {
				front.unlink();
				front.unlinkCacheable();
			} else {
				unused = capacity;
				return;
			}
		} while (true);
	}

}