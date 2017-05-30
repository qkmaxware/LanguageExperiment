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
public class Container {
    
    private Class clazz;
    private Object obj;
    
    public Container(Object o){
        if(o == null){
            clazz = null;
            obj = null;
        }else{
            clazz = o.getClass();
            obj = o;
        }
    }
    
    public boolean Typof(Class c){
        return this.clazz.isInstance(c);
    }
    
    public Class GetType(){
        return clazz;
    }
    
    public Object Get(){
        return obj;
    }
}
