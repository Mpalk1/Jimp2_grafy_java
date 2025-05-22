import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SubgraphPanel extends JPanel {
    private final Graph graph;
    private final int subgraphIndex;
    private final Color color;

    public SubgraphPanel(Graph graph, int subgraphIndex, Color color) {
        this.graph = graph;
        this.subgraphIndex = subgraphIndex;
        this.color = color;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int[][] matrix = graph.getPositionMatrix();
        if (matrix == null) return;

        int rows = matrix.length;
        int cols = matrix[0].length;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        float cellWidth = (float) panelWidth / cols;
        float cellHeight = (float) panelHeight / rows;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2));

        ArrayList<Node> nodes = graph.getNodesInSubgraph(subgraphIndex);


        for (Node node : nodes) {
            int u = node.getIndex();
            int[] posU = graph.getNodePosition(u);
            if (posU == null) continue;

            float x1 = posU[1] * cellWidth + cellWidth / 2;
            float y1 = posU[0] * cellHeight + cellHeight / 2;

            for (int v : node.connections) {
                Node target = graph.getNodeByIndex(v);
                if (target == null || target.getSubgraph() != subgraphIndex) continue;
                int[] posV = graph.getNodePosition(v);
                if (posV == null) continue;

                float x2 = posV[1] * cellWidth + cellWidth / 2;
                float y2 = posV[0] * cellHeight + cellHeight / 2;
                g2.drawLine(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2));
            }
        }


        for (Node node : nodes) {
            int[] pos = graph.getNodePosition(node.getIndex());
            if (pos == null) continue;
            float x = pos[1] * cellWidth + cellWidth / 2;
            float y = pos[0] * cellHeight + cellHeight / 2;
            g2.drawOval(Math.round(x) - 5, Math.round(y) - 5, 10, 10);
        }
    }

}
