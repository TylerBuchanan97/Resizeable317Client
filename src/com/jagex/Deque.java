package com.jagex;

public final class Deque {

	public Node empty = new Node();
	private Node current;

	public Deque() {
		empty.next = empty;
		empty.previous = empty;
	}

	public void clear() {
		if (empty.next == empty) {
			return;
		}

		do {
			Node node = empty.next;
			if (node == empty) {
				return;
			}

			node.unlink();
		} while (true);
	}

	public Node getFront() {
		Node node = empty.next;
		if (node == empty) {
			current = null;
			return null;
		}

		current = node.next;
		return node;
	}

	public Node getNext() {
		Node node = current;
		if (node == empty) {
			current = null;
			return null;
		}

		current = node.next;
		return node;
	}

	public Node getPrevious() {
		Node node = current;
		if (node == empty) {
			current = null;
			return null;
		}

		current = node.previous;
		return node;
	}

	public Node getTail() {
		Node node = empty.previous;
		if (node == empty) {
			current = null;
			return null;
		}

		current = node.previous;
		return node;
	}

	public Node popFront() {
		Node node = empty.next;
		if (node == empty) {
			return null;
		}

		node.unlink();
		return node;
	}

	public void pushBack(Node node) {
		if (node.previous != null) {
			node.unlink();
		}

		node.previous = empty.previous;
		node.next = empty;
		node.previous.next = node;
		node.next.previous = node;
	}

	public void pushFront(Node node) {
		if (node.previous != null) {
			node.unlink();
		}

		node.previous = empty;
		node.next = empty.next;
		node.previous.next = node;
		node.next.previous = node;
	}

}