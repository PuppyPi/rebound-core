/*
 * Created on May 17, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api;

import java.io.Flushable;
import java.io.IOException;
import rebound.io.streaming.api.advanced.LengthMutableWriteStream;
import rebound.io.streaming.api.advanced.PreservingSkipWriteStream;

/**
 * A {@link WriteStream} is any {@link Stream stream} which transfers data from the user to the data store.
 * @author RProgrammer
 */
public interface WriteStream
extends Stream, Flushable
{
	/**
	 * Since updates to the data may not actually be committed to the datastore by the <code>return</code> of the <code>write()</code> method,
	 * this ensures that they are.
	 * Most updates will be write()s, but any others may be cached as well, such as
	 * {@link LengthMutableWriteStream#setLength(long)}, or non-{@link PreservingSkipWriteStream preserving} {@link #skip(long) skip}s.
	 */
	@Override
	public void flush() throws IOException, ClosedStreamException;
	
	
	
	public default WriteStream _justAMethodToPreventBothDirectionalitiesInterfacesFromBeingSimultaneouslyImplemented()
	{
		return null;
	}
}
