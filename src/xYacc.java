import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;


public class xYacc {
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
