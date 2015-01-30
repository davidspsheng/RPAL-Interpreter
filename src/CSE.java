import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;


public class CSE {
	//Object
		public ArrayList<ArrayList<Unit>> lExpressionSet;
		
		private Stack<Unit> ControlStructure;
		private Stack<Unit> stack;
		private Stack<Integer> curEnvIndex;
		private int maxEnvIndex;
		
		private HashMap<Integer, HashMap<String, Unit>> EnvSet;
		
		private Unit res;
		
		Set<String> FunctionKW;
		
		CSE(){
			lExpressionSet = new ArrayList<ArrayList<Unit>>();
			
			ControlStructure = new Stack<Unit>();
			stack = new Stack<Unit>();
			curEnvIndex = new Stack<Integer>();
			maxEnvIndex = 0;
			
			EnvSet = new HashMap<Integer, HashMap<String, Unit>>();
			
			initialFunSet();
		}
		
		private void initialFunSet(){
			FunctionKW = new HashSet<String>();
			FunctionKW.add("Print");
			FunctionKW.add("Stem");
			FunctionKW.add("Stern");
			FunctionKW.add("ItoS");
			FunctionKW.add("Isstring");
			FunctionKW.add("Isinteger");
			FunctionKW.add("Istuple");
			FunctionKW.add("Istruthvalue");
			FunctionKW.add("Isfunction");
			FunctionKW.add("Isdummy");
			FunctionKW.add("Order");
			FunctionKW.add("Conc");
		}
		
		public void start(TreeNode root){
				// linearize
			ArrayList<Unit> lExpression = new ArrayList<Unit>();
			linearize(root, 0, lExpression, true);
			
				//CSE Interpret
			CSEInterpret();
			
		}
		
		private void addlExpressionSet(int index, ArrayList<Unit> lExpression){
			if(index >= lExpressionSet.size()){
				int j = lExpressionSet.size();
				for(int i=0; i<=(index-j); i++){
					lExpressionSet.add(null);
				}
			}
			lExpressionSet.set(index, lExpression);
		}
		
		private int addUnit(TreeNode t, ArrayList<Unit> lExpression, int index){ // lambda -- 1; -> -- 2; tau -- 0; Otherwise -- 0
			//KW:		gamma; Operator(Unary, Binary); Ystar; beta
			//Lambda:	lambda
			//Token:	Digit; String; Identifier(Function<Print, stern, stem, ItoS, Isstring, Isinteger, Istuple, Order tuple>)
			//Branch:	lExp_then, lExp_else
			//tau:		tuple
			if(t.name.equals("l") && t.token == null){
				TreeNode var = t.children;
				ArrayList<String> v = new ArrayList<String>();
				if(var.name.equals(",")){	//	Children of "," only can be <IDENTIFIER>
					TreeNode child = var.children;
					while(child != null){
						v.add(child.token.content);
						child = child.next;
					}
				}
				else if(var.name.equals("IDENTIFIER")){
					v.add(var.token.content);
				}
				else{	}
				
				Lambda lambda = new Lambda(v, index + 1, -1);
				lExpression.add(new Unit("Lambda",lambda));
				
				return 1;
			}
			else if(t.name.equals("->") && t.token == null){
					// do nothing
				return 2;
			}
			else if(t.name.equals("tau") && t.token == null){
				int n = 0;
				TreeNode child = t.children;
				while(child != null){
					n++;
					child = child.next;
				}
				
				lExpression.add(new Unit("tau", n));
				
				return 0;
			}
			else{
				if(t.token == null){
					lExpression.add(new Unit("KW", t.name));
				}
				else{
					lExpression.add(new Unit("Token", t.token));
				}
				
				return 0;
			}
		}
		
		private int linearize(TreeNode root, int ind, ArrayList<Unit> lExpression, boolean toWrite){	
	// toWrite == true: update lExpressionSet at the end
			int index = ind;
			
			switch(addUnit(root, lExpression, index)){
			case 0:{	//Otherwise: Normal: two children; Unary: single
				TreeNode child = root.children;
				while(child != null){
					index = linearize(child, index, lExpression, false);
					
					child = child.next;
				}
				break;
			}
			case 1:{	//lambda:	Need to check the variable is single or multiple--(x, y)
				TreeNode child = root.children.next;	//	right child
				ArrayList<Unit> newlExpression = new ArrayList<Unit>();
					// Open an new lExpression
				index = linearize(child, index + 1, newlExpression, true);
				break;
			}
			case 2:{	//->
				TreeNode _cond = root.children;	// Cond: B
				TreeNode _then = _cond.next;
				TreeNode _else = _then.next;
				
					//	then
				lExpression.add(new Unit("Branch", index + 1));
				ArrayList<Unit> thenlExpression = new ArrayList<Unit>();
				index = linearize(_then, index + 1, thenlExpression, true);
				
					//	else
				lExpression.add(new Unit("Branch", index + 1));
				ArrayList<Unit> elselExpression = new ArrayList<Unit>();
				index = linearize(_else, index + 1, elselExpression, true);
				
					// beta
				lExpression.add(new Unit("KW", "beta"));
				
					//	Cond
				index = linearize(_cond, index, lExpression, false);
				
				break;
			}
			default:
				break;
			}
			
			if(toWrite == true){
				addlExpressionSet(ind, lExpression);
			}
			return index;
		}
		
		private void CSEInterpret(){
			EnvSet.put(0, new HashMap<String, Unit>());
			loadEnvironment(getMaxEnvIndex(), 0);
			interpret();
		}
		
		private int getMaxEnvIndex(){
			return maxEnvIndex++;
		}
		
		private void loadEnvironment(int envIndex, int ind){
			this.ControlStructure.push(new Unit("Environment", envIndex));
			this.stack.push(new Unit("Environment", envIndex));
			for(int i = 0; i < lExpressionSet.get(ind).size(); i++){
				this.ControlStructure.push(lExpressionSet.get(ind).get(i));
			}
			curEnvIndex.push(envIndex);
		}
		
		private int quitEnvironment(){
			Unit u = ControlStructure.pop();
			if(u.type.equals("Environment")){
				Unit val = stack.pop();
				Unit e = stack.pop();
				if(!e.type.equals("Environment")){
					error(1, "Unmatching environment flag in Stack!");
				}
				else{
					stack.push(val);
				}
			}
			else{
				error(1, "Unmatching environment flag in ControlStructure!");
			}
			curEnvIndex.pop();	// change current environment index
			
			return (Integer) u.value;
		}
		
		private int getCurEnvIndex(){
			return curEnvIndex.peek();
		}
		
		private void topLambda(){
			Unit u = ControlStructure.pop();
			int index = getCurEnvIndex();
			((Lambda) (u.value)).setCEI(index);
			stack.push(u);
		}
		
		private boolean inBinary(String s){
			if(s.equals("or") || s.equals("&") || s.equals("gr") || s.equals("ge") || s.equals("ls")){
				return true;
			}
			else if(s.equals("le") || s.equals("eq") || s.equals("ne")){
				return true;
			}
			else if(s.equals("+") || s.equals("-") || s.equals("*") || s.equals("-") || s.equals("**")){
				return true;
			}
			else if(s.equals("aug")){
				return true;
			}
			else{
				return false;
			}
		}
		
		private boolean inUnary(String s){
			if(s.equals("not") || s.equals("neg")){
				return true;
			}
			else{
				return false;
			}
		}
		
		private String printTuple(Tuple t){
			String s = "(";
			for(int i = 0; i < t.element.size(); i++){
				if(t.element.get(i).type.equals("Integer")){
					s += (Integer) t.element.get(i).value;
				}
				else if(t.element.get(i).type.equals("String") || t.element.get(i).type.equals("Boolean")){
					s += (String) t.element.get(i).value;
				}
				else if(t.element.get(i).type.equals("Tuple")){
					s += printTuple((Tuple) t.element.get(i).value);
				}
				s += ",";
			}
			s = s.substring(0, s.length() - 1);
			s += ")";
			return s;
		}
		
		private void opearteBinary(String op){
			Unit op1 = stack.pop();
			Unit op2 = stack.pop();
			
			switch(op){
			case "or":
				if(op1.type.equals("Boolean") && op2.type.equals("Boolean")){
					if(((String) (op1.value)).equals("false") && ((String) (op2.value)).equals("false")){
						stack.push(new Unit("Boolean", "false"));
					}
					else{
						stack.push(new Unit("Boolean", "true"));
					}
				}
				else{
					error(30, "Or two operands expected to be Boolean, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "&":
				if(op1.type.equals("Boolean") && op2.type.equals("Boolean")){
					if(((String) (op1.value)).equals("true") && ((String) (op2.value)).equals("true")){
						stack.push(new Unit("Boolean", "true"));
					}
					else{
						stack.push(new Unit("Boolean", "false"));
					}
				}
				else{
					error(30, "& two operands expected to be Boolean, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "gr":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					if((Integer)(op1.value) > (Integer)(op2.value)){
						stack.push(new Unit("Boolean", "true"));
					}
					else{
						stack.push(new Unit("Boolean", "false"));
					}
				}
				else{
					error(30, "gr two operands expected to be Integer, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "ge":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					if((Integer)(op1.value) >= (Integer)(op2.value)){
						stack.push(new Unit("Boolean", "true"));
					}
					else{
						stack.push(new Unit("Boolean", "false"));
					}
				}
				else{
					error(30, "ge two operands expected to be Integer, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "ls":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					if((Integer)(op1.value) < (Integer)(op2.value)){
						stack.push(new Unit("Boolean", "true"));
					}
					else{
						stack.push(new Unit("Boolean", "false"));
					}
				}
				else{
					error(30, "ls two operands expected to be Integer, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "le":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					if((Integer)(op1.value) <= (Integer)(op2.value)){
						stack.push(new Unit("Boolean", "true"));
					}
					else{
						stack.push(new Unit("Boolean", "false"));
					}
				}
				else{
					error(30, "le two operands expected to be Integer, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "eq":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					if((Integer)(op1.value) == (Integer)(op2.value)){
						stack.push(new Unit("Boolean", "true"));
					}
					else{
						stack.push(new Unit("Boolean", "false"));
					}
				}
				else if(op1.type.equals("String") && op2.type.equals("String")){
					if(((String)(op1.value)).equals((String)(op2.value))){
						stack.push(new Unit("Boolean", "true"));
					}
					else{
						stack.push(new Unit("Boolean", "false"));
					}
				}
				else if(op1.type.equals("Boolean") && op2.type.equals("Boolean")){
					if(((String)(op1.value)).equals((String)(op2.value))){
						stack.push(new Unit("Boolean", "true"));
					}
					else{
						stack.push(new Unit("Boolean", "false"));
					}
				}
				else if(op1.type.equals("Tuple") && op2.type.equals("Tuple")){
					///////////////// COMPARE Tuple
				}
				else{
					error(30, "eq two operands expected to be the same type, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "ne":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					if((Integer)(op1.value) == (Integer)(op2.value)){
						stack.push(new Unit("Boolean", "false"));
					}
					else{
						stack.push(new Unit("Boolean", "true"));
					}
				}
				else if(op1.type.equals("String") && op2.type.equals("String")){
					if(!((String)(op1.value)).equals((String)(op2.value))){
						stack.push(new Unit("Boolean", "true"));
					}
					else{
						stack.push(new Unit("Boolean", "false"));
					}
				}
				else if(op1.type.equals("Boolean") && op2.type.equals("Boolean")){
					if(!((String)(op1.value)).equals((String)(op2.value))){
						stack.push(new Unit("Boolean", "true"));
					}
					else{
						stack.push(new Unit("Boolean", "false"));
					}
				}
				else if(op1.type.equals("Tuple") && op2.type.equals("Tuple")){
					///////////////// COMPARE Tuple
				}
				else{
					error(30, "ne two operands expected to be the same type, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "+":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					int res = (Integer)(op1.value) + (Integer)(op2.value);
					stack.push(new Unit("Integer", res));
				}
				else{
					error(30, "+ two operands expected to be Integer, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "-":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					int res = (Integer)(op1.value) - (Integer)(op2.value);
					stack.push(new Unit("Integer", res));
				}
				else{
					error(30, "- two operands expected to be Integer, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "*":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					int res = (Integer)(op1.value) * (Integer)(op2.value);
					stack.push(new Unit("Integer", res));
				}
				else{
					error(30, "* two operands expected to be Integer, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "/":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					if((Integer)(op2.value) != 0){
						int res = (Integer)(op1.value) / (Integer)(op2.value);
						stack.push(new Unit("Integer", res));
					}
					else{
						error(30, "/ divide by 0!");
					}
				}
				else{
					error(30, "+ two operands expected to be Integer, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "**":
				if(op1.type.equals("Integer") && op2.type.equals("Integer")){
					int res = (int) Math.pow((Integer)(op1.value), (Integer)(op2.value));
					stack.push(new Unit("Integer", res));
				}
				else{
					error(30, "- two operands expected to be Integer, but find " + op1.type + ", " + op2.type);
				}
				break;
			case "aug":
				if(op1.type.equals("nil")){	// op2 can be everything
					ArrayList<Unit> a = new ArrayList<Unit>();
					a.add(op2);
					Tuple tuple = new Tuple(a);
					stack.push(new Unit("Tuple", tuple));
				}
				else if(op1.type.equals("Tuple")){
					((Tuple) op1.value).element.add(op2);
					stack.push(new Unit("Tuple", (Tuple) op1.value));
				}
				else{
					error(30, "aug the first operand expected to be nil/Tuple, but find " + op1.type);
				}
				break;
			default:
				break;
			}
		}
		
		private void opearteUnary(String op){
			Unit op1 = stack.pop();
			
			switch(op){
			case "not":
				if(op1.type.equals("Boolean")){
					if(((String) (op1.value)).equals("true")){
						stack.push(new Unit("Boolean", "false"));
					}
					else{
						stack.push(new Unit("Boolean", "true"));
					}
				}
				else{
					error(31, "not one operand expected to be Boolean, but find " + op1.type);
				}
				break;
			case "neg":
				if(op1.type.equals("Integer")){
					int res = 0 - ((Integer) (op1.value));
					stack.push(new Unit("Integer", res));
				}
				else{
					error(31, "neg one operand expected to be Integer, but find " + op1.type);
				}
				break;
			default:
				break;
			}
		}
		
		private void topGamma(){
			Unit u = stack.pop();
			switch(u.type){
			case "Lambda":
				ArrayList<String> var = ((Lambda) u.value).variable;
				int parEnvIndex = ((Lambda) u.value).callEnvIndex;
				HashMap<String, Unit> table = EnvSet.get(parEnvIndex);	//	get Parent Env Table
				
					// use table to generate newTable, then insert newTable
				HashMap<String, Unit> newTable = new HashMap<String, Unit>();
				Set<String> ks = table.keySet();
				Iterator<String> it = ks.iterator();
				while(it.hasNext()){
					String key = it.next();
					newTable.put(key, table.get(key));
				}
					// Update the current Env Table
				Unit val = stack.pop();
				if(var.size() == 1){	// Only one variable
					newTable.put(var.get(0), val);
				}
				else{	// var.size() > 1
					if(val.type.equals("Tuple")){
						Tuple tuple = (Tuple) val.value;
						if(var.size() == tuple.order()){
							for(int i = 0; i < var.size(); i++){
								newTable.put(var.get(i), tuple.element.get(i));
							}
						}
						else{
							error(90, "Gamma-Lambda multiple variable binding expected the same length, but find " + var.size() + ", " + tuple.order());
						}
					}
					else{
						error(90, "Gamma-Lambda multiple variable binding expected Tuple, but find " + val.type);
					}
				}
				
				int newEnvIndex = getMaxEnvIndex();
				EnvSet.put(newEnvIndex, newTable);
				
					// open the new environment
				int ind = ((Lambda) u.value).expIndex;
				loadEnvironment(newEnvIndex, ind);
				
				break;
			case "Ystar":	// gamma	Y Lambda(c, k, x) => RecY(c, k, x)
				Unit l = stack.pop();
				if(l.type.equals("Lambda")){
					Lambda la = (Lambda) l.value;
					RecY recy = new RecY(la.variable, la.expIndex, la.callEnvIndex);
					stack.push(new Unit("RecY", recy));
				}
				else{
					error(90, "Gamma-Ystar expected Lambda on top of Stack, but find " + l.type);
				}
				break;
			case "RecY":	// gamma	RecY(x, k, c) R =>	gamma gamma		Lambda(x, k, c) RecY(x, k, c) R
				ControlStructure.push(new Unit("KW", "gamma"));
				ControlStructure.push(new Unit("KW", "gamma"));
				
				RecY rec = (RecY) u.value;
				Lambda ll = new Lambda(rec.variable, rec.expIndex, rec.callEnvIndex);
				stack.push(u);	// push RecY
				stack.push(new Unit("Lambda", ll));	// push Lambda
				break;
			case "Tuple":	// gamma	Tuple Integer
				Unit uInt = stack.pop();
				if(uInt.type.equals("Integer")){
					int index = (Integer) uInt.value;
					stack.push(((Tuple) u.value).ith(index));
				}
				else{
					error(90, "Gamma-Tuple expected Integer on top of Stack, but find " + uInt.type);
				}
				break;
			default:
				break;
			}
		}
		
		private void topKW(){
			Unit u = ControlStructure.pop();
			String kw = (String) (u.value);
			if(kw.equals("gamma")){
				topGamma();
			}
			else if(kw.equals("Ystar")){
				stack.push(new Unit("Ystar", "Ystar"));
			}
			else if(kw.equals("beta")){
				Unit _else = ControlStructure.pop();
				Unit _then = ControlStructure.pop();
				Unit _cond = stack.pop();
				if(_cond.type.equals("Boolean")){
					String truthVal = (String) (_cond.value);
					switch(truthVal){
					case "true":
						ControlStructure.push(_then);
						break;
					case "false":
						ControlStructure.push(_else);
						break;
					default:
						error(20, "Unknow boolean value in the stack " + truthVal);
						break;
					}
				}
				else{
					error(21, "beta Expect a boolean value but find " + _cond.type);
				}
			}
			else if(inBinary(kw)){
				opearteBinary(kw);
			}
			else if(inUnary(kw)){
				opearteUnary(kw);
			}
			else{	//	true, false, nil, dummy
				if(kw.equals("true") || kw.equals("false")){
					stack.push(new Unit("Boolean", kw));
				}
				else{
					stack.push(new Unit(kw, kw));
				}
			}
		}
		
		private void topBranch(){
			Unit u = ControlStructure.pop();
			int index = (Integer) u.value;
			
			for(int i = 0; i < lExpressionSet.get(index).size(); i++){
				this.ControlStructure.push(lExpressionSet.get(index).get(i));
			}
		}
		
		private void topToken(){
			Unit u = ControlStructure.pop();
			Token token = (Token) u.value;
			
			if(token.attribute.equals("INTEGER")){
				stack.push(new Unit("Integer", Integer.valueOf(token.content)));
			}
			else if(token.attribute.equals("STRING")){
				String s = token.content;
				if(s.equals("\\n")){
					s = String.valueOf('\n');
					stack.push(new Unit("String", s));
				}
				else if(s.equals("\\t")){
					s = String.valueOf('\t');
					stack.push(new Unit("String", s));
				}
				else{
					stack.push(new Unit("String", token.content));
				}
			}
			else if(token.attribute.equals("IDENTIFIER")){
				String id = token.content;
					
				int index = getCurEnvIndex();
				HashMap<String, Unit> table = EnvSet.get(index);
				if(table.containsKey(id)){	// first Lookup Env Table
					Unit obj = table.get(id);
					Unit newObj = new Unit(obj.type, obj.value);
					stack.push(newObj);
				}
				else if(FunctionKW.contains(id)){
					Unit g = ControlStructure.pop();
					if(g.type.equals("KW") && ((String) g.value).equals("gamma")){
						Unit op1 = stack.pop();
						switch(id){	// check has a special function
						case "Print":
							if(op1.type.equals("Integer")){
								System.out.print((Integer) op1.value);
							}
							else if(op1.type.equals("String") || op1.type.equals("Boolean")){
								System.out.print((String) op1.value);
							}
							else if(op1.type.equals("nil") || op1.type.equals("dummy")){	//???????
								System.out.print((String) op1.value);
							}
							else if(op1.type.equals("Tuple")){
								System.out.println(printTuple((Tuple) op1.value));
							}
							else{
								error(80, "Print function print unexpected data type. --" + op1.type);
							}
								// push dummy into the stack
							stack.push(new Unit("dummy", "dummy"));
							break;
						case "Stem":
							if(op1.type.equals("String")){
								op1.value = ((String) op1.value).substring(0, 1);
								stack.push(op1);
							}
							else{
								error(80, "Stem function expected String type, but find " + op1.type);
							}
							break;
						case "Stern":
							if(op1.type.equals("String")){
								op1.value = ((String) op1.value).substring(1, ((String) op1.value).length());
								stack.push(op1);
							}
							else{
								error(80, "Stern function expected String type, but find " + op1.type);
							}
							break;
						case "ItoS":
							if(op1.type.equals("Integer")){
								op1.value = String.valueOf((Integer) op1.value);
								op1.type = "String";
								stack.push(op1);
							}
							else{
								error(80, "ItoS function expected Integer type, but find " + op1.type);
							}
							break;
						case "Isstring":
							if(op1.type.equals("String")){
								stack.push(new Unit("Boolean", "true"));
							}
							else{
								stack.push(new Unit("Boolean", "false"));
							}
							break;
						case "Isinteger":
							if(op1.type.equals("Integer")){
								stack.push(new Unit("Boolean", "true"));
							}
							else{
								stack.push(new Unit("Boolean", "false"));
							}
							break;
						case "Istuple":
							if(op1.type.equals("Tuple")){
								stack.push(new Unit("Boolean", "true"));
							}
							else{
								stack.push(new Unit("Boolean", "false"));
							}
							break;
						case "Istruthvalue":
							if(op1.type.equals("Boolean")){
								stack.push(new Unit("Boolean", "true"));
							}
							else{
								stack.push(new Unit("Boolean", "false"));
							}
							break;
						case "Isfunction":
							if(op1.type.equals("Lambda")){	//?????????????????????????
								stack.push(new Unit("Boolean", "true"));
							}
							else{
								stack.push(new Unit("Boolean", "false"));
							}
							break;
						case "Isdummy":
							if(op1.type.equals("dummy")){
								stack.push(new Unit("Boolean", "true"));
							}
							else{
								stack.push(new Unit("Boolean", "false"));
							}
							break;
						case "Order":
							if(op1.type.equals("Tuple")){
								int order = ((Tuple) op1.value).order();
								stack.push(new Unit("Integer", order));
							}
							else if(op1.type.equals("nil")){
								int order = 0;
								stack.push(new Unit("Integer", order));
							}
							else{
								error(80, "Order function expected Tuple type, but find " + op1.type);
							}
							break;
						case "Conc":
							Unit g1 = ControlStructure.pop();
							if(g1.type.equals("KW") && ((String) g1.value).equals("gamma")){
								Unit op2 = stack.pop();
								if(op1.type.equals("String") && op2.type.equals("String")){
									op1.value = (String) op1.value + (String) op2.value;
									stack.push(op1);
								}
								else{
									error(80, "Conc function two operands expected String type, but find " + op1.type + ", " + op2.type);
								}
							}
							else{
								error(80, "Conc function expected two gamma on the top of Control Structure, but find " + g.type + ", " + g1.type);
							}
							break;
						default:
							break;
						}
					}
					else{
						error(80, "Element before Function keyword on the top of Control Structure is not Gamma! -- " + g.type);
					}
				}
				else{
					error(80, "Unkown Identifier on the top of Control Structure! -- " + id);
				}
			}
			else{		}
		}
		
		private void topTau(){
			Unit u = ControlStructure.pop();
			int num = (Integer) u.value;
			ArrayList<Unit> ele = new ArrayList<Unit>();
			for(int i = 0; i < num; i++){
				Unit op = stack.pop();
				ele.add(op);
			}
			stack.push(new Unit("Tuple", new Tuple(ele)));
		}
		
		private void interpret(){
			boolean isEnd = false;
			while(isEnd == false){
				Unit u = ControlStructure.peek();	//	Just peek, not pop!
				switch(u.type){
				case "Environment":
					if(quitEnvironment() == 0){
						isEnd = true;
						res = stack.pop();
					}
					break;
				case "Lambda":
					topLambda();
					break;
				case "KW":
					topKW();
					break;
				case "Branch":
					topBranch();
					break;
				case "Token":
					topToken();
					break;
				case "tau":
					topTau();
					break;
				default:	// "RecY" and "Branch" cannot be here!
					break;
				}
				
					//	Debug Print
//				Output output = new Output();
//				output.printArrayListUnit(ControlStructure, 0);
//				output.printArrayListUnit(stack, 1);
			}
		}
		
		private void error(int index, String s){
			System.out.println(index + " " + s);
			System.exit(0);
		}
}
