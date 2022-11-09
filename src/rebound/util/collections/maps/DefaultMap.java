package rebound.util.collections.maps;

import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.util.collections.CollectionUtilities;

public interface DefaultMap<K, V>
extends Map<K, V>
{
	//What you need to implement :3
	public @ReadonlyValue Set<K> keySet();
	public void clear();
	public int size();
	public boolean containsKey(Object key);
	public V get(Object key);
	public V put(K key, V value);
	public V remove(Object key);
	
	
	
	
	
	
	
	@Override
	public default Set<Entry<K, V>> entrySet()
	{
		return new SimpleEntrySetOfMapView<>(this);
	}
	
	@Override
	public default Collection<V> values()
	{
		return new SimpleValueCollectionOfMapView(this);
	}
	
	@Override
	public default boolean isEmpty()
	{
		return size() == 0;
	}
	
	@Override
	public default void putAll(Map<? extends K, ? extends V> m)
	{
		CollectionUtilities.defaultPutAll(this, m);
	}
	
	
	public default boolean containsValue(Object value)
	{
		for (K key : this.keySet())
			if (eq(value, this.get(key)))
				return true;
		return false;
	}
}
