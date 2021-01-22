package rebound.dataformats.html;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import rebound.text.encodings.detection.TextEncodingDetector;
import rebound.util.functional.throwing.FunctionalInterfacesThrowingCheckedExceptionsStandard.NullaryFunctionThrowingIOException;

public enum HTMLTextEncodingDetectorMinusXMLDetector
implements TextEncodingDetector
{
	I;
	
	
	@Override
	public Charset detectEncoding(NullaryFunctionThrowingIOException<InputStream> opener) throws IOException
	{
//		try (InputStream in = opener.f())
//		{
//			//TODO Check for meta http-equiv ContentType = text/html; charset=!!!    :>
//			//			Put it in code adjacent to StandardHTMLGetMetaRefresh  :>
//			
//			//TODO Check doctype like for XML!
//			//Check for HTML doctype—*multiple ones!*  I've seen it in the wild X'D    Case in point: http://www.fluorophores.tugraz.at/substance/
//			
//			
//		}
		
		return null;
	}
}
