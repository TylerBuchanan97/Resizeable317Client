package com.jagex;

final class NpcDefinition {

	public static Client client;
	public static int count;
	public static Cache modelCache = new Cache(30);
	private static NpcDefinition[] cache;
	private static int cacheIndex;
	private static Buffer data;
	private static int[] offsets;

	public static final void init(Archive archive) {
		data = new Buffer(archive.extract("npc.dat"));
		Buffer indices = new Buffer(archive.extract("npc.idx"));
		count = indices.readUShort();
		offsets = new int[count];

		int index = 2;
		for (int id = 0; id < count; id++) {
			offsets[id] = index;
			index += indices.readUShort();
		}

		cache = new NpcDefinition[20];
		for (int id = 0; id < 20; id++) {
			cache[id] = new NpcDefinition();
		}
	}

	public static final NpcDefinition lookup(int id) {
		for (int index = 0; index < 20; index++) {
			if (cache[index].id == id) {
				return cache[index];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		NpcDefinition definition = cache[cacheIndex] = new NpcDefinition();
		data.position = offsets[id];
		definition.id = id;
		definition.decode(data);
		return definition;
	}

	public static final void reset() {
		modelCache = null;
		offsets = null;
		cache = null;
		data = null;
	}

	public boolean clickable = true;
	public int combat = -1;
	public byte[] description;
	public boolean drawMinimapDot = false;
	public int halfTurnAnimation = -1;
	public int headIcon = -1;
	public long id = -1;
	public int idleAnimation = -1;
	public String[] interactions;
	public int[] morphisms;
	public int morphVarbitIndex = -1;
	public int morphVariableIndex = -1;
	public String name;
	public boolean priorityRender = false;
	public int rotateAntiClockwiseAnimation = -1;
	public int rotateClockwiseAnimation = -1;
	public int rotation = 32;
	public byte size = 1;
	public int walkingAnimation = -1;
	private int[] additionalModels;
	private int lightModifier;
	private int[] modelIds;
	private int[] originalColours;
	private int[] replacementColours;
	private int scaleXY = 128;
	private int scaleZ = 128;
	private int shadowModifier;

	public final Model getAnimatedModel(int primaryFrame, int secondaryFrame, int[] interleaveOrder) {
		if (morphisms != null) {
			NpcDefinition definition = morph();
			if (definition == null) {
				return null;
			}
			return definition.getAnimatedModel(primaryFrame, secondaryFrame, interleaveOrder);
		}

		Model model = (Model) modelCache.get(id);
		if (model == null) {
			boolean unprepared = false;
			for (int part = 0; part < modelIds.length; part++) {
				if (!Model.loaded(modelIds[part])) {
					unprepared = true;
				}
			}

			if (unprepared) {
				return null;
			}
			Model[] models = new Model[this.modelIds.length];
			for (int part = 0; part < this.modelIds.length; part++) {
				models[part] = Model.lookup(this.modelIds[part]);
			}

			if (models.length == 1) {
				model = models[0];
			} else {
				model = new Model(models.length, models);
			}

			if (originalColours != null) {
				for (int i = 0; i < originalColours.length; i++) {
					model.recolour(originalColours[i], replacementColours[i]);
				}

			}
			model.skin();
			model.light(64 + lightModifier, 850 + shadowModifier, -30, -50, -30, true);
			modelCache.put(model, id);
		}

		Model empty = Model.EMPTY_MODEL;
		empty.method464(model, Frame.isInvalid(primaryFrame) & Frame.isInvalid(secondaryFrame));

		if (primaryFrame != -1 && secondaryFrame != -1) {
			empty.apply(primaryFrame, secondaryFrame, interleaveOrder);
		} else if (primaryFrame != -1) {
			empty.apply(primaryFrame);
		}

		if (scaleXY != 128 || scaleZ != 128) {
			empty.scale(scaleXY, scaleXY, scaleZ);
		}

		empty.method466();
		empty.faceGroups = null;
		empty.vertexGroups = null;
		if (size == 1) {
			empty.aBoolean1659 = true;
		}
		return empty;
	}

	public final Model model() {
		if (morphisms != null) {
			NpcDefinition definition = morph();
			if (definition == null) {
				return null;
			}
			return definition.model();
		}
		if (additionalModels == null) {
			return null;
		}

		boolean unprepared = false;
		for (int i = 0; i < additionalModels.length; i++) {
			if (!Model.loaded(additionalModels[i])) {
				unprepared = true;
			}
		}

		if (unprepared) {
			return null;
		}
		Model[] additional = new Model[additionalModels.length];
		for (int i = 0; i < additionalModels.length; i++) {
			additional[i] = Model.lookup(additionalModels[i]);
		}

		Model model;
		if (additional.length == 1) {
			model = additional[0];
		} else {
			model = new Model(additional.length, additional);
		}

		if (originalColours != null) {
			for (int i = 0; i < originalColours.length; i++) {
				model.recolour(originalColours[i], replacementColours[i]);
			}

		}
		return model;
	}

	public final NpcDefinition morph() {
		int child = -1;
		if (morphVarbitIndex != -1) {
			VariableBits bits = VariableBits.bits[morphVarbitIndex];
			int variable = bits.setting;
			int low = bits.low;
			int high = bits.high;
			int mask = Client.BIT_MASKS[high - low];
			child = client.settings[variable] >> low & mask;
		} else if (morphVariableIndex != -1) {
			child = client.settings[morphVariableIndex];
		}

		if (child < 0 || child >= morphisms.length || morphisms[child] == -1) {
			return null;
		}

		return lookup(morphisms[child]);
	}

	private final void decode(Buffer buffer) {
		do {
			int opcode = buffer.readUByte();
			if (opcode == 0) {
				return;
			}

			if (opcode == 1) {
				int count = buffer.readUByte();
				modelIds = new int[count];
				for (int i = 0; i < count; i++) {
					modelIds[i] = buffer.readUShort();
				}
			} else if (opcode == 2) {
				name = buffer.readString();
			} else if (opcode == 3) {
				description = buffer.readStringBytes();
			} else if (opcode == 12) {
				size = buffer.readByte();
			} else if (opcode == 13) {
				idleAnimation = buffer.readUShort();
			} else if (opcode == 14) {
				walkingAnimation = buffer.readUShort();
			} else if (opcode == 17) {
				walkingAnimation = buffer.readUShort();
				halfTurnAnimation = buffer.readUShort();
				rotateClockwiseAnimation = buffer.readUShort();
				rotateAntiClockwiseAnimation = buffer.readUShort();
			} else if (opcode >= 30 && opcode < 40) {
				if (interactions == null) {
					interactions = new String[5];
				}
				interactions[opcode - 30] = buffer.readString();
				if (interactions[opcode - 30].equalsIgnoreCase("hidden")) {
					interactions[opcode - 30] = null;
				}
			} else if (opcode == 40) {
				int count = buffer.readUByte();
				originalColours = new int[count];
				replacementColours = new int[count];
				for (int i = 0; i < count; i++) {
					originalColours[i] = buffer.readUShort();
					replacementColours[i] = buffer.readUShort();
				}

			} else if (opcode == 60) {
				int count = buffer.readUByte();
				additionalModels = new int[count];

				for (int i = 0; i < count; i++) {
					additionalModels[i] = buffer.readUShort();
				}
			} else if (opcode == 90) {
				buffer.readUShort();
			} else if (opcode == 91) {
				buffer.readUShort();
			} else if (opcode == 92) {
				buffer.readUShort();
			} else if (opcode == 93) {
				drawMinimapDot = false;
			} else if (opcode == 95) {
				combat = buffer.readUShort();
			} else if (opcode == 97) {
				scaleXY = buffer.readUShort();
			} else if (opcode == 98) {
				scaleZ = buffer.readUShort();
			} else if (opcode == 99) {
				priorityRender = true;
			} else if (opcode == 100) {
				lightModifier = buffer.readByte();
			} else if (opcode == 101) {
				shadowModifier = buffer.readByte() * 5;
			} else if (opcode == 102) {
				headIcon = buffer.readUShort();
			} else if (opcode == 103) {
				rotation = buffer.readUShort();
			} else if (opcode == 106) {
				morphVarbitIndex = buffer.readUShort();
				if (morphVarbitIndex == 65535) {
					morphVarbitIndex = -1;
				}
				morphVariableIndex = buffer.readUShort();
				if (morphVariableIndex == 65535) {
					morphVariableIndex = -1;
				}

				int count = buffer.readUByte();
				morphisms = new int[count + 1];

				for (int i = 0; i <= count; i++) {
					morphisms[i] = buffer.readUShort();
					if (morphisms[i] == 65535) {
						morphisms[i] = -1;
					}
				}

			} else if (opcode == 107) {
				clickable = false;
			} else {
				System.out.println("Unrecognised opcode=" + opcode);
			}
		} while (true);
	}

}