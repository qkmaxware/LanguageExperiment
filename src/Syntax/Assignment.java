/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import Values.Pair;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class Assignment implements AST{

    private AST scopeResolution;
    private AST exp;
    
    public boolean overwrite = true;
    
    @Override
    public AST getChild(int i) {
        if(i == 0)
            return scopeResolution;
        return exp;
    }

    @Override
    public void setChild(int i, AST child) {
        if(i == 0)
            scopeResolution = child;
        else{
            exp = child;
        }
    }

    @Override
    public Object Run(Scope parent) {
        //Run scope resolution to decide where to place someObject
        Pair<Scope,String> p = (Pair<Scope,String>)this.scopeResolution.Run(parent);
        
        //Run expression to get result to store.
        Object value = exp.Run(parent);
        
        //Set the variable to the scope
        Scope s = p.left.FindScopeWhereDefined(p.right);
        if(overwrite && s != null){
            s.Set(p.right, value);
        }else{
            p.left.Set(p.right, value);
        }
        
        return null;
    }
    
}
