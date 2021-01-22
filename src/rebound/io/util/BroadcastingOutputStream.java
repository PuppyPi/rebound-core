/*
 * Created on Dec 2, 2007
 * 	by the great Eclipse(c)
 */
package rebound.io.util;

import java.io.IOException;
import java.io.OutputStream;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;

public class BroadcastingOutputStream
extends OutputStream
{
	protected OutputStream[] sinks;
	
	public BroadcastingOutputStream()
	{
		super();
		sinks = new OutputStream[0];
	}
	
	public BroadcastingOutputStream(@LiveValue OutputStream[] sinks)
	{
		super();
		this.sinks = sinks;
	}
	
	
	/**
	 * Returns either a {@link BroadcastingOutputStream} broadcasting to the given OutputStreams, or
	 * simply passes through the given OutputStream if only one is given,
	 * (or returns a dummy stream if none or null array are given)
	 */
	public static OutputStream inst(@PossiblySnapshotPossiblyLiveValue OutputStream... sinks)
	{
		if (sinks == null || sinks.length == 0)
			return new BroadcastingOutputStream(); //basically a dummy
		else if (sinks.length == 1)
			return sinks[0];
		else
			return new BroadcastingOutputStream(sinks);
	}
	
	
	
	
	
	@Override
	public void close() throws IOException
	{
		for (OutputStream o : getSinks())
			o.close();
	}
	
	@Override
	public void flush() throws IOException
	{
		for (OutputStream o : getSinks())
			o.flush();
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		for (OutputStream o : getSinks())
			o.write(b, off, len);
	}
	
	@Override
	public void write(byte[] b) throws IOException
	{
		for (OutputStream o : getSinks())
			o.write(b);
	}
	
	@Override
	public void write(int b) throws IOException
	{
		for (OutputStream o : getSinks())
			o.write(b);
	}
	
	
	
	
	
	public OutputStream[] getSinks()
	{
		return this.sinks;
	}
	
	public void setSinks(OutputStream[] sinks)
	{
		this.sinks = sinks;
	}
}
