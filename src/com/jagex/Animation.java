package com.jagex;

public class Animation {

	public static Animation[] animations;
	public static int count;

	public static void init(Archive archive) {
		Buffer buffer = new Buffer(archive.extract("seq.dat"));
		count = buffer.readUShort();

		if (animations == null) {
			animations = new Animation[count];
		}

		for (int i = 0; i < count; i++) {
			if (animations[i] == null) {
				animations[i] = new Animation();
			}
			animations[i].decode(buffer);
		}
	}

	/**
	 * The animation precedence (will this animation 'override' other animations or will this one yield).
	 */
	public int animatingPrecedence = -1;

	public int frameCount;

	public int[] interleaveOrder;

	/**
	 * The amount of frames subtracted to restart the loop.
	 */
	public int loopOffset = -1;

	/**
	 * The maximum times this animation will loop.
	 */
	public int maximumLoops = 99;

	/**
	 * Indicates whether or not this player's shield will be displayed whilst this animation is played.
	 */
	public int playerShieldDelta = -1;

	/**
	 * Indicates whether or not this player's weapon will be displayed whilst this animation is played.
	 */
	public int playerWeaponDelta = -1;

	public int[] primaryFrames;

	public int priority = 5;

	public int replayMode = 2;

	public int[] secondaryFrames;

	public boolean stretches = false;

	/**
	 * The walking precedence (will the player be prevented from moving or can they continue).
	 */
	public int walkingPrecedence = -1;

	private int[] durations;

	/**
	 * Reads values from the buffer.
	 * 
	 * @param buffer The buffer.
	 */
	public void decode(Buffer buffer) {
		do {
			int opcode = buffer.readUByte();
			if (opcode == 0) {
				break;
			}
			if (opcode == 1) {
				frameCount = buffer.readUByte();
				primaryFrames = new int[frameCount];
				secondaryFrames = new int[frameCount];
				durations = new int[frameCount];
				for (int i = 0; i < frameCount; i++) {
					primaryFrames[i] = buffer.readUShort();
					secondaryFrames[i] = buffer.readUShort();
					if (secondaryFrames[i] == 65535) {
						secondaryFrames[i] = -1;
					}
					durations[i] = buffer.readUShort();
				}

			} else if (opcode == 2) {
				loopOffset = buffer.readUShort();
			} else if (opcode == 3) {
				int count = buffer.readUByte();
				interleaveOrder = new int[count + 1];
				for (int i = 0; i < count; i++) {
					interleaveOrder[i] = buffer.readUByte();
				}

				interleaveOrder[count] = 0x98967f;
			} else if (opcode == 4) {
				stretches = true;
			} else if (opcode == 5) {
				priority = buffer.readUByte();
			} else if (opcode == 6) {
				playerShieldDelta = buffer.readUShort();
			} else if (opcode == 7) {
				playerWeaponDelta = buffer.readUShort();
			} else if (opcode == 8) {
				maximumLoops = buffer.readUByte();
			} else if (opcode == 9) {
				animatingPrecedence = buffer.readUByte();
			} else if (opcode == 10) {
				walkingPrecedence = buffer.readUByte();
			} else if (opcode == 11) {
				replayMode = buffer.readUByte();
			} else if (opcode == 12) {
				buffer.readInt(); // unused
			} else {
				System.out.println("Error unrecognised seq config code: " + opcode);
			}
		} while (true);
		if (frameCount == 0) {
			frameCount = 1;
			primaryFrames = new int[1];
			primaryFrames[0] = -1;
			secondaryFrames = new int[1];
			secondaryFrames[0] = -1;
			durations = new int[1];
			durations[0] = -1;
		}
		if (animatingPrecedence == -1) {
			if (interleaveOrder != null) {
				animatingPrecedence = 2;
			} else {
				animatingPrecedence = 0;
			}
		}
		if (walkingPrecedence == -1) {
			if (interleaveOrder != null) {
				walkingPrecedence = 2;
				return;
			}
			walkingPrecedence = 0;
		}
	}

	public int duration(int frameId) {
		int duration = durations[frameId];
		if (duration == 0) {
			Frame frame = Frame.lookup(primaryFrames[frameId]);
			if (frame != null) {
				duration = durations[frameId] = frame.duration;
			}
		}

		return duration == 0 ? 1 : duration;
	}

}