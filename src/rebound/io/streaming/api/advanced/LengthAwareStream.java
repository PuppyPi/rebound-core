/*
 * Created on May 23, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api.advanced;

import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.Stream;

/**
 * A {@link LengthAwareStream Length-aware} {@link Stream stream} is one that is conscious of the size of its datastore.
 * @author RProgrammer
 */
public interface LengthAwareStream
extends Stream
{
	/**
	 * This must return the exact size of the data store. (In number-of-units-of-data, which are of some type defined by the subclassing stream)
	 */
	public long getLength() throws ClosedStreamException;
}
