package rebound.util;

public class Either<A, B>
{
	public static <A, B> Either<A, B> forA(A value)
	{
		return new Either<>(false, value);
	}
	
	public static <A, B> Either<A, B> forB(B value)
	{
		return new Either<>(true, value);
	}
	
	
	
	protected final boolean b;
	protected final Object value;
	
	protected Either(boolean b, Object value)
	{
		this.b = b;
		this.value = value;
	}
	
	
	
	public boolean isA()
	{
		return !isB();
	}
	
	public boolean isB()
	{
		return b;
	}
	
	public Object getValue()
	{
		return value;
	}
	
	
	public A getValueIfA()
	{
		if (isB())
			throw new NotThisException();
		else
			return (A)getValue();
	}
	
	public B getValueIfB()
	{
		if (isB())
			return (B)getValue();
		else
			throw new NotThisException();
	}
	
	
	
	
	
	
	public static class NotThisException
	extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
	}
}
