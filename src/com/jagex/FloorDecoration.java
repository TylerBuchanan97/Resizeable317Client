package com.jagex;

public final class FloorDecoration {

	/**
	 * The key of this decoration.
	 */
	public int key;

	/**
	 * The renderable of this decoration.
	 */
	public Renderable renderable;

	/**
	 * A packed config value containing the type and orientation of this decoration, in the form
	 * {@code (orientation << 6) | type}.
	 */
	byte config; // (orientation << 6) | type

	/**
	 * The draw height of this decoration.
	 */
	int height;

	/**
	 * The x coordinate of this decoration.
	 */
	int x;

	/**
	 * The y coordinate of this decoration.
	 */
	int y;

}