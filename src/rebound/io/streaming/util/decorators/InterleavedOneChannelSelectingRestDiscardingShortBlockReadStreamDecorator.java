package rebound.io.streaming.util.decorators;

import static rebound.math.SmallIntegerMathUtilities.*;
import java.io.EOFException;
import java.io.IOException;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ShortBlockReadStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentShortBlockReadStream;

public class InterleavedOneChannelSelectingRestDiscardingShortBlockReadStreamDecorator
extends AbstractStreamDecorator<ShortBlockReadStream>
implements IndolentShortBlockReadStream
{
	protected final int numberOfChannels;
	
	protected int currentChannelOfUnderlying;
	
	
	
	public InterleavedOneChannelSelectingRestDiscardingShortBlockReadStreamDecorator(ShortBlockReadStream underlying, int numberOfChannels, int currentChannelOfUnderlying, int desiredChannel)
	{
		this(underlying, numberOfChannels, progmod(currentChannelOfUnderlying - desiredChannel, numberOfChannels));
	}
	
	/**
	 * Our default desired-channel is 0 :3
	 * 
	 * (But if you think about it, this is the only constructor you need ;3 )
	 */
	public InterleavedOneChannelSelectingRestDiscardingShortBlockReadStreamDecorator(ShortBlockReadStream underlying, int numberOfChannels, int currentChannelOfUnderlying)
	{
		super(underlying);
		
		if (currentChannelOfUnderlying < 0)
			throw new IllegalArgumentException();
		if (currentChannelOfUnderlying >= numberOfChannels)
			throw new IllegalArgumentException();
		
		this.numberOfChannels = numberOfChannels;
		this.currentChannelOfUnderlying = currentChannelOfUnderlying;
	}
	
	public InterleavedOneChannelSelectingRestDiscardingShortBlockReadStreamDecorator(ShortBlockReadStream underlying, int numberOfChannels)
	{
		this(underlying, numberOfChannels, 0);
	}
	
	
	
	
	@Override
	public int readIndolent(short[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException
	{
		if (requestedLength == 0)
			return 0;
		
		
		if (syncUpToOurChannel())
			return 0;
		
		
		final int numberOfChannels = this.numberOfChannels;
		
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
			
			if (a == 0)
				return 0;
			
			int actualRead = ceilingDivision(a, numberOfChannels);
			this.currentChannelOfUnderlying = a - actualRead * numberOfChannels;
			
			for (int i = 1; i < actualRead; i++)
			{
				buffer[offset+i] = buffer[offset+i*numberOfChannels];
			}
			
			return actualRead;
		}
		else
		{
			try
			{
				buffer[offset] = this.underlying.read();
			}
			catch (EOFException exc)
			{
				if (!this.underlying.isEOF())
					throw new ImpossibleException();
				
				return 0;
			}
			
			this.currentChannelOfUnderlying = 1;
			
			return 1;
		}
	}
	
	
	@Override
	public long skipIndolent(long requestedLength) throws IOException, ClosedStreamException
	{
		if (requestedLength == 0)
			return 0;
		
		
		if (syncUpToOurChannel())
			return 0;
		
		
		assert this.currentChannelOfUnderlying == 0;
		
		
		
		long r = requestedLength * this.numberOfChannels;
		
		long a = this.underlying.skip(r);
		
		if (a != r)
		{
			if (!this.underlying.isEOF())
				throw new ImpossibleException();
		}
		
		return a / this.numberOfChannels;  //flooring division :3
	}
	
	
	
	/**
	 * @return if we eof'd on the way!
	 */
	protected boolean syncUpToOurChannel() throws IOException
	{
		if (this.currentChannelOfUnderlying != 0)
		{
			int a = this.numberOfChannels - this.currentChannelOfUnderlying;
			
			boolean eof = this.underlying.skip(a) != a;
			
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
