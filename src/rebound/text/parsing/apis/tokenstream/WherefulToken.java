package rebound.text.parsing.apis.tokenstream;

import rebound.annotations.semantic.SignalType;
import rebound.text.StringUtilities;

/**
 * {@link WherefulToken#getLengthOfMaskedSource()} *must* be == {@link #getMaskedSource()}.length()!!
 * And {@link WherefulToken#getCharacterInMaskedSource(int)} *must* be == {@link #getMaskedSource()}.charAt()!!
 * 
 * + Note: {@link #toString()} MUST delegate to {@link #getMaskedSource()}!!  So that things like {@link StringUtilities#concatenateStrings(Iterable)} will work!!
 */
@SignalType
public interface WherefulToken
extends Tokenlike
{
	/**
	 * @return the 0-based character position/index or -1 if unknown!
	 */
	public int getStartingCharacterIndexInSource();
	
	public default int getEndingIndexInSource()
	{
		return getStartingCharacterIndexInSource() + getLengthOfMaskedSource();
	}
	
	
	
	
	
	
	public static int simpleTokensHashCode(WherefulToken t, int typingHashCode)
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + t.getStartingCharacterIndexInSource();
		result = prime * result + t.getLengthOfMaskedSource();
		return result;
	}
	
	/**
	 * Note that this only makes sense for comparing two tokens that come from the same source!!
	 * Otherwise the starts/lengths/etc. will be referring to different source texts!!  \o/
	 */
	public static boolean simpleTokensEqualNotCountingTyping(WherefulToken a, WherefulToken b)
	{
		if (a == b)
			return true;  //acounts for nulls!
		
		if (a == null || b == null)
			return false;
		
		return a.getStartingCharacterIndexInSource() == b.getStartingCharacterIndexInSource() && a.getLengthOfMaskedSource() == b.getLengthOfMaskedSource();
	}
}
