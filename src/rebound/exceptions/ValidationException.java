/*
 * Created on Feb 4, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

public class ValidationException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public ValidationException()
	{
	}
	
	public ValidationException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ValidationException(String message)
	{
		super(message);
	}
	
	public ValidationException(Throwable cause)
	{
		super(cause);
	}
	
	
	
	/**
	 * Ie, a validation exception that's not supposed to happen / the reason it did was a bug in the code, not bad input
	 * heheh ^^''
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static class ImpossibleValidationException
	extends ValidationException
	{
		private static final long serialVersionUID = 1L;
		
		public ImpossibleValidationException()
		{
		}
		
		public ImpossibleValidationException(String message, Throwable cause)
		{
			super(message, cause);
		}
		
		public ImpossibleValidationException(String message)
		{
			super(message);
		}
		
		public ImpossibleValidationException(Throwable cause)
		{
			super(cause);
		}
	}
}