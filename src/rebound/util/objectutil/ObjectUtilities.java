/*
 * Created on May 19, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.objectutil;

import static rebound.concurrency.immutability.JavaImmutability.*;
import static rebound.util.BasicExceptionUtilities.*;
import static rebound.util.Primitives.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.purelyforhumans.DeprecatedInFavorOfMember;
import rebound.annotations.semantic.AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse;
import rebound.concurrency.immutability.JavaImmutability;
import rebound.concurrency.immutability.StaticallyConcurrentlyImmutable;
import rebound.concurrency.immutability.StaticallyMutable;
import rebound.concurrency.immutability.StaticallyThreadUnsafelyImmutable;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.ReturnPath.SingletonReturnPath;
import rebound.exceptions.UnexpectedHardcodedEnumValueException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.util.AngryReflectionUtility;
import rebound.util.AngryReflectionUtility.JavaVisibility;
import rebound.util.ExceptionUtilities;
import rebound.util.Primitives;
import rebound.util.ValueType;
import rebound.util.classhacking.jre.BetterJREGlassbox;
import rebound.util.classhacking.jre.JREGlassBox.ArraysGlassBox;
import rebound.util.collections.ArrayUtilities;
import rebound.util.functional.EqualityComparator;
import rebound.util.objectutil.InstantiationNotSupportedException.InputlessInstantiationNotSupportedException;
import rebound.util.objectutil.Trimmable.TrimmableTrimRV;

//TODO go through and add to all the self-Instantiator things, ProvidesInstantiator as well :>    (or just plain replace that old pattern of self-instantiator???)

//TODO make singletons be instances in single-member enums (ie, *actual* java singletons? XD )

//Todo grandfathering for copyInto (maybe??)


/**
 * Missing utilities and systems for..
 * 		+ null-tolerant equality and inequality testing,
 * 		+ instantiation
 * 		+ immutability
 * 		+ cloning
 * 		+ pooling
 * 
 * @author RProgrammer
 */
@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
public class ObjectUtilities
implements JavaNamespace
{
	/**
	 * Attempts to compare the objects, and throws a {@link CompareNotSupportedException} if not supported ._.
	 * :>
	 */
	@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
	@Deprecated
	@DeprecatedInFavorOfMember(cls=BasicObjectUtilities.class, member="int cmp(Object,Object)")
	public static int cmp(Object a, Object b) throws CompareNotSupportedException
	{
		if (a == null && b == null)
		{
			return 0;
		}
		else if (a instanceof Comparable)
		{
			return ((Comparable)a).compareTo(b);
		}
		else if (b instanceof Comparable)
		{
			return -((Comparable)b).compareTo(a);
		}
		//else if (eq(a, b)) //this is a not-good idea, since it means cmp may or may not fail depending on the data (ie, if equal it works even if comparing isn't actually supported, but if not equal if fails and doesn't tell greater-than or less-than ness ._. )
		//{
		//	return 0;
		//}
		
		//No grandfathering needed!  JRE supports ordered-comparing since Java 2! :D
		
		
		throw new CompareNotSupportedException();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Fundamental properties! :D!   (platform/core/literal context/interpretation anyways X3 )
	
	/**
	 * @return true or false if known, null is it couldn't be determined
	 */
	public static JavaImmutability isImmutable(@Nullable Object object)
	{
		//Java things which could never implement our interfaces!
		if (object == null)
			return Concurrently_Immutable;
		//else if (object.getClass().isEnum()) //Enum singletons may or may not be immutable!
		else if (object.getClass().isAnnotation()) //these def. are tho xD
			return Concurrently_Immutable;
		else if (object.getClass().isArray())
			return Array.getLength(object) == 0 ? Concurrently_Immutable : Mutable;
		
		//Our trait-override interfaces!! :D
		else if (object instanceof StaticallyMutable)
			return Mutable;
		else if (object instanceof StaticallyConcurrentlyImmutable)
			return Concurrently_Immutable;
		else if (object instanceof StaticallyThreadUnsafelyImmutable)
			return Non_Thread_Safe_Immutable;
		else if (object instanceof RuntimeImmutability)
			return ((RuntimeImmutability)object).isImmutable();
		
		//Grandfathering of things which *could* implement our interfaces, but don't of course XD
		JavaImmutability rv = grandfatheringIsImmutableOperations(object);
		if (rv != null)
			return rv;
		
		return null;
	}
	
	
	public static JavaImmutability isImmutableIncludingSlowReflectionTests(@Nullable Object object)
	{
		JavaImmutability rv = isImmutable(object);
		if (rv != null)
			return rv;
		
		if (AngryReflectionUtility.areAllInstanceFieldsFinal(object.getClass())) //null is handled above
			return Concurrently_Immutable;
		
		return null;
	}
	
	
	public static JavaImmutability grandfatheringIsImmutableOperations(Object x)
	{
		//Things for public interface
		if (x == null)
			return Concurrently_Immutable;
		
		//Note that these use getClass() == x to check, since subclasses may be different >;)   (even for final types; hey, why not? forwards-compatibility! XD')
		
		
		else if (x.getClass() == Object.class)
			return Concurrently_Immutable;
		
		
		
		else if (x instanceof Appendable) //takes care of StringBuilder and StringBuffer ^_^
			return Mutable;
		
		else if (x.getClass() == Boolean.class || x.getClass() == Character.class || x.getClass() == Byte.class || x.getClass() == Short.class || x.getClass() == Integer.class || x.getClass() == Long.class || x.getClass() == Float.class || x.getClass() == Double.class)
			return Concurrently_Immutable;
		else if (x.getClass() == String.class)
			return Concurrently_Immutable;
		else if (x.getClass() == Class.class || x.getClass() == Package.class || x.getClass() == Method.class || x.getClass() == Field.class || x.getClass() == Constructor.class) //even though their AccessibleObject.override flag is mutable; ehh.., let's just go ahead and consider them immutable (the important parts at least)   (they're supposed to be anyways xD )
			return Concurrently_Immutable;
		else if (x.getClass() == ClassLoader.class)
			return Mutable;
		
		
		else if (x.getClass() == Thread.class || x.getClass() == ThreadGroup.class)
			return Mutable;
		else if (x.getClass() == Process.class)
			return Mutable;
		
		//Note readonly Buffers are readonly, not necessarily immutable XP
		
		
		
		//As of Java 7, these still internally use deprecated non-final caching fields, so they are not thread-safe-grade Immutable :<
		else if (x.getClass() == BigInteger.class)
			return Non_Thread_Safe_Immutable;
		else if (x.getClass() == BigDecimal.class)
			return Non_Thread_Safe_Immutable;
		
		
		
		else if (x.getClass() == ArrayList.class)
			return Mutable;
		else if (x.getClass() == Vector.class)
			return Mutable;
		else if (x.getClass() == Stack.class)
			return Mutable;
		else if (x.getClass() == LinkedList.class)
			return Mutable;
		else if (x.getClass() == BitSet.class)
			return Mutable;
		
		else if (x.getClass() == ArrayDeque.class)
			return Mutable;
		else if (x.getClass() == PriorityQueue.class)
			return Mutable;
		
		else if (x.getClass() == HashSet.class)
			return Mutable;
		else if (x.getClass() == LinkedHashSet.class)
			return Mutable;
		else if (x.getClass() == TreeSet.class)
			return Mutable;
		
		else if (x.getClass() == HashMap.class)
			return Mutable;
		else if (x.getClass() == Hashtable.class || x.getClass() == Properties.class)
			return Mutable;
		else if (x.getClass() == IdentityHashMap.class)
			return Mutable;
		else if (x.getClass() == EnumMap.class)
			return Mutable;
		else if (x.getClass() == LinkedHashMap.class)
			return Mutable;
		else if (x.getClass() == TreeMap.class)
			return Mutable;
		
		else if (x.getClass() == UUID.class)
			return Concurrently_Immutable;
		
		//As of Java 7, these still internally use deprecated non-final caching fields, so they are not thread-safe-grade Immutable  >:j
		else if (x.getClass() == java.util.Date.class || x.getClass() == java.sql.Date.class)
			return Non_Thread_Safe_Immutable;
		
		
		
		else if (x.getClass() == File.class) //the nonfinal fields are only set by constructors and readObject(), so is ok :>
			return Concurrently_Immutable;
		
		
		
		else if (x.getClass() == URL.class) //mmmmm ok, but exposed in a JRE-protected set() method  :\      (also, yes non-final fields, but *eagerly* computed! :> )
			return Concurrently_Immutable;
		else if (x.getClass() == URI.class) //oh definitely not!  thread-unsafe (lazily recomputed) cached fields all over the place! XD
			return Non_Thread_Safe_Immutable;
		
		
		
		else if (x.getClass() == BetterJREGlassbox.Type_Arrays_asList) //This has a fixed key set / size, but the slots can be modified :>
			return Mutable;
		
		//All these are immutable by virtue of they're always empty!! XD
		else if (x.getClass() == BetterJREGlassbox.Type_Collections_emptyEnumeration)
			return Concurrently_Immutable;
		else if (x.getClass() == BetterJREGlassbox.Type_Collections_emptyIterator)
			return Concurrently_Immutable;
		else if (x.getClass() == BetterJREGlassbox.Type_Collections_emptyList)
			return Concurrently_Immutable;
		else if (x.getClass() == BetterJREGlassbox.Type_Collections_emptyListIterator)
			return Concurrently_Immutable;
		else if (x.getClass() == BetterJREGlassbox.Type_Collections_emptyMap)
			return Concurrently_Immutable;
		else if (x.getClass() == BetterJREGlassbox.Type_Collections_emptySet)
			return Concurrently_Immutable;
		
		//All these are 'readonly' *interfaces* but not necessarily 'immutable' *objects* ;)
		//What they really are depends on the underlying (and encapsulated/hidden) thing behind them that they delegate to; so we can't tells ._.
		//	BetterJREGlassbox.Type_Collections_unmodifiableCollection
		//	BetterJREGlassbox.Type_Collections_unmodifiableList
		//	BetterJREGlassbox.Type_Collections_unmodifiableMap
		//	BetterJREGlassbox.Type_Collections_unmodifiableSet
		//	BetterJREGlassbox.Type_Collections_unmodifiableSortedMap
		//	BetterJREGlassbox.Type_Collections_unmodifiableSortedSet
		
		
		else if (x.getClass() == Cursor.class)
			return Non_Thread_Safe_Immutable;
		else if (x.getClass() == Color.class)
			return Concurrently_Immutable;  //Todo, right?? ;;
		
		
		return null;
	}
	
	/**
	 * Note: {@link #isConcurrentlyImmutable(Object) concurrency-grade immutability} implies this and more guarantees besides :>
	 * (so !{@link #isThreadUnsafelyImmutable(Object)} == 'isMutable()'  ^w^ )
	 */
	public static Boolean isThreadUnsafelyImmutable(Object object)
	{
		JavaImmutability imt = isImmutable(object);
		
		if (imt == Concurrently_Immutable)
			return true;
		else if (imt == Non_Thread_Safe_Immutable)
			return true;
		else if (imt == Mutable)
			return false;
		else if (imt == null)
			return null;
		else
			throw new UnexpectedHardcodedEnumValueException(imt);
	}
	
	public static Boolean grandfatheringIsThreadUnsafelyImmutable(Object x)
	{
		if (x.getClass().isArray())
			return Array.getLength(x) == 0;
		
		if (x.getClass() == BigInteger.class)
			return true;
		else if (x.getClass() == BigDecimal.class)
			return true;
		else if (x.getClass() == java.util.Date.class || x.getClass() == java.sql.Date.class)
			return true;
		else if (x.getClass() == URI.class)
			return true;
		
		
		return null;
	}
	
	
	
	
	
	
	
	//<Identity{ful|less}ness things! :D!
	public static Boolean hasIdentity(Object object)
	{
		if (object == null)
			return true; //null is a singleton token (immutable, identityful, empty, and uncloneable!), like enum singletons; it supports the concept of identity-comparison!  (in terms of implementations: it's *memory address* is what distinguishes it, not its *memory contents*; so is identity-having ^_^ )
		
		if (isFalseAndNotNull(isThreadUnsafelyImmutable(object)))
		{
			//Mutables always have identity ^_^
			return true;
		}
		else
		{
			if (object instanceof StaticallyIdentityless)
			{
				return false;
			}
			else if (object instanceof StaticallyIdentityful)
			{
				return true;
			}
			else if (object instanceof RuntimeIdentityfulness)
			{
				return ((RuntimeIdentityfulness)object).hasIdentity();
			}
			else
			{
				return grandfatheringHasIdentity(object);
			}
		}
	}
	
	public static Boolean grandfatheringHasIdentity(Object x)
	{
		//x shouldn't be null here x>  (that's should be checked above :> )
		//x is also assumed to be immutable XD
		
		if (x instanceof Enum)
			return true; //just a default, enum things don't *have* to have identity; they could just be being used as singletons for performances or whatever! :>
		else if (x.getClass() == Object.class)
			return true;
		
		else if (Primitives.isPrimitiveWrapperInstance(x))
			return false;
		else if (x.getClass().isAnnotation())
			return false;
		
		else if (x instanceof String)
			return false;
		
		else if (x.getClass() == Class.class || x.getClass() == Package.class || x.getClass() == Method.class || x.getClass() == Field.class || x.getClass() == Constructor.class) //even though their AccessibleObject.override flag is mutable; ehh.., let's just go ahead and consider them immutable (the important parts at least)   (they're supposed to be anyways xD )
			return true;
		
		else if (x.getClass() == BigInteger.class || x.getClass() == BigDecimal.class)
			return false;
		
		else if (x.getClass() == UUID.class) //the Java object doesn't have identity!!, it *is* an identity! XD   (an identifier string :> )
			return false;
		
		else if (x.getClass() == File.class) //basically a special kind of string; really a container-identifer string, like UUID, (but to a "container" thing, which can change if the names do, not an identifier to the file itself, you know ;)  )
			return false;
		else if (x.getClass() == URL.class)
			return false;
		else if (x.getClass() == URI.class)
			return false;
		
		
		//// More controversial defaults! ////
		
		//Data things will be considered by their data, not as identityful entities in their own right :>
		else if (x instanceof Collection)
			return false;
		else if (x instanceof Map)
			return false;
		else if (x instanceof CharSequence)
			return false;
		
		//won't ever be immutable though I think; oh well X)
		else if (x instanceof Buffer)
			return false;
		else if (x.getClass().isArray())
			return false;
		
		
		return null;
	}
	//Identity{ful|less}ness things! :D!>
	
	
	
	
	//Fundamental properties! :D!   (platform/core/literal context/interpretation anyways X3 )  >
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Equality/Identity/Equivalence-testing things
	
	public static boolean eqr(Object a, Object b)
	{
		if (a == null || b == null || isTrueOrNull(hasIdentity(a)) || isTrueOrNull(hasIdentity(b)))
		{
			return a == b;
		}
		else
		{
			return BasicObjectUtilities.eq(a, b);
		}
	}
	
	public static boolean eqr(Object... many)
	{
		if (many.length < 2)
			return true; //arbitrary choice for length=0 x>
		
		Object first = many[0];
		for (int i = 1; i < many.length; i++)
			if (!eqr(first, many[i]))
				return false;
		return true;
	}
	
	protected static final EqualityComparator EquivalenceOrIdentityEqualityComparatorByDefaultIdentityfulnessTest = BasicObjectUtilities.getEquivalenceOrIdentityEqualityComparator(o -> isTrueAndNotNull(hasIdentity(o)));
	
	public static <T> EqualityComparator<T> getEquivalenceOrIdentityEqualityComparator()
	{
		return EquivalenceOrIdentityEqualityComparatorByDefaultIdentityfulnessTest;
	}
	
	
	public static <E> Comparator<E> makeReverseComparator(final Comparator<E> original)
	{
		//Todo deflatten if a reverse comparator is already given! \o/
		return (a, b) -> -original.compare(a, b);
	}
	//Inequality-testing things>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Cloning things
	//	public static class UncloneableSingletonException
	//	extends RuntimeException
	//	{
	//		private static final long serialVersionUID = 1L;
	//
	//
	//		public UncloneableSingletonException()
	//		{
	//		}
	//
	//		public UncloneableSingletonException(String message)
	//		{
	//			super(message);
	//		}
	//
	//		public UncloneableSingletonException(Throwable cause)
	//		{
	//			super(cause);
	//		}
	//
	//		public UncloneableSingletonException(String message, Throwable cause)
	//		{
	//			super(message, cause);
	//		}
	//	}
	
	
	
	public static class CloneNotSupportedReturnPath
	extends SingletonReturnPath
	{
		private static final long serialVersionUID = 1L;
		
		public static final CloneNotSupportedReturnPath I = new CloneNotSupportedReturnPath();
		protected CloneNotSupportedReturnPath() {}
		
		@Override
		public RuntimeException toException()
		{
			return new WrappedThrowableRuntimeException(new CloneNotSupportedException());
		}
	}
	
	public static <E> E attemptCloneRp(E object) throws CloneNotSupportedReturnPath
	{
		try
		{
			return attemptClone(object);
		}
		catch (WrappedThrowableRuntimeException exc)
		{
			if (exc.getCause() instanceof CloneNotSupportedException)
				throw CloneNotSupportedReturnPath.I;
			else
				throw exc;
		}
	}
	
	
	
	/**
	 * Attempts to clone the object lightly, using various techniques.
	 * throws a <code>new {@link WrappedThrowableRuntimeException}(new {@link CloneNotSupportedException}())</code> instead of a {@link CloneNotSupportedException} when that would occur ^_^
	 */
	public static <E> E attemptClone(E object) throws WrappedThrowableRuntimeException
	{
		if (object == null)
		{
			return object;
		}
		else if (object.getClass().isEnum())
		{
			if (isFalseAndNotNull(hasIdentity(object)))
				return object;
			else
				throw new WrappedThrowableRuntimeException(new CloneNotSupportedException());
		}
		else if (object.getClass().isArray())
		{
			return ArrayUtilities.cloneArray(object);
		}
		else if (isFalseAndNotNull(hasIdentity(object)))
		{
			//mutable ==> identity
			//.: ~identity ==> ~mutable  ^_^
			assert !isFalseAndNotNull(isThreadUnsafelyImmutable(object));
			return object;
		}
		else if (object instanceof PubliclyCloneable)
		{
			return ((PubliclyCloneable<E>)object).clone();
		}
		else if (object instanceof Copyable && object instanceof ProvidesInstantiator)
		{
			Object newInstance = newInstance(((ProvidesInstantiator)object).getInstantiator());
			((Copyable)newInstance).setFrom(object);
			return (E)newInstance;
		}
		
		
		
		return grandfatheringCloneOperations(object);
	}
	
	
	public static <E> E grandfatheringCloneOperations(E object) throws WrappedThrowableRuntimeException
	{
		//Things for public interface
		if (object == null)
			return object;
		if (isFalseAndNotNull(hasIdentity(object)))  //implies immutability :3
			return object;
		//
		
		
		
		//Non-immutable, yet cloneable grandfathered-in classes!
		
		
		//Non-obviously-cloneable:
		//Empty! :D
		
		
		//Not-quite-immutable: immutable enough for cloneing, but not for thread-safety  >,>
		if (object.getClass() == BigInteger.class)
			return object;
		if (object.getClass() == BigDecimal.class)
			return object;
		if (object.getClass() == java.util.Date.class || object.getClass() == java.sql.Date.class)
			return object;
		if (object.getClass() == URI.class)
			return object;
		
		
		//Simple new Foo(Foo) constructor   (added here so the non-reflective version will work, and for speed)
		if (object.getClass() == StringBuilder.class)
			return (E)new StringBuilder((StringBuilder)object);
		if (object.getClass() == StringBuffer.class)
			return (E)new StringBuilder((StringBuffer)object);
		if (object.getClass() == PriorityQueue.class)
			return (E)new PriorityQueue((PriorityQueue)object);
		
		
		//Simple public clone() method :>   (added here so the non-reflective version will work, and for speed)
		if (object instanceof ArrayList)
			return (E)((ArrayList)object).clone();
		if (object.getClass() == ArraysGlassBox.ArrayList)
			return (E)Arrays.asList(((Collection)object).toArray());
		if (object instanceof Vector)
			return (E)((Vector)object).clone();
		if (object instanceof Stack)
			return (E)((Stack)object).clone();
		if (object instanceof LinkedList)
			return (E)((LinkedList)object).clone();
		if (object instanceof BitSet)
			return (E)((BitSet)object).clone();
		
		if (object instanceof ArrayDeque)
			return (E)((ArrayDeque)object).clone();
		
		if (object instanceof HashSet)
			return (E)((HashSet)object).clone();
		if (object instanceof LinkedHashSet)
			return (E)((LinkedHashSet)object).clone();
		if (object instanceof TreeSet)
			return (E)((TreeSet)object).clone();
		
		if (object instanceof HashMap)
			return (E)((HashMap)object).clone();
		if (object instanceof Hashtable)  //includes java.util.Properties :>
			return (E)((Hashtable)object).clone();
		if (object instanceof IdentityHashMap)
			return (E)((IdentityHashMap)object).clone();
		if (object instanceof EnumMap)
			return (E)((EnumMap)object).clone();
		if (object instanceof LinkedHashMap)
			return (E)((LinkedHashMap)object).clone();
		if (object instanceof TreeMap)
			return (E)((TreeMap)object).clone();
		
		
		if (object instanceof RectangularShape) //includes Rectangle2D, Rectangle, RoundRectangle2D, Ellipse2D, Arc2D  :D
			return (E)((RectangularShape)object).clone();
		if (object instanceof Path2D)
			return (E)((Path2D)object).clone();
		if (object instanceof Area)
			return (E)((Area)object).clone();
		if (object instanceof Line2D)
			return (E)((Line2D)object).clone();
		if (object instanceof QuadCurve2D)
			return (E)((QuadCurve2D)object).clone();
		if (object instanceof CubicCurve2D)
			return (E)((CubicCurve2D)object).clone();
		
		if (object.getClass() == Polygon.class)  //ooolllddddd! :P xD
			return (E)(new Polygon(((Polygon)object).xpoints, ((Polygon)object).ypoints, ((Polygon)object).npoints));
		
		
		throw new WrappedThrowableRuntimeException(new CloneNotSupportedException());
	}
	
	
	/**
	 * Attempts to clone the object more..intensely, involving reflection-based techniques >;)
	 * throws a WrappedThrowableRuntimeException(new CloneNotSupportedException()) if these fail, same as {@link #attemptClone(Object)}
	 */
	public static <E> E attemptCloneWithReflection(E object) throws WrappedThrowableRuntimeException
	{
		try
		{
			return attemptClone(object);
		}
		catch (WrappedThrowableRuntimeException exc)
		{
			if (exc.getCause() instanceof CloneNotSupportedException)
			{
				//Try the more intense techniques  >;)
				//continue on..
			}
			else
			{
				throw exc;
			}
		}
		
		
		
		
		
		//		//We already checked for identity (and there isn't a slower, more accurate test for that XP )     (not to show identityless!, the heavier immutability test could only reveal it is identityful! ..which..means we need to do what we'd do anyways: clone it!  XD  )
		
		
		
		
		
		//More intense techniques >;)
		{
			Class c = object.getClass();
			
			
			
			//Copyable and Instantiateable  >;)
			{
				if (object instanceof Copyable)
				{
					Object instantiator = null;
					
					
					if (object instanceof ProvidesInstantiator)
						instantiator = ((ProvidesInstantiator)object).getInstantiator();
					
					else
					{
						try
						{
							instantiator = getInstantiatorOrClass(object.getClass());
						}
						catch (WrappedThrowableRuntimeException exc)
						{
							if (exc.getCause() instanceof InstantiationNotSupportedException)
							{
								//Try the other techniques..
								instantiator = null;
							}
							else
							{
								throw exc;
							}
						}
					}
					
					
					if (instantiator != null)
					{
						Object newInstance = newInstance(instantiator);
						
						((Copyable)newInstance).setFrom(object);
						
						return (E)newInstance;
					}
				}
			}
			
			
			
			
			//Has a public clone method, but just doesn't implement PubliclyCloneable
			{
				Method cloneMethod = AngryReflectionUtility.getMethod(c, "clone", new Class[]{}, JavaVisibility.PUBLIC, null, false, true); //don't require that the return type be the class; it could be Object!
				
				if (cloneMethod != null)
				{
					try
					{
						Object raw = cloneMethod.invoke(object);
						
						if (raw == null)
							return null;
						
						if (!c.isAssignableFrom(raw.getClass()))
							throw new ClassCastException();
						
						return (E)raw;
					}
					catch (IllegalArgumentException exc)
					{
						throw new ImpossibleException();
					}
					catch (IllegalAccessException exc)
					{
						throw new ImpossibleException();
					}
					catch (InvocationTargetException exc)
					{
						if (exc.getTargetException() instanceof CloneNotSupportedException)
						{
							//pass
						}
						else
						{
							throw ExceptionUtilities.rewrapToUnchecked(exc);
						}
					}
				}
			}
			
			
			
			
			
			
			
			
			
			//Has a public constructor that accepts one of the same type (which we will assume is basically a clone!)
			{
				Constructor constructor = AngryReflectionUtility.getConstructor(c, new Class[]{c}, JavaVisibility.PUBLIC);
				
				if (constructor != null)
				{
					try
					{
						return (E)constructor.newInstance(object);
					}
					catch (IllegalArgumentException exc)
					{
						throw new ImpossibleException();
					}
					catch (IllegalAccessException exc)
					{
						throw new ImpossibleException();
					}
					catch (InvocationTargetException exc)
					{
						if (exc.getTargetException() instanceof CloneNotSupportedException)
						{
							//pass
						}
						else
						{
							throw ExceptionUtilities.rewrapToUnchecked(exc);
						}
					}
					catch (InstantiationException exc)
					{
						throw new ImpossibleException("How could we have an instance whose runtime type is an abstract class!  ("+c+")");
					}
				}
			}
		}
		
		
		
		throw new WrappedThrowableRuntimeException(new CloneNotSupportedException());
	}
	
	
	
	
	//Todo attemptSerializationBasedCloneTechnique
	
	//Cloning things>
	
	
	
	
	
	
	
	
	
	//<Copying things
	/**
	 * Uses {@link Copyable} if dest supports it, but also tries some grandfathering-in operations ^_^
	 * @throws ClassCastException should be thrown if source is wrong type :>
	 * @throws CopyNotSupportedException basically if dest is wrong
	 */
	public static void copy(Object source, Object dest) throws CopyNotSupportedException, ClassCastException
	{
		if (dest instanceof Copyable)
		{
			((Copyable)dest).setFrom(source); //should throw ClassCastException if it can't because of mismatching types :>
		}
		else
		{
			grandfatheringCopyOperations(source, dest);
		}
	}
	
	public static void grandfatheringCopyOperations(Object source, Object dest) throws CopyNotSupportedException, ClassCastException
	{
		//Grandfathering operations ^_^
		
		if (dest instanceof Collection)
		{
			if (source instanceof Collection)
			{
				((Collection)dest).clear();
				((Collection)dest).addAll((Collection)source);
				return;
			}
			else if (source instanceof Object[])
			{
				((Collection)dest).clear();
				//((Collection)dest).addAll(Arrays.asList((Object[])source));
				for (Object e : (Object[])source)
					((Collection)dest).add(e);
			}
			else if (source instanceof Iterable)
			{
				((Collection)dest).clear();
				for (Object e : (Iterable)source)
					((Collection)dest).add(e);
			}
			else
			{
				throw new ClassCastException(getClassNameNT(source));
			}
		}
		
		//TODO more? :>
		
		throw new CopyNotSupportedException();
	}
	//Copying things>
	
	
	
	
	
	
	
	//<Instantiation things
	/**
	 * Instantiates an instance either by Class.newInstance() if a {@link Class} is provided (public no-args constructor only), or by an {@link Instantiator} (.newInstance()) if one of those is provided :>
	 */
	public static Object newInstance(Object instantiationObject) throws InstantiationNotSupportedException
	{
		if (instantiationObject instanceof Class)
		{
			Class instantiationClass = (Class)instantiationObject;
			
			if (AngryReflectionUtility.getPublicNoArgsConstructor(instantiationClass) != null)
			{
				try
				{
					return instantiationClass.newInstance();
				}
				catch (InstantiationException exc)
				{
					throw new WrappedThrowableRuntimeException(exc);
				}
				catch (IllegalAccessException exc)
				{
					throw new WrappedThrowableRuntimeException(exc);
				}
				
				//Note: InvocationTargetException is unwrapped in newInstance():   from jdk1.7.0_06: "Unsafe.getUnsafe().throwException(e.getTargetException());"
			}
			else
			{
				//Todo optimize ._.
				return getInstantiator(instantiationClass).newInstance();
			}
		}
		else if (instantiationObject instanceof Instantiator)
		{
			return ((Instantiator)instantiationObject).newInstance();
		}
		else
		{
			throw new IllegalArgumentException("Unsupported instantiator type: "+(instantiationObject == null ? "null" : instantiationObject.getClass()));
		}
	}
	
	
	/**
	 * If the class implements {@link JavaNamespace}, these are skipped (see "otherwise" below).
	 * If the class has a public, no-args constructor, this simply returns the class (wrap it with a {@link NoArgsDefaultInstantiator} if you want a proper {@link Instantiator}).
	 * If the class has a public static method which returns its own type, and has a name of "newInstance", "createInstance", etc. (see {@link #Recognized_Instantiation_Static_Method_Names} for a complete list), then that method is wrapped and exposed as an {@link Instantiator}.
	 * If the class has a public static method which returns an {@link Instantiator} and the name "getInstantiator", or a public static field "INSTANTIATOR" of type {@link Instantiator}, then that is [called and] returned.
	 * 
	 * Otherwise, throws a {@link WrappedThrowableRuntimeException}(new {@link InstantiationNotSupportedException}()) if the class doesn't support any specification.  xP
	 */
	public static Object getInstantiatorOrClass(Class c) throws InstantiationNotSupportedException
	{
		if (JavaNamespace.class.isAssignableFrom(c))
			throw new InputlessInstantiationNotSupportedException();
		
		if (AngryReflectionUtility.getPublicNoArgsConstructor(c) != null)
			return c;
		
		
		//I could be wrong, but I don't *think* there's any grandfathering needed here.. (JRE seems real good about using public no-args constructors (or createInstance() static methods, or equivalent)  :> )
		
		
		
		//Try the static-member-that-provides-an-Instantiator thing x>
		{
			//Try the method first
			{
				Method m = AngryReflectionUtility.getMethod(c, "getInstantiator", new Class[]{}, JavaVisibility.PUBLIC, Instantiator.class, true, false);
				
				if (m != null)
				{
					try
					{
						return m.invoke(null);
					}
					catch (IllegalAccessException exc)
					{
						throw new ImpossibleException("But it's public! (isn't it?)");
					}
					catch (IllegalArgumentException exc)
					{
						throw new ImpossibleException("But we checked the signature!");
					}
					catch (InvocationTargetException exc)
					{
						throw ExceptionUtilities.rewrapToUnchecked(exc);
					}
				}
			}
			
			
			//Then the field
			{
				Field m = AngryReflectionUtility.getField(c, "Instantiator", JavaVisibility.PUBLIC, Instantiator.class, true, false);
				
				if (m != null)
				{
					try
					{
						return m.get(null);
					}
					catch (IllegalAccessException exc)
					{
						throw new ImpossibleException("But it's public! (isn't it?)");
					}
					catch (IllegalArgumentException exc)
					{
						throw new ImpossibleException("But we checked the signature!");
					}
				}
				
				
				m = AngryReflectionUtility.getField(c, "INSTANTIATOR", JavaVisibility.PUBLIC, Instantiator.class, true, false);
				
				if (m != null)
				{
					try
					{
						return m.get(null);
					}
					catch (IllegalAccessException exc)
					{
						throw new ImpossibleException("But it's public! (isn't it?)");
					}
					catch (IllegalArgumentException exc)
					{
						throw new ImpossibleException("But we checked the signature!");
					}
				}
			}
		}
		
		
		
		
		//Try the static-method-instantiator-thing
		{
			for (String recognizedInstantiationMethodName : Recognized_Instantiation_Static_Method_Names)
			{
				Method m = AngryReflectionUtility.getMethod(c, recognizedInstantiationMethodName, new Class[]{}, JavaVisibility.PUBLIC, null, true, false);  //don't require that the return type be the class; it could be Object!
				
				if (m == null)
					continue;
				
				if (m.getReturnType() == Void.class || m.getReturnType().isPrimitive())
					continue;
				
				final Method m_f = m;
				
				return new Instantiator()
				{
					@Override
					public Object newInstance() throws WrappedThrowableRuntimeException
					{
						try
						{
							return m_f.invoke(null);
						}
						catch (IllegalAccessException exc)
						{
							throw new ImpossibleException("But it's public! (isn't it?)");
						}
						catch (IllegalArgumentException exc)
						{
							throw new ImpossibleException("But we checked the signature!");
						}
						catch (InvocationTargetException exc)
						{
							throw ExceptionUtilities.rewrapToUnchecked(exc);
						}
					}
				};
			}
		}
		
		
		//Try the static-singleton-field thing (which we'll assume is correct to use if present; ie,
		{
			if (c.isEnum())
			{
				Object[] constants = c.getEnumConstants();
				
				if (constants.length == 1)
					return new SingletonInstantiator(constants[0]);
			}
			else
			{
				for (String recognizedSingletonStaticFieldName : Recognized_Singleton_Static_Field_Names)
				{
					Field f = AngryReflectionUtility.getField(c, recognizedSingletonStaticFieldName, JavaVisibility.PUBLIC, null, true, false);  //don't require that the field type be the class; it could be Object! :)
					
					if (f == null)
						continue;
					
					if (f.getType().isPrimitive())
						continue;
					
					Object singleton = null;
					
					try
					{
						singleton = f.get(null);
					}
					catch (IllegalAccessException exc)
					{
						throw new ImpossibleException("But it's public! (isn't it?)");
					}
					catch (IllegalArgumentException exc)
					{
						throw new ImpossibleException("But we checked the signature!");
					}
					
					return new SingletonInstantiator(singleton);
				}
			}
		}
		
		
		
		
		
		throw new InputlessInstantiationNotSupportedException();
	}
	
	
	
	public static final String[] Recognized_Instantiation_Static_Method_Names =
{
		"newInstance",
		"getNewInstance",
		"createInstance",
		"createNewInstance",
		"createNew",
		"inst",
		"instantiate",
		
		//we'll just hope it doesn't get a singleton when we want a fresh instance!  (unless of course it's immutable-indistinguishable! :D )
		"getInstance",
		"instance",
};
	
	
	public static final String[] Recognized_Singleton_Static_Field_Names =
{
		"I",
		
		"instance",
		"Instance",
		"INSTANCE",
		
		"SingletonInstance",
		"Singleton_Instance",
		"SINGLETON_INSTANCE",
};
	
	
	
	/**
	 * If an {@link Instantiator} is passed; -simply returns it (casts).  :>
	 * If a {@link Class} is passed; -wraps it in a {@link NoArgsDefaultInstantiator}.
	 * Otherwise throws an {@link IllegalArgumentException}
	 */
	public static Instantiator getInstantiatorFromInstantiationObject(Object c) throws InstantiationNotSupportedException, IllegalArgumentException
	{
		if (c instanceof Instantiator)
			return (Instantiator)c;
		else if (c instanceof Class)
			return new NoArgsDefaultInstantiator((Class)c);
		else
			throw new IllegalArgumentException("Unsupported instantiator type: "+(c == null ? "null" : c.getClass()));
	}
	
	public static <T> Instantiator<T> getInstantiator(Class<T> c) throws InstantiationNotSupportedException
	{
		return getInstantiatorFromInstantiationObject(getInstantiatorOrClass(c));
	}
	//Instantiation things>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//	//<Language-level pooling-like things  >;)
	//
	//	protected static final ThreadLocal<SimplePool> SIMPLE_POOLS = new ThreadLocal<SimplePool>();
	//
	//	/**
	//	 * Usage notes:
	//	 * 		1. BE VERY CAREFUL WITH THIS! (in making sure you either use self-resetting {@link PoolingAwareObject}s or consider the fields as undefined)
	//	 * 		2. BE VERY CAREFUL WITH THIS! (in making sure you take care of concurrency/thread-safety things correctly!!)
	//	 * 		XD
	//	 *
	//	 * (these are best for simple things; eg, that implement {@link PoolingAwareObject} and/or {@link ReinitializableObject})
	//	 */
	//	public static <T> Pool<T> getPoolForClass(Class<T> c)
	//	{
	//		if (Unpoolable.class.isAssignableFrom(c))
	//			throw new UnpoolableException();
	//
	//
	//		//Todo (create and) Utilize AWESOME JNI libs! :D!
	//		//		+ set up in the static initializer somewhere; one way or the other, always
	//
	//
	//		//Java pools
	//		{
	//			SimplePool<T> pool = SIMPLE_POOLS.get();
	//
	//			if (pool == null)
	//			{
	//				pool = new SimplePool<T>(getInstantiator(c));
	//				SIMPLE_POOLS.set(pool);
	//			}
	//
	//			return pool;
	//		}
	//	}
	//
	//	//Language-level pooling-like things>
	
	
	
	
	/**
	 * @return if you could trim it again :>
	 */
	@Nonnull
	public static TrimmableTrimRV trimThing(Object o, boolean trimAllTheWay)
	{
		if (trimAllTheWay)
		{
			// Shortest.
			// loop.
			// ever.
			// XD
			while (trimThing(o, false) == TrimmableTrimRV.CouldKeepInvoking);
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
		
		else
		{
			if (o instanceof Trimmable)
			{
				return ((Trimmable)o).couldYouMaybeUseALittleLessMemoryIfYouDontMind();
			}
			
			//Grandfathering!
			else if (o instanceof ArrayList)
			{
				((ArrayList)o).trimToSize();
				return TrimmableTrimRV.DontKeepInvoking;
			}
			else if (o instanceof Vector)
			{
				//if (((Vector)o).capacity() != ((Vector)o).size())
				((Vector)o).trimToSize();
				return TrimmableTrimRV.DontKeepInvoking;
			}
			
			
			else
			{
				//oh, ok; that's fine; sorry to bother you mr. object ._.
				return TrimmableTrimRV.DontKeepInvoking;
			}
		}
	}
	
	
	
	
	/* For old tertiary logic api (null meaning unknown if it trimmed any memory usage; and two methods for whether we should keep trying or not)
	public static Boolean trimThing(Object o)
	{
		if (o instanceof Trimmable)
		{
			return ((Trimmable)o).couldYouMaybeUseALittleLessMemoryIfYouDontMind();
		}
		
		//Grandfathering!
		else if (o instanceof ArrayList)
		{
			((ArrayList)o).trimToSize();
			return null;
		}
		else if (o instanceof Vector)
		{
			if (((Vector)o).capacity() != ((Vector)o).size())
			{
				((Vector)o).trimToSize();
				return true;
			}
			else
			{
				return false;
			}
		}
		
		
		else
		{
			//oh, ok; that's fine; sorry to bother you mr. object ._.
			return false;
		}
	}
	
	
	/**
	 * Just keep trimming 'till it don't trim no more!
	 * (within sanity limits, of course xD )
	 * /
	public static Boolean trimThingHard(Object o)
	{
		//keep trimming as long as it's working! :D
		
		int sanityLimitTrue = 4096;
		int sanityLimitNull = 32;
		
		Boolean ultimate = null;
		for (int totalIterations = 0, iterationsSinceLastTrue = 0; totalIterations < sanityLimitTrue; totalIterations++, iterationsSinceLastTrue++)
		{
			if (iterationsSinceLastTrue >= sanityLimitNull)
				break;
			
			Boolean rv = trimThing(o);
			
			if (totalIterations == 0)
				ultimate = rv;
			else
			{
				if (rv != null && rv)
				{
					ultimate = true;
					iterationsSinceLastTrue = 0;
				}
			}
			
			if (rv != null && !rv)
				break;
		}
		
		return ultimate;
	}
	 */
	
	
	
	public static int hashr(Object o)
	{
		if (o == null)
			return 0;
		else
			if (isTrueOrNull(hasIdentity(o)))
				return System.identityHashCode(o);
			else
				return o.hashCode();
	}
	
	
	public static int arrayHashCodeByContentsRespectingFundamentalProperties(Object[] a, int offset, int length)
	{
		if (a == null)
			return 0;
		
		int result = 1;
		
		for (int i = offset; i < offset+length; i++)
		{
			Object element = a[i];
			result = 31 * result + (element == null ? 0 : hashr(element));
		}
		
		return result;
	}
	
	public static int arrayHashCodeByContentsRespectingFundamentalProperties(Object[] a)
	{
		return arrayHashCodeByContentsRespectingFundamentalProperties(a, 0, a.length);
	}
	
	
	
	
	
	
	
	
	public static String toStringNT(Object o)
	{
		return o != null ? o.toString() : "<null>";
	}
	
	
	public static String getClassNameNT(Object o)
	{
		return o != null ? o.getClass().getName() : "<null-type>";
	}
	
	
	public static <T> T defaultIfNullOrPass(T o, T def)
	{
		return o == null ? def : o;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<[Im]mutability-testing things
	public static Boolean isConcurrentlyImmutable(Object object)
	{
		JavaImmutability imt = isImmutable(object);
		
		if (imt == JavaImmutability.Concurrently_Immutable)
			return true;
		else if (imt == JavaImmutability.Non_Thread_Safe_Immutable)
			return false;
		else if (imt == JavaImmutability.Mutable)
			return false;
		else if (imt == null)
			return null;
		else
			throw new UnexpectedHardcodedEnumValueException(imt);
	}
	//[Im]mutability-testing things>
	
	
	
	
	/**
	 * false-negatives but no false-positives
	 */
	public static boolean isDefinitelyValueType(@Nullable Object object)
	{
		return isTrueAndNotNull(isValueType(object));
	}
	
	public static @Nullable Boolean isValueType(@Nullable Object object)
	{
		return object instanceof ValueType ? ((ValueType)object).isValueType() : isDefinitelyValueTypeGrandfathering(object);
	}
	
	public static Boolean isDefinitelyValueTypeGrandfathering(@Nullable Object object)
	{
		if (
		object == null || object instanceof Boolean ||
		object instanceof Number || object instanceof Character ||
		object instanceof String ||
		object instanceof Enum ||
		object instanceof UUID
		)
			return true;
		
		
		else if (
		//		object.getClass() == ArrayList.class ||
		//		object.getClass() == Vector.class ||
		//		object.getClass() == HashMap.class ||
		//		object.getClass() == Hashtable.class ||
		//		object.getClass() == Properties.class ||
		isImmutable(object) == JavaImmutability.Mutable
		)
			return false;
		
		
		else
			return null;
	}
	
	
	
	
	
	
	
	public static void requireNull(Object x)
	{
		if (x != null)
			throw new IllegalArgumentException("Object is not null when it should be null!");
	}

	public static <T> T requireInstanceOf(@Nonnull T obj, Class c)
	{
		if (!c.isInstance(obj))
			throw newClassCastExceptionOrNullPointerException(obj, c);
		return obj;
	}
}
