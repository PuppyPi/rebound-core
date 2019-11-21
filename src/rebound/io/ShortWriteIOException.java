package rebound.io;

public class ShortWriteIOException
extends ShortTransferIOException
{
	private static final long serialVersionUID = 1L;
	
	public ShortWriteIOException(int amountRequested, int amountActual)
	{
		super(amountRequested, amountActual);
	}
}
