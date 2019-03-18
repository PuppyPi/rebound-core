/*
 * Created on May 17, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api;

import java.io.IOException;
import java.io.InputStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentBlockReadStream;
import rebound.util.collections.Slice;

/**
 * A {@link BlockReadStream} (contrasted with a {@link UnitReadStream}) is capable of reading more than one
 * unit of data at a time.
 * 
 * <p>
 * To be a valid {@link BlockReadStream}, an implementation must contain a read method with the declaration:<br>
 * <code>int read(<i>type</i>[] buffer, int offset, int length)</code><br>
 * Where <code>type</code> is some Primitive or Object class.
 * This method will read some amount of data not more than the requested <code>length</code> into the <code>buffer</code> starting at <code>offset</code>.
 * Unlike {@link InputStream#read(byte[], int, int)}, this read() may only read less than the requested amount of data if there is not enough data to be read! (aka EOF).
 * If an implementation wishes to only provide a lazy {@link InputStream}-like implementation, it can implement this read() method with a simple call to {@link StreamImplUtilities}.forceRead(...) passing itself.  The lazy (<i>Indolent</i>) implementation is implemented in a separate {@link IndolentBlockReadStream method}.
 * </p>
 * 
 * <p>
 * It should be noted that, with proper caching of an array of length 1, a
 * BlockReadStream can function as a UnitReadStream with minimal performance
 * penalty, while a UnitReadStream has a far greater performance penalty when
 * wrapped for block reading.  Hence {@link BlockReadStream} extends
 * {@link UnitReadStream}, but not vice versa!
 * </p>
 * 
 * @author RProgrammer
 */
public interface BlockReadStream<B, A>
extends ReadStream, UnitReadStream<B>
{
	public int read(Slice<A> buffer) throws IOException, ClosedStreamException;
}
