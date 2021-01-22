package rebound.util.collections.prim;

import static rebound.util.BasicExceptionUtilities.*;
import java.util.List;
import rebound.annotations.semantic.SignalType;
import rebound.util.collections.ListWithFill;
import rebound.util.collections.ListWithRemoveRange;
import rebound.util.collections.ListWithSetAll;
import rebound.util.collections.ListWithSetSize;
import rebound.util.collections.NiceList;
import rebound.util.objectutil.Equivalenceable;
import rebound.util.objectutil.PubliclyCloneable;

@SignalType
public interface PrimitiveList<BoxedType, ArrayType>
extends PrimitiveCollection<BoxedType, ArrayType>, List<BoxedType>, NiceList<BoxedType>, ListWithRemoveRange, PubliclyCloneable, ListWithSetSize<BoxedType>, ListWithSetAll, ListWithFill<BoxedType>, Equivalenceable
{
	@Override
	public default void setFrom(final Object source)
	{
		if (!(source instanceof List))
			throw newClassCastExceptionOrNullPointerException(source);
		
		List otherList = (List) source;
		
		int selfSize = this.size();
		int sourceListSize = otherList.size();
		
		if (selfSize != sourceListSize)
			this.setSize(sourceListSize);
		
		assert this.size() == sourceListSize;
		int size = sourceListSize;  // = selfSize;
		
		this.setAll(0, otherList, 0, size);
	}
}
