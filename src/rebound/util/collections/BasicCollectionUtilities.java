package rebound.util.collections;

import static java.util.Collections.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import rebound.exceptions.NonSingletonException;
import rebound.exceptions.StopIterationReturnPath;
import rebound.math.SmallIntegerMathUtilities;
import rebound.util.collections.SimpleIterator.SimpleIterable;
import rebound.util.objectutil.JavaNamespace;

public class BasicCollectionUtilities
implements JavaNamespace
{
	public static <E> E getSingleElement(Iterable<E> collection) throws NonSingletonException
	{
		return getSingleElement(collection.iterator());
	}
	
	public static <E> E getSingleElementOrNone(Iterable<E> collection, @Nullable E sentinelIfNone) throws NonSingletonException
	{
		return getSingleElementOrNone(collection.iterator(), sentinelIfNone);
	}
	
	
	@Nullable
	public static <E> E getSingleElementOrNullIfNone(Iterable<E> collection) throws NonSingletonException
	{
		return getSingleElementOrNone(collection, null);
	}
	
	
	
	
	
	
	public static <E> E getSingleElement(SimpleIterable<E> collection) throws NonSingletonException
	{
		return getSingleElement(collection.simpleIterator());
	}
	
	public static <E> E getSingleElementOrNone(SimpleIterable<E> collection, @Nullable E sentinelIfNone) throws NonSingletonException
	{
		return getSingleElementOrNone(collection.simpleIterator(), sentinelIfNone);
	}
	
	
	@Nullable
	public static <E> E getSingleElementOrNullIfNone(SimpleIterable<E> collection) throws NonSingletonException
	{
		return getSingleElementOrNone(collection, null);
	}
	
	
	
	
	
	
	
	
	
	public static <E> E getSingleElement(Iterator<E> i) throws NonSingletonException
	{
		if (!i.hasNext())
			throw new NonSingletonException("no elements! ><");
		E first = i.next();
		if (i.hasNext())
			throw new NonSingletonException("more than one element! o_o");
		return first;
	}
	
	public static <E> E getSingleElementOrNone(Iterator<E> i, @Nullable E sentinelIfNone) throws NonSingletonException
	{
		if (!i.hasNext())
			return sentinelIfNone;
		E first = i.next();
		if (i.hasNext())
			throw new NonSingletonException("more than one element! o_o");
		return first;
	}
	
	
	@Nullable
	public static <E> E getSingleElementOrNullIfNone(Iterator<E> collection) throws NonSingletonException
	{
		return getSingleElementOrNone(collection, null);
	}
	
	
	
	
	
	
	
	
	public static <E> E getSingleElementOrNoneOrMoreThanOne(Iterator<E> i, @Nullable E sentinelIfNone, @Nullable E sentinelIfMoreThanOne) throws NonSingletonException
	{
		if (!i.hasNext())
			return sentinelIfNone;
		E first = i.next();
		if (i.hasNext())
			return sentinelIfMoreThanOne;
		return first;
	}
	
	
	public static <E> E getSingleElementOrNoneOrMoreThanOne(Enumeration<E> e, @Nullable E sentinelIfNone, @Nullable E sentinelIfMoreThanOne) throws NonSingletonException
	{
		if (!e.hasMoreElements())
			return sentinelIfNone;
		E first = e.nextElement();
		if (e.hasMoreElements())
			return sentinelIfMoreThanOne;
		return first;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> E getSingleElement(SimpleIterator<E> collection) throws NonSingletonException
	{
		SimpleIterator<E> i = collection;
		
		E first;
		try
		{
			first = i.nextrp();
		}
		catch (StopIterationReturnPath exc)
		{
			throw new NonSingletonException("no elements! ><");
		}
		
		try
		{
			i.nextrp();
		}
		catch (StopIterationReturnPath exc)
		{
			return first;
		}
		
		throw new NonSingletonException("more than one element! o_o");
	}
	
	public static <E> E getSingleElementOrNone(SimpleIterator<E> collection, @Nullable E sentinelIfNone) throws NonSingletonException
	{
		SimpleIterator<E> i = collection;
		
		E first;
		try
		{
			first = i.nextrp();
		}
		catch (StopIterationReturnPath exc)
		{
			return sentinelIfNone;
		}
		
		try
		{
			i.nextrp();
		}
		catch (StopIterationReturnPath exc)
		{
			return first;
		}
		
		throw new NonSingletonException("more than one element! o_o");
	}
	
	
	@Nullable
	public static <E> E getSingleElementOrNullIfNone(SimpleIterator<E> collection) throws NonSingletonException
	{
		return getSingleElementOrNone(collection, null);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <K, V> K getSingleKey(Map<K, V> map) throws NonSingletonException
	{
		return getSingleElement(map.keySet());
	}
	
	public static <K, V> K getSingleKeyOrNone(Map<K, V> map, @Nullable K sentinelIfNone) throws NonSingletonException
	{
		return getSingleElementOrNone(map.keySet(), sentinelIfNone);
	}
	
	
	@Nullable
	public static <K, V> K getSingleKeyOrNullIfNone(Map<K, V> map) throws NonSingletonException
	{
		return getSingleKeyOrNone(map, null);
	}
	
	
	
	
	
	public static <K, V> V getSingleValue(Map<K, V> map) throws NonSingletonException
	{
		return getSingleElement(map.values());
	}
	
	public static <K, V> V getSingleValueOrNone(Map<K, V> map, @Nullable V sentinelIfNone) throws NonSingletonException
	{
		return getSingleElementOrNone(map.values(), sentinelIfNone);
	}
	
	
	@Nullable
	public static <K, V> V getSingleValueOrNullIfNone(Map<K, V> map) throws NonSingletonException
	{
		return getSingleValueOrNone(map, null);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected static final SimpleIterator Empty_SimpleIterator = () -> {throw StopIterationReturnPath.I;};
	
	public static <E> SimpleIterator<E> emptySimpleIterator()
	{
		return Empty_SimpleIterator;
	}
	
	
	
	protected static final SimpleIterable Empty_SimpleIterable = () -> Empty_SimpleIterator;
	
	public static <E> SimpleIterable<E> emptySimpleIterable()
	{
		return Empty_SimpleIterable;
	}
	
	
	
	
	
	public static interface IterableWithIsEmpty
	{
		public boolean isEmpty();
	}
	
	public static interface IterableWithSize
	{
		public int size();
	}
	
	public static boolean isEmptyIterable(final Iterable x)
	{
		if (x instanceof Collection)
			return ((Collection)x).isEmpty();
		if (x instanceof IterableWithIsEmpty)
			return ((IterableWithIsEmpty)x).isEmpty();
		if (x instanceof IterableWithSize)
			return ((IterableWithSize)x).size() == 0;
		
		return !x.iterator().hasNext();
	}
	
	public static boolean isEmptySimpleIterable(final SimpleIterable x)
	{
		if (x instanceof Collection)
			return ((Collection)x).isEmpty();
		if (x instanceof IterableWithIsEmpty)
			return ((IterableWithIsEmpty)x).isEmpty();
		if (x instanceof IterableWithSize)
			return ((IterableWithSize)x).size() == 0;
		
		try
		{
			x.simpleIterator().nextrp();
		}
		catch (StopIterationReturnPath exc)
		{
			return true;
		}
		
		return false;
	}
	
	public static int sizeOfCollectionlike(final Object x)
	{
		if (x == null)
			throw new NullPointerException();
		
		else if (x instanceof Object[])
		{
			return ((Object[])x).length;
		}
		else if (x.getClass().isArray()) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		{
			return Array.getLength(x);
		}
		else if (x instanceof Collection)
		{
			return ((Collection)x).size();
		}
		else if (x instanceof Map)
		{
			return ((Map)x).size(); //== x.values().size()  ^_^
		}
		else if (x instanceof CharSequence)
		{
			return ((CharSequence)x).length();
		}
		else if (x instanceof Buffer)
		{
			return ((Buffer)x).limit();
		}
		else if (x instanceof IterableWithSize)
		{
			return ((IterableWithSize)x).size();
		}
		else if (x instanceof Iterable)
		{
			//O(n) length calculation..but that's what needs to be dones for, eg, linked lists or C strings! :<
			int s = 0;
			for (@SuppressWarnings("unused") Object e : (Iterable)x)
				s = SmallIntegerMathUtilities.safe_inc_s32(s);
			return s;
		}
		//Iterators, Enumerations, SimpleIterators, etc. don't work because you might not be able to get back the stuff after we discard it! ;_;   (ie, you could figure out what the length *was*! ..but that's too late! XD    Much like the test for witches.. "destructive examination" XD!  )
		else
			throw new ClassCastException("Only arrays, "+Collection.class.getName()+"'s, "+Map.class.getName()+"'s(values), "+CharSequence.class.getName()+"'s, "+Buffer.class.getName()+"'s, and "+Iterable.class.getName()+"'s (O(n) length calculation XP) are supported; sorries ._.   not "+x.getClass().getName()+"'s");
	}
	
	
	
	
	
	
	
	
	public static <E> Set<E> emptyIfNull(Set<E> x)
	{
		return x == null ? emptySet() : x;
	}
	
	public static <E> List<E> emptyIfNull(List<E> x)
	{
		return x == null ? emptyList() : x;
	}
	
	public static <E> Collection<E> emptyIfNull(Collection<E> x)
	{
		return x == null ? emptySet() : x;
	}
	
	public static <E> Iterable<E> emptyIfNull(Iterable<E> x)
	{
		return x == null ? emptySet() : x;
	}
	
	public static <E> SimpleIterable<E> emptyIfNull(SimpleIterable<E> x)
	{
		return x == null ? emptySimpleIterable() : x;
	}
	
	public static <K,V> Map<K,V> emptyIfNull(Map<K,V> x)
	{
		return x == null ? emptyMap() : x;
	}
	
	
	
	
	public static <E> boolean isEmptyOrNull(Set<E> x)
	{
		return x == null || x.isEmpty();
	}
	
	public static <E> boolean isEmptyOrNull(List<E> x)
	{
		return x == null || x.isEmpty();
	}
	
	public static <E> boolean isEmptyOrNull(E[] x)
	{
		return x == null || x.length == 0;
	}
	
	public static <E> boolean isEmptyOrNull(CharSequence x)
	{
		return x == null || x.length() == 0;
	}
	
	public static <E> boolean isEmptyOrNull(Buffer x)
	{
		return x == null || x.remaining() == 0;
	}
	
	public static <E> boolean isEmptyOrNull(Collection<E> x)
	{
		return x == null || x.isEmpty();
	}
	
	public static <E> boolean isEmptyOrNull(Iterable<E> x)
	{
		return x == null || isEmptyIterable(x);
	}
	
	public static <E> boolean isEmptyOrNull(SimpleIterable<E> x)
	{
		return x == null || isEmptySimpleIterable(x);
	}
	
	public static <K,V> boolean isEmptyOrNull(Map<K,V> x)
	{
		return x == null || x.isEmpty();
	}
	
	
	
	
	
	
	/* <<<
	primxp
	public static boolean isEmptyOrNull(_$$prim$$_[] x)
	{
		return x == null || x.length == 0;
	}
	
	 */
	public static boolean isEmptyOrNull(boolean[] x)
	{
		return x == null || x.length == 0;
	}
	
	public static boolean isEmptyOrNull(byte[] x)
	{
		return x == null || x.length == 0;
	}
	
	public static boolean isEmptyOrNull(char[] x)
	{
		return x == null || x.length == 0;
	}
	
	public static boolean isEmptyOrNull(short[] x)
	{
		return x == null || x.length == 0;
	}
	
	public static boolean isEmptyOrNull(float[] x)
	{
		return x == null || x.length == 0;
	}
	
	public static boolean isEmptyOrNull(int[] x)
	{
		return x == null || x.length == 0;
	}
	
	public static boolean isEmptyOrNull(double[] x)
	{
		return x == null || x.length == 0;
	}
	
	public static boolean isEmptyOrNull(long[] x)
	{
		return x == null || x.length == 0;
	}
	
	
	//>>>
}
