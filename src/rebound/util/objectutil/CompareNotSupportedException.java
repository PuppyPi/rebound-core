package rebound.util.objectutil;

/**
 * Analogous to {@link CloneNotSupportedException} :>
 * @author RProgrammer
 */
public class CompareNotSupportedException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public CompareNotSupportedException()
	{
	}
	
	public CompareNotSupportedException(String message)
	{
		super(message);
	}
	
	public CompareNotSupportedException(Throwable cause)
	{
		super(cause);
	}
	
	public CompareNotSupportedException(String message, Throwable cause)
	{
		super(message, cause);
	}
}