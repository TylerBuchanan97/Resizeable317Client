package com.jagex;

final class ObjectDefinition {

	// Thanks Super_

	public static Cache baseModels = new Cache(500);
	public static Client client;
	public static boolean lowMemory;
	public static Cache models = new Cache(30);
	private static ObjectDefinition[] cache;
	private static int cacheIndex;
	private static int count;
	private static Buffer data;
	private static int[] indices;
	private static Model[] parts = new Model[4];

	public static final void dispose() {
		baseModels = null;
		models = null;
		indices = null;
		cache = null;
		data = null;
	}

	public static final void init(Archive archive) {
		data = new Buffer(archive.extract("loc.dat"));
		Buffer buffer = new Buffer(archive.extract("loc.idx"));
		count = buffer.readUShort();
		indices = new int[count];
		int index = 2;
		for (int i = 0; i < count; i++) {
			indices[i] = index;
			index += buffer.readUShort();
		}

		cache = new ObjectDefinition[20];
		for (int i = 0; i < 20; i++) {
			cache[i] = new ObjectDefinition();
		}
	}

	public static final ObjectDefinition lookup(int id) {
		for (int i = 0; i < 20; i++) {
			if (cache[i].id == id) {
				return cache[i];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDefinition definition = cache[cacheIndex];
		data.position = indices[id];
		definition.id = id;
		definition.reset();
		definition.decode(data);
		return definition;
	}

	public int animation;
	public boolean castsShadow;
	public boolean contouredGround;
	public int decorDisplacement;
	public byte[] description;
	public boolean hollow;
	public int id = -1;
	public boolean impenetrable;
	public String[] interactions;
	public boolean interactive;
	public int length;
	public int mapscene;
	public int minimapFunction;
	public int[] morphisms;
	public int morphVarbitIndex;
	public int morphVariableIndex;
	public String name;
	public boolean obstructiveGround;
	public boolean occludes;
	public boolean solid;
	public int supportItems;
	public int surroundings;
	public int width;
	private byte ambientLighting;
	private boolean delayShading;
	private boolean inverted;
	private byte lightDiffusion;
	private int[] modelIds;
	private int[] modelPositions;
	private int[] originalColours;
	private int[] replacementColours;
	private int scaleX;
	private int scaleY;
	private int scaleZ;
	private int translateX;
	private int translateY;
	private int translateZ;

	public final void loadModels(ResourceProvider provider) {
		if (modelIds != null) {
			for (int id : modelIds) {
				provider.loadExtra(0, id & 0xffff);
			}
		}
	}

	public final Model modelAt(int position, int orientation, int aY, int bY, int cY, int dY, int frameId) {
		Model model = model(position, frameId, orientation);
		if (model == null) {
			return null;
		}
		if (contouredGround || delayShading) {
			model = new Model(contouredGround, delayShading, model);
		}
		if (contouredGround) {
			int y = (aY + bY + cY + dY) / 4;
			for (int vertex = 0; vertex < model.vertices; vertex++) {
				int x = model.vertexX[vertex];
				int z = model.vertexZ[vertex];
				int l2 = aY + (bY - aY) * (x + 64) / 128;
				int i3 = dY + (cY - dY) * (x + 64) / 128;
				int j3 = l2 + (i3 - l2) * (z + 64) / 128;
				model.vertexY[vertex] += j3 - y;
			}

			model.computeSphericalBounds();
		}
		return model;
	}

	public final ObjectDefinition morph() {
		int morphismIndex = -1;
		if (morphVarbitIndex != -1) {
			VariableBits bits = VariableBits.bits[morphVarbitIndex];
			int variable = bits.setting;
			int low = bits.low;
			int high = bits.high;
			int mask = Client.BIT_MASKS[high - low];
			morphismIndex = client.settings[variable] >> low & mask;
		} else if (morphVariableIndex != -1) {
			morphismIndex = client.settings[morphVariableIndex];
		}
		if (morphismIndex < 0 || morphismIndex >= morphisms.length || morphisms[morphismIndex] == -1) {
			return null;
		}
		return lookup(morphisms[morphismIndex]);
	}

	public final boolean ready() {
		if (modelIds == null) {
			return true;
		}
		boolean ready = true;
		for (int id : modelIds) {
			ready &= Model.loaded(id & 0xffff);
		}

		return ready;
	}

	public final boolean ready(int position) {
		if (modelPositions == null) {
			if (modelIds == null) {
				return true;
			}

			if (position != 10) {
				return true;
			}

			boolean ready = true;
			for (int id : modelIds) {
				ready &= Model.loaded(id & 0xffff);
			}

			return ready;
		}

		for (int i = 0; i < modelPositions.length; i++) {
			if (modelPositions[i] == position) {
				return Model.loaded(modelIds[i] & 0xffff);
			}
		}

		return true;
	}

	private final void decode(Buffer buffer) {
		int interactive = -1;

		outer: do {
			int opcode;
			opcode = buffer.readUByte();
			if (opcode == 0) {
				break outer;
			}

			if (opcode == 1) {
				int count = buffer.readUByte();
				if (count > 0) {
					if (modelIds == null || lowMemory) {
						modelPositions = new int[count];
						modelIds = new int[count];

						for (int i = 0; i < count; i++) {
							modelIds[i] = buffer.readUShort();
							modelPositions[i] = buffer.readUByte();
						}
					} else {
						buffer.position += count * 3;
					}
				}
			} else if (opcode == 2) {
				name = buffer.readString();
			} else if (opcode == 3) {
				description = buffer.readStringBytes();
			} else if (opcode == 5) {
				int count = buffer.readUByte();
				if (count > 0) {
					if (modelIds == null || lowMemory) {
						modelPositions = null;
						modelIds = new int[count];

						for (int i = 0; i < count; i++) {
							modelIds[i] = buffer.readUShort();
						}
					} else {
						buffer.position += count * 2;
					}
				}
			} else if (opcode == 14) {
				width = buffer.readUByte();
			} else if (opcode == 15) {
				length = buffer.readUByte();
			} else if (opcode == 17) {
				solid = false;
			} else if (opcode == 18) {
				impenetrable = false;
			} else if (opcode == 19) {
				interactive = buffer.readUByte();
				if (interactive == 1) {
					this.interactive = true;
				}
			} else if (opcode == 21) {
				contouredGround = true;
			} else if (opcode == 22) {
				delayShading = true;
			} else if (opcode == 23) {
				occludes = true;
			} else if (opcode == 24) {
				animation = buffer.readUShort();
				if (animation == 65535) {
					animation = -1;
				}
			} else if (opcode == 28) {
				decorDisplacement = buffer.readUByte();
			} else if (opcode == 29) {
				ambientLighting = buffer.readByte();
			} else if (opcode == 39) {
				lightDiffusion = buffer.readByte();
			} else if (opcode >= 30 && opcode < 39) {
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
				minimapFunction = buffer.readUShort();
			} else if (opcode == 62) {
				inverted = true;
			} else if (opcode == 64) {
				castsShadow = false;
			} else if (opcode == 65) {
				scaleX = buffer.readUShort();
			} else if (opcode == 66) {
				scaleY = buffer.readUShort();
			} else if (opcode == 67) {
				scaleZ = buffer.readUShort();
			} else if (opcode == 68) {
				mapscene = buffer.readUShort();
			} else if (opcode == 69) {
				surroundings = buffer.readUByte();
			} else if (opcode == 70) {
				translateX = buffer.readShort();
			} else if (opcode == 71) {
				translateY = buffer.readShort();
			} else if (opcode == 72) {
				translateZ = buffer.readShort();
			} else if (opcode == 73) {
				obstructiveGround = true;
			} else if (opcode == 74) {
				hollow = true;
			} else if (opcode == 75) {
				supportItems = buffer.readUByte();
			} else if (opcode == 77) {
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
			} else {
				continue;
			}
		} while (true);

		if (interactive == -1) {
			this.interactive = false;
			if (modelIds != null && (modelPositions == null || modelPositions[0] == 10)) {
				this.interactive = true;
			}
			if (interactions != null) {
				this.interactive = true;
			}
		}
		if (hollow) {
			solid = false;
			impenetrable = false;
		}
		if (supportItems == -1) {
			supportItems = solid ? 1 : 0;
		}
	}

	private final Model model(int position, int frame, int orientation) {
		Model base = null;
		long key;
		if (modelPositions == null) {
			if (position != 10) {
				return null;
			}
			key = (id << 6) + orientation + ((long) (frame + 1) << 32);
			Model rsl = (Model) models.get(key);
			if (rsl != null) {
				return rsl;
			}
			if (modelIds == null) {
				return null;
			}
			boolean invert = inverted ^ orientation > 3;
			int count = modelIds.length;
			for (int i = 0; i < count; i++) {
				int id = modelIds[i];
				if (invert) {
					id += 0x10000;
				}
				base = (Model) baseModels.get(id);
				if (base == null) {
					base = Model.lookup(id & 0xffff);
					if (base == null) {
						return null;
					}
					if (invert) {
						base.invert();
					}
					baseModels.put(base, id);
				}
				if (count > 1) {
					parts[i] = base;
				}
			}

			if (count > 1) {
				base = new Model(count, parts);
			}
		} else {
			int index = -1;
			for (int i = 0; i < modelPositions.length; i++) {
				if (modelPositions[i] != position) {
					continue;
				}
				index = i;
				break;
			}

			if (index == -1) {
				return null;
			}
			key = (id << 6) + (index << 3) + orientation + ((long) (frame + 1) << 32);
			Model model = (Model) models.get(key);
			if (model != null) {
				return model;
			}
			int id = modelIds[index];
			boolean flag3 = inverted ^ orientation > 3;
			if (flag3) {
				id += 0x10000;
			}
			base = (Model) baseModels.get(id);
			if (base == null) {
				base = Model.lookup(id & 0xffff);
				if (base == null) {
					return null;
				}
				if (flag3) {
					base.invert();
				}
				baseModels.put(base, id);
			}
		}
		boolean scale;
		if (scaleX != 128 || scaleY != 128 || scaleZ != 128) {
			scale = true;
		} else {
			scale = false;
		}
		boolean translate;
		if (translateX != 0 || translateY != 0 || translateZ != 0) {
			translate = true;
		} else {
			translate = false;
		}
		Model model = new Model(originalColours == null, Frame.isInvalid(frame), orientation == 0 && frame == -1 && !scale
				&& !translate, base);
		if (frame != -1) {
			model.skin();
			model.apply(frame);
			model.faceGroups = null;
			model.vertexGroups = null;
		}
		while (orientation-- > 0) {
			model.rotateClockwise();
		}
		if (originalColours != null) {
			for (int k2 = 0; k2 < originalColours.length; k2++) {
				model.recolour(originalColours[k2], replacementColours[k2]);
			}

		}
		if (scale) {
			model.scale(scaleX, scaleZ, scaleY);
		}
		if (translate) {
			model.translate(translateX, translateY, translateZ);
		}
		model.light(64 + ambientLighting, 768 + lightDiffusion * 5, -50, -10, -50, !delayShading);
		if (supportItems == 1) {
			model.anInt1654 = model.modelHeight;
		}
		models.put(model, key);
		return model;
	}

	private final void reset() {
		modelIds = null;
		modelPositions = null;
		name = null;
		description = null;
		originalColours = null;
		replacementColours = null;
		width = 1;
		length = 1;
		solid = true;
		impenetrable = true;
		interactive = false;
		contouredGround = false;
		delayShading = false;
		occludes = false;
		animation = -1;
		decorDisplacement = 16;
		ambientLighting = 0;
		lightDiffusion = 0;
		interactions = null;
		minimapFunction = -1;
		mapscene = -1;
		inverted = false;
		castsShadow = true;
		scaleX = 128;
		scaleY = 128;
		scaleZ = 128;
		surroundings = 0;
		translateX = 0;
		translateY = 0;
		translateZ = 0;
		obstructiveGround = false;
		hollow = false;
		supportItems = -1;
		morphVarbitIndex = -1;
		morphVariableIndex = -1;
		morphisms = null;
	}

}