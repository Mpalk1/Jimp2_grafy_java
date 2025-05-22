import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {

        JFrame window = new JFrame("Jimp2 Grafy");
        window.setResizable(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        window.setSize(1600, 1000);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());

        Graph graph = new Graph();
        GraphParser.parseGraphWithSubgraphs("src/main/resources/out.csrrg", graph);
        //GraphParser.parseGraph("src/main/resources/graf.csrrg", graph);
        System.out.println(graph);
        System.out.println(graph.getNum_subgraphs());
        System.out.println(graph.displayPositionMatrix());
        MenuPanel menuPanel = new MenuPanel();
        DrawingPanel drawingPanel = new DrawingPanel(graph);
        window.add(menuPanel, BorderLayout.NORTH);
        window.add(drawingPanel, BorderLayout.CENTER);


//        GraphParser.parseGraph("src/main/resources/graf.csrrg", graph);
//        GraphParser.parseGraphWithSubgraphs("src/main/resources/out.csrrg", graph);
//        System.out.println(graph.getNum_nodes());
//        System.out.println(graph);
//        System.out.println(graph.displayPositionMatrix());

        //window.pack();
        //window.validate();
        window.setVisible(true);
    }
}
