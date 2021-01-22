package rebound.dataformats.xml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import rebound.text.encodings.detection.TextEncodingDetector;
import rebound.util.functional.throwing.FunctionalInterfacesThrowingCheckedExceptionsStandard.NullaryFunctionThrowingIOException;

public enum XMLTextEncodingDetector
implements TextEncodingDetector
{
	I;
	
	
	@Override
	public Charset detectEncoding(NullaryFunctionThrowingIOException<InputStream> opener) throws IOException, UnsupportedCharsetException
	{
		try (InputStream in = opener.f())
		{
			return XMLEncodingDetection.readXMLDeclarationForEncodingOrNull(in);
		}
	}
}
