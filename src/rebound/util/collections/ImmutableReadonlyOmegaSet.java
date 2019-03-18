package rebound.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import rebound.concurrency.immutability.StaticallyConcurrentlyImmutable;
import rebound.exceptions.OverflowException;
import rebound.exceptions.ReadonlyUnsupportedOperationException;
import rebound.util.objectutil.SingletonEnum;
import rebound.util.objectutil.StaticallyIdentityless;

/**
 * It's an enum because it doesn't have to extend any classes, is immutable and single-valued,
 * and so we can make it an enum and get serialization-proof singletonness for free! 8>
 * 
 * @author Puppy Pie ^_^
 */
public enum ImmutableReadonlyOmegaSet
implements Set, StaticallyConcurrentlyImmutable,
StaticallyIdentityless, //it's just a set after all! :>   And it's *equivalent* to other omega sets! ^w^
SingletonEnum
{
	I,
	;
	
	
	@Override
	public boolean contains(Object o)
	{
		//Contains everything.  it's the omega set B)   XD
		return true;
	}
	
	@Override
	public boolean containsAll(Collection c)
	{
		//Contains everything.  it's the omega set B)   XD
		return true;
	}
	
	
	@Override
	public int size()
	{
		//Oh..that's not going to work..  B)  XD
		throw new OverflowException();
	}
	
	@Override
	public boolean isEmpty()
	{
		//...WELL IT'S DEFINITELY NOT EMPTY THAT'S FOR SURE!! XD!!
		return false;
	}
	
	@Override
	public Iterator iterator()
	{
		//Oh..my...that would be a bad idea! 0,0  ..let's just throw an exception XD' ^^'
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object[] toArray()
	{
		//Oh..that's not going to work..  B)  XD
		throw new OverflowException();
	}
	
	@Override
	public Object[] toArray(Object[] a)
	{
		//Oh..that's not going to work..  B)  XD
		throw new OverflowException();
	}
	
	
	
	
	
	//Readonly omega set :3  XD!
	@Override
	public boolean add(Object e)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Object o)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(Collection c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void clear()
	{
		throw new ReadonlyUnsupportedOperationException();
	}
}