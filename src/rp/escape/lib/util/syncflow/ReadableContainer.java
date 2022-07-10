package rp.escape.lib.util.syncflow;

import rebound.annotations.semantic.AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse;
import rebound.annotations.semantic.SignalType;

@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
@FunctionalInterface
@SignalType
public interface ReadableContainer<T>
{
	public T get();
}
