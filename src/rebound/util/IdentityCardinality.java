package rebound.util;

import java.util.Collection;
import java.util.Set;

/**
 * Cardinality = No negatives (eg, {@link Set#size()} :3
 * Identity = The identities (additive identity = zero, multiplicative identity = one, ehh it's not a numerical identity so it's not important = everything else XD )
 * 
 * Consider the reason {@link Collection#isEmpty()} exists when {@link Collection#size()} already exists.
 * This is like that, but instead of zero-notzero, it's zero-one-noteither :> 
 * It's the return value for a slightly more informative version of isEmpty(), identityCardinality()  :>
 */
public enum IdentityCardinality
{
	Zero,
	One,
	Multiple,
}
