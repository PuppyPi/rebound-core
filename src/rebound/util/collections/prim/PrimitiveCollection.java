package rebound.util.collections.prim;

import java.util.Collection;
import rebound.annotations.semantic.SignalType;
import rebound.util.collections.CollectionWithDefaultElement;
import rebound.util.collections.RuntimeWriteabilityCollection;
import rebound.util.objectutil.Copyable;
import rebound.util.objectutil.DefaultToStringRestrictionCircumvention;

@SignalType
public interface PrimitiveCollection<BoxedType, ArrayType>
extends Collection<BoxedType>, CollectionWithDefaultElement<BoxedType>, Copyable, DefaultToStringRestrictionCircumvention, RuntimeWriteabilityCollection
{
	public Class getPrimitiveType();
	
	public Class<BoxedType> getBoxedType();
	
	public Class<ArrayType> getArrayType();
}
