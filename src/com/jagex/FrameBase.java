package com.jagex;

public class FrameBase {

	public int count;
	public int[][] groups;
	public int[] transformationType;

	public FrameBase(Buffer buffer) {
		count = buffer.readUByte();
		transformationType = new int[count];
		groups = new int[count][];
		for (int index = 0; index < count; index++) {
			transformationType[index] = buffer.readUByte();
		}

		for (int group = 0; group < count; group++) {
			int groupCount = buffer.readUByte();
			groups[group] = new int[groupCount];
			for (int index = 0; index < groupCount; index++) {
				groups[group][index] = buffer.readUByte();
			}
		}
	}

}