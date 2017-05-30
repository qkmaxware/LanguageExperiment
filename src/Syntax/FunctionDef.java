/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import Values.Function;
import java.util.LinkedList;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class FunctionDef implements AST{

    public LinkedList<String> parameters = new LinkedList<String>();
    public String output;
    private AST block;
    
    @Override
    public AST getChild(int i) {
        return block;
    }

    @Override
    public void setChild(int i, AST child) {
        block = child;
    }

    @Override
    public Object Run(Scope parent) {
        Function fn = new Function();
        fn.in = new String[parameters.size()];
        fn.in = parameters.toArray(fn.in);
        fn.out = output;
        fn.fn = block;
        
        return fn;
    }
    
}
