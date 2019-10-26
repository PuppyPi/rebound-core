package rebound.util.objectutil;

/**
 * The entire point of {@link ObjectMethodDefaultsAdapterSuperclass} and the interfaces is to help overcome the Java 8 artificial restriction that interface default methods can override any method *other* than hashCode(), equals(), toString(), etc.
 */
public abstract class ObjectMethodDefaultsAdapterSuperclass
implements DefaultEqualsRestrictionCircumvention, DefaultHashCodeRestrictionCircumvention, DefaultToStringRestrictionCircumvention
{
	@Override
	public boolean equals(Object o)
	{
		return _equals(o);
	}
	
	@Override
	public int hashCode()
	{
		return _hashCode();
	}
	
	@Override
	public String toString()
	{
		return _toString();
	}
}
