package rebound.util.collections.prim;

public enum SpanningOperationImplementationType
{
	/**
	 * Eg, a single byte at a time is read to read an int! (or possibly a single short at a time, but something slower than it could be theoretically!)
	 */
	Piecemeal,
	
	/**
	 * Eg, something faster than reading 4 single bytes one at a time is available to read an int!
	 */
	Fast,
	
	/**
	 * Eg, reading a short with this interface counts as one single operation in concurrency/parallelism/multiprocessing contexts!!
	 */
	AtomicTo2,
	
	/**
	 * Eg, reading a short/int with this interface counts as one single operation in concurrency/parallelism/multiprocessing contexts!!
	 */
	AtomicTo4,
	
	/**
	 * Eg, reading a short/int/long with this interface counts as one single operation in concurrency/parallelism/multiprocessing contexts!!
	 */
	AtomicTo8,
}
