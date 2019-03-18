/*
 * Created on Jan 17, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * Like {@link NotFoundCheckedException} but for cases when it definitely *should* be found (and otherwise is some coding error // bug xP ) :>
 * 
 * @author RProgrammer
 */
public class NotFoundException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NotFoundException()
	{
		super();
	}
	
	public NotFoundException(String message)
	{
		super(message);
	}
	
	public NotFoundException(Throwable cause)
	{
		super(cause);
	}
	
	public NotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
