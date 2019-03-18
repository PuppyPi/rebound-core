package rebound.io.streaming.util.decorators;

import static rebound.math.SmallIntegerMathUtilities.*;
import java.io.EOFException;
import java.io.IOException;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentByteBlockWriteStream;

/**
 * A stream decorator that promises never to read amount but a multiple of a given number from the underlying stream, and only to use read([], int, int), not read() or any of the other read() methods! ^,^
 */
public class MultipleWritingByteBlockWriteStreamDecorator
extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
implements IndolentByteBlockWriteStream
{
	protected final int multiplesOfThis;
	protected final byte[] intermediateBuffer;
	protected int intermediateBufferSize;
	
	public MultipleWritingByteBlockWriteStreamDecorator(ByteBlockWriteStream underlying, int multiplesOfThis)
	{
		super(underlying);
		this.multiplesOfThis = multiplesOfThis;
		this.intermediateBuffer = new byte[multiplesOfThis];
	}
	
	
	
	@Override
	public int writeIndolent(byte[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException
	{
		if (requestedLength < 0)
			throw new IllegalArgumentException();
		if (requestedLength == 0)
			return 0;
		
		
		if (this.intermediateBufferSize != 0)
		{
			int remainingInIntermediateBuffer = this.multiplesOfThis - this.intermediateBufferSize;
			
			int l = least(remainingInIntermediateBuffer, requestedLength);
			
			System.arraycopy(buffer, offset, this.intermediateBuffer, this.intermediateBufferSize, l);
			
			this.intermediateBufferSize += l;
			
			if (this.intermediateBufferSize == this.multiplesOfThis)
			{
				int r = this.underlying.write(this.intermediateBuffer, 0, this.multiplesOfThis);
				if (r < this.multiplesOfThis)
				{
					if (!this.underlying.isEOF())
						throw new ImpossibleException();
					
					return 0;  //the earler methods should really have returned a eofingly-short write as well but eh, what're you gonna do? XD'
				}
				
				this.intermediateBufferSize = 0;
			}
			
			return l;
		}
		else
		{
			if (requestedLength >= this.multiplesOfThis)
			{
				int a = requestedLength / this.multiplesOfThis;
				int aa = a * this.multiplesOfThis;
				
				return this.underlying.write(buffer, offset, aa);
			}
			else
			{
				assert this.intermediateBufferSize == 0;
				System.arraycopy(buffer, offset, this.intermediateBuffer, 0, requestedLength);
				
				this.intermediateBufferSize = requestedLength;
				
				return requestedLength;
			}
		}
	}
	
	
	
	
	
	@Override
	public long skipIndolent(long requestedLength) throws IOException, ClosedStreamException
	{
		return skip(requestedLength);
	}
	
	@Override
	public long skip(long requestedLength) throws IOException, ClosedStreamException
	{
		if (requestedLength < 0)
			throw new IllegalArgumentException();
		if (requestedLength == 0)
			return 0;
		
		
		long skippedByWriting = 0;
		
		while (skippedByWriting < requestedLength && this.intermediateBufferSize != 0)
		{
			this.write((byte)0);
			skippedByWriting++;
		}
		
		if (requestedLength > 0)
		{
			long a = requestedLength / this.multiplesOfThis;
			
			long s = this.underlying.skip(a);
			if (s < a)
			{
				if (!this.underlying.isEOF())
					throw new ImpossibleException();
			}
			else
			{
				long r = requestedLength % this.multiplesOfThis;
				
				for (int i = 0; i < r; i++)
				{
					try
					{
						this.write((byte)0);
					}
					catch (EOFException exc)
					{
						break;
					}
					
					skippedByWriting++;
				}
			}
			
			return a * this.multiplesOfThis + skippedByWriting;
		}
		else
		{
			return skippedByWriting;
		}
	}
	
	
	
	
	
	
	
	protected byte[] buf1 = new byte[1];
	@Override
	public void write(byte unit) throws EOFException, IOException, ClosedStreamException
	{
		this.buf1[0] = unit;
		if (write(this.buf1) == 0)
			throw new EOFException();
	}
}
