import java.util.*;
import java.io.*;

public class p1 {
	public ArrayList<Token> tokens;
	public String DOTFileName = "AbstractSyntaxTree.dot";
//	public String STDoutputFile = "E:\\Courses\\3-Spring2014\\Programming Language Principles\\Projects\\Project1\\win-p1\\p1\\output.txt";
	public String STDoutputFile = "output.txt";
	
	p1(){
		tokens = new ArrayList<Token>();
		
	}
	
	public String readFile(String fileName){
		File file = new File(fileName);
        BufferedReader reader = null;
        String s = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	// tempString
            	s += tempString + "\n";
            }
            
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
	}
	
	public String writeNode(Token token, int val){
		if(token.attribute.equals("INTEGER")){
			return token.content + "." + val;
		}
		else if(token.content.equals("->")){
			return "arrow_" + val;
		}
		else if(token.content.equals("-")){
			return "minus_" + val;
		}
		else if(token.content.equals("+")){
			return "plus_" + val;
		}
		else if(token.content.equals("*")){
			return "mul_" + val;
		}
		else if(token.content.equals("=")){
			return "equal_" + val;
		}
		else if(token.content.equals("@")){
			return "AT_" + val;
 		}
		else if(token.content.equals(",")){
			return "COMMA_" + val;
 		}
		else if(token.content.equals("&")){
			return "AND_" + val;
 		}
		else if(token.content.equals(" ")){
			return "EmptyString_" + val;
		}
		else if(token.content.equals("\\n")){
			return "slpashN_" + val;
		}
		else if(token.attribute.equals("STRING")){
			if(token.content.trim().split(" ").length != 1){
				String ss[] = token.content.trim().split(" ");
				int len = token.content.split(" ").length;
				String s = "";
				for(int i = 0; i < len; i++){
					s += ss[i] + "_";
				}
				return s + val;
			}
			else{
				return token.attribute + token.content + "_" + val;
			}
		}
		else{
			return token.content + "_" + val;
		}
	}
	
	public int scanTree(TreeNode root, int val){
		if(root == null){
			return 0;
		}
		String s = "";
		TreeNode child = root.children;
		int t = 1;
		while(child != null){
			s = writeNode(new Token("OPERATOR", root.name), val) + " -> ";
			if(child.token != null){	// not intermediate node
				s += writeNode(child.token, t + val) + ";\r\n";
			}
			else{
				s += writeNode(new Token("OPERATOR", child.name), t + val) + ";\r\n";
			}
			writeFile(s, DOTFileName);
			t += scanTree(child, t + val);
			 
			child = child.next;
		}
		return t;
	 }
	
	public void writeFile(String s, String fileName){
		 try {
	         FileWriter writer = new FileWriter(fileName, true);   
	         writer.write(s);   
	         writer.close();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        }
	 }
	
	public void writeDOTFile(xYacc yacc) throws IOException{
		 File file = new File(DOTFileName);
		 if(file.exists()){
			 file.delete();
		 }
		 
		 	// head
		 String s = "digraph G {\r\n";
		 writeFile(s, DOTFileName);
		 
		 scanTree(yacc.getRoot(), 0);
		 
		 	//	end
		 s = "}";
		 writeFile(s, DOTFileName);
	 }
	
	public void STDscan(TreeNode root, String dots){
		if(root == null)
			return;
		String s = "";
		if(root.name.equals("false") || root.name.equals("true") || root.name.equals("nil") || root.name.equals("dummy")){
			s = dots + "<" + root.name + ">";
		}
		else if(root.token == null){
			s = dots + root.name;
		}
		else if(root.name.equals("IDENTIFIER")){
			s = dots + "<ID:" + root.token.content + ">";
		}
		else if(root.name.equals("INTEGER")){
			s = dots + "<INT:" + root.token.content + ">";
		}
		else if(root.name.equals("STRING")){
			s = dots + "<STR:'" + root.token.content + "'>";
		}
		else{	}
		
		s += " \r\n";
		writeFile(s, STDoutputFile);
		TreeNode child = root.children;
		while(child != null){
			STDscan(child, dots + ".");
			
			child = child.next;
		}
	}
	
	public void writeOutputFile(xYacc yacc){
		File file = new File(STDoutputFile);
		if(file.exists()){
			file.delete();
		}
		TreeNode root = yacc.getRoot();
		STDscan(root, "");
	}
	
	public void CallProcess() throws Exception{
		 Runtime runtime = Runtime.getRuntime();//E:\Programming Tools\Java\Programming Language Principles\p1   E:\Programming Tools\Java\Programming Language Principles\p1
		 runtime.exec("\"E:\\Programming Tools\\graphviz-2.34\\release\\bin\\dot.exe\" -Tpng \"E:\\Programming Tools\\Java\\Programming Language Principles\\p1\\AbstractSyntaxTree.dot\"  -o o.png");
//		 System.out.println("\"E:\\Programming Tools\\graphviz-2.34\\release\\bin\\dot.exe\" -Tpng \"E:\\Programming Tools\\Java\\Programming Language Principles\\p1\\AbstractSyntaxTree.dot\"  -o o.png");
	}
	
	public void tokenPrint(ArrayList<Token> a){
		for(int i=0;i<a.size();i++){
			System.out.println("<" + a.get(i).attribute + "> " + "<" + a.get(i).content + ">");
		}
	}
	
	public TreeNode start(String fileName) throws Exception {
//		p1 p1 = new p1();
//		fileName = "C:\\Users\\shengspxuan\\Downloads\\win-p1\\p1\\tests\\wsum2";
//		fileName = "Program.txt";
		
		Input input = new Input(fileName);
		xLex lex = new xLex(input);
		xYacc yacc = new xYacc(lex);
		yacc.parse();
		input.closeFile();	//	close BufferReader.
		System.out.println("Parsing is Done!");
		
	//	p1.writeOutputFile(yacc);
		
//		p1.writeDOTFile(yacc);	
//		p1.CallProcess();
		
		return yacc.getRoot();

	}

}


final class Input{
	BufferedReader reader;
	String buffer;
	char preChar;
	int lineNum;
	
	private int index;
	private String content;
	
	Input(String fileName) throws FileNotFoundException{
		reader = null;
		buffer = "";
		preChar = '\0';
		lineNum = 0;
		
		openFile(fileName);
	}
	
	private void openFile(String fileName) throws FileNotFoundException{
		File file = new File(fileName);
		reader = new BufferedReader(new FileReader(file));
	}
	
	public void closeFile() throws IOException{
		reader.close();
	}
	
	private int readBuffer(BufferedReader reader) throws IOException{
        String tempString = null;
        if ((tempString = reader.readLine()) != null) {
        	buffer = tempString + '\n';
        	lineNum++;
        	return 1;
        }
        else{
        	return -1;
        }
	}
	
	public char nextChar() throws IOException{
		if(buffer.equals("")){
			if(readBuffer(reader) != -1){
				preChar = buffer.charAt(0);
				buffer = buffer.substring(1, buffer.length());
			}
			else{
				preChar = '\0';	// end of file
			}
		}
		else{
			preChar = buffer.charAt(0);
			buffer = buffer.substring(1, buffer.length());
		}
		return preChar;
	}
	
	public void retract(){
		if(preChar != '\0')
			buffer = String.valueOf(preChar) + buffer;
	}
	
	public boolean isFileEnd(){
		if(content.charAt(index + 1) == '`'){
			return true;
		}
		else
			return false;
	}

}

final class Token{
	public String attribute;
	public String content;
	public int lineNum;
	
	Token(){
		this.attribute = null;
		this.content = null;
		this.lineNum = 0;
	}
	
	Token(String att, String con){
		this.attribute = att;
		this.content = con;
	}
	
	Token(String att, String con, int lineNum){
		this.attribute = att;
		this.content = con;
		this.lineNum = lineNum;
	}
}

final class xLex{
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

final class xYacc {
	private xLex lex;
	private Stack<TreeNode> stack;
	private Set<String> keywords;
	private Token NT;
	
	xYacc(xLex lex) throws IOException{
		this.lex = lex;
		stack = new Stack<TreeNode>();
		initialKeyword();
		NT = nextToken();	//	initialize NT(next_token) for procedure E()
	}
	
	public TreeNode getRoot(){
		return stack.peek();
	}
	
	private void initialKeyword(){
		keywords = new HashSet<String>();
		keywords.add("let");
		keywords.add("in");
		keywords.add("fn");
		keywords.add("where");
		keywords.add("aug");
		keywords.add("or");
		keywords.add("gr");
		keywords.add("ge");
		keywords.add("ls");
		keywords.add("le");
		keywords.add("eq");
		keywords.add("ne");
		keywords.add("true");
		keywords.add("false");
		keywords.add("nil");
		keywords.add("dummy");
		keywords.add("within");
		keywords.add("and");
		keywords.add("rec");
		keywords.add("not");
		keywords.add("&");
	}
	
	public Token nextToken() throws IOException{
		while(true){
			Token token = lex.getNextToken();
			if(!token.attribute.equals("DELETE"))
				return token;
		}
	}
	
	private void buildTree(String name, int n){
		TreeNode root = new TreeNode(name, null);
		for(int i = 0; i < n; i++){
		//	if(!stack.isEmpty()){
				TreeNode child = stack.pop();
				if(root.children == null){
					root.children = child;
				}
				else{
					child.next = root.children;
					root.children = child;
				}
//			}
//			else{
//				error(40, "Unexpected Token " + NT.content + ".", NT.lineNum );
//			}
		}
		stack.push(root);
	}
	
	private void buildTree(Token token, int n){
		if(n != 0){
			System.out.println("BuildTree Error!");
			System.exit(0);
		}
		TreeNode t = new TreeNode(token.attribute, token);
		stack.push(t);
	}
	
	private void read(Token token) throws IOException{
		if(!token.content.equals(NT.content) && !token.attribute.equals(NT.attribute)){
			error(50, "Expected " + token.content + ", but found: " + NT.content + ".", NT.lineNum );
			System.exit(0);
		}
		if(token.attribute.equals("IDENTIFIER")){
			if(!keywords.contains(token.content))
				buildTree(token, 0);
		}
		else if(token.attribute.equals("INTEGER") || token.attribute.equals("STRING")){
			buildTree(token, 0);
		}
		else{	}
		NT = nextToken();
			//	deal with Token(END) HERE????????
	}
	
	public void parse() throws IOException{
		E();
		
		if(!NT.attribute.equals("END")){
			error(30, "Unexpected ending!", NT.lineNum);
		}
	}
	
	private void E() throws IOException{	// E-> 'let' D 'in' E <let> | -> 'fn' Vb+ '.' E	<lambda>
		switch(NT.content){
		case "let":
			read(new Token("IDENTIFIER", "let"));	//	read 'let'
			D();
			read(new Token("IDENTIFIER", "in"));
			E();
			buildTree("let", 2);
			break;
		case "fn":
			read(new Token("IDENTIFIER", "fn"));
			Vb();
			int n = 1;
			while(true){
				if(NT.attribute.equals("IDENTIFIER") || NT.content.equals("(")){
					Vb();
					n++;
				}
				else{
					break;
				}
			}
			read(new Token("OPERATOR", "."));
			E();
			buildTree("lambda", n + 1);
			break;
		default:
			Ew();
			break;
		}
	}
	
	private void Ew() throws IOException{	//	Ew -> T 'where' Dr | -> T
		T();
		if(NT.content.equals("where")){
			read(new Token("IDENTIFIER", "where"));
			Dr();
			buildTree("where", 2);
		}
	}
	
	private void T() throws IOException{	//	T -> Ta ("," Ta)+ | -> Ta
		Ta();
		int n = 1;
		while(true){
			if(NT.content.equals(",")){
				read(new Token("OPERATOR", ","));
				Ta();
				n++;
			}
			else{
				break;
			}
		}
		if(n != 1){
			buildTree("tau", n);
		}
	}
	
	private void Ta() throws IOException{	//	Ta -> Ta "aug" Tc | Tc
		Tc();
		while(true){
			if(NT.content.equals("aug")){
				read(NT);
				Tc();
				buildTree("aug", 2);
			}
			else{
				break;
			}
		}
	}
	
	private void Tc() throws IOException{	//	Tc -> B "->" Tc "|" Tc | -> B
		B();
		if(NT.content.equals("->")){
			read(NT);
			Tc();
			read(new Token("OPERATOR", "|"));
			Tc();
			buildTree("->", 3);
		}
	}
	
	private void B() throws IOException{	//	B -> B "or" Bt | -> Bt
		Bt();
		while(true){
			if(NT.content.equals("or")){
				read(NT);
				Bt();
				buildTree("or", 2);
			}
			else{
				break;
			}
		}
	}
	
	private void Bt() throws IOException{	//	Bt -> Bt "&" Bs | -> Bs
		Bs();
		while(true){
			if(NT.content.equals("&")){
				read(NT);
				Bs();
				buildTree("&", 2);
			}
			else{
				break;
			}
		}
	}
	
	private void Bs() throws IOException{	//	Bs -> "not" Bp | Bp
		if(NT.content.equals("not")){
			read(NT);
			Bp();
			buildTree("not", 1);
		}
		else{
			Bp();
		}
	}
	
	private void Bp() throws IOException{	//	Bp -> A ("gr" | ">") A || -> A ("ge" | ">=") A || -> A ("ls" | "<")  A
						//	   -> A ("le" | "<=") A || -> A "eq" A || -> A "ne" A || -> A
		A();
		if(NT.content.equals("gr") || NT.content.equals(">")){
			read(NT);
			A();
			buildTree("gr", 2);
		}
		else if(NT.content.equals("ge") || NT.content.equals(">=")){
			read(NT);
			A();
			buildTree("ge", 2);
		}
		else if(NT.content.equals("ls") || NT.content.equals("<")){
			read(NT);
			A();
			buildTree("ls", 2);
		}
		else if(NT.content.equals("le") || NT.content.equals("<=")){
			read(NT);
			A();
			buildTree("le", 2);
		}
		else if(NT.content.equals("eq")){
			read(NT);
			A();
			buildTree("eq", 2);
		}
		else if(NT.content.equals("ne")){
			read(NT);
			A();
			buildTree("ne", 2);
		}
		else{	}		
	}
	
	private void A() throws IOException{	//	A -> A "+" At | -> A "-" At | ->   "+" At | ->   "-" At | At
		if(NT.content.equals("+") && NT.attribute.equals("OPERATOR")){	// A ->  "+" At
			read(NT);
			At();
		}
		else if(NT.content.equals("-") && NT.attribute.equals("OPERATOR")){	//	A ->   "-" At	"neg"
			read(NT);
			At();
			buildTree("neg", 1);
		}
		else{
			At();
		}
			//	A -> A "+" At | -> A "-" At
		while(true){
			if(NT.content.equals("+") && NT.attribute.equals("OPERATOR")){
				read(NT);
				At();
				buildTree("+", 2);
			}
			else if(NT.content.equals("-") && NT.attribute.equals("OPERATOR")){
				read(NT);
				At();
				buildTree("-", 2);
			}
			else{
				break;
			}
		}
	}
	
	private void At() throws IOException{	//	At -> At "*" Af | -> At "/" Af | -> Af
		Af();
		while(true){
			if(NT.content.equals("*") && NT.attribute.equals("OPERATOR")){
				read(NT);
				Af();
				buildTree("*", 2);
			}
			else if(NT.content.equals("/") && NT.attribute.equals("OPERATOR")){
				read(NT);
				Af();
				buildTree("/", 2);
			}
			else{
				break;
			}
		}
	}
	
	private void Af() throws IOException{	//	Af -> Ap "**" Af | Ap
		Ap();
		if(NT.content.equals("**") && NT.attribute.equals("OPERATOR")){
			read(NT);
			Af();
			buildTree("**", 2);
		}
	}
	
	private void Ap() throws IOException{	//	Ap -> Ap "@" '<IDENTIFIER>' R
		R();
		while(true){
			if(NT.content.equals("@") && NT.attribute.equals("OPERATOR")){
				read(NT);
				read(new Token("IDENTIFIER", NT.content));
				R();
				
				buildTree("@", 3);
			}
			else{
				break;
			}
		}
	}
	
	private void R() throws IOException{	// R -> R Rn | -> Rn
		Rn();
		while(true){
			if(NT.attribute.equals("IDENTIFIER") && !keywords.contains(NT.content)){
				Rn();
				buildTree("gamma", 2);
			}
			else if(NT.attribute.equals("INTEGER")){
				Rn();
				buildTree("gamma", 2);
			}
			else if(NT.attribute.equals("STRING")){
				Rn();
				buildTree("gamma", 2);
			}
			else if(NT.content.equals("true")){
				Rn();
				buildTree("gamma", 2);
			}
			else if(NT.content.equals("false")){
				Rn();
				buildTree("gamma", 2);
			}
			else if(NT.content.equals("nil")){
				Rn();
				buildTree("gamma", 2);
			}
			else if(NT.content.equals("(")){
				Rn();
				buildTree("gamma", 2);
			}
			else if(NT.content.equals("dummy")){
				Rn();
				buildTree("gamma", 2);
			}
			else{
				break;
			}
		}
	}
	
	private void Rn() throws IOException{	//	Rn -> '<IDENTIFIER>' | -> '<INTERGER>' | -> '<STRING>'
						//	   -> "true" | -> "false" | -> "nil" | -> "(" E ")" | -> "dummy"
		if(NT.content.equals("true") && NT.attribute.equals("IDENTIFIER")){
			read(NT);
			buildTree("true", 0);
		}
		else if(NT.content.equals("false") && NT.attribute.equals("IDENTIFIER")){
			read(NT);
			buildTree("false", 0);
		}
		else if(NT.content.equals("nil") && NT.attribute.equals("IDENTIFIER")){
			read(NT);
			buildTree("nil", 0);
		}
		else if(NT.content.equals("dummy") && NT.attribute.equals("IDENTIFIER")){
			read(NT);
			buildTree("dummy", 0);
		}
		else if(NT.attribute.equals("IDENTIFIER")){
			read(NT);
		}
		else if(NT.attribute.equals("INTEGER")){
			read(NT);
		}
		else if(NT.attribute.equals("STRING")){
			read(NT);
		}
		else if(NT.content.equals("(")){
			read(NT);
			E();
			read(new Token(")", ")"));
		}
		else{
			error(15, "Unkown matching pattern " + NT.content, NT.lineNum);
		}
	}
	
	private void D() throws IOException{	//	D -> Da 'within' D <within> | -> Da
		Da();
		if(NT.content.equals("within")){
			read(NT);
			D();
			buildTree("within", 2);
		}
	}
	
	private void Da() throws IOException{	//	Da -> Dr ( "and" Dr )+ | -> Dr
		Dr();
		int n = 1;
		while(true){
			if(NT.content.equals("and")){
				read(NT);
				Dr();
				n++;
			}
			else{
				break;
			}
		}
		
		if(n != 1){
			buildTree("and", n);
		}
	}
	
	private void Dr() throws IOException{	//	Dr -> "rec" Db | Db
		if(NT.content.equals("rec")){
			read(NT);
			Db();
			buildTree("rec", 1);
		}
		else{
			Db();
		}
	}
	
	private void Db() throws IOException{	//	Db -> Vl "=" E | -> '<IDENTIFIER>' Vb+ "=" E | -> "(" D ")"
		if(NT.attribute.equals("IDENTIFIER") && !keywords.contains(NT.content)){	// Db -> '<IDENTIFIER>' Vb+ "=" E OR(||) Db -> Vl '=' E
			Token preToken = NT;
			read(NT);
			
			if(NT.content.equals("=") || NT.content.equals(",")){	// Db -> Vl '=' E
				Vl(preToken);
				read(new Token("OPERATOR", "="));
				E();
				buildTree("=", 2);
			}
			else{	// Db -> '<IDENTIFIER>' Vb+ "=" E
				Vb();
				int n = 2;
				while(true){
					if((NT.attribute.equals("IDENTIFIER") && !keywords.contains(NT.content)) || NT.content.equals("(")){
						Vb();
						n++;
					}
					else{
						break;
					}
				}
				read(new Token("OPERATOR", "="));
				E();
				buildTree("function_form", n + 1);
			}
		}
		else if(NT.content.equals("(")){	// Db -> "(" D ")"
			read(NT);
			D();
			read(new Token(")", ")"));
		}
		else{		}
	}
	
	private void Vb() throws IOException{	//	Vb -> '<IDENTIFIER>' | -> "(" Vl ")" | -> "(" ")"
		if(NT.attribute.equals("IDENTIFIER")){
			read(NT);
		}
		else if(NT.content.equals("(")){
			read(NT);
			if(NT.content.equals(")")){
				read(NT);
				buildTree("()", 0);
			}
			else{
				Vl(null);
				read(new Token(")", ")"));
			}
		}
		else{
			error(20, "Unkown matching pattern " + NT.content, NT.lineNum);
		}
	}
	
	private void Vl(Token preToken) throws IOException{	//	Vl -> '<IDENTIFIER>' list ","
		if(preToken != null && NT.content.equals("=")){	//	Vl has been consumed, so return directly.
			return;
		}
		if((preToken == null && NT.attribute.equals("IDENTIFIER")) || (preToken != null && NT.content.equals(","))){
			if(preToken == null && NT.attribute.equals("IDENTIFIER"))	//	Vb -> '(' Vl ')'
				read(NT);
			int n = 1;
			while(true){
				if(NT.content.equals(",")){
					read(NT);
					if(NT.attribute.equals("IDENTIFIER")){
						read(NT);
					}
					else{
						error(21, "<IDENTIFIER>", NT.lineNum);
					}
					n++;
				}
				else{
					break;
				}
			}
			
			if(n != 1){
				buildTree(",", n);
			}
		}
		else if(preToken.attribute.equals("IDENTIFIER") && NT.content.equals(",")){	// Db -> Vl '=' E
				// preToken has been read!
			int n = 1;
			while(true){
				if(NT.content.equals(",")){
					read(NT);
					if(NT.attribute.equals("IDENTIFIER")){
						read(NT);
					}
					else{
						error(21, "<IDENTIFIER>", NT.lineNum);
					}
					n++;
				}
				else{
					break;
				}
			}
			
			if(n != 1){
				buildTree(",", n);
			}
		}
		else{
			error(21, "<IDENTIFIER>", NT.lineNum);
		}
	}
	
	private void error(int n, String m, int lineNum){
		String s = "Yacc:	";
		if(lineNum != 0)
			s += "Line " + lineNum + ": ";
		switch(n){
		case 0:
			s = "Error in READ() function. Mismatch symbol is '" + m + "'.";
			break;
		case 1:
			s = "Error in E() function. Mismatch symbol is '" + m + "'.";
			break;
		case 3:
			s = "Error in T() function. Mismatch symbol is '" + m + "'.";
			break;
		case 5:
			s = "Error in Tc() function. Mismatch symbol is '" + m + "'.";
			break;
		case 13:
			s = "Error in Ap() function. Mismatch symbol is '" + m + "'.";
			break;
		case 15:
			s = "Error in Rn() function. Mismatch symbol is '" + m + "'.";
			break;
		case 19:
			s = "Error in Db() function. Mismatch symbol is '" + m + "'.";
			break;
		case 20:
			s = "Error in Vb() function. Mismatch symbol is '" + m + "'.";
			break;
		case 21:
			s = "Error in Vl() function. Mismatch symbol is '" + m + "'.";
			break;
		case 30:
			s += m;
			break;
		case 40:
			s += m;
			break;
		case 50:
			s += m;
			break;
		default:
			s += "Unkown Error!-- " + n;
			break;
		}
		System.out.println(s);
		System.exit(0);
	}
	
	
}

final class TreeNode {
	public String name;
	public Token token;
	public TreeNode children;
	public TreeNode next;
	
	TreeNode(String name, Token token){
		this.name = name;
		this.token = token;
		this.children = null;
		this.next = null;
	}
}








