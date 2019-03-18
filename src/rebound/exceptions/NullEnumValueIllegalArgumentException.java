/*
 * Created on May 28, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * For when someone passed a <code>null</code> value in an enum argument >,>
 * @author RProgrammer
 */
public class NullEnumValueIllegalArgumentException
extends IllegalArgumentException
{
	private static final long serialVersionUID = 1L;
	
	public NullEnumValueIllegalArgumentException(Class<? extends Enum> enumClass)
	{
		this("Illegal null value for enum type: "+enumClass.getName());
	}
	
	public NullEnumValueIllegalArgumentException()
	{
	}
	
	public NullEnumValueIllegalArgumentException(Throwable cause)
	{
		super(cause);
	}
	
	public NullEnumValueIllegalArgumentException(String s)
	{
		super(s);
	}
	
	public NullEnumValueIllegalArgumentException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
