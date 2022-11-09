package rebound.util.collections.maps;

import static java.util.Objects.*;
import static rebound.testing.WidespreadTestingUtilities.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;

/**
 * This combines {@link Map} and {@link List} internally to make a 
 */
public class FullyAsymptoticallyNiceMap<K, V>
implements DefaultMap<K, V>, IndexableMap<K, V>
{
	protected final Map<K, Integer> map;
	protected final List<K> keyList;
	protected final List<V> valueList;
	
	public FullyAsymptoticallyNiceMap()
	{
		this(new HashMap<>(), new ArrayList<>(), new ArrayList<>());
	}
	
	public FullyAsymptoticallyNiceMap(Map<K, Integer> map, List<K> keyList, List<V> valueList)
	{
		this.map = requireNonNull(map);
		this.keyList = requireNonNull(keyList);
		this.valueList = requireNonNull(valueList);
	}
	
	
	
	
	
	
	@Override
	public K getKeyByIndex(int index) throws IndexOutOfBoundsException
	{
		return keyList.get(index);
	}
	
	@Override
	public V getValueByIndex(int index) throws IndexOutOfBoundsException
	{
		return valueList.get(index);
	}
	
	@Override
	public Entry<K, V> getEntryByIndex(int index) throws IndexOutOfBoundsException
	{
		return new SimpleEntry<>(keyList.get(index), valueList.get(index));
	}
	
	
	
	
	
	
	
	
	
	public void clear()
	{
		map.clear();
		keyList.clear();
		valueList.clear();
	}
	
	public int size()
	{
		return keyList.size();
	}
	
	public boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}
	
	public V get(Object key)
	{
		Integer i = map.get(key);
		return i == null ? null : valueList.get(i);
	}
	
	public V put(K key, V value)
	{
		Integer i = map.get(key);
		
		if (i == null)
		{
			int ii = size();
			keyList.add(key);
			valueList.add(value);
			map.put(key, ii);
			return null;
		}
		else
		{
			keyList.set(i, key);
			V previousValue = valueList.set(i, value);
			map.put(key, i);
			return previousValue;
		}
	}
	
	
	
	
	public V remove(Object key)
	{
		Integer i = map.get(key);
		
		if (i == null)
		{
			return null;
		}
		else
		{
			return removeByBoth(i, key);
		}
	}
	
	@Override
	public Entry<K, V> removeByIndex(int index) throws IndexOutOfBoundsException
	{
		K k = getKeyByIndex(index);
		V v = removeByBoth(index, k);
		return new SimpleEntry<>(k, v);
	}
	
	protected V removeByBoth(int index, Object key)
	{
		asrt(!isEmpty());  //if it's empty we can't use the "last index"!
		int last = size() - 1;
		
		V previousValue;
		
		if (index == last)
		{
			keyList.remove(index);
			previousValue = valueList.remove(index);
			Integer i = map.remove(key);
			asrt(i != null);
			asrt(i.intValue() == index);
		}
		else
		{
			//We need to swap things around so that it's still a contiguous block of indexes!!
			//Luckily we can do this in constant time since the ordering doesn't need to be consistent at all!! :D
			
			K lastKey = keyList.remove(last);
			V lastValue = valueList.remove(last);
			
			keyList.set(index, lastKey);
			previousValue = valueList.set(index, lastValue);
			map.put(lastKey, index);
			
			Integer i = map.remove(key);
			asrt(i != null);
			asrt(i.intValue() == index);
		}
		
		return previousValue;
	}
	
	
	
	
	
	
	
	
	
	
	protected final KeySet keySet = new KeySet();
	
	@Override
	public @ReadonlyValue Set<K> keySet()
	{
		return keySet;
	}
	
	protected class KeySet
	extends AbstractKeySetOfIndexableMapView<K>
	{
		@Override
		public IndexableMap<K, ?> getOwningMap()
		{
			return FullyAsymptoticallyNiceMap.this;
		}
	}
	
	
	
	
	
	
	
	protected final ValueCollection valueCollection = new ValueCollection();
	
	@Override
	public @ReadonlyValue Collection<V> values()
	{
		return valueCollection;
	}
	
	protected class ValueCollection
	extends AbstractValueCollectionOfIndexableMapView<V>
	{
		@Override
		public IndexableMap<?, V> getOwningMap()
		{
			return FullyAsymptoticallyNiceMap.this;
		}
	}
	
	
	
	
	
	
	
	protected final EntrySet entrySet = new EntrySet();
	
	@Override
	public @ReadonlyValue Set<Entry<K, V>> entrySet()
	{
		return entrySet;
	}
	
	protected class EntrySet
	extends AbstractEntrySetOfIndexableMapView<K, V>
	{
		@Override
		public IndexableMap<K, V> getOwningMap()
		{
			return FullyAsymptoticallyNiceMap.this;
		}
	}
}
