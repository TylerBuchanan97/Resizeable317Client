package com.jagex;

public class CollisionMap {

	public int[][] adjacencies;
	public int height;
	public int width;
	public int xOffset;
	public int yOffset;

	public CollisionMap(int width, int height) {
		xOffset = 0;
		yOffset = 0;
		this.width = width;
		this.height = height;
		adjacencies = new int[width][height];
		init();
	}

	public void flagObject(int x, int y, int orientation, int type, boolean blockWalking) {
		x -= xOffset;
		y -= yOffset;

		if (type == 0) {
			if (orientation == 0) {
				flag(x, y, 128);
				flag(x - 1, y, 8);
			} else if (orientation == 1) {
				flag(x, y, 2);
				flag(x, y + 1, 32);
			} else if (orientation == 2) {
				flag(x, y, 8);
				flag(x + 1, y, 128);
			} else if (orientation == 3) {
				flag(x, y, 32);
				flag(x, y - 1, 2);
			}
		}

		if (type == 1 || type == 3) {
			if (orientation == 0) {
				flag(x, y, 1);
				flag(x - 1, y + 1, 16);
			} else if (orientation == 1) {
				flag(x, y, 4);
				flag(x + 1, y + 1, 64);
			} else if (orientation == 2) {
				flag(x, y, 16);
				flag(x + 1, y - 1, 1);
			} else if (orientation == 3) {
				flag(x, y, 64);
				flag(x - 1, y - 1, 4);
			}
		}

		if (type == 2) {
			if (orientation == 0) {
				flag(x, y, 130);
				flag(x - 1, y, 8);
				flag(x, y + 1, 32);
			} else if (orientation == 1) {
				flag(x, y, 10);
				flag(x, y + 1, 32);
				flag(x + 1, y, 128);
			} else if (orientation == 2) {
				flag(x, y, 40);
				flag(x + 1, y, 128);
				flag(x, y - 1, 2);
			} else if (orientation == 3) {
				flag(x, y, 160);
				flag(x, y - 1, 2);
				flag(x - 1, y, 8);
			}
		}

		if (blockWalking) {
			if (type == 0) {
				if (orientation == 0) {
					flag(x, y, 0x10000);
					flag(x - 1, y, 4096);
				} else if (orientation == 1) {
					flag(x, y, 1024);
					flag(x, y + 1, 16384);
				} else if (orientation == 2) {
					flag(x, y, 4096);
					flag(x + 1, y, 0x10000);
				} else if (orientation == 3) {
					flag(x, y, 16384);
					flag(x, y - 1, 1024);
				}
			}

			if (type == 1 || type == 3) {
				if (orientation == 0) {
					flag(x, y, 512);
					flag(x - 1, y + 1, 8192);
				} else if (orientation == 1) {
					flag(x, y, 2048);
					flag(x + 1, y + 1, 32768);
				} else if (orientation == 2) {
					flag(x, y, 8192);
					flag(x + 1, y - 1, 512);
				} else if (orientation == 3) {
					flag(x, y, 32768);
					flag(x - 1, y - 1, 2048);
				}
			}

			if (type == 2) {
				if (orientation == 0) {
					flag(x, y, 0x10400);
					flag(x - 1, y, 4096);
					flag(x, y + 1, 16384);
				} else if (orientation == 1) {
					flag(x, y, 5120);
					flag(x, y + 1, 16384);
					flag(x + 1, y, 0x10000);
				} else if (orientation == 2) {
					flag(x, y, 20480);
					flag(x + 1, y, 0x10000);
					flag(x, y - 1, 1024);
				} else if (orientation == 3) {
					flag(x, y, 0x14000);
					flag(x, y - 1, 1024);
					flag(x - 1, y, 4096);
				}
			}
		}
	}

	public void flagObject(int initialX, int initialY, int width, int height, boolean impenetrable, int orientation) {
		int value = 256;
		if (impenetrable) {
			value += 0x20000;
		}

		initialX -= xOffset;
		initialY -= yOffset;

		if (orientation == 1 || orientation == 3) {
			int tmp = width;
			width = height;
			height = tmp;
		}

		for (int x = initialX; x < initialX + width; x++) {
			if (x >= 0 && x < this.width) {
				for (int y = initialY; y < initialY + height; y++) {
					if (y >= 0 && y < this.height) {
						flag(x, y, value);
					}
				}
			}
		}
	}

	public void init() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
					adjacencies[x][y] = 0xffffff;
				} else {
					adjacencies[x][y] = 0x1000000;
				}
			}
		}
	}

	public boolean method220(int initialY, int initialX, int finalX, int finalY, int type, int orientation) {
		if (initialX == finalX && initialY == finalY) {
			return true;
		}

		initialX -= xOffset;
		initialY -= yOffset;
		finalX -= xOffset;
		finalY -= yOffset;

		if (type == 6 || type == 7) {
			if (type == 7) {
				orientation = orientation + 2 & 3;
			}

			if (orientation == 0) {
				if (initialX == finalX + 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x80) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (adjacencies[initialX][initialY] & 2) == 0) {
					return true;
				}
			} else if (orientation == 1) {
				if (initialX == finalX - 1 && initialY == finalY && (adjacencies[initialX][initialY] & 8) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (adjacencies[initialX][initialY] & 2) == 0) {
					return true;
				}
			} else if (orientation == 2) {
				if (initialX == finalX - 1 && initialY == finalY && (adjacencies[initialX][initialY] & 8) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (adjacencies[initialX][initialY] & 0x20) == 0) {
					return true;
				}
			} else if (orientation == 3) {
				if (initialX == finalX + 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x80) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (adjacencies[initialX][initialY] & 0x20) == 0) {
					return true;
				}
			}
		}

		if (type == 8) {
			if (initialX == finalX && initialY == finalY + 1 && (adjacencies[initialX][initialY] & 0x20) == 0) {
				return true;
			} else if (initialX == finalX && initialY == finalY - 1 && (adjacencies[initialX][initialY] & 2) == 0) {
				return true;
			} else if (initialX == finalX - 1 && initialY == finalY && (adjacencies[initialX][initialY] & 8) == 0) {
				return true;
			} else if (initialX == finalX + 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x80) == 0) {
				return true;
			}
		}

		return false;
	}

	public boolean method221(int x, int y, int finalX, int finalY, int height, int surroundings, int width) {
		int maxX = finalX + width - 1;
		int maxY = finalY + height - 1;

		if (x >= finalX && x <= maxX && y >= finalY && y <= maxY) {
			return true;
		} else if (x == finalX - 1 && y >= finalY && y <= maxY && (adjacencies[x - xOffset][y - yOffset] & 8) == 0
				&& (surroundings & 8) == 0) {
			return true;
		} else if (x == maxX + 1 && y >= finalY && y <= maxY && (adjacencies[x - xOffset][y - yOffset] & 0x80) == 0
				&& (surroundings & 2) == 0) {
			return true;
		} else if (y == finalY - 1 && x >= finalX && x <= maxX && (adjacencies[x - xOffset][y - yOffset] & 2) == 0
				&& (surroundings & 4) == 0) {
			return true;
		}

		return y == maxY + 1 && x >= finalX && x <= maxX && (adjacencies[x - xOffset][y - yOffset] & 0x20) == 0
				&& (surroundings & 1) == 0;
	}

	public boolean reachedGoal(int initialX, int initialY, int finalX, int finalY, int orientation, int type) {
		if (initialX == finalX && initialY == finalY) {
			return true;
		}

		initialX -= xOffset;
		initialY -= yOffset;
		finalX -= xOffset;
		finalY -= yOffset;

		if (type == 0) {
			if (orientation == 0) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (adjacencies[initialX][initialY] & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (adjacencies[initialX][initialY] & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == 1) {
				if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX - 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x1280180) == 0) {
					return true;
				}
			} else if (orientation == 2) {
				if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (adjacencies[initialX][initialY] & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (adjacencies[initialX][initialY] & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == 3) {
				if (initialX == finalX && initialY == finalY - 1) {
					return true;
				} else if (initialX == finalX - 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x1280180) == 0) {
					return true;
				}
			}
		}

		if (type == 2) {
			if (orientation == 0) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x1280180) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (adjacencies[initialX][initialY] & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == 1) {
				// UNLOADED_TILE | BLOCKED_TILE | UNKNOWN | OBJECT_TILE | WALL_EAST
				if (initialX == finalX - 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (adjacencies[initialX][initialY] & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == 2) {
				if (initialX == finalX - 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (adjacencies[initialX][initialY] & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1) {
					return true;
				}
			} else if (orientation == 3) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (adjacencies[initialX][initialY] & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x1280180) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1) {
					return true;
				}
			}
		}

		if (type == 9) {
			if (initialX == finalX && initialY == finalY + 1 && (adjacencies[initialX][initialY] & 0x20) == 0) {
				return true;
			} else if (initialX == finalX && initialY == finalY - 1 && (adjacencies[initialX][initialY] & 2) == 0) {
				return true;
			} else if (initialX == finalX - 1 && initialY == finalY && (adjacencies[initialX][initialY] & 8) == 0) {
				return true;
			} else if (initialX == finalX + 1 && initialY == finalY && (adjacencies[initialX][initialY] & 0x80) == 0) {
				return true;
			}
		}

		return false;
	}

	public void removeFloorDecoration(int x, int y) {
		x -= xOffset;
		y -= yOffset;
		adjacencies[x][y] &= 0xdfffff;
	}

	public void removeObject(int x, int y, int orientation, int type, boolean impenetrable) {
		x -= xOffset;
		y -= yOffset;

		if (type == 0) {// wall
			if (orientation == 0) {
				removeFlag(x, y, 128);
				removeFlag(x - 1, y, 8);
			} else if (orientation == 1) {
				removeFlag(x, y, 2);
				removeFlag(x, y + 1, 32);
			} else if (orientation == 2) {
				removeFlag(x, y, 8);
				removeFlag(x + 1, y, 128);
			} else if (orientation == 3) {
				removeFlag(x, y, 32);
				removeFlag(x, y - 1, 2);
			}
		}

		if (type == 1 || type == 3) { // wall decor/floor decor
			if (orientation == 0) {
				removeFlag(x, y, 1);
				removeFlag(x - 1, y + 1, 16);
			} else if (orientation == 1) {
				removeFlag(x, y, 4);
				removeFlag(x + 1, y + 1, 64);
			} else if (orientation == 2) {
				removeFlag(x, y, 16);
				removeFlag(x + 1, y - 1, 1);
			} else if (orientation == 3) {
				removeFlag(x, y, 64);
				removeFlag(x - 1, y - 1, 4);
			}
		}

		if (type == 2) { // interactable object
			if (orientation == 0) {
				removeFlag(x, y, 130);
				removeFlag(x - 1, y, 8);
				removeFlag(x, y + 1, 32);
			} else if (orientation == 1) {
				removeFlag(x, y, 10);
				removeFlag(x, y + 1, 32);
				removeFlag(x + 1, y, 128);
			} else if (orientation == 2) {
				removeFlag(x, y, 40);
				removeFlag(x + 1, y, 128);
				removeFlag(x, y - 1, 2);
			} else if (orientation == 3) {
				removeFlag(x, y, 160);
				removeFlag(x, y - 1, 2);
				removeFlag(x - 1, y, 8);
			}
		}

		if (impenetrable) {
			if (type == 0) {
				if (orientation == 0) {
					removeFlag(x, y, 0x10000);
					removeFlag(x - 1, y, 4096);
				} else if (orientation == 1) {
					removeFlag(x, y, 1024);
					removeFlag(x, y + 1, 16384);
				} else if (orientation == 2) {
					removeFlag(x, y, 4096);
					removeFlag(x + 1, y, 0x10000);
				} else if (orientation == 3) {
					removeFlag(x, y, 16384);
					removeFlag(x, y - 1, 1024);
				}
			}

			if (type == 1 || type == 3) {
				if (orientation == 0) {
					removeFlag(x, y, 512);
					removeFlag(x - 1, y + 1, 8192);
				} else if (orientation == 1) {
					removeFlag(x, y, 2048);
					removeFlag(x + 1, y + 1, 32768);
				} else if (orientation == 2) {
					removeFlag(x, y, 8192);
					removeFlag(x + 1, y - 1, 512);
				} else if (orientation == 3) {
					removeFlag(x, y, 32768);
					removeFlag(x - 1, y - 1, 2048);
				}
			}

			if (type == 2) {
				if (orientation == 0) {
					removeFlag(x, y, 0x10400);
					removeFlag(x - 1, y, 4096);
					removeFlag(x, y + 1, 16384);
				} else if (orientation == 1) {
					removeFlag(x, y, 5120);
					removeFlag(x, y + 1, 16384);
					removeFlag(x + 1, y, 0x10000);
				} else if (orientation == 2) {
					removeFlag(x, y, 20480);
					removeFlag(x + 1, y, 0x10000);
					removeFlag(x, y - 1, 1024);
				} else if (orientation == 3) {
					removeFlag(x, y, 0x14000);
					removeFlag(x, y - 1, 1024);
					removeFlag(x - 1, y, 4096);
				}
			}
		}
	}

	public void removeObject(int orientation, int width, int initialX, int initialY, int length, boolean impenetrable) {
		int value = 256;
		if (impenetrable) {
			value += 0x20000;
		}
		initialX -= xOffset;
		initialY -= yOffset;
		if (orientation == 1 || orientation == 3) {
			int temp = width;
			width = length;
			length = temp;
		}

		for (int x = initialX; x < initialX + width; x++) {
			if (x >= 0 && x < this.width) {
				for (int y = initialY; y < initialY + length; y++) {
					if (y >= 0 && y < height) {
						removeFlag(x, y, value);
					}
				}
			}
		}
	}

	public void setUnwalkable(int x, int y) {
		x -= xOffset;
		y -= yOffset;
		adjacencies[x][y] |= 0x200000;
	}

	private void flag(int x, int y, int value) {
		adjacencies[x][y] |= value;
	}

	private void removeFlag(int x, int y, int value) {
		adjacencies[x][y] &= 0xffffff - value;
	}

}