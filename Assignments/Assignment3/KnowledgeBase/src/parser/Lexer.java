package parser;

import java.io.*;

public class Lexer {

	@SuppressWarnings("unused")
	private boolean isEof = false;
	private char ch = ' '; 
	private BufferedReader input;
	private String line = "";
	@SuppressWarnings("unused")
	private int lineno = 0;
	private int col = 1;
	private final String letters = "abcdefghijklmnopqrstuwxyz" + "ABCDEFGHIJKLMNOPQRSTUWXYZ";
	private final String digits = "0123456789";
	private final char eolnCh = '\n';
	private final char eofCh = '\004';


	public Lexer(String fileName) { // source filename
		try {
			input = new BufferedReader (new FileReader(fileName));
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found: " + fileName);
			System.exit(1);
		}
	}

	private char nextChar() { // Return next char
		if (ch == eofCh)
			error("Attempt to read past end of file");
		col++;
		if (col >= line.length()) {
			try {
				line = input.readLine( );
			} catch (IOException e) {
				System.err.println(e);
				System.exit(1);
			} // try
			if (line == null) // at end of file
				line = "" + eofCh;
			else {
				// System.out.println(lineno + ":\t" + line);
				lineno++;
				line += eolnCh;
			} // if line
			col = 0;
		} // if col
		return line.charAt(col);
	}


	public Token next() { // Return next token
		do {
			if (isLetter(ch) || isDigit(ch)) { // ident or keyword
				String spelling = concat(letters + digits);
				return Token.keyword(spelling);
			}
            else {
				switch (ch) {			
					case ' ': case '\t': case '\r':
						ch = nextChar();
						break;
					case '/':  // comment
						ch = nextChar();
						do {
							ch = nextChar();
						} while (ch != eolnCh);
						ch = nextChar();
						break;
					case eolnCh:
						ch = nextChar();
						return Token.eolnTok;
					case eofCh: 
						return Token.eofTok;
					case '(': 
						ch = nextChar();
						return Token.leftParenTok;
					case ')': 
						ch = nextChar();
						return Token.rightParenTok;
					case '^': 
						ch = nextChar();
						return Token.andTok;
					case 'v': 
						ch = nextChar();
						return Token.orTok;
                    case 'V': 
						ch = nextChar();
						return Token.orTok;
					case '=':
                        check('>');
						return Token.ifTok;
					case '<':
						ch = nextChar();
						if (ch != '=') 
							error("Illegal character, expecting =");
						ch = nextChar();
                        if (ch != '>') 
							error("Illegal character, expecting >");
						ch = nextChar();
						return Token.iffTok;
					case '~':
						ch = nextChar();
						return Token.notTok;
					default:  error("Illegal character " + ch); 
				} 
			}
		} while (true);
	} 


	private boolean isLetter(char c) {
		return letters.contains(c + "");
	}

	private boolean isDigit(char c) {
		return Character.isDigit( c );
	}

	private void check(char c) {
		ch = nextChar();
		if (ch != c) 
			error("Illegal character, expecting " + c);
		ch = nextChar();
	}

	private String concat(String set) {
		String r = "";
		do {
			r += ch;
			ch = nextChar();
		} while (set.indexOf(ch) >= 0);
		return r;
	}

	public void error (String msg) {
		System.err.print(line);
		System.err.println("Error: column " + col + " " + msg);
		System.exit(1);
	}
}
