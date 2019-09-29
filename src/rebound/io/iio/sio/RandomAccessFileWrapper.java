package rebound.io.iio.sio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import rebound.io.iio.unions.CloseableFlushableRandomAccessBytesInterface;

public class RandomAccessFileWrapper
implements CloseableFlushableRandomAccessBytesInterface
{
	protected final RandomAccessFile underlying;
	
	public RandomAccessFileWrapper(RandomAccessFile underlying)
	{
		this.underlying = underlying;
	}
	
	public RandomAccessFileWrapper(File f, boolean write) throws FileNotFoundException
	{
		this(new RandomAccessFile(f, write ? "rw" : "r"));
	}
	
	public RandomAccessFileWrapper(String f, boolean write) throws FileNotFoundException
	{
		this(new File(f), write);
	}
	
	
	
	
	
	
	@Override
	public int read() throws IOException
	{
		return this.underlying.read();
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return this.underlying.read(b, off, len);
	}
	
	@Override
	public void write(int b) throws IOException
	{
		this.underlying.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		this.underlying.write(b, off, len);
	}
	
	@Override
	public long getFilePointer() throws IOException
	{
		return this.underlying.getFilePointer();
	}
	
	@Override
	public void seek(long pos) throws IOException
	{
		this.underlying.seek(pos);
	}
	
	@Override
	public long length() throws IOException
	{
		return this.underlying.length();
	}
	
	@Override
	public void setLength(long newLength) throws IOException
	{
		this.underlying.setLength(newLength);
	}
	
	
	@Override
	public void close() throws IOException
	{
		this.underlying.close();
	}
	
	@Override
	public void flush() throws IOException
	{
		//java.io.RandomAccessFiles are apparently unbuffered!! \0/
	}
	
	
	
	@Override
	public long skip(long amount) throws IOException
	{
		if (amount > Integer.MAX_VALUE)
			amount = Integer.MAX_VALUE;
		if (amount < Integer.MIN_VALUE)
			amount = Integer.MIN_VALUE;
		
		return this.underlying.skipBytes((int)amount);
	}
}
