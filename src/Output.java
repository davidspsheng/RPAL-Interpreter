import java.util.ArrayList;
import java.util.Stack;


public class Output {
//	private String lExpression = "lExpression.txt";
	
	Output(){	}
	
	public void OutputlExpression(ArrayList<ArrayList<Unit>> lES){
		System.out.println();
		for(int i=0;i<lES.size();i++){
			String s = "lE " + i + ":";
			ArrayList<Unit> a = lES.get(i);
			for(int j=0; j<a.size();j++){
				if(a.get(j).type.equals("KW")){
					String l = (String) (a.get(j).value);
					s += " " + l;
				}
				else if(a.get(j).type.equals("Lambda")){
					Lambda l = (Lambda) (a.get(j).value);
					s += " lambda<" + l.expIndex;
					for(int k =0;k<l.variable.size();k++){
						s += "," + l.variable.get(k);
					}
					s += ">";
				}
				else if(a.get(j).type.equals("Token")){
					Token t = (Token) (a.get(j).value);
					s += " " + t.content;
				}
				else if(a.get(j).type.equals("Branch")){
					int index = (Integer) (a.get(j).value);
					s += " Brach(lE" + index + " )";
				}
				else if(a.get(j).type.equals("tau")){
					int num = (Integer) (a.get(j).value);
					s += " tau" + num;
				}
				else{	}
				
			}
			System.out.println(s);
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
	
	public void printArrayListUnit(Stack<Unit> st, int flag){
		String s = "";
		if(flag == 0){	//	Control Structure
			for(int i=0;i<st.size();i++){
				Unit u = st.get(i);
				switch(u.type){
					case "Environment":
						s += " e" + (Integer) u.value;
						break;
					case "Lambda":
						s += " Lambda(" + ((Lambda) u.value).callEnvIndex;
						for(int k =0;k<((Lambda) u.value).variable.size();k++){
							s += "," + ((Lambda) u.value).variable.get(k);
						}
						s += "," + ((Lambda) u.value).expIndex + ")";
						break;
					case "KW":
						s += " " + (String) u.value;
						break;
					case "Branch":
						int index = (Integer) (u.value);
						s += " Brach(lE" + index + " )";
						break;
					case "Token":
						s += " " + ((Token) u.value).content;
						break;
					case "tau":
						index = (Integer) (u.value);
						s += " tau" + index;
						break;
				}
			}
			System.out.print(s + "\t");
		}
		if(flag == 1){	// Stack
			for(int i=st.size()-1;i>=0;i--){
				Unit u = st.get(i);
				switch(u.type){
					case "Environment":
						s += " e" + (Integer) u.value;
						break;
					case "RecY":
						s += " RecY(" + ((RecY) u.value).callEnvIndex;
						for(int k =0;k<((RecY) u.value).variable.size();k++){
							s += "," + ((RecY) u.value).variable.get(k);
						}
						s += "," + ((RecY) u.value).expIndex + ")";
						break;
					case "Lambda":
						s += " Lambda(" + ((Lambda) u.value).callEnvIndex;
						for(int k =0;k<((Lambda) u.value).variable.size();k++){
							s += "," + ((Lambda) u.value).variable.get(k);
						}
						s += "," + ((Lambda) u.value).expIndex + ")";
						break;
					case "Ystar":
						s += " " + (String) u.value;
						break;
					case "Integer":
						s += " " + (Integer) u.value;
						break;
					case "Boolean":
						s = " " + (String) (u.value);
						break;
					case "String":
						s += " " + (String) u.value;
						break;
					case "Tuple":
						s += " " + printTuple((Tuple) u.value);
						break;
					default:
						s += " " + u.type;
						break;
				}
			}
			System.out.println(s);
		}
	}
}
