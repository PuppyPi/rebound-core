package rebound.util.collections;

import java.util.Collection;
import java.util.HashSet;
import rebound.annotations.semantic.AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse;
import rebound.util.objectutil.StaticallyIdentityful;

@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
public class HashSetWithIdentity<E>
extends HashSet<E>
implements StaticallyIdentityful
{
	private static final long serialVersionUID = 1L;
	
	
	
	public HashSetWithIdentity()
	{
		super();
	}
	
	public HashSetWithIdentity(Collection<? extends E> c)
	{
		super(c);
	}
	
	public HashSetWithIdentity(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}
	
	public HashSetWithIdentity(int initialCapacity)
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
}
