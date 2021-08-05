package rebound.util.collections;

import static rebound.util.collections.CollectionUtilities.*;
import java.util.List;
import javax.annotation.Nullable;

//TODO fast iterator for long singly-linked lists!

public class SinglyPreLinkedReadonlyList<E>
implements DefaultReadonlyList<E>
{
	protected final @Nullable E head;
	protected final List<E> tail;
	protected int size;
	
	public SinglyPreLinkedReadonlyList(E head, List<E> tail)
	{
		this.head = head;
		this.tail = tail;
		this.size = 0;  //a cache :3
	}
	
	public SinglyPreLinkedReadonlyList(E head, List<E> tail, int size)
	{
		this.head = head;
		this.tail = tail;
		this.size = size;
	}
	
	
	@Override
	public int size()
	{
		int s = size;
		if (s == 0)
		{
			s = 1 + tail.size();
			size = s;
		}
		return s;
	}
	
	@Override
	public boolean isEmpty()
	{
		return false;
	}
	
	@Override
	public E get(int index)
	{
		return index == 0 ? head : tail.get(index - 1);
	}
	
	
	
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof List && defaultListsEquivalent(this, (List)obj);
	}
	
	@Override
	public int hashCode()
	{
		return defaultListHashCode(this);
	}
	
	@Override
	public String toString()
	{
		return _toString();
	}
}
