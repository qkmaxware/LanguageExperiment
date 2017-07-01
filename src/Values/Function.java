/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Values;

import Syntax.AST;
import lang.Container;
import lang.LiveException;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class Function implements ICloneable{
    
    public String[] in;
    public String out;
    public AST fn;
    
    public Object Call(Scope caller, Object... parameters){
        Scope n = caller.Next();
        if(in.length > parameters.length)
            throw new LiveException("Function is missing required parameters.");
        
        for(int i = 0; i < in.length; i++){
            n.Set(in[i], parameters[i]);
        }
        Scope s = (Scope)fn.Run(n);
        
        Container cn = s.GetLocal(out);
        if(cn != null)
            return cn.Get();
        return null;
    }

    @Override
    public Object Clone() {
        Function f = new Function();
        f.in = this.in;
        f.out = this.out;
        f.fn = this.fn;
        
        return f;
    }
    
}
