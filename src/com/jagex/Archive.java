package com.jagex;

public final class Archive {

	public int entries;
	public int[] extractedSizes;
	public int[] identifiers;
	public int[] indices;
	public byte[] output;
	public int[] sizes;
	private boolean extracted;

	public Archive(byte[] data) {
		Buffer buffer = new Buffer(data);
		int length = buffer.readTriByte();
		int decompressedLength = buffer.readTriByte();

		if (decompressedLength != length) {
			byte[] output = new byte[length];
			BZip2Decompressor.decompress(output, length, data, decompressedLength, 6);
			this.output = output;
			buffer = new Buffer(this.output);
			extracted = true;
		} else {
			output = data;
			extracted = false;
		}

		entries = buffer.readUShort();
		identifiers = new int[entries];
		extractedSizes = new int[entries];
		sizes = new int[entries];
		indices = new int[entries];

		int offset = buffer.position + entries * 10;
		for (int file = 0; file < entries; file++) {
			identifiers[file] = buffer.readInt();
			extractedSizes[file] = buffer.readTriByte();
			sizes[file] = buffer.readTriByte();
			indices[file] = offset;
			offset += sizes[file];
		}
	}

	public byte[] extract(String name) {
		int hash = 0;
		name = name.toUpperCase();
		for (int index = 0; index < name.length(); index++) {
			hash = hash * 61 + name.charAt(index) - 32;
		}

		for (int file = 0; file < entries; file++) {
			if (identifiers[file] == hash) {
				byte[] output = new byte[extractedSizes[file]];

				if (!extracted) {
					BZip2Decompressor.decompress(output, extractedSizes[file], this.output, sizes[file], indices[file]);
				} else {
					for (int i = 0; i < extractedSizes[file]; i++) {
						output[i] = this.output[indices[file] + i];
					}
				}

				return output;
			}
		}

		return null;
	}

}