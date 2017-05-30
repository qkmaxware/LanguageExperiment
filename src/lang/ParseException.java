/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lang;

/**
 *
 * @author Colin Halseth
 */
public class ParseException extends RuntimeException{
    
    public ParseException(String msg){
        super(msg);
    }
      
    @Override
    public String toString(){
        return super.toString();
    }
}
