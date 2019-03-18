package rebound.io.streaming.util.decorators;

import static rebound.math.SmallIntegerMathUtilities.*;
import java.io.EOFException;
import java.io.IOException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentByteBlockReadStream;

/**
 * A stream decorator that promises never to read amount but a multiple of a given number from the underlying stream, and only to use read([], int, int), not read() or any of the other read() methods! ^,^
 */
public class MultipleReadingByteBlockReadStreamDecorator
extends AbstractStreamDecorator<ByteBlockReadStream>
implements IndolentByteBlockReadStream
{
	protected final int multiplesOfThis;
	protected final byte[] intermediateBuffer;
	protected int intermediateBufferPosition, intermediateBufferSize;
	
	public MultipleReadingByteBlockReadStreamDecorator(ByteBlockReadStream underlying, int multiplesOfThis)
	{
		super(underlying);
		this.multiplesOfThis = multiplesOfThis;
		this.intermediateBuffer = new byte[multiplesOfThis];
	}
	
	
	@Override
	public byte read() throws EOFException, IOException, ClosedStreamException
	{
		if (this.intermediateBufferSize == 0)
		{
			refillIntermediateBuffer();
			
			if (this.intermediateBufferSize == 0)
			{
				if (!super.isEOF())
					throw new AssertionError();
				
				throw new EOFException();
			}
		}
		
		
		byte u = this.intermediateBuffer[this.intermediateBufferPosition];
		
		this.intermediateBufferSize--;
		this.intermediateBufferPosition++;
		
		return u;
	}
	
	
	@Override
	public int readIndolent(byte[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException
	{
		if (requestedLength < 0)
			throw new IllegalArgumentException();
		
		int multiplesOfThis = this.multiplesOfThis;
		
		if (this.intermediateBufferSize == 0)
		{
			if (requestedLength < multiplesOfThis)
			{
				if (requestedLength == 0)
					return 0;
				
				refillIntermediateBuffer();
				
				if (this.intermediateBufferSize == 0)
				{
					if (!super.isEOF())
						throw new AssertionError();
					
					return 0;
				}
			}
			else
			{
				int fullAmount = requestedLength / multiplesOfThis;  //flooring division :3
				
				return this.underlying.read(buffer, offset, fullAmount * multiplesOfThis);
			}
		}
		
		
		//Read from the intermediate buffer! :D
		{
			int l = least(requestedLength, this.intermediateBufferSize);
			
			System.arraycopy(this.intermediateBuffer, this.intermediateBufferPosition, buffer, offset, l);
			
			this.intermediateBufferPosition += l;
			this.intermediateBufferSize -= l;
			
			return l;
		}
	}
	
	
	@Override
	public long skipIndolent(long requestedLength) throws IOException, ClosedStreamException
	{
		return StreamImplUtilities.skipByDiscarding(this, requestedLength);
	}
	
	
	
	protected void refillIntermediateBuffer() throws IOException
	{
		this.intermediateBufferSize = this.underlying.read(this.intermediateBuffer, 0, this.intermediateBuffer.length);
		this.intermediateBufferPosition = 0;
	}
	
	
	
	
	
	@Override
	public boolean isEOF() throws IOException, ClosedStreamException
	{
		return super.isEOF() && this.intermediateBufferSize == 0;
	}
}
