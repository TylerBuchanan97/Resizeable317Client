package com.jagex;

public class MouseCapturer implements Runnable {

	public int capturedCoordinateCount;
	public Client client;
	public int[] coordinatesX = new int[500];
	public int[] coordinatesY = new int[500];
	public boolean running = true;
	public Object synchronizedObject = new Object();

	public MouseCapturer(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		while (running) {
			synchronized (synchronizedObject) {
				if (capturedCoordinateCount < 500) {
					coordinatesX[capturedCoordinateCount] = client.mouseEventX;
					coordinatesY[capturedCoordinateCount] = client.mouseEventY;
					capturedCoordinateCount++;
				}
			}

			try {
				Thread.sleep(50L);
			} catch (Exception ex) {
			}
		}
	}

}