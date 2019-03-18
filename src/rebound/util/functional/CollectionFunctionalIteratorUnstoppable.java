package rebound.util.functional;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface CollectionFunctionalIteratorUnstoppable<E>
extends CollectionFunctionalIterator<E>
{
	@Nonnull
	public void observeUnstoppable(E element);
	
	
	@Override
	public default ContinueSignal observe(E element)
	{
		observeUnstoppable(element);
		return ContinueSignal.Continue;
	}
}
