import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;
private HashMap<String, Integer> table;
public class Encoder {

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

	public void encode()
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
			map.put(word, index++);
			pw.print(map.get(word.length()-2));
		}
	}

}
