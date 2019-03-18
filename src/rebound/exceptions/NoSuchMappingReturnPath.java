/*
 * Created on Jan 29, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

import rebound.exceptions.ReturnPath.SingletonReturnPath;

public class NoSuchMappingReturnPath
extends SingletonReturnPath
{
	private static final long serialVersionUID = 1L;
	
	public static final NoSuchMappingReturnPath I = new NoSuchMappingReturnPath();
	protected NoSuchMappingReturnPath() {}
	
	
	public static class NoSuchMappingException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		
		public NoSuchMappingException()
		{
		}
		
		public NoSuchMappingException(String message)
		{
			super(message);
		}
		
		public NoSuchMappingException(Throwable cause)
		{
			super(cause);
		}
		
		public NoSuchMappingException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
	
	@Override
	public NoSuchMappingException toException()
	{
		return new NoSuchMappingException();
	}
}
