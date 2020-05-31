package rebound.file;

import java.io.IOException;

public class InvalidPathNameIOException
extends IOException
{
	private static final long serialVersionUID = 1L;
	
	public InvalidPathNameIOException()
	{
		super();
	}
	
	public InvalidPathNameIOException(String message)
	{
		super(message);
	}
	
	public InvalidPathNameIOException(Throwable cause)
	{
		super(cause);
	}
	
	public InvalidPathNameIOException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
