package com.zuhlke.skiingRouting;
import java.util.ArrayList;
import java.util.List;

class Node {
    private int x;
    private int y;
    private int altitude;
    private int pathDownFromPeak = 1;
    private int altitudeDropFromPeak = 0;
    private Node parent = null;

    public Node(int x, int y, int altitude) {
        this.x = x;
        this.y = y;
        this.altitude = altitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public int getPathDownFromPeak() {
        return pathDownFromPeak;
    }

    public int getAltitudeDropFromPeak() {
        return altitudeDropFromPeak;
    }

    public Node getParent() {
        return parent;
    }

    public void identifyBestNeighbourToSkiDownFrom(Node[][] nodeMap) {
        List<Node> neighbours = getNeighbours(nodeMap);
        neighbours.forEach(neighbour -> {
            if (neighbour.altitude > altitude) {
                if (hasBetterPathDownFromPeak(neighbour) || (hasEqualPathDwonFromPeak(neighbour) && hasBetterAltitudeDropFromPeak(neighbour))) {
                    parent = neighbour;
                    pathDownFromPeak = neighbour.pathDownFromPeak + 1;
                    altitudeDropFromPeak += (neighbour.altitude - altitude);
                }
            }
        });
    }

    private boolean hasEqualPathDwonFromPeak(Node neighbour) {
        return neighbour.pathDownFromPeak + 1 == pathDownFromPeak;
    }

    private boolean hasBetterPathDownFromPeak(Node neighbour) {
        return neighbour.pathDownFromPeak + 1 > pathDownFromPeak;
    }

    private boolean hasBetterAltitudeDropFromPeak(Node neighbour) {
        return (neighbour.altitudeDropFromPeak + neighbour.getAltitude() - altitude) > neighbour.altitudeDropFromPeak;
    }

    private List<Node> getNeighbours(Node[][] nodeMap) {
        List<Node> neighbours = new ArrayList<>();
        if (x > 0) {                    // top
            neighbours.add(nodeMap[x - 1][y]);
        }
        if (x < nodeMap.length - 1) {   // bottom
            neighbours.add(nodeMap[x + 1][y]);
        }
        if (y > 0) {                    // left
            neighbours.add(nodeMap[x][y - 1]);
        }
        if (y < nodeMap.length - 1) {   // right
            neighbours.add(nodeMap[x][y + 1]);
        }

        return neighbours;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]@%d -> %s", x, y, altitude, parent);
    }

}
