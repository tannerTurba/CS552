/*
 * Tanner Turba
 * December 4, 2023
 * CS 552 - Artificial Intelligence - Assignment 3
 * 
 * This is the parser that is used to derive meaning from Tokens that are 
 * received from the Lexer.
 */
package parser;

import types.*;

public class Parser {
    Token token;
    Lexer lexer;

    /**
     * Creates a parser
     * @param l the lexer to use
     */
    public Parser(Lexer l) {
        lexer = l;
        token = lexer.next();
    }

    /**
     * Closes the parser
     */
    public void close() {
        lexer.close();
    }

    /**
     * Gets a command string, which will tell the KBDriver what to do
     * @return a command string
     */
    public String getCommand() {
        // Consume all leading white space
        if (token.equals(Token.eofTok)) {
            return "EOF";
        }
        while (token.equals(Token.eolnTok)) {
            token = lexer.next();
        }

        // Get the value of the token and return
        String cmd = token.getValue();
        lexer.setSpaceDelimited(false);
        token = lexer.next();
        return cmd;
    }
  
    /**
     * Make sure the Token matches a specific type before getting
     * the next Token
     * @param t the desired TokenType
     * @return the Token as a String
     */
    private String match(TokenType t) {
        String value = token.getValue();
        if (token.getType().equals(t)) {
            token = lexer.next();
        } else {
            //print error and exit execution
            System.err.println("Syntax error: expecting: " + t 
                           + "; saw: " + token);
            System.exit(1);
        }
        return value;
    }

    /**
     * Gets a clause from the lexer
     * @return a Clause
     */
    public Clause getClause() {
        Clause c = new Clause();
        while (!token.equals(Token.eolnTok)) {
            // a
            if (token.getType() == TokenType.Symbol) {
                c.add(new Symbol(token.getValue(), false));
            }
            // ~a
            else if (token.equals(Token.notTok)) {
                token = lexer.next();
                c.add(new Symbol(token.getValue(), true));
            }
            token = lexer.next();
        }
        lexer.setSpaceDelimited(true);
        return c;
    }
  
    /**
     * Gets a Sentence from the input which is defines as the following:
     * Sentence ::= UnarySentence 
     *            | BinarySentence
     * @return a Sentence
     */
    public Sentence getSentence() {
        Sentence s = getBinarySentence();
        lexer.setSpaceDelimited(true);
        token = lexer.next(); //consume newline
        return s;
    }

    /**
     * Gets a BinarySentence from the input, which is defined as the following:
     * BinarySentence ::= UnarySentence ^ UnarySentence
     *                  | UnarySentence v UnarySentence
     *                  | UnarySentence => UnarySentence
     *                  | UnarySentence <=> UnarySentence
     * @return a BinarySentence
     */
    public Sentence getBinarySentence() {
        UnarySentence left = getUnarySentence();

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
            // No BinaryConnective, so it must be a UnarySentence. Return left
            return left;
        }
        
        token = lexer.next();
        UnarySentence right = getUnarySentence();
        return new BinarySentence(left, binaryConnector, right);
    }

    /**
     * Gets a UnarySentence from the input, which is defined as the following:
     * UnarySentence ::= Symbol 
     *                 | (Sentence) 
     *                 | ~UnarySentence
     * @return a UnarySentence
     */
    public UnarySentence getUnarySentence() {
        // (<Sentence>)
        if (token.equals(Token.leftParenTok)) {
            token = lexer.next();
            Sentence sentence = getBinarySentence();
            token = lexer.next(); //Consume closing parens
            return new UnarySentence(sentence);
        }
        // ~ <Symbol>
        else if (token.equals(Token.notTok)) {
            token = lexer.next();
            UnarySentence unarySentence = getUnarySentence();
            return new UnarySentence(unarySentence);
        }
        // <Symbol>
        else {
            return new UnarySentence(getSymbol());
        }
    }

    /**
     * Gets a Symbol from the input, which can be defined as the following:
     * Symbol ::= P | Q | R ...
     * @return
     */
    public Symbol getSymbol() {
        String val = match(TokenType.Symbol);
        return new Symbol(val, false);
    }
}