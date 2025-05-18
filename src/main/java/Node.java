import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Node {
    ArrayList<Integer> connections; // tablica z indeksami polaczen
    private int num_connections; // liczba polaczen
    private int subgraph; // to ktorego podgrafu nalezy
    private int index;

    public Node(){
        this.connections = new ArrayList<>();
    }

    public void addConnection(int node_index){
        if (!this.connections.contains(node_index)) {
            this.connections.add(node_index);
        }
    }
    public void addConnection(int... node_index){
        for(int index: node_index){
            if (!this.connections.contains(index)) {
                this.connections.add(index);
            }
        }
    }
    public void setNum_connections(){
        this.num_connections = this.connections.size();
    }
    public int getNum_connections(){
        return this.num_connections;
    }
    public void setSubgraph(int subgraph){
        this.subgraph = subgraph;
    }
    public int getSubgraph(){
        return this.subgraph;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        String connectionsString = connections.stream().map(String::valueOf).collect(Collectors.joining(" "));
        return "Wezel: " + this.index + "(" + this.num_connections + ") [podgraf " + this.subgraph + "], polaczenia: " + connectionsString + "\n";
    }



}
