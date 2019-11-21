package rebound.exceptions;

/**
 * "It's not *necessarily* an error..
 * ..but let's be honest it's probably an error XD"
 * 
 * Nominally, {@link StackOverflowError} would be considered a {@link HeuristicException} :3
 */
public class HeuristicException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public HeuristicException()
	{
		super();
	}

	protected HeuristicException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HeuristicException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public HeuristicException(String message)
	{
		super(message);
	}

	public HeuristicException(Throwable cause)
	{
		super(cause);
	}
}
