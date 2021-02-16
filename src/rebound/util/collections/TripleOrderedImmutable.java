package rebound.util.collections;

import rebound.util.objectutil.ObjectMethodDefaultsAdapterSuperclass;

public class TripleOrderedImmutable<A, B, C>
extends ObjectMethodDefaultsAdapterSuperclass
implements TripleOrdered<A, B, C>
{
	protected final A a;
	protected final B b;
	protected final C c;
	
	public TripleOrderedImmutable(A a, B b, C c)
	{
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	@Override
	public A getA()
	{
		return this.a;
	}
	
	@Override
	public B getB()
	{
		return this.b;
	}
	
	@Override
	public C getC()
	{
		return this.c;
	}
}
