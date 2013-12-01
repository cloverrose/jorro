package main;
import java.util.*;

public class Input {
    public List<Value> data;

    public Input(){
        this.data = new ArrayList<Value>();
    }

    public int hashCode(){
        return this.data.hashCode();
    }

    public boolean satisfy(Map<String, Boolean> input){
        for(Value value : this.data){
            if(value.satisfy(input)){
                return true;
            }
        }
        return false;
    }
}
