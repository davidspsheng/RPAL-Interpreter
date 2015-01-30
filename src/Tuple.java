import java.util.ArrayList;


public class Tuple {
	public ArrayList<Unit> element;	//	
	
	Tuple(ArrayList<Unit> ele){
		this.element = ele;
	}
	
	public int order(){
		return element.size();
	}
	
	public Unit ith(int index){
		return element.get(index - 1);
	}
}
