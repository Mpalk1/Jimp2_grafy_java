import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BinaryGraphParser {
    private static final int NEWLINE_MARKER = 0x16E360;// zgodnie z dokumentacja drugiego zespolu

    private static int readLeb128(InputStream is) throws IOException {
        int result = 0;
        int shift = 0;
        byte b;
        do {
            int nextByte = is.read();
            if (nextByte == -1) {
                throw new IOException("Błąd podczas odczytu LEB128.");
            }
            b = (byte) nextByte;
            result |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return result;
    }

    private static List<Integer> readDeltaEncodedLeb128List(InputStream is) throws IOException {
        List<Integer> decodedList = new ArrayList<>();
        int previousValue = 0;
        while (true) {
            int value;
            try {
                value = readLeb128(is);
            } catch (IOException e) {
                throw new IOException("Błąd odczytu listy LEB128 kodowanej delta: " + e.getMessage(), e);
            }

            if (value == NEWLINE_MARKER) {
                break;
            }
            int actualValue = previousValue + value;
            decodedList.add(actualValue);
            previousValue = actualValue;
        }
        return decodedList;
    }

//    private static List<Integer> decodeDeltaLEB128(byte[] data) {
//        List<Integer> result = new ArrayList<>();
//        int i = 0;
//        int prev = 0;
//        while (i < data.length) {
//            int shift = 0;
//            int value = 0;
//            while (true) {
//                int b = data[i++] & 0xFF;
//                value |= (b & 0x7F) << shift;
//                if ((b & 0x80) == 0) break;
//                shift += 7;
//            }
//            prev += value;
//            result.add(prev);
//        }
//        return result;
//    }

    private static List<Integer> readLeb128List(InputStream is) throws IOException {
        List<Integer> decodedList = new ArrayList<>();
        while (true) {
            int value;
            try {
                value = readLeb128(is);
            } catch (IOException e) {
                throw new IOException("Błąd odczytu listy LEB128: " + e.getMessage(), e);
            }

            if (value == NEWLINE_MARKER) {
                break;
            }
            decodedList.add(value);
        }
        return decodedList;
    }

    private static Map<String, Integer> readHeader(InputStream is) throws IOException {
        byte[] headerBytes = new byte[14];
        int bytesRead = 0;
        int offset = 0;
        while (offset < 14 && (bytesRead = is.read(headerBytes, offset, 14 - offset)) != -1) {
            offset += bytesRead;
        }

        if (offset != 14) {
            throw new IOException("Nieprawidłowy nagłówek pliku binarnego: oczekiwano 14 bajtów");
        }

        if (headerBytes[0] != 'K' || headerBytes[1] != 'W') {
            throw new IOException("Nieprawidłowa sygnatura pliku: oczekiwano 'KW'");
        }

        ByteBuffer buffer = ByteBuffer.wrap(headerBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int fileSize = buffer.getInt(2);
        buffer.getInt(6);
        int dataOffset = buffer.getInt(10);

        Map<String, Integer> headerInfo = new HashMap<>();
        headerInfo.put("fileSize", fileSize);
        headerInfo.put("dataOffset", dataOffset);
        return headerInfo;
    }

    public static void parseGraphWithSubgraphsBinary(String filename, Graph graph) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {

            Map<String, Integer> headerInfo = readHeader(is);
            int dataOffset = headerInfo.get("dataOffset");

            is.skip(dataOffset - 14);

            int maxNodesInRow = readLeb128(is);
            int newlineAfterMaxNodes = readLeb128(is);
            if (newlineAfterMaxNodes != NEWLINE_MARKER) {
                throw new IOException("Oczekiwano newline po maxNodesInRow");
            }
            graph.setMaxNodesInRow(maxNodesInRow);

            List<Integer> nodeIdsInRows = readLeb128List(is);
            List<Integer> rowStartIndices = readDeltaEncodedLeb128List(is);
            List<Integer> allConnectionsList = readLeb128List(is);

            Map<Integer, Node> nodeMap = new HashMap<>();
            Set<Integer> uniqueNodeIds = new HashSet<>();

            for (int nodeId : allConnectionsList) {
                uniqueNodeIds.add(nodeId);
            }
            for (int nodeId : nodeIdsInRows) {
                uniqueNodeIds.add(nodeId);
            }

            List<Integer> sortedUniqueNodeIds = new ArrayList<>(uniqueNodeIds);
            Collections.sort(sortedUniqueNodeIds);

            for (int nodeId : sortedUniqueNodeIds) {
                Node node = new Node();
                node.setIndex(nodeId);
                graph.addNode(node);
                nodeMap.put(nodeId, node);
            }

            List<List<Integer>> listOfSubgraphPointers = new ArrayList<>();
            while (is.available() > 0) {
                try {
                    List<Integer> currentSubgraphPointers = readDeltaEncodedLeb128List(is);
                    if (!currentSubgraphPointers.isEmpty()) {
                        listOfSubgraphPointers.add(currentSubgraphPointers);
                    } else if (is.available() == 0) {
                        break;
                    }
                } catch (IOException e) {
                    if (is.available() == 0) {
                        System.out.println("Osiągnięto koniec pliku podczas odczytu wskaźników podgrafów");
                        break;
                    }
                    System.err.println("Błąd odczytu linii wskaźników podgrafów: " + e.getMessage());
                    throw e;
                }
            }

            int subgraphId = 0;
            int totalSubgraphsCount = 0;
            int currentOffsetInAllConnectionsList = 0;

            for (List<Integer> currentLinePointers : listOfSubgraphPointers) {
                int nextSubgraphStartOffset = allConnectionsList.size();

                if (subgraphId + 1 < listOfSubgraphPointers.size()) {
                    List<Integer> nextLinePointers = listOfSubgraphPointers.get(subgraphId + 1);
                    if (!nextLinePointers.isEmpty()) {
                        nextSubgraphStartOffset = nextLinePointers.get(0);
                    }
                }
                List<Integer> effectivePointersForSubgraph = new ArrayList<>(currentLinePointers);
                effectivePointersForSubgraph.add(nextSubgraphStartOffset);

                for (int i = 0; i < effectivePointersForSubgraph.size() - 1; i++) {
                    int startIdx = effectivePointersForSubgraph.get(i);
                    int endIdx = effectivePointersForSubgraph.get(i + 1);

                    if (startIdx < currentOffsetInAllConnectionsList || startIdx >= allConnectionsList.size()) {
                        System.err.println("startIdx poza zakresem");
                        continue;
                    }
                    if (endIdx > allConnectionsList.size() || endIdx < startIdx) {
                         System.err.println("endIdx poza zakresem lub mniejszy niż startIdx");
                         endIdx = Math.min(endIdx, allConnectionsList.size());
                    }

                    int sourceId = allConnectionsList.get(startIdx);
                    Node sourceNode = nodeMap.get(sourceId);

                    if (sourceNode == null) {
                        System.err.println("Węzeł nie znaleziony w mapie węzłów");
                        continue;
                    }
                    sourceNode.setSubgraph(subgraphId);

                    for (int j = startIdx + 1; j < endIdx; j++) {
                        int targetId = allConnectionsList.get(j);
                        Node targetNode = nodeMap.get(targetId);
                        if (targetNode == null) {
                            System.err.println("Węzeł nie jest w mapie węzłów");
                            continue;
                        }

                        if (!sourceNode.connections.contains(targetId)) {
                            sourceNode.addConnection(targetId);
                        }
                        
                        if (!targetNode.connections.contains(sourceId)) {
                            targetNode.addConnection(sourceId);
                        }
                        targetNode.setSubgraph(subgraphId);
                    }
                }
                currentOffsetInAllConnectionsList = nextSubgraphStartOffset;
                subgraphId++;
                totalSubgraphsCount++;
            }

            for (Node node : graph.nodes) {
                node.setNum_connections();
            }
            graph.setNum_nodes();
            graph.setNum_subgraphs(totalSubgraphsCount);
            int rows = 0;
            if (!rowStartIndices.isEmpty()) {
                rows = 1;
                for (int i = 0; i < rowStartIndices.size() - 1; i++) {
                    rows++;
                    if (Objects.equals(rowStartIndices.get(i), rowStartIndices.get(i + 1))) {
                        rows++;
                    }
                }
            } else {
                rows = 0;
            }
            int cols = graph.getMaxNodesInRow();
            graph.setPositionMatrix(rows, cols);
            int currentRow = 0;
            for (int i = 0; i < nodeIdsInRows.size(); i++) {
                while (currentRow + 1 < rowStartIndices.size() && i >= rowStartIndices.get(currentRow + 1)) {
                    currentRow++;
                }
                int col = nodeIdsInRows.get(i);
                int nodeIdToPlace = nodeIdsInRows.get(i);
                if (nodeMap.containsKey(nodeIdToPlace)) {
                    graph.setNodePosition(currentRow, col, nodeIdToPlace);
                } else {
                    System.err.println("Węzeł z nodeIdsInRows nie jest w mapie węzłów");
                }
            }
        }
        System.out.println(graph);
    }
}