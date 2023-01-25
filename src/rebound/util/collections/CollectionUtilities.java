/*
 * Created on Aug 17, 2010
 * 	by the great Eclipse(c)
 */
package rebound.util.collections;

import static java.util.Collections.*;
import static java.util.Objects.*;
import static rebound.GlobalCodeMetastuffContext.*;
import static rebound.bits.BitfieldSafeCasts.*;
import static rebound.math.MathUtilities.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.testing.WidespreadTestingUtilities.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.BasicExceptionUtilities.*;
import static rebound.util.CodeHinting.*;
import static rebound.util.Primitives.*;
import static rebound.util.collections.BasicCollectionUtilities.*;
import static rebound.util.collections.SimpleIterator.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import static rebound.util.objectutil.ObjectUtilities.*;
import java.awt.Container;
import java.lang.reflect.Array;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Signed;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.operationspecification.CollectionValue;
import rebound.annotations.semantic.operationspecification.HashableValue;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.simpledata.Nonempty;
import rebound.annotations.semantic.simpledata.NonnullElements;
import rebound.annotations.semantic.simpledata.NonnullKeys;
import rebound.annotations.semantic.simpledata.NonnullValues;
import rebound.annotations.semantic.simpledata.NullableElements;
import rebound.exceptions.AlreadyExistsException;
import rebound.exceptions.DuplicatesException;
import rebound.exceptions.EmptyInputReturnPath;
import rebound.exceptions.EmptyInputReturnPath.EmptyInputException;
import rebound.exceptions.GenericDataStructuresFormatException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NoSuchElementReturnPath;
import rebound.exceptions.NoSuchMappingReturnPath;
import rebound.exceptions.NoSuchMappingReturnPath.NoSuchMappingException;
import rebound.exceptions.NonForwardInjectiveMapException;
import rebound.exceptions.NonReverseInjectiveMapException;
import rebound.exceptions.NonSurjectiveMapException;
import rebound.exceptions.NotFoundException;
import rebound.exceptions.NotSingletonException;
import rebound.exceptions.NotSupportedReturnPath;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.OverflowException;
import rebound.exceptions.ReadonlyUnsupportedOperationException;
import rebound.exceptions.StopIterationReturnPath;
import rebound.exceptions.StructuredClassCastException;
import rebound.math.Direction1D;
import rebound.math.MathUtilities;
import rebound.math.SmallIntegerMathUtilities;
import rebound.text.StringUtilities.WhatToDoWithEmpties;
import rebound.util.Either;
import rebound.util.IdentityCardinality;
import rebound.util.Maybe;
import rebound.util.Primitives;
import rebound.util.classhacking.jre.BetterJREGlassbox;
import rebound.util.classhacking.jre.JREGlassBox;
import rebound.util.collections.SimpleIterator.SimpleIterable;
import rebound.util.collections.maps.EquivalenceMap;
import rebound.util.collections.maps.IdentityMap;
import rebound.util.collections.maps.MapWithBoundKeyEqualityComparator;
import rebound.util.collections.maps.MapWithBoundValueEqualityComparator;
import rebound.util.collections.maps.MapWithGetRandomEntry;
import rebound.util.collections.maps.WeakKeyedMap;
import rebound.util.collections.maps.WeakValuedMap;
import rebound.util.collections.prim.PrimitiveCollection;
import rebound.util.collections.prim.PrimitiveCollections;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.ByteCollection;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.CharacterCollection;
import rebound.util.collections.prim.PrimitiveCollections.CharacterList;
import rebound.util.collections.prim.PrimitiveCollections.DoubleCollection;
import rebound.util.collections.prim.PrimitiveCollections.DoubleList;
import rebound.util.collections.prim.PrimitiveCollections.FloatCollection;
import rebound.util.collections.prim.PrimitiveCollections.FloatList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableBooleanArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteIntervalList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteIntervalSet;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableCharacterArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableCharacterIntervalList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableCharacterIntervalSet;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableDoubleArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableFloatArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableIntegerArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableIntegerIntervalList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableIntegerIntervalSet;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableLongArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableLongIntervalList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableLongIntervalSet;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableShortArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableShortIntervalList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableShortIntervalSet;
import rebound.util.collections.prim.PrimitiveCollections.IntegerCollection;
import rebound.util.collections.prim.PrimitiveCollections.IntegerList;
import rebound.util.collections.prim.PrimitiveCollections.LongCollection;
import rebound.util.collections.prim.PrimitiveCollections.LongList;
import rebound.util.collections.prim.PrimitiveCollections.ShortCollection;
import rebound.util.collections.prim.PrimitiveCollections.ShortList;
import rebound.util.collections.prim.PrimitiveCollections.SimpleBooleanIterable;
import rebound.util.collections.prim.PrimitiveCollections.SimpleBooleanIterator;
import rebound.util.collections.prim.PrimitiveCollections.SortedByteSetBackedByList;
import rebound.util.collections.prim.PrimitiveCollections.SortedCharacterSetBackedByList;
import rebound.util.collections.prim.PrimitiveCollections.SortedDoubleSetBackedByList;
import rebound.util.collections.prim.PrimitiveCollections.SortedFloatSetBackedByList;
import rebound.util.collections.prim.PrimitiveCollections.SortedIntegerSetBackedByList;
import rebound.util.collections.prim.PrimitiveCollections.SortedLongSetBackedByList;
import rebound.util.collections.prim.PrimitiveCollections.SortedShortSetBackedByList;
import rebound.util.container.ContainerInterfaces.BooleanContainer;
import rebound.util.container.ContainerInterfaces.ObjectContainer;
import rebound.util.container.SimpleContainers.SimpleBooleanContainer;
import rebound.util.container.SimpleContainers.SimpleObjectContainer;
import rebound.util.functional.CollectionFunctionalIterable;
import rebound.util.functional.ContinueSignal;
import rebound.util.functional.EqualityComparator;
import rebound.util.functional.FunctionInterfaces.BinaryFunction;
import rebound.util.functional.FunctionInterfaces.BinaryFunctionToBoolean;
import rebound.util.functional.FunctionInterfaces.NullaryFunction;
import rebound.util.functional.FunctionInterfaces.UnaryFunction;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionBooleanToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionByteToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionCharToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionDoubleToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionFloatToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToInt;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToObject;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionLongToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionShortToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryProcedure;
import rebound.util.functional.FunctionInterfaces.UnaryProcedureBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryProcedureInt;
import rebound.util.functional.MapFunctionalIterable;
import rebound.util.functional.SuccessfulIterationStopType;
import rebound.util.functional.functions.DefaultEqualityComparator;
import rebound.util.objectutil.BasicObjectUtilities;
import rebound.util.objectutil.Equivalenceable;
import rebound.util.objectutil.PubliclyCloneable;

//TODO Implement the NotYetImplemented things ^^''
//TODO reorderElement (in list) :>
//TODO reorderElements (in list) :>
//TODO rename mergeLists to concat (edit: ALREADY?! REALLY XDDD''')—new todo: Resolve duplicates between Merge and (Concatenate, Union)  ^^'''
//Todo Appensions (edit: Contatenations?):   Lightweight views of two or more [sub]lists which acts like the them merged back to back!  Can be combined with sublists and used on efficiently-backed char or boolean GeneralLists to make huge Text and Bit string manipulations SUUUUPER efficient! 8>
//Todo toRichGeneralList, toNewVariableLengthMutableRichGeneralList  8>
//Todo toNewMutableRichGeneralList :>
//Todo our own unmodifiableList view-creator system (eg, a trait interface!) for supporting PrimitiveLists and DirectByteList.asReadonly()! :D

public class CollectionUtilities
{
	public static <E> E nextMandatory(SimpleIterator<E> i) throws NoSuchElementException
	{
		try
		{
			return i.nextrp();
		}
		catch (StopIterationReturnPath r)
		{
			throw new NoSuchElementException();
		}
	}
	
	public static <E> E nextMandatory(SimpleIterator<E> i, String message) throws NoSuchElementException
	{
		try
		{
			return i.nextrp();
		}
		catch (StopIterationReturnPath r)
		{
			throw new NoSuchElementException(message);
		}
	}
	
	
	
	public static <E> void validateRowsAllSameSize(List<List<E>> rows) throws IllegalArgumentException
	{
		if (!areRowsAllTheSameSize(rows))
			throw new IllegalArgumentException("Rows are not all the same number of cells/columns!! \\o/");
	}
	
	
	
	
	public static <E> boolean areRowsAllTheSameSize(List<List<E>> rows)
	{
		boolean has = false;
		int size = 0;
		
		for (List<?> row : rows)
		{
			if (!has)
			{
				size = row.size();
				has = true;
			}
			else
			{
				if (row.size() != size)
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	
	
	public static <K, V> V getrp(Map<K, V> map, K key) throws NoSuchMappingReturnPath
	{
		if (map instanceof MapWithGetRP)
		{
			return ((MapWithGetRP<K, V>)map).getrp(key);
		}
		else
		{
			V v = map.get(key);
			
			//Only check containsKey if necessary!  ;D
			if (v != null)
			{
				return v;
			}
			else
			{
				if (map.containsKey(key))
				{
					return null;
				}
				else
				{
					throw NoSuchMappingReturnPath.I;
				}
			}
		}
	}
	
	
	/**
	 * @return (value which may be actually null or if absent always null,     was value present?)
	 */
	public static <K, V> PairOrdered<V, Boolean> getp(Map<K, V> map, K key)
	{
		try
		{
			return pair(getrp(map, key), true);
		}
		catch (NoSuchMappingReturnPath exc)
		{
			return pair(null, false);
		}
	}
	
	
	public static <K, V> V getMandatory(Map<K, V> map, K key) throws NoSuchMappingException
	{
		try
		{
			return getrp(map, key);
		}
		catch (NoSuchMappingReturnPath exc)
		{
			throw new NoSuchMappingException("Key "+repr(key)+" not found in "+repr(map));
		}
	}
	
	
	
	
	public static <K, V> V removerp(Map<K, V> map, K key) throws NoSuchMappingReturnPath
	{
		//We have to always call containsKey() because if it returns null, we can't tell if it was really null valued or absent after it's been *removed*! xD'
		if (map.containsKey(key))
			return map.remove(key);
		else
			throw NoSuchMappingReturnPath.I;
	}
	
	
	public static <K, V> V removeMandatory(Map<K, V> map, K key) throws NoSuchMappingException
	{
		try
		{
			return removerp(map, key);
		}
		catch (NoSuchMappingReturnPath exc)
		{
			throw new NoSuchMappingException("Key "+repr(key)+" not found in "+repr(map));
		}
	}
	
	public static <E> boolean removeMandatory(Collection<E> collection, E element) throws NoSuchElementException
	{
		if (collection.contains(element))
			return collection.remove(element);
		else
			throw new NoSuchMappingException("Element "+repr(element)+" not found in "+repr(collection));
	}
	
	public static <K, V> void removeExactMandatory(Map<K, V> map, K key, V value) throws NoSuchMappingException
	{
		V v;
		try
		{
			v = getrp(map, key);
		}
		catch (NoSuchMappingReturnPath exc)
		{
			throw new NoSuchMappingException("Key "+repr(key)+" not found in "+repr(map));
		}
		
		if (!eq(v, value))
			throw new NoSuchMappingException("Key "+repr(key)+" was not mapped to "+repr(value)+" in "+repr(map));
		
		try
		{
			removerp(map, key);
		}
		catch (NoSuchMappingReturnPath exc)
		{
			throw new ImpossibleException("We already checked it was present in the map with getrp()!");
		}
	}
	
	public static <E> boolean removeIfWithStopSignal(Collection<E> c, UnaryFunction<? super E, Either<Boolean, Boolean>> filter)
	{
		if (c instanceof CollectionWithRemoveIfWithStopSignal)
			return ((CollectionWithRemoveIfWithStopSignal<E>)c).removeIfWithStopSignal(filter);
		else
			return defaultRemoveIfWithStopSignal(c, filter);
	}
	
	
	
	
	
	public static <E> void addNewMandatory(Collection<E> collection, E element) throws AlreadyExistsException
	{
		if (!collection.add(element))
			throw new AlreadyExistsException("Duplicate element attempted to be added: "+repr(element));
	}
	
	public static <E> void addAllNewMandatory(Collection<E> dest, Iterable<E> source) throws AlreadyExistsException
	{
		for (E e : source)
			addNewMandatory(dest, e);
	}
	
	
	
	public static <K, V> void putNewMandatory(Map<K, V> map, K key, V value) throws AlreadyExistsException
	{
		if (map.containsKey(key))
			throw new AlreadyExistsException("Conflict between keys: "+repr(key)+"   The current value is: "+repr(map.get(key))+" and the new conflicting value is: "+repr(value));
		map.put(key, value);
	}
	
	public static <K, V> void putAllNewMandatory(Map<K, V> dest, Map<K, V> source) throws AlreadyExistsException
	{
		for (Entry<K, V> e : source.entrySet())
			putNewMandatory(dest, e.getKey(), e.getValue());
	}
	
	
	
	/**
	 * Unlike {@link #putNewMandatory(Map, Object, Object)},
	 * this one will NOT fail if an {@link Object#equals(Object) equivalent} value is already mapped to that key! :DD
	 * It only fails upon attempting to map *different* values to the same key!! ;D
	 */
	public static <K, V> void putNewUniqueMandatory(Map<K, V> map, K key, V newValue) throws AlreadyExistsException
	{
		V currentValue = map.get(key);
		if (currentValue != null || map.containsKey(key))
		{
			if (!eq(currentValue, newValue))
				throw new AlreadyExistsException("Conflict between keys: "+repr(key)+"   The current value is: "+repr(currentValue)+" and the new conflicting value is: "+repr(newValue));
		}
		
		map.put(key, newValue);
	}
	
	public static <K, V> void putAllNewUniqueMandatory(Map<K, V> dest, Map<K, V> source) throws AlreadyExistsException
	{
		for (Entry<K, V> e : source.entrySet())
			putNewUniqueMandatory(dest, e.getKey(), e.getValue());
	}
	
	
	
	
	
	
	
	/**
	 * An alternative to {@link Map#clear()}: set all the entries to null.
	 */
	public static void nullify(Map map)
	{
		for (Object key : map.keySet())
		{
			map.put(key, null);
		}
	}
	
	
	
	
	
	
	public static Boolean isIdentityMap(Map map)
	{
		if (map instanceof IdentityMap)
			return true;
		if (map instanceof IdentityHashMap)
			return true;
		if (map instanceof EnumMap)
			return true;
		return null;
	}
	
	public static boolean isIdentityMapConventionalDefault(Map map)
	{
		return Primitives.unboxNT(isIdentityMap(map), false);
	}
	
	public static Boolean isEquivalenceMap(Map map)
	{
		if (map instanceof EquivalenceMap)
			return false;
		if (map instanceof IdentityHashMap)
			return false;
		if (map instanceof EnumMap)
			return true; // it's the same for enum instances ;>
		return null;
	}
	
	public static boolean isEquivalenceMapConventionalDefault(Map map)
	{
		return Primitives.unboxNT(isEquivalenceMap(map), true);
	}
	
	
	
	
	
	
	
	
	@Nullable
	public static <V> V getTypeCheckingNullable(Map map, Object key, Class<V> type) throws NotFoundException, StructuredClassCastException
	{
		if (type.isPrimitive())
			type = Primitives.getWrapperClassFromPrimitiveOrPassThroughWrapper(type);
		
		
		Object v = map.get(key);
		
		if (v == null)
		{
			if (map.containsKey(key))
			{
				//Oh, it's legitimately null! XD
				return null;
			}
			else
			{
				throw new NotFoundException(repr(key)+" was not found in "+repr(map));
			}
		}
		else
		{
			if (type.isInstance(v))
			{
				return (V)v;
			}
			else
			{
				throw new StructuredClassCastException(v.getClass().getName()+" encountered, not "+type.getClass().getName(), v.getClass(), type);
			}
		}
	}
	
	
	
	@Nonnull
	public static <V> V getTypeCheckingNonnull(Map map, Object key, Class<V> type) throws NotFoundException, StructuredClassCastException, NullPointerException
	{
		if (type.isPrimitive())
			type = Primitives.getWrapperClassFromPrimitiveOrPassThroughWrapper(type);
		
		
		Object v = map.get(key);
		
		if (v == null)
		{
			if (map.containsKey(key))
			{
				throw new NullPointerException();
			}
			else
			{
				throw new NotFoundException(repr(key)+" was not found in "+repr(map));
			}
		}
		else
		{
			if (type.isInstance(v))
			{
				return (V)v;
			}
			else
			{
				throw new StructuredClassCastException(v.getClass().getName()+" encountered, not "+type.getClass().getName(), v.getClass(), type);
			}
		}
	}
	
	
	
	@Nullable
	public static <V> V getTypeCheckingNullableDefaulting(Map map, Object key, Class<V> type, V defaultValue) throws NotFoundException, StructuredClassCastException, NullPointerException
	{
		if (type.isPrimitive())
			type = Primitives.getWrapperClassFromPrimitiveOrPassThroughWrapper(type);
		
		
		Object v = map.get(key);
		
		if (v == null)
		{
			if (map.containsKey(key))
			{
				//Oh, it's legitimately null! XD
				return null;
			}
			else
			{
				return defaultValue;
			}
		}
		else
		{
			if (type.isInstance(v))
			{
				return (V)v;
			}
			else
			{
				throw new StructuredClassCastException(v.getClass().getName()+" encountered, not "+type.getClass().getName(), v.getClass(), type);
			}
		}
	}
	
	
	
	@Nonnull
	public static <V> V getTypeCheckingNonnullDefaulting(Map map, Object key, Class<V> type, V defaultValue) throws NotFoundException, StructuredClassCastException, NullPointerException
	{
		if (type.isPrimitive())
			type = Primitives.getWrapperClassFromPrimitiveOrPassThroughWrapper(type);
		
		
		requireNonNull(defaultValue);
		
		
		Object v = map.get(key);
		
		if (v == null)
		{
			if (map.containsKey(key))
			{
				throw new NullPointerException();
			}
			else
			{
				return defaultValue;
			}
		}
		else
		{
			if (type.isInstance(v))
			{
				return (V)v;
			}
			else
			{
				throw new StructuredClassCastException(v.getClass().getName()+" encountered, not "+type.getName(), v.getClass(), type);
			}
		}
	}
	
	
	
	@Nullable
	public static <V> V getTypeCheckingNullOnlyOnAbsent(Map map, Object key, Class<V> type) throws NotFoundException, StructuredClassCastException, NullPointerException
	{
		if (type.isPrimitive())
			type = Primitives.getWrapperClassFromPrimitiveOrPassThroughWrapper(type);
		
		
		Object v = map.get(key);
		
		if (v == null)
		{
			if (map.containsKey(key))
			{
				throw new NullPointerException();
			}
			else
			{
				return null;
			}
		}
		else
		{
			if (type.isInstance(v))
			{
				return (V)v;
			}
			else
			{
				throw new StructuredClassCastException(v.getClass().getName()+" encountered, not "+type.getClass().getName(), v.getClass(), type);
			}
		}
	}
	
	
	
	
	//	/**
	//	 * Note: changing the underlying map's readability/writability is not supported xP
	//	 */
	//	public static <K, V> CollectionUtilities.SimpleThreadUnsafeCachingMap<K, V> makeSimpleThreadUnsafeCachingMap(final Map<K, V> underlyingMap)
	//	{
	//		if (isFalseAndNotNull(isMapReadable(underlyingMap)))
	//			//doesn't support reading, so not much point in making a cacher is there?! XD
	//			throw new IllegalArgumentException("map is not readable!! XD!");
	//
	//		final boolean writeable = isTrueOrNull(isMapWritable(underlyingMap)); //be on safe side, with null/unknown :>
	//
	//		class cachingmap
	//		extends CollectionUtilities.AlternateAbstractMap<K, V>
	//		implements CollectionUtilities.SimpleThreadUnsafeCachingMap<K, V>, ExposedCache<Map<K, V>>
	//		{
	//			public cachingmap()
	//			{
	//				super(underlyingMap.keySet());
	//			}
	//
	//			public void resetCache()
	//			{
	//				cacheMap.clear(); //make sures this gets cleared fo sho :3   (even if other one throws error ;> )
	//
	//				if (underlyingMap instanceof FlushableCache)
	//					((FlushableCache)underlyingMap).resetCache();
	//			}
	//
	//			@Override
	//			public Map<K, V> getCache()
	//			{
	//				return cacheMap;
	//			}
	//
	//
	//			Map<K, V> cacheMap = new HashMap<K, V>();
	//
	//			@Override
	//			public V get(Object key)
	//			{
	//				V v = cacheMap.get(key);
	//
	//				if (v != null || cacheMap.containsKey(key))
	//					return v;
	//
	//				else
	//				{
	//					v = underlyingMap.get(key);
	//
	//					if (v != null || underlyingMap.containsKey(key))
	//						cacheMap.put((K)key, v); //we're assuming if the underlying map actually worked, that the key is valid (is-a(K) predicate is true :> )
	//
	//					return v;
	//				}
	//			}
	//
	//			@Override
	//			public boolean containsKey(Object key)
	//			{
	//				return cacheMap.containsKey(key) || underlyingMap.containsKey(key); //yay short-circuiting! :D XD
	//			}
	//
	//
	//
	//			@Override
	//			public V put(K key, V value)
	//			{
	//				if (!writeable)
	//					throw new ReadonlyUnsupportedOperationException();
	//
	//				V prev = underlyingMap.put(key, value);
	//
	//				cacheMap.put(key, value); //don't invoke this one unless you're sure it got in the underlying one (if it throws error!) ;>
	//
	//				return prev;
	//			}
	//
	//
	//			@Override
	//			public void clear()
	//			{
	//				if (!writeable)
	//					throw new ReadonlyUnsupportedOperationException();
	//
	//				cacheMap.clear(); //make sures this gets cleared fo sho :3   (even if other one throws error ;> )
	//
	//				underlyingMap.clear();
	//			}
	//
	//
	//
	//
	//			@Override
	//			public boolean isReadableMap()
	//			{
	//				return true; //we already checked this x>
	//			}
	//
	//			@Override
	//			public boolean isWritableMap()
	//			{
	//				return writeable;
	//			}
	//
	//
	//
	//
	//			//TODO others!
	//
	//			//TODO etc..................
	//
	//
	//
	//		}
	//
	//		return new cachingmap();
	//	}
	
	
	public static <K, V> EqualityComparator<K> getBoundKeyEqualityComparator(Map<K, V> map)
	{
		if (map instanceof MapWithBoundKeyEqualityComparator)
			return ((MapWithBoundKeyEqualityComparator)map).getKeyEqualityComparator();
		else
			return getDefaultEqualityComparator();
	}
	
	
	public static <K, V> EqualityComparator<V> getBoundValueEqualityComparator(Map<K, V> map)
	{
		if (map instanceof MapWithBoundValueEqualityComparator)
			return ((MapWithBoundValueEqualityComparator)map).getValueEqualityComparator();
		else
			return getDefaultEqualityComparator();
	}
	
	
	@LiveValue
	public static Map unmodifiableMap(Map map)
	{
		if (isFalseAndNotNull(isMapWritable(map)))
			//THEN DON'T DECORATE IT \o/  :D!
			return map;
		
		else
		{
			//Else decorate it! :D
			
			return Collections.unmodifiableMap(map);
		}
	}
	
	
	@Nullable
	public static Boolean isMapReadable(@Nonnull Map map)
	{
		if (map == null)
			throw new NullPointerException();
		
		//		else if (collectionThing instanceof StaticallyReadableCollection)
		//			return true;
		//		else if (collectionThing instanceof StaticallyUnreadableCollection)
		//			return false;
		else if (map instanceof RuntimeReadabilityMap)
			return ((RuntimeReadabilityMap)map).isReadableMap();
		else
		{
			if (map.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableMap)
				return true;
			
			else if (map.getClass() == HashMap.class)
				return true;
			else if (map.getClass() == TreeMap.class)
				return true;
			else if (map.getClass() == IdentityHashMap.class)
				return true;
			else if (map.getClass() == WeakHashMap.class)
				return true;
			else if (map.getClass() == EnumMap.class)
				return true;
			
			//TODO moreeeee grandfatheringggggg! :>
			
			
			//Check the views! ^_~
			if (isTrueAndNotNull(PolymorphicCollectionUtilities.isReadableCollection(map.entrySet())))
				return true;
			if (isTrueAndNotNull(PolymorphicCollectionUtilities.isReadableCollection(map.keySet())))
				return true;
			if (isTrueAndNotNull(PolymorphicCollectionUtilities.isReadableCollection(map.values())))
				return true;
			
			
			return null;
		}
	}
	
	
	@Nullable
	public static Boolean isMapWritable(@Nonnull Map map)
	{
		if (map == null)
			throw new NullPointerException();
		
		//		else if (collectionThing instanceof StaticallyReadableCollection)
		//			return true;
		//		else if (collectionThing instanceof StaticallyUnreadableCollection)
		//			return false;
		else if (map instanceof RuntimeWriteabilityMap)
			return ((RuntimeWriteabilityMap)map).isWritableMap();
		else
		{
			if (map.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableMap)
				return true;
			
			else if (map.getClass() == HashMap.class)
				return true;
			else if (map.getClass() == TreeMap.class)
				return true;
			else if (map.getClass() == IdentityHashMap.class)
				return true;
			else if (map.getClass() == WeakHashMap.class)
				return true;
			else if (map.getClass() == EnumMap.class)
				return true;
			
			//TODO moreeeee grandfatheringggggg! :>
			
			
			//Check the views! ^_~
			if (isTrueAndNotNull(PolymorphicCollectionUtilities.isReadableCollection(map.entrySet())))
				return true;
			if (isTrueAndNotNull(PolymorphicCollectionUtilities.isReadableCollection(map.keySet())))
				return true;
			if (isTrueAndNotNull(PolymorphicCollectionUtilities.isReadableCollection(map.values())))
				return true;
			
			
			return null;
		}
	}
	
	
	public static <M extends Map<?, ?>> M keysnotnull(M map) throws NullPointerException
	{
		allnotnull(map.keySet());
		return map;
	}
	
	
	public static <M extends Map<?, ?>> M valuesnotnull(M map) throws NullPointerException
	{
		allnotnull(map.values());
		return map;
	}
	
	
	/**
	 * Note: this should *always* be true, as per the {@link Map} API specification!
	 * :P!
	 */
	public static <M extends Map<?, ?>> M entriesnotnull(M map) throws NullPointerException
	{
		allnotnull(map.entrySet());
		return map;
	}
	
	
	public static <M extends Map<?, ?>> M allnotnull(M map) throws NullPointerException
	{
		keysnotnull(map);
		valuesnotnull(map);
		return map;
	}
	
	
	@Nonnull
	public static <E> List<E> emptyListIfNull(@Nullable List<E> o)
	{
		return o == null ? emptyList() : o;
	}
	
	@Nonnull
	public static <E> Set<E> emptySetIfNull(@Nullable Set<E> o)
	{
		return o == null ? emptySet() : o;
	}
	
	@Nonnull
	public static <E> Collection<E> emptyCollectionIfNull(@Nullable Collection<E> o)
	{
		return o == null ? emptyCollection() : o;
	}
	
	@Nonnull
	public static <E> Iterable<E> emptyIterableIfNull(@Nullable Iterable<E> o)
	{
		return o == null ? emptyIterable() : o;
	}
	
	
	@Nonnull
	public static <K, V> Map<K, V> emptyMapIfNull(@Nullable Map<K, V> o)
	{
		return o == null ? emptyMap() : o;
	}
	
	
	
	
	
	public static <E> Collection<E> emptyCollection()
	{
		return emptySet();  //why not? X3
	}
	
	public static <E> Set<E> singletonSet(E e)
	{
		return singleton(e);
	}
	
	
	
	
	
	
	public static <K, V> void defaultPutAll(Map<K, V> self, Map<? extends K, ? extends V> m)
	{
		for (Entry<? extends K, ? extends V> e : m.entrySet())
		{
			self.put(e.getKey(), e.getValue());
		}
	}
	
	public static <K, V> void defaultReplaceAll(Map<K, V> self, BiFunction<? super K, ? super V, ? extends V> function)
	{
		//Copied from the JRE spec default impl!
		//Wouldn't it be nice if the default impl of all default functions could be accessed statically? X>
		
		requireNonNull(function);
		
		for (Entry<K, V> entry : self.entrySet())
		{
			K k;
			V v;
			
			try
			{
				k = entry.getKey();
				v = entry.getValue();
			}
			catch (IllegalStateException ise)
			{
				// this usually means the entry is no longer in the map.
				throw new ConcurrentModificationException(ise);
			}
			
			// ise thrown from function is not a cme.
			v = function.apply(k, v);
			
			try
			{
				entry.setValue(v);
			}
			catch (IllegalStateException ise)
			{
				// this usually means the entry is no longer in the map.
				throw new ConcurrentModificationException(ise);
			}
		}
	}
	
	public static <E> void defaultReplaceAll(List<E> self, UnaryOperator<E> operator)
	{
		//Copied from the JRE spec default impl!
		//Wouldn't it be nice if the default impl of all default functions could be accessed statically? X>
		
		requireNonNull(operator);
		
		ListIterator<E> li = self.listIterator();
		
		while (li.hasNext())
		{
			li.set(operator.apply(li.next()));
		}
	}
	
	
	
	public static <K, V> int defaultClear(Map<K, V> self)
	{
		Set<Entry<K, V>> s = self.entrySet();
		
		int sizeBeforeClearing = s.size();
		
		for (Entry<? extends K, ? extends V> e : s)
		{
			self.remove(e.getKey());
		}
		
		return sizeBeforeClearing;
	}
	
	
	public static <K, V> void removeAllEntriesForValue(Map<K, V> map, Predicate<V> valueTest)
	{
		for (Object e : map.entrySet().toArray())  //snapshot the entry set in case the map impl. has problems with simultaneous/"concurrent" modification!!
		{
			Entry<K, V> entry = (Entry<K, V>)e;
			
			if (valueTest.test(entry.getValue()))
			{
				map.remove(entry.getKey());
			}
		}
	}
	
	
	
	
	
	public static <E> Predicate<E> createContainsTestWrapper(final E[] array)
	{
		//Todo if the array is above a threshold, automatically use a faster implementation.
		//Todo		how to determine the impl.?  (EnumMap, HashMap, TreeMap, ...)
		return (E o) ->
		{
			if (o == null)
			{
				for (E e : array)
					if (e == null)
						return true;
				return false;
			}
			else
			{
				for (E e : array)
					if (o.equals(e))
						return true;
				return false;
			}
		};
	}
	
	
	/**
	 * Performance is probably only best when using some kind of Set implementation (eg, based on a log(N) map).
	 */
	public static <E> Predicate<E> createContainsTestWrapper(final Collection<E> set)
	{
		return (E o) -> set.contains(o);
	}
	
	
	
	
	
	
	
	@ThrowAwayValue
	public static <E> List<E> concatenateListsOP(@ReadonlyValue Iterable<E> a, @ReadonlyValue Iterable<E> b)
	{
		if (a instanceof PrimitiveCollection || b instanceof PrimitiveCollection)
		{
			List<E> rv = PrimitiveCollections.concatenateManyPrimitiveListsOP(listof(a, b));
			if (rv != null)
				return rv;
			//else: continue :3
		}
		
		
		List<E> l = a instanceof Collection && b instanceof Collection ? new ArrayList<>(((Collection)a).size() + ((Collection)a).size()) : new ArrayList<>();
		
		addAll(l, a);
		addAll(l, b);
		
		return l;
	}
	
	
	/**
	 * If one of these is a single-element list, this is guaranteed to do the singly-linked list cons()ing thing :3
	 * 		(see {@link SinglyPreLinkedReadonlyList} and {@link SinglyPostLinkedReadonlyList} for an example implementation :3 )
	 */
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static <E> List<E> concatenateListsOPC(@ReadonlyValue Iterable<E> a, @ReadonlyValue Iterable<E> b)
	{
		if (a instanceof Collection && b instanceof Collection)
		{
			Collection aa = (Collection) a;
			Collection bb = (Collection) b;
			
			int aSize = aa.size();
			int bSize = bb.size();
			
			if (aSize == 0)
			{
				if (bSize == 0)
				{
					return emptyList();
				}
				else
				{
					return asList(b);
				}
			}
			else if (bSize == 0)
			{
				return asList(a);
			}
			else
			{
				if (aSize == 1)
				{
					//this means we prefer SinglyPreLinkedReadonlyList over SinglyPostLinkedReadonlyList if they're both 1 element long..but it's an arbitrary preference X3
					
					if (b instanceof List)
						return new SinglyPreLinkedReadonlyList<E>(getSingleElement(a), (List)b);
					//else we'd need to recreate a list for b anyway, so we might as well just include a in it XD'
				}
				else if (bSize == 1)
				{
					if (a instanceof List)
						return new SinglyPostLinkedReadonlyList<E>((List)a, getSingleElement(b));
					//else we'd need to recreate a list for b anyway, so we might as well just include a in it XD'
				}
			}
		}
		
		return concatenateListsOP(a, b);
	}
	
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static <E> List<E> concatenateManyListsOPC_V(@ReadonlyValue Iterable<E>... lists)
	{
		return concatenateManyListsOPC(asList(lists));
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static <E> List<E> concatenateManyListsOPC(@ReadonlyValue Collection<? extends Iterable<E>> lists)
	{
		int n = lists.size();
		
		if (n == 0)
		{
			return emptyList();
		}
		else if (n == 1)
		{
			return toList(getSingleElement(lists));
		}
		else if (n == 2)
		{
			Iterator<? extends Iterable<E>> i = lists.iterator();
			
			if (!i.hasNext()) throw new ImpossibleException("Collection iterator mismatched with collection size!");
			Iterable<E> a = i.next();
			if (!i.hasNext()) throw new ImpossibleException("Collection iterator mismatched with collection size!");
			Iterable<E> b = i.next();
			if (i.hasNext()) throw new ImpossibleException("Collection iterator mismatched with collection size!");
			
			return concatenateListsOPC(a, b);
		}
		else
		{
			if (forAny(l -> l instanceof PrimitiveCollection, lists))
			{
				List<E> rv = PrimitiveCollections.concatenateManyPrimitiveListsOP(lists);
				if (rv != null)
					return rv;
				//else: continue :3
			}
			
			
			boolean hasInfo = true;
			int totalSize = 0;
			boolean hasMultipleNonEmpties = false;
			Iterable<E> nonEmpty = null;
			{
				for (Iterable<E> input : lists)
				{
					if (input instanceof Collection)
					{
						Collection in = (Collection) input;
						
						int size = in.size();
						
						if (size == 0)
						{
							//No worries :3
						}
						else
						{
							totalSize += size;
							
							if (nonEmpty == null)
							{
								nonEmpty = input;
							}
							else
							{
								if (!hasMultipleNonEmpties)
									hasMultipleNonEmpties = true;
							}
						}
					}
					else
					{
						hasInfo = false;
						break;
					}
				}
			}
			
			
			if (hasInfo)
			{
				if (nonEmpty == null)
				{
					asrt(totalSize == 0);
					
					return emptyList();
				}
				else
				{
					if (!hasMultipleNonEmpties)
					{
						return toList(nonEmpty);
					}
				}
			}
			
			
			List<E> l = hasInfo ? new ArrayList<>(totalSize) : new ArrayList<>();
			
			for (Iterable<E> in : lists)
				addAll(l, in);
			
			return l;
		}
	}
	
	
	@SnapshotValue
	@ReadonlyValue
	public static <I, O, Ol extends Iterable<O>> List<O> concatenateManyListsMappingOP(Mapper<I, Ol> mapper, @ReadonlyValue Collection<I> inputs)
	{
		int n = inputs.size();
		
		if (n == 0)
		{
			return emptyList();
		}
		else if (n == 1)
		{
			Ol o;
			
			try
			{
				o = mapper.f(getSingleElement(inputs));
			}
			catch (FilterAwayReturnPath exc)
			{
				return emptyList();
			}
			
			return asList(o);
		}
		else
		{
			List<O> l = new ArrayList<>(inputs.size());
			
			for (I i : inputs)
			{
				Ol o;
				
				try
				{
					o = mapper.f(i);
				}
				catch (FilterAwayReturnPath exc)
				{
					continue;
				}
				
				addAll(l, o);
			}
			
			return l;
		}
	}
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> toList(Iterable<E> i)
	{
		if (i instanceof List)
			return (List<E>)i;
		else
			return toNewList(i);
	}
	
	@SnapshotValue
	public static <E> List<E> toNewList(Iterable<E> i)
	{
		if (i instanceof Collection)
			return new ArrayList<>((Collection<E>)i);
		else
		{
			List<E> l = new ArrayList<>();
			for (E e : i)
				l.add(e);
			return l;
		}
	}
	
	
	
	
	public static <E> Iterable<E> concatenateIterablesOP(@ReadonlyValue Iterable<E>... iterables)
	{
		return new ConcatenatingIterable<E>(asList(iterables));
	}
	
	public static <E> Iterator<E> concatenateIteratorsOP(@ReadonlyValue Iterator<E>... iterators)
	{
		return new ConcatenatingIterator<E>(asList(iterators).iterator());
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Iterable<E> concatenateIterablesOPC(@ReadonlyValue Iterable<E>... iterables)
	{
		if (iterables.length == 0)
			return emptyIterable();
		else if (iterables.length == 1)
			return iterables[0];
		else
			return new ConcatenatingIterable<E>(asList(iterables));
	}
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Iterator<E> concatenateIteratorsOPC(@ReadonlyValue Iterator<E>... iterators)
	{
		if (iterators.length == 0)
			return emptyIterator();
		else if (iterators.length == 1)
			return iterators[0];
		else
			return new ConcatenatingIterator<E>(asList(iterators).iterator());
	}
	
	
	
	
	
	
	
	public static <E> SimpleIterator<E> concatenateIteratorsOPC(SimpleIterator<E> first, SimpleIterator<E> second)
	{
		requireNonNull(first);
		requireNonNull(second);
		
		if (second == first)
			throw new IllegalArgumentException();
		
		
		return new SimpleIterator<E>()
		{
			SimpleIterator<E> current = first;
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				while (true)
				{
					try
					{
						return current.nextrp();
					}
					catch (StopIterationReturnPath rp)
					{
						if (current == second)
							throw rp;
						else
							current = second;
					}
				}
			}
		};
	}
	
	public static <E> SimpleIterator<E> concatenateManyIteratorsOPC(Iterable<SimpleIterator<E>> iterators)
	{
		return concatenateManyIteratorsOPC(simpleIterator(iterators));
	}
	
	public static <E> SimpleIterator<E> concatenateManyIteratorsOPC(SimpleIterator<SimpleIterator<E>> iterators)
	{
		return new SimpleIterator<E>()
		{
			SimpleIterator<E> current = null;
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				while (true)
				{
					if (current == null)
						current = requireNonNull(iterators.nextrp());  //propagate StopIterationReturnPath :3
					
					try
					{
						return current.nextrp();
					}
					catch (StopIterationReturnPath exc)
					{
						current = null;
					}
				}
			}
		};
	}
	
	
	public static <I, O> SimpleIterator<O> mapConcatenating(Mapper<I, SimpleIterator<O>> mapper, Iterable<I> underlying)
	{
		return mapConcatenating(mapper, simpleIterator(underlying));
	}
	
	public static <I, O> SimpleIterator<O> mapConcatenating(Mapper<I, SimpleIterator<O>> mapper, SimpleIterator<I> underlying)
	{
		return new SimpleIterator<O>()
		{
			SimpleIterator<O> current = null;
			
			@Override
			public O nextrp() throws StopIterationReturnPath
			{
				while (true)
				{
					if (current == null)
					{
						while (true)
						{
							try
							{
								current = requireNonNull(mapper.f(underlying.nextrp()));  //propagate StopIterationReturnPath :3
								break;
							}
							catch (FilterAwayReturnPath exc)
							{
							}
						}
					}
					
					try
					{
						return current.nextrp();
					}
					catch (StopIterationReturnPath exc)
					{
						current = null;
					}
				}
			}
		};
	}
	
	
	public static <E> SimpleIterator<E> concatenateGeneratorsOverIntegerInterval(int start, int count, UnaryFunctionIntToObject<SimpleIterator<E>> generatorProducer)
	{
		return mapConcatenating(generatorProducer::f, intervalIntegersList(start, count));
	}
	
	
	public static <E> SimpleIterator<E> concatenateGeneratorsOverAllIntegers(int start, UnaryFunctionIntToObject<SimpleIterator<E>> generatorProducer)
	{
		return mapConcatenating(i -> generatorProducer.f(i + start), AllNonnegativeIntegers);
	}
	
	
	
	
	
	public static final SimpleIterable<Integer> AllNonnegativeIntegers = () -> new SimpleIterator<Integer>()
	{
		int v = 0;
		boolean eof = false;
		
		@Override
		public Integer nextrp() throws StopIterationReturnPath
		{
			if (eof)
				throw new OverflowException();  //not StopIterationReturnPath because this is conceptually supposed to be ALL integers XD
			
			int v = this.v;
			
			if (v == Integer.MAX_VALUE)
				eof = true;
			else
				this.v = v+1;
			
			return v;
		}
	};
	
	public static final SimpleIterable<Integer> AllIntegers = () -> new SimpleIterator<Integer>()
	{
		int v = 0;
		boolean eof = false;
		
		@Override
		public Integer nextrp() throws StopIterationReturnPath
		{
			if (eof)
				throw new OverflowException();  //not StopIterationReturnPath because this is conceptually supposed to be ALL integers XD
			
			int v = this.v;
			
			if (v == AllIntegersSequenceEnd)
				eof = true;
			else
			{
				this.v = allIntegersSequenceNext(v);
			}
			
			return v;
		}
	};
	
	
	
	
	
	
	/**
	 * Example:  base=[1,2,3], extras=[a,b,c], outputs will be:
	 * 		[1,2,3]
	 * 		[1,2,3,a]
	 * 		[1,2,3,a,b]
	 * 		[1,2,3,a,b,c]
	 */
	public static <E> SimpleIterator<List<E>> ascendingListAppensionGenerator(List<E> base, List<E> extras)
	{
		if (extras.isEmpty())
		{
			return singletonSimpleIterator(base);
		}
		else
		{
			return ascendingListAppensionGenerator(base, simpleIterator(extras));
		}
	}
	
	
	public static <E> SimpleIterator<List<E>> ascendingListAppensionGenerator(List<E> base, SimpleIterator<E> extras)
	{
		requireNonNull(base);
		requireNonNull(extras);
		
		return new SimpleIterator<List<E>>()
		{
			List<E> current = base;
			
			@Override
			public List<E> nextrp() throws StopIterationReturnPath
			{
				if (current == null)  //eof
					throw StopIterationReturnPath.I;
				
				List<E> c = current;
				
				try
				{
					current = new SinglyPostLinkedReadonlyList<E>(c, extras.nextrp());
				}
				catch (StopIterationReturnPath exc)
				{
					current = null;
				}
				
				return c;
			}
		};
	}
	
	
	
	
	
	
	
	/**
	 * Example:  base=[1,2,3], extras=[a,b,c], outputs will be:
	 * 		[1,2,3]
	 * 		[a,1,2,3]
	 * 		[b,a,1,2,3]
	 * 		[c,b,a,1,2,3]
	 */
	public static <E> SimpleIterator<List<E>> ascendingListPrepensionGenerator(List<E> base, List<E> extras)
	{
		if (extras.isEmpty())
		{
			return singletonSimpleIterator(base);
		}
		else
		{
			return ascendingListPrepensionGenerator(base, simpleIterator(extras));
		}
	}
	
	
	public static <E> SimpleIterator<List<E>> ascendingListPrepensionGenerator(List<E> base, SimpleIterator<E> extras)
	{
		requireNonNull(base);
		requireNonNull(extras);
		
		return new SimpleIterator<List<E>>()
		{
			List<E> current = base;
			
			@Override
			public List<E> nextrp() throws StopIterationReturnPath
			{
				if (current == null)  //eof
					throw StopIterationReturnPath.I;
				
				List<E> c = current;
				
				try
				{
					current = new SinglyPreLinkedReadonlyList<E>(extras.nextrp(), c);
				}
				catch (StopIterationReturnPath exc)
				{
					current = null;
				}
				
				return c;
			}
		};
	}
	
	
	
	
	//TODO rename these from Generator to Iterator since that means something else now XD''
	//	(..unless of course we decide to rename Generator to Explorer or something..since it is bidirectional and all XD'''   ..idk ^^'')
	
	public static <E> SimpleIterator<E> repeatingGenerator(E value, int number)
	{
		return new SimpleIterator<E>()
		{
			int i = 0;
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				if (i == number)
					throw StopIterationReturnPath.I;
				
				i++;
				
				return value;
			}
		};
	}
	
	
	/**
	 * + This is "lenient" in the way that {@link #asSubListBySizeLenient(SimpleIterator, int, int)} is lenient—if there aren't enough values in the underlying stream, then it just stops there.  Ie, the number of elements in this stream is <code>{@link SmallIntegerMathUtilities#least(int, int) least}(number, theNumberOfElementsInThisStream)</code>  :3
	 */
	public static <E> SimpleIterator<E> truncatingGenerator(SimpleIterator<E> underlying, int number)
	{
		return new SimpleIterator<E>()
		{
			int i = 0;
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				if (i == number)
					throw StopIterationReturnPath.I;
				
				i++;
				
				return underlying.nextrp();  //Propagate StopIterationReturnPath  :3
			}
		};
	}
	
	
	/**
	 * + This is "strict" in the way that {@link #asSubListBySizeLenient(SimpleIterator, int, int)} is lenient—if there aren't enough values in the underlying stream, then {@link NoSuchElementException} is thrown by {@link SimpleIterator#nextrp()}!
	 */
	public static <E> SimpleIterator<E> truncatingGeneratorStrict(SimpleIterator<E> underlying, int number)
	{
		return new SimpleIterator<E>()
		{
			int i = 0;
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				if (i == number)
					throw StopIterationReturnPath.I;
				
				i++;
				
				try
				{
					return underlying.nextrp();
				}
				catch (StopIterationReturnPath rp)
				{
					throw new NoSuchElementException("Only "+(i-1)+" elements were in the underlying stream, we expected "+number+"!");
				}
			}
		};
	}
	
	
	
	
	
	
	/**
	 * If they aren't the same length, the output stops at whichever is shorter.
	 */
	public static <E> SimpleIterator<PairOrdered<E, E>> unmatchingPairs(SimpleIterator<E> a, SimpleIterator<E> b, EqualityComparator<? super E> equals)
	{
		return new SimpleIterator<PairOrdered<E, E>>()
		{
			@Override
			public PairOrdered<E, E> nextrp() throws StopIterationReturnPath
			{
				while (true)
				{
					E ae = a.nextrp();  //propagate StopIterationReturnPath :3
					E be = b.nextrp();  //propagate StopIterationReturnPath :3
					
					if (!equals.equals(ae, be))
						return pair(ae, be);
				}
			}
		};
	}
	
	public static <E> SimpleIterator<PairOrdered<E, E>> unmatchingPairs(SimpleIterator<E> a, SimpleIterator<E> b)
	{
		return unmatchingPairs(a, b, getDefaultEqualityComparator());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static enum CommutativeMarbleBuckets
	{
		Commutative,
		Noncommutative,
	}
	
	
	/**
	 * Since the buckets can't hold zero in them, but must start at 1, the maximum number of buckets is always the total number of marbles :3
	 * Ie, the shortest sequence is always [n] and the longest always [1,1,1,1,...] with length n  :3
	 */
	public static SimpleIterator<List<Integer>> marbleBucketsSimple(int totalMarbles, @Nonnull CommutativeMarbleBuckets commutative)
	{
		return marbleBucketsRanges(totalMarbles, 1, null, 0, null, commutative);
	}
	
	
	
	
	
	public static SimpleIterator<List<Integer>> marbleBucketsRangesForMaxMarbles(@Nullable Integer maxTotalMarbles, int minimumInBuckets, @Nullable Integer maximumInBucketsInclusive, int minimumNumberOfBuckets, @Nullable Integer maximumNumberOfBucketsInclusive, @Nonnull CommutativeMarbleBuckets commutative)
	{
		if (maxTotalMarbles == null)
		{
			if (maximumInBucketsInclusive == null)
				throw new IllegalArgumentException("That would be an infinite amount of output!");
			
			if (maximumNumberOfBucketsInclusive == null)
				throw new IllegalArgumentException("That would be an infinite amount of output!");
			
			maxTotalMarbles = maximumInBucketsInclusive * maximumNumberOfBucketsInclusive;
		}
		
		return mapConcatenating(n -> marbleBucketsRanges(n, minimumInBuckets, maximumInBucketsInclusive, minimumNumberOfBuckets, maximumNumberOfBucketsInclusive, commutative), intervalIntegersList(0, maxTotalMarbles+1));
	}
	
	
	public static SimpleIterator<List<Integer>> marbleBucketsRanges(int totalMarbles, int minimumInBuckets, @Nullable Integer maximumInBucketsInclusive, int minimumNumberOfBuckets, @Nullable Integer maximumNumberOfBucketsInclusive, @Nonnull CommutativeMarbleBuckets commutative)
	{
		if (minimumInBuckets == 0)
		{
			if (maximumNumberOfBucketsInclusive == null)
				throw new IllegalArgumentException("That would cause immediate infinite recursion!");
			else
				return _marbleBucketsMaximumBucketValueMinimumMaximumNumberOfBuckets(totalMarbles, maximumInBucketsInclusive, minimumNumberOfBuckets, maximumNumberOfBucketsInclusive, commutative);  //needs special stuff!
		}
		
		
		if (minimumInBuckets < 0)
			throw new IllegalArgumentException();
		
		if (maximumInBucketsInclusive != null && maximumInBucketsInclusive < minimumInBuckets)
			throw new IllegalArgumentException();
		
		
		if (minimumNumberOfBuckets < 0)
			throw new IllegalArgumentException();
		
		if (maximumNumberOfBucketsInclusive != null && maximumNumberOfBucketsInclusive < minimumNumberOfBuckets)
			throw new IllegalArgumentException();
		
		
		
		
		return marbleBucketsMinimumMaximumBucketValueGeneralListPredicate(totalMarbles, minimumInBuckets, maximumInBucketsInclusive,
		
		l -> true,
		
		l ->
		{
			int n = l.size();
			return n >= minimumNumberOfBuckets && (maximumNumberOfBucketsInclusive == null || n <= maximumNumberOfBucketsInclusive);
		},
		
		commutative
		);
	}
	
	
	
	/**
	 * minimumInBuckets is zero
	 * maximumNumberOfBucketsInclusive is never null
	 */
	protected static SimpleIterator<List<Integer>> _marbleBucketsMaximumBucketValueMinimumMaximumNumberOfBuckets(int totalMarbles, @Nullable Integer maximumInBucketsInclusive, int minimumNumberOfBuckets, int maximumNumberOfBucketsInclusive, @Nonnull CommutativeMarbleBuckets commutative)
	{
		requireNonNull(commutative);
		
		if (maximumInBucketsInclusive != null && maximumInBucketsInclusive < 0)
			throw new IllegalArgumentException();
		
		if (minimumNumberOfBuckets < 0)
			throw new IllegalArgumentException();
		
		if (maximumNumberOfBucketsInclusive < minimumNumberOfBuckets)
			throw new IllegalArgumentException();
		
		
		//So this can't create trailing zeros, so we just need to add them explicitly X3
		
		int minimumInBucketsOptimized = (commutative == CommutativeMarbleBuckets.Commutative && totalMarbles > 0) ? 1 : 0;  //making this be 1 is an optimization because if the first bucket is 0 then all buckets have to be zero, but if there's > 0 marbles, then the first bucket can't ever be zero of course! and thus no bucket can be zero!, but it doesn't work if there are zero marbles or we'll see [] not [[0]] !
		SimpleIterator<List<Integer>> i = marbleBucketsMinimumMaximumBucketValueGeneralListPredicate(totalMarbles, minimumInBucketsOptimized, maximumInBucketsInclusive,
		
		//This prefilter is necessary for when minimumInBuckets == 0 to avoid infinite recursion!
		l ->
		{
			int n = l.size();
			return n <= maximumNumberOfBucketsInclusive;
		},
		
		l ->
		{
			int n = l.size();
			//return n >= minimumNumberOfBuckets && n <= maximumNumberOfBucketsInclusive;
			return n <= maximumNumberOfBucketsInclusive;  //don't impose a minimum because we can always pad it with zeros on the end to reach the minimum!
		},
		
		commutative
		);
		
		return mapConcatenating(l ->
		{
			int n = l.size();
			
			int remainingForMin = greatest(minimumNumberOfBuckets - n, 0);
			int remainingBucketsForMax = maximumNumberOfBucketsInclusive - n;
			
			asrt(remainingBucketsForMax >= 0);
			
			if (remainingBucketsForMax == 0)
				return singletonSimpleIterator(remainingForMin == 0 ? l : concatenateListsOPC(l, repeatedList(0, remainingForMin)));
			else if (remainingForMin == remainingBucketsForMax)
				return singletonSimpleIterator(concatenateListsOPC(l, repeatedList(0, remainingForMin)));
			else if (remainingForMin == 0)
				return ascendingListAppensionGenerator(l, repeatingGenerator(0, remainingBucketsForMax));
			else
				return ascendingListAppensionGenerator(concatenateListsOPC(l, repeatedList(0, remainingForMin)), repeatingGenerator(0, remainingBucketsForMax-remainingForMin));
			
		}, i);
	}
	
	
	
	
	
	/**
	 * ===Commutative version===
	 * Like non-commutative marble-buckets, but the order doesn't matter and the produced lists are always sorted high to low.
	 * It will produce all possible duplicates within a list though, like [3, 2, 2, 1] but never [3, 2, 1, 2].
	 * 
	 * So like, it's the same output as non-commutative marble-buckets, but the lists are sorted and duplicate lists elided.
	 * ===/===
	 * 
	 * @param minimumInBuckets  if this is 0 and builtInFilteringPredicate doesn't limit the maximum number of buckets somehow, then it's possible to immediately fall into infinite recursion with this trying to make an infinite integer list with infinite zero's (and some finite non-zeros XD) to output!
	 */
	public static SimpleIterator<List<Integer>> marbleBucketsMinimumMaximumBucketValueGeneralListPredicate(int totalMarbles, int minimumInBuckets, @Nullable Integer maximumInBucketsInclusive, @Nonnull Predicate<List<Integer>> preFilteringPredicateOnPartialLists, @Nonnull Predicate<List<Integer>> builtInFilteringPredicate, @Nonnull CommutativeMarbleBuckets commutative)
	{
		requireNonNull(commutative);
		
		if (totalMarbles < 0)
			throw new IllegalArgumentException();
		
		if (minimumInBuckets < 0)
			throw new IllegalArgumentException();
		
		if (totalMarbles == 0)
		{
			if (builtInFilteringPredicate.test(emptyList()) && minimumInBuckets == 0)
				return singletonSimpleIterator(emptyList());
			else
				return emptySimpleIterator();
		}
		else
		{
			return combineGeneratorsGeneral(l ->
			{
				if (!preFilteringPredicateOnPartialLists.test(l))
					return emptySimpleIterator();
				
				int remaining = totalMarbles - sumS32(l);  //remaining = inclusiveEnd
				
				if (remaining == 0)
				{
					return builtInFilteringPredicate.test(l) ? null : emptySimpleIterator();
				}
				else if (remaining < minimumInBuckets)
				{
					return emptySimpleIterator();
				}
				else
				{
					int maxIntrinsic = (commutative == CommutativeMarbleBuckets.Noncommutative || l.isEmpty()) ? remaining : least(remaining, (int)l.get(l.size()-1));
					
					int max = maximumInBucketsInclusive == null ? maxIntrinsic : least(maxIntrinsic, (int)maximumInBucketsInclusive);
					
					return max < minimumInBuckets ? emptySimpleIterator() : simpleIterator(intervalIntegersListByInclusiveEnd(minimumInBuckets, max));
				}
			});
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This converts the output of noncommutative marble-buckets into the equivalent output from commutative marble-buckets by removing duplicates and resorting everything! :D
	 */
	public static List<List<Integer>> produceCommutativeMarbleBucketsFromNonCommutativeOutput(List<List<Integer>> noncommutative)
	{
		//defaultListsCompare(a, b, getDefaultOrderingComparator(), lengthsFirst, invertElementComparison, shorterIsLarger);
		
		List<List<Integer>> l = mapToList(o -> reversed(sorted(o)), noncommutative);  //reversed(sorted(x)) is the order that the commutative generator natively puts out! :3
		l = sorted(l, (a, b) -> defaultListsCompare(a, b, getDefaultOrderingComparator(), false, false, false));  //this is the order that the commutative generator natively puts out! :3
		return uniqueifiedPresortedToListOPC(l, (List<Integer> a, List<Integer> b) -> eqv(a, b));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Produces all sequences of the given length, of the given digits.
	 * + This is big-endian
	 * 
	 * For integers, this is just the place value system! XD :D
	 */
	public static <E> SimpleIterator<List<E>> combineGeneratorsHomogeneous(int length, SimpleIterable<E> digits)
	{
		return combineGeneratorsGeneral(soFar -> soFar.size() >= length ? null : digits.simpleIterator());
	}
	
	/**
	 * Like {@link #combineGeneratorsHomogeneous(int, SimpleIterable)} but allows the equivalent of variable-base :3
	 * Ie, the "ones" place can be [0,1,2,3] and the next place be [0,1,2,3] and the next place be [0,1,2,3] like normal.
	 * But unlike the homogeneous version, the next place could be [0,1,2] and the next [0,1,2,3,4,5] and etc.
	 * 
	 * And of course they don't have to be integers!  They can be anything! :D
	 */
	public static <E> SimpleIterator<List<E>> combineGeneratorsAcontextual(List<SimpleIterator<E>> digits)
	{
		return combineGeneratorsAcontextual(digits.size(), digits::get);
	}
	public static <E> SimpleIterator<List<E>> combineGeneratorsAcontextual(int size, UnaryFunctionIntToObject<SimpleIterator<E>> digits)
	{
		return combineGeneratorsGeneral(soFar ->
		{
			int n = soFar.size();
			return n >= size ? null : digits.f(n);
		});
	}
	public static <I, O> SimpleIterator<List<O>> combineGeneratorsAcontextualMapping(UnaryFunction<I, SimpleIterator<O>> mapper, List<I> inputs)
	{
		return combineGeneratorsAcontextual(inputs.size(), i -> mapper.f(inputs.get(i)));
	}
	
	
	
	/**
	 * Like {@link #combineGeneratorsAcontextual(List)} but allows the "base" to vary not only by which digit-place it's in
	 * But on the *actual contents* of the preceding digits! :D
	 * 
	 * So the "ones" place can be [0,1,2,3] and the next place be [0,1,2,3] if there's a 0 in the one's place, but be [0,1,2] if there's a 1 in the one's place!
	 */
	public static <E> SimpleIterator<List<E>> combineGeneratorsGeneral(UnaryFunction<List<E>, SimpleIterator<E>> digitsForPlaceProvidedSequenceUpToThatPlaceOrNullForEnd)
	{
		SimpleIterator<E> firstAlphabet = digitsForPlaceProvidedSequenceUpToThatPlaceOrNullForEnd.f(emptyList());
		
		if (firstAlphabet == null)
			return emptySimpleIterator();
		else
			return _combineGeneratorsOverPlaceValueIncrementProgressionGeneral(firstAlphabet, digitsForPlaceProvidedSequenceUpToThatPlaceOrNullForEnd);
	}
	
	protected static <E> SimpleIterator<List<E>> _combineGeneratorsOverPlaceValueIncrementProgressionGeneral(SimpleIterator<E> firstAlphabet, UnaryFunction<List<E>, SimpleIterator<E>> digitsForPlaceProvidedSequenceUpToThatPlaceOrNullForEnd)
	{
		SimpleIterator<PairOrdered<E, List<E>>> i = combineHighALowBGeneral(firstAlphabet, a ->
		{
			SimpleIterator<E> nextAlphabet = digitsForPlaceProvidedSequenceUpToThatPlaceOrNullForEnd.f(singletonList(a));
			
			if (nextAlphabet == null)
			{
				return simpleIterator(singletonList(emptyList()));
			}
			else
			{
				return _combineGeneratorsOverPlaceValueIncrementProgressionGeneral(nextAlphabet, l -> digitsForPlaceProvidedSequenceUpToThatPlaceOrNullForEnd.f(concatenateListsOPC(singletonList(a), l)));
			}
		});
		
		return map(p -> concatenateListsOPC(singletonList(p.getA()), p.getB()), i);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static <A, B> SimpleIterator<PairOrdered<A, B>> combineHighALowBHomogeneous(SimpleIterator<A> alphabetA, SimpleIterable<B> alphabetB)
	{
		return combineHighALowBGeneral(alphabetA, a -> alphabetB.simpleIterator());
	}
	
	public static <A, B> SimpleIterator<PairOrdered<A, B>> combineLowAHighBHomogeneous(SimpleIterable<A> alphabetA, SimpleIterator<B> alphabetB)
	{
		return combineLowAHighBGeneral(b -> alphabetA.simpleIterator(), alphabetB);
	}
	
	
	
	
	public static <A, B> SimpleIterator<PairOrdered<A, B>> combineHighALowBGeneral(SimpleIterator<A> alphabetA, UnaryFunction<A, SimpleIterator<B>> getAlphabetB)
	{
		return new SimpleIterator<PairOrdered<A,B>>()
		{
			A currentA;
			SimpleIterator<B> currentBs = null;
			
			@Override
			public PairOrdered<A, B> nextrp() throws StopIterationReturnPath
			{
				while (true)
				{
					if (currentBs == null)
					{
						currentA = alphabetA.nextrp();  //propagate StopIterationReturnPath :3
						currentBs = getAlphabetB.f(currentA);
					}
					
					B currentB;
					try
					{
						currentB = currentBs.nextrp();
					}
					catch (StopIterationReturnPath exc)
					{
						currentBs = null;
						continue;
					}
					
					return pair(currentA, currentB);
				}
			}
		};
	}
	
	public static <A, B> SimpleIterator<PairOrdered<A, B>> combineLowAHighBGeneral(UnaryFunction<B, SimpleIterator<A>> getAlphabetA, SimpleIterator<B> alphabetB)
	{
		return new SimpleIterator<PairOrdered<A,B>>()
		{
			SimpleIterator<A> currentAs = null;
			B currentB;
			
			@Override
			public PairOrdered<A, B> nextrp() throws StopIterationReturnPath
			{
				while (true)
				{
					if (currentAs == null)
					{
						currentB = alphabetB.nextrp();  //propagate StopIterationReturnPath :3
						currentAs = getAlphabetA.f(currentB);
					}
					
					A currentA;
					try
					{
						currentA = currentAs.nextrp();
					}
					catch (StopIterationReturnPath exc)
					{
						currentAs = null;
						continue;
					}
					
					return pair(currentA, currentB);
				}
			}
		};
	}
	
	
	
	public static <A, B> SimpleIterator<PairOrdered<A, B>> _combineHighALowBGeneral_alt1(SimpleIterator<A> alphabetA, UnaryFunction<A, SimpleIterator<B>> getAlphabetB)
	{
		return map(p -> swappair(p), combineLowAHighBGeneral(getAlphabetB, alphabetA));
	}
	
	public static <A, B> SimpleIterator<PairOrdered<A, B>> _combineLowAHighBGeneral_alt1(UnaryFunction<B, SimpleIterator<A>> getAlphabetA, SimpleIterator<B> alphabetB)
	{
		return map(p -> swappair(p), combineHighALowBGeneral(alphabetB, getAlphabetA));
	}
	
	
	
	
	
	
	
	public static <E> SimpleIterator<PairOrdered<E, E>> combineHighALowBHomogeneousCommutative(List<E> alphabet)
	{
		SimpleIterator<PairOrdered<Integer, Integer>> i = combineHighALowBGeneral(simpleIterator(intervalIntegersList(0, alphabet.size())), a -> simpleIterator(intervalIntegersList(0, a+1)));
		return map(p -> pair(alphabet.get(p.getA()), alphabet.get(p.getB())), i);
	}
	
	public static <E> SimpleIterator<PairOrdered<E, E>> combineLowAHighBHomogeneousCommutative(List<E> alphabet)
	{
		SimpleIterator<PairOrdered<Integer, Integer>> i = combineLowAHighBGeneral(b -> simpleIterator(intervalIntegersList(0, b+1)), simpleIterator(intervalIntegersList(0, alphabet.size())));
		return map(p -> pair(alphabet.get(p.getA()), alphabet.get(p.getB())), i);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> E getArbitraryElementThrowing(Iterable<E> c) throws NoSuchElementException
	{
		try
		{
			return getArbitraryElementRP(c);
		}
		catch (NoSuchElementReturnPath exc)
		{
			throw exc.toException();
		}
	}
	
	public static <E> E getArbitraryElementDefaulting(Iterable<E> c)
	{
		try
		{
			return getArbitraryElementRP(c);
		}
		catch (NoSuchElementReturnPath exc)
		{
			return null;
		}
	}
	
	
	public static <E> E getArbitraryElementRP(Iterable<E> c) throws NoSuchElementReturnPath
	{
		if (c instanceof List)
		{
			List<E> l = (List<E>) c;
			
			if (l.isEmpty())
				throw NoSuchElementReturnPath.I;
			
			return l.get(0);
		}
		
		if (c instanceof CollectionWithGetArbitraryElement)
			return ((CollectionWithGetArbitraryElement<E>)c).getArbitraryElement();
		
		
		//Fallback :)
		{
			if (c instanceof Collection)
				if (((Collection)c).isEmpty())  //faster than making an iterator just to find it's empty?? *shrugs* \o/
					throw NoSuchElementReturnPath.I;
			
			for (E e : c)
				return e;
			
			throw NoSuchElementReturnPath.I;
		}
	}
	
	//Todo return-path version that throws NoSuchElementReturnPath???
	//Todo 	public static <E> E getLastElement(List<E> c)
	
	
	
	
	
	
	
	public static <E> E popArbitraryElementThrowing(Iterable<E> c) throws NoSuchElementException
	{
		try
		{
			return popArbitraryElementRP(c);
		}
		catch (NoSuchElementReturnPath exc)
		{
			throw exc.toException();
		}
	}
	
	public static <E> E popArbitraryElementDefaulting(Collection<E> c)
	{
		try
		{
			return popArbitraryElementRP(c);
		}
		catch (NoSuchElementReturnPath exc)
		{
			return null;
		}
	}
	
	
	public static <E> E popArbitraryElementRP(Iterable<E> c) throws NoSuchElementReturnPath
	{
		if (c instanceof CollectionWithPopArbitraryElement)
			return ((CollectionWithPopArbitraryElement<E>)c).popArbitraryElement();
		
		if (c instanceof List)
		{
			return ((List<E>)c).remove(0);  //remove-by-index not value; faster and more reliable ^^
		}
		
		//Fallback :)
		Iterator<E> i = c.iterator();
		if (i.hasNext())
		{
			E e = i.next();
			i.remove();
			return e;
		}
		else
		{
			return null;
		}
		
		//if (!c.remove(e))
		//	throw new ImpossibleException("java.util.Collections requirement not met!  Couldn't remove an element the collection returned as being a member of it!  D:    (collection type = "+c.getClass().getName()+")");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Todo return-path version that throws NoSuchElementReturnPath???
	public static <E> E getArbitraryMatchingElement(Predicate<? super E> predicate, Collection<E> c)
	{
		if (c == null)
			return null;
		
		for (E e : c)
			if (predicate.test(e))
				return e;
		
		return null;
	}
	
	
	//Todo return-path version that throws NoSuchElementReturnPath???
	public static <E> E popArbitraryMatchingElement(Predicate<? super E> predicate, Collection<E> c)
	{
		if (c == null)
			return null;
		
		Iterator<E> i = c.iterator();
		while (i.hasNext())
		{
			E e = i.next();
			if (predicate.test(e))
			{
				i.remove();
				return e;
			}
		}
		
		return null;
	}
	
	
	
	//Todo getFirstMatchingElement  (which is usually the same, but to be explicits :3 )
	//Todo popFirstMatchingElement  (which is usually the same, but to be explicits :3 )
	
	
	
	
	
	
	//< Todo de-duplicate with above X'DD
	
	public static <E> E getAndRemoveIfPresentOrReturnDefaultValue(Iterable<E> thingWithIteratorsSupportingRemoving, Predicate<E> predicate, @Nullable E defaultValue)
	{
		Iterator<E> i = thingWithIteratorsSupportingRemoving.iterator();
		
		while (i.hasNext())
		{
			E e = i.next();
			
			if (predicate.test(e))
			{
				i.remove();
				return e;
			}
		}
		
		return defaultValue;
	}
	
	
	
	public static <E> E getIfPresentOrReturnDefaultValue(Iterable<E> thingWithIterators, Predicate<E> predicate, @Nullable E defaultValue)
	{
		Iterator<E> i = thingWithIterators.iterator();
		
		while (i.hasNext())
		{
			E e = i.next();
			
			if (predicate.test(e))
			{
				//No i.remove();  the only difference XD :333
				return e;
			}
		}
		
		return defaultValue;
	}
	
	// >
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * + noop iff indexA == indexB
	 */
	public static <E> void swapElements(List<E> list, int indexA, int indexB)
	{
		if (indexA == indexB)
			return;  //noop! ^w^
		
		
		if (list instanceof ListWithSwapCapability)
			((ListWithSwapCapability)list).swap(indexA, indexB);
		
		else
		{
			E tmp = list.get(indexA);
			list.set(indexA, list.get(indexB));
			list.set(indexB, tmp);
			
			
			
			
			
			//No one. say. anything.  XD'''''!
			//   ( I had swap() confused with reorder() in my head and this is what happened X'D )
			//     --PP
			
			/*
			if (indexA == indexB)
				throw new AssertionError();
			else if (indexB < indexA)
			{
				//Swap indexes!
				//Lots of swapping going on apparently! XD
				int _ = indexA;
				indexA = indexB;
				indexB = _;
			}
			
			
			//indexB > indexA  ^_^
			
			if (indexB == indexA + 1) //Special fast version in this case! :D
			{
				list.add(indexA, list.remove(indexB)); //possibly a teeny bit fasters than alternate?
				
				//Alternate version! :D
				//(why do I like alternate versions so much here??? XD )
				//list.add(indexB, list.remove(indexA));
			}
			else
			{
				list.add(indexA+1, list.remove(indexB));
				list.add(indexB, list.remove(indexA));
				
				
				//Which is most efficient!? \o/  :D
				
				
				//Versions which add the new element *after* the old!
				//list.add(indexA+1, list.remove(indexB));
				//list.add(indexB, list.remove(indexA));
				
				//list.add(indexB, list.remove(indexA));
				//list.add(indexA, list.remove(indexB-1));
				
				
				//Versions which add the new element *before* the old!
				//list.add(indexA, list.remove(indexB));
				//list.add(indexB, list.remove(indexA+1));
				
				//list.add(indexB-1, list.remove(indexA));
				//list.add(indexA, list.remove(indexB+1));
				
				
				
				//Alternate versions, if removing twice at the first is preferrable for some reason!
				/*
		E a = list.remove(indexA);
		E b = list.remove(indexB - 1); //indexB comes after indexA, so it will be affected! 00
		
		list.add(indexA, b);
		list.add(indexB, a);
			 * /
				
				/*
		E a = list.remove(indexA);
		E b = list.remove(indexB - 1); //indexB comes after indexA, so it will be affected! 00
		
		list.add(indexB-1, a);
		list.add(indexA, b);
			 * /
				
				
				/*
		E b = list.remove(indexB);
		E a = list.remove(indexA);
		
		list.add(indexA, b);
		list.add(indexB, a);
			 * /
				
				/*
		E b = list.remove(indexB);
		E a = list.remove(indexA);
		
		list.add(indexB-1, a);
		list.add(indexA, b);
			 * /
		}
			 */
		}
	}
	
	
	
	
	
	
	/**
	 * @return the new index of the element that used to be at <code>source</code>!  ^w^
	 */
	public static <E> int reorderRelative(List<E> list, int source, int amountToMove)
	{
		int n = list.size();
		
		if (source < 0 || source >= n)
			throw new IndexOutOfBoundsException();
		
		int dest = SmallIntegerMathUtilities.progmod(source + amountToMove, n);
		
		if (dest == source)
			return source;
		
		//TODO IS THIS WRITTEN CORRECTLY!??!??!!
		E e = list.remove(source);
		list.add(dest, e);
		
		return dest;
	}
	
	
	
	
	
	/**
	 * This is precisely equivalent to:
	 * 
	 * <code>
	 * 		e = list.remove(source)
	 * 		list.add(dest - (source < dest ? 1 : 0), e)
	 * </code>
	 * 
	 * Note that {@link #reorderAbsolute(List, int, int) reorderAbsolute}(x, i, i+1) does nothing XD     (as well as, (x,i,i) of course :3 )
	 */
	public static <E> void reorderAbsolute(List<E> list, int source, int destInPreRemoveCoordinates) throws IndexOutOfBoundsException
	{
		if (source == destInPreRemoveCoordinates)
		{
			return;
		}
		else
		{
			if (list instanceof ListWithReorder)
			{
				((ListWithReorder) list).reorder(source, destInPreRemoveCoordinates);
			}
			else
			{
				//Check the sizes first so that we never remove the element if the adding-back would fail!! \o/
				int destInPostRemoveCoordinates;
				{
					int oldSize = list.size();
					if (source < 0 || source >= oldSize)
						throw new IndexOutOfBoundsException();
					
					destInPostRemoveCoordinates = source < destInPreRemoveCoordinates ? (destInPreRemoveCoordinates - 1) : destInPreRemoveCoordinates;
					int newSize = oldSize - 1;
					if (destInPostRemoveCoordinates < 0 || destInPostRemoveCoordinates > newSize)  //note the > not >= !!
						throw new IndexOutOfBoundsException();
				}
				
				E e = list.remove(source);
				list.add(destInPostRemoveCoordinates, e);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	//TODO Make things implement this! :P!
	
	/**
	 * @return the number of elements copied :>    (only really useful (given you can just call {@link Collection#size()} x3 ) if it's a weak-reference collection, and the size may shrink during usage! (even in single-threaded programs!) )
	 */
	public static int copyIntoArray(Collection source, Object[] dest, int destOffset)
	{
		if (source instanceof CollectionWithCopyIntoArray)
			return ((CollectionWithCopyIntoArray)source).copyIntoArray(dest, destOffset);
		else
		{
			if (source.size() < 128)  //super-arbitrary threshold!  (which may shrink, but oh well xD')
			{
				Object[] c = source.toArray();
				System.arraycopy(c, 0, dest, destOffset, c.length);  //checks array bounds ^_^
				return c.length;
			}
			else
			{
				int i = destOffset;
				
				for (Object e : source)
				{
					dest[i] = e;  //checks array bounds ^_^
					i++;
				}
				
				return i;
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Convenience :3
	public static <E> void setListSize(List<E> list, int newSize)
	{
		setListSize(list, newSize, CollectionWithDefaultElement.getDefaultElement(list));
	}
	
	
	public static <E> void setListSize(List<E> list, int newSize, E elementToAddIfGrowing)
	{
		if (list instanceof ListWithSetSize)
		{
			((ListWithSetSize) list).setSize(newSize, elementToAddIfGrowing);
		}
		else if (list instanceof Vector && elementToAddIfGrowing == null)
		{
			((Vector) list).setSize(newSize);
		}
		else
		{
			defaultSetListSize(list, newSize, elementToAddIfGrowing);
		}
	}
	
	public static <E> void defaultSetListSize(List<E> list, int newSize, E elementToAddIfGrowing)
	{
		int oldSize = list.size();
		
		if (newSize > oldSize)
		{
			int amountToAdd = newSize - oldSize;
			
			for (int i = 0; i < amountToAdd; i++)
			{
				list.add(elementToAddIfGrowing);
			}
		}
		else if (newSize < oldSize)
		{
			for (int i = oldSize-1; i >= newSize; i--)
			{
				list.remove(i);
			}
		}
		else
		{
			//Set it to the same size!?  That's a no-op!  \xD/
		}
	}
	
	
	public static <E> void setListSizeShrinking(List<E> list, int newSize) throws IllegalArgumentException
	{
		if (newSize > list.size())
			throw new IllegalArgumentException();
		else
			setListSize(list, newSize, CollectionWithDefaultElement.getDefaultElement(list));
	}
	
	public static <E> void setListSizeGrowing(List<E> list, int newSize, E elementToAddIfGrowing) throws IllegalArgumentException
	{
		if (newSize < list.size())
			throw new IllegalArgumentException();
		else
			setListSize(list, newSize, elementToAddIfGrowing);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO TEST X'D
	public static void removeRange(List list, int start, int pastEnd)
	{
		rangeCheckFor_removeRange(list, start, pastEnd);
		
		if (list instanceof ListWithRemoveRange)
			((ListWithRemoveRange)list).removeRange(start, pastEnd);
		else
			defaultRemoveRange(list, start, pastEnd);
	}
	
	public static void removeRangeByLength(List list, int start, int length)
	{
		removeRange(list, start, start+length);
	}
	
	
	
	public static void defaultRemoveRange(List list, int start, int pastEnd)
	{
		shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(list, pastEnd, -(pastEnd - start));
	}
	
	
	
	
	public static <E> int indexOf(Predicate<E> predicate, @CollectionValue E[] list)
	{
		int length = list.length;
		for (int i = 0; i < length; i++)
			if (predicate.test(list[i]))
				return i;
		return -1;
	}
	
	public static <E> int indexOf(Predicate<E> predicate, @CollectionValue List<E> list)
	{
		if (isRandomAccessFast(list))
		{
			int length = list.size();
			for (int i = 0; i < length; i++)
				if (predicate.test(list.get(i)))
					return i;
			return -1;
		}
		else
		{
			int i = 0;
			for (E element : list)
			{
				if (predicate.test(element))
					return i;
				i++;
			}
			return -1;
		}
	}
	
	
	public static <E> int indexOfSingle(Predicate<E> predicate, @CollectionValue List<E> list) throws NotSingletonException
	{
		if (isRandomAccessFast(list))
		{
			int rv = -1;
			int length = list.size();
			for (int i = 0; i < length; i++)
			{
				if (predicate.test(list.get(i)))
				{
					if (rv == -1)
						rv = i;
					else
						throw new NotSingletonException();
				}
			}
			return rv;
		}
		else
		{
			int rv = -1;
			int i = 0;
			
			for (E element : list)
			{
				if (predicate.test(element))
				{
					if (rv == -1)
						rv = i;
					else
						throw new NotSingletonException();
				}
				
				i++;
			}
			
			return -1;
		}
	}
	
	
	
	
	
	
	
	public static <E> boolean contains(Predicate<E> predicate, @CollectionValue E[] list)
	{
		return indexOf(predicate, list) != -1;
	}
	
	public static <E> boolean contains(Predicate<E> predicate, @CollectionValue List<E> list)
	{
		return indexOf(predicate, list) != -1;
	}
	
	public static <E> boolean contains(Predicate<E> predicate, @CollectionValue Iterable<E> collection)
	{
		for (E e : collection)
			if (predicate.test(e))
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static <E> E findFirstRP(Predicate<E> predicate, @CollectionValue E[] list) throws NoSuchElementReturnPath
	{
		int length = list.length;
		E element = null;
		for (int i = 0; i < length; i++)
		{
			element = list[i];
			if (predicate.test(element))
				return element;
		}
		throw NoSuchElementReturnPath.I;
	}
	
	public static <E> E findFirstRP(Predicate<E> predicate, @CollectionValue List<E> list) throws NoSuchElementReturnPath
	{
		if (isRandomAccessFast(list))
		{
			int length = list.size();
			E element = null;
			for (int i = 0; i < length; i++)
			{
				element = list.get(i);
				if (predicate.test(element))
					return element;
			}
			throw NoSuchElementReturnPath.I;
		}
		else
		{
			for (E element : list)
				if (predicate.test(element))
					return element;
			throw NoSuchElementReturnPath.I;
		}
	}
	
	public static <E> E findFirstRP(Predicate<E> predicate, @CollectionValue Iterator<E> it) throws NoSuchElementReturnPath
	{
		while (it.hasNext())
		{
			E element = it.next();
			if (predicate.test(element))
				return element;
		}
		throw NoSuchElementReturnPath.I;
	}
	
	
	
	public static @Nullable Object findFirst(Predicate predicate, @CollectionValue Object list)
	{
		try
		{
			return PolymorphicCollectionUtilities.findFirstRP(predicate, list);
		}
		catch (NoSuchElementReturnPath exc)
		{
			return null;
		}
	}
	
	public static @Nullable <E> E findFirst(Predicate<E> predicate, @CollectionValue E[] list)
	{
		try
		{
			return findFirstRP(predicate, list);
		}
		catch (NoSuchElementReturnPath exc)
		{
			return null;
		}
	}
	
	public static @Nullable <E> E findFirst(Predicate<E> predicate, @CollectionValue List<E> list)
	{
		try
		{
			return findFirstRP(predicate, list);
		}
		catch (NoSuchElementReturnPath exc)
		{
			return null;
		}
	}
	
	
	
	public static <E> int findFirstIndex(Predicate<E> predicate, @CollectionValue List<E> list)
	{
		return findFirstIntegerInIntervalOrNegativeOne(i -> predicate.test(list.get(i)), 0, list.size());
	}
	
	public static <E> int findLastIndex(Predicate<E> predicate, @CollectionValue List<E> list)
	{
		return findLastIntegerInIntervalOrNegativeOne(i -> predicate.test(list.get(i)), 0, list.size());
	}
	
	
	public static <E> int findFirstIndex(Predicate<E> predicate, @CollectionValue List<E> list, int start)
	{
		return findFirstIntegerInIntervalOrNegativeOne(i -> predicate.test(list.get(i)), start, list.size());
	}
	
	public static <E> int findLastIndex(Predicate<E> predicate, @CollectionValue List<E> list, int start)
	{
		return findLastIntegerInIntervalOrNegativeOne(i -> predicate.test(list.get(i)), 0, start+1);
	}
	
	
	
	
	
	public static int findFirstIndex(UnaryFunctionCharToBoolean predicate, @CollectionValue CharSequence list)
	{
		return findFirstIntegerInIntervalOrNegativeOne(i -> predicate.f(list.charAt(i)), 0, list.length());
	}
	
	public static int findLastIndex(UnaryFunctionCharToBoolean predicate, @CollectionValue CharSequence list)
	{
		return findLastIntegerInIntervalOrNegativeOne(i -> predicate.f(list.charAt(i)), 0, list.length());
	}
	
	
	public static int findFirstIndex(UnaryFunctionCharToBoolean predicate, @CollectionValue CharSequence list, int start)
	{
		return findFirstIntegerInIntervalOrNegativeOne(i -> predicate.f(list.charAt(i)), start, list.length());
	}
	
	public static int findLastIndex(UnaryFunctionCharToBoolean predicate, @CollectionValue CharSequence list, int start)
	{
		return findLastIntegerInIntervalOrNegativeOne(i -> predicate.f(list.charAt(i)), 0, start+1);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * + Note: If you stick List.get(i) or similar inside your predicate, this can be used to find the *index* not merely the object! ;D
	 */
	@Nullable
	public static Integer findFirstIntegerInInterval(Predicate<Integer> predicate, int inclusiveLowerBound, int exclusiveUpperBound)
	{
		for (int i = inclusiveLowerBound; i < exclusiveUpperBound; i++)
			if (predicate.test(i))
				return i;
		
		return null;
	}
	
	
	
	/**
	 * + Note: If you stick List.get(i) or similar inside your predicate, this can be used to find the *index* not merely the object! ;D
	 */
	@Nullable
	public static Integer findLastIntegerInInterval(Predicate<Integer> predicate, int inclusiveLowerBound, int exclusiveUpperBound)
	{
		for (int i = exclusiveUpperBound-1; i >= inclusiveLowerBound; i--)
			if (predicate.test(i))
				return i;
		
		return null;
	}
	
	
	
	
	
	
	/**
	 * + Note: If you stick List.get(i) or similar inside your predicate, this can be used to find the *index* not merely the object! ;D
	 */
	public static int findFirstIntegerInIntervalOrNegativeOne(Predicate<Integer> predicate, int inclusiveLowerBound, int exclusiveUpperBound)
	{
		for (int i = inclusiveLowerBound; i < exclusiveUpperBound; i++)
			if (predicate.test(i))
				return i;
		
		return -1;
	}
	
	
	
	/**
	 * + Note: If you stick List.get(i) or similar inside your predicate, this can be used to find the *index* not merely the object! ;D
	 */
	public static int findLastIntegerInIntervalOrNegativeOne(Predicate<Integer> predicate, int inclusiveLowerBound, int exclusiveUpperBound)
	{
		for (int i = exclusiveUpperBound-1; i >= inclusiveLowerBound; i--)
			if (predicate.test(i))
				return i;
		
		return -1;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:intsonly$$_
	
	public static _$$prim$$_[] interval_$$Primitive$$_sArray(_$$prim$$_ first, int count)
	{
		_$$prim$$_[] a = new _$$prim$$_[count];
		
		for (int i = 0; i < count; i++)
			a[i] = (_$$prim$$_)(first + i);
		
		return a;
	}
	
	public static Immutable_$$Primitive$$_IntervalList interval_$$Primitive$$_sList(_$$prim$$_ first, int count)
	{
		return new Immutable_$$Primitive$$_IntervalList(first, count);
	}
	
	public static Immutable_$$Primitive$$_IntervalSet interval_$$Primitive$$_sSet(_$$prim$$_ first, int count)
	{
		return new Immutable_$$Primitive$$_IntervalSet(first, count);
	}
	
	
	 */
	
	
	public static byte[] intervalBytesArray(byte first, int count)
	{
		byte[] a = new byte[count];
		
		for (int i = 0; i < count; i++)
			a[i] = (byte)(first + i);
		
		return a;
	}
	
	public static ImmutableByteIntervalList intervalBytesList(byte first, int count)
	{
		return new ImmutableByteIntervalList(first, count);
	}
	
	public static ImmutableByteIntervalSet intervalBytesSet(byte first, int count)
	{
		return new ImmutableByteIntervalSet(first, count);
	}
	
	
	
	
	public static char[] intervalCharactersArray(char first, int count)
	{
		char[] a = new char[count];
		
		for (int i = 0; i < count; i++)
			a[i] = (char)(first + i);
		
		return a;
	}
	
	public static ImmutableCharacterIntervalList intervalCharactersList(char first, int count)
	{
		return new ImmutableCharacterIntervalList(first, count);
	}
	
	public static ImmutableCharacterIntervalSet intervalCharactersSet(char first, int count)
	{
		return new ImmutableCharacterIntervalSet(first, count);
	}
	
	
	
	
	public static short[] intervalShortsArray(short first, int count)
	{
		short[] a = new short[count];
		
		for (int i = 0; i < count; i++)
			a[i] = (short)(first + i);
		
		return a;
	}
	
	public static ImmutableShortIntervalList intervalShortsList(short first, int count)
	{
		return new ImmutableShortIntervalList(first, count);
	}
	
	public static ImmutableShortIntervalSet intervalShortsSet(short first, int count)
	{
		return new ImmutableShortIntervalSet(first, count);
	}
	
	
	
	
	public static int[] intervalIntegersArray(int first, int count)
	{
		int[] a = new int[count];
		
		for (int i = 0; i < count; i++)
			a[i] = (int)(first + i);
		
		return a;
	}
	
	public static ImmutableIntegerIntervalList intervalIntegersList(int first, int count)
	{
		return new ImmutableIntegerIntervalList(first, count);
	}
	
	public static ImmutableIntegerIntervalSet intervalIntegersSet(int first, int count)
	{
		return new ImmutableIntegerIntervalSet(first, count);
	}
	
	
	
	
	public static long[] intervalLongsArray(long first, int count)
	{
		long[] a = new long[count];
		
		for (int i = 0; i < count; i++)
			a[i] = (long)(first + i);
		
		return a;
	}
	
	public static ImmutableLongIntervalList intervalLongsList(long first, int count)
	{
		return new ImmutableLongIntervalList(first, count);
	}
	
	public static ImmutableLongIntervalSet intervalLongsSet(long first, int count)
	{
		return new ImmutableLongIntervalSet(first, count);
	}
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:byte,char,short,int$$_
	
	
	public static _$$prim$$_[] interval_$$Primitive$$_sArrayByExclusiveEnd(_$$prim$$_ first, int pastEnd)
	{
		return interval_$$Primitive$$_sArray(first, pastEnd - first);
	}
	
	public static Immutable_$$Primitive$$_IntervalList interval_$$Primitive$$_sListByExclusiveEnd(_$$prim$$_ first, int pastEnd)
	{
		return interval_$$Primitive$$_sList(first, pastEnd - first);
	}
	
	public static Immutable_$$Primitive$$_IntervalSet interval_$$Primitive$$_sSetByExclusiveEnd(_$$prim$$_ first, int pastEnd)
	{
		return interval_$$Primitive$$_sSet(first, pastEnd - first);
	}
	
	
	
	public static _$$prim$$_[] interval_$$Primitive$$_sArrayByInclusiveEnd(_$$prim$$_ first, _$$prim$$_ last)
	{
		return interval_$$Primitive$$_sArrayByExclusiveEnd(first, last+1);
	}
	
	public static Immutable_$$Primitive$$_IntervalList interval_$$Primitive$$_sListByInclusiveEnd(_$$prim$$_ first, _$$prim$$_ last)
	{
		return interval_$$Primitive$$_sListByExclusiveEnd(first, last+1);
	}
	
	public static Immutable_$$Primitive$$_IntervalSet interval_$$Primitive$$_sSetByInclusiveEnd(_$$prim$$_ first, _$$prim$$_ last)
	{
		return interval_$$Primitive$$_sSetByExclusiveEnd(first, last+1);
	}
	
	
	 */
	
	
	
	public static byte[] intervalBytesArrayByExclusiveEnd(byte first, int pastEnd)
	{
		return intervalBytesArray(first, pastEnd - first);
	}
	
	public static ImmutableByteIntervalList intervalBytesListByExclusiveEnd(byte first, int pastEnd)
	{
		return intervalBytesList(first, pastEnd - first);
	}
	
	public static ImmutableByteIntervalSet intervalBytesSetByExclusiveEnd(byte first, int pastEnd)
	{
		return intervalBytesSet(first, pastEnd - first);
	}
	
	
	
	public static byte[] intervalBytesArrayByInclusiveEnd(byte first, byte last)
	{
		return intervalBytesArrayByExclusiveEnd(first, last+1);
	}
	
	public static ImmutableByteIntervalList intervalBytesListByInclusiveEnd(byte first, byte last)
	{
		return intervalBytesListByExclusiveEnd(first, last+1);
	}
	
	public static ImmutableByteIntervalSet intervalBytesSetByInclusiveEnd(byte first, byte last)
	{
		return intervalBytesSetByExclusiveEnd(first, last+1);
	}
	
	
	
	
	
	public static char[] intervalCharactersArrayByExclusiveEnd(char first, int pastEnd)
	{
		return intervalCharactersArray(first, pastEnd - first);
	}
	
	public static ImmutableCharacterIntervalList intervalCharactersListByExclusiveEnd(char first, int pastEnd)
	{
		return intervalCharactersList(first, pastEnd - first);
	}
	
	public static ImmutableCharacterIntervalSet intervalCharactersSetByExclusiveEnd(char first, int pastEnd)
	{
		return intervalCharactersSet(first, pastEnd - first);
	}
	
	
	
	public static char[] intervalCharactersArrayByInclusiveEnd(char first, char last)
	{
		return intervalCharactersArrayByExclusiveEnd(first, last+1);
	}
	
	public static ImmutableCharacterIntervalList intervalCharactersListByInclusiveEnd(char first, char last)
	{
		return intervalCharactersListByExclusiveEnd(first, last+1);
	}
	
	public static ImmutableCharacterIntervalSet intervalCharactersSetByInclusiveEnd(char first, char last)
	{
		return intervalCharactersSetByExclusiveEnd(first, last+1);
	}
	
	
	
	
	
	public static short[] intervalShortsArrayByExclusiveEnd(short first, int pastEnd)
	{
		return intervalShortsArray(first, pastEnd - first);
	}
	
	public static ImmutableShortIntervalList intervalShortsListByExclusiveEnd(short first, int pastEnd)
	{
		return intervalShortsList(first, pastEnd - first);
	}
	
	public static ImmutableShortIntervalSet intervalShortsSetByExclusiveEnd(short first, int pastEnd)
	{
		return intervalShortsSet(first, pastEnd - first);
	}
	
	
	
	public static short[] intervalShortsArrayByInclusiveEnd(short first, short last)
	{
		return intervalShortsArrayByExclusiveEnd(first, last+1);
	}
	
	public static ImmutableShortIntervalList intervalShortsListByInclusiveEnd(short first, short last)
	{
		return intervalShortsListByExclusiveEnd(first, last+1);
	}
	
	public static ImmutableShortIntervalSet intervalShortsSetByInclusiveEnd(short first, short last)
	{
		return intervalShortsSetByExclusiveEnd(first, last+1);
	}
	
	
	
	
	
	public static int[] intervalIntegersArrayByExclusiveEnd(int first, int pastEnd)
	{
		return intervalIntegersArray(first, pastEnd - first);
	}
	
	public static ImmutableIntegerIntervalList intervalIntegersListByExclusiveEnd(int first, int pastEnd)
	{
		return intervalIntegersList(first, pastEnd - first);
	}
	
	public static ImmutableIntegerIntervalSet intervalIntegersSetByExclusiveEnd(int first, int pastEnd)
	{
		return intervalIntegersSet(first, pastEnd - first);
	}
	
	
	
	public static int[] intervalIntegersArrayByInclusiveEnd(int first, int last)
	{
		return intervalIntegersArrayByExclusiveEnd(first, last+1);
	}
	
	public static ImmutableIntegerIntervalList intervalIntegersListByInclusiveEnd(int first, int last)
	{
		return intervalIntegersListByExclusiveEnd(first, last+1);
	}
	
	public static ImmutableIntegerIntervalSet intervalIntegersSetByInclusiveEnd(int first, int last)
	{
		return intervalIntegersSetByExclusiveEnd(first, last+1);
	}
	
	
	// >>>
	
	
	
	
	
	
	
	
	public static long[] intervalLongsArrayByExclusiveEnd(long first, long pastEnd)
	{
		return intervalLongsArray(first, safeCastS64toS32(pastEnd - first));
	}
	
	public static ImmutableLongIntervalList intervalLongsListByExclusiveEnd(long first, long pastEnd)
	{
		return intervalLongsList(first, safeCastS64toS32(pastEnd - first));
	}
	
	public static ImmutableLongIntervalSet intervalLongsSetByExclusiveEnd(long first, long pastEnd)
	{
		return intervalLongsSet(first, safeCastS64toS32(pastEnd - first));
	}
	
	
	
	public static long[] intervalLongsArrayByInclusiveEnd(long first, long last)
	{
		return intervalLongsArrayByExclusiveEnd(first, last+1);
	}
	
	public static ImmutableLongIntervalList intervalLongsListByInclusiveEnd(long first, long last)
	{
		return intervalLongsListByExclusiveEnd(first, last+1);
	}
	
	public static ImmutableLongIntervalSet intervalLongsSetByInclusiveEnd(long first, long last)
	{
		return intervalLongsSetByExclusiveEnd(first, last+1);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static <E> int countEq(E candidate, @CollectionValue E[] list)
	{
		return count(x -> eq(candidate, x), list);
	}
	
	public static <E> int countEq(E candidate, @CollectionValue Iterable list)
	{
		return count(x -> eq(candidate, x), list);
	}
	
	
	
	
	
	
	public static <E> int count(Predicate<E> predicate, @CollectionValue E[] list)
	{
		int length = list.length;
		int count = 0;
		for (int i = 0; i < length; i++)
			if (predicate.test(list[i]))
				count++;
		return count;
	}
	
	public static <E> int count(Predicate<E> predicate, @CollectionValue Iterable<E> collection)
	{
		if (collection instanceof List)
		{
			List<E> list = (List<E>) collection;
			
			if (isRandomAccessFast(list))
			{
				int length = list.size();
				int count = 0;
				for (int i = 0; i < length; i++)
					if (predicate.test(list.get(i)))
						count++;
				return count;
			}
			else
			{
				int count = 0;
				for (E element : list)
				{
					if (predicate.test(element))
						count++;
				}
				return count;
			}
		}
		else
		{
			int count = 0;
			for (E element : collection)
			{
				if (predicate.test(element))
					count++;
			}
			return count;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Converters and viewwrappers! :D
	protected static final String Converters_ClassCastException_Message_Prefix = "Only arrays, "+Collection.class.getName()+"'s, "+Iterable.class.getName()+"'s, "+SimpleIterable.class.getName()+"'s, "+Iterator.class.getName()+"'s, "+Enumeration.class.getName()+"'s, "+SimpleIterator.class.getName()+"'s, and "+Map.class.getName()+"'s(values) are supported,  not ";
	
	/*
		Object[]
		prim[]
		[subtype]
		Iterable
			Collection
			List
			Set
			:>
		Map (values ;> )
		SimpleIterable
		Iterator
		Enumeration
		SimpleIterator
	 */
	
	
	protected static ClassCastException newComponentTypeMismatchException(Class source, Class dest)
	{
		return new ClassCastException("Source elements are "+source.getCanonicalName()+" but dest elements are "+dest.getCanonicalName()+"; source is not 'instanceof' dest!  D:");
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static <E> List<E> asList(Iterable<E> iterable)
	{
		if (iterable instanceof List)
			return (List<E>)iterable;
		else if (iterable instanceof Collection)
			return ((Collection)iterable).isEmpty() ? emptyList() : new ArrayList<>((Collection)iterable);
			else if (iterable instanceof SimpleIterable)
				return asList(((SimpleIterable)iterable).simpleIterator());
			else
				return asList(iterable.iterator());
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static <E> List<E> asList(Iterator<E> iterator)
	{
		List<E> list = null;
		for (E e : singleUseIterable(iterator))
		{
			if (list == null)
				list = new ArrayList<>();
			list.add(e);
		}
		return list == null ? emptyList() : list;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static <E> List<E> asList(SimpleIterator<E> iterator)
	{
		List<E> list = null;
		
		while (true)
		{
			E e;
			try
			{
				e = iterator.nextrp();
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
			
			if (list == null)
				list = new ArrayList<>();
			
			list.add(e);
		}
		
		return list == null ? emptyList() : list;
	}
	
	//Importing java.util.Arrays.* conflicts with java.util.ArrayList so they can't both be imported! :[
	//So we offer a delegate here to get around that! ^wwwww^
	//(also it's not varargs (unless you want that with asListV()), since varargs with Object causes a lot of confusion when passing an Object[] to it X'D )
	@LiveValue
	@WritableValue
	public static <E> List<E> asList(E[] array)  //fixed-length but writable view of the array!
	{
		return Arrays.asList(array);
	}
	@LiveValue
	@WritableValue
	public static <E> List<E> asListV(E... array)
	{
		return asList(array);
	}
	
	
	
	
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or pastEnd is too big), this just returns as much as it can.
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> asSubListByEndLenient(Iterable<E> iterable, int start, int pastEnd)
	{
		if (pastEnd < start)  throw new IllegalArgumentException();
		
		if (iterable instanceof List)
		{
			List<E> l = (List<E>) iterable;
			int n = l.size();
			return l.subList(least(start, n), least(pastEnd, n));
		}
		else
		{
			return toNewMutableVariablelengthSubListLenient(iterable, start, pastEnd);
		}
	}
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or pastEnd is too big), this just returns as much as it can.
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> asSubListByEndLenient(Iterator<E> iterator, int start, int pastEnd)
	{
		return toNewMutableVariablelengthSubListLenient(iterator, start, pastEnd);
	}
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or pastEnd is too big), this just returns as much as it can.
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> asSubListByEndLenient(SimpleIterator<E> iterator, int start, int pastEnd)
	{
		return toNewMutableVariablelengthSubListLenient(iterator, start, pastEnd);
	}
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or pastEnd is too big), this just returns as much as it can.
	 */
	@LiveValue
	@WritableValue
	public static <E> List<E> asSubListByEndLenient(E[] array, int start, int pastEnd)  //fixed-length but writable view of the array!
	{
		if (pastEnd < start)  throw new IllegalArgumentException();
		
		int n = array.length;
		return Arrays.asList(array).subList(least(start, n), least(pastEnd, n));  //Arrays.asList() produces a view implementation, so this is fine :>
	}
	
	
	
	
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or size is too big), this just returns as much as it can.
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> asSubListBySizeLenient(Iterable<E> iterable, int start, int size)
	{
		return asSubListByEndLenient(iterable, start, start+size);
	}
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or size is too big), this just returns as much as it can.
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> asSubListBySizeLenient(Iterator<E> iterator, int start, int size)
	{
		return asSubListByEndLenient(iterator, start, start+size);
	}
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or size is too big), this just returns as much as it can.
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> asSubListBySizeLenient(SimpleIterator<E> iterator, int start, int size)
	{
		return asSubListByEndLenient(iterator, start, start+size);
	}
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or size is too big), this just returns as much as it can.
	 */
	@LiveValue
	@WritableValue
	public static <E> List<E> asSubListBySizeLenient(E[] array, int start, int size)  //fixed-length but writable view of the array!
	{
		return asSubListByEndLenient(array, start, start+size);
	}
	
	
	
	
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (because start is too big), this just returns as much as it can.
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> asSubListFromStartLenient(Iterable<E> iterable, int sizeWhichIsPastEnd)
	{
		return asSubListByEndLenient(iterable, 0, sizeWhichIsPastEnd);
	}
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (because start is too big), this just returns as much as it can.
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> asSubListFromStartLenient(Iterator<E> iterator, int sizeWhichIsPastEnd)
	{
		return asSubListByEndLenient(iterator, 0, sizeWhichIsPastEnd);
	}
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (because start is too big), this just returns as much as it can.
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> asSubListFromStartLenient(SimpleIterator<E> iterator, int sizeWhichIsPastEnd)
	{
		return asSubListByEndLenient(iterator, 0, sizeWhichIsPastEnd);
	}
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (because start is too big), this just returns as much as it can.
	 */
	@LiveValue
	@WritableValue
	public static <E> List<E> asSubListFromStartLenient(E[] array, int sizeWhichIsPastEnd)  //fixed-length but writable view of the array!
	{
		return asSubListByEndLenient(array, 0, sizeWhichIsPastEnd);
	}
	
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> asSetUniqueifying(Iterable<E> iterable)
	{
		return iterable instanceof Set ? (Set<E>)iterable : toNewMutableSetUniqueifying(iterable);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> asSetUniqueifying(SimpleIterator<E> iterator)
	{
		return toNewMutableSetUniqueifying(iterator);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> asSetUniqueifying(Iterator<E> iterator)
	{
		return toNewMutableSetUniqueifying(iterator);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> asSetUniqueifying(E[] array)
	{
		return asSetUniqueifying(asList(array));
	}
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> asSetUniqueifyingV(E... array)
	{
		return asSetUniqueifying(array);
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> asSetThrowing(Iterable<E> iterable) throws AlreadyExistsException
	{
		return iterable instanceof Set ? (Set<E>)iterable : toNewMutableSetThrowing(iterable);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> asSetThrowing(SimpleIterator<E> iterator) throws AlreadyExistsException
	{
		return toNewMutableSetThrowing(iterator);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> asSetThrowing(Iterator<E> iterator) throws AlreadyExistsException
	{
		return toNewMutableSetThrowing(iterator);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> asSetThrowing(E[] array) throws AlreadyExistsException
	{
		return asSetThrowing(asList(array));
	}
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> asSetThrowingV(E... array) throws AlreadyExistsException
	{
		return asSetThrowing(array);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@ThrowAwayValue
	public static <E> List<E> toNewMutableVariablelengthList(Iterable<E> iterable)
	{
		if (iterable instanceof Collection)
			return new ArrayList<>((Collection)iterable);
		else if (iterable instanceof SimpleIterable)
			return toNewMutableVariablelengthList(((SimpleIterable)iterable).simpleIterator());
		else
			return toNewMutableVariablelengthList(iterable.iterator());
	}
	
	@ThrowAwayValue
	public static <E> List<E> toNewMutableVariablelengthList(Iterator<E> iterator)
	{
		List<E> list = new ArrayList<>();
		for (E e : singleUseIterable(iterator))
			list.add(e);
		return list;
	}
	
	@ThrowAwayValue
	public static <E> List<E> toNewMutableVariablelengthList(SimpleIterator<E> iterator)
	{
		List<E> list = new ArrayList<>();
		
		while (true)
		{
			E e;
			try
			{
				e = iterator.nextrp();
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
			
			list.add(e);
		}
		
		return list;
	}
	
	
	
	
	
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or pastEnd is too big), this just returns as much as it can.
	 */
	@ThrowAwayValue
	public static <E> List<E> toNewMutableVariablelengthSubListLenient(Iterable<E> iterable, int start, int pastEnd)
	{
		if (pastEnd < start)  throw new IllegalArgumentException();
		
		if (iterable instanceof Collection)
		{
			if (start == 0)
			{
				int size = ((Collection) iterable).size();
				
				if (size == pastEnd)
				{
					return new ArrayList<>((Collection)iterable);
				}
			}
		}
		
		
		//else
		{
			if (iterable instanceof SimpleIterable)
				return toNewMutableVariablelengthSubListLenient(((SimpleIterable)iterable).simpleIterator(), start, pastEnd);
			else
				return toNewMutableVariablelengthSubListLenient(iterable.iterator(), start, pastEnd);
		}
	}
	
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or pastEnd is too big), this just returns as much as it can.
	 */
	@ThrowAwayValue
	public static <E> List<E> toNewMutableVariablelengthSubListLenient(Iterator<E> iterator, int start, int pastEnd)
	{
		if (pastEnd < start)  throw new IllegalArgumentException();
		
		List<E> list = new ArrayList<>(pastEnd - start);
		
		int i = 0;
		for (E e : singleUseIterable(iterator))
		{
			if (i >= pastEnd)
				break;
			
			if (i >= start)
				list.add(e);
			
			i++;
		}
		
		return list;
	}
	
	
	/**
	 * • Lenient meaning if the underlying thing isn't long enough (either because start or pastEnd is too big), this just returns as much as it can.
	 */
	@ThrowAwayValue
	public static <E> List<E> toNewMutableVariablelengthSubListLenient(SimpleIterator<E> iterator, int start, int pastEnd)
	{
		if (pastEnd < start)  throw new IllegalArgumentException();
		
		List<E> list = new ArrayList<>(pastEnd - start);
		
		int i = 0;
		while (i < pastEnd)
		{
			E e;
			try
			{
				e = iterator.nextrp();
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
			
			if (i >= start)
				list.add(e);
			
			i++;
		}
		
		return list;
	}
	
	
	
	
	
	
	
	@ThrowAwayValue
	public static <E> List<E> toNewMutableVariablelengthList(E[] array)
	{
		return toNewMutableVariablelengthList(asList(array));
	}
	@ThrowAwayValue
	public static <E> List<E> toNewMutableVariablelengthListV(E... array)
	{
		return toNewMutableVariablelengthList(array);
	}
	
	
	
	
	@ThrowAwayValue
	public static <E> Set<E> toNewMutableSetUniqueifying(Iterable<E> iterable)
	{
		if (iterable instanceof Collection)
		{
			return new HashSet<>((Collection)iterable);
		}
		else
		{
			Set<E> set = new HashSet<>();
			for (E e : iterable)
				set.add(e);
			return set;
		}
	}
	
	@ThrowAwayValue
	public static <E> Set<E> toNewMutableSetUniqueifying(Iterator<E> iterator)
	{
		return toNewMutableSetUniqueifying(singleUseIterable(iterator));
	}
	
	@ThrowAwayValue
	public static <E> Set<E> toNewMutableSetUniqueifying(SimpleIterator<E> iterator)
	{
		Set<E> set = new HashSet<>();
		
		while (true)
		{
			E e;
			try
			{
				e = iterator.nextrp();
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
			
			set.add(e);
		}
		
		return set;
	}
	
	@ThrowAwayValue
	public static <E> Set<E> toNewMutableSetUniqueifying(E[] array)
	{
		return toNewMutableSetUniqueifying(asList(array));
	}
	@ThrowAwayValue
	public static <E> Set<E> toNewMutableSetUniqueifyingV(E... array)
	{
		return toNewMutableSetUniqueifying(array);
	}
	
	
	
	
	@ThrowAwayValue
	public static <E> Set<E> toNewMutableSetThrowing(Iterable<E> iterable) throws AlreadyExistsException
	{
		if (iterable instanceof Collection)
		{
			return new HashSet<>((Collection)iterable);
		}
		else
		{
			Set<E> set = new HashSet<>();
			for (E e : iterable)
				addNewMandatory(set, e);
			return set;
		}
	}
	
	@ThrowAwayValue
	public static <E> Set<E> toNewMutableSetThrowing(Iterator<E> iterator) throws AlreadyExistsException
	{
		return toNewMutableSetThrowing(singleUseIterable(iterator));
	}
	
	@ThrowAwayValue
	public static <E> Set<E> toNewMutableSetThrowing(SimpleIterator<E> iterator) throws AlreadyExistsException
	{
		Set<E> set = new HashSet<>();
		
		while (true)
		{
			E e;
			try
			{
				e = iterator.nextrp();
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
			
			addNewMandatory(set, e);
		}
		
		return set;
	}
	
	@ThrowAwayValue
	public static <E> Set<E> toNewMutableSetThrowing(E[] array) throws AlreadyExistsException
	{
		return toNewMutableSetThrowing(asList(array));
	}
	@ThrowAwayValue
	public static <E> Set<E> toNewMutableSetThrowingV(E... array) throws AlreadyExistsException
	{
		return toNewMutableSetThrowing(array);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@LiveValue
	public static <E> Iterable<E> singleUseIterable(final Iterator<E> x)
	{
		return () -> x;
	}
	
	@LiveValue //:D!  (many fasters! :D )
	public static <E> Iterable<E> singleUseIterable(final SimpleIterator<E> x)
	{
		return singleUseIterable(x.toIterator());
	}
	
	
	@LiveValue //:D!  (many fasters! :D )
	public static <E> Iterable<E> singleUseIterable(final Enumeration<E> x)
	{
		return singleUseIterable(PolymorphicCollectionUtilities.anyToIterator(x));
	}
	
	@LiveValue
	@WritableValue
	public static <E> Iterator<E> getRemoveCallbackIteratorDecorator(final Iterator<E> readonlyIterator, final Collection<E> backingCollection)
	{
		return new Iterator<E>()
		{
			E current = null;
			
			@Override
			public boolean hasNext()
			{
				return readonlyIterator.hasNext();
			}
			
			@Override
			public E next()
			{
				this.current = readonlyIterator.next();
				return this.current;
			}
			
			@Override
			public void remove()
			{
				backingCollection.remove(this.current);
			}
		};
	}
	
	
	
	@LiveValue
	@WritableValue
	public static <E> Iterator<E> getRemoveCallbackIteratorDecorator(final Iterator<E> readonlyIterator, final UnaryProcedure<E> removeHook)
	{
		return new Iterator<E>()
		{
			E current = null;
			
			@Override
			public boolean hasNext()
			{
				return readonlyIterator.hasNext();
			}
			
			@Override
			public E next()
			{
				this.current = readonlyIterator.next();
				return this.current;
			}
			
			@Override
			public void remove()
			{
				removeHook.f(this.current);
			}
		};
	}
	
	
	/**
	 * @see RandomAccess
	 */
	@LiveValue
	@WritableValue
	public static <E> Iterator<E> getRandomAccessRemoveCallbackIteratorDecorator(final Iterator<E> readonlyIterator, final List<E> backingList)
	{
		return getIndexBasedRemoveCallbackIteratorDecorator(readonlyIterator, backingList::remove);
	}
	
	
	@LiveValue
	@WritableValue
	public static <E> Iterator<E> getIndexBasedRemoveCallbackIteratorDecorator(final Iterator<E> readonlyIterator, final UnaryProcedureInt remove)
	{
		return new Iterator<E>()
		{
			int i = 0;
			
			@Override
			public boolean hasNext()
			{
				return readonlyIterator.hasNext();
			}
			
			@Override
			public E next()
			{
				E r = readonlyIterator.next();
				this.i++;
				return r;
			}
			
			@Override
			public void remove()
			{
				remove.f(this.i);
			}
		};
	}
	
	
	
	
	@LiveValue
	@WritableValue
	public static <E> Iterator<E> newFullRandomAccessIterator(int initialSize, final UnaryFunction<Integer, E> getByIndex, final UnaryProcedureInt removeByIndex)
	{
		return new Iterator<E>()
		{
			int nextIndex = 0;
			int size = initialSize;
			boolean alreadyRemoved = false;
			
			@Override
			public boolean hasNext()
			{
				return nextIndex < size;
			}
			
			@Override
			public E next()
			{
				if (nextIndex >= size)
					throw new NoSuchElementException();
				
				E r = getByIndex.f(nextIndex);
				
				if (alreadyRemoved)
					this.alreadyRemoved = false;
				
				this.nextIndex++;
				
				return r;
			}
			
			@Override
			public void remove() throws IllegalStateException
			{
				if (nextIndex == 0)
					throw new IllegalStateException();  //different line numbers :3
				if (alreadyRemoved)
					throw new IllegalStateException();  //different line numbers :3
				
				
				//do first in case the latter throws error
				{
					alreadyRemoved = true;
					size--;
					nextIndex--;
				}
				
				removeByIndex.f(this.nextIndex);
			}
		};
	}
	
	
	
	
	
	
	//These were always a bad idea XD''
	//
	//	/**
	//	 * Note: you probably should use {@link #toArray(Object, Class)} and specify the component type explicitly if you can xP
	//	 *
	//	 *
	//	 * Like {@link #toObjectArray(Object)}, but attempts to determine the correct runtime type of the array by checking the highest common type of all the elements.
	//	 * (obviously this doesn't work for an empty thing, and throws a {@link NonDuckTypableException} instead of returning an Object[0] 8| )
	//	 *
	//	 * If you need it to work for empty arrays, use {@link #toArray(Object, Class)}!!
	//	 */
	//	@SnapshotValue
	//	@ThrowAwayValue
	//	public static <E> E[] toDynTypedArray(Object x) throws NonDuckTypableException
	//	{
	//		Object[] objectArray = toObjectArray(x);
	//
	//		if (objectArray.length == 0)
	//			throw new NonDuckTypableException("Empty input; no objects to figure out the proper component type with! ;_;"); //pretty much the only time we can't determine the array type at runtime!
	//		else
	//		{
	//			Class scs = AngryReflectionUtility.getSubestCommonSuperclass(AngryReflectionUtility.getClassesOf(objectArray));
	//			Object[] typedArray = (Object[])Array.newInstance(scs, objectArray.length);
	//			System.arraycopy(objectArray, 0, typedArray, 0, objectArray.length);
	//			return (E[])typedArray;
	//		}
	//	}
	//
	//
	//	/**
	//	 * Note: you probably should use {@link #toArray(Object, Class)} and specify the component type explicitly if you can xP
	//	 */
	//	@SnapshotValue
	//	@ThrowAwayValue
	//	public static <E> E[] toDynTypedArray(Object... objectArray) throws NonDuckTypableException
	//	{
	//		if (objectArray.length == 0)
	//			throw new NonDuckTypableException("Empty input; no objects to figure out the proper component type with! ;_;"); //pretty much the only time we can't determine the array type at runtime!
	//		else
	//		{
	//			Class scs = AngryReflectionUtility.getSubestCommonSuperclass(ArrayUtilities.concatArrays(AngryReflectionUtility.getClassesOf(objectArray), new Class[]{objectArray.getClass().getComponentType()}));
	//			Object[] typedArray = (Object[])Array.newInstance(scs, objectArray.length);
	//			System.arraycopy(objectArray, 0, typedArray, 0, objectArray.length);
	//			return (E[])typedArray;
	//		}
	//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected static final Iterator Empty_Iterator = new Iterator()
	{
		@Override
		public boolean hasNext()
		{
			return false;
		}
		
		@Override
		public Object next()
		{
			throw new NoSuchElementException();
		}
	};
	
	public static <E> Iterator<E> emptyIterator()
	{
		return Empty_Iterator;
	}
	
	
	
	protected static final Iterable Empty_Iterable = () -> Empty_Iterator;
	
	public static <E> Iterable<E> emptyIterable()
	{
		return Empty_Iterable;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> Iterator<E> singletonIterator(final E singleElement)
	{
		return new Iterator<E>()
		{
			boolean atEnd = false;
			
			@Override
			public boolean hasNext()
			{
				return !this.atEnd;
			}
			
			@Override
			public E next()
			{
				if (this.atEnd)
				{
					throw new NoSuchElementException();
				}
				else
				{
					this.atEnd = true;
					return singleElement;
				}
			}
			
			@Override
			public void remove()
			{
				throw new ReadonlyUnsupportedOperationException();
			}
		};
	}
	
	public static <E> Iterable<E> singletonIterable(final E singleElement)
	{
		return () -> singletonIterator(singleElement);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static <E> Iterator<E> iteratorWithDifferentRemove(Iterator<E> underlying, Runnable remove)
	{
		return new Iterator<E>()
		{
			@Override
			public boolean hasNext()
			{
				return underlying.hasNext();
			}
			
			@Override
			public E next()
			{
				return underlying.next();
			}
			
			@Override
			public void remove()
			{
				remove.run();
			}
		};
	}
	
	
	
	
	public static <E> Iterator<E> iteratorWithDifferentRemoveProvidedContext(Iterator<E> underlying, IndexAndElementProvidingRemove<E> remove)
	{
		return new Iterator<E>()
		{
			int indexOfLastReturnedElement = -1;
			E lastReturnedElement = null;
			
			@Override
			public boolean hasNext()
			{
				return underlying.hasNext();
			}
			
			@Override
			public E next()
			{
				E e = underlying.next();
				this.indexOfLastReturnedElement++;
				this.lastReturnedElement = e;
				return e;
			}
			
			@Override
			public void remove()
			{
				if (this.indexOfLastReturnedElement < 0)
					throw new IllegalStateException();  //next() has not yet been called!      ( throwing this exact exception is what the API specifies, btw ^www^ )
				
				remove.remove(this.lastReturnedElement, this.indexOfLastReturnedElement);
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> Iterator<E> reversedIterator(final ListIterator<E> listIterator)
	{
		return new Iterator<E>()
		{
			@Override
			public boolean hasNext()
			{
				return listIterator.hasPrevious();
			}
			
			@Override
			public E next()
			{
				return listIterator.previous();
			}
			
			@Override
			public void remove()
			{
				listIterator.remove();
			}
		};
	}
	
	public static <E> Iterator<E> reversedIterator(List<E> list)
	{
		return reversedIterator(list.listIterator(list.size()));
	}
	
	public static <E> Iterator<E> reversedIterator(final E[] list)
	{
		return new Iterator<E>()
		{
			int i = list.length;
			
			@Override
			public boolean hasNext()
			{
				return this.i > 0;
			}
			
			@Override
			public E next()
			{
				try
				{
					return list[--this.i];
				}
				catch (ArrayIndexOutOfBoundsException exc)
				{
					throw new NoSuchElementException();
				}
			}
			
			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
	
	
	
	public static void reverseListInPlace(@WritableValue List list)
	{
		//Todo a way not based on random access!!
		
		int n = list.size();
		
		int h = n / 2;  //floor division!  so that if it's even we do exactly half of them, and if it's odd we leave the middle one alone and reverse the rest on both sides of it!
		
		for (int a = 0; a < h; a++)
		{
			int b = n - a - 1;
			swapElements(list, a, b);
		}
	}
	
	
	
	
	//TODO use a view of the list instead!! :D
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> reversed(@ReadonlyValue List<E> list)
	{
		if (list.size() <= 1)
			return list;
		
		List<E> copylist = new ArrayList<>(list);
		reverseListInPlace(copylist);
		return copylist;
	}
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> shuffled(@ReadonlyValue List<E> list)
	{
		if (list.size() <= 1)
			return list;
		
		List<E> copylist = new ArrayList<>(list);
		shuffle(copylist);
		return copylist;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> shuffled(@ReadonlyValue List<E> list, Random r)
	{
		if (list.size() <= 1)
			return list;
		
		List<E> copylist = new ArrayList<>(list);
		shuffle(copylist, r);
		return copylist;
	}
	
	//TODO use a view of the list instead!! :D
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> rotated(@ReadonlyValue List<E> list, int amount)
	{
		if (list.size() <= 1 || amount == 0)
			return list;
		
		List<E> copylist = new ArrayList<>(list);
		rotate(copylist, amount);
		return copylist;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Converters and viewwrappers! :D >
	
	
	public static <E> void push(List<E> self, E x)
	{
		self.add(x);
	}
	
	
	public static <E> E pop(List<E> self, E def)
	{
		if (self.isEmpty())
			return def;
		else
			return self.remove(self.size()-1);
	}
	
	public static <E> E pop(List<E> self)
	{
		return pop(self, null);
	}
	
	public static <E> E poprp(List<E> self) throws NoSuchElementReturnPath
	{
		if (self.isEmpty())
			throw NoSuchElementReturnPath.I;
		else
			return self.remove(self.size()-1);
	}
	
	
	
	@WritableValue
	@ThrowAwayValue
	public static <E> List<E> makelist(UnaryFunctionIntToObject<E> generator, int inclusiveLowBound, int exclusiveHighBound)
	{
		List<E> newlist = new ArrayList<>(exclusiveHighBound - inclusiveLowBound);
		
		for (int index = inclusiveLowBound; index < exclusiveHighBound; index++)
		{
			newlist.add(generator.f(index));
		}
		
		return newlist;
	}
	
	
	
	
	
	
	
	
	
	
	
	public static <I> boolean forAllOrSome(Mapper<? super I, Boolean> mapper, Iterable<I> inputs)
	{
		for (I i : inputs)
		{
			try
			{
				if (!mapper.f(i))
					return false;
			}
			catch (FilterAwayReturnPath exc)
			{
				//Then act as if it wasn't there! \o/
			}
		}
		
		return true;
	}
	
	public static <I> boolean forAnyOrSome(Mapper<? super I, Boolean> mapper, Iterable<I> inputs)
	{
		for (I i : inputs)
		{
			try
			{
				if (mapper.f(i))
					return true;
			}
			catch (FilterAwayReturnPath exc)
			{
				//Then act as if it wasn't there! \o/
			}
		}
		
		return false;
	}
	
	
	
	
	
	public static <I> boolean forAllOrSome(Mapper<? super I, Boolean> mapper, I[] inputs)
	{
		for (I i : inputs)
		{
			try
			{
				if (!mapper.f(i))
					return false;
			}
			catch (FilterAwayReturnPath exc)
			{
				//Then act as if it wasn't there! \o/
			}
		}
		
		return true;
	}
	
	public static <I> boolean forAnyOrSome(Mapper<? super I, Boolean> mapper, I[] inputs)
	{
		for (I i : inputs)
		{
			try
			{
				if (mapper.f(i))
					return true;
			}
			catch (FilterAwayReturnPath exc)
			{
				//Then act as if it wasn't there! \o/
			}
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <I> boolean forAll(Predicate<? super I> predicate, Iterable<I> inputs)
	{
		for (I i : inputs)
		{
			if (!predicate.test(i))
				return false;
		}
		
		return true;
	}
	
	public static <I> boolean forAny(Predicate<? super I> predicate, Iterable<I> inputs)
	{
		for (I i : inputs)
		{
			if (predicate.test(i))
				return true;
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @return iff it passes: if nothing outside the whitelist is present in the collection
	 */
	public static <E> boolean checkWhitelist(Iterable<E> collection, E... whitelist)
	{
		throw new NotYetImplementedException();
	}
	
	public static <E> boolean checkWhitelist(Iterable<E> collection, Iterable<E> whitelist)
	{
		throw new NotYetImplementedException();
	}
	
	
	/**
	 * @return iff it passes: if nothing inside the blacklist is present in the collection
	 */
	public static <E> boolean checkBlacklist(Iterable<E> collection, E... blacklist)
	{
		throw new NotYetImplementedException();
	}
	
	public static <E> boolean checkBlacklist(Iterable<E> collection, Iterable<E> blacklist)
	{
		throw new NotYetImplementedException();
	}
	
	
	/**
	 * @return iff it passes: if everything inside the minlist is present (at least once) in the collection
	 */
	public static <E> boolean checkMinimumlist(Iterable<E> collection, E... minlist)
	{
		throw new NotYetImplementedException();
	}
	
	public static <E> boolean checkMinimumlist(Iterable<E> collection, Iterable<E> minlist)
	{
		throw new NotYetImplementedException();
	}
	
	
	
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue //we may check if it's already uniqued and pass it through, unmodified in the future; who knows! :>
	public static <E> E[] uniqueifyArray(E[] x)
	{
		return (E[])PolymorphicCollectionUtilities.anyToArray(PolymorphicCollectionUtilities.anyToSet(x), x.getClass().getComponentType());
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue //we may check if it's already uniqued and pass it through, unmodified in the future; who knows! :>
	public static List uniqueifyListOrderPreservingOPC(@ReadonlyValue Iterable x)
	{
		if (x == null)
			throw new NullPointerException();
		
		Set s = new HashSet<>();
		
		List l = new ArrayList();
		for (Object e : x)
		{
			if (!s.contains(e))
			{
				l.add(e);
				s.add(e);
			}
		}
		
		return l;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue //we may check if it's already uniqued and pass it through, unmodified in the future; who knows! :>
	public static <E> E[] uniqueifyArrayOrderPreservingOPC(@ReadonlyValue E[] x)
	{
		//		if (x == null)
		//			throw new NullPointerException();
		//		
		//		Set s = PolymorphicCollectionUtilities.anyToSet(x);
		//		
		//		E[] a = (E[])Array.newInstance(x.getClass().getComponentType(), x.length);
		//		int i = 0;
		//		for (E e : x)
		//			if (s.contains(e))
		//				a[i++] = e;
		//		
		//		int size = i;
		//		
		//		if (size < a.length)
		//		{
		//			E[] n = (E[])Array.newInstance(x.getClass().getComponentType(), size);
		//			System.arraycopy(a, 0, n, 0, size);
		//			a = n;
		//		}
		//		
		//		return a;
		
		List r = uniqueifyListOrderPreservingOPC(asList(x));
		return (E[]) r.toArray(Arrays.copyOf(x, r.size()));
	}
	
	
	
	
	
	/**
	 * Analogous to the Unix command 'uniq' :>
	 */
	public static <E> void uniqueifyPresortedIP(@WritableValue Iterable<E> input, EqualityComparator<? super E> equalityComparator)
	{
		Iterator<E> i = input.iterator();
		
		E last = null;
		boolean hasLast = false;
		
		while (i.hasNext())
		{
			E e = i.next();
			
			if (!hasLast)
			{
				last = e;
				hasLast = true;
			}
			else
			{
				if (equalityComparator.equals(e, last))
				{
					i.remove();
				}
				else
				{
					last = e;
				}
			}
		}
	}
	
	/**
	 * Analogous to the Unix command 'uniq' :>
	 */
	public static <E> void uniqueifyPresortedIP(@WritableValue Iterable<E> input)
	{
		uniqueifyPresortedIP(input, getDefaultEqualityComparator());
	}
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> uniqueifiedPresortedToListOPC(Iterable<E> input)
	{
		if (!(input instanceof List) || hasAdjacentDuplicates(input))
			return uniqueifiedPresortedToListOP(input);
		else
			return (List<E>)input;
	}
	
	@SnapshotValue
	public static <E> List<E> uniqueifiedPresortedToListOP(Iterable<E> input)
	{
		return asList(uniqueifiedPresorted(simpleIterator(input)));
	}
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> uniqueifiedPresortedToListOPC(Iterable<E> input, EqualityComparator<? super E> equalityComparator)
	{
		if (!(input instanceof List) || hasAdjacentDuplicates(input, equalityComparator))
			return uniqueifiedPresortedToListOP(input, equalityComparator);
		else
			return (List<E>)input;
	}
	
	@SnapshotValue
	public static <E> List<E> uniqueifiedPresortedToListOP(Iterable<E> input, EqualityComparator<? super E> equalityComparator)
	{
		return asList(uniqueifiedPresorted(simpleIterator(input), equalityComparator));
	}
	
	
	
	
	/**
	 * Analogous to the Unix command 'uniq' :>
	 */
	public static <E> SimpleIterator<E> uniqueifiedPresorted(@WritableValue SimpleIterator<E> input, EqualityComparator<? super E> equalityComparator)
	{
		return new SimpleIterator<E>()
		{
			boolean eof = false;
			E last = null;
			boolean hasLast = false;
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				if (eof)
					throw StopIterationReturnPath.I;
				
				while (true)
				{
					asrt(!eof);
					
					E current;
					try
					{
						current = input.nextrp();
					}
					catch (StopIterationReturnPath rp)
					{
						if (hasLast)
						{
							eof = true;
							E l = last;
							last = null;  //for garbage collection :3
							return l;
						}
						else
						{
							throw rp;
						}
					}
					
					if (hasLast)
					{
						if (!equalityComparator.equals(current, last))
						{
							E l = last;
							last = current;
							return l;
						}
					}
					else
					{
						last = current;
						hasLast = true;
					}
				}
			}
		};
	}
	
	/**
	 * Analogous to the Unix command 'uniq' :>
	 */
	public static <E> SimpleIterator<E> uniqueifiedPresorted(@WritableValue SimpleIterator<E> input)
	{
		return uniqueifiedPresorted(input, getDefaultEqualityComparator());
	}
	
	
	
	
	
	
	
	
	
	
	
	public static <E> void addAll(Collection<E> self, Iterable<? extends E> source)
	{
		if (source instanceof Collection)
			self.addAll((Collection<E>)source);
		else
			for (E e : source)
				self.add(e);
	}
	
	public static <E> void addAll(Collection<E> self, E[] source, int sourceOffset, int count)
	{
		for (int i = 0; i < count; i++)
			self.add(source[sourceOffset+i]);
	}
	
	public static <E> void addAll(Collection<E> self, E[] source)
	{
		addAll(self, source, 0, source.length);
	}
	
	public static <E> void addAll(Collection<E> self, List<? extends E> source, int sourceOffset, int count)
	{
		for (int i = 0; i < count; i++)
			self.add(source.get(sourceOffset+i));
	}
	
	public static <E> E getExtantInstance(Collection<E> collection, E possiblyEquivalentButDifferentInstance)
	{
		if (possiblyEquivalentButDifferentInstance == null)
			return null; //only one instance equivalent to that! xD
		
		if (collection instanceof CollectionWithGetExtantInstanceNatural)
			return ((CollectionWithGetExtantInstanceNatural<E>)collection).getExtantInstance(possiblyEquivalentButDifferentInstance);
		
		//Same fallback for lists as sets (but with lists there really isn't any other way xP )
		for (E e : collection)
			if (eq(possiblyEquivalentButDifferentInstance, e))
				return e;
		return null;
	}
	
	public static <E> E getExtantInstance(Collection<E> collection, E possiblyEquivalentButDifferentInstance, EqualityComparator<E> equalityComparator)
	{
		if (possiblyEquivalentButDifferentInstance == null)
			return null; //only one instance equivalent to that! xD
		
		if (collection instanceof CollectionWithGetExtantInstanceSpecified)
			return ((CollectionWithGetExtantInstanceSpecified<E>)collection).getExtantInstance(possiblyEquivalentButDifferentInstance, equalityComparator);
		
		//Same fallback for lists as sets (but with lists there really isn't any other way xP )
		for (E e : collection)
			if (equalityComparator.equals(possiblyEquivalentButDifferentInstance, e))
				return e;
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//SETS AHAHA (X>)
	
	
	/**
	 * Subset *or* Equal(Equivalent) :>
	 * + If 'sub' is empty this always returns true :>
	 */
	public static boolean isSubset(Set sub, Set sup)
	{
		for (Object e : sub)
			if (!sup.contains(e))
				return false;
		return true;
	}
	
	
	public static <E> Set<E> intersection(Set<E>... sets) //AND (2:8), 3:128, 4:32768, ...
	{
		if (sets.length == 0)
			return new HashSet<E>();
		
		else if (sets.length == 1)
			return new HashSet<E>(sets[0]);
		
		else
		{
			Set<E> output = new HashSet<E>();
			
			for (E e : sets[0])
			{
				boolean all = true;
				{
					for (int setIndex = 1; all && setIndex < sets.length; setIndex++)
					{
						all &= sets[setIndex].contains(e);
					}
				}
				
				if (all)
				{
					output.add(e);
				}
			}
			
			return output;
		}
	}
	
	public static <E> E intersectionSingletonOrNull(@NonnullElements Set<E> a, @NonnullElements Set<E> b) throws NotSingletonException
	{
		if (b.size() < a.size())
		{
			Set<E> c = b;
			b = a;
			a = c;
		}
		
		//now a is the smaller :3
		
		E i = null;
		
		for (E e : a)
		{
			if (b.contains(e))
			{
				if (i != null)
					throw new NotSingletonException();
				else
					i = e;
			}
		}
		
		return i;
	}
	
	/**
	 * Like {@link #intersectionSingletonOrNull(Set, Set)} but allows the {@link Set}s to contain nulls :3
	 */
	public static <E> Maybe<E> intersectionSingletonOrNothing(@NullableElements Set<E> a, @NullableElements Set<E> b) throws NotSingletonException
	{
		if (b.size() < a.size())
		{
			Set<E> c = b;
			b = a;
			a = c;
		}
		
		//now a is the smaller :3
		
		boolean has = false;
		E i = null;
		
		for (E e : a)
		{
			if (b.contains(e))
			{
				if (has)
					throw new NotSingletonException();
				else
				{
					i = e;
					has = true;
				}
			}
		}
		
		return has ? just(i) : nothing();
	}
	
	public static <E> boolean containsAny(Collection<E> a, Collection<E> b) throws NotSingletonException
	{
		//Todo make a trait predicate for it!
		
		if (a instanceof Set == b instanceof Set)  //Todo a trait predicate for containsIsFast beyond just Set's!  (and for Set's which have a slow .contains() to override and give false! like ListBackedSet!)
		{
			if (b.size() < a.size())
			{
				Collection<E> c = b;
				b = a;
				a = c;
			}
		}
		else if (a instanceof Set)
		{
			Collection<E> c = b;
			b = a;
			a = c;
		}
		
		//now b has a faster .contains() and/or a is the smaller :3
		
		for (E e : a)
			if (b.contains(e))
				return true;
		
		return false;
	}
	
	
	public static <E> Set<E> anysection(Set<E>... sets) //AND (2:8), 3:200, 4:65256, ...
	{
		if (sets.length == 0)
			return emptySet();
		
		else if (sets.length == 1)
			return emptySet();
		
		else
		{
			Set<E> output = new HashSet<E>();
			
			for (E e : unionV(sets))
			{
				int n = 0;
				{
					for (int setIndex = 0; n < 2 && setIndex < sets.length; setIndex++)
					{
						n += sets[setIndex].contains(e) ? 1 : 0;
					}
				}
				
				if (n >= 2)
				{
					output.add(e);
				}
			}
			
			return output;
		}
	}
	
	/**
	 * aka "symmetric difference"
	 */
	public static <E> Set<E> symdiff(Set<E> a, Set<E> b) //XOR (6)
	{
		Set output = new HashSet();
		
		for (Object e : a)
			if (!b.contains(e))
				output.add(e);
		
		for (Object e : b)
			if (!a.contains(e))
				output.add(e);
		
		return output;
	}
	
	
	@ReadonlyValue
	public static <K, V> Map<K, V> unionManyMaps(@ReadonlyValue Iterable<Map<K, V>> maps) throws AlreadyExistsException
	{
		return unionManyMapsFiltering(maps, (k, v) -> true);
	}
	
	@ReadonlyValue
	public static <K, V> Map<K, V> unionManyMapsFiltering(@ReadonlyValue Iterable<Map<K, V>> maps, MapEntryPredicate<K, V> filter) throws AlreadyExistsException
	{
		int maxSize;
		{
			maxSize = 0;
			for (Map<K, V> m : maps)
				maxSize += m.size();
		}
		
		Map<K, V> r = new HashMap<>(maxSize);
		
		for (Map<K, V> m : maps)
		{
			for (Entry<K, V> e : m.entrySet())
			{
				if (filter.test(e.getKey(), e.getValue()))
					putNewUniqueMandatory(r, e.getKey(), e.getValue());
			}
		}
		
		return r;
	}
	
	@ReadonlyValue
	public static <K, V> Map<K, V> unionMaps(@ReadonlyValue Map<K, V> a, @ReadonlyValue Map<K, V> b) throws AlreadyExistsException
	{
		if (a.isEmpty())
			return b;
		
		return unionMapsFilteringSecond(a, b, (k, v) -> true);
	}
	
	@ReadonlyValue
	public static <K, V> Map<K, V> unionMapsFilteringSecond(@ReadonlyValue Map<K, V> a, @ReadonlyValue Map<K, V> b, MapEntryPredicate<K, V> filter) throws AlreadyExistsException
	{
		if (b.isEmpty())
			return a;
		
		Map<K, V> r = new HashMap<>(a.size() + b.size());
		r.putAll(a);
		
		//r.putAll(b);
		
		for (Entry<K, V> e : b.entrySet())
		{
			if (filter.test(e.getKey(), e.getValue()))
				putNewUniqueMandatory(r, e.getKey(), e.getValue());
		}
		
		return r;
	}
	
	@ReadonlyValue
	public static <K, V> Map<K, Set<V>> unionGeneralMaps(@ReadonlyValue Map<K, ? extends Iterable<V>> a, @ReadonlyValue Map<K, ? extends Iterable<V>> b)
	{
		Map<K, Set<V>> r = new HashMap<>(a.size() + b.size());
		putAllGeneralMaps(r, a);
		putAllGeneralMaps(r, b);
		return r;
	}
	
	/**
	 * Aka unionGeneralMapsIP()
	 * XD
	 */
	public static <K, V> void putAllGeneralMaps(@ReadonlyValue Map<K, Set<V>> acceptor, @ReadonlyValue Map<K, ? extends Iterable<V>> donor)
	{
		for (Entry<K, ? extends Iterable<V>> e : donor.entrySet())
		{
			addAll(getOrCreate(acceptor, e.getKey(), () -> new HashSet<V>()), e.getValue());
		}
	}
	
	public static <E> Set<E> union(Iterable<E> a, Iterable<E> b) //OR (14)
	{
		return unionV(a, b);
	}
	
	public static <E> Set<E> unionV(Iterable<E>... sets) //OR (14)
	{
		Set<E> output = new HashSet<>();
		
		for (Iterable<E> c : sets)
			addAll(output, c);
		
		return output;
	}
	
	public static <E> Set<E> unionMany(Iterable<? extends Iterable<E>> sets) //OR (14)
	{
		Set<E> output = new HashSet<>();
		
		for (Iterable<E> c : sets)
			addAll(output, c);
		
		return output;
	}
	
	public static <I, O> Set<O> unionManyMapping(Mapper<I, Iterable<O>> mapper, Iterable<I> input) //OR (14)
	{
		Set<O> output = new HashSet<>();
		
		for (I i : input)
		{
			Iterable<O> o;
			try
			{
				o = mapper.f(i);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			addAll(output, o);
		}
		
		return output;
	}
	
	
	public static <E> Set<E> setdiff(Iterable<E> minuend, Collection<E> subtrahendToTakeAway) //umm gate number 2 whatever we call that! XD      ¬(a ⇒ b)  XD
	{
		//Todo support for removeAll() being used when copying the entire minuend into 'output' is actually more performant!
		
		Set output = new HashSet();
		
		for (Object e : minuend)
			if (!subtrahendToTakeAway.contains(e))
				output.add(e);
		
		return output;
	}
	
	
	
	
	
	public static <A, B, O> Set<O> cartesianProductMapped(Iterable<A> aSet, Iterable<B> bSet, BinaryFunction<A, B, O> mapper)
	{
		Set<O> oSet = new HashSet<>();
		
		for (A a : aSet)
		{
			for (B b : bSet)
			{
				O o = mapper.f(a, b);
				oSet.add(o);
			}
		}
		
		return oSet;
	}
	
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> cartesianListProductManyToTableOP(List<List<E>> inputs)
	{
		int width = inputs.size();
		
		if (width == 0)
			return newTable();
		
		int height = productMapping32(i -> i.size(), inputs);
		
		if (height == 0)
			return newTable();
		
		
		SimpleTable<E> out = newTableNullfilled(width, height);
		
		
		int[] exclusiveHighBounds;
		{
			exclusiveHighBounds = new int[width];
			
			for (int i = 0; i < width; i++)
				exclusiveHighBounds[i] = inputs.get(i).size();
		}
		
		
		int[] indexesForColumns = new int[width];
		int rowIndex = 0;
		
		while (true)
		{
			for (int columnIndex = 0; columnIndex < width; columnIndex++)
				out.setCellContents(columnIndex, rowIndex, inputs.get(columnIndex).get(indexesForColumns[columnIndex]));
			
			if (!increment(indexesForColumns, 0, exclusiveHighBounds))
				break;
			
			rowIndex++;
		}
		
		return out;
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <K, V> Map<K, V> unionDisjointMapsOPC(Map<K, V> a, Map<K, V> b)
	{
		if (a.isEmpty())
			return b;
		else if (b.isEmpty())
			return a;
		else
			return unionDisjointMapsOP(a, b);
	}
	
	@ThrowAwayValue
	public static <K, V> Map<K, V> unionDisjointMapsOP(Map<K, V> a, Map<K, V> b)
	{
		Map<K, V> o = new HashMap<>(a);
		
		for (Entry<K, V> e : b.entrySet())
			putNewMandatory(o, e.getKey(), e.getValue());
		
		return o;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> Collection<E> unionMultisets(Iterable<E> a, Iterable<E> b) //OR (14)
	{
		return unionV(a, b);
	}
	
	public static <E> Collection<E> unionMultisetsV(Iterable<E>... sets) //OR (14)
	{
		Collection<E> output = new ArrayList<>();
		
		for (Iterable<E> c : sets)
			addAll(output, c);
		
		return output;
	}
	
	public static <E> Collection<E> unionMultisetsMany(Iterable<? extends Iterable<E>> sets) //OR (14)
	{
		Collection<E> output = new ArrayList<>();
		
		for (Iterable<E> c : sets)
			addAll(output, c);
		
		return output;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//////// Things for usage with equality comparators! :D ////////
	
	/**
	 * Returns the default-equality comparator for a collection.
	 * Returns {@link BasicObjectUtilities#getDefaultEqualityComparator()} for grandfathered collections :>
	 */
	public static <E> EqualityComparator<E> getBoundCollectionEqualityComparator(Collection<E> collection)
	{
		if (collection instanceof CollectionWithBoundEqualityComparator)
		{
			return ((CollectionWithBoundEqualityComparator)collection).getEqualityComparator();
		}
		else
		{
			return getDefaultEqualityComparator();
		}
	}
	
	
	
	public static <E> boolean removeEqC(Collection<E> collection, E element, EqualityComparator<E> equalityComparator)
	{
		if (collection instanceof CollectionWithInvocationProvideableEqualityComparators)
			return ((CollectionWithInvocationProvideableEqualityComparators)collection).remove(element, equalityComparator);
		else
			return removeMatching(e -> equalityComparator.equals(element), collection);
	}
	
	public static <E> boolean containsEqC(Collection<E> collection, E element, EqualityComparator<E> equalityComparator)
	{
		if (collection instanceof CollectionWithInvocationProvideableEqualityComparators)
			return ((CollectionWithInvocationProvideableEqualityComparators)collection).remove(element, equalityComparator);
		else
		{
			Iterator<E> i = collection.iterator();
			
			E e = null;
			while (i.hasNext())
			{
				e = i.next();
				
				if (equalityComparator.equals(e, element))
				{
					return true;
				}
			}
			
			return false;
		}
	}
	
	//TODO more! :D
	
	
	
	
	
	
	
	
	public static <E> List<List<E>> splitlist(List<E> list, Predicate<E> delimiter, int limit, WhatToDoWithEmpties whatToDoWithEmpties)
	{
		if (whatToDoWithEmpties == null) throw new NullPointerException();
		
		
		boolean leaveInEmpties = whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties;
		
		List<List<E>> chunks = new ArrayList<>();
		int chunkStart = 0;
		for (int i = 0; i < list.size() && (limit == -1 || chunks.size() < limit); i++)
		{
			if (delimiter.test(list.get(i)))
			{
				if (leaveInEmpties || i - chunkStart != 0)
				{
					chunks.add(new ArrayList<>(list.subList(chunkStart, i)));
				}
				
				chunkStart = i + 1; //Token doesn't include the delimiter
			}
		}
		
		chunks.add(new ArrayList<>(list.subList(chunkStart, list.size())));
		
		return chunks;
	}
	
	
	public static <E> List<List<E>> splitlist(List<E> list, Predicate<E> delimiter, int limit)
	{
		return splitlist(list, delimiter, limit, WhatToDoWithEmpties.LeaveInEmpties);
	}
	
	public static <E> List<List<E>> splitlist(List<E> list, Predicate<E> delimiter)
	{
		return splitlist(list, delimiter, -1);
	}
	
	
	
	
	
	
	
	
	public static <O, D extends O> List<O> joinlists(Iterable<? extends Iterable<? extends O>> lists, D delimiter)
	{
		List<O> r = new ArrayList<>();
		
		boolean first = true;
		for (Iterable<? extends O> list : lists)
		{
			if (first)
				first = false;
			else
				r.add(delimiter);
			
			addAll(r, list);
		}
		
		return r;
	}
	
	
	
	public static <O, D extends O> List<O> joinSingletonLists(Iterable<? extends O> list, D delimiter)
	{
		List<O> r = new ArrayList<>();
		
		boolean first = true;
		for (O e : list)
		{
			if (first)
				first = false;
			else
				r.add(delimiter);
			
			r.add(e);
		}
		
		return r;
	}
	
	
	
	
	
	
	
	
	
	
	public static <E> Iterator<E> unmodifiableIterator(final Iterator<E> underlying)
	{
		return new Iterator<E>()
		{
			@Override
			public boolean hasNext()
			{
				return underlying.hasNext();
			}
			
			@Override
			public E next()
			{
				return underlying.next();
			}
			
			@Override
			public void remove()
			{
				throw new ReadonlyUnsupportedOperationException();
			}
		};
	}
	
	
	
	public static <E> ListIterator<E> unmodifiableListIterator(final ListIterator<E> underlying)
	{
		return new ListIterator<E>()
		{
			@Override
			public boolean hasNext()
			{
				return underlying.hasNext();
			}
			
			@Override
			public E next()
			{
				return underlying.next();
			}
			
			@Override
			public boolean hasPrevious()
			{
				return underlying.hasPrevious();
			}
			
			@Override
			public E previous()
			{
				return underlying.previous();
			}
			
			@Override
			public int nextIndex()
			{
				return underlying.nextIndex();
			}
			
			@Override
			public int previousIndex()
			{
				return underlying.previousIndex();
			}
			
			
			
			@Override
			public void remove()
			{
				throw new ReadonlyUnsupportedOperationException();
			}
			
			@Override
			public void set(E e)
			{
				throw new ReadonlyUnsupportedOperationException();
			}
			
			@Override
			public void add(E e)
			{
				throw new ReadonlyUnsupportedOperationException();
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Don't implement statically readable/writable etc.; because they mights want to keep it runtime-reinterpretables :>
	// /not sure about this whole further-subclass layer.. x>
	
	//	public static interface StaticallyReadableMap {}
	//	public static interface StaticallyUnreadableMap {}
	
	//	public static interface StaticallyWriteableMap {}
	//	public static interface StaticallyUnwriteableMap {}
	
	
	
	//Taken out because excessively complicated and (I think) a bit unwarranted with a nice happy inlining JIT :>
	
	//	public static interface StaticallyWriteableCollection {}
	//	public static interface StaticallyUnwriteableCollection {}
	
	//	public static interface StaticallyReadableCollection {}
	//	public static interface StaticallyUnreadableCollection {}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean defaultContainsAll(Collection self, Collection param)
	{
		boolean all = true;
		for (Object e : param)
			all &= self.contains(e);
		return all;
	}
	
	public static <E> boolean defaultAddAll(Collection<E> self, Collection<? extends E> param)
	{
		boolean any = false;
		for (E e : param)
			any |= self.add(e);
		return any;
	}
	
	public static <E> boolean defaultRemoveAll(Collection<E> self, Collection param)
	{
		if (param == self)
		{
			boolean any = !self.isEmpty();
			if (any)
				self.clear();
			return any;
		}
		else
		{
			boolean any = false;
			for (Object e : param)
				any |= self.remove(e);
			return any;
		}
	}
	
	public static <E> boolean defaultRetainAll(Collection<E> self, Collection param)
	{
		boolean any = false;
		for (E e : self)
			if (!param.contains(e))
				any |= self.remove(e);
		return any;
	}
	
	
	public static <E> boolean defaultRemoveIf(@Nonnull Collection<E> self, @Nonnull Predicate<? super E> filter)
	{
		requireNonNull(self);
		requireNonNull(filter);
		
		boolean removedAtLeastOne = false;
		Iterator<E> i = self.iterator();
		
		while (i.hasNext())
		{
			E e = i.next();
			
			if (filter.test(e))
			{
				i.remove();
				removedAtLeastOne = true;
			}
		}
		
		return removedAtLeastOne;
	}
	
	
	public static <E> boolean defaultRemoveIfWithStopSignal(Collection<E> c, UnaryFunction<? super E, Either<Boolean, Boolean>> filter)
	{
		boolean didAnything = false;
		
		Iterator<E> i = c.iterator();
		while (i.hasNext())
		{
			Either<Boolean, Boolean> r = filter.f(i.next());
			
			boolean b = (Boolean)r.getValue();
			if (b)
			{
				i.remove();
				didAnything = true;
			}
			
			if (r.isB())
				break;
		}
		
		return didAnything;
	}
	
	
	/**
	 * you prolly want to just discard its return value x3
	 */
	public static <E> boolean defaultClear(Collection<E> self)
	{
		boolean any = false;
		
		Iterator<E> i = self.iterator();
		while (i.hasNext())
		{
			i.next();
			i.remove();
		}
		
		return any;
	}
	
	
	
	
	
	
	
	public static <E> boolean defaultSizelessListsEquivalent(@Nullable Iterable<? extends E> a, @Nullable Iterable<? extends E> b, EqualityComparator<E> equalityComparator)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		
		Iterator<? extends E> ia = a.iterator();
		Iterator<? extends E> ib = b.iterator();
		
		return defaultRemaindersOfIteratorsEquivalent(ia, ib, equalityComparator);
	}
	
	public static <E> boolean defaultRemaindersOfIteratorsEquivalent(@Nonnull Iterator<? extends E> ia, @Nonnull Iterator<? extends E> ib, EqualityComparator<E> equalityComparator)
	{
		requireNonNull(ia);
		requireNonNull(ib);
		
		while (true)
		{
			boolean ahn = ia.hasNext();
			boolean bhn = ib.hasNext();
			
			if (ahn != bhn) return false;
			
			if (ahn)
			{
				assert bhn;
				
				E an = ia.next();
				E bn = ib.next();
				
				if (!equalityComparator.equals(an, bn))
					return false;
			}
			else
			{
				break;
			}
		}
		
		return true;
	}
	
	public static <E> boolean defaultSizelessListsEquivalent(Iterable<E> a, Iterable<E> b)
	{
		return defaultSizelessListsEquivalent(a, b, (EqualityComparator)getDefaultEqualityComparator());
	}
	
	
	
	
	
	
	
	public static <E> boolean defaultListsEquivalentDeep(@Nullable List<? extends E> a, @Nullable List<? extends E> b)
	{
		return defaultListsEquivalent(a, b, (x, y) -> x instanceof List && y instanceof List ? defaultListsEquivalentDeep((List)x, (List)y) : eq(x, y));
	}
	
	public static <E> boolean defaultListsEquivalent(@Nullable List<? extends E> a, @Nullable List<? extends E> b)
	{
		return defaultListsEquivalent(a, b, getDefaultEqualityComparator());
	}
	
	public static <E> boolean defaultListsEquivalent(@Nullable List<? extends E> a, @Nullable List<? extends E> b, EqualityComparator<E> equalityComparator)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		
		return defaultListsEquivalent(a.size(), a, b.size(), b, equalityComparator);
	}
	
	public static <E> boolean defaultListsEquivalent(int aSize, @Nonnull Iterable<? extends E> a, int bSize, @Nonnull Iterable<? extends E> b, EqualityComparator<E> equalityComparator)
	{
		if (aSize != bSize)
			return false;
		
		if (arbitrary(aSize, bSize) == 0)
			return true;
		
		
		Iterator<? extends E> ia = a.iterator();
		Iterator<? extends E> ib = b.iterator();
		
		while (true)
		{
			boolean ahn = ia.hasNext();
			boolean bhn = ib.hasNext();
			
			if (ahn != bhn) throw new ConcurrentModificationException("sizes were checked to be equal, but iterators produce unequal numbers of elements; either something is modifying the list un-thread-safely!, or it's just a really big bug in the list code XD''");
			
			if (ahn)
			{
				assert bhn;
				
				E an = ia.next();
				E bn = ib.next();
				
				if (!equalityComparator.equals(an, bn))
					return false;
			}
			else
			{
				break;
			}
		}
		
		return true;
	}
	
	
	
	
	/**
	 * @param a The one that will be iterated over (and should have fast iteration (eg, {@link ArrayList}))
	 * @param b The one that {@link Set#contains(Object)} will be called on and should have a fast implementation for (eg, {@link HashSet})  :3
	 */
	public static <E> boolean defaultSetsEquivalent(Collection<? extends E> a, Set<? extends E> b)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		
		int as = a.size();
		int bs = b.size();
		
		if (as != bs)
			return false;
		
		if (arbitrary(as, bs) == 0)
			return true;
		
		
		for (E an : a)
		{
			if (!b.contains(an))
				return false;
		}
		
		return true;
	}
	
	
	
	
	
	public static <E> boolean extraCheckingSetsEquivalent(Set<? extends E> a, Set<? extends E> b)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		
		int as = a.size();
		int bs = b.size();
		
		if (as != bs)
			return false;
		
		if (arbitrary(as, bs) == 0)
			return true;
		
		
		Iterator<? extends E> ia = a.iterator();
		Iterator<? extends E> ib = b.iterator();
		
		while (true)
		{
			boolean ahn = ia.hasNext();
			boolean bhn = ib.hasNext();
			
			if (ahn != bhn) throw new ConcurrentModificationException("sizes were checked to be equal, but iterators produce unequal numbers of elements; either something is modifying the set un-thread-safely!, or it's just a really big bug in the set code XD''");
			
			if (ahn)
			{
				assert bhn;
				
				E an = ia.next();
				E bn = ib.next();
				
				if (!b.contains(an))
					return false;
				if (!a.contains(bn))
					return false;
			}
			else
			{
				break;
			}
		}
		
		return true;
	}
	
	
	
	
	
	public static <E> boolean defaultSetsEquivalent(Collection<? extends E> a, Collection<? extends E> b, EqualityComparator<E> eq)
	{
		//Todo test that it's functionally equivalent to this!!: return defaultMultiSetsEquivalent_SmallNaive(a, b, eq);
		
		
		if (a == b) return true;
		if (a == null || b == null) return false;
		
		int as = a.size();
		int bs = b.size();
		
		if (as != bs)
			return false;
		
		if (as == 0)
		{
			assert bs == 0;
			return true;
		}
		
		for (E e : a)
		{
			if (!contains(ee -> eq.equals(e, ee), b))
				return false;
			
			assert(count(x -> eq.equals(x, e), a) == 1);
			assert(count(x -> eq.equals(x, e), b) == 1);
		}
		
		return true;
	}
	
	
	
	
	
	
	public static <E> boolean defaultMultiSetsEquivalent_SmallNaive(Collection<? extends E> a, Collection<? extends E> b)
	{
		return defaultMultiSetsEquivalent_SmallNaive(a, b, getDefaultEqualityComparator());
	}
	
	public static <E> boolean defaultMultiSetsEquivalent_SmallNaive(Collection<? extends E> a, Collection<? extends E> b, EqualityComparator<E> eq)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		
		int as = a.size();
		int bs = b.size();
		
		if (as != bs)
			return false;
		
		if (as == 0)
		{
			assert bs == 0;
			return true;
		}
		
		for (E e : a)
		{
			int ca = count(x -> eq.equals(x, e), a);
			int cb = count(x -> eq.equals(x, e), b);
			
			if (ca != cb)
			{
				return false;
			}
		}
		
		return true;
	}
	
	
	
	
	/* *
	 * Where ordering is important :>
	 * /
	public static <E> int defaultListHashCode(Iterable<E> list)
	{
		//Note: I don't really know about hash codes; there is probably a better way to make this hashy and all pseudorandom, but I don't know of a good super-fast way to just rapidly hash a counter offhand; so ohwells XP
		
		int hashCode = 0;
		
		int i = 0;
		
		for (E e : list)
		{
			hashCode += hashNT(e) ^ i;
			i++;
		}
		
		hashCode += i; //(i is now 'size' ^_^ )
		
		return hashCode;
	}
	 */
	
	/* *
	 * Where ordering is *not* important :>
	 * /
	public static <E> int defaultSetHashCode(Iterable<E> set)
	{
		//Note: I don't really know about hash codes; there is probably a better way to make this hashy and all pseudorandom, but I don't know of a good super-fast way to just rapidly hash a counter offhand; so ohwells XP
		
		int hashCode = 0;
		
		int size = 0;
		
		for (E e : set)
		{
			hashCode += hashNT(e);
			size++;
		}
		
		hashCode += size; //(i is now 'size' ^_^ )
		
		return hashCode;
	}
	 */
	
	
	
	
	
	
	
	
	
	
	
	public static <K, V> boolean defaultMapsEquivalent(Map<? extends K, ? extends V> a, Map<? extends K, ? extends V> b, EqualityComparator<V> valuesEqualityComparator)
	{
		if (!eqvSets(a.keySet(), b.keySet()))  //this'll take care of the quick-test for if the sizes aren't equal :33
			return false;
		
		Set tokenKeys = a.keySet(); //arbitrary since they're the same!
		
		for (Object key : tokenKeys)
		{
			V valueA = a.get(key);
			V valueB = b.get(key);
			
			if (!valuesEqualityComparator.equals(valueA, valueB))
				return false;
		}
		
		return true;
	}
	
	public static <K, V> boolean defaultMapsEquivalent(Map<? extends K, ? extends V> a, Map<? extends K, ? extends V> b)
	{
		return defaultMapsEquivalent(a, b, (EqualityComparator)getDefaultEqualityComparator());
	}
	
	
	
	
	public static <K, V> boolean defaultMapsEquivalent(Map<? extends K, ? extends V> a, Map<? extends K, ? extends V> b, EqualityComparator<K> keysEqualityComparator, EqualityComparator<V> valuesEqualityComparator)
	{
		if (!defaultSetsEquivalent(a.keySet(), b.keySet(), keysEqualityComparator))  //this'll take care of the quick-test for if the sizes aren't equal :33
			return false;
		
		Set<? extends K> aks = a.keySet();
		Set<? extends K> bks = b.keySet();
		
		for (K keyA : aks)
		{
			K keyB = (K)lookupThrowingIfAbsentOrMultiple(keyA, (Set)bks, (EqualityComparator)keysEqualityComparator);
			
			assert keysEqualityComparator.equals(keyA, keyB);
			
			V valueA = a.get(keyA);
			V valueB = b.get(keyB);
			
			if (!valuesEqualityComparator.equals(valueA, valueB))
				return false;
		}
		
		return true;
	}
	
	
	public static <E> E lookupThrowingIfAbsentOrMultiple(E token, Collection<? extends E> c, EqualityComparator<? super E> eq)
	{
		E r = null;
		boolean has = false;
		
		for (E e : c)
		{
			if (eq.equals(e, token))
			{
				if (has)
					throw new NotSingletonException("more than one element! o_o");
				
				r = e;
				has = true;
			}
		}
		
		if (!has)
			throw new NotSingletonException("no elements! ><");
		
		return r;
	}
	
	
	
	
	
	
	
	/**
	 * Where ordering is important :>
	 * 
	 * Just the standard algorithm straight out of {@link List#hashCode()} ^_^
	 * 
	 * int hashCode = 1;
	 * for (E e : list)
	 * 		hashCode = 31*hashCode + (e == null ? 0 : e.hashCode());
	 */
	public static <E> int defaultListHashCode(Iterable<E> list)
	{
		int hashCode = 1;
		for (E e : list)
			hashCode = 31*hashCode + (e == null ? 0 : e.hashCode());
		
		return hashCode;
	}
	
	
	/**
	 * Where ordering is *not* important :>
	 * 
	 * 
	 * Just the standard algorithm straight out of {@link Set#hashCode()} ^_^
	 * 
	 * int hashCode = 0;
	 * for (E e : set)
	 * 		hashCode += (e == null ? 0 : e.hashCode());
	 */
	public static <E> int defaultMultiSetHashCode(Iterable<E> collection)
	{
		int hashCode = 0;
		for (E e : collection)
			hashCode += (e == null ? 0 : e.hashCode());
		
		return hashCode;
	}
	
	public static <E> int defaultSetHashCode(Set<E> set)
	{
		return defaultMultiSetHashCode(set);
	}
	
	
	
	
	
	
	/**
	 * Just the standard algorithm straight out of {@link Map#hashCode()} ^_^
	 * 
	 * int hashCode = 0;
	 * for (Entry<K, V> e : map.entrySet())
	 * 		hashCode += e.hashCode();
	 */
	public static <K, V> int defaultMapHashCode(Map<K, V> map)
	{
		int hashCode = 0;
		for (Entry<K, V> e : map.entrySet())
			hashCode += e.hashCode();
		return hashCode;
	}
	
	
	
	
	
	
	
	
	
	public static <E> boolean defaultContains(Iterable<E> list, Object item, EqualityComparator<E> equalityComparator)
	{
		for (E e : list)
			if (((EqualityComparator)equalityComparator).equals(item, e))
				return true;
		return false;
	}
	
	public static <E> boolean defaultContains(Iterable<E> list, Object item)
	{
		return defaultContains(list, item, (EqualityComparator)getDefaultEqualityComparator());
	}
	
	
	
	
	public static <E> E defaultGetRandomAccess(Iterable<E> list, @Nonnegative int index)
	{
		requireNonNull(list);
		requireNonNegative(index);
		
		int i = 0;
		for (E e : list)
		{
			if (i == index)
				return e;
			i++;
		}
		
		throw new IndexOutOfBoundsException("The iterable yielded only "+i+" elements, but we wanted the "+index+"th one (starting at 0).");
	}
	
	
	
	public static <E> int defaultListIndexOf(Iterable<E> list, Object item, EqualityComparator<E> equalityComparator)
	{
		int i = 0;
		for (E e : list)
		{
			if (((EqualityComparator)equalityComparator).equals(item, e))
				return i;
			i++;
		}
		
		return -1;
	}
	
	public static <E> int defaultListIndexOf(Iterable<E> list, Object item)
	{
		return defaultListIndexOf(list, item, (EqualityComparator)getDefaultEqualityComparator());
	}
	
	
	
	
	
	public static <E> int defaultListLastIndexOf(List<E> list, Object item, EqualityComparator<E> equalityComparator)
	{
		int len = list.size();
		
		ListIterator<E> li = list.listIterator(len);
		
		int i = len;
		E e = null;
		while (li.hasPrevious())
		{
			i--;
			e = li.previous();
			
			if (((EqualityComparator)equalityComparator).equals(item, e))
				return i;
		}
		
		return -1;
	}
	
	public static <E> int defaultListLastIndexOf(List<E> list, Object item)
	{
		return defaultListIndexOf(list, item, (EqualityComparator)getDefaultEqualityComparator());
	}
	
	
	
	
	
	
	
	
	public static <E> boolean defaultListAddAll(List<E> list, int startingIndex, Collection<? extends E> source)
	{
		boolean changed = false;
		
		int i = startingIndex;
		for (E e : source)
		{
			list.add(i, e); //no boolean return value; it always returns modifies the list, or throws exception :>
			changed = true;
			
			i++;
		}
		
		return changed;
	}
	
	public static <E> boolean defaultRemoveBySearch(Iterable<E> list, Object item, EqualityComparator<E> equalityComparator)
	{
		Iterator<E> i = list.iterator();
		
		E e = null;
		while (i.hasNext())
		{
			e = i.next();
			
			if (((EqualityComparator)equalityComparator).equals(item, e))
			{
				i.remove();
				return true;
			}
		}
		
		return false;
	}
	
	public static <E> boolean defaultRemoveBySearch(Iterable<E> list, Object item)
	{
		return defaultRemoveBySearch(list, item, getDefaultEqualityComparator());
	}
	
	
	
	
	public static Object[] defaultToArray(Collection<?> self)
	{
		//return defaultToArray(self, new Object[0]);
		
		int len = self.size();
		
		Object[] array = new Object[len];
		
		int i = 0;
		for (Object e : self)
		{
			array[i] = e;
			i++;
		}
		
		if (i != len)
			throw new ImpossibleException("Sizes didn't match up!!!");
		
		return array;
	}
	
	
	//Todo is this up to that crazy old definition Java has? x'D
	/**
	 * Uses iterator :>
	 */
	public static <E, T> T[] defaultToArray(Collection<E> self, T[] array)
	{
		int len = self.size();
		
		if (array.length < len)
			array = (T[])Array.newInstance(array.getClass().getComponentType(), len);
		
		int i = 0;
		for (E e : self)
		{
			array[i] = (T)e;
			i++;
		}
		
		if (i != len)
			throw new ImpossibleException("Sizes didn't match up!!!");
		
		return array;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO support caching the Entry view objects XP
	//	public static abstract class AlternateAbstractMap<K, V>
	//	implements CollectionUtilities.RuntimeReadabilityMap, CollectionUtilities.RuntimeWriteabilityMap, Map<K, V>, MapWithBoundKeyEqualityComparator<K>, MapWithBoundValueEqualityComparator<V>
	//	{
	//		protected final Set<K> actualKeySet; //doesn't have to redirect messages back to this map (like remove()) ^_^
	//		protected final Set<K> keySetView; //simply delegates to this map and to actualKeySet :>
	//
	//		protected final Set<V> valuesCollectionView;
	//		protected final Set<Entry<K, V>> entrySetView;
	//
	//		/**
	//		 * The key set is used as data, not required to be readonly if the map's readonly, or to redirect modification requests back to the map! :D
	//		 * It is required to be live though; as in, there isn't any mechanism to re-get it if the underlying set of keys changes ;_;
	//		 * You'll just have to make sure this reflects that (or it's cached and it's not supposed to; although that's kind of redefining the notion of 'actual underlying key set' y'know ;> )
	//		 * Note: if you want it to be a cached version of an occasionally-updated set, maybies look into {@link CachingSet} ^v^
	//		 */
	//		public AlternateAbstractMap(Set<K> keySet)
	//		{
	//			actualKeySet = keySet;
	//
	//
	//			class ks
	//			extends AbstractSet<K>
	//			implements MapKeySetView<K>
	//			{
	//				//TODO
	//
	//				@Override
	//				public boolean contains(Object o)
	//				{
	//					return containsKey(o);
	//				}
	//
	//				public boolean remove(Object o)
	//				{
	//					boolean contained = containsKey(o); //can't use null from return value of Map.remove()  (or any special value without multiple paths or oop escaping!), since it could be valid!  (oy; singleton return values/paths / output X> )
	//					AlternateAbstractMap.this.remove(o);
	//					return contained;
	//				}
	//			}
	//			keySetView = new ks();
	//
	//
	//			class vc
	//			extends AbstractSet<V>
	//			implements MapValueCollectionView<V>
	//			{
	//
	//			};
	//			valuesCollectionView = new vc();
	//		}
	//
	//		protected Set<K> getActualKeySet()
	//		{
	//			return this.actualKeySet;
	//		}
	//
	//
	//
	//		@Override
	//		public abstract V get(Object key);
	//
	//		/**
	//		 * Should affect {@link #actualKeySet} as wells :>
	//		 */
	//		@Override
	//		public abstract V put(K key, V value);
	//
	//		/**
	//		 * Should affect {@link #actualKeySet} as wells :>
	//		 */
	//		@Override
	//		public abstract V remove(Object key);
	//
	//		/**
	//		 * Should affect {@link #actualKeySet} as wells :>
	//		 */
	//		@Override
	//		public abstract void clear();
	//
	//
	//
	//
	//		/**
	//		 * Default: delegate to {@link #actualKeySet}
	//		 * (which is NOT NECESSARILY GOOD FOR LAZILY COMPUTED KEY SETS 0,0 )
	//		 */
	//		@Override
	//		public boolean containsKey(Object key)
	//		{
	//			return actualKeySet.contains(key);
	//		}
	//
	//		/**
	//		 * Default: delegate to {@link #actualKeySet}
	//		 * (which is NOT NECESSARILY GOOD FOR LAZILY COMPUTED KEY SETS 0,0 )
	//		 */
	//		@Override
	//		public int size()
	//		{
	//			return actualKeySet.size();
	//		}
	//
	//		/**
	//		 * Default: {@link #size()} == 0
	//		 * Override if you has a better way! :>
	//		 */
	//		@Override
	//		public boolean isEmpty()
	//		{
	//			return size() == 0;
	//		}
	//
	//
	//
	//
	//
	//
	//		@Override
	//		public Set<K> keySet()
	//		{
	//			return keySetView;
	//		}
	//
	//		@Override
	//		public Collection<V> values()
	//		{
	//			return valuesCollectionView;
	//		}
	//
	//		@Override
	//		public Set<Entry<K, V>> entrySet()
	//		{
	//			return entrySetView;
	//		}
	//
	//
	//
	//		@Override
	//		public boolean containsValue(Object value)
	//		{
	//			V v = null;
	//			for (K key : actualKeySet) //a bit fasters :>
	//			{
	//				v = this.get(key);
	//				if (((EqualityComparator)getBoundValueEqualityComparator(this)).equals(value, v))
	//					return true;
	//			}
	//
	//			return false;
	//		}
	//
	//		@Override
	//		public void putAll(Map<? extends K, ? extends V> m)
	//		{
	//			for (K otherkey : m.keySet())
	//				this.put(otherkey, m.get(otherkey));
	//		}
	//	}
	//
	//	public static abstract class AlternateAbstractReadonlyMap<K, V>
	//	extends AlternateAbstractMap<K, V>
	//	{
	//		public AlternateAbstractReadonlyMap(Set<K> keySet)
	//		{
	//			super(keySet);
	//		}
	//
	//
	//		@Override
	//		public V put(K key, V value)
	//		{
	//			throw new ReadonlyUnsupportedOperationException();
	//		}
	//
	//		@Override
	//		public void clear()
	//		{
	//			throw new ReadonlyUnsupportedOperationException();
	//		}
	//
	//		@Override
	//		public V remove(Object key)
	//		{
	//			throw new ReadonlyUnsupportedOperationException();
	//		}
	//
	//		@Override
	//		public void putAll(Map<? extends K, ? extends V> m)
	//		{
	//			throw new ReadonlyUnsupportedOperationException();
	//		}
	//
	//
	//
	//		@Override
	//		public boolean isReadableMap()
	//		{
	//			return true;
	//		}
	//
	//		@Override
	//		public boolean isWritableMap()
	//		{
	//			return false;
	//		}
	//	}
	//
	//	public static abstract class AlternateAbstractWriteonlyMap<K, V>
	//	extends AlternateAbstractMap<K, V>
	//	{
	//		public AlternateAbstractWriteonlyMap(Set<K> keySet)
	//		{
	//			super(keySet);
	//		}
	//
	//		//TODO
	//		@Override
	//		public V get(Object key)
	//		{
	//		}
	//
	//		@Override
	//		public Set<K> keySet()
	//		{
	//			return super.keySet();
	//
	//		}
	//
	//		@Override
	//		public boolean containsKey(Object key)
	//		{
	//			throw new UnsupportedAddressTypeException();
	//		}
	//
	//
	//		@Override
	//		public boolean isReadableMap()
	//		{
	//			return false;
	//		}
	//
	//		@Override
	//		public boolean isWritableMap()
	//		{
	//			return true;
	//		}
	//	}
	//
	//	public static abstract class AlternateAbstractReadwriteMap<K, V>
	//	extends AlternateAbstractMap<K, V>
	//	{
	//		public AlternateAbstractReadwriteMap(Set<K> keySet)
	//		{
	//			super(keySet);
	//		}
	//
	//
	//		//TODO
	//
	//		@Override
	//		public boolean isReadableMap()
	//		{
	//			return true;
	//		}
	//
	//		@Override
	//		public boolean isWritableMap()
	//		{
	//			return true;
	//		}
	//	}
	//
	//	public static abstract class AlternateAbstractMapWithExplicitlyCompletelyRecomputedKeys<K, V>
	//	extends AlternateAbstractMap<K, V>
	//	implements FlushableCache, CollectionUtilities.MapWithExplicitlyRecomputedKeyset
	//	{
	//		public AlternateAbstractMapWithExplicitlyCompletelyRecomputedKeys(NullaryFunction<Set<K>> keySetComputorFunction)
	//		{
	//			super(new CachingSet<K>(keySetComputorFunction));
	//		}
	//
	//		/**
	//		 * Turned back to explicit-implementation-required because delegating to keyset just to check if a given key is present would make the ENTIRE KEYSET NEED TO BE COMPUTED,
	//		 * which, the fact that you can check individual things without computing the entire keyset (eg, in get() ^_^ ) is prooooobably the reason you're using this class to begin with! XD'
	//		 */
	//		@Override
	//		public abstract boolean containsKey(Object key);
	//
	//		/**
	//		 * Turned back to explicit-implementation-required because delegating to keyset just to check if a given key is present would make the ENTIRE KEYSET NEED TO BE COMPUTED,
	//		 * which, the fact that you can check individual things without computing the entire keyset (eg, in get() ^_^ ) is prooooobably the reason you're using this class to begin with! XD'
	//		 */
	//		@Override
	//		public abstract int size();
	//
	//
	//
	//		@Override
	//		public void resetCache()
	//		{
	//			//There is no super.resetCache(), but we would've called it :>   (And you should too, subclasses!)  :D
	//			recomputeKeyset();
	//		}
	//
	//		@Override
	//		public void recomputeKeyset()
	//		{
	//			((CachingSet)getActualKeySet()).resetCache();
	//		}
	//	}
	//
	//	public static abstract class AlternateAbstractReadonlyMapWithExplicitlyCompletelyRecomputedKeys<K, V>
	//	extends AlternateAbstractReadonlyMap<K, V>
	//	implements FlushableCache, CollectionUtilities.MapWithExplicitlyRecomputedKeyset
	//	{
	//		public AlternateAbstractReadonlyMapWithExplicitlyCompletelyRecomputedKeys(NullaryFunction<Set<K>> keySetComputorFunction)
	//		{
	//			super(new CachingSet<K>(keySetComputorFunction));
	//		}
	//
	//		/**
	//		 * Turned back to explicit-implementation-required because delegating to keyset just to check if a given key is present would make the ENTIRE KEYSET NEED TO BE COMPUTED,
	//		 * which, the fact that you can check individual things without computing the entire keyset (eg, in get() ^_^ ) is prooooobably the reason you're using this class to begin with! XD'
	//		 */
	//		@Override
	//		public abstract boolean containsKey(Object key);
	//
	//		/**
	//		 * Turned back to explicit-implementation-required because delegating to keyset just to check if a given key is present would make the ENTIRE KEYSET NEED TO BE COMPUTED,
	//		 * which, the fact that you can check individual things without computing the entire keyset (eg, in get() ^_^ ) is prooooobably the reason you're using this class to begin with! XD'
	//		 */
	//		@Override
	//		public abstract int size();
	//
	//
	//
	//		@Override
	//		public void resetCache()
	//		{
	//			//There is no super.resetCache(), but we would've called it :>   (And you should too, subclasses!)  :D
	//			recomputeKeyset();
	//		}
	//
	//		@Override
	//		public void recomputeKeyset()
	//		{
	//			((CachingSet)getActualKeySet()).resetCache();
	//		}
	//	}
	//
	//	public static abstract class AlternateAbstractWriteonlyMapWithExplicitlyCompletelyRecomputedKeys<K, V>
	//	extends AlternateAbstractWriteonlyMap<K, V>
	//	implements FlushableCache, CollectionUtilities.MapWithExplicitlyRecomputedKeyset
	//	{
	//		public AlternateAbstractWriteonlyMapWithExplicitlyCompletelyRecomputedKeys(NullaryFunction<Set<K>> keySetComputorFunction)
	//		{
	//			super(new CachingSet<K>(keySetComputorFunction));
	//		}
	//
	//		/**
	//		 * Turned back to explicit-implementation-required because delegating to keyset just to check if a given key is present would make the ENTIRE KEYSET NEED TO BE COMPUTED,
	//		 * which, the fact that you can check individual things without computing the entire keyset (eg, in get() ^_^ ) is prooooobably the reason you're using this class to begin with! XD'
	//		 */
	//		@Override
	//		public abstract boolean containsKey(Object key);
	//
	//		/**
	//		 * Turned back to explicit-implementation-required because delegating to keyset just to check if a given key is present would make the ENTIRE KEYSET NEED TO BE COMPUTED,
	//		 * which, the fact that you can check individual things without computing the entire keyset (eg, in get() ^_^ ) is prooooobably the reason you're using this class to begin with! XD'
	//		 */
	//		@Override
	//		public abstract int size();
	//
	//
	//
	//		@Override
	//		public void resetCache()
	//		{
	//			//There is no super.resetCache(), but we would've called it :>   (And you should too, subclasses!)  :D
	//			recomputeKeyset();
	//		}
	//
	//		@Override
	//		public void recomputeKeyset()
	//		{
	//			((CachingSet)getActualKeySet()).resetCache();
	//		}
	//	}
	//
	//	public static abstract class AlternateAbstractReadwriteMapWithExplicitlyCompletelyRecomputedKeys<K, V>
	//	extends AlternateAbstractReadwriteMap<K, V>
	//	implements FlushableCache, CollectionUtilities.MapWithExplicitlyRecomputedKeyset
	//	{
	//		public AlternateAbstractReadwriteMapWithExplicitlyCompletelyRecomputedKeys(NullaryFunction<Set<K>> keySetComputorFunction)
	//		{
	//			super(new CachingSet<K>(keySetComputorFunction));
	//		}
	//
	//		/**
	//		 * Turned back to explicit-implementation-required because delegating to keyset just to check if a given key is present would make the ENTIRE KEYSET NEED TO BE COMPUTED,
	//		 * which, the fact that you can check individual things without computing the entire keyset (eg, in get() ^_^ ) is prooooobably the reason you're using this class to begin with! XD'
	//		 */
	//		@Override
	//		public abstract boolean containsKey(Object key);
	//
	//		/**
	//		 * Turned back to explicit-implementation-required because delegating to keyset just to check if a given key is present would make the ENTIRE KEYSET NEED TO BE COMPUTED,
	//		 * which, the fact that you can check individual things without computing the entire keyset (eg, in get() ^_^ ) is prooooobably the reason you're using this class to begin with! XD'
	//		 */
	//		@Override
	//		public abstract int size();
	//
	//
	//
	//		@Override
	//		public void resetCache()
	//		{
	//			//There is no super.resetCache(), but we would've called it :>   (And you should too, subclasses!)  :D
	//			recomputeKeyset();
	//		}
	//
	//		@Override
	//		public void recomputeKeyset()
	//		{
	//			((CachingSet)getActualKeySet()).resetCache();
	//		}
	//	}
	
	public static <C extends Collection<?>> C allnotnull(C collection) throws NullPointerException
	{
		for (Object o : collection)
			if (o == null)
				throw new NullPointerException();
		return collection;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@ThrowAwayValue
	public static <E> E[] sorted(@ReadonlyValue E[] input)
	{
		E[] sorted = input.clone();
		Arrays.sort(sorted);
		return sorted;
	}
	
	@ThrowAwayValue
	public static <E> E[] sorted(@ReadonlyValue E[] input, Comparator<? super E> comparator)
	{
		E[] sorted = input.clone();
		Arrays.sort(sorted, comparator);
		return sorted;
	}
	
	
	
	
	@ThrowAwayValue
	public static <E> List<E> sorted(@ReadonlyValue Collection<E> input)
	{
		Object[] array = input.toArray();
		Arrays.sort(array);
		return (List<E>)Arrays.asList(array);
	}
	
	@ThrowAwayValue
	public static <E> List<E> sorted(@ReadonlyValue Collection<E> input, Comparator<? super E> comparator)
	{
		Object[] array = input.toArray();
		Arrays.sort((E[])array, comparator);
		return (List<E>)Arrays.asList(array);
	}
	
	
	
	
	
	public static List getMultidimensionalListWrapper(Object[] arrayOfR, int depth)
	{
		if (depth < 1)
			throw new IllegalArgumentException("Depth < 1");
		else if (depth == 1)
			return Arrays.asList(arrayOfR);
		else
		{
			List[] arrayOfLists = new List[arrayOfR.length];
			for (int i = 0; i < arrayOfLists.length; i++)
				arrayOfLists[i] = getMultidimensionalListWrapper((Object[])arrayOfR[i], depth-1);
			return Arrays.asList(arrayOfLists);
		}
	}
	
	public static <E> List<E> getOneDimensionalListWrapper(E[] array)
	{
		return getMultidimensionalListWrapper(array, 1);
	}
	
	public static <E> List<List<E>> getTwoDimensionalListWrapper(E[][] arrayOfArrays)
	{
		return getMultidimensionalListWrapper(arrayOfArrays, 2);
	}
	
	public static <E> List<List<List<E>>> getThreeDimensionalListWrapper(E[][][] arrayOfArraysOfArrays)
	{
		return getMultidimensionalListWrapper(arrayOfArraysOfArrays, 3);
	}
	
	
	
	
	
	
	public static List getMultidimensionalListShallowCopy(List sourceList, int depth, Class<? extends List> listClass)
	{
		try
		{
			if (depth < 1)
				throw new IllegalArgumentException("Depth < 1");
			else if (depth == 1)
			{
				List destList = listClass.newInstance();
				for (Object e : sourceList)
					destList.add(e);
				return destList;
			}
			else
			{
				List destList = listClass.newInstance();
				for (List e : (List<List>)sourceList)
					destList.add(getMultidimensionalListShallowCopy(e, depth-1, listClass));
				return destList;
			}
		}
		catch (InstantiationException exc)
		{
			throw new RuntimeException();
		}
		catch (IllegalAccessException exc)
		{
			throw new RuntimeException();
		}
	}
	
	public static List getMultidimensionalListShallowCopy(List sourceList, int depth)
	{
		return getMultidimensionalListShallowCopy(sourceList, depth, ArrayList.class);
	}
	
	
	public static <E> List<E> getOneDimensionalListShallowCopy(List<E> sourceList)
	{
		return getMultidimensionalListShallowCopy(sourceList, 1);
	}
	
	public static <E> List<List<E>> getTwoDimensionalListShallowCopy(List<List<E>> sourceList)
	{
		return getMultidimensionalListShallowCopy(sourceList, 2);
	}
	
	public static <E> List<List<List<E>>> getThreeDimensionalListShallowCopy(List<List<List<E>>> sourceList)
	{
		return getMultidimensionalListShallowCopy(sourceList, 3);
	}
	
	
	
	
	
	
	/**
	 * In-place!
	 * @return list, for convenience
	 */
	public static <A,B> List convertMultidimensional(List list, int depth, UnaryFunction<A,B> converter)
	{
		if (depth < 0)
			throw new IllegalArgumentException("Depth < 0");
		else if (depth == 0)
			return list;
		else if (depth == 1)
		{
			for (int i = 0; i < list.size(); i++)
			{
				A source = (A)list.get(i);
				B dest = converter.f(source);
				list.set(i, dest);
			}
		}
		else
		{
			for (int i = 0; i < list.size(); i++)
			{
				convertMultidimensional((List)list.get(i), depth-1, converter);
			}
		}
		
		return list;
	}
	
	
	public static <A,B,E> List<E> convertOneDimensional(List<E> list, UnaryFunction<A,B> converter)
	{
		return convertMultidimensional(list, 1, converter);
	}
	
	public static <A,B,E> List<List<E>> convertTwoDimensional(List<List<E>> listOfLists, UnaryFunction<A,B> converter)
	{
		return convertMultidimensional(listOfLists, 2, converter);
	}
	
	public static <A,B,E> List<List<List<E>>> convertThreeDimensional(List<List<List<E>>> listOfListOfLists, UnaryFunction<A,B> converter)
	{
		return convertMultidimensional(listOfListOfLists, 3, converter);
	}
	
	
	
	
	
	/**
	 * The inverse operation of this is {@link PolymorphicCollectionUtilities#mergeLists(Object...)}  ^wwww^
	 * @param elements note that its length must be a multiple of the partitionSize!! \o/
	 */
	@ReadonlyValue
	public static <E> List<List<E>> partition(@LiveValue @ReadonlyValue List<E> elements, int partitionSize)
	{
		int nElements = elements.size();
		
		int nPartitions = ceilingDivision(nElements, partitionSize);
		
		List<E>[] partitions = new List[nPartitions];
		
		for (int i = 0; i < nPartitions; i++)
		{
			int start = i * partitionSize;
			partitions[i] = elements.subList(start, least(start + partitionSize, nElements));
		}
		
		return asList(partitions);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <K, V> Map<K, V> keysToMap(UnaryFunction<K, V> valuemaker, Set<K> keys)
	{
		HashMap<K, V> map = new HashMap<>();
		for (K key : keys)
			map.put(key, valuemaker.f(key));
		return map;
	}
	
	public static <K, V> Map<K, V> valuesToMapSilent(UnaryFunction<V, K> keymaker, Collection<V> values)
	{
		HashMap<K, V> map = new HashMap<>();
		for (V value : values)
			map.put(keymaker.f(value), value);
		return map;
	}
	
	public static <K, V> Map<K, V> valuesToMapErring(UnaryFunction<V, K> keymaker, Collection<V> values) throws AlreadyExistsException
	{
		HashMap<K, V> map = new HashMap<>();
		for (V value : values)
		{
			K key = keymaker.f(value);
			if (map.containsKey(key))
				throw new AlreadyExistsException("Key: "+toStringNT(key));
			map.put(key, value);
		}
		return map;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Note: if the function returns null, then the entry will be skipped!  So this combines map and filter in one! :D
	 */
	public static <K, V> Map<K, V> rimap(Iterable<Entry<K, V>> input) throws NonReverseInjectiveMapException
	{
		return maptodict(e -> e, input);
	}
	
	
	/**
	 * Note: if the function returns null, then the entry will be skipped!  So this combines map and filter in one! :D
	 */
	public static <Ki, Vi, Ko, Vo> Map<Ko, Vo> mapdict(Mapper<Entry<Ki, Vi>, Entry<Ko, Vo>> function, Map<Ki, Vi> input) throws NonReverseInjectiveMapException
	{
		return maptodict(function, input.entrySet());
	}
	
	
	public static <I, Ko, Vo> Map<Ko, Vo> maptodict(Mapper<I, Entry<Ko, Vo>> function, Iterable<I> input) throws NonReverseInjectiveMapException
	{
		Map<Ko, Vo> output = new HashMap<>();
		
		for (I in : input)
		{
			Entry<Ko, Vo> eOut;
			try
			{
				eOut = function.f(in);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			if (eOut != null)
			{
				Ko key = eOut.getKey();
				
				if (output.containsKey(key))
					throw new NonReverseInjectiveMapException("Conflict!  Multiple entries mapping to the key: "+repr(key));
				
				output.put(key, eOut.getValue());
			}
		}
		
		return output;
	}
	
	/**
	 * @throws NonReverseInjectiveMapException can only happen if the input is not a Set!
	 */
	public static <K, V> Map<K, V> maptodictSameKeys(Mapper<K, V> function, Iterable<K> input) throws NonReverseInjectiveMapException
	{
		return maptodict(k -> new SimpleEntry<K, V>(k, function.f(k)), input);
	}
	
	public static <K, V> Map<K, V> maptodictSameValues(Mapper<V, K> function, Iterable<V> input) throws NonReverseInjectiveMapException
	{
		return maptodict(v -> new SimpleEntry<K, V>(function.f(v), v), input);
	}
	
	
	public static <K, Vi, Vo> Map<K, Vo> mapdictvalues(Mapper<Vi, Vo> function, Map<K, Vi> input)
	{
		Map<K, Vo> output = new HashMap<>();
		
		for (Entry<K, Vi> eIn : input.entrySet())
		{
			Vo vOut;
			try
			{
				vOut = function.f(eIn.getValue());
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			K key = eIn.getKey();
			
			if (output.containsKey(key))
				throw new NonReverseInjectiveMapException("This means the *original* map already existed in corrupt non-reverse-injective state!");
			
			output.put(key, vOut);
		}
		
		return output;
	}
	
	public static <Ki, Ko, V> Map<Ko, V> mapdictkeys(Mapper<Ki, Ko> function, Map<Ki, V> input) throws NonReverseInjectiveMapException
	{
		return mapdict(e -> new SimpleEntry<>(function.f(e.getKey()), e.getValue()), input);
	}
	
	
	
	
	
	public static <I, Ko, Vo> Map<Ko, Set<Vo>> maptogendict(Mapper<I, Entry<Ko, Vo>> function, Iterable<I> input) throws NonReverseInjectiveMapException
	{
		Map<Ko, Set<Vo>> output = new HashMap<>();
		
		for (I in : input)
		{
			Entry<Ko, Vo> eOut;
			try
			{
				eOut = function.f(in);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			if (eOut != null)
			{
				Ko key = eOut.getKey();
				
				Set<Vo> set = getOrCreate(output, key, HashSet::new);
				
				set.add(eOut.getValue());
			}
		}
		
		return output;
	}
	
	
	
	
	
	
	
	
	public static <K, V> Map<K, V> filterdict(MapEntryPredicate<K, V> predicate, Map<K, V> input)
	{
		Map<K, V> output = new HashMap<>();
		
		for (Entry<K, V> e : input.entrySet())
		{
			if (predicate.test(e.getKey(), e.getValue()))
			{
				output.put(e.getKey(), e.getValue());
			}
		}
		
		return output;
	}
	
	public static <K, V> Map<K, V> filterdictByValues(Predicate<V> predicate, Map<K, V> input)
	{
		return filterdict((k, v) -> predicate.test(v), input);
	}
	
	public static <K, V> Map<K, V> filterdictByKeys(Predicate<K> predicate, Map<K, V> input)
	{
		return filterdict((k, v) -> predicate.test(k), input);
	}
	
	
	
	
	
	public static <K, V extends Collection<?>> Map<K, V> maptodictSameKeysFilteringAwayEmptyValues(Mapper<K, V> function, Iterable<K> input) throws NonReverseInjectiveMapException
	{
		return maptodict(k ->
		{
			V v = function.f(k);
			
			if (v.isEmpty())
				throw FilterAwayReturnPath.I;
			else
				return new SimpleEntry<K, V>(k, v);
			
		}, input);
	}
	
	
	
	
	public static <K, V> void mapdictvaluesIP(Mapper<V, V> function, @WritableValue Map<K, V> map)
	{
		for (Entry<K, V> eIn : map.entrySet())
		{
			K key = eIn.getKey();
			
			V vOut;
			try
			{
				vOut = function.f(eIn.getValue());
			}
			catch (FilterAwayReturnPath exc)
			{
				map.remove(key);
				continue;
			}
			
			map.put(key, vOut);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@ReadonlyValue
	@HashableValue
	public static Map mapof()
	{
		return emptyMap();
	}
	
	@ReadonlyValue
	@HashableValue
	public static Map mapof(Object key, Object value)
	{
		return singletonMap(key, value);
	}
	
	/**
	 * This is not map() because that would conflict with the verb "map" as in {@link #map(Mapper, Iterator)} / etc. and also be confusing I think XD
	 */
	@ReadonlyValue
	@HashableValue
	public static Map mapof(Object... keysAndValues)
	{
		return mapofArray(keysAndValues);
	}
	
	
	
	@ReadonlyValue
	@HashableValue
	public static Map mapofInverted(Object... valuesAndKeys)
	{
		return mapofInvertedArray(valuesAndKeys);
	}
	
	@ThrowAwayValue
	public static Map newMap(Object... keysAndValues)
	{
		return newMapArray(keysAndValues);
	}
	
	@ThrowAwayValue
	public static Map newMapInverted(Object... valuesAndKeys)
	{
		return newMapInvertedArray(valuesAndKeys);
	}
	
	
	/**
	 * The keys are simply themselves, the values are {@link Maybe}s with the actual value (which may legitimately be null inside the Maybe!) or nulls to be elided.
	 */
	@ReadonlyValue
	@HashableValue
	public static Map mapofSome(Object... keysAndValues)
	{
		if (keysAndValues.length == 0)
		{
			return emptyMap();
		}
		else if (keysAndValues.length == 2)
		{
			return keysAndValues[1] == null ? emptyMap() : singletonMap(keysAndValues[0], ((Maybe)keysAndValues[1]).getJust());
		}
		else
		{
			return newMapMaybeArray(keysAndValues);
		}
	}
	
	
	
	
	@ReadonlyValue
	@HashableValue
	public static Map mapofArray(Object[] keysAndValues)
	{
		if (keysAndValues.length == 0)
			return emptyMap();
		else if (keysAndValues.length == 2)
			return singletonMap(keysAndValues[0], keysAndValues[1]);
		else
		{
			Class keysEnumClass;
			{
				keysEnumClass = null;
				
				for (int i = 0; i < keysAndValues.length; i += 2)
				{
					Object key = keysAndValues[i];
					
					Class c = key == null ? null : key.getClass();
					
					if (c == null)
					{
						//Abort!
						keysEnumClass = null;
						break;
					}
					else
					{
						if (keysEnumClass == null)
						{
							if (c.isEnum())
							{
								keysEnumClass = c;
							}
							else
							{
								//Abort!
								keysEnumClass = null;
								break;
							}
						}
						else
						{
							if (keysEnumClass != c)
							{
								//Abort!
								keysEnumClass = null;
								break;
							}
						}
					}
				}
			}
			
			if (keysEnumClass == null)
				return newMapArray(keysAndValues);
			else
				return newEnumMapArray(keysEnumClass, keysAndValues);
		}
	}
	
	@ReadonlyValue
	@HashableValue
	public static Map mapofInvertedArray(Object[] valuesAndKeys)
	{
		if (valuesAndKeys.length == 0)
			return emptyMap();
		else if (valuesAndKeys.length == 2)
			return singletonMap(valuesAndKeys[1], valuesAndKeys[0]);
		else
			return newMapInvertedArray(valuesAndKeys);
	}
	
	
	@ThrowAwayValue
	public static Map newMapMaybeArray(Object[] keysAndValues)
	{
		if ((keysAndValues.length % 2) != 0)
			throw new IllegalArgumentException();
		
		
		Map m = new HashMap();
		
		for (int i = 0; i < keysAndValues.length; i += 2)
		{
			Object key = keysAndValues[i];
			
			if (m.containsKey(key))
				throw new AlreadyExistsException();
			
			Maybe<?> valuem = (Maybe<?>) keysAndValues[i+1];
			
			if (valuem != null)
			{
				Object value = valuem.getJust();
				m.put(key, value);
			}
		}
		
		return m;
	}
	
	@ThrowAwayValue
	public static Map newMapArray(Object[] keysAndValues)
	{
		if ((keysAndValues.length % 2) != 0)
			throw new IllegalArgumentException();
		
		
		Map m = new HashMap();
		
		for (int i = 0; i < keysAndValues.length; i += 2)
		{
			Object key = keysAndValues[i];
			
			if (m.containsKey(key))
				throw new AlreadyExistsException();
			
			m.put(key, keysAndValues[i+1]);
		}
		
		return m;
	}
	
	@ThrowAwayValue
	public static EnumMap newEnumMapArray(Class keyClass, Object[] keysAndValues)
	{
		if ((keysAndValues.length % 2) != 0)
			throw new IllegalArgumentException();
		
		
		EnumMap m = new EnumMap(keyClass);
		
		for (int i = 0; i < keysAndValues.length; i += 2)
		{
			Object key = keysAndValues[i];
			
			if (m.containsKey(key))
				throw new AlreadyExistsException();
			
			m.put((Enum)key, keysAndValues[i+1]);
		}
		
		return m;
	}
	
	@ThrowAwayValue
	public static Map newMapInvertedArray(Object[] valuesAndKeys)
	{
		if ((valuesAndKeys.length % 2) != 0)
			throw new IllegalArgumentException();
		
		
		Map m = new HashMap();
		
		for (int i = 0; i < valuesAndKeys.length; i += 2)
		{
			Object key = valuesAndKeys[i+1];
			
			if (m.containsKey(key))
				throw new AlreadyExistsException();
			
			m.put(key, valuesAndKeys[i]);
		}
		
		return m;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@ReadonlyValue
	@HashableValue
	public static <E> Set<E> setof(E member)
	{
		return singletonSet(member);
	}
	
	@ReadonlyValue
	@HashableValue
	public static <E> Set<E> setof()
	{
		return emptySet();
	}
	
	/**
	 * This is not set() because that would conflict with the verb "set", as in {@link ObjectContainer#get() get()}/{@link ObjectContainer#set(Object) set()} and also be confusing I think XD
	 */
	@ReadonlyValue
	@HashableValue
	public static <E> Set<E> setof(E... members)
	{
		return setofArray(members);
	}
	
	
	
	@ThrowAwayValue
	public static <E> Set<E> newSet(E... members)
	{
		return newSetArray(members);
	}
	
	
	@ReadonlyValue
	@HashableValue
	public static <E> Set<E> setofArray(E[] members)
	{
		//Todo performance threshold for when a naive set backed by a list should be used instead of a hashset!
		
		if (members.length == 0)
			return emptySet();
		else if (members.length == 1)
			return singletonSet(members[0]);
		else
			return newSet(members);
	}
	
	@ThrowAwayValue
	public static <E> Set<E> newSetArray(E[] members)
	{
		return new HashSet<E>(asList(members));
	}
	
	
	@ReadonlyValue
	@HashableValue
	public static <E> Set<E> setofSome(Maybe<E>... members)
	{
		if (members.length == 0)
		{
			return emptySet();
		}
		else if (members.length == 1)
		{
			return members[0] == null ? emptySet() : singletonSet(members[0].getJust());
		}
		else
		{
			return mapToSet(m ->
			{
				if (m == null)
					throw FilterAwayReturnPath.I;
				else
					return m.getJust();
			}, members);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@ReadonlyValue
	@HashableValue
	public static <E> List<E> listof()
	{
		return emptyList();
	}
	
	@ReadonlyValue
	@HashableValue
	public static <E> List<E> listof(E member)
	{
		return singletonList(member);
	}
	
	/**
	 * This is not list() because that conflicts with the verb "list", as in {@link Container#list()} X'D
	 */
	@ReadonlyValue
	@HashableValue
	public static <E> List<E> listof(E... members)
	{
		return listofArray(members);
	}
	
	
	
	@ThrowAwayValue
	public static <E> List<E> newList(E... members)
	{
		return newListArray(members);
	}
	
	/**
	 * This is deprecated not because it's going to be removed, but to serve as a reminder to specify which one you mean, {@link #listof(Object...)} or {@link #newList(Object...)} X3
	 * (It delegates to {@link #newList(Object...)} in reality :3 )
	 */
	@ThrowAwayValue
	@Deprecated
	public static <E> List<E> newlistofUnspecifiedWritability(E... members)
	{
		return newListArray(members);
	}
	
	
	@ReadonlyValue
	@HashableValue
	public static <E> List<E> listofSome(Maybe<E>... members)
	{
		if (members.length == 0)
		{
			return emptyList();
		}
		else if (members.length == 1)
		{
			return members[0] == null ? emptyList() : singletonList(members[0].getJust());
		}
		else
		{
			return mapToList(m ->
			{
				if (m == null)
					throw FilterAwayReturnPath.I;
				else
					return m.getJust();
			}, members);
		}
	}
	
	
	
	
	
	@ReadonlyValue
	@HashableValue
	public static <E> List<E> listofArray(E[] members)
	{
		if (members.length == 0)
			return emptyList();
		else if (members.length == 1)
			return singletonList(members[0]);
		else
			return asList(members);
	}
	
	@ThrowAwayValue
	public static <E> List<E> newListArray(E[] members)
	{
		return new ArrayList<>(listofArray(members));
	}
	
	/**
	 * Contrast with {@link PrimitiveCollections#newIntegerListZerofilled(int)} and etc. :3
	 */
	@ThrowAwayValue
	@WritableValue
	public static <E> List<E> newListNullfilled(int size)
	{
		ArrayList<E> l = new ArrayList<>(size);
		fillByAdding(l, null, size);
		return l;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@ReadonlyValue
	@HashableValue  //TODO is this respected??!
	public static <E> SimpleTable<E> tableof(int width, E... contents)
	{
		return tableofArray(width, contents);
	}
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> newTable(int width, E... contents)
	{
		return newTableArray(width, contents);
	}
	
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> newTableFromOneRow(List<E> contents)
	{
		return newTableList(contents.size(), contents);  //row major order allows this :3
	}
	
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> newTableFromOther(@ReadonlyValue @SnapshotValue SimpleTable<E> contents)
	{
		SimpleTable<E> t = newTable();
		t.setFrom(contents);
		return t;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@WritableValue
	public static <E> SimpleTable<E> writableTable(@PossiblySnapshotPossiblyLiveValue SimpleTable<E> contents)
	{
		return contents.isWritableTable() ? contents : newTableFromOther(contents);
	}
	
	
	
	@ReadonlyValue
	public static <E> SimpleTable<E> tableofArray(int width, E[] contents)
	{
		if (width == 0 || contents.length == 0)
			return emptyTable();
		else
			return newTableArray(width, contents);
	}
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> newTableArray(int width, E[] contents)
	{
		if (contents.length % width != 0)
			throw new IllegalArgumentException();
		
		int height = contents.length / width;
		
		
		SimpleTable<E> t = newTableNullfilled(width, height);
		
		for (int r = 0; r < height; r++)
		{
			for (int c = 0; c < width; c++)
			{
				int i = r * width + c;
				
				t.setCellContents(c, r, contents[i]);
			}
		}
		
		return t;
	}
	
	@ReadonlyValue
	public static <E> SimpleTable<E> tableofList(int width, List<E> contents)
	{
		if (width == 0 || contents.size() == 0)
			return emptyTable();
		else
			return newTableList(width, contents);
	}
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> newTableList(int width, List<E> contents)
	{
		if (contents.size() % width != 0)
			throw new IllegalArgumentException();
		
		int height = contents.size() / width;
		
		
		SimpleTable<E> t = newTableNullfilled(width, height);
		
		for (int r = 0; r < height; r++)
		{
			for (int c = 0; c < width; c++)
			{
				int i = r * width + c;
				
				t.setCellContents(c, r, contents.get(i));
			}
		}
		
		return t;
	}
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> newTable()
	{
		return newTableNullfilled(0, 0);
	}
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> newTableNullfilled(int width, int height)
	{
		//TODO A more efficient implementation!!
		return new NestedListsSimpleTable<E>(width, height);
	}
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> newTableGivenfilled(int width, int height, E initialValue)
	{
		//TODO A more efficient implementation!!
		SimpleTable<E> t = newTableNullfilled(width, height);
		t.setAllToSameValue(initialValue);
		return t;
	}
	
	
	
	@ReadonlyValue
	public static <E> SimpleTable<E> emptyTable()
	{
		//Todo make an immutable empty subclass that can be statically cached xD ^^'''
		return newTable();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <K, I, O> O getAndDoOrDefault(Map<K, I> map, K key, UnaryFunction<I, O> function, O defaultValue)
	{
		I input = map.get(key);
		
		if (input == null)
		{
			if (!map.containsKey(key))
			{
				return defaultValue;
			}
		}
		
		return function.f(input);
	}
	
	
	public static <K, I, O> O getremoveAndDoOrDefault(Map<K, I> map, K key, UnaryFunction<I, O> function, O defaultValue)
	{
		if (map.containsKey(key))
		{
			return function.f(map.remove(key));
		}
		else
		{
			return defaultValue;
		}
	}
	
	
	
	
	/**
	 * Note: NOT weak map (weak-value or weak-key) safe!!
	 */
	public static <K, V> V getOrCreate(Map<K, V> map, K key, NullaryFunction<V> creator)
	{
		V v = map.get(key);
		
		if (v == null && !map.containsKey(key))
		{
			v = creator.f();
			map.put(key, v);
		}
		
		return v;
	}
	
	
	
	
	
	public static enum SetRelationRequirement
	{
		ExactEquality,
		ActualCanBeSubsetOfExpected,
		ActualCanBeSupersetOfExpected,
	}
	
	
	
	
	public static <E> boolean testSetRelation(Set<E> actual, Set<E> expected, SetRelationRequirement relation)
	{
		if (relation == SetRelationRequirement.ExactEquality)
		{
			return actual.equals(expected);
		}
		else if (relation == SetRelationRequirement.ActualCanBeSubsetOfExpected)
		{
			return expected.containsAll(actual);
		}
		else if (relation == SetRelationRequirement.ActualCanBeSupersetOfExpected)
		{
			return actual.containsAll(expected);
		}
		else
		{
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(relation);
		}
	}
	
	
	
	
	
	
	
	public static <E extends Enum<E> & StandardEnumSetConvertibleToIntFlags> EnumSet<E> convertFromBitfieldToEnumSet(long bitfield, Class<E> enumType, SetRelationRequirement setRelation)
	{
		EnumSet<E> set = EnumSet.noneOf(enumType);
		long validationCheck = 0;
		
		for (E member : enumType.getEnumConstants())
		{
			long bv = member.getBitfieldValue();
			
			if ((validationCheck & bv) != 0)
				throw new ImpossibleException("Two or more members of "+enumType.getName()+" overlap in the 1's of their binary values!!");
			validationCheck |= bv;
			
			if ((bitfield & bv) != 0)
			{
				set.add(member);
				bitfield &= ~bv;
			}
		}
		
		if (setRelation == SetRelationRequirement.ExactEquality)
		{
			if (bitfield != 0)
				throw new IllegalArgumentException("The bitfield contained extra unre");
		}
		
		return set;
	}
	
	
	
	public static <E extends Enum<E> & StandardEnumSetConvertibleToIntFlags> long convertFromEnumSetToBitfield(EnumSet<E> set, Class<E> enumType)
	{
		long bitfield = 0;
		long validationCheck = 0;
		
		for (E member : enumType.getEnumConstants())
		{
			long bv = member.getBitfieldValue();
			
			if ((validationCheck & bv) != 0)
				throw new ImpossibleException("Two or more members of "+enumType.getName()+" overlap in the 1's of their binary values!!");
			validationCheck |= bv;
			
			if (set.contains(member))
			{
				bitfield |= bv;
			}
		}
		
		return bitfield;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO reduce() ^_^
	//Todo primitive versions :>  (primxp ftw! 0_0 XD)
	
	
	/**
	 * Guaranteed to propagate {@link SimpleIterator#drain()} (which is used instead of a close() method to explicitly close stream resources in some systems that auto-close on EOF!)
	 */
	public static <I, O> SimpleIterator<O> map(Mapper<I, O> mapper, SimpleIterator<? extends I> underlying)
	{
		return new SimpleIterator<O>()
		{
			@Override
			public O nextrp() throws StopIterationReturnPath
			{
				while (true)
				{
					I i = underlying.nextrp();  //propagate StopIterationReturnPath!  ^,^
					
					try
					{
						return mapper.f(i);
					}
					catch (FilterAwayReturnPath e)
					{
						continue;
					}
				}
			}
			
			@Override
			public void drain()
			{
				underlying.drain();
			}
		};
	}
	
	/**
	 * Guaranteed to propagate {@link SimpleIterator#drain()} (which is used instead of a close() method to explicitly close stream resources in some systems that auto-close on EOF!)
	 */
	public static <E> SimpleIterator<E> filter(Predicate<E> predicate, SimpleIterator<E> underlying)
	{
		return new SimpleIterator<E>()
		{
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				while (true)
				{
					E e = underlying.nextrp();  //propagate StopIterationReturnPath!  ^,^
					
					if (predicate.test(e))
						return e;
					else
						continue;
				}
			}
			
			@Override
			public void drain()
			{
				underlying.drain();
			}
		};
	}
	
	
	
	
	public static <I, O> SimpleIterator<O> map(Mapper<I, O> mapper, Iterator<I> underlying)
	{
		return map(mapper, SimpleIterator.simpleIterator(underlying));
	}
	
	public static <E> SimpleIterator<E> filter(Predicate<E> predicate, Iterator<E> underlying)
	{
		return filter(predicate, SimpleIterator.simpleIterator(underlying));
	}
	
	
	
	public static <I, O> SimpleIterable<O> mapped(Mapper<I, O> mapper, SimpleIterable<I> underlying)
	{
		return () -> map(mapper, underlying.simpleIterator());
	}
	
	public static <E> SimpleIterable<E> filtered(Predicate<E> predicate, SimpleIterable<E> underlying)
	{
		return () -> filter(predicate, underlying.simpleIterator());
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> SimpleIterable<E> filteredOPC(Predicate<E> predicate, SimpleIterable<E> underlying)
	{
		if (forAll(predicate, underlying))
			return underlying;
		else
			return () -> filter(predicate, underlying.simpleIterator());
	}
	
	
	public static <I, O> SimpleIterable<O> mapped(Mapper<I, O> mapper, Iterable<I> underlying)
	{
		return () -> map(mapper, underlying.iterator());
	}
	
	public static <E> SimpleIterable<E> filtered(Predicate<E> predicate, Iterable<E> underlying)
	{
		return () -> filter(predicate, underlying.iterator());
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> SimpleIterable<E> filteredOPC(Predicate<E> predicate, Iterable<E> underlying)
	{
		if (forAll(predicate, underlying))
			return SimpleIterable.simpleIterable(underlying);
		else
			return () -> filter(predicate, underlying.iterator());
	}
	
	
	
	
	
	
	
	
	
	
	//Old map/filter!
	@ThrowAwayValue
	public static <I, O> Collection<O> mapToNewCollection(Mapper<I, O> mapper, Iterable<? extends I> input)
	{
		if (input == null)
			return null;
		else if (input instanceof Set)
			return mapToNewSet(mapper, (Set<I>)input);
		else if (input instanceof List)
			return mapToNewList(mapper, (List<I>)input);
		else
			return mapToNewList(mapper, (List<I>)PolymorphicCollectionUtilities.anyToList(input));
	}
	
	@ThrowAwayValue
	public static <I, O> List<O> mapToNewList(Mapper<I, O> mapper, Iterable<? extends I> input)
	{
		return PolymorphicCollectionUtilities.anyToNewMutableVariablelengthList(mapped(mapper, (Iterable)input));
	}
	
	@ThrowAwayValue
	public static <I, O> Set<O> mapToNewSet(Mapper<I, O> mapper, Iterable<? extends I> input)
	{
		return PolymorphicCollectionUtilities.anyToNewMutableSet(mapped(mapper, (Iterable)input));
	}
	
	//The array comes before the input so the input can come last like all the other map/filter/reduce's, because the input might be chained from something else!
	@ThrowAwayValue
	public static <I, O, A extends I> O[] mapToNewArray(Mapper<I, O> mapper, Class<? super O> outputComponentType, A[] input)
	{
		return (O[])PolymorphicCollectionUtilities.anyToNewArray(mapped(mapper, (Iterable)Arrays.asList(input)), outputComponentType);
	}
	
	@ThrowAwayValue
	public static <I, O> O[] mapToNewArray(Mapper<I, O> mapper, Class<? super O> outputComponentType, Iterable<I> input)
	{
		return (O[])PolymorphicCollectionUtilities.anyToNewArray(mapped(mapper, input), outputComponentType);
	}
	
	@ThrowAwayValue
	public static <I, O, A extends I> Object[] mapToNewObjectArray(Mapper<I, O> mapper, A[] input)
	{
		return mapToNewArray(mapper, Object.class, input);
	}
	
	@ThrowAwayValue
	public static <I, O, A extends I> Object[] mapToNewObjectArrayV(Mapper<I, O> mapper, A... input)
	{
		return mapToNewArray(mapper, Object.class, input);
	}
	
	
	
	
	
	@ThrowAwayValue
	public static <E> Collection<E> filterToNewCollection(Predicate<? super E> filter, Iterable<E> input)
	{
		if (input == null)
			return null;
		
		Collection<E> newlist = input instanceof Set ? new HashSet<E>() : new ArrayList<E>(input instanceof Collection ? ((Collection)input).size() : 0);
		for (E e : input)
			if (filter.test(e))
				newlist.add(e);
		
		trimToSize(newlist);
		
		return newlist;
	}
	
	@ThrowAwayValue
	public static <E> List<E> filterToNewList(Predicate<? super E> filter, Iterable<E> input)
	{
		return (List<E>)filterToNewCollection(filter, input);
	}
	
	@ThrowAwayValue
	public static <E> Set<E> filterToNewSet(Predicate<? super E> filter, Set<E> input)
	{
		return (Set<E>)filterToNewCollection(filter, (Iterable<E>)input);
	}
	
	
	
	//Component types work nicelies with filter as opposed to map since the output type will (almost) always be the same as the input type! :D
	
	//Still one here though, for if you *want* to change the component type as it's passing through ;3
	@ThrowAwayValue
	public static <E, O> O[] filterToNewArray(Predicate<? super E> filter, Class<O> outputComponentType, E[] input)
	{
		if (input == null)
			return null;
		
		O[] newarray = (O[])Array.newInstance(outputComponentType, input.length);
		
		int i2 = 0;
		for (E e : input)
			if (filter.test(e))
				newarray[i2++] = (O)e;
		
		int newsize = i2;
		
		//Trim array :>
		O[] trimmedNewArray = null;
		{
			trimmedNewArray = (O[])Array.newInstance(outputComponentType, newsize);
			System.arraycopy(newarray, 0, trimmedNewArray, 0, trimmedNewArray.length);
		}
		
		return trimmedNewArray;
	}
	
	@ThrowAwayValue
	public static <E> E[] filterToNewArray(Predicate<? super E> filter, E[] input)
	{
		if (input == null)
			return null;
		
		return (E[])filterToNewArray(filter, input.getClass().getComponentType(), input);
	}
	
	@ThrowAwayValue
	public static <E> E[] filterToNewArrayV(Predicate<? super E> filter, E... input)
	{
		return filterToNewArray(filter, input);
	}
	
	
	
	
	@ThrowAwayValue
	public static <I, O> Collection<O> filterToNewCollectionSubtyped(Class<O> c, Iterable<I> input)
	{
		return (Collection<O>)filterToNewCollection(e -> c.isInstance(e), input);
	}
	
	@ThrowAwayValue
	public static <I, O> List<O> filterToNewListSubtyped(Class<O> c, Iterable<I> input)
	{
		return (List<O>)filterToNewList(e -> c.isInstance(e), input);
	}
	
	@ThrowAwayValue
	public static <I, O> Set<O> filterToNewSetSubtyped(Class<O> c, Set<I> input)
	{
		return (Set<O>)filterToNewSet(e -> c.isInstance(e), input);
	}
	
	
	
	
	
	/*
	@ThrowAwayValue
	public static <I, O> Collection<O> mapToNew(UnaryFunction<I, O> mapper, Iterable<? extends I> input)
	{
		if (input == null)
			return null;
		
		Collection<O> newcollection = null;
		{
			if (input instanceof Collection)
			{
				int inputSize = ((Collection)input).size();
				newcollection = input instanceof Set ? new HashSet<O>(inputSize) : new ArrayList<O>(inputSize);
			}
			else
			{
				newcollection = input instanceof Set ? new HashSet<O>() : new ArrayList<O>();
			}
		}
		
		for (I i : input)
			newcollection.add(mapper.f(i));
		
		trimToSize(newcollection);
		
		return newcollection;
	}
	
	@ThrowAwayValue
	public static <I, O> List<O> mapToNew(UnaryFunction<I, O> mapper, List<? extends I> input)
	{
		//Dynamically determines correct type! :D   (currently!)
		return (List<O>)mapToNew(mapper, (Iterable<I>)input);
	}
	
	@ThrowAwayValue
	public static <I, O> Set<O> mapToNew(UnaryFunction<I, O> mapper, Set<? extends I> input)
	{
		//Dynamically determines correct type! :D   (currently!)
		return (Set<O>)mapToNew(mapper, (Iterable<I>)input);
	}
	
	
	
	@ThrowAwayValue
	public static <I, O, A extends I> O[] mapToNewArray(UnaryFunction<I, O> mapper, Class<? super O> outputComponentType, A[] input)
	{
		if (input == null)
			return null;
		
		O[] newarray = (O[])Array.newInstance(outputComponentType, input.length);
		
		int len = input.length;
		for (int i = 0; i < len; i++)
			newarray[i] = mapper.f(input[i]);
		
		return newarray;
	}
	
	@ThrowAwayValue
	public static <I, O, A extends I> Object[] mapToNewObjectArray(UnaryFunction<I, O> mapper, A[] input)
	{
		return mapToNewArray(mapper, Object.class, input);
	}
	
	@ThrowAwayValue
	public static <I, O, A extends I> Object[] mapToNewObjectArrayV(UnaryFunction<I, O> mapper, A... input)
	{
		return mapToNewArray(mapper, Object.class, input);
	}
	
	
	
	
	
	@ThrowAwayValue
	public static <E> Collection<E> filterToNew(Predicate<? super E> filter, Iterable<E> input)
	{
		if (input == null)
			return null;
		
		Collection<E> newlist = input instanceof Set ? new HashSet<E>() : new ArrayList<E>(input instanceof Collection ? ((Collection)input).size() : 0);
		for (E e : input)
			if (filter.test(e))
				newlist.add(e);
		
		trimToSize(newlist);
		
		return newlist;
	}
	
	@ThrowAwayValue
	public static <E> List<E> filterToNew(Predicate<? super E> filter, List<E> input)
	{
		return (List<E>)filterToNew(filter, (Iterable<E>)input);
	}
	
	@ThrowAwayValue
	public static <E> Set<E> filterToNew(Predicate<? super E> filter, Set<E> input)
	{
		return (Set<E>)filterToNew(filter, (Iterable<E>)input);
	}
	
	
	
	//Component types work nicelies with filter as opposed to map since the output type will (almost) always be the same as the input type! :D
	
	//Still one here though, for if you *want* to change the component type as it's passing through ;3
	@ThrowAwayValue
	public static <E, O> O[] filterToNewArray(Predicate<? super E> filter, Class<O> outputComponentType, E[] input)
	{
		if (input == null)
			return null;
		
		O[] newarray = (O[])Array.newInstance(outputComponentType, input.length);
		
		int i2 = 0;
		for (E e : input)
			if (filter.test(e))
				newarray[i2++] = (O)e;
		
		int newsize = i2;
		
		//Trim array :>
		O[] trimmedNewArray = null;
		{
			trimmedNewArray = (O[])Array.newInstance(outputComponentType, newsize);
			System.arraycopy(newarray, 0, trimmedNewArray, 0, trimmedNewArray.length);
		}
		
		return trimmedNewArray;
	}
	
	@ThrowAwayValue
	public static <E> E[] filterToNewArray(Predicate<? super E> filter, E[] input)
	{
		if (input == null)
			return null;
		
		return (E[])filterToNewArray(filter, input.getClass().getComponentType(), input);
	}
	
	@ThrowAwayValue
	public static <E> E[] filterToNewArrayV(Predicate<? super E> filter, E... input)
	{
		return filterToNewArray(filter, input);
	}
	 */
	
	
	
	
	
	
	
	
	
	
	//Todo make these produce caching Views instead of actual concrete nonscalars! :33
	
	@ReadonlyValue
	public static <I, O> Collection<O> mapToCollection(Mapper<I, O> mapper, Iterable<? extends I> input)
	{
		if (input == null)
			return null;
		else if (input instanceof Set)
			return mapToSet(mapper, (Set<I>)input);
		else if (input instanceof List)
			return mapToList(mapper, (List<I>)input);
		else
			return mapToList(mapper, (List<I>)PolymorphicCollectionUtilities.anyToList(input));
	}
	
	@ReadonlyValue
	public static <I, O> List<O> mapToList(Mapper<I, O> mapper, Iterable<? extends I> input)
	{
		return asList(mapped(mapper, (Iterable)input));
	}
	
	@ReadonlyValue
	public static <I, O> List<O> mapToList(Mapper<I, O> mapper, Enumeration<? extends I> input)  //NOTE this is relied on to construct an in-memory snapshot!!  (eg, if the input is connected to a stream or database or etc. and becomes invalid, this method can be relied on to create a snapshot in memory)
	{
		return mapToList(mapper, enumerationToIterator(input));
	}
	
	@ReadonlyValue
	public static <I, O> List<O> mapToList(Mapper<I, O> mapper, Iterator<? extends I> input)  //NOTE this is relied on to construct an in-memory snapshot!!  (eg, if the input is connected to a stream or database or etc. and becomes invalid, this method can be relied on to create a snapshot in memory)
	{
		return mapToList(mapper, simpleIterator(input));
	}
	
	@ReadonlyValue
	public static <I, O> List<O> mapToList(Mapper<I, O> mapper, SimpleIterator<? extends I> input)  //NOTE this is relied on to construct an in-memory snapshot!!  (eg, if the input is connected to a stream or database or etc. and becomes invalid, this method can be relied on to create a snapshot in memory)
	{
		return asList(map(mapper, input));
	}
	
	@ReadonlyValue
	public static <I, O> List<O> mapToList(Mapper<I, O> mapper, I[] input)
	{
		return mapToList(mapper, asList(input));
	}
	
	
	
	@ReadonlyValue
	public static <I, O> Set<O> mapToSet(Mapper<I, O> mapper, Iterable<? extends I> input)
	{
		return PolymorphicCollectionUtilities.anyToNewMutableSet(mapped(mapper, (Iterable)input));
	}
	
	@ReadonlyValue
	public static <I, O> Set<O> mapToSet(Mapper<I, O> mapper, Iterator<? extends I> input)  //NOTE this is relied on to construct an in-memory snapshot!!  (eg, if the input is connected to a stream or database or etc. and becomes invalid, this method can be relied on to create a snapshot in memory)
	{
		return PolymorphicCollectionUtilities.anyToNewMutableSet(map(mapper, (Iterator)input));
	}
	
	@ReadonlyValue
	public static <I, O> Set<O> mapToSet(Mapper<I, O> mapper, SimpleIterator<? extends I> input)  //NOTE this is relied on to construct an in-memory snapshot!!  (eg, if the input is connected to a stream or database or etc. and becomes invalid, this method can be relied on to create a snapshot in memory)
	{
		return PolymorphicCollectionUtilities.anyToNewMutableSet(map(mapper, (SimpleIterator)input));
	}
	
	@ReadonlyValue
	public static <I, O> Set<O> mapToSet(Mapper<I, O> mapper, I[] input)
	{
		return mapToSet(mapper, asList(input));
	}
	
	
	
	@ReadonlyValue
	public static <I, O> Set<O> mapToSetThrowingOnDuplicates(Mapper<I, O> mapper, Iterable<? extends I> input)
	{
		return PolymorphicCollectionUtilities.anyToNewMutableSet(mapped(mapper, (Iterable)input), true);
	}
	
	@ReadonlyValue
	public static <I, O> Set<O> mapToSetThrowingOnDuplicates(Mapper<I, O> mapper, Iterator<? extends I> input)  //NOTE this is relied on to construct an in-memory snapshot!!  (eg, if the input is connected to a stream or database or etc. and becomes invalid, this method can be relied on to create a snapshot in memory)
	{
		return PolymorphicCollectionUtilities.anyToNewMutableSet(map(mapper, (Iterator)input), true);
	}
	
	@ReadonlyValue
	public static <I, O> Set<O> mapToSetThrowingOnDuplicates(Mapper<I, O> mapper, SimpleIterator<? extends I> input)  //NOTE this is relied on to construct an in-memory snapshot!!  (eg, if the input is connected to a stream or database or etc. and becomes invalid, this method can be relied on to create a snapshot in memory)
	{
		return PolymorphicCollectionUtilities.anyToNewMutableSet(map(mapper, (SimpleIterator)input), true);
	}
	
	@ReadonlyValue
	public static <I, O> Set<O> mapToSetThrowingOnDuplicates(Mapper<I, O> mapper, I[] input)
	{
		return mapToSetThrowingOnDuplicates(mapper, asList(input));
	}
	
	
	
	
	
	//The array comes before the input so the input can come last like all the other map/filter/reduce's, because the input might be chained from something else!
	@ReadonlyValue
	public static <I, O, A extends I> O[] mapToArray(Mapper<I, O> mapper, Class<? super O> outputComponentType, A[] input)
	{
		return (O[])PolymorphicCollectionUtilities.anyToNewArray(mapped(mapper, (Iterable)Arrays.asList(input)), outputComponentType);
	}
	
	@ReadonlyValue
	public static <I, O, A extends I> Object[] mapToObjectArray(Mapper<I, O> mapper, A[] input)
	{
		return mapToArray(mapper, Object.class, input);
	}
	
	@ReadonlyValue
	public static <I, O, A extends I> Object[] mapToObjectArrayV(Mapper<I, O> mapper, A... input)
	{
		return mapToArray(mapper, Object.class, input);
	}
	
	
	
	
	@ReadonlyValue
	public static <I, O> List<O> mapToListConcatenating(Mapper<I, List<O>> mapper, Iterable<? extends I> input)
	{
		List<O> output = new ArrayList<>();
		
		for (I i : input)
		{
			List<O> l;
			try
			{
				l = mapper.f(i);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			output.addAll(l);
		}
		
		return output;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@ReadonlyValue
	protected static <E> Collection<E> filterToCollection(Predicate<? super E> filter, Iterable<E> input, boolean setOutput)
	{
		if (input == null)
			return null;
		
		Collection<E> newlist = null;
		
		for (E e : input)
		{
			if (filter.test(e))
			{
				if (newlist == null)
				{
					newlist = setOutput ? singletonSet(e) : singletonList(e);
				}
				else
				{
					if (!(setOutput ? (newlist instanceof ArrayList) : (newlist instanceof HashSet)))  //isMutable()
						newlist = setOutput ? new HashSet<E>() : new ArrayList<E>(input instanceof Collection ? ((Collection)input).size() : 0);
					
					newlist.add(e);
				}
			}
		}
		
		return newlist == null ? (setOutput ? emptySet() : emptyList()) : newlist;
	}
	
	@ThrowAwayValue
	protected static <E> Collection<E> filterToMutableCollection(Predicate<? super E> filter, Iterable<E> input, boolean setOutput)
	{
		if (input == null)
			return null;
		
		Collection<E> newlist = setOutput ? new HashSet<E>() : new ArrayList<E>(input instanceof Collection ? ((Collection)input).size() : 0);
		for (E e : input)
			if (filter.test(e))
				newlist.add(e);
		
		return newlist;
	}
	
	
	@ReadonlyValue
	public static <E> Collection<E> filterToCollection(Predicate<? super E> filter, Iterable<E> input)
	{
		return filterToCollection(filter, input, false);
	}
	
	@ReadonlyValue
	public static <E> List<E> filterToList(Predicate<? super E> filter, Iterable<E> input)
	{
		return (List<E>)filterToCollection(filter, input, false);
	}
	
	@ReadonlyValue
	public static <E> Set<E> filterToSet(Predicate<? super E> filter, Iterable<E> input)
	{
		return (Set<E>)filterToCollection(filter, input, true);
	}
	
	@ReadonlyValue
	public static <E> List<E> filterToList(Predicate<? super E> filter, E[] input)
	{
		return filterToList(filter, asList(input));
	}
	
	@ReadonlyValue
	public static <E> Set<E> filterToSet(Predicate<? super E> filter, E[] input)
	{
		return filterToSet(filter, asList(input));
	}
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> filterToListOPC(Predicate<? super E> filter, List<E> input)
	{
		if (forAll(filter, input))
			return input;
		else
			return (List<E>)filterToCollection(filter, (Iterable<E>)input, false);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> Set<E> filterToSetOPC(Predicate<? super E> filter, Set<E> input)
	{
		if (forAll(filter, input))
			return input;
		else
			return (Set<E>)filterToCollection(filter, (Iterable<E>)input, true);
	}
	
	
	@ThrowAwayValue
	public static <E> List<E> filterToMutableList(Predicate<? super E> filter, List<E> input)
	{
		return (List<E>)filterToMutableCollection(filter, (Iterable<E>)input, false);
	}
	
	@ThrowAwayValue
	public static <E> Set<E> filterToMutableSet(Predicate<? super E> filter, Set<E> input)
	{
		return (Set<E>)filterToMutableCollection(filter, (Iterable<E>)input, true);
	}
	
	
	
	//Component types work nicelies with filter as opposed to map since the output type will (almost) always be the same as the input type! :D
	
	//Still one here though, for if you *want* to change the component type as it's passing through ;3
	@ReadonlyValue
	public static <E, O> O[] filterToArray(Predicate<? super E> filter, Class<O> outputComponentType, Collection<E> input)
	{
		if (input == null)
			return null;
		
		O[] newarray = (O[])Array.newInstance(outputComponentType, input.size());
		
		int i2 = 0;
		for (E e : input)
			if (filter.test(e))
				newarray[i2++] = (O)e;
		
		int newsize = i2;
		
		//Trim array :>
		O[] trimmedNewArray = null;
		{
			trimmedNewArray = (O[])Array.newInstance(outputComponentType, newsize);
			System.arraycopy(newarray, 0, trimmedNewArray, 0, trimmedNewArray.length);
		}
		
		return trimmedNewArray;
	}
	
	@ReadonlyValue
	public static <E, O> O[] filterToArray(Predicate<? super E> filter, Class<O> outputComponentType, E[] input)
	{
		if (input == null)
			return null;
		
		O[] newarray = (O[])Array.newInstance(outputComponentType, input.length);
		
		int i2 = 0;
		for (E e : input)
			if (filter.test(e))
				newarray[i2++] = (O)e;
		
		int newsize = i2;
		
		//Trim array :>
		O[] trimmedNewArray = null;
		{
			trimmedNewArray = (O[])Array.newInstance(outputComponentType, newsize);
			System.arraycopy(newarray, 0, trimmedNewArray, 0, trimmedNewArray.length);
		}
		
		return trimmedNewArray;
	}
	
	@ReadonlyValue
	public static <E> E[] filterToArray(Predicate<? super E> filter, E[] input)
	{
		if (input == null)
			return null;
		
		return (E[])filterToArray(filter, input.getClass().getComponentType(), input);
	}
	
	@ReadonlyValue
	public static <E> E[] filterToArrayV(Predicate<? super E> filter, E... input)
	{
		return filterToArray(filter, input);
	}
	
	
	
	
	@ReadonlyValue
	public static <I, O> Collection<O> filterToCollectionSubtyped(Class<O> c, Iterable<I> input)
	{
		return (Collection<O>)filterToCollection(e -> c.isInstance(e), input);
	}
	
	@ReadonlyValue
	public static <I, O> List<O> filterToListSubtyped(Class<O> c, Iterable<I> input)
	{
		return (List<O>)filterToList(e -> c.isInstance(e), input);
	}
	
	@ReadonlyValue
	public static <I, O> Set<O> filterToSetSubtyped(Class<O> c, Iterable<I> input)
	{
		return (Set<O>)filterToSet(e -> c.isInstance(e), input);
	}
	
	
	
	@ReadonlyValue
	public static <E> List<E> filterMiddleToList(Predicate<? super E> filter, List<E> input)
	{
		Interval i = filterMiddleToInterval(filter, input);
		return input.subList(i.getOffset(), i.getOffset()+i.getLength());
	}
	
	@ReadonlyValue
	public static <E> Interval filterMiddleToInterval(Predicate<? super E> filter, List<E> input)
	{
		int n = input.size();
		
		int start;
		{
			int i = 0;
			
			while (i < n)
			{
				if (filter.test(input.get(i)))
					break;
				
				i++;
			}
			
			start = i;
		}
		
		
		if (start == n)
			return new Interval(0, n);
		
		
		int end;
		{
			int i = n - 1;
			
			while (i >= 0)
			{
				if (filter.test(input.get(i)))
					break;
				
				i--;
			}
			
			end = i + 1;
		}
		
		
		return new Interval(start, end-start);
	}
	
	
	
	
	
	
	/**
	 * @return if we did anything—if we altered it in any way
	 */
	public static <E> boolean mapListInPlace(Mapper<E, E> mapper, List<E> list)
	{
		boolean didAnything = false;
		
		//iterating backwards lets us not worry about incrementing or not (i++ only if e is not filtered away), and can be far more efficient for the most common list implementation (ArrayList and related) when removing large numbers of elements :3
		
		if (isRandomAccessFast(list))
		{
			int n = list.size();
			
			for (int i = n - 1; i >= 0; i--)
			{
				E in = list.get(i);
				
				try
				{
					E out = mapper.f(in);
					
					if (out != in)
					{
						list.set(i, out);
						didAnything = true;
					}
				}
				catch (FilterAwayReturnPath exc)
				{
					list.remove(i);
					didAnything = true;
				}
			}
		}
		else
		{
			ListIterator<E> li = list.listIterator(list.size());
			
			while (li.hasPrevious())
			{
				E in = li.previous();
				
				try
				{
					E out = mapper.f(in);
					
					if (out != in)
					{
						li.set(out);
						didAnything = true;
					}
				}
				catch (FilterAwayReturnPath exc)
				{
					li.remove();
					didAnything = true;
				}
			}
		}
		
		return didAnything;
	}
	
	
	
	
	
	/**
	 * @return if we did anything—if we altered it in any way
	 */
	public static <E> boolean filterInPlace(Predicate<E> predicate, Iterable<E> iterable)
	{
		if (iterable instanceof List)
		{
			return mapListInPlace(e ->
			{
				if (predicate.test(e))
					return e;
				else
					throw FilterAwayReturnPath.I;
				
			}, (List<E>)iterable);
		}
		else
		{
			Iterator<E> i = iterable.iterator();
			
			E e = null;
			while (i.hasNext())
			{
				e = i.next();
				
				if (!predicate.test(e))
				{
					i.remove();
					return true;
				}
			}
			
			return false;
		}
	}
	
	/**
	 * @return if we did anything—if we altered it in any way
	 */
	public static <E> boolean removeMatching(Predicate<E> predicate, Iterable<E> iterable)
	{
		return filterInPlace(predicate.negate(), iterable);
	}
	
	
	
	
	
	
	//Todo
	//	public static <K, V> boolean mapDictInPlace(Mapper<Entry<K, V>, Entry<K, V>> mapper, Map<K, V> dict)
	//	{
	//		
	//	}
	
	/**
	 * @return if we did anything—if we altered it in any way
	 */
	public static <K, V> boolean filterDictInPlace(MapEntryPredicate<K, V> predicate, @WritableValue Map<K, V> dict)
	{
		boolean didAnything = false;
		
		for (Object k : dict.keySet().toArray())
		{
			K key = (K) k;
			
			if (!predicate.test(key, dict.get(key)))
			{
				dict.remove(key);
				didAnything = true;
			}
		}
		
		return didAnything;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> void doOn(UnaryProcedure<? super E> procedure, Iterator<E> input)
	{
		while (input.hasNext())
			procedure.f(input.next());
	}
	
	public static <E> void doOn(UnaryProcedure<? super E> procedure, SimpleIterator<E> input)
	{
		while (true)
		{
			E e;
			{
				try
				{
					e = input.nextrp();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			procedure.f(e);
		}
	}
	
	
	
	public static <E> void doOn(UnaryProcedure<? super E> procedure, SimpleIterable<E> input)
	{
		doOn(procedure, input.simpleIterator());
	}
	
	public static <E> void doOn(UnaryProcedure<? super E> procedure, Iterable<E> input)
	{
		for (E e : input)
			procedure.f(e);
	}
	
	public static <E> void doOn(UnaryProcedure<? super E> procedure, E[] input)
	{
		for (E e : input)
			procedure.f(e);
	}
	
	public static <E> void doOnV(UnaryProcedure<? super E> procedure, E... input)
	{
		doOn(procedure, input);
	}
	
	
	
	/**
	 * Applies a procedure to each element in a collection, and optionally removes some without damaging the iteration!  ^w^
	 * 
	 * @param procedure the procedure; return if we should remove the element just passed to you or not ^w^
	 * @return number of elements removed :>
	 */
	public static <E> int doOnAndRemoveSome(Predicate<? super E> procedure, Iterable<E> input)
	{
		Iterator<E> i = input.iterator();
		
		int numberRemoved = 0;
		
		while (i.hasNext())
		{
			boolean remove = procedure.test(i.next());
			if (remove)
			{
				i.remove();
				numberRemoved++;
			}
		}
		
		return numberRemoved;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> boolean any(Predicate<E> predicate, @CollectionValue E[] list)
	{
		for (E e : list)
			if (predicate.test(e))
				return true;
		return false;
	}
	
	public static <E> boolean all(Predicate<E> predicate, @CollectionValue E[] list)
	{
		for (E e : list)
			if (!predicate.test(e))
				return false;
		return true;
	}
	
	
	
	public static <E> boolean any(Predicate<E> predicate, @CollectionValue Iterable<E> list)
	{
		for (E e : list)
			if (predicate.test(e))
				return true;
		return false;
	}
	
	public static <E> boolean all(Predicate<E> predicate, @CollectionValue Iterable<E> list)
	{
		for (E e : list)
			if (!predicate.test(e))
				return false;
		return true;
	}
	
	
	
	/* <<<
	primxp
	public static boolean any(UnaryFunction_$$Prim$$_ToBoolean predicate, @CollectionValue _$$prim$$_[] list)
	{
		for (_$$prim$$_ e : list)
			if (predicate.f(e))
				return true;
		return false;
	}
	
	public static boolean all(UnaryFunction_$$Prim$$_ToBoolean predicate, @CollectionValue _$$prim$$_[] list)
	{
		for (_$$prim$$_ e : list)
			if (!predicate.f(e))
				return false;
		return true;
	}
	
	 */
	public static boolean any(UnaryFunctionBooleanToBoolean predicate, @CollectionValue boolean[] list)
	{
		for (boolean e : list)
			if (predicate.f(e))
				return true;
		return false;
	}
	
	public static boolean all(UnaryFunctionBooleanToBoolean predicate, @CollectionValue boolean[] list)
	{
		for (boolean e : list)
			if (!predicate.f(e))
				return false;
		return true;
	}
	
	public static boolean any(UnaryFunctionByteToBoolean predicate, @CollectionValue byte[] list)
	{
		for (byte e : list)
			if (predicate.f(e))
				return true;
		return false;
	}
	
	public static boolean all(UnaryFunctionByteToBoolean predicate, @CollectionValue byte[] list)
	{
		for (byte e : list)
			if (!predicate.f(e))
				return false;
		return true;
	}
	
	public static boolean any(UnaryFunctionCharToBoolean predicate, @CollectionValue char[] list)
	{
		for (char e : list)
			if (predicate.f(e))
				return true;
		return false;
	}
	
	public static boolean all(UnaryFunctionCharToBoolean predicate, @CollectionValue char[] list)
	{
		for (char e : list)
			if (!predicate.f(e))
				return false;
		return true;
	}
	
	public static boolean any(UnaryFunctionShortToBoolean predicate, @CollectionValue short[] list)
	{
		for (short e : list)
			if (predicate.f(e))
				return true;
		return false;
	}
	
	public static boolean all(UnaryFunctionShortToBoolean predicate, @CollectionValue short[] list)
	{
		for (short e : list)
			if (!predicate.f(e))
				return false;
		return true;
	}
	
	public static boolean any(UnaryFunctionFloatToBoolean predicate, @CollectionValue float[] list)
	{
		for (float e : list)
			if (predicate.f(e))
				return true;
		return false;
	}
	
	public static boolean all(UnaryFunctionFloatToBoolean predicate, @CollectionValue float[] list)
	{
		for (float e : list)
			if (!predicate.f(e))
				return false;
		return true;
	}
	
	public static boolean any(UnaryFunctionIntToBoolean predicate, @CollectionValue int[] list)
	{
		for (int e : list)
			if (predicate.f(e))
				return true;
		return false;
	}
	
	public static boolean all(UnaryFunctionIntToBoolean predicate, @CollectionValue int[] list)
	{
		for (int e : list)
			if (!predicate.f(e))
				return false;
		return true;
	}
	
	public static boolean any(UnaryFunctionDoubleToBoolean predicate, @CollectionValue double[] list)
	{
		for (double e : list)
			if (predicate.f(e))
				return true;
		return false;
	}
	
	public static boolean all(UnaryFunctionDoubleToBoolean predicate, @CollectionValue double[] list)
	{
		for (double e : list)
			if (!predicate.f(e))
				return false;
		return true;
	}
	
	public static boolean any(UnaryFunctionLongToBoolean predicate, @CollectionValue long[] list)
	{
		for (long e : list)
			if (predicate.f(e))
				return true;
		return false;
	}
	
	public static boolean all(UnaryFunctionLongToBoolean predicate, @CollectionValue long[] list)
	{
		for (long e : list)
			if (!predicate.f(e))
				return false;
		return true;
	}
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	public static <E> void check(UnaryFunction<E, ? extends RuntimeException> thrower, E[] list)
	{
		for (E element : list)
		{
			RuntimeException exc = thrower.f(element);
			if (exc != null)
				throw exc;
		}
	}
	
	public static <E> void check(UnaryFunction<E, ? extends RuntimeException> thrower, Iterable<E> list)
	{
		for (E element : list)
		{
			RuntimeException exc = thrower.f(element);
			if (exc != null)
				throw exc;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * You say permuter composition, I say cartesian product :3
	 * You say potayto, I say potahto :33
	 */
	public static <I, O> SimpleIterator<O> compositionPermuter(SimpleIterator<I> inputs, UnaryFunction<I, SimpleIterator<O>> secondPermuter)
	{
		return new SimpleIterator<O>()
		{
			SimpleIterator<O> currentSecondPermuter = null;
			
			@Override
			public O nextrp() throws StopIterationReturnPath
			{
				if (this.currentSecondPermuter == null)
				{
					I nextInput = inputs.nextrp();  //allow its Stop to propagate if produced! :D
					
					this.currentSecondPermuter = secondPermuter.f(nextInput);
					
					requireNonNull(this.currentSecondPermuter);
				}
				
				return this.currentSecondPermuter.nextrp();
			}
			
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param collection (note: if this is a {@link Set}, then the counts will all be 1 and there's no purpose to using this method XDDD )
	 */
	public static <E> Map<E, Integer> getCounts(Collection<E> collection)
	{
		Map<E, Integer> counts = new HashMap<>();
		
		for (E e : collection)
			counts.put(e, Collections.frequency(collection, e));
		
		return counts;
	}
	
	
	
	
	
	public static void putSomeV(Map source, Map dest, Object... keys)
	{
		for (Object key : keys)
		{
			Object v = source.get(key);
			
			if (v != null || source.containsKey(key))
				dest.put(key, v);
		}
	}
	
	public static void putAllIfNotNull(Map dest, Map source)
	{
		if (source != null && dest != null)
			dest.putAll(source);
	}
	
	
	
	
	
	
	/**
	 * @return The number actually read, or -1 for EOF!
	 */
	public static int readInto(Iterator source, Object[] dest, int offset, int maxLength)
	{
		int m = offset + maxLength;
		for (int i = offset; i < m; i++)
		{
			if (source.hasNext())
			{
				dest[i] = source.next();
			}
			else
			{
				if (i == offset)
					return -1;
				else
					return i - offset;
			}
		}
		
		return maxLength;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Note that this is shallow (eg, two lists's contents will be compared whether their equals() methods do that or not; but if their contents are lists themselves, those will be equals() compared ^^' )
	 * (This is necessary for {@link Set}s to be compared quickly :3 )
	 */
	public static boolean eqv(@Nullable Object a, @Nullable Object b)
	{
		if (a == null)
			return b == null;
		else if (b == null)
			return a == null;
		else
		{
			if (a instanceof Equivalenceable)
			{
				try
				{
					return ((Equivalenceable)a).equivalent(b);
				}
				catch (NotSupportedReturnPath exc)
				{
				}
			}
			
			if (b instanceof Equivalenceable)
			{
				try
				{
					return ((Equivalenceable)b).equivalent(a);
				}
				catch (NotSupportedReturnPath exc)
				{
				}
			}
			
			//else
			{
				if (isTrueAndNotNull(isThreadUnsafelyImmutable(a)) && isTrueAndNotNull(isThreadUnsafelyImmutable(b)))  //handles String, Primitive Wrappers, etc. :D
				{
					return eq(a, b);
				}
				else if (a instanceof Enum || b instanceof Enum)
				{
					return a == b;
				}
				else
				{
					if (a instanceof List && b instanceof List)
						return defaultListsEquivalent((List)a, (List)b, (aa, bb) -> eqv(aa, bb));
					
					else if (a instanceof Set && b instanceof Set)
						return defaultSetsEquivalent((Set)a, (Set)b, (aa, bb) -> eqv(aa, bb));
					
					else if (a instanceof Collection && b instanceof Collection)
						return defaultMultiSetsEquivalent_SmallNaive((Collection)a, (Collection)b, (aa, bb) -> eqv(aa, bb));  //Todo do heuristics and benchmarking and use asymptotically faster algorithms when that would actually increase performance.  (right now all I use this for is tiny sets of like 5 elements at most, mostly X3 )
					
					else if (a instanceof Map && b instanceof Map)
						return defaultMapsEquivalent((Map)a, (Map)b, (aa, bb) -> eqv(aa, bb));
					
					else
						throw new UnsupportedOperationException("eqv(a "+a.getClass().getName() + ", a " + b.getClass().getName()+")");
				}
			}
		}
	}
	
	
	public static boolean eqvMany(Iterable<Object> xs)
	{
		return transitiveReduce(xs, (a, b) -> eqv(a, b));
	}
	
	public static boolean eqvManyV(Object... xs)
	{
		return transitiveReduceV((a, b) -> eqv(a, b), xs);
	}
	
	
	
	
	
	public static <E> boolean eqvSets(Set<? extends E> a, Set<? extends E> b)
	{
		return eqv(a, b);
	}
	
	public static <E> boolean eqvAsSets(Iterable<? extends E> a, Iterable<? extends E> b)
	{
		return eqvSets(asSetThrowing(a), asSetThrowing(b));
	}
	
	/**
	 * Using {@link Collection#equals} even of two members of the same <code>runtime type</code> might require other things like say ordering if they are {@link List}s.
	 * This does proper multi-set equivalence.  Ie, order doesn't matter just like {@link Set}s, the only difference from {@link Set}s being that duplicate elements can be contained :3
	 * (You can think of sets as restricted multi-sets, and multi-set-equivalence is comparing the *count integer* not the *is-member/contains boolean* for each element, and sets just merely ever contain 0 or 1 of each element :3 )
	 */
	public static <E> boolean eqvMultiSets(Collection<? extends E> a, Collection<? extends E> b)
	{
		//We can't go based on the runtime type because the Java Collections Framework doesn't really do inheritance right x'D
		return eqvMultiSets(a, b, DefaultEqualityComparator.I);
	}
	
	public static <E> boolean eqvLists(List<? extends E> a, List<? extends E> b)
	{
		return eqv(a, b);
	}
	
	public static <K, V> boolean eqvMaps(Map<? extends K, ? extends V> a, Map<? extends K, ? extends V> b)
	{
		return eqv(a, b);
	}
	
	
	
	
	
	public static <E> boolean eqvMultiSets(Collection<? extends E> a, Collection<? extends E> b, EqualityComparator<E> equalityComparator)
	{
		//Todo do heuristics and benchmarking and use asymptotically faster algorithms when that would actually increase performance.  (right now all I use this for is tiny sets of like 5 elements at most, mostly X3 )
		return defaultMultiSetsEquivalent_SmallNaive(a, b, equalityComparator);
	}
	
	public static <E> boolean eqvLists(List<? extends E> a, List<? extends E> b, EqualityComparator<E> equalityComparator)
	{
		return defaultListsEquivalent(a, b, equalityComparator);
	}
	
	public static <K, V> boolean eqvMaps(Map<? extends K, ? extends V> a, Map<? extends K, ? extends V> b, EqualityComparator<V> valuesEqualityComparator)
	{
		return defaultMapsEquivalent(a, b, valuesEqualityComparator);
	}
	
	
	
	public static int hashCodeOfContents(@Nullable Object a)
	{
		if (a == null)
		{
			return 0;
		}
		else if (a instanceof Equivalenceable)
		{
			return ((Equivalenceable)a).hashCodeOfContents();
		}
		else if (isTrueAndNotNull(isThreadUnsafelyImmutable(a)))
		{
			return a.hashCode();
		}
		else
		{
			try
			{
				return hashCodeOfContentsGrandfathering(a);
			}
			catch (NotSupportedReturnPath exc)
			{
				throw new UnsupportedOperationException();
			}
		}
	}
	
	
	
	public static int hashCodeOfContentsGrandfathering(@Nonnull Object a) throws NotSupportedReturnPath
	{
		if (a instanceof List)
			return defaultListHashCode((List)a);
		if (a instanceof Set)
			return defaultSetHashCode((Set)a);
		if (a instanceof Collection)  //Must come after List and Set!!
			return defaultMultiSetHashCode((Set)a);
		
		if (a instanceof Map)
			return defaultMapHashCode((Map)a);
		
		throw NotSupportedReturnPath.I;
	}
	
	
	
	
	
	
	
	public static <E> boolean acyclicDeepEqvSets(Set<? extends E> a, Set<? extends E> b)
	{
		//TODO
		throw new NotYetImplementedException();
	}
	
	public static <E> boolean acyclicDeepEqvCollections(Collection<? extends E> a, Collection<? extends E> b)
	{
		return eqvMultiSets(a, b, PolymorphicCollectionUtilities.acyclicDeepEqv);
	}
	
	public static <E> boolean acyclicDeepEqvLists(List<? extends E> a, List<? extends E> b)
	{
		return eqvLists(a, b, PolymorphicCollectionUtilities.acyclicDeepEqv);
	}
	
	public static <K, V> boolean acyclicDeepEqvMaps(Map<? extends K, ? extends V> a, Map<? extends K, ? extends V> b)
	{
		return eqvMaps(a, b, PolymorphicCollectionUtilities.acyclicDeepEqv);
	}
	
	
	
	
	
	
	
	
	
	
	//Todo X'DDD
	//	public static boolean graphTheoreticEqualityWithImplicitAnchorNodeness(Object anchorNodeA, Object anchorNodeB)
	//	{
	//		FINALLY HANDLE SET/COLLECTION PROPERLY X'DDDD
	//	}
	//
	//	@ImplementationTransparency
	//	public static boolean graphTheoreticEqualityWithImplicitAnchorNodeness(Object anchorNodeA, Object anchorNodeB, GraphEquivalenceSession activeGraphEquivalenceSession)
	//	{
	//
	//	}
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <K, V> Map<V, K> inverseMapOPC(@ReadonlyValue Map<K, V> map) throws NonForwardInjectiveMapException
	{
		return inverseMapOP(map);  //Todo weak caching impl for immutable maps?
	}
	
	
	@ThrowAwayValue
	public static <K, V> Map<V, K> inverseMapOP(@ReadonlyValue Map<K, V> map) throws NonForwardInjectiveMapException
	{
		//		Map<V, K> inverse = new HashMap<>();
		//		
		//		for (Entry<K, V> e : map.entrySet())
		//		{
		//			if (inverse.containsKey(e.getValue()))
		//				throw new NonForwardInjectiveMapException();
		//			else
		//				inverse.put(e.getValue(), e.getKey());
		//		}
		//		
		//		return inverse;
		
		return inverseMapOP(map.keySet(), map::get);
	}
	
	
	
	public static <K, V> Map<V, K> inverseMapOP(Iterable<K> keys, Mapper<K, V> mapper) throws NonForwardInjectiveMapException
	{
		Map<V, K> inverse = new HashMap<>();
		
		for (K key : keys)
		{
			V value;
			try
			{
				value = mapper.f(key);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			if (inverse.containsKey(value))
				throw new NonForwardInjectiveMapException("Duplicates of value-in-input / key-in-output "+repr(value));
			else
				inverse.put(value, key);
		}
		
		return inverse;
	}
	
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <V> Map<V, Integer> inverseListmapOPC(@ReadonlyValue List<V> map) throws NonForwardInjectiveMapException
	{
		return inverseListmapOP(map);  //Todo weak caching impl for immutable maps?
	}
	
	
	@ThrowAwayValue
	public static <V> Map<V, Integer> inverseListmapOP(@ReadonlyValue List<V> map) throws NonForwardInjectiveMapException
	{
		//		Map<V, Integer> inverse = new HashMap<>();
		//		
		//		for (Entry<V> e : map.entrySet())
		//		{
		//			if (inverse.containsKey(e.getValue()))
		//				throw new NonForwardInjectiveMapException();
		//			else
		//				inverse.put(e.getValue(), e.getKey());
		//		}
		//		
		//		return inverse;
		
		return inverseListmapOP(map.size(), map::get);
	}
	
	
	
	public static <V> Map<V, Integer> inverseListmapOP(int size, Mapper<Integer, V> mapper) throws NonForwardInjectiveMapException
	{
		Map<V, Integer> inverse = new HashMap<>();
		
		for (int key = 0; key < size; key++)
		{
			V value;
			try
			{
				value = mapper.f(key);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			if (inverse.containsKey(value))
				throw new NonForwardInjectiveMapException("Duplicates of value-in-input / key-in-output "+repr(value));
			else
				inverse.put(value, key);
		}
		
		return inverse;
	}
	
	
	
	
	
	
	/**
	 * The inverse function of this is {@link #inverseGeneralMapOP(Map)} :3
	 */
	@ThrowAwayValue
	public static <V, K> Map<V, Set<K>> inverseMapGeneralOP(Map<K, V> inputMap)
	{
		return inverseMapGeneralOP(inputMap.keySet(), inputMap::get);
	}
	
	/**
	 * The inverse function of this is {@link #inverseGeneralMapOP(Iterable, Mapper)} :3
	 */
	@ThrowAwayValue
	public static <V, K> Map<V, Set<K>> inverseMapGeneralOP(Iterable<K> keys, Mapper<K, V> mapper)
	{
		Map<V, Set<K>> inverse = new HashMap<>();
		
		for (K key : keys)
		{
			try
			{
				getOrCreate(inverse, mapper.f(key), HashSet::new).add(key);
			}
			catch (FilterAwayReturnPath exc)
			{
			}
		}
		
		return inverse;
	}
	
	
	
	
	
	
	/**
	 * The inverse function of this is {@link #inverseMapGeneralOP(Map)} :3
	 */
	@ThrowAwayValue
	public static <K, V> Map<V, K> inverseGeneralMapOP(@ReadonlyValue Map<K, Set<V>> generalMap) throws NonForwardInjectiveMapException
	{
		//		Map<V, K> inverse = new HashMap<>();
		//		
		//		for (Entry<K, V> e : map.entrySet())
		//		{
		//			if (inverse.containsKey(e.getValue()))
		//				throw new NonForwardInjectiveMapException();
		//			else
		//				inverse.put(e.getValue(), e.getKey());
		//		}
		//		
		//		return inverse;
		
		return inverseGeneralMapOP(generalMap.keySet(), generalMap::get);
	}
	
	
	
	/**
	 * The inverse function of this is {@link #inverseMapGeneralOP(Iterable, Mapper)} :3
	 */
	public static <K, V> Map<V, K> inverseGeneralMapOP(Iterable<K> keys, Mapper<K, Set<V>> mapper) throws NonForwardInjectiveMapException
	{
		Map<V, K> inverse = new HashMap<>();
		
		for (K key : keys)
		{
			Set<V> values;
			try
			{
				values = mapper.f(key);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			for (V value : values)
			{
				if (inverse.containsKey(value))
					throw new NonForwardInjectiveMapException("Duplicates of value-in-input / key-in-output "+repr(value));
				else
					inverse.put(value, key);
			}
		}
		
		return inverse;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * This is its own inverse!  It's a mathematical involution!  :>
	 */
	@ThrowAwayValue
	public static <V, K> Map<V, Set<K>> inverseGeneralMapGeneralOP(Map<K, Set<V>> inputMap)
	{
		return inverseGeneralMapGeneralOP(inputMap.keySet(), inputMap::get);
	}
	
	/**
	 * This is its own inverse!  It's a mathematical involution!  :>
	 */
	@ThrowAwayValue
	public static <V, K> Map<V, Set<K>> inverseGeneralMapGeneralOP(Iterable<K> keys, Mapper<K, Set<V>> mapper)
	{
		Map<V, Set<K>> inverse = new HashMap<>();
		
		for (K key : keys)
		{
			Set<V> values;
			try
			{
				values = mapper.f(key);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			for (V value : values)
			{
				getOrCreate(inverse, value, HashSet::new).add(key);
			}
		}
		
		return inverse;
	}
	
	
	
	
	
	
	
	
	public static <E> boolean hasDuplicates(Iterable<E> input)
	{
		Set<E> seen = new HashSet<>();
		
		for (E e : input)
		{
			if (seen.contains(e))
			{
				return true;
			}
			else
			{
				seen.add(e);
			}
		}
		
		return false;
	}
	
	public static <E> Set<E> findDuplicates(Iterable<E> input)
	{
		Set<E> seen = new HashSet<>();
		Set<E> duplicates = new HashSet<>();
		
		for (E e : input)
		{
			if (seen.contains(e))
			{
				duplicates.add(e);
			}
			else
			{
				seen.add(e);
			}
		}
		
		return duplicates;
	}
	
	public static <E> Iterable<E> requireNoDuplicates(Iterable<E> input) throws DuplicatesException
	{
		Set<E> duplicates = findDuplicates(input);
		
		if (!duplicates.isEmpty())
			throw new DuplicatesException("Duplicate elements!: {"+reprListContentsSingleLine(duplicates)+"}    (the number of times each duplicate appears is not represented here)");
		
		return input;
	}
	
	public static <E> void requireNoIntersection(Set<E> a, Set<E> b) throws AlreadyExistsException
	{
		Set<E> conflicts = intersection(a, b);
		
		if (!conflicts.isEmpty())
			throw new AlreadyExistsException("Conflicts detected!: {"+reprListContentsSingleLine(conflicts)+"}");
	}
	
	
	
	
	
	public static <E> boolean hasAdjacentDuplicates(Iterable<E> input)
	{
		return hasAdjacentDuplicates(input, getDefaultEqualityComparator());
	}
	
	public static <E> boolean hasAdjacentDuplicates(Iterable<E> input, EqualityComparator<? super E> equals)
	{
		boolean first = true;
		E prev = null;
		
		for (E e : input)
		{
			if (first)
				first = false;
			else if (equals.equals(prev, e))
				return true;
			
			prev = e;
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@ThrowAwayValue
	public static <I,O> Map<O, Integer> getColumnHeadersToIndexesMap(SimpleTable<I> table, int headerRowIndex) throws NonForwardInjectiveMapException
	{
		return getColumnHeadersToIndexesMap(table, headerRowIndex, null);
	}
	
	@ThrowAwayValue
	public static <I,O> Map<O, Integer> getColumnHeadersToIndexesMap(SimpleTable<I> table, int headerRowIndex, @Nullable Mapper<I, O> columnHeaderMapperAndFilter) throws NonForwardInjectiveMapException
	{
		return getColumnHeadersToIndexesMap(table.rowToList(headerRowIndex), columnHeaderMapperAndFilter);
	}
	
	
	
	@ThrowAwayValue
	public static <I,O> Map<O, Integer> getColumnHeadersToIndexesMap(List<I> tableHeaders, @Nullable Mapper<I, O> columnHeaderMapperAndFilter) throws NonForwardInjectiveMapException
	{
		Map<O, Integer> headersToIndexes = new HashMap<>();
		
		int columnIndex = 0;
		for (I header : tableHeaders)
		{
			try
			{
				O mappedHeader = columnHeaderMapperAndFilter == null ? (O)header : columnHeaderMapperAndFilter.f(header);
				
				if (headersToIndexes.containsKey(mappedHeader))
					throw new NonForwardInjectiveMapException("Conflict on header value: "+repr(header)+"  (mapped to "+repr(mappedHeader)+")");
				headersToIndexes.put(mappedHeader, columnIndex);
			}
			catch (FilterAwayReturnPath exc)
			{
			}
			
			columnIndex++;
		}
		
		return headersToIndexes;
	}
	
	
	
	@ThrowAwayValue
	public static Map<String, Integer> getColumnHeadersToIndexesMapStringsDefaultLowercasing(List<String> tableHeaders) throws NonForwardInjectiveMapException
	{
		return CollectionUtilities.getColumnHeadersToIndexesMap(tableHeaders, h ->
		{
			h = h.trim();
			
			if (h.isEmpty())
				throw FilterAwayReturnPath.I;
			
			return h.toLowerCase();
		});
	}
	
	
	@ThrowAwayValue
	public static Map<String, Integer> getColumnHeadersToIndexesMapStringsDefaultLowercasingValidatingExactCaseInsensitively(List<String> tableHeaders, String... expectedHeaders) throws NonForwardInjectiveMapException
	{
		return getColumnHeadersToIndexesMapStringsDefaultLowercasingValidatingExactCaseInsensitively(tableHeaders, PolymorphicCollectionUtilities.anyToSet(expectedHeaders));
	}
	
	@ThrowAwayValue
	public static Map<String, Integer> getColumnHeadersToIndexesMapStringsDefaultLowercasingValidatingExactCaseInsensitively(List<String> tableHeaders, Set<String> expectedHeaders) throws NonForwardInjectiveMapException
	{
		Map<String, Integer> h = getColumnHeadersToIndexesMapStringsDefaultLowercasing(tableHeaders);
		
		Set<String> expectedHeadersLowercased = mapToNewSet(String::toLowerCase, expectedHeaders);
		
		Set<String> actualHeadersLowercased = h.keySet();
		
		if (!expectedHeadersLowercased.equals(actualHeadersLowercased))
			throw new GenericDataStructuresFormatException("Headers didn't match up--even case insensitively!!: (Actual="+reprSingleLine(actualHeadersLowercased)+", Expected="+reprSingleLine(expectedHeadersLowercased)+")");
		
		return h;
	}
	
	
	
	
	
	
	
	
	
	public static <K, V> MapFunctionalIterable<K, V> asFunctionalIterable(Map<K, V> map)
	{
		return iteree ->
		{
			for (Entry<K, V> entry : map.entrySet())
			{
				ContinueSignal r = iteree.observe(entry.getKey(), entry.getValue());
				
				if (r == null)
				{
					logBug();
					r = ContinueSignal.Continue;
				}
				
				if (r == ContinueSignal.Stop)
					return SuccessfulIterationStopType.StoppedPrematurely;
			}
			
			return SuccessfulIterationStopType.CompletedNaturally;
		};
	}
	
	
	public static <E> CollectionFunctionalIterable<E> asFunctionalIterable(Iterable<E> collection)
	{
		return iteree ->
		{
			for (E element : collection)
			{
				ContinueSignal r = iteree.observe(element);
				
				if (r == null)
				{
					logBug();
					r = ContinueSignal.Continue;
				}
				
				if (r == ContinueSignal.Stop)
					return SuccessfulIterationStopType.StoppedPrematurely;
			}
			
			return SuccessfulIterationStopType.CompletedNaturally;
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//< Bit Settssssssss!! :DDD
	
	//TODO TESTTTTTTT THISSSSSSS!!
	@LiveValue
	public static Set<Integer> bitSetAsIndexSetInOrder(@LiveValue BitSet bitSet)
	{
		//Todo make this a separate class that's inspectable so we can see the bitSet inside, then support SUPER efficient addAll()/removeAll()/retainAll() bulk operations for when combining two of these!! :DDD
		return new AbstractSet<Integer>()
		{
			@Override
			public boolean isEmpty()
			{
				return bitSet.isEmpty();
			}
			
			@Override
			public int size()
			{
				return bitSet.cardinality();
			}
			
			@Override
			public Iterator<Integer> iterator()
			{
				return iteratorOverOneBitsInBitSetInOrder(bitSet);
			}
			
			
			
			@Override
			public boolean contains(Object o)
			{
				if (MathUtilities.isInteger(o))
				{
					int index;
					try
					{
						index = MathUtilities.safeCastIntegerToS32(o);
					}
					catch (OverflowException exc)
					{
						return false;
					}
					
					return bitSet.get(index);
				}
				else
				{
					return false;
				}
			}
			
			@Override
			public boolean remove(Object o)
			{
				if (MathUtilities.isInteger(o))
				{
					int index;
					try
					{
						index = MathUtilities.safeCastIntegerToS32(o);
					}
					catch (OverflowException exc)
					{
						return false;
					}
					
					boolean wasSet = bitSet.get(index);
					bitSet.clear(index);
					return wasSet;
				}
				else
				{
					return false;
				}
			}
			
			@Override
			public boolean add(Integer e)
			{
				int index = e;
				
				boolean wasSet = bitSet.get(index);
				bitSet.set(index);
				return !wasSet;
			}
			
			
			@Override
			public void clear()
			{
				bitSet.clear();
			}
		};
	}
	
	
	public static Iterable<Integer> bitSetAsBooleanIterableInOrder(BitSet bitSet)
	{
		return () -> iteratorOverOneBitsInBitSetInOrder(bitSet);
	}
	
	
	public static Iterator<Integer> iteratorOverOneBitsInBitSetInOrder(BitSet bitSet)
	{
		return new Iterator<Integer>()
		{
			int nextIndex = bitSet.nextSetBit(0);  //note: may return what was passed if that bit is set!!
			
			@Override
			public Integer next()
			{
				if (this.nextIndex < 0)
					throw new NoSuchElementException();
				
				
				
				int currentIndex = this.nextIndex;
				
				//Increment!
				{
					if (currentIndex == Integer.MAX_VALUE)
						throw new OverflowException();
					
					this.nextIndex = bitSet.nextSetBit(currentIndex+1);
				}
				
				return currentIndex;
			}
			
			@Override
			public boolean hasNext()
			{
				return this.nextIndex >= 0;
			}
		};
	}
	
	// Bit Settssssssss!! :DDD >
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> boolean startsWithLists(@Nonnull List<E> longer, @Nonnull List<E> shorter)
	{
		int ln = longer.size();
		int sn = shorter.size();
		
		if (ln < sn)
			return false;
		else if (ln == sn)
			return longer.equals(shorter);
		else
		{
			Iterator<? extends E> il = longer.iterator();
			Iterator<? extends E> is = shorter.iterator();
			
			boolean lHasElement = false;
			boolean sHasElement = false;
			E lElement = null;
			E sElement = null;
			
			while (true)
			{
				lHasElement = il.hasNext();
				sHasElement = is.hasNext();
				
				if (!lHasElement && sHasElement)
					throw new ConcurrentModificationException("sizes were checked to be equal, but iterators produce unequal numbers of elements; either something is modifying the list un-thread-safely!, or it's just a really big bug in the list code XD''");
				else if (lHasElement && !sHasElement)
					break;
				else if (lHasElement && sHasElement)
				{
					assert sHasElement;
					
					lElement = il.next();
					sElement = is.next();
					
					if (!eq(lElement, sElement))
						return false;
				}
				else
				{
					break;
				}
			}
			
			return true;
		}
	}
	
	
	
	
	
	
	
	
	public static <E> boolean endsWithLists(@Nonnull List<E> longer, @Nonnull List<E> shorter)
	{
		int ln = longer.size();
		int sn = shorter.size();
		
		if (ln < sn)
			return false;
		else if (ln == sn)
			return longer.equals(shorter);
		else
		{
			ListIterator<? extends E> il = longer.listIterator(ln);
			ListIterator<? extends E> is = shorter.listIterator(sn);
			
			boolean lHasElement = false;
			boolean sHasElement = false;
			E lElement = null;
			E sElement = null;
			
			while (true)
			{
				lHasElement = il.hasPrevious();
				sHasElement = is.hasPrevious();
				
				if (!lHasElement && sHasElement)
					throw new ConcurrentModificationException("sizes were checked to be equal, but iterators produce unequal numbers of elements; either something is modifying the list un-thread-safely!, or it's just a really big bug in the list code XD''");
				else if (lHasElement && !sHasElement)
					break;
				else if (lHasElement && sHasElement)
				{
					assert sHasElement;
					
					lElement = il.previous();
					sElement = is.previous();
					
					if (!eq(lElement, sElement))
						return false;
				}
				else
				{
					break;
				}
			}
			
			return true;
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * This is essentially the inverse of {@link Map#entrySet()} :DD
	 * But (like you'd expect) it's not a live view!
	 */
	@ThrowAwayValue
	public static <K, V> Map<K, V> toMap(Collection<Entry<K, V>> entries) throws NonReverseInjectiveMapException
	{
		Map<K, V> map = new HashMap<>();
		
		for (Entry<K, V> e : entries)
		{
			K k = e.getKey();
			
			if (map.containsKey(k))
				throw new NonReverseInjectiveMapException("Duplicate keys!: "+repr(k));
			else
				map.put(k, e.getValue());
		}
		
		return map;
	}
	
	
	
	
	public static <K, V> List<Entry<K, V>> newkvlist(Object... keysAndValues)
	{
		if ((keysAndValues.length % 2) != 0)
			throw new IllegalArgumentException();
		
		
		List<Entry<K, V>> emap = new ArrayList<>();
		
		for (int i = 0; i < keysAndValues.length; i += 2)
			emap.add(new SimpleEntry(keysAndValues[i], keysAndValues[i+1]));
		
		return emap;
	}
	
	
	/**
	 * Not guaranteed to preserve ordering!!
	 */
	public static <K, V> Collection<Entry<K, V>> newemap(Object... keysAndValues)
	{
		return newkvlist(keysAndValues);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> expandListOfRowsToSmallestWidth(List<List<E>> rows, E filler)
	{
		int h = rows.size();
		
		if (h == 0)
			return newTable();
		
		int largestRowSize = greatestMap(List::size, rows);
		
		if (largestRowSize == 0)
			return newTable();
		
		
		int w = largestRowSize;
		
		SimpleTable<E> table = newTableNullfilled(w, h);
		
		
		for (int r = 0; r < h; r++)
		{
			List<E> row = rows.get(r);
			
			int filled = least(w, row.size());
			for (int c = 0; c < filled; c++)
			{
				table.setCellContents(c, r, row.get(c));
			}
			
			int fillered = w - filled;
			for (int c = 0; c < fillered; c++)
			{
				table.setCellContents(filled + c, r, filler);
			}
		}
		
		
		return table;
	}
	
	
	
	
	
	
	
	public static <E> List<E> repeatedList(E value, int count)
	{
		Object[] a = new Object[count];
		for (int i = 0; i < count; i++)
			a[i] = value;
		return (List<E>)asList(a);
	}
	
	
	public static <E> void fillBySetting(List<? super E> list, E e)
	{
		fillBySetting(list, 0, list.size(), e);
		//Collections.fill(list, e);
	}
	
	public static <E> void fillBySetting(List<? super E> list, int start, int count, E value)
	{
		if (count != 0)
		{
			rangeCheckInterval(list.size(), start, start + count);
			
			if (list instanceof ListWithFill)
			{
				((ListWithFill)list).fillBySetting(start, count, value);
			}
			else
			{
				if (start == 0 && count == list.size())
					Collections.fill(list, value);
				else
					Collections.fill(list.subList(start, start+count), value);
			}
		}
	}
	
	
	
	public static <E> void fillByAdding(Collection<? super E> collection, E fill, int number)
	{
		//Todo make a trait interface for this :3
		
		if (collection instanceof Vector && fill == null)
		{
			((Vector)collection).setSize(collection.size() + number);
		}
		else
		{
			for (int i = 0; i < number; i++)
				collection.add(fill);
		}
	}
	
	
	
	
	
	
	/**
	 * Contracts contiguous strings of duplicates into {@link PairOrdered}s :3
	 * 
	 * @return a list of the original elements, or {@link PairOrdered}(element, numberOfDuplicates)
	 */
	public static <E> List<Object> contractDuplicatesOPC(List<E> list)
	{
		return contractDuplicatesOPC(list, getDefaultEqualityComparator());
	}
	
	public static <E> List<Object> contractDuplicatesOPC(List<E> list, EqualityComparator<? super E> equals)
	{
		int runStart = -1;
		int i = 0;
		E prev = null;
		
		List<Object> output = null;
		
		for (E e : list)
		{
			if (i == 0)
			{
				prev = e;
			}
			else
			{
				if (output == null)
				{
					if (equals.equals(prev, e))
					{
						runStart = i-1;
						output = new ArrayList<>(list.size());
						output.addAll(list.subList(0, i-1));
					}
					else
					{
						prev = e;
					}
				}
				else
				{
					if (equals.equals(prev, e))
					{
						if (runStart == -1)
							runStart = i-1;
					}
					else
					{
						if (runStart == -1)
						{
							output.add(prev);
						}
						else
						{
							output.add(pair(prev, i - runStart));
							runStart = -1;
						}
						
						prev = e;
					}
				}
			}
			
			i++;
		}
		
		if (output == null)
		{
			return (List)list;
		}
		else
		{
			if (runStart == -1)
			{
				if (i != 0)
					output.add(prev);
			}
			else
			{
				output.add(pair(prev, i - runStart));
			}
			
			return output;
		}
	}
	
	
	
	
	public static <E> SimpleIterator<Object> contractDuplicates(SimpleIterator<E> input)
	{
		return contractDuplicates(input, getDefaultEqualityComparator());
	}
	
	public static <E> SimpleIterator<Object> contractDuplicates(SimpleIterator<E> input, EqualityComparator<? super E> equals)
	{
		return new SimpleIterator<Object>()
		{
			boolean eof = false;
			int runLength = 0;
			E runRepresentativeOrPrevious;
			
			
			@Override
			public Object nextrp() throws StopIterationReturnPath
			{
				if (eof)
					throw StopIterationReturnPath.I;
				
				
				while (true)
				{
					asrt(!eof);
					
					final E current;
					
					try
					{
						current = input.nextrp();
					}
					catch (StopIterationReturnPath exc)
					{
						final int length = runLength;
						
						final boolean bof = length == 0;
						
						if (bof)
						{
							throw StopIterationReturnPath.I;
						}
						else
						{
							final E prev = runRepresentativeOrPrevious;
							
							eof = true;
							runLength = -1;  //it doesn't hurt X3
							runRepresentativeOrPrevious = null;  //for garbage collection!
							return length == 1 ? prev : pair(prev, length);
						}
					}
					
					
					
					
					
					final int length = runLength;
					final boolean bof = length == 0;
					
					if (bof)
					{
						runRepresentativeOrPrevious = current;
						runLength = 1;
					}
					else
					{
						final E prev = runRepresentativeOrPrevious;
						
						final boolean matches = equals.equals(current, prev);
						
						if (matches)
						{
							runLength = length + 1;
						}
						else
						{
							runLength = 1;
							runRepresentativeOrPrevious = current;
							return length == 1 ? prev : pair(prev, length);
						}
					}
				}
			}
		};
	}
	
	
	
	
	
	
	
	
	
	public static <E> List<E> sortedWithListOrdering(@ReadonlyValue Collection<E> input, List<E> ordering)
	{
		return sorted(input, (a, b) -> compareWithListOrdering(ordering, a, b));
	}
	
	public static <E> void sortWithListOrdering(@WritableValue List<E> list, List<E> ordering)
	{
		sort(list, (a, b) -> compareWithListOrdering(ordering, a, b));
	}
	
	
	
	public static <T> int compareWithListOrdering(List<T> ordering, T a, T b)
	{
		int ia = ordering.indexOf(a);
		int ib = ordering.indexOf(b);
		
		return ia == -1 ? (ib == -1 ? 0 : 1) : (ib == -1 ? -1 : cmp(ia, ib));
	}
	
	public static <T> int compareWithListOrderingChainable(int prev, List<T> ordering, T a, T b)
	{
		return prev != 0 ? prev : compareWithListOrdering(ordering, a, b);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <I, O> SimpleTable<O> mapTableOP(UnaryFunction<I, O> mapper, SimpleTable<I> input)
	{
		int w = input.getNumberOfColumns();
		int h = input.getNumberOfRows();
		
		SimpleTable<O> output = newTableNullfilled(w, h);
		
		for (int r = 0; r < h; r++)
		{
			for (int c = 0; c < w; c++)
			{
				output.setCellContents(c, r, mapper.f(input.getCellContents(c, r)));
			}
		}
		
		return output;
	}
	
	
	
	
	
	public static <E> SimpleTable<E> transposeOP(SimpleTable<E> input)
	{
		int wi = input.getNumberOfColumns();
		int hi = input.getNumberOfRows();
		
		SimpleTable<E> output = newTableNullfilled(hi, wi);
		
		for (int yi = 0; yi < hi; yi++)
		{
			for (int xi = 0; xi < wi; xi++)
			{
				int xo = yi;
				int yo = xi;
				
				output.setCellContents(xo, yo, input.getCellContents(xi, yi));
			}
		}
		
		return output;
	}
	
	
	
	/**
	 * Merge a table into one column! :>
	 * @param merger order is preserved :3
	 */
	public static <E> SimpleTable<E> mergeColumnsOP(SimpleTable<E> input, BinaryFunction<E, E, E> merger)
	{
		int wi = input.getNumberOfColumns();
		int h = input.getNumberOfRows();
		
		if (wi == 0 || h == 0)
		{
			return newTable();
		}
		else
		{
			SimpleTable<E> output = newTableNullfilled(1, h);
			
			for (int y = 0; y < h; y++)
			{
				E e = input.getCellContents(0, y);
				
				for (int xi = 1; xi < wi; xi++)
				{
					E ee = input.getCellContents(xi, y);
					
					e = merger.f(e, ee);
				}
				
				output.setCellContents(0, y, e);
			}
			
			return output;
		}
	}
	
	
	
	/**
	 * Merge a table into one row! :>
	 * @param merger order is preserved :3
	 */
	public static <E> SimpleTable<E> mergeRowsOP(SimpleTable<E> input, BinaryFunction<E, E, E> merger)
	{
		int w = input.getNumberOfColumns();
		int hi = input.getNumberOfRows();
		
		if (w == 0 || hi == 0)
		{
			return newTable();
		}
		else
		{
			SimpleTable<E> output = newTableNullfilled(w, 1);
			
			for (int x = 0; x < w; x++)
			{
				E e = input.getCellContents(x, 0);
				
				for (int yi = 1; yi < hi; yi++)
				{
					E ee = input.getCellContents(x, yi);
					
					e = merger.f(e, ee);
				}
				
				output.setCellContents(x, 0, e);
			}
			
			return output;
		}
	}
	
	
	
	
	public static <E> SimpleTable<E> listifyOP(int numberOfRepeatedColumnsAtStart, int numberOfRepeatedRowsAtStart, SimpleTable<E> input)
	{
		int wd = input.getNumberOfColumns();
		int hd = input.getNumberOfRows();
		
		if (wd <= numberOfRepeatedColumnsAtStart)
			throw new IllegalArgumentException();
		wd -= numberOfRepeatedColumnsAtStart;
		
		if (hd <= numberOfRepeatedRowsAtStart)
			throw new IllegalArgumentException();
		hd -= numberOfRepeatedRowsAtStart;
		
		
		
		
		SimpleTable<E> output = newTableNullfilled(numberOfRepeatedColumnsAtStart + numberOfRepeatedRowsAtStart + 1, wd*hd);
		
		int i = 0;
		for (int yd = 0; yd < hd; yd++)
		{
			for (int xd = 0; xd < wd; xd++)
			{
				int X = numberOfRepeatedColumnsAtStart + xd;
				int Y = numberOfRepeatedRowsAtStart + yd;
				
				//Repeated things :3
				{
					for (int e = 0; e < numberOfRepeatedColumnsAtStart; e++)
						output.setCellContents(e, i, input.getCellContents(e, Y));
					
					for (int e = 0; e < numberOfRepeatedRowsAtStart; e++)
						output.setCellContents(numberOfRepeatedColumnsAtStart + e, i, input.getCellContents(X, e));
				}
				
				
				//Individual thing :D
				{
					E datum = input.getCellContents(X, Y);
					output.setCellContents(numberOfRepeatedColumnsAtStart+numberOfRepeatedRowsAtStart, i, datum);
				}
				
				
				i++;
			}
		}
		
		return output;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> List<E> newfillList(int size, E value)
	{
		return (List<E>)asList(ArrayUtilities.newfillArray(size, value, Object.class));
	}
	
	
	
	
	
	
	
	
	
	
	public static void rangeCheckMember(int collectionSize, int index)
	{
		if (collectionSize < 0)  throw new ImpossibleException("negative contextualizing collection length!!:  "+collectionSize);
		if (index < 0)  throw new IndexOutOfBoundsException("negative index!!  "+index);
		if (index >= collectionSize)  throw new IndexOutOfBoundsException("index "+index+" >= size "+collectionSize);   // >= not > !!
	}
	
	public static void rangeCheckCursorPoint(int collectionSize, int index)
	{
		if (collectionSize < 0)  throw new ImpossibleException("negative contextualizing collection length!!:  "+collectionSize);
		if (index < 0)  throw new IndexOutOfBoundsException("negative index!!  "+index);
		if (index > collectionSize)  throw new IndexOutOfBoundsException("index "+index+" > size "+collectionSize);   // > not >= !!
	}
	
	
	public static void rangeCheckInterval(int collectionSize, int start, int endExcl)
	{
		if (collectionSize < 0)  throw new ImpossibleException("negative contextualizing collection length!!:  "+collectionSize);
		if (start < 0)  throw new IndexOutOfBoundsException("negative index!!  "+start);
		if (endExcl < 0)  throw new IndexOutOfBoundsException("negative index!!  "+endExcl);
		if (start > collectionSize)  throw new IndexOutOfBoundsException("index "+start+" > size "+collectionSize);
		if (endExcl > collectionSize)  throw new IndexOutOfBoundsException("index bound "+endExcl+" > size "+collectionSize);
		if (endExcl < start)  throw new IndexOutOfBoundsException("index bounds reversed!  ("+start+" to "+endExcl+")");
	}
	
	public static void rangeCheckIntervalByLength(int collectionSize, int fromIndex, int length)
	{
		if (collectionSize < 0)  throw new ImpossibleException("negative contextualizing collection length!!:  "+collectionSize);
		if (length < 0)  throw new IndexOutOfBoundsException("negative length!!  "+length);
		rangeCheckInterval(collectionSize, fromIndex, fromIndex+length);
	}
	
	
	public static void rangeCheckFor_shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(List list, int start, int amount)
	{
		rangeCheckCursorPoint(list.size(), start);
	}
	
	public static void rangeCheckFor_removeRange(List list, int start, int pastEnd)
	{
		rangeCheckInterval(list.size(), start, pastEnd);
	}
	
	
	
	
	
	
	
	
	
	//Todo ones for U64's ^^'
	
	public static void rangeCheckMemberS64(long collectionSize, long index)
	{
		if (collectionSize < 0)  throw new ImpossibleException("negative contextualizing collection length!!:  "+collectionSize);
		if (index < 0)  throw new IndexOutOfBoundsException("negative index!!  "+index);
		if (index >= collectionSize)  throw new IndexOutOfBoundsException("index "+index+" >= size "+collectionSize);   // >= not > !!
	}
	
	public static void rangeCheckCursorPointS64(long collectionSize, long index)
	{
		if (collectionSize < 0)  throw new ImpossibleException("negative contextualizing collection length!!:  "+collectionSize);
		if (index < 0)  throw new IndexOutOfBoundsException("negative index!!  "+index);
		if (index > collectionSize)  throw new IndexOutOfBoundsException("index "+index+" > size "+collectionSize);   // > not >= !!
	}
	
	
	public static void rangeCheckIntervalS64(long collectionSize, long start, long endExcl)
	{
		if (collectionSize < 0)  throw new ImpossibleException("negative contextualizing collection length!!:  "+collectionSize);
		if (start < 0)  throw new IndexOutOfBoundsException("negative index!!  "+start);
		if (endExcl < 0)  throw new IndexOutOfBoundsException("negative index!!  "+endExcl);
		if (start > collectionSize)  throw new IndexOutOfBoundsException("index "+start+" > size "+collectionSize);
		if (endExcl > collectionSize)  throw new IndexOutOfBoundsException("index bound "+endExcl+" > size "+collectionSize);
		if (endExcl < start)  throw new IndexOutOfBoundsException("index bounds reversed!  ("+start+" to "+endExcl+")");
	}
	
	public static void rangeCheckIntervalByLengthS64(long collectionSize, long fromIndex, long length)
	{
		if (collectionSize < 0)  throw new ImpossibleException("negative contextualizing collection length!!:  "+collectionSize);
		if (length < 0)  throw new IndexOutOfBoundsException("negative length!!  "+length);
		rangeCheckIntervalS64(collectionSize, fromIndex, fromIndex+length);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Shift the region [start, size()] by amount (which can be negative to indicate backwards shifting!), and adjust size so that the last member shifted is still the last member after shifting :3
	 * (Note that this description is a little confusing if start == size() XD, but still this is very much still well-defined!)
	 * 
	 * If amount is negative, this is equivalent to {@link #removeRange(List, int, int) removeRange}(list, start+amount, start).
	 * If amount is positive, this is meant to insert a block <i>undefined</i> (not necessarily null!) contents at 'start' that is 'amount' elements in size :>
	 * If amount is zero, this is basically a no-op.
	 * 
	 * The utility of this method is that almost all size-changing list methods can be implemented in terms of it, get(), set(), and indexOf()!  :D
	 * 		{@link List#add(Object)}, {@link List#remove(int)}, {@link List#remove(Object)}, {@link List#addAll(Collection)}, {@link List#add(int, Object)}, {@link List#addAll(int, Collection)}, {@link ListWithSetSize#setSize(int)}, {@link ListWithSetSize#setSize(int, Object)}, {@link ListWithRemoveRange#removeRange(int, int)}, etc.!  :D
	 * 
	 * In all cases, the size of the list afterward will be the original size + amount  :D
	 */
	public static void shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(List list, int start, @Signed int amount)
	{
		rangeCheckFor_shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(list, start, amount);
		
		if (amount == 0)
			return;
		
		if (list instanceof ShiftableList)
			((ShiftableList)list).shiftRegionStretchingFromIndexToEndByAmountChangingSize(start, amount);
		else if (list instanceof ListWithSetSize)
			defaultShiftRegionStretchingFromIndexToEndByAmountChangingSizeOfListWithSetSize((ListWithSetSize) list, start, amount);
		else if (list instanceof ListWithRemoveRange && amount < 0)
			((ListWithRemoveRange)list).removeRange(start+amount, start);
		else
			defaultShiftRegionStretchingFromIndexToEndByAmountChangingSizeOfListWithoutSetSize(list, start, amount);
	}
	
	
	public static void defaultShiftRegionStretchingFromIndexToEndByAmountChangingSizeOfListWithSetSize(ListWithSetSize list, int start, @Signed int amount)
	{
		int originalSize = list.size();
		
		if (amount > 0)
		{
			//Allocate
			list.setSize(originalSize + amount);
			
			//Then copy :>
			listcopy(list, start, list, start + amount, originalSize - start);
		}
		else if (amount < 0)
		{
			//Copy
			listcopy(list, start, list, start + amount, originalSize - start);
			
			//Then shrink :>
			list.setSize(originalSize + amount);
		}
	}
	
	
	public static void defaultShiftRegionStretchingFromIndexToEndByAmountChangingSizeOfListWithoutSetSize(List list, int start, @Signed int amount)
	{
		//No need to accomodate primitive lists specially, they'll always implement ListWithSetSize, so the other impl. deals with them! :D
		
		if (amount > 0)
		{
			//TODO Different implementation here for !isRandomAccess
			
			int pastEndToAdd = start + amount;
			
			for (int i = 0; i < pastEndToAdd; i++)
				list.add(null);  //It's very important that we don't need to worry about primitive lists here—otherwise this would throw NullPointerException!  XD''
		}
		else if (amount < 0)
		{
			int startOfIntervalToRemove = start + amount;
			int pastEndToRemove = start;
			
			if (isRandomAccessFast(list))
			{
				for (int i = pastEndToRemove-1; i >= startOfIntervalToRemove; i--)
					list.remove(i);
			}
			else
			{
				ListIterator li = list.listIterator(pastEndToRemove);
				
				int length = -amount;
				
				for (int i = 0; i < length; i++)
				{
					li.previous();
					li.remove();
				}
			}
		}
	}
	
	
	
	
	public static void listcopy(List source, List dest)
	{
		int size = source.size();
		if (size != dest.size())
			throw new IllegalArgumentException("Lists to copy into each other aren't the same size!   (Source is "+source.size()+" elements and Dest is "+dest.size()+" elements!)");
		
		listcopy(source, 0, dest, 0, size);
	}
	
	
	
	/**
	 * Exactly like {@link System#arraycopy(Object, int, Object, int, int)} but for lists! :D
	 */
	public static void listcopy(List source, int sourceOffset, List dest, int destOffset, @Nonnegative int amount)
	{
		//These are important because if if might be supposed to fail, but if we unwrap the sublist it might silently corrupt data instead of failing!!
		rangeCheckIntervalByLength(source.size(), sourceOffset, amount);
		rangeCheckIntervalByLength(dest.size(), destOffset, amount);
		
		
		//Unwrap sublists! :D
		{
			while (source instanceof Sublist)
			{
				Sublist ss = (Sublist) source;
				sourceOffset += ss.getSublistStartingIndex();
				source = requireNonNull(ss.getUnderlying());
			}
			
			while (dest instanceof Sublist)
			{
				Sublist ds = (Sublist) dest;
				destOffset += ds.getSublistStartingIndex();
				dest = requireNonNull(ds.getUnderlying());
			}
		}
		
		
		
		if (dest instanceof ListWithSetAll)
		{
			((ListWithSetAll) dest).setAll(destOffset, source, sourceOffset, amount);
		}
		else
		{
			//No need to accomodate primitive lists specially, they'll always implement ListWithSetAll! :D
			defaultListcopy(source, sourceOffset, dest, destOffset, amount);
		}
	}
	
	
	public static void defaultListcopy(List source, int sourceOffset, List dest, int destOffset, @Nonnegative int amount)
	{
		int sS = source.size();
		int dS = dest.size();
		if (dS < sS)
			throw new IndexOutOfBoundsException();
		if (sS == 0)
			return;  //dS can only be 0 if sS is zero so no need to test for "|| dS == 0" too! ^,^
		
		
		if (destOffset < sourceOffset)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
		{
			ListIterator s = source.listIterator(sourceOffset);
			ListIterator d = dest.listIterator(destOffset);
			
			int i = 0;
			while (i < amount)
			{
				d.next();
				d.set(s.next());
				i++;
			}
		}
		else
		{
			ListIterator s = source.listIterator(sourceOffset+amount);
			ListIterator d = dest.listIterator(destOffset+amount);
			
			int i = 0;
			while (i < amount)
			{
				d.previous();
				d.set(s.previous());
				i++;
			}
		}
	}
	
	
	public static <E> void defaultRandomAccessListcopy(List<? extends E> source, int sourceOffset, List<E> dest, int destOffset, int amount)
	{
		int sourceSize = source.size();
		int destSize = dest.size();
		if (destSize < sourceSize)
			throw new IndexOutOfBoundsException();
		if (sourceSize == 0)
			return;  //destSize can only be 0 if sourceSize is zero so no need to test for "|| destSize == 0" too! ^,^
		
		rangeCheckIntervalByLength(sourceSize, sourceOffset, amount);
		rangeCheckIntervalByLength(destSize, destOffset, amount);
		
		if (destOffset < sourceOffset)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
		{
			for (int i = 0; i < amount; i++)
				dest.set(destOffset+i, source.get(sourceOffset+i));
		}
		else
		{
			for (int i = amount-1; i >= 0; i--)
				dest.set(destOffset+i, source.get(sourceOffset+i));
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int findNextNullOrListSize(List<?> l)
	{
		int i = l.indexOf(null);
		return i == -1 ? l.size() : i;
	}
	
	
	public static <E> int storeInNextNullOrAppendToList(List<E> l, E e)
	{
		int i = l.indexOf(null);
		
		if (i == -1)
		{
			int s = l.size();
			l.add(e);
			return s;
		}
		else
		{
			l.set(i, e);
			return i;
		}
	}
	
	
	
	
	
	
	
	public static <E> List<E> immutableCopyList(List<E> list)
	{
		if (isTrueAndNotNull(isThreadUnsafelyImmutable(list)))
			return list;
		
		
		/* <<<
		primxp
		
		if (list instanceof _$$Primitive$$_List)
			return (List<E>)Immutable_$$Primitive$$_ArrayList.newCopying((List<_$$Primitive$$_>)list);
		 */
		
		if (list instanceof BooleanList)
			return (List<E>)ImmutableBooleanArrayList.newCopying((List<Boolean>)list);
		
		if (list instanceof ByteList)
			return (List<E>)ImmutableByteArrayList.newCopying((List<Byte>)list);
		
		if (list instanceof CharacterList)
			return (List<E>)ImmutableCharacterArrayList.newCopying((List<Character>)list);
		
		if (list instanceof ShortList)
			return (List<E>)ImmutableShortArrayList.newCopying((List<Short>)list);
		
		if (list instanceof FloatList)
			return (List<E>)ImmutableFloatArrayList.newCopying((List<Float>)list);
		
		if (list instanceof IntegerList)
			return (List<E>)ImmutableIntegerArrayList.newCopying((List<Integer>)list);
		
		if (list instanceof DoubleList)
			return (List<E>)ImmutableDoubleArrayList.newCopying((List<Double>)list);
		
		if (list instanceof LongList)
			return (List<E>)ImmutableLongArrayList.newCopying((List<Long>)list);
		//>>>
		
		return Collections.unmodifiableList(new ArrayList<>(list));
	}
	
	
	
	
	
	
	public static <E> Set<E> immutableCopySet(Set<E> set)
	{
		if (isTrueAndNotNull(isThreadUnsafelyImmutable(set)))
			return set;
		
		
		/* <<<
		primxp
		_$$primxpconf:noboolean$$_
		
		if (set instanceof _$$Primitive$$_Collection)
			return (Set<E>)new Sorted_$$Primitive$$_SetBackedByList(Immutable_$$Primitive$$_ArrayList.newCopying(Sorted_$$Primitive$$_SetBackedByList.sorted((Collection<_$$Primitive$$_>)set).getUnderlying()));
		 */
		
		
		if (set instanceof ByteCollection)
			return (Set<E>)new SortedByteSetBackedByList(ImmutableByteArrayList.newCopying(SortedByteSetBackedByList.sorted((Collection<Byte>)set).getUnderlying()));
		
		
		if (set instanceof CharacterCollection)
			return (Set<E>)new SortedCharacterSetBackedByList(ImmutableCharacterArrayList.newCopying(SortedCharacterSetBackedByList.sorted((Collection<Character>)set).getUnderlying()));
		
		
		if (set instanceof ShortCollection)
			return (Set<E>)new SortedShortSetBackedByList(ImmutableShortArrayList.newCopying(SortedShortSetBackedByList.sorted((Collection<Short>)set).getUnderlying()));
		
		
		if (set instanceof FloatCollection)
			return (Set<E>)new SortedFloatSetBackedByList(ImmutableFloatArrayList.newCopying(SortedFloatSetBackedByList.sorted((Collection<Float>)set).getUnderlying()));
		
		
		if (set instanceof IntegerCollection)
			return (Set<E>)new SortedIntegerSetBackedByList(ImmutableIntegerArrayList.newCopying(SortedIntegerSetBackedByList.sorted((Collection<Integer>)set).getUnderlying()));
		
		
		if (set instanceof DoubleCollection)
			return (Set<E>)new SortedDoubleSetBackedByList(ImmutableDoubleArrayList.newCopying(SortedDoubleSetBackedByList.sorted((Collection<Double>)set).getUnderlying()));
		
		
		if (set instanceof LongCollection)
			return (Set<E>)new SortedLongSetBackedByList(ImmutableLongArrayList.newCopying(SortedLongSetBackedByList.sorted((Collection<Long>)set).getUnderlying()));
		//>>>
		
		return Collections.unmodifiableSet(new HashSet<>(set));
	}
	
	
	
	
	
	
	public static <K, V> Map<K, V> immutableCopyMap(Map<K, V> map)
	{
		if (isTrueAndNotNull(isThreadUnsafelyImmutable(map)))
			return map;
		
		return Collections.unmodifiableMap(new HashMap<>(map));
	}
	
	
	
	
	
	
	
	
	
	public static <E> boolean isRandomAccessFast(List<E> list)
	{
		if (list instanceof RandomAccess)
			return true;
		else if (FastRandomAccess.is(list))
			return true;
		else
			return false;
	}
	
	
	
	
	//These funky generics work for PrimitiveLists and other such things :>
	
	public static <E, L extends List<E>> L subListToEnd(L list, int start)
	{
		asrt(start >= 0);
		return (L)list.subList(start, list.size());
	}
	
	public static <E, L extends List<E>> L subListFromBeginning(L list, int pastEndAkaSize)
	{
		asrt(pastEndAkaSize >= 0);
		return (L)list.subList(0, pastEndAkaSize);
	}
	
	public static <E, L extends List<E>> L subListBySize(L list, int start, int subListSize)
	{
		asrt(start >= 0);
		asrt(subListSize >= 0);
		return (L)list.subList(start, start + subListSize);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <I> boolean reduceBoolean(BinaryFunction<I, I, Boolean> f, Iterable<I> inputs, boolean and)
	{
		return reduceBoolean(f, SimpleIterable.simpleIterable(inputs), and);
	}
	
	public static <I> boolean reduceBoolean(BinaryFunction<I, I, Boolean> f, SimpleIterable<I> inputs, boolean and)
	{
		return reduceBoolean(f, inputs.simpleIterator(), and);
	}
	
	public static <I> boolean reduceBoolean(BinaryFunction<I, I, Boolean> f, SimpleIterator<I> inputs, boolean and)
	{
		I first;
		try
		{
			first = inputs.nextrp();
		}
		catch (StopIterationReturnPath exc1)
		{
			return and;
		}
		
		while (true)
		{
			I next;
			try
			{
				next = inputs.nextrp();
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
			
			if (!f.f(first, next))
				return !and;
		}
		
		return and;
	}
	
	
	
	
	
	
	
	
	public static <I> boolean reduceAnding(BinaryFunction<I, I, Boolean> f, Iterable<I> inputs)
	{
		return reduceBoolean(f, inputs, true);
	}
	
	public static <I> boolean reduceAnding(BinaryFunction<I, I, Boolean> f, SimpleIterable<I> inputs)
	{
		return reduceBoolean(f, inputs, true);
	}
	
	public static <I> boolean reduceAnding(BinaryFunction<I, I, Boolean> f, SimpleIterator<I> inputs)
	{
		return reduceBoolean(f, inputs, true);
	}
	
	
	
	public static <I> boolean reduceOrring(BinaryFunction<I, I, Boolean> f, Iterable<I> inputs)
	{
		return reduceBoolean(f, inputs, false);
	}
	
	public static <I> boolean reduceOrring(BinaryFunction<I, I, Boolean> f, SimpleIterable<I> inputs)
	{
		return reduceBoolean(f, inputs, false);
	}
	
	public static <I> boolean reduceOrring(BinaryFunction<I, I, Boolean> f, SimpleIterator<I> inputs)
	{
		return reduceBoolean(f, inputs, false);
	}
	
	
	
	
	
	
	public static <E> Set<E> getAllWithObserver(UnaryProcedure<UnaryProcedure<E>> observerUsingFunction)
	{
		Set<E> set = new HashSet<>();
		observerUsingFunction.f(set::add);
		return set;
	}
	
	public static <E> Collection<E> getAllMultiSetWithObserver(UnaryProcedure<UnaryProcedure<E>> observerUsingFunction)
	{
		Collection<E> set = new ArrayList<>();
		observerUsingFunction.f(set::add);
		return set;
	}
	
	public static <E> boolean hasAnyWithObserver(UnaryProcedure<UnaryProcedure<E>> observerUsingFunction)
	{
		BooleanContainer hasAny_C = new SimpleBooleanContainer(false);
		observerUsingFunction.f(e -> hasAny_C.set(true));
		return hasAny_C.get();
	}
	
	public static boolean hasAnyWithRunnable(UnaryProcedure<Runnable> observerUsingFunction)
	{
		BooleanContainer hasAny_C = new SimpleBooleanContainer(false);
		observerUsingFunction.f(() -> hasAny_C.set(true));
		return hasAny_C.get();
	}
	
	public static boolean hasAnyWithPredicate(UnaryProcedure<UnaryProcedureBoolean> observerUsingFunction)
	{
		BooleanContainer hasAny_C = new SimpleBooleanContainer(false);
		observerUsingFunction.f(v -> {if (v) hasAny_C.set(true);});
		return hasAny_C.get();
	}
	
	public static <E> boolean hasAtLeastOneMatchingWithObserver(UnaryProcedure<UnaryProcedure<E>> observerUsingFunction, Predicate<E> predicate)
	{
		BooleanContainer hasMatching_C = new SimpleBooleanContainer(false);
		observerUsingFunction.f(e -> { if (predicate.test(e)) hasMatching_C.set(true); });
		return hasMatching_C.get();
	}
	
	
	public static <E> E getArbitraryOrNullIfNoneWithObserver(UnaryProcedure<UnaryFunction<E, ContinueSignal>> observerUsingFunction)
	{
		ObjectContainer<E> c = new SimpleObjectContainer<>(null);
		
		observerUsingFunction.f(v ->
		{
			requireNonNull(v);
			c.set(v);
			return ContinueSignal.Stop;
		});
		
		return c.get();
	}
	
	
	
	
	
	
	
	
	
	
	
	public static <E> void ensureListCapacityGrowingWithNullsIfNecessary(List<E> list, int minimumSize)
	{
		ensureListCapacityGrowingIfNecessary(list, minimumSize, null);
	}
	
	public static <E> void setInListGrowingWithNullsIfNecessary(List<E> list, int index, E element)
	{
		setInListGrowingIfNecessary(list, index, element, null);
	}
	
	
	public static <E> void ensureListCapacityGrowingIfNecessary(List<E> list, int minimumSize, E elementToAddIfGrowing)
	{
		if (list.size() < minimumSize)
			setListSizeGrowing(list, minimumSize, elementToAddIfGrowing);
	}
	
	public static <E> void setInListGrowingIfNecessary(List<E> list, int index, E element, E elementToAddIfGrowing)
	{
		ensureListCapacityGrowingWithNullsIfNecessary(list, index+1);
		list.set(index, element);
	}
	
	
	
	
	
	
	
	public static <T extends Iterable<?>> T requireNonNullElements(@NonnullElements T collection)
	{
		if (isAnyNull(collection))  //superfast for PrimitiveCollections! :D
			throw new NullPointerException();
		
		return collection;
	}
	
	public static <T extends Map<?, ?>> T requireNonNullKeys(@NonnullKeys T map)
	{
		if (isAnyNull(map.keySet()))  //superfast for PrimitiveCollections! :D
			throw new NullPointerException();
		return map;
	}
	
	public static <T extends Map<?, ?>> T requireNonNullValues(@NonnullValues T map)
	{
		if (isAnyNull(map.values()))  //superfast for PrimitiveCollections! :D
			throw new NullPointerException();
		return map;
	}
	
	public static <T extends Map<?, ?>> T requireNonNullKeysAndValues(@NonnullKeys T map)
	{
		requireNonNullKeys(map);
		requireNonNullValues(map);
		return map;
	}
	
	public static <T extends SimpleTable<?>> T requireNonNullCells(@NonnullElements T table)
	{
		table.apply(v -> requireNonNull(v));
		return table;
	}
	
	
	
	public static <T extends Iterable<?>> T requireNonEmpty(@Nonempty T c)
	{
		if (isEmptyIterable(c))
			throw new IllegalArgumentException();
		return c;
	}
	
	public static <T extends Map<?, ?>> T requireNonEmpty(@Nonempty T map)
	{
		if (map.isEmpty())
			throw new IllegalArgumentException();
		return map;
	}
	
	public static <T extends SimpleTable<?>> T requireNonEmpty(@Nonempty T table)
	{
		if (table.isEmpty())
			throw new IllegalArgumentException();
		return table;
	}
	
	
	
	
	
	
	
	
	
	
	public static <T extends Iterable<?>> T requireInstanceOfElements(@NonnullElements T collection, Class c)
	{
		//Todo fast checks for PrimitiveCollections; perhaps trait predicates/overridability! :3
		for (Object t : collection)
			requireInstanceOf(t, c);
		return collection;
	}
	
	public static <T extends Map<?, ?>> T requireInstanceOfKeys(@NonnullKeys T map, Class c)
	{
		//Todo fast checks for PrimitiveCollections; perhaps trait predicates/overridability! :3
		for (Object t : map.keySet())
			requireInstanceOf(t, c);
		return map;
	}
	
	public static <T extends Map<?, ?>> T requireInstanceOfValues(@NonnullValues T map, Class c)
	{
		//Todo fast checks for PrimitiveCollections; perhaps trait predicates/overridability! :3
		for (Object t : map.values())
			requireInstanceOf(t, c);
		return map;
	}
	
	public static <T extends Map<?, ?>> T requireInstanceOfKeysAndValues(@NonnullKeys T map, Class c)
	{
		requireInstanceOfKeys(map, c);
		requireInstanceOfValues(map, c);
		return map;
	}
	
	public static <T extends SimpleTable<?>> T requireInstanceOfCells(@NonnullElements T table, Class c)
	{
		table.apply(v -> requireInstanceOf(v, c));
		return table;
	}
	
	
	
	public static <E> Iterable<E> cloneIterable(Iterable<E> iterable)
	{
		if (iterable instanceof PubliclyCloneable)
			return (Iterable<E>) ((PubliclyCloneable) iterable).clone();
		else
		{
			if (iterable instanceof Collection)
				return new ArrayList<>((Collection)iterable);
			else
			{
				List<E> l = new ArrayList<>();
				addAll(l, iterable);
				return l;
			}
		}
	}
	
	public static <E> Collection<E> cloneCollection(Collection<E> Collection)
	{
		if (Collection instanceof PubliclyCloneable)
			return (Collection<E>) ((PubliclyCloneable) Collection).clone();
		else
			return new ArrayList<>(Collection);
	}
	
	public static <E> List<E> cloneList(List<E> list)
	{
		if (list instanceof PubliclyCloneable)
			return (List<E>) ((PubliclyCloneable) list).clone();
		else
			return new ArrayList<>(list);
	}
	
	public static <E> Set<E> cloneSet(Set<E> Set)
	{
		if (Set instanceof PubliclyCloneable)
			return (Set<E>) ((PubliclyCloneable) Set).clone();
		else
			return new HashSet<>(Set);
	}
	
	public static <K, V> Map<K, V> cloneMap(Map<K, V> Map)
	{
		if (Map instanceof PubliclyCloneable)
			return (Map<K, V>) ((PubliclyCloneable) Map).clone();
		else
			return new HashMap<>(Map);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static IdentityCardinality identityCardinalityFromCardinality(int size)
	{
		if (size == 0)
			return IdentityCardinality.Zero;
		else if (size == 1)
			return IdentityCardinality.One;
		else
			return IdentityCardinality.Multiple;
	}
	
	public static IdentityCardinality identityCardinalityOf(Collection<?> collection)
	{
		return identityCardinalityFromCardinality(collection.size());
	}
	
	public static IdentityCardinality identityCardinalityOf(Map<?, ?> map)
	{
		return identityCardinalityFromCardinality(map.size());
	}
	
	public static IdentityCardinality identityCardinalityOf(Iterable<?> iterable)
	{
		if (iterable instanceof Collection)
			return identityCardinalityOf((Collection)iterable);
		
		Iterator<?> i = iterable.iterator();
		
		if (i.hasNext())
		{
			i.next();
			
			if (i.hasNext())
			{
				return IdentityCardinality.Multiple;
			}
			else
			{
				return IdentityCardinality.One;
			}
		}
		else
		{
			return IdentityCardinality.Zero;
		}
	}
	
	
	/**
	 * Assumes no values will be empty or null!
	 */
	public static IdentityCardinality identityCardinalityOfGeneralMap(Map<?, ? extends Iterable<?>> gmap)
	{
		IdentityCardinality ic = identityCardinalityOf(gmap);
		
		if (ic == IdentityCardinality.One)
		{
			Iterable<?> v = getSingleElement(gmap.values());
			return identityCardinalityOf(v);
		}
		else
		{
			return ic;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static <I, O> boolean eqMapping(Mapper<I, O> mapper, SimpleIterator<I> inputs, EqualityComparator<O> eq)
	{
		boolean has = false;
		O arbitrary = null;
		
		while (true)
		{
			I i;
			try
			{
				i = inputs.nextrp();
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
			
			O o;
			try
			{
				o = mapper.f(i);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			if (!has)
			{
				arbitrary = o;
				has = true;
			}
			else
			{
				if (!eq.equals(o, arbitrary))
					return false;
			}
		}
		
		if (!has)
			throw new IllegalArgumentException("No inputs!");
		
		return true;
	}
	
	
	
	
	public static <I, O> boolean eqMapping(Mapper<I, O> mapper, SimpleIterable<I> inputs, EqualityComparator<O> eq)
	{
		return eqMapping(mapper, inputs.simpleIterator(), eq);
	}
	
	public static <I, O> boolean eqMapping(Mapper<I, O> mapper, Iterator<I> inputs, EqualityComparator<O> eq)
	{
		return eqMapping(mapper, SimpleIterator.simpleIterator(inputs), eq);
	}
	
	public static <I, O> boolean eqMapping(Mapper<I, O> mapper, Iterable<I> inputs, EqualityComparator<O> eq)
	{
		return eqMapping(mapper, SimpleIterable.simpleIterable(inputs), eq);
	}
	
	public static <I, O> boolean eqMapping(Mapper<I, O> mapper, I[] inputs, EqualityComparator<O> eq)
	{
		return eqMapping(mapper, SimpleIterator.simpleIterator(inputs), eq);
	}
	
	
	
	public static <I, O> boolean eqMapping(Mapper<I, O> mapper, SimpleIterator<I> inputs)
	{
		return eqMapping(mapper, inputs, DefaultEqualityComparator.I);
	}
	
	public static <I, O> boolean eqMapping(Mapper<I, O> mapper, Iterator<I> inputs)
	{
		return eqMapping(mapper, inputs, DefaultEqualityComparator.I);
	}
	
	public static <I, O> boolean eqMapping(Mapper<I, O> mapper, Iterable<I> inputs)
	{
		return eqMapping(mapper, inputs, DefaultEqualityComparator.I);
	}
	
	public static <I, O> boolean eqMapping(Mapper<I, O> mapper, SimpleIterable<I> inputs)
	{
		return eqMapping(mapper, inputs, DefaultEqualityComparator.I);
	}
	
	public static <I, O> boolean eqMapping(Mapper<I, O> mapper, I[] inputs)
	{
		return eqMapping(mapper, inputs, DefaultEqualityComparator.I);
	}
	
	public static <I, O> boolean eqMappingV(Mapper<I, O> mapper, I... inputs)
	{
		return eqMapping(mapper, inputs, DefaultEqualityComparator.I);
	}
	
	
	
	public static <I> boolean transitiveReduce(SimpleIterator<I> inputs, BinaryFunctionToBoolean<I, I> f)
	{
		return eqMapping(x -> x, inputs, (a, b) -> f.f(a, b));
	}
	
	public static <I> boolean transitiveReduce(Iterator<I> inputs, BinaryFunctionToBoolean<I, I> f)
	{
		return eqMapping(x -> x, inputs, (a, b) -> f.f(a, b));
	}
	
	public static <I> boolean transitiveReduce(Iterable<I> inputs, BinaryFunctionToBoolean<I, I> f)
	{
		return eqMapping(x -> x, inputs, (a, b) -> f.f(a, b));
	}
	
	public static <I> boolean transitiveReduce(SimpleIterable<I> inputs, BinaryFunctionToBoolean<I, I> f)
	{
		return eqMapping(x -> x, inputs, (a, b) -> f.f(a, b));
	}
	
	public static <I> boolean transitiveReduce(I[] inputs, BinaryFunctionToBoolean<I, I> f)
	{
		return eqMapping(x -> x, inputs, (a, b) -> f.f(a, b));
	}
	
	public static <I> boolean transitiveReduceV(BinaryFunctionToBoolean<I, I> f, I... inputs)
	{
		return eqMapping(x -> x, inputs, (a, b) -> f.f(a, b));
	}
	
	
	
	
	
	
	@Nullable
	public static <E> Integer binarySearchList(UnaryFunction<? super E, Direction1D> predicate, List<E> list)
	{
		return MathUtilities.binarySearchS32(i -> predicate.f(list.get(i)), 0, list.size());
	}
	
	
	
	
	
	
	
	
	
	public static final Comparator<Entry<?, ?>> EntryComparator = (a, b) -> cmp2chainable(cmp2(a.getKey(), b.getKey()), a.getValue(), b.getValue());
	
	
	
	
	
	
	
	public static boolean isAllNull(Iterable<?> i)
	{
		if (i instanceof PrimitiveCollection)
			return false;
		else
			return forAll(e -> e == null, i);
	}
	
	public static boolean isAnyNull(Iterable<?> i)
	{
		if (i instanceof PrimitiveCollection)
			return false;
		else
			return forAny(e -> e == null, i);
	}
	
	
	
	
	
	
	
	
	
	
	@ReadonlyValue
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<List<E>> simpleMergeOPC(BinaryFunctionToBoolean<List<E>, List<E>> shouldMerge, List<List<E>> input)
	{
		return mergeOPC(shouldMerge, (List<List<E>> l) -> singletonList(concatenateManyListsOPC(l)), input);
	}
	
	
	
	
	
	
	
	@ReadonlyValue
	@SnapshotValue
	public static <E> List<E> mergeOP(BinaryFunctionToBoolean<E, E> shouldMerge, UnaryFunction<List<E>, List<E>> merger, List<E> input)
	{
		return mergeOPx(shouldMerge, merger, input, false);
	}
	
	@ReadonlyValue
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> mergeOPC(BinaryFunctionToBoolean<E, E> shouldMerge, UnaryFunction<List<E>, List<E>> merger, List<E> input)
	{
		return mergeOPx(shouldMerge, merger, input, true);
	}
	
	@ReadonlyValue
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> mergeOPx(BinaryFunctionToBoolean<E, E> shouldMerge, UnaryFunction<List<E>, List<E>> merger, List<E> input, boolean conserve)
	{
		return mergeOPx(shouldMerge, merger, input, initialCapacity -> new ArrayList<>(initialCapacity), conserve);
	}
	
	@ReadonlyValue
	@PossiblySnapshotPossiblyLiveValue
	public static <E> List<E> mergeOPx(BinaryFunctionToBoolean<E, E> shouldMerge, UnaryFunction<List<E>, List<E>> merger, List<E> input, UnaryFunctionIntToObject<List<E>> outputInstantiator, boolean conserve)
	{
		if (input.isEmpty())
			return conserve ? input : outputInstantiator.f(0);
		
		List<E> merged = conserve ? null : outputInstantiator.f(input.size());
		
		int mergeRunStart = 0;
		
		int n;
		E prev = null;
		{
			int i = 0;
			
			for (E e : input)
			{
				if (i > 0)
				{
					if (shouldMerge.f(prev, e))
					{
						//Keep going :>
					}
					else
					{
						//Commit extant previous merge run! :D
						{
							int runLength = i - mergeRunStart;
							
							asrt(runLength > 0);
							
							if (runLength == 1)
							{
								//asrt(mergeRunStart == i - 1);
								
								if (merged != null)
								{
									//asrt(input.get(mergeRunStart) == prev);
									merged.add(prev);
								}
								else
								{
									//Leave it :>
								}
							}
							else
							{
								if (merged == null)
								{
									merged = outputInstantiator.f(input.size());
									merged.addAll(input.subList(0, mergeRunStart));
								}
								
								merged.addAll(merger.f(input.subList(mergeRunStart, i)));
							}
						}
						
						mergeRunStart = i;
					}
				}
				
				i++;
				prev = e;
			}
			
			
			n = i;
		}
		
		
		
		
		//Commit last merge run! :D
		{
			int runLength = n - mergeRunStart;
			
			asrt(runLength > 0);
			
			if (runLength == 1)
			{
				//asrt(mergeRunStart == n - 1);
				
				if (merged != null)
				{
					//asrt(input.get(mergeRunStart) == prev);
					merged.add(prev);
				}
				else
				{
					//Leave it :>
				}
			}
			else
			{
				if (merged == null)
				{
					merged = outputInstantiator.f(input.size());
					merged.addAll(input.subList(0, mergeRunStart));
				}
				
				merged.addAll(merger.f(input.subList(mergeRunStart, n)));
			}
		}
		
		
		
		
		
		
		if (merged == null)
		{
			asrt(conserve);
			return input;
		}
		else
		{
			return merged;
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Useful for, eg, generating random data :>
	 */
	@ThrowAwayValue
	public static <I, O> List<O> mapNullaryToList(Mapper<I, O> mapper, NullaryFunction<I> generator, int amount)
	{
		List<O> rv = new ArrayList<>();
		
		for (int i = 0; i < amount; i++)
		{
			O o;
			try
			{
				o = mapper.f(generator.f());
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			rv.add(o);
		}
		
		return rv;
	}
	
	
	
	/**
	 * Useful for, eg, generating random data :>
	 * + Noops on duplicate elements, like {@link #mapToSet(Mapper, Iterable)} :3
	 */
	@ThrowAwayValue
	public static <I, O> Set<O> mapNullaryToSet(Mapper<I, O> mapper, NullaryFunction<I> generator, int amount)
	{
		Set<O> rv = new HashSet<>();
		
		for (int i = 0; i < amount; i++)
		{
			O o;
			try
			{
				o = mapper.f(generator.f());
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			rv.add(o);
		}
		
		return rv;
	}
	
	
	
	
	
	@ThrowAwayValue
	public static <E> List<E> nullaryToList(NullaryFunction<E> generator, int amount)
	{
		return mapNullaryToList(x -> x, generator, amount);
	}
	
	public static <E> Set<E> nullaryToSet(NullaryFunction<E> generator, int amount)
	{
		return mapNullaryToSet(x -> x, generator, amount);
	}
	
	
	
	
	public static <E> Iterator<E> enumerationToIterator(Enumeration<E> e)
	{
		return new Iterator<E>()
		{
			@Override
			public boolean hasNext()
			{
				return e.hasMoreElements();
			}
			
			@Override
			public E next()
			{
				return e.nextElement();
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> void redimTablePreservingContentsIP(@WritableValue SimpleTable<E> table, int newWidth, int newHeight, E elementToAddIfExpanding)
	{
		//Width!
		{
			int current = table.getNumberOfColumns();
			if (newWidth < current)
			{
				while (current != newWidth)
				{
					current--;
					table.deleteColumn(current);
				}
			}
			else if (newWidth > current)
			{
				int h = table.getNumberOfRows();
				
				while (current != newWidth)
				{
					table.insertEmptyColumn(current);
					
					for (int y = 0; y < h; y++)
						table.setCellContents(current, y, elementToAddIfExpanding);
					
					current++;
				}
			}
			//else do nothing :D
		}
		
		
		//Height!
		{
			int current = table.getNumberOfRows();
			if (newHeight < current)
			{
				while (current != newHeight)
				{
					current--;
					table.deleteRow(current);
				}
			}
			else if (newHeight > current)
			{
				int w = table.getNumberOfColumns();
				
				while (current != newHeight)
				{
					table.insertEmptyRow(current);
					
					for (int x = 0; x < w; x++)
						table.setCellContents(x, current, elementToAddIfExpanding);
					
					current++;
				}
			}
			//else do nothing :D
		}
	}
	
	
	
	
	public static <E> void appendRowExpandingIP(@WritableValue SimpleTable<E> table, @ReadonlyValue List<E> row, E elementToAddIfExpanding)
	{
		int tableWidth = table.getNumberOfColumns();
		int rowWidth = row.size();
		
		int h = table.getNumberOfRows();
		
		if (rowWidth > tableWidth)
		{
			redimTablePreservingContentsIP(table, rowWidth, h, elementToAddIfExpanding);
		}
		
		
		int y = h;
		
		table.appendEmptyRow();
		
		int x = 0;
		while (x < rowWidth)
		{
			table.setCellContents(x, y, row.get(x));
			x++;
		}
		
		while (x < tableWidth)  //eg, in case rowWidth < tableWidth :3
		{
			table.setCellContents(x, y, elementToAddIfExpanding);
			x++;
		}
	}
	
	
	
	
	
	@ThrowAwayValue
	public static <E> SimpleTable<E> nonrectangularRowsToNewTable(List<List<E>> rows, E elementToAddIfExpanding)
	{
		if (rows.isEmpty())
		{
			return newTable();
		}
		else
		{
			SimpleTable<E> table = newTableFromOneRow(rows.get(0));
			
			for (List<E> row : rows.subList(1, rows.size()))
				appendRowExpandingIP(table, row, elementToAddIfExpanding);
			
			return table;
		}
	}
	
	
	
	public static <E> List<E> removeAllOPC(List<E> l, E e)
	{
		int i = l.indexOf(e);
		
		if (i == -1)
			return l;
		else
		{
			List<E> ll = new ArrayList<>(l);
			
			while (true)
			{
				ll.remove(i);
				i = ll.indexOf(e);
				
				if (i == -1)
					break;
			}
			
			return ll;
		}
	}
	
	
	
	public static <E> List<E> replaceAllOPC(List<E> l, E old, E neu)
	{
		int i = l.indexOf(old);
		
		if (i == -1)
			return l;
		else
		{
			List<E> ll = new ArrayList<>(l);
			
			while (true)
			{
				ll.set(i, neu);
				i = ll.indexOf(old);
				
				if (i == -1)
					break;
			}
			
			return ll;
		}
	}
	
	
	
	
	
	
	
	public static interface AliasObserver<E>
	{
		public void observeAlias(E id, E targetId);
		public void observeEntryNotSayingIfAlias(E id);
	}
	
	public static <E> Map<E, Set<E>> produceAliasMap(UnaryProcedure<AliasObserver<E>> observeAllIds)
	{
		Map<E, Set<E>> map = new HashMap<>();
		
		observeAllIds.f(new AliasObserver<E>()
		{
			@Override
			public void observeAlias(E startingId, E redirectTargetId)
			{
				if (redirectTargetId != null)
				{
					//Merge them into the same object reference so others will add into it :>
					// (and it's good for performance :3 )
					{
						Set<E> startingAliases = map.get(startingId);
						Set<E> redirectTargetAliases = map.get(redirectTargetId);
						
						if (startingAliases == null)
						{
							if (redirectTargetAliases == null)
							{
								Set<E> s = newSet(startingId, redirectTargetId);
								map.put(redirectTargetId, s);
								map.put(startingId, s);
							}
							else
							{
								redirectTargetAliases.add(startingId);
								map.put(startingId, redirectTargetAliases);
							}
						}
						else
						{
							if (redirectTargetAliases == null)
							{
								startingAliases.add(redirectTargetId);
								map.put(redirectTargetId, startingAliases);
							}
							else
							{
								redirectTargetAliases.addAll(startingAliases);
								map.put(startingId, redirectTargetAliases);
							}
						}
					}
				}
			}
			
			@Override
			public void observeEntryNotSayingIfAlias(E startingId)
			{
				getOrCreate(map, startingId, HashSet::new).add(startingId);
			}
		});
		
		return map;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * It will be fair!!  (Ie, order preserved!)
	 * 
	 * It's not guaranteed to be a thread safe queue, unlike {@link ArrayBlockingQueue}!
	 */
	//@NotThreadSafe
	public static <E> Queue<E> newArrayQueue(int capacity)
	{
		return new ArrayBlockingQueue<>(capacity, true);  //Todo use a non-concurrency-safe version of ArrayBlockingQueue since we don't need that X3
	}
	
	
	
	
	
	
	
	
	
	
	public static <E> E firstOrNull(Iterable<E> c)
	{
		Iterator<E> i = c.iterator();
		return i.hasNext() ? i.next() : null;
	}
	
	public static <E> E firstOrNull(Collection<E> c)
	{
		if (c instanceof List)
			return firstOrNull((List<E>)c);
		else if (c.isEmpty())
			return null;
		else
			return firstOrNull((Iterable<E>)c);
	}
	
	public static <E> E firstOrNull(List<E> c)
	{
		return c.isEmpty() ? null : c.get(0);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param function  the first argument will always be either <code>identity</code> or the output from a previous invocation, and the second argument will always be an element from the input list ({@link Iterable}) :3
	 */
	public static <E> E reduceWithIdentity(BinaryFunction<E, E, E> function, E identity, Iterable<E> inputs)
	{
		E current = identity;
		
		for (E e : inputs)
		{
			current = function.f(current, e);
		}
		
		return current;
	}
	
	
	public static <E> E reduce(BinaryFunction<E, E, E> function, E identityIfListIsEmpty, Iterable<E> inputs)
	{
		try
		{
			return reduceRP(function, inputs);
		}
		catch (EmptyInputReturnPath rp)
		{
			return identityIfListIsEmpty;
		}
	}
	
	public static <E> E reduceNonempty(BinaryFunction<E, E, E> function, Iterable<E> inputs) throws EmptyInputException
	{
		try
		{
			return reduceRP(function, inputs);
		}
		catch (EmptyInputReturnPath rp)
		{
			throw rp.toException();
		}
	}
	
	public static <E> E reduceRP(BinaryFunction<E, E, E> function, Iterable<E> inputs) throws EmptyInputReturnPath
	{
		boolean has = false;
		E current = null;
		
		for (E e : inputs)
		{
			if (has)
			{
				current = function.f(current, e);
			}
			else
			{
				current = e;
				has = true;
			}
		}
		
		if (!has)
			throw EmptyInputReturnPath.I;
		
		return current;
	}
	
	
	
	public static <I, O> O mapreduce(Mapper<I, O> mapper, BinaryFunction<O, O, O> function, O identityIfListIsEmpty, Iterable<I> inputs)
	{
		try
		{
			return mapreduceRP(mapper, function, inputs);
		}
		catch (EmptyInputReturnPath rp)
		{
			return identityIfListIsEmpty;
		}
	}
	
	public static <I, O> O mapreduceNonempty(Mapper<I, O> mapper, BinaryFunction<O, O, O> function, Iterable<I> inputs) throws EmptyInputException
	{
		try
		{
			return mapreduceRP(mapper, function, inputs);
		}
		catch (EmptyInputReturnPath rp)
		{
			throw rp.toException();
		}
	}
	
	public static <I, O> O mapreduceRP(Mapper<I, O> mapper, BinaryFunction<O, O, O> function, Iterable<I> inputs) throws EmptyInputReturnPath
	{
		boolean has = false;
		O current = null;
		
		for (I i : inputs)
		{
			O e;
			try
			{
				e = mapper.f(i);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			if (has)
			{
				current = function.f(current, e);
			}
			else
			{
				current = e;
				has = true;
			}
		}
		
		if (!has)
			throw EmptyInputReturnPath.I;
		
		return current;
	}
	
	
	
	
	
	public static interface InPlaceReducer<I, O>
	{
		public void f(@ReadonlyValue I input, @WritableValue O outputToMergeWith);
	}
	
	/**
	 * @return mutableOutputStartingWithIdentity for convenience :3
	 */
	public static <I, O> O reduceByOperation(UnaryProcedure<UnaryProcedure<I>> theThingThatVisitsButWontReturnAnything, O mutableOutputStartingWithIdentity, InPlaceReducer<I, O> reducer)
	{
		theThingThatVisitsButWontReturnAnything.f(i ->
		{
			reducer.f(i, mutableOutputStartingWithIdentity);
		});
		
		return mutableOutputStartingWithIdentity;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Boolean isWeakKeyedMap(Object o)
	{
		if (o instanceof WeakKeyedMap)
			return ((WeakKeyedMap)o).isWeakKeyedMap();
		else if (o instanceof WeakHashMap)
			return true;
		else if (o.getClass() == HashMap.class)
			return false;
		else if (o.getClass() == Hashtable.class)
			return false;
		else if (o.getClass() == Properties.class)
			return false;
		else if (o.getClass() == TreeMap.class)
			return false;
		else
			return null;
	}
	
	
	public static Boolean isWeakValuedMap(Object o)
	{
		if (o instanceof WeakValuedMap)
			return ((WeakValuedMap)o).isWeakValuedMap();
		else if (o instanceof WeakHashMap)
			return false;
		else if (o.getClass() == HashMap.class)
			return false;
		else if (o.getClass() == Hashtable.class)
			return false;
		else if (o.getClass() == Properties.class)
			return false;
		else if (o.getClass() == TreeMap.class)
			return false;
		else
			return null;
	}
	
	
	public static Boolean isWeakMap(Object o)
	{
		Boolean a = isWeakKeyedMap(o);
		Boolean b = isWeakValuedMap(o);
		
		if (isTrueAndNotNull(a) || isTrueAndNotNull(b))
			return true;
		else if (a == null)
			return b;
		else
			return a;
	}
	
	
	
	
	
	public static Boolean isWeakCollection(Object o)
	{
		if (o instanceof WeakCollection)
			return ((WeakCollection)o).isWeakCollection();
		else if (o.getClass() == ArrayList.class)
			return false;
		else if (o.getClass() == Vector.class)
			return false;
		else if (o.getClass() == HashSet.class)
			return false;
		else
			return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> boolean clearReturningIfChanged(Collection<E> c)
	{
		if (c.isEmpty())
		{
			return false;
		}
		else
		{
			c.clear();
			return true;
		}
	}
	
	public static <K, V> boolean clearReturningIfChanged(Map<K, V> m)
	{
		if (m.isEmpty())
		{
			return false;
		}
		else
		{
			m.clear();
			return true;
		}
	}
	
	
	
	
	
	public static <E> PairOrdered<E, Boolean> setReturningIfChanged(List<E> list, int index, E element)
	{
		E v = list.get(index);
		
		if (eq(v, element))
		{
			return pair(v, false);
		}
		else
		{
			list.set(index, element);
			return pair(v, true);
		}
	}
	
	
	public static <E> boolean replaceAllReturningIfChanged(List<E> list, UnaryOperator<E> operator)
	{
		boolean changed = false;
		
		ListIterator<E> li = list.listIterator();
		
		while (li.hasNext())
		{
			E oldValue = li.next();
			E newValue = operator.apply(oldValue);
			
			if (!eq(newValue, oldValue))
			{
				li.set(newValue);
				changed = true;
			}
		}
		
		return changed;
	}
	
	
	
	
	
	
	
	
	
	public static <K, V> PairOrdered<V, Boolean> mergeReturningIfChanged(Map<K, V> map, K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
	{
		requireNonNull(remappingFunction);
		requireNonNull(value);
		
		PairOrdered<V, Boolean> r = getp(map, key);
		V oldValue = r.getA();
		V newValue = (oldValue == null) ? value : remappingFunction.apply(oldValue, value);
		
		boolean present = r.getB();
		
		if (newValue == null)
		{
			if (present)
				map.remove(key);
			
			return pair(newValue, present);
		}
		else
		{
			if (present)
			{
				if (eq(oldValue, value))
				{
					return pair(newValue, false);
				}
				else
				{
					map.put(key, value);
					return pair(newValue, true);
				}
			}
			else
			{
				map.put(key, value);
				return pair(newValue, true);
			}
		}
	}
	
	
	
	public static <K, V> PairOrdered<V, Boolean> putReturningIfChanged(Map<K, V> map, K key, V value)
	{
		PairOrdered<V, Boolean> p = getp(map, (K)key);
		boolean present = p.getB();
		
		if (present)
		{
			V v = p.getA();
			
			if (eq(v, value))
			{
				return pair(v, false);
			}
			else
			{
				map.put(key, value);
				return pair(v, true);
			}
		}
		else
		{
			map.put(key, value);
			return pair(null, true);
		}
	}
	
	
	
	public static <K, V> PairOrdered<V, Boolean> removeReturningIfChanged(Map<K, V> map, Object key)
	{
		PairOrdered<V, Boolean> p = getp(map, (K)key);
		
		boolean present = p.getB();
		if (present)
		{
			V n = map.remove(key);
			
			if (!eq(p.getA(), n))
				logBug();
		}
		
		return p;
	}
	
	
	
	public static <K, V> boolean putAllReturningIfChanged(Map<K, V> map, Map<? extends K, ? extends V> source)
	{
		boolean changed = false;
		
		for (Entry<? extends K, ? extends V> e : source.entrySet())
		{
			V v;
			try
			{
				v = getrp(map, e.getKey());
			}
			catch (NoSuchMappingReturnPath rp)
			{
				map.put(e.getKey(), e.getValue());
				changed = true;
				continue;
			}
			
			if (!eq(v, e.getValue()))
			{
				map.put(e.getKey(), e.getValue());
				changed = true;
			}
		}
		
		return changed;
	}
	
	
	public static <K, V> PairOrdered<V, Boolean> putIfAbsentReturningIfChanged(Map<K, V> map, K key, V value)
	{
		PairOrdered<V, Boolean> p = getp(map, (K)key);
		boolean present = p.getB();
		
		if (!present)
		{
			map.put(key, value);
			return pair(p.getA(), true);
		}
		else
		{
			if (p.getA() != null)
				logBug(repr(p.getA()));
			
			return pair(null, false);
		}
	}
	
	
	public static <K, V> boolean replaceAllReturningIfChanged(Map<K, V> map, BiFunction<? super K, ? super V, ? extends V> function)
	{
		requireNonNull(function);
		
		boolean changed = false;
		
		HashMap<K, V> copy = new HashMap<>(map);
		
		for (Map.Entry<K, V> entry : map.entrySet())
		{
			K k;
			V v;
			try
			{
				k = entry.getKey();
				v = entry.getValue();
			}
			catch (IllegalStateException exc)
			{
				//Undo!!
				map.clear();
				map.putAll(copy);
				
				// this usually means the entry is no longer in the map.
				throw new ConcurrentModificationException(exc);
			}
			
			// An IllegalStateException thrown from this function is not a concurrent modification problem!
			V newValue = function.apply(k, v);
			
			if (!eq(newValue, v))
			{
				try
				{
					entry.setValue(newValue);
				}
				catch (IllegalStateException exc)
				{
					//Undo!!
					map.clear();
					map.putAll(copy);
					
					// this usually means the entry is no longer in the map.
					throw new ConcurrentModificationException(exc);
				}
				
				changed = true;
			}
		}
		
		return changed;
	}
	
	
	/**
	 * Analog of {@link Map#replace(Object, Object)} (aka putIfPresent() X3 )
	 */
	public static <K, V> PairOrdered<V, Boolean> replaceReturningIfChanged(Map<K, V> map, K key, V value)
	{
		//		if (map.containsKey(key))
		//		{
		//			return map.put(key, value);
		//		}
		//		else
		//		{
		//			return null;
		//		}
		
		PairOrdered<V, Boolean> p = getp(map, (K)key);
		boolean present = p.getB();
		
		if (present)
		{
			V v = p.getA();
			
			if (eq(v, value))
			{
				return pair(v, false);
			}
			else
			{
				map.put(key, value);
				return pair(v, true);
			}
		}
		else
		{
			return pair(null, false);
		}
	}
	
	
	
	public static <K, V> PairOrdered<V, Boolean> computeIfAbsentReturningIfChanged(Map<K, V> map, K key, Function<? super K, ? extends V> mappingFunction)
	{
		requireNonNull(mappingFunction);
		
		V v = map.get(key);
		
		if (v == null)  //the JRE equates null with absent here so we must, too!!
		{
			V newValue = mappingFunction.apply(key);
			
			if (newValue != null)
			{
				map.put(key, newValue);
				return pair(newValue, true);
			}
			else
			{
				return pair(null, false);
			}
		}
		else
		{
			return pair(v, false);
		}
	}
	
	public static <K, V> PairOrdered<V, Boolean> computeIfPresentReturningIfChanged(Map<K, V> map, K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
	{
		requireNonNull(remappingFunction);
		
		V oldValue = map.get(key);
		
		if (oldValue != null)
		{
			V newValue = remappingFunction.apply(key, oldValue);
			
			if (newValue != null)
			{
				if (eq(oldValue, newValue))
				{
					map.put(key, newValue);
					return pair(newValue, true);
				}
				else
				{
					return pair(newValue, false);
				}
			}
			else
			{
				map.remove(key);
				return pair(null, true);
			}
		}
		else
		{
			return pair(null, false);  //the JRE equates null with absent here so we must, too!!
		}
	}
	
	
	public static <K, V> PairOrdered<V, Boolean> computeReturningIfChanged(Map<K, V> map, K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
	{
		requireNonNull(remappingFunction);
		
		PairOrdered<V, Boolean> p = getp(map, (K)key);
		V oldValue = p.getA();
		
		V newValue = remappingFunction.apply(key, oldValue);
		if (newValue == null)
		{
			// delete mapping
			if (p.getB())
			{
				// something to remove
				map.remove(key);
				return pair(null, true);
			}
			else
			{
				// nothing to do. Leave things as they were.
				return pair(null, false);
			}
		}
		else
		{
			// add or replace old mapping
			boolean present = p.getB();
			
			if (present)
			{
				V v = p.getA();
				
				if (eq(v, newValue))
				{
					return pair(v, false);
				}
				else
				{
					map.put(key, newValue);
					return pair(v, true);
				}
			}
			else
			{
				map.put(key, newValue);
				return pair(null, true);
			}
		}
	}
	
	
	
	
	
	
	
	
	public static <E> void setCollectionFrom(Collection<E> dest, Collection<E> source)
	{
		setCollectionFromReturningIfChanged(dest, source);
	}
	
	public static <K, V> void setMapFrom(Map<K, V> dest, Map<K, V> source)
	{
		setMapFromReturningIfChanged(dest, source);
	}
	
	
	
	public static <E> boolean setCollectionFromReturningIfChanged(Collection<E> dest, Collection<E> source)
	{
		if (eqv(dest, source))
		{
			return false;
		}
		else
		{
			dest.clear();
			dest.addAll(source);
			return true;
		}
	}
	
	
	public static <K, V> boolean setMapFromReturningIfChanged(Map<K, V> dest, Map<K, V> source)
	{
		if (eqv(dest, source))
		{
			return false;
		}
		else
		{
			dest.clear();
			dest.putAll(source);
			return true;
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * @param map keys are the (zero-based) indexes for the list!
	 */
	@ReadonlyValue
	public static <E> List<E> dict2list(Map<Integer, E> map) throws NonSurjectiveMapException
	{
		int n = map.size();
		
		List<E> list = (List<E>) asList(new Object[n]);
		boolean[] has = new boolean[n];
		
		for (Entry<Integer, E> e : map.entrySet())
		{
			int index = e.getKey();
			
			asrt(!has[index]);  //how can a java.util.Map have duplicate keys?!? @,@   X'D
			list.set(index, e.getValue());
			has[index] = true;
		}
		
		
		for (int index = 0; index < n; index++)
		{
			if (!has[index])
				throw new NonSurjectiveMapException("Computed indexes are not surjective!!  No entries were assigned to this index!!:  "+index);
		}
		
		return list;
	}
	
	
	
	
	public static <I, Vo> List<Vo> mapToDictlist(Mapper<I, Entry<Integer, Vo>> function, Iterable<I> input) throws NonReverseInjectiveMapException, NonSurjectiveMapException
	{
		//Todo more efficient version ^^'  but keep this impl. to use for unit testing :>
		Map<Integer, Vo> dict = maptodict(function, input);
		return dict2list(dict);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	protected static final ListSet emptyListSet = new ListBackedSet(emptyList());
	
	public static <E> ListSet<E> emptyListSet()
	{
		return emptyListSet;
	}
	
	public static <E> ListSet<E> singletonListSet(E member)
	{
		return new ListBackedSet(singletonList(member));
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * When copying a subregion of a necklace (rotation-equivalent list aka modular list), two copies may be needed if the region crosses over the last-first element break.
	 * This takes a modular start and end point—a modular interval—and returns two normal contiguous list-copy intervals.
	 * (Note that the second one will be 0, 0 indicating an empty interval if only one copy region is needed :3 )
	 * 
	 * + Note that the inputs to this can't encode an Empty Necklace!  (Whereas IE can't encode a full one!)
	 * 
	 * @return {copyInterval0StartInclusive, copyInterval0EndExclusive,  copyInterval1StartInclusive, copyInterval1EndExclusive}
	 */
	public static int[] necklaceCopyIndexesII(int startInclusive, int endInclusive, int necklaceSize)
	{
		asrt(startInclusive >= 0);
		asrt(endInclusive >= 0);
		asrt(startInclusive < necklaceSize);
		asrt(endInclusive < necklaceSize);
		
		if (endInclusive < startInclusive)
		{
			return new int[]{
			startInclusive, necklaceSize,
			0, endInclusive + 1,
			};
		}
		else
		{
			return new int[]{
			startInclusive, endInclusive + 1,
			0, 0,
			};
		}
	}
	
	/**
	 * The same as {@link #necklaceCopyIndexesII(int, int, int)} except it accepts the endpoint of the input as exclusive not inclusive :3
	 * + Note that the inputs to this can't encode a Full Necklace!  (Whereas II can't encode an empty one!)
	 */
	public static int[] necklaceCopyIndexesIE(int startInclusive, int endExclusive, int necklaceSize)
	{
		asrt(startInclusive >= 0);
		asrt(endExclusive >= 0);
		asrt(startInclusive < necklaceSize);
		asrt(endExclusive <= necklaceSize);
		
		if (startInclusive == endExclusive)
			return new int[]{0,0, 0,0};
		else
			return necklaceCopyIndexesII(startInclusive, endExclusive == 0 ? necklaceSize - 1 : endExclusive - 1, necklaceSize);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static boolean isSmallFiniteSet(Set set)
	{
		if (set instanceof SmallFinite)
			return ((SmallFinite)set).isSmallFinite();
		else if (set instanceof ListBackedSet)
			return isSmallFiniteList(((ListBackedSet)set).getBacking());
		else if (set instanceof AssertedSet)
			return isSmallFiniteCollection(((AssertedSet)set).getUnderlying());
		else
			return set == Collections.EMPTY_SET || set instanceof HashSet || set instanceof TreeSet || set.getClass() == singletonSet(null).getClass();
	}
	
	public static boolean isSmallFiniteList(List list)
	{
		if (list instanceof SmallFinite)
			return ((SmallFinite)list).isSmallFinite();
		else
			return list == Collections.EMPTY_LIST || list instanceof ArrayList || list instanceof Vector || list instanceof LinkedList || list.getClass() == singletonSet(null).getClass() || list.getClass() == Arrays.asList().getClass();
	}
	
	public static boolean isSmallFiniteCollection(Collection c)
	{
		if (c instanceof Set)
			return isSmallFiniteSet((Set)c);
		else if (c instanceof List)
			return isSmallFiniteList((List)c);
		else
			return SmallFinite.is(c);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Necklace<?> emptyNecklace()
	{
		return EmptyNecklace.I;
	}
	
	public static <E> Necklace<E> singletonNecklace(E e)
	{
		return new SingletonNecklace<E>(e);
	}
	
	public static <E> Necklace<E> precanonicalizedNecklace(List<E> canonicalRotation)
	{
		return new PrecanonicalizedNecklace<>(canonicalRotation);
	}
	
	public static <E> Necklace<E> necklaceCanonicalizedBySmallestMember(List<E> arbitraryRotation, Comparator<E> comparison)
	{
		int indexOfSmallestMember = MathUtilities.least(intervalIntegersList(0, arbitraryRotation.size()), (a, b) -> comparison.compare(arbitraryRotation.get(a), arbitraryRotation.get(b)));
		return precanonicalizedNecklace(rotated(arbitraryRotation, -indexOfSmallestMember));
	}
	
	
	
	
	
	
	public static Bracelet<?> emptyBracelet()
	{
		return EmptyBracelet.I;
	}
	
	public static <E> Bracelet<E> singletonBracelet(E e)
	{
		return new SingletonBracelet<E>(e);
	}
	
	public static <E> Bracelet<E> precanonicalizedBracelet(List<E> canonicalRotationAndReflection)
	{
		return new PrecanonicalizedBracelet<>(canonicalRotationAndReflection);
	}
	
	public static <E> Bracelet<E> braceletCanonicalizedBySmallestMemberAndSmallestAscendingReflection(List<E> arbitraryRotation, Comparator<E> comparison)
	{
		int indexOfSmallestMember = MathUtilities.least(intervalIntegersList(0, arbitraryRotation.size()), (a, b) -> comparison.compare(arbitraryRotation.get(a), arbitraryRotation.get(b)));
		List<E> l = rotated(arbitraryRotation, -indexOfSmallestMember);
		
		int n = l.size();
		
		if (n > 2)
		{
			E elementBeforeFirst = l.get(n - 1);
			
			//if the loop finishes without any being different, then it means all elements other than the first are equivalent, so we can't determine which reflection to use—but it doesn't matter because reflecting it wouldn't do anything anyway! XD
			
			for (int i = 1; i < n - 1; i++)
			{
				E elementAfterFirst = l.get(i);
				
				int r = comparison.compare(elementBeforeFirst, elementAfterFirst);
				if (r < 0)
					break;
				else if (r > 0)
				{
					l = reversed(l);
					l = rotated(l, 1);  //put the canonical starting element back at the start :3
					break;
				}
				//else (if r == 0): continue
			}
		}
		
		return precanonicalizedBracelet(l);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * NOTE THAT THIS EXACT ALGORITHM MUST NEVER CHANGE; DATABASES DEPEND ON IT
	 * Any changes must keep this functionally equivalent!!!
	 */
	public static <E extends Comparable> int defaultListsCompare(@Nullable List<? extends E> listA, @Nullable List<? extends E> listB)
	{
		return defaultListsCompare(listA, listB, Comparator.naturalOrder());
	}
	
	/**
	 * NOTE THAT THIS EXACT ALGORITHM MUST NEVER CHANGE; DATABASES DEPEND ON IT
	 * Any changes must keep this functionally equivalent!!!
	 */
	public static <E> int defaultListsCompare(@Nullable List<? extends E> listA, @Nullable List<? extends E> listB, @Nonnull Comparator<E> comparison)
	{
		return defaultListsCompare(listA, listB, comparison, true);
	}
	
	/**
	 * NOTE THAT THIS EXACT ALGORITHM MUST NEVER CHANGE; DATABASES DEPEND ON IT
	 * Any changes must keep this functionally equivalent!!!
	 */
	public static <E> int defaultListsCompare(@Nullable List<? extends E> listA, @Nullable List<? extends E> listB, @Nonnull Comparator<E> comparison, boolean lengthsFirst)
	{
		if (listA == null)
			return listB == null ? 0 : -1;
		if (listB == null) //&& listA != null
			return 1;
		
		if (lengthsFirst)
		{
			int r = cmp(listA.size(), listB.size());
			if (r != 0) return r;
		}
		
		Iterator<? extends E> ia = listA.iterator();
		Iterator<? extends E> ib = listB.iterator();
		
		while (true)
		{
			boolean chesterton = ia.hasNext();
			boolean ibn = ib.hasNext();
			
			if (chesterton != ibn)
				return chesterton ? 1 : -1;
			
			if (!arbitrary(chesterton, ibn))
				return 0;
			
			E a = ia.next();
			E b = ib.next();
			
			int r = comparison.compare(a, b);
			if (r != 0)  return r;
		}
	}
	
	/**
	 * NOTE THAT THIS EXACT ALGORITHM MUST NEVER CHANGE; DATABASES DEPEND ON IT
	 * Any changes must keep this functionally equivalent!!!
	 */
	public static <E> int defaultListsCompare(@Nullable List<? extends E> listA, @Nullable List<? extends E> listB, @Nonnull Comparator<E> comparison, boolean lengthsFirst, boolean invertElementComparison, boolean shorterIsLarger)
	{
		//Todo check my boolean math here XD''
		boolean invertIn = invertElementComparison ^ shorterIsLarger;
		boolean invertOut = shorterIsLarger;
		
		int r = defaultListsCompare(listA, listB, invertIn ? comparison.reversed() : comparison, lengthsFirst);
		
		return invertOut ? -r : r;
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * NOTE THAT THIS EXACT ALGORITHM MUST NEVER CHANGE; DATABASES DEPEND ON IT
	 * Any changes must keep this functionally equivalent!!!
	 */
	public static <E> int defaultSetsCompare(@Nullable Set<? extends E> setA, @Nullable Set<? extends E> setB, Comparator<E> comparison)
	{
		return defaultSetsCompareFullRV(setA, setB, comparison).comparisonResult;
	}
	
	
	public static class defaultSetsCompareRV<E>
	{
		public final int comparisonResult;
		public final @Nullable List<E> orderingOnA;  //*might* be null if comparisonResult != 0
		public final @Nullable List<E> orderingOnB;  //*might* be null if comparisonResult != 0
		
		public defaultSetsCompareRV(int comparisonResult, List<E> orderingOnA, List<E> orderingOnB)
		{
			this.comparisonResult = comparisonResult;
			
			if (comparisonResult == 0)
			{
				requireNonNull(orderingOnA);
				requireNonNull(orderingOnB);
			}
			
			this.orderingOnA = orderingOnA;
			this.orderingOnB = orderingOnB;
		}
	}
	
	
	/**
	 * NOTE THAT THIS EXACT ALGORITHM MUST NEVER CHANGE; DATABASES DEPEND ON IT
	 * Any changes must keep this functionally equivalent!!!
	 */
	public static <E> defaultSetsCompareRV<? extends E> defaultSetsCompareFullRV(@Nullable Set<? extends E> setA, @Nullable Set<? extends E> setB, Comparator<E> comparison)
	{
		int r = cmp(setA.size(), setB.size());
		if (r != 0)  return new defaultSetsCompareRV(r, null, null);
		
		List<? extends E> orderingOnA = sorted(setA, comparison);
		List<? extends E> orderingOnB = sorted(setB, comparison);
		
		return new defaultSetsCompareRV(defaultListsCompare(orderingOnA, orderingOnB, comparison), orderingOnA, orderingOnB);
	}
	
	
	
	
	
	/**
	 * Equivalent to {@link #defaultSetsCompare(Set, Set, Comparator)} with 2-element sets :3
	 * 
	 * NOTE THAT THIS EXACT ALGORITHM MUST NEVER CHANGE; DATABASES DEPEND ON IT
	 * Any changes must keep this functionally equivalent!!!
	 */
	public static <E> int defaultDoubletonsCompare(@Nullable PairCommutative<? extends E> setA, @Nullable PairCommutative<? extends E> setB, Comparator<E> comparison)
	{
		final E a0, a1;
		{
			final E aA = setA.getA();
			final E aB = setA.getB();
			
			if (comparison.compare(aA, aB) <= 0)
			{
				a0 = aA;
				a1 = aB;
			}
			else
			{
				a0 = aB;
				a1 = aA;
			}
		}
		
		
		final E b0, b1;
		{
			final E bA = setB.getA();
			final E bB = setB.getB();
			
			if (comparison.compare(bA, bB) <= 0)
			{
				b0 = bA;
				b1 = bB;
			}
			else
			{
				b0 = bB;
				b1 = bA;
			}
		}
		
		
		int r = comparison.compare(a0, b0);
		if (r != 0)  return r;
		
		return comparison.compare(a1, b1);
	}
	
	
	
	/**
	 * Compares the {@link PairOrdered#getA() A}'s first then the {@link PairOrdered#getB() B}'s
	 * 
	 * NOTE THAT THIS EXACT ALGORITHM MUST NEVER CHANGE; DATABASES DEPEND ON IT
	 * Any changes must keep this functionally equivalent!!!
	 */
	public static <A, B> int comparePairNormal(PairOrdered<? extends A, ? extends B> pairA, PairOrdered<? extends A, ? extends B> pairB, Comparator<A> comparisonA, Comparator<B> comparisonB)
	{
		int r = comparisonA.compare(pairA.getA(), pairB.getA());
		if (r != 0)  return r;
		
		return comparisonB.compare(pairA.getB(), pairB.getB());
	}
	
	/**
	 * Compares the {@link PairOrdered#getB() B}'s first then the {@link PairOrdered#getA() A}'s!
	 * 
	 * NOTE THAT THIS EXACT ALGORITHM MUST NEVER CHANGE; DATABASES DEPEND ON IT
	 * Any changes must keep this functionally equivalent!!!
	 */
	public static <A, B> int comparePairReversed(PairOrdered<? extends A, ? extends B> pairA, PairOrdered<? extends A, ? extends B> pairB, Comparator<A> comparisonA, Comparator<B> comparisonB)
	{
		int r = comparisonB.compare(pairA.getB(), pairB.getB());
		if (r != 0)  return r;
		
		return comparisonA.compare(pairA.getA(), pairB.getA());
	}
	
	
	
	
	
	
	/**
	 * NOTE THAT THIS EXACT ALGORITHM MUST NEVER CHANGE; DATABASES DEPEND ON IT
	 * Any changes must keep this functionally equivalent!!!
	 */
	public static <K, V> int defaultMapsCompare(@Nullable Map<? extends K, ? extends V> mapA, @Nullable Map<? extends K, ? extends V> mapB, Comparator<K> keyComparison, Comparator<V> valueComparison)
	{
		defaultSetsCompareRV<? extends K> r = defaultSetsCompareFullRV(mapA.keySet(), mapB.keySet(), keyComparison);
		
		int rc = r.comparisonResult;
		
		if (rc != 0)
		{
			return rc;
		}
		else
		{
			//The keysets are the same, then!
			List<? extends K> keysInOrder = arbitrary(requireNonNull(r.orderingOnA), requireNonNull(r.orderingOnB));
			
			List<? extends V> valuesInOrderA = (List)mapToList(k -> getMandatory((Map<K, V>)mapA, (K)k), keysInOrder);  //Todo a View list implementation would be good here :3
			List<? extends V> valuesInOrderB = (List)mapToList(k -> getMandatory((Map<K, V>)mapB, (K)k), keysInOrder);  //Todo a View list implementation would be good here :3
			
			return defaultListsCompare(valuesInOrderA, valuesInOrderB, valueComparison);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Extracts the common leading elements of a list (out-of-place, readonly) and returns a new list of those elements, and new lists of the uncommon remainders of the original lists :>
	 * + Always uses {@link List#subList(int, int)} for the outputs!
	 * @return (common leading, newA, newB); or null for ([], a, b) ie no-commonalities
	 */
	@ReadonlyValue
	public static @Nullable <E> TripleOrdered<List<E>, List<E>, List<E>> extractCommonFirsts(@ReadonlyValue List<E> a, @ReadonlyValue List<E> b, EqualityComparator<E> eq)
	{
		int an = a.size();
		int bn = b.size();
		
		int n = least(an, bn);
		
		int firstDifferent = 0;
		
		while (true)
		{
			if (firstDifferent == n)
				break;
			
			if (!eq.equals(a.get(firstDifferent), b.get(firstDifferent)))
				break;
			
			firstDifferent++;
		}
		
		if (firstDifferent == 0)
		{
			//There were no commonalities
			//return new List[]{emptyList(), a, b};
			return null;
		}
		else
		{
			List<E> common = arbitrary(a, b).subList(0, firstDifferent);
			List<E> newA = a.subList(firstDifferent, an);
			List<E> newB = b.subList(firstDifferent, bn);
			
			return triple(common, newA, newB);
		}
	}
	
	
	
	
	/**
	 * Extracts the common trailing elements of a list (out-of-place, readonly) and returns new lists of the uncommon remainders of the original lists and a new list of those elements :>
	 * + Always uses {@link List#subList(int, int)} for the outputs!
	 * @return (newA, newB, common trailing); or null for (a, b, []) ie no-commonalities
	 */
	@ReadonlyValue
	public static @Nullable <E> TripleOrdered<List<E>, List<E>, List<E>> extractCommonLasts(@ReadonlyValue List<E> a, @ReadonlyValue List<E> b, EqualityComparator<E> eq)
	{
		int an = a.size();
		int bn = b.size();
		
		int n = least(an, bn);
		
		int firstDifferentReverseIndex = 0;
		
		while (true)
		{
			if (firstDifferentReverseIndex == n)
				break;
			
			if (!eq.equals(a.get(an-firstDifferentReverseIndex-1), b.get(bn-firstDifferentReverseIndex-1)))
				break;
			
			firstDifferentReverseIndex++;
		}
		
		if (firstDifferentReverseIndex == 0)
		{
			//There were no commonalities
			//return new List[]{a, b, emptyList()};
			return null;
		}
		else
		{
			List<E> common = arbitraryBoolean() ? a.subList(an-firstDifferentReverseIndex, an) : b.subList(bn-firstDifferentReverseIndex, bn);
			List<E> newA = a.subList(0, an-firstDifferentReverseIndex);
			List<E> newB = b.subList(0, bn-firstDifferentReverseIndex);
			
			return triple(newA, newB, common);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Extracts the common leading elements of a list (out-of-place, readonly) and returns a new list of those elements, and new lists of the uncommon remainders of the original lists :>
	 * + Always uses {@link List#subList(int, int)} for the outputs!
	 * @return (common leading, new lists); or null for ([], inputs) ie no-commonalities
	 */
	@ReadonlyValue
	public static @Nullable <E> PairOrdered<List<E>, List<List<E>>> extractManyCommonFirsts(@ReadonlyValue List<List<E>> inputs, EqualityComparator<E> eq)
	{
		int nl = inputs.size();
		
		if (nl == 0)
			return pair(emptyList(), emptyList());
		
		int n = leastMap(List::size, inputs);
		
		int firstDifferent;
		{
			int i = 0;
			
			while (true)
			{
				if (i == n)
					break;
				
				boolean allEqual;
				{
					allEqual = true;
					
					E f = inputs.get(0).get(i);
					
					for (int listIndex = 1; listIndex < nl; listIndex++)
					{
						if (!eq.equals(f, inputs.get(listIndex).get(i)))
						{
							allEqual = false;
							break;
						}
					}
				}
				
				if (!allEqual)
					break;
				
				i++;
			}
			
			firstDifferent = i;
		}
		
		if (firstDifferent == 0)
		{
			//There were no commonalities
			//return pair(emptyList(), inputs);
			return null;
		}
		else
		{
			List<E> cl = inputs.get(arbitraryIntNonnegative() % nl);
			List<E> common = cl.subList(0, firstDifferent);
			
			List<List<E>> newLists = mapToList(l -> l.subList(firstDifferent, l.size()), inputs);
			
			return pair(common, newLists);
		}
	}
	
	
	
	
	/**
	 * Extracts the common trailing elements of a list (out-of-place, readonly) and returns new lists of the uncommon remainders of the original lists and a new list of those elements :>
	 * + Always uses {@link List#subList(int, int)} for the outputs!
	 * @return (new lists, common trailing); or null for (inputs, []) ie no-commonalities
	 */
	@ReadonlyValue
	public static @Nullable <E> PairOrdered<List<List<E>>, List<E>> extractManyCommonLasts(@ReadonlyValue List<List<E>> inputs, EqualityComparator<E> eq)
	{
		int nl = inputs.size();
		
		if (nl == 0)
			return pair(emptyList(), emptyList());
		
		int n = leastMap(List::size, inputs);
		
		int firstDifferentReverseIndex;
		{
			int i = 0;
			
			while (true)
			{
				if (i == n)
					break;
				
				boolean allEqual;
				{
					allEqual = true;
					
					E f = inputs.get(0).get(i);
					
					for (int listIndex = 1; listIndex < nl; listIndex++)
					{
						if (!eq.equals(f, inputs.get(listIndex).get(i)))
						{
							allEqual = false;
							break;
						}
					}
				}
				
				if (!allEqual)
					break;
				
				i++;
			}
			
			firstDifferentReverseIndex = i;
		}
		
		if (firstDifferentReverseIndex == 0)
		{
			//There were no commonalities
			//return pair(inputs, emptyList());
			return null;
		}
		else
		{
			List<E> cl = inputs.get(arbitraryIntNonnegative() % nl);
			int cln = cl.size();
			List<E> common = cl.subList(cln-firstDifferentReverseIndex, cln);
			
			List<List<E>> newLists = mapToList(l -> l.subList(0, l.size()-firstDifferentReverseIndex), inputs);
			
			return pair(newLists, common);
		}
	}
	
	
	
	
	
	public static <E extends Collection<?>> E altIfEmpty(E x, E alternate)
	{
		return x.isEmpty() ? alternate : x;
	}
	
	
	
	
	
	
	public static boolean isAll(Iterable<Boolean> l)
	{
		for (Boolean e : l)
			if (!e)
				return false;
		return true;
	}
	
	public static boolean isAny(Iterable<Boolean> l)
	{
		for (Boolean e : l)
			if (e)
				return true;
		return false;
	}
	
	
	
	public static boolean isAll(SimpleBooleanIterable l)
	{
		SimpleBooleanIterator i = l.newSimpleBooleanIterator();
		
		while (true)
		{
			try
			{
				if (!i.nextrpBoolean())
					return false;
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
		}
		
		return true;
	}
	
	public static boolean isAny(SimpleBooleanIterable l)
	{
		SimpleBooleanIterator i = l.newSimpleBooleanIterator();
		
		while (true)
		{
			try
			{
				if (i.nextrpBoolean())
					return true;
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * Like {@link ArrayList#trimToSize()} but works for {@link ArrayList}, {@link Vector}, and anything implementing {@link CollectionWithTrimToSize} :>
	 */
	public static void trimToSize(Collection<?> l)
	{
		//Ordered by how common they are for (probably nanoseconds of) performance XD
		
		if (l instanceof ArrayList)
			((ArrayList)l).trimToSize();
		else if (l instanceof CollectionWithTrimToSize)
			((CollectionWithTrimToSize)l).trimToSize();
		else if (l instanceof Vector)
			((Vector)l).trimToSize();
		
		//else, do nothing; this is a performance hint, so just ignore it if not supported
	}
	
	
	/**
	 * This always outputs the same runtime type as {@link #asList(Object[])}, which helps JIT compiling achieve the same performance between array-based code and list-based code :>
	 */
	public static <E> List<E> trimmed(List<E> l)
	{
		if (l.getClass() == JREGlassBox.ArraysGlassBox.ArrayList)
			return l;
		else
			return (List<E>)Arrays.asList(l.toArray());
	}
	
	
	
	
	
	
	
	public static <E> boolean matchesWildcardPatternGenericLists(int candidateLength, boolean wildcardOnStart, List<List<E>> wildcardPattern, boolean wildcardOnEnd, BinaryFunction<List<E>, Integer, Integer> indexOfSublist) throws IllegalArgumentException
	{
		return matchesWildcardPatternGeneric(candidateLength, wildcardOnStart, wildcardPattern.size(), eIndex -> wildcardPattern.get(eIndex).size(), wildcardOnEnd, (eIndex, start) -> indexOfSublist.f(wildcardPattern.get(eIndex), start));
	}
	
	
	public static boolean matchesWildcardPatternGeneric(int candidateLength, boolean wildcardOnStart, int patternLengthInElements, UnaryFunctionIntToInt getPatternElementLength, boolean wildcardOnEnd, BinaryFunction<Integer, Integer, Integer> indexOf) throws IllegalArgumentException
	{
		return _matchesWildcardPatternGeneric(candidateLength, wildcardOnStart, patternLengthInElements, getPatternElementLength, wildcardOnEnd, indexOf, true);
	}
	
	private static boolean _matchesWildcardPatternGeneric(int candidateLength, boolean wildcardOnStart, int patternLengthInElements, UnaryFunctionIntToInt getPatternElementLength, boolean wildcardOnEnd, BinaryFunction<Integer, Integer, Integer> indexOf, boolean _referenceImpl) throws IllegalArgumentException
	{
		if (patternLengthInElements == 0)
		{
			if (wildcardOnStart && wildcardOnEnd)
				return true;
			else
				throw new IllegalArgumentException("wildcardOnStart="+wildcardOnStart+", wildcardOnEnd="+wildcardOnEnd);
		}
		else if (patternLengthInElements == 1)
		{
			int p = indexOf.f(0, 0);
			
			if (p == -1)
				return false;
			else
			{
				if (!wildcardOnStart && p != 0)
					return false;
				
				if (!wildcardOnEnd && p + getPatternElementLength.f(0) != candidateLength)
					return false;
			}
			
			return true;
		}
		else
		{
			int cursor = 0;
			
			for (int i = 0; i < patternLengthInElements; i++)
			{
				boolean first = i == 0;
				boolean last = i == patternLengthInElements - 1;
				
				int p = indexOf.f(i, cursor);
				
				if (p == -1)
					return false;
				
				if (first && !wildcardOnStart && p != 0)
					return false;
				
				int end = p + getPatternElementLength.f(i);
				
				if (last && !wildcardOnEnd && end != candidateLength)
					return false;
				
				cursor = end;
			}
			
			return true;
		}
	}
	
	@ImplementationTransparency  //For testing!
	public static <S> boolean _matchesWildcardPatternGeneric_ImplA(int candidateLength, boolean wildcardOnStart, int patternLengthInElements, UnaryFunctionIntToInt getPatternElementLength, boolean wildcardOnEnd, BinaryFunction<Integer, Integer, Integer> indexOf) throws IllegalArgumentException
	{
		return _matchesWildcardPatternGeneric(candidateLength, wildcardOnStart, patternLengthInElements, getPatternElementLength, wildcardOnEnd, indexOf, true);
	}
	
	@ImplementationTransparency  //For testing!
	public static <S> boolean _matchesWildcardPatternGeneric_ImplB(int candidateLength, boolean wildcardOnStart, int patternLengthInElements, UnaryFunctionIntToInt getPatternElementLength, boolean wildcardOnEnd, BinaryFunction<Integer, Integer, Integer> indexOf) throws IllegalArgumentException
	{
		return _matchesWildcardPatternGeneric(candidateLength, wildcardOnStart, patternLengthInElements, getPatternElementLength, wildcardOnEnd, indexOf, false);
	}
	
	
	
	
	
	
	
	
	
	//TODO testtttttt theseeeeee! X3'
	
	public static <E> int findRotatingPatternBreakAnyPhase(Iterable<E> list, List<Predicate<E>> patterns)
	{
		return findRotatingPatternBreakAnyPhase(simpleIterator(list), patterns);
	}
	
	public static <E> int findRotatingPatternBreak(Iterable<E> list, List<Predicate<E>> patterns)
	{
		return findRotatingPatternBreak(simpleIterator(list), patterns);
	}
	
	
	
	/**
	 * @return never returns the first element since that defines the phase!  (this only returns 0 if the input is empty, in which case it always returns 0!)
	 */
	public static <E> int findRotatingPatternBreakAnyPhase(SimpleIterator<E> list, List<Predicate<E>> patterns)
	{
		E first;
		try
		{
			first = list.nextrp();
		}
		catch (StopIterationReturnPath exc)
		{
			//Empty input!
			return 0;
		}
		
		int indexOfFirstMatchingPattern = findFirstIndex(p -> p.test(first), patterns);
		
		return findRotatingPatternBreak(list, rotated(patterns, -indexOfFirstMatchingPattern));
	}
	
	
	/**
	 * @return the index of the first element to break the pattern or the size of the input "list" if none break it! :D
	 */
	public static <E> int findRotatingPatternBreak(SimpleIterator<E> list, List<Predicate<E>> patterns)
	{
		int n = patterns.size();
		
		int i = 0;
		
		while (true)
		{
			E e;
			try
			{
				e = list.nextrp();
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
			
			Predicate<E> p = patterns.get(i % n);
			
			if (!p.test(e))
				return i;
			else
				i++;
		}
		
		return i;
	}
	
	
	
	
	
	
	
	
	public static <E> E getRandomElement(Collection<E> elements, UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound)
	{
		if (CollectionWithGetRandomElement.is(elements))
			return ((CollectionWithGetRandomElement<E>)elements).getRandomElement(pullIntegerZeroToExclusiveHighBound);
		else
			return defaultGetRandomAccess(elements, pullIntegerZeroToExclusiveHighBound.f(elements.size()));
	}
	
	
	public static <K, V> K getRandomKey(Map<K, V> map, UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound)
	{
		if (MapWithGetRandomEntry.is(map))
			return ((MapWithGetRandomEntry<K, V>)map).getRandomKey(pullIntegerZeroToExclusiveHighBound);
		else
			return getRandomElement(map.keySet(), pullIntegerZeroToExclusiveHighBound);
	}
	
	public static <K, V> V getRandomValue(Map<K, V> map, UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound)
	{
		if (MapWithGetRandomEntry.is(map))
			return ((MapWithGetRandomEntry<K, V>)map).getRandomValue(pullIntegerZeroToExclusiveHighBound);
		else
			return getRandomElement(map.values(), pullIntegerZeroToExclusiveHighBound);
	}
	
	public static <K, V> Entry<K, V> getRandomEntry(Map<K, V> map, UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound)
	{
		if (MapWithGetRandomEntry.is(map))
			return ((MapWithGetRandomEntry<K, V>)map).getRandomEntry(pullIntegerZeroToExclusiveHighBound);
		else
			return getRandomElement(map.entrySet(), pullIntegerZeroToExclusiveHighBound);
	}
}
