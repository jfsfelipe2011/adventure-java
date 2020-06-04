package com.zegames.world;

public class Node {
    private final Vector2i tile;
    private final Node parent;
    private final double fCost;
    private final double gCost;
    private double hCost;

    public Node(Vector2i tile, Node parent, double gCost, double hCost) {
        this.tile = tile;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    public Vector2i getTile() {
        return tile;
    }

    public double getfCost() {
        return fCost;
    }

    public Node getParent() {
        return parent;
    }

    public double getgCost() {
        return gCost;
    }
}
