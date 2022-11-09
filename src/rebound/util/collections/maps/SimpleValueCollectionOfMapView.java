package rebound.util.collections.maps;

import java.util.Map;

public class SimpleValueCollectionOfMapView<V>
extends AbstractValueCollectionOfMapView<V>
{
	protected final Map<?, V> owningMap;
	
	public SimpleValueCollectionOfMapView(Map<?, V> owningMap)
	{
		this.owningMap = owningMap;
	}
	
	@Override
	public Map<?, V> getOwningMap()
	{
		return owningMap;
	}
}
