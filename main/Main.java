package main;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import parse.MyTSListener;
import parse.TSLexer;
import parse.TSParser;

import com.fasterxml.jackson.databind.*;

import java.util.*;
import java.io.*;

public class Main {
    private static Map<String, String> parseArguments(String[] args){
        Map<String, String> ret = new HashMap<String, String>();
        if(args.length==0){
            throw new RuntimeException("Usage: java " + Main.class.getName() + " inputfile");
        }
        String filename = args[0];
        ret.put("filename", filename);
        for(int i=1;i<args.length;i++){
            String arg = args[i];
            if(arg.equals("-c")){
                i++;
                String configFile = args[i];
                ret.put("configFile", configFile);
                continue;
            }
            if(arg.equals("-o")){
                i++;
                String dotFile = args[i];
                ret.put("dotFile", dotFile);
                continue;
            }
        }
        if(!ret.containsKey("dotFile")){
            String dotFile;
            int index = filename.lastIndexOf(".");
            if(index == -1){
                dotFile = filename + ".dot";
            }else{
                dotFile = filename.substring(0, index) + ".dot";
            }
            ret.put("dotFile", dotFile);
        }
        return ret;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Map<String, String> parsedArgs = parseArguments(args);
        TransitionSystem ts = loadTransitionSystem(parsedArgs.get("filename"));
        System.out.println("NumState: " + ts.getNumState());
        Config config;
        if(parsedArgs.containsKey("configFile")){
            config = loadConfig(parsedArgs.get("configFile"));
        }else{
            config = new Config();
        }
        ts.simulation(config, parsedArgs.get("dotFile"));
    }

    private static TransitionSystem loadTransitionSystem(String filename) throws IOException{
        CharStream input = new ANTLRFileStream(filename);
        TSLexer lex = new TSLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        TSParser parser = new TSParser(tokens);
        ParserRuleContext tree = parser.file();
        ParseTreeWalker walker = new ParseTreeWalker();

        MyTSListener myTSListener = new MyTSListener();
        walker.walk(myTSListener, tree);

        return myTSListener.ts;
    }

    private static Config loadConfig(String filename) throws IOException{
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        return mapper.readValue(new File(filename), Config.class);
    }
}
