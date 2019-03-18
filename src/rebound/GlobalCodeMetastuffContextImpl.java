package rebound;

import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;

/*
 * In its own file to be able to be easily used with the JRE stanard services API  (or any API using the FQN of the principal interface they implement ^^' )
 */

public interface GlobalCodeMetastuffContextImpl
{
	public void logBug();
	public void logBug(String message);
	public void logBug(Throwable exc);
	public void logBug(String message, Throwable exc);
	public void logStaticResourceAccessIOFailure(@Nullable File resourceFile, @Nullable IOException exc);
	public void logStaticResourceAccessIntegrityFailure(@Nullable File resourceFile, @Nullable Exception exc);
	
	
	
	public static enum NoopingGlobalCodeMetastuffContextImpl
	implements GlobalCodeMetastuffContextImpl
	{
		I;
		
		@Override
		public void logBug()
		{
		}
		
		@Override
		public void logBug(String message)
		{
		}
		
		@Override
		public void logBug(Throwable exc)
		{
		}
		
		@Override
		public void logBug(String message, Throwable exc)
		{
		}
		
		@Override
		public void logStaticResourceAccessIOFailure(@Nullable File resourceFile, @Nullable IOException exc)
		{
		}
		
		@Override
		public void logStaticResourceAccessIntegrityFailure(@Nullable File resourceFile, @Nullable Exception exc)
		{
		}
	}
}
