package rebound.file;

import java.io.IOException;

public class SymlinkCycleIOException
extends IOException
{
	private static final long serialVersionUID = 1L;

	public SymlinkCycleIOException()
	{
		super();
	}

	public SymlinkCycleIOException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SymlinkCycleIOException(String message)
	{
		super(message);
	}

	public SymlinkCycleIOException(Throwable cause)
	{
		super(cause);
	}
}
