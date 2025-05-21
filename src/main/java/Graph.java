import java.util.ArrayList;

public class Graph {
    ArrayList<Node> nodes;
    private int num_nodes; // ilosc wezlow
    private int[][] positionMatrix; // macierz pozycji węzłów
    private int maxNodesInRow; // maksymalna liczba węzłów w wierszu

    public Graph(){
        this.nodes = new ArrayList<>();
    }

    public void addNode(Node node){
        this.nodes.add(node);
    }

    public void setNum_nodes(){
        this.num_nodes = nodes.size();
    }

    public int getNum_nodes(){
        return this.num_nodes;
    }

    public void setPositionMatrix(int rows, int cols) {
        this.positionMatrix = new int[rows][cols];
        // Inicjalizacja wszystkich pozycji jako puste (-1)
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
        return -1; // -1 oznacza brak węzła na tej pozycji
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

    // Metoda do wyświetlania macierzy pozycji
    public String displayPositionMatrix() {
        if (positionMatrix == null) {
            return "Macierz pozycji nie została zainicjalizowana";
        }

        StringBuilder sb = new StringBuilder("Macierz pozycji węzłów:\n");
        for (int[] matrix : positionMatrix) {
            for (int i : matrix) {
                if (i == -1) {
                    sb.append("-1 "); // puste miejsce
                } else {
                    sb.append(i).append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
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