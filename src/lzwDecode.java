import java.util.*;
import java.io.*;

class Pair1
{
	private int times;
	private char word;
	Pair1 (int one, char two)
	{
		times = one;
		word = two;
	}
	public int getTimes()
	{
		return times;
	}
	public char getWord()
	{
		return word;
	}
}

public class lzwDecode
{
	static Comparator<Pair1> pair1Comparator = new Comparator<Pair1>() 
	{
        //@Override
        public int compare(Pair1 one, Pair1 two) 
		{
	        return one.getTimes() - two.getTimes();
	    }
    };

    static final int MAX = 2147483647;
    // priorityQueue holding the recent-ness of the char + the char
	static PriorityQueue<Pair1> pq = new PriorityQueue<>(pair1Comparator);

	static HashMap<String, Integer> latest = new HashMap<String, Integer>();

	public static HashMap<Integer,String> init(HashMap<Integer,String> table)
	{
		// fill the table with the standard ascii 1-128
		for(int a = 0; a < 128; a++)
		{
			char current = (char)(a);
			// (corresponding ascii (char) + pattern (string))
			// reversed from prev bc i'm pretty sure containsValue is O(N) while containsKey is O(1)
			// also bc get is way easier to use than whatever we would need to use to find the matching key
			table.put((int)current,current+"");
			pq.add(new Pair1(MAX, current));
			latest.put(current+"", MAX);
		}
		return table;
	}

	public static void decode (String input, String output) 
	{
		// the table containing the pattern and corresponding ascii - "a" -> 'a'
		HashMap<Integer, String> table = new HashMap<Integer, String>();
		// holds the decoded message
		StringBuilder decoding = new StringBuilder("");

		// fill the table with the standard ascii 1-128
		init(table);
		// the first available character
		int num = 128; 
		int place = 128;
		int counter = 0;
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(input));
			int current = br.read();

			// java doesn't allow for empty chars, so we just treat 'a' as '' bc it gets overwritten first
			char prev='a';
			String decodeBlock = "";
			// the first char of the previous block, used for the special case when LZW doesn't work + adding to the table
			String prevChar = "";

			// check if the first char of the encoding is in the table
			// should always be there, just being safe (we have big problems if it isn't in the table)
			if(table.containsKey(current))
			{
				prev = ((char)current);
				// add to the decoding 
				decoding.append(table.get(current));
				prevChar = table.get(current).charAt(0)+"";
			}
			/*System.out.println(table);
			System.out.println(latest);
			System.out.println("num: "+num);
			System.out.println();*/

			while(current != -1)
			{

				current = br.read();
				
				//System.out.println("current: "+current);
				//System.out.println("num: "+num);
				// until EOF
				if(current == -1)
					break;

				// current character isn't found in the table, weird lzw case
				if(!table.containsKey(current))
				{
					//System.out.println("one");
					// add the first char of the prev to the end of the decoded prev to find the missing value
					decodeBlock = table.get((int)prev) + prevChar;
					// if it's not in the table don't bother adding it to anything else

					prevChar = decodeBlock.charAt(0)+"";
					///////////////////////////////////////////////////////////
						//System.out.println("weird three");
						table.put(place, table.get((int)prev)+prevChar);
						latest.put(table.get((int)prev)+prevChar, counter);
						pq.add(new Pair1(counter, (char)place));
						counter++;
						//System.out.println(table);
						//System.out.println(latest);
						//System.out.println();
					//////////////////////////////////////////////////////////////////
				}

				// current character is in the table!
				if(table.containsKey(current))
				{
					//if(num == 316)
					//{
					//	System.out.println(table);
					//	System.out.println(current);
					//}
					//System.out.println("two");
					//System.out.println(current);
					// simply decode the current char
					decodeBlock = table.get(current);
					//System.out.println("prev: "+prevChar);
					prevChar = decodeBlock.charAt(0)+"";
					//System.out.println("new prev: "+prevChar);
					///////////////////////////////////////////////////////////
				
					//System.out.println("three");
					table.put(place, table.get((int)prev)+prevChar);
					latest.put(table.get((int)prev)+prevChar, counter);
					pq.add(new Pair1(counter, (char)place));
					counter++;
					//System.out.println(table);
					//System.out.println(latest);
					//System.out.println();
					
					
					//prevChar = decodeBlock.charAt(0)+"";
					//////////////////////////////////////////////////////////////////
					// table already contains it, so don't change that
					// update the latest and add to the priority queue

					if(current >= 128)
					{
						//System.out.println(num+ " "+ current);
						latest.replace(table.get(current), counter);
						pq.add(new Pair1(counter, (char)current));
						counter++;
					}
					//System.out.println(table);
					//System.out.println(latest);
					//System.out.println();
				}

				// add whatever was decoded to decoding
				decoding.append(decodeBlock);
				//System.out.println("added: "+decodeBlock);

				// save first char of the decoded block
				//prevChar = decodeBlock.charAt(0)+"";
				//System.out.println("prevchar: "+prevChar);
				

				//////////////////////////////////////////////////////////////////////////////////////////////
				if(num >= 256-1)
				{
					Pair1 x = pq.poll();
					//System.out.println("first: "+table.get((int)x.getWord()));
					//System.out.println("first: "+x.getTimes());
					//!table.containsKey((int)x.getWord()) || !latest.containsKey(table.get((int)x.getWord())) || 
					while(!table.containsKey((int)x.getWord()) || !latest.containsKey(table.get((int)x.getWord())) || latest.get(table.get((int)x.getWord())) != x.getTimes() || (int)x.getWord() < 128)
						x = pq.poll();

					//System.out.println("final: "+table.get((int)x.getWord()));
					//System.out.println("final: "+x.getTimes());
					//System.out.println("new: "+table.get((int)prev)+prevChar);

					latest.remove(table.get((int)x.getWord()));

					//System.out.println("to remove: "+table.get((int)prev)+" "+prevChar);

					table.remove((int)x.getWord());
					place = (int)x.getWord()-1;
				}
				//////////////////////////////////////////////////////////////////////////////////////////////



				// max 256 bc the extended ascii table ends at 255, so we can't represent anything past 255
				// update: changed it to 55296
				// max should be 65536 bc in utf - 8 1 char is maxxed at 65536, but bc of some weird utf rule, we're capped at 55296
				// add to the table
				

				// increase the next available ascii/table slot
				num++;
				place++;
				// save the previous
				prev = (char)current;

				//System.out.println(table);
				//System.out.println(latest);
				//System.out.println();
				
			}
			br.close();
			System.out.println(num);
			//System.out.println(table);
		}
		catch(IOException e)
		{
			System.out.println("IOException");
		}
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			// write out the decoding
			bw.write(decoding.toString());
			//System.out.println(decoding.toString());
			bw.close();
		}
		catch(IOException e)
		{
			System.out.println("IOException");
		}
	}
}