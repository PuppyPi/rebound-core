package rebound.io.iio;

import java.io.IOException;
import rebound.exceptions.NotYetImplementedException;
import rebound.io.iio.unions.CloseableFlushableRandomAccessBytesInterface;

public class RandomAccessBytesBufferingWrapper
implements CloseableFlushableRandomAccessBytesInterface
{
	protected final CloseableFlushableRandomAccessBytesInterface underlying;
	protected final long lengthCache;
	protected long positionCache;
	
	protected final byte[] buffer;
	protected int bufferSize;
	protected long bufferOffsetInUnderlying;
	protected boolean bufferValid;
	
	
	public RandomAccessBytesBufferingWrapper(CloseableFlushableRandomAccessBytesInterface underlying, int bufferSize) throws IOException
	{
		this.underlying = underlying;
		this.buffer = new byte[bufferSize];
		this.bufferValid = false;
		
		this.positionCache = underlying.getFilePointer();
		this.lengthCache = underlying.length();
	}
	
	public RandomAccessBytesBufferingWrapper(CloseableFlushableRandomAccessBytesInterface underlying) throws IOException
	{
		this(underlying, 4096);
	}
	
	
	
	public boolean isBufferValid()
	{
		return this.bufferValid;
	}
	
	public long getStartOfBufferInUnderlying()
	{
		return this.bufferOffsetInUnderlying;
	}
	
	public long getOnePastEndOfBufferInUnderlying()
	{
		return this.bufferOffsetInUnderlying+this.buffer.length;
	}
	
	public boolean isPositionInBufferAndBufferIsValid(long pos)
	{
		return isBufferValid() && (pos >= getStartOfBufferInUnderlying() && pos < getOnePastEndOfBufferInUnderlying());
	}
	
	protected void ensureBufferHasCurrentPosition() throws IOException
	{
		if (!isPositionInBufferAndBufferIsValid(this.positionCache))
		{
			this.underlying.seek(this.positionCache);
			this.bufferSize = this.underlying.read(this.buffer);
			
			//AFTER it, in case it fails!!
			this.bufferValid = true;
			
			this.bufferOffsetInUnderlying = this.positionCache;
		}
	}
	
	
	
	
	@Override
	public int read() throws IOException
	{
		ensureBufferHasCurrentPosition();
		
		if (!isPositionInBufferAndBufferIsValid(this.positionCache))
			return -1;
		else
			return this.buffer[(int)(this.positionCache - this.bufferOffsetInUnderlying)];
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		//Todo use a better buffering strategy ^^''
		
		ensureBufferHasCurrentPosition();
		
		if (isPositionInBufferAndBufferIsValid(this.positionCache) && isPositionInBufferAndBufferIsValid(this.positionCache + len - 1))  //if the requested region is entirely inside the buffer! :D
		{
			System.arraycopy(this.buffer, (int)(this.positionCache - this.bufferOffsetInUnderlying), b, off, len);
			this.positionCache += len;
			return len;
		}
		else
		{
			this.underlying.seek(this.positionCache);
			
			this.bufferValid = false;
			int amt = this.underlying.read(b, off, len);
			
			this.positionCache += amt;
			
			return amt;
		}
	}
	
	
	@Override
	public long skip(long numberOfBytesToSkip) throws IOException
	{
		seek(getFilePointer() + numberOfBytesToSkip);
		return numberOfBytesToSkip;
	}
	
	@Override
	public long getFilePointer() throws IOException
	{
		return this.positionCache;
	}
	@Override
	public long length() throws IOException
	{
		return this.lengthCache;
	}
	
	@Override
	public void seek(long newFilePointerPosition) throws IOException
	{
		this.positionCache = newFilePointerPosition;
	}
	
	@Override
	public void close() throws IOException
	{
		this.underlying.close();
	}
	
	
	
	
	
	
	
	
	
	
	//TODO Support writing!!
	
	@Override
	public void setLength(long newLength) throws IOException
	{
		throw new NotYetImplementedException();
	}
	
	@Override
	public void write(int b) throws IOException
	{
		throw new NotYetImplementedException();
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		throw new NotYetImplementedException();
	}
	
	@Override
	public void flush() throws IOException
	{
		throw new NotYetImplementedException();
	}
}
