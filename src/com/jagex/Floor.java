package com.jagex;

public class Floor {

	public static int count;
	public static Floor[] floors;

	public static void init(Archive archive) {
		Buffer buffer = new Buffer(archive.extract("flo.dat"));
		count = buffer.readUShort();

		if (floors == null) {
			floors = new Floor[count];
		}

		for (int id = 0; id < count; id++) {
			if (floors[id] == null) {
				floors[id] = new Floor();
			}
			floors[id].decode(buffer);
		}
	}

	public int chroma;
	public int colour;
	public int hue;
	public int luminance;
	public String name;
	public int rgb;
	public int saturation;
	public boolean shadowing = true;
	public int texture = -1;
	public int weightedHue;

	public void decode(Buffer buffer) {
		do {
			int opcode = buffer.readUByte();
			if (opcode == 0) {
				return;
			}

			if (opcode == 1) {
				rgb = buffer.readTriByte();
				blend(rgb);
			} else if (opcode == 2) {
				texture = buffer.readUByte();
			} else if (opcode == 5) {
				shadowing = false;
			} else if (opcode == 6) {
				name = buffer.readString();
			} else if (opcode == 7) {
				int hue = this.hue;
				int saturation = this.saturation;
				int luminance = this.luminance;
				int chroma = this.weightedHue;
				int rgb = buffer.readTriByte();
				blend(rgb);
				this.hue = hue;
				this.saturation = saturation;
				this.luminance = luminance;
				this.weightedHue = chroma;
				this.chroma = chroma;
			} else if (opcode != 3) {
				System.out.println("Error unrecognised config code: " + opcode);
			}
		} while (true);
	}

	private void blend(int rgb) {
		double r = (rgb >> 16 & 0xff) / 256D;
		double g = (rgb >> 8 & 0xff) / 256D;
		double b = (rgb & 0xff) / 256D;
		double darkest = r;

		if (g < darkest) {
			darkest = g;
		}
		if (b < darkest) {
			darkest = b;
		}

		double brightest = r;
		if (g > brightest) {
			brightest = g;
		}
		if (b > brightest) {
			brightest = b;
		}

		double hue = 0;
		double saturation = 0;
		double lumination = (darkest + brightest) / 2;

		if (darkest != brightest) {
			if (lumination < 0.5D) {
				saturation = (brightest - darkest) / (brightest + darkest);
			}
			if (lumination >= 0.5D) {
				saturation = (brightest - darkest) / (2 - brightest - darkest);
			}
			if (r == brightest) {
				hue = (g - b) / (brightest - darkest);
			} else if (g == brightest) {
				hue = 2 + (b - r) / (brightest - darkest);
			} else if (b == brightest) {
				hue = 4 + (r - g) / (brightest - darkest);
			}
		}

		hue /= 6;
		this.hue = (int) (hue * 256);
		this.saturation = (int) (saturation * 256);
		this.luminance = (int) (lumination * 256);

		if (this.saturation < 0) {
			this.saturation = 0;
		} else if (this.saturation > 255) {
			this.saturation = 255;
		}

		if (this.luminance < 0) {
			this.luminance = 0;
		} else if (this.luminance > 255) {
			this.luminance = 255;
		}

		if (lumination > 0.5D) {
			chroma = (int) ((1 - lumination) * saturation * 512);
		} else {
			chroma = (int) (lumination * saturation * 512);
		}
		if (chroma < 1) {
			chroma = 1;
		}

		weightedHue = (int) (hue * chroma);
		int blendedHue = this.hue + (int) (Math.random() * 16) - 8;
		if (blendedHue < 0) {
			blendedHue = 0;
		} else if (blendedHue > 255) {
			blendedHue = 255;
		}

		int blendedSat = this.saturation + (int) (Math.random() * 48) - 24;
		if (blendedSat < 0) {
			blendedSat = 0;
		} else if (blendedSat > 255) {
			blendedSat = 255;
		}

		int blendedLum = this.luminance + (int) (Math.random() * 48) - 24;
		if (blendedLum < 0) {
			blendedLum = 0;
		} else if (blendedLum > 255) {
			blendedLum = 255;
		}
		colour = encode(blendedHue, blendedSat, blendedLum);
	}

	/**
	 * Encodes the hue, saturation, and luminance into a colour value.
	 * 
	 * @param hue The hue.
	 * @param saturation The saturation.
	 * @param luminance The luminance.
	 * @return The colour.
	 */
	private final int encode(int hue, int saturation, int luminance) {
		if (luminance > 179) {
			saturation /= 2;
		}

		if (luminance > 192) {
			saturation /= 2;
		}

		if (luminance > 217) {
			saturation /= 2;
		}

		if (luminance > 243) {
			saturation /= 2;
		}

		return (hue / 4 << 10) + (saturation / 32 << 7) + luminance / 2;
	}

}