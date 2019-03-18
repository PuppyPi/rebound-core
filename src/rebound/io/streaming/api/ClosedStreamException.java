/*
 * Created on May 17, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api;

/**
 * This is thrown if an operation is attempted on a {@link Stream} that {@link Stream#isClosed() is closed}.
 * It is a RuntimeException becuase this should not happen unless there is an internal flaw in the logic of a program.  (As opposed to an IOException which may even be anticipated)
 * @author RProgrammer
 */
public class ClosedStreamException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public ClosedStreamException()
	{
		super("An operation was attempted on a closed stream.");
	}
}
