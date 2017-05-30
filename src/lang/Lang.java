/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lang;

import Syntax.Program;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 *
 * @author Colin Halseth
 */
public class Lang {
    
    private static boolean DebugMode = true;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if(!DebugMode && args.length == 0){
            System.out.println("Please provide a file to compile.");
            System.exit(0);
        }
        
        try{
            Parser parser = new Parser();
        
            //Load in code
            String contents;
            if(!DebugMode){
                String fname = args[0];
                contents = String.join("\n", Files.readAllLines(Paths.get(fname)));
            }else{
                contents = GetDebugProgram();
            }
            Program compiled = parser.Parse(contents);
        
            //Run interpreter
            Interpreter runtime = new Interpreter();
            runtime.Execute(compiled);
        
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    private static String GetDebugProgram(){
        return "var a = [1,2,3,4,5,6]; array_push(a, 7); var i = 0; while(i < count(a)){ write(a[i]); i = i+1; };";
    }
    
}
