package rebound.io;

public class LongReadIOException
extends LongTransferIOException
{
	private static final long serialVersionUID = 1L;
	
	public LongReadIOException(int amountRequested, int amountActual)
	{
		super(amountRequested, amountActual);
	}
}
