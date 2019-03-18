package rebound.io.streaming.util;

public class ShortReadIOException
extends ShortTransferIOException
{
	private static final long serialVersionUID = 1L;
	
	public ShortReadIOException(int amountRequested, int amountRead)
	{
		super(amountRequested, amountRead);
	}
}
