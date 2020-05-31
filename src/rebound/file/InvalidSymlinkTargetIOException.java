package rebound.file;

import java.io.IOException;

public class InvalidSymlinkTargetIOException
extends IOException
{
	private static final long serialVersionUID = 1L;
	
	public InvalidSymlinkTargetIOException()
	{
		super();
	}
	
	public InvalidSymlinkTargetIOException(String message)
	{
		super(message);
	}
	
	public InvalidSymlinkTargetIOException(Throwable cause)
	{
		super(cause);
	}
	
	public InvalidSymlinkTargetIOException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
