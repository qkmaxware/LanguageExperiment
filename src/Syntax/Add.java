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
public class Add implements AST{

    private AST[] c = new AST[2]; 
    
    @Override
    public AST getChild(int i) {
        return c[i];
    }

    @Override
    public void setChild(int i, AST child) {
        c[i] = child;
    }

    @Override
    public Object Run(Scope parent) {
        Object left = c[0].Run(parent);
        Object right = c[1].Run(parent);
        
        if(left instanceof Numeric && right instanceof Numeric){
            return Numeric.Add((Numeric)left, (Numeric)right);
        }
        else if(left instanceof Values.CharSequence && right instanceof Values.CharSequence){
            return Values.CharSequence.Concat((Values.CharSequence)left, (Values.CharSequence)right);
        }
        else{
            throw new LiveException("Failed to add non-numeric values: "+left.toString()+" , "+right.toString());
        }
    }
    
}
