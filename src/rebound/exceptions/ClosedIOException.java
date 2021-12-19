package rebound.exceptions;

import java.io.IOException;

public class ClosedIOException
extends IOException
implements ClosedExceptionType
{
	private static final long serialVersionUID = 1L;
	
	public ClosedIOException()
	{
		super();
	}
	
	public ClosedIOException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ClosedIOException(String message)
	{
		super(message);
	}
	
	public ClosedIOException(Throwable cause)
	{
		super(cause);
	}
}
