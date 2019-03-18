package rebound.util.collections;

@FunctionalInterface
public interface IndexAndElementProvidingRemove<E>
{
	public void remove(E lastReturnedElement, int indexOfLastReturnedElement);
}