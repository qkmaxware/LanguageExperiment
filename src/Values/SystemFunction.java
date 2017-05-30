/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Values;

import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public abstract class SystemFunction extends Function{
    
    public Object Call(Scope caller, Object... parameters){
        Scope n = caller.Next();
        
        Object result = Function(n, parameters);
        return result;
    }
    
    public abstract Object Function(Scope s, Object...params);
    
}
