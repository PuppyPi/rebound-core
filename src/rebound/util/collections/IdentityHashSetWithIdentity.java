package rebound.util.collections;

import rebound.util.objectutil.StaticallyIdentityful;

// XD
public class IdentityHashSetWithIdentity<E>
extends IdentityHashSet<E>
implements StaticallyIdentityful
{
	private static final long serialVersionUID = 1L;
	
	
	
	
	public IdentityHashSetWithIdentity()
	{
		super();
	}
	
	public IdentityHashSetWithIdentity(int expectedMaxSize)
	{
		super(expectedMaxSize);
	}
	
	public IdentityHashSetWithIdentity(Iterable<E> initialContents)
	{
		super(initialContents);
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
