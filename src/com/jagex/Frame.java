package com.jagex;

public class Frame {

	private static Frame[] frames;
	private static boolean[] transparent;

	public static void clearFrames() {
		frames = null;
	}

	public static void init(int size) {
		frames = new Frame[size + 1];

		transparent = new boolean[size + 1];
		for (int index = 0; index < size + 1; index++) {
			transparent[index] = true;
		}
	}

	public static boolean isInvalid(int frame) {
		return frame == -1;
	}

	public static void load(byte[] data) {
		Buffer buffer = new Buffer(data);
		buffer.position = data.length - 8;
		int attributesOffset = buffer.readUShort();
		int translationsOffset = buffer.readUShort();
		int durationsOffset = buffer.readUShort();
		int baseOffset = buffer.readUShort();

		int offset = 0;
		Buffer head = new Buffer(data);
		head.position = offset;
		offset += attributesOffset + 2;
		Buffer attributes = new Buffer(data);
		attributes.position = offset;
		offset += translationsOffset;
		Buffer translations = new Buffer(data);
		translations.position = offset;
		offset += durationsOffset;
		Buffer durations = new Buffer(data);
		durations.position = offset;
		offset += baseOffset;
		Buffer bases = new Buffer(data);
		bases.position = offset;
		FrameBase base = new FrameBase(bases);
		int frameCount = head.readUShort();

		int[] transformationIndices = new int[500];
		int[] transformX = new int[500];
		int[] transformY = new int[500];
		int[] transformZ = new int[500];

		for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
			int id = head.readUShort();
			Frame frame = frames[id] = new Frame();
			frame.duration = durations.readUByte();
			frame.base = base;
			int transformations = head.readUByte();
			int lastIndex = -1;
			int transformation = 0;

			for (int index = 0; index < transformations; index++) {
				int attribute = attributes.readUByte();
				if (attribute > 0) {
					if (base.transformationType[index] != 0) {
						for (int next = index - 1; next > lastIndex; next--) {
							if (base.transformationType[next] != 0) {
								continue;
							}

							transformationIndices[transformation] = next;
							transformX[transformation] = 0;
							transformY[transformation] = 0;
							transformZ[transformation] = 0;
							transformation++;
							break;
						}
					}

					transformationIndices[transformation] = index;

					int value = 0;
					if (base.transformationType[index] == 3) {
						value = 128;
					}

					if ((attribute & 1) != 0) {
						transformX[transformation] = translations.readSmarts();
					} else {
						transformX[transformation] = value;
					}
					if ((attribute & 2) != 0) {
						transformY[transformation] = translations.readSmarts();
					} else {
						transformY[transformation] = value;
					}
					if ((attribute & 4) != 0) {
						transformZ[transformation] = translations.readSmarts();
					} else {
						transformZ[transformation] = value;
					}

					lastIndex = index;
					transformation++;
					if (base.transformationType[index] == 5) {
						transparent[id] = false;
					}
				}
			}

			frame.transformationCount = transformation;
			frame.translationIndices = new int[transformation];
			frame.transformX = new int[transformation];
			frame.transformY = new int[transformation];
			frame.transformZ = new int[transformation];
			for (int index = 0; index < transformation; index++) {
				frame.translationIndices[index] = transformationIndices[index];
				frame.transformX[index] = transformX[index];
				frame.transformY[index] = transformY[index];
				frame.transformZ[index] = transformZ[index];
			}
		}
	}

	public static Frame lookup(int index) {
		if (frames == null) {
			return null;
		}
		return frames[index];
	}

	public FrameBase base;
	public int duration;
	public int transformationCount;
	public int[] transformX;
	public int[] transformY;
	public int[] transformZ;
	public int[] translationIndices;

}