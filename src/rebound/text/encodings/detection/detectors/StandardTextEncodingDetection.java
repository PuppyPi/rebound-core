package rebound.text.encodings.detection.detectors;

import java.nio.charset.StandardCharsets;
import rebound.dataformats.html.HTMLTextEncodingDetectorMinusXMLDetector;
import rebound.dataformats.xml.XMLTextEncodingDetector;
import rebound.text.encodings.detection.TextEncodingDetector;
import rebound.text.encodings.detection.util.CascadedTextEncodingDetector;
import rebound.text.encodings.detection.util.ExhhaustiveTextEncodingDetector;

public class StandardTextEncodingDetection
{
	private static final TextEncodingDetector CertainDetector = new CascadedTextEncodingDetector(
	StandardTextEditorConventionsTextEncodingDetector.I,
	XMLTextEncodingDetector.I,  //TODO Make one that doesn't include Unicode BOM detection as "certain detection"  (in case we don't know for sure it's XML!)
	HTMLTextEncodingDetectorMinusXMLDetector.I
	);
	
	
	private static final TextEncodingDetector CertainPlusHeuristicsDetector = new CascadedTextEncodingDetector(
	CertainDetector
	//TODO Unicode BOM   (see XMLEncodingDetection for an implementation!!)
	);
	
	
	private static final TextEncodingDetector CertainPlusHeuristicsPlusExhaustiveDetector = new CascadedTextEncodingDetector(
	CertainPlusHeuristicsDetector,
	
	new ExhhaustiveTextEncodingDetector(
	StandardCharsets.UTF_8,
	StandardCharsets.UTF_16LE,
	StandardCharsets.UTF_16BE,
	StandardCharsets.US_ASCII,
	StandardCharsets.ISO_8859_1
	)
	
	);
	
	
	
	
	
	public static TextEncodingDetector certain()
	{
		return CertainDetector;
	}
	
	public static TextEncodingDetector certainPlusHeuristics()
	{
		return CertainPlusHeuristicsDetector;
	}
	
	public static TextEncodingDetector certainPlusHeuristicsPlusExhaustive()
	{
		return CertainPlusHeuristicsPlusExhaustiveDetector;
	}
}
