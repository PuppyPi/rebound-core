/*
 * Created on Jul 16, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.concurrency;

import rebound.exceptions.ReturnPath.SingletonReturnPath;

/* <<<
srp

TimeoutReturnPath
 */
public class TimeoutReturnPath
extends SingletonReturnPath
{
	private static final long serialVersionUID = 1L;
	
	public static final TimeoutReturnPath I = new TimeoutReturnPath();
	protected TimeoutReturnPath() {}
	
	
	public static class TimeoutException
	extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		
		public TimeoutException()
		{
			super();
		}
		
		public TimeoutException(String message)
		{
			super(message);
		}
		
		public TimeoutException(Throwable cause)
		{
			super(cause);
		}
		
		public TimeoutException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
	
	@Override
	public TimeoutException toException()
	{
		return new TimeoutException();
	}
}
// >>>
