
public class p2 {

	
	public static void main(String[] args) throws Exception {
//		String fileName = "C:\\Users\\shengspxuan\\Downloads\\win-p1\\p1\\tests\\wsum2";
		String fileName = "Program.txt";
		
		p1 p1 = new p1();
		TreeNode root = p1.start(fileName);
		
			//	standardize
		STD std = new STD(root);
		std.start();
		System.out.println("Standardizing is Done!");
		
			// write Standardizing file
		p1.STDscan(std.STDroot, "");
		
			//	CSE
		CSE cse = new CSE();
		cse.start(std.STDroot);
		
			// print linearizing result
//		Output output = new Output();
//		output.OutputlExpression(cse.lExpressionSet);
		
	}

}