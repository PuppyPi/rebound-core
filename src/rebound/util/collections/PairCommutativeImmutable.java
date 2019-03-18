package rebound.util.collections;

import rebound.util.objectutil.ObjectMethodDefaultsAdapterSuperclass;

public class PairCommutativeImmutable<E>
extends ObjectMethodDefaultsAdapterSuperclass
implements PairCommutative<E>
{
	protected final E a;
	protected final E b;
	
	public PairCommutativeImmutable(E a, E b)
	{
		this.a = a;
		this.b = b;
	}
	
	@Override
	public E getA()
	{
		return this.a;
	}
	
	@Override
	public E getB()
	{
		return this.b;
	}
}
