package rebound.bits;

import static rebound.util.collections.BasicCollectionUtilities.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import rebound.io.iio.BasicInputByteStream;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.PairOrdered;
import rebound.util.collections.Slice;

//Todo manifest Bytereluctant Max3 and Max2 ones here :3
//Todo implement the actual general-case formulas in case people want to use 6-bits instead of 8-bits or something (like how base64 does? ;D) or even non-binary things!! like base96 in printable ascii! or base127 (not base128 but soclose!) in ascii-without-nul-chars!

/**
 * The documents and notes are quite extensive and stored (not in this code repo) under /uuid/d8e6449c-dd6e-4348-985c-cbf5c6b0e2ce  :3
 */
public class VariableLengthIntegerTranscodingSchemes
{
	public static int readAndDecodeBytereluctantVariableLengthIntegerMax4(InputStream in) throws IOException, EOFException
	{
		byte byte0 = Bytes.getByte(in);
		
		if (!decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte0, 0, 4))
			return decodeBytereluctantVariableLengthInteger1(byte0);
		
		byte byte1 = Bytes.getByte(in);
		
		if (!decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte1, 1, 4))
			return decodeBytereluctantVariableLengthInteger2(byte0, byte1);
		
		byte byte2 = Bytes.getByte(in);
		
		if (!decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte2, 2, 4))
			return decodeBytereluctantVariableLengthInteger3(byte0, byte1, byte2);
		
		byte byte3 = Bytes.getByte(in);
		
		return decodeBytereluctantVariableLengthInteger4Terminal(byte0, byte1, byte2, byte3);
	}
	
	
	
	
	public static int readAndDecodeBytereluctantVariableLengthIntegerMax4(BasicInputByteStream in) throws IOException, EOFException
	{
		byte byte0 = Bytes.getByte(in);
		
		if (!decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte0, 0, 4))
			return decodeBytereluctantVariableLengthInteger1(byte0);
		
		byte byte1 = Bytes.getByte(in);
		
		if (!decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte1, 1, 4))
			return decodeBytereluctantVariableLengthInteger2(byte0, byte1);
		
		byte byte2 = Bytes.getByte(in);
		
		if (!decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte2, 2, 4))
			return decodeBytereluctantVariableLengthInteger3(byte0, byte1, byte2);
		
		byte byte3 = Bytes.getByte(in);
		
		return decodeBytereluctantVariableLengthInteger4Terminal(byte0, byte1, byte2, byte3);
	}
	
	
	
	
	/**
	 * @return (the decoded value, the remainder of the data)
	 * @throws EOFException if it was too short!
	 */
	public static PairOrdered<Integer, Slice<byte[]>> decodeBytereluctantVariableLengthIntegerMax4(Slice<byte[]> encodedForm) throws EOFException
	{
		int length = encodedForm.getLength();
		
		if (length < 1)  throw new EOFException("Expected at least one byte no matter what!!");
		byte byte0 = ArrayUtilities.getByte(encodedForm, 0);
		
		if (!decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte0, 0, 4))
			return pair(decodeBytereluctantVariableLengthInteger1(byte0), encodedForm.subsliceToEnd(1));
		
		if (length < 2)  throw new EOFException("Expected at least 2 bytes but only got 1");
		byte byte1 = ArrayUtilities.getByte(encodedForm, 1);
		
		if (!decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte1, 1, 4))
			return pair(decodeBytereluctantVariableLengthInteger2(byte0, byte1), encodedForm.subsliceToEnd(2));
		
		if (length < 2)  throw new EOFException("Expected at least 3 bytes but only got 2");
		byte byte2 = ArrayUtilities.getByte(encodedForm, 2);
		
		if (!decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte2, 2, 4))
		return pair(decodeBytereluctantVariableLengthInteger3(byte0, byte1, byte2), encodedForm.subsliceToEnd(3));
		
		if (length < 2)  throw new EOFException("Expected at least 4 bytes but only got 3");
		byte byte3 = ArrayUtilities.getByte(encodedForm, 3);
		
		return pair(decodeBytereluctantVariableLengthInteger4Terminal(byte0, byte1, byte2, byte3), encodedForm.subsliceToEnd(4));
	}
	
	
	
	
	
	/**
	 * This to tell whether to read one more byte and, if so, which of these kind of methods to call :3
	 * (Example here is for maxBytes = 4)
	 * • {@link #decodeBytereluctantVariableLengthInteger1(byte)}
	 * • {@link #decodeBytereluctantVariableLengthInteger2(byte, byte)}
	 * • {@link #decodeBytereluctantVariableLengthInteger3(byte, byte, byte)}
	 * • {@link #decodeBytereluctantVariableLengthInteger4Terminal(byte, byte, byte, byte)}
	 */
	public static boolean decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte byteValue, int byteIndex, int maxBytes)
	{
		return byteIndex < (maxBytes-1) ? ((byteValue & 0x80) != 0) : false;
	}
	
	public static boolean decodeBytereluctantVariableLengthIntegerInfinite_HasAnotherByte(byte byteValue)
	{
		return (byteValue & 0x80) != 0;
	}
	
	
	/**
	 * Use {@link #decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte, int, int)} to tell which of these to call :3
	 */
	public static int decodeBytereluctantVariableLengthInteger1(byte byte0)
	{
		// For bytereluctant form:
		// 		N = 8
		// 		n = N - 1 = 7
		// 		B = 2^n = 2^7 = 128
		
		// For this method:
		//		l = 1
		
		// v(l) = ((B^l - 1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, l-1)
		// v(1) = ((B^1 - 1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, 1-1)
		// v(1) = ((B-1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, 0)
		// v(1) = (1 - 1)  +  sum(x[i]*B^i, i, 0, 0)
		// v(1) = sum(x[i]*B^i, i, 0, 0)
		// v(1) = x[0]*B^0
		// v(1) = x[0]
		// v(1) = a
		
		int a = (int)byte0;  //it can't have its high bit (0x80) set because this was already determined to be the last byte in order to figure out this was the method to call, which means the high bit is zero so we don't have to worry about masking it out or it causing sign-extension in Java casting! :3
		return a;  //just what it is, the same in both of them; 0 operations XD
	}
	
	
	/**
	 * Use {@link #decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte, int, int)} to tell which of these to call :3
	 */
	public static int decodeBytereluctantVariableLengthInteger2(byte byte0, byte byte1)
	{
		// For bytereluctant form:
		// 		N = 8
		// 		n = N - 1 = 7
		// 		B = 2^n = 2^7 = 128
		
		// For this method:
		//		l = 2
		
		// v(l) = ((B^l - 1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, l-1)
		// v(2) = ((B^2 - 1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, 2-1)
		// v(2) = ((B^2 - 1)/(B-1) - 1)  +  x[0]*B^0 + x[1]*B^1
		// v(2) = ((B^2 - 1)/(B-1) - 1)  +  x[0] + x[1]*B
		// v(2) = ((B-1)*(B+1)/(B-1) - 1)  +  x[0] + x[1]*B
		// v(2) = ((B+1) - 1)  +  x[0] + x[1]*B
		// v(2) = B + x[0] + x[1]*B
		// v(2) = B + a + b*B
		// v(2) = 128 + a + b*2^7
		// v(2) = 128 + a + b << 7
		// v(2) = a | (b << 7 + 128)    //because all of those are multiples of 2^7 which is the exclusive max of 'a' and so their low 7 bits are zero, so addition and inclusive OR are the same :>  (and inclusive OR is a simpler operation in binary and so could be faster X3 )
		// v(2) = a | ((b + 1) << 7)  //same thing; ahaha oh wait, it's the same as the other form (x[0] + sum((x[i]+1)*B^i, i, 0, l-1))
		
		int a = byte0 & 0x7F;
		int b = (int)byte1;  //it can't have its high bit (0x80) set because this was already determined to be the last byte in order to figure out this was the method to call, which means the high bit is zero so we don't have to worry about masking it out or it causing sign-extension in Java casting! :3
		
		return   a | ((b + 1) << 7);    //3 operations (but "increment" is simpler and perhaps slightly faster than general addition X3 )
		//return a | ((b << 7) + 128);  //3 operations
	}
	
	
	/**
	 * Use {@link #decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte, int, int)} to tell which of these to call :3
	 */
	public static int decodeBytereluctantVariableLengthInteger3(byte byte0, byte byte1, byte byte2)
	{
		// For bytereluctant form:
		// 		N = 8
		// 		n = N - 1 = 7
		// 		B = 2^n = 2^7 = 128
		
		// For this method:
		//		l = 3
		
		// v(l) = ((B^l - 1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, l-1)
		// v(3) = ((B^3 - 1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, 3-1)
		// v(3) = ((B^3 - 1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, 2)
		// v(3) = ((B^3 - 1)/(B-1) - 1)  +  x[0]*B^0 + x[1]*B^1 + x[2]*B^2
		// v(3) = ((B^3 - 1)/(B-1) - 1)  +  x[0] + x[1]*B + x[2]*B^2
		// v(3) = ((128^3 - 1)/(128-1) - 1)  +  a + b*B + c*B^2
		// v(3) = 16512 + a + b*B + c*B^2
		// v(3) = 16512 + a + b*2^7 + c*2^(7*2)
		// v(3) = 16512 + a + b*2^7 + c*2^14
		// v(3) = 16512 + a + b << 7 + c << 14
		// v(3) = a | (b << 7 + c << 14 + 16512)    //because all of those are multiples of 2^7 which is the exclusive max of 'a' and so their low 7 bits are zero, so addition and inclusive OR are the same :>  (and inclusive OR is a simpler operation in binary and so could be faster X3 )
		// v(3) = a | ((b + c << 7 + 129) << 7)  //same thing; idk, just for posterity, you could do it this way X3
		
		int a = byte0 & 0x7F;
		int b = byte1 & 0x7F;
		int c = (int)byte2;  //it can't have its high bit (0x80) set because this was already determined to be the last byte in order to figure out this was the method to call, which means the high bit is zero so we don't have to worry about masking it out or it causing sign-extension in Java casting! :3
		
		//return a | ((b + 1) << 7) + ((c + 1) << 14);  //6 operations
		return   a | ((b << 7) + (c << 14) + 16512);    //5 operations
	}
	
	
	/**
	 * Use {@link #decodeBytereluctantVariableLengthIntegerFinite_HasAnotherByte(byte, int, int)} to tell which of these to call :3
	 */
	public static int decodeBytereluctantVariableLengthInteger4Terminal(byte byte0, byte byte1, byte byte2, byte byte3)
	{
		// For bytereluctant form:
		// 		N = 8
		// 		n = N - 1 = 7
		// 		B = 2^n = 2^7 = 128
		
		// For this method:
		//		l = 4
		
		// v(l) = ((B^l - 1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, l-1)
		// v(4) = ((B^4 - 1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, 4-1)
		// v(4) = ((B^4 - 1)/(B-1) - 1)  +  sum(x[i]*B^i, i, 0, 3)
		// v(4) = ((B^4 - 1)/(B-1) - 1)  +  x[0]*B^0 + x[1]*B^1 + x[2]*B^2 + x[3]*B^3
		// v(4) = ((B^4 - 1)/(B-1) - 1)  +  x[0] + x[1]*B + x[2]*B^2 + x[3]*B^3
		// v(4) = ((B^4 - 1)/(B-1) - 1)  +  a + b*B + c*B^2 + d*B^3
		// v(4) = ((128^4 - 1)/(128-1) - 1)  +  a + b*2^7 + c*2^7^2 + d*2^7^3
		// v(4) = ((128^4 - 1)/(128-1) - 1)  +  a + b*2^7 + c*2^(7*2) + d*2^(7*3)
		// v(4) = 2113664  +  a + b*2^7 + c*2^(7*2) + d*2^(7*3)
		// v(4) = 2113664  +  a + b << 7 + c << (7*2) + d << (7*3)
		// v(4) = 2113664  +  a  +  b << 7  +  c << 14  +  d << 21
		// v(4) = a  |  (b << 7  +  c << 14  +  d << 21  +  2113664)    //because all of those are multiples of 2^7 which is the exclusive max of 'a' and so their low 7 bits are zero, so addition and inclusive OR are the same :>  (and inclusive OR is a simpler operation in binary and so could be faster X3 )
		// v(4) = a  |  ((b  +  c << 7  +  d << 14  +  16513) << 7)  //same thing; idk, just for posterity, you could do it this way X3
		
		int a = byte0 & 0x7F;
		int b = byte1 & 0x7F;
		int c = byte2 & 0x7F;
		int d = byte3 & 0xFF;  //it *can* have its high bit set because it's the terminal one!!  but we include that one as data so we want to mask it in not out X3 but in Java this is the simplest/standardest way to use a byte as an unsigned integer (mask it with 255/0xFF during its implicit upcasting into (signed) 32-bit to avoid sign-extension X3'' )
		
		//return a | ((b + 1) << 7) + ((c + 1) << 14) + ((d + 1) << 21);  //9 operations
		return   a | ((b << 7) + (c << 14) + (d << 21) + 2113664);        //7 operations
	}
	
	
	
	
	public static byte[] encodeBytereluctantVariableLengthIntegerMax4(int value) throws IllegalArgumentException
	{
		int v = value;
		
		if (v < 0) throw new IllegalArgumentException();  //Who knows if they intend for it to be seen as negative or unsigned and REALLY BIG; either way, that's not okay XD'
		if (v > BytereluctantVariableLengthIntegerMax4_MaxValue) throw new IllegalArgumentException();  //different line number than if it's negative X3
		
		// For bytereluctant form:
		// 		B = 2^7 = 128
		// 		M = 4
		
		if (v <= 127)
		{
			// i = 0
			// 		x[i] = ((v - sum(B^j,j,1,i-1))/B^i - (i = 0 ? 0 : 1)) % ((i = M-1) ? 2*B : B)
			// 		x[0] = ((v - sum(B^j,j,1,0-1))/B^i - 0) % (B)
			// 		x[0] = ((v - 0)/B^i - 0) % (B)
			// 		x[0] = (v/B^0) % B
			// 		x[0] = v % B
			// 		x[0] = v       //it's the last one in the list so there's no need for a modulus; it just won't be >= B X3
			// 		a = v
			
			int a = v;
			
			return new byte[]{(byte)a};
		}
		
		else if (v <= 16511)
		{
			// i = 0
			// 		x[i] = ((v - sum(B^j,j,1,i-1))/B^i - (i = 0 ? 0 : 1)) % ((i = M-1) ? 2*B : B)
			// 		x[0] = ((v - sum(B^j,j,1,0-1))/B^i - 0) % (B)
			// 		x[0] = ((v - 0)/B^i - 0) % (B)
			// 		x[0] = (v/B^0) % B
			// 		x[0] = v % B
			// 		a = v % B
			// 		a = v % 2^7
			// 		a = v & (2^7-1)   //AND is simpler than modulus and I actually can confirm it's faster on some CPU architectures!  (maybe not modern ones idk but still X3 )
			// 		a = v & 0x7F
			
			// i = 1
			// 		x[i] = ((v - sum(B^j,j,1,i-1))/B^i - (i = 0 ? 0 : 1)) % ((i = M-1) ? 2*B : B)
			// 		x[1] = ((v - sum(B^j,j,1,1-1))/B^1 - (1 = 0 ? 0 : 1)) % ((1 = 4-1) ? 2*B : B)
			// 		x[1] = ((v - sum(B^j,j,1,0))/B^1 - (1)) % (B)
			// 		x[1] = ((v - 0)/B^1 - 1) % B
			// 		x[1] = (v/B - 1) % B
			// 		x[1] = v/B - 1       //it's the last one in the list so there's no need for a modulus; it just won't be >= B X3
			// 		b = v/2^7 - 1
			// 		b = v >>> 7 - 1   //Shift-Down is simpler than division and it can be faster :3
			
			int a = v & 0x7F;
			int b = (v >>> 7) - 1;
			
			//Don't forget to add in the There's-More-Bytes high-bits! XD
			a |= 0x80;
			
			return new byte[]{(byte)a, (byte)b};
		}
		
		else if (v <= 2113663)
		{
			// i = 0
			// 		x[i] = ((v - sum(B^j,j,1,i-1))/B^i - (i = 0 ? 0 : 1)) % ((i = M-1) ? 2*B : B)
			// 		x[0] = ((v - sum(B^j,j,1,0-1))/B^i - 0) % (B)
			// 		x[0] = ((v - 0)/B^i - 0) % (B)
			// 		x[0] = (v/B^0) % B
			// 		x[0] = v % B
			// 		a = v % B
			// 		a = v % 2^7
			// 		a = v & (2^7-1)   //AND is simpler than modulus and I actually can confirm it's faster on some CPU architectures!  (maybe not modern ones idk but still X3 )
			// 		a = v & 0x7F
			
			// i = 1
			// 		x[i] = ((v - sum(B^j,j,1,i-1))/B^i - (i = 0 ? 0 : 1)) % ((i = M-1) ? 2*B : B)
			// 		x[1] = ((v - sum(B^j,j,1,1-1))/B^1 - (1 = 0 ? 0 : 1)) % ((1 = 4-1) ? 2*B : B)
			// 		x[1] = ((v - sum(B^j,j,1,0))/B^1 - (1)) % (B)
			// 		x[1] = ((v - 0)/B^1 - 1) % B
			// 		x[1] = (v/B - 1) % B
			// 		b = (v/B - 1) % B
			// 		b = (v/2^7 - 1) % 2^7
			// 		b = (v >>> 7 - 1) % 2^7
			// 		b = (v >>> 7 - 1) & (2^7-1)
			// 		b = (v >>> 7 - 1) & 0x7F
			
			// i = 2
			// 		x[i] = ((v - sum(B^j,j,1,i-1))/B^i - (i = 0 ? 0 : 1)) % ((i = M-1) ? 2*B : B)
			// 		x[2] = ((v - sum(B^j,j,1,2-1))/B^2 - (2 = 0 ? 0 : 1)) % ((2 = 4-1) ? 2*B : B)
			// 		x[2] = ((v - sum(B^j,j,1,1))/B^2 - (1)) % (B)
			// 		x[2] = ((v - B^1)/B^2 - 1) % B
			// 		x[2] = ((v - B)/B^2 - 1) % B
			// 		x[2] = (v - B)/B^2 - 1       //it's the last one in the list so there's no need for a modulus; it just won't be >= B X3
			// 		c = (v - B)/B^2 - 1
			// 		c = (v - 2^7)/(2^7)^2 - 1
			// 		c = (v - 2^7)/(2^(7*2)) - 1
			// 		c = (v - 128)/(2^14) - 1
			// 		c = (v - 128) >>> 14 - 1
			
			int a = v & 0x7F;
			int b = ((v >>> 7) - 1) & 0x7F;
			int c = ((v - 128) >>> 14) - 1;
			
			//Don't forget to add in the There's-More-Bytes high-bits! XD
			a |= 0x80;
			b |= 0x80;
			
			return new byte[]{(byte)a, (byte)b, (byte)c};
		}
		
		else
		{
			// i = 0
			// 		x[i] = ((v - sum(B^j,j,1,i-1))/B^i - (i = 0 ? 0 : 1)) % ((i = M-1) ? 2*B : B)
			// 		x[0] = ((v - sum(B^j,j,1,0-1))/B^i - 0) % (B)
			// 		x[0] = ((v - 0)/B^i - 0) % (B)
			// 		x[0] = (v/B^0) % B
			// 		x[0] = v % B
			// 		a = v % B
			// 		a = v % 2^7
			// 		a = v & (2^7-1)   //AND is simpler than modulus and I actually can confirm it's faster on some CPU architectures!  (maybe not modern ones idk but still X3 )
			// 		a = v & 0x7F
			
			// i = 1
			// 		x[i] = ((v - sum(B^j,j,1,i-1))/B^i - (i = 0 ? 0 : 1)) % ((i = M-1) ? 2*B : B)
			// 		x[1] = ((v - sum(B^j,j,1,1-1))/B^1 - (1 = 0 ? 0 : 1)) % ((1 = 4-1) ? 2*B : B)
			// 		x[1] = ((v - sum(B^j,j,1,0))/B^1 - (1)) % (B)
			// 		x[1] = ((v - 0)/B^1 - 1) % B
			// 		x[1] = (v/B - 1) % B
			// 		b = (v/B - 1) % B
			// 		b = (v/2^7 - 1) % 2^7
			// 		b = (v >>> 7 - 1) % 2^7
			// 		b = (v >>> 7 - 1) & (2^7-1)
			// 		b = (v >>> 7 - 1) & 0x7F
			
			// i = 2
			// 		x[i] = ((v - sum(B^j,j,1,i-1))/B^i - (i = 0 ? 0 : 1)) % ((i = M-1) ? 2*B : B)
			// 		x[2] = ((v - sum(B^j,j,1,2-1))/B^2 - (2 = 0 ? 0 : 1)) % ((2 = 4-1) ? 2*B : B)
			// 		x[2] = ((v - sum(B^j,j,1,1))/B^2 - (1)) % (B)
			// 		x[2] = ((v - B^1)/B^2 - 1) % B
			// 		x[2] = ((v - B)/B^2 - 1) % B
			// 		c = ((v - B)/B^2 - 1) % B
			// 		c = ((v - 2^7)/(2^7)^2 - 1) % 2^7
			// 		c = ((v - 128)/2^(7*2) - 1) & (2^7-1)
			// 		c = ((v - 128)/2^14 - 1) & (2^7-1)
			// 		c = ((v - 128) >>> 14 - 1) & 0x7F
			
			// i = 3
			// 		x[i] = ((v - sum(B^j,j,1,i-1))/B^i - (i = 0 ? 0 : 1)) % ((i = M-1) ? 2*B : B)
			// 		x[3] = ((v - sum(B^j,j,1,3-1))/B^3 - (3 = 0 ? 0 : 1)) % ((3 = 4-1) ? 2*B : B)
			// 		x[3] = ((v - sum(B^j,j,1,3-1))/B^3 - (3 = 0 ? 0 : 1)) % ((3 = 3) ? 2*B : B)
			// 		x[3] = ((v - sum(B^j,j,1,2))/B^3 - (3 = 0 ? 0 : 1)) % ((3 = 3) ? 2*B : B)
			// 		x[3] = ((v - sum(B^j,j,1,2))/B^3 - (1)) % (2*B)
			// 		x[3] = ((v - B^1 - B^2)/B^3 - (1)) % (2*B)
			// 		x[3] = ((v - B^1 - B^2)/B^3 - 1) % (2*B)
			// 		x[3] = ((v - B - B^2)/B^3 - 1) % (2*B)
			// 		x[3] = (v - B - B^2)/B^3 - 1            //it's the last one in the list (indeed in the whole scheme!) so there's no need for a modulus; it just won't be >= B X3
			// 		d = (v - B - B^2)/B^3 - 1
			// 		d = (v - 128 - 128^2)/(2^7)^3 - 1
			// 		d = (v - 128 - 128^2)/2^(7*3) - 1
			// 		d = (v - 128 - 128^2)/2^21 - 1
			// 		d = (v - 16512)/2^21 - 1
			// 		d = (v - 16512) >>> 21 - 1
			
			int a = v & 0x7F;
			int b = ((v >>> 7) - 1) & 0x7F;
			int c = (((v - 128) >>> 14) - 1) & 0x7F;
			int d = ((v - 16512) >>> 21) - 1;
			
			//Don't forget to add in the There's-More-Bytes high-bits! XD
			a |= 0x80;
			b |= 0x80;
			c |= 0x80;
			
			return new byte[]{(byte)a, (byte)b, (byte)c, (byte)d};
		}
	}
	
	
	public static final int BytereluctantVariableLengthIntegerMax1_MaxValue = 256;   //That's just exactly a single u8 (since the high bit *is* used on the terminal group), nothing special XD
	public static final int BytereluctantVariableLengthIntegerMax2_MaxValue = 32896;  //A little over 15 bits :3
	public static final int BytereluctantVariableLengthIntegerMax3_MaxValue = 4210816;  //A little over 22 bits :3
	public static final int BytereluctantVariableLengthIntegerMax4_MaxValue = 538984575;  //A little over 29 bits :3
}
