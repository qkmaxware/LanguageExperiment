/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lang;

import Syntax.*;
import Values.Array;
import Values.IIndexable;
import Values.Numeric;
import Values.Structure;
import Values.SystemFunction;
import java.util.Scanner;

/**
 *
 * @author Colin Halseth
 */
public class Interpreter {

    private Scope global;
    
    public Interpreter(){
        global = new Scope();
        
        //Create global functions
        global.Set("write", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                for(Object o : params){
                    System.out.println(String.valueOf(o));
                }
                return null;
            }
        });
        
        Scanner s = new Scanner(System.in);
        global.Set("read", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                String in = s.nextLine();
                //Attempt to match the data type to the input
                if(in.matches(Numeric.regex.pattern())){
                    return Numeric.Parse(in);
                }else if(in.matches("^((true)|(false))$")){
                    return new Numeric(Boolean.parseBoolean(in)? 1 : 0,0);
                }else{
                    return new Values.CharSequence(in);
                }
            }
        });
        global.Set("count", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                if(params.length == 0 || !(params[0] instanceof IIndexable))
                    throw new LiveException("Function 'count' expects one countable argument");
                return new Numeric(((IIndexable)params[0]).Count());
            }
        });
        global.Set("array_push", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                if(params.length < 2 || !(params[0] instanceof Array))
                    throw new LiveException("Function 'array_push' expects an array, and a value");
                ((Array)params[0]).Push(params[1]);
                return null;
            }
        });
        global.Set("exists", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                if(params.length == 0)
                    throw new LiveException("Function 'exists' expects one string for an argument");
                Scope p = scope.FindScopeWhereDefined(params[0].toString());
                if(p == null)
                    return Numeric.False;
                return Numeric.True;
            }
        });
        global.Set("delete", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                 if(params.length == 0)
                    throw new LiveException("Function 'delete' expects one string for an argument");
                Scope s = scope.FindScopeWhereDefined(params[0].toString());
                if(s != null)
                    s.Clear(params[0].toString());
                return null;
            }
        });
        
        
        //Create Math functions
        Structure Maths = new Structure();
        Maths.scope.Set("pi", Numeric.PI);
        Maths.scope.Set("e", Numeric.E);
        global.Set("i", Numeric.I);
        Maths.scope.Set("c", new Numeric(299792458));
        Maths.scope.Set("re", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                if(params.length == 0 || !(params[0] instanceof Numeric))
                    throw new LiveException("Function 're' requires 1 numeric argument");
                return new Numeric(((Numeric)params[0]).GetReal());
            }
        });
        Maths.scope.Set("img", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                if(params.length == 0 || !(params[0] instanceof Numeric))
                    throw new LiveException("Function 'img' requires 1 numeric argument");
                return new Numeric(((Numeric)params[0]).GetImaginary());
            }
        });
        Maths.scope.Set("exp", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                if(params.length == 0 || !(params[0] instanceof Numeric))
                    throw new LiveException("Function 'exp' requires 1 numeric argument");
                return Numeric.Pow(Numeric.E, (Numeric)params[0]);
            }
        });
        Maths.scope.Set("max", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                if(params.length < 2 || !(params[0] instanceof Numeric))
                    throw new LiveException("Function 'exp' requires 2 numeric argument");
                int compare = Numeric.Compare((Numeric)params[0], (Numeric)params[1]);
                return (compare > 0 ? params[0] : params[1]);
            }
        });
        Maths.scope.Set("min", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                if(params.length < 2 || !(params[0] instanceof Numeric))
                    throw new LiveException("Function 'exp' requires 2 numeric argument");
                int compare = Numeric.Compare((Numeric)params[0], (Numeric)params[1]);
                return (compare < 0 ? params[0] : params[1]);
            }
        });
        Maths.scope.Set("rng", new SystemFunction(){
            @Override
            public Object Function(Scope scope, Object... params) {
                return new Numeric(Math.random());
            }
        });
        //round, abs, floor, ceil, sin, cos, tan
        global.Set("math", Maths);
    }
    
    public void Execute(Program program){ 
        program.Run(global);
    }
    
}
