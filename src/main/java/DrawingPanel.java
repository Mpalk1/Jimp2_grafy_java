import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class DrawingPanel extends JPanel {
    private final Graph graph;
    private boolean isMaximized = false;
    private SubgraphPanel maximizedPanel = null;
    private Color[] subgraphColors; // Store colors for each subgraph

    public DrawingPanel(Graph graph) {
        this.graph = graph;
        generateColors(); // Generate colors once
        setLayout(new GridLayout(getGridRows(), getGridCols()));
        initializeSubPanels();
    }

    private void generateColors() {
        int count = graph.getNum_subgraphs();
        subgraphColors = new Color[count];
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            subgraphColors[i] = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        }
    }

    private void initializeSubPanels() {
        removeAll();

        int count = graph.getNum_subgraphs();

        for (int i = 0; i < count; i++) {
            SubgraphPanel panel = new SubgraphPanel(graph, i, subgraphColors[i]);
            panel.setBorder(BorderFactory.createTitledBorder("Podgraf " + i));
            add(panel);
        }

        revalidate();
        repaint();
    }

    public SubgraphPanel getMaximizedPanel() {
        return maximizedPanel;
    }

    public int getGridRows() {
        int sg = graph.getNum_subgraphs();
        return (int) Math.ceil(Math.sqrt(sg));
    }

    public int getGridCols() {
        int sg = graph.getNum_subgraphs();
        return (int) Math.ceil((double) sg / getGridRows());
    }

    public void maximizePanel(SubgraphPanel panel) {
        removeAll();
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        panel.resetButton.setVisible(true);
        this.maximizedPanel = panel;
        this.isMaximized = true;

        revalidate();
        repaint();
    }

    public void restoreOriginalLayout() {
        removeAll();
        setLayout(new GridLayout(getGridRows(), getGridCols()));

        int count = graph.getNum_subgraphs();

        for (int i = 0; i < count; i++) {
            SubgraphPanel panel = new SubgraphPanel(graph, i, subgraphColors[i]);
            panel.setBorder(BorderFactory.createTitledBorder("Podgraf " + i));
            add(panel);
        }

        this.maximizedPanel = null;
        this.isMaximized = false;

        revalidate();
        repaint();
    }
}