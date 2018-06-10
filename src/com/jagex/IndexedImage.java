package com.jagex;

public final class IndexedImage extends Raster {

	public int drawOffsetX;
	public int drawOffsetY;
	public int height;
	public int[] palette;
	public byte[] raster;
	public int resizeHeight;
	public int resizeWidth;
	public int width;

	public IndexedImage(Archive archive, String name, int id) {
		Buffer image = new Buffer(archive.extract(name + ".dat"));
		Buffer meta = new Buffer(archive.extract("index.dat"));
		meta.position = image.readUShort();
		resizeWidth = meta.readUShort();
		resizeHeight = meta.readUShort();
		int colours = meta.readUByte();
		palette = new int[colours];

		for (int index = 0; index < colours - 1; index++) {
			palette[index + 1] = meta.readTriByte();
		}

		for (int i = 0; i < id; i++) {
			meta.position += 2;
			image.position += meta.readUShort() * meta.readUShort();
			meta.position++;
		}

		drawOffsetX = meta.readUByte();
		drawOffsetY = meta.readUByte();
		width = meta.readUShort();
		height = meta.readUShort();
		int type = meta.readUByte();
		int pixels = width * height;
		raster = new byte[pixels];

		if (type == 0) {
			for (int index = 0; index < pixels; index++) {
				raster[index] = image.readByte();
			}
		} else if (type == 1) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					raster[x + y * width] = image.readByte();
				}
			}
		}
	}

	public void downscale() {
		resizeWidth /= 2;
		resizeHeight /= 2;
		byte[] raster = new byte[resizeWidth * resizeHeight];
		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				raster[(x + drawOffsetX >> 1) + (y + drawOffsetY >> 1) * resizeWidth] = this.raster[i++];
			}
		}

		this.raster = raster;
		width = resizeWidth;
		height = resizeHeight;
		drawOffsetX = 0;
		drawOffsetY = 0;
	}

	public void draw(int x, int y) {
		x += drawOffsetX;
		y += drawOffsetY;
		int rasterOffset = x + y * Raster.width;
		int sourceOffset = 0;
		int height = this.height;
		int width = this.width;
		int deltaWidth = Raster.width - width;
		int clip = 0;

		if (y < Raster.clipBottom) {
			int dy = Raster.clipBottom - y;
			height -= dy;
			y = Raster.clipBottom;
			sourceOffset += dy * width;
			rasterOffset += dy * Raster.width;
		}

		if (y + height > Raster.clipTop) {
			height -= y + height - Raster.clipTop;
		}

		if (x < Raster.clipLeft) {
			int dx = Raster.clipLeft - x;
			width -= dx;
			x = Raster.clipLeft;
			sourceOffset += dx;
			rasterOffset += dx;
			clip += dx;
			deltaWidth += dx;
		}

		if (x + width > Raster.clipRight) {
			int dx = x + width - Raster.clipRight;
			width -= dx;
			clip += dx;
			deltaWidth += dx;
		}

		if (width > 0 && height > 0) {
			draw(height, Raster.raster, raster, deltaWidth, rasterOffset, width, sourceOffset, palette, clip);
		}
	}

	public void flipHorizontally() {
		byte[] raster = new byte[width * height];
		int pixel = 0;
		for (int y = 0; y < height; y++) {
			for (int x = width - 1; x >= 0; x--) {
				raster[pixel++] = this.raster[x + y * width];
			}
		}

		this.raster = raster;
		drawOffsetX = resizeWidth - width - drawOffsetX;
	}

	public void flipVertically() {
		byte[] raster = new byte[width * height];
		int pixel = 0;
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				raster[pixel++] = this.raster[x + y * width];
			}
		}

		this.raster = raster;
		drawOffsetY = resizeHeight - height - drawOffsetY;
	}

	public void offsetColour(int redOffset, int greenOffset, int blueOffset) {
		for (int i = 0; i < palette.length; i++) {
			int red = palette[i] >> 16 & 0xff;
			red += redOffset;

			if (red < 0) {
				red = 0;
			} else if (red > 255) {
				red = 255;
			}

			int green = palette[i] >> 8 & 0xff;
			green += greenOffset;

			if (green < 0) {
				green = 0;
			} else if (green > 255) {
				green = 255;
			}

			int blue = palette[i] & 0xff;
			blue += blueOffset;

			if (blue < 0) {
				blue = 0;
			} else if (blue > 255) {
				blue = 255;
			}

			palette[i] = (red << 16) + (green << 8) + blue;
		}
	}

	public void resize() {
		if (width == resizeWidth && height == resizeHeight) {
			return;
		}

		byte[] raster = new byte[resizeWidth * resizeHeight];
		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				raster[x + drawOffsetX + (y + drawOffsetY) * resizeWidth] = this.raster[i++];
			}
		}

		this.raster = raster;
		width = resizeWidth;
		height = resizeHeight;
		drawOffsetX = 0;
		drawOffsetY = 0;
	}

	private void draw(int height, int[] raster, byte[] image, int rasterOffset, int rasterPosition, int width, int imagePosition,
			int[] palette, int imageOffset) {
		int minX = -(width >> 2);
		width = -(width & 3);
		for (int y = -height; y < 0; y++) {
			for (int x = minX; x < 0; x++) {
				byte pixel = image[imagePosition++];
				if (pixel != 0) {
					raster[rasterPosition++] = palette[pixel & 0xff];
				} else {
					rasterPosition++;
				}

				pixel = image[imagePosition++];
				if (pixel != 0) {
					raster[rasterPosition++] = palette[pixel & 0xff];
				} else {
					rasterPosition++;
				}

				pixel = image[imagePosition++];
				if (pixel != 0) {
					raster[rasterPosition++] = palette[pixel & 0xff];
				} else {
					rasterPosition++;
				}

				pixel = image[imagePosition++];
				if (pixel != 0) {
					raster[rasterPosition++] = palette[pixel & 0xff];
				} else {
					rasterPosition++;
				}
			}

			for (int x = width; x < 0; x++) {
				byte pixel = image[imagePosition++];
				if (pixel != 0) {
					raster[rasterPosition++] = palette[pixel & 0xff];
				} else {
					rasterPosition++;
				}
			}

			rasterPosition += rasterOffset;
			imagePosition += imageOffset;
		}
	}

}