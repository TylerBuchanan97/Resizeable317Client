package com.jagex;

public class VariableParameter {

	public static int count;
	public static VariableParameter[] parameters;

	public static void init(Archive archive) {
		Buffer buffer = new Buffer(archive.extract("varp.dat"));
		count = buffer.readUShort();
		if (parameters == null) {
			parameters = new VariableParameter[count];
		}

		for (int id = 0; id < count; id++) {
			if (parameters[id] == null) {
				parameters[id] = new VariableParameter();
			}
			parameters[id].decode(buffer);
		}

		if (buffer.position != buffer.payload.length) {
			System.out.println("varptype load mismatch");
		}
	}

	public int parameter;

	public void decode(Buffer buffer) {
		do {
			int opcode = buffer.readUByte();
			if (opcode == 0) {
				return;
			}

			if (opcode == 1 || opcode == 2) {
				buffer.readUByte();
			} else if (opcode == 5) {
				parameter = buffer.readUShort();
			} else if (opcode == 7) {
				buffer.readInt();
			} else if (opcode == 10) {
				buffer.readString();
			} else if (opcode == 12) {
				buffer.readInt();
			} else if (opcode != 4 || opcode != 6 || opcode != 8 || opcode != 11 || opcode != 13) {
				System.out.println("Error unrecognised config code: " + opcode);
			}
		} while (true);
	}

}