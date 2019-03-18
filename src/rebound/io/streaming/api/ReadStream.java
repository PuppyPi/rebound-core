/*
 * Created on May 17, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api;

/**
 * A {@link ReadStream} is any {@link Stream stream} which transfers data from the data store to the user.
 * @author RProgrammer
 */
public interface ReadStream
extends Stream
{
	public default ReadStream _justAMethodToPreventBothDirectionalitiesInterfacesFromBeingSimultaneouslyImplemented()
	{
		return null;
	}
}
