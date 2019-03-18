/*
 * Created on May 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import java.util.Collection;
import java.util.RandomAccess;
import rebound.annotations.semantic.temporal.ConstantReturnValue;

/**
 * + Note: this implicitly implies both {@link Collection#size()}, {@link MaximumSizeCollection#getMaximumSize()} are {@link ConstantReturnValue}, always equal to {@link Collection#size()}
 * (if {@link #hasFixedSize()} is true, of course :> )
 * 
 * ^_^
 * 
 * @author Puppy Pie ^_^
 */
public interface FixedSizeCollection
{
	/**
	 * This allows, say, decorators and wrappers and etc. to implement this without
	 * requiring 2^N number of implementations for each signal interface they support XD''
	 * (*cough* {@link RandomAccess} *cough*  XD'' )
	 * 
	 * + Not implementing {@link FixedSizeCollection} should be counted the same as implementing it, but this being <code>false</code> :>
	 */
	public boolean hasFixedSize();
}
