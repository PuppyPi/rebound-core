/*
 * Created on Jan 16, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * {@link ReturnPath}'s mean a type of Java exception thing which will NOT have a valid stacktrace!
 * But is very purely just a means of extending the channel of possible return values.
 * (eg, for things which can return anything validly, like Iterator.next or Map.get ;> )
 * They may or may not be cached and may or may not be singleton (no guarantees on reference-equality!),
 * but just don't let them be passed up the stack as an actual exception! (without wrapping them, of course ;> )
 * 
 * @author Puppy Pie ^_^
 */
public abstract class ReturnPath
extends Throwable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * NOTE: this will contain the stack trace *of when you call this*, not when the return path was thrown!
	 * (that's kind of the point of return paths! XD )
	 */
	public abstract RuntimeException toException();
	
	
	/**
	 * A simplest type of {@link ReturnPath} which only ever has one instance, and is guaranteed to have
	 * a public static final field of its subclass type named "I" which is the singleton instance :>
	 * 
	 * But NOT ability to use reference-equality test in place of instanceof!
	 * So to make one, you don't have to implement readObject and writeObject to make it a serialization-proof singleton, like enum members ;_;
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static abstract class SingletonReturnPath
	extends ReturnPath
	{
		private static final long serialVersionUID = 1L;
	}
}
