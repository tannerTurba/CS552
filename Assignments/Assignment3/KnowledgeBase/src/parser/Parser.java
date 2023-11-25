package parser;

import types.*;

public class Parser {
    Token token;          		// current token from the input stream
    Lexer lexer;

    public Parser(Lexer ts) { 	// Open the Clite source program
        lexer = ts;           	// as a token stream, and
        token = lexer.next(); 	// retrieve its first Token
    }

    public String getCommand() {
        if (token.equals(Token.eofTok)) {
            return "EOF";
        }
        String cmd = token.value();
        token = lexer.next();
        return cmd;
    }
  
    private String match (TokenType t) {
        String value = token.value();
        if (token.type().equals(t)) {
            token = lexer.next();
        } else {
            error(t);
        }
        return value;
    }
 
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }

    public Clause getClause() {
        Clause c = new Clause();
        while (!token.equals(Token.eolnTok)) {
            if (token.type() == TokenType.Symbol) {
                c.add(new UnarySentence(token.value(), false).getSymbol());
            }
            else if (token.equals(Token.notTok)) {
                token = lexer.next();
                UnarySentence s = new UnarySentence(token.value(), true);
                c.add(s.getSymbol());
            }
            token = lexer.next();
        }
        return c;
    }
  
    // Sentence ::= UnarySentence | BinarySentence
    public Sentence getSentence() {
        Sentence s = getBinarySentence();
        token = lexer.next(); //consume newline
        return s;
    }

    // BinarySentence ::= UnarySentence ^ UnarySentence | UnarySentence v UnarySentence | UnarySentence => UnarySentence | UnarySentence <=> UnarySentence
    public Sentence getBinarySentence() {
        UnarySentence s1 = getUnarySentence();

        // Token token = lexer.next();
        BinaryConnective binaryConnector;
        if (token.equals(Token.andTok)) {
            binaryConnector = new BinaryConnective("^");
        }
        else if (token.equals(Token.orTok)) {
            binaryConnector = new BinaryConnective("v");
        }
        else if (token.equals(Token.ifTok)) {
            binaryConnector = new BinaryConnective("=>");
        }
        else if (token.equals(Token.iffTok)){
            binaryConnector = new BinaryConnective("<=>");
        }
        else {
            return s1;
        }
        
        token = lexer.next();
        UnarySentence s2 = getUnarySentence();
        return new BinarySentence(s1, binaryConnector, s2);
    }

    // UnarySentence ::= Symbol | (Sentence) | ~UnarySentence
    public UnarySentence getUnarySentence() {
        if (token.equals(Token.leftParenTok)) {
            token = lexer.next();
            Sentence sentence = getBinarySentence();
            token = lexer.next(); //Consume closing parens
            // token = lexer.next();
            return new UnarySentence(sentence);
        }
        else if (token.equals(Token.notTok)) {
            token = lexer.next();
            UnarySentence unarySentence = getUnarySentence();
            token = lexer.next();
            unarySentence.getSymbol().negate();
            return unarySentence;
        }
        else {
            return new UnarySentence(getSymbol());
        }
    }

    // Symbol ::= P | Q | R ...
    public Symbol getSymbol() {
        String val = match(TokenType.Symbol);
        return new Symbol(val, false);
    }

    // @SuppressWarnings("rawtypes")
	// private Value literal( ) {
    //     String s = null;
    //     switch (token.type()) {
    //     case IntLiteral:        	
    //         s = match(TokenType.IntLiteral);
    //         return new Value<Integer>( Integer.parseInt(s) );
    //     case True:
    //         s = match(TokenType.True);
    //         return new Value<Boolean>(true);
    //     case False:
    //         s = match(TokenType.False);
    //         return new Value<Boolean>(false);
    //     default:
    //         throw new IllegalArgumentException( "error" );
    //     }
    // }
    
    private boolean isUnaryOp() {
        return token.type().equals(TokenType.Not);
    }
    
    // public static void main(String args[]) {
    // 	String dir = "/Users/hunt/Dropbox/UWL/teaching/cs421/OtherMaterials/AuthorMaterials2/softwarestudents/clite/programs/";
    // 	String file = dir +  "hunt-p2.cpp";
    	
    // 	Parser parser  = new Parser(new Lexer(file) );
    //     Program prog = parser.program();
    //     System.out.println(TypeChecker.isValid( prog ) );
    //     System.out.println( prog );
    // }

} // Parser