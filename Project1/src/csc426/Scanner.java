package csc426;

import java.io.IOException;
import java.io.Reader;

/**
 * A Lexical Analyzer for a subset of YASL. Uses a (Mealy) state machine to
 * extract the next available token from the input each time next() is called.
 * Input comes from a Reader, which will generally be a BufferedReader wrapped
 * around a FileReader or InputStreamReader (though for testing it may also be
 * simply a StringReader).
 * 
 * @author bhoward
 */
public class Scanner {
	/**
	 * Construct the Scanner ready to read tokens from the given Reader.
	 * 
	 * @param in
	 */
	public Scanner(Reader in) {
		source = new Source(in);
	}

	/**
	 * Extract the next available token. When the input is exhausted, it will
	 * return an EOF token on all future calls.
	 * 
	 * @return the next Token object
	 */
	public Token next() {
		// TODO implement the state machine here:
		// - have a "state" variable start in the initial state
		// - repeatedly look at source.current (the current character),
		//   perform an appropriate action based on them, and assign
		//   a new state until the end of a token is seen
		// - call source.advance() on each state transition, until you
		//   see the first character after the token
		// - if source.atEOF is true, then return an EOF token:
		//     new Token(source.line, source.column, TokenType.EOF, null)
		
		int state = 0;
		StringBuilder lexeme = new StringBuilder();
		int startLine = source.line;
		int startColumn = source.column;
		
		char[] testKey;
		int temp;
		
		while (true) {
			switch (state) {
			case 0:
				//EOF case 
				if (source.atEOF) {
					return new Token(source.line, source.column, TokenType.EOF, null);
				}
				
				//zero case
				else if (source.current == '0') {
					startLine = source.line;
					startColumn = source.column;
					lexeme.append(source.current);
					source.advance();
					state = 1;	
				}
				
				//number case
				else if (Character.isDigit(source.current)) {
					startLine = source.line;
					startColumn = source.column;
					lexeme.append(source.current);
					source.advance();
					state = 2;
				}
				
				//semicolon case
				else if (source.current == ';') {
					startLine = source.line;
					startColumn = source.column;
					source.advance();
					state = 3;
				}	
				
				//plus case
				else if (source.current == '+') {
					startLine = source.line;
					startColumn = source.column;
					source.advance();
					state = 4;
				}   
				
				//minus case
				else if (source.current == '-') {
					startLine = source.line;
					startColumn = source.column;
					source.advance();
					state = 5;		
				}	
				
				//star case
				else if (source.current == '*') {
					startLine = source.line;
					startColumn = source.column;
					source.advance();
					state = 6;
				}	
				
				//assign case
				else if (source.current == '=') {
					startLine = source.line;
					startColumn = source.column;
					source.advance();
					state = 7;
				}
				
				//period case	
				else if (source.current == '.') {
					startLine = source.line;
					startColumn = source.column;
					source.advance();
					state = 8;
				}	
				
				//braces comment case
				else if (source.current == '{') {
					while(source.current != '}' || !source.atEOF) {
						source.advance();
						if(source.atEOF) {
							System.err.println("Unterminated comment!");
							System.exit(0); //exit because it's at the EOF
						}
						else if(source.current  == '}') {
							break;
						}
					}
					source.advance();
				}	
				
				//slash comment	
				else if (source.current == '/') {
					startLine = source.line;
					startColumn = source.column;
					source.advance();
					if(source.current == '/') {
						while(source.current!= '\n') {
							source.advance();
						}
						source.advance();
					}
					else {
						System.err.println("Comment error at " + startLine + ":" + startColumn);
						while(!Character.isWhitespace(source.current)) {
							source.advance();}
						source.advance();
					}
				}	
				
				//identifier and keyword case
				else if (Character.isLetter(source.current)) {
					startLine = source.line;
					startColumn = source.column;
					state = 9;
				} 
				
				//whitespace case
				else if (Character.isWhitespace(source.current)) {
					source.advance();
				} 
				
				//error
				else {
					startLine = source.line;
					startColumn = source.column;
					System.err.println("Illegal character at (" + startLine + ":" + startColumn + ") : " + source.current);
					while(!Character.isWhitespace(source.current)) {
						source.advance();
					} //skip the whole part that have the illegal character, not just the character itself
					source.advance();
				}
				break;
				
			case 1:
				return new Token(startLine, startColumn, TokenType.NUM, lexeme.toString());
				
			case 2:
				if (!Character.isDigit(source.current) && !Character.isWhitespace(source.current)) {
					while(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
						lexeme.append(source.current);
						source.advance();
					}
					return new Token(startLine, startColumn, TokenType.ID, lexeme.toString());
				} 
				else {
					while(!Character.isWhitespace(source.current) && (Character.isDigit(source.current))) {
						lexeme.append(source.current);
						source.advance();
						if(Character.isLetter(source.current)) {
							while(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
								lexeme.append(source.current);
								source.advance();
							}
							return new Token(startLine, startColumn, TokenType.ID, lexeme.toString());
						}
					}
					return new Token(startLine, startColumn, TokenType.NUM, lexeme.toString());
				}
				
			case 3:
				return new Token(startLine, startColumn, TokenType.SEMI, "");
			
			case 4:
				return new Token(startLine, startColumn, TokenType.PLUS, "");
			
			case 5:
				return new Token(startLine, startColumn, TokenType.MINUS, "");
				
			case 6:
				return new Token(startLine, startColumn, TokenType.STAR, "");
				
			case 7:
				return new Token(startLine, startColumn, TokenType.ASSIGN, "");
			
			case 8:
				return new Token(startLine, startColumn, TokenType.PERIOD, "");
			
			case 9:
				if(source.current == 'p') {
					lexeme.append(source.current);
					source.advance();
					if(source.current == 'r') {
						lexeme.append(source.current);
						source.advance();
						if(source.current == 'o') {
							state = 10;
						}
						else if(source.current == 'i') {
							state = 16;
						}
						else {
							while(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
								lexeme.append(source.current);
								source.advance();
							}
							return new Token(startLine, startColumn, TokenType.ID, lexeme.toString());
						}
					}
					else {
						while(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
							lexeme.append(source.current);
							source.advance();
						}
						return new Token(startLine, startColumn, TokenType.ID, lexeme.toString());
					}
				}
				else if(source.current == 'v') {
					state = 11;
				}
				else if(source.current == 'b') {
					state = 12;
				}
				else if(source.current == 'e') {
					state = 13;
				}
				else if(source.current == 'd') {
					state = 14;
				}
				else if(source.current == 'm') {
					state = 15;
				}
				else if(source.current == 'c') {
					state = 17;
				}
				else {
					while(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
						lexeme.append(source.current);
						source.advance();
					}
					return new Token(startLine, startColumn, TokenType.ID, lexeme.toString()); 
				}
				
			case 10:
				testKey = "ogram".toCharArray();
				temp = 0;
				while(temp<testKey.length) {
					if(source.current == testKey[temp]){
						lexeme.append(source.current);
						source.advance();
						temp++;
					}
					else{
						state = 9;
						break;
					}	
				}
				if(temp == 5) {
					if(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
						state = 9;
					}
					else{
						return new Token(startLine, startColumn, TokenType.PROGRAM, "");
					}
				}
				
			case 11:
				testKey = "val".toCharArray();
				temp = 0;
				while(temp<testKey.length) {
					if(source.current == testKey[temp]){
						lexeme.append(source.current);
						source.advance();
						temp++;
					}
					else{
						state = 9;
						break;
					}	
				}
				if(temp == 3) {
					if(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
						state = 9;
					}
					else{
						return new Token(startLine, startColumn, TokenType.VAL, "");
					}
				}
			
			case 12:
				testKey = "begin".toCharArray();
				temp = 0;
				while(temp<testKey.length) {
					if(source.current == testKey[temp]){
						lexeme.append(source.current);
						source.advance();
						temp++;
					}
					else{
						state = 9;
						break;
					}	
				}
				if(temp == 5) {
					if(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
						state = 9;
					}
					else{
						return new Token(startLine, startColumn, TokenType.BEGIN, "");
					}
				}
				
			case 13:
				testKey = "end".toCharArray();
				temp = 0;
				while(temp<testKey.length) {
					if(source.current == testKey[temp]){
						lexeme.append(source.current);
						source.advance();
						temp++;
					}
					else{
						state = 9;
						break;
					}	
				}
				if(temp == 3) {
					if(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
						state = 9;
					}
					else{
						return new Token(startLine, startColumn, TokenType.END, "");
					}
				}
				
			case 14:
				testKey = "div".toCharArray();
				temp = 0;
				while(temp<testKey.length) {
					if(source.current == testKey[temp]){
						lexeme.append(source.current);
						source.advance();
						temp++;
					}
					else{
						state = 9;
						break;
					}	
				}
				if(temp == 3) {
					if(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
						state = 9;
					}
					else{
						return new Token(startLine, startColumn, TokenType.DIV, "");
					}
				}
			
			case 15:
				testKey = "mod".toCharArray();
				temp = 0;
				while(temp<testKey.length) {
					if(source.current == testKey[temp]){
						lexeme.append(source.current);
						source.advance();
						temp++;
					}
					else{
						state = 9;
						break;
					}	
				}
				if(temp == 3) {
					if(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
						state = 9;
					}
					else{
						return new Token(startLine, startColumn, TokenType.MOD, "");
					}
				}
			
			case 16:
				testKey = "int".toCharArray();
				temp = 0;
				while(temp<testKey.length) {
					if(source.current == testKey[temp]){
						lexeme.append(source.current);
						source.advance();
						temp++;
					}
					else{
						state = 9;
						break;
					}	
				}
				if(temp == 3) {
					if(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
						state = 9;
					}
					else{
						return new Token(startLine, startColumn, TokenType.PRINT, "");
					}
				}
				
			case 17:
				testKey = "const".toCharArray();
				temp = 0;
				while(temp<testKey.length) {
					if(source.current == testKey[temp]){
						lexeme.append(source.current);
						source.advance();
						temp++;
					}
					else{
						state = 9;
						break;
					}	
				}
				if(temp == 5) {
					if(!Character.isWhitespace(source.current) && (Character.isDigit(source.current) || Character.isLetter(source.current))) {
						state = 9;
					}
					else{
						return new Token(startLine, startColumn, TokenType.CONST, "");
					}
				}
			}
		}
	}
	/**
	 * Close the underlying Reader.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		source.close();
	}

	private Source source;
}
