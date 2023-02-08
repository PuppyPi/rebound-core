package rebound.text.encodings.detection.util;

import static rebound.testing.WidespreadTestingUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.UnsupportedCharsetException;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.io.ucs4.UCS4Reader;
import rebound.io.ucs4.UCS4ReaderFromNormalUTF16Reader;
import rebound.io.ucs4.UTF16EncodingException;
import rebound.text.encodings.detection.TextEncodingDetector;
import rebound.util.functional.throwing.FunctionalInterfacesThrowingCheckedExceptionsStandard.NullaryFunctionThrowingIOException;

public class ExhhaustiveTextEncodingDetector
implements TextEncodingDetector
{
	protected final Iterable<Charset> underlyings;
	
	public ExhhaustiveTextEncodingDetector(Iterable<Charset> underlyings)
	{
		this.underlyings = underlyings;
	}
	
	public ExhhaustiveTextEncodingDetector(Charset... underlyings)
	{
		this(asList(underlyings));
	}
	
	@ImplementationTransparency
	public Iterable<Charset> getUnderlyings()
	{
		return underlyings;
	}
	
	
	
	
	
	@Override
	public Charset detectEncoding(NullaryFunctionThrowingIOException<InputStream> opener) throws IOException, UnsupportedCharsetException
	{
		for (Charset encoding : underlyings)
		{
			try (InputStream in = opener.f())
			{
				CharsetDecoder decoder = encoding.newDecoder();
				decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
				decoder.onMalformedInput(CodingErrorAction.REPORT);
				
				try
				{
					UCS4Reader r = new UCS4ReaderFromNormalUTF16Reader(new InputStreamReader(in, decoder));
					
					int[] b = new int[4096];
					
					boolean bad = false;
					
					while (!bad)
					{
						int amt = r.read(b);
						
						if (amt == -1)
							return encoding;
						else
						{
							for (int i = 0; i < amt && !bad; i++)
							{
								if (b[i] == 0)  //basically no text file will actually legitimately have the NUL character in it, so this is useful for checking if it's correct (particularly with 8-bit encodings like ISO-8859-1 which might otherwise accept *any* input as silent errors! XD'' )
									bad = true;
							}
						}
					}
					
					asrt(bad);
					//continue on since it's bad X3
				}
				catch (CharacterCodingException | UTF16EncodingException exc)
				{
					//continue on X3
				}
			}
		}
		
		return null;
	}
	
	
	
	@Override
	public Charset detectEncoding(byte[] in, int offset, int length) throws UnsupportedCharsetException
	{
		for (Charset encoding : underlyings)
		{
			CharsetDecoder decoder = encoding.newDecoder();
			decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
			decoder.onMalformedInput(CodingErrorAction.REPORT);
			
			try
			{
				decoder.decode(ByteBuffer.wrap(in, offset, length));
			}
			catch (CharacterCodingException exc)
			{
				//continue on X3
			}
		}
		
		return null;
	}
}
