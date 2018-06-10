package com.jagex;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;

public final class DirectSprite extends Raster {

	public int height;
	public int horizontalOffset;
	public int[] raster;
	public int resizeHeight;
	public int resizeWidth;
	public int verticalOffset;
	public int width;

	public DirectSprite(Archive archive, String name, int id) {
		Buffer sprite = new Buffer(archive.extract(name + ".dat"));
		Buffer meta = new Buffer(archive.extract("index.dat"));
		meta.position = sprite.readUShort();
		resizeWidth = meta.readUShort();
		resizeHeight = meta.readUShort();
		int colours = meta.readUByte();
		int[] palette = new int[colours];

		for (int index = 0; index < colours - 1; index++) {
			int colour = meta.readTriByte();
			palette[index + 1] = colour == 0 ? 1 : colour;
		}

		for (int i = 0; i < id; i++) {
			meta.position += 2;
			sprite.position += meta.readUShort() * meta.readUShort();
			meta.position++;
		}

		horizontalOffset = meta.readUByte();
		verticalOffset = meta.readUByte();
		width = meta.readUShort();
		height = meta.readUShort();
		int format = meta.readUByte();
		int pixels = width * height;
		raster = new int[pixels];

		if (format == 0) {
			for (int index = 0; index < pixels; index++) {
				raster[index] = palette[sprite.readUByte()];
			}
		} else if (format == 1) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					raster[x + y * width] = palette[sprite.readUByte()];
				}
			}
		}
	}

	public DirectSprite(byte[] data, Component component) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(data);
			MediaTracker mediaTracker = new MediaTracker(component);
			mediaTracker.addImage(image, 0);
			mediaTracker.waitForAll();
			width = image.getWidth(component);
			height = image.getHeight(component);
			resizeWidth = width;
			resizeHeight = height;
			horizontalOffset = 0;
			verticalOffset = 0;
			raster = new int[width * height];
			PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, raster, 0, width);
			grabber.grabPixels();
		} catch (Exception ex) {
			System.out.println("Error converting jpg");
		}
	}

	public DirectSprite(int width, int height) {
		raster = new int[width * height];
		this.width = resizeWidth = width;
		this.height = resizeHeight = height;
		horizontalOffset = verticalOffset = 0;
	}

	public void drawSprite(int x, int y) {
		x += horizontalOffset;
		y += verticalOffset;
		int rasterClip = x + y * Raster.width;
		int imageClip = 0;
		int height = this.height;
		int width = this.width;
		int rasterOffset = Raster.width - width;
		int imageOffset = 0;

		if (y < Raster.clipBottom) {
			int dy = Raster.clipBottom - y;
			height -= dy;
			y = Raster.clipBottom;
			imageClip += dy * width;
			rasterClip += dy * Raster.width;
		}

		if (y + height > Raster.clipTop) {
			height -= y + height - Raster.clipTop;
		}

		if (x < Raster.clipLeft) {
			int dx = Raster.clipLeft - x;
			width -= dx;
			x = Raster.clipLeft;
			imageClip += dx;
			rasterClip += dx;
			imageOffset += dx;
			rasterOffset += dx;
		}

		if (x + width > Raster.clipRight) {
			int dx = x + width - Raster.clipRight;
			width -= dx;
			imageOffset += dx;
			rasterOffset += dx;
		}

		if (width > 0 && height > 0) {
			draw(Raster.raster, raster, 0, imageClip, rasterClip, width, height, rasterOffset, imageOffset);
		}
	}

	public void initRaster() {
		Raster.init(height, width, raster);
	}

	public void method346(int x, int y) {
		x += horizontalOffset;
		y += verticalOffset;
		int l = x + y * Raster.width;
		int i1 = 0;
		int j1 = height;
		int k1 = width;
		int l1 = Raster.width - k1;
		int i2 = 0;
		if (y < Raster.clipBottom) {
			int j2 = Raster.clipBottom - y;
			j1 -= j2;
			y = Raster.clipBottom;
			i1 += j2 * k1;
			l += j2 * Raster.width;
		}
		if (y + j1 > Raster.clipTop) {
			j1 -= y + j1 - Raster.clipTop;
		}
		if (x < Raster.clipLeft) {
			int k2 = Raster.clipLeft - x;
			k1 -= k2;
			x = Raster.clipLeft;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (x + k1 > Raster.clipRight) {
			int l2 = x + k1 - Raster.clipRight;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (k1 > 0 && j1 > 0) {
			method347(l, k1, j1, i2, i1, l1, raster, Raster.raster);
		}
	}

	public void method350(int x, int y, int k) {
		x += horizontalOffset;
		y += verticalOffset;
		int i1 = x + y * Raster.width;
		int j1 = 0;
		int height = this.height;
		int width = this.width;
		int dx = Raster.width - width;
		int j2 = 0;
		if (y < Raster.clipBottom) {
			int k2 = Raster.clipBottom - y;
			height -= k2;
			y = Raster.clipBottom;
			j1 += k2 * width;
			i1 += k2 * Raster.width;
		}
		if (y + height > Raster.clipTop) {
			height -= y + height - Raster.clipTop;
		}
		if (x < Raster.clipLeft) {
			int l2 = Raster.clipLeft - x;
			width -= l2;
			x = Raster.clipLeft;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			dx += l2;
		}
		if (x + width > Raster.clipRight) {
			int i3 = x + width - Raster.clipRight;
			width -= i3;
			j2 += i3;
			dx += i3;
		}
		if (width > 0 && height > 0) {
			method351(j1, width, Raster.raster, 0, raster, j2, height, dx, k, i1);
		}
	}

	public void method352(int i, int theta, int ai[], int k, int ai1[], int i1, int j1, int k1, int l1, int i2) {
		try {
			int j2 = -l1 / 2;
			int k2 = -i / 2;
			int l2 = (int) (Math.sin(theta / 326.11D) * 65536);
			int i3 = (int) (Math.cos(theta / 326.11D) * 65536);
			l2 = l2 * k >> 8;
			i3 = i3 * k >> 8;
			int j3 = (i2 << 16) + k2 * l2 + j2 * i3;
			int k3 = (i1 << 16) + k2 * i3 - j2 * l2;
			int l3 = k1 + j1 * Raster.width;
			for (j1 = 0; j1 < i; j1++) {
				int i4 = ai1[j1];
				int j4 = l3 + i4;
				int k4 = j3 + i3 * i4;
				int l4 = k3 - l2 * i4;
				for (k1 = -ai[j1]; k1 < 0; k1++) {
					Raster.raster[j4++] = raster[(k4 >> 16) + (l4 >> 16) * width];
					k4 += i3;
					l4 -= l2;
				}

				j3 += l2;
				k3 += i3;
				l3 += Raster.width;
			}
		} catch (Exception ex) {
		}
	}

	public void method353(int i, int j, int k, int l, int j1, int k1, double theta, int l1) {
		try {
			int i2 = -k / 2;
			int j2 = -k1 / 2;
			int k2 = (int) (Math.sin(theta) * 65536D);
			int l2 = (int) (Math.cos(theta) * 65536D);
			k2 = k2 * j1 >> 8;
			l2 = l2 * j1 >> 8;
			int i3 = (l << 16) + j2 * k2 + i2 * l2;
			int j3 = (j << 16) + j2 * l2 - i2 * k2;
			int k3 = l1 + i * Raster.width;
			for (i = 0; i < k1; i++) {
				int l3 = k3;
				int i4 = i3;
				int j4 = j3;
				for (l1 = -k; l1 < 0; l1++) {
					int k4 = raster[(i4 >> 16) + (j4 >> 16) * width];
					if (k4 != 0) {
						Raster.raster[l3++] = k4;
					} else {
						l3++;
					}
					i4 += l2;
					j4 -= k2;
				}

				i3 += k2;
				j3 += l2;
				k3 += Raster.width;
			}
		} catch (Exception ex) {
		}
	}

	public void method354(IndexedImage image, int y, int x) {
		x += horizontalOffset;
		y += verticalOffset;
		int k = x + y * Raster.width;
		int l = 0;
		int height = this.height;
		int width = this.width;
		int deltaWidth = Raster.width - width;
		int l1 = 0;

		if (y < Raster.clipBottom) {
			int dy = Raster.clipBottom - y;
			height -= dy;
			y = Raster.clipBottom;
			l += dy * width;
			k += dy * Raster.width;
		}

		if (y + height > Raster.clipTop) {
			height -= y + height - Raster.clipTop;
		}

		if (x < Raster.clipLeft) {
			int dx = Raster.clipLeft - x;
			width -= dx;
			x = Raster.clipLeft;
			l += dx;
			k += dx;
			l1 += dx;
			deltaWidth += dx;
		}

		if (x + width > Raster.clipRight) {
			int dx = x + width - Raster.clipRight;
			width -= dx;
			l1 += dx;
			deltaWidth += dx;
		}
		if (width > 0 && height > 0) {
			method355(raster, width, image.raster, height, Raster.raster, 0, deltaWidth, k, l1, l);
		}
	}

	public void recolour(int redOffset, int greenOffset, int blueOffset) {
		for (int index = 0; index < raster.length; index++) {
			int rgb = raster[index];

			if (rgb != 0) {
				int red = rgb >> 16 & 0xff;
				red += redOffset;

				if (red < 1) {
					red = 1;
				} else if (red > 255) {
					red = 255;
				}

				int green = rgb >> 8 & 0xff;
				green += greenOffset;

				if (green < 1) {
					green = 1;
				} else if (green > 255) {
					green = 255;
				}

				int blue = rgb & 0xff;
				blue += blueOffset;

				if (blue < 1) {
					blue = 1;
				} else if (blue > 255) {
					blue = 255;
				}

				raster[index] = (red << 16) + (green << 8) + blue;
			}
		}
	}

	public void resize() {
		int[] raster = new int[resizeWidth * resizeHeight];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				raster[(y + verticalOffset) * resizeWidth + x + horizontalOffset] = this.raster[y * width + x];
			}
		}

		this.raster = raster;
		width = resizeWidth;
		height = resizeHeight;
		horizontalOffset = 0;
		verticalOffset = 0;
	}

	private void draw(int raster[], int[] image, int colour, int imagePosition, int rasterPosition, int width, int height,
			int rasterOffset, int imageOffset) {
		int minX = -(width >> 2);
		width = -(width & 3);
		for (int y = -height; y < 0; y++) {
			for (int x = minX; x < 0; x++) {
				colour = image[imagePosition++];
				if (colour != 0) {
					raster[rasterPosition++] = colour;
				} else {
					rasterPosition++;
				}
				colour = image[imagePosition++];

				if (colour != 0) {
					raster[rasterPosition++] = colour;
				} else {
					rasterPosition++;
				}
				colour = image[imagePosition++];

				if (colour != 0) {
					raster[rasterPosition++] = colour;
				} else {
					rasterPosition++;
				}
				colour = image[imagePosition++];

				if (colour != 0) {
					raster[rasterPosition++] = colour;
				} else {
					rasterPosition++;
				}
			}

			for (int k2 = width; k2 < 0; k2++) {
				colour = image[imagePosition++];
				if (colour != 0) {
					raster[rasterPosition++] = colour;
				} else {
					rasterPosition++;
				}
			}

			rasterPosition += rasterOffset;
			imagePosition += imageOffset;
		}
	}

	private void method347(int rasterPosition, int width, int height, int sourceOffset, int sourcePosition, int rasterOffset,
			int[] source, int[] raster) {
		int minX = -(width >> 2);
		width = -(width & 3);
		for (int y = -height; y < 0; y++) {
			for (int x = minX; x < 0; x++) {
				raster[rasterPosition++] = source[sourcePosition++];
				raster[rasterPosition++] = source[sourcePosition++];
				raster[rasterPosition++] = source[sourcePosition++];
				raster[rasterPosition++] = source[sourcePosition++];
			}

			for (int k2 = width; k2 < 0; k2++) {
				raster[rasterPosition++] = source[sourcePosition++];
			}

			rasterPosition += rasterOffset;
			sourcePosition += sourceOffset;
		}
	}

	private void method351(int i, int j, int ai[], int k, int ai1[], int l, int i1, int j1, int k1, int l1) {
		int j2 = 256 - k1;
		for (int k2 = -i1; k2 < 0; k2++) {
			for (int l2 = -j; l2 < 0; l2++) {
				k = ai1[i++];
				if (k != 0) {
					int i3 = ai[l1];
					ai[l1++] = ((k & 0xff00ff) * k1 + (i3 & 0xff00ff) * j2 & 0xff00ff00)
							+ ((k & 0xff00) * k1 + (i3 & 0xff00) * j2 & 0xff0000) >> 8;
				} else {
					l1++;
				}
			}

			l1 += j1;
			i += l;
		}
	}

	private void method355(int ai[], int i, byte abyte0[], int j, int ai1[], int k, int l, int i1, int j1, int k1) {
		int l1 = -(i >> 2);
		i = -(i & 3);
		for (int j2 = -j; j2 < 0; j2++) {
			for (int k2 = l1; k2 < 0; k2++) {
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0) {
					ai1[i1++] = k;
				} else {
					i1++;
				}
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0) {
					ai1[i1++] = k;
				} else {
					i1++;
				}
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0) {
					ai1[i1++] = k;
				} else {
					i1++;
				}
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0) {
					ai1[i1++] = k;
				} else {
					i1++;
				}
			}

			for (int l2 = i; l2 < 0; l2++) {
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0) {
					ai1[i1++] = k;
				} else {
					i1++;
				}
			}

			i1 += l;
			k1 += j1;
		}
	}

}