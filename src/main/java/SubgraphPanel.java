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

        int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE;

        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                if (matrix[row][col] != -1) {
                    minRow = Math.min(minRow, row);
                    maxRow = Math.max(maxRow, row);
                    minCol = Math.min(minCol, col);
                    maxCol = Math.max(maxCol, col);
                }
            }
        }

        if (minRow == Integer.MAX_VALUE) return;

        int rows = maxRow - minRow + 1;
        int cols = maxCol - minCol + 1;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        float cellWidth = (float) panelWidth / cols;
        float cellHeight = (float) panelHeight / rows;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2));

        Font font = new Font("Arial", Font.BOLD, 12);
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();

        ArrayList<Node> nodes = graph.getNodesInSubgraph(subgraphIndex);

        for (Node node : nodes) {
            int[] posU = graph.getNodePosition(node.getIndex());
            if (posU == null) continue;

            float x1 = (posU[1] - minCol) * cellWidth + cellWidth / 2;
            float y1 = (posU[0] - minRow) * cellHeight + cellHeight / 2;

            for (int v : node.connections) {
                Node target = graph.getNodeByIndex(v);
                if (target == null || target.getSubgraph() != subgraphIndex) continue;

                int[] posV = graph.getNodePosition(v);
                if (posV == null) continue;

                float x2 = (posV[1] - minCol) * cellWidth + cellWidth / 2;
                float y2 = (posV[0] - minRow) * cellHeight + cellHeight / 2;
                g2.drawLine(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2));
            }
        }

        for (Node node : nodes) {
            int[] pos = graph.getNodePosition(node.getIndex());
            if (pos == null) continue;

            float x = (pos[1] - minCol) * cellWidth + cellWidth / 2;
            float y = (pos[0] - minRow) * cellHeight + cellHeight / 2;

            g2.drawOval(Math.round(x) - 10, Math.round(y) - 10, 20, 20);

            String indexStr = String.valueOf(node.getIndex());
            int textWidth = metrics.stringWidth(indexStr);
            int textHeight = metrics.getHeight();

            g2.setColor(Color.BLACK);
            g2.drawString(indexStr, Math.round(x) - textWidth/2, Math.round(y) + textHeight/4);
            g2.setColor(color);
        }
    }
}