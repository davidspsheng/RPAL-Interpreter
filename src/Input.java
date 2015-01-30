import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Input {
	BufferedReader reader;
	String buffer;
	char preChar;
	int lineNum;
	
	private int index;
	private String content;
	
	Input(String fileName) throws FileNotFoundException{
		reader = null;
		buffer = "";
		preChar = '\0';
		lineNum = 0;
		
		openFile(fileName);
	}
	
	private void openFile(String fileName) throws FileNotFoundException{
		File file = new File(fileName);
		reader = new BufferedReader(new FileReader(file));
	}
	
	public void closeFile() throws IOException{
		reader.close();
	}
	
	private int readBuffer(BufferedReader reader) throws IOException{
        String tempString = null;
        if ((tempString = reader.readLine()) != null) {
        	buffer = tempString + '\n';
        	lineNum++;
        	return 1;
        }
        else{
        	return -1;
        }
	}
	
	public char nextChar() throws IOException{
		if(buffer.equals("")){
			if(readBuffer(reader) != -1){
				preChar = buffer.charAt(0);
				buffer = buffer.substring(1, buffer.length());
			}
			else{
				preChar = '\0';	// end of file
			}
		}
		else{
			preChar = buffer.charAt(0);
			buffer = buffer.substring(1, buffer.length());
		}
		return preChar;
	}
	
	public void retract(){
		if(preChar != '\0')
			buffer = String.valueOf(preChar) + buffer;
	}
	
	public boolean isFileEnd(){
		if(content.charAt(index + 1) == '`'){
			return true;
		}
		else
			return false;
	}
}
