package com.jagex;

/**
 * An object that plays a graphic when it's spawned, such as the doors on waterbirth island.
 */
final class AnimableObject extends Renderable {

	public int cycle;
	public int renderHeight;
	public boolean transformationCompleted = false;
	public int x;
	public int y;
	public int z;
	private int duration;
	private int elapsedFrames;
	private Graphic graphic;

	public AnimableObject(int x, int y, int z, int renderHeight, int graphic, int delay, int currentTime) {
		this.graphic = Graphic.graphics[graphic];
		this.z = z;
		this.x = x;
		this.y = y;
		this.renderHeight = renderHeight;
		cycle = currentTime + delay;
	}

	@Override
	public final Model model() {
		Model graphicModel = graphic.getModel();
		if (graphicModel == null) {
			return null;
		}

		int frame = graphic.animation.primaryFrames[elapsedFrames];
		Model model = new Model(true, Frame.isInvalid(frame), false, graphicModel);

		if (!transformationCompleted) {
			model.skin();
			model.apply(frame);
			model.faceGroups = null;
			model.vertexGroups = null;
		}

		if (graphic.breadthScale != 128 || graphic.depthScale != 128) {
			model.scale(graphic.breadthScale, graphic.breadthScale, graphic.depthScale);
		}

		if (graphic.rotation != 0) {
			if (graphic.rotation == 90) {
				model.rotateClockwise();
			} else if (graphic.rotation == 180) {
				model.rotateClockwise();
				model.rotateClockwise();
			} else if (graphic.rotation == 270) {
				model.rotateClockwise();
				model.rotateClockwise();
				model.rotateClockwise();
			}
		}

		model.light(64 + graphic.modelBrightness, 850 + graphic.modelShadow, -30, -50, -30, true);
		return model;
	}

	public final void nextAnimationStep(int elapsedTime) {
		for (duration += elapsedTime; duration > graphic.animation.duration(elapsedFrames);) {
			duration -= graphic.animation.duration(elapsedFrames) + 1;
			elapsedFrames++;
			if (elapsedFrames >= graphic.animation.frameCount
					&& (elapsedFrames < 0 || elapsedFrames >= graphic.animation.frameCount)) {
				elapsedFrames = 0;
				transformationCompleted = true;
			}
		}
	}

}