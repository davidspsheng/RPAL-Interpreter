
public class STD {
public TreeNode STDroot;
	
	STD(TreeNode root){
		this.STDroot = root;
	}
	
	public TreeNode start(){
		stdScan(STDroot);
		
		return STDroot;
	}

	private void stdScan(TreeNode root){
		if(root == null)
			return;
		TreeNode child = root.children;
		while(child != null){
			stdScan(child);
			
			child = child.next;
		}
		
		standardize(root);
	}
	
	private TreeNode rightRec(TreeNode t){
		if(t.next == null){
			return t;
		}
		else{
			TreeNode root = new TreeNode("l", null);
			root.children = t;
			t.next = rightRec(t.next);
			return root;
		}
	}
	
	private TreeNode buildTree(String name, TreeNode c1, TreeNode c2){
		TreeNode root = new TreeNode(name, null);
		root.children = c1;
		c1.next = c2;
		c2.next = null;
		return root;
	}
	
	private TreeNode copy(TreeNode t){	// Only <Identifier> can be copied.
		TreeNode newT = new TreeNode(t.name, t.token);
		return newT;
	}
	
	private void standardize(TreeNode root){
		if(root.name.equals("let")){
			checkNode(root);
			TreeNode X = root.children.children;
			TreeNode E = root.children.children.next;
			TreeNode P = root.children.next;
			TreeNode l = buildTree("l", X, P);
			root.children = l;
			l.next = E;
			root.name = "gamma";
		}
		else if(root.name.equals("where")){
			checkNode(root);
			TreeNode X = root.children.next.children;
			TreeNode E = root.children.next.children.next;
			TreeNode P = root.children;
			TreeNode l = buildTree("l", X, P);
			root.children = l;
			l.next = E;
			root.name = "gamma";
		}
		else if(root.name.equals("function_form")){
			checkNode(root);
			TreeNode F = root.children;
			TreeNode V = F.next;
			TreeNode subRoot = new TreeNode("l", null);
			subRoot.children = V;
			V.next = rightRec(V.next);
			root.children = F;
			F.next = subRoot;
			root.name = "=";
		}
		else if(root.name.equals("lambda")){
			checkNode(root);
			TreeNode V = root.children;
			V.next = rightRec(V.next);
			root.name = "l";
		}
		else if(root.name.equals("within")){
			checkNode(root);
			TreeNode X1 = root.children.children;
			TreeNode E1 = X1.next;
			
			TreeNode X2 = root.children.next.children;
			TreeNode E2 = X2.next;
			
			TreeNode l = buildTree("l", X1, E2);
			TreeNode g =  buildTree("gamma", l, E1);
			root.children = X2;
			X2.next = g;
			root.name = "=";
		}
		else if(root.name.equals("@")){
			checkNode(root);
			TreeNode E1 = root.children;
			TreeNode N = E1.next;
			TreeNode E2 = N.next;
			
			TreeNode g = buildTree("gamma", N, E1);
			root.children = g;
			g.next = E2;
			root.name = "gamma";
		}
		else if(root.name.equals("and")){
			checkNode(root);
			TreeNode comma = new TreeNode(",", null);
			TreeNode tau = new TreeNode("tau", null);
			
			TreeNode child = root.children;
			comma.children = child.children;
			TreeNode tC = comma.children;
			
			tau.children = child.children.next;
			TreeNode tT = tau.children;
			
			child = child.next;
			while(child != null){
				tC.next = child.children;
				tC = tC.next;
				
				tT.next = child.children.next;
				tT = tT.next;
				
				child = child.next;
			}
			tC.next = null;
			tT.next = null;
			
			root.children = comma;
			comma.next = tau;
			root.name = "=";
		}
		else if(root.name.equals("rec")){
			checkNode(root);
			TreeNode X = root.children.children;
			TreeNode newX = copy(X);
			TreeNode E = root.children.children.next;
			TreeNode l = buildTree("l", X, E);
			TreeNode g = buildTree("gamma", new TreeNode("Ystar", null), l);
			root.children = newX;
			newX.next = g;
			root.name = "=";
		}
		else{	}
	}
	
	private int getChildrenNum(TreeNode root){
		int n = 0;
		TreeNode t = root.children;
		while(t != null){
			n++;
			t = t.next;
		}
		return n;
	}
	
	private void checkNode(TreeNode root){
		if(root.name.equals("let")){
			int n = getChildrenNum(root);
			if(n != 2){
				System.out.println("let Node has wrong number of nodes! -- " + n);
				System.exit(0);
			}
			else{
				if(!root.children.name.equals("=")){
					System.out.println("Left child of let Node is not '=' ! -- " + root.children.name);
					System.exit(0);
				}
			}
		}
		else if(root.name.equals("where")){
			int n = getChildrenNum(root);
			if(n != 2){
				System.out.println("where Node has wrong number of nodes! -- " + n);
				System.exit(0);
			}
			else{
				if(!root.children.next.name.equals("=")){
					System.out.println("Right child of where Node is not '=' ! -- " + root.children.next.name);
					System.exit(0);
				}
			}
		}
		else if(root.name.equals("function_form")){
			int n = getChildrenNum(root);
			if(n < 3){
				System.out.println("fcn_form Node has wrong number of nodes! -- " + n);
				System.exit(0);
			}
		}
		else if(root.name.equals("lambda")){
			int n = getChildrenNum(root);
			if(n < 2){
				System.out.println("lambda Node has wrong number of nodes! -- " + n);
				System.exit(0);
			}
		}
		else if(root.name.equals("within")){
			int n = getChildrenNum(root);
			if(n != 2){
				System.out.println("within Node has wrong number of nodes! -- " + n);
				System.exit(0);
			}
			if(!root.children.name.equals("=")){
				System.out.println("Left child of within Node is not '=' ! -- " + root.children.name);
				System.exit(0);
			}
			if(!root.children.next.name.equals("=")){
				System.out.println("Left child of within Node is not '=' ! -- " + root.children.next.name);
				System.exit(0);
			}
		}
		else if(root.name.equals("@")){
			int n = getChildrenNum(root);
			if(n != 3){
				System.out.println("@ Node has wrong number of nodes! -- " + n);
				System.exit(0);
			}
		}
		else if(root.name.equals("and")){
			int n = getChildrenNum(root);
			if(n <= 1){
				System.out.println("and Node has wrong number of nodes! -- " + n);
				System.exit(0);
			}
			n = 1;
			TreeNode child = root.children;
			while(child != null){
				if(!child.name.equals("=")){
					System.out.println("Child of and Node is not '='! -- " + n + "th");
					System.exit(0);
				}
				child = child.next;
			}
		}
		else if(root.name.equals("rec")){
			int n = getChildrenNum(root);
			if(n != 1){
				System.out.println("rec Node has wrong number of nodes! -- " + n);
				System.exit(0);
			}
			if(!root.children.name.equals("=")){
				System.out.println("Child of rec Node is not '=' ! -- " + root.children.name);
				System.exit(0);
			}
		}
		else{	}
	}
}
