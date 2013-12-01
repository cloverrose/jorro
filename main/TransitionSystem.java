package main;
import java.util.*;
import java.io.*;

public class TransitionSystem {
    private Map<Integer, Map<Input, Integer>> relations;
    private Map<Integer, Value> mu;
    private Map<Integer, Set<String>> requireInput;
    private int state;

    public TransitionSystem(){
        this.relations = new HashMap<Integer, Map<Input, Integer>>();
        this.mu = new HashMap<Integer, Value>();
        this.requireInput = new HashMap<Integer, Set<String>>();
        this.state = 0;
    }

    // use in parse phase
    public void addRelation(int from, Input input, int to){
        if(!this.relations.containsKey(from)){
            this.relations.put(from, new HashMap<Input, Integer>());
        }
        this.relations.get(from).put(input, to);
    }

    public void addMu(int state, Value value){
        this.mu.put(state, value);
    }

    public void addRequireInput(int state, Set<String> require){
        this.requireInput.put(state, require);
    }

    private Map<String, Boolean> getInput(BufferedReader stdin, Set<String> require, Config config) throws IOException{
        Map<String, Boolean> autoInput = config.autoInput;
        Map<String, Boolean> ret = new HashMap<String, Boolean>();
        for(String reqprop : require){
            if(autoInput != null && autoInput.containsKey(reqprop)){
                ret.put(reqprop, autoInput.get(reqprop));
                continue;
            }
            while(true){
                System.out.print("value of " + config.doRename(reqprop) + "[t/f]> ");
                String line = stdin.readLine();
                if(line.equals("t")){
                    ret.put(reqprop, true);
                    break;
                }else if(line.equals("f")){
                    ret.put(reqprop, false);
                    break;
                }
            }
        }
        return ret;
    }

    private int calcNextState(Map<String, Boolean> input){
        for(Map.Entry<Input, Integer> e : this.relations.get(this.state).entrySet()){
            Input k = e.getKey();
            if(k.satisfy(input)){
                return e.getValue();
            }
        }
        throw new RuntimeException();
    }

    // show info
    public int getNumState(){
        return this.relations.size();
    }

    private void showValue(Config config){
        List<String> viewProp = config.viewProp;
        System.out.println("Value");
        Value value = mu.get(this.state);
        if(viewProp == null){
            for(Map.Entry<String, Boolean> e : value.data.entrySet()){
                System.out.println("    " + config.doRename(e.getKey()) + ": " + e.getValue());
            }
        }else{
            for(String prop : viewProp){
                if(!value.data.containsKey(prop)){
                    continue;
                }
                System.out.println("    " + config.doRename(prop) + ": " + value.data.get(prop));
            }
        }
    }

    private void showMove(int nextState, Config config){
        List<String> viewProp = config.viewProp;
        System.out.println("Move");
        System.out.println("    " + this.state + " -> " + nextState);
        if(nextState == this.state){
            return;
        }
        if(viewProp == null){
            return;
        }
        Value value = this.mu.get(this.state);
        Value nextValue = this.mu.get(nextState);

        boolean first = true;
        for(String prop : viewProp){
            if(!value.data.containsKey(prop)){
                continue;
            }
            boolean v = value.data.get(prop);
            boolean nextV = nextValue.data.get(prop);
            if(v != nextV){
                if(first){
                    first = false;
                    System.out.println("Diff");
                }
                System.out.println("    " + config.doRename(prop) + ": " + v + " -> " + nextV);
            }
        }
    }

    private String negate(String prop){
        return "¬" + prop;
    }

    private void setNodeAttrs(Digraph graph, int node, Config config){
        Map<String, Map<String, String>> guiAttr = config.guiAttr;
        if(guiAttr != null){
            // set configured style
            for(Map.Entry<String, Boolean> e : this.mu.get(node).data.entrySet()){
                String prop = e.getKey();
                if(e.getValue() && guiAttr.containsKey(prop)){
                    graph.setNodeAttrs(node, guiAttr.get(prop));
                }
            }
        }
        // set pretty label
        List<String> viewProp = config.viewProp;
        if(viewProp == null){
            return;
        }
        List<String> intProp = config.intProp;
        Map<String, Boolean> propValue = this.mu.get(node).data;
        List<String> buf = new ArrayList<String>();
        for(String prop : viewProp){
            if(!propValue.containsKey(prop)){
                continue;
            }
            if(intProp != null && intProp.contains(prop) && !propValue.get(prop)){
                continue;
            }
            String _prop = config.doRename(prop);
            if(!propValue.get(prop)){
                _prop = negate(_prop);
            }
            buf.add(_prop);
        }
        graph.setNodeAttr(node, Digraph.LABEL, "[" + node + "]\n" + Util.join("\n", buf));
    }

    private void setEdgeAttrs(Digraph graph, int from, int to, Map<String, Boolean> input, Config config){
        Map<String, Boolean> autoInput = config.autoInput;
        // set pretty label
        List<String> buf = new ArrayList<String>();
        for(Map.Entry<String, Boolean> e : input.entrySet()){
            String reqprop = e.getKey();
            boolean value = e.getValue();
            if(autoInput != null && autoInput.containsKey(reqprop)){
                continue;
            }
            String _reqprop = config.doRename(reqprop);
            if(!value){
                _reqprop = negate(_reqprop);
            }
            buf.add(_reqprop);
        }
        graph.setEdgeAttr(from, to, Digraph.LABEL, Util.join("\n", buf));
    }

    private void runDot(Runtime runTime, String dotFile) throws IOException{
        runTime.exec("dot -Tpdf " + dotFile + " -o " + dotFile.replace(".dot", ".pdf"));
    }

    // main method
    public void simulation(Config config, String dotFile) throws IOException, InterruptedException{
        System.out.println("Start simulation");
        String defaultNodeFillColor = "white";
        String defaultNodeFontColor = "black";
        String defaultEdgeColor = "black";
        String currentNodeFillColor = "red";
        String prevNodeFillColor = "pink";


        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Digraph graph = new Digraph();
        graph.setGlobalNodeAttr(Digraph.STYLE, "filled");
        graph.setGlobalNodeAttr(Digraph.FILLCOLOR, defaultNodeFillColor);
        graph.setGlobalNodeAttr(Digraph.FONTCOLOR, defaultNodeFontColor);
        graph.setGlobalEdgeAttr(Digraph.COLOR, defaultEdgeColor);

        this.state = 0;
        graph.addNode(this.state);
        graph.setNodeAttr(this.state, Digraph.FILLCOLOR, currentNodeFillColor);
        setNodeAttrs(graph, this.state, config);
        graph.writeDot(dotFile);
        Runtime runTime = Runtime.getRuntime();
        runDot(runTime, dotFile);
        int prevState = -1;
        for(long c=0;;c++){
            System.out.println("[" + c + " step]");
            System.out.println("================================================================================");
            showValue(config);
            System.out.println("--------------------------------------------------------------------------------");
            Set<String> require = this.requireInput.get(this.state);
            Map<String, Boolean> input;
            if(require.size() == 0){
                System.out.println("Auto");
                Thread.sleep(1000);
                input = new HashMap<String, Boolean>();
            }else{
                input = getInput(stdin, require, config);
            }
            int nextState = calcNextState(input);
            System.out.println("--------------------------------------------------------------------------------");
            showMove(nextState, config);
            boolean isNewNextNode = !graph.containsNode(nextState);
            boolean isNewEdge = !graph.containsEdge(this.state, nextState);
            graph.addEdge(this.state, nextState);
            if(prevState != -1){
                graph.clearNodeAttr(prevState, Digraph.FILLCOLOR);
                graph.clearEdgeAttr(prevState, this.state, Digraph.COLOR);
            }
            graph.setNodeAttr(this.state, Digraph.FILLCOLOR, prevNodeFillColor);
            graph.setNodeAttr(nextState, Digraph.FILLCOLOR, currentNodeFillColor);
            graph.setEdgeAttr(this.state, nextState, Digraph.COLOR, currentNodeFillColor);
            if(isNewNextNode){
                setNodeAttrs(graph, nextState, config);
            }
            if(isNewEdge){
                setEdgeAttrs(graph, this.state, nextState, input, config);
            }
            graph.writeDot(dotFile);
            runDot(runTime, dotFile);
            prevState = this.state;
            this.state = nextState;
        }
    }
}
