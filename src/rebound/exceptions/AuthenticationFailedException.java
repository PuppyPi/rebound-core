package rebound.exceptions;

import java.io.IOException;

public class AuthenticationFailedException
extends IOException
{
	private static final long serialVersionUID = 1l;
	
	public AuthenticationFailedException()
	{
		super();
	}
	
	public AuthenticationFailedException(String message)
	{
		super(message);
	}
	
	public AuthenticationFailedException(Throwable cause)
	{
		super(cause);
	}
	
	public AuthenticationFailedException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
