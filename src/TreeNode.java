
public class TreeNode {
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
