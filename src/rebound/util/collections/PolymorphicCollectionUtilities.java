package rebound.util.collections;

import static rebound.util.AngryReflectionUtility.*;
import static rebound.util.Primitives.*;
import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.w3c.dom.NodeList;
import rebound.annotations.hints.PerformanceSetting;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.operationspecification.CollectionValue;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.exceptions.AlreadyExistsException;
import rebound.exceptions.NoSuchElementReturnPath;
import rebound.exceptions.NoSuchMappingReturnPath;
import rebound.exceptions.ReadonlyPossiblyUnsupportedOperationException;
import rebound.exceptions.ReadonlyUnsupportedOperationException;
import rebound.exceptions.StopIterationReturnPath;
import rebound.math.MathUtilities;
import rebound.util.BasicExceptionUtilities;
import rebound.util.NIOBufferUtilities;
import rebound.util.Primitives;
import rebound.util.classhacking.jre.BetterJREGlassbox;
import rebound.util.collections.SimpleIterator.SimpleIterable;
import rebound.util.collections.SimpleIterator.SimpleIteratorWithRemove;
import rebound.util.collections.prim.PrimitiveCollections;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.CharacterList;
import rebound.util.collections.prim.PrimitiveCollections.DoubleList;
import rebound.util.collections.prim.PrimitiveCollections.FloatList;
import rebound.util.collections.prim.PrimitiveCollections.IntegerList;
import rebound.util.collections.prim.PrimitiveCollections.LongList;
import rebound.util.collections.prim.PrimitiveCollections.ShortList;
import rebound.util.functional.EqualityComparator;
import rebound.util.functional.FunctionInterfaces.UnaryFunction;
import rebound.util.objectutil.BasicObjectUtilities;

//We say "collection" but include Arrays, Iterables, Iterators, and Enumerations  ^_^
//edit: And now Maps/Dicts! :D

public class PolymorphicCollectionUtilities
{
	@CollectionValue
	@PossiblySnapshotPossiblyLiveValue
	public static Object anySublist(@CollectionValue Object list, int start, int length)
	{
		//Override :3
		if (list instanceof SublistOverride)
		{
			return ((SublistOverride)list).sublist(start, length);
		}
		
		//Collapse!! :D!
		else if (list instanceof Sublist)
		{
			Sublist listC = (Sublist)list;
			
			CollectionUtilities.rangeCheckIntervalByLength(listC.size(), start, length);
			
			return anySublist(listC.getUnderlying(), listC.getSublistStartingIndex() + start, length);
		}
		
		
		//Grandfathering ;D!
		else if (list instanceof List)
		{
			//Also takes care of RichGeneralList ^w^
			return ((List)list).subList(start, start+length);
		}
		else if (list instanceof CharSequence)
		{
			//To keep java.lang.String from copying, currently best to just use char[] and unmodifiable general-lists :P
			return ((CharSequence)list).subSequence(start, start+length);
		}
		else if (list instanceof Buffer)
		{
			Buffer listC = (Buffer)list;
			
			CollectionUtilities.rangeCheckIntervalByLength(listC.remaining(), start, length);
			
			Buffer slice = NIOBufferUtilities.slice(listC);
			slice.limit(listC.position() + start + length);
			slice.position(listC.position() + start);
			
			return slice;
		}
		else if (list.getClass().isArray())
		{
			return anySublist(anyToList(list), start, length);
			//return new RichGeneralListBackedbyLiveArray(list, start, length, false);
		}
		else
		{
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(list);
		}
	}
	
	//<Unifying Map and List ^_^
	public static Object getuni(Object mapOrList, Object key)
	{
		if (mapOrList instanceof Map)
		{
			return ((Map)mapOrList).get(key);
		}
		else if (mapOrList instanceof List)
		{
			int indexkey = MathUtilities.safeCastIntegerToS32(key);
			if (indexkey < 0 || indexkey >= ((List)mapOrList).size())
				return null; //no mapping :>
			return ((List)mapOrList).get(indexkey);
		}
		else if (mapOrList.getClass().isArray())
		{
			int indexkey = MathUtilities.safeCastIntegerToS32(key);
			if (indexkey < 0 || indexkey >= Array.getLength(mapOrList))
				return null; //no mapping :>
			return Array.get(mapOrList, indexkey);
		}
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(mapOrList);
	}
	
	public static Object getunirp(Object mapOrList, Object key) throws NoSuchMappingReturnPath
	{
		if (mapOrList instanceof Map)
		{
			return CollectionUtilities.getrp(((Map)mapOrList), key);
		}
		else if (mapOrList instanceof List)
		{
			int indexkey = MathUtilities.safeCastIntegerToS32(key);
			if (indexkey < 0 || indexkey >= ((List)mapOrList).size())
				throw NoSuchMappingReturnPath.I;
			return ((List)mapOrList).get(indexkey);
		}
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(mapOrList);
	}
	
	public static void setuni(Object mapOrList, Object key, Object value) throws IndexOutOfBoundsException
	{
		if (mapOrList instanceof Map)
		{
			((Map)mapOrList).put(key, value);
		}
		else if (mapOrList instanceof List)
		{
			int indexkey = MathUtilities.safeCastIntegerToS32(key);
			int len = ((List)mapOrList).size();
			if (indexkey < 0 || indexkey > len)
				throw new IndexOutOfBoundsException(String.valueOf(indexkey));
			if (indexkey == len)
				((List)mapOrList).add(value);
			else
				((List)mapOrList).set(indexkey, value);
		}
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(mapOrList);
	}
	
	//	@LiveValue
	//	public static Set keysuni(Object mapOrList)
	//	{
	//		if (mapOrList instanceof Map)
	//			return ((Map)mapOrList).keySet();
	//		else if (mapOrList instanceof List)
	//		{
	//			final List list = (List)mapOrList;
	//
	//			return new AbstractSingleContiguousIntegerRangeHybridListAndSet()
	//			{
	//				@Override
	//				public long getInclusiveLowBound()
	//				{
	//					return 0;
	//				}
	//
	//				@Override
	//				public long getExclusiveHighBound()
	//				{
	//					return list.size();
	//				}
	//			};
	//		}
	//		else
	//			throw ExceptionUtilities.newClassCastExceptionOrNullPointerException(mapOrList);
	//	}
	
	public static Collection contentsuni(Object thing)
	{
		if (thing instanceof Map)
			return ((Map)thing).values();
		else if (thing instanceof Collection) //handles java.util.List :>
			return (Collection)thing;
		else if (thing == null)
			return null;
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(thing);
	}
	
	/**
	 * Unifies arrays, {@link RandomAccess}, and {@link FastRandomAccess}!   ^w^
	 */
	@PerformanceSetting
	public static boolean isRandomAccessFast(Object list)
	{
		if (list.getClass().isArray())
			return true;
		else if (list instanceof RandomAccess)
			return true;
		else if (FastRandomAccess.is(list))
			return true;
		else
			return false;
	}
	
	public static Iterator anyToReversedIterator(Object list)
	{
		if (list instanceof Object[])
			return CollectionUtilities.reversedIterator((Object[])list);
		else if (list instanceof ListIterator)
			return CollectionUtilities.reversedIterator((ListIterator)list);
		else
			return CollectionUtilities.reversedIterator(anyToList(list));
	}
	
	public static SimpleIterator anyToReversedSimpleIterator(Object list)
	{
		return anyToSimpleIterator(anyToReversedIterator(list));
	}
	
	public static int indexOf(Predicate predicate, @CollectionValue Object list)
	{
		if (list instanceof Object[])
		{
			return CollectionUtilities.indexOf(predicate, (Object[])list);
		}
		else if (list instanceof List)
		{
			return CollectionUtilities.indexOf(predicate, (List)list);
		}
		else
		{
			int i = 0;
			Iterator it = anyToIterator(list);
			while (it.hasNext())
			{
				Object element = it.next();
				if (predicate.test(element))
					return i;
				i++;
			}
			return -1;
		}
	}
	
	public static boolean contains(Predicate predicate, @CollectionValue Object list)
	{
		return indexOf(predicate, list) != -1;
	}
	
	public static int count(Predicate predicate, @CollectionValue Object collection)
	{
		if (collection instanceof Object[])
		{
			return CollectionUtilities.count(predicate, (Object[])collection);
		}
		else if (collection instanceof List)
		{
			return CollectionUtilities.count(predicate, (List)collection);
		}
		else
		{
			int count = 0;
			Iterator it = anyToIterator(collection);
			while (it.hasNext())
			{
				Object element = it.next();
				if (predicate.test(element))
					count++;
			}
			return count;
		}
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue //because you wouldn't know if writes will be propagated through or not! ><
	public static Object anyToArray(final Object x, final Class componentType)
	{
		//		if (componentType.isPrimitive())
		//			throw new IllegalArgumentException("Generics don't allow us to support primitive arrays; sorries ._.");
		//		else if (componentType == Object.class)
		//			return toObjectArray(x);
		
		
		if (x == null)
			return null;
		
		else if (x instanceof Object[])
		{
			if (componentType.isPrimitive())
			{
				if (!Primitives.getWrapperClassFromPrimitiveOrPassThroughWrapper(componentType).isAssignableFrom(x.getClass().getComponentType()))
					throw new ClassCastException("Trying to convert a "+x.getClass().getCanonicalName()+" into a "+componentType.getCanonicalName()+"[]");
				
				//Source is not primitive, dest is --> unboxingggg!
				return anyToNewArray(x, componentType); //no better way I sees XD
			}
			else
			{
				if (x.getClass().getComponentType() == componentType)   //note: not isAssignableFrom / instanceof / etc, *exactly equals*  (because you might be making an array that you haves to stick a supertype object into, not just sametype and subtype objects!!)
					return x;
				else
					return Arrays.copyOf((Object[])x, Array.getLength(x), arrayClassOf(componentType)); //no better way I sees XD
			}
		}
		else if (x.getClass().isArray()) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		{
			if (componentType.isPrimitive())
			{
				if (componentType != x.getClass().getComponentType())
					//Todo mayyyyybe do lossless casts (eg, int->long) in futures..but even then that wouldn't be right for eg, treating them as unsigned! :P!
					throw new ClassCastException("Trying to convert a "+x.getClass().getCanonicalName()+" into a "+componentType.getCanonicalName()+"[]");
				
				return x;
			}
			else
			{
				if (!componentType.isAssignableFrom(Primitives.getWrapperClassFromPrimitiveOrPassThroughWrapper(x.getClass().getComponentType())))
					throw new ClassCastException("Trying to convert a "+x.getClass().getCanonicalName()+" into a "+componentType.getCanonicalName()+"[]");
				
				//Source is primitive, dest is not --> boxingggg!
				return anyToNewArray(x, componentType); //no better way I sees XD
			}
		}
		
		/* <<<
	primxp
			if (x instanceof _$$Primitive$$_List && componentType == _$$prim$$_.class)
				return ((_$$Primitive$$_List)x).to_$$Prim$$_ArrayPossiblyLive();
		 */
		if (x instanceof BooleanList && componentType == boolean.class)
			return ((BooleanList)x).toBooleanArrayPossiblyLive();
		if (x instanceof ByteList && componentType == byte.class)
			return ((ByteList)x).toByteArrayPossiblyLive();
		if (x instanceof CharacterList && componentType == char.class)
			return ((CharacterList)x).toCharArrayPossiblyLive();
		if (x instanceof ShortList && componentType == short.class)
			return ((ShortList)x).toShortArrayPossiblyLive();
		if (x instanceof FloatList && componentType == float.class)
			return ((FloatList)x).toFloatArrayPossiblyLive();
		if (x instanceof IntegerList && componentType == int.class)
			return ((IntegerList)x).toIntArrayPossiblyLive();
		if (x instanceof DoubleList && componentType == double.class)
			return ((DoubleList)x).toDoubleArrayPossiblyLive();
		if (x instanceof LongList && componentType == long.class)
			return ((LongList)x).toLongArrayPossiblyLive();
		
		// >>>
		
		
		
		if (TransparentContiguousArrayBackedCollection.is(x))
		{
			Slice underlyingArraySlice = ((TransparentContiguousArrayBackedCollection)x).getLiveContiguousArrayBackingUNSAFE();
			Object underlyingArray = underlyingArraySlice.getUnderlying();
			int size = underlyingArraySlice.getLength();
			int underlyingArrayOffset = underlyingArraySlice.getOffset();
			
			//Check component types are compatible!
			{
				Class xComponentType = underlyingArray.getClass().getComponentType();
				
				if (!componentType.isAssignableFrom(xComponentType))  //Note!  Handles primitives like we do!  Strict equality makes true ^w^
					throw new ClassCastException("Source elements are "+xComponentType.getCanonicalName()+" but dest elements are "+componentType.getCanonicalName()+" it's not assignable!  :P!");
			}
			
			if (underlyingArrayOffset == 0 && size == Array.getLength(underlyingArray) && underlyingArray.getClass().getComponentType() == componentType)
				return underlyingArray;
			else
			{
				Object newArray = Array.newInstance(componentType, size);
				System.arraycopy(underlyingArray, underlyingArrayOffset, newArray, 0, size);
				return newArray;
			}
		}
		
		if (x instanceof Sublist)
		{
			//Note that this here would also take care of unwrapping RichGeneralListBackedbyLiveArray's, if the above didn't x3
			Object underlying = ((Sublist)x).getUnderlying();
			if (((Sublist)x).getSublistStartingIndex() == 0 && ((Sublist)x).size() == BasicCollectionUtilities.sizeOfCollectionlike(underlying))
				return anyToArray(underlying, componentType);
		}
		
		return anyToNewArray(x, componentType); //no better way I sees XD
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static Object anyToNewArray(final Object x, final Class componentType) throws ClassCastException
	{
		if (componentType.isPrimitive())
			throw new IllegalArgumentException("Generics don't allow us to support primitive arrays; sorries ._.");
		//		else if (componentType == Object.class)
		//			return toNewObjectArray(x);
		
		
		if (x == null)
			throw new NullPointerException();
		
		else if (x instanceof Object[])
		{
			if (!componentType.isAssignableFrom(x.getClass().getComponentType()))
				throw new ClassCastException("Trying to convert a "+x.getClass().getCanonicalName()+" into a "+componentType.getCanonicalName()+"[]");
			
			if (x.getClass().getComponentType() == componentType)
				return ((Object[])x).clone();
			else
				return Arrays.copyOf((Object[])x, Array.getLength(x), arrayClassOf(componentType));
		}
		else if (x.getClass().isArray()) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		{
			/*  Can't do because generics xP
				if (componentType.isPrimitive())
				{
					if (componentType != x.getClass().getComponentType())
						throw new ClassCastException("Trying to convert a "+x.getClass().getCanonicalName()+" into a "+componentType.getCanonicalName()+"[]");
					
					return ObjectUtilities.attemptClone(x);
				}
			 */
			
			if (!componentType.isAssignableFrom(Primitives.getWrapperClassFromPrimitiveOrPassThroughWrapper(x.getClass().getComponentType())))
				throw new ClassCastException("Trying to convert a "+x.getClass().getCanonicalName()+" into a "+componentType.getCanonicalName()+"[]");
			
			Object[] r = (Object[])Array.newInstance(componentType, Array.getLength(x));
			Primitives.boxInto(x, r);
			return r;
		}
		
		/* <<<
	primxp
			if (x instanceof _$$Primitive$$_List && componentType == _$$prim$$_.class)
				return ((_$$Primitive$$_List)x).to_$$Prim$$_Array();
		 */
		if (x instanceof BooleanList && componentType == boolean.class)
			return ((BooleanList)x).toBooleanArray();
		if (x instanceof ByteList && componentType == byte.class)
			return ((ByteList)x).toByteArray();
		if (x instanceof CharacterList && componentType == char.class)
			return ((CharacterList)x).toCharArray();
		if (x instanceof ShortList && componentType == short.class)
			return ((ShortList)x).toShortArray();
		if (x instanceof FloatList && componentType == float.class)
			return ((FloatList)x).toFloatArray();
		if (x instanceof IntegerList && componentType == int.class)
			return ((IntegerList)x).toIntArray();
		if (x instanceof DoubleList && componentType == double.class)
			return ((DoubleList)x).toDoubleArray();
		if (x instanceof LongList && componentType == long.class)
			return ((LongList)x).toLongArray();
		
		// >>>
		
		if (TransparentContiguousArrayBackedCollection.is(x))
		{
			Slice underlyingArraySlice = ((TransparentContiguousArrayBackedCollection)x).getLiveContiguousArrayBackingUNSAFE();
			Object underlyingArray = underlyingArraySlice.getUnderlying();
			int size = underlyingArraySlice.getLength();
			int underlyingArrayOffset = underlyingArraySlice.getOffset();
			
			//Check component types are compatible!
			{
				Class xComponentType = underlyingArray.getClass().getComponentType();
				
				if (!componentType.isAssignableFrom(xComponentType))  //Note!  Handles primitives like we do!  Strict equality makes true ^w^
					throw new ClassCastException("Source elements are "+xComponentType.getCanonicalName()+" but dest elements are "+componentType.getCanonicalName()+"; it's not assignable!  :P!");
			}
			
			Object newArray = Array.newInstance(componentType, size);
			System.arraycopy(underlyingArray, underlyingArrayOffset, newArray, 0, size);
			return newArray;
		}
		
		if (x instanceof Sublist)
		{
			//Note that this here would also take care of unwrapping RichGeneralListBackedbyLiveArray's, if the above didn't x3
			Object underlying = ((Sublist)x).getUnderlying();
			if (((Sublist)x).getSublistStartingIndex() == 0 && ((Sublist)x).size() == BasicCollectionUtilities.sizeOfCollectionlike(underlying))
				return anyToNewArray(underlying, componentType);
		}
		
		//		if (x instanceof RichGeneralList)
		//		{
		//			//Check component types are compatible!
		//			{
		//				Class xComponentType = ((RichGeneralList)x).getComponentType();
		//
		//				if (!componentType.isAssignableFrom(xComponentType))  //Note!  Handles primitives like we do!  Strict equality makes true ^w^
		//					throw new ClassCastException("Source elements are "+xComponentType.getCanonicalName()+" but dest elements are "+componentType.getCanonicalName()+"; it's not assignable!  :P!");
		//			}
		//
		//			//General implementation ^_^
		//			int size = ((RichGeneralList)x).size();
		//			Object array = Array.newInstance(componentType, size);
		//			((RichGeneralList)x).copyIntoArray(0, array, 0, size);
		//			return array;
		//		}
		
		
		
		//SPEED Support primitive iterators/iterables toooo :3
		/* << <
			primxp
			
			if (componentType == _$$prim$$_.class)
			{
				//Quite possibly faster to (possibly!) run through it once and allocate the array a single time, than have to reallocate it and copy multiple times!
				int size = collectionSize(x);
				_$$prim$$_[] array = new _$$prim$$_[size];
				
				for (int i = 0; i < size; i++)
				{
					try
					{
						array[i] = xC.nextrp_$$Prim$$_();
					}
					catch (StopIterationReturnPath exc)
					{
						throw new ImpossibleException("The size check or first pass showed we had this many elements! ;; ");
					}
				}
				
				return array;
			}
		 */
		
		// >> >
		
		
		
		
		
		//Below here are non-general lists, ie, just reference-types   But we can still unbox them possibly! :D
		if (componentType.isPrimitive())
		{
			//We're slow, but this is a slow thing anyways; ohwell :P
			//SPEED add auto[un]boxing optimized code paths to all the reference nonscalars below!
			Object[] objectArray = (Object[])anyToArray(x, Primitives.getWrapperClassFromPrimitiveOrPassThroughWrapper(componentType));  //use toArray not toNewArray since we are going to make another copy anyways! :P
			return Primitives.unbox(objectArray);
		}
		
		else if (x instanceof Collection)
		{
			return ((Collection)x).toArray((Object[])Array.newInstance(componentType, ((Collection)x).size()));
		}
		else if (x instanceof SimpleIterable)
		{
			return anyToNewArray(((SimpleIterable)x).simpleIterator(), componentType);
		}
		else if (x instanceof Iterable)
		{
			List list = new ArrayList();
			for (Object e : (Iterable)x)
				list.add(e);
			return list.toArray((Object[])Array.newInstance(componentType, list.size()));
		}
		else if (x instanceof Iterator)
		{
			List list = new ArrayList();
			while (((Iterator)x).hasNext())
				list.add(((Iterator)x).next());
			return list.toArray((Object[])Array.newInstance(componentType, list.size()));
		}
		else if (x instanceof SimpleIterator)
		{
			List list = new ArrayList();
			
			while (true)
			{
				try
				{
					list.add(((SimpleIterator)x).nextrp());
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return list.toArray((Object[])Array.newInstance(componentType, list.size()));
		}
		else if (x instanceof Enumeration)
		{
			List list = new ArrayList();
			while (((Enumeration)x).hasMoreElements())
				list.add(((Enumeration)x).nextElement());
			return list.toArray((Object[])Array.newInstance(componentType, list.size()));
		}
		
		else
			throw new ClassCastException(CollectionUtilities.Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static Object[] anyToObjectArray(final Object x)
	{
		return (Object[])anyToArray(x, Object.class);
		
		
		//if (x == null)
		//	return null;
		//
		//else if (x instanceof Object[])
		//	return (Object[])x;
		//else
		//	return toNewObjectArray(x); //no better way I sees XD
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static Object[] anyToNewObjectArray(final Object x)
	{
		return (Object[])anyToNewArray(x, Object.class);
		
		
		//if (x == null)
		//	throw new NullPointerException();
		//
		//else if (x instanceof Object[])
		//{
		//	return ((Object[])x).clone();
		//}
		//else if (x.getClass().isArray()) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		//{
		//	return Primitives.box(x);
		//}
		//else if (x instanceof Collection)
		//{
		//	return ((Collection)x).toArray();
		//}
		//else if (x instanceof Iterable)
		//{
		//	return toCollection(x).toArray(); //no better way I sees XD
		//}
		//else if (x instanceof SimpleIterable)
		//{
		//	return toCollection(x).toArray(); //no better way I sees XD
		//}
		//else if (x instanceof Iterator)
		//{
		//	return toCollection(x).toArray(); //no better way I sees XD
		//}
		//else if (x instanceof Enumeration)
		//{
		//	return toCollection(x).toArray(); //no better way I sees XD
		//}
		//else if (x instanceof SimpleIterator)
		//{
		//	return toCollection(x).toArray(); //no better way I sees XD
		//}
		//
		//else
		//	throw new ClassCastException(Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
	}
	
	/**
	 * This produces multi-useable {@link Iterable}s, in contrast with {@link CollectionUtilities#singleUseIterable(Iterator)} and such :>
	 * 
	 * Note: Doesn't really need a 'toNewIterable' does it? ;>
	 */
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static Iterable anyToIterable(final Object x)
	{
		if (x == null)
			return null;
		
		else if (x instanceof Iterable) //takes care of not-optimizable Collection case, also takes care of SimpleIterable! ^wwww^
		{
			return (Iterable)x;
		}
		else if (x instanceof Object[])
		{
			return Arrays.asList((Object[])x);
		}
		else if (x.getClass().isArray() || (x instanceof Slice && ((Slice)x).getUnderlying().getClass().isArray())) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		{
			//Avoid heavy allocationthings! :D
			return new Iterable()
			{
				@Override
				public Iterator iterator()
				{
					return anyToIterator(x);
				}
			};
		}
		else if (x instanceof SimpleIterable)
		{
			return new Iterable()
			{
				@Override
				public Iterator iterator()
				{
					return anyToIterator(((SimpleIterable)x).simpleIterator());
				}
			};
		}
		else if (x instanceof Iterator)
		{
			//@SnapshotValue XP
			return anyToCollection(x); //no better way I sees XD   (for multi-use iterables, that is!)
		}
		else if (x instanceof Enumeration)
		{
			//@SnapshotValue XP
			return anyToCollection(x); //no better way I sees XD   (for multi-use iterables, that is!)
		}
		else if (x instanceof SimpleIterator)
		{
			//@SnapshotValue XP
			return anyToCollection(x); //no better way I sees XD   (for multi-use iterables, that is!)
		}
		else if (x instanceof NodeList)
		{
			return anyToIterable(anyToSimpleIterable(x));
		}
		
		else
			throw new ClassCastException(CollectionUtilities.Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
	}
	
	/**
	 * This produces multi-useable {@link SimpleIterable}s, in contrast with {@link CollectionUtilities#singleUseIterable(Iterator)} and such :>
	 * 
	 * Note: Doesn't really need a 'toNewSimpleIterable' does it? ;>
	 */
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static SimpleIterable anyToSimpleIterable(final Object x)
	{
		if (x == null)
			return null;
		
		else if (x instanceof SimpleIterable)
		{
			return (SimpleIterable)x;
		}
		else if (x instanceof Object[] || x instanceof Iterable || x instanceof Map || x.getClass().isArray())
		{
			return new SimpleIterable()
			{
				@Override
				public SimpleIterator simpleIterator()
				{
					return anyToSimpleIterator(x);
				}
			};
		}
		else if (x instanceof NodeList)
		{
			return () -> anyToSimpleIterator(x);
		}
		
		//Single shot things we has to do snapshottily xP
		else if (x instanceof Iterator)
		{
			//@SnapshotValue XP
			return anyToSimpleIterable(anyToIterable(x)); //no better way I sees XD   (for multi-use iterables, that is!)
		}
		else if (x instanceof Enumeration)
		{
			//@SnapshotValue XP
			return anyToSimpleIterable(anyToIterable(x)); //no better way I sees XD   (for multi-use iterables, that is!)
		}
		else if (x instanceof SimpleIterator)
		{
			//@SnapshotValue XP
			return anyToSimpleIterable(anyToIterable(x)); //no better way I sees XD   (for multi-use iterables, that is!)
		}
		
		else
			throw new ClassCastException(CollectionUtilities.Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static Collection anyToCollection(final Object x)
	{
		if (x == null)
			return null;
		
		else if (x instanceof Collection)
		{
			return (Collection)x;
		}
		else if (x instanceof Object[])
		{
			return anyToList(x); //no better way I sees XD
		}
		else if (x.getClass().isArray()) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		{
			return anyToList(x); //no better way I sees XD
		}
		else if (x instanceof Iterable)
		{
			return anyToList(x); //no better way I sees XD
		}
		else if (x instanceof SimpleIterable)
		{
			return anyToList(x); //no better way I sees XD
		}
		else if (x instanceof Iterator)
		{
			return anyToList(x); //no better way I sees XD
		}
		else if (x instanceof Enumeration)
		{
			return anyToList(x); //no better way I sees XD
		}
		else if (x instanceof SimpleIterator)
		{
			return anyToList(x); //no better way I sees XD
		}
		
		else
			throw new ClassCastException(CollectionUtilities.Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static Collection anyToNewMutableVariablelengthCollection(final Object x)
	{
		return anyToNewMutableVariablelengthList(x); //no better way I sees! XD!!
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static List anyToList(final Object x)
	{
		if (x == null)
			return null;
		
		else if (x instanceof List)
		{
			return (List)x;
		}
		else if (x instanceof Slice)
		{
			Slice s = (Slice)x;
			int o = s.getOffset();
			return anyToList(s.getUnderlying()).subList(o, o+s.getLength());
		}
		
		//		else if (x.getClass().isArray())
		//		{
		//			return new RichGeneralListBackedbyLiveArray(x);
		//		}
		else if (x instanceof Object[])
		{
			return Arrays.asList((Object[])x);
		}
		
		//Old way, before Primitive Collections! :D
		//		else if (x.getClass().isArray()) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		//		{
		//			return Arrays.asList(Primitives.box(x)); //I think this is also probably faster for bulk things, at least :>   (again, given code in ArrayList constructor! :D )
		//		}
		
		/* <<<
		primxp
		else if (x instanceof _$$prim$$_[])
		{
			return _$$prim$$_ArrayAsList((_$$prim$$_[])x);
		}
		 */
		else if (x instanceof boolean[])
		{
			return booleanArrayAsList((boolean[])x);
		}
		else if (x instanceof byte[])
		{
			return byteArrayAsList((byte[])x);
		}
		else if (x instanceof char[])
		{
			return charArrayAsList((char[])x);
		}
		else if (x instanceof short[])
		{
			return shortArrayAsList((short[])x);
		}
		else if (x instanceof float[])
		{
			return floatArrayAsList((float[])x);
		}
		else if (x instanceof int[])
		{
			return intArrayAsList((int[])x);
		}
		else if (x instanceof double[])
		{
			return doubleArrayAsList((double[])x);
		}
		else if (x instanceof long[])
		{
			return longArrayAsList((long[])x);
		}
		
		//>>>
		
		
		
		
		
		else if (x instanceof Iterable) //takes care of not-optimizable Collection case
		{
			return anyToNewMutableVariablelengthList(x); //no better way I sees XD
		}
		else if (x instanceof SimpleIterable)
		{
			return anyToNewMutableVariablelengthList(x); //no better way I sees XD
		}
		else if (x instanceof Iterator)
		{
			return anyToNewMutableVariablelengthList(x); //no better way I sees XD
		}
		else if (x instanceof Enumeration)
		{
			return anyToNewMutableVariablelengthList(x); //no better way I sees XD
		}
		else if (x instanceof SimpleIterator)
		{
			return anyToNewMutableVariablelengthList(x); //no better way I sees XD
		}
		
		
		
		//Todo better versions? ^^'
		else if (x instanceof String)
			return anyToList(((String)x).toCharArray());
		else if (x instanceof CharSequence)
			return anyToList(((CharSequence)x).toString().toCharArray());
		
		
		
		else
			throw new ClassCastException(CollectionUtilities.Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static List anyToNewMutableVariablelengthList(final Object x)
	{
		if (x == null)
			throw new NullPointerException();
		
		//		else if (x.getClass().isArray())
		//		{
		//			return new RichGeneralListBackedbyLiveArray(x);
		//		}
		else if (x instanceof Object[])
		{
			return new ArrayList(Arrays.asList((Object[])x)); //I think this is faster than looping (given code in ArrayList constructor! 8> ), even with extra view object asList creates! :>   (which may be stack allocated with jit awesomeness! 8> )
		}
		//Old way, before Primitive Collections! :D
		//		else if (x.getClass().isArray()) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		//		{
		//			return new ArrayList(Arrays.asList(Primitives.box(x))); //I think this is also probably faster for bulk things, at least :>   (again, given code in ArrayList constructor! :D )
		//		}
		
		/* <<<
		primxp
		else if (x instanceof _$$prim$$_[])
		{
			return _$$prim$$_ArrayAsMutableList((_$$prim$$_[])x);
		}
		 */
		else if (x instanceof boolean[])
		{
			return booleanArrayAsMutableList((boolean[])x);
		}
		else if (x instanceof byte[])
		{
			return byteArrayAsMutableList((byte[])x);
		}
		else if (x instanceof char[])
		{
			return charArrayAsMutableList((char[])x);
		}
		else if (x instanceof short[])
		{
			return shortArrayAsMutableList((short[])x);
		}
		else if (x instanceof float[])
		{
			return floatArrayAsMutableList((float[])x);
		}
		else if (x instanceof int[])
		{
			return intArrayAsMutableList((int[])x);
		}
		else if (x instanceof double[])
		{
			return doubleArrayAsMutableList((double[])x);
		}
		else if (x instanceof long[])
		{
			return longArrayAsMutableList((long[])x);
		}
		
		//>>>
		
		
		
		
		else if (x instanceof Collection)
		{
			return new ArrayList((Collection)x); //many fast from code in new ArrayList(Collection)  :>!
		}
		else if (x instanceof Iterable)
		{
			List list = new ArrayList();
			for (Object e : (Iterable)x)
				list.add(e);
			return list;
		}
		else if (x instanceof SimpleIterable)
		{
			return anyToNewMutableVariablelengthList(((SimpleIterable)x).simpleIterator());
		}
		else if (x instanceof Iterator)
		{
			List list = new ArrayList();
			while (((Iterator)x).hasNext())
				list.add(((Iterator)x).next());
			return list;
		}
		else if (x instanceof Enumeration)
		{
			List list = new ArrayList();
			while (((Enumeration)x).hasMoreElements())
				list.add(((Enumeration)x).nextElement());
			return list;
		}
		else if (x instanceof SimpleIterator)
		{
			List list = new ArrayList();
			
			while (true)
			{
				try
				{
					list.add(((SimpleIterator)x).nextrp());
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return list;
		}
		
		else
			throw new ClassCastException(CollectionUtilities.Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static Set anyToSet(final Object x)
	{
		//Todo rich/general sets :P
		
		if (x == null)
			return null;
		
		else if (x instanceof Set)
		{
			return (Set)x;
		}
		else if (x instanceof Object[])
		{
			return anyToNewMutableSet(x); //no better way I sees XD
		}
		else if (x.getClass().isArray()) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		{
			return anyToNewMutableSet(x); //no better way I sees XD
		}
		else if (x instanceof Iterable) //takes care of not-optimizable Collection case
		{
			return anyToNewMutableSet(x); //no better way I sees XD
		}
		else if (x instanceof SimpleIterable)
		{
			return anyToNewMutableSet(x); //no better way I sees XD
		}
		else if (x instanceof Iterator)
		{
			return anyToNewMutableSet(x); //no better way I sees XD
		}
		else if (x instanceof Enumeration)
		{
			return anyToNewMutableSet(x); //no better way I sees XD
		}
		else if (x instanceof SimpleIterator)
		{
			return anyToNewMutableSet(x); //no better way I sees XD
		}
		
		else
			throw new ClassCastException(CollectionUtilities.Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static Set anyToNewMutableSet(final Object x)
	{
		return anyToNewMutableSet(x, false);
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static Set anyToNewMutableSet(final Object x, boolean throwOnDuplicates) throws AlreadyExistsException
	{
		//Todo rich/general sets :P
		
		if (x == null)
		{
			throw new NullPointerException();
		}
		else if (x instanceof Object[])
		{
			Set r = new HashSet(Arrays.asList((Object[])x)); //new HashSet(Collection) may just add each element iteratively, but it tunes the load factor and initial capacity and such, so there ya go! :>!
			if (r.size() != ((Object[])x).length)
				if (throwOnDuplicates) throw new AlreadyExistsException();
			return r;
		}
		else if (x.getClass().isArray()) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		{
			Object[] a = Primitives.box(x);
			Set r = new HashSet(Arrays.asList(a));
			if (r.size() != a.length)
				if (throwOnDuplicates) throw new AlreadyExistsException();
			return r;
		}
		else if (x instanceof Collection) //takes care of instanceof Set case :>
		{
			Set r = new HashSet((Collection)x);
			if (r.size() != ((Collection)x).size())
				if (throwOnDuplicates) throw new AlreadyExistsException();
			return r;
		}
		else if (x instanceof Iterable)
		{
			Set set = new HashSet();
			for (Object e : (Iterable)x)
			{
				if (throwOnDuplicates)
					addNewMandatory(set, e);
				else
					set.add(e);
			}
			return set;
		}
		else if (x instanceof SimpleIterable)
		{
			return anyToNewMutableSet(((SimpleIterable)x).simpleIterator(), throwOnDuplicates);
		}
		else if (x instanceof Iterator)
		{
			Set set = new HashSet();
			while (((Iterator)x).hasNext())
			{
				Object e = ((Iterator)x).next();
				if (throwOnDuplicates)
					addNewMandatory(set, e);
				else
					set.add(e);
			}
			return set;
		}
		else if (x instanceof Enumeration)
		{
			Set set = new HashSet();
			while (((Enumeration)x).hasMoreElements())
			{
				Object e = ((Enumeration)x).nextElement();
				if (throwOnDuplicates)
					addNewMandatory(set, e);
				else
					set.add(e);
			}
			return set;
		}
		else if (x instanceof SimpleIterator)
		{
			Set set = new HashSet();
			
			while (true)
			{
				Object e;
				try
				{
					e = ((SimpleIterator)x).nextrp();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				if (throwOnDuplicates)
					addNewMandatory(set, e);
				else
					set.add(e);
			}
			
			return set;
		}
		
		else
		{
			throw new ClassCastException(CollectionUtilities.Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
		}
	}
	
	@ReadonlyValue
	public static Set anyToNewSet(final Object x)
	{
		//Todo faster impls possible??
		return anyToNewMutableSet(x);
	}
	
	@LiveValue //:D!  (many fasters! :D )
	public static Iterator anyToIterator(final Object x)
	{
		if (x == null)
			return null;
		
		else if (x instanceof Object[] || (x instanceof Slice && ((Slice)x).getUnderlying() instanceof Object[]))
		{
			//return Arrays.asList((Object[])x).iterator();
			final Object[] array;
			final int offset;
			final int length;
			{
				if (x instanceof Object[])
				{
					array = (Object[]) x;
					offset = 0;
					length = Array.getLength(x);
				}
				else
				{
					Slice<Object[]> s = (Slice<Object[]>) x;
					array = s.getUnderlying();
					offset = s.getOffset();
					length = s.getLength();
				}
			}
			
			
			return new Iterator()
			{
				int cursor = 0;
				
				@Override
				public boolean hasNext()
				{
					return this.cursor < length;
				}
				
				@Override
				public Object next()
				{
					if (!hasNext())
						throw new NoSuchElementException("Has only "+length+" elements, no more! sorries! ;_;");
					
					Object r = array[offset+this.cursor];
					this.cursor++;
					return r;
				}
				
				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
		else if (x.getClass().isArray() || (x instanceof Slice && ((Slice)x).getUnderlying().getClass().isArray()))  //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		{
			final Object array;
			final int offset;
			final int length;
			{
				if (x instanceof Slice)
				{
					Slice s = (Slice) x;
					array = s.getUnderlying();
					offset = s.getOffset();
					length = s.getLength();
				}
				else
				{
					array = x;
					offset = 0;
					length = Array.getLength(x);
				}
			}
			
			return new Iterator()
			{
				int cursor = 0;
				
				@Override
				public boolean hasNext()
				{
					return this.cursor < length;
				}
				
				@Override
				public Object next()
				{
					if (!hasNext())
						throw new NoSuchElementException("Has only "+length+" elements, no more! sorries! ;_;");
					
					Object r = Primitives.getBoxing(array, offset+this.cursor);
					this.cursor++;
					return r;
				}
				
				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
		else if (x instanceof Iterable)
		{
			return ((Iterable)x).iterator();
		}
		else if (x instanceof SimpleIterable)
		{
			return anyToIterator(((SimpleIterable)x).simpleIterator());
		}
		
		else if (x instanceof NodeList)
		{
			return anyToIterator(anyToSimpleIterator(x));
		}
		
		else if (x instanceof Iterator)
		{
			return (Iterator)x;
		}
		else if (x instanceof Enumeration)
		{
			return new Iterator()
			{
				@Override
				public boolean hasNext()
				{
					return ((Enumeration)x).hasMoreElements();
				}
				
				@Override
				public Object next()
				{
					return ((Enumeration)x).nextElement();
				}
				
				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
		else if (x instanceof SimpleIterator)
		{
			return new Iterator()
			{
				boolean hasBuffer = false;
				Object buffer = null;
				
				@Override
				public boolean hasNext()
				{
					try
					{
						this.buffer = ((SimpleIterator)x).nextrp();
						this.hasBuffer = true;
						return true;
					}
					catch (StopIterationReturnPath exc)
					{
						return false;
					}
				}
				
				@Override
				public Object next()
				{
					if (this.hasBuffer)
					{
						this.hasBuffer = false;
						return this.buffer;
					}
					else
					{
						try
						{
							return ((SimpleIterator)x).nextrp();
						}
						catch (StopIterationReturnPath exc)
						{
							//They should have called hasNext() first, which would have buffered an element; so we do what normal java.util.Iterators do! ;>
							throw new NoSuchElementException();
						}
					}
				}
				
				@Override
				public void remove()
				{
					if (x instanceof SimpleIteratorWithRemove)
						((SimpleIteratorWithRemove)x).remove();
					else
						throw new UnsupportedOperationException();
				}
			};
		}
		
		else
			throw new ClassCastException(CollectionUtilities.Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
	}
	
	@LiveValue //:D!  (many fasters! :D )
	public static Enumeration anyToEnumerator(final Object x)
	{
		if (x == null)
			return null;
		
		//Not as speedily implemented because java.util.Enumeration is an antiquated interface xP
		
		else if (x instanceof Enumeration)
		{
			return (Enumeration)x;
		}
		else
		{
			final Iterator i = anyToIterator(x);
			
			return new Enumeration()
			{
				@Override
				public boolean hasMoreElements()
				{
					return i.hasNext();
				}
				
				@Override
				public Object nextElement()
				{
					return i.next();
				}
			};
		}
	}
	
	@LiveValue //:D!  (many fasters! :D )
	public static SimpleIterator anyToSimpleIterator(final Object x)
	{
		if (x == null)
			return null;
		
		else if (x instanceof SimpleIterator)
		{
			return (SimpleIterator)x;
		}
		else if (x instanceof SimpleIterable)
		{
			return ((SimpleIterable)x).simpleIterator();
		}
		else if (x instanceof Object[])
		{
			return new SimpleIterator()
			{
				int cursor = 0;
				
				@Override
				public Object nextrp() throws StopIterationReturnPath
				{
					if (this.cursor >= ((Object[])x).length)
						throw StopIterationReturnPath.I;
					
					Object r = ((Object[])x)[this.cursor];
					this.cursor++;
					return r;
				}
			};
		}
		else if (x.getClass().isArray()) //&& !(x instanceof Object[])  ==> (implies)   x is a primitive array! :D
		{
			//			return new RichGeneralListBackedbyLiveArray(x).simpleIterator();  //produces a general iterator with direct non-boxing/unboxing methods! ^_^
			
			final int l = Array.getLength(x);
			
			return new SimpleIterator()
			{
				int cursor = 0;
				
				@Override
				public Object nextrp() throws StopIterationReturnPath
				{
					if (this.cursor >= l)
						throw StopIterationReturnPath.I;
					
					Object r = Primitives.getBoxing(x, this.cursor);
					this.cursor++;
					return r;
				}
			};
		}
		
		else if (x instanceof NodeList)
		{
			NodeList l = (NodeList) x;
			
			return new SimpleIterator()
			{
				int i = 0;
				
				@Override
				public Object nextrp() throws StopIterationReturnPath
				{
					if (this.i >= l.getLength())
						throw StopIterationReturnPath.I;
					
					return l.item(this.i);
				}
			};
		}
		
		else if (x instanceof Iterable || x instanceof Iterator)
		{
			final Iterator i;
			
			if (x instanceof Iterable)
				i = ((Iterable)x).iterator();
			else
				i = (Iterator)x;
			
			return new SimpleIterator()
			{
				@Override
				public Object nextrp() throws StopIterationReturnPath
				{
					if (!i.hasNext())
						throw StopIterationReturnPath.I;
					else
						return i.next();
				}
			};
		}
		else if (x instanceof Enumeration)
		{
			return new SimpleIterator()
			{
				@Override
				public Object nextrp() throws StopIterationReturnPath
				{
					if (!((Enumeration)x).hasMoreElements())
						throw StopIterationReturnPath.I;
					else
						return ((Enumeration)x).nextElement();
				}
			};
		}
		
		else
			throw new ClassCastException(CollectionUtilities.Converters_ClassCastException_Message_Prefix+x.getClass().getName()+"'s   sorries ._.");
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <T> T[] anyToArrayTyped(final Object x, final Class<T> componentType)
	{
		return (T[])anyToArray(x, componentType);
	}
	
	@ThrowAwayValue
	public static <T> T[] anyToNewArrayTyped(final Object x, final Class<T> componentType)
	{
		return (T[])anyToNewArray(x, componentType);
	}
	
	
	
	
	
	/* <<<
	primxp
	
	@PossiblySnapshotPossiblyLiveValue
	public static _$$prim$$_[] anyToArray_$$Prim$$_(final Object x)
	{
		return (_$$prim$$_[])anyToArray(x, _$$prim$$_.class);
	}
	
	@ThrowAwayValue
	public static _$$prim$$_[] anyToNewArray_$$Prim$$_(final Object x)
	{
		return (_$$prim$$_[])anyToNewArray(x, _$$prim$$_.class);
	}
	
	
	
	 */
	
	@PossiblySnapshotPossiblyLiveValue
	public static boolean[] anyToArrayBoolean(final Object x)
	{
		return (boolean[])anyToArray(x, boolean.class);
	}
	
	@ThrowAwayValue
	public static boolean[] anyToNewArrayBoolean(final Object x)
	{
		return (boolean[])anyToNewArray(x, boolean.class);
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static byte[] anyToArrayByte(final Object x)
	{
		return (byte[])anyToArray(x, byte.class);
	}
	
	@ThrowAwayValue
	public static byte[] anyToNewArrayByte(final Object x)
	{
		return (byte[])anyToNewArray(x, byte.class);
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static char[] anyToArrayChar(final Object x)
	{
		return (char[])anyToArray(x, char.class);
	}
	
	@ThrowAwayValue
	public static char[] anyToNewArrayChar(final Object x)
	{
		return (char[])anyToNewArray(x, char.class);
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static short[] anyToArrayShort(final Object x)
	{
		return (short[])anyToArray(x, short.class);
	}
	
	@ThrowAwayValue
	public static short[] anyToNewArrayShort(final Object x)
	{
		return (short[])anyToNewArray(x, short.class);
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static float[] anyToArrayFloat(final Object x)
	{
		return (float[])anyToArray(x, float.class);
	}
	
	@ThrowAwayValue
	public static float[] anyToNewArrayFloat(final Object x)
	{
		return (float[])anyToNewArray(x, float.class);
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static int[] anyToArrayInt(final Object x)
	{
		return (int[])anyToArray(x, int.class);
	}
	
	@ThrowAwayValue
	public static int[] anyToNewArrayInt(final Object x)
	{
		return (int[])anyToNewArray(x, int.class);
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static double[] anyToArrayDouble(final Object x)
	{
		return (double[])anyToArray(x, double.class);
	}
	
	@ThrowAwayValue
	public static double[] anyToNewArrayDouble(final Object x)
	{
		return (double[])anyToNewArray(x, double.class);
	}
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static long[] anyToArrayLong(final Object x)
	{
		return (long[])anyToArray(x, long.class);
	}
	
	@ThrowAwayValue
	public static long[] anyToNewArrayLong(final Object x)
	{
		return (long[])anyToNewArray(x, long.class);
	}
	
	
	
	
	//>>>
	
	
	
	
	
	
	
	
	
	
	@LiveValue //:D!  (many fasters! :D )
	public static Iterable anyToSingleUseIterable(final Object x)
	{
		return CollectionUtilities.singleUseIterable(anyToIterator(x));
	}
	
	/**
	 * Checks that it is a set--meaning it has no duplicates ^_^
	 */
	public static boolean checkSet(Object x)
	{
		return anyToSet(x).size() == BasicCollectionUtilities.sizeOfCollectionlike(x);
	}
	
	public static boolean isAll(Object listOfBooleans)
	{
		if (listOfBooleans instanceof boolean[])
		{
			for (boolean e : (boolean[])listOfBooleans)
				if (!e)
					return false;
			return true;
		}
		else
		{
			for (Boolean e : (Iterable<Boolean>)anyToSingleUseIterable(listOfBooleans))
				if (!e)
					return false;
			return true;
		}
	}
	
	public static boolean isAny(Object listOfBooleans)
	{
		if (listOfBooleans instanceof boolean[])
		{
			for (boolean e : (boolean[])listOfBooleans)
				if (e)
					return true;
			return false;
		}
		else
		{
			for (Boolean e : (Iterable<Boolean>)anyToSingleUseIterable(listOfBooleans))
				if (e)
					return true;
			return false;
		}
	}
	
	public static Object findFirstRP(Predicate predicate, @CollectionValue Object list) throws NoSuchElementReturnPath
	{
		if (list instanceof Object[])
		{
			return CollectionUtilities.findFirstRP(predicate, (Object[])list);
		}
		else if (list instanceof List)
		{
			return CollectionUtilities.findFirstRP(predicate, (List)list);
		}
		else
		{
			Iterator it = anyToIterator(list);
			while (it.hasNext())
			{
				Object element = it.next();
				if (predicate.test(element))
					return element;
			}
			throw NoSuchElementReturnPath.I;
		}
	}
	
	public static void addAll(Collection self, Object source)
	{
		if (source instanceof Iterable)
			CollectionUtilities.addAll(self, (Iterable)source);
		else if (source instanceof Object[])
			CollectionUtilities.addAll(self, (Object[])source);
		else
			//SPEED basically inline it XD'
			CollectionUtilities.addAll(self, anyToSingleUseIterable(source));
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue //we may check if it's already uniqued and pass it through, unmodified in the future; who knows! :>
	public static List anyToListUniqueifying(Object x)
	{
		return anyToList(anyToSet(x));
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue //we may check if it's already uniqued and pass it through, unmodified in the future; who knows! :>
	public static <E> E[] anyToArrayUniqueifying(Object x, Class<E> componentType)
	{
		return (E[])anyToArray(anyToSet(x), componentType);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue //we may check if it's already uniqued and pass it through, unmodified in the future; who knows! :>
	public static Object[] anyToObjectArrayUniqueifying(Object x)
	{
		return anyToObjectArray(anyToSet(x));
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static List mergeLists(@ReadonlyValue Object... listthings)
	{
		if (listthings.length == 0)
			return Collections.emptyList();
		else if (listthings.length == 1)
			return anyToList(listthings[0]);
		else
			return mergeListsToNew(listthings); //no better way I sees XD
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static List mergeListsToNew(@ReadonlyValue Object... listthings)
	{
		List merged = new ArrayList();
		
		for (Object listthing : listthings)
			addAll(merged, listthing);
		
		return merged;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static Set mergeSets(@ReadonlyValue Object... setthings)
	{
		if (setthings.length == 0)
			return Collections.emptySet();
		else if (setthings.length == 1)
			return anyToSet(setthings[0]);
		else
			return mergeSetsToNew(setthings); //no better way I sees XD
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static Set mergeSetsToNew(@ReadonlyValue Object... setthings)
	{
		Set merged = new HashSet();
		
		for (Object listthing : setthings)
			addAll(merged, listthing);
		
		return merged;
	}
	
	@LiveValue
	public static Object anyToUnmodifiableList(@CollectionValue Object list)
	{
		if (isFalseAndNotNull(isWritableCollection(list)))
			//THEN DON'T DECORATE IT \o/  :D!
			return list;
		
		else
		{
			/* <<<
			primxp
				if (list instanceof _$$Primitive$$_List)
					return PrimitiveCollections.unmodifiable_$$Primitive$$_List((_$$Primitive$$_List)list);
			 */
			if (list instanceof BooleanList)
				return PrimitiveCollections.unmodifiableBooleanList((BooleanList)list);
			if (list instanceof ByteList)
				return PrimitiveCollections.unmodifiableByteList((ByteList)list);
			if (list instanceof CharacterList)
				return PrimitiveCollections.unmodifiableCharacterList((CharacterList)list);
			if (list instanceof ShortList)
				return PrimitiveCollections.unmodifiableShortList((ShortList)list);
			if (list instanceof FloatList)
				return PrimitiveCollections.unmodifiableFloatList((FloatList)list);
			if (list instanceof IntegerList)
				return PrimitiveCollections.unmodifiableIntegerList((IntegerList)list);
			if (list instanceof DoubleList)
				return PrimitiveCollections.unmodifiableDoubleList((DoubleList)list);
			if (list instanceof LongList)
				return PrimitiveCollections.unmodifiableLongList((LongList)list);
			
			// >>>
			
			
			
			//Else decorate it! :D
			
			//			if (list instanceof RichGeneralList)
			//				return new RichGeneralListAsUnmodifiable((RichGeneralList)list);
			if (list instanceof List)
				return Collections.unmodifiableList((List)list);
			else if (list.getClass().isArray())
				//				return new RichGeneralListBackedbyLiveArray(list, true);
				return anyToUnmodifiableList(anyToList(list));
			else
				throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(list);
		}
	}
	
	/**
	 * Note that {@link #isReadableCollection(Object)} and {@link PolymorphicCollectionUtilities#isWritableCollection(Object)} are completely orthogonal properties ^w^
	 * 
	 * + Usually in the JRE, it's safe to assume unknown-readability is yes-readable, but technically not always!
	 */
	@Nullable
	public static Boolean isReadableCollection(@Nonnull Object collectionThing)
	{
		if (collectionThing == null)
			throw new NullPointerException();
		
		//		else if (collectionThing instanceof StaticallyReadableCollection)
		//			return true;
		//		else if (collectionThing instanceof StaticallyUnreadableCollection)
		//			return false;
		else if (collectionThing instanceof RuntimeReadabilityCollection)
			return ((RuntimeReadabilityCollection)collectionThing).isReadableCollection();
		else
		{
			if (collectionThing.getClass().isArray())
				return true;
			
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableCollection)
				return true;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableList)
				return true;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableSet)
				return true;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableSortedSet)
				return true;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Arrays_asList)
				return true;
			
			else if (collectionThing.getClass() == ArrayList.class)
				return true;
			else if (collectionThing.getClass() == HashSet.class)
				return true;
			else if (collectionThing.getClass() == LinkedList.class)
				return true;
			else if (collectionThing.getClass() == Vector.class)
				return true;
			else if (collectionThing.getClass() == Stack.class)
				return true;
			else if (collectionThing.getClass() == PriorityQueue.class)
				return true;
			
			//TODO moreeeee grandfatheringggggg! :>
			
			return null;
		}
	}
	
	/**
	 * Note that {@link #isReadableCollection(Object)} and {@link #isWritableCollection(Object)} are generally completely orthogonal/independent properties ^w^
	 */
	@Nullable
	public static Boolean isWritableCollection(@Nonnull Object collectionThing)
	{
		if (collectionThing == null)
			throw new NullPointerException();
		
		//		else if (collectionThing instanceof StaticallyWriteableCollection)
		//			return true;
		//		else if (collectionThing instanceof StaticallyUnwriteableCollection)
		//			return false;
		else if (collectionThing instanceof RuntimeWriteabilityCollection)
			return ((RuntimeWriteabilityCollection)collectionThing).isWritableCollection();
		
		
		else
		{
			if (collectionThing.getClass().isArray())
				return true;
			
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableCollection)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableList)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableSet)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableSortedSet)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_emptyList)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_emptySet)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_singletonList)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_singletonSet)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Arrays_asList)
				return true;
			
			else if (collectionThing.getClass() == ArrayList.class)
				return true;
			else if (collectionThing.getClass() == HashSet.class)
				return true;
			else if (collectionThing.getClass() == LinkedList.class)
				return true;
			else if (collectionThing.getClass() == Vector.class)
				return true;
			else if (collectionThing.getClass() == Stack.class)
				return true;
			else if (collectionThing.getClass() == PriorityQueue.class)
				return true;
			
			//TODO moreeeee grandfatheringggggg! :>
			
			return null;
		}
	}
	
	public static void ensureWritableCollection(@Nonnull Object collectionThing)
	{
		Boolean b = isWritableCollection(collectionThing);
		
		if (b == null)
			throw new ReadonlyPossiblyUnsupportedOperationException();
		if (!b)
			throw new ReadonlyUnsupportedOperationException();
	}
	
	
	
	/**
	 * If this is true, then {@link #isWritableCollection(Object)} must be true as well :3
	 * If {@link #isWritableCollection(Object)} is false, this must be false as well :3
	 */
	@Nullable
	public static Boolean isCollectionVariableSize(@Nonnull Object collectionThing)
	{
		if (collectionThing == null)
			throw new NullPointerException();
		
		//Todo a trait predicate for this too like there is for writability! :>
		//		else if (collectionThing instanceof RuntimeWriteabilityCollection)
		//			return ((RuntimeWriteabilityCollection)collectionThing).isWritableCollection();
		//		else
		{
			if (collectionThing.getClass().isArray())
				return false;  //writable but fixed-length!
			
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableCollection)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableList)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableSet)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_unmodifiableSortedSet)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_emptyList)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_emptySet)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_singletonList)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Collections_singletonSet)
				return false;
			else if (collectionThing.getClass() == BetterJREGlassbox.Type_Arrays_asList)
				return false;  //writable but fixed-length!
			
			else if (collectionThing.getClass() == ArrayList.class)
				return true;
			else if (collectionThing.getClass() == HashSet.class)
				return true;
			else if (collectionThing.getClass() == LinkedList.class)
				return true;
			else if (collectionThing.getClass() == Vector.class)
				return true;
			else if (collectionThing.getClass() == Stack.class)
				return true;
			else if (collectionThing.getClass() == PriorityQueue.class)
				return true;
			
			//TODO moreeeee grandfatheringggggg! :>
			
			if (isFalseAndNotNull(isWritableCollection(collectionThing)))
				return false;
			
			return null;
		}
	}
	
	public static List toListMultidimensional(Object mdArray, int dimensions)
	{
		if (dimensions < 1)
			throw new IllegalArgumentException();
		else if (dimensions == 1)
			return anyToList(mdArray);
		else
		{
			List mdList = new ArrayList<>();
			
			for (Object o : anyToSingleUseIterable(mdArray))
			{
				mdList.add(toListMultidimensional(o, dimensions-1));
			}
			
			return mdList;
		}
	}
	
	public static <E> List<List<E>> toList2D(Object array2d)
	{
		return toListMultidimensional(array2d, 2);
	}
	
	public static boolean any(Predicate predicate, @CollectionValue Object list)
	{
		for (Object e : anyToSingleUseIterable(list))
			if (predicate.test(e))
				return true;
		return false;
	}
	
	public static boolean all(Predicate predicate, @CollectionValue Object list)
	{
		for (Object e : anyToSingleUseIterable(list))
			if (!predicate.test(e))
				return false;
		return true;
	}
	
	public static <E> void check(UnaryFunction<E, ? extends RuntimeException> thrower, @CollectionValue Object list)
	{
		Iterator<E> it = anyToIterator(list);
		
		while (it.hasNext())
		{
			E element = it.next();
			RuntimeException exc = thrower.f(element);
			if (exc != null)
				throw exc;
		}
	}
	
	public static boolean acyclicDeepEqv(Object a, Object b)
	{
		if (a instanceof List && b instanceof List)
			return CollectionUtilities.acyclicDeepEqvLists((List)a, (List)b);
		else if (a instanceof Map && b instanceof Map)
			return CollectionUtilities.acyclicDeepEqvMaps((Map)a, (Map)b);
		else
			return BasicObjectUtilities.eq(a, b);
	}
	
	public static final EqualityComparator<Object> acyclicDeepEqv = PolymorphicCollectionUtilities::acyclicDeepEqv;
	
	/**
	 * @return true = definitely fixed length, false = definitely variable length, null = unknown!  :>
	 */
	public static Boolean isFixedLengthNotVariableLength(Object x)
	{
		if (x instanceof KnowsLengthFixedness)
		{
			return ((KnowsLengthFixedness) x).isFixedLengthNotVariableLength();
		}
		
		//Don't use instanceof because subclasses might change whether it's variable-length or fixed!
		else if (x.getClass() == BetterJREGlassbox.Type_Arrays_asList)
		{
			return true;
		}
		else if (x.getClass() == ArrayList.class)
		{
			return false;
		}
		else if (x.getClass() == Vector.class)
		{
			return false;
		}
		else if (x.getClass() == LinkedList.class)
		{
			return false;
		}
		else if (x.getClass() == ArrayDeque.class)
		{
			return false;
		}
		else if (x.getClass() == HashSet.class)
		{
			return false;
		}
		else if (x.getClass() == HashMap.class)
		{
			return false;
		}
		else if (x.getClass() == TreeSet.class)
		{
			return false;
		}
		else if (x.getClass() == TreeMap.class)
		{
			return false;
		}
		//TODO More??
		
		else if (x.getClass() == ArrayBlockingQueue.class)
		{
			return true;
		}
		//TODO More??
		
		else
		{
			return null;
		}
	}
}
