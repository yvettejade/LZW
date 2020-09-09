import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Encoder {
	private HashMap<String, Integer> tableOfCodes;
	//constructs Encoder
	public Encoder()
	{

	}
	//fills in hashmap with initial characters and corresponding number values from ascii chart(0-127)
	public HashMap<String, Integer> fillInAsciiValues()
	{
		tableOfCodes = new HashMap<String, Integer>();
		for (int i = 0; i < 128; i++)
		{
			tableOfCodes.put(Character.toString((char)i), (Integer)i);
		}
		return tableOfCodes;
	}

	public void encode(String inputFile) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader (inputFile));
		PrintWriter pw = new PrintWriter ("encodedFile.txt");
		//fills in hashmap with initial characters from method above
		this.tableOfCodes = fillInAsciiValues(); 
		String currentChars = ""; 
		Integer index=127;
		while (br.ready())
		{
			currentChars+=(char)br.read();
			//generates a string that isn't a key in tableOfCodes yet
			while (tableOfCodes.get(currentChars)!=null)
			{
				currentChars+=(char)br.read();
			}
			//if the size of the hashmap exceeds 2000, values will no longer be added to the table 
			if (tableOfCodes.size()>2000) 
			{
				String key = currentChars.substring(0, currentChars.length()-1);
				pw.print(tableOfCodes.get(key));
				currentChars=currentChars.substring(currentChars.length()-1);
			}
			//adds new key and value to tableOfCodes
			//prints the Integer value that corresponds to the latest recognizable key
			//resets currentChars to start with its last letter
			else
			{
				String key = currentChars.substring(0,currentChars.length()-1); 
				index++;
				tableOfCodes.put(currentChars, index); 
				pw.print(tableOfCodes.get(key));
				currentChars=currentChars.substring(currentChars.length()-1); 
			}
		}
		br.close();
		pw.close();

	}

}
