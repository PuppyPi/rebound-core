package rebound.util.collections;

import rebound.util.objectutil.ObjectMethodDefaultsAdapterSuperclass;

public class PairOrderedImmutable<A, B>
extends ObjectMethodDefaultsAdapterSuperclass
implements PairOrdered<A, B>
{
	protected final A a;
	protected final B b;
	
	public PairOrderedImmutable(A a, B b)
	{
		this.a = a;
		this.b = b;
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
}
