/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import Values.IComparable;
import Values.Numeric;
import lang.LiveException;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class Compare implements AST{

    @Override
    public AST getChild(int i) {
        return children[i];
    }

    @Override
    public void setChild(int i, AST child) {
        children[i] = child;
    }

    public static enum Mode{
        Greater, Less, GreaterEquals, LessEquals, Equals, NotEquals
    }
    
    private AST[] children = new AST[2];
    public Mode mode = Mode.Equals;
    
    
    @Override
    public Object Run(Scope parent) {
        Object left = this.children[0].Run(parent);
        Object right = this.children[1].Run(parent);
        
        if(!(left instanceof IComparable) || !(right instanceof IComparable))
            throw new LiveException("One or more values are not comparable");
        
        double l = ((IComparable)left).GetCompareValue();
        double r = ((IComparable)right).GetCompareValue();
        
        switch(mode){
            case Greater:
                if(l > r)
                    return Numeric.True;
                else
                    return Numeric.False;
            case Less:
                if(l < r)
                    return Numeric.True;
                else
                    return Numeric.False;
            case GreaterEquals:
                if(l >= r)
                    return Numeric.True;
                else
                    return Numeric.False;
            case LessEquals:
                if(l <= r)
                    return Numeric.True;
                else
                    return Numeric.False;
            case Equals:
                if(l == r)
                    return Numeric.True;
                else
                    return Numeric.False;
            case NotEquals:
                if(l != r)
                    return Numeric.True;
                else
                    return Numeric.False;
        }
        
        return Numeric.False;
    }
}
