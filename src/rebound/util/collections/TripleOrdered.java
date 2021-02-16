package rebound.util.collections;

import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.util.List;
import rebound.annotations.semantic.operationspecification.HashableType;
import rebound.text.StringUtilities.Reprable;
import rebound.util.objectutil.DefaultEqualsRestrictionCircumvention;
import rebound.util.objectutil.DefaultHashCodeRestrictionCircumvention;
import rebound.util.objectutil.DefaultToStringRestrictionCircumvention;

@HashableType
public interface TripleOrdered<A, B, C>
extends DefaultEqualsRestrictionCircumvention, DefaultHashCodeRestrictionCircumvention, DefaultToStringRestrictionCircumvention, Reprable
{
	public A getA();
	public B getB();
	public C getC();
	
	
	
	
	/**
	 * Same as a 3-element {@link List}
	 */
	@Override
	public default int _hashCode()
	{
		A a = getA();
		B b = getB();
		C c = getC();
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		return result;
	}
	
	@Override
	public default boolean _equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof TripleOrdered))
			return false;
		TripleOrdered other = (TripleOrdered) obj;
		
		return eq(this.getA(), other.getA()) && eq(this.getB(), other.getB()) && eq(this.getC(), other.getC());
	}
	
	
	
	
	@Override
	public default String _toString()
	{
		return "("+getA()+", "+getB()+", "+getC()+")";
	}
}
