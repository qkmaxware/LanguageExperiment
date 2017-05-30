/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Syntax;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lang.LiveException;
import lang.Parser;
import lang.Scope;

/**
 *
 * @author Colin Halseth
 */
public class Include implements AST{

    public String filename;
    public static Parser parser = new Parser();
            
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
        try{
            String contents = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            Program compiled = parser.Parse(contents);

            compiled.RunInScope(parent);
        }catch(Exception e){
            throw new LiveException(e.getMessage());
        }
        return null;
    }
    
}
