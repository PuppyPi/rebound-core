/*
 * Created on Dec 9, 2008
 * 	by the great Eclipse(c)
 */
package rebound.util.collections;

import java.util.Comparator;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.CharacterList;
import rebound.util.collections.prim.PrimitiveCollections.DoubleList;
import rebound.util.collections.prim.PrimitiveCollections.FloatList;
import rebound.util.collections.prim.PrimitiveCollections.IntegerList;
import rebound.util.collections.prim.PrimitiveCollections.LongList;
import rebound.util.collections.prim.PrimitiveCollections.ShortList;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToObject;
import rebound.util.objectutil.JavaNamespace;

public class SortingUtilities
implements JavaNamespace
{
	//Objects! :D
	public static <E> int findIndexForValueInSortedSet(E[] sortedSet, E value, Comparator<E> comparator)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.length, value, comparator);
	}
	
	public static <E> int findIndexForValueInSortedSet(E[] sortedSet, int lowBound, int highBound, E value, Comparator<E> comparator)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value, comparator);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	
	
	public static <E> int findInsertionPointInSet(E[] sortedSet, E value, Comparator<E> comparator)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.length, value, comparator);
	}
	
	public static <E> int findInsertionPointInSet(E[] sortedSet, int arrayLowBound, int arrayHighBound, E value, Comparator<E> comparator)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value, comparator);
	}
	
	public static <E> int findInsertionPointInSet_Jumping(E[] sortedSet, int arrayLowBound, int arrayHighBound, E value, Comparator<E> comparator)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (comparator.compare(sortedSet[start], value) == 0)
					return ~start;
				else if (comparator.compare(value, sortedSet[start]) < 0)
				{
					assert start == 0 || comparator.compare(value, sortedSet[start-1]) > 0;
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (comparator.compare(value, sortedSet[midpoint]) == 0)
				{
					return ~midpoint;
				}
				else if (comparator.compare(value, sortedSet[midpoint]) < 0)
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	public static <E> int findInsertionPointInSet_Naive(E[] sortedSet, int arrayLowBound, int arrayHighBound, E value, Comparator<E> comparator)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		E curr = null;
		while (i < arrayHighBound)
		{
			curr = sortedSet[i];
			if (comparator.compare(curr, value) == 0)
				return ~i;
			if (comparator.compare(curr, value) > 0)
				break;
			i++;
		}
		
		return i;
	}
	
	
	
	
	
	
	
	
	
	//Objects! :D
	public static <E> int findIndexForValueInSortedSet(UnaryFunctionIntToObject<E> sortedSet, int sortedSetSize, E value, Comparator<E> comparator)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSetSize, value, comparator);
	}
	
	public static <E> int findIndexForValueInSortedSet(UnaryFunctionIntToObject<E> sortedSet, int lowBound, int highBound, E value, Comparator<E> comparator)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value, comparator);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	
	
	public static <E> int findInsertionPointInSet(UnaryFunctionIntToObject<E> sortedSet, int sortedSetSize, E value, Comparator<E> comparator)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSetSize, value, comparator);
	}
	
	public static <E> int findInsertionPointInSet(UnaryFunctionIntToObject<E> sortedSet, int arrayLowBound, int arrayHighBound, E value, Comparator<E> comparator)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value, comparator);
	}
	
	public static <E> int findInsertionPointInSet_Jumping(UnaryFunctionIntToObject<E> sortedSet, int arrayLowBound, int arrayHighBound, E value, Comparator<E> comparator)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (comparator.compare(sortedSet.f(start), value) == 0)
					return ~start;
				else if (comparator.compare(value, sortedSet.f(start)) < 0)
				{
					assert start == 0 || comparator.compare(value, sortedSet.f(start-1)) > 0;
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (comparator.compare(value, sortedSet.f(midpoint)) == 0)
				{
					return ~midpoint;
				}
				else if (comparator.compare(value, sortedSet.f(midpoint)) < 0)
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	public static <E> int findInsertionPointInSet_Naive(UnaryFunctionIntToObject<E> sortedSet, int arrayLowBound, int arrayHighBound, E value, Comparator<E> comparator)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		E curr = null;
		while (i < arrayHighBound)
		{
			curr = sortedSet.f(i);
			if (comparator.compare(curr, value) == 0)
				return ~i;
			if (comparator.compare(curr, value) > 0)
				break;
			i++;
		}
		
		return i;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:noboolean$$_
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 ⎋a/
	public static int findIndexForValueInSortedSet(_$$prim$$_[] sortedSet, _$$prim$$_ value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * aka indexOf()
	 ⎋a/
	public static int findIndexForValueInSortedSet(_$$prim$$_[] sortedSet, int lowBound, int highBound, _$$prim$$_ value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(_$$prim$$_[], int, int, _$$prim$$_)}.
	 ⎋a/
	public static int findInsertionPointInSet(_$$prim$$_[] sortedSet, _$$prim$$_ value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is a unique-set) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.length</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 ⎋a/
	public static int findInsertionPointInSet(_$$prim$$_[] sortedSet, int arrayLowBound, int arrayHighBound, _$$prim$$_ value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 ⎋a/
	public static int findInsertionPointInSet_Jumping(_$$prim$$_[] sortedSet, int arrayLowBound, int arrayHighBound, _$$prim$$_ value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet[start] == value)
					return ~start;
				else if (value < sortedSet[start])
				{
					assert start == 0 || value > sortedSet[start-1];
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet[midpoint])
				{
					return ~midpoint;
				}
				else if (value < sortedSet[midpoint])
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 ⎋a/
	public static int findInsertionPointInSet_Naive(_$$prim$$_[] sortedSet, int arrayLowBound, int arrayHighBound, _$$prim$$_ value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		_$$prim$$_ curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet[i];
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 ⎋a/
	public static int findIndexForValueInSortedSet(_$$Primitive$$_List sortedSet, _$$prim$$_ value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * aka indexOf()
	 ⎋a/
	public static int findIndexForValueInSortedSet(_$$Primitive$$_List sortedSet, int lowBound, int highBound, _$$prim$$_ value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(_$$Primitive$$_List, int, int, _$$prim$$_)}.
	 ⎋a/
	public static int findInsertionPointInSet(_$$Primitive$$_List sortedSet, _$$prim$$_ value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is currently a unique-set contentwise) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.size()</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 ⎋a/
	public static int findInsertionPointInSet(_$$Primitive$$_List sortedSet, int arrayLowBound, int arrayHighBound, _$$prim$$_ value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 ⎋a/
	public static int findInsertionPointInSet_Jumping(_$$Primitive$$_List sortedSet, int arrayLowBound, int arrayHighBound, _$$prim$$_ value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet.get(start) == value)
					return ~start;
				else if (value < sortedSet.get(start))
				{
					assert start == 0 || value > sortedSet.get(start-1);
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet.get(midpoint))
				{
					return ~midpoint;
				}
				else if (value < sortedSet.get(midpoint))
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 ⎋a/
	public static int findInsertionPointInSet_Naive(_$$Primitive$$_List sortedSet, int arrayLowBound, int arrayHighBound, _$$prim$$_ value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		_$$prim$$_ curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet.get(i);
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	 */
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(byte[] sortedSet, byte value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(byte[] sortedSet, int lowBound, int highBound, byte value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(byte[], int, int, byte)}.
	 */
	public static int findInsertionPointInSet(byte[] sortedSet, byte value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is a unique-set) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.length</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(byte[] sortedSet, int arrayLowBound, int arrayHighBound, byte value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(byte[] sortedSet, int arrayLowBound, int arrayHighBound, byte value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet[start] == value)
					return ~start;
				else if (value < sortedSet[start])
				{
					assert start == 0 || value > sortedSet[start-1];
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet[midpoint])
				{
					return ~midpoint;
				}
				else if (value < sortedSet[midpoint])
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(byte[] sortedSet, int arrayLowBound, int arrayHighBound, byte value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		byte curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet[i];
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(ByteList sortedSet, byte value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(ByteList sortedSet, int lowBound, int highBound, byte value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(ByteList, int, int, byte)}.
	 */
	public static int findInsertionPointInSet(ByteList sortedSet, byte value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is currently a unique-set contentwise) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.size()</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(ByteList sortedSet, int arrayLowBound, int arrayHighBound, byte value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(ByteList sortedSet, int arrayLowBound, int arrayHighBound, byte value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet.get(start) == value)
					return ~start;
				else if (value < sortedSet.get(start))
				{
					assert start == 0 || value > sortedSet.get(start-1);
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet.get(midpoint))
				{
					return ~midpoint;
				}
				else if (value < sortedSet.get(midpoint))
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(ByteList sortedSet, int arrayLowBound, int arrayHighBound, byte value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		byte curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet.get(i);
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(char[] sortedSet, char value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(char[] sortedSet, int lowBound, int highBound, char value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(char[], int, int, char)}.
	 */
	public static int findInsertionPointInSet(char[] sortedSet, char value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is a unique-set) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.length</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(char[] sortedSet, int arrayLowBound, int arrayHighBound, char value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(char[] sortedSet, int arrayLowBound, int arrayHighBound, char value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet[start] == value)
					return ~start;
				else if (value < sortedSet[start])
				{
					assert start == 0 || value > sortedSet[start-1];
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet[midpoint])
				{
					return ~midpoint;
				}
				else if (value < sortedSet[midpoint])
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(char[] sortedSet, int arrayLowBound, int arrayHighBound, char value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		char curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet[i];
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(CharacterList sortedSet, char value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(CharacterList sortedSet, int lowBound, int highBound, char value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(CharacterList, int, int, char)}.
	 */
	public static int findInsertionPointInSet(CharacterList sortedSet, char value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is currently a unique-set contentwise) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.size()</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(CharacterList sortedSet, int arrayLowBound, int arrayHighBound, char value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(CharacterList sortedSet, int arrayLowBound, int arrayHighBound, char value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet.get(start) == value)
					return ~start;
				else if (value < sortedSet.get(start))
				{
					assert start == 0 || value > sortedSet.get(start-1);
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet.get(midpoint))
				{
					return ~midpoint;
				}
				else if (value < sortedSet.get(midpoint))
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(CharacterList sortedSet, int arrayLowBound, int arrayHighBound, char value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		char curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet.get(i);
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(short[] sortedSet, short value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(short[] sortedSet, int lowBound, int highBound, short value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(short[], int, int, short)}.
	 */
	public static int findInsertionPointInSet(short[] sortedSet, short value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is a unique-set) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.length</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(short[] sortedSet, int arrayLowBound, int arrayHighBound, short value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(short[] sortedSet, int arrayLowBound, int arrayHighBound, short value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet[start] == value)
					return ~start;
				else if (value < sortedSet[start])
				{
					assert start == 0 || value > sortedSet[start-1];
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet[midpoint])
				{
					return ~midpoint;
				}
				else if (value < sortedSet[midpoint])
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(short[] sortedSet, int arrayLowBound, int arrayHighBound, short value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		short curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet[i];
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(ShortList sortedSet, short value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(ShortList sortedSet, int lowBound, int highBound, short value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(ShortList, int, int, short)}.
	 */
	public static int findInsertionPointInSet(ShortList sortedSet, short value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is currently a unique-set contentwise) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.size()</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(ShortList sortedSet, int arrayLowBound, int arrayHighBound, short value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(ShortList sortedSet, int arrayLowBound, int arrayHighBound, short value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet.get(start) == value)
					return ~start;
				else if (value < sortedSet.get(start))
				{
					assert start == 0 || value > sortedSet.get(start-1);
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet.get(midpoint))
				{
					return ~midpoint;
				}
				else if (value < sortedSet.get(midpoint))
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(ShortList sortedSet, int arrayLowBound, int arrayHighBound, short value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		short curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet.get(i);
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(float[] sortedSet, float value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(float[] sortedSet, int lowBound, int highBound, float value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(float[], int, int, float)}.
	 */
	public static int findInsertionPointInSet(float[] sortedSet, float value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is a unique-set) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.length</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(float[] sortedSet, int arrayLowBound, int arrayHighBound, float value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(float[] sortedSet, int arrayLowBound, int arrayHighBound, float value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet[start] == value)
					return ~start;
				else if (value < sortedSet[start])
				{
					assert start == 0 || value > sortedSet[start-1];
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet[midpoint])
				{
					return ~midpoint;
				}
				else if (value < sortedSet[midpoint])
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(float[] sortedSet, int arrayLowBound, int arrayHighBound, float value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		float curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet[i];
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(FloatList sortedSet, float value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(FloatList sortedSet, int lowBound, int highBound, float value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(FloatList, int, int, float)}.
	 */
	public static int findInsertionPointInSet(FloatList sortedSet, float value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is currently a unique-set contentwise) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.size()</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(FloatList sortedSet, int arrayLowBound, int arrayHighBound, float value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(FloatList sortedSet, int arrayLowBound, int arrayHighBound, float value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet.get(start) == value)
					return ~start;
				else if (value < sortedSet.get(start))
				{
					assert start == 0 || value > sortedSet.get(start-1);
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet.get(midpoint))
				{
					return ~midpoint;
				}
				else if (value < sortedSet.get(midpoint))
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(FloatList sortedSet, int arrayLowBound, int arrayHighBound, float value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		float curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet.get(i);
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(int[] sortedSet, int value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(int[] sortedSet, int lowBound, int highBound, int value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(int[], int, int, int)}.
	 */
	public static int findInsertionPointInSet(int[] sortedSet, int value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is a unique-set) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.length</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(int[] sortedSet, int arrayLowBound, int arrayHighBound, int value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(int[] sortedSet, int arrayLowBound, int arrayHighBound, int value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet[start] == value)
					return ~start;
				else if (value < sortedSet[start])
				{
					assert start == 0 || value > sortedSet[start-1];
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet[midpoint])
				{
					return ~midpoint;
				}
				else if (value < sortedSet[midpoint])
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(int[] sortedSet, int arrayLowBound, int arrayHighBound, int value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		int curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet[i];
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(IntegerList sortedSet, int value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(IntegerList sortedSet, int lowBound, int highBound, int value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(IntegerList, int, int, int)}.
	 */
	public static int findInsertionPointInSet(IntegerList sortedSet, int value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is currently a unique-set contentwise) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.size()</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(IntegerList sortedSet, int arrayLowBound, int arrayHighBound, int value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(IntegerList sortedSet, int arrayLowBound, int arrayHighBound, int value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet.get(start) == value)
					return ~start;
				else if (value < sortedSet.get(start))
				{
					assert start == 0 || value > sortedSet.get(start-1);
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet.get(midpoint))
				{
					return ~midpoint;
				}
				else if (value < sortedSet.get(midpoint))
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(IntegerList sortedSet, int arrayLowBound, int arrayHighBound, int value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		int curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet.get(i);
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(double[] sortedSet, double value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(double[] sortedSet, int lowBound, int highBound, double value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(double[], int, int, double)}.
	 */
	public static int findInsertionPointInSet(double[] sortedSet, double value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is a unique-set) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.length</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(double[] sortedSet, int arrayLowBound, int arrayHighBound, double value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(double[] sortedSet, int arrayLowBound, int arrayHighBound, double value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet[start] == value)
					return ~start;
				else if (value < sortedSet[start])
				{
					assert start == 0 || value > sortedSet[start-1];
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet[midpoint])
				{
					return ~midpoint;
				}
				else if (value < sortedSet[midpoint])
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(double[] sortedSet, int arrayLowBound, int arrayHighBound, double value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		double curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet[i];
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(DoubleList sortedSet, double value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(DoubleList sortedSet, int lowBound, int highBound, double value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(DoubleList, int, int, double)}.
	 */
	public static int findInsertionPointInSet(DoubleList sortedSet, double value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is currently a unique-set contentwise) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.size()</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(DoubleList sortedSet, int arrayLowBound, int arrayHighBound, double value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(DoubleList sortedSet, int arrayLowBound, int arrayHighBound, double value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet.get(start) == value)
					return ~start;
				else if (value < sortedSet.get(start))
				{
					assert start == 0 || value > sortedSet.get(start-1);
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet.get(midpoint))
				{
					return ~midpoint;
				}
				else if (value < sortedSet.get(midpoint))
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(DoubleList sortedSet, int arrayLowBound, int arrayHighBound, double value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		double curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet.get(i);
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(long[] sortedSet, long value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(long[] sortedSet, int lowBound, int highBound, long value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(long[], int, int, long)}.
	 */
	public static int findInsertionPointInSet(long[] sortedSet, long value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.length, value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is a unique-set) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.length</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(long[] sortedSet, int arrayLowBound, int arrayHighBound, long value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(long[] sortedSet, int arrayLowBound, int arrayHighBound, long value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet[start] == value)
					return ~start;
				else if (value < sortedSet[start])
				{
					assert start == 0 || value > sortedSet[start-1];
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet[midpoint])
				{
					return ~midpoint;
				}
				else if (value < sortedSet[midpoint])
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(long[] sortedSet, int arrayLowBound, int arrayHighBound, long value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		long curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet[i];
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<indexOf
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(LongList sortedSet, long value)
	{
		return findIndexForValueInSortedSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * aka indexOf()
	 */
	public static int findIndexForValueInSortedSet(LongList sortedSet, int lowBound, int highBound, long value)
	{
		int insertionPoint = findInsertionPointInSet(sortedSet, lowBound, highBound, value);
		if (insertionPoint < 0)
			return ~insertionPoint;
		else
			return -1;
	}
	//indexOf>
	
	
	
	
	
	
	
	
	
	
	
	//<Finding of insertion points
	/**
	 * See {@link #findInsertionPointInSet(LongList, int, int, long)}.
	 */
	public static int findInsertionPointInSet(LongList sortedSet, long value)
	{
		return findInsertionPointInSet(sortedSet, 0, sortedSet.size(), value);
	}
	
	
	/**
	 * Finds the index between two elements of a sorted array (that is currently a unique-set contentwise) which a given value would be inserted
	 * in order to preserve the sortedness.
	 * 0 means 'before all elements', 1 means 'between the 0th and 1th', ..., <code>elements.size()</code> means 'after all elements', etc.
	 * If the integer is already in the array, this will return a negative number.
	 * This negative number also encodes the location at which the duplicate element was found as such:<br>
	 * <code>return -(index + 1)</code>
	 * So a duplicate at 0 will return -1, a duplicate at 1 will return -2, 2=>-3, 3=>-4, etc.<br>
	 */
	public static int findInsertionPointInSet(LongList sortedSet, int arrayLowBound, int arrayHighBound, long value)
	{
		return findInsertionPointInSet_Jumping(sortedSet, arrayLowBound, arrayHighBound, value);
	}
	
	
	/**
	 * Starts in the middle of a block, figures out which half it's in, then repeats on that subblock until individual elements are found.
	 */
	public static int findInsertionPointInSet_Jumping(LongList sortedSet, int arrayLowBound, int arrayHighBound, long value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int start = arrayLowBound, end = arrayHighBound;
		int midpoint = 0;
		
		while (true)
		{
			if (end - start == 1)
			{
				if (sortedSet.get(start) == value)
					return ~start;
				else if (value < sortedSet.get(start))
				{
					assert start == 0 || value > sortedSet.get(start-1);
					return start;
				}
				else
				{
					return start + 1;
				}
			}
			else
			{
				midpoint = (end - start) / 2 + start;
				
				if (value == sortedSet.get(midpoint))
				{
					return ~midpoint;
				}
				else if (value < sortedSet.get(midpoint))
				{
					end = midpoint;
				}
				else
				{
					start = midpoint;
				}
			}
		}
	}
	
	
	/**
	 * Starts at the beginning and searches each element individually until two consecutive elements
	 * are found that are lower and higher than the given element.<br>
	 */
	public static int findInsertionPointInSet_Naive(LongList sortedSet, int arrayLowBound, int arrayHighBound, long value)
	{
		if (arrayHighBound - arrayLowBound == 0)
			return arrayLowBound;
		
		int i = arrayLowBound;
		long curr = 0;
		while (i < arrayHighBound)
		{
			curr = sortedSet.get(i);
			if (curr == value)
				return ~i;
			if (curr > value)
				break;
			i++;
		}
		
		return i;
	}
	//Finding of insertion points>
	
	
	
	
	
	
	
	
	
	
	
	
	//>>>
}
