/*
 * Created on May 17, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api;

import java.io.EOFException;
import java.io.IOException;

/**
 * A {@link UnitReadStream} is a {@link ReadStream} which reads only one unit of data at a time.
 * <p>
 * To be a valid {@link UnitReadStream}, an implementation must contain a read method with the declaration:<br>
 * <code><i>type</i> read() throws EOFException, IOException, ClosedStreamException</code><br>
 * Where <code>type</code> is some Primitive or Object class.
 * This method will attempt to read the given unit of data from the stream, assuming it is of the
 * correct type.  If there is no more data to be read due to the cursor being at EOF, then an
 * {@link EOFException} is thrown.
 * </p>
 * @author RProgrammer
 */
public interface UnitReadStream<B>
extends ReadStream
{
	public B readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException;
}
