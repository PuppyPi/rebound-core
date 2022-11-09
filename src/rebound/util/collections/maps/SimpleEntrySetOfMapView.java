package rebound.util.collections.maps;

import java.util.Map;

public class SimpleEntrySetOfMapView<K, V>
extends AbstractEntrySetOfMapView<K, V>
{
	protected final Map<K, V> owningMap;
	
	public SimpleEntrySetOfMapView(Map<K, V> owningMap)
	{
		this.owningMap = owningMap;
	}
	
	@Override
	public Map<K, V> getOwningMap()
	{
		return owningMap;
	}
}
