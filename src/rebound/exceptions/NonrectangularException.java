package rebound.exceptions;

/**
 * Eg, when you have an array of arrays, but the inner arrays are not all the same length!!  \o/
 * (In a context where they're supposed to be XD )
 */
public class NonrectangularException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NonrectangularException()
	{
	}
	
	public NonrectangularException(String message)
	{
		super(message);
	}
	
	public NonrectangularException(Throwable cause)
	{
		super(cause);
	}
	
	public NonrectangularException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
