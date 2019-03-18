/*
 * Created on Feb 24, 2012
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * Specifically for enums :>
 * @author RProgrammer
 */
public class UnexpectedHardcodedEnumValueException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	protected Object offendingValue;
	
	public UnexpectedHardcodedEnumValueException()
	{
		this.offendingValue = null;
	}
	
	public UnexpectedHardcodedEnumValueException(Object offendingValue)
	{
		this.offendingValue = offendingValue;
	}
	
	public Object getOffendingValue()
	{
		return this.offendingValue;
	}
}
