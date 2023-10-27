package rebound.util.collections.prim;

import rebound.annotations.hints.ImplementationTransparency;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysBooleanCollection;

//TODO Elementwise boolean operations between BooleanLists!!  AND, OR, NOT, XOR!  \:D/

@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForBooleanList
extends DefaultToArraysBooleanCollection, NonuniformMethodsForBooleanList32, NonuniformMethodsForBooleanListMiscellaneous
{
	@Override
	public default long getBitfield(int offset, int length)
	{
		return NonuniformMethodsForBooleanList32.super.getBitfield(offset, length);
	}
}
