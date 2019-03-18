package rebound.io.iio.unions;

import java.io.Closeable;
import rebound.io.iio.InputByteStream;

/**
 * Just merges {@link InputByteStream}, {@link Closeable} :33
 * @author Puppy Pie ^w^
 */
public interface CloseableInputByteStreamInterface
extends InputByteStream, Closeable
{
}
