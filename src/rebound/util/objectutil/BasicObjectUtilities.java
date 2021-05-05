package rebound.util.objectutil;

import static rebound.util.Primitives.*;
import java.util.Comparator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import rebound.exceptions.StructuredClassCastException;
import rebound.util.Maybe;
import rebound.util.functional.EqualityComparator;
import rebound.util.functional.functions.DefaultEqualityComparator;

public class BasicObjectUtilities
implements JavaNamespace
{
	/**
	 * Tests equivalence, as opposed to identity (==)  ^_^
	 * 
	 * Note that this *must always only ever mean* nothing more than a null-tolerant {@link Object#equals(Object)}!
	 *  (code relies on that!)
	 */
	public static boolean eq(Object a, Object b)
	{
		if (a == b)
			return true;
		
		if (a == null)  // || b == null)  //&& a != b
			return false;
		
		return a.equals(b);
		
		
		
		
		/*
		if (a == null)
			return b == null;
		//else if (b == null) return false;  //see note below for non-symmetric equalities..
		else
			return a.equals(b); //note that identity-equals may not be considered equals for some crazy wonky objects!  (like non-reflexive/symmetric-equality patterns)   (I'm not sure if we're ok with supporting that, but hey, why not? xD )
		 */
		
		
		/*
		if (a == null && b == null)
			return true;
		else if (a == null)// || b == null) //see note below for non-symmetric equalities..
			return false;
		else
			return a.equals(b); //note that identity-equals may not be considered equals for some crazy wonky objects!  (like non-reflexive/symmetric-equality patterns)   (I'm not sure if we're ok with supporting that, but hey, why not? xD )
		 */
	}
	
	public static boolean eq(Object... many)
	{
		if (many.length < 2)
			return true; //arbitrary choice for length=0 x>
		
		Object first = many[0];
		for (int i = 1; i < many.length; i++)
			if (!eq(many[i], first))
				return false;
		return true;
	}
	
	public static boolean eqC(Iterable<? extends Object> many)
	{
		boolean hasFirst = false;
		Object first = null;
		
		for (Object o : many)
		{
			if (hasFirst)
			{
				if (!eq(o, first))
					return false;
			}
			else
			{
				hasFirst = true;
				first = o;
			}
		}
		
		return true;
	}
	
	/**
	 * + Also null-tolerant ^_^
	 */
	public static boolean eqIdentityUnboxing(Object a, Object b)
	{
		if (a == b) //succeedfast (instead of failfast xD)
			return true;
		
		if (a == null)
			return b == null;
		else
		{
			if (isPrimitiveWrapperInstance(a))
			{
				return a.equals(b);
			}
			else
			{
				//return a == b;
				return false; //we already checked for that!
			}
		}
	}
	
	public static <E> boolean eqWith(EqualityComparator<E> comparator, E... many)
	{
		if (many.length < 2)
			return true; //arbitrary choice for length=0 x>
		
		if (comparator == null || comparator == DefaultEqualityComparator.I)
			return eq(many);
		if (comparator == BasicObjectUtilities.IdentityUnboxingEqualityComparator)
			return BasicObjectUtilities.eqIdentityUnboxing(many);
		
		E first = many[0];
		for (int i = 1; i < many.length; i++)
			if (!comparator.equals(first, many[i]))
				return false;
		return true;
	}
	
	public static boolean eqIdentityUnboxing(Object... many)
	{
		if (many.length < 2)
			return true; //arbitrary choice for length=0 x>
		
		Object first = many[0];
		for (int i = 1; i < many.length; i++)
			if (!eqIdentityUnboxing(first, many[i]))
				return false;
		return true;
	}
	
	public static boolean eqWith(Object a, Object b, Object comparator)
	{
		if (comparator == null)
			return eq(a, b);
		else if (comparator instanceof EqualityComparator)
			return ((EqualityComparator)comparator).equals(a, b);
		else if (comparator instanceof Comparator)
			return ((Comparator)comparator).compare(a, b) == 0;
		else
			throw new IllegalArgumentException(new StructuredClassCastException(comparator.getClass()));
	}
	
	public static <T> GeneralComparatorToEqualityComparator<T> makeEqualityComparatorFromGeneralComparator(final Comparator<T> inequalityComparator)
	{
		return new GeneralComparatorToEqualityComparator<T>
		()
		{
			@Override
			public boolean equals(T a, T b)
			{
				return inequalityComparator.compare(a, b) == 0;
			}
			
			@Override
			public Comparator<T> getUnderlyingGeneralComparator()
			{
				return inequalityComparator;
			}
		};
	}
	
	
	/**
	 * @return true if either is null (ie "whatever it needs to be"), otherwise {@link #eq(Object, Object)}
	 */
	public static boolean eqSome(@Nullable Object a, @Nullable Object b)
	{
		return a == null || b == null ? true : eq(a, b);
	}
	
	/**
	 * @return true if either is null (ie "whatever it needs to be"), otherwise {@link #eq(Object, Object) eq(a.getJust(), b.getJust())}
	 */
	public static boolean eqSomeMaybes(Maybe<Object> a, Maybe<Object> b)
	{
		return a == null || b == null ? true : eq(a.getJust(), b.getJust());
	}
	
	
	
	
	public static <T> EqualityComparator<T> getNaturalEqualityComparator()
	{
		return DefaultEqualityComparator.I;
	}
	
	protected static final EqualityComparator IdentityUnboxingEqualityComparator = new EqualityComparator()
	{
		@Override
		public boolean equals(Object a, Object b)
		{
			return eqIdentityUnboxing(a, b);
		};
	};
	
	public static <T> EqualityComparator<T> getIdentityUnboxingEqualityComparator()
	{
		return IdentityUnboxingEqualityComparator;
	}
	
	public static <T> EqualityComparator<T> getIdentityNonunboxingEqualityComparator()
	{
		return StrictReferenceIdentityEqualityComparator.I;
	}
	
	public static <T> EqualityComparator<T> getEquivalenceOrIdentityEqualityComparator(final Predicate<T> hasIdentity)
	{
		return new EqualityComparator<T>
		()
		{
			@Override
			public boolean equals(T a, T b)
			{
				return hasIdentity.test(a) || hasIdentity.test(b) ? (a == b) : eq(a, b);
			}
		};
	}
	//Equality/Identity/Equivalence-testing things>
	
	//<Inequality-testing things   (xD)
	
	//TODO Rename this to cmp() after Escape breaks free of rebound dependencies (or just Java in general!!)  X">
	/**
	 * Attempts to compare the objects, and throws a {@link CompareNotSupportedException} if not supported ._.
	 * :>
	 */
	public static int cmp2(@Nullable Object a, @Nullable Object b) throws CompareNotSupportedException
	{
		if (a == null)
		{
			if (b == null)
				return 0;
			else
				return -1;
		}
		else if (b == null)
		{
			//a != null
			return 1;
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
	
	
	public static int cmp2chainable(int previous, Object a, Object b) throws CompareNotSupportedException
	{
		return previous != 0 ? previous : cmp2(a, b);
	}
	
	public static <T> Comparator<T> chainComparators(Comparator<T> first, Comparator<T> second)
	{
		return new Comparator<T>()
		{
			@Override
			public int compare(T o1, T o2)
			{
				int r = first.compare(o1, o2);
				return r == 0 ? second.compare(o1, o2) : r;
			}
		};
	}
	
	
	
	
	
	protected static final Comparator NaturalOrderingComparator = new Comparator()
	{
		@Override
		public int compare(Object a, Object b)
		{
			return cmp2(a, b);
		}
		
		@Override
		public Comparator reversed()
		{
			return ReverseNaturalOrderingComparator;
		}
	};
	
	
	protected static final Comparator ReverseNaturalOrderingComparator = new Comparator()
	{
		@Override
		public int compare(Object a, Object b)
		{
			return -cmp2(a, b);
		}
		
		@Override
		public Comparator reversed()
		{
			return NaturalOrderingComparator;
		}
	};
	
	
	
	
	
	
	/**
	 * Just invokes {@link #cmp2(Object, Object)} on everything ^_^
	 */
	public static <E extends Comparable<E>> Comparator<E> getNaturalOrderingComparator()
	{
		return NaturalOrderingComparator;
	}
	
	/**
	 * Reverse of {@link #getNaturalOrderingComparator()} ^_^
	 */
	public static <E extends Comparable<E>> Comparator<E> getReverseNaturalOrderingComparator()
	{
		return ReverseNaturalOrderingComparator;
	}
	
	public static <T> T notnull(T object) throws NullPointerException
	{
		if (object == null)
			throw new NullPointerException();
		
		return object;
	}
	
	public static int hashNT(Object o)
	{
		return o == null ? 0 : o.hashCode(); //0 is as per System.identityHashCode ^_^
	}
	
	public static Class getClassNT(Object o)
	{
		return o != null ? o.getClass() : null;
	}
	
	public static Class getClassNTVoid(Object o)
	{
		return o != null ? o.getClass() : Void.class;  //aka "unit type" in otherwhere ;D
	}
	
	/**
	 * @return <code>value</code> if not null, otherwise <code>def</code>
	 */
	public static <T> T def(T value, T def)
	{
		return value != null ? value : def;
	}
}
