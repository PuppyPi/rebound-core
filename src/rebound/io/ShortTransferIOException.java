package rebound.io;

import java.io.IOException;

public class ShortTransferIOException
extends IOException
{
	private static final long serialVersionUID = 1L;
	
	
	
	protected final int amountRequested, amountRead;
	
	public ShortTransferIOException(int amountRequested, int amountActual)
	{
		this.amountRequested = amountRequested;
		this.amountRead = amountActual;
	}
	
	
	public int getAmountRequested()
	{
		return this.amountRequested;
	}
	
	public int getAmountRead()
	{
		return this.amountRead;
	}
}
