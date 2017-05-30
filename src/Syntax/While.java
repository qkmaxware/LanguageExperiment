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
public class While implements AST{

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
        Scope scope = parent;
        AST bool = children[0];
        AST block = children[1];
        
        if(bool == null || block == null)
            throw new LiveException("Missing condition or block for while statement");
            
        while(true){
            Object o = bool.Run(scope);
            if(!(o instanceof Numeric))
                throw new LiveException("Cannot evaluate while condition to a boolean value");
            
            boolean b = !((Numeric)o).Equals(Numeric.Zero);
            if(b){
                block.Run(scope);
            }else{
                break;
            }
        }
        
        return null;
    }
    
}
