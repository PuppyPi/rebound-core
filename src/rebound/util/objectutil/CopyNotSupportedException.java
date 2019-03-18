package rebound.util.objectutil;

/**
 * Analogous to {@link CloneNotSupportedException} :>
 * @author RProgrammer
 */
public class CopyNotSupportedException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public CopyNotSupportedException()
	{
	}
	
	public CopyNotSupportedException(String message)
	{
		super(message);
	}
	
	public CopyNotSupportedException(Throwable cause)
	{
		super(cause);
	}
	
	public CopyNotSupportedException(String message, Throwable cause)
	{
		super(message, cause);
	}
}