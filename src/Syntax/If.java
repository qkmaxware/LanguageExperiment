/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import Values.Numeric;
import lang.LiveException;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class If implements AST{

    private AST[] children = new AST[2];
    
    @Override
    public AST getChild(int i) {
        return children[i];
    }

    @Override
    public void setChild(int i, AST child) {
        children[i] = child;
    }

    @Override
    public Object Run(Scope parent) {
        Scope next = parent;
        Object bool = children[0].Run(next);
        if(!(bool instanceof Numeric))
            throw new LiveException("Conditional statement did not evaluate to a boolean value");
        
        boolean b = !((Numeric)bool).Equals(Numeric.Zero);
        if(b){
            children[1].Run(next);
        }
        return null;
    }
    
}
