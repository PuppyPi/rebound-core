package rebound.io;

import java.io.IOException;

/**
 * This is much worse than a {@link ShortTransferIOException}, because (eg, if you're the implemtnation of a read() method, reading into an array) some data must usually be deleted/discarded!
 */
public class LongTransferIOException
extends IOException
{
	private static final long serialVersionUID = 1L;
	
	
	
	protected final int amountRequested, amountRead;
	
	public LongTransferIOException(int amountRequested, int amountActual)
	{
		if (amountActual <= amountRequested)
			throw new IllegalArgumentException("That's not a long transfer! XD");
		
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
