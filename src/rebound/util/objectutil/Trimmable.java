/*
 * Created on Jun 28, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.objectutil;

import rebound.annotations.semantic.SignalInterface;

/**
 * The use of this interface is for things which could stand to use less memory if resources become scarce.
 * Like, imagine a user watching the ram usage and being like, Hey, you, program, don't get so handsy with the rams >,>
 * XD
 * Or an automated system which automatically invokes this on tons of things when memory becomes scarce. :>
 * 
 * So with that in mind, don't go like, clearing the entire 100 MB precomputed cache that octuples performance in the first invocation XD
 * It's set up so you can do things gradually, and if they want more trimming, then they can keep invoking it until it returns {@link TrimmableTrimRV#DontKeepInvoking}!  ^_^
 * 
 * 
 * 
 * ..also, yes, it is the Fluttershy interface.
 * XD!
 * :>!
 * 
 * @author RProgrammer
 */
@SignalInterface
public interface Trimmable
{
	public static enum TrimmableTrimRV
	{
		CouldKeepInvoking,
		DontKeepInvoking,
	}
	
	/**
	 * We uses an enum instead of a boolean here for clarifications so people don't just think this should return true if successful, or if unsuccessful! (real contract: return if it has become *possibly* idempotent!!)  :>
	 * 
	 * + Never supposeds to return <code>null</code>; throw an exception if they do -,-  or at least assume (the safer option:) {@link TrimmableTrimRV#DontKeepInvoking} :>
	 * 
	 * @return {@link TrimmableTrimRV#CouldKeepInvoking} if you could try again to reduce memory usage even more; {@link TrimmableTrimRV#DontKeepInvoking} if that's as good as it gets (object willing) or it might possibly not do anything on subsequent invocations orrrr just don't keep invoking it to further reduce usage X3
	 */
	public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind();
}
