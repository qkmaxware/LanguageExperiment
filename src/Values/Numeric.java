/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Values;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Colin Halseth
 */
public class Numeric implements ICloneable, IComparable{
    
    public static final Numeric Zero = new Numeric(0,0);
    public static final Numeric One = new Numeric(1,0);
    public static final Numeric False = Zero;
    public static final Numeric True = One;
    public static final Numeric I = new Numeric(0,1);
    public static final Numeric E = new Numeric(Math.E,0);
    public static final Numeric PI = new Numeric(Math.PI,0);
    
    private double real;
    private double img;
    
    public static final Pattern regex = Pattern.compile("^((?<real>-?\\s*[0-9]+(?:\\.[0-9]+)?)?\\s*\\+?\\s*(?:(?<img>-?\\s*[0-9]+(?:\\.[0-9]+)?)[ij])?)$");
    
    public Numeric(double r){
        this(r,0);
    }
    
    public Numeric(double r, double i){
        this.real = r;
        this.img = i;
    }
    
    public double GetReal(){
        return this.real;
    }
    
    public double GetImaginary(){
        return this.img;
    }
    
    public static Numeric Parse(String in){
        Matcher m = regex.matcher(in);
        Numeric n = new Numeric(0,0);
        if(m.find()){
            String real = m.group("real");
            String img = m.group("img");
            if(real != null)
                n.real = Double.parseDouble(real);
            if(img != null)
                n.img = Double.parseDouble(img);
        }
        return n;
    }
    
    public static Numeric Add(Numeric a, Numeric b){
        return new Numeric(a.real + b.real, a.img + b.img);
    }
    
    public static Numeric Sub(Numeric a, Numeric b){
        return new Numeric(a.real - b.real, a.img - b.img);
    }
    
    public static Numeric Mul(Numeric a, Numeric b){
        return new Numeric((a.real * (b.real)) - (a.img * (b.img)), (a.real * (b.img)) + (a.img * (b.real)));
    }
    
    public static int Compare(Numeric a, Numeric b){
        if(a.img == 0 && b.img == 0){
            if(a.real > b.real){
                return 1;
            }
            else if(a.real < b.real){
                return -1;
            }
            else {
                return 0;
            }
        }
        else{
            double aa = a.real * a.real + a.img * a.img;
            double bb = b.real * b.real + b.img * b.img;
            if(aa > bb){
                return 1;
            }
            else if(aa < bb){
                return -1;
            }
            else {
                return 0;
            }
        }
    }
    
    public static Numeric Div(Numeric a, Numeric c){
        double d = (c.real * (c.real))+ (c.img *(c.img));
        double r = ((a.real *(c.real))+(a.img*(c.img)))/(d);
        double i = ((a.img *(c.real))-(a.real*(c.img)))/(d);
        return new Numeric(r,i);
    }
    
    public static Numeric Arg(Numeric a){
        return new Numeric(Math.atan2(a.img, a.real));
    }
    
    public static Numeric Pow(Numeric a, Numeric c){
        //Fix this so I dont convert to decimal values (loses accuracy)
        
        //In exponential form 
        //(a+ib)^(c+id) = e^(ln(r)(c+id)+i theta (c+id))
        // -> ln(r)c + ln(r)id + i0c - 0d
        //e^(i theta) = cos0 + isin0
        //e^(ln(r)c - 0d) * e^(i(ln(r)*d + 0c))
        double r = Math.sqrt(a.real*(a.real) + a.img*(a.img));
        double theta = Arg(a).real;
        double lnr = Math.log(r);
        
        //e^(ln(r)c - 0d)
        double scalar = Math.pow(Math.E, lnr*c.real - theta*c.img);
        
        //e^(i(ln(r)*d + 0c)) = e^(i a) = cos(a) + isin(a)
        double real = Math.cos(lnr*c.img + theta*c.real);
        double img =  Math.sin(lnr*c.img + theta*c.real);
        
        return new Numeric(scalar * real, scalar * img);
    }
    
    public boolean Equals(Numeric b){
        return real == b.real && img == b.img;
    }
     
    public String toString(){
        if(img == 0 && real != 0)
            return real+"";
        else if(real == 0 && img != 0)
            return img + "i";
        else if(real == 0 && img == 0)
            return "0.0";
        else
            return real +" + "+ img + "i";
    }

    @Override
    public Object Clone() {
        return new Numeric(this.real, this.img);
    }

    @Override
    public double GetCompareValue() {
        return Math.signum(real) * Math.sqrt(real*real + img*img);
    }
    
}
