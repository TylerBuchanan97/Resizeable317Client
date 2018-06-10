package com.jagex;

final class Player extends Mob {

	static Cache models = new Cache(260);
	boolean aBoolean1699 = false;
	int anInt1709;
	int anInt1711;
	int anInt1712;
	int anInt1713;
	int anInt1719;
	int anInt1720;
	int anInt1721;
	int anInt1722;
	int[] appearanceColours = new int[5];
	long appearanceHash;
	int[] appearanceModels = new int[12];
	int combat;
	int gender;
	int headIcon;
	long modelKey = -1;
	String name;
	NpcDefinition npcDefinition;
	int objectAppearanceEndTick; // the tick when the player stops appearing as an object
	int objectAppearanceStartTick; // the tick when the player appear as an object
	Model objectModel; // if the player is appearing as an object
	int skill;
	int team;
	boolean visible = false;

	public final Model getBodyModel() {
		if (!visible) {
			return null;
		}

		if (npcDefinition != null) {
			return npcDefinition.model();
		}

		boolean unprepared = false;
		for (int i = 0; i < 12; i++) {
			int model = appearanceModels[i];
			if (model >= 256 && model < 512 && !IdentityKit.kits[model - 256].loaded()) {
				unprepared = true;
			}
			if (model >= 512 && !ItemDefinition.lookup(model - 512).headPieceReady(gender)) {
				unprepared = true;
			}
		}

		if (unprepared) {
			return null;
		}

		Model[] bodyModels = new Model[12];
		int count = 0;
		for (int part = 0; part < 12; part++) {
			int modelId = appearanceModels[part];

			if (modelId >= 256 && modelId < 512) {
				Model model = IdentityKit.kits[modelId - 256].headModel();
				if (model != null) {
					bodyModels[count++] = model;
				}
			}

			if (modelId >= 512) {
				Model model = ItemDefinition.lookup(modelId - 512).asHeadPiece(gender);
				if (model != null) {
					bodyModels[count++] = model;
				}
			}
		}

		Model model = new Model(count, bodyModels);
		for (int part = 0; part < 5; part++) {
			if (appearanceColours[part] != 0) {
				model.recolour(Client.PLAYER_BODY_RECOLOURS[part][0], Client.PLAYER_BODY_RECOLOURS[part][appearanceColours[part]]);

				if (part == 1) {
					model.recolour(Client.SKIN_COLOURS[0], Client.SKIN_COLOURS[appearanceColours[part]]);
				}
			}
		}

		return model;
	}

	@Override
	public final boolean isVisible() {
		return visible;
	}

	@Override
	public final Model model() {
		if (!visible) {
			return null;
		}

		Model appearanceModel = method452();
		if (appearanceModel == null) {
			return null;
		}

		super.height = ((Renderable) appearanceModel).modelHeight;
		appearanceModel.aBoolean1659 = true;
		if (aBoolean1699) {
			return appearanceModel;
		}

		if (super.graphicId != -1 && super.currentAnimation != -1) {
			Graphic graphic = Graphic.graphics[super.graphicId];
			Model graphicModel = graphic.getModel();
			if (graphicModel != null) {
				Model model = new Model(true, Frame.isInvalid(super.currentAnimation), false, graphicModel);
				model.translate(0, -super.graphicHeight, 0);
				model.skin();
				model.apply(graphic.animation.primaryFrames[super.currentAnimation]);
				model.faceGroups = null;
				model.vertexGroups = null;
				if (graphic.breadthScale != 128 || graphic.depthScale != 128) {
					model.scale(graphic.breadthScale, graphic.breadthScale, graphic.depthScale);
				}
				model.light(64 + graphic.modelBrightness, 850 + graphic.modelShadow, -30, -50, -30, true);
				appearanceModel = new Model(new Model[] { appearanceModel, model }, 2);
			}
		}

		if (objectModel != null) {
			if (Client.tick >= objectAppearanceEndTick) {
				objectModel = null;
			}

			if (Client.tick >= objectAppearanceStartTick && Client.tick < objectAppearanceEndTick) {
				Model model = objectModel;
				model.translate(anInt1711 - super.worldX, anInt1712 - anInt1709, anInt1713 - super.worldY);

				if (super.nextStepOrientation == 512) {
					model.rotateClockwise();
					model.rotateClockwise();
					model.rotateClockwise();
				} else if (super.nextStepOrientation == 1024) {
					model.rotateClockwise();
					model.rotateClockwise();
				} else if (super.nextStepOrientation == 1536) {
					model.rotateClockwise();
				}

				appearanceModel = new Model(new Model[] { appearanceModel, model }, 2);
				if (super.nextStepOrientation == 512) {
					model.rotateClockwise();
				} else if (super.nextStepOrientation == 1024) {
					model.rotateClockwise();
					model.rotateClockwise();
				} else if (super.nextStepOrientation == 1536) {
					model.rotateClockwise();
					model.rotateClockwise();
					model.rotateClockwise();
				}
				model.translate(super.worldX - anInt1711, anInt1709 - anInt1712, super.worldY - anInt1713);
			}
		}

		appearanceModel.aBoolean1659 = true;
		return appearanceModel;
	}

	public final void updateAppearance(Buffer buffer) {
		buffer.position = 0;
		gender = buffer.readUByte();
		headIcon = buffer.readUByte();
		npcDefinition = null;
		team = 0;

		for (int bodyPart = 0; bodyPart < 12; bodyPart++) {
			int reset = buffer.readUByte();

			if (reset == 0) {
				appearanceModels[bodyPart] = 0;
				continue;
			}

			int id = buffer.readUByte();
			appearanceModels[bodyPart] = (reset << 8) + id;
			if (bodyPart == 0 && appearanceModels[0] == 65535) {
				npcDefinition = NpcDefinition.lookup(buffer.readUShort());
				break;
			}

			if (appearanceModels[bodyPart] >= 512 && appearanceModels[bodyPart] - 512 < ItemDefinition.count) {
				int team = ItemDefinition.lookup(appearanceModels[bodyPart] - 512).team;
				if (team != 0) {
					this.team = team;
				}
			}
		}

		for (int part = 0; part < 5; part++) {
			int colour = buffer.readUByte();
			if (colour < 0 || colour >= Client.PLAYER_BODY_RECOLOURS[part].length) {
				colour = 0;
			}

			appearanceColours[part] = colour;
		}

		super.idleAnimation = buffer.readUShort();
		if (super.idleAnimation == 65535) {
			super.idleAnimation = -1;
		}

		super.turnAnimation = buffer.readUShort();
		if (super.turnAnimation == 65535) {
			super.turnAnimation = -1;
		}

		super.walkingAnimation = buffer.readUShort();
		if (super.walkingAnimation == 65535) {
			super.walkingAnimation = -1;
		}

		super.halfTurnAnimation = buffer.readUShort();
		if (super.halfTurnAnimation == 65535) {
			super.halfTurnAnimation = -1;
		}

		super.quarterClockwiseTurnAnimation = buffer.readUShort();
		if (super.quarterClockwiseTurnAnimation == 65535) {
			super.quarterClockwiseTurnAnimation = -1;
		}

		super.quarterAnticlockwiseTurnAnimation = buffer.readUShort();
		if (super.quarterAnticlockwiseTurnAnimation == 65535) {
			super.quarterAnticlockwiseTurnAnimation = -1;
		}

		super.runAnimation = buffer.readUShort();
		if (super.runAnimation == 65535) {
			super.runAnimation = -1;
		}

		name = StringUtils.format(StringUtils.decodeBase37(buffer.readLong()));
		combat = buffer.readUByte();
		skill = buffer.readUShort();
		visible = true;
		appearanceHash = 0;

		for (int model = 0; model < 12; model++) {
			appearanceHash <<= 4;
			if (appearanceModels[model] >= 256) {
				appearanceHash += appearanceModels[model] - 256;
			}
		}

		if (appearanceModels[0] >= 256) {
			appearanceHash += appearanceModels[0] - 256 >> 4;
		}
		if (appearanceModels[1] >= 256) {
			appearanceHash += appearanceModels[1] - 256 >> 8;
		}
		for (int colour = 0; colour < 5; colour++) {
			appearanceHash <<= 3;
			appearanceHash += appearanceColours[colour];
		}

		appearanceHash <<= 1;
		appearanceHash += gender;
	}

	private final Model method452() {
		if (npcDefinition != null) {
			int frame = -1;
			if (super.emoteAnimation >= 0 && super.animationDelay == 0) {
				frame = Animation.animations[super.emoteAnimation].primaryFrames[super.displayedEmoteFrames];
			} else if (super.movementAnimation >= 0) {
				frame = Animation.animations[super.movementAnimation].primaryFrames[super.displayedMovementFrames];
			}
			Model model = npcDefinition.getAnimatedModel(frame, -1, null);
			return model;
		}

		long hash = appearanceHash;
		int primaryFrame = -1;
		int secondaryFrame = -1;
		int shieldModel = -1;
		int weaponModel = -1;
		if (super.emoteAnimation >= 0 && super.animationDelay == 0) {
			Animation emote = Animation.animations[super.emoteAnimation];
			primaryFrame = emote.primaryFrames[super.displayedEmoteFrames];
			if (super.movementAnimation >= 0 && super.movementAnimation != super.idleAnimation) {
				secondaryFrame = Animation.animations[super.movementAnimation].primaryFrames[super.displayedMovementFrames];
			}
			if (emote.playerShieldDelta >= 0) {
				shieldModel = emote.playerShieldDelta;
				hash += shieldModel - appearanceModels[5] << 40;
			}
			if (emote.playerWeaponDelta >= 0) {
				weaponModel = emote.playerWeaponDelta;
				hash += weaponModel - appearanceModels[3] << 48;
			}
		} else if (super.movementAnimation >= 0) {
			primaryFrame = Animation.animations[super.movementAnimation].primaryFrames[super.displayedMovementFrames];
		}

		Model model = (Model) models.get(hash);
		if (model == null) {
			boolean invalid = false;
			for (int bodyPart = 0; bodyPart < 12; bodyPart++) {
				int appearanceModel = appearanceModels[bodyPart];
				if (weaponModel >= 0 && bodyPart == 3) {
					appearanceModel = weaponModel;
				}
				if (shieldModel >= 0 && bodyPart == 5) {
					appearanceModel = shieldModel;
				}
				if (appearanceModel >= 256 && appearanceModel < 512 && !IdentityKit.kits[appearanceModel - 256].bodyLoaded()) {
					invalid = true;
				}
				if (appearanceModel >= 512 && !ItemDefinition.lookup(appearanceModel - 512).equipmentReady(gender)) {
					invalid = true;
				}
			}

			if (invalid) {
				if (modelKey != -1L) {
					model = (Model) models.get(modelKey);
				}
				if (model == null) {
					return null;
				}
			}
		}

		if (model == null) {
			Model[] models = new Model[12];
			int count = 0;
			for (int index = 0; index < 12; index++) {
				int part = appearanceModels[index];
				if (weaponModel >= 0 && index == 3) {
					part = weaponModel;
				}
				if (shieldModel >= 0 && index == 5) {
					part = shieldModel;
				}
				if (part >= 256 && part < 512) {
					Model bodyModel = IdentityKit.kits[part - 256].bodyModel();
					if (bodyModel != null) {
						models[count++] = bodyModel;
					}
				}
				if (part >= 512) {
					Model equipment = ItemDefinition.lookup(part - 512).asEquipment(gender);
					if (equipment != null) {
						models[count++] = equipment;
					}
				}
			}

			model = new Model(count, models);
			for (int part = 0; part < 5; part++) {
				if (appearanceColours[part] != 0) {
					model.recolour(Client.PLAYER_BODY_RECOLOURS[part][0],
							Client.PLAYER_BODY_RECOLOURS[part][appearanceColours[part]]);
					if (part == 1) {
						model.recolour(Client.SKIN_COLOURS[0], Client.SKIN_COLOURS[appearanceColours[part]]);
					}
				}
			}

			model.skin();
			model.light(64, 850, -30, -50, -30, true);
			Player.models.put(model, hash);
			modelKey = hash;
		}

		if (aBoolean1699) {
			return model;
		}

		Model empty = Model.EMPTY_MODEL;
		empty.method464(model, Frame.isInvalid(primaryFrame) & Frame.isInvalid(secondaryFrame));
		if (primaryFrame != -1 && secondaryFrame != -1) {
			empty.apply(primaryFrame, secondaryFrame, Animation.animations[super.emoteAnimation].interleaveOrder);
		} else if (primaryFrame != -1) {
			empty.apply(primaryFrame);
		}

		empty.method466();
		empty.faceGroups = null;
		empty.vertexGroups = null;
		return empty;
	}

}