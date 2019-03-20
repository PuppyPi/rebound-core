package rebound.text.parsing.apis.tokenstream;

import javax.annotation.Nonnull;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.SignalType;

@SignalType
public interface Tokenlike
{
	/**
	 * Note: likely lazily computed! :D
	 */
	@Nonnull
	public String getMaskedSource();
	
	
	
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser  //because this impl might easily be ill-performing!
	public default char getCharacterInMaskedSource(int index)
	{
		return getMaskedSource().charAt(index);
	}
	
	
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser  //because this impl might easily be ill-performing!
	public default int getLengthOfMaskedSource()
	{
		return getMaskedSource().length();
	}
}
