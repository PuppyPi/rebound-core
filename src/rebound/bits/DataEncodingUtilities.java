/*
 * Created on Jun 18, 2009
 * 	by the great Eclipse(c)
 */
package rebound.bits;

import static rebound.text.StringUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.ByteListRO;
import rebound.util.collections.prim.PrimitiveCollections.CharacterList;
import rebound.util.collections.prim.PrimitiveCollections.CharacterListRO;
import rebound.util.objectutil.JavaNamespace;

/**
 * This class provides encoding and decoding utilities for common data-to-text encodings.
 * Supported encodings currently include:
 * <ul>
 * 	<li>Hexadecimal</li>
 * 	<li>Quoted-Printable (decode only)</li>
 * </ul>
 * 
 * On the todo list are:
 * <ul>
 * 	<li>Todo Base64</li>
 * 	<li>Todo Quoted-Printable (encode)</li>
 * 	<li>Todo ASCII 85</li>
 * 	<li>Todo uuencode</li>
 * 	<li>Todo Base96 (<b>unstandardized</b>)</li>
 * </ul>
 * 
 * <p>Fixed-overhead (hex,base64,...) decoding methods have the signature <code>void decode<i>Format</i>_<i>variant</i>(char[] source, int sourceOffset, int sourceBound, byte[] dest, int destOffset, int length, <i>format parameters</i>)</code><br>
 * Decoders for variable-overhead (QP,...) formats have the signature <code>byte[] decode<i>Format</i>_<i>variant</i>(char[] source, int offset, int length, <i>format parameters</i>)</code><br>
 * <code>source</code> and <code>dest</code> are obviously the source text and destination buffer with respective offsets.
 * <code>sourceBound</code> is the boundary at which decoding must not extend past.  (ie, the index of the first character outside the provided range)
 * <code>length</code> is the number of bytes to extract.
 * The different parameters for variable-overhead are the standard Java array,offset,length triplet for source[].
 * A <code>TextSyntaxException</code> is thrown if the text could not be successfully decoded.
 * {@link IllegalArgumentException} may be thrown for invalid format parameters, but check is not guaranteed.
 * Note: The first decode method for a format stores the documentation specific to that format (eg, any parameters or variants).
 * Convenience wrappers for decode are:
 * <ul>
 * 	<li><code>public static byte[] decode<i>Format</i>_<i>variant</i>(String source, <i>format parameters</i>) throws SyntaxException</code></li>
 * 	<li><code>public static byte[] decode<i>Format</i>_<i>variant</i>(char[] source, int offset, int length, </i>format parameters</i>) throws SyntaxException</code> (for fixed-overhead)</li>
 * </ul>
 * Where sourceOffset &lt;-- offset, and sourceBound &lt;-- offset+length.
 * 
 * <p>Fixed-overhead encoding methods have the signature <code>void encode<i>Format</i>_<i>variant</i>(byte[] source, int sourceOffset, char[] dest, int destOffset, int destBound, int length, <i>format parameters</i>)</code><br>
 * And for variable-length formats, <code>char[] encode<i>Format</i>_<i>variant</i>(byte[] source, int offset, int length, <i>format parameters</i>)</code><br>
 * All parameters are the same as Decode, except that destBound takes the place of sourceBound.
 * <code>TextSyntaxException</code>s are never thrown by encoding methods (though {@link IllegalArgumentException} may still be thrown for invalid format parameters).
 * Convenience wrappers for encode are:
 * <ul>
 * 	<li><code>public static String encode<i>Format</i>_<i>variant</i>(byte[] source, </i>format parameters</i>)</code></li>
 * 	<li><code>public static String encode<i>Format</i>_<i>variant</i>(byte[] source, int sourceOffset, int length, </i>format parameters</i>)</code></li>
 * </ul>
 * 
 * 
 * <p>Variants are to be used in place of parameters only when there are a small number of permutations (eg, Uppercase/Lowercase, not Alphabet) and the performance penalty for parameters would be high, even if not used.<br>
 * Example: Hex encode uses a parameter for text-case rather than a variant because the application of the case introduces only a single extra operation: <code>c |= hexcase;</code> (the constant values are chosen such that this works).
 * Decoding should be as lenient as possible while not incurring similarly too-high performance penalties.<br>
 * Example: Hex decode does not require that the expected case be specified ahead of time because normalizing the case also takes a single operation. (<code>c &= ~0x20;</code>)
 * Example: Hex decode does not automatically scan for delimiters on each iteration because that would introduce a penalty of greater than O(1) time, and scanning just the first delimiter is not robust.
 * 
 * <p>Two extra utility methods are included for some formats to calculate one length from another.
 * Length converters are only included for algorithms whose lengths can be converted in constant time and without access to the source.
 * <code>public static int get<i>Format</i>DatalenFromTextlen_<i>variant</i>(int textLength, </i>format parameters</li>)</code>
 * <code>public static int get<i>Format</i>TextlenFromDatalen_<i>variant</i>(int dataLength, </i>format parameters</li>)</code>
 * If the length that is to be returned is ambiguous from the given parameters then the documentation will define the results, though usually it returns the smallest of the possibilities.
 * 
 * <h2>Efficiency of encoding formats</h2>
 * The data is assumed to be in the worst case, but parameters are assumed to be in best general case (eg, no delimiters in Hex)<br>
 * Overhead = (TextLength / DataLength * 100) - 100:<br>
 * <table border="1">
 * 	<tr><th>Format</th><th>Overhead</th><th>Assumptions</th></tr>
 * 	<tr><td>Base96 (<b>unstandardized</b>)</td><td>14.3%</td><th>No line breaks</th></tr>
 * 	<tr><td>ASCII 85</td><td>25.0%</td><th>TODO Check this</th></tr>
 * 	<tr><td>Base64</td><td>33.3%</td><th>No line breaks</th></tr>
 * 	<tr><td>uuencode</td><td>39.7%</td><th>All lines of the maximum length (63 working chars) TODO Is it allowed to not have line breaks?, Is it allowed to have 0x0A-only line breaks?</th></tr>
 * 	<tr><td>Hexadecimal</td><td>100.0% (2x original size)</td><th>No delimiters</th></tr>
 * 	<tr><td>Quoted-Printable</td><td>200.0% (3x original size)</td><th>No line breaks, TODO Is this allowed?</th></tr>
 * </table>
 * @author RProgrammer
 */
public class DataEncodingUtilities
implements JavaNamespace
{
	/* todo list:
	 * Todo Base64
	 * Todo Base96
	 * 	Add parameter/variant for baseXX which turns off line breaking
	 * Todo Quoted-Printable
	 * 	Add parameter for qpencode which controls line breaking
	 * Todo ASCII 85
	 * Todo uuencode
	 * 	Add parameter for uuencode which controls line breaking
	 */
	
	
	//<Hex
	public static final int
	HEX_UPPERCASE = 0x07,
	HEX_LOWERCASE = 0x27;
	
	/**
	 * Hexadecimal format encodes bytes as pairs of base-16 digits, optionally separated with a delimiter string, using the alphabet [0123456789ABCDEF] (case is specified in a parameter)
	 * <br>
	 * Format parameters for hex (there are no <i>variants</i>):
	 * <table border="1">
	 * 	<tr><th>Name</th><th rowspan="1">Occurrence<br>Ee/Dd = en-/de-code main/wrapper</th><th>Description</th>
	 * 	<tr><td>hexcase</td><td>Ee</td>			<td>Specifies the case of non-numeric digits (a-f/A-F), must be either {@link #HEX_LOWERCASE} or {@link #HEX_UPPERCASE}</td></tr>
	 * 	<tr><td>delimiterSize</td><td>EDd</td>	<td>Specifies the size of the delimiter between hex-pairs; used in skipping</td></tr>
	 * 	<tr><td>delimiter</td><td>e</td>		<td>Specifies the actual delimiter; used in writing the text</td></tr>
	 * </table>
	 * 
	 * 
	 * Examples of parsable hex strings:
	 * <ul>
	 * 	<li>"024A3c71" (0-char delimiters)</li>
	 * 	<li>"02:4A:3c:71" (1-char delimiters)</li>
	 * 	<li>"02:4A:3c:71:" Trailing delimiters don't matter</li>
	 * 	<li>"02 4A:3c-71" Note that decode() doesn't validate delimiters</li>
	 * 	<li>"02, 4A__3c[]71" (2-char delimiters)</li>
	 * </ul>
	 * 
	 * Examples of generated hex strings (with delimiters inserted):
	 * <ul>
	 * 	<li>"024A3c71" (0-char delimiters, or <code>null</code> delimiter)</li>
	 * 	<li>"02:4A:3c:71" (":" delimiter)</li>
	 * 	<li>"02, 4A, 3c, 71" (", " delimiter)</li>
	 * </ul>
	 * @param delimiterSize The size--in characters--of each delimiter
	 */
	public static void decodeHex(@ReadonlyValue @Nonnull char[] source, int sourceOffset, int sourceLength, @WritableValue @Nonnull byte[] dest, int destOffset, int destLength, int delimiterSize) throws EOFException, InvalidInputCharacterException
	{
		decodeHex(charArrayAsList(source, sourceOffset, sourceLength), byteArrayAsList(dest, destOffset, destLength), delimiterSize);
	}
	
	public static void decodeHex(@ReadonlyValue @Nonnull CharacterListRO source, @WritableValue @Nonnull ByteList dest, int delimiterSize) throws EOFException, InvalidInputCharacterException
	{
		final int sourceLength = source.size();
		//int destOffset = 0;
		int destLength = dest.size();
		
		
		int sourcePointer = 0;
		
		delimiterSize++; //read - skipSize
		
		int destIndex = 0;
		char c = 0;
		while (destIndex < destLength)
		{
			//Ensure that both characters are within the bounds
			if (!(sourcePointer+1 < sourceLength))
				throw new EOFException();
			
			
			
			//First character
			{
				c = source.getChar(sourcePointer); //Throw ArrayIndexException for EOF
				
				//Normalize letters (this makes 0x41('A') be 0xA, and 0x46('F') be 0xF)
				//				c -= ((c & 0x40) != 0) ? 0x37 : 0;
				c -= ((c & 0x40) >> 6) * 0x37;
				
				//This will implicitly unset the case bit for letters, and normalize numbers
				c <<= 4;
				
				//Check validity
				if (c < 0x10) //Java chars are uint16's, so "negatives" will wrap around
					throw new InvalidInputCharacterExceptionWithPosition(sourcePointer);
				
				dest.setByte(destIndex, (byte)(c));
				
				sourcePointer++;
			}
			
			
			//Second character
			{
				c = source.getChar(sourcePointer); //Throw ArrayIndexException for EOF
				
				//Unset the case bit for letters, and normalize numbers
				c &= ~0x30;
				
				//Normalize letters (this makes 0x41('A') be 0xA, and 0x46('F') be 0xF)
				//				c -= ((c & 0x40) != 0) ? 0x37 : 0;
				c -= ((c & 0x40) >> 6) * 0x37;
				
				
				//Check validity
				if (c >= 0x10) //Java chars are uint16's, so "negatives" will wrap around
					throw new InvalidInputCharacterExceptionWithPosition(sourcePointer);
				
				dest.setByte(destIndex, (byte)(dest.get(destIndex) | c));
				
				//				sourceOffset++;
			}
			
			
			destIndex++; //One more byte under our belt
			
			sourcePointer += delimiterSize;  //Skip delimiter+1 (or noop if delimiterSize == 0)
		}
	}
	
	
	
	
	
	
	public static void decodeHex(Reader in, OutputStream out) throws InvalidInputCharacterException, IOException
	{
		while (true)
		{
			int d0;
			
			while (true)
			{
				int v0 = in.read();
				
				if (v0 < 0)
					return;
				else
				{
					char c0 = (char) v0;
					
					if (c0 >= '0' && c0 <= '9')
					{
						d0 = c0 - '0';
						break;
					}
					else if (c0 >= 'a' && c0 <= 'f')
					{
						d0 = c0 - 'a' + 10;
						break;
					}
					else if (c0 >= 'A' && c0 <= 'F')
					{
						d0 = c0 - 'A' + 10;
						break;
					}
					//else: keep going over the delimiters :3
				}
			}
			
			
			
			
			//Second character
			int d1;
			{
				int v1 = in.read();
				
				char c1 = (char) v1;
				
				if (c1 >= '0' && c1 <= '9')
				{
					d1 = c1 - '0';
				}
				else if (c1 >= 'a' && c1 <= 'f')
				{
					d1 = c1 - 'a' + 10;
				}
				else if (c1 >= 'A' && c1 <= 'F')
				{
					d1 = c1 - 'A' + 10;
				}
				else
				{
					throw new InvalidInputCharacterException();
				}
			}
			
			
			
			byte b = (byte)(d1 | (d0 << 4));
			
			
			
			out.write(b);
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * In supporting delimiters, this method does not write them but merely skips over where they would be located.
	 * @param hexcase One of {@link #HEX_LOWERCASE} or {@link #HEX_UPPERCASE}
	 * @param delimiterSize The size--in characters--of each delimiter
	 */
	public static void encodeHex(byte[] source, int sourceOffset, int sourceLength, char[] dest, int destOffset, int destLength, int hexcase, int delimiterSize)
	{
		encodeHex(byteArrayAsList(source, sourceOffset, sourceLength), charArrayAsList(dest, destOffset, destLength), hexcase, delimiterSize);
	}
	
	public static void encodeHex(ByteListRO source, @WritableValue CharacterList dest, int hexcase, int delimiterSize)
	{
		int sourceLength = source.size();
		
		delimiterSize += 1;  //let it be known as skip size
		int sourceIndex = 0;
		int destIndex = 0;
		char c = 0;
		while (sourceIndex < sourceLength)
		{
			//First character
			{
				c = (char)(source.getByte(sourceIndex) & 0xF0);
				c = (char)((((c / 160) * hexcase) + 48) + (c >> 4));
				dest.setChar(destIndex, c);
			}
			
			destIndex++;
			
			//Second character
			{
				c = (char)(source.getByte(sourceIndex) & 0x0F);
				c = (char)((((c / 10) * hexcase) + 48) + c);
				dest.setChar(destIndex, c);
			}
			
			destIndex += delimiterSize;
			
			sourceIndex++;
		}
	}
	//Hex>
	
	
	
	//<QP
	public static byte[] decodeQuotedPrintable(char[] source, int offset, int length) throws InvalidInputCharacterException
	{
		byte[] dest = new byte[length]; //raw is always shorter than encoded
		int sourceIndex = offset, destIndex = 0;
		char currChar = 0, nextChar = 0, nextNextChar = 0;
		
		while (sourceIndex - offset < length)
		{
			currChar = source[sourceIndex];
			
			if (currChar == 0x3D) //'='
			{
				if (sourceIndex+1 - offset < length)
				{
					nextChar = source[sourceIndex+1];
					
					if (nextChar == '\r')
					{
						//Either a "=\r" or "=\r\n" soft line break (remember, soft line breaks encode nothing)
						
						if (sourceIndex+2 - offset < length)
						{
							nextNextChar = source[sourceIndex+2];
							if (nextNextChar == '\n')
							{
								//Skip the "=\r\n" soft line break
								sourceIndex+=3;
							}
							else
							{
								//Skip the "=\r" soft line break
								sourceIndex += 2;
							}
						}
						else
						{
							//Skip the "=\r"EOF soft line break
							sourceIndex += 2;
						}
					}
					else if (nextChar == '\n')
					{
						//Skip the "=\n" soft line break
						sourceIndex += 2;
					}
					else
					{
						//=XX hex escape, another char is expected
						if (sourceIndex+2 - offset < length)
						{
							nextNextChar = source[sourceIndex+2];
							
							///////<Decode the hex pair/////
							{
								//First character
								{
									//Unset the case bit for letters, and normalize numbers
									nextChar &= ~0x30;
									
									//Normalize letters
									if ((nextChar & 0x40) != 0)
										nextChar -= 0x37; //This makes 0x41('A') be 0xA, and 0x46('F') be 0xF
									
									
									//Check validity
									if (nextChar >= 0x10)
										throw new InvalidInputCharacterExceptionWithPosition(sourceIndex+2);
									
									dest[destIndex] |= nextChar << 4;
								}
								
								
								//Second character
								{
									//Unset the case bit for letters, and normalize numbers
									nextNextChar &= ~0x30;
									
									//Normalize letters
									if ((nextNextChar & 0x40) != 0)
										nextNextChar -= 0x37; //This makes 0x41('A') be 0xA, and 0x46('F') be 0xF
									
									
									//Check validity
									if (nextNextChar >= 0x10)
										throw new InvalidInputCharacterExceptionWithPosition(sourceIndex+2);
									
									dest[destIndex] |= nextNextChar;
								}
							}
							///////Decode the hex pair>/////
							
							destIndex++;
							sourceIndex += 3;
						}
						else
						{
							//Missing second hex char, behavior undefined
							// silently ignore.
							sourceIndex += 2;
						}
					}
				}
				else
				{
					//Just a lone '=' at the eof; behavior is undefined
					// silently ignore.
					sourceIndex++;
				}
			}
			else
			{
				if (currChar > 0x007F)
					throw new InvalidInputCharacterExceptionWithPosition(sourceIndex);
				else
				{
					dest[destIndex] = (byte)(currChar & 0x007F);
					destIndex++;
					sourceIndex++;
				}
			}
		}
		
		
		//Pack the array
		byte[] packed = null;
		{
			packed = new byte[destIndex];
			System.arraycopy(dest, 0, packed, 0, destIndex);
		}
		
		return packed;
	}
	//QP>
	
	
	
	
	
	
	//<Binary
	public static String encodeBinary(byte[] data, int dataOffsetInBytes, int dataLengthInBytes, int lengthInBits, String byteDelimiter)
	{
		if (lengthInBits > data.length * 8)
			throw new IllegalArgumentException("more bits specified than there is data!");
		
		StringBuilder buff = new StringBuilder();
		int len = 0;
		
		for (int i = 0; i < dataLengthInBytes; i++)
		{
			for (int b = 0; b < 8; b++)
			{
				if (len >= lengthInBits)
					return buff.toString();
				
				boolean bit = (data[dataOffsetInBytes + i] & (1 << b)) != 0;
				buff.append(bit ? '1' : '0');
				len++;
			}
			
			//Nice delimiters on the byte boundaries :>
			if (byteDelimiter != null && byteDelimiter.length() != 0 && i < dataLengthInBytes-1)
				buff.append(byteDelimiter);
		}
		
		if (len < lengthInBits)
			throw new AssertionError();
		
		return buff.toString();
	}
	
	public static String encodeBinary(byte[] data, int dataOffsetInBytes, int dataLengthInBytes, String byteDelimiter)
	{
		return encodeBinary(data, dataOffsetInBytes, dataLengthInBytes, dataLengthInBytes*8, byteDelimiter);
	}
	
	public static String encodeBinary(byte[] data, int dataOffsetInBytes, int dataLengthInBytes)
	{
		return encodeBinary(data, dataOffsetInBytes, dataLengthInBytes, dataLengthInBytes*8, " ");
	}
	
	public static String encodeBinary(byte[] data, int lengthInBits, String byteDelimiter)
	{
		return encodeBinary(data, 0, data.length, lengthInBits, byteDelimiter);
	}
	
	public static String encodeBinary(byte[] data, int lengthInBits)
	{
		return encodeBinary(data, lengthInBits, " ");
	}
	
	public static String encodeBinary(byte[] data)
	{
		return encodeBinary(data, data.length * 8);
	}
	
	
	
	
	
	
	public static String encodeBinary(Iterable<Boolean> data)
	{
		return mapToString(b -> b ? '1' : '0', data);
	}
	
	public static BooleanList decodeBinary(String encoded)
	{
		BooleanList l = newBooleanList();
		
		forEach(c ->
		{
			if (c == '0')
				l.add(false);
			else if (c == '1')
				l.add(true);
			
			//otherwise ignore in case there are byte delimiters or something X3
			
		}, encoded);
		
		return l;
	}
	//Binary>
	
	
	
	
	
	
	
	
	
	
	//<Convenience methods
	//<Hex
	public static String encodeHexNoDelimiter(ByteList source, int hexcase)
	{
		return encodeHex(source, hexcase, (char[])null);
	}
	
	
	public static String encodeHex(ByteList source, int hexcase, @Nullable String delimiter)
	{
		return encodeHex(source, hexcase, delimiter == null ? null : delimiter.toCharArray());
	}
	
	public static String encodeHex(ByteList source, int hexcase, @Nullable char[] delimiter)
	{
		int sourceLength = source.size();
		int delimiterSize = delimiter == null ? 0 : delimiter.length;
		CharacterList dest = charArrayAsList(new char[getHexTextlenFromDatalen(sourceLength, delimiterSize)]);
		
		encodeHex(source, dest, hexcase, delimiterSize);
		
		if (delimiterSize > 1)
		{
			for (int i = 0; i < sourceLength-1; i++) //{< length-1}  because there is no trailing delimiter
				System.arraycopy(delimiter, 0, dest, (i * (2+delimiterSize) + 2), delimiter.length);
		}
		else if (delimiterSize == 1)
		{
			char delimiterChar = delimiter[0];
			for (int i = 0; i < sourceLength-1; i++) //{< length-1}  because there is no trailing delimiter
				dest.setChar(i * (2+delimiterSize) + 2, delimiterChar);
		}
		//Do nothing for delimiterSize == 0
		
		return newString(dest.toCharArraySlicePossiblyLive());
	}
	
	
	public static ByteList decodeHex(@Nonnull CharacterList source, int delimiterSize) throws EOFException, InvalidInputCharacterException
	{
		//DivideInt_ceiling, because "0d:21:" (6/3) should be 2 bytes, but "0d:21" (5/3) should also be 2 bytes
		int length = getHexDatalenFromTextlen(source.size(), delimiterSize);
		
		ByteList dest = byteArrayAsList(new byte[length]);
		decodeHex(source, dest, delimiterSize);
		
		return dest;
	}
	
	public static ByteList decodeHexToList(@Nonnull String source, int delimiterSize) throws EOFException, InvalidInputCharacterException
	{
		return decodeHex(stringToList(source), delimiterSize);
	}
	
	/**
	 * Wraps the exceptions into {@link AssertionError}s, indicating that an error is to be counted as a bug in the code!! (eg, if the given data is hardcoded).
	 */
	public static ByteList decodeHexMandatoryToList(String source, int delimiterSize) throws AssertionError
	{
		try
		{
			return decodeHexToList(source, delimiterSize);
		}
		catch (EOFException exc)
		{
			throw new AssertionError(exc);
		}
		catch (InvalidInputCharacterException exc)
		{
			throw new AssertionError(exc);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String encodeHexNoDelimiter(byte[] source, int hexcase)
	{
		return encodeHex(source, 0, source.length, hexcase, (char[])null);
	}
	
	public static String encodeHexNoDelimiter(byte[] source, int sourceOffset, int sourceLength, int hexcase)
	{
		return encodeHex(source, sourceOffset, sourceLength, hexcase, (char[])null);
	}
	
	
	public static String encodeHex(byte[] source, int hexcase, @Nullable String delimiter)
	{
		return encodeHex(source, 0, source.length, hexcase, delimiter);
	}
	
	public static String encodeHex(byte[] source, int sourceOffset, int sourceLength, int hexcase, @Nullable String delimiter)
	{
		return encodeHex(source, sourceOffset, sourceLength, hexcase, delimiter == null ? null : delimiter.toCharArray());
	}
	
	public static String encodeHex(byte[] source, int hexcase, @Nullable char[] delimiter)
	{
		return encodeHex(source, 0, source.length, hexcase, delimiter);
	}
	
	public static String encodeHex(byte[] source, int sourceOffset, int sourceLength, int hexcase, @Nullable char[] delimiter)
	{
		int delimiterSize = delimiter == null ? 0 : delimiter.length;
		char[] dest = new char[getHexTextlenFromDatalen(sourceLength, delimiterSize)];
		
		encodeHex(source, sourceOffset, sourceLength, dest, 0, dest.length, hexcase, delimiterSize);
		
		if (delimiterSize > 1)
		{
			for (int i = 0; i < sourceLength-1; i++) //{< length-1}  because there is no trailing delimiter
				System.arraycopy(delimiter, 0, dest, (i * (2+delimiterSize) + 2), delimiter.length);
		}
		else if (delimiterSize == 1)
		{
			char delimiterChar = delimiter[0];
			for (int i = 0; i < sourceLength-1; i++) //{< length-1}  because there is no trailing delimiter
				dest[i * (2+delimiterSize) + 2] = delimiterChar;
		}
		//Do nothing for delimiterSize == 0
		
		return new String(dest);
	}
	
	
	public static byte[] decodeHex(@Nonnull char[] source, int sourceOffset, int sourceLength, int delimiterSize) throws EOFException, InvalidInputCharacterException
	{
		//DivideInt_ceiling, because "0d:21:" (6/3) should be 2 bytes, but "0d:21" (5/3) should also be 2 bytes
		int length = getHexDatalenFromTextlen(sourceLength, delimiterSize);
		
		byte[] dest = new byte[length];
		decodeHex(source, sourceOffset, sourceLength, dest, 0, length, delimiterSize);
		
		return dest;
	}
	
	public static byte[] decodeHex(@Nonnull String source, int delimiterSize) throws EOFException, InvalidInputCharacterException
	{
		return decodeHex(source.toCharArray(), 0, source.length(), delimiterSize);
	}
	
	/**
	 * Wraps the exceptions into {@link AssertionError}s, indicating that an error is to be counted as a bug in the code!! (eg, if the given data is hardcoded).
	 */
	public static byte[] decodeHexMandatory(String source, int delimiterSize) throws AssertionError
	{
		try
		{
			return decodeHex(source, delimiterSize);
		}
		catch (EOFException exc)
		{
			throw new AssertionError(exc);
		}
		catch (InvalidInputCharacterException exc)
		{
			throw new AssertionError(exc);
		}
	}
	
	
	
	
	public static byte[] decodeHexNoDelimiter(@Nonnull String source) throws EOFException, InvalidInputCharacterException
	{
		return decodeHex(source, 0);
	}
	
	/**
	 * Wraps the exceptions into {@link AssertionError}s, indicating that an error is to be counted as a bug in the code!! (eg, if the given data is hardcoded).
	 */
	public static byte[] decodeHexMandatoryNoDelimiter(String source) throws AssertionError
	{
		return decodeHexMandatory(source, 0);
	}
	//Hex>
	
	
	//<QP
	public static byte[] decodeQuotedPrintable(String source) throws InvalidInputCharacterException
	{
		char[] charArray = source.toCharArray();
		return decodeQuotedPrintable(charArray, 0, charArray.length);
	}
	//QP>
	//Convenience methods>
	
	
	
	
	
	//<Utilities
	public static int getHexDatalenFromTextlen(int textLength, int delimiterSize)
	{
		return textLength / (2+delimiterSize) + (textLength % (2+delimiterSize) == 0 ? 0 : 1);
	}
	
	/**
	 * Note: This assumes there is no trailing delimiter (eg, "0d:21", not "0d:21:")
	 */
	public static int getHexTextlenFromDatalen(int dataLength, int delimiterSize)
	{
		return dataLength == 0 ? 0 : dataLength * (2+delimiterSize) - delimiterSize;
	}
	
	
	protected static String getUnicodeEscape(char c)
	{
		char[] escape = new char[6];
		escape[0] = '\\';
		escape[1] = 'u';
		escape[2] = Character.forDigit((c & 0xF000) >> 12, 16);
		escape[3] = Character.forDigit((c & 0x0F00) >> 8, 16);
		escape[4] = Character.forDigit((c & 0x00F0) >> 4, 16);
		escape[5] = Character.forDigit((c & 0x000F) >> 0, 16);
		return new String(escape);
	}
	//Utilities>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//////// Oldddd codddde! ////////
	
	
	//FormattingUtilities.java
	
	//	/**
	//	 * takes unprefixed, undelimited ascii hex and decodes it into a byte array.<br>
	//	 * @param hex eg, "0F230C59"
	//	 * @return The byte array, possibly empty, never null
	//	 * @throws TextSyntaxException if illegal characters are encountered
	//	 * @throws IllegalArgumentException if the string is of odd length
	//	 */
	//	@Deprecated
	//	public static byte[] decodeHex(String hex) throws TextSyntaxException, IllegalArgumentException
	//	{
	//		if (hex.length() % 2 == 1)
	//			throw new IllegalArgumentException("Provided string is odd in length");
	//
	//		int pairCount = hex.length() / 2;
	//		byte[] data = new byte[pairCount];
	//
	//		byte d = 0;
	//		for (int i = 0; i < pairCount; i++)
	//		{
	//			//High digit
	//			d = (byte)Character.digit(hex.charAt(i*2), 16);
	//
	//			if (d == -1)
	//				throw TextSyntaxException.inst("Invalid hex char '"+hex.charAt(i*2)+"'");
	//
	//			data[i] |= d << 4;
	//
	//			//Low digit
	//			d = (byte)Character.digit(hex.charAt(i*2+1), 16);
	//
	//			if (d == -1)
	//				throw TextSyntaxException.inst("Invalid hex char '"+hex.charAt(i*2+1)+"'");
	//
	//			data[i] |= d;
	//		}
	//
	//		return data;
	//	}
	//
	//	/**
	//	 * takes unprefixed, optionally delimited (1 char) ascii hex and decodes it into a byte array.<br>
	//	 * Note: The hex must be padded. eg, "04-36-00-10" is valid, but "4-36-0-10" is not.
	//	 * @param hex eg, "A0-43:22,440C"
	//	 * @return The byte array, possibly empty, never null
	//	 */
	//	@Deprecated
	//	public static byte[] decodeHex_delimited(String hex)
	//	{
	//		//Todo implement
	//		throw new NotYetImplementedException();
	//	}
	//
	//
	//
	//	@Deprecated
	//	/**
	//	 * Encode bytes in non-delimited, unprefixed, uppercase hex format.
	//	 */
	//	public static String encodeHex(byte[] data)
	//	{
	//		char[] hex = new char[data.length * 2];
	//
	//		int d = 0;
	//		for (int i = 0; i < data.length; i++)
	//		{
	//			//High digit
	//			d = (data[i] & 0xF0) >> 4;
	//
	//		if (d < 10)
	//			d += 30;
	//		else
	//			d += 31;
	//
	//		hex[i*2] = (char)d;
	//
	//		//Low digit
	//		d = data[i] & 0x0F;
	//
	//		if (d < 10)
	//			d += 30;
	//		else
	//			d += 31;
	//
	//		hex[i*2+1] = (char)d;
	//		}
	//
	//		return new String(hex);
	//	}
}
