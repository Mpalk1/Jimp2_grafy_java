import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        JFrame window = new JFrame("Jimp2 Grafy");
        window.setResizable(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(1600, 1000);
        window.setLocationRelativeTo(null);

        Graph graph = GraphParser.parseGraph("src/main/resources/graf6.csrrg");
        System.out.println(graph);

        //window.setVisible(true); // to zawsze na koncu
    }
}
