package rebound.util.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import rebound.annotations.semantic.AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse;
import rebound.annotations.semantic.SignalType;
import rebound.exceptions.OverflowException;
import rebound.exceptions.StopIterationReturnPath;

@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
@FunctionalInterface
public interface SimpleIterator<E>
{
	public E nextrp() throws StopIterationReturnPath;
	
	
	
	
	//should really be <C super E> but that gives a compiler error for some reason???
	public default <C> C[] drainToNewArray(Class<C> componentType)
	{
		ArrayList<C> l = new ArrayList<>();  //should be stack-allocated following JIT :3!
		this.drainTo((List<? super E>)l);
		return l.toArray((C[])Array.newInstance(componentType, l.size()));
	}
	
	public default Object[] drainToNewArray()
	{
		ArrayList<Object> l = new ArrayList<>();  //should be stack-allocated following JIT :3!
		this.drainTo(l);
		return l.toArray();
	}
	
	
	
	public default int drainTo(Collection<? super E> sink)
	{
		int number = 0;
		
		while (true)
		{
			E e;
			{
				try
				{
					e = this.nextrp();
				}
				catch (StopIterationReturnPath e1)
				{
					break;
				}
			}
			
			sink.add(e);
			
			if (number == Integer.MAX_VALUE)
				throw new OverflowException();
			number++;
		}
		
		return number;
	}
	
	
	
	
	
	
	
	
	
	@SignalType //todo make trait predicate based :P
	public static interface SimpleIteratorWithRemove
	{
		public void remove();
	}
	
	
	
	
	
	
	
	@FunctionalInterface
	public static interface SimpleIterable<E>
	extends Iterable<E>
	{
		public SimpleIterator<E> simpleIterator();
		
		@Override
		public default Iterator<E> iterator()
		{
			return simpleIterator().toIterator();
		}
		
		
		
		
		
		public static final SimpleIterable<?> EmptySimpleIterable = () -> EmptySimpleIterator;
		
		
		//Todo simpleIterable(...) :>
		
		
		
		public static <D> SimpleIterable<D> simpleIterable(Iterable<D> iterable)
		{
			return new SimpleIterable<D>()
			{
				@Override
				public SimpleIterator<D> simpleIterator()
				{
					return SimpleIterator.simpleIterator(iterable.iterator());
				}
				
				@Override
				public Iterator<D> iterator()
				{
					return iterable.iterator();
				}
			};
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default Iterator<E> toIterator()
	{
		return defaultToIterator(this);
	}
	
	public static <E> Iterator<E> defaultToIterator(SimpleIterator<E> i)
	{
		return new Iterator<E>
		()
		{
			boolean eof;
			boolean hasBuff;
			E buff;
			
			
			@Override
			public boolean hasNext()
			{
				if (eof)
				{
					return false;
				}
				else
				{
					if (hasBuff)
					{
						return true;
					}
					else
					{
						try
						{
							buff = i.nextrp();
							hasBuff = true;
							return true;
						}
						catch (StopIterationReturnPath e)
						{
							eof = true;
							
							buff = null;  //for gc :3
							hasBuff = false;
							return false;
						}
					}
				}
			}
			
			
			@Override
			public E next()
			{
				if (eof)
					throw new NoSuchElementException();
				else
				{
					if (hasBuff)
					{
						E e = buff;
						hasBuff = false;
						buff = null;  //for gc :3
						return e;
					}
					else
					{
						throw new IllegalStateException("You should have called hasNext() before next()! D:      (I mean it might be fine this specific time, but it might not! \\o/    A broken clock might be right twice a day but you shouldn't write code that depends on twice-a-day-ness! )");
					}
				}
			}
			
			
			@Override
			public void remove()
			{
				((SimpleIteratorWithRemove)i).remove();
			}
		};
	}
	
	
	
	
	
	public static <E> SimpleIterator<E> toSimpleIterator(Iterator<E> i)
	{
		class InnerIteratorAdapter
		implements SimpleIterator<E>, SimpleIteratorWithRemove
		{
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				if (i.hasNext())
					return i.next();
				else
					throw StopIterationReturnPath.I;
			}
			
			@Override
			public void remove()
			{
				i.remove();
			}
		}
		
		return new InnerIteratorAdapter();
	}
	
	
	
	
	
	
	
	
	public static final SimpleIterator EmptySimpleIterator = () -> {throw StopIterationReturnPath.I;};
	
	
	public static <E> SimpleIterator<E> singletonSimpleIterator(E onlyElement)
	{
		return new SimpleIterator<E>
		()
		{
			boolean consumed = false;
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				if (consumed)
				{
					throw StopIterationReturnPath.I;
				}
				else
				{
					consumed = true;
					return onlyElement;
				}
			}
		};
	}
	
	
	
	public static SimpleIterator simpleIterator(Object x)
	{
		if (x instanceof SimpleIterable)
			return ((SimpleIterable)x).simpleIterator();
		else if (x instanceof Object[])
			return simpleIterator((Object[])x);
		else if (x instanceof Iterable)
			return SimpleIterator.toSimpleIterator(((Iterable)x).iterator());
		else
			if (x == null) throw new NullPointerException();
			else throw new ClassCastException(x.getClass().getName());
	}
	
	public static <E> SimpleIterator<E> simpleIterator(Iterable<E> x)
	{
		if (x instanceof SimpleIterable)
			return ((SimpleIterable<E>)x).simpleIterator();
		else
			return SimpleIterator.toSimpleIterator(x.iterator());
	}
	
	
	public static <E> SimpleIterator<E> simpleIterator(E... array)
	{
		return new SimpleIterator<E>
		()
		{
			int i = 0;
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				if (i >= array.length)
				{
					throw StopIterationReturnPath.I;
				}
				else
				{
					E e = array[i];
					i++;
					return e;
				}
			}
		};
	}
	
	
	public static <E> SimpleIterator<E> simpleIterator(E[] array, int offset, int length)
	{
		if (offset < 0) throw new IllegalArgumentException();
		if (length < 0) throw new IllegalArgumentException();
		if (offset+length > array.length) throw new IndexOutOfBoundsException();
		
		int bound = offset+length;
		
		return new SimpleIterator<E>
		()
		{
			int i = offset;
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				if (i >= bound)
				{
					throw StopIterationReturnPath.I;
				}
				else
				{
					E e = array[i];
					i++;
					return e;
				}
			}
		};
	}
}
