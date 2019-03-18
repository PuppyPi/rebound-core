package rebound.util.collections;

import rebound.exceptions.ReturnPath.SingletonReturnPath;

public class FilterAwayReturnPath
extends SingletonReturnPath
{
	private static final long serialVersionUID = 1L;
	
	public static final FilterAwayReturnPath I = new FilterAwayReturnPath();
	
	
	protected FilterAwayReturnPath()
	{
	}
	
	
	@Override
	public RuntimeException toException()
	{
		return new FilterAwayException();
	}
	
	
	
	
	public static class FilterAwayException
	extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		
		public FilterAwayException()
		{
			super();
		}
		
		public FilterAwayException(String message, Throwable cause)
		{
			super(message, cause);
		}
		
		public FilterAwayException(String message)
		{
			super(message);
		}
		
		public FilterAwayException(Throwable cause)
		{
			super(cause);
		}
	}
}
