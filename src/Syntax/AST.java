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
public interface AST {
    
    public AST getChild(int i);
    public void setChild(int i, AST child);
    public Object Run(Scope parent);
    
}
