package rebound.formats.bitpack;

import static rebound.formats.bitpack.BitpackCore.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;

public class Bitpack
{
	public static int getLengthIfDecoded(ByteList encoded)
	{
		if (encoded.isEmpty())
		{
			return 0;
		}
		else
		{
			int remainder = getEncodedRemainder(encoded);
			
			int x = remainder >= 6 ? 1 : 0;  //at the low-level x can just = (remainder >= 6)  ;D
			
			return (encoded.size()-(x+1)) * 8 + remainder;
		}
	}
	
	
	@ImplementationTransparency
	public static int getEncodedRemainder(ByteList b)
	{
		int n = b.size();
		return n == 0 ? 0 : ((b.get(n-1) & 0xE0) >>> 5); 
	}
	
	
	
	
	
	
	
	
	public static void encodeAppendingIntoList(@ReadonlyValue BooleanList unencoded, @WritableValue ByteList encoded)
	{
		int numberOfBits = unencoded.size();
		
		if (numberOfBits != 0)
		{
			int nWholeBytes = numberOfBits / 8;  //flooring division!
			
			for (int i = 0; i < nWholeBytes; i++)
			{
				byte b = 0;
				
				int base = i*8;
				
				for (int j = 7; j >= 0; j--)
				{
					b |= unencoded.get(base+j) ? 1 : 0;
					b <<= 1;
				}
				
				encoded.add(b);
			}
			
			
		}
	}
	
	public static void decodeAppendingIntoList(@ReadonlyValue ByteList encoded, @WritableValue BooleanList unencoded)
	{
		//TODO
	}
	
	
	public static void encodeSettingIntoPresizedList(@ReadonlyValue BooleanList unencoded, @WritableValue ByteList encoded)
	{
		//TODO
	}
	
	public static void decodeSettingIntoPresizedList(@ReadonlyValue ByteList encoded, @WritableValue BooleanList unencoded)
	{
		//TODO
	}
	
	
	
	
	
	
	
	
	
	public static ByteList encodeToNewList(@ReadonlyValue BooleanList unencoded)
	{
		byte[] a = new byte[getLengthIfEncoded(unencoded.size())];
		ByteList l = byteArrayAsList(a);
		encodeSettingIntoPresizedList(unencoded, l);
		return l;
	}
	
	public static byte[] encodeToNewArray(@ReadonlyValue BooleanList unencoded)
	{
		byte[] a = new byte[getLengthIfEncoded(unencoded.size())];
		ByteList l = byteArrayAsList(a);
		encodeSettingIntoPresizedList(unencoded, l);
		return a;
	}
	
	
	public static BooleanList decodeToNewList(@ReadonlyValue ByteList encoded)
	{
		BooleanList unencoded = newBooleanListZerofilled(getLengthIfDecoded(encoded));
		decodeSettingIntoPresizedList(encoded, unencoded);
		return unencoded;
	}
	
	public static boolean[] decodeToNewArray(@ReadonlyValue ByteList encoded)
	{
		return decodeToNewList(encoded).toBooleanArrayPossiblyLive();
	}
}
