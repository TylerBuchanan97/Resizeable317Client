package com.jagex;

import java.awt.*;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@SuppressWarnings("serial")
public final class RSFrame extends Frame {

	// Frame_Sub1

	GameApplet applet;

	public RSFrame(GameApplet applet, int width, int height) {
		this.applet = applet;

		setTitle("Jagex");
		setBackground(Color.BLACK);
		setResizable(false);
		setVisible(true);
		toFront();
		setSize(width + 8, height + 28);
		setMinimumSize(new Dimension(width + 8, height + 28));

		addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);//Calling the super class function, as I don't know if it actually does anything right now.

                applet.frameHeight = getHeight() - 28;
                applet.frameWidth = getWidth() - 8;

                Client.instance.windowResized = true;

                System.out.println("height or width adjusted");
            }
        });
	}

	@Override
	public Graphics getGraphics() {
		Graphics graphics = super.getGraphics();
		graphics.translate(4, 24);
		return graphics;
	}

	@Override
	public final void paint(Graphics graphics) {
		applet.paint(graphics);
	}

	@Override
	public final void update(Graphics graphics) {
		applet.update(graphics);
	}

}