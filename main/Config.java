package main;
import java.util.*;

public class Config {
    public Map<String, Boolean> autoInput;
    public List<String> viewProp;
    public Map<String, Map<String, String>> guiAttr;
    public Map<String, String> rename;
    public List<String> intProp;


    public String doRename(String s){
        if(this.rename != null && this.rename.containsKey(s)){
            return this.rename.get(s);
        }
        return s;
    }
}
