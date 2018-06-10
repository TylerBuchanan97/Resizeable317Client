package com.jagex;

public class Widget {

	public static Widget[] widgets;
	static Cache models = new Cache(30);
	private static Cache spriteCache;

	public static void clearModels(int id, int type, Model model) {
		models.unlink();

		if (model != null && type != 4) {
			models.put(model, (type << 16) + id);
		}
	}

	public static void load(Archive interfaces, Archive graphics, Font[] fonts) {
		spriteCache = new Cache(50000);
		Buffer buffer = new Buffer(interfaces.extract("data"));
		int parent = -1;
		int count = buffer.readUShort();
		widgets = new Widget[count];

		while (buffer.position < buffer.payload.length) {
			int id = buffer.readUShort();
			if (id == 65535) {
				parent = buffer.readUShort();
				id = buffer.readUShort();
			}

			Widget widget = widgets[id] = new Widget();
			widget.id = id;
			widget.parent = parent;
			widget.type = buffer.readUByte();
			widget.anInt217 = buffer.readUByte();
			widget.anInt214 = buffer.readUShort();
			widget.width = buffer.readUShort();
			widget.height = buffer.readUShort();
			widget.alpha = (byte) buffer.readUByte();
			widget.anInt230 = buffer.readUByte();

			if (widget.anInt230 != 0) {
				widget.anInt230 = (widget.anInt230 - 1 << 8) + buffer.readUByte();
			} else {
				widget.anInt230 = -1;
			}

			int i1 = buffer.readUByte();
			if (i1 > 0) {
				widget.scriptOperators = new int[i1];
				widget.anIntArray212 = new int[i1];
				for (int j1 = 0; j1 < i1; j1++) {
					widget.scriptOperators[j1] = buffer.readUByte();
					widget.anIntArray212[j1] = buffer.readUShort();
				}

			}

			int scriptCount = buffer.readUByte();
			if (scriptCount > 0) {
				widget.scripts = new int[scriptCount][];
				for (int script = 0; script < scriptCount; script++) {
					int instructionCount = buffer.readUShort();
					widget.scripts[script] = new int[instructionCount];
					for (int instruction = 0; instruction < instructionCount; instruction++) {
						widget.scripts[script][instruction] = buffer.readUShort();
					}
				}
			}

			if (widget.type == 0) {
				widget.scrollLimit = buffer.readUShort();
				widget.aBoolean266 = buffer.readUByte() == 1;
				int children = buffer.readUShort();
				widget.children = new int[children];
				widget.childX = new int[children];
				widget.childY = new int[children];

				for (int index = 0; index < children; index++) {
					widget.children[index] = buffer.readUShort();
					widget.childX[index] = buffer.readShort();
					widget.childY[index] = buffer.readShort();
				}
			}

			if (widget.type == 1) {
				buffer.readUShort();
				buffer.readUByte(); // == 1
			}

			if (widget.type == 2) {
				widget.inventoryIds = new int[widget.width * widget.height];
				widget.inventoryAmounts = new int[widget.width * widget.height];
				widget.aBoolean259 = buffer.readUByte() == 1;
				widget.aBoolean249 = buffer.readUByte() == 1;
				widget.aBoolean242 = buffer.readUByte() == 1;
				widget.aBoolean235 = buffer.readUByte() == 1;
				widget.spritePaddingX = buffer.readUByte();
				widget.spritePaddingY = buffer.readUByte();
				widget.spriteX = new int[20];
				widget.spriteY = new int[20];
				widget.sprites = new DirectSprite[20];

				for (int index = 0; index < 20; index++) {
					int exists = buffer.readUByte();
					if (exists == 1) {
						widget.spriteX[index] = buffer.readShort();
						widget.spriteY[index] = buffer.readShort();
						String name = buffer.readString();
						if (graphics != null && name.length() > 0) {
							int position = name.lastIndexOf(",");
							widget.sprites[index] = getSprite(Integer.parseInt(name.substring(position + 1)), graphics,
									name.substring(0, position));
						}
					}
				}

				widget.actions = new String[5];
				for (int index = 0; index < 5; index++) {
					widget.actions[index] = buffer.readString();

					if (widget.actions[index].length() == 0) {
						widget.actions[index] = null;
					}
				}
			}

			if (widget.type == 3) {
				widget.filled = buffer.readUByte() == 1;
			}

			if (widget.type == 4 || widget.type == 1) {
				widget.centeredText = buffer.readUByte() == 1;
				int font = buffer.readUByte();
				if (fonts != null) {
					widget.font = fonts[font];
				}

				widget.shadowedText = buffer.readUByte() == 1;
			}

			if (widget.type == 4) {
				widget.hiddenText = buffer.readString();
				widget.text = buffer.readString();
			}

			if (widget.type == 1 || widget.type == 3 || widget.type == 4) {
				widget.colour = buffer.readInt();
			}

			if (widget.type == 3 || widget.type == 4) {
				widget.anInt219 = buffer.readInt();
				widget.anInt216 = buffer.readInt();
				widget.anInt239 = buffer.readInt();
			} else if (widget.type == 5) {
				String name = buffer.readString();
				if (graphics != null && name.length() > 0) {
					int index = name.lastIndexOf(",");
					widget.aClass30_Sub2_Sub1_Sub1_207 = getSprite(Integer.parseInt(name.substring(index + 1)), graphics,
							name.substring(0, index));
				}

				name = buffer.readString();
				if (graphics != null && name.length() > 0) {
					int index = name.lastIndexOf(",");
					widget.aClass30_Sub2_Sub1_Sub1_260 = getSprite(Integer.parseInt(name.substring(index + 1)), graphics,
							name.substring(0, index));
				}
			} else if (widget.type == 6) {
				int content = buffer.readUByte();
				if (content != 0) {
					widget.mediaType = 1;
					widget.media = (content - 1 << 8) + buffer.readUByte();
				}

				content = buffer.readUByte();
				if (content != 0) {
					widget.anInt255 = 1;
					widget.anInt256 = (content - 1 << 8) + buffer.readUByte();
				}

				content = buffer.readUByte();
				if (content != 0) {
					widget.mediaAnimationId = (content - 1 << 8) + buffer.readUByte();
				} else {
					widget.mediaAnimationId = -1;
				}

				content = buffer.readUByte();
				if (content != 0) {
					widget.anInt258 = (content - 1 << 8) + buffer.readUByte();
				} else {
					widget.anInt258 = -1;
				}

				widget.spriteScale = buffer.readUShort();
				widget.spritePitch = buffer.readUShort();
				widget.spriteRoll = buffer.readUShort();
			} else if (widget.type == 7) {
				widget.inventoryIds = new int[widget.width * widget.height];
				widget.inventoryAmounts = new int[widget.width * widget.height];
				widget.centeredText = buffer.readUByte() == 1;
				int font = buffer.readUByte();
				if (fonts != null) {
					widget.font = fonts[font];
				}

				widget.shadowedText = buffer.readUByte() == 1;
				widget.colour = buffer.readInt();
				widget.spritePaddingX = buffer.readShort();
				widget.spritePaddingY = buffer.readShort();
				widget.aBoolean249 = buffer.readUByte() == 1;
				widget.actions = new String[5];

				for (int index = 0; index < 5; index++) {
					widget.actions[index] = buffer.readString();
					if (widget.actions[index].length() == 0) {
						widget.actions[index] = null;
					}
				}
			}

			if (widget.anInt217 == 2 || widget.type == 2) {
				widget.aString222 = buffer.readString();
				widget.aString218 = buffer.readString();
				widget.anInt237 = buffer.readUShort();
			}

			if (widget.anInt217 == 1 || widget.anInt217 == 4 || widget.anInt217 == 5 || widget.anInt217 == 6) {
				widget.hover = buffer.readString();

				if (widget.hover.length() == 0) {
					if (widget.anInt217 == 1) {
						widget.hover = "Ok";
					} else if (widget.anInt217 == 4) {
						widget.hover = "Select";
					} else if (widget.anInt217 == 5) {
						widget.hover = "Select";
					} else if (widget.anInt217 == 6) {
						widget.hover = "Continue";
					}
				}
			}
		}
	}

	private static DirectSprite getSprite(int position, Archive archive, String name) {
		long key = (StringUtils.hashSpriteName(name) << 8) + position;
		DirectSprite sprite = (DirectSprite) spriteCache.get(key);
		if (sprite != null) {
			return sprite;
		}

		try {
			sprite = new DirectSprite(archive, name, position);
			spriteCache.put(sprite, key);
		} catch (Exception ex) {
			return null;
		}
		return sprite;
	}

	public boolean aBoolean235;

	public boolean aBoolean242;
	public boolean aBoolean249;
	public boolean aBoolean259;
	public boolean aBoolean266;
	public DirectSprite aClass30_Sub2_Sub1_Sub1_207;
	public DirectSprite aClass30_Sub2_Sub1_Sub1_260;
	public String[] actions;
	public byte alpha;
	public int anInt214;
	public int anInt216;
	public int anInt217;
	public int anInt219;
	public int anInt230;
	public int anInt237;
	public int anInt239;
	public int anInt255;
	public int anInt256;
	public int anInt258;
	public int[] anIntArray212;
	public int[] scriptOperators;
	public String aString218;
	public String aString222;
	public boolean centeredText;
	public int[] children;
	public int[] childX;
	public int[] childY;
	public int colour;
	public int displayedFrameCount;
	/**
	 * Indicates whether or not the widget should be drawn filled, or just as an outline.
	 */
	public boolean filled;
	public Font font;
	public int height;
	public String hiddenText;
	public int horizontalDrawOffset;
	public String hover;
	public int id;
	public int[] inventoryAmounts;
	public int[] inventoryIds;
	public int lastFrameTime;
	public int media;
	public int mediaAnimationId;
	public int mediaType;
	public int parent;
	public int[][] scripts;
	public int scrollLimit;
	public int scrollPosition;
	public boolean shadowedText;
	public int spritePaddingX;
	public int spritePaddingY;
	public int spritePitch;
	public int spriteRoll;
	public DirectSprite[] sprites; // FIXME nope
	public int spriteScale;
	public int[] spriteX;
	public int[] spriteY;
	public String text;
	public int type;
	public int verticalDrawOffset;
	public int width;

	public Model method209(int primaryFrame, int secondaryFrame, boolean flag) {
		Model model = flag ? getModel(anInt255, anInt256) : getModel(mediaType, media);

		if (model == null) {
			return null;
		}

		if (primaryFrame == -1 && secondaryFrame == -1 && model.triangleColours == null) {
			return model;
		}

		Model animated = new Model(true, Frame.isInvalid(primaryFrame) & Frame.isInvalid(secondaryFrame), false, model);
		if (primaryFrame != -1 || secondaryFrame != -1) {
			animated.skin();
		}

		if (primaryFrame != -1) {
			animated.apply(primaryFrame);
		}

		if (secondaryFrame != -1) {
			animated.apply(secondaryFrame);
		}

		animated.light(64, 768, -50, -10, -50, true);
		return animated;
	}

	public void swapInventoryItems(int first, int second) {
		int tmp = inventoryIds[first];
		inventoryIds[first] = inventoryIds[second];
		inventoryIds[second] = tmp;

		tmp = inventoryAmounts[first];
		inventoryAmounts[first] = inventoryAmounts[second];
		inventoryAmounts[second] = tmp;
	}

	private Model getModel(int type, int id) {
		Model model = (Model) models.get((type << 16) + id);
		if (model != null) {
			return model;
		}

		if (type == 1) {
			model = Model.lookup(id);
		} else if (type == 2) {
			model = NpcDefinition.lookup(id).model();
		} else if (type == 3) {
			model = Client.localPlayer.getBodyModel();
		} else if (type == 4) {
			model = ItemDefinition.lookup(id).asStack(50);
		}

		if (model != null) {
			models.put(model, (type << 16) + id);
		}

		return model;
	}

}