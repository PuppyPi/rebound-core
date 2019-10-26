package rebound.util.collections;

import static rebound.util.objectutil.BasicObjectUtilities.*;
import rebound.annotations.semantic.operationspecification.HashableType;
import rebound.text.StringUtilities.Reprable;
import rebound.util.objectutil.DefaultEqualsRestrictionCircumvention;
import rebound.util.objectutil.DefaultHashCodeRestrictionCircumvention;
import rebound.util.objectutil.DefaultToStringRestrictionCircumvention;

@HashableType
public interface PairOrdered<A, B>
extends DefaultEqualsRestrictionCircumvention, DefaultHashCodeRestrictionCircumvention, DefaultToStringRestrictionCircumvention, Reprable
{
	public A getA();
	public B getB();
	
	
	
	
	@Override
	public default int _hashCode()
	{
		A a = getA();
		B b = getB();
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		return result;
	}
	
	@Override
	public default boolean _equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof PairOrdered))
			return false;
		PairOrdered other = (PairOrdered) obj;
		
		return eq(this.getA(), other.getA()) && eq(this.getB(), other.getB());
	}
	
	
	
	
	@Override
	public default String _toString()
	{
		return "("+getA()+", "+getB()+")";
	}
}
