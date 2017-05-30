/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import Values.Pair;
import Values.Structure;
import java.util.LinkedList;
import lang.Container;
import lang.LiveException;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class ScopeResolution implements AST{

    public LinkedList<String> scopes;
    
    @Override
    public AST getChild(int i) {
        return null;
    }

    @Override
    public void setChild(int i, AST child) {}

    @Override
    public Object Run(Scope parent) {
        //id, no scope
        String id = scopes.getLast();
        Scope s = parent;
        
        //scope provided
        Scope q = parent;
        if(scopes.size() > 1){
            for(String scope : this.scopes){
                if(scope == this.scopes.getLast()){
                    s = q;
                    break;
                }
                Scope p = q.FindScopeWhereDefined(scope);
                if(p == null)
                    throw new LiveException("Scope "+scope+" does not exist in the current context");
                Container o = p.Get(scope);
                if(o == null || !(o.Get() instanceof Structure))
                    throw new LiveException("Object "+scope+" does not contain it's own scope");
                q = ((Structure)(o.Get())).scope;
            }
        }
        
        Pair p = new Pair<Scope, String>(s, id);
        
        return p;
    }
    
    
    
}
