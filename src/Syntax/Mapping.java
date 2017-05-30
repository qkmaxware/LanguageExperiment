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
public class Mapping implements AST{

    public class pair{
        public String name;
        public AST exp;
    }
    
    public pair MappingDetails = new pair();
    
    
    @Override
    public AST getChild(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setChild(int i, AST child) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object Run(Scope parent) {
        parent.Set(this.MappingDetails.name, this.MappingDetails.exp.Run(parent));
        return null;
    }
    
}
