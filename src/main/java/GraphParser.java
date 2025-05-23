import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GraphParser {

    public static void parseGraph(String filename, Graph graph) throws IOException { // do plikow csrrg

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            graph.setMaxNodesInRow(Integer.parseInt(reader.readLine()));

            String line = reader.readLine();
            String[] nodeIds = line.split(";");

            String[] rowStartIndicesStr = reader.readLine().split(";");
            List<Integer> rowStartIndices = Arrays.stream(rowStartIndicesStr).map(String::trim).map(Integer::parseInt).toList();

            Map<Integer, Node> nodeMap = new HashMap<>();
            for (int i = 0; i < nodeIds.length; i++) {
                Node node = new Node();
                node.setIndex(i);
                graph.addNode(node);
                nodeMap.put(i, node);
            }

            String connectionsTemplate = reader.readLine(); // linia 4

            line = reader.readLine(); // linia 5
            if (line != null) {
                processConnections(line, connectionsTemplate, nodeMap);
            }

            for (Node node : graph.nodes) {
                node.setNum_connections();
            }

            int rows = 0;
            for (int i = 0; i < rowStartIndices.size() - 1; i++) {
                rows++;
                if (Objects.equals(rowStartIndices.get(i), rowStartIndices.get(i + 1))) {
                    rows++;
                }
            }

            int cols = graph.getMaxNodesInRow();
            graph.setPositionMatrix(rows, cols);

            int currentRow = 0;
            for (int i = 0; i < nodeIds.length; i++) {
                while (currentRow + 1 < rowStartIndices.size() && i >= rowStartIndices.get(currentRow + 1)) {
                    currentRow++;
                }
                int col = Integer.parseInt(nodeIds[i].trim());
                graph.setNodePosition(currentRow, col, i);
            }
            graph.setNum_subgraphs(1);
        }
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

            if (sourceNode == null) {
                beginIdx = endIdx;
                indexPtr++;
                continue;
            }

            for (int i = 0; i < endIdx - beginIdx - 1 && connectionPtr < connections.length; i++) {
                int targetIdx = Integer.parseInt(connections[connectionPtr++]);
                if (!sourceNode.connections.contains(targetIdx)) {
                    sourceNode.addConnection(targetIdx);
                }
                Node targetNode = nodeMap.get(targetIdx);
                if (targetNode != null && !targetNode.connections.contains(nodeIdx)) {
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

    public static void parseGraphWithSubgraphs(String filename, Graph graph) throws IOException { // Parsowanie plikow tekstowych od 2 zespolu
        List<String> lines = Files.readAllLines(Paths.get(filename));

        String[] allNodes = lines.get(3).split(";");
        Map<Integer, Node> nodeMap = new HashMap<>();

        for (String allNode : allNodes) {
            int nodeId = Integer.parseInt(allNode.trim());
            if (!nodeMap.containsKey(nodeId)) {
                Node node = new Node();
                node.setIndex(nodeId);
                graph.addNode(node);
                nodeMap.put(nodeId, node);
            }
        }

        List<Integer> allPointers = new ArrayList<>();
        for (int lineIdx = 4; lineIdx < lines.size(); lineIdx++) {
            String line = lines.get(lineIdx).trim();
            if (line.isEmpty()) continue;
            String[] pointers = line.split(";");
            for (String ptr : pointers) {
                try {
                    int pointer = Integer.parseInt(ptr.trim());
                    allPointers.add(pointer);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid pointer: " + ptr);
                }
            }
        }
        allPointers.add(allNodes.length);
        int currentPointerIdx = 0;
        int subgraphId = 0;

        for (int lineIdx = 4; lineIdx < lines.size(); lineIdx++) {
            String line = lines.get(lineIdx).trim();
            if (line.isEmpty()) continue;

            String[] pointers = line.split(";");
            int pointersInLine = pointers.length;

            for (int i = 0; i < pointersInLine; i++) {
                int startIdx = allPointers.get(currentPointerIdx);
                int endIdx = (currentPointerIdx + 1 < allPointers.size()) ? allPointers.get(currentPointerIdx + 1) : allNodes.length;

                if (startIdx >= allNodes.length) {
                    currentPointerIdx++;
                    continue;
                }

                int sourceId = Integer.parseInt(allNodes[startIdx].trim());
                Node sourceNode = nodeMap.get(sourceId);
                if (sourceNode == null) {
                    currentPointerIdx++;
                    continue;
                }

                sourceNode.setSubgraph(subgraphId);

                for (int j = startIdx + 1; j < endIdx && j < allNodes.length; j++) {
                    int targetId = Integer.parseInt(allNodes[j].trim());
                    Node targetNode = nodeMap.get(targetId);
                    if (targetNode == null) continue;
                    if (!sourceNode.connections.contains(targetId)) {
                        sourceNode.addConnection(targetId);
                    }
                    if (!targetNode.connections.contains(sourceId)) {
                        targetNode.addConnection(sourceId);
                    }
                    targetNode.setSubgraph(subgraphId);
                }
                currentPointerIdx++;
            }
            subgraphId++;
        }

        for (Node node : graph.nodes) {
            node.setNum_connections();
        }
        graph.setNum_nodes();

        graph.setMaxNodesInRow(Integer.parseInt(lines.getFirst()));
        String[] nodeIds = lines.get(1).split(";");
        String[] rowStartIndicesStr = lines.get(2).split(";");
        List<Integer> rowStartIndices = Arrays.stream(rowStartIndicesStr).map(String::trim).map(Integer::parseInt).toList();

        int rows = 0;
        for (int i = 0; i < rowStartIndices.size() - 1; i++) {
            rows++;
            if (Objects.equals(rowStartIndices.get(i), rowStartIndices.get(i + 1))) {
                rows++;
            }
        }
        int cols = graph.getMaxNodesInRow();
        graph.setPositionMatrix(rows, cols);

        int currentRow = 0;
        for (int i = 0; i < nodeIds.length; i++) {
            while (currentRow + 1 < rowStartIndices.size() && i >= rowStartIndices.get(currentRow + 1)) {
                currentRow++;
            }
            int col = Integer.parseInt(nodeIds[i].trim());
            graph.setNodePosition(currentRow, col, i);
        }
        graph.setNum_subgraphs(lines.size() - 4);
    }
}