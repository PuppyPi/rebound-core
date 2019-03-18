package rebound.util.functional;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface CollectionFunctionalIterator<E>
{
	@Nonnull
	public ContinueSignal observe(E element);
}
