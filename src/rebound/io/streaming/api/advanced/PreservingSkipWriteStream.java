/*
 * Created on May 17, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api.advanced;

import rebound.io.streaming.api.WriteStream;

/**
 * A {@link WriteStream} that implements this behavioral interface guarantees that an invocation of {@link #skip(long)}
 * will not affect the data store in any way.  If this is an extending write stream, then this guarantees that skip() will not
 * extend it, but merely record the amount skipped so that the next write will do the actual extension, and then some.
 * @author RProgrammer
 */
public interface PreservingSkipWriteStream
extends WriteStream
{
}
