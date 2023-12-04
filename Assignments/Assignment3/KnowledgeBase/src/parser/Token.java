/*
 * Tanner Turba
 * December 4, 2023
 * CS 552 - Artificial Intelligence - Assignment 3
 * 
 * This is the class that defines a Token that is returned from the Lexer.
 */
package parser;

public class Token {
    private static final int KEYWORDS = TokenType.Eof.ordinal();
    private static final String[] reserved = new String[KEYWORDS];
    private static Token[] token = new Token[KEYWORDS];
    public static final Token eofTok = new Token(TokenType.Eof, "<<EOF>>");
    public static final Token leftParenTok = new Token(TokenType.LeftParen, "(");
    public static final Token rightParenTok = new Token(TokenType.RightParen, ")");
    public static final Token ifTok = new Token(TokenType.If, "=>");
    public static final Token iffTok = new Token(TokenType.Iff, "<=>");
    public static final Token notTok = new Token(TokenType.Not, "~");
    public static final Token andTok = new Token(TokenType.And, "^");
    public static final Token orTok = new Token(TokenType.Or, "v");
    public static final Token eolnTok = new Token(TokenType.EoLn, "\n");

    private TokenType type;
    private String value = "";

    /**
     * Create a Token with the TokenType t and Value v.
     * @param t TokenType
     * @param v Value
     */
    private Token (TokenType t, String v) {
        type = t;
        value = v;
        if (t.compareTo(TokenType.Eof) < 0) {
            int ti = t.ordinal();
            reserved[ti] = v;
            token[ti] = this;
        }
    }

    /**
     * Gets the type of the Token
     * @return a TokenType
     */
    public TokenType getType() { 
        return type; 
    }

    /**
     * Gets the value of the Token
     * @return a String value
     */
    public String getValue() { 
        return value; 
    }

    /**
     * Gets a string representation of the Token
     */
    public String toString() {
        return value;
    }

    /**
     * Creates a Token of the type Symbol 
     * @param name the value of the Token
     * @return a new Token
     */
    public static Token symbol(String name) {
        char ch = name.charAt(0);
        if (ch >= 'A' && ch <= 'Z') new Token(TokenType.Symbol, name);
        for (int i = 0; i < KEYWORDS; i++)
           if (name.equals(reserved[i]))  return token[i];
        return new Token(TokenType.Symbol, name);
    }
}