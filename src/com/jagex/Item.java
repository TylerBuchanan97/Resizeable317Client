package com.jagex;

final class Item extends Renderable {

	// Class30_Sub2_Sub4_Sub2

	public int amount;
	public int id;

	@Override
	public final Model model() {
		return ItemDefinition.lookup(id).asGroundStack(amount);
	}

}