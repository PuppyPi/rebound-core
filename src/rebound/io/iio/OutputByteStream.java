package rebound.io.iio;

import java.io.Closeable;
import java.io.Flushable;

/**
 * A Java Interface abstracting the essence of {@link java.io.OutputStream} :3
 * @author Puppy Pie ^w^
 */
public interface OutputByteStream
extends Flushable, Closeable, BasicOutputByteStream
{
}
