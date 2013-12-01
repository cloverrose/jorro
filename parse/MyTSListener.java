package parse;

import java.util.*;

import main.Input;
import main.Pair;
import main.TransitionSystem;
import main.Value;

public class MyTSListener extends TSBaseListener {
    public TransitionSystem ts;

    @Override public void enterFile(TSParser.FileContext ctx) {
        this.ts = new TransitionSystem();
        for(TSParser.StateDefContext stateDefCtx : ctx.stateDef()){
            parseStateDef(stateDefCtx, this.ts);
        }
    }

    private void parseStateDef(TSParser.StateDefContext ctx, TransitionSystem ts) {
        int from = Integer.parseInt(ctx.getChild(1).getText());
        boolean first = true;
        Set<String> require = new HashSet<String>();
        for(TSParser.EdgeDefContext edgeDefCtx : ctx.edgeDef()){
            Pair<Integer, List<Pair<Value, Input>>> edge = parseEdgeDef(edgeDefCtx, from);
            int to = edge.a;
            for(Pair<Value, Input> p : edge.b){
                ts.addRelation(from, p.b, to);
                if(first){
                    first = false;
                    ts.addMu(from, p.a);
                }
                for(Value value : p.b.data){
                    require.addAll(value.data.keySet());
                }
            }
        }
        ts.addRequireInput(from, require);
    }

    private Pair<Integer, List<Pair<Value, Input>>> parseEdgeDef(TSParser.EdgeDefContext ctx, int from) {
        int to = Integer.parseInt(ctx.getChild(2).getText());
        List<Pair<Value, Input>> label = parseOrlabel(ctx.orlabel());
        return new Pair<Integer, List<Pair<Value, Input>>>(to, label);
    }

    private List<Pair<Value, Input>> parseOrlabel(TSParser.OrlabelContext ctx) {
        List<Pair<Value, Input>> ret = new ArrayList<Pair<Value, Input>>();
        for(TSParser.UlabelContext uCtx : ctx.ulabel()){
            ret.add(parseUlabel(uCtx));
        }
        return ret;
    }

    private Pair<Value, Input> parseUlabel(TSParser.UlabelContext ctx) {
        Value value = parseAndformula(ctx.andformula());
        Input input = parseOrformula(ctx.orformula());
        return new Pair<Value, Input>(value, input);
    }

    private Input parseOrformula(TSParser.OrformulaContext ctx) {
        Input input = new Input();
        for(TSParser.AndformulaContext andCtx : ctx.andformula()){
            input.data.add(parseAndformula(andCtx));
        }
        return input;
    }

    private Value parseAndformula(TSParser.AndformulaContext ctx) {
        Value value = new Value();
        for(TSParser.NotformulaContext notCtx : ctx.notformula()){
            Pair<String, Boolean> p = parseNotformula(notCtx);
            if(p != null){
                value.data.put(p.a, p.b);
            }
        }
        return value;
    }

    private Pair<String, Boolean> parseNotformula(TSParser.NotformulaContext ctx) {
        if(ctx.getChildCount() == 1 && ctx.getChild(0).getText().equals("T")) {
            return null;
        }

        String prop;
        boolean value;
        if(ctx.getChildCount() == 2 && ctx.getChild(0).getText().equals("!")){
            prop = ctx.getChild(1).getText();
            value = false;
        }else{
            prop = ctx.getChild(0).getText();
            value = true;
        }
        return new Pair<String, Boolean>(prop, value);
    }
}
