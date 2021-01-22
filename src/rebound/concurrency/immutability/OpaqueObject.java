package rebound.concurrency.immutability;

import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.operationspecification.IdentityHashableType;
import rebound.util.collections.CollectionUtilities;
import rebound.util.objectutil.Equivalenceable;

/**
 * Something which is compared in equality by its identity (like the default implementation of {@link Object#equals(Object)} / {@link Object#hashCode()})
 * And for which the contents are not used in comparing equivalence (in {@link Equivalenceable} / {@link CollectionUtilities#eqv(Object, Object)})
 * 
 * For example, a TCP Socket or File Handle or SQL Database Connection or GUI Window or Game Sprite, etc.c.
 * Something that's not a data-ish object like a String or Int or Boolean is.
 */
@IdentityHashableType
public interface OpaqueObject
extends Equivalenceable
{
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	@Override
	public int hashCode();
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	@Override
	public boolean equals(Object obj);
	
	
	@Override
	public default boolean equivalent(Object other)
	{
		return equals(other);
	}
	
	
	@Override
	public default int hashCodeOfContents()
	{
		return hashCode();
	}
}
