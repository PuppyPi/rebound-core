package rebound.concurrency.immutability;

import rebound.util.objectutil.ObjectUtilities;

/**
 * Note: {@link ObjectUtilities#isConcurrentlyImmutable(Object) concurrency-grade immutability} implies {@link rebound.util.objectutil.ObjectUtilities#isThreadUnsafelyImmutable(Object) thread-unsafe immutability} :>
 * so if you implement {@link StaticallyConcurrentlyImmutable}, you doesn't have to implement this! ^_^
 */
public interface StaticallyThreadUnsafelyImmutable
{
}
