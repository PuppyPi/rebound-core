/*
 * Created on Feb 24, 2012
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * For when there is a hardcoded finite set of enum values, other constants, or even subclasses that some value can take on, and an instance is encountered outside that set.
 * This is a classic and true bug; it is usually caused by adding functionality (and adding allowable values) but not updating a switch-statement, or if/else tree, etc.
 * @author RProgrammer
 */
public class UnexpectedHardcodedValueException
extends ImpossibleException
{
	private static final long serialVersionUID = 1L;
	
	public UnexpectedHardcodedValueException()
	{
	}
	
	public UnexpectedHardcodedValueException(String message)
	{
		super(message);
	}
	
	public UnexpectedHardcodedValueException(Throwable cause)
	{
		super(cause);
	}
	
	public UnexpectedHardcodedValueException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
