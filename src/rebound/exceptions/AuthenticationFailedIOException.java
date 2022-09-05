package rebound.exceptions;

import java.io.IOException;

public class AuthenticationFailedIOException
extends IOException
{
	private static final long serialVersionUID = 1l;
	
	public AuthenticationFailedIOException()
	{
		super();
	}
	
	public AuthenticationFailedIOException(String message)
	{
		super(message);
	}
	
	public AuthenticationFailedIOException(Throwable cause)
	{
		super(cause);
	}
	
	public AuthenticationFailedIOException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
