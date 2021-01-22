/*
 * Created on Dec 2, 2007
 * 	by the great Eclipse(c)
 */
package rebound.io.util;

import java.io.IOException;
import java.io.Writer;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;

public class BroadcastingWriter
extends Writer
{
	protected Writer[] sinks;
	
	public BroadcastingWriter()
	{
		super();
		sinks = new Writer[0];
	}
	
	public BroadcastingWriter(@LiveValue Writer... sinks)
	{
		super();
		this.sinks = sinks;
	}
	
	
	/**
	 * Returns either a {@link BroadcastingWriter} broadcasting to the given Writers, or
	 * simply passes through the given Writer if only one is given,
	 * (or returns a dummy stream if none or null array are given)
	 */
	public static Writer inst(@PossiblySnapshotPossiblyLiveValue Writer... sinks)
	{
		if (sinks == null || sinks.length == 0)
			return new BroadcastingWriter(); //basically a dummy
		else if (sinks.length == 1)
			return sinks[0];
		else
			return new BroadcastingWriter(sinks);
	}
	
	
	
	
	
	@Override
	public void close() throws IOException
	{
		for (Writer o : getSinks())
			o.close();
	}
	
	@Override
	public void flush() throws IOException
	{
		for (Writer o : getSinks())
			o.flush();
	}
	
	@Override
	public void write(char[] b, int off, int len) throws IOException
	{
		for (Writer o : getSinks())
			o.write(b, off, len);
	}
	
	@Override
	public void write(char[] b) throws IOException
	{
		for (Writer o : getSinks())
			o.write(b);
	}
	
	@Override
	public void write(int b) throws IOException
	{
		for (Writer o : getSinks())
			o.write(b);
	}
	
	@Override
	public void write(String str) throws IOException
	{
		for (Writer o : getSinks())
			o.write(str);
	}
	
	@Override
	public void write(String str, int off, int len) throws IOException
	{
		for (Writer o : getSinks())
			o.write(str, off, len);
	}
	
	@Override
	public Writer append(char c) throws IOException
	{
		for (Writer o : getSinks())
			o.append(c);
		return this;
	}
	
	@Override
	public Writer append(CharSequence csq) throws IOException
	{
		for (Writer o : getSinks())
			o.append(csq);
		return this;
	}
	
	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException
	{
		for (Writer o : getSinks())
			o.append(csq, start, end);
		return this;
	}
	
	
	
	
	
	public Writer[] getSinks()
	{
		return this.sinks;
	}
	
	public void setSinks(Writer[] sinks)
	{
		this.sinks = sinks;
	}
}
