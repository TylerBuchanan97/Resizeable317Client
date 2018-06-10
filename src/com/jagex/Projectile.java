package com.jagex;

final class Projectile extends Renderable {

	public int destinationElevation;
	public int elevationPitch;
	public int endTick;
	public int leapScale;
	public boolean mobile;
	public int pitch;
	public int plane;
	public int sourceElevation;
	public int sourceX;
	public int sourceY;
	public int startTick;
	public int target;
	public double x;
	public double y;
	public int yaw;
	public double z;
	private double acceleration;
	private int elapsed;
	private int frameIndex;
	private Graphic graphic;
	private double velocity;
	private double velocityX;
	private double velocityY;
	private double velocityZ;

	public Projectile(int sourceX, int sourceY, int sourceElevation, int destinationElevation, int elevationPitch, int startTick,
			int endTick, int leapScale, int plane, int target, int graphic) {
		this.graphic = Graphic.graphics[graphic];
		this.plane = plane;
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.sourceElevation = sourceElevation;
		this.endTick = endTick;
		this.startTick = startTick;
		this.elevationPitch = elevationPitch;
		this.leapScale = leapScale;
		this.target = target;
		this.destinationElevation = destinationElevation;
		mobile = false;
	}

	@Override
	public final Model model() {
		Model base = graphic.getModel();
		if (base == null) {
			return null;
		}

		int frame = -1;
		if (graphic.animation != null) {
			frame = graphic.animation.primaryFrames[frameIndex];
		}

		Model model = new Model(true, Frame.isInvalid(frame), false, base);
		if (frame != -1) {
			model.skin();
			model.apply(frame);
			model.faceGroups = null;
			model.vertexGroups = null;
		}

		if (graphic.breadthScale != 128 || graphic.depthScale != 128) {
			model.scale(graphic.breadthScale, graphic.breadthScale, graphic.depthScale);
		}

		model.pitch(pitch, 1);
		model.light(64 + graphic.modelBrightness, 850 + graphic.modelShadow, -30, -50, -30, true);
		return model;
	}

	public final void target(int destX, int destY, int destZ, int endTick) {
		if (!mobile) {
			double dX = destX - sourceX;
			double dY = destY - sourceY;
			double distance = Math.sqrt(dX * dX + dY * dY);
			x = sourceX + dX * leapScale / distance;
			y = sourceY + dY * leapScale / distance;
			z = sourceElevation;
		}

		double tick = startTick + 1 - endTick;
		velocityX = (destX - x) / tick;
		velocityY = (destY - y) / tick;
		velocity = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
		if (!mobile) {
			velocityZ = -velocity * Math.tan(elevationPitch * 0.02454369D);
		}

		acceleration = 2D * (destZ - z - velocityZ * tick) / (tick * tick);
	}

	public final void update(int time) {
		mobile = true;
		x += velocityX * time;
		y += velocityY * time;
		z += velocityZ * time + 0.5D * acceleration * time * time;
		velocityZ += acceleration * time;
		yaw = (int) (Math.atan2(velocityX, velocityY) * 325.94900000000001D) + 1024 & 0x7ff;
		pitch = (int) (Math.atan2(velocityZ, velocity) * 325.94900000000001D) & 0x7ff;

		if (graphic.animation != null) {
			for (elapsed += time; elapsed > graphic.animation.duration(frameIndex);) {
				elapsed -= graphic.animation.duration(frameIndex) + 1;
				frameIndex++;
				if (frameIndex >= graphic.animation.frameCount) {
					frameIndex = 0;
				}
			}
		}
	}

}