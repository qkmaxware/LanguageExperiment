/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lang;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Colin Halseth
 */
public class Scope {
 
    private HashMap<String, Container> vars = new HashMap<String, Container>();
    private Scope parent;
    
    public void Set(String name, Object value){
        vars.put(name, new Container(value));
    }
    
    public Container Get(String name){
        if(vars.containsKey(name)){
            return vars.get(name);
        }
        else{
            if(parent == null)
                return null;
            return parent.Get(name);
        }
    }
    
    public void Clear(String name){
        vars.remove(name);
    }
    
    public Container GetLocal(String name){
        if(vars.containsKey(name)){
            return vars.get(name);
        }
        return null;
    }
    
    public Scope FindScopeWhereDefined(String name){
        if(vars.containsKey(name)){
            return this;
        }
        else{
            if(parent == null)
                return null;
            return parent.FindScopeWhereDefined(name);
        }
    }
    
    public Scope Next(){
        Scope s = new Scope();
        s.parent = this;
        return s;
    }
 
    public Set<Entry<String,Container>> GetKeyValuePairs(){
        return this.vars.entrySet();
    }
    
    public Scope GetParent(){
        return this.parent;
    }
    
    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append("{\n");
        for(Entry<String, Container> keyValue : this.vars.entrySet()){
            b.append(keyValue.getKey()+": "+keyValue.getValue().Get().toString());
            b.append(",\n");
        }
        b.append("}");
        return b.toString();
    }
    
}
