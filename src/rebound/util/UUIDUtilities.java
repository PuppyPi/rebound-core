package rebound.util;

import static rebound.GlobalCodeMetastuffContext.*;
import static rebound.testing.WidespreadTestingUtilities.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.util.UUID;

public class UUIDUtilities
{
	protected static final boolean jreStandardsCompliantInCasing;
	
	static
	{
		String s = "a8f664ab-ac64-4583-90e4-09162dc33e9b";
		
		long hi = 0xA8F664ABAC644583l;
		long lo = 0x90E409162DC33E9Bl;
		
		UUID a = UUID.fromString(s);
		UUID b = new UUID(hi, lo);
		
		asrt(eq(a, b));
		
		String actual = b.toString();
		
		if (eq(actual, s))
		{
			jreStandardsCompliantInCasing = true;
		}
		else
		{
			if (eq(actual, s.toUpperCase()))
			{
				jreStandardsCompliantInCasing = false;
			}
			else
			{
				logBug("Neither case matched!!: "+s+" -> "+repr(actual));
				jreStandardsCompliantInCasing = false;
			}
			
			asrt(eq(actual.toLowerCase(), s));
		}
	}
	
	
	public static String uuidToLowercaseString(UUID uuid)
	{
		return jreStandardsCompliantInCasing ? uuid.toString() : uuid.toString().toLowerCase();
	}
	
	
	
	
	
	
	
	public static boolean isJREStandardsCompliantInCasing()
	{
		return jreStandardsCompliantInCasing;
	}
}
