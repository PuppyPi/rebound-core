/*
 * Created on Apr 4, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * This stream filter resolves '\n', '\r', and the '\r\n' pair all as '\n' like a proper text file should be ;)<br>
 * (keeps a pushback buffer 1 character long, though!, so it might have read at most 1 character from the underlying stream that the )
 * 
 * @author RProgrammer
 */
public class UniversalNewlineReader
extends FilterReader
{
	protected boolean closed;
	protected boolean eof;
	
	protected boolean hasPush = false;
	protected char pushback;
	
	protected UniversalNewlineReader(Reader in)
	{
		super(in);
	}
	
	public static UniversalNewlineReader wrap(Reader in)
	{
		return in instanceof UniversalNewlineReader ? (UniversalNewlineReader)in : new UniversalNewlineReader(in);
	}
	
	
	
	
	@Override
	public int read() throws IOException
	{
		if (this.closed)
			throw new IOException("Stream has already been closed.");
		
		if (this.eof)
			return -1;
		
		
		int c1 = 0;
		{
			if (this.hasPush)
			{
				c1 = this.pushback;
				this.hasPush = false;
			}
			else
			{
				c1 = this.in.read();
				
				if (c1 == -1)
				{
					this.eof = true;
					return -1;
				}
			}
		}
		
		if (c1 == '\r')
		{
			int c2 = this.in.read();
			
			if (c2 == -1)
			{
				this.eof = true;
				return '\n';
			}
			else if (c2 == '\n')
			{
				return '\n';
			}
			else
			{
				this.hasPush = true;
				this.pushback = (char)c2;
				return '\n';
			}
		}
		else
		{
			return c1;
		}
	}
	
	
	
	
	
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		if (this.closed)
			throw new IOException("Stream has already been closed.");
		
		int c = 0;
		for (int i = 0; i < len; i++)
		{
			c = read();
			if (c == -1)
				return i == 0 ? -1 : i;
			cbuf[off+i] = (char)c;
		}
		
		return len;
	}
	
	
	@Override
	public long skip(long n) throws IOException
	{
		if (this.closed)
			throw new IOException("Stream has already been closed.");
		
		for (long i = 0; i < n; i++)
		{
			if (this.eof)
				return i;
			read();
		}
		return n;
	}
	
	
	@Override
	public int read(char[] cbuf) throws IOException
	{
		return read(cbuf, 0, cbuf.length);
	}
	
	
	
	
	@Override
	public void close() throws IOException
	{
		if (!this.closed)
		{
			this.closed = true;
			super.close();
		}
	}
	
	@Override
	public void mark(int readAheadLimit) throws IOException
	{
		throw new IOException("mark() not supported");
	}
	
	@Override
	public void reset() throws IOException
	{
		throw new IOException("reset() not supported");
	}
	
	@Override
	public boolean markSupported()
	{
		return false;
	}
}
