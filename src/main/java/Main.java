import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static File GraphFile;
    public static File SubGraphFile;
    private static JFrame window;
    private static Graph graph;
    private static DrawingPanel drawingPanel;

    public static void main(String[] args) throws IOException {
        Graph graphtest = new Graph();
        GraphParser.parseGraphWithSubgraphs("C:/Users/macie/IdeaProjects/Jimp2_grafy_java/src/main/resources/graf6_2_10_F", graphtest);
        System.out.println(graphtest);
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