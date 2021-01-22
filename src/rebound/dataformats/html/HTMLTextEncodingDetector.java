package rebound.dataformats.html;

import java.nio.charset.StandardCharsets;
import rebound.dataformats.xml.XMLTextEncodingDetector;
import rebound.text.encodings.detection.TextEncodingDetector;
import rebound.text.encodings.detection.util.CascadedTextEncodingDetector;
import rebound.text.encodings.detection.util.ExhhaustiveTextEncodingDetector;

public class HTMLTextEncodingDetector
{
	/**
	 * Note that this might very easily 
	 */
	public static final TextEncodingDetector HTMLStandardDetection = new CascadedTextEncodingDetector(
	
	HTMLTextEncodingDetectorMinusXMLDetector.I,
	XMLTextEncodingDetector.I,
	
	new ExhhaustiveTextEncodingDetector(
	StandardCharsets.UTF_8,
	StandardCharsets.UTF_16LE,
	StandardCharsets.UTF_16BE,
	StandardCharsets.US_ASCII,
	StandardCharsets.ISO_8859_1
	)
	
	);
}
