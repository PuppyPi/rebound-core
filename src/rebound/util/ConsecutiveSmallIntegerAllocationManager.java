package rebound.util;

import rebound.annotations.semantic.SignalType;

/**
 * A kind of {@link SmallIntegerAllocationManager} that is guaranteed to always allocate the lowest possible (non-negative) integer, regardless of what order {@link #allocateNew() allocates} and {@link #notifyFreed(int) frees} (and {@link #notifyUsed(int) etc.}) are done in or how fragmented the space is.
 */
@SignalType
public interface ConsecutiveSmallIntegerAllocationManager
extends SmallIntegerAllocationManager
{
}
