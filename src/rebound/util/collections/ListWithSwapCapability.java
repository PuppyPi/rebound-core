package rebound.util.collections;

import rebound.annotations.semantic.SignalInterface;

/*
 * Note! There exist things which expect this to NOT extend java.util.List!!!
 */
@SignalInterface
public interface ListWithSwapCapability
{
	/**
	 * + noop iff indexA == indexB
	 */
	public void swap(int indexA, int indexB);
}