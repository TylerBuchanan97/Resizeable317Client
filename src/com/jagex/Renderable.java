package com.jagex;

public class Renderable extends CacheableNode {

	// Class30_Sub2_Sub4

	public int modelHeight = 1000;
	VertexNormal[] normals;

	public Model model() {
		return null;
	}

	public void render(int x, int y, int orientation, int j, int k, int l, int i1, int height, int key) {
		Model model = model();
		if (model != null) {
			modelHeight = model.modelHeight;
			model.render(x, y, orientation, j, k, l, i1, height, key);
		}
	}

}