import java.io.*;
import java.lang.*;
public class lzwEncodingTester
{
	public static void main(String[] args) throws IOException
	{
		long start = System.currentTimeMillis();
		//Encoder test = new Encoder();
		//test.encode("lzw.txt");
		lzwEncoding test = new lzwEncoding();
		test.encode("lzw.txt", "lzwEncoded.txt");

		//riEncoder test = new riEncoder();
		//test.encode("lzw.txt");

		long end = System.currentTimeMillis();
		System.out.println(end-start);

		System.out.println();
		System.out.println("-----------------------------------------------------------------------------------");
		System.out.println();
		
		start = System.currentTimeMillis();
		//riDecoder test1 = new riDecoder();
		//test1.decode("lzwEncoded.txt", "lzwNew.txt");
		lzwDecode test1 = new lzwDecode();
		test1.decode("lzwEncoded.txt", "lzwNew.txt");
		end = System.currentTimeMillis();
		System.out.println(end-start);
	}
}