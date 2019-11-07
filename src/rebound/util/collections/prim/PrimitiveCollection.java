package rebound.util.collections.prim;

import java.util.Collection;
import rebound.annotations.semantic.SignalType;

@SignalType
public interface PrimitiveCollection<BoxedType, ArrayType>
extends Collection<BoxedType>
{
	public Class getPrimitiveType();
	
	public Class<BoxedType> getBoxedType();
	
	public Class<ArrayType> getArrayType();
}
