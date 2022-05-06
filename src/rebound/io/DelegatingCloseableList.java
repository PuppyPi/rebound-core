package rebound.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class DelegatingCloseableList<E>
implements CloseableList<E>
{
	protected final Closeable closeable;
	protected final List<E> underlying;
	
	public DelegatingCloseableList(Closeable closeable, List<E> underlying)
	{
		this.closeable = closeable;
		this.underlying = underlying;
	}
	
	public void close() throws IOException
	{
		closeable.close();
	}
	
	
	
	public boolean equals(Object o)
	{
		return underlying.equals(o);
	}
	
	public int hashCode()
	{
		return underlying.hashCode();
	}
	
	@Override
	public String toString()
	{
		return underlying.toString();
	}
	
	
	public void forEach(Consumer<? super E> action)
	{
		underlying.forEach(action);
	}
	
	public int size()
	{
		return underlying.size();
	}
	
	public boolean isEmpty()
	{
		return underlying.isEmpty();
	}
	
	public boolean contains(Object o)
	{
		return underlying.contains(o);
	}
	
	public Iterator<E> iterator()
	{
		return underlying.iterator();
	}
	
	public Object[] toArray()
	{
		return underlying.toArray();
	}
	
	public <T> T[] toArray(T[] a)
	{
		return underlying.toArray(a);
	}
	
	public boolean add(E e)
	{
		return underlying.add(e);
	}
	
	public boolean remove(Object o)
	{
		return underlying.remove(o);
	}
	
	public boolean containsAll(Collection<?> c)
	{
		return underlying.containsAll(c);
	}
	
	public boolean addAll(Collection<? extends E> c)
	{
		return underlying.addAll(c);
	}
	
	public boolean addAll(int index, Collection<? extends E> c)
	{
		return underlying.addAll(index, c);
	}
	
	public boolean removeAll(Collection<?> c)
	{
		return underlying.removeAll(c);
	}
	
	public boolean retainAll(Collection<?> c)
	{
		return underlying.retainAll(c);
	}
	
	public void replaceAll(UnaryOperator<E> operator)
	{
		underlying.replaceAll(operator);
	}
	
	public boolean removeIf(Predicate<? super E> filter)
	{
		return underlying.removeIf(filter);
	}
	
	public void sort(Comparator<? super E> c)
	{
		underlying.sort(c);
	}
	
	public void clear()
	{
		underlying.clear();
	}
	
	public E get(int index)
	{
		return underlying.get(index);
	}
	
	public E set(int index, E element)
	{
		return underlying.set(index, element);
	}
	
	public void add(int index, E element)
	{
		underlying.add(index, element);
	}
	
	public Stream<E> stream()
	{
		return underlying.stream();
	}
	
	public E remove(int index)
	{
		return underlying.remove(index);
	}
	
	public Stream<E> parallelStream()
	{
		return underlying.parallelStream();
	}
	
	public int indexOf(Object o)
	{
		return underlying.indexOf(o);
	}
	
	public int lastIndexOf(Object o)
	{
		return underlying.lastIndexOf(o);
	}
	
	public ListIterator<E> listIterator()
	{
		return underlying.listIterator();
	}
	
	public ListIterator<E> listIterator(int index)
	{
		return underlying.listIterator(index);
	}
	
	public List<E> subList(int fromIndex, int toIndex)
	{
		return underlying.subList(fromIndex, toIndex);
	}
	
	public Spliterator<E> spliterator()
	{
		return underlying.spliterator();
	}
}
