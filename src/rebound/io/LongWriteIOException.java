package rebound.io;

public class LongWriteIOException
extends LongTransferIOException
{
	private static final long serialVersionUID = 1L;
	
	public LongWriteIOException(int amountRequested, int amountActual)
	{
		super(amountRequested, amountActual);
	}
}
