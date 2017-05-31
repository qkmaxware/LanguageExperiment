/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lang;

import Syntax.*;
import Values.Numeric;
import java.util.LinkedList;

/**
 *
 * @author Colin Halseth
 */
public class Parser {
    
    //Keywords
    ParseToken NEW = new ParseToken("clone","^new(?=\\W|$)");
    ParseToken TRUE = new ParseToken("true","^true(?=\\W|$)");
    ParseToken FALSE = new ParseToken("false","^false(?=\\W|$)");
    ParseToken STRUCT = new ParseToken("def","^def(?=\\W|$)");
    ParseToken INCLUDE = new ParseToken("include","^include(?=\\W|$)");
    ParseToken WHILE = new ParseToken("while","^while(?=\\W|$)");
    ParseToken IF = new ParseToken("if","^if(?=\\W|$)");
    ParseToken VAR = new ParseToken("var","^var(?=\\W|$)");
    ParseToken TRY = new ParseToken("try","^try(?=\\W|$)");
    ParseToken CATCH = new ParseToken("catch","^catch(?=\\W|$)");
    //Primitives
    ParseToken WORD = new ParseToken("name", "^[a-zA-Z_]\\w*");
    ParseToken STRING = new ParseToken("quote", "^([\"'])(?:(?=(\\\\?))\\2.)*?\\1");
    ParseToken NUM = new ParseToken("digit","^[0-9]+(\\.[0-9]+)?[ij]?");
    ParseToken SCOPE = new ParseToken("scope_resolution", "^\\.");
    ParseToken ADD = new ParseToken("add", "^\\+");
    ParseToken SUB = new ParseToken("sub", "^\\-");
    ParseToken MUL = new ParseToken("mul", "^\\*");
    ParseToken DIV = new ParseToken("div", "^\\/");
    ParseToken POW = new ParseToken("pow", "^\\^");
    ParseToken OR = new ParseToken("or", "^\\|");
    ParseToken AND = new ParseToken("and", "^\\&");
    ParseToken NOT = new ParseToken("not", "^\\!");
    ParseToken MAP = new ParseToken("mapping", "^:");
    ParseToken FUNC = new ParseToken("function","^\\-\\>");
    ParseToken COMMA = new ParseToken("comma","^,");
    ParseToken OPEN_BRACKET = new ParseToken("open_bracket","^\\(");
    ParseToken CLOSE_BRACKET = new ParseToken("close_bracket","^\\)");
    ParseToken OPEN_BLOCK = new ParseToken("open_block","^\\{");
    ParseToken CLOSE_BLOCK = new ParseToken("close_block","^\\}");
    ParseToken OPEN_ARRAY= new ParseToken("open_array","^\\[");
    ParseToken CLOSE_ARRAY= new ParseToken("close_array","^\\]");
    ParseToken EQUALS = new ParseToken("equals","^=");
    ParseToken STATEMENT_END = new ParseToken("end","^\\;");
    ParseToken COMMENT = new ParseToken("comment","^([#])(?:(?=(\\\\?))\\2.)*?\\1");
    ParseToken GREATER = new ParseToken("greater", "^\\>");
    ParseToken LESS = new ParseToken("less", "^\\<");
    
    private Tokenizer tokenizer;
    
    public Parser (){
        tokenizer = new Tokenizer(
                NEW,
                TRUE,
                FALSE,
                STRUCT,
                INCLUDE,
                WHILE,
                IF,
                VAR,
                TRY,
                CATCH,
                WORD, 
                STRING, 
                NUM, 
                SCOPE, 
                FUNC, 
                ADD,
                SUB,
                MUL,
                DIV,
                POW,
                OR,
                AND,
                NOT,
                MAP,
                COMMA,
                OPEN_BRACKET, 
                CLOSE_BRACKET, 
                OPEN_BLOCK, 
                CLOSE_BLOCK, 
                OPEN_ARRAY,
                CLOSE_ARRAY,
                EQUALS,
                GREATER,
                LESS,
                STATEMENT_END,
                COMMENT
        );
    }
    
    public Program Parse(String script){
        RQueue<ParseToken.TokenMatch> queue = new RQueue<ParseToken.TokenMatch>(
                StripComments(this.tokenizer.Tokenize(script))
        );
        
        return (Program)ParseProgram(queue);
    }
    
    public LinkedList<ParseToken.TokenMatch> StripComments(LinkedList<ParseToken.TokenMatch> matches){
        LinkedList<ParseToken.TokenMatch> s = new LinkedList<ParseToken.TokenMatch>();
        for(ParseToken.TokenMatch match : matches){
            if(!match.equals(COMMENT)){
                s.addLast(match);
            }
        }
        return s;
    }
    
    //<program> :: <statement>*
    private AST ParseProgram(RQueue<ParseToken.TokenMatch> queue){

        Program program = new Program();
        
       AST eval = ParseStatement(queue);
       if(eval != null){
            program.setChild(-1, eval); 
            while(ParseSemiColon(queue)){
                AST nextEval = ParseStatement(queue);
                if(nextEval == null){
                    break;
                }
                program.setChild(-1, nextEval); 
            }
       }

        return program;
    }
    
    // <statement> :: ( <include> | <try-catch> <assignment> | <conditional> | <loop> | <block> | <evaluatable> | <loop> );
    private AST ParseStatement(RQueue<ParseToken.TokenMatch> queue){
        int r = queue.RestorePoint();
        
        //Parse inlcude statement
        AST s = ParseInclude(queue);
        if(s != null)
            return s;
        
        //Try catch block
        queue.RestoreTo(r);
        s = ParseTryCatch(queue);
        if(s != null)
            return s;
        
        //Conditional statement
        queue.RestoreTo(r);
        s = ParseIf(queue);
        if(s != null)
            return s;
        
        //Assignment
        queue.RestoreTo(r);
        s = ParseAssignment(queue);
        if(s != null)
            return s;
        
        //Some kind of expression
        queue.RestoreTo(r);
        s = ParseEvaluatable(queue);
        if(s != null)
            return s;
        
        //Loop statment
        queue.RestoreTo(r);
        s = ParseLoop(queue);
        if(s != null)
            return s;
        
        //Code block
        queue.RestoreTo(r);
        s = ParseBlock(queue);
        if(s != null)
            return s;
        
        queue.RestoreTo(r);
        
        return null;
    }
    
    // <try-catch> :: try <block> catch <name> <block>
    private AST ParseTryCatch(RQueue<ParseToken.TokenMatch> queue){
        if(!ParseTry(queue)){
            return null;
        }
        
        AST tryBlock = ParseBlock(queue);
        if(tryBlock == null){
            throw new ParseException("Expecting code block after try keyword, but none found.");
        }
        
        TryCatch ast = new TryCatch();
        ast.tryBlock = tryBlock;
        
        if(ParseCatch(queue)){
            //Optional name
            String s = ParseWord(queue);
            ast.thrownName = s;
            
            AST catchBlock = ParseBlock(queue);
            if(catchBlock == null){
                throw new ParseException("Expecting code block after catch keyword, but none found.");
            }
            
            ast.catchBlock = catchBlock;
        }
        
        return ast;
    }
    
    // <include> :: include <string>
    private AST ParseInclude(RQueue<ParseToken.TokenMatch> queue){
        if(!ParseIncludeKeyword(queue)){
            return null;
        }
        
        String s = ParseStringLiteral(queue);
        if(s == null)
            throw new ParseException("Expecting filename string after include, but none given");
        
        Include i = new Include();
        i.filename = s;
        
        return i;
    }
    
    // <if> :: if <expression> <block>
    private AST ParseIf(RQueue<ParseToken.TokenMatch> queue){
        if(!ParseIfKeyword(queue)){
            return null;
        }
        
        AST exp = ParseNumExp(queue);
        if(exp == null){
            throw new ParseException("If statement must be followed by a boolean expression");
        }
        
        AST block = ParseStatement(queue);
        if(block == null){
            throw new ParseException("Expecting if statement body, by found none");
        }
        
        If i = new If();
        i.setChild(0, exp);
        i.setChild(1, block);
        
        return i;
    }
    
    // <loop> :: <while-loop> | <for-loop>
    private AST ParseLoop(RQueue<ParseToken.TokenMatch> queue){
        int r = queue.RestorePoint();
        
        AST loop = ParseWhileLoop(queue);
        if(loop != null)
            return loop;
        queue.RestoreTo(r);
        
        return null;
    }
    
    // <while-loop> :: while <expression> <block>
    private AST ParseWhileLoop(RQueue<ParseToken.TokenMatch> queue){
        if(!ParseWhileKeyword(queue)){
            return null;
        }
        
        AST exp = ParseNumExp(queue);
        if(exp == null){
            throw new ParseException("If statement must be followed by a boolean expression");
        }
        
        AST block = ParseStatement(queue);
        if(block == null){
            throw new ParseException("Expecting if statement body, by found none");
        }
        
        While i = new While();
        i.setChild(0, exp);
        i.setChild(1, block);
        
        return i;
    }
    
    // <block> :: { <program> }
    private AST ParseBlock(RQueue<ParseToken.TokenMatch> queue){
        if(!ParseOpenBlock(queue)){
            return null;
        }
        AST program = ParseProgram(queue);
        
        if(!ParseCloseBlock(queue)){
            return null;
        }
        
        return program;
    }
    
    // <assignment> :: <identifier> = <evaluatable>
    private AST ParseAssignment(RQueue<ParseToken.TokenMatch> queue){
        boolean isDefinition = ParseVarKeyword(queue);
        
        AST identifier = ParseScopeResolution(queue);
        if(!ParseEquals(queue)){
            return null;
        }
        AST exp = ParseEvaluatable(queue);
        
        Assignment as = new Assignment();
        as.overwrite = !isDefinition;
        as.setChild(0, identifier);
        as.setChild(1, exp);
        return as;
    }
    
    // <evaluatable> :: <struct> | <num-exp> | <bool-exp> | <str-exp> | <array> | <func-def> | <clone>
    private AST ParseEvaluatable(RQueue<ParseToken.TokenMatch> queue){
        AST r; int i = queue.RestorePoint();
        
        //Structure definition
        r = ParseStruct(queue);
        if(r != null){
            return r;
        }
        queue.RestoreTo(i);
        
        //Parse different types of expressions (numberic, bool, string)
        r = ParseNumExp(queue);
        if(r != null){
            return r;
        }
        queue.RestoreTo(i);
        
        //Parse array
        r = ParseArray(queue);
        if(r != null){
            return r;
        }
        queue.RestoreTo(i);
        
        //Parse function definition
        r = ParseFunction(queue);
        if(r != null){
            return r;
        }
        queue.RestoreTo(i);
        
        //Parse object cloning
        r = ParseClone(queue);
        if(r != null){
            return r;
        }
        queue.RestoreTo(i);
        
        return null;
    }
    
    //--------------------------------------------------------------------------
    
    // <num-exp> :: <term> | <term> { +/- <term> }+
    public AST ParseNumExp(RQueue<ParseToken.TokenMatch> queue){
        AST term1 = ParseTerm(queue);
        if(term1 == null)
            return term1;
        
        AST value = term1;
        //Loop for multiple terms
        while(true){
            //Parse +/-, if none return value
            AST sym = null;
            if(ParseAddSymbol(queue)){
                sym = new Add();
            }else if(ParseSubSymbol(queue)){
                sym = new Sub();
            }
            if(sym == null){
                return value;
            }
            
            //Parse second term
            AST term2 = ParseTerm(queue);
            if(term2 == null){
                throw new ParseException("Expecting expression after + or - sign, but none found");
            }
            
            //Create addition node and assign it to the value
            sym.setChild(0, term1);
            sym.setChild(1, term2);
            value = sym;
        }
    }
    
    // <term> :: <factor> | <factor> { */ <factor> }+
    public AST ParseTerm(RQueue<ParseToken.TokenMatch> queue){
        AST term1 = ParseFactor(queue);
        if(term1 == null)
            return term1;
        
        AST value = term1;
        //Loop for multiple terms
        while(true){
            //Parse +/-, if none return value
            AST sym = null;
            if(ParseMulSymbol(queue)){
                sym = new Mul();
            }else if(ParseDivSymbol(queue)){
                sym = new Div();
            }
            if(sym == null){
                return value;
            }
            
            //Parse second term
            AST term2 = ParseFactor(queue);
            if(term2 == null){
                throw new ParseException("Expecting expression after * or / sign, but none found");
            }
            
            //Create addition node and assign it to the value
            sym.setChild(0, term1);
            sym.setChild(1, term2);
            value = sym;
        }
    }
    
    // <factor> :: <exponent> | <exponent> ^ <exponent>
    public AST ParseFactor(RQueue<ParseToken.TokenMatch> queue){
    
        AST exp1 = ParseOrTerm(queue);
        if(exp1 == null)
            return null;
        
        if(ParsePowerSign(queue)){
            AST exp2 = ParseOrTerm(queue);
            
            AST pow = new Pow();
            pow.setChild(0, exp1);
            pow.setChild(1, exp2);
            
            return pow;
        }else{
            return exp1;
        }
    }
    
    // <or term> :: <and term> | <and term> <or> <and term>
    public AST ParseOrTerm(RQueue<ParseToken.TokenMatch> queue){
        AST term1 = ParseAndTerm(queue);
        if(term1 == null)
            return term1;
        
        AST value = term1;
        //Loop for multiple terms
        while(true){
            //Parse +/-, if none return value
            AST sym = new Or();
            if(!ParseOrSymbol(queue)){
                return value;
            }
            
            //Parse second term
            AST term2 = ParseAndTerm(queue);
            if(term2 == null){
                throw new ParseException("Expecting expression after OR, but none given");
            }
            
            //Create addition node and assign it to the value
            sym.setChild(0, term1);
            sym.setChild(1, term2);
            value = sym;
        }
    }
    
    // <and term> :: <comparison> | <comparison> <and> <comparison>
    public AST ParseAndTerm(RQueue<ParseToken.TokenMatch> queue){
        AST term1 = ParseComparision(queue);
        if(term1 == null)
            return term1;
        
        AST value = term1;
        //Loop for multiple terms
        while(true){
            //Parse +/-, if none return value
            AST sym = new And();
            if(!ParseAndSymbol(queue)){
                return value;
            }
            
            //Parse second term
            AST term2 = ParseComparision(queue);
            if(term2 == null){
                throw new ParseException("Expecting expression AND, but none given");
            }
            
            //Create addition node and assign it to the value
            sym.setChild(0, term1);
            sym.setChild(1, term2);
            value = sym;
        }
    }
    
    // <comparision> :: <neg-term> ( > | < | >= | <= | != | == ) <neg-term>
    public AST ParseComparision(RQueue<ParseToken.TokenMatch> queue){
        //TODO use-non-numberic literals
        AST exp1 = ParseNegTerm(queue);
        if(exp1 == null)
            return null;

        //Parse comparator
        Compare compare = new Compare();
        if(ParseGreaterThan(queue)){
            if(ParseEquals(queue)){
                compare.mode = Compare.Mode.GreaterEquals;
            }else{
                compare.mode = Compare.Mode.Greater;
            }
        }else if(ParseLessThan(queue)){
            if(ParseEquals(queue)){
                compare.mode = Compare.Mode.LessEquals;
            }else{
                compare.mode = Compare.Mode.Less;
            }
        }else if(ParseNot(queue) && ParseEquals(queue)){
            compare.mode = Compare.Mode.NotEquals;
        }else if(ParseEquals(queue) && ParseEquals(queue)){
            compare.mode = Compare.Mode.Equals;
        }else{
            return exp1;
        }
        
        AST exp2 = ParseNegTerm(queue);
        if(exp2 == null)
            throw new ParseException("Invalid comparision operator");

        compare.setChild(0, exp1);
        compare.setChild(1, exp2);
        return compare;
    }
    
    // <neg term> :: <not-term> | - <not-term>
    public AST ParseNegTerm(RQueue<ParseToken.TokenMatch> queue){
        if(ParseSubSymbol(queue)){
            AST term = ParseNotTerm(queue);
            AST mul = new Mul();
            Value v = new Value();
            v.value = new Numeric(-1,0);
            mul.setChild(0, v);
            mul.setChild(1, term);
            return mul;
        }else{
            return ParseNotTerm(queue);
        }
    }
    
    //TODO might swap this with ParseComparision
    // <not term> :: <root-term> | <not> <root-term> 
    public AST ParseNotTerm(RQueue<ParseToken.TokenMatch> queue){
        if(ParseNot(queue)){
            AST term = ParseRootTerm(queue);
            
            AST not = new Not();
            not.setChild(0, term);
            return not;
        }else{
            return ParseRootTerm(queue);
        }
    }

    // <root term> :: ( <expression> ) | <num-literal> | <bool-literal> | <string-literal>
    public AST ParseRootTerm(RQueue<ParseToken.TokenMatch> queue){
        int i = queue.RestorePoint();
        
        AST r = ParseBracketExp(queue);
        if(r != null){
            return r;
        }
        queue.RestoreTo(i);
        
        r = ParseBooleanLiteral(queue);
        if(r != null){
            return r;
        }
        queue.RestoreTo(i);
        
        r = ParseNumbericLiteral(queue);
        if(r != null){
            return r;
        }
        queue.RestoreTo(i);
        
        String s = ParseStringLiteral(queue);
        if(s != null){
            Value v = new Value();
            v.value = new Values.CharSequence(s);
            return v;
        }
        queue.RestoreTo(i);
        
        return null;
    }
    
    // <num-literal> :: <number> | <func-call> | <indexor> | <identifier>
    public AST ParseNumbericLiteral(RQueue<ParseToken.TokenMatch> queue){
        int i = queue.RestorePoint();
        
        //Number
        String num = ParseNumber(queue);
        if(num != null){
            Value v = new Value();
            v.value = Numeric.Parse(num);
            return v;
        }
        queue.RestoreTo(i);
        
        //Function Call
        AST func = ParseFuncCall(queue);
        if(func != null){
            return func;
        }
        queue.RestoreTo(i);
        
        //Indexor
        func = ParseVariableIndex(queue);
        if(func != null){
            return func;
        }
        queue.RestoreTo(i);
        
        //Identifier
        func = ParseVariableAccessor(queue);
        if(func != null){
            return func;
        }
        queue.RestoreTo(i);
        
        return null;
    }
    
    // <func-call> :: <name> ( <param-list> )
    public AST ParseFuncCall(RQueue<ParseToken.TokenMatch> queue){
        AST scopeResolution = ParseScopeResolution(queue);
        if(scopeResolution == null)
            return null;
        
        if(!ParseOpenBracket(queue)){
            return null;
        }
        
        LinkedList<AST> parameters = new LinkedList<AST>();
        AST exp1 = ParseNumExp(queue);
        if(exp1 != null){
            parameters.add(exp1);
            while(ParseComma(queue)){
                AST exp2 = ParseNumExp(queue);
                if(exp2 == null){
                    throw new ParseException("Expected another paramter in function call, but none was given");
                }
                parameters.add(exp2);
            }
        }
        
        if(!ParseCloseBracket(queue)){
            return null;
        }
        
        FunctionCall call = new FunctionCall();
        call.scope = scopeResolution;
        call.params = new AST[parameters.size()];
        call.params = parameters.toArray(call.params);
        return call;
    }
    
    // <identifier> :: <name>
    public AST ParseVariableAccessor(RQueue<ParseToken.TokenMatch> queue){
        AST scopeResolution = ParseScopeResolution(queue);
        Accessor access = new Accessor();
        access.scope = scopeResolution;
        
        if(scopeResolution == null)
            return null;
        
        return access;   
    }
    
    // <indexor> :: <name> [ <num-expression> ]
    public AST ParseVariableIndex(RQueue<ParseToken.TokenMatch> queue){
        AST scopeResolution = ParseScopeResolution(queue);
        
        if(scopeResolution == null)
            return null;
        
        if(!ParseOpenArray(queue)){
           return null;
        } 
        
        AST exp = ParseNumExp(queue);
        
        if(exp == null)
            return null;
        
        if(!ParseCloseArray(queue)){
           return null;
        } 
        
        Indexor access = new Indexor();
        access.scope = scopeResolution;
        access.index = exp;
       
        return access;   
    }
    
    // <bool-literal> :: <true> | <false> | <comparison>
    public AST ParseBooleanLiteral(RQueue<ParseToken.TokenMatch> queue){
        if(ParseTrueLiteral(queue)){
            Value v = new Value();
            v.value = new Numeric(1,0);
            return v;
        }
        
        if(ParseFalseLiteral(queue)){
            Value v = new Value();
            v.value = new Numeric(0,0);
            return v;
        }
        
        return null;
    }
    
    // <bracket-exp> :: ( <expression> )
    public AST ParseBracketExp(RQueue<ParseToken.TokenMatch> queue){
        if(!ParseOpenBracket(queue)){
            return null;
        }
        
        AST exp = this.ParseNumExp(queue);
        
        if(!ParseCloseBracket(queue)){
            return null;
        }
        
        return exp;
    }
    
    //--------------------------------------------------------------------------
    
    // <array> :: [ <evaluatable> , * ]
    private AST ParseArray(RQueue<ParseToken.TokenMatch> queue){
       if(!ParseOpenArray(queue)){
           return null;
       } 
       
       ArrayDef array = new ArrayDef();
       
       //Parse evaluatables (can be empty)
       AST eval = ParseEvaluatable(queue);
       if(eval != null){
            array.setChild(-1, eval);
            while(ParseComma(queue)){
                AST nextEval = ParseEvaluatable(queue);
                if(nextEval == null){
                    //throw error
                    throw new ParseException("Expecting another value in array, but none was given.");
                }
                array.setChild(-1, nextEval); 
            }
       }
       
       if(!ParseCloseArray(queue)){
           return null;
       } 

       return array;
    }
    
    private AST ParseFunction(RQueue<ParseToken.TokenMatch> queue){
        if(!ParseOpenBracket(queue)){
            return null;
        }
        
        FunctionDef fn = new FunctionDef();
        
        //Parse param list
        String param = ParseWord(queue);
        if(param != null){
            fn.parameters.add(param); 
            while(ParseComma(queue)){
                String nextParam = ParseWord(queue);
                if(nextParam == null){
                     //throw error
                    throw new ParseException("Expecting another value in argument list, but none was given.");
                }
                fn.parameters.add(nextParam); 
            }
        }
        
        if(!ParseCloseBracket(queue)){
            return null;
        }
        
        if(!ParseFuncMap(queue)){
            return null;
        }

        //Parse output param (optional)
        String output = ParseWord(queue);
        fn.output = output;
        
        //Parse Block
        AST block = ParseBlock(queue);
        if(block == null){
            throw new ParseException("A function declaration is missing it's body.");
        }
        
        fn.setChild(0, block);

        return fn;
    }
    
    // <clone> :: new <identifier>
    private AST ParseClone(RQueue<ParseToken.TokenMatch> queue){
        if(!ParseNew(queue)){
            return null;
        }
        AST scope = ParseScopeResolution(queue);
        if(scope == null){
            //throw error
        }
        Instanciate n = new Instanciate();
        n.setChild(0, scope);
        return n;
    }
    
    // <struct> :: def { <mapping> list }
    private AST ParseStruct(RQueue<ParseToken.TokenMatch> queue){
        if(!ParseDef(queue)){
            return null;
        }
        
        if(!ParseOpenBlock(queue)){
            return null;
        }
        
        StructDef struct = new StructDef();
        
        while(true){
            AST mapping = ParseMapping(queue);
            if(mapping == null)
                break;
            struct.setChild(-1, mapping);
            if(!ParseComma(queue)){
                break;
            }
        }
        
        if(!ParseCloseBlock(queue)){
            return null;
        }
       
        return struct;
    }
    
    // <mapping> :: <word> : <evaluatable>
    private AST ParseMapping(RQueue<ParseToken.TokenMatch> queue){
    
        String map = ParseWord(queue);
        if(map == null)
            return null;
        
        if(!ParseMap(queue)){
            return null;
        }
        
        AST exp = ParseEvaluatable(queue);
        
        Mapping mapping = new Mapping();
        mapping.MappingDetails.name = map;
        mapping.MappingDetails.exp = exp;
        
        return mapping;
    }
    
    // <scope-resolution> :: <identifier> . <scope-resolution>
    private AST ParseScopeResolution(RQueue<ParseToken.TokenMatch> queue){
        if(queue.isEmpty() || !queue.peek().equals(WORD)){
            return null;
        }
        
        LinkedList<String> scopes = new LinkedList<String>();
        String scope = queue.pollFirst().match;
        String id = scope;
        scopes.push(id);
        while(ParseScope(queue)){
            id = ParseWord(queue);
            if(id == null){
                throw new ParseException("Expecting identifier after scope, but none was given");
            }
            scopes.addLast(id);
        }
        
        ScopeResolution res = new ScopeResolution();
        res.scopes = scopes;
        
        return res;
    }
    
    private boolean ParseEquals(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(EQUALS)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseScope(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(SCOPE)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseDef(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(STRUCT)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseOpenBracket(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(OPEN_BRACKET)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseCloseBracket(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(CLOSE_BRACKET)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseOpenBlock(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(OPEN_BLOCK)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseCloseBlock(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(CLOSE_BLOCK)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseComma(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(COMMA)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseSemiColon(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(STATEMENT_END)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private String ParseWord(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(WORD)){
            return queue.pollFirst().match;
        }
        return null;
    }
    
    private boolean ParseMap(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(MAP)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseNew(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(NEW)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseOpenArray(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(OPEN_ARRAY)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseCloseArray(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(CLOSE_ARRAY)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseFuncMap(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(FUNC)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseAddSymbol(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(ADD)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseSubSymbol(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(SUB)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseMulSymbol(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(MUL)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseDivSymbol(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(DIV)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParsePowerSign(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(POW)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseOrSymbol(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(OR)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseAndSymbol(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(AND)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseNot(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(NOT)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseTrueLiteral(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(TRUE)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseFalseLiteral(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(FALSE)){
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private String ParseNumber(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(NUM)){  
            return queue.pollFirst().match;
        }
        return null;
    }
    
    private boolean ParseIncludeKeyword(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(INCLUDE)){  
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private String ParseStringLiteral(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(STRING)){  
            return queue.pollFirst().match.replaceAll("^\\\"|\\\"$", "");
        }
        return null;
    }
    
    private boolean ParseIfKeyword(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(IF)){  
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseWhileKeyword(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(WHILE)){  
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseVarKeyword(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(VAR)){  
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseGreaterThan(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(GREATER)){  
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseLessThan(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(LESS)){  
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseTry(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(TRY)){  
            queue.pollFirst();
            return true;
        }
        return false;
    }
    
    private boolean ParseCatch(RQueue<ParseToken.TokenMatch> queue){
        if(!queue.isEmpty() && queue.peek().equals(CATCH)){  
            queue.pollFirst();
            return true;
        }
        return false;
    }
}
