package rebound.util.collections;

import java.util.Collections;
import java.util.List;

public enum EmptyNecklace
implements Necklace
{
	I;
	
	@Override
	public List asListFromCanonicalStartingPoint() throws UnsupportedOperationException
	{
		return Collections.EMPTY_LIST;
	}
}
