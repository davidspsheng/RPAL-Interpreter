import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class xLex {
	Set<Character> operator_symbol;
	private Input input;
	
	xLex(Input input){
		this.input = input;
		initialOperSymSet();
	}
	
	private void initialOperSymSet(){
		this.operator_symbol = new HashSet<Character>();
		operator_symbol.add('+');
		operator_symbol.add('-');
		operator_symbol.add('*');
		operator_symbol.add('<');
		operator_symbol.add('>');
		operator_symbol.add('&');
		operator_symbol.add('.');
		operator_symbol.add('@');
		operator_symbol.add('/');
		operator_symbol.add(':');
		operator_symbol.add('=');
		operator_symbol.add('~');
		operator_symbol.add('|');
		operator_symbol.add('$');
		operator_symbol.add('!');
		operator_symbol.add('#');
		operator_symbol.add('%');
		operator_symbol.add('^');
		operator_symbol.add('_');
		operator_symbol.add('[');
		operator_symbol.add(']');
		operator_symbol.add('{');
		operator_symbol.add('}');
		operator_symbol.add('"');
		operator_symbol.add('`');	//???????????????????????
		operator_symbol.add('?');
	}

	public boolean isLetter(char c){
		if(c >= 'a' && c <= 'z')
			return true;
		else if(c >= 'A' && c <= 'Z')
			return true;
		else 
			return false;
	}

	public boolean isDigit(char c){
		if(c >= '0' && c <= '9')
			return true;
		else
			return false;
	}
	
	public Token getNextToken() throws IOException{
		Token token = new Token();
		char c = input.nextChar();
		int state = 0;
		String content = "";	// token content
		token.lineNum = input.lineNum;
		while(true){	//	try to get next token
			switch(state){
			case 0:	//	start state
				if(isLetter(c)){	// IDENTIFIER
					content += String.valueOf(c);
					state = 1;
				}
				else if(isDigit(c)){	// INTEGER
					content += String.valueOf(c);
					state = 3;
				}
				else if(c == '/'){	//	COMMENT DELETE
					content += String.valueOf(c);
					c = input.nextChar();
					if(c == '/'){
						content += String.valueOf(c);
						state = 11;
					}
					else{
						input.retract();
						state = 5;
					}
				}
				else if(operator_symbol.contains(c)){	//OPERATOR
					content += String.valueOf(c);
					state = 5;
				}
				else if(c == "'".charAt(0)){	//	STRING
					state = 7;
				}
				else if(c == ' ' || c == '\t' || c == '\n'){	// EMPTYSPACE DELETE
					content += String.valueOf(c);
					state = 9;
				}
				else if(c == '(' || c == ')' || c == ';' || c == ','){
					token.attribute = String.valueOf(c);
					token.content = String.valueOf(c);
					return token;
				}
				else if(c == '\0'){
					token.attribute = "END";
					token.content = "";
					return token;
				}
				else{
					error(0);
				}
				break;
			case 1:	// step 1 of IDENTIFIER
				c = input.nextChar();
				if(isLetter(c) || isDigit(c) || c == '_'){	// Letter|Digit|'_'
					content += String.valueOf(c);
				}
				else{	//	other
					state = 2;
				}
				break;
			case 2:	//	final point of IDENTIFIER
				input.retract();
				token.attribute = "IDENTIFIER";
				token.content = content;
				return token;
			case 3:	// step 1 of INTEGER
				c = input.nextChar();
				if(isDigit(c)){
					content += String.valueOf(c);
				}
				else{
					state = 4;
				}
				break;
			case 4:	//	final point of INTEGER
				input.retract();
				token.attribute = "INTEGER";
				token.content = content;
				return token;
			case 5:	// step 1 of OPERATOR
				c = input.nextChar();
				if(operator_symbol.contains(c))
					content += String.valueOf(c);
				else
					state = 6;
				break;
			case 6:	//	final point of OPERATOR
				input.retract();
				token.attribute = "OPERATOR";
				token.content = content;
				return token;
			case 7:	// step 1 of STRING
				c = input.nextChar();
				if(c == '\t' || c == '\n' || c == '\\' /*|| c == "\'".charAt(0)*/)	//??????????????????
					content += String.valueOf(c);
				else if(c == '(' || c == ')' || c == ';' || c == ',' || c == ' '){
					content += String.valueOf(c);
				}
				else if(isLetter(c) || isDigit(c) || this.operator_symbol.contains(c)){
					content += String.valueOf(c);
				}
				else if(c == "'".charAt(0)){
					state = 8;
				}
				else{	//	other
					error(7);
				}
				break;
			case 8:	//	final point of STRING
				token.attribute = "STRING";
				token.content = content;
				return token;
			case 9:	// step 1 of EMPTYSPACE DELETE
				c = input.nextChar();
				if(c == ' ' || c == '\t' || c == '\n'){
					content += String.valueOf(c);
				}
				else{	// other
					state = 10;
				}
				break;
			case 10:	//	final point of EMPTYSPACE DELETE
				input.retract();
				token.attribute = "DELETE";
				token.content = content;
				return token;
			case 11:	// step 1 of COMMENT DELETE
				c = input.nextChar();
				if(c == '\n' || c == '\0')
					state = 12;
				else
					content += String.valueOf(c);
				break;
			case 12:	//	final point of COMMENT DELETE
				token.attribute = "DELETE";
				token.content = content;
				return token;
			}
		}
	}
	
	public void error(int n){
		String s = "Lex: Line:" + input.lineNum + " ";
		switch(n){
		case 0:
			s += "Unindentified character...";
			break;
		case 7:
			s += "Illegal String!";
			break;
		default:
			s += "Unknow character!";
			break;
		}
		System.out.println(s);
		System.exit(0);
	}
}
