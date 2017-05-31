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
public class LiveThrownException extends RuntimeException{
    
    private Object obj;
    
    public LiveThrownException(Object ob){
        super();
        obj = ob;
    }
      
    @Override
    public String toString(){
        return obj.toString();
    }
    
    public Object GetThrownObject(){
        return obj;
    }
}