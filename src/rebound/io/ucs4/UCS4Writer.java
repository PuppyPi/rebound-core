package rebound.io.ucs4;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import rebound.text.StringUtilities;

/**
 * Exactly analogous to {@link Writer}, but using 4-byte int's instead of 2-byte char's to represent Unicode characters!
 * ( also it's an interface! :D )
 * 
 * @author Puppy Pie ^w^
 */
public interface UCS4Writer
extends Closeable, Flushable
{
	public void write(int c) throws IOException;
	
	public default void write(int[] cbuf) throws IOException
	{
		write(cbuf, 0, cbuf.length);
	}
	
	public void write(int[] cbuf, int off, int len) throws IOException;
	
	
	
	
	
	public default void write(String str) throws IOException
	{
		StringUtilities.defaultWriteStringToUCS4(str, this);
	}
	
	public default UCS4Writer append(CharSequence csq) throws IOException
	{
		if (csq == null)
			write("null");  //to be compatible with java.io.Writer :3'
		else
			StringUtilities.defaultWriteStringToUCS4(csq.toString(), this);
		
		return this;
	}
	
	
	
	
	
	@Override
	public void flush() throws IOException;
	
	@Override
	public void close() throws IOException;
}
