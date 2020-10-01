import java.util.*;
import java.io.*;

class Pair
{
	private int times;
	private String word;
	Pair (int one, String two)
	{
		times = one;
		word = two;
	}
	public int getTimes()
	{
		return times;
	}
	public String getWord()
	{
		return word;
	}
}

public class lzwEncoding 
{
	static Comparator<Pair> pairComparator = new Comparator<Pair>() 
	{
        //@Override
        public int compare(Pair one, Pair two) 
		{
	        return one.getTimes() - two.getTimes();
	    }
    };
    

	static final int MAX = 2147483647;
	// priorityQueue holding the recent-ness of the char + the char
	static PriorityQueue<Pair> pq = new PriorityQueue<>(pairComparator);

	static HashMap<String, Integer> latest = new HashMap<String, Integer>();


	public static HashMap<String,Integer> init(HashMap<String,Integer> table)
	{
		// fill the table with the standard ascii 1-128
		for(int a = 0; a < 128; a++)
		{
			char current = (char)(a);
			// (pattern (string) + corresponding ascii (char))
			table.put(current+"",a);
			// add inits to the priorityQueue
			// we REALLY don't want to get rid of the inits bc it's inconvenient, so set them to the max int, so they never get hit
			pq.add(new Pair(MAX, current+""));
			latest.put(current+"", MAX);
		}
		return table;
	}

	public static void encode (String input, String output) 
	{
		// the table containing the pattern and corresponding ascii - "a" -> 'a'
		HashMap<String, Integer> table = new HashMap<String, Integer>();
		// holds the encoded message
		StringBuilder encoding = new StringBuilder("");
		// fill the table with the standard ascii 1-128
		init(table);

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(input));
			int current = br.read();

			// last char of previous pattern
			String prev = (char)current + "";

			// the next available ascii/table slot
			int num = 128;
			int counter = 0;

			/*System.out.println("table");
			System.out.println(table);
			System.out.println("latest");
			System.out.println(latest);
			System.out.println("pq");
			System.out.println(pq.peek().getWord()+" "+pq.peek().getTimes());*/
			while(current != -1)
			{
				current = br.read();

				//System.out.println("current: "+current);
				//System.out.println(table);
				//System.out.println(latest);
				//System.out.println();
				// current portion of the text being scanned for new patterns
				String temp = prev + (char)current;

				// pattern not found
				//System.out.println(temp);
				if(!table.containsKey(temp))
				{
					// encode previous
					//System.out.println("prev: "+prev);
					int y=table.get(prev);
					encoding.append((char)y);
					//System.out.println("added: "+y);
					//System.out.println();

					// max 256 bc the extended ascii table ends at 255, so we can't represent anything past 255
					// add to the table
					int place = num;
					if(num < 256)
					{
						//System.out.println("temp: "+temp);
						table.put(temp, num);
						latest.put(temp, counter);
						pq.add(new Pair(counter, temp));
						counter++;
					}
					else
					{
						//System.out.println("temp: "+temp);
						Pair x = pq.poll();

						//System.out.println("first: "+(int)table.get(x.getWord()));
						//System.out.println("first: "+x.getTimes());

						while(latest.get(x.getWord()) != x.getTimes() || table.get(x.getWord()) < 128)
							x = pq.poll();

						//System.out.println("final: "+(int)table.get(x.getWord()));
						//System.out.println("final: "+x.getTimes());

						table.put(temp, (int)table.get(x.getWord()));
						table.remove(x.getWord());
						latest.put(temp, counter);
						latest.remove(x.getWord());
						pq.add(new Pair(counter, temp));
						counter++;
					}
					num++;

					// reset bc we've already encoded the previous
					prev = "";
				}
				else // table already contains the key, update the last time
				{
					//System.out.println("hi");
					latest.replace(temp, counter);
					pq.add(new Pair(counter, temp));
					counter++;
				}
				// add to or set the previous
				prev += (char)current;
			}
			System.out.println(num);
			System.out.println(table);
			br.close();
		}
		catch(IOException e)
		{
			System.out.println("IOException");
		}
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			// write out the encoding
			bw.write(encoding.toString());
			bw.close();
		}
		catch(IOException e)
		{
			System.out.println("IOException");
		}
	}
}