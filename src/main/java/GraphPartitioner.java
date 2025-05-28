/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author wojko
 */
public class GraphPartitioner {
    private static void calculateSubgraphsSizes(Graph graph, int numParts, int[] sizes) {
        Arrays.fill(sizes, 0);
        for (Node node : graph.nodes) {
            if (node.getSubgraph() >= 0 && node.getSubgraph() < numParts) {
                sizes[node.getSubgraph()]++;
            }
        }
    }

    private static int countGain(Graph graph, int nodeIndex, int newPart) {
        int gain = 0;
        Node node = graph.getNodeByIndex(nodeIndex);
        if (node == null) return 0;

        int currentPart = node.getSubgraph();
        for (int neighborIndex : node.connections) {
            Node neighbor = graph.getNodeByIndex(neighborIndex);
            if (neighbor != null) {
                if (neighbor.getSubgraph() == currentPart) {
                    gain--;
                } else if (neighbor.getSubgraph() == newPart) {
                    gain++;
                }
            }
        }
        return gain;
    }

    private static boolean subgraphsAreBalanced(int[] sizes, int numParts, int ideal, int margin) {
        int allowedDiff = ideal * margin / 100;
        for (int i = 0; i < numParts; i++) {
            if (sizes[i] < ideal - allowedDiff || sizes[i] > ideal + allowedDiff) {
                return false;
            }
        }
        return true;
    }

    private static int countEdges(Graph graph) {
        int totalConnections = 0;
        for (Node node : graph.nodes) {
            totalConnections += node.getNum_connections();
        }
        return totalConnections / 2;
    }

    private static void subgraphsInit(Graph graph, int numParts, int margin) {
        int numNodes = graph.getNum_nodes();
        if (numParts <= 0 || numNodes == 0) return;

        int ideal = numNodes / numParts;
        int allowedDiff = ideal * margin / 100;
        int maxSize = ideal + allowedDiff;
        ArrayList<Node> nodes = new ArrayList<>(graph.nodes);
        nodes.sort((a, b) -> Integer.compare(b.getNum_connections(), a.getNum_connections()));
        int[] sizes = new int[numParts];
        boolean[] assigned = new boolean[numNodes];
        Queue<Integer>[] partQueues = new LinkedList[numParts];

        for (int i = 0; i < numParts; i++) {
            partQueues[i] = new LinkedList<>();
        }

        // Initial assignment
        for (int p = 0; p < numParts; p++) {
            for (Node node : nodes) {
                int idx = graph.nodes.indexOf(node);
                if (!assigned[idx] && node.getNum_connections() > 0) {
                    node.setSubgraph(p);
                    assigned[idx] = true;
                    sizes[p]++;
                    partQueues[p].add(idx);
                    break;
                }
            }
        }

        boolean changed;
        do {
            changed = false;
            for (int p = 0; p < numParts; p++) {
                if (sizes[p] >= maxSize || partQueues[p].isEmpty()) continue;

                Integer current = partQueues[p].poll();
                if (current == null) continue;  // Additional safety check

                Node currentNode = graph.nodes.get(current);
                for (int neighborIndex : currentNode.connections) {
                    if (!assigned[neighborIndex] && sizes[p] < maxSize) {
                        Node neighbor = graph.getNodeByIndex(neighborIndex);
                        neighbor.setSubgraph(p);
                        assigned[neighborIndex] = true;
                        sizes[p]++;
                        partQueues[p].add(neighborIndex);
                        changed = true;
                    }
                }
            }
        } while (changed);

        // Assign remaining nodes
        for (Node node : nodes) {
            int idx = graph.nodes.indexOf(node);
            if (!assigned[idx]) {
                int bestPart = -1;
                int maxConnections = -1;

                for (int p = 0; p < numParts; p++) {
                    if (sizes[p] >= maxSize) continue;

                    int connections = 0;
                    for (int neighborIndex : node.connections) {
                        Node neighbor = graph.getNodeByIndex(neighborIndex);
                        if (neighbor != null && neighbor.getSubgraph() == p) {
                            connections++;
                        }
                    }
                    if (connections > maxConnections ||
                            (connections == maxConnections && (bestPart == -1 || sizes[p] < sizes[bestPart]))) {
                        maxConnections = connections;
                        bestPart = p;
                    }
                }

                if (bestPart == -1) {
                    bestPart = 0;
                    for (int p = 1; p < numParts; p++) {
                        if (sizes[p] < sizes[bestPart]) {
                            bestPart = p;
                        }
                    }
                }
                node.setSubgraph(bestPart);
                sizes[bestPart]++;
            }
        }
    }

    private static void optimizeSubgraphs(Graph graph, int numParts, int margin, int[] sizes) {
        int iterations = 0;
        int OPTIMIZE_MAX_ITER = 100;
        int ideal = graph.getNum_nodes() / numParts;
        int allowedDiff = ideal * margin / 100;
        boolean improved;

        do {
            improved = false;
            List<Node> boundaryNodes = getBoundaryNodes(graph);

            for (Node node : boundaryNodes) {
                int nodeIndex = graph.nodes.indexOf(node);
                int currentPart = node.getSubgraph();
                
                if (sizes[currentPart] <= ideal - allowedDiff) continue;

                Set<Integer> adjacentParts = new HashSet<>();
                for (int neighborIndex : node.connections) {
                    int part = graph.getNodeByIndex(neighborIndex).getSubgraph();
                    if (part != currentPart) {
                        adjacentParts.add(part);
                    }
                }
                for (int targetPart : adjacentParts) {
                    if (sizes[targetPart] >= ideal + allowedDiff) continue;

                    int gain = countGain(graph, nodeIndex, targetPart);
                    boolean maintainsConnectivity = checkConnectivityAfterMove(graph, nodeIndex, currentPart);

                    if ((gain > 0 || sizes[currentPart] > ideal + allowedDiff/2) && maintainsConnectivity) {
                        node.setSubgraph(targetPart);
                        sizes[currentPart]--;
                        sizes[targetPart]++;
                        improved = true;
                        break;
                    }
                }
            }
            iterations++;
        } while (improved && iterations < OPTIMIZE_MAX_ITER);
    }

    private static List<Node> getBoundaryNodes(Graph graph) {
        List<Node> boundaryNodes = new ArrayList<>();
        for (Node node : graph.nodes) {
            int currentPart = node.getSubgraph();
            for (int neighborIndex : node.connections) {
                if (graph.getNodeByIndex(neighborIndex).getSubgraph() != currentPart) {
                    boundaryNodes.add(node);
                    break;
                }
            }
        }
        boundaryNodes.sort((a, b) -> {
            int extA = countExternalConnections(graph, a);
            int extB = countExternalConnections(graph, b);
            return Integer.compare(extB, extA);
        });
        return boundaryNodes;
    }

    private static int countExternalConnections(Graph graph, Node node) {
        int count = 0;
        int currentPart = node.getSubgraph();
        for (int neighborIndex : node.connections) {
            if (graph.getNodeByIndex(neighborIndex).getSubgraph() != currentPart) {
                count++;
            }
        }
        return count;
    }

    private static boolean checkConnectivityAfterMove(Graph graph, int nodeIndex, int part) {
        Set<Integer> partitionNodes = new HashSet<>();
        for (Node n : graph.nodes) {
            if (n.getSubgraph() == part && graph.nodes.indexOf(n) != nodeIndex) {
                partitionNodes.add(graph.nodes.indexOf(n));
            }
        }

        if (partitionNodes.isEmpty()) return true;

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        int start = partitionNodes.iterator().next();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            Node currentNode = graph.getNodeByIndex(current);

            for (int neighborIndex : currentNode.connections) {
                if (partitionNodes.contains(neighborIndex) && !visited.contains(neighborIndex)) {
                    visited.add(neighborIndex);
                    queue.add(neighborIndex);
                }
            }
        }
        return visited.size() == partitionNodes.size();
    }


    private static void fixDisconnectedComponents(Graph graph, int numParts, int margin) {
        int ideal = graph.getNum_nodes() / numParts;
        int allowedDiff = ideal * margin / 100;
        int[] sizes = new int[numParts];
        calculateSubgraphsSizes(graph, numParts, sizes);

        for (int p = 0; p < numParts; p++) {
            List<Set<Integer>> components = findConnectedComponents(graph, p);
            if (components.size() <= 1) continue;

            components.sort((a, b) -> Integer.compare(b.size(), a.size()));

            for (int i = 1; i < components.size(); i++) {
                Set<Integer> component = components.get(i);
                Map<Integer, Integer> adjacentConnections = new HashMap<>();

                for (int nodeIndex : component) {
                    Node node = graph.getNodeByIndex(nodeIndex);
                    for (int neighborIndex : node.connections) {
                        int neighborPart = graph.getNodeByIndex(neighborIndex).getSubgraph();
                        if (neighborPart != p) {
                            adjacentConnections.merge(neighborPart, 1, Integer::sum);
                        }
                    }
                }

                int bestPart = -1;
                int maxConnections = 0;
                for (Map.Entry<Integer, Integer> entry : adjacentConnections.entrySet()) {
                    int part = entry.getKey();
                    int connections = entry.getValue();
                    if (connections > maxConnections && sizes[part] + component.size() <= ideal + allowedDiff) {
                        maxConnections = connections;
                        bestPart = part;
                    }
                }

                if (bestPart == -1 && !adjacentConnections.isEmpty()) {
                    bestPart = adjacentConnections.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
                }
                if (bestPart != -1) {
                    for (int nodeIndex : component) {
                        graph.getNodeByIndex(nodeIndex).setSubgraph(bestPart);
                    }
                    sizes[p] -= component.size();
                    sizes[bestPart] += component.size();
                } else {
                    for (int nodeIndex : component) {
                        graph.getNodeByIndex(nodeIndex).setSubgraph(p);
                    }
                }
            }
        }
    }

    private static List<Set<Integer>> findConnectedComponents(Graph graph, int part) {
        List<Set<Integer>> components = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (Node node : graph.nodes) {
            int nodeIndex = graph.nodes.indexOf(node);
            if (node.getSubgraph() == part && !visited.contains(nodeIndex)) {
                Set<Integer> component = new HashSet<>();
                Queue<Integer> queue = new LinkedList<>();
                queue.add(nodeIndex);
                visited.add(nodeIndex);
                component.add(nodeIndex);

                while (!queue.isEmpty()) {
                    int current = queue.poll();
                    Node currentNode = graph.getNodeByIndex(current);

                    for (int neighborIndex : currentNode.connections) {
                        if (graph.getNodeByIndex(neighborIndex).getSubgraph() == part && 
                            !visited.contains(neighborIndex)) {
                            visited.add(neighborIndex);
                            component.add(neighborIndex);
                            queue.add(neighborIndex);
                        }
                    }
                }
                components.add(component);
            }
        }
        return components;
    }
    
    private static int removeSubgraphsConnections(Graph graph) {
        int cuts = 0;

        for (Node node : graph.nodes) {
            ArrayList<Integer> newConnections = new ArrayList<>();
            int currentPart = node.getSubgraph();

            for (int neighborIndex : node.connections) {
                Node neighbor = graph.getNodeByIndex(neighborIndex);
                if (neighbor != null && neighbor.getSubgraph() == currentPart) {
                    newConnections.add(neighborIndex);
                } else {
                    cuts++;
                }
            }

            if (newConnections.size() != node.connections.size()) {
                node.connections = newConnections;
                node.setNum_connections();
            }
        }
        return cuts / 2;
    }

    private static boolean partitionsConnected(Graph graph, int numParts) {
        int numNodes = graph.getNum_nodes();
        if (numParts <= 0 || numNodes == 0) return true;

        for (int p = 0; p < numParts; p++) {
            Set<Integer> partitionNodes = new HashSet<>();
            int startNode = -1;

            for (int i = 0; i < numNodes; i++) {
                if (graph.nodes.get(i).getSubgraph() == p) {
                    partitionNodes.add(i);
                    if (startNode == -1) {
                        startNode = i;
                    }
                }
            }

            if (partitionNodes.isEmpty()) continue;
            if (startNode == -1) return false;

            Set<Integer> visited = new HashSet<>();
            Queue<Integer> queue = new LinkedList<>();
            queue.add(startNode);
            visited.add(startNode);

            while (!queue.isEmpty()) {
                int current = queue.poll();
                Node currentNode = graph.nodes.get(current);

                for (int neighborIndex : currentNode.connections) {
                    if (partitionNodes.contains(neighborIndex) && !visited.contains(neighborIndex)) {
                        visited.add(neighborIndex);
                        queue.add(neighborIndex);
                    }
                }
            }

            if (visited.size() != partitionNodes.size()) {
                return false;
            }
        }

        return true;
    }
    
    public static class Options {
        public boolean verbose = false;
        public boolean force = false;
    }

    public static boolean makeSubgraphs(Graph graph, int numParts, int margin, Options opts) {
        int numNodes = graph.getNum_nodes();
        if (numParts <= 0 || numNodes == 0) {
            return true;
        }
        int[] sizes = new int[numParts];
        int ideal = numNodes / numParts;

        if (opts.verbose) {
            System.out.println("*****");
            System.out.printf("Ilość krawędzi grafu: %d\n", countEdges(graph));
            System.out.println("*****");
        }

        subgraphsInit(graph, numParts, margin);
        optimizeSubgraphs(graph, numParts, margin, sizes);
        fixDisconnectedComponents(graph, numParts, margin);

        if (!partitionsConnected(graph, numParts) && !opts.force) {
            System.err.println("ERROR: Podgrafy nie są spójne.");
            return false;
        }

        int cuts = removeSubgraphsConnections(graph);

        if (opts.verbose) {
            System.out.println("*****");
            System.out.printf("Ilość krawędzi grafu po przecięciach: %d\n", countEdges(graph));
            System.out.printf("Ilość przeciętych krawędzi: %d\n", cuts);
            int[] partCounts = new int[numParts];
            for (Node node : graph.nodes) {
                int part = node.getSubgraph();
                if (part >= 0 && part < numParts) {
                    partCounts[part]++;
                }
            }
            System.out.print("Ilość węzłów w podgrafach: ");
            for (int p = 0; p < numParts; p++) {
                System.out.printf("%d:%d ", p, partCounts[p]);
            }
            System.out.println();
        }

        calculateSubgraphsSizes(graph, numParts, sizes);
        if (!subgraphsAreBalanced(sizes, numParts, ideal, margin)) {
            if (!opts.force) {
                System.err.println("ERROR: Różnica wielkości podgrafów przekracza margines.");
                return false;
            } else if (opts.verbose) {
                System.out.println("Różnica pomiędzy podgrafami nie mieści się w marginesie, kontynuowanie.");
            }
        } else if (opts.verbose) {
            System.out.println("Różnica pomiędzy podgrafami mieści się w marginesie.");
        }
   
        if (!partitionsConnected(graph, numParts)) {
            if (!opts.force) {
                System.err.println("ERROR: Podgrafy nie są spójne.");
                return false;
            } else if (opts.verbose) {
                System.out.println("Podgrafy nie są spójne, kontynuowanie.");
            }
        } else if (opts.verbose) {
            System.out.println("Podgrafy są spójne.");
        }

        return true;
    }
}
