package com.jagex;

public final class Queue {

	public CacheableNode head;
	private CacheableNode current;

	public Queue() {
		head = new CacheableNode();
		head.nextCacheable = head;
		head.previousCacheable = head;
	}

	public CacheableNode getNext() {
		CacheableNode current = this.current;
		if (current == head) {
			this.current = null;
			return null;
		}

		this.current = current.nextCacheable;
		return current;
	}

	public CacheableNode peek() {
		CacheableNode next = head.nextCacheable;
		if (next == head) {
			current = null;
			return null;
		}

		current = next.nextCacheable;
		return next;
	}

	public CacheableNode pop() {
		CacheableNode next = head.nextCacheable;
		if (next == head) {
			return null;
		}

		next.unlinkCacheable();
		return next;
	}

	public void push(CacheableNode node) {
		if (node.previousCacheable != null) {
			node.unlinkCacheable();
		}

		node.previousCacheable = head.previousCacheable;
		node.nextCacheable = head;
		node.previousCacheable.nextCacheable = node;
		node.nextCacheable.previousCacheable = node;
	}

	public int size() {
		int count = 0;
		for (CacheableNode next = head.nextCacheable; next != head; next = next.nextCacheable) {
			count++;
		}

		return count;
	}

}