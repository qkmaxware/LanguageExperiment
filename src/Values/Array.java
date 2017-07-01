/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Values;

import java.util.ArrayList;
import java.util.Arrays;
import lang.LiveException;
import lang.LiveThrownException;

/**
 *
 * @author Colin Halseth
 */
public class Array implements ICloneable, IIndexable, IComparable{
    
    private ArrayList<Object> elements;
    
    public Array(Object[] members){
        this.elements = new ArrayList<Object>(Arrays.asList(members));
    }
    
    public Object Get(int i){
        if(i >= elements.size() || i < -elements.size()){
            throw new LiveThrownException("Array index out of bounds");
        }

        if(i < 0)
            i = elements.size() - i;
        
        return elements.get(i);
    }
    
    public void Set(int i, Object o){
        if(i >= elements.size() || i < elements.size()){
            throw new LiveThrownException("Array index out of bounds");
        }

        if(i < 0)
            i = elements.size() - i;
        
        elements.set(i, o);
    }
    
    public void Push(Object o){
        elements.add(o);
    }
    
    public Object Peek(){
        if(this.Count() == 0)
            return null;
        return this.Get(-1);
    }
    
    public Object Pop(){
        if(this.Count() == 0)
            return null;
        Object o = this.elements.remove(this.elements.size() - 1);
        return o;
    }
    
    public int Count(){
        return elements.size();
    }
    
    public String toString(){
        return this.elements.toString();
    }

    @Override
    public Object Clone() {
        Object[] os = new Object[this.elements.size()];
        for(int i = 0; i < this.elements.size(); i++){
            if(this.elements.get(i) instanceof ICloneable){
                os[i] = ((ICloneable)this.elements.get(i)).Clone();
            }else{
                os[i] = this.elements.get(i);
            }
        }
        
        return new Array(os);
    }

    @Override
    public double GetCompareValue() {
        return this.elements.size();
    }
    
}
