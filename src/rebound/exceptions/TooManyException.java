/*
 * Created on
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * Eg, when filtering a list, expecting to find a single unique value, but multiple are found!
 * (in that case, see also {@link NotFoundException} ^_^ )
 * 
 * @author Puppy Pie ^_^
 */
public class TooManyException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	
	public TooManyException()
	{
		super();
	}
	
	public TooManyException(String message)
	{
		super(message);
	}
	
	public TooManyException(Throwable cause)
	{
		super(cause);
	}
	
	public TooManyException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
