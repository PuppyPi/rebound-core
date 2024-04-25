package rebound.math.intervals;

import static java.util.Objects.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import rebound.annotations.semantic.operationspecification.UnhashableType;
import rebound.annotations.semantic.simpledata.Emptyable;

@UnhashableType  //because how do we know what things are in the member intervals!?
@Immutable
public class ArithmeticGenericIntervalsUnion<N>
{
	/**
	 * These must be in order with no overlaps or touching-that-could-be-combined-to-one-bigger-interval!!
	 * + This means only the first one can have -∞ and the last one can have +∞ !
	 * + None of the internal intervals may be empty!
	 * + This list being empty is the unique way to encode the Empty Set! :D
	 */
	protected final @Emptyable @Nonnull List<ArithmeticGenericInterval<N>> intervals;
	
	public ArithmeticGenericIntervalsUnion(List<ArithmeticGenericInterval<N>> intervals)
	{
		this.intervals = requireNonNullElements(requireNonNull(intervals));
	}
	
	public List<ArithmeticGenericInterval<N>> getIntervals()
	{
		return intervals;
	}
}
