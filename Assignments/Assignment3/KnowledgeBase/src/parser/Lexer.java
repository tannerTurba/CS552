/*
 * Tanner Turba
 * December 4, 2023
 * CS 552 - Artificial Intelligence - Assignment 3
 * 
 * This is the lexer that is used to derive symbols from input String.
 * Can be configured to take input from a file or from user input.
 */
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
	private final String specialChars = "vV<>=()^~#";
	private final char eolnCh = '\n';
	private final char eofCh = '\004';
	private boolean isSpaceDelimited = true;

	/**
	 * Creates a lexer that reads from a file
	 * @param filePath the filepath to read from
	 */
	public Lexer(String filePath) {
		try {
			input = new BufferedReader(new FileReader(filePath));
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found: " + filePath);
			System.exit(1);
		}
	}

	/**
	 * Creates a lexer that reads from user input
	 */
	public Lexer() {
		input = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * Closes the lexer
	 */
	public void close() {
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the next character 
	 * @return the next character
	 */
	private char nextChar() {
		if (ch == eofCh)
			error("Attempt to read past end of file");
		col++;

		// If column counter exceeds the number of characters in the line
		if (col >= line.length()) {
			// Try to read line
			try {
				line = input.readLine( );
			} catch (IOException e) {
				System.err.println(e);
				System.exit(1);
			}

			// Set line to EOF, which will eventually return to caller
			if (line == null)
				line = "" + eofCh;
			else {
				lineno++;
				line += eolnCh;
			} 
			col = 0;
		}
		return line.charAt(col);
	}

	/**
	 * Gets the next Token from the input
	 * @return a Token
	 */
	public Token next() {
		do {
			// keyword
			if (isLetter(ch) || isDigit(ch)) {
				String spelling = concat(letters + digits);
				return Token.symbol(spelling);
			}
            else {
				switch (ch) {			
					case '\u0020': case '\t': case '\r':
						ch = nextChar();
						break;
					case '#':  // comment
						ch = nextChar();
						do {
							ch = nextChar();
						} while (ch != eolnCh);
						isSpaceDelimited = true;
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
						if (ch != '>') 
							error("Illegal character, expecting =");
						ch = nextChar();
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

	/**
	 * Determines if a character is a valid letter
	 * @param c the character to test
	 * @return true if c is a valid letter
	 */
	private boolean isLetter(char c) {
		return letters.contains(c + "");
	}

	/**
	 * Determines if a character is a valid digit
	 * @param c the character to test
	 * @return true if c is a valid digit
	 */
	private boolean isDigit(char c) {
		return Character.isDigit( c );
	}

	/**
	 * Concatenates characters together until one that is not a part of 
	 * the identified set is read
	 * @param set The set of valid characters
	 * @return the resulting String
	 */
	private String concat(String set) {
		String r = "";
		if (isSpaceDelimited) {
			// concatenate until a space or invalid character is found.
			do {
				r += ch;
				ch = nextChar();
			} while (set.indexOf(ch) >= 0);
		}
		else {
			// concatenate until an invalid character is found.
			do {
				if (set.indexOf(ch) >= 0) {
					r += ch;
					ch = nextChar();
				}
				else {
					ch = nextChar();
				}
			} while (specialChars.indexOf(ch) < 0 && ch != eofCh && ch != eolnCh);
		}
		return r;
	}

	/**
	 * Set whether or not space delimiting should be enforeced
	 * @param isSpaceDelimited true if space delimiting should be enforeced
	 */
	public void setSpaceDelimited(boolean isSpaceDelimited) {
		this.isSpaceDelimited = isSpaceDelimited;
	}

	/**
	 * Prints an error message to the console
	 * @param msg the error message
	 */
	public void error (String msg) {
		System.err.print(line);
		System.err.println("Error: column " + col + " " + msg);
		System.exit(1);
	}
}
