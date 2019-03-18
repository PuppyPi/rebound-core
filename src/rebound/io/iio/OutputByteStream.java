package rebound.io.iio;

import java.io.IOException;

/**
 * An Java Interface abstracting the essence of {@link java.io.OutputStream} :3
 * @author Puppy Pie ^w^
 */
public interface OutputByteStream
{
	public void write(int b) throws IOException;
	
	
	
	public default void write(byte b[]) throws IOException
	{
		write(b, 0, b.length);
	}
	
	public void write(byte b[], int off, int len) throws IOException;
}
