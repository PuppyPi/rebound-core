/*
 * Created on Jan 9, 2009
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * eg, log(0), 1/0, 0^-1, 0^0, ..  ^w^
 * 
 * @author Puppy Pie ^_^
 */
public class OutOfDomainArithmeticException
extends ArithmeticException
{
	private static final long serialVersionUID = 1L;
	
	public OutOfDomainArithmeticException()
	{
	}
	
	public OutOfDomainArithmeticException(String s)
	{
		super(s);
	}
	
	
	
	/**
	 * Ie, it's in the domain...of complex/imaginary algebra!
	 * but not of the code that threw this exception.
	 * ^^''
	 * 
	 * eg, sqrt(-1) ^w^
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static class ComplexNumberArithmeticException
	extends OutOfDomainArithmeticException
	{
		private static final long serialVersionUID = 1L;
		
		public ComplexNumberArithmeticException()
		{
		}
		
		public ComplexNumberArithmeticException(String s)
		{
			super(s);
		}
	}
	
	
	/**
	 * Ie, it's in the domain...of (a) hyperreal algebra!
	 * but not of the code that threw this exception.
	 * ^^''
	 * 
	 * eg, lim x->0+  [ 1/x ]
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static class HyperrealArithmeticException
	extends OutOfDomainArithmeticException
	{
		private static final long serialVersionUID = 1L;
		
		public HyperrealArithmeticException()
		{
		}
		
		public HyperrealArithmeticException(String s)
		{
			super(s);
		}
	}
}
