package main;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class Digraph {
    public static final String FILLCOLOR = "fillcolor";
    public static final String COLOR = "color";
    public static final String LABEL = "label";
    public static final String STYLE = "style";
    public static final String FONTCOLOR = "fontcolor";
    private static final String ESCAPE_N = "\"\\N\"";

    private Map<Integer, Map<String, String>> nodes;
    private Map<Integer, Map<Integer, Map<String, String>>> edges;
    private Set<Integer> edgedNodes;
    private Map<String, String> globalNodeAttrs;
    private Map<String, String> globalEdgeAttrs;

    public Digraph(){
        this.nodes = new HashMap<Integer, Map<String, String>>();
        this.edges = new HashMap<Integer, Map<Integer, Map<String, String>>>();
        this.edgedNodes = new HashSet<Integer>();
        this.globalNodeAttrs = new HashMap<String, String>();
        this.globalEdgeAttrs = new HashMap<String, String>();
    }

    public boolean containsNode(int node){
        return this.nodes.containsKey(node);
    }
    public boolean containsEdge(int from, int to){
        return this.edges.containsKey(from) && this.edges.get(from).containsKey(to);
    }

    public void addNode(int node){
        if(!this.nodes.containsKey(node)){
            this.nodes.put(node, new HashMap<String, String>());
        }
    }

    public void addEdge(int from, int to){
        if(!this.edges.containsKey(from)){
            this.edges.put(from, new HashMap<Integer, Map<String, String>>());
        }
        Map<Integer, Map<String, String>> toEdges = this.edges.get(from);
        if(!toEdges.containsKey(to)){
            toEdges.put(to, new HashMap<String, String>());
        }
        addNode(from);
        addNode(to);
        this.edgedNodes.add(from);
        this.edgedNodes.add(to);
    }

    public void setNodeAttr(int node, String key, String value){
        if(key.equals(LABEL) && value.contains("\n")){
            this.globalNodeAttrs.put(LABEL, ESCAPE_N);
        }
        this.nodes.get(node).put(key, value);
    }
    public void setEdgeAttr(int from, int to, String key, String value){
        if(key.equals(LABEL) && value.contains("\n")){
            this.globalEdgeAttrs.put(LABEL, ESCAPE_N);
        }
        this.edges.get(from).get(to).put(key, value);
    }

    public void setNodeAttrs(int node, Map<String, String> attrs){
        for(Map.Entry<String, String> e : attrs.entrySet()){
            this.setNodeAttr(node, e.getKey(), e.getValue());
        }
    }
    public void setEdgeAttrs(int from, int to, Map<String, String> attrs){
        for(Map.Entry<String, String> e : attrs.entrySet()){
            this.setEdgeAttr(from, to, e.getKey(), e.getValue());
        }
    }
    public void clearNodeAttr(int node, String key){
        this.nodes.get(node).remove(key);
    }
    public void clearEdgeAttr(int from, int to, String key){
        this.edges.get(from).get(to).remove(key);
    }
    public void setGlobalNodeAttr(String key, String value){
        this.globalNodeAttrs.put(key, value);
    }
    public void setGlobalEdgeAttr(String key, String value){
        this.globalEdgeAttrs.put(key, value);
    }

    private String convertMap(Map<String, String> m){
        List<String> buf = new ArrayList<String>();
        for(Map.Entry<String, String> e : m.entrySet()){
            buf.add(e.getKey() + "=" + e.getValue());
        }
        return "[" + Util.join(", ", buf) + "]";
    }

    private Map<String, String> escape(Map<String, String> m){
        Map<String, String> ret = new HashMap<String, String>();
        for(Map.Entry<String, String> e : m.entrySet()){
            String key = e.getKey();
            String value = e.getValue();

            if(key.equals(LABEL)){
                value = "\"" + value + "\"";
            }
            ret.put(key, value);
        }
        return ret;
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("strict digraph {\n");
        if(this.globalNodeAttrs.size() > 0){
            sb.append("    node " + convertMap(this.globalNodeAttrs) + ";\n");
        }
        if(this.globalEdgeAttrs.size() > 0){
            sb.append("    edge " + convertMap(this.globalEdgeAttrs) + ";\n");
        }

        for(Map.Entry<Integer, Map<String, String>> e : this.nodes.entrySet()){
            int node = e.getKey();
            Map<String, String> attrs = e.getValue();
            if(attrs.size() > 0){
                Map<String, String> _attrs = attrs;
                if(this.globalNodeAttrs.containsKey(LABEL) && this.globalNodeAttrs.get(LABEL).equals(ESCAPE_N)){
                    _attrs = escape(attrs);
                }
                sb.append("    " + node + " " + convertMap(_attrs) + ";\n");
            }else if(!this.edgedNodes.contains(node)){
                sb.append("    " + node + ";\n");
            }
        }

        for(Map.Entry<Integer, Map<Integer, Map<String, String>>> e : this.edges.entrySet()){
            int from = e.getKey();
            for(Map.Entry<Integer, Map<String, String>> e2 : e.getValue().entrySet()){
                int to = e2.getKey();
                sb.append("    " + from + " -> " + to);
                Map<String, String> attrs = e2.getValue();
                if(attrs.size() > 0){
                    Map<String, String> _attrs = attrs;
                    if(this.globalEdgeAttrs.containsKey(LABEL) && this.globalEdgeAttrs.get(LABEL).equals(ESCAPE_N)){
                        _attrs = escape(attrs);
                    }
                    sb.append(" " + convertMap(_attrs));
                }
                sb.append(";\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public void writeDot(String filename) {
        try {
            PrintStream ps = new PrintStream(filename);
            ps.print(this.toString());
            ps.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Digraph g = new Digraph();
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 0);
        g.addNode(100);
        g.setEdgeAttr(0, 1, FILLCOLOR, "pink");
        g.setNodeAttr(1, COLOR, "red");
        System.out.println(g.toString());
        g.writeDot("hogehoge.dot");
    }
}
