package rp.escape.lib.util.syncflow;

import rebound.annotations.semantic.AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse;
import rebound.annotations.semantic.SignalType;
import rebound.concurrency.immutability.StaticallyMutable;

@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
@SignalType
public interface MutableContainer<T>
extends ReadableContainer<T>, StaticallyMutable
{
	public void set(T v);
	
	
	public default T replace(T newValue)
	{
		T old = this.get();
		this.set(newValue);
		return old;
	}
}
