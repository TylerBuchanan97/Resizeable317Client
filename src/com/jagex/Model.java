package com.jagex;

public class Model extends Renderable {

	// Class30_Sub2_Sub4_Sub6

	public static boolean aBoolean1684;
	public static int anInt1685;
	public static int anInt1686;
	public static int anInt1687;
	public static int[] anIntArray1688 = new int[1000];
	public static int[] COSINE;
	public static Model EMPTY_MODEL = new Model();
	public static int[] SINE;
	static boolean[] aBooleanArray1663 = new boolean[4096];
	static boolean[] aBooleanArray1664 = new boolean[4096];
	static int[] anIntArray1665 = new int[4096];
	static int[] anIntArray1666 = new int[4096];
	static int[] anIntArray1667 = new int[4096];
	static int[] anIntArray1668 = new int[4096];
	static int[] anIntArray1669 = new int[4096];
	static int[] anIntArray1670 = new int[4096];
	static int[] anIntArray1671 = new int[1500];
	static int[] anIntArray1673 = new int[12];
	static int[] anIntArray1675 = new int[2000];
	static int[] anIntArray1676 = new int[2000];
	static int[] anIntArray1677 = new int[12];
	static int[] anIntArray1678 = new int[10];
	static int[] anIntArray1679 = new int[10];
	static int[] anIntArray1680 = new int[10];
	static int[] anIntArray1691;
	static int[] anIntArray1692;
	static int[][] anIntArrayArray1672 = new int[1500][512];
	static int[][] anIntArrayArray1674 = new int[12][2000];
	static int centroidX;
	static int centroidY;
	static int centroidZ;
	static ModelHeader[] headers;
	static Provider provider;
	private static int[] anIntArray1622 = new int[2000];
	private static int[] anIntArray1623 = new int[2000];
	private static int[] anIntArray1624 = new int[2000];
	private static int[] anIntArray1625 = new int[2000];
	static {
		SINE = Rasterizer.SINE;
		COSINE = Rasterizer.COSINE;
		anIntArray1691 = Rasterizer.anIntArray1482;
		anIntArray1692 = Rasterizer.anIntArray1469;
	}

	public static void clear(int id) {
		headers[id] = null;
	}

	public static void dispose() {
		headers = null;
		aBooleanArray1663 = null;
		aBooleanArray1664 = null;
		anIntArray1665 = null;
		anIntArray1666 = null;
		anIntArray1667 = null;
		anIntArray1668 = null;
		anIntArray1669 = null;
		anIntArray1670 = null;
		anIntArray1671 = null;
		anIntArrayArray1672 = null;
		anIntArray1673 = null;
		anIntArrayArray1674 = null;
		anIntArray1675 = null;
		anIntArray1676 = null;
		anIntArray1677 = null;
		SINE = null;
		COSINE = null;
		anIntArray1691 = null;
		anIntArray1692 = null;
	}

	public static void init(int count, Provider provider) {
		headers = new ModelHeader[count];
		Model.provider = provider;
	}

	public static void load(byte[] data, int id) {
		if (data == null) {
			ModelHeader header = headers[id] = new ModelHeader();
			header.vertices = 0;
			header.triangles = 0;
			header.texturedTriangles = 0;
			return;
		}

		Buffer buffer = new Buffer(data);
		buffer.position = data.length - 18;
		ModelHeader header = headers[id] = new ModelHeader();
		header.data = data;
		header.vertices = buffer.readUShort();
		header.triangles = buffer.readUShort();
		header.texturedTriangles = buffer.readUByte();
		int useTextures = buffer.readUByte();
		int useTrianglePriority = buffer.readUByte();
		int useTransparency = buffer.readUByte();
		int useTriangleSkinning = buffer.readUByte();
		int useVertexSkinning = buffer.readUByte();
		int xDataOffset = buffer.readUShort();
		int yDataOffset = buffer.readUShort();
		int zDataOffset = buffer.readUShort();
		int triangleDataLength = buffer.readUShort();
		int offset = 0;
		header.vertexDirectionOffset = offset;
		offset += header.vertices;
		header.triangleTypeOffset = offset;
		offset += header.triangles;
		header.trianglePriorityOffset = offset;

		if (useTrianglePriority == 255) {
			offset += header.triangles;
		} else {
			header.trianglePriorityOffset = -useTrianglePriority - 1;
		}

		header.triangleSkinOffset = offset;
		if (useTriangleSkinning == 1) {
			offset += header.triangles;
		} else {
			header.triangleSkinOffset = -1;
		}

		header.texturePointerOffset = offset;
		if (useTextures == 1) {
			offset += header.triangles;
		} else {
			header.texturePointerOffset = -1;
		}

		header.vertexSkinOffset = offset;
		if (useVertexSkinning == 1) {
			offset += header.vertices;
		} else {
			header.vertexSkinOffset = -1;
		}

		header.triangleAlphaOffset = offset;
		if (useTransparency == 1) {
			offset += header.triangles;
		} else {
			header.triangleAlphaOffset = -1;
		}

		header.triangleDataOffset = offset;
		offset += triangleDataLength;
		header.colourDataOffset = offset;
		offset += header.triangles * 2;
		header.uvMapTriangleOffset = offset;
		offset += header.texturedTriangles * 6;
		header.xDataOffset = offset;
		offset += xDataOffset;
		header.yDataOffset = offset;
		offset += yDataOffset;
		header.zDataOffset = offset;
		offset += zDataOffset;
	}

	public static boolean loaded(int id) {
		if (headers == null) {
			return false;
		}

		ModelHeader header = headers[id];
		if (header == null) {
			provider.provide(id);
			return false;
		}
		return true;
	}

	public static Model lookup(int id) {
		if (headers == null) {
			return null;
		}

		ModelHeader header = headers[id];
		if (header == null) {
			provider.provide(id);
			return null;
		}
		return new Model(id);
	}

	public static final int method481(int i, int j, int k) {
		if ((k & 2) == 2) {
			if (j < 0) {
				j = 0;
			} else if (j > 127) {
				j = 127;
			}
			return 127 - j;
		}

		j = j * (i & 0x7f) >> 7;
		if (j < 2) {
			j = 2;
		} else if (j > 126) {
			j = 126;
		}
		return (i & 0xff80) + j;
	}

	public boolean aBoolean1659;
	public int anInt1641;
	public int anInt1646;
	public int anInt1647;
	public int anInt1648;
	public int anInt1649;
	public int anInt1650;
	public int anInt1651;
	public int anInt1652;
	public int anInt1653;
	public int anInt1654;
	public int anIntArray1634[];
	public int anIntArray1635[];
	public int anIntArray1636[];
	public int faceAlphas[];
	public int faceGroups[][];
	public int tecturedTriangleVertexX[];
	public int tecturedTriangleVertexY[];
	public int tecturedTriangleVertexZ[];
	public int texturedTriangles;
	public int texturePoints[];
	public int triangleColours[];
	public int trianglePriorities[];
	public int triangles;
	public int triangleSkinValues[];
	public int triangleVertexX[];
	public int triangleVertexY[];
	public int triangleVertexZ[];
	public int vertexGroups[][];
	public int vertexSkins[];
	public int vertexX[];
	public int vertexY[];
	public int vertexZ[];
	public int vertices;
	VertexNormal normals[];
	private int anInt1617;

	public Model(boolean shallowTriangleColoursCopy, boolean shallowTriangleAlphaCopy, boolean shallowVertexCopy, Model model) {
		anInt1617 = 1;
		aBoolean1659 = false;
		vertices = model.vertices;
		triangles = model.triangles;
		texturedTriangles = model.texturedTriangles;
		if (shallowVertexCopy) {
			vertexX = model.vertexX;
			vertexY = model.vertexY;
			vertexZ = model.vertexZ;
		} else {
			vertexX = new int[vertices];
			vertexY = new int[vertices];
			vertexZ = new int[vertices];
			for (int j = 0; j < vertices; j++) {
				vertexX[j] = model.vertexX[j];
				vertexY[j] = model.vertexY[j];
				vertexZ[j] = model.vertexZ[j];
			}

		}
		if (shallowTriangleColoursCopy) {
			triangleColours = model.triangleColours;
		} else {
			triangleColours = new int[triangles];
			for (int k = 0; k < triangles; k++) {
				triangleColours[k] = model.triangleColours[k];
			}

		}
		if (shallowTriangleAlphaCopy) {
			faceAlphas = model.faceAlphas;
		} else {
			faceAlphas = new int[triangles];
			if (model.faceAlphas == null) {
				for (int l = 0; l < triangles; l++) {
					faceAlphas[l] = 0;
				}

			} else {
				for (int i1 = 0; i1 < triangles; i1++) {
					faceAlphas[i1] = model.faceAlphas[i1];
				}

			}
		}
		vertexSkins = model.vertexSkins;
		triangleSkinValues = model.triangleSkinValues;
		texturePoints = model.texturePoints;
		triangleVertexX = model.triangleVertexX;
		triangleVertexY = model.triangleVertexY;
		triangleVertexZ = model.triangleVertexZ;
		trianglePriorities = model.trianglePriorities;
		anInt1641 = model.anInt1641;
		tecturedTriangleVertexX = model.tecturedTriangleVertexX;
		tecturedTriangleVertexY = model.tecturedTriangleVertexY;
		tecturedTriangleVertexZ = model.tecturedTriangleVertexZ;
	}

	public Model(boolean flag, boolean flag1, Model model) {
		anInt1617 = 1;
		aBoolean1659 = false;
		vertices = model.vertices;
		triangles = model.triangles;
		texturedTriangles = model.texturedTriangles;
		if (flag) {
			vertexY = new int[vertices];
			for (int j = 0; j < vertices; j++) {
				vertexY[j] = model.vertexY[j];
			}

		} else {
			vertexY = model.vertexY;
		}
		if (flag1) {
			anIntArray1634 = new int[triangles];
			anIntArray1635 = new int[triangles];
			anIntArray1636 = new int[triangles];
			for (int k = 0; k < triangles; k++) {
				anIntArray1634[k] = model.anIntArray1634[k];
				anIntArray1635[k] = model.anIntArray1635[k];
				anIntArray1636[k] = model.anIntArray1636[k];
			}

			texturePoints = new int[triangles];
			if (model.texturePoints == null) {
				for (int l = 0; l < triangles; l++) {
					texturePoints[l] = 0;
				}

			} else {
				for (int i1 = 0; i1 < triangles; i1++) {
					texturePoints[i1] = model.texturePoints[i1];
				}

			}
			super.normals = new VertexNormal[vertices];
			for (int j1 = 0; j1 < vertices; j1++) {
				VertexNormal class33 = super.normals[j1] = new VertexNormal();
				VertexNormal class33_1 = ((Renderable) model).normals[j1];
				class33.anInt602 = class33_1.anInt602;
				class33.anInt603 = class33_1.anInt603;
				class33.anInt604 = class33_1.anInt604;
				class33.anInt605 = class33_1.anInt605;
			}

			normals = model.normals;
		} else {
			anIntArray1634 = model.anIntArray1634;
			anIntArray1635 = model.anIntArray1635;
			anIntArray1636 = model.anIntArray1636;
			texturePoints = model.texturePoints;
		}
		vertexX = model.vertexX;
		vertexZ = model.vertexZ;
		triangleColours = model.triangleColours;
		faceAlphas = model.faceAlphas;
		trianglePriorities = model.trianglePriorities;
		anInt1641 = model.anInt1641;
		triangleVertexX = model.triangleVertexX;
		triangleVertexY = model.triangleVertexY;
		triangleVertexZ = model.triangleVertexZ;
		tecturedTriangleVertexX = model.tecturedTriangleVertexX;
		tecturedTriangleVertexY = model.tecturedTriangleVertexY;
		tecturedTriangleVertexZ = model.tecturedTriangleVertexZ;
		super.modelHeight = ((Renderable) model).modelHeight;
		anInt1650 = model.anInt1650;
		anInt1653 = model.anInt1653;
		anInt1652 = model.anInt1652;
		anInt1646 = model.anInt1646;
		anInt1648 = model.anInt1648;
		anInt1649 = model.anInt1649;
		anInt1647 = model.anInt1647;
	}

	public Model(int modelCount, Model[] models) {
		anInt1617 = 1;
		aBoolean1659 = false;
		boolean flag = false;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		vertices = 0;
		triangles = 0;
		texturedTriangles = 0;
		anInt1641 = -1;
		for (int k = 0; k < modelCount; k++) {
			Model model = models[k];
			if (model != null) {
				vertices += model.vertices;
				triangles += model.triangles;
				texturedTriangles += model.texturedTriangles;
				flag |= model.texturePoints != null;
				if (model.trianglePriorities != null) {
					flag1 = true;
				} else {
					if (anInt1641 == -1) {
						anInt1641 = model.anInt1641;
					}
					if (anInt1641 != model.anInt1641) {
						flag1 = true;
					}
				}
				flag2 |= model.faceAlphas != null;
				flag3 |= model.triangleSkinValues != null;
			}
		}

		vertexX = new int[vertices];
		vertexY = new int[vertices];
		vertexZ = new int[vertices];
		vertexSkins = new int[vertices];
		triangleVertexX = new int[triangles];
		triangleVertexY = new int[triangles];
		triangleVertexZ = new int[triangles];
		tecturedTriangleVertexX = new int[texturedTriangles];
		tecturedTriangleVertexY = new int[texturedTriangles];
		tecturedTriangleVertexZ = new int[texturedTriangles];
		if (flag) {
			texturePoints = new int[triangles];
		}
		if (flag1) {
			trianglePriorities = new int[triangles];
		}
		if (flag2) {
			faceAlphas = new int[triangles];
		}
		if (flag3) {
			triangleSkinValues = new int[triangles];
		}
		triangleColours = new int[triangles];
		vertices = 0;
		triangles = 0;
		texturedTriangles = 0;
		int l = 0;
		for (int i = 0; i < modelCount; i++) {
			Model model = models[i];
			if (model != null) {
				for (int j1 = 0; j1 < model.triangles; j1++) {
					if (flag) {
						if (model.texturePoints == null) {
							texturePoints[triangles] = 0;
						} else {
							int k1 = model.texturePoints[j1];
							if ((k1 & 2) == 2) {
								k1 += l << 2;
							}
							texturePoints[triangles] = k1;
						}
					}
					if (flag1) {
						if (model.trianglePriorities == null) {
							trianglePriorities[triangles] = model.anInt1641;
						} else {
							trianglePriorities[triangles] = model.trianglePriorities[j1];
						}
					}
					if (flag2) {
						if (model.faceAlphas == null) {
							faceAlphas[triangles] = 0;
						} else {
							faceAlphas[triangles] = model.faceAlphas[j1];
						}
					}
					if (flag3 && model.triangleSkinValues != null) {
						triangleSkinValues[triangles] = model.triangleSkinValues[j1];
					}
					triangleColours[triangles] = model.triangleColours[j1];
					triangleVertexX[triangles] = method465(model, model.triangleVertexX[j1]);
					triangleVertexY[triangles] = method465(model, model.triangleVertexY[j1]);
					triangleVertexZ[triangles] = method465(model, model.triangleVertexZ[j1]);
					triangles++;
				}

				for (int l1 = 0; l1 < model.texturedTriangles; l1++) {
					tecturedTriangleVertexX[texturedTriangles] = method465(model, model.tecturedTriangleVertexX[l1]);
					tecturedTriangleVertexY[texturedTriangles] = method465(model, model.tecturedTriangleVertexY[l1]);
					tecturedTriangleVertexZ[texturedTriangles] = method465(model, model.tecturedTriangleVertexZ[l1]);
					texturedTriangles++;
				}

				l += model.texturedTriangles;
			}
		}
	}

	public Model(Model[] models, int modelCount) {
		anInt1617 = 1;
		aBoolean1659 = false;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;
		vertices = 0;
		triangles = 0;
		texturedTriangles = 0;
		anInt1641 = -1;
		for (int k = 0; k < modelCount; k++) {
			Model model = models[k];
			if (model != null) {
				vertices += model.vertices;
				triangles += model.triangles;
				texturedTriangles += model.texturedTriangles;
				flag1 |= model.texturePoints != null;
				if (model.trianglePriorities != null) {
					flag2 = true;
				} else {
					if (anInt1641 == -1) {
						anInt1641 = model.anInt1641;
					}
					if (anInt1641 != model.anInt1641) {
						flag2 = true;
					}
				}
				flag3 |= model.faceAlphas != null;
				flag4 |= model.triangleColours != null;
			}
		}

		vertexX = new int[vertices];
		vertexY = new int[vertices];
		vertexZ = new int[vertices];
		triangleVertexX = new int[triangles];
		triangleVertexY = new int[triangles];
		triangleVertexZ = new int[triangles];
		anIntArray1634 = new int[triangles];
		anIntArray1635 = new int[triangles];
		anIntArray1636 = new int[triangles];
		tecturedTriangleVertexX = new int[texturedTriangles];
		tecturedTriangleVertexY = new int[texturedTriangles];
		tecturedTriangleVertexZ = new int[texturedTriangles];
		if (flag1) {
			texturePoints = new int[triangles];
		}
		if (flag2) {
			trianglePriorities = new int[triangles];
		}
		if (flag3) {
			faceAlphas = new int[triangles];
		}
		if (flag4) {
			triangleColours = new int[triangles];
		}
		vertices = 0;
		triangles = 0;
		texturedTriangles = 0;
		int i1 = 0;
		for (int j1 = 0; j1 < modelCount; j1++) {
			Model model = models[j1];
			if (model != null) {
				int k1 = vertices;
				for (int l1 = 0; l1 < model.vertices; l1++) {
					vertexX[vertices] = model.vertexX[l1];
					vertexY[vertices] = model.vertexY[l1];
					vertexZ[vertices] = model.vertexZ[l1];
					vertices++;
				}

				for (int i2 = 0; i2 < model.triangles; i2++) {
					triangleVertexX[triangles] = model.triangleVertexX[i2] + k1;
					triangleVertexY[triangles] = model.triangleVertexY[i2] + k1;
					triangleVertexZ[triangles] = model.triangleVertexZ[i2] + k1;
					anIntArray1634[triangles] = model.anIntArray1634[i2];
					anIntArray1635[triangles] = model.anIntArray1635[i2];
					anIntArray1636[triangles] = model.anIntArray1636[i2];
					if (flag1) {
						if (model.texturePoints == null) {
							texturePoints[triangles] = 0;
						} else {
							int j2 = model.texturePoints[i2];
							if ((j2 & 2) == 2) {
								j2 += i1 << 2;
							}
							texturePoints[triangles] = j2;
						}
					}
					if (flag2) {
						if (model.trianglePriorities == null) {
							trianglePriorities[triangles] = model.anInt1641;
						} else {
							trianglePriorities[triangles] = model.trianglePriorities[i2];
						}
					}
					if (flag3) {
						if (model.faceAlphas == null) {
							faceAlphas[triangles] = 0;
						} else {
							faceAlphas[triangles] = model.faceAlphas[i2];
						}
					}
					if (flag4 && model.triangleColours != null) {
						triangleColours[triangles] = model.triangleColours[i2];
					}
					triangles++;
				}

				for (int k2 = 0; k2 < model.texturedTriangles; k2++) {
					tecturedTriangleVertexX[texturedTriangles] = model.tecturedTriangleVertexX[k2] + k1;
					tecturedTriangleVertexY[texturedTriangles] = model.tecturedTriangleVertexY[k2] + k1;
					tecturedTriangleVertexZ[texturedTriangles] = model.tecturedTriangleVertexZ[k2] + k1;
					texturedTriangles++;
				}

				i1 += model.texturedTriangles;
			}
		}

		method466();
	}

	private Model() {
		anInt1617 = 1;
		aBoolean1659 = false;
	}

	private Model(int id) {
		anInt1617 = 1;
		aBoolean1659 = false;
		ModelHeader header = headers[id];
		vertices = header.vertices;
		triangles = header.triangles;
		texturedTriangles = header.texturedTriangles;
		vertexX = new int[vertices];
		vertexY = new int[vertices];
		vertexZ = new int[vertices];
		triangleVertexX = new int[triangles];
		triangleVertexY = new int[triangles];
		triangleVertexZ = new int[triangles];
		tecturedTriangleVertexX = new int[texturedTriangles];
		tecturedTriangleVertexY = new int[texturedTriangles];
		tecturedTriangleVertexZ = new int[texturedTriangles];
		if (header.vertexSkinOffset >= 0) {
			vertexSkins = new int[vertices];
		}
		if (header.texturePointerOffset >= 0) {
			texturePoints = new int[triangles];
		}
		if (header.trianglePriorityOffset >= 0) {
			trianglePriorities = new int[triangles];
		} else {
			anInt1641 = -header.trianglePriorityOffset - 1;
		}
		if (header.triangleAlphaOffset >= 0) {
			faceAlphas = new int[triangles];
		}
		if (header.triangleSkinOffset >= 0) {
			triangleSkinValues = new int[triangles];
		}
		triangleColours = new int[triangles];
		Buffer one = new Buffer(header.data);
		one.position = header.vertexDirectionOffset;
		Buffer two = new Buffer(header.data);
		two.position = header.xDataOffset;
		Buffer three = new Buffer(header.data);
		three.position = header.yDataOffset;
		Buffer four = new Buffer(header.data);
		four.position = header.zDataOffset;
		Buffer vertexSkin = new Buffer(header.data);
		vertexSkin.position = header.vertexSkinOffset;
		int baseOffsetX = 0;
		int baseOffsetY = 0;
		int baseOffsetZ = 0;
		for (int i = 0; i < vertices; i++) {
			int mask = one.readUByte();
			int x = 0;
			if ((mask & 1) != 0) {
				x = two.readSmarts();
			}
			int y = 0;
			if ((mask & 2) != 0) {
				y = three.readSmarts();
			}
			int z = 0;
			if ((mask & 4) != 0) {
				z = four.readSmarts();
			}
			vertexX[i] = baseOffsetX + x;
			vertexY[i] = baseOffsetY + y;
			vertexZ[i] = baseOffsetZ + z;
			baseOffsetX = vertexX[i];
			baseOffsetY = vertexY[i];
			baseOffsetZ = vertexZ[i];
			if (vertexSkins != null) {
				vertexSkins[i] = vertexSkin.readUByte();
			}
		}

		one.position = header.colourDataOffset;
		two.position = header.texturePointerOffset;
		three.position = header.trianglePriorityOffset;
		four.position = header.triangleAlphaOffset;
		vertexSkin.position = header.triangleSkinOffset;
		for (int i = 0; i < triangles; i++) {
			triangleColours[i] = one.readUShort();
			if (texturePoints != null) {
				texturePoints[i] = two.readUByte();
			}
			if (trianglePriorities != null) {
				trianglePriorities[i] = three.readUByte();
			}
			if (faceAlphas != null) {
				faceAlphas[i] = four.readUByte();
			}
			if (triangleSkinValues != null) {
				triangleSkinValues[i] = vertexSkin.readUByte();
			}
		}

		one.position = header.triangleDataOffset;
		two.position = header.triangleTypeOffset;
		int trianglePointOffsetX = 0;
		int trianglePointOffsetY = 0;
		int trianglePointOffsetZ = 0;
		int offset = 0;
		for (int i = 0; i < triangles; i++) {
			int type = two.readUByte();
			if (type == 1) {
				trianglePointOffsetX = one.readSmarts() + offset;
				offset = trianglePointOffsetX;
				trianglePointOffsetY = one.readSmarts() + offset;
				offset = trianglePointOffsetY;
				trianglePointOffsetZ = one.readSmarts() + offset;
				offset = trianglePointOffsetZ;
				triangleVertexX[i] = trianglePointOffsetX;
				triangleVertexY[i] = trianglePointOffsetY;
				triangleVertexZ[i] = trianglePointOffsetZ;
			}
			if (type == 2) {
				trianglePointOffsetY = trianglePointOffsetZ;
				trianglePointOffsetZ = one.readSmarts() + offset;
				offset = trianglePointOffsetZ;
				triangleVertexX[i] = trianglePointOffsetX;
				triangleVertexY[i] = trianglePointOffsetY;
				triangleVertexZ[i] = trianglePointOffsetZ;
			}
			if (type == 3) {
				trianglePointOffsetX = trianglePointOffsetZ;
				trianglePointOffsetZ = one.readSmarts() + offset;
				offset = trianglePointOffsetZ;
				triangleVertexX[i] = trianglePointOffsetX;
				triangleVertexY[i] = trianglePointOffsetY;
				triangleVertexZ[i] = trianglePointOffsetZ;
			}
			if (type == 4) {
				int k4 = trianglePointOffsetX;
				trianglePointOffsetX = trianglePointOffsetY;
				trianglePointOffsetY = k4;
				trianglePointOffsetZ = one.readSmarts() + offset;
				offset = trianglePointOffsetZ;
				triangleVertexX[i] = trianglePointOffsetX;
				triangleVertexY[i] = trianglePointOffsetY;
				triangleVertexZ[i] = trianglePointOffsetZ;
			}
		}

		one.position = header.uvMapTriangleOffset;
		for (int j4 = 0; j4 < texturedTriangles; j4++) {
			tecturedTriangleVertexX[j4] = one.readUShort();
			tecturedTriangleVertexY[j4] = one.readUShort();
			tecturedTriangleVertexZ[j4] = one.readUShort();
		}
	}

	public void apply(int frame) {
		if (vertexGroups == null) {
			return;
		}

		if (frame == -1) {
			return;
		}

		Frame animation = Frame.lookup(frame);
		if (animation == null) {
			return;
		}

		FrameBase base = animation.base;
		centroidX = 0;
		centroidY = 0;
		centroidZ = 0;
		for (int i = 0; i < animation.transformationCount; i++) {
			int index = animation.translationIndices[i];
			transform(base.transformationType[index], base.groups[index], animation.transformX[i], animation.transformY[i],
					animation.transformZ[i]);
		}
	}

	public void apply(int primaryFrame, int secondaryFrame, int[] interleaveOrder) {
		if (primaryFrame == -1) {
			return;
		} else if (interleaveOrder == null || secondaryFrame == -1) {
			apply(primaryFrame);
			return;
		}

		Frame primary = Frame.lookup(primaryFrame);
		if (primary == null) {
			return;
		}

		Frame secondary = Frame.lookup(secondaryFrame);
		if (secondary == null) {
			apply(primaryFrame);
			return;
		}
		FrameBase skins = primary.base;
		centroidX = 0;
		centroidY = 0;
		centroidZ = 0;
		int l = 0;
		int i1 = interleaveOrder[l++];
		for (int i = 0; i < primary.transformationCount; i++) {
			int k1;
			for (k1 = primary.translationIndices[i]; k1 > i1; i1 = interleaveOrder[l++]) {

			}
			if (k1 != i1 || skins.transformationType[k1] == 0) {
				transform(skins.transformationType[k1], skins.groups[k1], primary.transformX[i], primary.transformY[i],
						primary.transformZ[i]);
			}
		}

		centroidX = 0;
		centroidY = 0;
		centroidZ = 0;
		l = 0;
		i1 = interleaveOrder[l++];
		for (int l1 = 0; l1 < secondary.transformationCount; l1++) {
			int i2;
			for (i2 = secondary.translationIndices[l1]; i2 > i1; i1 = interleaveOrder[l++]) {

			}
			if (i2 == i1 || skins.transformationType[i2] == 0) {
				transform(skins.transformationType[i2], skins.groups[i2], secondary.transformX[l1], secondary.transformY[l1],
						secondary.transformZ[l1]);
			}
		}
	}

	public void computeSphericalBounds() {
		super.modelHeight = 0;
		anInt1651 = 0;
		for (int i = 0; i < vertices; i++) {
			int y = vertexY[i];
			if (-y > super.modelHeight) {
				super.modelHeight = -y;
			}
			if (y > anInt1651) {
				anInt1651 = y;
			}
		}

		anInt1653 = (int) (Math.sqrt(anInt1650 * anInt1650 + super.modelHeight * super.modelHeight) + 0.98999999999999999D);
		anInt1652 = anInt1653 + (int) (Math.sqrt(anInt1650 * anInt1650 + anInt1651 * anInt1651) + 0.98999999999999999D);
	}

	public void invert() {
		for (int j = 0; j < vertices; j++) {
			vertexZ[j] = -vertexZ[j];
		}

		for (int k = 0; k < triangles; k++) {
			int l = triangleVertexX[k];
			triangleVertexX[k] = triangleVertexZ[k];
			triangleVertexZ[k] = l;
		}
	}

	public final void light(int brightness, int shadow, int k, int l, int i1, boolean flag) {
		int j1 = (int) Math.sqrt(k * k + l * l + i1 * i1);
		int k1 = shadow * j1 >> 8;
		if (anIntArray1634 == null) {
			anIntArray1634 = new int[triangles];
			anIntArray1635 = new int[triangles];
			anIntArray1636 = new int[triangles];
		}
		if (super.normals == null) {
			super.normals = new VertexNormal[vertices];
			for (int l1 = 0; l1 < vertices; l1++) {
				super.normals[l1] = new VertexNormal();
			}

		}
		for (int i2 = 0; i2 < triangles; i2++) {
			int j2 = triangleVertexX[i2];
			int l2 = triangleVertexY[i2];
			int i3 = triangleVertexZ[i2];
			int j3 = vertexX[l2] - vertexX[j2];
			int k3 = vertexY[l2] - vertexY[j2];
			int l3 = vertexZ[l2] - vertexZ[j2];
			int i4 = vertexX[i3] - vertexX[j2];
			int j4 = vertexY[i3] - vertexY[j2];
			int k4 = vertexZ[i3] - vertexZ[j2];
			int l4 = k3 * k4 - j4 * l3;
			int i5 = l3 * i4 - k4 * j3;
			int j5;
			for (j5 = j3 * j4 - i4 * k3; l4 > 8192 || i5 > 8192 || j5 > 8192 || l4 < -8192 || i5 < -8192 || j5 < -8192; j5 >>= 1) {
				l4 >>= 1;
				i5 >>= 1;
			}

			int k5 = (int) Math.sqrt(l4 * l4 + i5 * i5 + j5 * j5);
			if (k5 <= 0) {
				k5 = 1;
			}
			l4 = l4 * 256 / k5;
			i5 = i5 * 256 / k5;
			j5 = j5 * 256 / k5;
			if (texturePoints == null || (texturePoints[i2] & 1) == 0) {
				VertexNormal vertex = super.normals[j2];
				vertex.anInt602 += l4;
				vertex.anInt603 += i5;
				vertex.anInt604 += j5;
				vertex.anInt605++;
				vertex = super.normals[l2];
				vertex.anInt602 += l4;
				vertex.anInt603 += i5;
				vertex.anInt604 += j5;
				vertex.anInt605++;
				vertex = super.normals[i3];
				vertex.anInt602 += l4;
				vertex.anInt603 += i5;
				vertex.anInt604 += j5;
				vertex.anInt605++;
			} else {
				int l5 = brightness + (k * l4 + l * i5 + i1 * j5) / (k1 + k1 / 2);
				anIntArray1634[i2] = method481(triangleColours[i2], l5, texturePoints[i2]);
			}
		}

		if (flag) {
			method480(brightness, k1, k, l, i1);
		} else {
			normals = new VertexNormal[vertices];
			for (int k2 = 0; k2 < vertices; k2++) {
				VertexNormal class33 = super.normals[k2];
				VertexNormal class33_1 = normals[k2] = new VertexNormal();
				class33_1.anInt602 = class33.anInt602;
				class33_1.anInt603 = class33.anInt603;
				class33_1.anInt604 = class33.anInt604;
				class33_1.anInt605 = class33.anInt605;
			}

		}
		if (flag) {
			method466();
		} else {
			method468();
		}
	}

	public void method464(Model model, boolean invalidFrames) {
		vertices = model.vertices;
		triangles = model.triangles;
		texturedTriangles = model.texturedTriangles;
		if (anIntArray1622.length < vertices) {
			anIntArray1622 = new int[vertices + 100];
			anIntArray1623 = new int[vertices + 100];
			anIntArray1624 = new int[vertices + 100];
		}
		vertexX = anIntArray1622;
		vertexY = anIntArray1623;
		vertexZ = anIntArray1624;
		for (int k = 0; k < vertices; k++) {
			vertexX[k] = model.vertexX[k];
			vertexY[k] = model.vertexY[k];
			vertexZ[k] = model.vertexZ[k];
		}

		if (invalidFrames) {
			faceAlphas = model.faceAlphas;
		} else {
			if (anIntArray1625.length < triangles) {
				anIntArray1625 = new int[triangles + 100];
			}
			faceAlphas = anIntArray1625;
			if (model.faceAlphas == null) {
				for (int l = 0; l < triangles; l++) {
					faceAlphas[l] = 0;
				}

			} else {
				for (int i1 = 0; i1 < triangles; i1++) {
					faceAlphas[i1] = model.faceAlphas[i1];
				}

			}
		}
		texturePoints = model.texturePoints;
		triangleColours = model.triangleColours;
		trianglePriorities = model.trianglePriorities;
		anInt1641 = model.anInt1641;
		faceGroups = model.faceGroups;
		vertexGroups = model.vertexGroups;
		triangleVertexX = model.triangleVertexX;
		triangleVertexY = model.triangleVertexY;
		triangleVertexZ = model.triangleVertexZ;
		anIntArray1634 = model.anIntArray1634;
		anIntArray1635 = model.anIntArray1635;
		anIntArray1636 = model.anIntArray1636;
		tecturedTriangleVertexX = model.tecturedTriangleVertexX;
		tecturedTriangleVertexY = model.tecturedTriangleVertexY;
		tecturedTriangleVertexZ = model.tecturedTriangleVertexZ;
	}

	public void method466() {
		super.modelHeight = 0;
		anInt1650 = 0;
		anInt1651 = 0;
		for (int vertex = 0; vertex < vertices; vertex++) {
			int x = vertexX[vertex];
			int y = vertexY[vertex];
			int z = vertexZ[vertex];
			if (-y > super.modelHeight) {
				super.modelHeight = -y;
			}
			if (y > anInt1651) {
				anInt1651 = y;
			}
			int i1 = x * x + z * z;
			if (i1 > anInt1650) {
				anInt1650 = i1;
			}
		}

		anInt1650 = (int) (Math.sqrt(anInt1650) + 0.99D);
		anInt1653 = (int) (Math.sqrt(anInt1650 * anInt1650 + super.modelHeight * super.modelHeight) + 0.99D);
		anInt1652 = anInt1653 + (int) (Math.sqrt(anInt1650 * anInt1650 + anInt1651 * anInt1651) + 0.99D);
	}

	public void method468() {
		super.modelHeight = 0;
		anInt1650 = 0;
		anInt1651 = 0;
		anInt1646 = 0xf423f;
		anInt1647 = 0xfff0bdc1;
		anInt1648 = 0xfffe7961;
		anInt1649 = 0x1869f;
		for (int j = 0; j < vertices; j++) {
			int k = vertexX[j];
			int l = vertexY[j];
			int i1 = vertexZ[j];
			if (k < anInt1646) {
				anInt1646 = k;
			}
			if (k > anInt1647) {
				anInt1647 = k;
			}
			if (i1 < anInt1649) {
				anInt1649 = i1;
			}
			if (i1 > anInt1648) {
				anInt1648 = i1;
			}
			if (-l > super.modelHeight) {
				super.modelHeight = -l;
			}
			if (l > anInt1651) {
				anInt1651 = l;
			}
			int j1 = k * k + i1 * i1;
			if (j1 > anInt1650) {
				anInt1650 = j1;
			}
		}

		anInt1650 = (int) Math.sqrt(anInt1650);
		anInt1653 = (int) Math.sqrt(anInt1650 * anInt1650 + super.modelHeight * super.modelHeight);
		anInt1652 = anInt1653 + (int) Math.sqrt(anInt1650 * anInt1650 + anInt1651 * anInt1651);
	}

	public final void method480(int i, int j, int k, int l, int i1) {
		for (int j1 = 0; j1 < triangles; j1++) {
			int k1 = triangleVertexX[j1];
			int i2 = triangleVertexY[j1];
			int j2 = triangleVertexZ[j1];
			if (texturePoints == null) {
				int i3 = triangleColours[j1];
				VertexNormal vertex = super.normals[k1];
				int k2 = i + (k * vertex.anInt602 + l * vertex.anInt603 + i1 * vertex.anInt604) / (j * vertex.anInt605);
				anIntArray1634[j1] = method481(i3, k2, 0);
				vertex = super.normals[i2];
				k2 = i + (k * vertex.anInt602 + l * vertex.anInt603 + i1 * vertex.anInt604) / (j * vertex.anInt605);
				anIntArray1635[j1] = method481(i3, k2, 0);
				vertex = super.normals[j2];
				k2 = i + (k * vertex.anInt602 + l * vertex.anInt603 + i1 * vertex.anInt604) / (j * vertex.anInt605);
				anIntArray1636[j1] = method481(i3, k2, 0);
			} else if ((texturePoints[j1] & 1) == 0) {
				int j3 = triangleColours[j1];
				int k3 = texturePoints[j1];
				VertexNormal class33_1 = super.normals[k1];
				int l2 = i + (k * class33_1.anInt602 + l * class33_1.anInt603 + i1 * class33_1.anInt604)
						/ (j * class33_1.anInt605);
				anIntArray1634[j1] = method481(j3, l2, k3);
				class33_1 = super.normals[i2];
				l2 = i + (k * class33_1.anInt602 + l * class33_1.anInt603 + i1 * class33_1.anInt604) / (j * class33_1.anInt605);
				anIntArray1635[j1] = method481(j3, l2, k3);
				class33_1 = super.normals[j2];
				l2 = i + (k * class33_1.anInt602 + l * class33_1.anInt603 + i1 * class33_1.anInt604) / (j * class33_1.anInt605);
				anIntArray1636[j1] = method481(j3, l2, k3);
			}
		}

		super.normals = null;
		normals = null;
		vertexSkins = null;
		triangleSkinValues = null;
		if (texturePoints != null) {
			for (int l1 = 0; l1 < triangles; l1++) {
				if ((texturePoints[l1] & 2) == 2) {
					return;
				}
			}
		}
		triangleColours = null;
	}

	public void pitch(int i, int j) {
		int k = SINE[i];
		int l = COSINE[i];
		for (int i1 = 0; i1 < vertices; i1++) {
			int j1 = vertexY[i1] * l - vertexZ[i1] * k >> 16;
			vertexZ[i1] = vertexY[i1] * k + vertexZ[i1] * l >> 16;
			vertexY[i1] = j1;
		}

		if (j < anInt1617 || j > anInt1617) {
			anInt1617 = 324;
		}
	}

	public void recolour(int oldColour, int newColour) {
		for (int i = 0; i < triangles; i++) {
			if (triangleColours[i] == oldColour) {
				triangleColours[i] = newColour;
			}
		}
	}

	public final void render(int i, int roll, int yaw, int pitch, int dx, int j1, int k1) {
		int viewX = Rasterizer.originViewX;
		int viewY = Rasterizer.originViewY;
		int j2 = SINE[i];
		int k2 = COSINE[i];
		int l2 = SINE[roll];
		int i3 = COSINE[roll];
		int j3 = SINE[yaw];
		int k3 = COSINE[yaw];
		int l3 = SINE[pitch];
		int i4 = COSINE[pitch];
		int j4 = j1 * l3 + k1 * i4 >> 16;
		for (int k4 = 0; k4 < vertices; k4++) {
			int x = vertexX[k4];
			int y = vertexY[k4];
			int z = vertexZ[k4];
			if (yaw != 0) {
				int k5 = y * j3 + x * k3 >> 16;
				y = y * k3 - x * j3 >> 16;
				x = k5;
			}
			if (i != 0) {
				int l5 = y * k2 - z * j2 >> 16;
				z = y * j2 + z * k2 >> 16;
				y = l5;
			}
			if (roll != 0) {
				int i6 = z * l2 + x * i3 >> 16;
				z = z * i3 - x * l2 >> 16;
				x = i6;
			}
			x += dx;
			y += j1;
			z += k1;
			int j6 = y * i4 - z * l3 >> 16;
			z = y * l3 + z * i4 >> 16;
			y = j6;
			anIntArray1667[k4] = z - j4;
			anIntArray1665[k4] = viewX + (x << 9) / z;
			anIntArray1666[k4] = viewY + (y << 9) / z;
			if (texturedTriangles > 0) {
				anIntArray1668[k4] = x;
				anIntArray1669[k4] = y;
				anIntArray1670[k4] = z;
			}
		}

		try {
			method483(false, false, 0);
		} catch (Exception _ex) {
		}
	}

	@Override
	public final void render(int x, int y, int orientation, int j, int k, int l, int i1, int height, int key) {
		int j2 = y * i1 - x * l >> 16;
		int k2 = height * j + j2 * k >> 16;
		int l2 = anInt1650 * k >> 16;
		int i3 = k2 + l2;

		if (i3 <= 50 || k2 >= 3500) {
			return;
		}

		int j3 = y * l + x * i1 >> 16;
		int k3 = j3 - anInt1650 << 9;
		if (k3 / i3 >= Raster.centreX) {
			return;
		}

		int l3 = j3 + anInt1650 << 9;
		if (l3 / i3 <= -Raster.centreX) {
			return;
		}

		int i4 = height * k - j2 * j >> 16;
		int j4 = anInt1650 * j >> 16;
		int k4 = i4 + j4 << 9;

		if (k4 / i3 <= -Raster.centreY) {
			return;
		}

		int l4 = j4 + (super.modelHeight * k >> 16);
		int i5 = i4 - l4 << 9;
		if (i5 / i3 >= Raster.centreY) {
			return;
		}

		int j5 = l2 + (super.modelHeight * j >> 16);
		boolean flag = false;
		if (k2 - j5 <= 50) {
			flag = true;
		}

		boolean flag1 = false;
		if (key > 0 && aBoolean1684) {
			int k5 = k2 - l2;
			if (k5 <= 50) {
				k5 = 50;
			}

			if (j3 > 0) {
				k3 /= i3;
				l3 /= k5;
			} else {
				l3 /= i3;
				k3 /= k5;
			}

			if (i4 > 0) {
				i5 /= i3;
				k4 /= k5;
			} else {
				k4 /= i3;
				i5 /= k5;
			}

			int i6 = anInt1685 - Rasterizer.originViewX;
			int k6 = anInt1686 - Rasterizer.originViewY;

			if (i6 > k3 && i6 < l3 && k6 > i5 && k6 < k4) {
				if (aBoolean1659) {
					anIntArray1688[anInt1687++] = key;
				} else {
					flag1 = true;
				}
			}
		}

		int viewX = Rasterizer.originViewX;
		int viewY = Rasterizer.originViewY;
		int sine = 0;
		int cosine = 0;

		if (orientation != 0) {
			sine = SINE[orientation];
			cosine = COSINE[orientation];
		}

		for (int vertex = 0; vertex < vertices; vertex++) {
			int xVertex = vertexX[vertex];
			int yVertex = vertexY[vertex];
			int zVertex = vertexZ[vertex];
			if (orientation != 0) {
				int j8 = zVertex * sine + xVertex * cosine >> 16;
				zVertex = zVertex * cosine - xVertex * sine >> 16;
				xVertex = j8;
			}

			xVertex += x;
			yVertex += height;
			zVertex += y;
			int k8 = zVertex * l + xVertex * i1 >> 16;
			zVertex = zVertex * i1 - xVertex * l >> 16;
			xVertex = k8;
			k8 = yVertex * k - zVertex * j >> 16;
			zVertex = yVertex * j + zVertex * k >> 16;
			yVertex = k8;
			anIntArray1667[vertex] = zVertex - k2;

			if (zVertex >= 50) {
				anIntArray1665[vertex] = viewX + (xVertex << 9) / zVertex;
				anIntArray1666[vertex] = viewY + (yVertex << 9) / zVertex;
			} else {
				anIntArray1665[vertex] = -5000;
				flag = true;
			}

			if (flag || texturedTriangles > 0) {
				anIntArray1668[vertex] = xVertex;
				anIntArray1669[vertex] = yVertex;
				anIntArray1670[vertex] = zVertex;
			}
		}

		try {
			method483(flag, flag1, key);
		} catch (Exception ex) {
		}
	}

	public void rotateClockwise() {
		for (int j = 0; j < vertices; j++) {
			int k = vertexX[j];
			vertexX[j] = vertexZ[j];
			vertexZ[j] = -k;
		}
	}

	public void scale(int x, int y, int z) {
		for (int i = 0; i < vertices; i++) {
			vertexX[i] = vertexX[i] * x / 128;
			vertexY[i] = vertexY[i] * z / 128;
			vertexZ[i] = vertexZ[i] * y / 128;
		}
	}

	public void skin() {
		if (vertexSkins != null) {
			int ai[] = new int[256];
			int j = 0;
			for (int l = 0; l < vertices; l++) {
				int j1 = vertexSkins[l];
				ai[j1]++;
				if (j1 > j) {
					j = j1;
				}
			}

			vertexGroups = new int[j + 1][];
			for (int k1 = 0; k1 <= j; k1++) {
				vertexGroups[k1] = new int[ai[k1]];
				ai[k1] = 0;
			}

			for (int j2 = 0; j2 < vertices; j2++) {
				int l2 = vertexSkins[j2];
				vertexGroups[l2][ai[l2]++] = j2;
			}

			vertexSkins = null;
		}
		if (triangleSkinValues != null) {
			int ai1[] = new int[256];
			int k = 0;
			for (int i1 = 0; i1 < triangles; i1++) {
				int l1 = triangleSkinValues[i1];
				ai1[l1]++;
				if (l1 > k) {
					k = l1;
				}
			}

			faceGroups = new int[k + 1][];
			for (int i2 = 0; i2 <= k; i2++) {
				faceGroups[i2] = new int[ai1[i2]];
				ai1[i2] = 0;
			}

			for (int k2 = 0; k2 < triangles; k2++) {
				int i3 = triangleSkinValues[k2];
				faceGroups[i3][ai1[i3]++] = k2;
			}

			triangleSkinValues = null;
		}
	}

	public void translate(int x, int y, int z) {
		for (int i = 0; i < vertices; i++) {
			vertexX[i] += x;
			vertexY[i] += y;
			vertexZ[i] += z;
		}
	}

	private final int method465(Model model, int vertex) {
		int index = -1;
		int x = model.vertexX[vertex];
		int y = model.vertexY[vertex];
		int z = model.vertexZ[vertex];
		for (int i = 0; i < vertices; i++) {
			if (x != vertexX[i] || y != vertexY[i] || z != vertexZ[i]) {
				continue;
			}
			index = i;
			break;
		}

		if (index == -1) {
			vertexX[vertices] = x;
			vertexY[vertices] = y;
			vertexZ[vertices] = z;
			if (model.vertexSkins != null) {
				vertexSkins[vertices] = model.vertexSkins[vertex];
			}
			index = vertices++;
		}
		return index;
	}

	private final void method483(boolean flag, boolean flag1, int config) {
		for (int j = 0; j < anInt1652; j++) {
			anIntArray1671[j] = 0;
		}

		for (int triangle = 0; triangle < triangles; triangle++) {
			if (texturePoints == null || texturePoints[triangle] != -1) {
				int l = triangleVertexX[triangle];
				int k1 = triangleVertexY[triangle];
				int j2 = triangleVertexZ[triangle];
				int i3 = anIntArray1665[l];
				int l3 = anIntArray1665[k1];
				int k4 = anIntArray1665[j2];
				if (flag && (i3 == -5000 || l3 == -5000 || k4 == -5000)) {
					aBooleanArray1664[triangle] = true;
					int j5 = (anIntArray1667[l] + anIntArray1667[k1] + anIntArray1667[j2]) / 3 + anInt1653;
					anIntArrayArray1672[j5][anIntArray1671[j5]++] = triangle;
				} else {
					if (flag1
							&& method486(anInt1685, anInt1686, anIntArray1666[l], anIntArray1666[k1], anIntArray1666[j2], i3, l3,
									k4)) {
						anIntArray1688[anInt1687++] = config;
						flag1 = false;
					}
					if ((i3 - l3) * (anIntArray1666[j2] - anIntArray1666[k1]) - (anIntArray1666[l] - anIntArray1666[k1])
							* (k4 - l3) > 0) {
						aBooleanArray1664[triangle] = false;
						if (i3 < 0 || l3 < 0 || k4 < 0 || i3 > Raster.anInt1385 || l3 > Raster.anInt1385 || k4 > Raster.anInt1385) {
							aBooleanArray1663[triangle] = true;
						} else {
							aBooleanArray1663[triangle] = false;
						}
						int k5 = (anIntArray1667[l] + anIntArray1667[k1] + anIntArray1667[j2]) / 3 + anInt1653;
						anIntArrayArray1672[k5][anIntArray1671[k5]++] = triangle;
					}
				}
			}
		}

		if (trianglePriorities == null) {
			for (int i1 = anInt1652 - 1; i1 >= 0; i1--) {
				int l1 = anIntArray1671[i1];
				if (l1 > 0) {
					int ai[] = anIntArrayArray1672[i1];
					for (int j3 = 0; j3 < l1; j3++) {
						method484(ai[j3]);
					}
				}
			}

			return;
		}
		for (int j1 = 0; j1 < 12; j1++) {
			anIntArray1673[j1] = 0;
			anIntArray1677[j1] = 0;
		}

		for (int i2 = anInt1652 - 1; i2 >= 0; i2--) {
			int k2 = anIntArray1671[i2];
			if (k2 > 0) {
				int ai1[] = anIntArrayArray1672[i2];
				for (int i4 = 0; i4 < k2; i4++) {
					int l4 = ai1[i4];
					int l5 = trianglePriorities[l4];
					int j6 = anIntArray1673[l5]++;
					anIntArrayArray1674[l5][j6] = l4;
					if (l5 < 10) {
						anIntArray1677[l5] += i2;
					} else if (l5 == 10) {
						anIntArray1675[j6] = i2;
					} else {
						anIntArray1676[j6] = i2;
					}
				}

			}
		}

		int l2 = 0;
		if (anIntArray1673[1] > 0 || anIntArray1673[2] > 0) {
			l2 = (anIntArray1677[1] + anIntArray1677[2]) / (anIntArray1673[1] + anIntArray1673[2]);
		}
		int k3 = 0;
		if (anIntArray1673[3] > 0 || anIntArray1673[4] > 0) {
			k3 = (anIntArray1677[3] + anIntArray1677[4]) / (anIntArray1673[3] + anIntArray1673[4]);
		}
		int j4 = 0;
		if (anIntArray1673[6] > 0 || anIntArray1673[8] > 0) {
			j4 = (anIntArray1677[6] + anIntArray1677[8]) / (anIntArray1673[6] + anIntArray1673[8]);
		}
		int i6 = 0;
		int k6 = anIntArray1673[10];
		int ai2[] = anIntArrayArray1674[10];
		int ai3[] = anIntArray1675;
		if (i6 == k6) {
			i6 = 0;
			k6 = anIntArray1673[11];
			ai2 = anIntArrayArray1674[11];
			ai3 = anIntArray1676;
		}
		int i5;
		if (i6 < k6) {
			i5 = ai3[i6];
		} else {
			i5 = -1000;
		}
		for (int l6 = 0; l6 < 10; l6++) {
			while (l6 == 0 && i5 > l2) {
				method484(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6) {
					i5 = ai3[i6];
				} else {
					i5 = -1000;
				}
			}
			while (l6 == 3 && i5 > k3) {
				method484(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6) {
					i5 = ai3[i6];
				} else {
					i5 = -1000;
				}
			}
			while (l6 == 5 && i5 > j4) {
				method484(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6) {
					i5 = ai3[i6];
				} else {
					i5 = -1000;
				}
			}
			int i7 = anIntArray1673[l6];
			int ai4[] = anIntArrayArray1674[l6];
			for (int j7 = 0; j7 < i7; j7++) {
				method484(ai4[j7]);
			}

		}

		while (i5 != -1000) {
			method484(ai2[i6++]);
			if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
				i6 = 0;
				ai2 = anIntArrayArray1674[11];
				k6 = anIntArray1673[11];
				ai3 = anIntArray1676;
			}
			if (i6 < k6) {
				i5 = ai3[i6];
			} else {
				i5 = -1000;
			}
		}
	}

	private final void method484(int i) {
		if (aBooleanArray1664[i]) {
			method485(i);
			return;
		}
		int j = triangleVertexX[i];
		int k = triangleVertexY[i];
		int l = triangleVertexZ[i];
		Rasterizer.aBoolean1462 = aBooleanArray1663[i];
		if (faceAlphas == null) {
			Rasterizer.anInt1465 = 0;
		} else {
			Rasterizer.anInt1465 = faceAlphas[i];
		}
		int i1;
		if (texturePoints == null) {
			i1 = 0;
		} else {
			i1 = texturePoints[i] & 3;
		}
		if (i1 == 0) {
			Rasterizer.method374(anIntArray1666[j], anIntArray1666[k], anIntArray1666[l], anIntArray1665[j], anIntArray1665[k],
					anIntArray1665[l], anIntArray1634[i], anIntArray1635[i], anIntArray1636[i]);
			return;
		}
		if (i1 == 1) {
			Rasterizer.method376(anIntArray1666[j], anIntArray1666[k], anIntArray1666[l], anIntArray1665[j], anIntArray1665[k],
					anIntArray1665[l], anIntArray1691[anIntArray1634[i]]);
			return;
		}
		if (i1 == 2) {
			int j1 = texturePoints[i] >> 2;
			int l1 = tecturedTriangleVertexX[j1];
			int j2 = tecturedTriangleVertexY[j1];
			int l2 = tecturedTriangleVertexZ[j1];
			Rasterizer.method378(anIntArray1666[j], anIntArray1666[k], anIntArray1666[l], anIntArray1665[j], anIntArray1665[k],
					anIntArray1665[l], anIntArray1634[i], anIntArray1635[i], anIntArray1636[i], anIntArray1668[l1],
					anIntArray1668[j2], anIntArray1668[l2], anIntArray1669[l1], anIntArray1669[j2], anIntArray1669[l2],
					anIntArray1670[l1], anIntArray1670[j2], anIntArray1670[l2], triangleColours[i]);
			return;
		}
		if (i1 == 3) {
			int k1 = texturePoints[i] >> 2;
			int i2 = tecturedTriangleVertexX[k1];
			int k2 = tecturedTriangleVertexY[k1];
			int i3 = tecturedTriangleVertexZ[k1];
			Rasterizer.method378(anIntArray1666[j], anIntArray1666[k], anIntArray1666[l], anIntArray1665[j], anIntArray1665[k],
					anIntArray1665[l], anIntArray1634[i], anIntArray1634[i], anIntArray1634[i], anIntArray1668[i2],
					anIntArray1668[k2], anIntArray1668[i3], anIntArray1669[i2], anIntArray1669[k2], anIntArray1669[i3],
					anIntArray1670[i2], anIntArray1670[k2], anIntArray1670[i3], triangleColours[i]);
		}
	}

	private final void method485(int i) {
		int j = Rasterizer.originViewX;
		int k = Rasterizer.originViewY;
		int l = 0;
		int i1 = triangleVertexX[i];
		int j1 = triangleVertexY[i];
		int k1 = triangleVertexZ[i];
		int l1 = anIntArray1670[i1];
		int i2 = anIntArray1670[j1];
		int j2 = anIntArray1670[k1];
		if (l1 >= 50) {
			anIntArray1678[l] = anIntArray1665[i1];
			anIntArray1679[l] = anIntArray1666[i1];
			anIntArray1680[l++] = anIntArray1634[i];
		} else {
			int k2 = anIntArray1668[i1];
			int k3 = anIntArray1669[i1];
			int k4 = anIntArray1634[i];
			if (j2 >= 50) {
				int k5 = (50 - l1) * anIntArray1692[j2 - l1];
				anIntArray1678[l] = j + (k2 + ((anIntArray1668[k1] - k2) * k5 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (k3 + ((anIntArray1669[k1] - k3) * k5 >> 16) << 9) / 50;
				anIntArray1680[l++] = k4 + ((anIntArray1636[i] - k4) * k5 >> 16);
			}
			if (i2 >= 50) {
				int l5 = (50 - l1) * anIntArray1692[i2 - l1];
				anIntArray1678[l] = j + (k2 + ((anIntArray1668[j1] - k2) * l5 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (k3 + ((anIntArray1669[j1] - k3) * l5 >> 16) << 9) / 50;
				anIntArray1680[l++] = k4 + ((anIntArray1635[i] - k4) * l5 >> 16);
			}
		}
		if (i2 >= 50) {
			anIntArray1678[l] = anIntArray1665[j1];
			anIntArray1679[l] = anIntArray1666[j1];
			anIntArray1680[l++] = anIntArray1635[i];
		} else {
			int l2 = anIntArray1668[j1];
			int l3 = anIntArray1669[j1];
			int l4 = anIntArray1635[i];
			if (l1 >= 50) {
				int i6 = (50 - i2) * anIntArray1692[l1 - i2];
				anIntArray1678[l] = j + (l2 + ((anIntArray1668[i1] - l2) * i6 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (l3 + ((anIntArray1669[i1] - l3) * i6 >> 16) << 9) / 50;
				anIntArray1680[l++] = l4 + ((anIntArray1634[i] - l4) * i6 >> 16);
			}
			if (j2 >= 50) {
				int j6 = (50 - i2) * anIntArray1692[j2 - i2];
				anIntArray1678[l] = j + (l2 + ((anIntArray1668[k1] - l2) * j6 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (l3 + ((anIntArray1669[k1] - l3) * j6 >> 16) << 9) / 50;
				anIntArray1680[l++] = l4 + ((anIntArray1636[i] - l4) * j6 >> 16);
			}
		}
		if (j2 >= 50) {
			anIntArray1678[l] = anIntArray1665[k1];
			anIntArray1679[l] = anIntArray1666[k1];
			anIntArray1680[l++] = anIntArray1636[i];
		} else {
			int i3 = anIntArray1668[k1];
			int i4 = anIntArray1669[k1];
			int i5 = anIntArray1636[i];
			if (i2 >= 50) {
				int k6 = (50 - j2) * anIntArray1692[i2 - j2];
				anIntArray1678[l] = j + (i3 + ((anIntArray1668[j1] - i3) * k6 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (i4 + ((anIntArray1669[j1] - i4) * k6 >> 16) << 9) / 50;
				anIntArray1680[l++] = i5 + ((anIntArray1635[i] - i5) * k6 >> 16);
			}
			if (l1 >= 50) {
				int l6 = (50 - j2) * anIntArray1692[l1 - j2];
				anIntArray1678[l] = j + (i3 + ((anIntArray1668[i1] - i3) * l6 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (i4 + ((anIntArray1669[i1] - i4) * l6 >> 16) << 9) / 50;
				anIntArray1680[l++] = i5 + ((anIntArray1634[i] - i5) * l6 >> 16);
			}
		}
		int j3 = anIntArray1678[0];
		int j4 = anIntArray1678[1];
		int j5 = anIntArray1678[2];
		int i7 = anIntArray1679[0];
		int j7 = anIntArray1679[1];
		int k7 = anIntArray1679[2];
		if ((j3 - j4) * (k7 - j7) - (i7 - j7) * (j5 - j4) > 0) {
			Rasterizer.aBoolean1462 = false;
			if (l == 3) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > Raster.anInt1385 || j4 > Raster.anInt1385 || j5 > Raster.anInt1385) {
					Rasterizer.aBoolean1462 = true;
				}
				int l7;
				if (texturePoints == null) {
					l7 = 0;
				} else {
					l7 = texturePoints[i] & 3;
				}
				if (l7 == 0) {
					Rasterizer.method374(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2]);
				} else if (l7 == 1) {
					Rasterizer.method376(i7, j7, k7, j3, j4, j5, anIntArray1691[anIntArray1634[i]]);
				} else if (l7 == 2) {
					int j8 = texturePoints[i] >> 2;
					int k9 = tecturedTriangleVertexX[j8];
					int k10 = tecturedTriangleVertexY[j8];
					int k11 = tecturedTriangleVertexZ[j8];
					Rasterizer.method378(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2],
							anIntArray1668[k9], anIntArray1668[k10], anIntArray1668[k11], anIntArray1669[k9],
							anIntArray1669[k10], anIntArray1669[k11], anIntArray1670[k9], anIntArray1670[k10],
							anIntArray1670[k11], triangleColours[i]);
				} else if (l7 == 3) {
					int k8 = texturePoints[i] >> 2;
					int l9 = tecturedTriangleVertexX[k8];
					int l10 = tecturedTriangleVertexY[k8];
					int l11 = tecturedTriangleVertexZ[k8];
					Rasterizer.method378(i7, j7, k7, j3, j4, j5, anIntArray1634[i], anIntArray1634[i], anIntArray1634[i],
							anIntArray1668[l9], anIntArray1668[l10], anIntArray1668[l11], anIntArray1669[l9],
							anIntArray1669[l10], anIntArray1669[l11], anIntArray1670[l9], anIntArray1670[l10],
							anIntArray1670[l11], triangleColours[i]);
				}
			}
			if (l == 4) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > Raster.anInt1385 || j4 > Raster.anInt1385 || j5 > Raster.anInt1385
						|| anIntArray1678[3] < 0 || anIntArray1678[3] > Raster.anInt1385) {
					Rasterizer.aBoolean1462 = true;
				}
				int i8;
				if (texturePoints == null) {
					i8 = 0;
				} else {
					i8 = texturePoints[i] & 3;
				}
				if (i8 == 0) {
					Rasterizer.method374(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2]);
					Rasterizer.method374(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], anIntArray1680[0],
							anIntArray1680[2], anIntArray1680[3]);
					return;
				}
				if (i8 == 1) {
					int l8 = anIntArray1691[anIntArray1634[i]];
					Rasterizer.method376(i7, j7, k7, j3, j4, j5, l8);
					Rasterizer.method376(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], l8);
					return;
				}
				if (i8 == 2) {
					int i9 = texturePoints[i] >> 2;
					int i10 = tecturedTriangleVertexX[i9];
					int i11 = tecturedTriangleVertexY[i9];
					int i12 = tecturedTriangleVertexZ[i9];
					Rasterizer.method378(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2],
							anIntArray1668[i10], anIntArray1668[i11], anIntArray1668[i12], anIntArray1669[i10],
							anIntArray1669[i11], anIntArray1669[i12], anIntArray1670[i10], anIntArray1670[i11],
							anIntArray1670[i12], triangleColours[i]);
					Rasterizer.method378(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], anIntArray1680[0],
							anIntArray1680[2], anIntArray1680[3], anIntArray1668[i10], anIntArray1668[i11], anIntArray1668[i12],
							anIntArray1669[i10], anIntArray1669[i11], anIntArray1669[i12], anIntArray1670[i10],
							anIntArray1670[i11], anIntArray1670[i12], triangleColours[i]);
					return;
				}
				if (i8 == 3) {
					int j9 = texturePoints[i] >> 2;
					int j10 = tecturedTriangleVertexX[j9];
					int j11 = tecturedTriangleVertexY[j9];
					int j12 = tecturedTriangleVertexZ[j9];
					Rasterizer.method378(i7, j7, k7, j3, j4, j5, anIntArray1634[i], anIntArray1634[i], anIntArray1634[i],
							anIntArray1668[j10], anIntArray1668[j11], anIntArray1668[j12], anIntArray1669[j10],
							anIntArray1669[j11], anIntArray1669[j12], anIntArray1670[j10], anIntArray1670[j11],
							anIntArray1670[j12], triangleColours[i]);
					Rasterizer.method378(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], anIntArray1634[i],
							anIntArray1634[i], anIntArray1634[i], anIntArray1668[j10], anIntArray1668[j11], anIntArray1668[j12],
							anIntArray1669[j10], anIntArray1669[j11], anIntArray1669[j12], anIntArray1670[j10],
							anIntArray1670[j11], anIntArray1670[j12], triangleColours[i]);
				}
			}
		}
	}

	private final boolean method486(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
		if (j < k && j < l && j < i1) {
			return false;
		}
		if (j > k && j > l && j > i1) {
			return false;
		}
		if (i < j1 && i < k1 && i < l1) {
			return false;
		}
		return i <= j1 || i <= k1 || i <= l1;
	}

	private void transform(int transformation, int[] groups, int dx, int dy, int dz) {
		int groupCount = groups.length;

		if (transformation == 0) {
			int vertices = 0;
			centroidX = 0;
			centroidY = 0;
			centroidZ = 0;
			for (int index = 0; index < groupCount; index++) {
				int group = groups[index];
				if (group < vertexGroups.length) {
					for (int vertex : vertexGroups[group]) {
						centroidX += vertexX[vertex];
						centroidY += vertexY[vertex];
						centroidZ += vertexZ[vertex];
						vertices++;
					}
				}
			}

			if (vertices > 0) {
				centroidX = centroidX / vertices + dx;
				centroidY = centroidY / vertices + dy;
				centroidZ = centroidZ / vertices + dz;
			} else {
				centroidX = dx;
				centroidY = dy;
				centroidZ = dz;
			}
		} else if (transformation == 1) {
			for (int index = 0; index < groupCount; index++) {
				int group = groups[index];
				if (group < vertexGroups.length) {
					for (int vertex : vertexGroups[group]) {
						vertexX[vertex] += dx;
						vertexY[vertex] += dy;
						vertexZ[vertex] += dz;
					}
				}
			}
		} else if (transformation == 2) {
			for (int index = 0; index < groupCount; index++) {
				int group = groups[index];
				if (group < vertexGroups.length) {
					for (int vertex : vertexGroups[group]) {
						vertexX[vertex] -= centroidX;
						vertexY[vertex] -= centroidY;
						vertexZ[vertex] -= centroidZ;
						int angleX = (dx & 0xff) * 8;
						int angleY = (dy & 0xff) * 8;
						int angleZ = (dz & 0xff) * 8; // TODO roll/yaw/?

						if (angleZ != 0) {
							int sin = SINE[angleZ];
							int cos = COSINE[angleZ];
							int x = vertexY[vertex] * sin + vertexX[vertex] * cos >> 16;
							vertexY[vertex] = vertexY[vertex] * cos - vertexX[vertex] * sin >> 16;
							vertexX[vertex] = x;
						}

						if (angleX != 0) {
							int sin = SINE[angleX];
							int cos = COSINE[angleX];
							int y = vertexY[vertex] * cos - vertexZ[vertex] * sin >> 16;
							vertexZ[vertex] = vertexY[vertex] * sin + vertexZ[vertex] * cos >> 16;
							vertexY[vertex] = y;
						}

						if (angleY != 0) {
							int sin = SINE[angleY];
							int cos = COSINE[angleY];
							int x = vertexZ[vertex] * sin + vertexX[vertex] * cos >> 16;
							vertexZ[vertex] = vertexZ[vertex] * cos - vertexX[vertex] * sin >> 16;
							vertexX[vertex] = x;
						}
						vertexX[vertex] += centroidX;
						vertexY[vertex] += centroidY;
						vertexZ[vertex] += centroidZ;
					}
				}
			}
		} else if (transformation == 3) {
			for (int i2 = 0; i2 < groupCount; i2++) {
				int group = groups[i2];
				if (group < vertexGroups.length) {
					for (int vertex : vertexGroups[group]) {
						vertexX[vertex] -= centroidX;
						vertexY[vertex] -= centroidY;
						vertexZ[vertex] -= centroidZ;
						vertexX[vertex] = vertexX[vertex] * dx / 128;
						vertexY[vertex] = vertexY[vertex] * dy / 128;
						vertexZ[vertex] = vertexZ[vertex] * dz / 128;
						vertexX[vertex] += centroidX;
						vertexY[vertex] += centroidY;
						vertexZ[vertex] += centroidZ;
					}
				}
			}
		} else if (transformation == 5 && faceGroups != null && faceAlphas != null) {
			for (int index = 0; index < groupCount; index++) {
				int group = groups[index];
				if (group < faceGroups.length) {
					for (int face : faceGroups[group]) {
						faceAlphas[face] += dx * 8;
						if (faceAlphas[face] < 0) {
							faceAlphas[face] = 0;
						}
						if (faceAlphas[face] > 255) {
							faceAlphas[face] = 255;
						}
					}
				}
			}
		}
	}
}
