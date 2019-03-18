package rebound.util.collections;

import java.util.List;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.SignalInterface;

@SignalInterface
public interface ListWithSetAll<E>
{
	/**
	 * Please use {@link CollectionUtilities#listcopy(List, int, List, int, int)} and similar instead of invoking this directly, since, if one or the other or both lists are {@link List#subList(int, int) sublists} or the other or the same one, then this could utterly corrupt the data being copied X'D
	 * listcopy() handles that properly so every implementation of {@link ListWithSetAll} doesn't have to ^^'
	 */
	@ImplementationTransparency
	public void setAll(int destIndex, List<? extends E> source, int sourceIndex, int amount) throws IndexOutOfBoundsException;
	
	public default void setAll(int destIndex, List<? extends E> source) throws IndexOutOfBoundsException
	{
		setAll(destIndex, source, 0, source.size());
	}
	
	
	
	
	
	
	
	//Let's just always use the static method listcopy(), how about?  XD
	//
	//	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	//	public default void setAll(int destIndex, List<? extends E> source) throws IndexOutOfBoundsException
	//	{
	//		setAll(destIndex, source, 0, source.size());
	//	}
	//
	//	@SignalInterface
	//	public static interface DefaultListWithSetAll<E>
	//	extends ListWithSetAll<E>
	//	{
	//		@ImplementationTransparency
	//		public void setAllNoWorriesAboutSublists(int destIndex, List<? extends E> source, int sourceIndex, int amount) throws IndexOutOfBoundsException;
	//
	//
	//		@Override
	//		public default void setAll(int destIndex, List<? extends E> source, int sourceIndex, int amount) throws IndexOutOfBoundsException
	//		{
	//			if (source instanceof Sublist)
	//			{
	//				//This is important for IF THE SUBLIST IS ONE OF *OUR* SUBLISTS!! X'D
	//				Sublist s = (Sublist) source;
	//				setAllNoWorriesAboutSublists(destIndex, s.getUnderlying(), s.getSublistStartingIndex(), source.size());
	//			}
	//			else
	//			{
	//				setAllNoWorriesAboutSublists(destIndex, source, sourceIndex, amount);
	//			}
	//		}
	//	}
}
