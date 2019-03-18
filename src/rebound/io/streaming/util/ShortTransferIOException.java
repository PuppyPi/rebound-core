package rebound.io.streaming.util;

import java.io.IOException;

public class ShortTransferIOException
extends IOException
{
	private static final long serialVersionUID = 1L;
	
	
	
	protected final int amountRequested, amountRead;
	
	public ShortTransferIOException(int amountRequested, int amountRead)
	{
		this.amountRequested = amountRequested;
		this.amountRead = amountRead;
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
