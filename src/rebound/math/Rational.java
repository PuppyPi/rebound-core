package rebound.math;

import static rebound.math.MathUtilities.*;
import java.math.BigInteger;
import javax.annotation.Nonnegative;
import rebound.annotations.semantic.SignalInterface;
import rebound.exceptions.OverflowException;

/**
 * This must extend {@link Number} as well!
 * 
 * @author RP
 */
@SignalInterface
public interface Rational<IntegerType>
extends Comparable
{
	/**
	 * Any integer! :D
	 * Either {@link Long} or a big integer (eg {@link BigInteger java.math's one} :3 )
	 */
	public IntegerType getNumerator();
	
	/**
	 * A positive integer :>
	 * 		(ie, not negative or zero :3 )
	 * Either {@link Long} or a big integer (eg {@link BigInteger java.math's one} :3 )
	 */
	@Nonnegative
	public IntegerType getDenominator();
	
	
	
	
	public default long getNumeratorS64() throws OverflowException
	{
		return safeCastIntegerToS64(getNumerator());
	}
	
	public default long getDenominatorS64() throws OverflowException
	{
		return safeCastIntegerToS64(getDenominator());
	}
	
	
	public double toDoubleEstimate();
	
	
	
	
	
	
	
	public String toFractionString();
	//public String toFractionString(int base);
	
	
	
	/**
	 * It should put parentheses around the repeating part and three periods (an ascii ellipsis) inside them!
	 * Eg, 113/875 = 0.129(142857...)
	 * 1/3 = 0.(3)
	 * 1/6 = 0.1(6)
	 */
	public String toPossiblyRepeatingDecimalString();
	//public String toPossiblyRepeatingDecimalString(int base);
	
	//this one wouldn't have the parentheses :3 , but wouldn't have perfect accuracy!
	//public String toDecimalString(int base, int numberOfRepetitionsIfRepeating);
	
	
	
	
	
	
	
	@Override
	public default int compareTo(Object o)
	{
		return MathUtilities.mathcmp(this, o);
	}
}
