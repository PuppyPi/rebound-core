package rebound.util.collections.maps;

import static rebound.testing.WidespreadTestingUtilities.*;
import java.util.Collection;
import java.util.Iterator;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.util.collections.CollectionWithGetRandomElement;
import rebound.util.collections.RandomAccessIterator;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToInt;

/**
 * This doesn't imply that the ordering is consistent across method invocations or meaningful in any way!
 * Or that it's even the same ordering the {@link Collection#iterator() iterator}s will return in! (though it probably is)
 * But it's useful in many of the same ways that the arbitrary ordering of the iterator is and in ways it's not!—particularly pseudorandom random-access sampling such as used in unit testing of various things!
 */
@FunctionalityType
public interface IndexableCollection<E>
extends Collection<E>, CollectionWithGetRandomElement<E>
{
	/**
	 * @throws IndexOutOfBoundsException if index < 0 or index >= {@link #size()}
	 */
	public E getByIndex(int index) throws IndexOutOfBoundsException;
	
	/**
	 * @return the pre-existing value; ie, whatever {@link #getByIndex(int)} would have returned before this operation :3
	 */
	public default E removeByIndex(int index) throws IndexOutOfBoundsException
	{
		E e = getByIndex(index);
		asrt(remove(e));
		return e;
	}
	
	
	
	
	public static <E> Iterator<E> defaultIteratorForIndexableCollections(IndexableCollection<E> self)
	{
		return new RandomAccessIterator<E>()
		{
			@Override
			protected int getUnderlyingSize()
			{
				return self.size();
			}
			
			@Override
			protected void removeFromUnderlying(int index)
			{
				self.removeByIndex(index);
			}
			
			@Override
			protected E getFromUnderlying(int index)
			{
				return self.getByIndex(index);
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	//<<< tp IndexableCollection
	@TraitPredicate
	public default boolean isIndexableCollection()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof IndexableCollection && ((IndexableCollection)x).isIndexableCollection();
	}
	//>>>
	
	
	
	
	@Override
	public default E getRandomElement(UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound) throws IllegalStateException
	{
		return getByIndex(pullIntegerZeroToExclusiveHighBound.f(this.size()));
	}
	
	@Override
	public default boolean isCollectionWithGetRandomElement()
	{
		return true;
	}
}
