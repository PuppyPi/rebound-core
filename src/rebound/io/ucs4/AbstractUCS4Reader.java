package rebound.io.ucs4;

import java.io.IOException;

public abstract class AbstractUCS4Reader
implements UCS4Reader
{
	/** Maximum skip-buffer size */
	protected static final int maxSkipBufferSize = 8192;
	
	/** Skip buffer, null until allocated */
	protected int[] skipBuffer = null;
	
	
	
	@Override
	public long skip(long amountRequested) throws IOException
	{
		if (amountRequested < 0L)
			throw new IllegalArgumentException("skip value is negative");
		
		int skipBufferSize = (int)Math.min(amountRequested, maxSkipBufferSize);
		if ((this.skipBuffer == null) || (this.skipBuffer.length < skipBufferSize))
			this.skipBuffer = new int[skipBufferSize];
		
		long remaining = amountRequested;
		while (remaining > 0)
		{
			int amt = read(this.skipBuffer, 0, (int)Math.min(remaining, skipBufferSize));
			if (amt == -1)
				break;
			remaining -= amt;
		}
		return amountRequested - remaining;
	}
}
