import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class Main {
    public static File GraphFile;
    public static File SubGraphFile;
    private static JFrame window;
    private static Graph graph;
    private static DrawingPanel drawingPanel;

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.setErr(new PrintStream(System.err, true, "UTF-8"));
        window = new JFrame("Jimp2 Grafy");
        window.setResizable(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());

        graph = new Graph();
        MenuPanel menuPanel = new MenuPanel(createFileSelectionCallback());
        window.add(menuPanel, BorderLayout.NORTH);
        window.setVisible(true);
    }

    private static MenuPanel.FileSelectionCallback createFileSelectionCallback() {
        return (file, fileType) -> {
            try {
                if (drawingPanel != null) {
                    window.remove(drawingPanel);
                }

                if ("SubGraph".equals(fileType)) {
                    GraphParser.parseGraphWithSubgraphs(file.getAbsolutePath(), graph);
                    System.out.println("Loaded subgraphs: " + graph.getNum_subgraphs());
                } else if ("Graph".equals(fileType)) {
                    GraphParser.parseGraph(file.getAbsolutePath(), graph);
                    int numParts = 7;
                    int margin = 40;
                    GraphPartitioner.Options options = new GraphPartitioner.Options();
                    options.verbose = true;
                    options.force = true;
                    graph.setNum_nodes();
                    
                    if (GraphPartitioner.makeSubgraphs(graph, numParts, margin, options) && options.verbose) {
                        System.out.println("\nGraf podzielony.");
                        for (int i = 0; i < numParts; i++) {
                            ArrayList<Node> subgraphNodes = graph.getNodesInSubgraph(i);
                            System.out.printf("Podgraf %d: ", i);
                            for (Node node : subgraphNodes) {
                                System.out.printf("%d ", node.getIndex());
                            }
                            System.out.println();
                        }
                        graph.setNum_subgraphs(numParts);
                    } else if (options.verbose){
                        System.out.println("\nGraf nie został podzielony.");
                    }
                }

                drawingPanel = new DrawingPanel(graph);
                window.add(drawingPanel, BorderLayout.CENTER);
                window.revalidate();
                window.repaint();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(window, "Error loading file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
    }
}