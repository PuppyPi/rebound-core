package rebound.text.encodings.detection.util;

import static rebound.util.collections.CollectionUtilities.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.text.encodings.detection.TextEncodingDetector;
import rebound.util.functional.throwing.FunctionalInterfacesThrowingCheckedExceptionsStandard.NullaryFunctionThrowingIOException;

public class CascadedTextEncodingDetector
implements TextEncodingDetector
{
	protected final Iterable<TextEncodingDetector> underlyings;
	
	public CascadedTextEncodingDetector(Iterable<TextEncodingDetector> underlyings)
	{
		this.underlyings = underlyings;
	}
	
	public CascadedTextEncodingDetector(TextEncodingDetector... underlyings)
	{
		this(asList(underlyings));
	}
	
	@ImplementationTransparency
	public Iterable<TextEncodingDetector> getUnderlyings()
	{
		return underlyings;
	}
	
	
	
	
	
	@Override
	public Charset detectEncoding(NullaryFunctionThrowingIOException<InputStream> opener) throws IOException, UnsupportedCharsetException
	{
		for (TextEncodingDetector u : underlyings)
		{
			Charset d = u.detectEncoding(opener);
			
			if (d != null)
				return d;
		}
		
		return null;
	}
}
