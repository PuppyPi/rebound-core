package rebound.io;

import static rebound.bits.Unsigned.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.io.iio.GuaranteedBasicInputByteStream;
import rebound.io.iio.InputByteStream;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.Slice;

public class TransparentByteArrayInputStream
extends GuaranteedInputStream
implements GuaranteedBasicInputByteStream, InputByteStream
{
	protected byte[] buff;
	protected int cursor;
	protected int remaining;
	
	protected int markPosition;  //this is an absolute index into the byte[], not relative to this.cursor!
	
	
	public TransparentByteArrayInputStream()
	{
		this(ArrayUtilities.EmptyByteArray);
	}
	
	public TransparentByteArrayInputStream(@LiveValue byte[] underlying)
	{
		setUnderlying(underlying);
	}
	
	public TransparentByteArrayInputStream(@LiveValue byte[] underlying, int offset, int length)
	{
		setUnderlying(underlying, offset, length);
	}
	
	public TransparentByteArrayInputStream(@LiveValue Slice<byte[]> underlying)
	{
		setUnderlying(underlying);
	}
	
	
	
	
	@LiveValue
	public byte[] getRawByteArray()
	{
		return buff;
	}
	
	@LiveValue
	public Slice<byte[]> getRemaining()
	{
		return new Slice<>(buff, cursor, remaining);
	}
	
	
	public void setUnderlying(@LiveValue byte[] underlying)
	{
		setUnderlying(underlying, 0, underlying.length);
	}
	
	public void setUnderlying(@LiveValue Slice<byte[]> underlying)
	{
		setUnderlying(underlying.getUnderlying(), underlying.getOffset(), underlying.getLength());
	}
	
	public void setUnderlying(@LiveValue byte[] underlying, int offset, int length)
	{
		this.buff = underlying;
		this.cursor = offset;
		this.remaining = length;
		
		this.markPosition = offset;
	}
	
	
	
	
	
	@Override
	public int available()
	{
		return remaining;
	}
	
	
	@Override
	public int read()
	{
		if (remaining == 0)
			return -1;
		else
		{
			byte v = buff[cursor];
			cursor++;
			remaining--;
			return upcast(v);
		}
	}
	
	@Override
	public int read(byte[] b, int off, int len)
	{
		if (remaining == 0)
			return -1;
		else
		{
			int amount = (int)least(len, remaining);
			
			System.arraycopy(buff, cursor, b, off, amount);
			
			cursor += amount;
			remaining -= amount;
			return amount;
		}
	}
	
	@Override
	public long skip(long n)
	{
		int amount = (int)least(n, remaining);
		cursor += amount;
		remaining -= amount;
		return amount;
	}
	
	
	
	@Override
	public boolean markSupported()
	{
		return true;
	}
	
	@Override
	public void mark(int readlimit)
	{
		this.markPosition = cursor;
	}
	
	@Override
	public void reset()
	{
		this.cursor = markPosition;
	}
	
	
	
	
	
	
	public void flush()
	{
	}
	
	public void close()
	{
		freeThings();
	}
	
	
	public void freeThings()
	{
		setUnderlying(ArrayUtilities.EmptyByteArray);
	}
}
