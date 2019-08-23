package com.zuhlke.skiingRouting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
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

        Node bestRouteDestination = getNodesAsStream().min(SORT_BY_PATH_THEN_ALTITUDE_DROP_DESC).orElse(null);
        System.out.println(bestRouteDestination);
    }
}

