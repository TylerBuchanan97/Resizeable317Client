package com.jagex;

import com.tyb.ScreenPolygonGenerator;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import static com.tyb.RelativeCoordinateProvider.*;

public final class ProducingGraphicsBuffer implements ImageProducer, ImageObserver {

	public int height;
	public Image image;
	public int[] pixels;
	public int width;
	ImageConsumer consumer;
	ColorModel model;

	public ProducingGraphicsBuffer(Component component, int width, int height) {
		this.width = width;
		this.height = height;

		pixels = new int[width * height];
		model = new DirectColorModel(32, 0xFF0000, 65280, 255);
		image = component.createImage(this);

		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		initializeRasterizer();
	}

	@Override
	public synchronized void addConsumer(ImageConsumer consumer) {
		this.consumer = consumer;
		consumer.setDimensions(width, height);
		consumer.setProperties(null);
		consumer.setColorModel(model);
		consumer.setHints(14);
	}

	public void drawImage(Graphics graphics, int x, int y) {
		setPixels();

		graphics.drawImage(image, getRelativeXForClipping(x), getRelativeYForClipping(y), this);
	}

    public void drawGameImage(Graphics graphics, int x, int y) {
        setPixels();

        int frameHeight = Client.instance.frameHeight;
        int frameWidth = Client.instance.frameWidth;

        if (frameHeight <= Client.DEFAULT_HEIGHT && frameWidth <= Client.DEFAULT_WIDTH) {
            int[] xPoints = new int[]{0, 516, 516, 0};
            int[] yPoints = new int[]{0, 0, 338, 338};
            Polygon polygon = new Polygon(xPoints, yPoints, xPoints.length);
            graphics.setClip(polygon);
        } else {
            Polygon polygon = ScreenPolygonGenerator.getScreenPolygon(Client.instance);
            graphics.setClip(polygon);
        }

        graphics.drawImage(image, x, y, this);

        graphics.setClip(null);
    }

	@Override
	public boolean imageUpdate(Image image, int flags, int x, int y, int width, int height) {
		return true;
	}

	public void initializeRasterizer() {
		Raster.init(height, width, pixels);
	}

	@Override
	public synchronized boolean isConsumer(ImageConsumer consumer) {
		return this.consumer == consumer;
	}

	@Override
	public synchronized void removeConsumer(ImageConsumer consumer) {
		if (this.consumer == consumer) {
			this.consumer = null;
		}
	}

	@Override
	public void requestTopDownLeftRightResend(ImageConsumer consumer) {
		System.out.println("TDLR");
	}

	public synchronized void setPixels() {
		if (consumer == null) {
			return;
		}

		consumer.setPixels(0, 0, width, height, model, pixels, 0, width);
		consumer.imageComplete(2);
	}

	@Override
	public void startProduction(ImageConsumer consumer) {
		addConsumer(consumer);
	}

}