
public class Token {
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
