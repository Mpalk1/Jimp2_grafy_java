import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {

        JFrame window = new JFrame("Jimp2 Grafy");
        window.setResizable(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(1600, 1000);
        window.setLocationRelativeTo(null);

        //Graph graph = GraphParser.parseGraph("src/main/resources/graf.csrrg");
        Graph graph = GraphParser.parseGraphWithSubgraphs("src/main/resources/out.csrrg");
        System.out.println(graph.getNum_nodes());
        System.out.println(graph);
        System.out.println(graph.displayPositionMatrix());



        //window.setVisible(true);
    }
}
