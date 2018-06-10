package com.jagex;

final class Region {

	static int anInt131;
	static int anInt145 = 99;
	static boolean lowMemory = true;
	private static final int[] anIntArray140 = { 16, 32, 64, 128 };
	private static final int[] anIntArray144 = { 0, -1, 0, 1 };
	private static final int[] anIntArray152 = { 1, 2, 4, 8 }; // orientation -> ??
	private static final int[] COSINE_VERTICES = { 1, 0, -1, 0 };
	private static int hueOffset = (int) (Math.random() * 17) - 8;
	private static int luminanceOffset = (int) (Math.random() * 33) - 16;

	public static final void load(Buffer buffer, ResourceProvider provider) {
		label0: {
			int id = -1;
			do {
				int offset = buffer.readSmart();
				if (offset == 0) {
					break label0;
				}
				id += offset;
				ObjectDefinition definition = ObjectDefinition.lookup(id);
				definition.loadModels(provider);
				do {
					int in = buffer.readSmart();
					if (in == 0) {
						break;
					}
					buffer.readUByte();
				} while (true);
			} while (true);
		}
	}

	public static final void method188(Scene scene, int orientation, int y, int type, int z, CollisionMap map, int ai[][][],
			int x, int id, int plane) {
		int aY = ai[z][x][y];
		int bY = ai[z][x + 1][y];
		int cY = ai[z][x + 1][y + 1];
		int dY = ai[z][x][y + 1];
		int meanY = (aY + bY + cY + dY) / 4;
		ObjectDefinition definition = ObjectDefinition.lookup(id);
		int key = x + (y << 7) + (id << 14) + 0x40000000;
		if (!definition.interactive) {
			key += 0x80000000;
		}
		byte config = (byte) ((orientation << 6) + type);
		if (type == 22) {
			Object obj;
			if (definition.animation == -1 && definition.morphisms == null) {
				obj = definition.modelAt(22, orientation, aY, bY, cY, dY, -1);
			} else {
				obj = new GameObject(id, orientation, 22, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addFloorDecor(x, y, plane, (Renderable) obj, key, config, meanY);
			if (definition.solid && definition.interactive) {
				map.setUnwalkable(x, y);
			}
			return;
		}
		if (type == 10 || type == 11) {
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(10, orientation, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, orientation, 10, bY, cY, aY, dY, definition.animation, true);
			}
			if (object != null) {
				int yaw = 0;
				if (type == 11) {
					yaw += 256;
				}
				int width;
				int length;
				if (orientation == 1 || orientation == 3) {
					width = definition.length;
					length = definition.width;
				} else {
					width = definition.width;
					length = definition.length;
				}
				scene.addObject(x, y, plane, width, length, (Renderable) object, key, config, yaw, meanY);
			}
			if (definition.solid) {
				map.flagObject(x, y, definition.width, definition.length, definition.impenetrable, orientation);
			}
			return;
		}
		if (type >= 12) {
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(type, orientation, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, orientation, type, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addObject(x, y, plane, 1, 1, (Renderable) object, key, config, 0, meanY);
			if (definition.solid) {
				map.flagObject(x, y, definition.width, definition.length, definition.impenetrable, orientation);
			}
			return;
		}
		if (type == 0) {
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(0, orientation, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, orientation, 0, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addWall(anIntArray152[orientation], (Renderable) object, key, y, config, x, null, meanY, 0, plane);
			if (definition.solid) {
				map.flagObject(x, y, orientation, type, definition.impenetrable);
			}
			return;
		}
		if (type == 1) {
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(1, orientation, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, orientation, 1, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addWall(anIntArray140[orientation], (Renderable) object, key, y, config, x, null, meanY, 0, plane);
			if (definition.solid) {
				map.flagObject(x, y, orientation, type, definition.impenetrable);
			}
			return;
		}
		if (type == 2) {
			int oppositeOrientation = orientation + 1 & 3;
			Object obj11;
			Object obj12;
			if (definition.animation == -1 && definition.morphisms == null) {
				obj11 = definition.modelAt(2, 4 + orientation, aY, bY, cY, dY, -1);
				obj12 = definition.modelAt(2, oppositeOrientation, aY, bY, cY, dY, -1);
			} else {
				obj11 = new GameObject(id, 4 + orientation, 2, bY, cY, aY, dY, definition.animation, true);
				obj12 = new GameObject(id, oppositeOrientation, 2, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addWall(anIntArray152[orientation], (Renderable) obj11, key, y, config, x, (Renderable) obj12, meanY,
					anIntArray152[oppositeOrientation], plane);
			if (definition.solid) {
				map.flagObject(x, y, orientation, type, definition.impenetrable);
			}
			return;
		}
		if (type == 3) {
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(3, orientation, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, orientation, 3, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addWall(anIntArray140[orientation], (Renderable) object, key, y, config, x, null, meanY, 0, plane);
			if (definition.solid) {
				map.flagObject(x, y, orientation, type, definition.impenetrable);
			}
			return;
		}
		if (type == 9) {
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(type, orientation, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, orientation, type, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addObject(x, y, plane, 1, 1, (Renderable) object, key, config, 0, meanY);
			if (definition.solid) {
				map.flagObject(x, y, definition.width, definition.length, definition.impenetrable, orientation);
			}
			return;
		}
		if (definition.contouredGround) {
			if (orientation == 1) {
				int k3 = dY;
				dY = cY;
				cY = bY;
				bY = aY;
				aY = k3;
			} else if (orientation == 2) {
				int l3 = dY;
				dY = bY;
				bY = l3;
				l3 = cY;
				cY = aY;
				aY = l3;
			} else if (orientation == 3) {
				int i4 = dY;
				dY = aY;
				aY = bY;
				bY = cY;
				cY = i4;
			}
		}
		if (type == 4) {
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(4, 0, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, 0, 4, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addWallDecoration(key, y, orientation * 512, plane, 0, meanY, (Renderable) object, x, config, 0,
					anIntArray152[orientation]);
			return;
		}
		if (type == 5) {
			int displacement = 16;
			int existing = scene.getWallKey(x, y, plane);
			if (existing > 0) {
				displacement = ObjectDefinition.lookup(existing >> 14 & 0x7fff).decorDisplacement;
			}
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(4, 0, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, 0, 4, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addWallDecoration(key, y, orientation * 512, plane, COSINE_VERTICES[orientation] * displacement, meanY,
					(Renderable) object, x, config, anIntArray144[orientation] * displacement, anIntArray152[orientation]);
			return;
		}
		if (type == 6) {
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(4, 0, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, 0, 4, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addWallDecoration(key, y, orientation, plane, 0, meanY, (Renderable) object, x, config, 0, 256);
			return;
		}
		if (type == 7) {
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(4, 0, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, 0, 4, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addWallDecoration(key, y, orientation, plane, 0, meanY, (Renderable) object, x, config, 0, 512);
			return;
		}
		if (type == 8) {
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(4, 0, aY, bY, cY, dY, -1);
			} else {
				object = new GameObject(id, 0, 4, bY, cY, aY, dY, definition.animation, true);
			}
			scene.addWallDecoration(key, y, orientation, plane, 0, meanY, (Renderable) object, x, config, 0, 768);
		}
	}

	public static final boolean modelReady(int objectId, int type) {
		ObjectDefinition definition = ObjectDefinition.lookup(objectId);
		if (type == 11) {
			type = 10;
		}
		if (type >= 5 && type <= 8) {
			type = 4;
		}
		return definition.ready(type);
	}

	public static final boolean objectsReady(byte[] data, int x, int y) {
		boolean ready = true;
		Buffer buffer = new Buffer(data);
		int id = -1;

		while (true) {
			int offset = buffer.readSmart();
			if (offset == 0) {
				return ready;
			}

			id += offset;
			int position = 0;
			boolean skip = false;

			while (true) {
				int terminate;
				if (skip) {
					terminate = buffer.readSmart();
					if (terminate == 0) {
						break;
					}

					buffer.readUByte();
				} else {
					terminate = buffer.readSmart();
					if (terminate == 0) {
						break;
					}

					position += terminate - 1;
					int localY = position & 63;
					int localX = position >> 6 & 63;
					int type = buffer.readUByte() >> 2;
					int viewportX = localX + x;
					int viewportY = localY + y;
					if (viewportX > 0 && viewportY > 0 && viewportX < 103 && viewportY < 103) {
						ObjectDefinition definition = ObjectDefinition.lookup(id);
						if (type != 22 || !lowMemory || definition.interactive || definition.obstructiveGround) {
							ready &= definition.ready();
							skip = true;
						}
					}
				}
			}
		}
	}

	private static final int calculateHeight(int x, int y) {
		int height = method176(x + 45365, y + 0x16713, 4) - 128 + (method176(x + 10294, y + 37821, 2) - 128 >> 1)
				+ (method176(x, y, 1) - 128 >> 2);
		height = (int) (height * 0.3D) + 35;
		if (height < 10) {
			height = 10;
		} else if (height > 60) {
			height = 60;
		}
		return height;
	}

	private static final int method170(int i, int j) {
		int k = i + j * 57;
		k = k << 13 ^ k;
		int l = k * (k * k * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
		return l >> 19 & 0xff;
	}

	private static final int method176(int i, int j, int k) {
		int l = i / k;
		int i1 = i & k - 1;
		int j1 = j / k;
		int k1 = j & k - 1;
		int l1 = method186(l, j1);
		int i2 = method186(l + 1, j1);
		int j2 = method186(l, j1 + 1);
		int k2 = method186(l + 1, j1 + 1);
		int l2 = method184(l1, i2, i1, k);
		int i3 = method184(j2, k2, i1, k);
		return method184(l2, i3, k1, k);
	}

	private static final int method184(int i, int j, int k, int l) {
		int i1 = 0x10000 - Rasterizer.COSINE[k * 1024 / l] >> 1;
		return (i * (0x10000 - i1) >> 16) + (j * i1 >> 16);
	}

	private static final int method186(int i, int j) {
		int k = method170(i - 1, j - 1) + method170(i + 1, j - 1) + method170(i - 1, j + 1) + method170(i + 1, j + 1);
		int l = method170(i - 1, j) + method170(i + 1, j) + method170(i, j - 1) + method170(i, j + 1);
		int i1 = method170(i, j);
		return k / 16 + l / 8 + i1 / 4;
	}

	private static final int method187(int i, int j) {
		if (i == -1) {
			return 0xbc614e;
		}
		j = j * (i & 0x7f) / 128;
		if (j < 2) {
			j = 2;
		} else if (j > 126) {
			j = 126;
		}
		return (i & 0xff80) + j;
	}

	private byte[][][] aByteArrayArrayArray134;
	private int[] anIntArray128;
	private int[][] anIntArrayArray139;
	private int[][][] anIntArrayArrayArray135;
	private int[] chromas;
	private byte[][][] collisionPlaneModifiers; // awful name - 0x2 = bridge, 0x4 = roof, etc
	private int[] hues;
	private int length;
	private int[] luminances;
	private byte[][][] overlayCollisions;
	private byte[][][] overlayOrientations;
	private byte[][][] overlays;
	private int[] saturations;
	private byte[][][] underlays;
	private int[][][] vertexZ;
	private int width;

	public Region(byte[][][] abyte0, int length, int width, int[][][] ai) {
		anInt145 = 99;
		this.width = width;
		this.length = length;
		vertexZ = ai;
		collisionPlaneModifiers = abyte0;
		underlays = new byte[4][width][length];
		overlays = new byte[4][width][length];
		overlayCollisions = new byte[4][width][length];
		overlayOrientations = new byte[4][width][length];
		anIntArrayArrayArray135 = new int[4][width + 1][length + 1];
		aByteArrayArrayArray134 = new byte[4][width + 1][length + 1];
		anIntArrayArray139 = new int[width + 1][length + 1];
		hues = new int[length];
		saturations = new int[length];
		luminances = new int[length];
		chromas = new int[length];
		anIntArray128 = new int[length];
	}

	public final void decodeConstructedLocations(CollisionMap[] maps, Scene scene, int i, int x, int k, int z, byte[] data,
			int i1, int orientation, int y) {
		decoding: {
			Buffer buffer = new Buffer(data);
			int id = -1;
			do {
				int idOffset = buffer.readSmart();
				if (idOffset == 0) {
					break decoding;
				}

				id += idOffset;
				int config = 0;

				do {
					int offset = buffer.readSmart();
					if (offset == 0) {
						break;
					}

					config += offset - 1;
					int l2 = config & 0x3f;
					int i3 = config >> 6 & 0x3f;
					int objectPlane = config >> 12;
					int packed = buffer.readUByte();
					int type = packed >> 2;
					int rotation = packed & 3;

					if (objectPlane == i && i3 >= i1 && i3 < i1 + 8 && l2 >= k && l2 < k + 8) {
						ObjectDefinition definition = ObjectDefinition.lookup(id);
						int localX = x + TileUtils.method157(definition.width, definition.length, orientation, l2 & 7, i3 & 7);
						int localY = y + TileUtils.method158(definition.width, definition.length, orientation, l2 & 7, i3 & 7);

						if (localX > 0 && localY > 0 && localX < 103 && localY < 103) {
							int mapPlane = objectPlane;
							if ((collisionPlaneModifiers[1][localX][localY] & 2) == 2) {
								mapPlane--;
							}

							CollisionMap map = mapPlane >= 0 ? maps[mapPlane] : null;
							method175(map, scene, id, localX, localY, z, type, rotation + orientation & 3);
						}
					}
				} while (true);
			} while (true);
		}
	}

	public final void decodeConstructedMapData(int plane, int rotation, CollisionMap[] maps, int absX, int minX, byte[] data,
			int minY, int tileZ, int absY) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (absX + x > 0 && absX + x < 103 && absY + y > 0 && absY + y < 103) {
					maps[tileZ].adjacencies[absX + x][absY + y] &= 0xfeffffff;
				}
			}
		}

		Buffer buffer = new Buffer(data);
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 64; x++) {
				for (int y = 0; y < 64; y++) {
					if (z == plane && x >= minX && x < minX + 8 && y >= minY && y < minY + 8) {
						decodeMapData(buffer, absX + TileUtils.method155(x & 7, y & 7, rotation),
								absY + TileUtils.method156(x & 7, y & 7, rotation), tileZ, 0, 0, rotation);
					} else {
						decodeMapData(buffer, -1, -1, 0, 0, 0, 0);
					}
				}
			}
		}
	}

	public final void decodeRegionLandscapes(CollisionMap[] maps, Scene scene, byte[] data, int localX, int localY) {
		decoding: {
			Buffer buffer = new Buffer(data);
			int id = -1;

			do {
				int idOffset = buffer.readSmart();
				if (idOffset == 0) {
					break decoding;
				}

				id += idOffset;
				int position = 0;

				do {
					int offset = buffer.readSmart();
					if (offset == 0) {
						break;
					}

					position += offset - 1;
					int yOffset = position & 0x3f;
					int xOffset = position >> 6 & 0x3f;
					int z = position >> 12;
					int config = buffer.readUByte();
					int type = config >> 2;
					int orientation = config & 3;
					int x = xOffset + localX;
					int y = yOffset + localY;

					if (x > 0 && y > 0 && x < 103 && y < 103) {
						int plane = z;
						if ((collisionPlaneModifiers[1][x][y] & 2) == 2) {
							plane--;
						}
						CollisionMap map = null;
						if (plane >= 0) {
							map = maps[plane];
						}
						method175(map, scene, id, x, y, z, type, orientation);
					}
				} while (true);
			} while (true);
		}
	}

	public final void decodeRegionMapData(byte[] data, int dY, int dX, int absoluteSectorX, int absoluteSectorY,
			CollisionMap maps[]) {
		for (int z = 0; z < 4; z++) {
			for (int regionX = 0; regionX < 64; regionX++) {
				for (int regionY = 0; regionY < 64; regionY++) {
					if (dX + regionX > 0 && dX + regionX < 103 && dY + regionY > 0 && dY + regionY < 103) {
						maps[z].adjacencies[dX + regionX][dY + regionY] &= 0xfeffffff;
					}
				}
			}
		}

		Buffer buffer = new Buffer(data);
		for (int z = 0; z < 4; z++) {
			for (int regionX = 0; regionX < 64; regionX++) {
				for (int regionY = 0; regionY < 64; regionY++) {
					decodeMapData(buffer, regionX + dX, regionY + dY, z, absoluteSectorX, absoluteSectorY, 0);
				}
			}
		}
	}

	/**
	 * Returns the plane that actually contains the collision flag, to adjust for objects such as bridges. TODO better name
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @return The correct z coordinate.
	 */
	public int getCollisionPlane(int x, int y, int z) {
		if ((collisionPlaneModifiers[z][x][y] & 8) != 0) {
			return 0;
		} else if (z > 0 && (collisionPlaneModifiers[1][x][y] & 2) != 0) {
			return z - 1;
		}

		return z;
	}

	public final void method171(CollisionMap[] maps, Scene scene) {
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 104; x++) {
				for (int y = 0; y < 104; y++) {
					if ((collisionPlaneModifiers[z][x][y] & 1) == 1) {
						int plane = z;
						if ((collisionPlaneModifiers[1][x][y] & 2) == 2) { // bridge
							plane--;
						}

						if (plane >= 0) {
							maps[plane].setUnwalkable(x, y);
						}
					}
				}
			}
		}

		hueOffset += (int) (Math.random() * 5D) - 2;
		if (hueOffset < -8) {
			hueOffset = -8;
		} else if (hueOffset > 8) {
			hueOffset = 8;
		}

		luminanceOffset += (int) (Math.random() * 5D) - 2;
		if (luminanceOffset < -16) {
			luminanceOffset = -16;
		} else if (luminanceOffset > 16) {
			luminanceOffset = 16;
		}

		for (int z = 0; z < 4; z++) {
			byte[][] abyte0 = aByteArrayArrayArray134[z];
			byte byte0 = 96;
			char c = '\u0300';
			byte byte1 = -50;
			byte byte2 = -10;
			byte byte3 = -50;
			int l3 = c * (int) Math.sqrt(byte1 * byte1 + byte2 * byte2 + byte3 * byte3) >> 8;
			for (int y = 1; y < length - 1; y++) {
				for (int x = 1; x < width - 1; x++) {
					int widthHeightChange = vertexZ[z][x + 1][y] - vertexZ[z][x - 1][y];
					int lengthHeightChange = vertexZ[z][x][y + 1] - vertexZ[z][x][y - 1];
					int r = (int) Math.sqrt(widthHeightChange * widthHeightChange + 0x10000 + lengthHeightChange
							* lengthHeightChange);
					int k12 = (widthHeightChange << 8) / r;
					int l13 = 0x10000 / r;
					int j15 = (lengthHeightChange << 8) / r;
					int j16 = byte0 + (byte1 * k12 + byte2 * l13 + byte3 * j15) / l3;
					int j17 = (abyte0[x - 1][y] >> 2) + (abyte0[x + 1][y] >> 3) + (abyte0[x][y - 1] >> 2)
							+ (abyte0[x][y + 1] >> 3) + (abyte0[x][y] >> 1);
					anIntArrayArray139[x][y] = j16 - j17;
				}
			}

			for (int index = 0; index < length; index++) {
				hues[index] = 0;
				saturations[index] = 0;
				luminances[index] = 0;
				chromas[index] = 0;
				anIntArray128[index] = 0;
			}

			for (int centreX = -5; centreX < width + 5; centreX++) {
				for (int y = 0; y < length; y++) {
					int maxX = centreX + 5;
					if (maxX >= 0 && maxX < width) {
						int id = underlays[z][maxX][y] & 0xff;

						if (id > 0) {
							Floor floor = Floor.floors[id - 1];
							hues[y] += floor.weightedHue;
							saturations[y] += floor.saturation;
							luminances[y] += floor.luminance;
							chromas[y] += floor.chroma;
							anIntArray128[y]++;
						}
					}

					int minX = centreX - 5;
					if (minX >= 0 && minX < width) {
						int id = underlays[z][minX][y] & 0xff;

						if (id > 0) {
							Floor floor = Floor.floors[id - 1];
							hues[y] -= floor.weightedHue;
							saturations[y] -= floor.saturation;
							luminances[y] -= floor.luminance;
							chromas[y] -= floor.chroma;
							anIntArray128[y]--;
						}
					}
				}

				if (centreX >= 1 && centreX < width - 1) {
					int l9 = 0;
					int j13 = 0;
					int j14 = 0;
					int k15 = 0;
					int k16 = 0;
					for (int centreY = -5; centreY < length + 5; centreY++) {
						int j18 = centreY + 5;
						if (j18 >= 0 && j18 < length) {
							l9 += hues[j18];
							j13 += saturations[j18];
							j14 += luminances[j18];
							k15 += chromas[j18];
							k16 += anIntArray128[j18];
						}

						int k18 = centreY - 5;
						if (k18 >= 0 && k18 < length) {
							l9 -= hues[k18];
							j13 -= saturations[k18];
							j14 -= luminances[k18];
							k15 -= chromas[k18];
							k16 -= anIntArray128[k18];
						}
						if (centreY >= 1
								&& centreY < length - 1
								&& (!lowMemory || (collisionPlaneModifiers[0][centreX][centreY] & 2) != 0 || (collisionPlaneModifiers[z][centreX][centreY] & 0x10) == 0
										&& getCollisionPlane(centreX, centreY, z) == anInt131)) {
							if (z < anInt145) {
								anInt145 = z;
							}
							int underlay = underlays[z][centreX][centreY] & 0xff;
							int overlay = overlays[z][centreX][centreY] & 0xff;
							if (underlay > 0 || overlay > 0) {
								int centreZ = vertexZ[z][centreX][centreY];
								int eastZ = vertexZ[z][centreX + 1][centreY];
								int northEastZ = vertexZ[z][centreX + 1][centreY + 1];
								int northZ = vertexZ[z][centreX][centreY + 1];
								int j20 = anIntArrayArray139[centreX][centreY];
								int k20 = anIntArrayArray139[centreX + 1][centreY];
								int l20 = anIntArrayArray139[centreX + 1][centreY + 1];
								int i21 = anIntArrayArray139[centreX][centreY + 1];
								int colour = -1;
								int adjustedColour = -1;
								if (underlay > 0) {
									int hue = l9 * 256 / k15;
									int saturation = j13 / k16;
									int luminance = j14 / k16;
									colour = encode(hue, saturation, luminance);
									hue = hue + hueOffset & 0xff;
									luminance += luminanceOffset;
									if (luminance < 0) {
										luminance = 0;
									} else if (luminance > 255) {
										luminance = 255;
									}
									adjustedColour = encode(hue, saturation, luminance);
								}

								if (z > 0) {
									boolean flag = true;
									if (underlay == 0 && overlayCollisions[z][centreX][centreY] != 0) {
										flag = false;
									}
									if (overlay > 0 && !Floor.floors[overlay - 1].shadowing) {
										flag = false;
									}
									if (flag && centreZ == eastZ && centreZ == northEastZ && centreZ == northZ) {
										anIntArrayArrayArray135[z][centreX][centreY] |= 0x924;
									}
								}

								int i22 = 0;
								if (colour != -1) {
									i22 = Rasterizer.anIntArray1482[method187(adjustedColour, 96)];
								}

								if (overlay == 0) {
									scene.method279(z, centreX, centreY, 0, 0, -1, centreZ, eastZ, northEastZ, northZ,
											method187(colour, j20), method187(colour, k20), method187(colour, l20),
											method187(colour, i21), 0, 0, 0, 0, i22, 0);
								} else {
									int collision = overlayCollisions[z][centreX][centreY] + 1;
									byte orientation = overlayOrientations[z][centreX][centreY];
									Floor floor = Floor.floors[overlay - 1];
									int texture = floor.texture;
									int floorColour;
									int k23;
									if (texture >= 0) {
										k23 = Rasterizer.method369(texture);
										floorColour = -1;
									} else if (floor.rgb == 0xff00ff) {
										k23 = 0;
										floorColour = -2;
										texture = -1;
									} else {
										floorColour = encode(floor.hue, floor.saturation, floor.luminance);
										k23 = Rasterizer.anIntArray1482[method185(floor.colour, 96)];
									}
									scene.method279(z, centreX, centreY, collision, orientation, texture, centreZ, eastZ,
											northEastZ, northZ, method187(colour, j20), method187(colour, k20),
											method187(colour, l20), method187(colour, i21), method185(floorColour, j20),
											method185(floorColour, k20), method185(floorColour, l20),
											method185(floorColour, i21), i22, k23);
								}
							}
						}
					}
				}
			}

			for (int y = 1; y < length - 1; y++) {
				for (int x = 1; x < width - 1; x++) {
					scene.method278(x, y, z, getCollisionPlane(x, y, z));
				}
			}
		}

		scene.method305(-10, 64, -50, 768, -50);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < length; y++) {
				if ((collisionPlaneModifiers[1][x][y] & 2) == 2) {
					scene.method276(x, y);
				}
			}
		}

		int flag = 1;
		int j2 = 2;
		int k2 = 4;
		for (int l2 = 0; l2 < 4; l2++) {
			if (l2 > 0) {
				flag <<= 3;
				j2 <<= 3;
				k2 <<= 3;
			}
			for (int z = 0; z <= l2; z++) {
				for (int y = 0; y <= length; y++) {
					for (int x = 0; x <= width; x++) {
						if ((anIntArrayArrayArray135[z][x][y] & flag) != 0) {
							int currentY = y;
							int l5 = y;
							int i7 = z;
							int k8 = z;
							for (; currentY > 0 && (anIntArrayArrayArray135[z][x][currentY - 1] & flag) != 0; currentY--) {
								;
							}
							for (; l5 < length && (anIntArrayArrayArray135[z][x][l5 + 1] & flag) != 0; l5++) {
								;
							}
							label0: for (; i7 > 0; i7--) {
								for (int j10 = currentY; j10 <= l5; j10++) {
									if ((anIntArrayArrayArray135[i7 - 1][x][j10] & flag) == 0) {
										break label0;
									}
								}
							}

							label1: for (; k8 < l2; k8++) {
								for (int k10 = currentY; k10 <= l5; k10++) {
									if ((anIntArrayArrayArray135[k8 + 1][x][k10] & flag) == 0) {
										break label1;
									}
								}
							}

							int l10 = (k8 + 1 - i7) * (l5 - currentY + 1);
							if (l10 >= 8) {
								char c1 = '\360';
								int k14 = vertexZ[k8][x][currentY] - c1;
								int l15 = vertexZ[i7][x][currentY];
								Scene.method277(l2, x * 128, l15, x * 128, l5 * 128 + 128, k14, currentY * 128, 1);
								for (int l16 = i7; l16 <= k8; l16++) {
									for (int l17 = currentY; l17 <= l5; l17++) {
										anIntArrayArrayArray135[l16][x][l17] &= ~flag;
									}
								}
							}
						}

						if ((anIntArrayArrayArray135[z][x][y] & j2) != 0) {
							int l4 = x;
							int i6 = x;
							int j7 = z;
							int l8 = z;
							for (; l4 > 0 && (anIntArrayArrayArray135[z][l4 - 1][y] & j2) != 0; l4--) {

							}
							for (; i6 < width && (anIntArrayArrayArray135[z][i6 + 1][y] & j2) != 0; i6++) {

							}
							label2: for (; j7 > 0; j7--) {
								for (int i11 = l4; i11 <= i6; i11++) {
									if ((anIntArrayArrayArray135[j7 - 1][i11][y] & j2) == 0) {
										break label2;
									}
								}
							}

							label3: for (; l8 < l2; l8++) {
								for (int j11 = l4; j11 <= i6; j11++) {
									if ((anIntArrayArrayArray135[l8 + 1][j11][y] & j2) == 0) {
										break label3;
									}
								}
							}

							int k11 = (l8 + 1 - j7) * (i6 - l4 + 1);
							if (k11 >= 8) {
								char c2 = '\360';
								int l14 = vertexZ[l8][l4][y] - c2;
								int i16 = vertexZ[j7][l4][y];
								Scene.method277(l2, l4 * 128, i16, i6 * 128 + 128, y * 128, l14, y * 128, 2);
								for (int i17 = j7; i17 <= l8; i17++) {
									for (int i18 = l4; i18 <= i6; i18++) {
										anIntArrayArrayArray135[i17][i18][y] &= ~j2;
									}
								}
							}
						}

						if ((anIntArrayArrayArray135[z][x][y] & k2) != 0) {
							int i5 = x;
							int j6 = x;
							int k7 = y;
							int i9 = y;
							for (; k7 > 0 && (anIntArrayArrayArray135[z][x][k7 - 1] & k2) != 0; k7--) {

							}
							for (; i9 < length && (anIntArrayArrayArray135[z][x][i9 + 1] & k2) != 0; i9++) {

							}
							label4: for (; i5 > 0; i5--) {
								for (int l11 = k7; l11 <= i9; l11++) {
									if ((anIntArrayArrayArray135[z][i5 - 1][l11] & k2) == 0) {
										break label4;
									}
								}
							}

							label5: for (; j6 < width; j6++) {
								for (int i12 = k7; i12 <= i9; i12++) {
									if ((anIntArrayArrayArray135[z][j6 + 1][i12] & k2) == 0) {
										break label5;
									}
								}
							}

							if ((j6 - i5 + 1) * (i9 - k7 + 1) >= 4) {
								int j12 = vertexZ[z][i5][k7];
								Scene.method277(l2, i5 * 128, j12, j6 * 128 + 128, i9 * 128 + 128, j12, k7 * 128, 4);
								for (int k13 = i5; k13 <= j6; k13++) {
									for (int i15 = k7; i15 <= i9; i15++) {
										anIntArrayArrayArray135[z][k13][i15] &= ~k2;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public final void method174(int minX, int minY, int dx, int dy) {
		for (int y = minY; y <= minY + dy; y++) {
			for (int x = minX; x <= minX + dx; x++) {
				if (x >= 0 && x < width && y >= 0 && y < length) {
					aByteArrayArrayArray134[0][x][y] = 127;

					if (x == minX && x > 0) {
						vertexZ[0][x][y] = vertexZ[0][x - 1][y];
					}

					if (x == minX + dx && x < width - 1) {
						vertexZ[0][x][y] = vertexZ[0][x + 1][y];
					}

					if (y == minY && y > 0) {
						vertexZ[0][x][y] = vertexZ[0][x][y - 1];
					}

					if (y == minY + dy && y < length - 1) {
						vertexZ[0][x][y] = vertexZ[0][x][y + 1];
					}
				}
			}
		}
	}

	private final void decodeMapData(Buffer buffer, int x, int y, int z, int absoluteSectorX, int absoluteSectorY, int rotation) {
		if (x >= 0 && x < 104 && y >= 0 && y < 104) {
			collisionPlaneModifiers[z][x][y] = 0;
			do {
				int type = buffer.readUByte();

				if (type == 0) {
					if (z == 0) {
						vertexZ[0][x][y] = -calculateHeight(0xe3b7b + x + absoluteSectorX, 0x87cce + y + absoluteSectorY) * 8;
					} else {
						vertexZ[z][x][y] = vertexZ[z - 1][x][y] - 240;
					}

					return;
				} else if (type == 1) {
					int height = buffer.readUByte();
					if (height == 1) {
						height = 0;
					}

					if (z == 0) {
						vertexZ[0][x][y] = -height * 8;
					} else {
						vertexZ[z][x][y] = vertexZ[z - 1][x][y] - height * 8;
					}

					return;
				} else if (type <= 49) {
					overlays[z][x][y] = buffer.readByte();
					overlayCollisions[z][x][y] = (byte) ((type - 2) / 4);
					overlayOrientations[z][x][y] = (byte) (type - 2 + rotation & 3);
				} else if (type <= 81) {
					collisionPlaneModifiers[z][x][y] = (byte) (type - 49);
				} else {
					underlays[z][x][y] = (byte) (type - 81);
				}
			} while (true);
		}

		do {
			int in = buffer.readUByte();
			if (in == 0) {
				break;
			} else if (in == 1) {
				buffer.readUByte();
				return;
			} else if (in <= 49) {
				buffer.readUByte();
			}
		} while (true);
	}

	/**
	 * Encodes the hue, saturation, and luminance into a colour value.
	 * 
	 * @param hue The hue.
	 * @param saturation The saturation.
	 * @param luminance The luminance.
	 * @return The colour.
	 */
	private final int encode(int hue, int saturation, int luminance) {
		if (luminance > 179) {
			saturation /= 2;
		}
		if (luminance > 192) {
			saturation /= 2;
		}
		if (luminance > 217) {
			saturation /= 2;
		}
		if (luminance > 243) {
			saturation /= 2;
		}

		return (hue / 4 << 10) + (saturation / 32 << 7) + luminance / 2;
	}

	private final void method175(CollisionMap map, Scene scene, int id, int x, int y, int z, int type, int orientation) {
		if (lowMemory && (collisionPlaneModifiers[0][x][y] & 2) == 0) {
			if ((collisionPlaneModifiers[z][x][y] & 0x10) != 0) {
				return;
			}
			if (getCollisionPlane(x, y, z) != anInt131) {
				return;
			}
		}
		if (z < anInt145) {
			anInt145 = z;
		}

		int centre = vertexZ[z][x][y];
		int east = vertexZ[z][x + 1][y];
		int northEast = vertexZ[z][x + 1][y + 1];
		int north = vertexZ[z][x][y + 1];
		int mean = centre + east + northEast + north >> 2;
		ObjectDefinition definition = ObjectDefinition.lookup(id);
		int key = x + (y << 7) + (id << 14) + 0x40000000;
		if (!definition.interactive) {
			key += 0x80000000;
		}
		byte config = (byte) ((orientation << 6) + type);

		if (type == 22) {
			if (lowMemory && !definition.interactive && !definition.obstructiveGround) {
				return;
			}
			Object object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(22, orientation, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, orientation, 22, east, northEast, centre, north, definition.animation, true);
			}
			scene.addFloorDecor(x, y, z, (Renderable) object, key, config, mean);
			if (definition.solid && definition.interactive && map != null) {
				map.setUnwalkable(x, y);
			}
			return;
		}

		if (type == 10 || type == 11) {
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(10, orientation, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, orientation, 10, east, northEast, centre, north, definition.animation, true);
			}

			if (object != null) {
				int yaw = 0;
				if (type == 11) {
					yaw += 256;
				}
				int width;
				int length;

				if (orientation == 1 || orientation == 3) {
					width = definition.length;
					length = definition.width;
				} else {
					width = definition.width;
					length = definition.length;
				}

				if (scene.addObject(x, y, z, width, length, object, key, config, yaw, mean) && definition.castsShadow) {
					Model model;
					if (object instanceof Model) {
						model = (Model) object;
					} else {
						model = definition.modelAt(10, orientation, centre, east, northEast, north, -1);
					}
					if (model != null) {
						for (int j5 = 0; j5 <= width; j5++) {
							for (int k5 = 0; k5 <= length; k5++) {
								int l5 = model.anInt1650 / 4;
								if (l5 > 30) {
									l5 = 30;
								}
								if (l5 > aByteArrayArrayArray134[z][x + j5][y + k5]) {
									aByteArrayArrayArray134[z][x + j5][y + k5] = (byte) l5;
								}
							}
						}
					}
				}
			}
			if (definition.solid && map != null) {
				map.flagObject(x, y, definition.width, definition.length, definition.impenetrable, orientation);
			}
			return;
		}
		if (type >= 12) {
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(type, orientation, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, orientation, type, east, northEast, centre, north, definition.animation, true);
			}
			scene.addObject(x, y, z, 1, 1, object, key, config, 0, mean);
			if (type >= 12 && type <= 17 && type != 13 && z > 0) {
				anIntArrayArrayArray135[z][x][y] |= 0x924;
			}
			if (definition.solid && map != null) {
				map.flagObject(x, y, definition.width, definition.length, definition.impenetrable, orientation);
			}
			return;
		}
		if (type == 0) {
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(0, orientation, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, orientation, 0, east, northEast, centre, north, definition.animation, true);
			}
			scene.addWall(anIntArray152[orientation], object, key, y, config, x, null, mean, 0, z);
			if (orientation == 0) {
				if (definition.castsShadow) {
					aByteArrayArrayArray134[z][x][y] = 50;
					aByteArrayArrayArray134[z][x][y + 1] = 50;
				}
				if (definition.occludes) {
					anIntArrayArrayArray135[z][x][y] |= 0x249;
				}
			} else if (orientation == 1) {
				if (definition.castsShadow) {
					aByteArrayArrayArray134[z][x][y + 1] = 50;
					aByteArrayArrayArray134[z][x + 1][y + 1] = 50;
				}
				if (definition.occludes) {
					anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
				}
			} else if (orientation == 2) {
				if (definition.castsShadow) {
					aByteArrayArrayArray134[z][x + 1][y] = 50;
					aByteArrayArrayArray134[z][x + 1][y + 1] = 50;
				}
				if (definition.occludes) {
					anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
				}
			} else if (orientation == 3) {
				if (definition.castsShadow) {
					aByteArrayArrayArray134[z][x][y] = 50;
					aByteArrayArrayArray134[z][x + 1][y] = 50;
				}
				if (definition.occludes) {
					anIntArrayArrayArray135[z][x][y] |= 0x492;
				}
			}
			if (definition.solid && map != null) {
				map.flagObject(x, y, orientation, type, definition.impenetrable);
			}
			if (definition.decorDisplacement != 16) {
				scene.displaceWallDecor(x, y, z, definition.decorDisplacement);
			}
			return;
		}
		if (type == 1) {
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(1, orientation, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, orientation, 1, east, northEast, centre, north, definition.animation, true);
			}
			scene.addWall(anIntArray140[orientation], object, key, y, config, x, null, mean, 0, z);
			if (definition.castsShadow) {
				if (orientation == 0) {
					aByteArrayArrayArray134[z][x][y + 1] = 50;
				} else if (orientation == 1) {
					aByteArrayArrayArray134[z][x + 1][y + 1] = 50;
				} else if (orientation == 2) {
					aByteArrayArrayArray134[z][x + 1][y] = 50;
				} else if (orientation == 3) {
					aByteArrayArrayArray134[z][x][y] = 50;
				}
			}
			if (definition.solid && map != null) {
				map.flagObject(x, y, orientation, type, definition.impenetrable);
			}
			return;
		}
		if (type == 2) {
			int oppositeOrientation = orientation + 1 & 3;
			Renderable obj11;
			Renderable obj12;
			if (definition.animation == -1 && definition.morphisms == null) {
				obj11 = definition.modelAt(2, 4 + orientation, centre, east, northEast, north, -1);
				obj12 = definition.modelAt(2, oppositeOrientation, centre, east, northEast, north, -1);
			} else {
				obj11 = new GameObject(id, 4 + orientation, 2, east, northEast, centre, north, definition.animation, true);
				obj12 = new GameObject(id, oppositeOrientation, 2, east, northEast, centre, north, definition.animation, true);
			}
			scene.addWall(anIntArray152[orientation], obj11, key, y, config, x, obj12, mean, anIntArray152[oppositeOrientation],
					z);
			if (definition.occludes) {
				if (orientation == 0) {
					anIntArrayArrayArray135[z][x][y] |= 0x249;
					anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
				} else if (orientation == 1) {
					anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
					anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
				} else if (orientation == 2) {
					anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
					anIntArrayArrayArray135[z][x][y] |= 0x492;
				} else if (orientation == 3) {
					anIntArrayArrayArray135[z][x][y] |= 0x492;
					anIntArrayArrayArray135[z][x][y] |= 0x249;
				}
			}
			if (definition.solid && map != null) {
				map.flagObject(x, y, orientation, type, definition.impenetrable);
			}
			if (definition.decorDisplacement != 16) {
				scene.displaceWallDecor(x, y, z, definition.decorDisplacement);
			}
			return;
		}
		if (type == 3) {
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(3, orientation, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, orientation, 3, east, northEast, centre, north, definition.animation, true);
			}
			scene.addWall(anIntArray140[orientation], object, key, y, config, x, null, mean, 0, z);
			if (definition.castsShadow) {
				if (orientation == 0) {
					aByteArrayArrayArray134[z][x][y + 1] = 50;
				} else if (orientation == 1) {
					aByteArrayArrayArray134[z][x + 1][y + 1] = 50;
				} else if (orientation == 2) {
					aByteArrayArrayArray134[z][x + 1][y] = 50;
				} else if (orientation == 3) {
					aByteArrayArrayArray134[z][x][y] = 50;
				}
			}
			if (definition.solid && map != null) {
				map.flagObject(x, y, orientation, type, definition.impenetrable);
			}
			return;
		}
		if (type == 9) {
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(type, orientation, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, orientation, type, east, northEast, centre, north, definition.animation, true);
			}
			scene.addObject(x, y, z, 1, 1, object, key, config, 0, mean);
			if (definition.solid && map != null) {
				map.flagObject(x, y, definition.width, definition.length, definition.impenetrable, orientation);
			}
			return;
		}
		if (definition.contouredGround) {
			if (orientation == 1) {
				int tmp = north;
				north = northEast;
				northEast = east;
				east = centre;
				centre = tmp;
			} else if (orientation == 2) {
				int tmp = north;
				north = east;
				east = tmp;
				tmp = northEast;
				northEast = centre;
				centre = tmp;
			} else if (orientation == 3) {
				int tmp = north;
				north = centre;
				centre = east;
				east = northEast;
				northEast = tmp;
			}
		}
		if (type == 4) {
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, 0, 4, east, northEast, centre, north, definition.animation, true);
			}
			scene.addWallDecoration(key, y, orientation * 512, z, 0, mean, object, x, config, 0, anIntArray152[orientation]);
			return;
		}
		if (type == 5) {
			int displacement = 16;
			int existing = scene.getWallKey(x, y, z);
			if (existing > 0) {
				displacement = ObjectDefinition.lookup(existing >> 14 & 0x7fff).decorDisplacement;
			}
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, 0, 4, east, northEast, centre, north, definition.animation, true);
			}
			scene.addWallDecoration(key, y, orientation * 512, z, COSINE_VERTICES[orientation] * displacement, mean, object, x,
					config, anIntArray144[orientation] * displacement, anIntArray152[orientation]);
			return;
		}
		if (type == 6) {
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, 0, 4, east, northEast, centre, north, definition.animation, true);
			}
			scene.addWallDecoration(key, y, orientation, z, 0, mean, object, x, config, 0, 256);
			return;
		}
		if (type == 7) {
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, 0, 4, east, northEast, centre, north, definition.animation, true);
			}
			scene.addWallDecoration(key, y, orientation, z, 0, mean, object, x, config, 0, 512);
			return;
		}
		if (type == 8) {
			Renderable object;
			if (definition.animation == -1 && definition.morphisms == null) {
				object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
			} else {
				object = new GameObject(id, 0, 4, east, northEast, centre, north, definition.animation, true);
			}
			scene.addWallDecoration(key, y, orientation, z, 0, mean, object, x, config, 0, 768);
		}
	}

	private final int method185(int colour, int j) {
		if (colour == -2) {
			return 0xbc614e;
		}

		if (colour == -1) {
			if (j < 0) {
				j = 0;
			} else if (j > 127) {
				j = 127;
			}
			return 127 - j;
		}

		j = j * (colour & 0x7f) / 128;
		if (j < 2) {
			j = 2;
		} else if (j > 126) {
			j = 126;
		}
		return (colour & 0xff80) + j;
	}

}