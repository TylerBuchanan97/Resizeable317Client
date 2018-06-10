package com.jagex;

final class Npc extends Mob {

	NpcDefinition definition;

	@Override
	public final boolean isVisible() {
		return definition != null;
	}

	@Override
	public final Model model() {
		if (definition == null) {
			return null;
		}

		Model mdoel = method450();
		if (mdoel == null) {
			return null;
		}

		super.height = ((Renderable) mdoel).modelHeight;
		if (super.graphicId != -1 && super.currentAnimation != -1) {
			Graphic graphic = Graphic.graphics[super.graphicId];
			Model graphicModel = graphic.getModel();

			if (graphicModel != null) {
				int frame = graphic.animation.primaryFrames[super.currentAnimation];
				Model nextModel = new Model(true, Frame.isInvalid(frame), false, graphicModel);
				nextModel.translate(0, -super.graphicHeight, 0);
				nextModel.skin();
				nextModel.apply(frame);
				nextModel.faceGroups = null;
				nextModel.vertexGroups = null;
				if (graphic.breadthScale != 128 || graphic.depthScale != 128) {
					nextModel.scale(graphic.breadthScale, graphic.breadthScale, graphic.depthScale);
				}

				nextModel.light(64 + graphic.modelBrightness, 850 + graphic.modelShadow, -30, -50, -30, true);
				mdoel = new Model(new Model[] { mdoel, nextModel }, 2);
			}
		}

		if (definition.size == 1) {
			mdoel.aBoolean1659 = true;
		}
		return mdoel;
	}

	private final Model method450() {
		if (super.emoteAnimation >= 0 && super.animationDelay == 0) {
			int emote = Animation.animations[super.emoteAnimation].primaryFrames[super.displayedEmoteFrames];
			int movement = -1;

			if (super.movementAnimation >= 0 && super.movementAnimation != super.idleAnimation) {
				movement = Animation.animations[super.movementAnimation].primaryFrames[super.displayedMovementFrames];
			}
			return definition.getAnimatedModel(emote, movement, Animation.animations[super.emoteAnimation].interleaveOrder);
		}

		int movement = -1;
		if (super.movementAnimation >= 0) {
			movement = Animation.animations[super.movementAnimation].primaryFrames[super.displayedMovementFrames];
		}

		return definition.getAnimatedModel(movement, -1, null);
	}

}