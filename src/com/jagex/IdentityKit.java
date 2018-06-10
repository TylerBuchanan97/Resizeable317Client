package com.jagex;

public class IdentityKit {

	public static int count;
	public static IdentityKit[] kits;

	public static void init(Archive archive) {
		Buffer buffer = new Buffer(archive.extract("idk.dat"));
		count = buffer.readUShort();
		if (kits == null) {
			kits = new IdentityKit[count];
		}

		for (int i = 0; i < count; i++) {
			if (kits[i] == null) {
				kits[i] = new IdentityKit();
			}
			kits[i].decode(buffer);
		}
	}

	public int[] bodyModels;
	public int[] headModels = { -1, -1, -1, -1, -1 };
	public int[] originalColours = new int[6];
	public int part = -1;
	public int[] replacementColours = new int[6];
	public boolean validStyle = false;

	public boolean bodyLoaded() {
		if (bodyModels == null) {
			return true;
		}

		boolean loaded = true;
		for (int i = 0; i < bodyModels.length; i++) {
			if (!Model.loaded(bodyModels[i])) {
				loaded = false;
			}
		}

		return loaded;
	}

	public Model bodyModel() {
		if (bodyModels == null) {
			return null;
		}

		Model[] models = new Model[bodyModels.length];
		for (int i = 0; i < bodyModels.length; i++) {
			models[i] = Model.lookup(bodyModels[i]);
		}

		Model model;
		if (models.length == 1) {
			model = models[0];
		} else {
			model = new Model(models.length, models);
		}

		for (int i = 0; i < 6; i++) {
			if (originalColours[i] == 0) {
				break;
			}
			model.recolour(originalColours[i], replacementColours[i]);
		}

		return model;
	}

	public void decode(Buffer buffer) {
		do {
			int opcode = buffer.readUByte();
			if (opcode == 0) {
				return;
			}

			if (opcode == 1) {
				part = buffer.readUByte();
			} else if (opcode == 2) {
				int count = buffer.readUByte();
				bodyModels = new int[count];
				for (int i = 0; i < count; i++) {
					bodyModels[i] = buffer.readUShort();
				}
			} else if (opcode == 3) {
				validStyle = true;
			} else if (opcode >= 40 && opcode < 50) {
				originalColours[opcode - 40] = buffer.readUShort();
			} else if (opcode >= 50 && opcode < 60) {
				replacementColours[opcode - 50] = buffer.readUShort();
			} else if (opcode >= 60 && opcode < 70) {
				headModels[opcode - 60] = buffer.readUShort();
			} else {
				System.out.println("Error unrecognised config code: " + opcode);
			}
		} while (true);
	}

	public Model headModel() {
		Model[] models = new Model[5];
		int count = 0;
		for (int i = 0; i < 5; i++) {
			if (headModels[i] != -1) {
				models[count++] = Model.lookup(headModels[i]);
			}
		}

		Model model = new Model(count, models);
		for (int i = 0; i < 6; i++) {
			if (originalColours[i] == 0) {
				break;
			}
			model.recolour(originalColours[i], replacementColours[i]);
		}

		return model;
	}

	public boolean loaded() {
		boolean loaded = true;
		for (int i = 0; i < 5; i++) {
			if (headModels[i] != -1 && !Model.loaded(headModels[i])) {
				loaded = false;
			}
		}

		return loaded;
	}

}