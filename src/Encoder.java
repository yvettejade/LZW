import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

public class Encoder {
	private HashMap<String, Integer> tableOfCodes;
	public Encoder()
	{

	}
	//fills in hashmap with initial characters and corresponding number values from ascii chart
	public HashMap<String, Integer> fillInAsciiValues()
	{
		for (int i = 0; i < 128; i++)
		{
			tableOfCodes.put(Character.toString((char)i), i);
		}
		return tableOfCodes;
	}

	public void encode(String inputFile) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader (inputFile));
		PrintWriter pw = new PrintWriter ("encodedFile.txt");
		this.tableOfCodes = fillInAsciiValues();
		String word = "";
		int index=128;
		while (br.ready())
		{
			word+=br.read();
			while (tableOfCodes.get(word)!=null)
			{
				word+=br.read();
			}
			if (tableOfCodes.size()>2000)
			{
				pw.print(tableOfCodes.get(word.length()-2));
			}
			else
			{
			tableOfCodes.put(word, index++);
			pw.print(tableOfCodes.get(word.length()-2));
			}
		}

	}

}
