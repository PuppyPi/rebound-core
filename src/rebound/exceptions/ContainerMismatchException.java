/*
 * Created on Oct 25, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * For when the owner of something doesn't match!
 * (eg, you have a map thing with keys that are inner classes of it, and has methods (say, get(Mapthing.Key)) which take *any* key instance, not just ones whose owner/enclosing-instance is the same one the method was called on!  --this is the exception for that ^_^ )
 * 
 * @author Puppy Pie ^_^
 */
public class ContainerMismatchException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public ContainerMismatchException()
	{
	}
	
	public ContainerMismatchException(String message)
	{
		super(message);
	}
	
	public ContainerMismatchException(Throwable cause)
	{
		super(cause);
	}
	
	public ContainerMismatchException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
	
	
	public static class LiteralJavaEnclosingInstanceOwnerMismatchException
	extends ContainerMismatchException
	{
		private static final long serialVersionUID = 1L;
		
		public LiteralJavaEnclosingInstanceOwnerMismatchException()
		{
		}
		
		public LiteralJavaEnclosingInstanceOwnerMismatchException(String message, Throwable cause)
		{
			super(message, cause);
		}
		
		public LiteralJavaEnclosingInstanceOwnerMismatchException(String message)
		{
			super(message);
		}
		
		public LiteralJavaEnclosingInstanceOwnerMismatchException(Throwable cause)
		{
			super(cause);
		}
	}
}
