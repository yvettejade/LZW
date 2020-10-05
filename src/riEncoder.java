import java.io.*;
import java.util.*;

/*
	THE BUG:
	sometimes, once we've hit the max table size, the encoder will edit the latest time it was used, but the decoder won't
	this probably has something to do with the order they're processed (ie place), but i just can't find it
	it seems to happen when we have something in the table already ex: " m" and we also have " my"
	the decoder sometimes skips the " m" and just goes straight to the " my" so the table replacements are in a different order
	thus the decoding and encoding tables are different, changing the decoding
*/
// data type for the priority queue, holds the time seen + the word
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

public class riEncoder 
{

	// since we have a custom class, we need a custom sort function
	// sorts by order seen (least recently seen at the front)
	static Comparator<Pair> pairComparator = new Comparator<Pair>() 
	{
        public int compare(Pair one, Pair two) 
		{
	        return one.getTimes() - two.getTimes();
	    }
    };

    static final int MAX = 2147483647;

	// priorityQueue holding the recent-ness of any word
	static PriorityQueue<Pair> pq = new PriorityQueue<>(pairComparator);

	// always holds the most recent time seen
	static HashMap<String, Integer> latest = new HashMap<String, Integer>();

	/*
		EDIT 1:
		changed HashMap<String, Integer> to HashMap<String, Character> so we can use ascii/unicode for max compression
	*/
	private HashMap<String, Integer> tableOfCodes;
	//constructs Encoder
	public riEncoder()
	{

	}
	//fills in hashmap with initial characters and corresponding number values from ascii chart(0-127)
	public HashMap<String, Integer> fillInAsciiValues()
	{
		tableOfCodes = new HashMap<String, Integer>();
		for (int i = 0; i < 128; i++)
		{
			/*
				hashmap update
			*/
			tableOfCodes.put(((char)(i))+"", i);
			// add inits to the priorityQueue
			// we REALLY don't want to get rid of the inits bc it's inconvenient, so set them to the max int, so they never get hit
			pq.add(new Pair(MAX, (char)(i)+""));
			latest.put((char)(i)+"", MAX);
			
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
		int index = 128;
		int place = 128;
		int counter = 0;
		while (br.ready())
		{
			//System.out.println(place);
			currentChars+=(char)br.read();
			//generates a string that isn't a key in tableOfCodes yet
			if(tableOfCodes.containsKey(currentChars))
			{
				// we already have this in our table + latest + pq
				// instead of searching the pq and removing from there, edit it in latest and add to the pq
				if(currentChars.length()>1)
				{
					latest.replace(currentChars, counter);
					pq.add(new Pair(counter, currentChars));
					counter++;
				}
			}
			//if the size of the hashmap exceeds 2000, values will no longer be added to the table 
			/*
				EDIT 2:
				max should be 65536 bc in utf - 8 1 char is maxxed at 65536, but bc of some weird utf rule, we're capped at 55296 (55295 bc > obvs)
			*/
			else
			{
				//adds new key and value to tableOfCodes
				//prints the Integer value that corresponds to the latest recognizable key
				//resets currentChars to start with its last letter

				String key = currentChars.substring(0,currentChars.length()-1); 
				
				/*
					hashmap update
				*/
				// table doesn't contain it, so add to the table
				tableOfCodes.put(currentChars, place); 
				int x = tableOfCodes.get(key);
				pw.print((char)x);

				// mark down the current time + put it in the pq
				latest.put(currentChars, counter);
				pq.add(new Pair(counter, currentChars));
				counter++;

				currentChars=currentChars.substring(currentChars.length()-1); 
				place++;
			}
			// the table is full, need to clear a spot for the next round
			if (tableOfCodes.size()>=55296)
			{
				// run through everything in the pq until you hit something that matches the latest time we've seen that string (and isn't initialized)
				// ie if the last time we've seen "ab" is 10, and we run into "ab", 5, ignore it bc it's not the most updated
				Pair x = pq.poll();
				
				while(latest.get(x.getWord()) != x.getTimes() || tableOfCodes.get(x.getWord()) < 128)
				{
					x = pq.poll();
				}

				// clear everything 
				// set place to the place that was just cleared
				place = tableOfCodes.get(x.getWord());
				tableOfCodes.remove(x.getWord());
				latest.remove(x.getWord());
			}
		}
		
		/*
			EDIT 3: 
			The encoder kept dropping the last character bc the while loop runs 1 time too few
			It needs to print whatever it's holding in currentChars (the last char of the file)
		*/
		int x = tableOfCodes.get(currentChars);
		pw.print((char)x);
		//System.out.println(tableOfCodes);
		br.close();
		pw.close();

	}

}