package rebound.util.collections;

import java.util.Collections;
import java.util.List;

public enum EmptyBracelet
implements Bracelet
{
	I;
	
	@Override
	public int size()
	{
		return 0;
	}
	
	@Override
	public List asListFromCanonicalStartingPointAndReflection() throws UnsupportedOperationException
	{
		return Collections.EMPTY_LIST;
	}
}
