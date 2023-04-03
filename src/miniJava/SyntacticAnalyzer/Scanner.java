/**
 *  Scan the a single line of input
 *
 *  Grammar:
 *   num ::= digit digit*
 *   digit ::= '0' | ... | '9'
 *   oper ::= '+' | '*'
 *   
 *   whitespace is the space character
 */
package miniJava.SyntacticAnalyzer;

import java.io.*;

import miniJava.ErrorReporter;

public class Scanner{

	private InputStream inputStream;
	private ErrorReporter reporter;
	private SourcePosition position;
	private char currentChar;
	private StringBuilder currentSpelling;
	
	// true when end of line is found
	private boolean eot = false; 

	public Scanner(InputStream inputStream, ErrorReporter reporter) {
		this.inputStream = inputStream;
		this.reporter = reporter;
		this.position = new SourcePosition();

		// initialize scanner state
		
		readChar();
	}

	/**
	 * skip whitespace and scan next token
	 */
	
	public Token scan() {
		// start of a token: collect spelling and identify token kind
		currentSpelling = new StringBuilder();
		TokenKind kind;
		String spelling;

		do {
			// skip whitespace
			while (!eot && isWhitespace(currentChar))
				skipIt();

			kind = scanToken();
			spelling = currentSpelling.toString();

			// skip comments
			if (kind == TokenKind.DIVIDE && (currentChar == '/' || currentChar == '*')) {
				kind = null;
				currentSpelling.setLength(0);

				if (currentChar == '/') {
					do {
						skipIt();
					} while (currentChar != eolWindows && currentChar != eolUnix && !eot);
				} else if (currentChar == '*') {
					skipIt();
					do {
						if (currentChar == '*') {
							skipIt();
							if (currentChar == '/') {
								skipIt();
								break;
							} else {
								continue;
							}
						}
						skipIt();
					} while (!eot);
					
					if (eot) {
						// comment was never closed
						kind = TokenKind.ERROR;
					}
				}
			}
		} while (kind == null);

		// return new token
		return new Token(kind, spelling);
	}

	
public TokenKind scanToken() {
		
		if(eot)
			return(TokenKind.EOT);
		
		switch(currentChar) {
		 case 'a':  case 'b':  case 'c':  case 'd':  case 'e':
		 case 'f':  case 'g':  case 'h':  case 'i':  case 'j':
		 case 'k':  case 'l':  case 'm':  case 'n':  case 'o':
		 case 'p':  case 'q':  case 'r':  case 's':  case 't':
		 case 'u':  case 'v':  case 'w':  case 'x':  case 'y':
		 case 'z':
		 case 'A':  case 'B':  case 'C':  case 'D':  case 'E':
		 case 'F':  case 'G':  case 'H':  case 'I':  case 'J':
		 case 'K':  case 'L':  case 'M':  case 'N':  case 'O':
		 case 'P':  case 'Q':  case 'R':  case 'S':  case 'T':
		 case 'U':  case 'V':  case 'W':  case 'X':  case 'Y':
		 case 'Z':
			 while (isLetter(currentChar) || isDigit(currentChar) || currentChar == '_') {
			 takeIt();
			 }
			 switch(currentSpelling.toString()) {
			 case "begin":
				 return(TokenKind.BEGIN);
				 
			 case "class":
				 return TokenKind.CLASS;
				 
			 case "else":
				 return TokenKind.ELSE;
				 
			 case "false":
				 return TokenKind.FALSE;
				 
			 case "true":
				 return TokenKind.TRUE;	
				 
			 case "if":
				 return TokenKind.IF;
				 
			 case "int":
				 return TokenKind.INT;
				 
			 case "new":
				 return TokenKind.NEW;
				 
			 case "null":
				 return TokenKind.NULL;
				 
			 case "boolean":
				 return TokenKind.BOOLEAN;
				 
			 //case "String" :
				 //return TokenKind.STRING;
				 
			 case "private":
				 return TokenKind.PRIVATE;
				 
			 case "public":
				 return TokenKind.PUBLIC;
				 
			 case "return":
				 return TokenKind.RETURN;
				 
			 case "static":
				 return TokenKind.STATIC;
				 
			 case "this":
				 return TokenKind.THIS;
				 
			 case "void":
				 return TokenKind.VOID;
				 
			case "while":
				return TokenKind.WHILE;
				
			case "DO":
				return TokenKind.DO;
				
			case "switch":
				return TokenKind.SWITCH;
				
			case "package":
				return TokenKind.PACKAGE;
				
			case "throw":
				return TokenKind.THROW;
				
			case "throws":
				return TokenKind.THROWS;
				
			case "try":
				return TokenKind.TRY;
				
			case "catch":
				return TokenKind.CATCH;
				
			case "abstract":
				return TokenKind.ABSTRAT;
				
			case "continue":
				return TokenKind.CONTINUE;
				
			case "default":
				return TokenKind.DEFAULT;
				
			case "break":
				return TokenKind.BREAK;
				
			case "double":
				return TokenKind.DOUBLE;
				
			case "implements":
				return TokenKind.IMPLEMENTS;
				
			case "protected":
				return TokenKind.PROTECTED;
				
			case "float":
				return TokenKind.FLOAT;
				
			case "native":
				return TokenKind.NATIVE;
				
			case "super":
				return TokenKind.SUPER;
																											
			default:
				return(TokenKind.ID);
}
			 
		case '(': 
			takeIt();
			return(TokenKind.LPAREN);

		case ')':
			takeIt();
			return(TokenKind.RPAREN);

		case '0': case '1': case '2': case '3': case '4':
		case '5': case '6': case '7': case '8': case '9':
			takeIt();
			while (isDigit(currentChar))
				takeIt();
			return(TokenKind.NUM);
		
		 case '[':
		      takeIt();
		      return TokenKind.LBRACKET;
		      
		 case ']':
		      takeIt();
		      return TokenKind.RBRACKET;
		 
		 case ';':
		      takeIt();
		      return TokenKind.SEMICOLON;
		 case '.':
			 takeIt();
			 return TokenKind.PERIOD;

		 case ',':
		      takeIt();
		      return TokenKind.COMMA;
		 		      
		 case '+':  
			 takeIt();
			 return TokenKind.PLUS;
		 
		 case '-':  
			 takeIt();
			 return TokenKind.MINUS;
			 
		 case '*': 
			 takeIt();
			 return TokenKind.TIMES;
			 		 
		 case '/': 
			 takeIt();
			 return TokenKind.DIVIDE;
			 
		 
		 case '=':
			 takeIt();
			 if(currentChar=='=') {
				 takeIt();
				 return TokenKind.EQUAL;
			 }
			 return TokenKind.ASSIGN;
			 
		 case '<': 
			 takeIt();
			 if(currentChar=='=') {
				 takeIt();
				 return TokenKind.LESSEQUAL;
			 }
			 return TokenKind.LESS;
			 		 
		 
		 case '>':  
			 takeIt();
			 if(currentChar=='=') {
				 takeIt();
				 return TokenKind.GREATEREQUAL;
			 }
			 return TokenKind.GREATER;
			 			 
		 case '&':
				takeIt();
				if (currentChar == '&') {
					takeIt();
					return TokenKind.AND;
				}
				scanError("Encountered single '&'");
				return TokenKind.ERROR; 		
				
		 case '|':
				takeIt();
				if (currentChar == '|') {
					takeIt();
					return TokenKind.OR;
				}
				scanError("Encountered single '|'");
				return TokenKind.ERROR;

		 case '%':  
			 takeIt();
			 return TokenKind.REMAINDER;			 		 
			 
		 case '!': 
			 takeIt();
			 if(currentChar=='=') {
				 takeIt();
				 return TokenKind.NOTEQUAL;
			 }
			 return TokenKind.NOT;
		 case '{':
			 takeIt();
			 return TokenKind.LBRACE;
		 case '}':
			 takeIt();
			 return TokenKind.RBRACE;
			 
		      	
		 default:
			scanError("Unrecognized character '" + currentChar + "' in input!!!!!!!");
			return(TokenKind.ERROR);
		}
	}

	private void takeIt() {
		currentSpelling.append(currentChar);
		nextChar();
	}

	private void skipIt() {
		nextChar();
	}

	private boolean isDigit(char c) {
		return (c >= '0') && (c <= '9');
	}
	
	private boolean isLetter(char c) {
		 return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'); 
	}
	
	private boolean isOperator(char c) {
		 return (c == '+' || c == '-' || c == '*' || c == '/' ||
				    c == '=' || c == '<' || c == '>' || c == '\\' ||
				    c == '&' || c == '@' || c == '%' || c == '^' ||
				    c == '?');
	}
	
	private boolean isAlphabetic(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	private boolean isWhitespace(char c) {    
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}

	private void scanError(String m) {
		reporter.reportError("Scan Error: " + m);
	}
	


	private final static char eolUnix = '\n';
	private final static char eolWindows = '\r';

	/**
	 * advance to next char in inputstream
	 * detect end of file or end of line as end of input
	 */
	private void nextChar() {
		if (!eot)
			readChar();
	}

	private void readChar() {        
        if (currentChar == '\t') {
            this.position.columnNumber += 4;
        } else if (currentChar == '\n') {
            this.position.lineNumber++;
            this.position.columnNumber = 1;
        } else {
            this.position.columnNumber++;
        }

        try {
            int c = inputStream.read();
            currentChar = (char) c;
            if (c == -1) {
                eot = true;
            }
        } catch (IOException e) {
            scanError("I/O Exception!");
            eot = true;
        }
    }
	   public SourcePosition currentPosition() {
	        return this.position.copy();
	    }
	}

    