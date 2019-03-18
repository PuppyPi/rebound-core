package rebound.io.streaming.util;

public class ShortWriteIOException
extends ShortTransferIOException
{
	private static final long serialVersionUID = 1L;
	
	public ShortWriteIOException(int amountRequested, int amountRead)
	{
		super(amountRequested, amountRead);
	}
}
