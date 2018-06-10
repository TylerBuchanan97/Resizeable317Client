package com.jagex;

public class Scene {

	public static int anInt470 = -1;
	public static int anInt471 = -1;
	public static int anInt475;
	public static boolean lowMemory = true;
	static boolean aBoolean467;
	static boolean aBooleanArrayArray492[][];
	static boolean[][][][] aBooleanArrayArrayArrayArray491 = new boolean[8][32][51][51];
	static Deque aClass19_477 = new Deque();
	static InteractableObject[] aClass28Array462 = new InteractableObject[100];
	static SceneCluster[] aClass47Array476 = new SceneCluster[500];
	static int anInt446;
	static int anInt447;
	static int anInt448;
	static int anInt449;
	static int anInt450;
	static int anInt451;
	static int anInt452;
	static int anInt453;
	static int anInt454;
	static int anInt455;
	static int anInt456;
	static int anInt457;
	static int anInt458;
	static int anInt459;
	static int anInt460;
	static int anInt461;
	static int anInt468;
	static int anInt469;
	static int anInt472 = 4;
	static int anInt493;
	static int anInt494;
	static int anInt495;
	static int anInt496;
	static int anInt497;
	static int anInt498;
	static final int[] anIntArray463 = { 53, -53, -53, 53 };
	static final int[] anIntArray464 = { -53, -53, 53, 53 };
	static final int[] anIntArray465 = { -45, 45, 45, -45 };
	static final int[] anIntArray466 = { 45, 45, -45, -45 };
	static int[] anIntArray473 = new int[anInt472];
	static final int[] anIntArray478 = { 19, 55, 38, 155, 255, 110, 137, 205, 76 };
	static final int[] anIntArray479 = { 160, 192, 80, 96, 0, 144, 80, 48, 160 };
	static final int[] anIntArray480 = { 76, 8, 137, 4, 0, 1, 38, 2, 19 };
	static final int[] anIntArray481 = { 0, 0, 2, 0, 0, 2, 1, 1, 0 };
	static final int[] anIntArray482 = { 2, 0, 0, 2, 0, 0, 0, 4, 4 };
	static final int[] anIntArray483 = { 0, 4, 4, 8, 0, 0, 8, 0, 0 };
	static final int[] anIntArray484 = { 1, 1, 0, 0, 0, 8, 0, 0, 8 };
	static final int[] anIntArray485 = { 41, 39248, 41, 4643, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 43086, 41, 41, 41, 41,
			41, 41, 41, 8602, 41, 28992, 41, 41, 41, 41, 41, 5056, 41, 41, 41, 7079, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41,
			3131, 41, 41, 41 };
	static SceneCluster[][] clusters = new SceneCluster[anInt472][500];

	public static void dispose() {
		aClass28Array462 = null;
		anIntArray473 = null;
		clusters = null;
		aClass19_477 = null;
		aBooleanArrayArrayArrayArray491 = null;
		aBooleanArrayArray492 = null;
	}

	public static void method277(int i, int j, int k, int l, int i1, int j1, int l1, int i2) {
		SceneCluster cluster = new SceneCluster();
		cluster.anInt787 = j / 128;
		cluster.anInt788 = l / 128;
		cluster.anInt789 = l1 / 128;
		cluster.anInt790 = i1 / 128;
		cluster.anInt791 = i2;
		cluster.anInt792 = j;
		cluster.anInt793 = l;
		cluster.anInt794 = l1;
		cluster.anInt795 = i1;
		cluster.anInt796 = j1;
		cluster.anInt797 = k;
		clusters[i][anIntArray473[i]++] = cluster;
	}

	public static void method310(int i, int j, int k, int l, int ai[]) {
		anInt495 = 0;
		anInt496 = 0;
		anInt497 = k;
		anInt498 = l;
		anInt493 = k / 2;
		anInt494 = l / 2;
		boolean aflag[][][][] = new boolean[9][32][53][53];
		for (int i1 = 128; i1 <= 384; i1 += 32) {
			for (int j1 = 0; j1 < 2048; j1 += 64) {
				anInt458 = Model.SINE[i1];
				anInt459 = Model.COSINE[i1];
				anInt460 = Model.SINE[j1];
				anInt461 = Model.COSINE[j1];
				int l1 = (i1 - 128) / 32;
				int j2 = j1 / 64;
				for (int l2 = -26; l2 <= 26; l2++) {
					for (int j3 = -26; j3 <= 26; j3++) {
						int k3 = l2 * 128;
						int i4 = j3 * 128;
						boolean flag2 = false;
						for (int k4 = -i; k4 <= j; k4 += 128) {
							if (!method311(ai[l1] + k4, i4, k3)) {
								continue;
							}
							flag2 = true;
							break;
						}
						aflag[l1][j2][l2 + 25 + 1][j3 + 25 + 1] = flag2;
					}
				}
			}
		}

		for (int k1 = 0; k1 < 8; k1++) {
			for (int i2 = 0; i2 < 32; i2++) {
				for (int k2 = -25; k2 < 25; k2++) {
					for (int i3 = -25; i3 < 25; i3++) {
						boolean flag1 = false;
						label0: for (int l3 = -1; l3 <= 1; l3++) {
							for (int j4 = -1; j4 <= 1; j4++) {
								if (aflag[k1][i2][k2 + l3 + 25 + 1][i3 + j4 + 25 + 1]) {
									flag1 = true;
								} else if (aflag[k1][(i2 + 1) % 31][k2 + l3 + 25 + 1][i3 + j4 + 25 + 1]) {
									flag1 = true;
								} else if (aflag[k1 + 1][i2][k2 + l3 + 25 + 1][i3 + j4 + 25 + 1]) {
									flag1 = true;
								} else {
									if (!aflag[k1 + 1][(i2 + 1) % 31][k2 + l3 + 25 + 1][i3 + j4 + 25 + 1]) {
										continue;
									}
									flag1 = true;
								}
								break label0;
							}
						}
						aBooleanArrayArrayArrayArray491[k1][i2][k2 + 25][i3 + 25] = flag1;
					}
				}
			}
		}
	}

	public static boolean method311(int i, int j, int k) {
		int l = j * anInt460 + k * anInt461 >> 16;
		int i1 = j * anInt461 - k * anInt460 >> 16;
		int j1 = i * anInt458 + i1 * anInt459 >> 16;
		int k1 = i * anInt459 - i1 * anInt458 >> 16;
		if (j1 < 50 || j1 > 3500) {
			return false;
		}
		int l1 = anInt493 + (l << 9) / j1;
		int i2 = anInt494 + (k1 << 9) / j1;
		return l1 >= anInt495 && l1 <= anInt497 && i2 >= anInt496 && i2 <= anInt498;
	}

	InteractableObject aClass28Array444[];
	int anInt442;
	int anInt443;
	int anInt488;
	int anIntArray486[];
	int anIntArray487[];
	int anIntArrayArray489[][] = { new int[16], { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1 }, { 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 },
			{ 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0 }, { 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1 } };
	int anIntArrayArray490[][] = { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
			{ 12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3 }, { 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 },
			{ 3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12 } };
	int anIntArrayArrayArray440[][][];
	int anIntArrayArrayArray445[][][];
	int height;
	int planeCount;
	SceneTile[][][] tiles;
	int width;

	public Scene(int x, int y, int z, int[][][] ai) {
		aClass28Array444 = new InteractableObject[5000];
		anIntArray486 = new int[10000];
		anIntArray487 = new int[10000];
		planeCount = z;
		width = x;
		height = y;
		tiles = new SceneTile[z][x][y];
		anIntArrayArrayArray445 = new int[z][x + 1][y + 1];
		anIntArrayArrayArray440 = ai;
		reset();
	}

	public boolean addEntity(int x, int y, int plane, Renderable renderable, int yaw, int key, int renderHeight, int delta,
			boolean accountForYaw) {
		if (renderable == null) {
			return true;
		}
		int minX = x - delta;
		int minY = y - delta;
		int maxX = x + delta;
		int maxY = y + delta;
		if (accountForYaw) {
			if (yaw > 640 && yaw < 1408) {
				maxY += 128;
			}
			if (yaw > 1152 && yaw < 1920) {
				maxX += 128;
			}
			if (yaw > 1664 || yaw < 384) {
				minY -= 128;
			}
			if (yaw > 128 && yaw < 896) {
				minX -= 128;
			}
		}
		minX /= 128;
		minY /= 128;
		maxX /= 128;
		maxY /= 128;
		return addObject(plane, minX, minY, maxX - minX + 1, maxY - minY + 1, x, y, renderHeight, renderable, yaw, true, key,
				(byte) 0);
	}

	public void addFloorDecor(int x, int y, int z, Renderable renderable, int key, byte config, int meanY) {
		if (renderable == null) {
			return;
		}
		FloorDecoration decor = new FloorDecoration();
		decor.renderable = renderable;
		decor.x = x * 128 + 64;
		decor.y = y * 128 + 64;
		decor.height = meanY;
		decor.key = key;
		decor.config = config;
		if (tiles[z][x][y] == null) {
			tiles[z][x][y] = new SceneTile(x, y, z);
		}
		tiles[z][x][y].floorDecoration = decor;
	}

	public void addGroundItem(int x, int y, int z, int key, Renderable primary, Renderable secondary, Renderable tertiary,
			int height) {
		GroundItemTile itemTile = new GroundItemTile();
		itemTile.primary = primary;
		itemTile.x = x * 128 + 64;
		itemTile.y = y * 128 + 64;
		itemTile.height = height;
		itemTile.key = key;
		itemTile.tertiary = secondary;
		itemTile.secondary = tertiary;
		int j1 = 0;
		SceneTile tile = tiles[z][x][y];
		if (tile != null) {
			for (int i = 0; i < tile.objectCount; i++) {
				if (tile.interactableObjects[i].renderable instanceof Model) {
					int l1 = ((Model) tile.interactableObjects[i].renderable).anInt1654;
					if (l1 > j1) {
						j1 = l1;
					}
				}
			}

		}
		itemTile.itemHeight = j1;
		if (tiles[z][x][y] == null) {
			tiles[z][x][y] = new SceneTile(x, y, z);
		}
		tiles[z][x][y].groundItem = itemTile;
	}

	public boolean addObject(int x, int y, int plane, int width, int length, Renderable renderable, int key, byte config,
			int yaw, int j) {
		if (renderable == null) {
			return true;
		}
		int absoluteX = x * 128 + 64 * width;
		int absoluteY = y * 128 + 64 * length;
		return addObject(plane, x, y, width, length, absoluteX, absoluteY, j, renderable, yaw, false, key, config);
	}

	public void addWall(int i, Renderable class30_sub2_sub4, int key, int y, byte config, int x, Renderable class30_sub2_sub4_1,
			int i1, int j1, int plane) {
		if (class30_sub2_sub4 == null && class30_sub2_sub4_1 == null) {
			return;
		}
		Wall wall = new Wall();
		wall.key = key;
		wall.config = config;
		wall.positionX = x * 128 + 64;
		wall.positionY = y * 128 + 64;
		wall.anInt273 = i1;
		wall.aClass30_Sub2_Sub4_278 = class30_sub2_sub4;
		wall.aClass30_Sub2_Sub4_279 = class30_sub2_sub4_1;
		wall.anInt276 = i;
		wall.anInt277 = j1;
		for (int z = plane; z >= 0; z--) {
			if (tiles[z][x][y] == null) {
				tiles[z][x][y] = new SceneTile(x, y, z);
			}
		}

		tiles[plane][x][y].wall = wall;
	}

	public void addWallDecoration(int key, int y, int orientation, int plane, int j1, int k1, Renderable renderable, int x,
			byte config, int i2, int attributes) {
		if (renderable == null) {
			return;
		}
		WallDecoration decor = new WallDecoration();
		decor.key = key;
		decor.config = config;
		decor.x = x * 128 + 64 + j1;
		decor.y = y * 128 + 64 + i2;
		decor.height = k1;
		decor.renderable = renderable;
		decor.attributes = attributes;
		decor.orientation = orientation;
		for (int z = plane; z >= 0; z--) {
			if (tiles[z][x][y] == null) {
				tiles[z][x][y] = new SceneTile(x, y, z);
			}
		}

		tiles[plane][x][y].wallDecoration = decor;
	}

	public void clearFloorDecoration(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return;
		}
		tile.floorDecoration = null;
	}

	public void clearGroundItem(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return;
		}
		tile.groundItem = null;
	}

	public void clearWall(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return;
		}
		tile.wall = null;
	}

	public void clearWallDecoration(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return;
		}
		tile.wallDecoration = null;
	}

	public void displaceWallDecor(int x, int y, int z, int displacement) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return;
		}
		WallDecoration decor = tile.wallDecoration;
		if (decor == null) {
			return;
		}
		int absX = x * 128 + 64;
		int absY = y * 128 + 64;
		decor.x = absX + (decor.x - absX) * displacement / 16;
		decor.y = absY + (decor.y - absY) * displacement / 16;
	}

	public void fill(int z) {
		anInt442 = z;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (tiles[z][x][y] == null) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}
		}
	}

	public int getConfig(int x, int y, int z, int key) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return -1;
		}
		if (tile.wall != null && tile.wall.key == key) {
			return tile.wall.config & 0xff;
		}
		if (tile.wallDecoration != null && tile.wallDecoration.key == key) {
			return tile.wallDecoration.config & 0xff;
		}
		if (tile.floorDecoration != null && tile.floorDecoration.key == key) {
			return tile.floorDecoration.config & 0xff;
		}
		for (int i = 0; i < tile.objectCount; i++) {
			if (tile.interactableObjects[i].key == key) {
				return tile.interactableObjects[i].config & 0xff;
			}
		}

		return -1;
	}

	public int getFloorDecorationKey(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null || tile.floorDecoration == null) {
			return 0;
		}
		return tile.floorDecoration.key;
	}

	public int getInteractableObjectKey(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return 0;
		}

		for (int i = 0; i < tile.objectCount; i++) {
			InteractableObject object = tile.interactableObjects[i];
			if ((object.key >> 29 & 3) == 2 && object.positionX == x && object.positionY == y) {
				return object.key;
			}
		}

		return 0;
	}

	public FloorDecoration getTileFloorDecoration(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null || tile.floorDecoration == null) {
			return null;
		}
		return tile.floorDecoration;
	}

	public Wall getTileWall(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return null;
		}
		return tile.wall;
	}

	public WallDecoration getTileWallDecoration(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return null;
		}
		return tile.wallDecoration;
	}

	public int getWallDecorationKey(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null || tile.wallDecoration == null) {
			return 0;
		}
		return tile.wallDecoration.key;
	}

	public int getWallKey(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null || tile.wall == null) {
			return 0;
		}
		return tile.wall.key;
	}

	public void method276(int x, int y) {
		SceneTile tile = tiles[0][x][y];
		for (int z = 0; z < 3; z++) {
			SceneTile above = tiles[z][x][y] = tiles[z + 1][x][y];
			if (above != null) {
				above.plane--;
				for (int i = 0; i < above.objectCount; i++) {
					InteractableObject object = above.interactableObjects[i];
					if ((object.key >> 29 & 3) == 2 && object.positionX == x && object.positionY == y) {
						object.plane--;
					}
				}
			}
		}

		if (tiles[0][x][y] == null) {
			tiles[0][x][y] = new SceneTile(x, y, 0);
		}
		tiles[0][x][y].aClass30_Sub3_1329 = tile;
		tiles[3][x][y] = null;
	}

	public void method278(int x, int y, int z, int l) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return;
		}
		tiles[z][x][y].anInt1321 = l;
	}

	public void method279(int plane, int x, int y, int l, int orientation, int texture, int centreZ, int eastZ, int northEastZ,
			int northZ, int k2, int l2, int i3, int j3, int k3, int l3, int i4, int j4, int k4, int l4) {
		if (l == 0) {
			GenericTile tile = new GenericTile(k2, l2, i3, j3, -1, k4, false);
			for (int z = plane; z >= 0; z--) {
				if (tiles[z][x][y] == null) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}

			tiles[plane][x][y].aClass43_1311 = tile;
			return;
		}
		if (l == 1) {
			GenericTile tile = new GenericTile(k3, l3, i4, j4, texture, l4, centreZ == eastZ && centreZ == northEastZ
					&& centreZ == northZ);
			for (int z = plane; z >= 0; z--) {
				if (tiles[z][x][y] == null) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}

			tiles[plane][x][y].aClass43_1311 = tile;
			return;
		}
		ComplexTile tile = new ComplexTile(y, k3, j3, northEastZ, texture, i4, orientation, k2, k4, i3, northZ, eastZ, centreZ,
				l, j4, l3, l2, x, l4);
		for (int z = plane; z >= 0; z--) {
			if (tiles[z][x][y] == null) {
				tiles[z][x][y] = new SceneTile(x, y, z);
			}
		}

		tiles[plane][x][y].aClass40_1312 = tile;
	}

	public boolean method286(int plane, int k, Renderable renderable, int l, int i1, int j1, int k1, int l1, int i2, int j2,
			int k2) {
		if (renderable == null) {
			return true;
		}
		return addObject(plane, l1, k2, i2 - l1 + 1, i1 - k2 + 1, j1, k, k1, renderable, l, true, j2, (byte) 0);
	}

	public void method288() {
		for (int i = 0; i < anInt443; i++) {
			InteractableObject object = aClass28Array444[i];
			method289(object);
			aClass28Array444[i] = null;
		}

		anInt443 = 0;
	}

	public void method293(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return;
		}
		for (int i = 0; i < tile.objectCount; i++) {
			InteractableObject object = tile.interactableObjects[i];
			if ((object.key >> 29 & 3) == 2 && object.positionX == x && object.positionY == y) {
				method289(object);
				return;
			}
		}
	}

	public InteractableObject method298(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null) {
			return null;
		}
		for (int i = 0; i < tile.objectCount; i++) {
			InteractableObject interactableObject = tile.interactableObjects[i];
			if ((interactableObject.key >> 29 & 3) == 2 && interactableObject.positionX == x && interactableObject.positionY == y) {
				return interactableObject;
			}
		}
		return null;
	}

	public void method305(int i, int j, int k, int l, int i1) {
		int j1 = (int) Math.sqrt(k * k + i * i + i1 * i1);
		int k1 = l * j1 >> 8;
		for (int z = 0; z < planeCount; z++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					SceneTile tile = tiles[z][x][y];
					if (tile != null) {
						Wall wall = tile.wall;
						if (wall != null && wall.aClass30_Sub2_Sub4_278 != null && wall.aClass30_Sub2_Sub4_278.normals != null) {
							method307(z, 1, 1, x, y, (Model) wall.aClass30_Sub2_Sub4_278);
							if (wall.aClass30_Sub2_Sub4_279 != null && wall.aClass30_Sub2_Sub4_279.normals != null) {
								method307(z, 1, 1, x, y, (Model) wall.aClass30_Sub2_Sub4_279);
								method308((Model) wall.aClass30_Sub2_Sub4_278, (Model) wall.aClass30_Sub2_Sub4_279, 0, 0, 0,
										false);
								((Model) wall.aClass30_Sub2_Sub4_279).method480(j, k1, k, i, i1);
							}
							((Model) wall.aClass30_Sub2_Sub4_278).method480(j, k1, k, i, i1);
						}
						for (int k2 = 0; k2 < tile.objectCount; k2++) {
							InteractableObject object = tile.interactableObjects[k2];
							if (object != null && object.renderable != null && object.renderable.normals != null) {
								method307(z, object.maxX - object.positionX + 1, object.maxY - object.positionY + 1, x, y,
										(Model) object.renderable);
								((Model) object.renderable).method480(j, k1, k, i, i1);
							}
						}

						FloorDecoration floorDecoration = tile.floorDecoration;
						if (floorDecoration != null && floorDecoration.renderable.normals != null) {
							method306(x, z, (Model) floorDecoration.renderable, y);
							((Model) floorDecoration.renderable).method480(j, k1, k, i, i1);
						}
					}
				}
			}
		}
	}

	public void method309(int[] raster, int x, int y, int plane, int i, int j) {
		SceneTile tile = tiles[plane][x][y];
		if (tile == null) {
			return;
		}

		GenericTile genericTile = tile.aClass43_1311;
		if (genericTile != null) {
			int j1 = genericTile.anInt722;
			if (j1 == 0) {
				return;
			}
			for (int k1 = 0; k1 < 4; k1++) {
				raster[i] = j1;
				raster[i + 1] = j1;
				raster[i + 2] = j1;
				raster[i + 3] = j1;
				i += j;
			}

			return;
		}

		ComplexTile complexTile = tile.aClass40_1312;
		if (complexTile == null) {
			return;
		}
		int l1 = complexTile.anInt684;
		int i2 = complexTile.orientation;
		int j2 = complexTile.anInt686;
		int k2 = complexTile.anInt687;
		int[] ai1 = anIntArrayArray489[l1];
		int[] ai2 = anIntArrayArray490[i2];
		int l2 = 0;

		if (j2 != 0) {
			for (int i3 = 0; i3 < 4; i3++) {
				raster[i] = ai1[ai2[l2++]] != 0 ? k2 : j2;
				raster[i + 1] = ai1[ai2[l2++]] != 0 ? k2 : j2;
				raster[i + 2] = ai1[ai2[l2++]] != 0 ? k2 : j2;
				raster[i + 3] = ai1[ai2[l2++]] != 0 ? k2 : j2;
				i += j;
			}
			return;
		}

		for (int j3 = 0; j3 < 4; j3++) {
			if (ai1[ai2[l2++]] != 0) {
				raster[i] = k2;
			}
			if (ai1[ai2[l2++]] != 0) {
				raster[i + 1] = k2;
			}
			if (ai1[ai2[l2++]] != 0) {
				raster[i + 2] = k2;
			}
			if (ai1[ai2[l2++]] != 0) {
				raster[i + 3] = k2;
			}
			i += j;
		}
	}

	public void method312(int i, int j) {
		aBoolean467 = true;
		anInt468 = j;
		anInt469 = i;
		anInt470 = -1;
		anInt471 = -1;
	}

	public void method313(int i, int j, int k, int l, int i1, int j1) {
		if (i < 0) {
			i = 0;
		} else if (i >= width * 128) {
			i = width * 128 - 1;
		}
		if (j < 0) {
			j = 0;
		} else if (j >= height * 128) {
			j = height * 128 - 1;
		}

		anInt448++;
		anInt458 = Model.SINE[j1];
		anInt459 = Model.COSINE[j1];
		anInt460 = Model.SINE[k];
		anInt461 = Model.COSINE[k];
		aBooleanArrayArray492 = aBooleanArrayArrayArrayArray491[(j1 - 128) / 32][k / 64];
		anInt455 = i;
		anInt456 = l;
		anInt457 = j;
		anInt453 = i / 128;
		anInt454 = j / 128;
		anInt447 = i1;
		anInt449 = anInt453 - 25;

		if (anInt449 < 0) {
			anInt449 = 0;
		}
		anInt451 = anInt454 - 25;
		if (anInt451 < 0) {
			anInt451 = 0;
		}
		anInt450 = anInt453 + 25;
		if (anInt450 > width) {
			anInt450 = width;
		}
		anInt452 = anInt454 + 25;
		if (anInt452 > height) {
			anInt452 = height;
		}
		method319();
		anInt446 = 0;

		for (int z = anInt442; z < planeCount; z++) {
			SceneTile planetiles[][] = tiles[z];
			for (int x = anInt449; x < anInt450; x++) {
				for (int y = anInt451; y < anInt452; y++) {
					SceneTile tile = planetiles[x][y];
					if (tile != null) {
						if (tile.anInt1321 > i1 || !aBooleanArrayArray492[x - anInt453 + 25][y - anInt454 + 25]
								&& anIntArrayArrayArray440[z][x][y] - l < 2000) {
							tile.aBoolean1322 = false;
							tile.aBoolean1323 = false;
							tile.anInt1325 = 0;
						} else {
							tile.aBoolean1322 = true;
							tile.aBoolean1323 = true;
							if (tile.objectCount > 0) {
								tile.aBoolean1324 = true;
							} else {
								tile.aBoolean1324 = false;
							}
							anInt446++;
						}
					}
				}
			}
		}

		for (int z = anInt442; z < planeCount; z++) {
			SceneTile planeTiles[][] = tiles[z];
			for (int dx = -25; dx <= 0; dx++) {
				int maxX = anInt453 + dx;
				int minX = anInt453 - dx;
				if (maxX >= anInt449 || minX < anInt450) {
					for (int dy = -25; dy <= 0; dy++) {
						int maxY = anInt454 + dy;
						int minY = anInt454 - dy;
						if (maxX >= anInt449) {
							if (maxY >= anInt451) {
								SceneTile tile = planeTiles[maxX][maxY];
								if (tile != null && tile.aBoolean1322) {
									method314(tile, true);
								}
							}
							if (minY < anInt452) {
								SceneTile tile = planeTiles[maxX][minY];
								if (tile != null && tile.aBoolean1322) {
									method314(tile, true);
								}
							}
						}
						if (minX < anInt450) {
							if (maxY >= anInt451) {
								SceneTile tile = planeTiles[minX][maxY];
								if (tile != null && tile.aBoolean1322) {
									method314(tile, true);
								}
							}
							if (minY < anInt452) {
								SceneTile tile = planeTiles[minX][minY];
								if (tile != null && tile.aBoolean1322) {
									method314(tile, true);
								}
							}
						}
						if (anInt446 == 0) {
							aBoolean467 = false;
							return;
						}
					}

				}
			}

		}

		for (int j2 = anInt442; j2 < planeCount; j2++) {
			SceneTile aclass30_sub3_2[][] = tiles[j2];
			for (int j3 = -25; j3 <= 0; j3++) {
				int l3 = anInt453 + j3;
				int j4 = anInt453 - j3;
				if (l3 >= anInt449 || j4 < anInt450) {
					for (int l4 = -25; l4 <= 0; l4++) {
						int j5 = anInt454 + l4;
						int k5 = anInt454 - l4;
						if (l3 >= anInt449) {
							if (j5 >= anInt451) {
								SceneTile class30_sub3_5 = aclass30_sub3_2[l3][j5];
								if (class30_sub3_5 != null && class30_sub3_5.aBoolean1322) {
									method314(class30_sub3_5, false);
								}
							}
							if (k5 < anInt452) {
								SceneTile class30_sub3_6 = aclass30_sub3_2[l3][k5];
								if (class30_sub3_6 != null && class30_sub3_6.aBoolean1322) {
									method314(class30_sub3_6, false);
								}
							}
						}
						if (j4 < anInt450) {
							if (j5 >= anInt451) {
								SceneTile class30_sub3_7 = aclass30_sub3_2[j4][j5];
								if (class30_sub3_7 != null && class30_sub3_7.aBoolean1322) {
									method314(class30_sub3_7, false);
								}
							}
							if (k5 < anInt452) {
								SceneTile class30_sub3_8 = aclass30_sub3_2[j4][k5];
								if (class30_sub3_8 != null && class30_sub3_8.aBoolean1322) {
									method314(class30_sub3_8, false);
								}
							}
						}
						if (anInt446 == 0) {
							aBoolean467 = false;
							return;
						}
					}

				}
			}

		}

		aBoolean467 = false;
	}

	public void method314(SceneTile newTile, boolean flag) {
		aClass19_477.pushBack(newTile);
		do {
			SceneTile front;
			do {
				front = (SceneTile) aClass19_477.popFront();
				if (front == null) {
					return;
				}
			} while (!front.aBoolean1323);

			int x = front.positionX;
			int y = front.positionY;
			int plane = front.plane;
			int l = front.anInt1310;
			SceneTile[][] planeTiles = this.tiles[plane];

			if (front.aBoolean1322) {
				if (flag) {
					if (plane > 0) {
						SceneTile tile = this.tiles[plane - 1][x][y];
						if (tile != null && tile.aBoolean1323) {
							continue;
						}
					}
					if (x <= anInt453 && x > anInt449) {
						SceneTile tile = planeTiles[x - 1][y];
						if (tile != null && tile.aBoolean1323 && (tile.aBoolean1322 || (front.anInt1320 & 1) == 0)) {
							continue;
						}
					}
					if (x >= anInt453 && x < anInt450 - 1) {
						SceneTile tile = planeTiles[x + 1][y];
						if (tile != null && tile.aBoolean1323 && (tile.aBoolean1322 || (front.anInt1320 & 4) == 0)) {
							continue;
						}
					}
					if (y <= anInt454 && y > anInt451) {
						SceneTile tile = planeTiles[x][y - 1];
						if (tile != null && tile.aBoolean1323 && (tile.aBoolean1322 || (front.anInt1320 & 8) == 0)) {
							continue;
						}
					}
					if (y >= anInt454 && y < anInt452 - 1) {
						SceneTile tile = planeTiles[x][y + 1];
						if (tile != null && tile.aBoolean1323 && (tile.aBoolean1322 || (front.anInt1320 & 2) == 0)) {
							continue;
						}
					}
				} else {
					flag = true;
				}

				front.aBoolean1322 = false;
				if (front.aClass30_Sub3_1329 != null) {
					SceneTile tile = front.aClass30_Sub3_1329;
					if (tile.aClass43_1311 != null) {
						if (!method320(0, x, y)) {
							method315(tile.aClass43_1311, 0, anInt458, anInt459, anInt460, anInt461, x, y);
						}
					} else if (tile.aClass40_1312 != null && !method320(0, x, y)) {
						method316(x, anInt458, anInt460, tile.aClass40_1312, anInt459, y, anInt461);
					}
					Wall wall = tile.wall;
					if (wall != null) {
						wall.aClass30_Sub2_Sub4_278.render(wall.positionX - anInt455, wall.positionY - anInt457, 0, anInt458,
								anInt459, anInt460, anInt461, wall.anInt273 - anInt456, wall.key);
					}
					for (int i = 0; i < tile.objectCount; i++) {
						InteractableObject object = tile.interactableObjects[i];
						if (object != null) {
							object.renderable.render(object.centreX - anInt455, object.centreY - anInt457, object.yaw, anInt458,
									anInt459, anInt460, anInt461, object.renderHeight - anInt456, object.key);
						}
					}
				}

				boolean flag1 = false;
				if (front.aClass43_1311 != null) {
					if (!method320(l, x, y)) {
						flag1 = true;
						method315(front.aClass43_1311, l, anInt458, anInt459, anInt460, anInt461, x, y);
					}
				} else if (front.aClass40_1312 != null && !method320(l, x, y)) {
					flag1 = true;
					method316(x, anInt458, anInt460, front.aClass40_1312, anInt459, y, anInt461);
				}

				int j1 = 0;
				int j2 = 0;
				Wall wall = front.wall;
				WallDecoration decoration = front.wallDecoration;

				if (wall != null || decoration != null) {
					if (anInt453 == x) {
						j1++;
					} else if (anInt453 < x) {
						j1 += 2;
					}
					if (anInt454 == y) {
						j1 += 3;
					} else if (anInt454 > y) {
						j1 += 6;
					}
					j2 = anIntArray478[j1];
					front.anInt1328 = anIntArray480[j1];
				}

				if (wall != null) {
					if ((wall.anInt276 & anIntArray479[j1]) != 0) {
						if (wall.anInt276 == 16) {
							front.anInt1325 = 3;
							front.anInt1326 = anIntArray481[j1];
							front.anInt1327 = 3 - front.anInt1326;
						} else if (wall.anInt276 == 32) {
							front.anInt1325 = 6;
							front.anInt1326 = anIntArray482[j1];
							front.anInt1327 = 6 - front.anInt1326;
						} else if (wall.anInt276 == 64) {
							front.anInt1325 = 12;
							front.anInt1326 = anIntArray483[j1];
							front.anInt1327 = 12 - front.anInt1326;
						} else {
							front.anInt1325 = 9;
							front.anInt1326 = anIntArray484[j1];
							front.anInt1327 = 9 - front.anInt1326;
						}
					} else {
						front.anInt1325 = 0;
					}
					if ((wall.anInt276 & j2) != 0 && !method321(l, x, y, wall.anInt276)) {
						wall.aClass30_Sub2_Sub4_278.render(wall.positionX - anInt455, wall.positionY - anInt457, 0, anInt458,
								anInt459, anInt460, anInt461, wall.anInt273 - anInt456, wall.key);
					}
					if ((wall.anInt277 & j2) != 0 && !method321(l, x, y, wall.anInt277)) {
						wall.aClass30_Sub2_Sub4_279.render(wall.positionX - anInt455, wall.positionY - anInt457, 0, anInt458,
								anInt459, anInt460, anInt461, wall.anInt273 - anInt456, wall.key);
					}
				}

				if (decoration != null && !method322(l, x, y, decoration.renderable.modelHeight)) {
					if ((decoration.attributes & j2) != 0) {
						decoration.renderable.render(decoration.x - anInt455, decoration.y - anInt457, decoration.orientation,
								anInt458, anInt459, anInt460, anInt461, decoration.height - anInt456, decoration.key);
					} else if ((decoration.attributes & 0x300) != 0) {
						int dx = decoration.x - anInt455;
						int l5 = decoration.height - anInt456;
						int dy = decoration.y - anInt457;
						int orientation = decoration.orientation;
						int k9;
						if (orientation == 1 || orientation == 2) {
							k9 = -dx;
						} else {
							k9 = dx;
						}
						int k10;
						if (orientation == 2 || orientation == 3) {
							k10 = -dy;
						} else {
							k10 = dy;
						}
						if ((decoration.attributes & 0x100) != 0 && k10 < k9) {
							int i11 = dx + anIntArray463[orientation];
							int k11 = dy + anIntArray464[orientation];
							decoration.renderable.render(i11, k11, orientation * 512 + 256, anInt458, anInt459, anInt460,
									anInt461, l5, decoration.key);
						}
						if ((decoration.attributes & 0x200) != 0 && k10 > k9) {
							int j11 = dx + anIntArray465[orientation];
							int l11 = dy + anIntArray466[orientation];
							decoration.renderable.render(j11, l11, orientation * 512 + 1280 & 0x7ff, anInt458, anInt459,
									anInt460, anInt461, l5, decoration.key);
						}
					}
				}

				if (flag1) {
					FloorDecoration decor = front.floorDecoration;
					if (decor != null) {
						decor.renderable.render(decor.x - anInt455, decor.y - anInt457, 0, anInt458, anInt459, anInt460,
								anInt461, decor.height - anInt456, decor.key);
					}
					GroundItemTile item = front.groundItem;
					if (item != null && item.itemHeight == 0) {
						if (item.tertiary != null) {
							item.tertiary.render(item.x - anInt455, item.y - anInt457, 0, anInt458, anInt459, anInt460, anInt461,
									item.height - anInt456, item.key);
						}
						if (item.secondary != null) {
							item.secondary.render(item.x - anInt455, item.y - anInt457, 0, anInt458, anInt459, anInt460,
									anInt461, item.height - anInt456, item.key);
						}
						if (item.primary != null) {
							item.primary.render(item.x - anInt455, item.y - anInt457, 0, anInt458, anInt459, anInt460, anInt461,
									item.height - anInt456, item.key);
						}
					}
				}
				int k4 = front.anInt1320;
				if (k4 != 0) {
					if (x < anInt453 && (k4 & 4) != 0) {
						SceneTile class30_sub3_17 = planeTiles[x + 1][y];
						if (class30_sub3_17 != null && class30_sub3_17.aBoolean1323) {
							aClass19_477.pushBack(class30_sub3_17);
						}
					}
					if (y < anInt454 && (k4 & 2) != 0) {
						SceneTile class30_sub3_18 = planeTiles[x][y + 1];
						if (class30_sub3_18 != null && class30_sub3_18.aBoolean1323) {
							aClass19_477.pushBack(class30_sub3_18);
						}
					}
					if (x > anInt453 && (k4 & 1) != 0) {
						SceneTile class30_sub3_19 = planeTiles[x - 1][y];
						if (class30_sub3_19 != null && class30_sub3_19.aBoolean1323) {
							aClass19_477.pushBack(class30_sub3_19);
						}
					}
					if (y > anInt454 && (k4 & 8) != 0) {
						SceneTile class30_sub3_20 = planeTiles[x][y - 1];
						if (class30_sub3_20 != null && class30_sub3_20.aBoolean1323) {
							aClass19_477.pushBack(class30_sub3_20);
						}
					}
				}
			}
			if (front.anInt1325 != 0) {
				boolean flag2 = true;
				for (int k1 = 0; k1 < front.objectCount; k1++) {
					if (front.interactableObjects[k1].anInt528 == anInt448
							|| (front.anIntArray1319[k1] & front.anInt1325) != front.anInt1326) {
						continue;
					}
					flag2 = false;
					break;
				}

				if (flag2) {
					Wall class10_1 = front.wall;
					if (!method321(l, x, y, class10_1.anInt276)) {
						class10_1.aClass30_Sub2_Sub4_278.render(class10_1.positionX - anInt455, class10_1.positionY - anInt457,
								0, anInt458, anInt459, anInt460, anInt461, class10_1.anInt273 - anInt456, class10_1.key);
					}
					front.anInt1325 = 0;
				}
			}
			if (front.aBoolean1324) {
				try {
					int i1 = front.objectCount;
					front.aBoolean1324 = false;
					int l1 = 0;
					label0: for (int k2 = 0; k2 < i1; k2++) {
						InteractableObject class28_1 = front.interactableObjects[k2];
						if (class28_1.anInt528 == anInt448) {
							continue;
						}
						for (int k3 = class28_1.positionX; k3 <= class28_1.maxX; k3++) {
							for (int l4 = class28_1.positionY; l4 <= class28_1.maxY; l4++) {
								SceneTile class30_sub3_21 = planeTiles[k3][l4];
								if (class30_sub3_21.aBoolean1322) {
									front.aBoolean1324 = true;
								} else {
									if (class30_sub3_21.anInt1325 == 0) {
										continue;
									}
									int l6 = 0;
									if (k3 > class28_1.positionX) {
										l6++;
									}
									if (k3 < class28_1.maxX) {
										l6 += 4;
									}
									if (l4 > class28_1.positionY) {
										l6 += 8;
									}
									if (l4 < class28_1.maxY) {
										l6 += 2;
									}
									if ((l6 & class30_sub3_21.anInt1325) != front.anInt1327) {
										continue;
									}
									front.aBoolean1324 = true;
								}
								continue label0;
							}

						}

						aClass28Array462[l1++] = class28_1;
						int i5 = anInt453 - class28_1.positionX;
						int i6 = class28_1.maxX - anInt453;
						if (i6 > i5) {
							i5 = i6;
						}
						int i7 = anInt454 - class28_1.positionY;
						int j8 = class28_1.maxY - anInt454;
						if (j8 > i7) {
							class28_1.anInt527 = i5 + j8;
						} else {
							class28_1.anInt527 = i5 + i7;
						}
					}

					while (l1 > 0) {
						int i3 = -50;
						int l3 = -1;
						for (int j5 = 0; j5 < l1; j5++) {
							InteractableObject class28_2 = aClass28Array462[j5];
							if (class28_2.anInt528 != anInt448) {
								if (class28_2.anInt527 > i3) {
									i3 = class28_2.anInt527;
									l3 = j5;
								} else if (class28_2.anInt527 == i3) {
									int j7 = class28_2.centreX - anInt455;
									int k8 = class28_2.centreY - anInt457;
									int l9 = aClass28Array462[l3].centreX - anInt455;
									int l10 = aClass28Array462[l3].centreY - anInt457;
									if (j7 * j7 + k8 * k8 > l9 * l9 + l10 * l10) {
										l3 = j5;
									}
								}
							}
						}

						if (l3 == -1) {
							break;
						}
						InteractableObject class28_3 = aClass28Array462[l3];
						class28_3.anInt528 = anInt448;
						if (!method323(l, class28_3.positionX, class28_3.maxX, class28_3.positionY, class28_3.maxY,
								class28_3.renderable.modelHeight)) {
							class28_3.renderable.render(class28_3.centreX - anInt455, class28_3.centreY - anInt457,
									class28_3.yaw, anInt458, anInt459, anInt460, anInt461, class28_3.renderHeight - anInt456,
									class28_3.key);
						}
						for (int k7 = class28_3.positionX; k7 <= class28_3.maxX; k7++) {
							for (int l8 = class28_3.positionY; l8 <= class28_3.maxY; l8++) {
								SceneTile class30_sub3_22 = planeTiles[k7][l8];
								if (class30_sub3_22.anInt1325 != 0) {
									aClass19_477.pushBack(class30_sub3_22);
								} else if ((k7 != x || l8 != y) && class30_sub3_22.aBoolean1323) {
									aClass19_477.pushBack(class30_sub3_22);
								}
							}

						}

					}
					if (front.aBoolean1324) {
						continue;
					}
				} catch (Exception _ex) {
					front.aBoolean1324 = false;
				}
			}
			if (!front.aBoolean1323 || front.anInt1325 != 0) {
				continue;
			}
			if (x <= anInt453 && x > anInt449) {
				SceneTile class30_sub3_8 = planeTiles[x - 1][y];
				if (class30_sub3_8 != null && class30_sub3_8.aBoolean1323) {
					continue;
				}
			}
			if (x >= anInt453 && x < anInt450 - 1) {
				SceneTile class30_sub3_9 = planeTiles[x + 1][y];
				if (class30_sub3_9 != null && class30_sub3_9.aBoolean1323) {
					continue;
				}
			}
			if (y <= anInt454 && y > anInt451) {
				SceneTile class30_sub3_10 = planeTiles[x][y - 1];
				if (class30_sub3_10 != null && class30_sub3_10.aBoolean1323) {
					continue;
				}
			}
			if (y >= anInt454 && y < anInt452 - 1) {
				SceneTile class30_sub3_11 = planeTiles[x][y + 1];
				if (class30_sub3_11 != null && class30_sub3_11.aBoolean1323) {
					continue;
				}
			}
			front.aBoolean1323 = false;
			anInt446--;
			GroundItemTile item = front.groundItem;
			if (item != null && item.itemHeight != 0) {
				if (item.tertiary != null) {
					item.tertiary.render(item.x - anInt455, item.y - anInt457, 0, anInt458, anInt459, anInt460, anInt461,
							item.height - anInt456 - item.itemHeight, item.key);
				}
				if (item.secondary != null) {
					item.secondary.render(item.x - anInt455, item.y - anInt457, 0, anInt458, anInt459, anInt460, anInt461,
							item.height - anInt456 - item.itemHeight, item.key);
				}
				if (item.primary != null) {
					item.primary.render(item.x - anInt455, item.y - anInt457, 0, anInt458, anInt459, anInt460, anInt461,
							item.height - anInt456 - item.itemHeight, item.key);
				}
			}
			if (front.anInt1328 != 0) {
				WallDecoration decor = front.wallDecoration;
				if (decor != null && !method322(l, x, y, decor.renderable.modelHeight)) {
					if ((decor.attributes & front.anInt1328) != 0) {
						decor.renderable.render(decor.x - anInt455, decor.y - anInt457, decor.orientation, anInt458, anInt459,
								anInt460, anInt461, decor.height - anInt456, decor.key);
					} else if ((decor.attributes & 0x300) != 0) {
						int l2 = decor.x - anInt455;
						int j3 = decor.height - anInt456;
						int i4 = decor.y - anInt457;
						int orientation = decor.orientation;
						int j6;
						if (orientation == 1 || orientation == 2) {
							j6 = -l2;
						} else {
							j6 = l2;
						}
						int l7;
						if (orientation == 2 || orientation == 3) {
							l7 = -i4;
						} else {
							l7 = i4;
						}
						if ((decor.attributes & 0x100) != 0 && l7 >= j6) {
							int i9 = l2 + anIntArray463[orientation];
							int i10 = i4 + anIntArray464[orientation];
							decor.renderable.render(i9, i10, orientation * 512 + 256, anInt458, anInt459, anInt460, anInt461, j3,
									decor.key);
						}
						if ((decor.attributes & 0x200) != 0 && l7 <= j6) {
							int j9 = l2 + anIntArray465[orientation];
							int j10 = i4 + anIntArray466[orientation];
							decor.renderable.render(j9, j10, orientation * 512 + 1280 & 0x7ff, anInt458, anInt459, anInt460,
									anInt461, j3, decor.key);
						}
					}
				}
				Wall class10_2 = front.wall;
				if (class10_2 != null) {
					if ((class10_2.anInt277 & front.anInt1328) != 0 && !method321(l, x, y, class10_2.anInt277)) {
						class10_2.aClass30_Sub2_Sub4_279.render(class10_2.positionX - anInt455, class10_2.positionY - anInt457,
								0, anInt458, anInt459, anInt460, anInt461, class10_2.anInt273 - anInt456, class10_2.key);
					}
					if ((class10_2.anInt276 & front.anInt1328) != 0 && !method321(l, x, y, class10_2.anInt276)) {
						class10_2.aClass30_Sub2_Sub4_278.render(class10_2.positionX - anInt455, class10_2.positionY - anInt457,
								0, anInt458, anInt459, anInt460, anInt461, class10_2.anInt273 - anInt456, class10_2.key);
					}
				}
			}
			if (plane < planeCount - 1) {
				SceneTile class30_sub3_12 = tiles[plane + 1][x][y];
				if (class30_sub3_12 != null && class30_sub3_12.aBoolean1323) {
					aClass19_477.pushBack(class30_sub3_12);
				}
			}
			if (x < anInt453) {
				SceneTile class30_sub3_13 = planeTiles[x + 1][y];
				if (class30_sub3_13 != null && class30_sub3_13.aBoolean1323) {
					aClass19_477.pushBack(class30_sub3_13);
				}
			}
			if (y < anInt454) {
				SceneTile class30_sub3_14 = planeTiles[x][y + 1];
				if (class30_sub3_14 != null && class30_sub3_14.aBoolean1323) {
					aClass19_477.pushBack(class30_sub3_14);
				}
			}
			if (x > anInt453) {
				SceneTile class30_sub3_15 = planeTiles[x - 1][y];
				if (class30_sub3_15 != null && class30_sub3_15.aBoolean1323) {
					aClass19_477.pushBack(class30_sub3_15);
				}
			}
			if (y > anInt454) {
				SceneTile class30_sub3_16 = planeTiles[x][y - 1];
				if (class30_sub3_16 != null && class30_sub3_16.aBoolean1323) {
					aClass19_477.pushBack(class30_sub3_16);
				}
			}
		} while (true);
	}

	public void method315(GenericTile tile, int plane, int j, int k, int l, int i1, int x, int y) {
		int l1;
		int i2 = l1 = (x << 7) - anInt455;
		int j2;
		int k2 = j2 = (y << 7) - anInt457;
		int l2;
		int i3 = l2 = i2 + 128;
		int j3;
		int k3 = j3 = k2 + 128;
		int l3 = anIntArrayArrayArray440[plane][x][y] - anInt456;
		int i4 = anIntArrayArrayArray440[plane][x + 1][y] - anInt456;
		int j4 = anIntArrayArrayArray440[plane][x + 1][y + 1] - anInt456;
		int k4 = anIntArrayArrayArray440[plane][x][y + 1] - anInt456;
		int l4 = k2 * l + i2 * i1 >> 16;
		k2 = k2 * i1 - i2 * l >> 16;
		i2 = l4;
		l4 = l3 * k - k2 * j >> 16;
		k2 = l3 * j + k2 * k >> 16;
		l3 = l4;
		if (k2 < 50) {
			return;
		}
		l4 = j2 * l + i3 * i1 >> 16;
		j2 = j2 * i1 - i3 * l >> 16;
		i3 = l4;
		l4 = i4 * k - j2 * j >> 16;
		j2 = i4 * j + j2 * k >> 16;
		i4 = l4;
		if (j2 < 50) {
			return;
		}
		l4 = k3 * l + l2 * i1 >> 16;
		k3 = k3 * i1 - l2 * l >> 16;
		l2 = l4;
		l4 = j4 * k - k3 * j >> 16;
		k3 = j4 * j + k3 * k >> 16;
		j4 = l4;
		if (k3 < 50) {
			return;
		}
		l4 = j3 * l + l1 * i1 >> 16;
		j3 = j3 * i1 - l1 * l >> 16;
		l1 = l4;
		l4 = k4 * k - j3 * j >> 16;
		j3 = k4 * j + j3 * k >> 16;
		k4 = l4;
		if (j3 < 50) {
			return;
		}
		int i5 = Rasterizer.originViewX + (i2 << 9) / k2;
		int j5 = Rasterizer.originViewY + (l3 << 9) / k2;
		int k5 = Rasterizer.originViewX + (i3 << 9) / j2;
		int l5 = Rasterizer.originViewY + (i4 << 9) / j2;
		int i6 = Rasterizer.originViewX + (l2 << 9) / k3;
		int j6 = Rasterizer.originViewY + (j4 << 9) / k3;
		int k6 = Rasterizer.originViewX + (l1 << 9) / j3;
		int l6 = Rasterizer.originViewY + (k4 << 9) / j3;
		Rasterizer.anInt1465 = 0;
		if ((i6 - k6) * (l5 - l6) - (j6 - l6) * (k5 - k6) > 0) {
			Rasterizer.aBoolean1462 = false;
			if (i6 < 0 || k6 < 0 || k5 < 0 || i6 > Raster.anInt1385 || k6 > Raster.anInt1385 || k5 > Raster.anInt1385) {
				Rasterizer.aBoolean1462 = true;
			}
			if (aBoolean467 && method318(anInt468, anInt469, j6, l6, l5, i6, k6, k5)) {
				anInt470 = x;
				anInt471 = y;
			}
			if (tile.texture == -1) {
				if (tile.anInt718 != 0xbc614e) {
					Rasterizer.method374(j6, l6, l5, i6, k6, k5, tile.anInt718, tile.anInt719, tile.anInt717);
				}
			} else if (!lowMemory) {
				if (tile.aBoolean721) {
					Rasterizer.method378(j6, l6, l5, i6, k6, k5, tile.anInt718, tile.anInt719, tile.anInt717, i2, i3, l1, l3, i4,
							k4, k2, j2, j3, tile.texture);
				} else {
					Rasterizer.method378(j6, l6, l5, i6, k6, k5, tile.anInt718, tile.anInt719, tile.anInt717, l2, l1, i3, j4, k4,
							i4, k3, j3, j2, tile.texture);
				}
			} else {
				int i7 = anIntArray485[tile.texture];
				Rasterizer.method374(j6, l6, l5, i6, k6, k5, method317(i7, tile.anInt718), method317(i7, tile.anInt719),
						method317(i7, tile.anInt717));
			}
		}

		if ((i5 - k5) * (l6 - l5) - (j5 - l5) * (k6 - k5) > 0) {
			Rasterizer.aBoolean1462 = false;
			if (i5 < 0 || k5 < 0 || k6 < 0 || i5 > Raster.anInt1385 || k5 > Raster.anInt1385 || k6 > Raster.anInt1385) {
				Rasterizer.aBoolean1462 = true;
			}
			if (aBoolean467 && method318(anInt468, anInt469, j5, l5, l6, i5, k5, k6)) {
				anInt470 = x;
				anInt471 = y;
			}
			if (tile.texture == -1) {
				if (tile.anInt716 != 0xbc614e) {
					Rasterizer.method374(j5, l5, l6, i5, k5, k6, tile.anInt716, tile.anInt717, tile.anInt719);
					return;
				}
			} else {
				if (!lowMemory) {
					Rasterizer.method378(j5, l5, l6, i5, k5, k6, tile.anInt716, tile.anInt717, tile.anInt719, i2, i3, l1, l3, i4,
							k4, k2, j2, j3, tile.texture);
					return;
				}
				int j7 = anIntArray485[tile.texture];
				Rasterizer.method374(j5, l5, l6, i5, k5, k6, method317(j7, tile.anInt716), method317(j7, tile.anInt717),
						method317(j7, tile.anInt719));
			}
		}
	}

	public void method316(int i, int j, int k, ComplexTile tile, int l, int i1, int j1) {
		int k1 = tile.anIntArray673.length;
		for (int l1 = 0; l1 < k1; l1++) {
			int i2 = tile.anIntArray673[l1] - anInt455;
			int k2 = tile.anIntArray674[l1] - anInt456;
			int i3 = tile.anIntArray675[l1] - anInt457;
			int k3 = i3 * k + i2 * j1 >> 16;
			i3 = i3 * j1 - i2 * k >> 16;
			i2 = k3;
			k3 = k2 * l - i3 * j >> 16;
			i3 = k2 * j + i3 * l >> 16;
			k2 = k3;
			if (i3 < 50) {
				return;
			}
			if (tile.anIntArray682 != null) {
				ComplexTile.anIntArray690[l1] = i2;
				ComplexTile.anIntArray691[l1] = k2;
				ComplexTile.anIntArray692[l1] = i3;
			}
			ComplexTile.anIntArray688[l1] = Rasterizer.originViewX + (i2 << 9) / i3;
			ComplexTile.anIntArray689[l1] = Rasterizer.originViewY + (k2 << 9) / i3;
		}

		Rasterizer.anInt1465 = 0;
		k1 = tile.anIntArray679.length;
		for (int j2 = 0; j2 < k1; j2++) {
			int l2 = tile.anIntArray679[j2];
			int j3 = tile.anIntArray680[j2];
			int l3 = tile.anIntArray681[j2];
			int i4 = ComplexTile.anIntArray688[l2];
			int j4 = ComplexTile.anIntArray688[j3];
			int k4 = ComplexTile.anIntArray688[l3];
			int l4 = ComplexTile.anIntArray689[l2];
			int i5 = ComplexTile.anIntArray689[j3];
			int j5 = ComplexTile.anIntArray689[l3];
			if ((i4 - j4) * (j5 - i5) - (l4 - i5) * (k4 - j4) > 0) {
				Rasterizer.aBoolean1462 = false;
				if (i4 < 0 || j4 < 0 || k4 < 0 || i4 > Raster.anInt1385 || j4 > Raster.anInt1385 || k4 > Raster.anInt1385) {
					Rasterizer.aBoolean1462 = true;
				}
				if (aBoolean467 && method318(anInt468, anInt469, l4, i5, j5, i4, j4, k4)) {
					anInt470 = i;
					anInt471 = i1;
				}
				if (tile.anIntArray682 == null || tile.anIntArray682[j2] == -1) {
					if (tile.anIntArray676[j2] != 0xbc614e) {
						Rasterizer.method374(l4, i5, j5, i4, j4, k4, tile.anIntArray676[j2], tile.anIntArray677[j2],
								tile.anIntArray678[j2]);
					}
				} else if (!lowMemory) {
					if (tile.aBoolean683) {
						Rasterizer.method378(l4, i5, j5, i4, j4, k4, tile.anIntArray676[j2], tile.anIntArray677[j2],
								tile.anIntArray678[j2], ComplexTile.anIntArray690[0], ComplexTile.anIntArray690[1],
								ComplexTile.anIntArray690[3], ComplexTile.anIntArray691[0], ComplexTile.anIntArray691[1],
								ComplexTile.anIntArray691[3], ComplexTile.anIntArray692[0], ComplexTile.anIntArray692[1],
								ComplexTile.anIntArray692[3], tile.anIntArray682[j2]);
					} else {
						Rasterizer.method378(l4, i5, j5, i4, j4, k4, tile.anIntArray676[j2], tile.anIntArray677[j2],
								tile.anIntArray678[j2], ComplexTile.anIntArray690[l2], ComplexTile.anIntArray690[j3],
								ComplexTile.anIntArray690[l3], ComplexTile.anIntArray691[l2], ComplexTile.anIntArray691[j3],
								ComplexTile.anIntArray691[l3], ComplexTile.anIntArray692[l2], ComplexTile.anIntArray692[j3],
								ComplexTile.anIntArray692[l3], tile.anIntArray682[j2]);
					}
				} else {
					int k5 = anIntArray485[tile.anIntArray682[j2]];
					Rasterizer.method374(l4, i5, j5, i4, j4, k4, method317(k5, tile.anIntArray676[j2]),
							method317(k5, tile.anIntArray677[j2]), method317(k5, tile.anIntArray678[j2]));
				}
			}
		}
	}

	public int method317(int j, int k) {
		k = 127 - k;
		k = k * (j & 0x7f) / 160;
		if (k < 2) {
			k = 2;
		} else if (k > 126) {
			k = 126;
		}
		return (j & 0xff80) + k;
	}

	public boolean method318(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
		if (j < k && j < l && j < i1) {
			return false;
		} else if (j > k && j > l && j > i1) {
			return false;
		} else if (i < j1 && i < k1 && i < l1) {
			return false;
		} else if (i > j1 && i > k1 && i > l1) {
			return false;
		}

		int i2 = (j - k) * (k1 - j1) - (i - j1) * (l - k);
		int j2 = (j - i1) * (j1 - l1) - (i - l1) * (k - i1);
		int k2 = (j - l) * (l1 - k1) - (i - k1) * (i1 - l);
		return i2 * k2 > 0 && k2 * j2 > 0;
	}

	public void reset() {
		for (int z = 0; z < planeCount; z++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					tiles[z][x][y] = null;
				}
			}
		}

		for (int x = 0; x < anInt472; x++) {
			for (int y = 0; y < anIntArray473[x]; y++) {
				clusters[x][y] = null;
			}

			anIntArray473[x] = 0;
		}

		for (int k1 = 0; k1 < anInt443; k1++) {
			aClass28Array444[k1] = null;
		}

		anInt443 = 0;
		for (int l1 = 0; l1 < aClass28Array462.length; l1++) {
			aClass28Array462[l1] = null;
		}
	}

	private boolean addObject(int plane, int minX, int minY, int deltaX, int deltaY, int centreX, int centreY, int renderHeight,
			Renderable renderable, int yaw, boolean flag, int key, byte config) {
		for (int x = minX; x < minX + deltaX; x++) {
			for (int y = minY; y < minY + deltaY; y++) {
				if (x < 0 || y < 0 || x >= width || y >= height) {
					return false;
				}
				SceneTile tile = tiles[plane][x][y];
				if (tile != null && tile.objectCount >= 5) {
					return false;
				}
			}
		}

		InteractableObject object = new InteractableObject();
		object.key = key;
		object.config = config;
		object.plane = plane;
		object.centreX = centreX;
		object.centreY = centreY;
		object.renderHeight = renderHeight;
		object.renderable = renderable;
		object.yaw = yaw;
		object.positionX = minX;
		object.positionY = minY;
		object.maxX = minX + deltaX - 1;
		object.maxY = minY + deltaY - 1;
		for (int x = minX; x < minX + deltaX; x++) {
			for (int y = minY; y < minY + deltaY; y++) {
				int k3 = 0;
				if (x > minX) {
					k3++;
				}
				if (x < minX + deltaX - 1) {
					k3 += 4;
				}
				if (y > minY) {
					k3 += 8;
				}
				if (y < minY + deltaY - 1) {
					k3 += 2;
				}
				for (int z = plane; z >= 0; z--) {
					if (tiles[z][x][y] == null) {
						tiles[z][x][y] = new SceneTile(x, y, z);
					}
				}

				SceneTile tile = tiles[plane][x][y];
				tile.interactableObjects[tile.objectCount] = object;
				tile.anIntArray1319[tile.objectCount] = k3;
				tile.anInt1320 |= k3;
				tile.objectCount++;
			}

		}

		if (flag) {
			aClass28Array444[anInt443++] = object;
		}
		return true;
	}

	private void method289(InteractableObject object) {
		for (int x = object.positionX; x <= object.maxX; x++) {
			for (int y = object.positionY; y <= object.maxY; y++) {
				SceneTile tile = tiles[object.plane][x][y];
				if (tile != null) {
					for (int i = 0; i < tile.objectCount; i++) {
						if (tile.interactableObjects[i] != object) {
							continue;
						}
						tile.objectCount--;
						for (int i1 = i; i1 < tile.objectCount; i1++) {
							tile.interactableObjects[i1] = tile.interactableObjects[i1 + 1];
							tile.anIntArray1319[i1] = tile.anIntArray1319[i1 + 1];
						}

						tile.interactableObjects[tile.objectCount] = null;
						break;
					}

					tile.anInt1320 = 0;
					for (int j1 = 0; j1 < tile.objectCount; j1++) {
						tile.anInt1320 |= tile.anIntArray1319[j1];
					}
				}
			}
		}
	}

	private void method306(int x, int z, Model model, int y) {
		if (x < width) {
			SceneTile tile = tiles[z][x + 1][y];
			if (tile != null && tile.floorDecoration != null && tile.floorDecoration.renderable.normals != null) {
				method308(model, (Model) tile.floorDecoration.renderable, 128, 0, 0, true);
			}
		}
		if (y < width) {
			SceneTile tile = tiles[z][x][y + 1];
			if (tile != null && tile.floorDecoration != null && tile.floorDecoration.renderable.normals != null) {
				method308(model, (Model) tile.floorDecoration.renderable, 0, 0, 128, true);
			}
		}
		if (x < width && y < height) {
			SceneTile tile = tiles[z][x + 1][y + 1];
			if (tile != null && tile.floorDecoration != null && tile.floorDecoration.renderable.normals != null) {
				method308(model, (Model) tile.floorDecoration.renderable, 128, 0, 128, true);
			}
		}
		if (x < width && y > 0) {
			SceneTile tile = tiles[z][x + 1][y - 1];
			if (tile != null && tile.floorDecoration != null && tile.floorDecoration.renderable.normals != null) {
				method308(model, (Model) tile.floorDecoration.renderable, 128, 0, -128, true);
			}
		}
	}

	private void method307(int plane, int j, int k, int l, int i1, Model model) {
		boolean flag = true;
		int initialX = l;
		int finalX = l + j;
		int initialY = i1 - 1;
		int finalY = i1 + k;
		for (int z = plane; z <= plane + 1; z++) {
			if (z != planeCount) {
				for (int x = initialX; x <= finalX; x++) {
					if (x >= 0 && x < width) {
						for (int y = initialY; y <= finalY; y++) {
							if (y >= 0 && y < height && (!flag || x >= finalX || y >= finalY || y < i1 && x != l)) {
								SceneTile tile = tiles[z][x][y];
								if (tile != null) {
									int i3 = (anIntArrayArrayArray440[z][x][y] + anIntArrayArrayArray440[z][x + 1][y]
											+ anIntArrayArrayArray440[z][x][y + 1] + anIntArrayArrayArray440[z][x + 1][y + 1])
											/ 4
											- (anIntArrayArrayArray440[plane][l][i1] + anIntArrayArrayArray440[plane][l + 1][i1]
													+ anIntArrayArrayArray440[plane][l][i1 + 1] + anIntArrayArrayArray440[plane][l + 1][i1 + 1])
											/ 4;
									Wall wall = tile.wall;
									if (wall != null && wall.aClass30_Sub2_Sub4_278 != null
											&& wall.aClass30_Sub2_Sub4_278.normals != null) {
										method308(model, (Model) wall.aClass30_Sub2_Sub4_278, (x - l) * 128 + (1 - j) * 64, i3,
												(y - i1) * 128 + (1 - k) * 64, flag);
									}
									if (wall != null && wall.aClass30_Sub2_Sub4_279 != null
											&& wall.aClass30_Sub2_Sub4_279.normals != null) {
										method308(model, (Model) wall.aClass30_Sub2_Sub4_279, (x - l) * 128 + (1 - j) * 64, i3,
												(y - i1) * 128 + (1 - k) * 64, flag);
									}
									for (int j3 = 0; j3 < tile.objectCount; j3++) {
										InteractableObject interactableObject = tile.interactableObjects[j3];
										if (interactableObject != null && interactableObject.renderable != null
												&& interactableObject.renderable.normals != null) {
											int k3 = interactableObject.maxX - interactableObject.positionX + 1;
											int l3 = interactableObject.maxY - interactableObject.positionY + 1;
											method308(model, (Model) interactableObject.renderable,
													(interactableObject.positionX - l) * 128 + (k3 - j) * 64, i3,
													(interactableObject.positionY - i1) * 128 + (l3 - k) * 64, flag);
										}
									}
								}
							}
						}
					}
				}
				initialX--;
				flag = false;
			}
		}
	}

	private void method308(Model class30_sub2_sub4_sub6, Model class30_sub2_sub4_sub6_1, int i, int j, int k, boolean flag) {
		anInt488++;
		int l = 0;
		int[] ai = class30_sub2_sub4_sub6_1.vertexX;
		int i1 = class30_sub2_sub4_sub6_1.vertices;
		for (int j1 = 0; j1 < class30_sub2_sub4_sub6.vertices; j1++) {
			VertexNormal class33 = ((Renderable) class30_sub2_sub4_sub6).normals[j1];
			VertexNormal class33_1 = class30_sub2_sub4_sub6.normals[j1];
			if (class33_1.anInt605 != 0) {
				int i2 = class30_sub2_sub4_sub6.vertexY[j1] - j;
				if (i2 <= class30_sub2_sub4_sub6_1.anInt1651) {
					int j2 = class30_sub2_sub4_sub6.vertexX[j1] - i;
					if (j2 >= class30_sub2_sub4_sub6_1.anInt1646 && j2 <= class30_sub2_sub4_sub6_1.anInt1647) {
						int k2 = class30_sub2_sub4_sub6.vertexZ[j1] - k;
						if (k2 >= class30_sub2_sub4_sub6_1.anInt1649 && k2 <= class30_sub2_sub4_sub6_1.anInt1648) {
							for (int l2 = 0; l2 < i1; l2++) {
								VertexNormal class33_2 = ((Renderable) class30_sub2_sub4_sub6_1).normals[l2];
								VertexNormal class33_3 = class30_sub2_sub4_sub6_1.normals[l2];
								if (j2 == ai[l2] && k2 == class30_sub2_sub4_sub6_1.vertexZ[l2]
										&& i2 == class30_sub2_sub4_sub6_1.vertexY[l2] && class33_3.anInt605 != 0) {
									class33.anInt602 += class33_3.anInt602;
									class33.anInt603 += class33_3.anInt603;
									class33.anInt604 += class33_3.anInt604;
									class33.anInt605 += class33_3.anInt605;
									class33_2.anInt602 += class33_1.anInt602;
									class33_2.anInt603 += class33_1.anInt603;
									class33_2.anInt604 += class33_1.anInt604;
									class33_2.anInt605 += class33_1.anInt605;
									l++;
									anIntArray486[j1] = anInt488;
									anIntArray487[l2] = anInt488;
								}
							}
						}
					}
				}
			}
		}

		if (l < 3 || !flag) {
			return;
		}
		for (int k1 = 0; k1 < class30_sub2_sub4_sub6.triangles; k1++) {
			if (anIntArray486[class30_sub2_sub4_sub6.triangleVertexX[k1]] == anInt488
					&& anIntArray486[class30_sub2_sub4_sub6.triangleVertexY[k1]] == anInt488
					&& anIntArray486[class30_sub2_sub4_sub6.triangleVertexZ[k1]] == anInt488) {
				class30_sub2_sub4_sub6.texturePoints[k1] = -1;
			}
		}

		for (int l1 = 0; l1 < class30_sub2_sub4_sub6_1.triangles; l1++) {
			if (anIntArray487[class30_sub2_sub4_sub6_1.triangleVertexX[l1]] == anInt488
					&& anIntArray487[class30_sub2_sub4_sub6_1.triangleVertexY[l1]] == anInt488
					&& anIntArray487[class30_sub2_sub4_sub6_1.triangleVertexZ[l1]] == anInt488) {
				class30_sub2_sub4_sub6_1.texturePoints[l1] = -1;
			}
		}
	}

	private void method319() {
		int j = anIntArray473[anInt447];
		SceneCluster[] clusters = Scene.clusters[anInt447];
		anInt475 = 0;
		for (int k = 0; k < j; k++) {
			SceneCluster cluster = clusters[k];
			if (cluster.anInt791 == 1) {
				int l = cluster.anInt787 - anInt453 + 25;
				if (l < 0 || l > 50) {
					continue;
				}
				int k1 = cluster.anInt789 - anInt454 + 25;
				if (k1 < 0) {
					k1 = 0;
				}
				int j2 = cluster.anInt790 - anInt454 + 25;
				if (j2 > 50) {
					j2 = 50;
				}
				boolean flag = false;
				while (k1 <= j2) {
					if (aBooleanArrayArray492[l][k1++]) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					continue;
				}
				int j3 = anInt455 - cluster.anInt792;
				if (j3 > 32) {
					cluster.anInt798 = 1;
				} else {
					if (j3 >= -32) {
						continue;
					}
					cluster.anInt798 = 2;
					j3 = -j3;
				}
				cluster.anInt801 = (cluster.anInt794 - anInt457 << 8) / j3;
				cluster.anInt802 = (cluster.anInt795 - anInt457 << 8) / j3;
				cluster.anInt803 = (cluster.anInt796 - anInt456 << 8) / j3;
				cluster.anInt804 = (cluster.anInt797 - anInt456 << 8) / j3;
				aClass47Array476[anInt475++] = cluster;
				continue;
			}
			if (cluster.anInt791 == 2) {
				int i1 = cluster.anInt789 - anInt454 + 25;
				if (i1 < 0 || i1 > 50) {
					continue;
				}
				int l1 = cluster.anInt787 - anInt453 + 25;
				if (l1 < 0) {
					l1 = 0;
				}
				int k2 = cluster.anInt788 - anInt453 + 25;
				if (k2 > 50) {
					k2 = 50;
				}
				boolean flag1 = false;
				while (l1 <= k2) {
					if (aBooleanArrayArray492[l1++][i1]) {
						flag1 = true;
						break;
					}
				}
				if (!flag1) {
					continue;
				}
				int k3 = anInt457 - cluster.anInt794;
				if (k3 > 32) {
					cluster.anInt798 = 3;
				} else {
					if (k3 >= -32) {
						continue;
					}
					cluster.anInt798 = 4;
					k3 = -k3;
				}
				cluster.anInt799 = (cluster.anInt792 - anInt455 << 8) / k3;
				cluster.anInt800 = (cluster.anInt793 - anInt455 << 8) / k3;
				cluster.anInt803 = (cluster.anInt796 - anInt456 << 8) / k3;
				cluster.anInt804 = (cluster.anInt797 - anInt456 << 8) / k3;
				aClass47Array476[anInt475++] = cluster;
			} else if (cluster.anInt791 == 4) {
				int j1 = cluster.anInt796 - anInt456;
				if (j1 > 128) {
					int i2 = cluster.anInt789 - anInt454 + 25;
					if (i2 < 0) {
						i2 = 0;
					}
					int l2 = cluster.anInt790 - anInt454 + 25;
					if (l2 > 50) {
						l2 = 50;
					}
					if (i2 <= l2) {
						int i3 = cluster.anInt787 - anInt453 + 25;
						if (i3 < 0) {
							i3 = 0;
						}
						int l3 = cluster.anInt788 - anInt453 + 25;
						if (l3 > 50) {
							l3 = 50;
						}
						boolean flag2 = false;
						label0: for (int i4 = i3; i4 <= l3; i4++) {
							for (int j4 = i2; j4 <= l2; j4++) {
								if (!aBooleanArrayArray492[i4][j4]) {
									continue;
								}
								flag2 = true;
								break label0;
							}

						}

						if (flag2) {
							cluster.anInt798 = 5;
							cluster.anInt799 = (cluster.anInt792 - anInt455 << 8) / j1;
							cluster.anInt800 = (cluster.anInt793 - anInt455 << 8) / j1;
							cluster.anInt801 = (cluster.anInt794 - anInt457 << 8) / j1;
							cluster.anInt802 = (cluster.anInt795 - anInt457 << 8) / j1;
							aClass47Array476[anInt475++] = cluster;
						}
					}
				}
			}
		}
	}

	private boolean method320(int z, int x, int y) {
		int l = anIntArrayArrayArray445[z][x][y];
		if (l == -anInt448) {
			return false;
		}
		if (l == anInt448) {
			return true;
		}
		int absX = x << 7;
		int absY = y << 7;
		if (method324(absX + 1, anIntArrayArrayArray440[z][x][y], absY + 1)
				&& method324(absX + 128 - 1, anIntArrayArrayArray440[z][x + 1][y], absY + 1)
				&& method324(absX + 128 - 1, anIntArrayArrayArray440[z][x + 1][y + 1], absY + 128 - 1)
				&& method324(absX + 1, anIntArrayArrayArray440[z][x][y + 1], absY + 128 - 1)) {
			anIntArrayArrayArray445[z][x][y] = anInt448;
			return true;
		}
		anIntArrayArrayArray445[z][x][y] = -anInt448;
		return false;
	}

	private boolean method321(int z, int x, int y, int l) {
		if (!method320(z, x, y)) {
			return false;
		}
		int i1 = x << 7;
		int j1 = y << 7;
		int k1 = anIntArrayArrayArray440[z][x][y] - 1;
		int l1 = k1 - 120;
		int i2 = k1 - 230;
		int j2 = k1 - 238;
		if (l < 16) {
			if (l == 1) {
				if (i1 > anInt455) {
					if (!method324(i1, k1, j1)) {
						return false;
					}
					if (!method324(i1, k1, j1 + 128)) {
						return false;
					}
				}
				if (z > 0) {
					if (!method324(i1, l1, j1)) {
						return false;
					}
					if (!method324(i1, l1, j1 + 128)) {
						return false;
					}
				}
				if (!method324(i1, i2, j1)) {
					return false;
				}
				return method324(i1, i2, j1 + 128);
			}
			if (l == 2) {
				if (j1 < anInt457) {
					if (!method324(i1, k1, j1 + 128)) {
						return false;
					}
					if (!method324(i1 + 128, k1, j1 + 128)) {
						return false;
					}
				}
				if (z > 0) {
					if (!method324(i1, l1, j1 + 128)) {
						return false;
					}
					if (!method324(i1 + 128, l1, j1 + 128)) {
						return false;
					}
				}
				if (!method324(i1, i2, j1 + 128)) {
					return false;
				}
				return method324(i1 + 128, i2, j1 + 128);
			}
			if (l == 4) {
				if (i1 < anInt455) {
					if (!method324(i1 + 128, k1, j1)) {
						return false;
					}
					if (!method324(i1 + 128, k1, j1 + 128)) {
						return false;
					}
				}
				if (z > 0) {
					if (!method324(i1 + 128, l1, j1)) {
						return false;
					}
					if (!method324(i1 + 128, l1, j1 + 128)) {
						return false;
					}
				}
				if (!method324(i1 + 128, i2, j1)) {
					return false;
				}
				return method324(i1 + 128, i2, j1 + 128);
			}
			if (l == 8) {
				if (j1 > anInt457) {
					if (!method324(i1, k1, j1)) {
						return false;
					}
					if (!method324(i1 + 128, k1, j1)) {
						return false;
					}
				}
				if (z > 0) {
					if (!method324(i1, l1, j1)) {
						return false;
					}
					if (!method324(i1 + 128, l1, j1)) {
						return false;
					}
				}
				if (!method324(i1, i2, j1)) {
					return false;
				}
				return method324(i1 + 128, i2, j1);
			}
		}
		if (!method324(i1 + 64, j2, j1 + 64)) {
			return false;
		}
		if (l == 16) {
			return method324(i1, i2, j1 + 128);
		}
		if (l == 32) {
			return method324(i1 + 128, i2, j1 + 128);
		}
		if (l == 64) {
			return method324(i1 + 128, i2, j1);
		}
		if (l == 128) {
			return method324(i1, i2, j1);
		}
		System.out.println("Warning unsupported wall type");
		return true;
	}

	private boolean method322(int plane, int x, int y, int l) {
		if (!method320(plane, x, y)) {
			return false;
		}
		int absoluteX = x << 7;
		int absoluteY = y << 7;
		return method324(absoluteX + 1, anIntArrayArrayArray440[plane][x][y] - l, absoluteY + 1)
				&& method324(absoluteX + 128 - 1, anIntArrayArrayArray440[plane][x + 1][y] - l, absoluteY + 1)
				&& method324(absoluteX + 128 - 1, anIntArrayArrayArray440[plane][x + 1][y + 1] - l, absoluteY + 128 - 1)
				&& method324(absoluteX + 1, anIntArrayArrayArray440[plane][x][y + 1] - l, absoluteY + 128 - 1);
	}

	private boolean method323(int plane, int minX, int maxX, int minY, int maxY, int j1) {
		if (minX == maxX && minY == maxY) {
			if (!method320(plane, minX, minY)) {
				return false;
			}
			int absoluteX = minX << 7;
			int absoluteY = minY << 7;
			return method324(absoluteX + 1, anIntArrayArrayArray440[plane][minX][minY] - j1, absoluteY + 1)
					&& method324(absoluteX + 128 - 1, anIntArrayArrayArray440[plane][minX + 1][minY] - j1, absoluteY + 1)
					&& method324(absoluteX + 128 - 1, anIntArrayArrayArray440[plane][minX + 1][minY + 1] - j1,
							absoluteY + 128 - 1)
					&& method324(absoluteX + 1, anIntArrayArrayArray440[plane][minX][minY + 1] - j1, absoluteY + 128 - 1);
		}
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				if (anIntArrayArrayArray445[plane][x][y] == -anInt448) {
					return false;
				}
			}
		}

		int k2 = (minX << 7) + 1;
		int l2 = (minY << 7) + 2;
		int i3 = anIntArrayArrayArray440[plane][minX][minY] - j1;
		if (!method324(k2, i3, l2)) {
			return false;
		}
		int j3 = (maxX << 7) - 1;
		if (!method324(j3, i3, l2)) {
			return false;
		}
		int k3 = (maxY << 7) - 1;
		if (!method324(k2, i3, k3)) {
			return false;
		}
		return method324(j3, i3, k3);
	}

	private boolean method324(int i, int j, int k) {
		for (int l = 0; l < anInt475; l++) {
			SceneCluster cluster = aClass47Array476[l];
			if (cluster.anInt798 == 1) {
				int i1 = cluster.anInt792 - i;
				if (i1 > 0) {
					int j2 = cluster.anInt794 + (cluster.anInt801 * i1 >> 8);
					int k3 = cluster.anInt795 + (cluster.anInt802 * i1 >> 8);
					int l4 = cluster.anInt796 + (cluster.anInt803 * i1 >> 8);
					int i6 = cluster.anInt797 + (cluster.anInt804 * i1 >> 8);
					if (k >= j2 && k <= k3 && j >= l4 && j <= i6) {
						return true;
					}
				}
			} else if (cluster.anInt798 == 2) {
				int j1 = i - cluster.anInt792;
				if (j1 > 0) {
					int k2 = cluster.anInt794 + (cluster.anInt801 * j1 >> 8);
					int l3 = cluster.anInt795 + (cluster.anInt802 * j1 >> 8);
					int i5 = cluster.anInt796 + (cluster.anInt803 * j1 >> 8);
					int j6 = cluster.anInt797 + (cluster.anInt804 * j1 >> 8);
					if (k >= k2 && k <= l3 && j >= i5 && j <= j6) {
						return true;
					}
				}
			} else if (cluster.anInt798 == 3) {
				int k1 = cluster.anInt794 - k;
				if (k1 > 0) {
					int l2 = cluster.anInt792 + (cluster.anInt799 * k1 >> 8);
					int i4 = cluster.anInt793 + (cluster.anInt800 * k1 >> 8);
					int j5 = cluster.anInt796 + (cluster.anInt803 * k1 >> 8);
					int k6 = cluster.anInt797 + (cluster.anInt804 * k1 >> 8);
					if (i >= l2 && i <= i4 && j >= j5 && j <= k6) {
						return true;
					}
				}
			} else if (cluster.anInt798 == 4) {
				int l1 = k - cluster.anInt794;
				if (l1 > 0) {
					int i3 = cluster.anInt792 + (cluster.anInt799 * l1 >> 8);
					int j4 = cluster.anInt793 + (cluster.anInt800 * l1 >> 8);
					int k5 = cluster.anInt796 + (cluster.anInt803 * l1 >> 8);
					int l6 = cluster.anInt797 + (cluster.anInt804 * l1 >> 8);
					if (i >= i3 && i <= j4 && j >= k5 && j <= l6) {
						return true;
					}
				}
			} else if (cluster.anInt798 == 5) {
				int i2 = j - cluster.anInt796;
				if (i2 > 0) {
					int j3 = cluster.anInt792 + (cluster.anInt799 * i2 >> 8);
					int k4 = cluster.anInt793 + (cluster.anInt800 * i2 >> 8);
					int l5 = cluster.anInt794 + (cluster.anInt801 * i2 >> 8);
					int i7 = cluster.anInt795 + (cluster.anInt802 * i2 >> 8);
					if (i >= j3 && i <= k4 && k >= l5 && k <= i7) {
						return true;
					}
				}
			}
		}
		return false;
	}

}