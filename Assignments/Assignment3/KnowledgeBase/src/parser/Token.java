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
    public static final Token tellCTok = new Token(TokenType.TellC, "tellc");

    private TokenType type;
    private String value = "";

    private Token (TokenType t, String v) {
        type = t;
        value = v;
        if (t.compareTo(TokenType.Eof) < 0) {
            int ti = t.ordinal();
            reserved[ti] = v;
            token[ti] = this;
        }
    }

    public TokenType type() { 
        return type; 
    }

    public String value() { 
        return value; 
    }

    public String toString() {
        return value;
    }

    public static Token keyword(String name) {
        char ch = name.charAt(0);
        if (ch >= 'A' && ch <= 'Z') return mkSymbolTok(name);
        for (int i = 0; i < KEYWORDS; i++)
           if (name.equals(reserved[i]))  return token[i];
        return mkSymbolTok(name);
    } // keyword

    public static Token mkSymbolTok(String name) {
        return new Token(TokenType.Symbol, name);
    }
}