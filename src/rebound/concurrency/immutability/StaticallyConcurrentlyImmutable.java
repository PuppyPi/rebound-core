package rebound.concurrency.immutability;

import rebound.util.objectutil.Copyable;

/**
 * Anything which is {@link StaticallyConcurrentlyImmutable} MUST BE IMMUTABLE. XD
 * But srsly, as in, concurrency-wise!
 * 
 * As in, all fields final, synchronization never required (on this object at least; the objects inside it stand on their own), cloneing can just return the same object, etc. etc. etc.
 * 
 * It should be some kind of error for anything to implement {@link Copyable} and {@link StaticallyConcurrentlyImmutable} XD
 * 
 * @author RProgrammer
 */
public interface StaticallyConcurrentlyImmutable
{
}
