package com.jagex;

final class SceneTile extends Node {

	public ComplexTile aClass40_1312;
	public GenericTile aClass43_1311;
	boolean aBoolean1322;
	boolean aBoolean1323;
	boolean aBoolean1324;
	SceneTile aClass30_Sub3_1329;
	int anInt1310;
	int anInt1320;
	int anInt1321;
	int anInt1325;
	int anInt1326;
	int anInt1327;
	int anInt1328;
	int anIntArray1319[];
	FloorDecoration floorDecoration;
	GroundItemTile groundItem;
	InteractableObject interactableObjects[];
	int objectCount;
	int plane;
	int positionX;
	int positionY;
	Wall wall;
	WallDecoration wallDecoration;

	public SceneTile(int x, int y, int z) {
		interactableObjects = new InteractableObject[5];
		anIntArray1319 = new int[5];
		anInt1310 = plane = z;
		positionX = x;
		positionY = y;
	}

}