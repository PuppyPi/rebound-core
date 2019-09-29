package rebound.util.collections;

import rebound.annotations.semantic.SignalType;
import rebound.exceptions.NotSupportedReturnPath;

@SignalType
public interface Equivalenceable
{
	public boolean equivalent(Object other) throws NotSupportedReturnPath;
	public int hashCodeOfContents();
}
