/*
 * Created on May 23, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api;

import java.io.IOException;
import java.io.OutputStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentBlockWriteStream;
import rebound.util.collections.Slice;

/**
 * A {@link BlockWriteStream} (contrasted with a {@link UnitWriteStream}) is capable of writing more than one
 * unit of data at a time.
 * 
 * <p>
 * To be a valid {@link BlockWriteStream}, an implementation must contain a write method with the declaration:<br>
 * <code>int write(<i>type</i>[] buffer, int offset, int length)</code><br>
 * Where <code>type</code> is some Primitive or Object class.
 * This method will write some amount of data not more than the requested <code>length</code> into the <code>buffer</code> starting at <code>offset</code>.
 * Unlike {@link OutputStream#write(byte[], int, int)}, this write() may only write less than the requested amount of data if there is not enough room for the data to be stored! (aka EOF).
 * If an implementation wishes to only provide a lazy {@link OutputStream}-like implementation, it can implement this write() method with a simple call to {@link StreamImplUtilities}.forceWrite(...) passing itself.  The lazy (<i>Indolent</i>) implementation is implemented in a separate {@link IndolentBlockWriteStream method}.
 * </p>
 * 
 * <p>
 * It should be noted that, with proper caching of an array of length 1, a
 * BlockWriteStream can function as a UnitWriteStream with minimal performance
 * penalty, while a UnitWriteStream has a far greater performance penalty when
 * wrapped for block writing.  Hence {@link BlockWriteStream} extends
 * {@link UnitWriteStream}, but not vice versa!
 * </p>
 * 
 * @author RProgrammer
 */
public interface BlockWriteStream<B, A>
extends WriteStream, UnitWriteStream<B>
{
	public int write(Slice<A> buffer) throws IOException, ClosedStreamException;
}
