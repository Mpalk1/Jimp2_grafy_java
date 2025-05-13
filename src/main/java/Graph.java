import java.util.ArrayList;

public class Graph {
    ArrayList<Node> nodes;
    private int num_nodes;

    public Graph(){
        nodes = new ArrayList<>();
    }

    public void addNode(Node node){
        this.nodes.add(node);
    }

    public void calculateNum_nodes(){
        this.num_nodes = nodes.size();
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
