/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import java.util.LinkedList;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class Program implements AST{

    private LinkedList<AST> children = new LinkedList<AST>();
    
    @Override
    public AST getChild(int i) {
        return children.get(i);
    }

    @Override
    public void setChild(int i, AST child) {
        if(i >= 0 && i < children.size()){
            children.set(i, child);
        }else{
            children.add(child);
        }
    }

    @Override
    public Object Run(Scope parent) {
        Scope myScope = parent.Next();
        
        return RunInScope(myScope);
    }
    
    public Object RunInScope(Scope s){
        for(AST ast : children){
            ast.Run(s);
        }
        
        return s;
    }
    
}
