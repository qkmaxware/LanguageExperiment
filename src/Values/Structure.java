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
    
    public final Scope scope;
    
    private Structure(){
        scope = new Scope();
    }
    
    public static Structure Create(){
        Structure s = new Structure();
        return s;
    }
    
    public String toString(){
        return scope.toString();
    }

    @Override
    public Object Clone() {
        Structure s = Structure.Create();
        
        for(Entry<String, Container> kv : scope.GetKeyValuePairs()){
            //Ignore cloning self references
            if(kv.getValue().Get() == this){
                s.scope.Set(kv.getKey(), s);
                continue;
            }
            
            if(kv.getValue().Get() instanceof ICloneable){
                s.scope.Set(kv.getKey(), ((ICloneable)kv.getValue().Get()).Clone());
            }else{
                s.scope.Set(kv.getKey(), kv.getValue());
            }
        }
        
        return s;
    }
}
