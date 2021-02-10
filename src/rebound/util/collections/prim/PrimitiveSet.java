package rebound.util.collections.prim;

import static rebound.util.BasicExceptionUtilities.*;
import java.util.Set;
import rebound.annotations.semantic.SignalType;
import rebound.util.objectutil.Equivalenceable;
import rebound.util.objectutil.PubliclyCloneable;

@SignalType
public interface PrimitiveSet<BoxedType, ArrayType>
extends PrimitiveCollection<BoxedType, ArrayType>, Set<BoxedType>, PubliclyCloneable, Equivalenceable
{
	@Override
	public default void setFrom(final Object source)
	{
		if (!(source instanceof Set))
			throw newClassCastExceptionOrNullPointerException(source);
		
		Set otherSet = (Set) source;
		
		this.clear();
		this.addAll(otherSet);
	}
}
