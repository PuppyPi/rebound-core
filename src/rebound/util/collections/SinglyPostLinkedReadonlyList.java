package rebound.util.collections;

import static rebound.util.collections.CollectionUtilities.*;
import java.util.List;
import javax.annotation.Nullable;

public class SinglyPostLinkedReadonlyList<E>
implements DefaultReadonlyList<E>
{
	protected final List<E> head;
	protected final @Nullable E tail;
	protected int size;
	
	
	public SinglyPostLinkedReadonlyList(List<E> head, E tail)
	{
		this.head = head;
		this.tail = tail;
		this.size = 0;  //a cache :3
	}
	
	public SinglyPostLinkedReadonlyList(List<E> head, E tail, int size)
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
			s = head.size() + 1;
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
		return index == size()-1 ? tail : head.get(index);
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
