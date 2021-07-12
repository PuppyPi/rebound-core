package rebound.math;

import static java.lang.Math.*;
import static rebound.GlobalCodeMetastuffContext.*;
import static rebound.bits.BitfieldSafeCasts.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.CodeHinting.*;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.exceptions.DivisionByZeroException;
import rebound.exceptions.InfinityException;
import rebound.exceptions.NotANumberException;
import rebound.exceptions.OutOfDomainArithmeticException;
import rebound.exceptions.OverflowException;
import rebound.exceptions.TruncationException;
import rebound.util.Primitives;
import rebound.util.collections.prim.PrimitiveCollections.DoubleList;
import rebound.util.collections.prim.PrimitiveCollections.FloatList;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToDouble;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToFloat;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToLong;

public class SmallFloatMathUtilities
{
	public static final double SQRT2 = sqrt(2d);
	public static final double SQRT3 = sqrt(3d);
	public static final double PI2 = PI * 2;
	public static final double PI3o2 = PI * 3d / 2d;
	public static final double PI1o2 = PI / 2d;
	
	public static final float SQRT2f = (float)SQRT2;
	public static final float SQRT3f = (float)SQRT3;
	public static final float PI2f = (float)PI2;
	public static final float PI3o2f = (float)PI3o2;
	public static final float PI1o2f = (float)PI1o2;
	
	
	public static final double TAU = PI2;
	public static final double TAU3o4 = PI3o2;
	public static final double TAU1o2 = PI;
	public static final double TAU1o4 = PI1o2;
	
	
	
	
	public static boolean isNaN(float x)
	{
		return x != x;
	}
	
	public static boolean isNaN(double x)
	{
		return x != x;
	}
	
	public static boolean isNaN(Float x)
	{
		return x == null ? false : isNaN(x.floatValue());
	}
	
	public static boolean isNaN(Double x)
	{
		return x == null ? false : isNaN(x.doubleValue());
	}
	
	public static void checkNotNaN(float x)
	{
		if (isNaN(x))
			throw new NotANumberException();
	}
	
	public static void checkNotNaN(float a, float b)
	{
		if (isNaN(a) || isNaN(b))
			throw new NotANumberException();
	}
	
	public static void checkNotNaN(float... vs)
	{
		for (float v : vs)
			if (isNaN(v))
				throw new NotANumberException();
	}
	
	public static void checkNotNaN(double x)
	{
		if (isNaN(x))
			throw new NotANumberException();
	}
	
	public static void checkNotNaN(double a, double b)
	{
		if (isNaN(a) || isNaN(b))
			throw new NotANumberException();
	}
	
	public static void checkNotNaN(double... vs)
	{
		for (double v : vs)
			if (isNaN(v))
				throw new NotANumberException();
	}
	
	public static void checkNotNaN(Float x)
	{
		if (isNaN(x))
			throw new NotANumberException();
	}
	
	public static void checkNotNaN(Double x)
	{
		if (isNaN(x))
			throw new NotANumberException();
	}
	
	public static boolean isFinite(float x)
	{
		return !(Float.isInfinite(x) || isNaN(x));
	}
	
	public static boolean isFinite(double x)
	{
		return !(Double.isInfinite(x) || isNaN(x));
	}
	
	public static float requireFinite(float x) throws NotANumberException, InfinityException
	{
		if (Float.isNaN(x))
			throw new NotANumberException();
		else if (Float.isInfinite(x))
			throw new InfinityException();
		else
			return x;
	}
	
	public static double requireFinite(double x) throws NotANumberException, InfinityException
	{
		if (Double.isNaN(x))
			throw new NotANumberException();
		else if (Double.isInfinite(x))
			throw new InfinityException();
		else
			return x;
	}
	
	public static float requireNotNaN(float x) throws NotANumberException
	{
		checkNotNaN(x);
		return x;
	}
	
	public static double requireNotNaN(double x) throws NotANumberException
	{
		checkNotNaN(x);
		return x;
	}
	
	public static float checkNotZeroForDivide(float x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static Float checkNotZeroForDivide(Float x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static double checkNotZeroForDivide(double x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static Double checkNotZeroForDivide(Double x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	
	
	
	
	
	
	/**
	 * Set both tolerances to zero to make this equivalent to {@link Primitives#eqSane(Double, Double)}  :>
	 */
	public static boolean equalWithinTolerances(double a, double b, double absoluteTolerance, double relativeTolerance)
	{
		if (a == b)  //takes care of infinities :3
			return true;
		
		else if (isNaN(a))
			return isNaN(b);
		else if (isNaN(b))
			return false;  //because !isNaN(a)
		
		else if (Double.isInfinite(a))
			return false;  //because a != b
		else if (Double.isInfinite(b))
			return false;  //because a != b
		
		else
		{
			// <= allows a tolerance of 0 to simply define exactly-equal, which just a bare < would not!  :33
			
			// d / abs((a+b)/2) <= relativeTolerance will always be false (correctly!..I think XD) when a+b == 0
			
			double d = abs(a - b);
			
			return d <= absoluteTolerance ||
			d / abs((a+b)/2) <= relativeTolerance;
		}
	}
	
	/**
	 * Set tolerance to zero to make this equivalent to {@link Primitives#eqSane(Double, Double)}  :>
	 */
	public static boolean equalWithinRelativeTolerance(double a, double b, double tolerance)
	{
		if (a == b)  //takes care of infinities :3
			return true;
		
		else if (isNaN(a))
			return isNaN(b);
		else if (isNaN(b))
			return false;  //because !isNaN(a)
		
		else if (Double.isInfinite(a))
			return false;  //because a != b
		else if (Double.isInfinite(b))
			return false;  //because a != b
		
		else
			// abs(a - b) / abs((a+b)/2) <= tolerance will always be false (correctly!..I think XD) when a+b == 0
			return abs(a - b) / abs((a+b)/2) <= tolerance;    // <= allows a tolerance of 0 to simply define exactly-equal, which just a bare < would not!  :33
	}
	
	/**
	 * Set tolerance to zero to make this equivalent to {@link Primitives#eqSane(Double, Double)}  :>
	 */
	public static boolean equalWithinTolerance(double a, double b, double tolerance)
	{
		if (a == b)  //takes care of infinities :3
			return true;
		
		else if (isNaN(a))
			return isNaN(b);
		else if (isNaN(b))
			return false;  //because !isNaN(a)
		
		else if (Double.isInfinite(a))
			return false;  //because a != b
		else if (Double.isInfinite(b))
			return false;  //because a != b
		
		else
			return abs(a - b) <= tolerance;    // <= allows a tolerance of 0 to simply define exactly-equal, which just a bare < would not!  :33
	}
	
	
	
	
	
	/**
	 * Set tolerance to zero to make this equivalent to {@link Primitives#eqSane(Float, Float)}  :>
	 */
	public static boolean equalWithinToleranceF32(float a, float b, float tolerance)
	{
		if (a == b)  //takes care of infinities :3
			return true;
		
		else if (isNaN(a))
			return isNaN(b);
		else if (isNaN(b))
			return false;  //because !isNaN(a)
		
		else if (Float.isInfinite(a))
			return false;  //because a != b
		else if (Float.isInfinite(b))
			return false;  //because a != b
		
		else
			return abs(a - b) <= tolerance;    // <= allows a tolerance of 0 to simply define exactly-equal, which just a bare < would not!  :33
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * NaN results in a signum of 0 here :3
	 */
	public static int signumToInt(float x)
	{
		return x > 0 ? 1 : (x < 0 ? -1 : 0);
	}
	
	/**
	 * NaN results in a signum of 0 here :3
	 */
	public static int signumToInt(double x)
	{
		return x > 0 ? 1 : (x < 0 ? -1 : 0);
	}
	
	
	
	public static boolean lessThanOrFlipped(float smallerCandidate, float largerCandidate, boolean flipped)
	{
		return flipped ? (largerCandidate < smallerCandidate) : (smallerCandidate < largerCandidate);
	}
	
	public static boolean lessThanEqualToOrFlipped(float smallerCandidate, float largerCandidate, boolean flipped)
	{
		return flipped ? (largerCandidate <= smallerCandidate) : (smallerCandidate <= largerCandidate);
	}
	
	
	public static boolean lessThanOrFlipped(double smallerCandidate, double largerCandidate, boolean flipped)
	{
		return flipped ? (largerCandidate < smallerCandidate) : (smallerCandidate < largerCandidate);
	}
	
	public static boolean lessThanEqualToOrFlipped(double smallerCandidate, double largerCandidate, boolean flipped)
	{
		return flipped ? (largerCandidate <= smallerCandidate) : (smallerCandidate <= largerCandidate);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static double arcpyr(int n)
	{
		//arcpyr(n) = (-1 + sqrt(1 + 8n))/2
		return (-1d + Math.sqrt(1d + 8d*n))/2;
	}
	
	public static int ceilarcpyr(int n)
	{
		if (n < 1)
			throw new IllegalArgumentException("n > 0");
		
		//ceil(arcpyr(n)) = ceil((-1 + sqrt(1 + 8n))/2)
		int o = (int)Math.ceil(arcpyr(n));
		
		//Check for implementation accuracy problems
		if (pyr(o) != n)
			throw new TruncationException();
		
		return o;
	}
	
	/**
		n	ceil(arcpyr(n))		ceilarcpyr modulus(n)
		1	1					0
		2	2					-1
		3	2					0
		4	3					-2
		5	3					-1
		6	3					0
		7	4					-3
		8	4					-2
		9	4					-1
		10	4					0
		11	5					-4
		12	5					-3
		13	5					-2
		14	5					-1
		15	5					0
		
		pyr(ceilarcpyr(n)) + modulus(n) = n
	 */
	public static int ceilarcpyrmodulus(int n)
	{
		return n - pyr(ceilarcpyr(n));
	}
	//Discrete frames>
	
	
	
	
	
	
	
	public static Comparator<Float> FloatComparison = (a, b) -> cmp((float)a, (float)b);
	public static Comparator<Double> DoubleComparison = (a, b) -> cmp((double)a, (double)b);
	
	
	public static int cmp(float a, float b)
	{
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	public static int cmp(double a, double b)
	{
		checkNotNaN(a);
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	
	public static int cmpChainable(int prev, float a, float b)
	{
		return prev != 0 ? prev : cmp(a, b);
	}
	
	public static int cmpChainable(int prev, double a, double b)
	{
		return prev != 0 ? prev : cmp(a, b);
	}
	
	
	
	
	
	
	
	
	
	public static int cmpNullAsNinf(@Nullable Float a, @Nullable Float b)
	{
		checkNotNaN(a);
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	public static int cmpNullAsNinf(@Nullable Double a, @Nullable Double b)
	{
		checkNotNaN(a);
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	
	public static int cmpChainableNullAsNinf(int prev, @Nullable Float a, @Nullable Float b)
	{
		return prev != 0 ? prev : cmpNullAsNinf(a, b);
	}
	
	public static int cmpChainableNullAsNinf(int prev, @Nullable Double a, @Nullable Double b)
	{
		return prev != 0 ? prev : cmpNullAsNinf(a, b);
	}
	
	
	
	
	
	
	
	
	
	public static double progmod(double n, double d)
	{
		if (d == 0)
			return 0;
		
		if (n >= 0)
			return n % d;
		else //if (n < 0)
			return (n - (Math.floor(n / d)*d)) % d;
	}
	
	public static float progmod(float n, float d)
	{
		if (d == 0)
			return 0;
		
		if (n >= 0)
			return n % d;
		else //if (n < 0)
			return (n - ((float)Math.floor(n / d)*d)) % d;
	}
	
	public static double truncate(double x, double min, double max)
	{
		if (x < min) return min;
		if (x > max) return max;
		return x;
	}
	
	public static float truncate(float x, float min, float max)
	{
		if (x < min) return min;
		if (x > max) return max;
		return x;
	}
	
	
	
	
	public static double least(double[] values, int offset, int length)  //Todo make orthogonal with greatest() and float  x'D
	{
		if (length == 0)
			throw new IllegalArgumentException();
		
		double e = values[offset+0];
		checkNotNaN(e);
		
		for (int i = 1; i < length; i++)
		{
			checkNotNaN(values[offset+i]);
			if (values[offset+i] < e)
				e = values[offset+i];
		}
		
		return e;
	}
	
	public static float least(float... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		float e = values[0];
		checkNotNaN(e);
		
		for (int i = 1; i < values.length; i++)
		{
			checkNotNaN(values[i]);
			if (values[i] < e)
				e = values[i];
		}
		
		return e;
	}
	
	public static float greatest(float... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		float e = values[0];
		checkNotNaN(e);
		
		for (int i = 1; i < values.length; i++)
		{
			checkNotNaN(values[i]);
			if (values[i] > e)
				e = values[i];
		}
		
		return e;
	}
	
	public static double least(double... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		double e = values[0];
		checkNotNaN(e);
		
		for (int i = 1; i < values.length; i++)
		{
			checkNotNaN(values[i]);
			if (values[i] < e)
				e = values[i];
		}
		
		return e;
	}
	
	public static double greatest(double... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		double e = values[0];
		checkNotNaN(e);
		
		for (int i = 1; i < values.length; i++)
		{
			checkNotNaN(values[i]);
			if (values[i] > e)
				e = values[i];
		}
		
		return e;
	}
	
	
	
	
	
	
	
	public static double least(DoubleList values, int offset, int length)  //Todo make orthogonal with greatest() and float  x'D
	{
		if (length == 0)
			throw new IllegalArgumentException();
		
		double e = values.getDouble(offset+0);
		checkNotNaN(e);
		
		for (int i = 1; i < length; i++)
		{
			checkNotNaN(values.getDouble(offset+i));
			if (values.getDouble(offset+i) < e)
				e = values.getDouble(offset+i);
		}
		
		return e;
	}
	
	
	public static float least(FloatList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		float e = values.get(0);
		checkNotNaN(e);
		
		for (int i = 1; i < values.size(); i++)
		{
			checkNotNaN(values.getFloat(i));
			if (values.getFloat(i) < e)
				e = values.getFloat(i);
		}
		
		return e;
	}
	
	public static float greatest(FloatList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		float e = values.get(0);
		checkNotNaN(e);
		
		for (int i = 1; i < values.size(); i++)
		{
			checkNotNaN(values.getFloat(i));
			if (values.getFloat(i) > e)
				e = values.getFloat(i);
		}
		
		return e;
	}
	
	public static double least(DoubleList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		double e = values.get(0);
		checkNotNaN(e);
		
		for (int i = 1; i < values.size(); i++)
		{
			checkNotNaN(values.getDouble(i));
			if (values.getDouble(i) < e)
				e = values.getDouble(i);
		}
		
		return e;
	}
	
	public static double greatest(DoubleList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		double e = values.get(0);
		checkNotNaN(e);
		
		for (int i = 1; i < values.size(); i++)
		{
			checkNotNaN(values.getDouble(i));
			if (values.getDouble(i) > e)
				e = values.getDouble(i);
		}
		
		return e;
	}
	
	
	
	
	
	
	public static int leastIndex(double[] values, int offset, int length)  //Todo make this orthogonal with the greatestIndex's ^^''
	{
		if (length == 0)
			throw new IllegalArgumentException();
		
		double leastSoFar = values[offset+0];
		int indexOfLeastSoFar = 0;
		checkNotNaN(leastSoFar);
		
		for (int i = 1; i < length; i++)
		{
			int o = offset+i;
			double v = values[o];
			checkNotNaN(v);
			if (v < leastSoFar)
			{
				leastSoFar = v;
				indexOfLeastSoFar = o;
			}
		}
		
		return indexOfLeastSoFar;
	}
	
	
	
	
	public static int greatestIndexDouble(double[] values, int offset, int length)
	{
		return greatestIndexDouble(i -> values[i], offset, length);
	}
	
	public static int greatestIndexDouble(DoubleList values, int offset, int length)
	{
		return greatestIndexDouble(i -> values.getDouble(i), offset, length);
	}
	
	public static int greatestIndexDouble(UnaryFunctionIntToDouble values, int offset, int length)
	{
		if (length == 0)
			throw new IllegalArgumentException();
		
		double leastSoFar = values.f(offset+0);
		int indexOfLeastSoFar = 0;
		checkNotNaN(leastSoFar);
		
		for (int i = 1; i < length; i++)
		{
			int o = offset+i;
			double v = values.f(o);
			checkNotNaN(v);
			if (v > leastSoFar)
			{
				leastSoFar = v;
				indexOfLeastSoFar = o;
			}
		}
		
		return indexOfLeastSoFar;
	}
	
	
	
	public static int greatestIndexFloat(float[] values, int offset, int length)
	{
		return greatestIndexFloat(i -> values[i], offset, length);
	}
	
	public static int greatestIndexFloat(FloatList values, int offset, int length)
	{
		return greatestIndexFloat(i -> values.getFloat(i), offset, length);
	}
	
	public static int greatestIndexFloat(UnaryFunctionIntToFloat values, int offset, int length)
	{
		if (length == 0)
			throw new IllegalArgumentException();
		
		float leastSoFar = values.f(offset+0);
		int indexOfLeastSoFar = 0;
		checkNotNaN(leastSoFar);
		
		for (int i = 1; i < length; i++)
		{
			int o = offset+i;
			float v = values.f(o);
			checkNotNaN(v);
			if (v > leastSoFar)
			{
				leastSoFar = v;
				indexOfLeastSoFar = o;
			}
		}
		
		return indexOfLeastSoFar;
	}
	
	
	
	
	
	
	
	public static float least(float a, float b)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		
		return b <= a ? b : a;
	}
	
	public static float greatest(float a, float b)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		
		return b >= a ? b : a;
	}
	
	public static float least(float a, float b, float c)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		checkNotNaN(c);
		
		if (c >= a && c >= b)
			return least(a, b);
		else if (b >= a && b >= c)
			return least(a, c);
		else if (a >= b && a >= c)
			return least(b, c);
		else
			throw new AssertionError();
	}
	
	public static float greatest(float a, float b, float c)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		checkNotNaN(c);
		
		if (c <= a && c <= b)
			return greatest(a, b);
		else if (b <= a && b <= c)
			return greatest(a, c);
		else if (a <= b && a <= c)
			return greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static float least(float a, float b, float c, float d)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		checkNotNaN(c);
		checkNotNaN(d);
		
		if (d >= a && d >= b && d >= c)
			return least(a, b, c);
		else if (c >= a && c >= b && c >= d)
			return least(a, b, d);
		else if (b >= a && b >= c && b >= d)
			return least(a, c, d);
		else if (a >= b && a >= c && a >= d)
			return least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static float greatest(float a, float b, float c, float d)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		checkNotNaN(c);
		checkNotNaN(d);
		
		if (d <= a && d <= b && d <= c)
			return greatest(a, b, c);
		else if (c <= a && c <= b && c <= d)
			return greatest(a, b, d);
		else if (b <= a && b <= c && b <= d)
			return greatest(a, c, d);
		else if (a <= b && a <= c && a <= d)
			return greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static double least(double a, double b)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		
		return b <= a ? b : a;
	}
	
	public static double greatest(double a, double b)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		
		return b >= a ? b : a;
	}
	
	public static double least(double a, double b, double c)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		checkNotNaN(c);
		
		if (c >= a && c >= b)
			return least(a, b);
		else if (b >= a && b >= c)
			return least(a, c);
		else if (a >= b && a >= c)
			return least(b, c);
		else
			throw new AssertionError();
	}
	
	public static double greatest(double a, double b, double c)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		checkNotNaN(c);
		
		if (c <= a && c <= b)
			return greatest(a, b);
		else if (b <= a && b <= c)
			return greatest(a, c);
		else if (a <= b && a <= c)
			return greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static double least(double a, double b, double c, double d)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		checkNotNaN(c);
		checkNotNaN(d);
		
		if (d >= a && d >= b && d >= c)
			return least(a, b, c);
		else if (c >= a && c >= b && c >= d)
			return least(a, b, d);
		else if (b >= a && b >= c && b >= d)
			return least(a, c, d);
		else if (a >= b && a >= c && a >= d)
			return least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static double greatest(double a, double b, double c, double d)
	{
		checkNotNaN(a);
		checkNotNaN(b);
		checkNotNaN(c);
		checkNotNaN(d);
		
		if (d <= a && d <= b && d <= c)
			return greatest(a, b, c);
		else if (c <= a && c <= b && c <= d)
			return greatest(a, b, d);
		else if (b <= a && b <= c && b <= d)
			return greatest(a, c, d);
		else if (a <= b && a <= c && a <= d)
			return greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	
	
	
	public static int safeCeilingToInt(double x)
	{
		x = Math.ceil(x);
		if (x > Integer.MAX_VALUE) throw new OverflowException();
		if (x < Integer.MIN_VALUE) throw new OverflowException();
		return (int)x;
	}
	
	public static long safeCeilingToLong(double x)
	{
		x = Math.ceil(x);
		if (x > Long.MAX_VALUE) throw new OverflowException();
		if (x < Long.MIN_VALUE) throw new OverflowException();
		return (long)x;
	}
	
	public static int safeFloorToInt(double x)
	{
		x = Math.floor(x);
		if (x > Integer.MAX_VALUE) throw new OverflowException();
		if (x < Integer.MIN_VALUE) throw new OverflowException();
		return (int)x;
	}
	
	public static long safeFloorToLong(double x)
	{
		x = Math.floor(x);
		if (x > Long.MAX_VALUE) throw new OverflowException();
		if (x < Long.MIN_VALUE) throw new OverflowException();
		return (long)x;
	}
	
	public static double kahanSum(double... inputs)
	{
		//Note: shamelessly just copying from wikipedia XD''
		
		double sum = 0;
		double c = 0;
		
		double y = 0, t = 0;
		for (double x : inputs)
		{
			y = x - c;
			t = sum + y;
			c = (t - sum) - y;
			sum = t;
		}
		
		return sum;
	}
	
	public static double kahanSum(UnaryFunctionIntToDouble list, int count)
	{
		double sum = 0;
		double c = 0;
		
		double x = 0, y = 0, t = 0;
		for (int i = 0; i < count; i++)
		{
			x = list.f(i);
			y = x - c;
			t = sum + y;
			c = (t - sum) - y;
			sum = t;
		}
		
		return sum;
	}
	
	public static double kahanSum(Iterable<Double> iterableOfDoubles)
	{
		double sum = 0;
		double c = 0;
		
		double y = 0, t = 0;
		for (Double x : iterableOfDoubles)
		{
			y = x - c;
			t = sum + y;
			c = (t - sum) - y;
			sum = t;
		}
		
		return sum;
	}
	
	public static double recursiveSum(UnaryFunctionIntToDouble list, int start, int count)
	{
		if (count == 1)
			return list.f(start);
		else
			return recursiveSum(list, start, count/2) + recursiveSum(list, start+(count/2), count - (count / 2)); //second one is basically ceiling division X3
	}
	
	public static double recursiveSum(int start, int count, double... inputs)
	{
		if (count == 1)
			return inputs[start];
		else
			return recursiveSum(start, count/2, inputs) + recursiveSum(start+(count/2), count - (count / 2), inputs); //second one is basically ceiling division X3
	}
	
	public static double recursiveSum(List<Double> listOfDoubles, int start, int count)
	{
		if (count == 1)
			return (listOfDoubles.get(start)).doubleValue(); //checks for nonnull ;>
		else
			return recursiveSum(listOfDoubles, start, count/2) + recursiveSum(listOfDoubles, start+(count/2), count - (count / 2)); //second one is basically ceiling division X3
	}
	
	public static double recursiveSum(UnaryFunctionIntToDouble list, int count)
	{
		return recursiveSum(list, 0, count);
	}
	
	public static double recursiveSum(double... inputs)
	{
		return recursiveSum(0, inputs.length, inputs);
	}
	
	public static double recursiveSum(List<Double> listOfDoubles)
	{
		return recursiveSum(listOfDoubles, 0, listOfDoubles.size());
	}
	
	public static double naiveSum(UnaryFunctionIntToDouble list, int count)
	{
		double s = 0;
		for (int i = 0; i < count; i++)
			s += list.f(i);
		return s;
	}
	
	public static double naiveSum(double... inputs)
	{
		double s = 0;
		for (double x : inputs)
			s += x;
		return s;
	}
	
	public static double naiveSum(Iterable<Double> iterableOfDoubles)
	{
		double s = 0;
		for (Double x : iterableOfDoubles)
			s += x;
		return s;
	}
	
	/**
	 * The spot on the circle between the points a and b! ^w^
	 * + Normalized here meaning everything is multiplied or divided such that the 'circle' is exactly 1.0d long (modulo 1)  ^_^
	 */
	public static double modularAverageNormalized(double a, double b)
	{
		return abs(a - b) <= abs(a+1 - b)
		? ((a+b)/2d)
		: (((a+1 + b)/2d) % 1);
	}
	
	public static double sq(double x)
	{
		return x*x;
	}
	
	public static double cube(double x)
	{
		return x*x*x;
	}
	
	public static double literalPower(double base, int exponent)
	{
		if (exponent < 0)
			throw new IllegalArgumentException();
		
		
		double exponentiation = 1;
		
		for (int i = 0; i < exponent; i++)
		{
			exponentiation *= base;
		}
		
		return exponentiation;
	}
	
	public static double deg2rad(double x)
	{
		return x * PI / 180;
	}
	
	public static double rev2rad(double x)
	{
		return x * PI2;
	}
	
	public static double rev2deg(double x)
	{
		return x * 360;
	}
	
	public static double deg2rev(double x)
	{
		return x / 360;
	}
	
	public static double cosr(double θrev)
	{
		θrev = progmod(θrev, 1);
		
		if (θrev == 0)
			return 1;
		
		else if (θrev == 0.25)
			return 0;
		
		else if (θrev == 0.5)
			return -1;
		
		else if (θrev == 0.75)
			return 0;
		
		else
			return cos(rev2rad(θrev));
	}
	
	public static double sinr(double θrev)
	{
		θrev = progmod(θrev, 1);
		
		if (θrev == 0)
			return 0;
		
		else if (θrev == 0.25)
			return 1;
		
		else if (θrev == 0.5)
			return 0;
		
		else if (θrev == 0.75)
			return -1;
		
		else
			return sin(rev2rad(θrev));
	}
	
	
	
	public static double tanr(double θrev)
	{
		θrev = progmod(θrev, 1);
		
		if (θrev == 0)
			return 0;
		
		if (θrev == 0.125)
			return 1;
		
		else if (θrev == 0.25)
			return Double.POSITIVE_INFINITY;
		
		if (θrev == 0.375)
			return -1;
		
		else if (θrev == 0.5)
			return 0;
		
		if (θrev == 0.625)
			return 1;
		
		else if (θrev == 0.75)
			return Double.NEGATIVE_INFINITY;
		
		if (θrev == 0.875)
			return -1;
		
		else
			return sinr(θrev) / cosr(θrev);
	}
	
	
	public static double cotr(double θrev)
	{
		θrev = progmod(θrev, 1);
		
		if (θrev == 0)
			return Double.POSITIVE_INFINITY;
		
		if (θrev == 0.125)
			return 1;
		
		else if (θrev == 0.25)
			return 0;
		
		if (θrev == 0.375)
			return -1;
		
		else if (θrev == 0.5)
			return Double.NEGATIVE_INFINITY;
		
		if (θrev == 0.625)
			return 1;
		
		else if (θrev == 0.75)
			return 0;
		
		if (θrev == 0.875)
			return -1;
		
		else
			return cosr(θrev) / sinr(θrev);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static double atanr(double yoverx)
	{
		if (yoverx == 0)
			return 0;
		
		if (yoverx == 1)
			return 0.125;
		
		else if (Double.isInfinite(yoverx))
			return 0.25;
		
		if (yoverx == -1)
			return 0.375;
		
		else
			return rad2rev(atan(yoverx));
	}
	
	
	public static double acotr(double yoverx)
	{
		if (yoverx == 0)
			return 0.25;
		
		if (yoverx == 1)
			return 0.125;
		
		else if (Double.isInfinite(yoverx))
			return 0;
		
		if (yoverx == -1)
			return 0.375;
		
		else
			return rad2rev(atan(1 / yoverx));
	}
	
	
	
	
	
	/**
	 * NOTE IT'S (Y,X) NOT (X,Y)
	 * 
	 * gawd who established that convention?  X'D
	 */
	public static double atanr2(double y, double x)
	{
		if (x == 0)
		{
			return y >= 0 ? 0.25 : 0.75;
		}
		
		if (y == 0)
		{
			return x >= 0 ? 0 : 0.5;
		}
		
		if (x == y)
		{
			return arbitrary(x, y) >= 0 ? 0.125 : 0.625;
		}
		
		if (-x == y)
		{
			return arbitrary(-x, y) >= 0 ? 0.375 : 0.875;
		}
		
		else
		{
			return rad2rev(atan2(y, x));
		}
	}
	
	
	
	
	/**
	 * NOTE IT'S (Y,X) NOT (X,Y)
	 * 
	 * gawd who established that convention?  X'D
	 */
	public static double acotr2(double y, double x)
	{
		return atanr2(x, y);  //wonderful :'3
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This is like doing <code>(int)round(t * (highest - lowest) + lowest)</code>,
	 * but with explicit bounds checks and guarantees it'll never return out of range even if there are floating point errors,
	 * handles NaN, and has optional bug reporting if given parameter is out of domain ([0,1]) :D
	 * 
	 * @param t a normalized value from [0, 1]  ^w^
	 */
	public static int denormalize(double t, int lowest, int highest, boolean strict)
	{
		if (isNaN(t))
		{
			logBug();
			return lowest;
		}
		
		//These checks work even for infinity! :D
		else if (t == 0)
			return lowest;
		
		else if (t == 1)
			return highest;
		
		
		else if (t < 0)
		{
			if (strict) logBug();
			return lowest;
		}
		
		else if (t > 1)
		{
			if (strict) logBug();
			return highest;
		}
		
		
		else
		{
			int v = (int)round(t * (highest - lowest) + lowest);
			
			//Just double-check!
			//(well really integer-check XD  *badum psst* ^^' )
			if (v > highest)
				return highest;
			else if (v < lowest)
				return lowest;
			else
				return v;
		}
	}
	
	/**
	 * This is like doing <code>(int)round(t * (highest - lowest) + lowest)</code>,
	 * but with explicit bounds checks and guarantees it'll never return out of range even if there are floating point errors,
	 * handles NaN :D
	 * 
	 * @param t a normalized value from [0, 1]  ^w^
	 */
	public static int denormalize(double t, int lowest, int highest)
	{
		return denormalize(t, lowest, highest, false);
	}
	//Very specific, yet mathematical things>
	
	public static double calcStddev(final List<Double> input, final UnaryFunctionIntToLong expectedValue)
	{
		//stddev = sqrt(sum((actual - expected)^2) / number)  ^_^
		
		int inputCount = input.size();
		
		//TODO Test the new summation formula things!!   Then use kahan sum or something >,>
		return sqrt(recursiveSum(new UnaryFunctionIntToDouble
		()
		{
			@Override
			public double f(int index)
			{
				double d = expectedValue.f(index) - safeCastDoubleToS64(input.get(index));
				return d*d;
			}
		}, inputCount) / inputCount);
	}
	
	public static double calcStddev(final UnaryFunctionIntToLong input, final UnaryFunctionIntToLong expectedValue, final int inputCount)
	{
		//stddev = sqrt(sum((actual - expected)^2) / number)  ^_^
		
		//TODO Test the new summation formula things!!   Then use kahan sum or something >,>
		return sqrt(recursiveSum(new UnaryFunctionIntToDouble
		()
		{
			@Override
			public double f(int index)
			{
				double d = expectedValue.f(index) - input.f(index);
				return d*d;
			}
		}, inputCount) / inputCount);
	}
	
	public static double calcStddev(final List<Double> input, final UnaryFunctionIntToDouble expectedValue)
	{
		//stddev = sqrt(sum((actual - expected)^2) / number)  ^_^
		
		int inputCount = input.size();
		
		//TODO Test the new summation formula things!!   Then use kahan sum or something >,>
		return sqrt(recursiveSum(new UnaryFunctionIntToDouble
		()
		{
			@Override
			public double f(int index)
			{
				double d = expectedValue.f(index) - safeCastDoubleToS64(input.get(index));
				return d*d;
			}
		}, inputCount) / inputCount);
	}
	
	public static double calcStddev(final UnaryFunctionIntToDouble input, final UnaryFunctionIntToDouble expectedValue, final int inputCount)
	{
		//stddev = sqrt(sum((actual - expected)^2) / number)  ^_^
		
		//TODO Test the new summation formula things!!   Then use kahan sum or something >,>
		return sqrt(recursiveSum(new UnaryFunctionIntToDouble
		()
		{
			@Override
			public double f(int index)
			{
				double d = expectedValue.f(index) - input.f(index);
				return d*d;
			}
		}, inputCount) / inputCount);
	}
	
	
	
	
	
	
	@ImplementationTransparency
	public static int powS32_float(int base, int exponent) throws ArithmeticException
	{
		//Degenerate and special cases
		if (exponent == 0)
		{
			if (base == 0) // 0^0
				throw new ArithmeticException("0^0 is undefined");
			else // x^0
				return 1;
		}
		else if (exponent < 0)
		{
			// x^(-p) is only >= 1 if x <= 1
			if (base == 0) // 0^n
				throw new OutOfDomainArithmeticException("Division by zero!  (x^(-p) = 1/(x^p); 0^(-p) = 1/(0^p) = 1/0");
			else if (base == 1) // 1^x
				return 1;
			else if (base == -1) // (-1)^n
				return exponent % 2 == 0 ? 1 : -1;
			else
				throw new TruncationException("x^(-P) is not an integer");
		}
		else
		{
			if (base == 0) // 0^p
				return 0;
			else if (base == 1) // 1^p
				return 1;
			else if (base < 0) // n^p
			{
				if (base == -2 && exponent == 31)
				{
					//We can't just do -(n^p) because it'd overflowwwww!!
					return Integer.MIN_VALUE;
				}
				else
				{
					return exponent % 2 == 0 ? pow(-base, exponent) : -pow(-base, exponent);
				}
			}
			else // p^p
			{
				double v = Math.pow(base, exponent);
				if (v <= 0)
					throw new AssertionError();
				if (v > Integer.MAX_VALUE)
					throw new OverflowException();
				
				return (int)v;
			}
		}
	}
	
	@ImplementationTransparency
	public static long powS64_float(long base, long exponent) throws ArithmeticException
	{
		//Degenerate and special cases
		if (exponent == 0)
		{
			if (base == 0) // 0^0
				throw new ArithmeticException("0^0 is undefined");
			else // x^0
				return 1;
		}
		else if (exponent < 0)
		{
			// x^(-p) is only >= 1 if x <= 1
			if (base == 0) // 0^n
				throw new ArithmeticException("Division by zero!  (x^(-p) = 1/(x^p); 0^(-p) = 1/(0^p) = 1/0");
			else if (base == 1) // 1^x
				return 1;
			else if (base == -1) // (-1)^n
				return exponent % 2 == 0 ? 1 : -1;
			else
				throw new TruncationException("x^(-P) is not an integer");
		}
		else
		{
			if (base == 0) // 0^p
				return 0;
			else if (base == 1) // 1^p
				return 1;
			else if (base < 0) // n^p
			{
				if (base == -2 && exponent == 31)
				{
					return Integer.MIN_VALUE;
				}
				else
				{
					return exponent % 2 == 0 ? pow(-base, exponent) : -pow(-base, exponent);
				}
			}
			else // p^p
			{
				double v = Math.pow(base, exponent);
				if (v <= 0)
					throw new AssertionError();
				if (v > Long.MAX_VALUE)
					throw new OverflowException();
				
				return (long)v;
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static byte safeCastSingleToS8(float input)
	{
		if (input > Byte.MAX_VALUE || input < Byte.MIN_VALUE)
			throw new OverflowException(input+" -> S8");
		return (byte)input;
	}
	
	public static short safeCastSingleToS16(float input)
	{
		if (input > Short.MAX_VALUE || input < Short.MIN_VALUE)
			throw new OverflowException(input+" -> S16");
		return (short)input;
	}
	
	public static char safeCastSingleToU16(float input)
	{
		if (input > Character.MAX_VALUE || input < Character.MIN_VALUE)
			throw new OverflowException(input+" -> S16");
		return (char)input;
	}
	
	public static int safeCastSingleToS32(float input)
	{
		if (input > Integer.MAX_VALUE || input < Integer.MIN_VALUE)
			throw new OverflowException(input+" -> S32");
		return (int)input;
	}
	
	public static long safeCastSingleToS64(float input)
	{
		if (input > Long.MAX_VALUE || input < Long.MIN_VALUE)
			throw new OverflowException(input+" -> S64");
		return (long)input;
	}
	
	public static byte safeCastDoubleToS8(double input)
	{
		if (input > Byte.MAX_VALUE || input < Byte.MIN_VALUE)
			throw new OverflowException(input+" -> S8");
		return (byte)input;
	}
	
	public static short safeCastDoubleToS16(double input)
	{
		if (input > Short.MAX_VALUE || input < Short.MIN_VALUE)
			throw new OverflowException(input+" -> S16");
		return (short)input;
	}
	
	public static char safeCastDoubleToU16(double input)
	{
		if (input > Character.MAX_VALUE || input < Character.MIN_VALUE)
			throw new OverflowException(input+" -> S16");
		return (char)input;
	}
	
	public static int safeCastDoubleToS32(double input)
	{
		if (input > Integer.MAX_VALUE || input < Integer.MIN_VALUE)
			throw new OverflowException(input+" -> S32");
		return (int)input;
	}
	
	public static long safeCastDoubleToS64(double input)
	{
		if (input > Long.MAX_VALUE || input < Long.MIN_VALUE)
			throw new OverflowException(input+" -> S64");
		return (long)input;
	}
	
	public static int safeCastIntegerValuedFloatingPointF32toS32(float x) throws OverflowException, TruncationException
	{
		if (isFloatingPointOutOfS32Bounds(x))
			throw new OverflowException();
		if (!isFloatingPointAnInteger(x))
			throw new TruncationException();
		return (int)x;
	}
	
	public static long safeCastIntegerValuedFloatingPointF32toS64(float x) throws OverflowException, TruncationException
	{
		if (isFloatingPointOutOfS64Bounds(x))
			throw new OverflowException();
		if (!isFloatingPointAnInteger(x))
			throw new TruncationException();
		return (long)x;
	}
	
	public static int safeCastIntegerValuedFloatingPointF64toS32(double x) throws OverflowException, TruncationException
	{
		if (isFloatingOutOfS32Bounds(x))
			throw new OverflowException();
		if (!isFloatingPointAnInteger(x))
			throw new TruncationException();
		return (int)x;
	}
	
	public static long safeCastIntegerValuedFloatingPointF64toS64(double x) throws OverflowException, TruncationException
	{
		if (isFloatingOutOfS64Bounds(x))
			throw new OverflowException();
		if (!isFloatingPointAnInteger(x))
			throw new TruncationException();
		return (long)x;
	}
	
	public static boolean isFloatingPointOutOfS32Bounds(float x)
	{
		//Todo use the logic in Unsigned.java??
		return x < Integer.MIN_VALUE || x > Integer.MAX_VALUE;
	}
	
	public static boolean isFloatingPointOutOfS64Bounds(float x)
	{
		//Todo use the logic in Unsigned.java??
		return x < Long.MIN_VALUE || x > Long.MAX_VALUE;
	}
	
	public static boolean isFloatingOutOfS32Bounds(double x)
	{
		return x < Integer.MIN_VALUE || x > Integer.MAX_VALUE;
	}
	
	public static boolean isFloatingOutOfS64Bounds(double x)
	{
		return x < Long.MIN_VALUE || x > Long.MAX_VALUE;
	}
	
	public static boolean isFloatingPointAnInteger(float x)
	{
		//Todo how to do this right??!
		return isFloatingPointPrimitive((double)x);
	}
	
	public static boolean isFloatingPointAnInteger(double x)
	{
		//Todo how to do this right??!
		return Math.floor(x) == x;
	}
	
	public static double rad2deg(double x)
	{
		return x * 180 / PI;
	}
	
	public static double rad2rev(double x)
	{
		return x / PI2;
	}
	
	public static boolean isFloatingPointPrimitive(Object x)
	{
		return x instanceof Float || x instanceof Double;
	}
	
	
	
	
	
	
	
	
	
	//In bits :33
	public static final int SizeOfSignificandInIEEE754Single = 23;
	public static final int SizeOfSignificandInIEEE754Double = 52;
	
	//In bits :33
	public static final int SizeOfCharacteristicInIEEE754Single = 8;
	public static final int SizeOfCharacteristicInIEEE754Double = 11;
	
	
	public static final int CharacteristicBiasInIEEE754Single = 127;  //(1 << (SizeOfCharacteristicInIEEE754Single - 1)) - 1;
	public static final int CharacteristicBiasInIEEE754Double = 1023;  //(1 << (SizeOfCharacteristicInIEEE754Double - 1)) - 1;
	
	public static final int SubnormalActualCharacteristicInIEEE754Single = -126;  //-CharacteristicBiasInIEEE754Single + 1 == 2 - (1 << (SizeOfCharacteristicInIEEE754Single - 1))
	public static final int SubnormalActualCharacteristicInIEEE754Double = -1022;  //-CharacteristicBiasInIEEE754Double + 1 == 2 - (1 << (SizeOfCharacteristicInIEEE754Double - 1))
	
	public static final int SubnormalPreCharacteristicInIEEE754 = 0;
	public static final int SubnormalPostCharacteristicInIEEE754Single = -CharacteristicBiasInIEEE754Single;
	public static final int SubnormalPostCharacteristicInIEEE754Double = -CharacteristicBiasInIEEE754Double;
	public static final int NonfinitePostCharacteristicInIEEE754Single = CharacteristicBiasInIEEE754Single+1;
	public static final int NonfinitePostCharacteristicInIEEE754Double = CharacteristicBiasInIEEE754Double+1;
	
	public static final int HighestPreSignificandBitInIEEE754Single = 4194304;  //1 << (SizeOfSignificandInIEEE754Single - 1)
	public static final long HighestPreSignificandBitInIEEE754Double = 2251799813685248l;  //1l << (SizeOfSignificandInIEEE754Double - 1)
	
	
	
	
	/*
	 * IEEE754 Single Precision format (in little bit-endian!):
	 * 			SSSSSSSS SSSSSSSS SSSSSSSC CCCCCCCN
	 * IEEE754 Double Precision format (in little bit-endian!):
	 * 			SSSSSSSS SSSSSSSS SSSSSSSS SSSSSSSS SSSSSSSS SSSSSSSS SSSSCCCC CCCCCCCN
	 * 
	 * C - PreCharacteristic bit
	 * S - PreSignificand bit
	 * N - Negative (sign) bit :3
	 * 
	 * In decoding,
	 * 		• Sign = N ? -1 : +1
	 * 		• Characteristic = (uint)PreCharacteristic - (bias = 2^(Nc-1) - 1)
	 * 		• PreSignificand is an Unsigned integer, it must be "divided" by 2^BitCount and unless subnormal, added to 1, to get the real Significand!
	 */
	
	
	
	/**
	 * Except for non-finite special values, the resulting mathematical value is:
	 * 		if (c != 0) Normal
	 * 			sign * (1+s/2^Ns) * 2^c
	 * 			 =
	 * 			sign * (2^c + s/2^(Ns - c))
	 * 
	 * 		else Subnormal!
	 * 			sign * (0+s/2^Ns) * 2^SNc
	 * 			 =
	 * 			sign * s / 2^(Ns - SNc)
	 * 
	 * 
	 * Ns = {@link #SizeOfSignificandInIEEE754Single}
	 * Nc = {@link #SizeOfCharacteristicInIEEE754Single}
	 * SNc = 2 - 2^Nc = {@link #SubnormalActualCharacteristicInIEEE754Single}
	 * 
	 * Special values decoding are:
	 * 		Nonfinite  =  c == 2^(Nc-1) = {@link #NonfinitePostCharacteristicInIEEE754Single}
	 * 		Infinity  =  Nonfinite && s == 0
	 * 			+ Sign determines if positive or negative! ;D
	 * 		NaN  =  Nonfinite && s != 0
	 * 
	 * @return {Sign (+1,-1), PreSignificand(s), PostCharacteristic(c)}  :DD
	 */
	public static int[] rawparseIEEE754SinglePrecision(float f)  //We can only hope this gets JITted and its returned array gets stack-allocated!!  :D
	{
		int bits = Float.floatToRawIntBits(f);
		
		
		int presignificand = bits & 0b00000000_01111111_11111111_11111111;
		int precharacteristic = (bits & 0b01111111_10000000_00000000_00000000) >>> SizeOfSignificandInIEEE754Single;
		int postcharacteristic = precharacteristic - CharacteristicBiasInIEEE754Single;
		
		boolean negative = (bits & 0b10000000_00000000_00000000_00000000) != 0;
		
		return new int[]{negative ? -1 : +1, presignificand, postcharacteristic};
	}
	
	
	
	
	/**
	 * Except for non-finite special values, the resulting mathematical value is:
	 * 		if (c != 0) Normal
	 * 			sign * (1+s/2^Ns) * 2^c
	 * 
	 * 		else Subnormal!
	 * 			sign * (0+s/2^Ns) * 2^SNc
	 * 			 =
	 * 			sign * s / 2^(Ns - SNc)
	 * 
	 * 
	 * Ns = {@link #SizeOfSignificandInIEEE754Double}
	 * Nc = {@link #SizeOfCharacteristicInIEEE754Double}
	 * SNc = 2 - 2^Nc = {@link #SubnormalActualCharacteristicInIEEE754Double}
	 * 
	 * Special values decoding are:
	 * 		Nonfinite  =  c == 2^(Nc-1) = {@link #NonfinitePostCharacteristicInIEEE754Double}
	 * 		Infinity  =  Nonfinite && s == 0
	 * 			+ Sign determines if positive or negative! ;D
	 * 		NaN  =  Nonfinite && s != 0
	 * 
	 * @return {Sign (+1,-1), PreSignificand(s), PostCharacteristic(c)}  :DD
	 */
	public static long[] rawparseIEEE754DoublePrecision(double f)  //We can only hope this gets JITted and its returned array gets stack-allocated!!  :D
	{
		long bits = Double.doubleToRawLongBits(f);
		
		
		long presignificand = bits & 0b00000000_00001111_11111111_11111111_11111111_11111111_11111111_11111111l;
		long precharacteristic = (bits & 0b01111111_11110000_00000000_00000000_00000000_00000000_00000000_00000000l) >>> SizeOfSignificandInIEEE754Double;
			long postcharacteristic = precharacteristic - CharacteristicBiasInIEEE754Double;
			
			boolean negative = (bits & 0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000l) != 0;
			
			return new long[]{negative ? -1 : +1, presignificand, postcharacteristic};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean isSubnormalFloatingPoint(float f)
	{
		int[] p = rawparseIEEE754SinglePrecision(f);
		int postcharacteristic = p[2];
		
		return postcharacteristic == SubnormalPostCharacteristicInIEEE754Single;
	}
	
	
	public static boolean _isFinite(float f)
	{
		int[] p = rawparseIEEE754SinglePrecision(f);
		int postcharacteristic = p[2];
		
		return postcharacteristic != NonfinitePostCharacteristicInIEEE754Single;
	}
	
	
	
	public static boolean _isPositiveInfinity(float f)
	{
		int[] p = rawparseIEEE754SinglePrecision(f);
		int sign = p[0];
		int presignificand = p[1];
		int postcharacteristic = p[2];
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Single && presignificand == 0 && sign == +1;
	}
	
	
	
	public static boolean _isNegativeInfinity(float f)
	{
		int[] p = rawparseIEEE754SinglePrecision(f);
		int sign = p[0];
		int presignificand = p[1];
		int postcharacteristic = p[2];
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Single && presignificand == 0 && sign == -1;
	}
	
	
	
	public static boolean _isInfinity(float f)
	{
		int[] p = rawparseIEEE754SinglePrecision(f);
		int presignificand = p[1];
		int postcharacteristic = p[2];
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Single && presignificand == 0;
	}
	
	
	
	public static boolean _isNaN(float f)
	{
		int[] p = rawparseIEEE754SinglePrecision(f);
		int presignificand = p[1];
		int postcharacteristic = p[2];
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Single && presignificand != 0;
	}
	
	
	
	public static boolean isQuietNaN(float f)
	{
		int[] p = rawparseIEEE754SinglePrecision(f);
		int presignificand = p[1];
		int postcharacteristic = p[2];
		
		boolean highestPresignificandBit = (presignificand & HighestPreSignificandBitInIEEE754Single) != 0;
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Single && highestPresignificandBit;  //&& presignificand != 0
	}
	
	
	
	public static boolean isSignallingNaN(float f)
	{
		int[] p = rawparseIEEE754SinglePrecision(f);
		int presignificand = p[1];
		int postcharacteristic = p[2];
		
		boolean highestPresignificandBit = (presignificand & HighestPreSignificandBitInIEEE754Single) != 0;
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Single && presignificand != 0 && !highestPresignificandBit;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean isSubnormalFloatingPoint(double f)
	{
		long[] p = rawparseIEEE754DoublePrecision(f);
		long postcharacteristic = p[2];
		
		return postcharacteristic == SubnormalPostCharacteristicInIEEE754Double;
	}
	
	
	public static boolean _isFinite(double f)
	{
		long[] p = rawparseIEEE754DoublePrecision(f);
		long postcharacteristic = p[2];
		
		return postcharacteristic != NonfinitePostCharacteristicInIEEE754Double;
	}
	
	
	
	public static boolean _isPositiveInfinity(double f)
	{
		long[] p = rawparseIEEE754DoublePrecision(f);
		long sign = p[0];
		long presignificand = p[1];
		long postcharacteristic = p[2];
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Double && presignificand == 0 && sign == +1;
	}
	
	
	
	public static boolean _isNegativeInfinity(double f)
	{
		long[] p = rawparseIEEE754DoublePrecision(f);
		long sign = p[0];
		long presignificand = p[1];
		long postcharacteristic = p[2];
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Double && presignificand == 0 && sign == -1;
	}
	
	
	
	public static boolean _isInfinity(double f)
	{
		long[] p = rawparseIEEE754DoublePrecision(f);
		long presignificand = p[1];
		long postcharacteristic = p[2];
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Double && presignificand == 0;
	}
	
	
	
	public static boolean _isNaN(double f)
	{
		long[] p = rawparseIEEE754DoublePrecision(f);
		long presignificand = p[1];
		long postcharacteristic = p[2];
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Double && presignificand != 0;
	}
	
	
	
	public static boolean isQuietNaN(double f)
	{
		long[] p = rawparseIEEE754DoublePrecision(f);
		long presignificand = p[1];
		long postcharacteristic = p[2];
		
		boolean highestPresignificandBit = (presignificand & HighestPreSignificandBitInIEEE754Double) != 0;
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Double && highestPresignificandBit;  //&& presignificand != 0
	}
	
	
	
	public static boolean isSignallingNaN(double f)
	{
		long[] p = rawparseIEEE754DoublePrecision(f);
		long presignificand = p[1];
		long postcharacteristic = p[2];
		
		boolean highestPresignificandBit = (presignificand & HighestPreSignificandBitInIEEE754Double) != 0;
		
		return postcharacteristic == NonfinitePostCharacteristicInIEEE754Double && presignificand != 0 && !highestPresignificandBit;
	}
	
	
	
	
	
	
	
	
	public static double relativeDifference(double a, double b)
	{
		if (isNaN(a) || isNaN(b))
			return Double.NaN;
		
		double d = (abs(a) + abs(b)) / 2;
		
		if (d == 0)
		{
			if (a == 0 && b == 0)
				return 0;
			else
				throw new AssertionError();
		}
		else
		{
			return (a - b) / d;
		}
	}
	
	
	public static float relativeDifference(float a, float b)
	{
		if (isNaN(a) || isNaN(b))
			return Float.NaN;
		
		float d = (abs(a) + abs(b)) / 2;
		
		if (d == 0)
		{
			if (a == 0 && b == 0)
				return 0;
			else
				throw new AssertionError();
		}
		else
		{
			return (a - b) / d;
		}
	}
	
	
	
	public static double relativeDifferenceAbs(double a, double b)
	{
		return abs(relativeDifference(a, b));
	}
	
	public static float relativeDifferenceAbs(float a, float b)
	{
		return abs(relativeDifference(a, b));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static float avg(float a, float b)
	{
		return (a + b) / 2;
	}
	
	public static float avg(float a, float b, float c)
	{
		return (a + b + c) / 3;
	}
	
	public static float avg(float a, float b, float c, float d)
	{
		return (a + b + c + d) / 4;
	}
	
	public static float avg(float a, float b, float c, float d, float e)
	{
		return (a + b + c + d + e) / 5;
	}
	
	
	public static float avg(float... input)
	{
		float sum = 0;
		for (float d : input)
			sum += d;
		return sum / input.length;
	}
	
	
	
	
	
	
	
	
	public static double avg(double a, double b)
	{
		return (a + b) / 2;
	}
	
	public static double avg(double a, double b, double c)
	{
		return (a + b + c) / 3;
	}
	
	public static double avg(double a, double b, double c, double d)
	{
		return (a + b + c + d) / 4;
	}
	
	public static double avg(double a, double b, double c, double d, double e)
	{
		return (a + b + c + d + e) / 5;
	}
	
	
	public static double avg(double... input)
	{
		double sum = 0;
		for (double d : input)
			sum += d;
		return sum / input.length;
	}
	
	
	public static double absdev(double avg, double... input)
	{
		double sum = 0;
		for (double d : input)
			sum += abs(d - avg);
		return sum / input.length;
	}
	
	
	public static double vardev(double avg, double... input)
	{
		double sum = 0;
		
		for (double d : input)
		{
			double diff = d - avg;
			sum += diff * diff;
		}
		
		return sum / input.length;
	}
	
	
	public static double stddev(double avg, double... input)
	{
		return sqrt(vardev(avg, input));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int roundNearzeroS32(float x)
	{
		//Todo overflow/NaN detection!
		return (int)x;
	}
	
	public static int roundNearzeroS32(double x)
	{
		//Todo overflow/NaN detection!
		return (int)x;
	}
	
	
	public static long roundNearzeroS64(float x)
	{
		//Todo overflow/NaN detection!
		return (long)x;
	}
	
	public static long roundNearzeroS64(double x)
	{
		//Todo overflow/NaN detection!
		return (long)x;
	}
	
	
	
	
	
	public static int roundFloorS32(float x)
	{
		//Todo overflow/NaN detection!
		return (int)Math.floor(x);
	}
	
	public static int roundFloorS32(double x)
	{
		//Todo overflow/NaN detection!
		return (int)Math.floor(x);
	}
	
	
	public static long roundFloorS64(float x)
	{
		//Todo overflow/NaN detection!
		return (long)Math.floor(x);
	}
	
	public static long roundFloorS64(double x)
	{
		//Todo overflow/NaN detection!
		return (long)Math.floor(x);
	}
	
	
	
	
	
	public static int roundCeilS32(float x)
	{
		//Todo overflow/NaN detection!
		return (int)Math.ceil(x);
	}
	
	public static int roundCeilS32(double x)
	{
		//Todo overflow/NaN detection!
		return (int)Math.ceil(x);
	}
	
	
	public static long roundCeilS64(float x)
	{
		//Todo overflow/NaN detection!
		return (long)Math.ceil(x);
	}
	
	public static long roundCeilS64(double x)
	{
		//Todo overflow/NaN detection!
		return (long)Math.ceil(x);
	}
	
	
	
	
	
	public static int roundClosestArbtiesS32(float x)
	{
		//Todo overflow/NaN detection!
		return Math.round(x);
	}
	
	public static int roundClosestArbtiesS32(double x)
	{
		return safeCastS64toS32(Math.round(x));
	}
	
	
	public static long roundClosestArbtiesS64(float x)
	{
		//Todo overflow detection!
		return Math.round((double)x);
	}
	
	public static long roundClosestArbtiesS64(double x)
	{
		//Todo overflow detection!
		return Math.round(x);
	}
	
	
	
	
	
	
	//Todo more truncating ones ^^''
	public static long roundTruncatingClosestArbtiesS64(double x)
	{
		requireNotNaN(x);
		return Math.round(x);
	}
	
	public static int roundTruncatingClosestArbtiesS32(double x)
	{
		requireNotNaN(x);
		long i = Math.round(x);
		i = SmallIntegerMathUtilities.greatest((long)Integer.MIN_VALUE, i);
		i = SmallIntegerMathUtilities.least((long)Integer.MAX_VALUE, i);
		return safeCastS64toS32(i);
	}
	
	
	public static int roundTruncatingClosestArbtiesS32(float x)
	{
		requireNotNaN(x);
		return Math.round(x);
	}
	
	
	
	
	
	
	
	
	
	public static final double Ln2 = Math.log(2);
	
	/**
	 * Floating point is in *binary*!  Come on this should be in the standard library--heck this should be the default hardware-implemented log that you make other logs from by ratios, shouldn't it!? X'D
	 */
	public static double log2(double x)
	{
		return Math.log(x) / Ln2;
	}
	
	public static double pow2(double x)
	{
		return Math.pow(2, x);
	}
	
	
	public static double log(double x, double base)
	{
		return Math.log(x) / Math.log(base);
	}
}
