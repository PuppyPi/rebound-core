package rebound.exceptions;

public class BinarySyntaxException
extends SyntaxException
{
	private static final long serialVersionUID = 1L;
	
	
	public BinarySyntaxException()
	{
		super();
	}
	
	public BinarySyntaxException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public BinarySyntaxException(String message)
	{
		super(message);
	}
	
	public BinarySyntaxException(Throwable cause)
	{
		super(cause);
	}
	
	
	
	
	public static BinarySyntaxException inst()
	{
		return new BinarySyntaxException(null, null);
	}
	
	public static BinarySyntaxException inst(String message)
	{
		return new BinarySyntaxException(message, null);
	}
	
	public static BinarySyntaxException inst(Throwable cause)
	{
		return new BinarySyntaxException(null, cause);
	}
	
	/**
	 * Instantiates a syntax exception with the given message and cause (nothing is appended or prepended to the message).
	 * This is especially useful for wrapping exceptions thrown by lower-level formats and parsers (eg, an "XMLException", an "InvalidUnicodeException", etc.)
	 */
	public static BinarySyntaxException inst(String message, Throwable cause)
	{
		return new BinarySyntaxException(message, cause);
	}
}
