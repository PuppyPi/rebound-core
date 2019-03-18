/*
 * Created on Jun 9, 2012
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

import java.io.IOException;

/**
 * In most cases, an error in binary protocol syntax (eg, incorrect magic number, invalid enum constant, etc.) is handled like any other I/O error;
 * this is a class conceptually similar to {@link SyntaxCheckedException}, but extending {@link IOException} :>
 * @author RProgrammer
 */
public abstract class SyntaxIOException
extends IOException
{
	private static final long serialVersionUID = 1L;
	
	public SyntaxIOException()
	{
	}
	
	public SyntaxIOException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public SyntaxIOException(String message)
	{
		super(message);
	}
	
	public SyntaxIOException(Throwable cause)
	{
		super(cause);
	}
	
	
	public abstract SyntaxException toSyntaxRuntimeException();
}



//Old
/*
/**
 * Thrown like a SyntaxException when a structural error is encountered while reading the network stream.
 * @author RProgrammer
 */
