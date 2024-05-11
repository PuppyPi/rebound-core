package rebound.util.collections.prim;

import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;

public interface BooleanListWithBitpackedBacking
extends BooleanList
{
	@ReadonlyValue
	public default Slice<byte[]> getUnderlyingByteArrayWithBitpackedPaddingBitsIfPresentOrNullIfNone()
	{
		return null;
	}
	
	
	@Override
	public default Slice<byte[]> getUnderlyingByteArrayWithUndefinedPaddingBitsIfPresentOrNullIfNone()
	{
		return getUnderlyingByteArrayWithBitpackedPaddingBitsIfPresentOrNullIfNone();
	}
}
