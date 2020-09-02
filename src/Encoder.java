
public class Encoder {
	import java.io.BufferedReader;
	import java.io.FileReader;
	import java.io.PrintWriter;
	import java.lang.reflect.Array;
	import java.util.*;
	public class Encoder {
		HashMap <String, Integer> map = new HashMap<String, Integer>;
		for (int i = 0; i < 128; i++)
		{
			map.put((char)i+"", i);
		}
		int index = 127;
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
