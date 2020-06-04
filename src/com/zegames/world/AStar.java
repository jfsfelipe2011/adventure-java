package com.zegames.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AStar {
    public static double lastime = System.currentTimeMillis();
    private static final Comparator<Node> nodeSorter = new Comparator<Node>() {
        @Override
        public int compare(Node n0, Node n1) {
            return Double.compare(n0.getfCost(), n1.getfCost());
        }
    };

    public static boolean clear() {
        return System.currentTimeMillis() - lastime >= 1000;
    }

    public static List<Node> findPath(World world, Vector2i start, Vector2i end) {
        lastime = System.currentTimeMillis();
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();

        Node current = new Node(start, null, 0, getDistance(start, end));

        openList.add(current);

        while (openList.size() > 0) {
            Collections.sort(openList, nodeSorter);
            current = openList.get(0);

            if (current.getTile().equals(end)) {
                List<Node> path = new ArrayList<>();

                while (current.getParent() != null) {
                    path.add(current);
                    current = current.getParent();
                }

                openList.clear();
                closedList.clear();

                return path;
            }

            openList.remove(current);
            closedList.add(current);

            for (int i = 0; i < 9; i++) {
                if (i == 4) {
                    continue;
                }

                int x = current.getTile().getX();
                int y = current.getTile().getY();
                int xi = (i % 3) - 1;
                int yi = (i / 3) - 1;
                Tile tile = World.tiles[x + xi + ((y + yi) * World.WIDTH)];

                if (tile == null || tile instanceof WallTile) {
                    continue;
                }

                if (i == 0) {
                    Tile test = World.tiles[x + xi + 1 + ((y + yi) * World.WIDTH)];
                    Tile test2 = World.tiles[x + xi + ((y + yi + 1) * World.WIDTH)];

                    if (test instanceof WallTile || test2 instanceof WallTile) {
                        continue;
                    }
                } else if (i == 2) {
                    Tile test = World.tiles[x + xi - 1 + ((y + yi) * World.WIDTH)];
                    Tile test2 = World.tiles[x + xi + ((y + yi + 1) * World.WIDTH)];

                    if (test instanceof WallTile || test2 instanceof WallTile) {
                        continue;
                    }
                } else if (i == 6) {
                    Tile test = World.tiles[x + xi + ((y + yi -1) * World.WIDTH)];
                    Tile test2 = World.tiles[x + xi + 1 + ((y + yi) * World.WIDTH)];

                    if (test instanceof WallTile || test2 instanceof WallTile) {
                        continue;
                    }
                } else if (i == 8) {
                    Tile test = World.tiles[x + xi + ((y + yi -1) * World.WIDTH)];
                    Tile test2 = World.tiles[x + xi - 1 + ((y + yi) * World.WIDTH)];

                    if (test instanceof WallTile || test2 instanceof WallTile) {
                        continue;
                    }
                }

                Vector2i a = new Vector2i(x + xi, y + yi);
                double gCost = current.getgCost() + getDistance(current.getTile(), a);
                double hConst = getDistance(a, end);

                Node node = new Node(a, current, gCost, hConst);

                if (vectorInList(closedList, a) && gCost >= current.getgCost()) {
                    continue;
                }

                if (!vectorInList(openList, a)) {
                    openList.add(node);
                } else if (gCost < current.getgCost()) {
                    openList.remove(current);
                    openList.add(node);
                }
            }
        }
        closedList.clear();
        return null;
    }

    private static double getDistance(Vector2i tile, Vector2i goal) {
        double dx = tile.getX() - goal.getX();
        double dy = tile.getY() - goal.getY();

        return Math.sqrt(dx * dx + dy * dx);
    }

    private static boolean vectorInList(List<Node> list, Vector2i vector) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTile().equals(vector)) {
                return true;
            }
        }

        return false;
    }
}
