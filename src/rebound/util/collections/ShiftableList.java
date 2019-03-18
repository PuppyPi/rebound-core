package rebound.util.collections;

import rebound.annotations.semantic.SignalInterface;

@SignalInterface
public interface ShiftableList
{
	/**
	 * After this is done, the size should be += by 'amount'! :D
	 * (Which natrually will be a decrease if amount is negative XD )
	 * 
	 * @see CollectionUtilities#shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(java.util.List, int, int)
	 */
	public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount);
}
