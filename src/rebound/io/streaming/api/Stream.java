/*
 * Created on May 17, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api;

import java.io.Closeable;
import java.io.IOException;
import rebound.io.streaming.api.advanced.PreservingSkipWriteStream;
import rebound.io.streaming.api.advanced.ResettableStream;
import rebound.io.streaming.api.advanced.SeekableStream;

/**
 * A {@link Stream} is anything which transfers encapsulated data (in the form of Objects, bytes, or other primitive types) on demand.
 * <br>
 * Every stream maintains the concept of a "cursor", though it may be fictitious in reality.
 * Principal operations, such as {@link ReadStream read} or {@link WriteStream write}, and others, such as {@link #skip(long) skip}, advance the cursor, while seeking operations, such as in {@link SeekableStream seekable streams}, or simple {@link ResettableStream resettable streams} can rewind the cursor.
 * <br>
 * A Stream in general has no support of internal thread safety, so one must assume it is not unless explicitly assured otherwise.
 * This is not to say that the underlying data store does not support concurrent streams, merely that for this particular stream object,
 * operations performed on it by more than one thread may result in undefined behavior.
 * @author RProgrammer
 */
public interface Stream
extends Closeable
{
	/**
	 * If this returns <code>true</code> then the cursor is at the end of the store (assuming the store is logically finite).
	 * Note that the converse if not true, if this returns <code>false</code> that does not necessarily mean the cursor is not at the end.
	 * It is possible that one can happen to advance the cursor exactly to the end of a stream, but until another advance is attempted (which will fail), the stream doesn't know it has reached EOF.
	 * Note: If this occurs, a {@link #skip(long)} is not guaranteed to discover the EOF.
	 */
	public boolean isEOF() throws IOException, ClosedStreamException;
	
	
	
	
	/**
	 * This first flushes any pending updates to the store, then releases all resources used by the stream.
	 * If an error is encountered during this process, the most that can be done to release resources is first done,
	 * then the error is allowed to be thrown by this method.
	 * After calling this, regardless of whether or not an IOException is thrown, {@link #isClosed()} must from that point on return <code>true</code>,
	 * and further invocations of this method will do nothing.
	 */
	@Override
	public void close() throws IOException;
	
	/**
	 * @return <code>false</code> until {@link #close()} is called, and <code>true</code> from then onward
	 */
	public boolean isClosed();
	
	/**
	 * <p>This advances the cursor without actually reading or writing any data from/to the store.
	 * Like the various read() methods, it must skip less than asked only if EOF is encountered.
	 * And while the stream is at eof, it must always do nothing and return 0.
	 * 
	 * <p>While this is very well defined for {@link ReadStream}s, {@link WriteStream}s can implement this in one of two ways:
	 * Either they will write token data which they must specify (eg, <code>null</code>, 0, etc.), or they will not write anything and
	 * leave the skipped space in the data store unchanged from its previous state.  The latter is the preferred behavior.
	 * They may also switch between these in the course of their lifetime (such as an extending write stream).
	 * Regardless, a skip request indicates that the user wishes to advance the cursor as fast as possible, not caring what
	 * is done to the skipped region in the data store.
	 * If the stream can skip without writing data, and will always do so, then it should implement the {@link PreservingSkipWriteStream} interface to indicate this guarantee.
	 * @param amount The maximum amount to skip
	 * @return The actual amount skipped, possibly 0, but never negative
	 * @throws IllegalArgumentException if <code>amount</code> is negative (similar to other stream advancement methods)
	 */
	public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException;
}
