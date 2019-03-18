package rebound.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import rebound.annotations.semantic.AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse;
import rebound.util.objectutil.StaticallyIdentityful;

@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
public class ArrayListWithIdentity<E>
extends ArrayList<E>
implements StaticallyIdentityful, KnowsLengthFixedness
{
	private static final long serialVersionUID = 1L;
	
	
	
	public ArrayListWithIdentity()
	{
		super();
	}
	
	public ArrayListWithIdentity(Collection<? extends E> c)
	{
		super(c);
	}
	
	public ArrayListWithIdentity(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	
	
	
	@Override
	public boolean equals(Object o)
	{
		return o == this;
	}
	
	@Override
	public int hashCode()
	{
		return System.identityHashCode(this);
	}
	
	
	@Override
	public Boolean isFixedLengthNotVariableLength()
	{
		return false;
	}
}
