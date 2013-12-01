package main;
import java.util.*;

public class Util {
    public static String join(String s, List<String> strs){
        StringBuilder ret = new StringBuilder();
        boolean first = true;
        for(String str : strs){
            if(first){
                first = false;
            }else{
                ret.append(s);
            }
            ret.append(str);
        }
        return ret.toString();
    }
}
