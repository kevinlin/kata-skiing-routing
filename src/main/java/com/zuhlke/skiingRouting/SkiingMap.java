package com.zuhlke.skiingRouting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class SkiingMap {
    private static final Comparator<Node> SORT_BY_ALTITUDE_DESC = (n1, n2) -> Integer.compare(n2.getAltitude(), n1.getAltitude());
    private static final Comparator<Node> SORT_BY_PATH_THEN_ALTITUDE_DROP_DESC = (n1, n2) -> {
        if (n2.getPathDownFromPeak() == n1.getPathDownFromPeak()) {
            return Integer.compare(n2.getAltitudeDropFromPeak(), n1.getAltitudeDropFromPeak());
        }
        return Integer.compare(n2.getPathDownFromPeak(), n1.getPathDownFromPeak());
    };

    private int[][] rawMap;
    private Node[][] nodeMap;

    public static void main(String[] args) throws IOException {
        SkiingMap skiingMap = new SkiingMap("./src/main/resources/map.txt");
        skiingMap.findLongestPathDown();
    }

    public SkiingMap(String filename) throws IOException {
        rawMap = Files.lines(Paths.get(filename)).skip(1).map(s -> Arrays.stream(s.split(" ")).mapToInt(Integer::parseInt).toArray()).toArray(int[][]::new);
        nodeMap = new Node[rawMap.length][rawMap[0].length];

        for (int x = 0; x < rawMap.length; x++) {
            for (int y = 0; y < rawMap[0].length; y++) {
                nodeMap[x][y] = new Node(x, y, rawMap[x][y]);
            }
        }
    }

    private Stream<Node> getNodesAsStream() {
        return Arrays.stream(nodeMap).flatMap(Arrays::stream);
    }

    private void findLongestPathDown() {
        getNodesAsStream().sorted(SORT_BY_ALTITUDE_DESC).forEachOrdered(node -> node.identifyBestNeighbourToSkiDownFrom(nodeMap));

        Node destination = getNodesAsStream().min(SORT_BY_PATH_THEN_ALTITUDE_DROP_DESC).orElse(null);
        Node startingPoint = destination;
        while (startingPoint.getParent() != null) {
            startingPoint = startingPoint.getParent();
        }
        System.out
                .println(String.format("Path: %d, Altitude Down: %d, Starting: %s, Ending: %s", destination.getPathDownFromPeak(), destination.getAltitudeDropFromPeak(), startingPoint, destination));
    }

    class Node {
        private int x;
        private int y;
        private int altitude;
        private int pathDownFromPeak = 1;
        private int altitudeDropFromPeak = 0;
        private Node parent = null;

        Node(int x, int y, int altitude) {
            this.x = x;
            this.y = y;
            this.altitude = altitude;
        }

        int getAltitude() {
            return altitude;
        }

        int getPathDownFromPeak() {
            return pathDownFromPeak;
        }

        int getAltitudeDropFromPeak() {
            return altitudeDropFromPeak;
        }

        Node getParent() {
            return parent;
        }

        void identifyBestNeighbourToSkiDownFrom(Node[][] nodeMap) {
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
}

