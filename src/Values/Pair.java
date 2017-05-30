/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Values;

/**
 *
 * @author Colin Halseth
 */
public class Pair<S,I> {
    
    public S left;
    public I right;
    
    public Pair(S left, I right){
        this.left = left; this.right = right;
    }
}
