package rebound.util.collections;

import rebound.annotations.semantic.SignalType;
import rebound.util.Either;
import rebound.util.functional.FunctionInterfaces.UnaryFunction;

@SignalType
public interface CollectionWithRemoveIfWithStopSignal<E>
{
	public boolean removeIfWithStopSignal(UnaryFunction<? super E, Either<Boolean, Boolean>> filter);
}
