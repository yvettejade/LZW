import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class riEncoder {
	/*
		EDIT 1:
		changed HashMap<String, Integer> to HashMap<String, Character> so we can use ascii/unicode for max compression
	*/
	private HashMap<String, Character> tableOfCodes;
	//constructs Encoder
	public riEncoder()
	{

	}
	//fills in hashmap with initial characters and corresponding number values from ascii chart(0-127)
	public HashMap<String, Character> fillInAsciiValues()
	{
		tableOfCodes = new HashMap<String, Character>();
		for (int i = 0; i < 128; i++)
		{
			/*
				hashmap update
			*/
			tableOfCodes.put(((char)i)+"", (char)i);
		}
		return tableOfCodes;
	}

	public void encode(String inputFile) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader (inputFile));
		PrintWriter pw = new PrintWriter ("lzwEncoded.txt");
		//fills in hashmap with initial characters from method above
		this.tableOfCodes = fillInAsciiValues(); 
		String currentChars = ""; 
		int index=127;
		while (br.ready())
		{
			currentChars+=(char)br.read();
			//generates a string that isn't a key in tableOfCodes yet
			while (tableOfCodes.get(currentChars)!=null)
			{
				currentChars+=(char)br.read();
			}
			//if the size of the hashmap exceeds 2000, values will no longer be added to the table 
			/*
				EDIT 2:
				max should be 65536 bc in utf - 8 1 char is maxxed at 65536, but bc of some weird utf rule, we're capped at 55296 (55295 bc > obvs)
			*/
			if (tableOfCodes.size()>55295) 
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
				/*
					hashmap update
				*/
				tableOfCodes.put(currentChars, (char)index); 
				pw.print(tableOfCodes.get(key));
				currentChars=currentChars.substring(currentChars.length()-1); 
			}
		}
		/*
			EDIT 3: 
			The encoder kept dropping the last character bc the while loop runs 1 time too few
			It needs to print whatever it's holding in currentChars (the last char of the file)
		*/
		pw.print(tableOfCodes.get(currentChars));
		br.close();
		pw.close();

	}

}