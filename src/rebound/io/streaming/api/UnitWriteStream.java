/*
 * Created on May 23, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api;

import java.io.EOFException;
import java.io.IOException;

/**
 * A {@link UnitWriteStream} is a {@link WriteStream} which can write only one unit of data at a time.
 * <p>
 * To be a valid {@link UnitWriteStream}, an implementation must contain a read method with the declaration:<br>
 * <code>void write(<i>type</i> unit) throws EOFException, IOException, ClosedStreamException</code><br>
 * Where <code>type</code> is some Primitive or Object class.
 * This method will attempt to write the given unit of data to the stream, assuming it is of the
 * correct type.  If the data store cannot hold any more data than is currently in it, and the
 * cursor is at EOF, then an {@link EOFException} is thrown.
 * </p>
 * @author RProgrammer
 */
public interface UnitWriteStream<B>
extends WriteStream
{
	public void writePossiblyUnboxing(B unit) throws EOFException, IOException, ClosedStreamException;
}
