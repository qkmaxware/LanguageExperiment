/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import Values.Function;
import Values.Pair;
import lang.Container;
import lang.LiveException;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class FunctionCall implements AST{
    
    public AST scope;
    public AST[] params = new AST[0];

    @Override
    public AST getChild(int i) {
        return params[i];
    }

    @Override
    public void setChild(int i, AST child) {
        params[i] = child;
    }

    @Override
    public Object Run(Scope parent) {
        Object[] paramValues = new Object[params.length];
        for(int i = 0; i < params.length; i++){
            paramValues[i] = params[i].Run(parent);
        }
        
        Pair<Scope,String> p = (Pair<Scope,String>)this.scope.Run(parent);
        
        Container j = p.left.Get(p.right);
        if(j == null){
            throw new LiveException("Function does not exist "+p.right);
        }
        if(!(j.Get() instanceof Function)){
            throw new LiveException("Variable "+p.right+" is not a function");
        }
        
        //Set "this" reference for fast access
        Scope s = parent.Next();
        s.Set("this", p.left);
        
        return ((Function)j.Get()).Call(s, paramValues);   
    }
    
}
