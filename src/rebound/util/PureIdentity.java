package rebound.util;

import rebound.annotations.semantic.operationspecification.IdentityHashableType;

/**
 * The purest form of Identity; just an object with nothing at all inside it but the fact that it's itself and is different from other ones!
 * This is useful as a vertex for graph-theory things :3
 * 
 * new {@link Object}() could be used instead, but this might as well be for documentation purposes, since you can use it as the type of a variable or generics parameter and it won't be misinterpreted as "anything" like {@link Object} would be!
 */
@IdentityHashableType
public final class PureIdentity
{
}
