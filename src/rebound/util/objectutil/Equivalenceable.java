package rebound.util.objectutil;

import javax.annotation.Nullable;
import rebound.annotations.semantic.SignalType;
import rebound.exceptions.NotSupportedReturnPath;

@SignalType
public interface Equivalenceable
{
	/**
	 * @return always false if parameter is null
	 */
	public boolean equivalent(@Nullable Object obj) throws NotSupportedReturnPath;
	
	public int hashCodeOfContents();
}
