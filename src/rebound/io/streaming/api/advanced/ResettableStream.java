/*
 * Created on Oct 28, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api.advanced;

import java.io.IOException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.Stream;

/**
 * Like a primitive {@link SeekableStream}, a {@link ResettableStream} is one that merely allows the cursor to be reset to BOF.
 * @author RProgrammer
 */
public interface ResettableStream
extends Stream
{
	/**
	 * This completely rewinds the cursor to BOF.
	 * If the cursor is already at BOF (or it's an empty data store), this must silently do nothing.
	 * @throws IOException If the location of the cursor is recorded only in the datastore and it could not be set for some reason, this is NOT thrown if the cursor is illegally set past EOF
	 */
	public void resetCursor() throws IOException, ClosedStreamException;
}
