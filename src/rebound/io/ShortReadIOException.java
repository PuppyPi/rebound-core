package rebound.io;

public class ShortReadIOException
extends ShortTransferIOException
{
	private static final long serialVersionUID = 1L;
	
	public ShortReadIOException(int amountRequested, int amountActual)
	{
		super(amountRequested, amountActual);
	}
}
