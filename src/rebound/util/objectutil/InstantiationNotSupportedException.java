package rebound.util.objectutil;

/**
 * Analogous to {@link CloneNotSupportedException} :>
 * @author RProgrammer
 */
public class InstantiationNotSupportedException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public InstantiationNotSupportedException()
	{
	}
	
	public InstantiationNotSupportedException(String message)
	{
		super(message);
	}
	
	public InstantiationNotSupportedException(Throwable cause)
	{
		super(cause);
	}
	
	public InstantiationNotSupportedException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
	/**
	 * Specific type of instantiation isn't supported, the one which provides no input (the one we do here)
	 * aka, no-args
	 * :>
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static class InputlessInstantiationNotSupportedException
	extends InstantiationNotSupportedException
	{
		private static final long serialVersionUID = 1L;
		
		public InputlessInstantiationNotSupportedException()
		{
		}
		
		public InputlessInstantiationNotSupportedException(String message)
		{
			super(message);
		}
		
		public InputlessInstantiationNotSupportedException(Throwable cause)
		{
			super(cause);
		}
		
		public InputlessInstantiationNotSupportedException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
}