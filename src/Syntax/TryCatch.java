/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import lang.LiveThrownException;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class TryCatch implements AST{

    public AST tryBlock;
    public AST catchBlock;
    public String thrownName;
    
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
        Scope next = parent.Next();
        try{
            if(this.tryBlock != null){
                tryBlock.Run(next);
            }
        }catch(LiveThrownException ex){
            if(thrownName != null){
                next.Set(thrownName, ex.GetThrownObject());
            }
            
            if(this.catchBlock != null){
                catchBlock.Run(next);
            }
        }
        return null;
    }
    
}
