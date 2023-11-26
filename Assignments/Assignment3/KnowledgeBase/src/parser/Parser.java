package parser;

import types.*;

public class Parser {
    Token token;
    Lexer lexer;

    public Parser(Lexer ts) {
        lexer = ts;
        token = lexer.next();
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

    public Clause getClause() {
        Clause c = new Clause();
        while (!token.equals(Token.eolnTok)) {
            if (token.type() == TokenType.Symbol) {
                c.add(new Symbol(token.value(), false));
            }
            else if (token.equals(Token.notTok)) {
                token = lexer.next();
                c.add(new Symbol(token.value(), true));
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
            binaryConnector = BinaryConnective.AND;
        }
        else if (token.equals(Token.orTok)) {
            binaryConnector = BinaryConnective.OR;
        }
        else if (token.equals(Token.ifTok)) {
            binaryConnector = BinaryConnective.IF;
        }
        else if (token.equals(Token.iffTok)){
            binaryConnector = BinaryConnective.IFF;
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
            // unarySentence.getSymbol().negate();
            return new UnarySentence(unarySentence);
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
}