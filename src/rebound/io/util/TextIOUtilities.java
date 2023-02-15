package rebound.io.util;

import static java.util.Objects.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import rebound.io.HeadReopenableInputStream;
import rebound.io.ucs4.UCS4ArrayWriter;
import rebound.io.ucs4.UCS4Reader;
import rebound.io.ucs4.UCS4Writer;
import rebound.text.encodings.detection.TextEncodingDetector;
import rebound.text.encodings.detection.detectors.StandardTextEncodingDetection;

public class TextIOUtilities
{
	public static final TextEncodingDetector DefaultFromStreamEncodingDetector = StandardTextEncodingDetection.certain();  //UTF-8 is an important and strong default, let's not use heuristics (idk if it matters though X3' )
	
	
	public static String readAllText(InputStream in, String encoding) throws IOException
	{
		return readAllText(in, Charset.forName(encoding));
	}
	
	public static String readAllText(InputStream in, Charset encoding) throws IOException
	{
		if (encoding == null)
			encoding = Charset.defaultCharset();
		
		CharsetDecoder decoder = encoding.newDecoder();
		decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		decoder.onMalformedInput(CodingErrorAction.REPORT);
		
		return new String(TextIOUtilities.readAll(new InputStreamReader(in, decoder)));
	}
	
	public static String readAllText(InputStream in, TextEncodingDetector encodingDetector) throws IOException
	{
		int headSize = encodingDetector.headSize();
		HeadReopenableInputStream headReopenable = new HeadReopenableInputStream(in, headSize);
		
		Charset encoding = encodingDetector.detectEncoding(headReopenable::openNewHeadStream);
		
		return readAllText(headReopenable.stream(), encoding == null ? StandardCharsets.UTF_8 : encoding);
	}
	
	public static String readAllText(InputStream in) throws IOException
	{
		return readAllText(in, DefaultFromStreamEncodingDetector);
	}
	
	public static long pump(Reader in, Writer out) throws IOException
	{
		return pump(in, out, 4096);
	}
	
	public static long pumpFixed(Reader in, Writer out, long length) throws IOException
	{
		return pumpFixed(in, out, length, 4096);
	}
	
	public static long pump(Reader in, Writer out, int bufferSize) throws IOException
	{
		long total = 0;
		char[] buffer = new char[bufferSize];
		int amt = in.read(buffer);
		while (amt >= 0)
		{
			total += amt;
			out.write(buffer, 0, amt);
			amt = in.read(buffer);
		}
		return total;
	}
	
	public static long pumpFixed(Reader in, Writer out, long length, int bufferSize) throws IOException
	{
		if (length > 0)
		{
			char[] buffer = new char[bufferSize];
			int amt = 0;
			long curr = 0;
			while (curr < length)
			{
				amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
				if (amt < 0)
					return curr;
				curr += amt;
				out.write(buffer, 0, amt);
			}
			return curr;
		}
		else
		{
			return 0;
		}
	}
	
	public static long pump(UCS4Reader in, UCS4Writer out) throws IOException
	{
		return pump(in, out, 4096);
	}
	
	public static long pumpFixed(UCS4Reader in, UCS4Writer out, long length) throws IOException
	{
		return pumpFixed(in, out, length, 4096);
	}
	
	public static long pump(UCS4Reader in, UCS4Writer out, int bufferSize) throws IOException
	{
		long total = 0;
		int[] buffer = new int[bufferSize];
		int amt = in.read(buffer);
		while (amt >= 0)
		{
			total += amt;
			out.write(buffer, 0, amt);
			amt = in.read(buffer);
		}
		return total;
	}
	
	public static long pumpFixed(UCS4Reader in, UCS4Writer out, long length, int bufferSize) throws IOException
	{
		if (length > 0)
		{
			int[] buffer = new int[bufferSize];
			int amt = 0;
			long curr = 0;
			while (curr < length)
			{
				amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
				if (amt < 0)
					return curr;
				curr += amt;
				out.write(buffer, 0, amt);
			}
			return curr;
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * Discards no more than a given amount of data from an InputStream.<br>
	 * The only reason any less can be skipped is because of EOF (determined by InputStream.skip(long) < 0).<br>
	 */
	public static long discard(Reader in, long amount) throws IOException
	{
		long skipped = 0;
		long c = 0;
		while (skipped < amount)
		{
			c = in.skip(amount-skipped);
			if (c == -1)
				return skipped;
			else
				skipped += c;
		}
		return skipped;
	}
	
	/**
	 * Discards no more than a given amount of data from an InputStream.<br>
	 * The only reason any less can be skipped is because of EOF (determined by InputStream.skip(long) == 0).<br>
	 */
	public static long discard(Reader in) throws IOException
	{
		long skipped = 0;
		long c = 0;
		while (true)
		{
			c = in.skip(65536);
			if (c == 0)
				return skipped;
			else
				skipped += c;
		}
	}
	
	/**
	 * Discards no more than a given amount of data from an InputStream.<br>
	 * The only reason any less can be skipped is because of EOF.<br>
	 * It does this by using read() rather than skip() on the given input stream :>
	 */
	public static long discardByReading(Reader in, long amount) throws IOException
	{
		char[] dummyBuff = new char[1024];
		
		long skipped = 0;
		long c = 0;
		while (skipped < amount)
		{
			c = in.read(dummyBuff, 0, (int)Math.min(dummyBuff.length, amount-skipped));
			if (c == -1)
				return skipped;
			else
				skipped += c;
		}
		return skipped;
	}
	
	public static long discardByReading(Reader in) throws IOException
	{
		char[] dummyBuff = new char[1024];
		
		long skipped = 0;
		long c = 0;
		while (true)
		{
			c = in.read(dummyBuff, 0, dummyBuff.length);
			if (c == -1)
				return skipped;
			else
				skipped += c;
		}
	}
	
	public static void forceRead(Reader in, char[] buff, int offset, int len) throws EOFException, IOException
	{
		int read = 0;
		int r = 0;
		while (true)
		{
			r = in.read(buff, offset+read, len - read);
			if (r < 0)
				throw new EOFException("Premature EOF");
			read += r;
			if (read >= len)
				return;
		}
	}
	
	public static void forceRead(Reader in, char[] buff) throws EOFException, IOException
	{
		forceRead(in, buff, 0, buff.length);
	}
	
	public static char[] forceReadToNew(Reader in, int length) throws EOFException, IOException
	{
		char[] c = new char[length];
		forceRead(in, c);
		return c;
	}
	
	/**
	 * This turns an interface with short reads into one that only reads less than requested due to EOF.
	 * Reads until the amount specified is read or EOF is reached.
	 * Returns the amount actually read.
	 */
	public static int readAsMuchAsPossible(Reader in, char[] buff, int offset, int len) throws IOException
	{
		int read = 0;
		
		int r = 0;
		while (true)
		{
			r = in.read(buff, offset + read, len - read);
			if (r < 0)
				break;
			read += r;
			if (read >= len)
				break;
		}
		
		return read;
	}
	
	public static int readAsMuchAsPossible(Reader in, char[] buff) throws IOException
	{
		return readAsMuchAsPossible(in, buff, 0, buff.length);
	}
	
	public static String readAllToString(Reader in) throws IOException
	{
		StringWriter buff = new StringWriter();
		pump(in, buff);
		return buff.toString();
	}
	
	public static char[] readAll(Reader in) throws IOException
	{
		CharArrayWriter buff = new CharArrayWriter();
		pump(in, buff);
		return buff.toCharArray();
	}
	
	public static int[] readAll(UCS4Reader in) throws IOException
	{
		UCS4ArrayWriter buff = new UCS4ArrayWriter();
		pump(in, buff);
		return buff.toIntArray();
	}
	
	public static void tryClose(Reader s)
	{
		if (s != null)
		{
			try
			{
				s.close();
			}
			catch (IOException exc)
			{
			}
		}
	}
	
	public static void tryClose(Writer s)
	{
		if (s != null)
		{
			try
			{
				s.close();
			}
			catch (IOException exc)
			{
			}
		}
	}
	
	public static char read1(Reader in) throws EOFException, IOException
	{
		int v = in.read();
		if (v == -1)
			throw new EOFException();
		return (char)v;
	}
	
	public static void closeWithoutError(Reader stream)
	{
		try
		{
			stream.close();
		}
		catch (IOException exc)
		{
		}
	}
	
	public static void closeWithoutError(Writer stream)
	{
		try
		{
			stream.close();
		}
		catch (IOException exc)
		{
		}
	}
	
	
	
	
	
	public static BufferedReader ensureBufferedReader(Reader in)
	{
		return in instanceof BufferedReader ? (BufferedReader)in : new BufferedReader(in);
	}
	
	public static BufferedWriter ensureBufferedWriter(Writer in)
	{
		return in instanceof BufferedWriter ? (BufferedWriter)in : new BufferedWriter(in);
	}
	
	
	
	
	
	
	
	
	
	public static void writeTextToBinaryProperly(OutputStream out, Charset encoding, PureTextWritingBody body) throws IOException
	{
		Writer w = new OutputStreamWriter(requireNonNull(out), encoding);
		
		
		//Our text implementation of course doesn't encode UTF-16 properly!! XD   It encodes the <?xml..?> as UTF-16 *without* adding a proper BOM, so it can't be re-parsed!..0
		{
			if (eq(encoding, StandardCharsets.UTF_16))
				throw new IllegalArgumentException("You gotta say which endian it is, little or big!");
			
			//..But we can easily fix that! :D
			if (eq(encoding, StandardCharsets.UTF_16LE))
			{
				out.write(0xFF);
				out.write(0xFE);
			}
			else if (eq(encoding, StandardCharsets.UTF_16BE))
			{
				out.write(0xFE);
				out.write(0xFF);
			}
		}
		
		
		body.run(w, encoding.name());
		
		
		w.flush();  //In case our stream decorator is buffering (*sigh* Java doesn't allow a standard interface for 'closing' or 'flushing' just a single level of decorator X'3 )
	}
}
