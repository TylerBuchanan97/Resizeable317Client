package com.jagex;

public class GameObject extends Renderable {

	public static Client client;
	int morphisms[];
	int morphVarbitsIndex;
	int morphVariableIndex;
	private Animation animation;
	private int anInt1603;
	private int anInt1605;
	private int anInt1606;
	private int centre;
	private int currentFrameId;
	private int id;
	private int lastFrameTick;
	private int orientation;
	private int position;

	public GameObject(int id, int orientation, int position, int bY, int cY, int aY, int dY, int animationId, boolean randomFrame) {
		this.id = id;
		this.position = position;
		this.orientation = orientation;
		anInt1603 = aY;
		centre = bY;
		anInt1605 = cY;
		anInt1606 = dY;

		if (animationId != -1) {
			animation = Animation.animations[animationId];
			currentFrameId = 0;
			lastFrameTick = Client.tick;
			if (randomFrame && animation.loopOffset != -1) {
				currentFrameId = (int) (Math.random() * animation.frameCount);
				lastFrameTick -= (int) (Math.random() * animation.duration(currentFrameId));
			}
		}

		ObjectDefinition definition = ObjectDefinition.lookup(id);
		morphVarbitsIndex = definition.morphVarbitIndex;
		morphVariableIndex = definition.morphVariableIndex;
		morphisms = definition.morphisms;
	}

	@Override
	public final Model model() {
		int lastFrame = -1;
		if (animation != null) {
			int tickDelta = Client.tick - lastFrameTick;
			if (tickDelta > 100 && animation.loopOffset > 0) {
				tickDelta = 100;
			}

			while (tickDelta > animation.duration(currentFrameId)) {
				tickDelta -= animation.duration(currentFrameId);
				currentFrameId++;
				if (currentFrameId < animation.frameCount) {
					continue;
				}
				currentFrameId -= animation.loopOffset;
				if (currentFrameId >= 0 && currentFrameId < animation.frameCount) {
					continue;
				}
				animation = null;
				break;
			}

			lastFrameTick = Client.tick - tickDelta;
			if (animation != null) {
				lastFrame = animation.primaryFrames[currentFrameId];
			}
		}

		ObjectDefinition definition = morphisms != null ? morph() : ObjectDefinition.lookup(id);

		return (definition == null) ? null : definition.modelAt(position, orientation, anInt1603, centre, anInt1605, anInt1606,
				lastFrame);
	}

	public final ObjectDefinition morph() {
		int state = -1;
		if (morphVarbitsIndex != -1) {
			VariableBits bits = VariableBits.bits[morphVarbitsIndex];
			int var = bits.setting;
			int lo = bits.low;
			int hi = bits.high;
			int mask = Client.BIT_MASKS[hi - lo];
			state = client.settings[var] >> lo & mask;
		} else if (morphVariableIndex != -1) {
			state = client.settings[morphVariableIndex];
		}

		if (state < 0 || state >= morphisms.length || morphisms[state] == -1) {
			return null;
		}
		return ObjectDefinition.lookup(morphisms[state]);
	}

}