/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import Values.IIndexable;
import Values.Numeric;
import Values.Pair;
import lang.Container;
import lang.LiveException;
import lang.Scope;


/**
 *
 * @author Colin Halseth
 */
public class Indexor implements AST{
    
    public AST scope;
    public AST index;
    
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
            throw new LiveException("Indexing non-existing variable "+p.right);
        }

        if(!(j.Get() instanceof IIndexable)){
            throw new LiveException(p.right+" is not an indexable variable");
        }
        
        IIndexable var = (IIndexable)j.Get();
        
        Object indexValue = index.Run(parent);
        if(!(indexValue instanceof Numeric)){
            throw new LiveException("Invalid index "+indexValue);
        }
        
        return var.Get(
                    (int)((Numeric)indexValue).GetReal()
                );
    }
    
}
