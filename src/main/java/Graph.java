import java.util.ArrayList;

public class Graph {
    ArrayList<Node> nodes;
    private int num_nodes; // ilosc wezlow

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            sb.append(node.toString());
        }
        return sb.toString();
    }
}
