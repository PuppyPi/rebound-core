package rebound.math;

import static rebound.GlobalCodeMetastuffContext.*;
import static rebound.math.MathUtilities.*;
import java.io.Serializable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.simpledata.NormalizesPrimitives;
import rebound.exceptions.DivisionByZeroException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.TruncationException;

//TODO Support formatting to possibly-repeating-decimals! :D
//TODO Support PARSING FROM possibly-repeating-decimals! :0 :D!(?)

/**
 * Note: it should really be Immutable*Reduced*Rational now :33
 * @author Puppy Pie ^w^
 */
public class ImmutableRational
extends Number
implements Rational, Serializable
{
	private static final long serialVersionUID = 1L;
	
	
	
	protected final Object numerator, denominator;
	
	
	/**
	 * See {@link MathUtilities#rational(Object, Object)} instead :33
	 */
	//	/**
	//	 * Consider using {@link MathUtilities#rational(Object, Object)} instead, since that just efficiently returns the numerator if the denominator == 1  ^w^
	//	 */
	@ImplementationTransparency
	@NormalizesPrimitives
	public ImmutableRational(Object numerator, Object denominator)
	{
		numerator = normalizeNumberToRationalOrInteger(numerator);
		denominator = normalizeNumberToRationalOrInteger(denominator);
		
		if (!isInteger(numerator))
			throw new IllegalArgumentException("Numerator not an integer!!: "+numerator);
		if (!isInteger(denominator))
			throw new IllegalArgumentException("Denominator not an integer!!: "+numerator);
		
		
		int denominatorSignum = signum(denominator);
		
		if (denominatorSignum < 0)
			numerator = negate(numerator);
		else if (denominatorSignum == 0)
			throw new DivisionByZeroException();
		
		
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	
	
	@Override
	public Object getNumerator()
	{
		return this.numerator;
	}
	
	@Override
	public Object getDenominator()
	{
		return this.denominator;
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.denominator == null) ? 0 : this.denominator.hashCode());
		result = prime * result + ((this.numerator == null) ? 0 : this.numerator.hashCode());
		return result;
	}
	
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		
		if (obj instanceof ImmutableRational)
		{
			ImmutableRational other = (ImmutableRational) obj;
			if (this.denominator == null)
			{
				if (other.denominator != null)
					return false;
			}
			else if (!this.denominator.equals(other.denominator))
				return false;
			if (this.numerator == null)
			{
				if (other.numerator != null)
					return false;
			}
			else if (!this.numerator.equals(other.numerator))
				return false;
			
			return true;
		}
		else if (obj instanceof Rational)
		{
			return equals(MathUtilities.reduce(obj));
		}
		else
		{
			return false;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public byte byteValue()
	{
		return SmallFloatMathUtilities.safeCastSingleToS8(longValue());
	}
	
	@Override
	public short shortValue()
	{
		return SmallFloatMathUtilities.safeCastSingleToS16(longValue());
	}
	
	@Override
	public int intValue()
	{
		return SmallFloatMathUtilities.safeCastSingleToS32(longValue());
	}
	
	
	@Override
	public long longValue()
	{
		if (!matheq(getDenominator(), 1))
			throw new TruncationException("Casting noninteger to integer!! \\o/");
		
		return safeCastIntegerToS64(getNumerator());
	}
	
	
	
	@Override
	public float floatValue()
	{
		return (float)doubleValue();
	}
	
	@Override
	public double doubleValue()
	{
		return ((Number)getNumerator()).doubleValue() / ((Number)getDenominator()).doubleValue();
	}
	
	
	
	
	@Override
	public double toDoubleEstimate()
	{
		return doubleValue();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public String toFractionString()
	{
		return getNumerator().toString()+"/"+getDenominator().toString();
	}
	
	@Override
	public String toPossiblyRepeatingDecimalString()
	{
		//TODO! :D!
		logBug(new NotYetImplementedException());
		return toFractionString();
	}
	
	
	
	@Override
	public String toString()
	{
		return toFractionString();
	}
}
