package rebound.io.iio;

import java.io.IOException;

/**
 * An Java Interface abstracting the essence of {@link java.io.InputStream} :3
 * @author Puppy Pie ^w^
 */
public interface InputByteStream
{
	public int read() throws IOException;
	
	
	
	public default int read(byte b[]) throws IOException
	{
		return read(b, 0, b.length);
	}
	
	public int read(byte b[], int off, int len) throws IOException;
	
	
	
	
	
	public long skip(long numberOfBytesToSkip) throws IOException;
}
