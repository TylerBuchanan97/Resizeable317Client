package com.jagex;

public class VariableBits {

	public static VariableBits[] bits;
	public static int count;

	public static void init(Archive archive) {
		Buffer buffer = new Buffer(archive.extract("varbit.dat"));
		count = buffer.readUShort();
		if (bits == null) {
			bits = new VariableBits[count];
		}

		for (int i = 0; i < count; i++) {
			if (bits[i] == null) {
				bits[i] = new VariableBits();
			}
			bits[i].decode(buffer);
		}

		if (buffer.position != buffer.payload.length) {
			System.out.println("varbit load mismatch");
		}
	}

	public int high;
	public int low;
	public int setting;

	public void decode(Buffer buffer) {
		do {
			int opcode = buffer.readUByte();
			if (opcode == 0) {
				return;
			}

			if (opcode == 1) {
				setting = buffer.readUShort();
				low = buffer.readUByte();
				high = buffer.readUByte();
			} else if (opcode == 10) {
				buffer.readString();
			} else if (opcode == 3 || opcode == 4) {
				buffer.readInt();
			} else if (opcode != 2) {
				System.out.println("Error unrecognised config code: " + opcode);
			}
		} while (true);
	}

}