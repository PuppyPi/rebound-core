package rebound.io.streaming.util.decorators;

import java.io.EOFException;
import java.io.IOException;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ShortBlockReadStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentShortBlockReadStream;

public class InterleavedAveragingToOneChannelSignedShortBlockReadStreamDecorator
extends AbstractStreamDecorator<ShortBlockReadStream>
implements IndolentShortBlockReadStream
{
	protected final int numberOfChannels;
	
	protected int currentChannelOfUnderlying = 0;
	protected long currentTotal = 0;
	
	
	public InterleavedAveragingToOneChannelSignedShortBlockReadStreamDecorator(ShortBlockReadStream underlying, int numberOfChannels)
	{
		super(underlying);
		this.numberOfChannels = numberOfChannels;
	}
	
	
	
	
	@Override
	public int readIndolent(short[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException
	{
		if (requestedLength == 0)
			return 0;
		
		
		final int numberOfChannels = this.numberOfChannels;
		
		
		//Finish the current read if a partial one in progress :3
		if (this.currentChannelOfUnderlying != 0)
		{
			while (this.currentChannelOfUnderlying != 0)
			{
				try
				{
					this.currentTotal += this.underlying.read();
				}
				catch (EOFException exc)
				{
					if (!this.underlying.isEOF())
						throw new ImpossibleException();
					
					return 0;
				}
				
				this.currentChannelOfUnderlying = (this.currentChannelOfUnderlying + 1) % numberOfChannels;
			}
			
			buffer[offset] = (short)(this.currentTotal / numberOfChannels);  //Todo customizable kinds of division (eg, rounding, flooring, ceilinging, etc.!)
			assert this.currentChannelOfUnderlying == 0;
			this.currentTotal = 0;
			
			return 1;
		}
		
		
		
		
		//A clean new read! :D
		else
		{
			assert this.currentChannelOfUnderlying == 0;
			
			
			int numberOfOurs = requestedLength / numberOfChannels;  //flooring division :3
			
			if (numberOfOurs != 0)
			{
				int r = numberOfOurs * numberOfChannels;
				
				int a = this.underlying.read(buffer, offset, r);
				
				if (a != r)
				{
					if (!this.underlying.isEOF())
						throw new ImpossibleException();
				}
				
				int actualRead = a / numberOfChannels;  //flooring division :3
				this.currentChannelOfUnderlying = a % numberOfChannels;
				
				for (int i = 0; i < actualRead; i++)
				{
					long total = 0;
					
					int o = offset + i*numberOfChannels;
					
					for (int e = 0; e < numberOfChannels; e++)
					{
						total += buffer[o+e];
					}
					
					buffer[offset+i] = (short)(total / numberOfChannels);  //Todo customizable kinds of division (eg, rounding, flooring, ceilinging, etc.!)
				}
				
				return actualRead;
			}
			else
			{
				//Just read 1 and let the partial read handler at the top handle it on the next invocation of our indolence X3
				
				try
				{
					this.currentTotal = this.underlying.read();
				}
				catch (EOFException exc)
				{
					if (!this.underlying.isEOF())
						throw new ImpossibleException();
				}
				
				this.currentChannelOfUnderlying = 1;
				
				return 0;
			}
		}
	}
	
	
	@Override
	public long skipIndolent(long requestedLength) throws IOException, ClosedStreamException
	{
		if (requestedLength == 0)
			return 0;
		
		
		if (skipUpToOurChannel())
			return 0;
		
		
		assert this.currentChannelOfUnderlying == 0;
		
		
		
		long r = requestedLength * this.numberOfChannels;
		
		long a = this.underlying.skip(r);
		
		if (a != r)
		{
			if (!this.underlying.isEOF())
				throw new ImpossibleException();
		}
		
		return a / this.numberOfChannels;
	}
	
	
	
	/**
	 * @return if we eof'd on the way!
	 */
	protected boolean skipUpToOurChannel() throws IOException
	{
		if (this.currentChannelOfUnderlying != 0)
		{
			int a = this.numberOfChannels - this.currentChannelOfUnderlying;
			
			boolean eof = this.underlying.skip(1) != a;
			
			if (eof)
			{
				if (!this.underlying.isEOF())
					throw new ImpossibleException();
			}
			else
			{
				this.currentChannelOfUnderlying = 0;
			}
			
			return eof;
		}
		else
		{
			return false;
		}
	}
	
	
	
	
	
	
	
	
	
	protected short[] buf1 = new short[1];
	@Override
	public short read() throws EOFException, IOException, ClosedStreamException
	{
		int r = read(this.buf1);
		
		if (r == 0)
			throw new EOFException();
		else if (r == 1)
			return this.buf1[0];
		else
			throw new ImpossibleException();
	}
}
