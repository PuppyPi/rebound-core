package rebound.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.exceptions.AlreadyExistsException;

public class ListBackedSet<E>
extends SetifyingDecorator<E>
implements ListSet<E>
{
	public ListBackedSet()
	{
		this(new ArrayList<>());
	}
	
	public ListBackedSet(List<E> backing)
	{
		super(backing);
	}
	
	@Override
	public List<E> getBacking()
	{
		return (List<E>) super.getBacking();
	}
	
	
	
	
	public static <E> ListBackedSet<E> newSafe(@LiveValue List<E> backing)
	{
		if (new HashSet<>(backing).size() != backing.size())
			throw new AlreadyExistsException();
		
		return new ListBackedSet<>(backing);
	}
	
	public static <E> ListBackedSet<E> newUniqueifyingOP(@SnapshotValue Collection<E> backing)
	{
		List<E> newBacking = new ArrayList<>(backing.size());
		
		for (E e : backing)
			if (!newBacking.contains(e))
				newBacking.add(e);
		
		return new ListBackedSet<>(newBacking);
	}
}
