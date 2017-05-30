/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class Value implements AST{

    public Object value;
    
    @Override
    public AST getChild(int i) {
        return null;
    }

    @Override
    public void setChild(int i, AST child) {}

    @Override
    public Object Run(Scope parent) {
        return value;
    }
    
}
