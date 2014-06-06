package com.websqrd.libs.crypt;

//import com.websqrd.fastcat.ir.io.CharVector;

public class HashFunctions
{
	private final static int keyA = 63689;
	private final static int keyB = 378551;
	
	public static int hash(char[] data, int offset, int datalength, int bucketSize)
	{
		return rsHash(data, offset, datalength, bucketSize);
	}
	
//	public static int  hash(CharVector term, int bucketSize)
//	{
//		return rsHash(term, bucketSize);
//	}
	
	public static int hash(String term, int bucketSize)
	{
		return rsHash(term, bucketSize);
	}
	
	public static int hash(byte[] data, int offset, int datalength, int bucketSize)
	{
		return rsHash(data, offset, datalength, bucketSize);
	}
	
	
	public static int rsHash(String term, int bucketSize) {
		int b = keyB;
		int a = keyA;
		int hashValue = 0;

		for(int i=0; i < term.length(); i++) {
			hashValue = hashValue * a + term.charAt(i);
			a = a * b;
		}
		return hashValue & (bucketSize - 1);
	}
	
	public static int rsHash(byte[] data, int offset,  int datalength, int bucketSize)
	{
		int b = keyB;
		int a = keyA;
		int hashValue = 0;
		int len = offset + datalength;

		for (int i = offset; i < len; i++) {
			hashValue = hashValue * a + (data[i] & 0xff);
			a = a * b;
		}
		return hashValue & (bucketSize - 1);	
	}

	public static int rsHash(char[] data, int offset,  int datalength, int bucketSize)
	{
		int b = keyB;
		int a = keyA;
		int hashValue = 0;
		int len = offset + datalength;

		for (int i = offset; i < len; i++) {
			hashValue = hashValue * a + data[i];
			a = a * b;
		}
		return hashValue & (bucketSize - 1);	
	}
	
//	public static int rsHash(CharVector term, int bucketSize) {
//		return rsHash(term.array, term.start, term.length, bucketSize);
//	}
	
//	public static int apHash(CharVector term, int bucketSize)
//	{
//		return apHash(term.array, term.start, term.length, bucketSize);
//	}
	
	public static int apHash(char[] data, int offset, int datalength, int bucketSize)
	{
		int hash = 0;
		int len = offset + datalength;
		for ( int i = offset ; i < len ; i ++ )
		{
			hash ^= ((i & 1)== 0) ? ((hash << 7) ^ data[i] ^ (hash>> 3)) :
				                                     (~((hash << 11)^data[i] ^ (hash >> 5)));
		}
		return (hash & (bucketSize-1));
	}
}
