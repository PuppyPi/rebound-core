package rebound.io;

import java.io.Closeable;
import java.util.List;

public interface CloseableList<E>
extends List<E>, Closeable
{
	@Override
	public void close();  //the other methods don't throw IOException X3
}
