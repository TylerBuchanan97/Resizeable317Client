package com.jagex;

public class TileUtils {

	public static int method155(int x, int y, int rotation) {
		rotation &= 3;
		if (rotation == 0) {
			return x;
		} else if (rotation == 1) {
			return y;
		} else if (rotation == 2) {
			return 7 - x;
		}

		return 7 - y;
	}

	public static int method156(int x, int y, int rotation) {
		rotation &= 3;
		if (rotation == 0) {
			return y;
		} else if (rotation == 1) {
			return 7 - x;
		} else if (rotation == 2) {
			return 7 - y;
		}

		return x;
	}

	public static int method157(int width, int length, int rotation, int l, int k) {
		rotation &= 3;
		if (rotation == 0) {
			return k;
		} else if (rotation == 1) {
			return l;
		} else if (rotation == 2) {
			return 7 - k - (width - 1);
		}

		return 7 - l - (length - 1);
	}

	public static int method158(int width, int length, int rotation, int j, int j1) {
		rotation &= 3;
		if (rotation == 0) {
			return j;
		} else if (rotation == 1) {
			return 7 - j1 - (width - 1);
		} else if (rotation == 2) {
			return 7 - j - (length - 1);
		}

		return j1;
	}

}