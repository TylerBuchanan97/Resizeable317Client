package com.tyb;

import com.jagex.Client;

import java.awt.*;

import static com.tyb.RelativeCoordinateProvider.getRelativeXForClipping;
import static com.tyb.RelativeCoordinateProvider.getRelativeYForClipping;

public class ScreenPolygonGenerator {

    public static Polygon getScreenPolygon(Client client) {
        Point topLeft = new Point(0, 0);
        Point topRight = new Point(client.frameWidth, 0);
        Point pointThree = new Point(client.frameWidth, getRelativeYForClipping(0));//TODO Name these
        Point pointFour = new Point(getRelativeXForClipping(516), getRelativeYForClipping(0));
        Point pointFive = new Point(getRelativeXForClipping(516), getRelativeYForClipping(338));
        Point pointSix = new Point(getRelativeXForClipping(0), getRelativeYForClipping(338));
        Point pointSeven = new Point(getRelativeXForClipping(0), client.frameHeight);
        Point pointEight = new Point(0, client.frameHeight);

        Point[] points = new Point[]{topLeft, topRight, pointThree, pointFour, pointFive, pointSix, pointSeven,
                pointEight};

        int[] xPoints = new int[points.length];
        int[] yPoints = new int[points.length];

        for (int x = 0; x < points.length; x++) {
            xPoints[x] = points[x].x;
            yPoints[x] = points[x].y;
        }

        return new Polygon(xPoints, yPoints, xPoints.length);
    }
}
