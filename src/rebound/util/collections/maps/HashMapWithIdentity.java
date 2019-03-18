package rebound.util.collections.maps;

import java.util.HashMap;
import java.util.Map;
import rebound.annotations.semantic.AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse;
import rebound.util.objectutil.StaticallyIdentityful;

@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
public class HashMapWithIdentity<K, V>
extends HashMap<K, V>
implements StaticallyIdentityful
{
	private static final long serialVersionUID = 1L;
	
	
	
	public HashMapWithIdentity()
	{
		super();
	}
	
	public HashMapWithIdentity(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}
	
	public HashMapWithIdentity(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	public HashMapWithIdentity(Map<? extends K, ? extends V> m)
	{
		super(m);
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
