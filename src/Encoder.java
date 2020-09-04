import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

public class Encoder {
	private HashMap<String, Integer> table;
	public Encoder()
	{
		
	}
	//fills in hashmap with initial characters and corresponding number values from ascii chart
	public HashMap<String, Integer> fillInAsciiValues()
	{
		for (int i = 0; i < 128; i++)
		{
			table.put((char)i+"", i);
		}
		return table;
	}

	public void encode() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader ("file.txt"));
		PrintWriter pw = new PrintWriter ("output.txt");
		this.table = fillInAsciiValues();
		String word = "";
		int index=128;
		while (br.ready())
		{
			word+=br.read();
			while (table.get(word)!=null)
			{
				word+=br.read();
			}
			table.put(word, index++);
			pw.print(table.get(word.length()-2));
		}
	}

}
