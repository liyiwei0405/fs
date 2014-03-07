package com.funshion.fsql.expression;

import java.io.IOException;

/**
 * The <a href="http://en.wikipedia.org/wiki/Lexical_analysis#Scanner">lexer</a>
 * is used to read characters and identify tokens and pass them to the parser
 */
public class Lexer {
	
	public static final int EOF = -1;
	private int columnNo = 1;
	private PeekReader in;
	
	
	public Lexer(String sql) throws IOException {
		this.in = new PeekReader(sql);
	}

	private int lookAhead(int i) {
		return in.peek(i);
	}

	private int read() {
		try {
			int c = in.read();;
			if (c == '\n') {
				throw new LexerException("\\n is not expected", columnNo) ;
			}
			columnNo++;
			return c;
		} catch (IOException e) {
			throw new LexerException(e.getMessage(), 
					columnNo);
		}
	}

	private void close() {
		try {
			in.close();
		} catch (IOException e) {
		}
	}

	private int next() {
		read();
		return lookAhead(1);
	}

	private char match(char c) {
		int input = read();
		if (input != c) {
			String inputChar = (input != EOF) ? "" + (char) input : "END_OF_FILE";
			throw new LexerException(
					"Expected '" + c + "' but got '" + inputChar + "'", 
					columnNo);
		}
		return c;
	}

	private String match(String str) {
		for (int i = 0; i < str.length(); i++) {
			match(str.charAt(i));
		}
		return str;
	}

	private Token createToken(TokenType type, char c) {
		match(c);
		return new Token(columnNo, type, "" + c);
	}

	private Token createToken(TokenType type, String str) {
		match(str);
		return new Token(columnNo, type, str);
	}

	public Token getNextToken() {
		int character = lookAhead(1);
		// Skip whitespace
		while (character == ' ' || character == '\t') {
			character = next();
		}
		switch (character) {
		case EOF: {
			// End of character stream.
			// Return null to indicate end of token stream
			close();
			return null;
		}
		case '(': {
			return createToken(TokenType.LPAREN, '(');
		}
		case ')': {
			return createToken(TokenType.RPAREN, ')');
		}
		case '?': {
			return createToken(TokenType.VALUEUNSET, '?');
		}
		case '=': {
			return createToken(TokenType.EQUAL, "=");
		}
		case '|': {
			return createToken(TokenType.OR, "||");
		}
		case '&': {
			return createToken(TokenType.AND, "&&");
		}
		case '!': {
			if (lookAhead(2) == '=') {
				return createToken(TokenType.NOT_EQUAL, "!=");
			} else {
				return createToken(TokenType.NOT, '!');
			}
		}
		case '<': {
			if (lookAhead(2) == '=') {
				return createToken(TokenType.LESS_EQUAL, "<=");
			} else {
				return createToken(TokenType.LESS_THEN, '<');
			}
		}
		case '>': {
			if (lookAhead(2) == '=') {
				return createToken(TokenType.GREATER_EQUAL, ">=");
			} else {
				return createToken(TokenType.GREATER_THEN, '>');
			}
		}
		case ',': {
			return createToken(TokenType.COMMA, ',');
		}
		case '\'':
		case '"': {
			return matchStringLiteral((char) character);
		}
		default: {
			if (character >= '0' && character <= '9') {
				return matchNumber();
			} else if ((character >= 'A' && character <= 'Z') ||
					(character >= 'a' && character <= 'z') ||
					character == '_') {
				return matchIdentifier();
			} else {
				throw new LexerException("Unexpected '" + ((char) character) + "' character", 
						columnNo);
			}
		}
		}
	}

	private Token matchNumber() {
		StringBuilder sb = new StringBuilder();
		boolean decimal = false;
		int character = lookAhead(1);
		while ((character >= '0' && character <= '9') || character == '.') {
			if (decimal && character == '.') {
				throw new LexerException("Unexcepted '.' character", 
						columnNo);
			} else if (character == '.') {
				decimal = true;
			}
			sb.append((char) character);
			character = next();
		}
		return new Token(columnNo, TokenType.NUMBER, sb.toString());
	}
	
	/**
	 * An identifier is either a keyword, function, or variable
	 *
	 * @return Token
	 */
	private Token matchIdentifier() {
		StringBuilder sb = new StringBuilder();
		int character = lookAhead(1);
		while ((character >= 'a' && character <= 'z') ||
				(character >= 'A' && character <= 'Z') ||
				(character >= '0' && character <= '9') ||
				character == '_') {
			sb.append((char) character);
			character = next();
		}
		
		String word = sb.toString();
		String wordLower = word.toLowerCase();
		Object var;
		if (wordLower.equals("true")) {
			return new Token(columnNo, TokenType.TRUE, word);
		} else if (wordLower.equals("false")) {
			return new Token(columnNo, TokenType.FALSE, word);
		}else if ((var = TokenType.getConditionType(wordLower)) != null) {
			if(var == TokenType.SEARCH){
				return new Token(columnNo,  TokenType.SEARCH, TokenType.SEARCH.str);
			}else if(var == TokenType.LIKE){
				return new Token(columnNo,  TokenType.LIKE, TokenType.LIKE.str);
			}else if(var == TokenType.IN){
				return new Token(columnNo,  TokenType.IN, TokenType.IN.str);
			}else {
				throw new RuntimeException("condMap not defined:" + word);
			}
		}else if ((var = TokenType.getConnjuctType(wordLower)) != null) {
			if(var == TokenType.AND){
				return new Token(columnNo,  TokenType.AND, TokenType.AND.str);
			}else if(var == TokenType.OR){
				return new Token(columnNo,  TokenType.OR, TokenType.OR.str);
			}else if(var == TokenType.NOT){
				return new Token(columnNo,  TokenType.NOT, TokenType.NOT.str);
			}else {
				throw new RuntimeException("conjunctMap not defined:" + word);
			}
		}else if (word.equals("order")) {
            return new Token(columnNo, TokenType.ORDER, word);
        }else if (word.equals("by")) {
            return new Token(columnNo, TokenType.BY, word);
        }else if (word.equals("limit")) {
            return new Token(columnNo, TokenType.LIMIT, word);
        }else if (word.equals("asc")) {
            return new Token(columnNo, TokenType.ASC, word);
        }else if (word.equals("desc")) {
            return new Token(columnNo, TokenType.DESC, word);
        }else{
			return new Token(columnNo, TokenType.VARIABLE, word);
		}
	}

	private Token matchStringLiteral(char quote) {
		match(quote);
		StringBuilder sb = new StringBuilder();
		int character = lookAhead(1);
		while (character != quote && character != EOF) {
			sb.append((char) character);
			character = next();
		}
		match(quote);
		return new Token(columnNo, TokenType.STRING_LITERAL, sb.toString());
	}
}
