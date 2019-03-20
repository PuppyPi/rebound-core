package rebound.text.parsing.apis.tokenstream;

import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.text.StringUtilities;

@FunctionalityType
public interface SimpleTokenWithLineAndColumnNumbers
extends WherefulToken
{
	/**
	 * @return the 1-based column number or -1 if unknown :33
	 */
	public static int getStartingLineNumberInSource1Based(Object token)
	{
		int n = -1;
		
		if (token instanceof SimpleTokenWithLineAndColumnNumbers)
			n = ((SimpleTokenWithLineAndColumnNumbers) token).getStartingLineNumberInSource1Based();
		
		
		if (n != -1)
			return n;
		else
		{
			if (token instanceof SimpleTokenWithMemoryOfOriginalSource)
			{
				int start = ((WherefulToken)token).getStartingCharacterIndexInSource();
				
				return StringUtilities.getLineNumber(start, ((SimpleTokenWithMemoryOfOriginalSource) token).getOriginalSource());
			}
			else
			{
				return -1;
			}
		}
	}
	
	
	
	/**
	 * @return the 1-based column number or -1 if unknown :33
	 */
	public static int getStartingColumnNumberInSource1Based(Object token)
	{
		int n = -1;
		
		if (token instanceof SimpleTokenWithLineAndColumnNumbers)
			n = ((SimpleTokenWithLineAndColumnNumbers) token).getStartingColumnNumberInSource1Based();
		
		
		if (n != -1)
			return n;
		else
		{
			if (token instanceof SimpleTokenWithMemoryOfOriginalSource)
			{
				int start = ((WherefulToken)token).getStartingCharacterIndexInSource();
				
				return StringUtilities.getColumnNumber(start, ((SimpleTokenWithMemoryOfOriginalSource) token).getOriginalSource());
			}
			else
			{
				return -1;
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * + Note that this can return -1 even if {@link #isSimpleTokenWithLineAndColumnNumbers()} returns true!!
	 * @return the 1-based column number or -1 if unknown :33
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default int getStartingLineNumberInSource1Based()
	{
		return -1;
	}
	
	
	/**
	 * + Note that this can return -1 even if {@link #isSimpleTokenWithLineAndColumnNumbers()} returns true!!
	 * @return the 1-based column number or -1 if unknown :33
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default int getStartingColumnNumberInSource1Based()
	{
		return -1;
	}
	
	
	
	
	
	
	
	//<<< tp SimpleTokenWithLineAndColumnNumbers
	@TraitPredicate
	public default boolean isSimpleTokenWithLineAndColumnNumbers()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof SimpleTokenWithLineAndColumnNumbers && ((SimpleTokenWithLineAndColumnNumbers)x).isSimpleTokenWithLineAndColumnNumbers();
	}
	//>>>
}
