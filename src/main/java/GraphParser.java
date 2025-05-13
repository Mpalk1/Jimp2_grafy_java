import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphParser {

    public static Graph parseGraph(String filename) throws IOException {
        Graph graph = new Graph();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();

        String[] nodeIds = line.split(";");

        Map<Integer, Node> nodeMap = new HashMap<>();
        for (int i = 0; i < nodeIds.length; i++) {
            Node node = new Node();
            node.setIndex(i);
            graph.addNode(node);
            nodeMap.put(i, node);
        }

        String connectionsTemplate = reader.readLine();

        line = reader.readLine();
        if (line != null) {
            processConnections(line, connectionsTemplate, nodeMap);
        }


        for (Node node : graph.nodes) {
            node.setNum_connections();
        }

        graph.setNum_nodes();
        return graph;
    }


    private static void processConnections(String indexesLine, String connectionsLine, Map<Integer, Node> nodeMap) {
        String[] indexes = indexesLine.split(";");
        String[] connections = connectionsLine.split(";");

        int beginIdx = Integer.parseInt(indexes[0]);
        int indexPtr = 1;
        int connectionPtr = 0;

        while (true) {
            int endIdx;
            if (indexPtr < indexes.length) {
                endIdx = Integer.parseInt(indexes[indexPtr]);
            } else {
                endIdx = Integer.MAX_VALUE;
            }

            if (connectionPtr >= connections.length) {
                break;
            }

            int nodeIdx = Integer.parseInt(connections[connectionPtr++]);
            Node sourceNode = nodeMap.get(nodeIdx);

            for (int i = 0; i < endIdx - beginIdx - 1 && connectionPtr < connections.length; i++) {
                int targetIdx = Integer.parseInt(connections[connectionPtr++]);


                if (!sourceNode.connections.contains(targetIdx)) {
                    sourceNode.addConnection(targetIdx);
                }

                Node targetNode = nodeMap.get(targetIdx);
                if (!targetNode.connections.contains(nodeIdx)) {
                    targetNode.addConnection(nodeIdx);
                }
            }

            beginIdx = endIdx;
            indexPtr++;

            if (endIdx == Integer.MAX_VALUE) {
                break;
            }
        }
    }

}