package rebound.util;

public class Either3<A, B, C>
{
	public static <A, B, C> Either3<A, B, C> forA(A value)
	{
		return new Either3<>((byte)0, value);
	}
	
	public static <A, B, C> Either3<A, B, C> forB(B value)
	{
		return new Either3<>((byte)1, value);
	}
	
	public static <A, B, C> Either3<A, B, C> forC(C value)
	{
		return new Either3<>((byte)2, value);
	}
	
	
	
	protected final byte i;
	protected final Object value;
	
	protected Either3(byte i, Object value)
	{
		this.i = i;
		this.value = value;
	}
	
	
	
	public boolean isA()
	{
		return i == 0;
	}
	
	public boolean isB()
	{
		return i == 1;
	}
	
	public boolean isC()
	{
		return i == 2;
	}
	
	
	
	public Object getValue()
	{
		return value;
	}
	
	
	
	public A getValueIfA() throws NotThisException
	{
		if (isB())
			throw new NotThisException();
		else
			return (A)getValue();
	}
	
	public B getValueIfB() throws NotThisException
	{
		if (isB())
			return (B)getValue();
		else
			throw new NotThisException();
	}
	
	public C getValueIfC() throws NotThisException
	{
		if (isC())
			return (C)getValue();
		else
			throw new NotThisException();
	}
}
