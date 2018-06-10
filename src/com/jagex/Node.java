package com.jagex;

public class Node {

	// Class30

	public long key;
	public Node next;
	Node previous;

	public void unlink() {
		if (previous == null) {
			return;
		}

		previous.next = next;
		next.previous = previous;
		next = null;
		previous = null;
	}

}