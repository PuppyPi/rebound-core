/*
 * Created on Aug 17, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.util;

import java.io.InputStream;

/**
 * An {@link InputStream} whose methods don't do anything and always appears like it's already reached EOF. 
 * This is useful for extending a FilterInputStream or etc. where you will override all the methods.
 * @author RProgrammer
 */
public class EmptyInputStream
extends InputStream
{
	/**
	 * For performance reasons only (since it's stateless)
	 */
	public static final EmptyInputStream I = new EmptyInputStream();
	
	
	@Override
	public void close() {}
	
	@Override
	public int available() {return 0;}
	@Override
	public synchronized void mark(int readlimit) {}
	@Override
	public boolean markSupported() {return false;}
	@Override
	public int read() {return -1;}
	@Override
	public int read(byte[] b) {return -1;}
	@Override
	public int read(byte[] b, int off, int len) {return -1;}
	@Override
	public synchronized void reset() {}
	@Override
	public long skip(long n) {return 0;}
	
	
	
	@Override
	public EmptyInputStream clone()
	{
		return I;
	}
	
	@Override
	public int hashCode()
	{
		return 1;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj != null && obj instanceof EmptyInputStream;
	}
	
	@Override
	public String toString()
	{
		return "An EmptyInputStream";
	}
}
