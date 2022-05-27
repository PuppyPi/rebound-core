package rebound.io;

import java.io.Closeable;
import java.io.IOException;

public interface UncheckedCloseable
extends Closeable
{
	/**
	 * Does not throw {@link IOException}
	 */
	@Override
	public void close();
}
