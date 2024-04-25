package rebound.util.collections;

/**
 * You can use this in the format:
 * <code>
 * 		BasicReferenceIterator i = something.newIterator();
 * 		while (i.next())
 * 		{
 * 			doSomething(i.get());
 * 		}
 * </code>
 * 
 * This way, it doesn't require the underlying implementation to be able to look ahead and determine if it's at EOF without actually advancing,
 * But it doesn't have to package the data into a Java Object like {@link SimpleIterator} does!
 */
public interface BasicIterator
{
	/**
	 * This must be called at least once initially before using the accessor methods subclasses of this will provide!
	 * @return true if there is more and you can call the accessors, false if it's the end and you mustn't call them!
	 */
	public boolean next();
}
