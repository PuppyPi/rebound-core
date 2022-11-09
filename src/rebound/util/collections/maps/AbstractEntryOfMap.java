/*
 * Created on Apr 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import java.util.Map;
import java.util.Map.Entry;
import rebound.annotations.semantic.reachability.LiveMethod;

public class AbstractEntryOfMap<K, V>
implements Entry<K, V>, MapEntry<K, V>
{
	protected final Map<K, V> owningMap;
	protected final K key;
	protected V valueCache;
	
	
	public AbstractEntryOfMap(Map<K, V> owningMap, K key, V value)
	{
		this.owningMap = owningMap;
		this.key = key;
		this.valueCache = value;
	}
	
	public AbstractEntryOfMap(Map<K, V> owningMap, K key)
	{
		this.owningMap = owningMap;
		this.key = key;
		this.valueCache = owningMap.get(key);
	}
	
	
	
	
	
	@Override
	public Map<K, V> getOwningMap()
	{
		return owningMap;
	}
	
	@Override
	public K getKey()
	{
		return key;
	}
	
	@Override
	public V getValue()
	{
		return valueCache;
	}
	
	@Override
	@LiveMethod //specified by java.util collections framework API  ^_^
	public V setValue(V value)
	{
		V previous = getOwningMap().put(getKey(), value);
		this.valueCache =  value;
		return previous;
	}
	
	
	
	//Being silly and making up stuff ^w^  XD
	@LiveMethod
	public V remove()
	{
		return getOwningMap().remove(getKey());
	}
	
	
	
	
	
	@Override
	public int hashCode()
	{
		//Straight from the spec! ^w^
		return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Entry))
			return false;
		if (obj == this) return true;
		
		Entry e1 = this;
		Entry e2 = (Entry)obj;
		
		//Straight from the spec! ^w^
		return (e1.getKey() == null ? e2.getKey() == null : e1.getKey().equals(e2.getKey())) && (e1.getValue() == null ? e2.getValue() == null : e1.getValue().equals(e2.getValue()));
	}
	
	
	
	
	@Override
	public String toString()
	{
		return "("+getKey().toString()+", "+getValue().toString()+")";
	}
}
