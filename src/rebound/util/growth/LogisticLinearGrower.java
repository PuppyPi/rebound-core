/*
 * Created on Sep 12, 2011
 * 	by the great Eclipse(c)
 */
package rebound.util.growth;

import static java.lang.Math.*;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.util.growth.Grower.GrowerComputationallyReduced;

/**
 * Use a linear-logistic curve, which approximates an exponential function at first, then smoothly tapers off just as quickly to approximate a linear function.
 * (in other words, it's for use when you want a grower that starts out growing really (exponentially) rapidly (eg, capacity*1.4), but doesn't get toooo big, and **smoothly** falls back to a linear growth rate (eg, capacity+128).    :>  )
 * 
 * Parameters:
 * 		y0 - Initial value (at iteration 0)
 * 		b - Base of approximated exponential function
 * 		m - Slope of approximated linear function
 * 
 * 		k (a parameter of the logistic function term) can be set with the following:
 * 		y2 - Value at which logistic term's slope = m
 * 
 * At low x, the graph approximates y=b^x+mx+y0  (an (asymptotically) exponential function :> )
 * At high x, especially once y reaches y2, the graph approximates y=k+mx+y0  (a linear function :> )
 * 
 * + Note: this grower ignores the oldSize variable, being a predictable mathematical model.
 */
public class LogisticLinearGrower
implements GrowerComputationallyReduced
{
	protected final double b, m, k;
	protected final int y0m1;
	
	
	protected LogisticLinearGrower(int y0m1, double b, double m, double k)
	{
		this.b = b;
		this.m = m;
		this.k = k;
		this.y0m1 = y0m1;
	}
	
	public static LogisticLinearGrower inst1(int y0, double b, double m, double k)
	{
		return new LogisticLinearGrower(y0-1, b, m, k);
	}
	
	public static LogisticLinearGrower inst2(int y0, double b, double m, double y2)
	{
		//k = 2*y1
		//k = y2/(1-m/(log(b)*y2))
		
		//Adjust for y-intercept
		//this.k = (y2-y0-1)/(1-m/(Math.log(b)*(y2-y0-1)));
		
		double s = y2-y0-1;
		double k = s/(1-m/(Math.log(b)*s));
		
		return inst1(y0, b, m, k);
	}
	
	
	
	
	
	/**
	 * + Note: 'oldsize' is always ignored, just reducedly dependent on the iteration index ^^'
	 */
	@Override
	public int getNewSizeReduced(int iteration)
	{
		//Logistic function: y = k/((k-1)/b^x+1)
		//Linear function y = mx
		//Intercept function y = y0
		//Total function y = k/((k-1)/b^x+1) + mx + y0
		
		if (iteration == 0)
		{
			return 1 + this.y0m1;
		}
		else
		{
			return ((int)Math.ceil(getNewSizeReducedFloatPart(iteration)))  +  this.y0m1;
		}
	}
	
	
	@ImplementationTransparency
	public double getNewSizeReducedFloatPart(double x)
	{
		return this.k / ((this.k-1) / Math.pow(this.b, x) + 1)  +  this.m*x;
	}
	
	
	
	
	/**
	 * This is for tests and such X3
	 * To determine if a result is so close to being rounded differently that it likely will be different on a different CPU X'D
	 */
	@ImplementationTransparency
	public double getInternalDiscrepancy(double x)
	{
		double v = getNewSizeReducedFloatPart(x);
		int i = (int) Math.ceil(v);
		double iv = i;
		
		return abs(v - iv);
	}
	
	
	
	
	
	
	public double getB()
	{
		return this.b;
	}
	
	public double getM()
	{
		return this.m;
	}
	
	public int getY0()
	{
		return this.y0m1;
	}
	
	public double getK()
	{
		return this.k;
	}
	
	//Todo getY2()  recalculating from k  ^^'
}
