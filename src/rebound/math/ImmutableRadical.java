package rebound.math;

import static java.util.Objects.*;
import static rebound.math.MathUtilities.*;
import rebound.annotations.semantic.simpledata.Positive;
import rebound.exceptions.OutOfDomainArithmeticException.ComplexNumberArithmeticException;

public class ImmutableRadical
implements Radical<Object, Object>
{
	protected final @Positive @PolyInteger Object degree;
	
	/**
	 * Must be nonnegative if degree {@link MathUtilities#isEven(Object)}
	 */
	protected final @RationalOrInteger Object radicand;
	
	
	public ImmutableRadical(Object degree, Object radicand) throws IllegalArgumentException, ComplexNumberArithmeticException
	{
		requireNonNull(degree);
		requireNonNull(radicand);
		
		if (mathcmp(degree, 1) < 0)
			throw new IllegalArgumentException("Degree must be >= 1 !");
		
		if (isEven(degree) && mathcmp(radicand, 0) < 0)
			throw new ComplexNumberArithmeticException();
		
		this.degree = degree;
		this.radicand = radicand;
	}
	
	
	public Object getDegree()
	{
		return degree;
	}
	
	
	public Object getRadicand()
	{
		return radicand;
	}
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + mathhash(degree);
		result = prime * result + mathhash(radicand);
		return result;
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImmutableRadical other = (ImmutableRadical) obj;
		if (!matheq(degree, other.degree))
			return false;
		if (!matheq(radicand, other.radicand))
			return false;
		return true;
	}
	
	
	@Override
	public String toString()
	{
		return (isInteger(radicand) ? radicand : ("("+radicand+")")) + "^(1/"+degree+")";
	}
}
