/*
 * Created on May 23, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api.advanced;

import java.io.IOException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.IllegalLengthException;
import rebound.io.streaming.api.IllegalLengthException.Reason;
import rebound.io.streaming.api.WriteStream;

/**
 * A {@link LengthMutableWriteStream Length-mutable} {@link WriteStream stream} is one that can not only see its datastore's size, but can also {@link #setLength(long) change} it.
 * If the datastore is extended, then the contents of the new space is undefined.
 * <br>
 * <p>
 * For convenience, a feature, called auto-extend, is supported.
 * When in auto-extend mode, any writes that would extend past the EOF (effectively) call {@link #setLength(long)} before performing the write.
 * The caveat to this is that <code>write()</code> may throw an {@link IllegalLengthException}.
 * {@link #skip(long) Skip} and {@link SeekableStream#setCursor(long) setCursor()} do not extend, but merely record that the cursor is past the EOF.
 * Note: auto-extend is analogous to the behavior of java.io.FileOutputStream.
 * @author RProgrammer
 */
public interface LengthMutableWriteStream
extends LengthAwareStream, WriteStream, PreservingSkipWriteStream
{
	/**
	 * Directly sets the length of the data store.
	 * @throws IllegalLengthException IfF the given length cannot be used for some {@link Reason reason}
	 * @throws UnsupportedOperationException If {@link #isAutoExtend()} == false  (or possibly other reasons!  (if not iff!))
	 */
	public void setLength(long value) throws UnsupportedOperationException, IllegalLengthException, ClosedStreamException, IOException;
	
	
	/**
	 * Checks if the stream is in auto-extend mode.
	 * The default value for this setting is <code>on</code> (<code>true</code>).
	 */
	public boolean isAutoExtend() throws ClosedStreamException;
	
	
	
	public boolean supportsSettingToAutoExtendValue(boolean value) throws ClosedStreamException;
	
	/**
	 * See {@link #isAutoExtend()}
	 */
	public void setAutoExtend(boolean autoExtend) throws ClosedStreamException, UnsupportedOperationException;
}
