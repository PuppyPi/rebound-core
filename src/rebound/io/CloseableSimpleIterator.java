package rebound.io;

import java.io.Closeable;
import java.io.IOException;
import rebound.exceptions.StopIterationReturnPath;
import rebound.util.collections.SimpleIterator;

public interface CloseableSimpleIterator<E>
extends SimpleIterator<E>, Closeable
{
	public static <E> CloseableSimpleIterator<E> closeByDraining(SimpleIterator<E> underlying)
	{
		return new CloseableSimpleIterator<E>()
		{
			boolean eof = false;
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				try
				{
					return underlying.nextrp();
				}
				catch (StopIterationReturnPath rp)
				{
					eof = true;
					throw rp;
				}
			}
			
			@Override
			public void close() throws IOException
			{
				if (!eof)
					underlying.drain();
			}
		};
	}
}
