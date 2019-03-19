package rebound.util.collections;

import java.util.List;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.SignalInterface;
import rebound.annotations.semantic.temporal.ConstantReturnValue;
import rebound.util.objectutil.UnderlyingInstanceAccessible;

@SignalInterface
public interface Sublist<E>
extends List<E>, UnderlyingInstanceAccessible<List<E>>, KnowsLengthFixedness, TransparentContiguousArrayBackedCollection
{
	@ConstantReturnValue
	@ImplementationTransparency
	@Override
	public List<E> getUnderlying();
	
	
	@ConstantReturnValue
	@ImplementationTransparency
	public int getSublistStartingIndex();
	
	
	//public int size();
	
	
	
	
	
	
	
	@Override
	public default Boolean isFixedLengthNotVariableLength()
	{
		return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.getUnderlying());
	}
	
	
	
	
	
	
	@Override
	public default boolean isTransparentContiguousArrayBackedCollection()
	{
		return TransparentContiguousArrayBackedCollection.is(this.getUnderlying());
	}
	
	@Override
	public default Slice<?> getLiveContiguousArrayBackingUNSAFE()
	{
		Slice<?> s = ((TransparentContiguousArrayBackedCollection)getUnderlying()).getLiveContiguousArrayBackingUNSAFE();
		return s.subslice(this.getSublistStartingIndex(), size());
	}
}
