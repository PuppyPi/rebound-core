package rebound.exceptions;

public class BinarySyntaxIOException
extends SyntaxIOException
{
	private static final long serialVersionUID = 1L;
	
	protected BinarySyntaxIOException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
	public static BinarySyntaxIOException inst()
	{
		return new BinarySyntaxIOException(null, null);
	}
	
	public static BinarySyntaxIOException inst(String message)
	{
		return new BinarySyntaxIOException(message, null);
	}
	
	public static BinarySyntaxIOException inst(Throwable cause)
	{
		return new BinarySyntaxIOException(null, cause);
	}
	
	/**
	 * Instantiates a syntax exception with the given message and cause (nothing is appended or prepended to the message).
	 * This is especially useful for wrapping exceptions thrown by lower-level formats and parsers (eg, an "XMLException", an "InvalidUnicodeException", etc.)
	 */
	public static BinarySyntaxIOException inst(String message, Throwable cause)
	{
		return new BinarySyntaxIOException(message, cause);
	}
	
	
	
	
	
	
	@Override
	public BinarySyntaxException toSyntaxRuntimeException()
	{
		return BinarySyntaxException.inst(getMessage(), getCause());
	}
}