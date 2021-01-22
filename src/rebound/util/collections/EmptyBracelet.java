package rebound.util.collections;

import java.util.Collections;
import java.util.List;

public enum EmptyBracelet
implements Bracelet
{
	I;
	
	@Override
	public List asListFromCanonicalStartingPointAndReflection() throws UnsupportedOperationException
	{
		return Collections.EMPTY_LIST;
	}
}
