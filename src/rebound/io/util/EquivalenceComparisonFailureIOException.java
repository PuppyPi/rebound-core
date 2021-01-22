package rebound.io.util;

import java.io.IOException;
import rebound.annotations.semantic.SignalType;

/**
 * This indicates the streams are not equivalent!
 * 
 * This being thrown by a read()/write() method is how you can stop a stream-comparison in progresss if it fails and there's no need to continue :3
 */
@SignalType
public class EquivalenceComparisonFailureIOException
extends IOException
{
	private static final long serialVersionUID = 1L;
	
	public EquivalenceComparisonFailureIOException()
	{
		super();
	}
	
	public EquivalenceComparisonFailureIOException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public EquivalenceComparisonFailureIOException(String message)
	{
		super(message);
	}
	
	public EquivalenceComparisonFailureIOException(Throwable cause)
	{
		super(cause);
	}
}
