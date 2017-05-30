/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import Values.Pair;
import lang.Container;
import lang.LiveException;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class Accessor implements AST{
    
    public AST scope;

    @Override
    public AST getChild(int i) {
        return scope;
    }

    @Override
    public void setChild(int i, AST child) {
        scope = child;
    }

    @Override
    public Object Run(Scope parent) {
        Pair<Scope,String> p = (Pair<Scope,String>)this.scope.Run(parent);
        
        Container j = p.left.Get(p.right);
        
        if(j == null){
            throw new LiveException("Accessing non-existing variable "+p.right);
        }

        return j.Get();
    }
    
}
