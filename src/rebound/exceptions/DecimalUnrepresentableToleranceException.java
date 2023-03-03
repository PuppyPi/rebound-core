package rebound.exceptions;

public class DecimalUnrepresentableToleranceException
extends ArithmeticException
{
	private static final long serialVersionUID = 1L;
	
	public DecimalUnrepresentableToleranceException()
	{
		super();
	}
	
	public DecimalUnrepresentableToleranceException(String message)
	{
		super(message);
	}
}
