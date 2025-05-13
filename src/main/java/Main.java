import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args){

        JFrame window = new JFrame("Jimp2 Grafy");
        window.setResizable(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(1600, 1000);
        window.setLocationRelativeTo(null);

        Graph graph = new Graph();
        for(int i = 0; i < 5; i++){
            graph.addNode(new Node());
            graph.nodes.get(i).addConnection(1, 2, 3, 4, 5);
            graph.nodes.get(i).setIndex(i);
            graph.nodes.get(i).setNum_connections();
        }
        System.out.println(graph);
        //window.setVisible(true); // to zawsze na koncu
    }
}
