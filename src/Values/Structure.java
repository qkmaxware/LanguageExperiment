/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Values;

import java.util.Map.Entry;
import lang.Container;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class Structure implements ICloneable {
    
    public Scope scope = new Scope();
    
    public String toString(){
        return scope.toString();
    }

    @Override
    public Object Clone() {
        Scope parent = scope.GetParent();
        Scope ns;
        if(parent != null){
            ns = parent.Next();
        }else{
            ns = new Scope();
        }
        
        for(Entry<String, Container> kv : scope.GetKeyValuePairs()){
            if(kv.getValue().Get() instanceof ICloneable){
                ns.Set(kv.getKey(), ((ICloneable)kv.getValue().Get()).Clone());
            }else{
                ns.Set(kv.getKey(), kv.getValue());
            }
        }
        
        Structure s = new Structure();
        s.scope = ns;
        
        return s;
    }
}
