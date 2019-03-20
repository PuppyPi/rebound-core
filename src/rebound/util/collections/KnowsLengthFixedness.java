package rebound.util.collections;

import rebound.annotations.semantic.SignalType;

@SignalType
public interface KnowsLengthFixedness
{
	/**
	 * @return true = definitely fixed length, false = definitely variable length, null = unknown!  :>
	 */
	public Boolean isFixedLengthNotVariableLength();
}
