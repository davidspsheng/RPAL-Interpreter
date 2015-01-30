import java.util.ArrayList;

public class RecY {
	public ArrayList<String> variable;
	public int expIndex;
	public int callEnvIndex;
	
	RecY(ArrayList<String> v, int ei, int cei){
		this.variable = v;
		this.expIndex = ei;
		this.callEnvIndex = cei;
	}
	
	public void setCEI(int cei){
		this.callEnvIndex = cei;
	}
}
