import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;
private HashMap<String, Integer> table;
public class Encoder {

	//fills in hashmap with initial characters and corresponding number values from ascii chart
	public HashMap<String, Integer> fillInAsciiValues()
	{
		HashMap <String, Integer> map = new HashMap<String, Integer>;
		for (int i = 0; i < 128; i++)
		{
			map.put((char)i+"", i);
		}
	}

	public void encode()
	{
		BufferedReader br = new BufferedReader(new FileReader ("file.txt"));
		PrintWriter pw = new PrintWriter ("output.txt");
		String word = "";
		while (br.ready())
		{
			word+=br.read();
			while (map.get(word)!=null)
			{
				word+=br.read();
			}
			map.put(word, index++);
			pw.print(map.);
		}
	}

}
