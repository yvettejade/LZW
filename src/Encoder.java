import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Encoder {
	private HashMap<String, Integer> tableOfCodes;
	public Encoder()
	{

	}
	//fills in hashmap with initial characters and corresponding number values from ascii chart
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
		this.tableOfCodes = fillInAsciiValues();
		String currentChars = "";
		Integer index=127;
		while (br.ready())
		{
			currentChars+=(char)br.read();
			while (tableOfCodes.get(currentChars)!=null)
			{
				currentChars+=(char)br.read();
			}
			if (tableOfCodes.size()>2000)
			{
				String key = currentChars.substring(0, currentChars.length()-1);
				pw.print(tableOfCodes.get(key));
				currentChars=currentChars.substring(currentChars.length()-1);
			}
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
