import java.util.ArrayList;

public class Graph {
    ArrayList<Node> nodes;
    private int num_nodes; // ilosc wezlow
    private int[][] positionMatrix; // macierz pozycji węzłów
    private int maxNodesInRow; // maksymalna liczba węzłów w wierszu
    private int num_subgraphs;

    public Graph() {
        this.nodes = new ArrayList<>();
    }

    public int getNum_subgraphs() {
        return this.num_subgraphs;
    }

    public void setNum_subgraphs(int sg) {
        this.num_subgraphs = sg;
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public void setNum_nodes() {
        this.num_nodes = nodes.size();
    }

    public int getNum_nodes() {
        return this.num_nodes;
    }

    public void setPositionMatrix(int rows, int cols) {
        this.positionMatrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                positionMatrix[i][j] = -1;
            }
        }
    }

    public void setNodePosition(int row, int col, int nodeIndex) {
        if (row >= 0 && row < positionMatrix.length && col >= 0 && col < positionMatrix[0].length) {
            positionMatrix[row][col] = nodeIndex;
        }
    }

    public int getNodeAtPosition(int row, int col) {
        if (row >= 0 && row < positionMatrix.length && col >= 0 && col < positionMatrix[0].length) {
            return positionMatrix[row][col];
        }
        return -1;
    }

    public void setMaxNodesInRow(int max) {
        this.maxNodesInRow = max;
    }

    public int getMaxNodesInRow() {
        return this.maxNodesInRow;
    }

    public int[][] getPositionMatrix() {
        return this.positionMatrix;
    }

    public String displayPositionMatrix() {
        if (positionMatrix == null) {
            return "Macierz pozycji nie została zainicjalizowana";
        }

        StringBuilder sb = new StringBuilder("Macierz pozycji węzłów:\n");
        for (int[] matrix : positionMatrix) {
            for (int i : matrix) {
                if (i == -1) {
                    sb.append("-1 ");
                } else {
                    sb.append(i).append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public ArrayList<Node> getNodesInSubgraph(int subgraphIndex) {
        ArrayList<Node> result = new ArrayList<>();
        for (Node node : nodes) {
            if (node.getSubgraph() == subgraphIndex) {
                result.add(node);
            }
        }
        return result;
    }

    public Node getNodeByIndex(int index) {
        for (Node node : nodes) {
            if (node.getIndex() == index) return node;
        }
        return null;
    }

    public int[] getNodePosition(int nodeIndex) {
        for (int row = 0; row < positionMatrix.length; row++) {
            for (int col = 0; col < positionMatrix[0].length; col++) {
                if (positionMatrix[row][col] == nodeIndex) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    public ArrayList<Integer> getConnections(int nodeIndex) {
        Node node = getNodeByIndex(nodeIndex);
        return node != null ? node.connections : new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            sb.append(node.toString());
        }
        return sb.toString();
    }
}