package rebound.io.ucs4;

public class UTF16EncodingException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public UTF16EncodingException()
	{
		super();
	}
	
	public UTF16EncodingException(String message)
	{
		super(message);
	}
	
	public UTF16EncodingException(Throwable cause)
	{
		super(cause);
	}
	
	public UTF16EncodingException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
