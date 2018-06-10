package com.jagex;

class Mob extends Renderable {

	// Class30_Sub2_Sub4_Sub1

	int animationDelay;
	boolean animationStretches = false;
	int anInt1503;
	int anInt1519;
	int anInt1522;
	int anInt1528;
	int anInt1542;
	int currentAnimation;
	int currentAnimationLoops;
	int currentHealth;
	int cycleStatus = -1000;
	int destinationX;
	int destinationY;
	int direction;
	int displayedEmoteFrames;
	int displayedMovementFrames;
	int emoteAnimation = -1;
	int endForceMovement;
	int faceX;
	int faceY;
	int graphicDelay;
	int graphicHeight;
	int graphicId = -1;
	int halfTurnAnimation = -1;
	int height = 200;
	int[] hitCycles = new int[4];
	int[] hitDamages = new int[4];
	int[] hitTypes = new int[4];
	int idleAnimation = -1;
	int initialX;
	int initialY;
	int interactingMob = -1;
	int maximumHealth;
	int movementAnimation = -1;
	int nextStepOrientation;
	int orientation;
	boolean[] pathRun = new boolean[10];
	int[] pathX = new int[10];
	int[] pathY = new int[10];
	int quarterAnticlockwiseTurnAnimation = -1;
	int quarterClockwiseTurnAnimation = -1;
	int remainingPath;
	int rotation = 32;
	int runAnimation = -1;
	int size = 1;
	String spokenText;
	int startForceMovement;
	int textColour;
	int textCycle = 100;
	int textEffect;
	int time;
	int turnAnimation = -1;
	int walkingAnimation = -1;
	int worldX;
	int worldY;

	public final void damage(int damage, int type, int cycle) {
		for (int index = 0; index < 4; index++) {
			if (hitCycles[index] <= cycle) {
				hitDamages[index] = damage;
				hitTypes[index] = type;
				hitCycles[index] = cycle + 70;
				break;
			}
		}
	}

	public boolean isVisible() {
		return false;
	}

	public final void move(int x, int y, boolean teleported) {
		if (emoteAnimation != -1 && Animation.animations[emoteAnimation].walkingPrecedence == 1) {
			emoteAnimation = -1;
		}

		if (!teleported) {
			int dirX = x - pathX[0];
			int dirY = y - pathY[0];
			if (dirX >= -8 && dirX <= 8 && dirY >= -8 && dirY <= 8) {
				if (remainingPath < 9) {
					remainingPath++;
				}
				for (int i = remainingPath; i > 0; i--) {
					pathX[i] = pathX[i - 1];
					pathY[i] = pathY[i - 1];
					pathRun[i] = pathRun[i - 1];
				}

				pathX[0] = x;
				pathY[0] = y;
				pathRun[0] = false;
				return;
			}
		}

		remainingPath = 0;
		anInt1542 = 0;
		anInt1503 = 0;
		pathX[0] = x;
		pathY[0] = y;
		worldX = pathX[0] << 7 + size << 6;
		worldY = pathY[0] << 7 + size << 6;
	}

	public final void resetPath() {
		remainingPath = 0;
		anInt1542 = 0;
	}

	public final void setPosition(int direction, boolean run) {
		int x = pathX[0];
		int y = pathY[0];
		if (direction == 0) {
			x--;
			y++;
		}
		if (direction == 1) {
			y++;
		}
		if (direction == 2) {
			x++;
			y++;
		}
		if (direction == 3) {
			x--;
		}
		if (direction == 4) {
			x++;
		}
		if (direction == 5) {
			x--;
			y--;
		}
		if (direction == 6) {
			y--;
		}
		if (direction == 7) {
			x++;
			y--;
		}
		if (emoteAnimation != -1 && Animation.animations[emoteAnimation].walkingPrecedence == 1) {
			emoteAnimation = -1;
		}
		if (remainingPath < 9) {
			remainingPath++;
		}
		for (int i = remainingPath; i > 0; i--) {
			pathX[i] = pathX[i - 1];
			pathY[i] = pathY[i - 1];
			pathRun[i] = pathRun[i - 1];
		}

		pathX[0] = x;
		pathY[0] = y;
		pathRun[0] = run;
	}

}