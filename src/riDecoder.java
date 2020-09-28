/*
	least recently used
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class riDecoder {
	/*
		EDIT 1:
		Changed to char, string so we can use utf more easily
	*/
	private HashMap<Character, String> codeTable;
	public riDecoder () { 
		 codeTable = new HashMap<Character,String>();
		 /*
		 	EDIT 2:
		 	In order to work with Lily and Isa's encoder, it can only init up to 128
		 */
		for (int i = 0; i < 128; i++) { //creates table of characters
			codeTable.put((char)i, (char)i + "");
		}
	}
	public void decode(String inputFile, String outputFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader (inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter (outputFile));
		
		/*
			starts at 128
		*/
		int index=128;
		int currentValue = br.read();
		String previous = codeTable.get((char)currentValue)+"";
		/*
			EDIT 3:
			Missing the first char of the previous block, used for the special case when LZW doesn't work + adding to the table
		*/
		String prevChar = "";
		//System.out.println((char)currentValue+"-"+previous);
		String currentString = "";
		/*
			EDIT 4:
			Can't change strings, so they're hella slow
			Added StringBuilder (or just print, but I like stringbuilders)
		*/
		StringBuilder decoded = new StringBuilder("");
		decoded.append(previous);
		
		while (br.ready()) 
		{
			currentValue = br.read();
			// if combination exists 
			if (codeTable.containsKey((char)currentValue))
			{ 
				currentString = codeTable.get((char)currentValue);
				String toAdd = previous + currentString.charAt(0);// previous combination + first letter of the one just read
				/*
					EDIT 5: 
					Should never occur, but just to be safe
					max should be 65536 bc in utf - 8 1 char is maxxed at 65536, but bc of some weird utf rule, we're capped at 55296
				*/
				if(index < 55296)
					codeTable.put((char)index, toAdd );
				index++;
				decoded.append(currentString);
				
			}
			else 
			{
				/*
					need to use prevChar to constructed the missing word
				*/
				currentString = previous + prevChar;
				//currentString = previous + previous.charAt(0);
				/* 
					Should never occur, but just to be safe
					max should be 65536 bc in utf - 8 1 char is maxxed at 65536, but bc of some weird utf rule, we're capped at 55296
				*/
				if(index < 55296)
					codeTable.put((char)index, currentString);
				decoded.append(currentString);
				index++;
			}
			/*
				need to mark down the first char of the prev block
			*/
			prevChar = currentString.charAt(0)+"";
			previous=codeTable.get((char)currentValue);
			
		}
		
		bw.write(decoded.toString());
		br.close();
		bw.close();
		
	}

}