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