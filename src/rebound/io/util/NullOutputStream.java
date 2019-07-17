/*
 * Created on Aug 17, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.util;

import java.io.OutputStream;

/**
 * An {@link OutputStream} whose methods don't do anything.  (a la > /dev/null ;D )
 * This is useful for extending a PrintStream, or FilterOutputStream, etc. where you will override all the methods.
 * @author RProgrammer
 */
public class NullOutputStream
extends OutputStream
{
	/**
	 * For performance reasons only (since it's stateless)
	 */
	public static final NullOutputStream Instance = new NullOutputStream();
	
	
	
	@Override
	public void close() {}
	
	@Override
	public void flush() {}
	
	@Override
	public void write(int b) {}
	
	@Override
	public void write(byte[] b) {}
	
	@Override
	public void write(byte[] b, int off, int len) {}
	
	
	
	@Override
	public NullOutputStream clone()
	{
		return Instance;
	}
	
	@Override
	public int hashCode()
	{
		return 1;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj != null && obj instanceof NullOutputStream;
	}
	
	@Override
	public String toString()
	{
		return "A NullOutputStream";
	}
}
