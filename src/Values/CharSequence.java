/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Values;

import lang.LiveException;
import lang.LiveThrownException;

/**
 *
 * @author Colin Halseth
 */
public class CharSequence implements IIndexable, IComparable{
    
    public String string;
    
    public CharSequence(String str){
        this.string = str;
    }
    
    public static CharSequence Concat(CharSequence a, CharSequence b){
        return new CharSequence(a.string+b.string);
    }
   
    public String toString(){
        return string;
    }

    @Override
    public Object Get(int i) {
        if(i < 0 || i >= string.length()){
            throw new LiveThrownException("String index of of bounds");
        }
        return new CharSequence(string.charAt(i)+"");
    }

    @Override
    public int Count() {
        return string.length();
    }

    @Override
    public double GetCompareValue() {
        return this.string.length();
    }
    
}
