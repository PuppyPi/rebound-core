package rebound.exceptions;

public class AnnotationStructureFormatException
extends StructureFormatException
{
	private static final long serialVersionUID = 1L;
	
	public AnnotationStructureFormatException()
	{
		super();
	}
	
	public AnnotationStructureFormatException(String message)
	{
		super(message);
	}
	
	public AnnotationStructureFormatException(Throwable cause)
	{
		super(cause);
	}
	
	public AnnotationStructureFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
