package com.jagex;

public final class HashTable {

	// Class1

	private int bucketCount;
	private Node[] buckets;

	public HashTable(int size) {
		bucketCount = size;
		buckets = new Node[size];

		for (int i = 0; i < size; i++) {
			Node node = buckets[i] = new Node();
			node.next = node;
			node.previous = node;
		}
	}

	public Node get(long key) {
		Node node = buckets[(int) (key & (bucketCount - 1))];
		for (Node next = node.next; next != node; next = next.next) {
			if (next.key == key) {
				return next;
			}
		}

		return null;
	}

	public void put(Node node, long key) {
		if (node.previous != null) {
			node.unlink();
		}

		Node current = buckets[(int) (key & (bucketCount - 1))];
		node.previous = current.previous;
		node.next = current;
		node.previous.next = node;
		node.next.previous = node;
		node.key = key;
	}

}