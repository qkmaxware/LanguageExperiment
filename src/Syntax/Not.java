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
public class Not implements AST{

    private AST[] c = new AST[1]; 
    
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
        
        if(left instanceof Numeric){
            //left != 0 AND right != 0
            boolean b = !((Numeric)left).Equals(Numeric.Zero);
            return (b ? Numeric.Zero : Numeric.One);
        }else{
            throw new LiveException("Failed to perform boolean negation on non-numeric values: "+left.toString());
        }
    }
    
}
