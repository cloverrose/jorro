package main;
import java.util.*;

public class Value {
    public Map<String, Boolean> data;

    public Value(){
        this.data = new HashMap<String, Boolean>();
    }

    public int hashCode(){
        return this.data.hashCode();
    }

    public boolean satisfy(Map<String, Boolean> input){
        for(Map.Entry<String, Boolean> e : input.entrySet()){
            if(this.data.get(e.getKey()) != e.getValue()){
                return false;
            }
        }
        return true;
    }
}
