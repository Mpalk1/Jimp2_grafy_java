import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class DrawingPanel extends JPanel {
    private final Graph graph;

    public DrawingPanel(Graph graph) {
        this.graph = graph;
        setLayout(new GridLayout(getGridRows(), getGridCols()));
        initializeSubPanels();
    }

    private void initializeSubPanels() {
        int count = graph.getNum_subgraphs();
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            Color color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            SubgraphPanel panel = new SubgraphPanel(graph, i, color);
            panel.setBorder(BorderFactory.createTitledBorder("Podgraf " + i));
            add(panel);
        }
    }

    private int getGridCols() {
        int sg = graph.getNum_subgraphs();
        return (int) Math.ceil(Math.sqrt(sg));
    }

    private int getGridRows() {
        int sg = graph.getNum_subgraphs();
        return (int) Math.ceil((double) sg / getGridCols());
    }
}
