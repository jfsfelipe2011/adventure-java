package com.zegames.world;

public class Vector2i {
    private final int x;
    private final int y;

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equals(Object object) {
        Vector2i vector2i = (Vector2i) object;

        return vector2i.getX() == this.x && vector2i.getY() == this.y;
    }
}
