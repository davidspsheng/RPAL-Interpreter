import java.util.ArrayList;


public class Lambda {
	public ArrayList<String> variable;
	public int expIndex;
	public int callEnvIndex;
	
	Lambda(ArrayList<String> v, int ei, int cei){
		this.variable = v;
		this.expIndex = ei;
		this.callEnvIndex = cei;
	}
	
	public void setCEI(int cei){
		this.callEnvIndex = cei;
	}
}
