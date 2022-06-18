package rebound.util.collections;

import rebound.annotations.semantic.SignalType;
import rebound.util.objectutil.Trimmable;

@SignalType
public interface CollectionWithTrimToSize
extends Trimmable
{
	public void trimToSize();
	
	
	@Override
	public default TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
	{
		trimToSize();
		return TrimmableTrimRV.DontKeepInvoking;
	}
}
