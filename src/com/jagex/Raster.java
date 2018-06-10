package com.jagex;

public class Raster extends CacheableNode {

	public static int anInt1385;
	public static int centreX;
	public static int centreY;
	public static int clipBottom;
	public static int clipLeft;
	public static int clipRight;
	public static int clipTop;
	public static int height;
	public static int[] raster;
	public static int width;

	public static void drawHorizontal(int x, int y, int length, int colour) {
		if (y < clipBottom || y >= clipTop) {
			return;
		}

		if (x < clipLeft) {
			length -= clipLeft - x;
			x = clipLeft;
		}

		if (x + length > clipRight) {
			length = clipRight - x;
		}

		int start = x + y * width;
		for (int i = 0; i < length; i++) {
			raster[start + i] = colour;
		}
	}

	public static void drawRectangle(int x, int y, int width, int height, int colour) {
		drawHorizontal(x, y, width, colour);
		drawHorizontal(x, y + height - 1, width, colour);
		drawVertical(x, y, height, colour);
		drawVertical(x + width - 1, y, height, colour);
	}

	public static void drawVertical(int x, int y, int length, int colour) {
		if (x < clipLeft || x >= clipRight) {
			return;
		}

		if (y < clipBottom) {
			length -= clipBottom - y;
			y = clipBottom;
		}

		if (y + length > clipTop) {
			length = clipTop - y;
		}

		int start = x + y * width;
		for (int i = 0; i < length; i++) {
			raster[start + i * width] = colour;
		}
	}

	public static void fillRectangle(int x, int y, int height, int width, int colour) {
		if (x < clipLeft) {
			width -= clipLeft - x;
			x = clipLeft;
		}

		if (y < clipBottom) {
			height -= clipBottom - y;
			y = clipBottom;
		}

		if (x + width > clipRight) {
			width = clipRight - x;
		}

		if (y + height > clipTop) {
			height = clipTop - y;
		}

		int dx = Raster.width - width;
		int pixel = x + y * Raster.width;
		for (int i2 = -height; i2 < 0; i2++) {
			for (int j2 = -width; j2 < 0; j2++) {
				raster[pixel++] = colour;
			}

			pixel += dx;
		}
	}

	public static void init(int height, int width, int[] pixels) {
		Raster.raster = pixels;
		Raster.width = width;
		Raster.height = height;
		setBounds(height, 0, width, 0);
	}

	public static void method335(int drawX, int drawY, int width, int height, int colour, int shift) {
		if (drawX < clipLeft) {
			width -= clipLeft - drawX;
			drawX = clipLeft;
		}

		if (drawY < clipBottom) {
			height -= clipBottom - drawY;
			drawY = clipBottom;
		}

		if (drawX + width > clipRight) {
			width = clipRight - drawX;
		}

		if (drawY + height > clipTop) {
			height = clipTop - drawY;
		}

		int reverseShift = 256 - shift; // TODO stupid name
		int r = (colour >> 16 & 0xff) * shift;
		int g = (colour >> 8 & 0xff) * shift;
		int b = (colour & 0xff) * shift;
		int dx = Raster.width - width;
		int pixel = drawX + drawY * Raster.width;
		for (int x = 0; x < height; x++) {
			for (int y = -width; y < 0; y++) {
				int currentRed = (raster[pixel] >> 16 & 0xff) * reverseShift;
				int currentGreen = (raster[pixel] >> 8 & 0xff) * reverseShift;
				int currentBlue = (raster[pixel] & 0xff) * reverseShift;
				raster[pixel++] = (r + currentRed >> 8 << 16) + (g + currentGreen >> 8 << 8) + (b + currentBlue >> 8);
			}

			pixel += dx;
		}
	}

	public static void method338(int x, int y, int width, int height, int colour, int k) {
		method340(x, y, width, colour, k);
		method340(x, y + height - 1, width, colour, k);
		if (height >= 3) {
			method342(x, y + 1, height - 2, colour, k);
			method342(x + width - 1, y + 1, height - 2, colour, k);
		}
	}

	public static void method340(int x, int y, int length, int colour, int l) {
		if (y < clipBottom || y >= clipTop) {
			return;
		}

		if (x < clipLeft) {
			length -= clipLeft - x;
			x = clipLeft;
		}

		if (x + length > clipRight) {
			length = clipRight - x;
		}

		int j1 = 256 - l;
		int r = (colour >> 16 & 0xff) * l;
		int g = (colour >> 8 & 0xff) * l;
		int b = (colour & 0xff) * l;
		int pixel = x + y * width;

		for (int i = 0; i < length; i++) {
			int j2 = (raster[pixel] >> 16 & 0xff) * j1;
			int k2 = (raster[pixel] >> 8 & 0xff) * j1;
			int l2 = (raster[pixel] & 0xff) * j1;
			raster[pixel++] = (r + j2 >> 8 << 16) + (g + k2 >> 8 << 8) + (b + l2 >> 8);
		}
	}

	public static void method342(int x, int y, int length, int colour, int k) {
		if (x < clipLeft || x >= clipRight) {
			return;
		}
		if (y < clipBottom) {
			length -= clipBottom - y;
			y = clipBottom;
		}
		if (y + length > clipTop) {
			length = clipTop - y;
		}
		int j1 = 256 - k;
		int r = (colour >> 16 & 0xff) * k;
		int g = (colour >> 8 & 0xff) * k;
		int b = (colour & 0xff) * k;
		int pixel = x + y * width;
		for (int i = 0; i < length; i++) {
			int existingRed = (raster[pixel] >> 16 & 0xff) * j1;
			int existingGreen = (raster[pixel] >> 8 & 0xff) * j1;
			int existingBlue = (raster[pixel] & 0xff) * j1;
			raster[pixel] = (r + existingRed >> 8 << 16) + (g + existingGreen >> 8 << 8) + (b + existingBlue >> 8);
			pixel += width;
		}
	}

	public static void reset() {
		int pixelCount = width * height;
		for (int i = 0; i < pixelCount; i++) {
			raster[i] = 0;
		}
	}

	public static void setBounds(int clipTop, int clipLeft, int clipRight, int clipBottom) {
		if (clipLeft < 0) {
			clipLeft = 0;
		}
		if (clipBottom < 0) {
			clipBottom = 0;
		}
		if (clipRight > Raster.width) {
			clipRight = Raster.width;
		}
		if (clipTop > Raster.height) {
			clipTop = Raster.height;
		}
		Raster.clipLeft = clipLeft;
		Raster.clipBottom = clipBottom;
		Raster.clipRight = clipRight;
		Raster.clipTop = clipTop;
		anInt1385 = Raster.clipRight - 1;
		centreX = Raster.clipRight / 2;
		centreY = Raster.clipTop / 2;
	}

	public static void setDefaultBounds() {
		clipLeft = 0;
		clipBottom = 0;
		clipRight = width;
		clipTop = height;
		anInt1385 = clipRight - 1;
		centreX = clipRight / 2;
	}

}