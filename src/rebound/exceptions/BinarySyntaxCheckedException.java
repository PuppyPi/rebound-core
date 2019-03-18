package rebound.exceptions;

public class BinarySyntaxCheckedException
extends SyntaxCheckedException
{
	private static final long serialVersionUID = 1L;
	
	protected BinarySyntaxCheckedException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
	public static BinarySyntaxCheckedException inst()
	{
		return new BinarySyntaxCheckedException(null, null);
	}
	
	public static BinarySyntaxCheckedException inst(String message)
	{
		return new BinarySyntaxCheckedException(message, null);
	}
	
	public static BinarySyntaxCheckedException inst(Throwable cause)
	{
		return new BinarySyntaxCheckedException(null, cause);
	}
	
	/**
	 * Instantiates a syntax exception with the given message and cause (nothing is appended or prepended to the message).
	 * This is especially useful for wrapping exceptions thrown by lower-level formats and parsers (eg, an "XMLException", an "InvalidUnicodeException", etc.)
	 */
	public static BinarySyntaxCheckedException inst(String message, Throwable cause)
	{
		return new BinarySyntaxCheckedException(message, cause);
	}
	
	
	
	
	
	
	@Override
	public BinarySyntaxException toSyntaxRuntimeException()
	{
		return BinarySyntaxException.inst(getMessage(), this);
	}
}
