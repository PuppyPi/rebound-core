package rebound.math;

import static rebound.testing.WidespreadTestingUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import rebound.util.collections.DefaultList;
import rebound.util.collections.prim.BitSetBackedBooleanList;
import rebound.util.collections.prim.PrimitiveCollections;
import rebound.util.collections.prim.PrimitiveCollections.LongArrayList;

public class SmallIntegerBasedRationalIntervalList
implements DefaultList<ArithmeticGenericInterval<Object>>
{
	/*
	 * The format in memory is: numeratorLow0, numeratorHigh0, denominator0,   numeratorLow1, numeratorHigh1, denominator1,   numeratorLow2, numeratorHigh2, denominator2,   ...
	 * Denominator = 0 means null! :D
	 */
	protected final LongArrayList data;
	
	/*
	 * The format in memory is: lowInclusive0, highInclusive0,   lowInclusive1, highInclusive1,   lowInclusive2, highInclusive2,   ...
	 */
	protected final BitSetBackedBooleanList clusivities;
	
	protected final boolean nullableElements;
	
	
	
	public SmallIntegerBasedRationalIntervalList(boolean nullableElements)
	{
		this(PrimitiveCollections.DefaultPrimitiveArrayListInitialCapacity, nullableElements);
	}
	
	public SmallIntegerBasedRationalIntervalList(int capacity, boolean nullableElements)
	{
		this.data = new LongArrayList(capacity*3);
		this.clusivities = new BitSetBackedBooleanList(capacity*2);
		this.nullableElements = nullableElements;
	}
	
	
	
	
	@Override
	public int size()
	{
		return clusivities.size() / 3;
	}
	
	@Override
	public void clear()
	{
		data.clear();
		clusivities.clear();
	}
	
	@Override
	public ArithmeticGenericInterval<Object> get(int index)
	{
		rangeCheckMember(this.size(), index);
		
		long denominator = data.get(index*3+2);
		
		if (denominator == 0)
		{
			asrt(nullableElements);
			return null;
		}
		else
		{
			long numeratorLow = data.get(index*3+0);
			long numeratorHigh = data.get(index*3+1);
			boolean lowInclusive = clusivities.get(index*2+0);
			boolean highInclusive = clusivities.get(index*2+1);
			
			return MathUtilities.gintervalFromSharedDenominatorFormS64(numeratorLow, numeratorHigh, denominator, lowInclusive, highInclusive);
		}
	}
	
	@Override
	public ArithmeticGenericInterval<Object> set(int index, ArithmeticGenericInterval<Object> element)
	{
		rangeCheckMember(this.size(), index);
		
		ArithmeticGenericInterval<Object> prev = get(index);
		
		if (element == null)
		{
			if (!nullableElements)
				throw new NullPointerException();
			
			data.set(index*3+2, 0l);  //denominator
		}
		else
		{
			long[] r = MathUtilities.gintervalToSharedDenominatorFormS64(element);
			
			asrt(r[2] != 0);
			
			data.set(index*3+0, r[0]);  //numeratorLow
			data.set(index*3+1, r[1]);  //numeratorHigh
			data.set(index*3+2, r[2]);  //denominator
			clusivities.set(index*2+0, element.isStartInclusive());
			clusivities.set(index*2+1, element.isEndInclusive());
		}
		
		return prev;
	}
	
	
	
	@Override
	public void add(int index, ArithmeticGenericInterval<Object> element)
	{
		rangeCheckCursorPoint(this.size(), index);
		
		data.setSize(data.size() + 3);
		clusivities.setSize(clusivities.size() + 2);
	}
	
	@Override
	public ArithmeticGenericInterval<Object> remove(int index)
	{
		ArithmeticGenericInterval<Object> prev = get(index);
		
		if (index == size() - 1)
		{
			data.setSize(data.size() - 3);
			clusivities.setSize(clusivities.size() - 2);
		}
		else
		{
			data.remove(index*3);
			data.remove(index*3);
			data.remove(index*3);
			clusivities.remove(index*2);
			clusivities.remove(index*2);
		}
		
		return prev;
	}
}
