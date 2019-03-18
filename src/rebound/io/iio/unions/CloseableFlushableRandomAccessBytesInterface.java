package rebound.io.iio.unions;

import java.io.Closeable;
import java.io.Flushable;
import rebound.io.iio.RandomAccessBytes;

/**
 * Just merges {@link RandomAccessBytes}, {@link Closeable}, and {@link Flushable} :33
 * @author Puppy Pie ^w^
 */
public interface CloseableFlushableRandomAccessBytesInterface
extends RandomAccessBytes, Closeable, Flushable
{
}
