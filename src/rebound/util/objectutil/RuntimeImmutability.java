package rebound.util.objectutil;

import javax.annotation.Nonnull;
import rebound.concurrency.immutability.JavaImmutability;

/**
 * While you can make something runtime-switchable concurrently immutable,
 * the usage has to do certain things;
 * 
 * eg,
 * 
 * Thread A:
 * 		1. Set things on the thing
 * 		2. Make the thing become immutable
 * 		3. Release the reference (eg, to thread B)
 * 
 * Thread B:
 * 		1. Do stuff.  (XD)
 * 
 * @author Puppy Pie ^_^
 */
public interface RuntimeImmutability
{
	@Nonnull
	public JavaImmutability isImmutable();
	
	
	
	
	public static interface Immutableable
	extends RuntimeImmutability
	{
		/**
		 * @return previous value
		 */
		@Nonnull
		public JavaImmutability becomeImmutable();
	}
	
	
	
	/**
	 * Be very careful with this one, of courses! XD
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static interface Mutableable
	extends RuntimeImmutability
	{
		/**
		 * @return previous value
		 */
		@Nonnull
		public JavaImmutability becomeMutable();
	}
	
	
	
	/**
	 * Be very careful with this one, of courses! XD
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static interface ReImmutableable
	extends RuntimeImmutability.Immutableable, RuntimeImmutability.Mutableable
	{
		/**
		 * @return previous value :>
		 */
		@Nonnull
		public JavaImmutability trySetImmutable(JavaImmutability newValue);
		
		/**
		 * ("generically" meaning, <code>true</code> could mean {@link JavaImmutability#Concurrently_Immutable} OR {@link JavaImmutability#Non_Thread_Safe_Immutable}!!)
		 * :>
		 * @return {previous value, new value}
		 */
		@Nonnull
		public JavaImmutability[] setGenericallyImmutable(boolean newValue);
	}
	
	
	
	public static interface ImmutableCopymakeable<I>
	{
		public I getImmutableVersion();
		
		public I getImmutableVersionOnlyIfCached();
		
		public boolean isImmutableVersionCached();
	}
}
//[Im]mutability-testing things! :D!>