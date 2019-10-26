package rebound.math;

import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.util.functional.FunctionInterfaces.UnaryProcedureLong;

@FunctionalInterface
public interface PlaceValueEncodingAlgorithm
{
	public void encode(@ActuallyUnsigned long value, @ActuallyUnsigned long radix, UnaryProcedureLong digitOutputted);
}
