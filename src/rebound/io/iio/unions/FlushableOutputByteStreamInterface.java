package rebound.io.iio.unions;

import java.io.Closeable;
import java.io.Flushable;
import rebound.io.iio.OutputByteStream;

/**
 * Just merges {@link OutputByteStream}, {@link Closeable}, and {@link Flushable} :33
 * @author Puppy Pie ^w^
 */
public interface FlushableOutputByteStreamInterface
extends OutputByteStream, Flushable
{
}
