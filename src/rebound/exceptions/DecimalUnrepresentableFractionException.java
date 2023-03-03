package rebound.exceptions;

public class DecimalUnrepresentableFractionException
extends ArithmeticException
{
	private static final long serialVersionUID = 1L;
	
	public DecimalUnrepresentableFractionException()
	{
		super();
	}
	
	public DecimalUnrepresentableFractionException(String message)
	{
		super(message);
	}
}
