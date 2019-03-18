package rebound.exceptions;

public class StructureFormatException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public StructureFormatException()
	{
		super();
	}
	
	public StructureFormatException(String message)
	{
		super(message);
	}
	
	public StructureFormatException(Throwable cause)
	{
		super(cause);
	}
	
	public StructureFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
