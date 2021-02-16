package rebound.util.collections;

import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.util.Set;
import rebound.annotations.semantic.operationspecification.HashableType;
import rebound.text.StringUtilities.Reprable;
import rebound.util.objectutil.DefaultEqualsRestrictionCircumvention;
import rebound.util.objectutil.DefaultHashCodeRestrictionCircumvention;
import rebound.util.objectutil.DefaultToStringRestrictionCircumvention;

@HashableType
public interface PairCommutative<E>
extends DefaultEqualsRestrictionCircumvention, DefaultHashCodeRestrictionCircumvention, DefaultToStringRestrictionCircumvention, Reprable
{
	public E getA();
	public E getB();
	
	
	
	
	/**
	 * Same as a 2-element {@link Set}
	 */
	@Override
	public default int _hashCode()
	{
		E a = getA();
		E b = getB();
		
		int result = 0;
		result += ((a == null) ? 0 : a.hashCode());
		result += ((b == null) ? 0 : b.hashCode());
		return result;
	}
	
	
	@Override
	public default boolean _equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof PairCommutative))
			return false;
		PairCommutative other = (PairCommutative) obj;
		
		return
		eq(this.getA(), other.getA()) && eq(this.getB(), other.getB())
		||
		eq(this.getA(), other.getB()) && eq(this.getB(), other.getA());
	}
	
	@Override
	public default String _toString()
	{
		return "{"+getA()+", "+getB()+"}";
	}
}
