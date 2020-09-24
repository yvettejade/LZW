import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Decoder {
	private HashMap<Integer, String> codeTable;
	public Decoder () { 
		 codeTable = new HashMap<Integer,String>();
		for (int i = 0; i <= 255; i++) { //creates table of characters
			codeTable.put(i, (char)i + "");
		}
	}
	public void decode(String inputFile, String outputFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader (inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter (outputFile));
		
		
		int index=255;
		String previous = codeTable.get(br.read())+"";
		String currentString = "";
		String decoded = "";
		decoded+=previous;
		
		while (br.ready()) {
			int currentValue = br.read();
			// if combination exists 
			if (codeTable.containsKey(currentValue)){ 
				currentString = codeTable.get(currentValue);
				String toAdd = previous + currentString.charAt(0);// previous combination + first letter of the one just read
				codeTable.put(index, toAdd );
				index++;
				decoded+=currentString;
				
			}
			else {
				currentString = previous + previous.charAt(0);
				codeTable.put(index, currentString);
				decoded+=currentString;
				index++;
			}
			previous=codeTable.get(currentValue);
			
		}
		
		bw.write(decoded);
		br.close();
		bw.close();
		
	}

}
