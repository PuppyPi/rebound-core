package rebound.util.collections;

import rebound.annotations.semantic.SignalType;

/*
 * Note! There exist things which expect this to NOT extend java.util.List!!!
 */
@SignalType
public interface ListWithSwapCapability
{
	/**
	 * + noop iff indexA == indexB
	 */
	public void swap(int indexA, int indexB);
}