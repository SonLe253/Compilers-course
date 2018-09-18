package csc426;

/**
 * Enumeration of the different kinds of tokens in the YASL subset.
 * 
 * @author bhoward
 */
public enum TokenType {
	NUM, // numeric literal
	SEMI, // semicolon (;)
	PLUS, // plus operator (+)
	MINUS, // minus operator (-)
	STAR, // times operator (*)
	EOF, // end-of-file
	ID, // identifier
	PROGRAM,
	VAL,
	BEGIN,
	PRINT,
	END,
	DIV,
	MOD,
	PERIOD, // period (.)
	ASSIGN, // assign operator (=)
	CONST
	
	
	// TODO add more token types here
}
