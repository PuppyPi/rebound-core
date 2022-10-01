package rebound.util.collections;

import static rebound.math.SmallIntegerMathUtilities.*;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import rebound.concurrency.blocks.ResizeableCapacityRestrictedCollection;
import rebound.exceptions.CapacityReachedException;

public class ResizeableCapacityRestrictedArrayDeque<E>
implements ResizeableCapacityRestrictedCollection<E>, Deque<E>
{
	protected Deque<E> underlying = new ArrayDeque<>();
	protected int capacity = Integer.MAX_VALUE;
	
	
	
	@Override
	public int getMaxCapacity()
	{
		return Integer.MAX_VALUE;  //just don't cause an out of memory error 8)''
	}
	
	@Override
	public int getCapacity()
	{
		return this.capacity;
	}
	
	@Override
	public void setCapacity(int capacity) throws IllegalArgumentException
	{
		requireNonNegative(capacity);
		
		if (capacity < size())
			throw new IllegalArgumentException();
		
		this.capacity = capacity;
	}
	
	
	
	
	
	@Override
	public boolean add(E e)
	{
		if (size() >= this.capacity)
			throw new CapacityReachedException();
		return this.underlying.add(e);
	}
	
	@Override
	public void addFirst(E e)
	{
		if (size() >= this.capacity)
			throw new CapacityReachedException();
		this.underlying.addFirst(e);
	}
	
	@Override
	public void addLast(E e)
	{
		if (size() >= this.capacity)
			throw new CapacityReachedException();
		this.underlying.addLast(e);
	}
	
	
	@Override
	public boolean offer(E e)
	{
		if (size() >= this.capacity)
			return false;
		return this.underlying.offer(e);
	}
	
	@Override
	public boolean offerFirst(E e)
	{
		if (size() >= this.capacity)
			return false;
		return this.underlying.offerFirst(e);
	}
	
	@Override
	public boolean offerLast(E e)
	{
		if (size() >= this.capacity)
			return false;
		return this.underlying.offerLast(e);
	}
	
	
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		if (size() + c.size() > this.capacity)  //not >= because if it reaches the capacity exactly that's fine X3
			throw new CapacityReachedException();
		return this.underlying.addAll(c);
	}
	
	
	
	
	
	
	
	
	
	/////////////////////// Pure delegates :3 ///////////////////////
	
	@Override
	public void forEach(Consumer<? super E> action)
	{
		this.underlying.forEach(action);
	}
	
	@Override
	public int size()
	{
		return this.underlying.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		return this.underlying.isEmpty();
	}
	
	@Override
	public boolean contains(Object o)
	{
		return this.underlying.contains(o);
	}
	
	@Override
	public E remove()
	{
		return this.underlying.remove();
	}
	
	@Override
	public E poll()
	{
		return this.underlying.poll();
	}
	
	@Override
	public Iterator<E> iterator()
	{
		return this.underlying.iterator();
	}
	
	@Override
	public E element()
	{
		return this.underlying.element();
	}
	
	@Override
	public E peek()
	{
		return this.underlying.peek();
	}
	
	@Override
	public Object[] toArray()
	{
		return this.underlying.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a)
	{
		return this.underlying.toArray(a);
	}
	
	@Override
	public boolean remove(Object o)
	{
		return this.underlying.remove(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c)
	{
		return this.underlying.containsAll(c);
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		return this.underlying.removeAll(c);
	}
	
	@Override
	public boolean removeIf(Predicate<? super E> filter)
	{
		return this.underlying.removeIf(filter);
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		return this.underlying.retainAll(c);
	}
	
	@Override
	public void clear()
	{
		this.underlying.clear();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this.underlying.equals(o);
	}
	
	@Override
	public int hashCode()
	{
		return this.underlying.hashCode();
	}
	
	@Override
	public Spliterator<E> spliterator()
	{
		return this.underlying.spliterator();
	}
	
	@Override
	public Stream<E> stream()
	{
		return this.underlying.stream();
	}
	
	@Override
	public Stream<E> parallelStream()
	{
		return this.underlying.parallelStream();
	}
	
	@Override
	public E removeFirst()
	{
		return this.underlying.removeFirst();
	}
	
	@Override
	public E removeLast()
	{
		return this.underlying.removeLast();
	}
	
	@Override
	public E pollFirst()
	{
		return this.underlying.pollFirst();
	}
	
	@Override
	public E pollLast()
	{
		return this.underlying.pollLast();
	}
	
	@Override
	public E getFirst()
	{
		return this.underlying.getFirst();
	}
	
	@Override
	public E getLast()
	{
		return this.underlying.getLast();
	}
	
	@Override
	public E peekFirst()
	{
		return this.underlying.peekFirst();
	}
	
	@Override
	public E peekLast()
	{
		return this.underlying.peekLast();
	}
	
	@Override
	public boolean removeFirstOccurrence(Object o)
	{
		return this.underlying.removeFirstOccurrence(o);
	}
	
	@Override
	public boolean removeLastOccurrence(Object o)
	{
		return this.underlying.removeLastOccurrence(o);
	}
	
	@Override
	public void push(E e)
	{
		this.underlying.push(e);
	}
	
	@Override
	public E pop()
	{
		return this.underlying.pop();
	}
	
	@Override
	public Iterator<E> descendingIterator()
	{
		return this.underlying.descendingIterator();
	}
}
