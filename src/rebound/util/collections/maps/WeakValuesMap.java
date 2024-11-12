/*
 * Created on Apr 12, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.reachability.gc.WeakValues;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.StopIterationReturnPath;
import rebound.util.collections.CollectionUtilities;
import rebound.util.collections.PolymorphicCollectionUtilities;
import rebound.util.collections.SimpleIterator;
import rebound.util.objectutil.ContainsPurgableWeakReferences;
import rebound.util.objectutil.Trimmable;

//Normally @StrongKeys, but you could back this with a WeakHashMap and then it would be weak on both ends!! XD
@WeakValues
public class WeakValuesMap<K, V>
implements Map<K, V>, Trimmable, WeakValuedMap, ContainsPurgableWeakReferences
{
	protected Map<K, Reference<V>> underlying;
	protected boolean purgeFrequently = false;
	
	
	public WeakValuesMap(Map<K, Reference<V>> underlying)
	{
		this.underlying = underlying;
	}
	
	/**
	 * Use default backing :3
	 */
	public WeakValuesMap()
	{
		this(new HashMap<K, Reference<V>>());
	}
	
	
	@ImplementationTransparency
	public Map<K, Reference<V>> getUnderlying()
	{
		return this.underlying;
	}
	
	@ImplementationTransparency
	public void setUnderlying(Map<K, Reference<V>> underlying)
	{
		this.underlying = underlying;
	}
	
	
	/**
	 * eg, you could use {@link SoftReference}s instead :33 
	 */
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	protected Reference<V> newReference(V value)
	{
		return new WeakReference<>(value);
	}
	
	
	
	
	
	@Override
	public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
	{
		purgeClearedWeakReferences();
		return TrimmableTrimRV.DontKeepInvoking;
	}
	
	
	
	@Override
	public int size()
	{
		if (purgeFrequently) purgeClearedWeakReferences();
		return underlying.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		if (purgeFrequently) purgeClearedWeakReferences();
		return underlying.isEmpty();
	}
	
	@Override
	public boolean containsValue(Object value)
	{
		if (purgeFrequently) purgeClearedWeakReferences();
		return underlying.containsValue(value);
	}
	
	
	
	
	
	public void ensurePurged(Object key)
	{
		get(key);  //works unless we change get() XD
	}
	
	@Override
	public V get(Object key)
	{
		Reference<V> ref = underlying.get(key);
		
		if (ref != null)
		{
			V v = ref.get();
			
			if (v == null)
				underlying.remove(key);
			
			return v;
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public boolean containsKey(Object key)
	{
		Reference<V> ref = underlying.get(key);
		
		if (ref != null)
		{
			V v = ref.get();
			
			if (v == null)
			{
				underlying.remove(key);
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}
	
	
	
	
	
	@Override
	public V put(K key, V value)
	{
		if (value == null)
			throw new NotYetImplementedException("null values not supported, sorries XP");  //Todo do the encoding-to-null *AND decoding on ALL reads!!* \o/
		
		Reference<V> ref = underlying.put(key, newReference(value));
		return ref != null ? ref.get() : null;
	}
	
	
	
	@Override
	public V remove(Object key)
	{
		Reference<V> ref = underlying.remove(key);
		return ref != null ? ref.get() : null;
	}
	
	
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		for (Entry<? extends K, ? extends V> e : m.entrySet())
		{
			this.put(e.getKey(), e.getValue());
		}
	}
	
	
	
	@Override
	public void clear()
	{
		//No need to purgeeee!! XDD
		underlying.clear();
	}
	
	
	
	
	
	
	
	
	/*
	 * ALL ITERATION OVER THE MAP MUST BE FUNNELED THROUGH keySet().iterator() !!!
	 * Otherwise ConcurrentModificationExceptions can happennnnnn!! /O/
	 */
	
	
	@Override
	public void purgeClearedWeakReferences()
	{
		for (@SuppressWarnings("unused") K k : keySet());
	}
	
	
	
	protected final Set<K> keySetView = new AbstractKeySetOfMapView<K>()
	{
		@Override
		public Map<K, ?> getOwningMap()
		{
			return WeakValuesMap.this;
		}
		
		@Override
		public Object getActualKeysOrConstructSnapshotOfKeysForReadingOnly()
		{
			return PolymorphicCollectionUtilities.anyToList(iterator());
		}
		
		public Iterator<K> iterator()
		{
			Iterator<K> u = underlying.keySet().iterator();
			
			return SimpleIterator.defaultToIterator(
			
			//This is where the magic happens! 8>
			new SimpleIterator<K>()
			{
				@Override
				public K nextrp() throws StopIterationReturnPath
				{
					while (u.hasNext())
					{
						K k = u.next();
						
						Reference<V> ref = WeakValuesMap.this.underlying.get(k);
						
						if (ref == null)
							throw new AssertionError();
						
						boolean wasGarbageCollectedSinceAdded = ref.get() == null;
						
						if (wasGarbageCollectedSinceAdded)
							u.remove();
						else
							return k;
					}
					
					throw StopIterationReturnPath.I;
				}
			}
			
			);
		}
		
		
		
		@Override
		public int hashCode()
		{
			return System.identityHashCode(this);
		}
		
		@Override
		public boolean equals(Object o)
		{
			return o == this;
		}
	};
	
	@Override
	public Set<K> keySet()
	{
		return keySetView;
	}
	
	
	
	
	
	
	
	protected final Collection<V> valueCollectionView = new AbstractValueCollectionOfMapView<V>()
	{
		@Override
		public Map<?, V> getOwningMap()
		{
			return WeakValuesMap.this;
		}
		
		public Collection<V> getActualValuesOrConstructSnapshotOfValues()
		{
			return CollectionUtilities.mapToNewCollection(Map.Entry::getValue, getOwningMap().entrySet());
		}
		
		public java.util.Iterator<V> iterator()
		{
			return getActualValuesOrConstructSnapshotOfValues().iterator();
		}
		
		
		
		@Override
		public int hashCode()
		{
			return System.identityHashCode(this);
		}
		
		@Override
		public boolean equals(Object o)
		{
			return o == this;
		}
	};
	
	@Override
	public Collection<V> values()
	{
		return valueCollectionView;
	}
	
	
	
	
	
	
	
	protected final Set<Entry<K, V>> entrySetView = new AbstractEntrySetOfMapView<K, V>()
	{
		@Override
		public Map<K, V> getOwningMap()
		{
			return WeakValuesMap.this;
		}
		
		@Override
		public Object getActualEntriesOrConstructSnapshotOfEntries()
		{
			return PolymorphicCollectionUtilities.anyToList(iterator());
		}
		
		@Override
		public Iterator<Entry<K, V>> iterator()
		{
			return super.iterator();
		}
		
		
		
		@Override
		public int hashCode()
		{
			return System.identityHashCode(this);
		}
		
		@Override
		public boolean equals(Object o)
		{
			return o == this;
		}
	};
	
	@Override
	public Set<Entry<K, V>> entrySet()
	{
		return entrySetView;
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public int hashCode()
	{
		return System.identityHashCode(this);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o == this;
	}
}
