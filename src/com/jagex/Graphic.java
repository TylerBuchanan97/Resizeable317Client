package com.jagex;

public class Graphic {

	public static int count;
	public static Graphic[] graphics;
	public static Cache models = new Cache(30);

	public static void init(Archive archive) {
		Buffer buffer = new Buffer(archive.extract("spotanim.dat"));
		count = buffer.readUShort();
		if (graphics == null) {
			graphics = new Graphic[count];
		}

		for (int id = 0; id < count; id++) {
			if (graphics[id] == null) {
				graphics[id] = new Graphic();
			}

			graphics[id].id = id;
			graphics[id].decode(buffer);
		}

	}

	public Animation animation;
	public int animationId = -1;
	public int breadthScale = 128;
	public int depthScale = 128;
	public int id;
	public int modelBrightness;
	public int modelId;
	public int modelShadow;
	public int[] originalColours = new int[6];
	public int[] replacementColours = new int[6];
	public int rotation;

	public void decode(Buffer buffer) {
		do {
			int opcode = buffer.readUByte();
			if (opcode == 0) {
				return;
			}

			if (opcode == 1) {
				modelId = buffer.readUShort();
			} else if (opcode == 2) {
				animationId = buffer.readUShort();
				if (Animation.animations != null) {
					animation = Animation.animations[animationId];
				}
			} else if (opcode == 4) {
				breadthScale = buffer.readUShort();
			} else if (opcode == 5) {
				depthScale = buffer.readUShort();
			} else if (opcode == 6) {
				rotation = buffer.readUShort();
			} else if (opcode == 7) {
				modelBrightness = buffer.readUByte();
			} else if (opcode == 8) {
				modelShadow = buffer.readUByte();
			} else if (opcode >= 40 && opcode < 50) {
				originalColours[opcode - 40] = buffer.readUShort();
			} else if (opcode >= 50 && opcode < 60) {
				replacementColours[opcode - 50] = buffer.readUShort();
			} else {
				System.out.println("Error unrecognised spotanim config code: " + opcode);
			}
		} while (true);
	}

	public Model getModel() {
		Model model = (Model) models.get(id);
		if (model != null) {
			return model;
		}

		model = Model.lookup(modelId);
		if (model == null) {
			return null;
		}

		for (int i = 0; i < 6; i++) {
			if (originalColours[0] != 0) {
				model.recolour(originalColours[i], replacementColours[i]);
			}
		}

		models.put(model, id);
		return model;
	}

}