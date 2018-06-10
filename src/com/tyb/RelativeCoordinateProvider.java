package com.tyb;

import com.jagex.Client;

public class RelativeCoordinateProvider {

    public static int getRelativeXForClipping(int x) {
        return x + (Client.instance.frameWidth - Client.DEFAULT_WIDTH);
    }

    public static int getRelativeYForClipping(int y) {
        return y + (Client.instance.frameHeight - Client.DEFAULT_HEIGHT);
    }

    public static int getRelativeXForGameFrame(int x) {
        return x - (Client.instance.frameWidth - Client.DEFAULT_WIDTH);
    }

    public static int getRelativeYForGameFrame(int y) {
        return y - (Client.instance.frameHeight - Client.DEFAULT_HEIGHT);
    }
}
