package rebound.exceptions;

import rebound.util.collections.prim.PrimitiveCollections.ByteList;

/**
 * The naive implementation coullllddddd be done and it would work, but instead we'll throw this exception because it'd probably be way too slow and you probably would rather it just fail and give an exact stacktrace than you spend forever trying to figure out where/why it's so insanely slow XD
 * 
 * Eg, when getting a {@link ByteList} and expecting to be able to unpack it to a byte[] for a bulk operation instead of actually do a loop on each byte individually XD''  (or allocate and copy into a new temporary buffer *every invocation*!)
 */
public class SlowVersionUnsupportedException
extends UnsupportedOptionException
{
	private static final long serialVersionUID = 1L;

	public SlowVersionUnsupportedException()
	{
		super();
	}

	public SlowVersionUnsupportedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SlowVersionUnsupportedException(String message)
	{
		super(message);
	}

	public SlowVersionUnsupportedException(Throwable cause)
	{
		super(cause);
	}
}
