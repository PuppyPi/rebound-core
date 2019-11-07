package rebound.util.collections.prim;

import java.util.List;
import rebound.annotations.semantic.SignalType;

@SignalType
public interface PrimitiveList<BoxedType, ArrayType>
extends List<BoxedType>, PrimitiveCollection<BoxedType, ArrayType>
{
}
