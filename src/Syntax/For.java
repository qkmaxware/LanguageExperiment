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
public class For implements AST{

    public AST assignment;
    public AST condition;
    public AST increment;
    
    public AST[] children = new AST[1];
    
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
        Scope scope = parent.Next();
        AST block = children[0];
        
        if(block == null)
            throw new LiveException("Missing condition or block for while statement");
        
        if(this.assignment == null)
            throw new LiveException("Missing assignment expression");
        if(this.condition == null)
            throw new LiveException("Missing required conditional statement");
        if(this.increment == null)
                throw new LiveException("Missing required increment or decrement statement");
        
        //Establish initial assignment
        this.assignment.Run(scope);
        
        while(true){
            Object o = condition.Run(scope);
            if(!(o instanceof Numeric))
                throw new LiveException("Cannot evaluate while condition to a boolean value");
            
            boolean b = !((Numeric)o).Equals(Numeric.Zero);
            if(b){
                block.Run(scope);
            }else{
                break;
            }
            
            increment.Run(scope);
        }
        
        return null;
    }
    
}
