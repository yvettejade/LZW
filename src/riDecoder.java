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
// data type for the priority queue, holds the time seen + the char version of the word
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

public class riDecoder 
{
	// since we have a custom class, we need a custom sort function
	// sorts by order seen (least recently seen at the front)
	static Comparator<Pair1> pair1Comparator = new Comparator<Pair1>() 
	{
        public int compare(Pair1 one, Pair1 two) 
		{
	        return one.getTimes() - two.getTimes();
	    }
    };

	/*
		EDIT 1:
		Changed to char, string so we can use utf more easily
	*/
	private HashMap<Integer, String> codeTable;

	static final int MAX = 2147483647;
    // priorityQueue holding the recent-ness of the char + the char
	static PriorityQueue<Pair1> pq = new PriorityQueue<>(pair1Comparator);

	// always holds the most recent time seen
	static HashMap<String, Integer> latest = new HashMap<String, Integer>();

	public riDecoder () 
	{ 
		 codeTable = new HashMap<Integer,String>();
		 /*
		 	EDIT 2:
		 	In order to work with Lily and Isa's encoder, it can only init up to 128
		 */
		for (int i = 0; i < 128; i++) 
		{ 
			// add inits to the priorityQueue
			// we REALLY don't want to get rid of the inits bc it's inconvenient, so set them to the max int, so they never get hit
			codeTable.put(i, (char)(i) + "");
			pq.add(new Pair1(MAX, (char)(i)));
			latest.put((char)(i)+"", MAX);
		}
		
	}
	public void decode(String inputFile, String outputFile) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader (inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter (outputFile));
		
		/*
			starts at 128
		*/
		int index=128;
		int place = 128;
		int counter = 0;

		int currentValue = br.read();
		String previous = codeTable.get(currentValue);
		/*
			EDIT 3:
			Missing the first char of the previous block, used for the special case when LZW doesn't work + adding to the table
		*/
		String prevChar = "";
		String currentString = "";
		/*
			EDIT 4:
			Can't change strings, so they're hella slow
			Added StringBuilder (or just print, but I like stringbuilders)
		*/
		StringBuilder decoded = new StringBuilder("");
		decoded.append(previous);
		prevChar = codeTable.get(currentValue).charAt(0)+"";
		
		while (br.ready()) 
		{
			currentValue = br.read();

			// if combination exists 
			if (codeTable.containsKey(currentValue))
			{ 
				currentString = codeTable.get(currentValue);
				// previous combination + first letter of the one just read
				String toAdd = previous+"" + currentString.charAt(0);
				/*
					EDIT 5: 
					Should never occur, but just to be safe
					max should be 65536 bc in utf - 8 1 char is maxxed at 65536, but bc of some weird utf rule, we're capped at 55296
				*/
				
				decoded.append(currentString);

				// add to the table + update the pq + latest
				codeTable.put(place, toAdd);
				latest.put(toAdd, counter);
				pq.add(new Pair1(counter, (char)place));
				counter++;

				// ignore any inits
				// update the latest and add to the priority queue
				if(currentValue >= 128)
				{
					latest.replace(codeTable.get(currentValue), counter);
					pq.add(new Pair1(counter, (char)currentValue));
					counter++;
				}
				
			}
			else //error isn't here
			{
				/*
					need to use prevChar to constructed the missing word
				*/
				currentString = previous + prevChar;
				
				// put the new code into the table + update pq + latest
				codeTable.put(place, currentString);
				latest.put(currentString, counter);
				pq.add(new Pair1(counter, (char)place));
				counter++;

				decoded.append(currentString);
			}

			/*
				need to mark down the first char of the prev block
			*/
			prevChar = currentString.charAt(0)+"";
			previous=codeTable.get(currentValue);


			/* 
				max should be 65536 bc in utf - 8 1 char is maxxed at 65536, but bc of some weird utf rule, we're capped at 55296
			*/
			// the table is full, need to clear a spot for the next round
			if(codeTable.size()>=55296)
			{
				// run through everything in the pq until you hit something that matches the latest time we've seen that string (and isn't initialized)
				// ie if the last time we've seen "ab" is 10, and we run into "ab", 5, ignore it bc it's not the most updated
				Pair1 x = pq.poll();

				while(!codeTable.containsKey((int)x.getWord()) || !latest.containsKey(codeTable.get((int)x.getWord())) || latest.get(codeTable.get((int)x.getWord())) != x.getTimes() || (int)x.getWord() < 128)
				{
					x = pq.poll();
				}

				// clear everything, set place
				latest.remove(codeTable.get((int)x.getWord()));
				codeTable.remove((int)x.getWord());
				place = (int)x.getWord()-1;
			}
			index++;
			place++;
			
		}
		bw.write(decoded.toString());
		br.close();
		bw.close();
		
	}

}