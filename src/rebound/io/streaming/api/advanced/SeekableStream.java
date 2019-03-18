/*
 * Created on May 23, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api.advanced;

import java.io.IOException;
import javax.annotation.Nonnegative;
import rebound.io.streaming.api.ClosedStreamException;

/**
 * A {@link SeekableStream seekable stream} is one that allows you direct control over- and access to- the cursor.
 * @author RProgrammer
 */
public interface SeekableStream
extends ResettableStream
{
	/**
	 * Any problems raised by having the cursor illegally past EOF should be raised upon the next advancement; merely setting the cursor past EOF only causes isEOF() to be true.
	 * Having the cursor legally past EOF (ie, {@link LengthMutableWriteStream#isAutoExtend() auto-extend}) will perform the extension upon the next write (not skip), rather than upon setting the cursor there.
	 * @param position The new position for the cursor (0 is at BOF, {@link LengthAwareStream#getLength()} is at EOF--if it has one)
	 * @throws IOException If the location of the cursor is recorded only in the datastore and it could not be set for some reason, this is NOT thrown if the cursor is illegally set past EOF
	 * @throws IllegalArgumentException if position is negative
	 */
	public void setCursor(@Nonnegative long position) throws IllegalArgumentException, IOException, ClosedStreamException;
	
	/**
	 * Gets the position of the cursor in the data store.
	 * 0 is BOF (next read will read the first element),
	 * &gt;= <code>length</code> is EOF.
	 * This will never return negative.
	 * @throws IOException If the location of the cursor is recorded only in the datastore and it could not be recalled for some reason
	 */
	public @Nonnegative long getCursor() throws IOException, ClosedStreamException;
	
	
	
	
	@Override
	public default void resetCursor() throws IOException, ClosedStreamException
	{
		setCursor(0);
	}
}
