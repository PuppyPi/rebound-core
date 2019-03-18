package rebound.io.streaming.util.implhelp;

import static rebound.math.SmallIntegerMathUtilities.*;
import java.io.EOFException;
import java.io.IOException;
import javax.annotation.Nullable;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentShortBlockReadStream;
import rebound.util.collections.Slice;

public abstract class AbstractUncontrollableUnderlyingShortBlockReadStream
extends AbstractStream
implements IndolentShortBlockReadStream
{
	protected boolean eof;
	
	protected int excessBufferPos;
	protected int excessBufferSize;
	protected short[] excessBuffer;
	
	
	@Override
	public boolean isEOF() throws IOException, ClosedStreamException
	{
		return this.eof;
	}
	
	
	@Override
	public int readIndolent(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
	{
		if (this.eof)
			return 0;
		
		if (this.excessBufferSize == 0)
		{
			Slice<short[]> u = readUncontrollableLength(length);
			
			if (u == null)
			{
				this.eof = true;
				return 0;
			}
			
			int l = least(u.getLength(), length);
			
			System.arraycopy(u.getUnderlying(), u.getOffset(), buffer, offset, l);
			
			int remainingSuperfluouslyRead = u.getLength() - l;
			
			if (remainingSuperfluouslyRead > 0)
			{
				if (this.excessBuffer == null || this.excessBuffer.length < remainingSuperfluouslyRead)
					this.excessBuffer = new short[remainingSuperfluouslyRead];
				
				this.excessBufferPos = 0;
				this.excessBufferSize = remainingSuperfluouslyRead;
				System.arraycopy(u.getUnderlying(), u.getOffset()+l, this.excessBuffer, 0, remainingSuperfluouslyRead);
			}
			
			finishedUsingBufferFromLastUncontrollableLengthRead();
			
			return l;
		}
		else
		{
			int l = least(this.excessBufferSize, length);
			
			System.arraycopy(this.excessBuffer, this.excessBufferPos, buffer, offset, l);
			
			this.excessBufferPos += l;
			this.excessBufferSize -= l;
			
			return l;
		}
	}
	
	
	/**
	 * @return null for eof :3
	 */
	@Nullable
	protected abstract Slice<short[]> readUncontrollableLength(int lengthRequested) throws IOException;
	
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	protected void finishedUsingBufferFromLastUncontrollableLengthRead() throws IOException {}
	
	
	
	
	
	
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
