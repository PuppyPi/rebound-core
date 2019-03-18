/*
 * Created on Oct 28, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.functional;

import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.collections.ArrayUtilities.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.reachability.EscapesVarargs;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.NotEscapedVarargs;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.temporal.ConstantReturnValue;
import rebound.annotations.semantic.temporal.PossiblySnapshotPossiblyLiveValue;
import rebound.exceptions.NoSuchMemberRuntimeException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.OverflowException;
import rebound.exceptions.UnreachableCodeException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.math.SmallIntegerMathUtilities;
import rebound.util.AngryReflectionUtility;
import rebound.util.BasicExceptionUtilities;
import rebound.util.ExceptionUtilities;
import rebound.util.collections.ArrayUtilities;
import rebound.util.functional.FunctionalInterfaces.NullaryFunction;
import rebound.util.functional.FunctionalInterfaces.UnaryFunction;
import rebound.util.functional.FunctionalInterfaces.UnaryFunctionBooleanToBoolean;
import rebound.util.functional.FunctionalInterfaces.UnaryFunctionByteToBoolean;
import rebound.util.functional.FunctionalInterfaces.UnaryFunctionCharToBoolean;
import rebound.util.functional.FunctionalInterfaces.UnaryFunctionDoubleToBoolean;
import rebound.util.functional.FunctionalInterfaces.UnaryFunctionFloatToBoolean;
import rebound.util.functional.FunctionalInterfaces.UnaryFunctionIntToBoolean;
import rebound.util.functional.FunctionalInterfaces.UnaryFunctionLongToBoolean;
import rebound.util.functional.FunctionalInterfaces.UnaryFunctionShortToBoolean;
import rebound.util.functional.FunctionalInterfaces.UnaryProcedure;
import rebound.util.objectutil.BasicObjectUtilities;
import rebound.util.objectutil.EqualityComparator;
import rebound.util.objectutil.JavaNamespace;
import rebound.util.objectutil.StrictReferenceIdentityEqualityComparator;

public class FunctionalUtilities
implements JavaNamespace
{
	public static final Runnable NoopNullaryProcedure = () -> {};
	
	public static final Predicate AlwaysTrue = a -> true;
	public static final Predicate AlwaysFalse = a -> false;
	
	
	
	
	
	public static Runnable methodHandleToFunctionalInterfaceForNullaryProcedure(MethodHandle h)
	{
		return () ->
		{
			try
			{
				h.invokeExact();
			}
			catch (Throwable t)
			{
				throw new WrappedThrowableRuntimeException(t);
			}
		};
	}
	
	
	public static NullaryFunction methodHandleToFunctionalInterfaceForNullaryFunction(MethodHandle h)
	{
		return () ->
		{
			try
			{
				return h.invokeExact();
			}
			catch (Throwable t)
			{
				throw new WrappedThrowableRuntimeException(t);
			}
		};
	}
	
	
	
	
	
	public static UnaryProcedure objectAcceptingMethodHandleToFunctionalInterfaceForUnaryProcedure(MethodHandle h)
	{
		return i ->
		{
			try
			{
				h.invokeExact(i);
			}
			catch (Throwable t)
			{
				throw new WrappedThrowableRuntimeException(t);
			}
		};
	}
	
	
	public static UnaryFunction objectAcceptingMethodHandleToFunctionalInterfaceForUnaryFunction(MethodHandle h)
	{
		return i ->
		{
			try
			{
				return h.invokeExact(i);
			}
			catch (Throwable t)
			{
				throw new WrappedThrowableRuntimeException(t);
			}
		};
	}
	
	
	
	
	
	public static UnaryProcedure objectOrPrimitiveAcceptingMethodHandleToFunctionalInterfaceForUnaryProcedure(MethodHandle h)
	{
		return i ->
		{
			try
			{
				h.invoke(i);
			}
			catch (Throwable t)
			{
				throw new WrappedThrowableRuntimeException(t);
			}
		};
	}
	
	
	public static UnaryFunction objectOrPrimitiveAcceptingMethodHandleToFunctionalInterfaceForUnaryFunction(MethodHandle h)
	{
		return i ->
		{
			try
			{
				return h.invoke(i);
			}
			catch (Throwable t)
			{
				throw new WrappedThrowableRuntimeException(t);
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	public static <E, S1 extends E, S2 extends E> E altnull(S1 value, S2 alternate)
	{
		return value != null ? value : alternate;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface ConvergentInPlaceOperationSinglePass<E>
	{
		/**
		 * @return if anything was changed (true) or it has convergedddd! :D  (false)
		 */
		public boolean f(E input);
	}
	
	/**
	 * This handles the case where expansions produce things which need to be further expanded! :o
	 * Which makes writing the expanding logic many easiers / more straight forward (if you can do it one layer, and just expect the codes to repeatedly apply the canonicalization over and over until it stops changing / converges!)  ^w^ :D
	 */
	public static <E> UnaryProcedure<E> wrapInRepeater(final ConvergentInPlaceOperationSinglePass<E> singlePassProcedure)
	{
		return new UnaryProcedure<E>
		()
		{
			@Override
			public void f(E input)
			{
				while (singlePassProcedure.f(input));
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface UnaryUnderliedFunctionR<F>
	{
		public F getUnderlyingFunction();
	}
	
	public static interface UnaryUnderliedFunctionW<F>
	{
		public void setUnderlyingFunction(F underlying);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Predicate<Object> instanceOfFunction(final Class cls)
	{
		return new Predicate<Object>
		()
		{
			@Override
			public boolean test(Object input)
			{
				return cls.isInstance(input);
			}
		};
	}
	
	
	
	//Type safe because making E anything more restricted than Object just makes the equals' method's job easier than normal! XD   (given it's already maximally liberal :>! )
	public static <E> Predicate<E> equalsPattern(Object matcher)
	{
		return new SingletonObjectEqualityPredicate(matcher);
	}
	
	public static <E> Predicate<E> equalsPattern(Object matcher, EqualityComparator equalityComparator)
	{
		//Fastersssss :3   (though we hope JIT makes this not so necessary right? X>' )
		if (equalityComparator instanceof StrictReferenceIdentityEqualityComparator)
			return identityPattern(matcher);
		
		return new SingletonObjectEqualityPredicate(matcher, equalityComparator);
	}
	
	public static <E> Predicate<E> identityPattern(final Object matcher)
	{
		return new SingletonObjectIdentityEqualityPredicate(matcher);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class SingletonObjectIdentityEqualityPredicate<Input>
	implements Predicate<Input>
	{
		protected final Input target;
		
		public SingletonObjectIdentityEqualityPredicate(Input target)
		{
			this.target = target;
		}
		
		public Input getTarget()
		{
			return this.target;
		}
		
		
		@Override
		public boolean test(Input input)
		{
			return input == this.target;
		}
	}
	
	
	public static class NaiveObjectArrayIdentitySearchPredicate<Input>
	implements Predicate<Input>
	{
		protected final Input[] targetArray;
		
		@EscapesVarargs
		public NaiveObjectArrayIdentitySearchPredicate(Input... targetArray)
		{
			this.targetArray = targetArray;
		}
		
		public Input[] getTargets()
		{
			return this.targetArray;
		}
		
		
		@Override
		public boolean test(final Input input)
		{
			final int targetArrayLength = this.targetArray.length;
			final Input[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (input == targetArray[i])
					return true;
			return false;
		}
	}
	
	
	
	
	public static class SingletonObjectEqualityPredicate<Input>
	implements Predicate<Input>
	{
		protected final Input target;
		protected final EqualityComparator<Input> equalityComparator;
		
		public SingletonObjectEqualityPredicate(Input target, EqualityComparator<Input> equalityComparator)
		{
			this.target = target;
			this.equalityComparator = equalityComparator;
		}
		
		public SingletonObjectEqualityPredicate(Input target)
		{
			this(target, (EqualityComparator)BasicObjectUtilities.getNaturalEqualityComparator());
		}
		
		public Input getTarget()
		{
			return this.target;
		}
		
		
		@Override
		public boolean test(Input input)
		{
			return this.equalityComparator.equals(input, this.target);
		}
	}
	
	
	public static class NaiveObjectArraySearchPredicate<Input>
	implements Predicate<Input>
	{
		protected final Input[] targetArray;
		protected final EqualityComparator<Input> equalityComparator;
		
		@EscapesVarargs
		public NaiveObjectArraySearchPredicate(EqualityComparator<Input> equalityComparator, Input... targetArray)
		{
			this.targetArray = targetArray;
			this.equalityComparator = equalityComparator;
		}
		
		@EscapesVarargs
		public NaiveObjectArraySearchPredicate(Input... targetArray)
		{
			this((EqualityComparator)BasicObjectUtilities.getNaturalEqualityComparator(), targetArray);
		}
		
		public Input[] getTargets()
		{
			return this.targetArray;
		}
		
		
		@Override
		public boolean test(final Input input)
		{
			final int targetArrayLength = this.targetArray.length;
			final Input[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (this.equalityComparator.equals(input, targetArray[i]))
					return true;
			return false;
		}
	}
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	
	public static interface Accessible_$$Prim$$_SetPredicate
	extends UnaryFunction_$$Prim$$_ToBoolean
	{
		@PossiblySnapshotPossiblyLiveValue
		@ReadonlyValue
		public _$$prim$$_[] getDefiningSetAsPOSSIBLYLIVEArray();
		
		@ThrowAwayValue
		public _$$prim$$_[] getDefiningSetAsThrowawayArray();
	}
	
	
	public static interface Accessible_$$Prim$$_SingleonPredicate
	extends UnaryFunction_$$Prim$$_ToBoolean, Accessible_$$Prim$$_SetPredicate
	{
		public _$$prim$$_ getTarget();
		
		@ThrowAwayValue
		@Override
		public default _$$prim$$_[] getDefiningSetAsThrowawayArray()
		{
			return new _$$prim$$_[]{getTarget()};
		}
	}
	
	
	
	
	public static class Singleton_$$Prim$$_EqualityPredicate
	implements Accessible_$$Prim$$_SingleonPredicate
	{
		protected final _$$prim$$_ target;
		
		public Singleton_$$Prim$$_EqualityPredicate(_$$prim$$_ target)
		{
			this.target = target;
		}
		
		public _$$prim$$_ getTarget()
		{
			return this.target;
		}
		
		@Override
		public boolean f(_$$prim$$_ input)
		{
			return input == target;
		}
		
		
		protected transient _$$prim$$_[] asArrayCache = null;
		
		
		@LiveValue
		@ReadonlyValue
		@Override
		public _$$prim$$_[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			_$$prim$$_[] a = this.asArrayCache;
			if (a == null)
			{
				a = new _$$prim$$_[]{getTarget()};
				this.asArrayCache = a;
			}
			return a;
		}
	}
	
	
	public static class Naive_$$Prim$$_ArraySearchPredicate
	implements Accessible_$$Prim$$_SetPredicate
	{
		protected final _$$prim$$_[] targetArray;
		
		@EscapesVarargs
		public Naive_$$Prim$$_ArraySearchPredicate(@LiveValue _$$prim$$_... targetArray)
		{
			this.targetArray = targetArray;
		}
		
		@LiveValue
		@ReadonlyValue
		@Override
		public _$$prim$$_[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			return this.targetArray;
		}
		
		@ThrowAwayValue
		@Override
		public _$$prim$$_[] getDefiningSetAsThrowawayArray()
		{
			return this.targetArray.clone();
		}
		
		@Override
		public boolean f(final _$$prim$$_ input)
		{
			final int targetArrayLength = this.targetArray.length;
			final _$$prim$$_[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (input == targetArray[i])
					return true;
			return false;
		}
	}
	
	
	
	
	 */
	
	
	
	public static interface AccessibleBooleanSetPredicate
	extends UnaryFunctionBooleanToBoolean
	{
		@PossiblySnapshotPossiblyLiveValue
		@ReadonlyValue
		public boolean[] getDefiningSetAsPOSSIBLYLIVEArray();
		
		@ThrowAwayValue
		public boolean[] getDefiningSetAsThrowawayArray();
	}
	
	
	public static interface AccessibleBooleanSingleonPredicate
	extends UnaryFunctionBooleanToBoolean, AccessibleBooleanSetPredicate
	{
		public boolean getTarget();
		
		@ThrowAwayValue
		@Override
		public default boolean[] getDefiningSetAsThrowawayArray()
		{
			return new boolean[]{getTarget()};
		}
	}
	
	
	
	
	public static class SingletonBooleanEqualityPredicate
	implements AccessibleBooleanSingleonPredicate
	{
		protected final boolean target;
		
		public SingletonBooleanEqualityPredicate(boolean target)
		{
			this.target = target;
		}
		
		@Override
		public boolean getTarget()
		{
			return this.target;
		}
		
		@Override
		public boolean f(boolean input)
		{
			return input == this.target;
		}
		
		
		protected transient boolean[] asArrayCache = null;
		
		
		@LiveValue
		@ReadonlyValue
		@Override
		public boolean[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			boolean[] a = this.asArrayCache;
			if (a == null)
			{
				a = new boolean[]{getTarget()};
				this.asArrayCache = a;
			}
			return a;
		}
	}
	
	
	public static class NaiveBooleanArraySearchPredicate
	implements AccessibleBooleanSetPredicate
	{
		protected final boolean[] targetArray;
		
		@EscapesVarargs
		public NaiveBooleanArraySearchPredicate(@LiveValue boolean... targetArray)
		{
			this.targetArray = targetArray;
		}
		
		@LiveValue
		@ReadonlyValue
		@Override
		public boolean[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			return this.targetArray;
		}
		
		@ThrowAwayValue
		@Override
		public boolean[] getDefiningSetAsThrowawayArray()
		{
			return this.targetArray.clone();
		}
		
		@Override
		public boolean f(final boolean input)
		{
			final int targetArrayLength = this.targetArray.length;
			final boolean[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (input == targetArray[i])
					return true;
			return false;
		}
	}
	
	
	
	
	
	
	
	public static interface AccessibleByteSetPredicate
	extends UnaryFunctionByteToBoolean
	{
		@PossiblySnapshotPossiblyLiveValue
		@ReadonlyValue
		public byte[] getDefiningSetAsPOSSIBLYLIVEArray();
		
		@ThrowAwayValue
		public byte[] getDefiningSetAsThrowawayArray();
	}
	
	
	public static interface AccessibleByteSingleonPredicate
	extends UnaryFunctionByteToBoolean, AccessibleByteSetPredicate
	{
		public byte getTarget();
		
		@ThrowAwayValue
		@Override
		public default byte[] getDefiningSetAsThrowawayArray()
		{
			return new byte[]{getTarget()};
		}
	}
	
	
	
	
	public static class SingletonByteEqualityPredicate
	implements AccessibleByteSingleonPredicate
	{
		protected final byte target;
		
		public SingletonByteEqualityPredicate(byte target)
		{
			this.target = target;
		}
		
		@Override
		public byte getTarget()
		{
			return this.target;
		}
		
		@Override
		public boolean f(byte input)
		{
			return input == this.target;
		}
		
		
		protected transient byte[] asArrayCache = null;
		
		
		@LiveValue
		@ReadonlyValue
		@Override
		public byte[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			byte[] a = this.asArrayCache;
			if (a == null)
			{
				a = new byte[]{getTarget()};
				this.asArrayCache = a;
			}
			return a;
		}
	}
	
	
	public static class NaiveByteArraySearchPredicate
	implements AccessibleByteSetPredicate
	{
		protected final byte[] targetArray;
		
		@EscapesVarargs
		public NaiveByteArraySearchPredicate(@LiveValue byte... targetArray)
		{
			this.targetArray = targetArray;
		}
		
		@LiveValue
		@ReadonlyValue
		@Override
		public byte[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			return this.targetArray;
		}
		
		@ThrowAwayValue
		@Override
		public byte[] getDefiningSetAsThrowawayArray()
		{
			return this.targetArray.clone();
		}
		
		@Override
		public boolean f(final byte input)
		{
			final int targetArrayLength = this.targetArray.length;
			final byte[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (input == targetArray[i])
					return true;
			return false;
		}
	}
	
	
	
	
	
	
	
	public static interface AccessibleCharSetPredicate
	extends UnaryFunctionCharToBoolean
	{
		@PossiblySnapshotPossiblyLiveValue
		@ReadonlyValue
		public char[] getDefiningSetAsPOSSIBLYLIVEArray();
		
		@ThrowAwayValue
		public char[] getDefiningSetAsThrowawayArray();
	}
	
	
	public static interface AccessibleCharSingleonPredicate
	extends UnaryFunctionCharToBoolean, AccessibleCharSetPredicate
	{
		public char getTarget();
		
		@ThrowAwayValue
		@Override
		public default char[] getDefiningSetAsThrowawayArray()
		{
			return new char[]{getTarget()};
		}
	}
	
	
	
	
	public static class SingletonCharEqualityPredicate
	implements AccessibleCharSingleonPredicate
	{
		protected final char target;
		
		public SingletonCharEqualityPredicate(char target)
		{
			this.target = target;
		}
		
		@Override
		public char getTarget()
		{
			return this.target;
		}
		
		@Override
		public boolean f(char input)
		{
			return input == this.target;
		}
		
		
		protected transient char[] asArrayCache = null;
		
		
		@LiveValue
		@ReadonlyValue
		@Override
		public char[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			char[] a = this.asArrayCache;
			if (a == null)
			{
				a = new char[]{getTarget()};
				this.asArrayCache = a;
			}
			return a;
		}
	}
	
	
	public static class NaiveCharArraySearchPredicate
	implements AccessibleCharSetPredicate
	{
		protected final char[] targetArray;
		
		@EscapesVarargs
		public NaiveCharArraySearchPredicate(@LiveValue char... targetArray)
		{
			this.targetArray = targetArray;
		}
		
		@LiveValue
		@ReadonlyValue
		@Override
		public char[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			return this.targetArray;
		}
		
		@ThrowAwayValue
		@Override
		public char[] getDefiningSetAsThrowawayArray()
		{
			return this.targetArray.clone();
		}
		
		@Override
		public boolean f(final char input)
		{
			final int targetArrayLength = this.targetArray.length;
			final char[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (input == targetArray[i])
					return true;
			return false;
		}
	}
	
	
	
	
	
	
	
	public static interface AccessibleShortSetPredicate
	extends UnaryFunctionShortToBoolean
	{
		@PossiblySnapshotPossiblyLiveValue
		@ReadonlyValue
		public short[] getDefiningSetAsPOSSIBLYLIVEArray();
		
		@ThrowAwayValue
		public short[] getDefiningSetAsThrowawayArray();
	}
	
	
	public static interface AccessibleShortSingleonPredicate
	extends UnaryFunctionShortToBoolean, AccessibleShortSetPredicate
	{
		public short getTarget();
		
		@ThrowAwayValue
		@Override
		public default short[] getDefiningSetAsThrowawayArray()
		{
			return new short[]{getTarget()};
		}
	}
	
	
	
	
	public static class SingletonShortEqualityPredicate
	implements AccessibleShortSingleonPredicate
	{
		protected final short target;
		
		public SingletonShortEqualityPredicate(short target)
		{
			this.target = target;
		}
		
		@Override
		public short getTarget()
		{
			return this.target;
		}
		
		@Override
		public boolean f(short input)
		{
			return input == this.target;
		}
		
		
		protected transient short[] asArrayCache = null;
		
		
		@LiveValue
		@ReadonlyValue
		@Override
		public short[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			short[] a = this.asArrayCache;
			if (a == null)
			{
				a = new short[]{getTarget()};
				this.asArrayCache = a;
			}
			return a;
		}
	}
	
	
	public static class NaiveShortArraySearchPredicate
	implements AccessibleShortSetPredicate
	{
		protected final short[] targetArray;
		
		@EscapesVarargs
		public NaiveShortArraySearchPredicate(@LiveValue short... targetArray)
		{
			this.targetArray = targetArray;
		}
		
		@LiveValue
		@ReadonlyValue
		@Override
		public short[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			return this.targetArray;
		}
		
		@ThrowAwayValue
		@Override
		public short[] getDefiningSetAsThrowawayArray()
		{
			return this.targetArray.clone();
		}
		
		@Override
		public boolean f(final short input)
		{
			final int targetArrayLength = this.targetArray.length;
			final short[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (input == targetArray[i])
					return true;
			return false;
		}
	}
	
	
	
	
	
	
	
	public static interface AccessibleFloatSetPredicate
	extends UnaryFunctionFloatToBoolean
	{
		@PossiblySnapshotPossiblyLiveValue
		@ReadonlyValue
		public float[] getDefiningSetAsPOSSIBLYLIVEArray();
		
		@ThrowAwayValue
		public float[] getDefiningSetAsThrowawayArray();
	}
	
	
	public static interface AccessibleFloatSingleonPredicate
	extends UnaryFunctionFloatToBoolean, AccessibleFloatSetPredicate
	{
		public float getTarget();
		
		@ThrowAwayValue
		@Override
		public default float[] getDefiningSetAsThrowawayArray()
		{
			return new float[]{getTarget()};
		}
	}
	
	
	
	
	public static class SingletonFloatEqualityPredicate
	implements AccessibleFloatSingleonPredicate
	{
		protected final float target;
		
		public SingletonFloatEqualityPredicate(float target)
		{
			this.target = target;
		}
		
		@Override
		public float getTarget()
		{
			return this.target;
		}
		
		@Override
		public boolean f(float input)
		{
			return input == this.target;
		}
		
		
		protected transient float[] asArrayCache = null;
		
		
		@LiveValue
		@ReadonlyValue
		@Override
		public float[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			float[] a = this.asArrayCache;
			if (a == null)
			{
				a = new float[]{getTarget()};
				this.asArrayCache = a;
			}
			return a;
		}
	}
	
	
	public static class NaiveFloatArraySearchPredicate
	implements AccessibleFloatSetPredicate
	{
		protected final float[] targetArray;
		
		@EscapesVarargs
		public NaiveFloatArraySearchPredicate(@LiveValue float... targetArray)
		{
			this.targetArray = targetArray;
		}
		
		@LiveValue
		@ReadonlyValue
		@Override
		public float[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			return this.targetArray;
		}
		
		@ThrowAwayValue
		@Override
		public float[] getDefiningSetAsThrowawayArray()
		{
			return this.targetArray.clone();
		}
		
		@Override
		public boolean f(final float input)
		{
			final int targetArrayLength = this.targetArray.length;
			final float[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (input == targetArray[i])
					return true;
			return false;
		}
	}
	
	
	
	
	
	
	
	public static interface AccessibleIntSetPredicate
	extends UnaryFunctionIntToBoolean
	{
		@PossiblySnapshotPossiblyLiveValue
		@ReadonlyValue
		public int[] getDefiningSetAsPOSSIBLYLIVEArray();
		
		@ThrowAwayValue
		public int[] getDefiningSetAsThrowawayArray();
	}
	
	
	public static interface AccessibleIntSingleonPredicate
	extends UnaryFunctionIntToBoolean, AccessibleIntSetPredicate
	{
		public int getTarget();
		
		@ThrowAwayValue
		@Override
		public default int[] getDefiningSetAsThrowawayArray()
		{
			return new int[]{getTarget()};
		}
	}
	
	
	
	
	public static class SingletonIntEqualityPredicate
	implements AccessibleIntSingleonPredicate
	{
		protected final int target;
		
		public SingletonIntEqualityPredicate(int target)
		{
			this.target = target;
		}
		
		@Override
		public int getTarget()
		{
			return this.target;
		}
		
		@Override
		public boolean f(int input)
		{
			return input == this.target;
		}
		
		
		protected transient int[] asArrayCache = null;
		
		
		@LiveValue
		@ReadonlyValue
		@Override
		public int[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			int[] a = this.asArrayCache;
			if (a == null)
			{
				a = new int[]{getTarget()};
				this.asArrayCache = a;
			}
			return a;
		}
	}
	
	
	public static class NaiveIntArraySearchPredicate
	implements AccessibleIntSetPredicate
	{
		protected final int[] targetArray;
		
		@EscapesVarargs
		public NaiveIntArraySearchPredicate(@LiveValue int... targetArray)
		{
			this.targetArray = targetArray;
		}
		
		@LiveValue
		@ReadonlyValue
		@Override
		public int[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			return this.targetArray;
		}
		
		@ThrowAwayValue
		@Override
		public int[] getDefiningSetAsThrowawayArray()
		{
			return this.targetArray.clone();
		}
		
		@Override
		public boolean f(final int input)
		{
			final int targetArrayLength = this.targetArray.length;
			final int[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (input == targetArray[i])
					return true;
			return false;
		}
	}
	
	
	
	
	
	
	
	public static interface AccessibleDoubleSetPredicate
	extends UnaryFunctionDoubleToBoolean
	{
		@PossiblySnapshotPossiblyLiveValue
		@ReadonlyValue
		public double[] getDefiningSetAsPOSSIBLYLIVEArray();
		
		@ThrowAwayValue
		public double[] getDefiningSetAsThrowawayArray();
	}
	
	
	public static interface AccessibleDoubleSingleonPredicate
	extends UnaryFunctionDoubleToBoolean, AccessibleDoubleSetPredicate
	{
		public double getTarget();
		
		@ThrowAwayValue
		@Override
		public default double[] getDefiningSetAsThrowawayArray()
		{
			return new double[]{getTarget()};
		}
	}
	
	
	
	
	public static class SingletonDoubleEqualityPredicate
	implements AccessibleDoubleSingleonPredicate
	{
		protected final double target;
		
		public SingletonDoubleEqualityPredicate(double target)
		{
			this.target = target;
		}
		
		@Override
		public double getTarget()
		{
			return this.target;
		}
		
		@Override
		public boolean f(double input)
		{
			return input == this.target;
		}
		
		
		protected transient double[] asArrayCache = null;
		
		
		@LiveValue
		@ReadonlyValue
		@Override
		public double[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			double[] a = this.asArrayCache;
			if (a == null)
			{
				a = new double[]{getTarget()};
				this.asArrayCache = a;
			}
			return a;
		}
	}
	
	
	public static class NaiveDoubleArraySearchPredicate
	implements AccessibleDoubleSetPredicate
	{
		protected final double[] targetArray;
		
		@EscapesVarargs
		public NaiveDoubleArraySearchPredicate(@LiveValue double... targetArray)
		{
			this.targetArray = targetArray;
		}
		
		@LiveValue
		@ReadonlyValue
		@Override
		public double[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			return this.targetArray;
		}
		
		@ThrowAwayValue
		@Override
		public double[] getDefiningSetAsThrowawayArray()
		{
			return this.targetArray.clone();
		}
		
		@Override
		public boolean f(final double input)
		{
			final int targetArrayLength = this.targetArray.length;
			final double[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (input == targetArray[i])
					return true;
			return false;
		}
	}
	
	
	
	
	
	
	
	public static interface AccessibleLongSetPredicate
	extends UnaryFunctionLongToBoolean
	{
		@PossiblySnapshotPossiblyLiveValue
		@ReadonlyValue
		public long[] getDefiningSetAsPOSSIBLYLIVEArray();
		
		@ThrowAwayValue
		public long[] getDefiningSetAsThrowawayArray();
	}
	
	
	public static interface AccessibleLongSingleonPredicate
	extends UnaryFunctionLongToBoolean, AccessibleLongSetPredicate
	{
		public long getTarget();
		
		@ThrowAwayValue
		@Override
		public default long[] getDefiningSetAsThrowawayArray()
		{
			return new long[]{getTarget()};
		}
	}
	
	
	
	
	public static class SingletonLongEqualityPredicate
	implements AccessibleLongSingleonPredicate
	{
		protected final long target;
		
		public SingletonLongEqualityPredicate(long target)
		{
			this.target = target;
		}
		
		@Override
		public long getTarget()
		{
			return this.target;
		}
		
		@Override
		public boolean f(long input)
		{
			return input == this.target;
		}
		
		
		protected transient long[] asArrayCache = null;
		
		
		@LiveValue
		@ReadonlyValue
		@Override
		public long[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			long[] a = this.asArrayCache;
			if (a == null)
			{
				a = new long[]{getTarget()};
				this.asArrayCache = a;
			}
			return a;
		}
	}
	
	
	public static class NaiveLongArraySearchPredicate
	implements AccessibleLongSetPredicate
	{
		protected final long[] targetArray;
		
		@EscapesVarargs
		public NaiveLongArraySearchPredicate(@LiveValue long... targetArray)
		{
			this.targetArray = targetArray;
		}
		
		@LiveValue
		@ReadonlyValue
		@Override
		public long[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			return this.targetArray;
		}
		
		@ThrowAwayValue
		@Override
		public long[] getDefiningSetAsThrowawayArray()
		{
			return this.targetArray.clone();
		}
		
		@Override
		public boolean f(final long input)
		{
			final int targetArrayLength = this.targetArray.length;
			final long[] targetArray = this.targetArray;
			for (int i = 0; i < targetArrayLength; i++)
				if (input == targetArray[i])
					return true;
			return false;
		}
	}
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	python
	
	prims = newSubdict(primxp.AllPrims, ["byte", "short", "char", "int"]);
	
	p(primxp.primxp(prims=prims, source="""
	
	public static class Exhaustive_$$Prim$$_HitMapPredicate
	implements Accessible_$$Prim$$_SetPredicate
	{
		protected final boolean[] hitMap;
		protected final int hitMapLogicalStart;
		
		@NotEscapedVarargs
		public Exhaustive_$$Prim$$_HitMapPredicate(@ReadonlyValue _$$prim$$_... targets)
		{
			if (targets.length == 0)
			{
				this.hitMap = EmptyBooleanArray;
				this.hitMapLogicalStart = 0;
			}
			else
			{
				int min = least(targets);
				int max = greatest(targets);
				
				int count = max-min+1;
				
				if (count < 0)
					throw new OverflowException();
				
				hitMap = new boolean[count];  //java initializes to false for us ^_^, which is precisely what we wants in this case! :D
				hitMapLogicalStart = min;
				
				for (_$$prim$$_ target : targets)
				{
					hitMap[target - hitMapLogicalStart] = true;
				}
			}
		}
		
		public Exhaustive_$$Prim$$_HitMapPredicate(boolean[] hitMap, _$$prim$$_ hitMapLogicalStart)
		{
			this.hitMap = hitMap;
			this.hitMapLogicalStart = hitMapLogicalStart;
		}
		
		@ThrowAwayValue
		public boolean[] getHitMapClone()
		{
			return this.hitMap.clone();
		}
		
		@LiveValue
		public boolean[] getHitMapLIVE()
		{
			return this.hitMap;
		}
		
		public _$$prim$$_ getHitMapLogicalStart()
		{
			return (_$$prim$$_)this.hitMapLogicalStart;
		}
		
		
		@Override
		public boolean f(final _$$prim$$_ input)
		{
			final int hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			//if (input < hitMapLogicalStart || input >= hitMapLogicalStart+hitMapLength) //(theoretical) overflow issues x>
			if (input < hitMapLogicalStart || input - hitMapLogicalStart >= hitMapLength)
				return false;
			
			return this.hitMap[input - hitMapLogicalStart];
		}
		
		
		
		
		
		protected transient _$$prim$$_[] definingSet = null;
		
		@LiveValue
		@ReadonlyValue
		@Override
		public _$$prim$$_[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			_$$prim$$_[] set = this.definingSet;
			
			if (set == null)
			{
				set = makeDefiningSet();
				this.definingSet = set;
			}
			
			return set;
		}
		
		@ThrowAwayValue
		@Override
		public _$$prim$$_[] getDefiningSetAsThrowawayArray()
		{
			//Don't make it then clone it immediately! Like below! X'D
			//	return getDefiningSetAsPOSSIBLYLIVEArray().clone();  //not this XDD'
			
			//Instead this :3
			_$$prim$$_[] set = this.definingSet;
			
			if (set == null)
			{
				return makeDefiningSet();
			}
			else
			{
				return set;
			}
		}
		
		protected _$$prim$$_[] makeDefiningSet()
		{
			final int hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			_$$prim$$_[] set = new _$$prim$$_[hitMapLength];
			int count = 0;
			
			
			for (int i = 0; i < hitMapLength; i++)
			{
				if (hitMap[i] == true)
				{
					set[count] = (_$$prim$$_)(hitMapLogicalStart + i);
					count++;
				}
			}
			
			//Trim it! :>
			if (count != hitMapLength)
			{
				_$$prim$$_[] newSet = new _$$prim$$_[count];
				System.arraycopy(set, 0, newSet, 0, count);
				set = newSet;
			}
			
			return set;
		}
	}
	
	
	
	
	
	"""));
	 */
	
	
	public static class ExhaustiveByteHitMapPredicate
	implements AccessibleByteSetPredicate
	{
		protected final boolean[] hitMap;
		protected final int hitMapLogicalStart;
		
		@NotEscapedVarargs
		public ExhaustiveByteHitMapPredicate(@ReadonlyValue byte... targets)
		{
			if (targets.length == 0)
			{
				this.hitMap = EmptyBooleanArray;
				this.hitMapLogicalStart = 0;
			}
			else
			{
				int min = least(targets);
				int max = greatest(targets);
				
				int count = max-min+1;
				
				if (count < 0)
					throw new OverflowException();
				
				this.hitMap = new boolean[count];  //java initializes to false for us ^_^, which is precisely what we wants in this case! :D
				this.hitMapLogicalStart = min;
				
				for (byte target : targets)
				{
					this.hitMap[target - this.hitMapLogicalStart] = true;
				}
			}
		}
		
		public ExhaustiveByteHitMapPredicate(boolean[] hitMap, byte hitMapLogicalStart)
		{
			this.hitMap = hitMap;
			this.hitMapLogicalStart = hitMapLogicalStart;
		}
		
		@ThrowAwayValue
		public boolean[] getHitMapClone()
		{
			return this.hitMap.clone();
		}
		
		@LiveValue
		public boolean[] getHitMapLIVE()
		{
			return this.hitMap;
		}
		
		public byte getHitMapLogicalStart()
		{
			return (byte)this.hitMapLogicalStart;
		}
		
		
		@Override
		public boolean f(final byte input)
		{
			final int hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			//if (input < hitMapLogicalStart || input >= hitMapLogicalStart+hitMapLength) //(theoretical) overflow issues x>
			if (input < hitMapLogicalStart || input - hitMapLogicalStart >= hitMapLength)
				return false;
			
			return this.hitMap[input - hitMapLogicalStart];
		}
		
		
		
		
		
		protected transient byte[] definingSet = null;
		
		@LiveValue
		@ReadonlyValue
		@Override
		public byte[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			byte[] set = this.definingSet;
			
			if (set == null)
			{
				set = makeDefiningSet();
				this.definingSet = set;
			}
			
			return set;
		}
		
		@ThrowAwayValue
		@Override
		public byte[] getDefiningSetAsThrowawayArray()
		{
			//Don't make it then clone it immediately! Like below! X'D
			//	return getDefiningSetAsPOSSIBLYLIVEArray().clone();  //not this XDD'
			
			//Instead this :3
			byte[] set = this.definingSet;
			
			if (set == null)
			{
				return makeDefiningSet();
			}
			else
			{
				return set;
			}
		}
		
		protected byte[] makeDefiningSet()
		{
			final int hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			byte[] set = new byte[hitMapLength];
			int count = 0;
			
			
			for (int i = 0; i < hitMapLength; i++)
			{
				if (this.hitMap[i] == true)
				{
					set[count] = (byte)(hitMapLogicalStart + i);
					count++;
				}
			}
			
			//Trim it! :>
			if (count != hitMapLength)
			{
				byte[] newSet = new byte[count];
				System.arraycopy(set, 0, newSet, 0, count);
				set = newSet;
			}
			
			return set;
		}
	}
	
	
	
	
	
	
	
	public static class ExhaustiveCharHitMapPredicate
	implements AccessibleCharSetPredicate
	{
		protected final boolean[] hitMap;
		protected final int hitMapLogicalStart;
		
		@NotEscapedVarargs
		public ExhaustiveCharHitMapPredicate(@ReadonlyValue char... targets)
		{
			if (targets.length == 0)
			{
				this.hitMap = EmptyBooleanArray;
				this.hitMapLogicalStart = 0;
			}
			else
			{
				int min = least(targets);
				int max = greatest(targets);
				
				int count = max-min+1;
				
				if (count < 0)
					throw new OverflowException();
				
				this.hitMap = new boolean[count];  //java initializes to false for us ^_^, which is precisely what we wants in this case! :D
				this.hitMapLogicalStart = min;
				
				for (char target : targets)
				{
					this.hitMap[target - this.hitMapLogicalStart] = true;
				}
			}
		}
		
		public ExhaustiveCharHitMapPredicate(boolean[] hitMap, char hitMapLogicalStart)
		{
			this.hitMap = hitMap;
			this.hitMapLogicalStart = hitMapLogicalStart;
		}
		
		@ThrowAwayValue
		public boolean[] getHitMapClone()
		{
			return this.hitMap.clone();
		}
		
		@LiveValue
		public boolean[] getHitMapLIVE()
		{
			return this.hitMap;
		}
		
		public char getHitMapLogicalStart()
		{
			return (char)this.hitMapLogicalStart;
		}
		
		
		@Override
		public boolean f(final char input)
		{
			final int hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			//if (input < hitMapLogicalStart || input >= hitMapLogicalStart+hitMapLength) //(theoretical) overflow issues x>
			if (input < hitMapLogicalStart || input - hitMapLogicalStart >= hitMapLength)
				return false;
			
			return this.hitMap[input - hitMapLogicalStart];
		}
		
		
		
		
		
		protected transient char[] definingSet = null;
		
		@LiveValue
		@ReadonlyValue
		@Override
		public char[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			char[] set = this.definingSet;
			
			if (set == null)
			{
				set = makeDefiningSet();
				this.definingSet = set;
			}
			
			return set;
		}
		
		@ThrowAwayValue
		@Override
		public char[] getDefiningSetAsThrowawayArray()
		{
			//Don't make it then clone it immediately! Like below! X'D
			//	return getDefiningSetAsPOSSIBLYLIVEArray().clone();  //not this XDD'
			
			//Instead this :3
			char[] set = this.definingSet;
			
			if (set == null)
			{
				return makeDefiningSet();
			}
			else
			{
				return set;
			}
		}
		
		protected char[] makeDefiningSet()
		{
			final int hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			char[] set = new char[hitMapLength];
			int count = 0;
			
			
			for (int i = 0; i < hitMapLength; i++)
			{
				if (this.hitMap[i] == true)
				{
					set[count] = (char)(hitMapLogicalStart + i);
					count++;
				}
			}
			
			//Trim it! :>
			if (count != hitMapLength)
			{
				char[] newSet = new char[count];
				System.arraycopy(set, 0, newSet, 0, count);
				set = newSet;
			}
			
			return set;
		}
	}
	
	
	
	
	
	
	
	public static class ExhaustiveShortHitMapPredicate
	implements AccessibleShortSetPredicate
	{
		protected final boolean[] hitMap;
		protected final int hitMapLogicalStart;
		
		@NotEscapedVarargs
		public ExhaustiveShortHitMapPredicate(@ReadonlyValue short... targets)
		{
			if (targets.length == 0)
			{
				this.hitMap = EmptyBooleanArray;
				this.hitMapLogicalStart = 0;
			}
			else
			{
				int min = least(targets);
				int max = greatest(targets);
				
				int count = max-min+1;
				
				if (count < 0)
					throw new OverflowException();
				
				this.hitMap = new boolean[count];  //java initializes to false for us ^_^, which is precisely what we wants in this case! :D
				this.hitMapLogicalStart = min;
				
				for (short target : targets)
				{
					this.hitMap[target - this.hitMapLogicalStart] = true;
				}
			}
		}
		
		public ExhaustiveShortHitMapPredicate(boolean[] hitMap, short hitMapLogicalStart)
		{
			this.hitMap = hitMap;
			this.hitMapLogicalStart = hitMapLogicalStart;
		}
		
		@ThrowAwayValue
		public boolean[] getHitMapClone()
		{
			return this.hitMap.clone();
		}
		
		@LiveValue
		public boolean[] getHitMapLIVE()
		{
			return this.hitMap;
		}
		
		public short getHitMapLogicalStart()
		{
			return (short)this.hitMapLogicalStart;
		}
		
		
		@Override
		public boolean f(final short input)
		{
			final int hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			//if (input < hitMapLogicalStart || input >= hitMapLogicalStart+hitMapLength) //(theoretical) overflow issues x>
			if (input < hitMapLogicalStart || input - hitMapLogicalStart >= hitMapLength)
				return false;
			
			return this.hitMap[input - hitMapLogicalStart];
		}
		
		
		
		
		
		protected transient short[] definingSet = null;
		
		@LiveValue
		@ReadonlyValue
		@Override
		public short[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			short[] set = this.definingSet;
			
			if (set == null)
			{
				set = makeDefiningSet();
				this.definingSet = set;
			}
			
			return set;
		}
		
		@ThrowAwayValue
		@Override
		public short[] getDefiningSetAsThrowawayArray()
		{
			//Don't make it then clone it immediately! Like below! X'D
			//	return getDefiningSetAsPOSSIBLYLIVEArray().clone();  //not this XDD'
			
			//Instead this :3
			short[] set = this.definingSet;
			
			if (set == null)
			{
				return makeDefiningSet();
			}
			else
			{
				return set;
			}
		}
		
		protected short[] makeDefiningSet()
		{
			final int hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			short[] set = new short[hitMapLength];
			int count = 0;
			
			
			for (int i = 0; i < hitMapLength; i++)
			{
				if (this.hitMap[i] == true)
				{
					set[count] = (short)(hitMapLogicalStart + i);
					count++;
				}
			}
			
			//Trim it! :>
			if (count != hitMapLength)
			{
				short[] newSet = new short[count];
				System.arraycopy(set, 0, newSet, 0, count);
				set = newSet;
			}
			
			return set;
		}
	}
	
	
	
	
	
	
	
	public static class ExhaustiveIntHitMapPredicate
	implements AccessibleIntSetPredicate
	{
		protected final boolean[] hitMap;
		protected final int hitMapLogicalStart;
		
		@NotEscapedVarargs
		public ExhaustiveIntHitMapPredicate(@ReadonlyValue int... targets)
		{
			if (targets.length == 0)
			{
				this.hitMap = EmptyBooleanArray;
				this.hitMapLogicalStart = 0;
			}
			else
			{
				int min = least(targets);
				int max = greatest(targets);
				
				int count = max-min+1;
				
				if (count < 0)
					throw new OverflowException();
				
				this.hitMap = new boolean[count];  //java initializes to false for us ^_^, which is precisely what we wants in this case! :D
				this.hitMapLogicalStart = min;
				
				for (int target : targets)
				{
					this.hitMap[target - this.hitMapLogicalStart] = true;
				}
			}
		}
		
		public ExhaustiveIntHitMapPredicate(boolean[] hitMap, int hitMapLogicalStart)
		{
			this.hitMap = hitMap;
			this.hitMapLogicalStart = hitMapLogicalStart;
		}
		
		@ThrowAwayValue
		public boolean[] getHitMapClone()
		{
			return this.hitMap.clone();
		}
		
		@LiveValue
		public boolean[] getHitMapLIVE()
		{
			return this.hitMap;
		}
		
		public int getHitMapLogicalStart()
		{
			return this.hitMapLogicalStart;
		}
		
		
		@Override
		public boolean f(final int input)
		{
			final int hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			//if (input < hitMapLogicalStart || input >= hitMapLogicalStart+hitMapLength) //(theoretical) overflow issues x>
			if (input < hitMapLogicalStart || input - hitMapLogicalStart >= hitMapLength)
				return false;
			
			return this.hitMap[input - hitMapLogicalStart];
		}
		
		
		
		
		
		protected transient int[] definingSet = null;
		
		@LiveValue
		@ReadonlyValue
		@Override
		public int[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			int[] set = this.definingSet;
			
			if (set == null)
			{
				set = makeDefiningSet();
				this.definingSet = set;
			}
			
			return set;
		}
		
		@ThrowAwayValue
		@Override
		public int[] getDefiningSetAsThrowawayArray()
		{
			//Don't make it then clone it immediately! Like below! X'D
			//	return getDefiningSetAsPOSSIBLYLIVEArray().clone();  //not this XDD'
			
			//Instead this :3
			int[] set = this.definingSet;
			
			if (set == null)
			{
				return makeDefiningSet();
			}
			else
			{
				return set;
			}
		}
		
		protected int[] makeDefiningSet()
		{
			final int hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			int[] set = new int[hitMapLength];
			int count = 0;
			
			
			for (int i = 0; i < hitMapLength; i++)
			{
				if (this.hitMap[i] == true)
				{
					set[count] = hitMapLogicalStart + i;
					count++;
				}
			}
			
			//Trim it! :>
			if (count != hitMapLength)
			{
				int[] newSet = new int[count];
				System.arraycopy(set, 0, newSet, 0, count);
				set = newSet;
			}
			
			return set;
		}
	}
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	public static class ExhaustiveLongHitMapPredicate
	implements AccessibleLongSetPredicate
	{
		protected final boolean[] hitMap;
		protected final long hitMapLogicalStart;
		
		@NotEscapedVarargs
		public ExhaustiveLongHitMapPredicate(@ReadonlyValue long... targets)
		{
			if (targets.length == 0)
			{
				this.hitMap = ArrayUtilities.EmptyBooleanArray;
				this.hitMapLogicalStart = 0;
			}
			else
			{
				long min = SmallIntegerMathUtilities.least(targets);
				long max = SmallIntegerMathUtilities.greatest(targets);
				
				long count = max-min+1;
				
				if (count < 0)
					throw new OverflowException();
				if (count > Integer.MAX_VALUE)
					throw new OverflowException();
				
				
				this.hitMap = new boolean[(int)count];  //java initializes to false for us ^_^, which is precisely what we wants in this case! :D
				this.hitMapLogicalStart = min;
				
				for (long target : targets)
				{
					this.hitMap[(int)(target - this.hitMapLogicalStart)] = true;
				}
			}
		}
		
		public ExhaustiveLongHitMapPredicate(boolean[] hitMap, long hitMapLogicalStart)
		{
			this.hitMap = hitMap;
			this.hitMapLogicalStart = hitMapLogicalStart;
		}
		
		@ThrowAwayValue
		public boolean[] getHitMapClone()
		{
			return this.hitMap.clone();
		}
		
		@LiveValue
		public boolean[] getHitMapLIVE()
		{
			return this.hitMap;
		}
		
		public long getHitMapLogicalStart()
		{
			return this.hitMapLogicalStart;
		}
		
		
		@Override
		public boolean f(final long input)
		{
			final long hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			//if (input < hitMapLogicalStart || input >= hitMapLogicalStart+hitMapLength) //(theoretical) overflow issues x>
			if (input < hitMapLogicalStart || input - hitMapLogicalStart >= hitMapLength)
				return false;
			
			return this.hitMap[(int)(input - hitMapLogicalStart)];
		}
		
		
		
		
		
		protected transient long[] definingSet = null;
		
		@LiveValue
		@ReadonlyValue
		@Override
		public long[] getDefiningSetAsPOSSIBLYLIVEArray()
		{
			long[] set = this.definingSet;
			
			if (set == null)
			{
				set = makeDefiningSet();
				this.definingSet = set;
			}
			
			return set;
		}
		
		@ThrowAwayValue
		@Override
		public long[] getDefiningSetAsThrowawayArray()
		{
			//Don't make it then clone it immediately! Like below! X'D
			//	return getDefiningSetAsPOSSIBLYLIVEArray().clone();  //not this XDD'
			
			//Instead this :3
			long[] set = this.definingSet;
			
			if (set == null)
			{
				return makeDefiningSet();
			}
			else
			{
				return set;
			}
		}
		
		protected long[] makeDefiningSet()
		{
			final long hitMapLogicalStart = this.hitMapLogicalStart;
			final int hitMapLength = this.hitMap.length;
			
			long[] set = new long[hitMapLength];
			int count = 0;
			
			
			for (int i = 0; i < hitMapLength; i++)
			{
				if (this.hitMap[i] == true)
				{
					set[count] = hitMapLogicalStart + i;
					count++;
				}
			}
			
			//Trim it! :>
			if (count != hitMapLength)
			{
				long[] newSet = new long[count];
				System.arraycopy(set, 0, newSet, 0, count);
				set = newSet;
			}
			
			return set;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @return number of parameters, or -1 for varargs! :D
	 */
	public static int getNumberOfParameters(Object function)
	{
		//TODO
		throw new NotYetImplementedException();
	}
	
	
	/**
	 * note: returns null for Void-returning functions :>
	 */
	public static Object apply(Object function, Object... inputs)
	{
		//TODO
		throw new NotYetImplementedException();
	}
	
	
	/*
	public static Object apply_$$Prim$$_(Object function, Object... inputs)
	{
		//TODO
		throw new NotYetImplementedException();
	}
	
	public static Object apply_$$Prim$$_(Object function, _$$esc$$_prim$$_ input)
	{
		//TODO
		throw new NotYetImplementedException();
	}
	 */
	
	
	public static class OverloadedMethodException
	extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		
		public OverloadedMethodException()
		{
		}
		
		public OverloadedMethodException(String message)
		{
			super(message);
		}
		
		public OverloadedMethodException(Throwable cause)
		{
			super(cause);
		}
		
		public OverloadedMethodException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
	
	
	public static Object lookupAndApplyNonoverloadedInstanceMethod(Object self, String instanceMethodName, Object... arguments) throws OverloadedMethodException, NoSuchMemberRuntimeException
	{
		MethodHandle unpartialled = lookupVirtualNonoverloadedMethodOnObject(self, instanceMethodName);
		
		MethodHandle partialled = unpartialled.bindTo(self);
		
		try
		{
			return partialled.invokeWithArguments(arguments);
		}
		catch (Throwable exc)
		{
			ExceptionUtilities.throwGeneralThrowableAttemptingUnverifiedThrow(exc);
			throw new UnreachableCodeException();
		}
	}
	
	@Nonnull
	public static MethodHandle lookupVirtualNonoverloadedMethodOnObject(Object self, String instanceMethodName) throws OverloadedMethodException, NoSuchMemberRuntimeException
	{
		if (self == null)
			throw new NoSuchMemberRuntimeException("Null's 'class' has no methods or any members >,>  xD");
		
		
		Method theMethod = null;
		
		for (Method m : self.getClass().getMethods())
		{
			if (!Modifier.isStatic(m.getModifiers()))
			{
				if (m.getName().equals(instanceMethodName))
				{
					if (theMethod == null)
						theMethod = m;
					else
						throw new OverloadedMethodException("<a "+self.getClass().getName()+">."+instanceMethodName+"(*)");
				}
			}
		}
		
		if (theMethod == null)
			throw new NoSuchMemberRuntimeException();
		
		try
		{
			return MethodHandles.publicLookup().unreflect(theMethod);
		}
		catch (IllegalAccessException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	
	
	public static Object lookupAndApplyNonoverloadedStaticMethod(Class namespace, String staticMethodName, Object... arguments) throws OverloadedMethodException, NoSuchMemberRuntimeException
	{
		MethodHandle f = lookupStaticNonoverloadedMethod(namespace, staticMethodName);
		try
		{
			return f.invokeWithArguments(arguments);
		}
		catch (Throwable exc)
		{
			ExceptionUtilities.throwGeneralThrowableAttemptingUnverifiedThrow(exc);
			throw new UnreachableCodeException();
		}
	}
	
	@Nonnull
	public static MethodHandle lookupStaticNonoverloadedMethod(Class namespace, String staticMethodName) throws OverloadedMethodException, NoSuchMemberRuntimeException
	{
		if (namespace == null)
			throw new NullPointerException();
		
		if ("<init>".equals(staticMethodName))
		{
			Constructor[] constructors = namespace.getConstructors();
			
			if (constructors.length == 0)
				throw new NoSuchMemberRuntimeException();
			else if (constructors.length > 1)
				throw new OverloadedMethodException("more than one <init> (constructor)!");
			else
			{
				try
				{
					return MethodHandles.publicLookup().unreflectConstructor(constructors[0]);
				}
				catch (IllegalAccessException exc)
				{
					throw new WrappedThrowableRuntimeException(exc);
				}
			}
		}
		else
		{
			Method theMethod = null;
			
			for (Method m : namespace.getDeclaredMethods())
			{
				if (Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()))
				{
					if (m.getName().equals(staticMethodName))
					{
						if (theMethod == null)
							theMethod = m;
						else
							throw new OverloadedMethodException("static "+namespace.getName()+"."+staticMethodName+"(*)");
					}
				}
			}
			
			if (theMethod == null)
				throw new NoSuchMemberRuntimeException();
			
			try
			{
				return MethodHandles.publicLookup().unreflect(theMethod);
			}
			catch (IllegalAccessException exc)
			{
				throw new WrappedThrowableRuntimeException(exc);
			}
		}
	}
	
	
	@Nonnull
	public static MethodHandle lookupInstanceNonoverloadedMethod(Class namespace, String instanceMethodName) throws OverloadedMethodException, NoSuchMemberRuntimeException
	{
		if (namespace == null)
			throw new NullPointerException();
		
		
		Method theMethod = null;
		
		for (Method m : namespace.getDeclaredMethods())
		{
			if (!Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()))
			{
				if (m.getName().equals(instanceMethodName))
				{
					if (theMethod == null)
						theMethod = m;
					else
						throw new OverloadedMethodException(namespace.getName()+"."+instanceMethodName+"(*)");
				}
			}
		}
		
		if (theMethod == null)
			throw new NoSuchMemberRuntimeException();
		
		try
		{
			return MethodHandles.publicLookup().unreflect(theMethod);
		}
		catch (IllegalAccessException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	
	
	@Nonnull
	public static MethodHandle mh(Class namespace, String methodName) throws OverloadedMethodException, NoSuchMemberRuntimeException
	{
		if (namespace == null)
			throw new NullPointerException();
		
		
		Method theMethod = null;
		
		for (Method m : namespace.getDeclaredMethods())
		{
			if (m.getName().equals(methodName))
			{
				if (theMethod == null)
					theMethod = m;
				else
					throw new OverloadedMethodException(namespace.getName()+"."+methodName+"(*)");
			}
		}
		
		if (theMethod == null)
			throw new NoSuchMemberRuntimeException();
		
		try
		{
			return MethodHandles.publicLookup().unreflect(theMethod);
		}
		catch (IllegalAccessException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	
	/**
	 * For {@link Method}s, {@link Constructor}s, and {@link Field}s as getters :>
	 */
	@Nonnull
	public static MethodHandle mh(Member m)
	{
		try
		{
			if (m instanceof Method)
				return MethodHandles.publicLookup().unreflect((Method)m);
			else if (m instanceof Constructor)
				return MethodHandles.publicLookup().unreflectConstructor((Constructor)m);
			else if (m instanceof Field)
				return MethodHandles.publicLookup().unreflectGetter((Field)m);
			else
				throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(m);
		}
		catch (IllegalAccessException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	
	
	
	
	
	public static interface ProvidesPrimaryMethodHandle
	{
		@ConstantReturnValue
		public MethodHandle getUnboundMethodHandle();
		
		@ConstantReturnValue
		public MethodHandle getBoundMethodHandle();
		
		public static abstract class AbstractProvidesPrimaryMethodHandle
		implements ProvidesPrimaryMethodHandle
		{
			protected final MethodHandle boundPrimary = getUnboundMethodHandle().bindTo(this);
			
			@Override
			public MethodHandle getBoundMethodHandle()
			{
				return boundPrimary;
			}
		}
	}
	
	
	
	
	protected static final Map<Class, MethodHandle> AccumulatedCacheOfSuccessfulMethodHandlesOfDiscoveredSingleFunctionInterfaces = new HashMap<>();
	
	public static Method findSingleFunctionInSingleFunctionInterface(Class interfaceClass)
	{
		if (!interfaceClass.isInterface())
			return null;
		
		Method theOnlyNonObjectMethod = null;
		
		for (Method m : interfaceClass.getDeclaredMethods())
		{
			Class[] parameterTypes = m.getParameterTypes();
			
			boolean overridesAnObjectMethod = false;
			{
				for (Method objectMethod : Object.class.getDeclaredMethods())
				{
					if (m.getName().equals(objectMethod.getName()) && Arrays.equals(parameterTypes, objectMethod.getParameterTypes()))
					{
						overridesAnObjectMethod = true;
						break;
					}
				}
			}
			
			if (overridesAnObjectMethod)
				continue;
			
			if (theOnlyNonObjectMethod == null)
				theOnlyNonObjectMethod = m;
			else
				return null; //more than one (non-java.lang.Object) method defined in the interface; so is not a single-function interface XP
		}
		
		return theOnlyNonObjectMethod;
	}
	
	public static MethodHandle getUnboundMethodHandleForSingleFunctionInterface(Object instanceOfSingleFunctionInterface) throws NoSuchMemberRuntimeException
	{
		if (instanceOfSingleFunctionInterface == null)
			throw new NoSuchMemberRuntimeException("null type doesn't implement any interfaces of course! XD");
		
		
		//Nice override :>
		if (instanceOfSingleFunctionInterface instanceof ProvidesPrimaryMethodHandle)
			return ((ProvidesPrimaryMethodHandle)instanceOfSingleFunctionInterface).getUnboundMethodHandle();
		
		
		//Default impl
		if (instanceOfSingleFunctionInterface instanceof Runnable)
			return Runnable_run;
		
		//Default default impl XD''  (many slowwwww)
		{
			Class runtimeClass = instanceOfSingleFunctionInterface.getClass();
			
			synchronized (AccumulatedCacheOfSuccessfulMethodHandlesOfDiscoveredSingleFunctionInterfaces)
			{
				//Try the runtime class itself!
				{
					MethodHandle h = AccumulatedCacheOfSuccessfulMethodHandlesOfDiscoveredSingleFunctionInterfaces.get(runtimeClass);
					
					if (h != null)
						return h;
				}
				
				
				Set<Class> allInterfaces = AngryReflectionUtility.getAllInterfaces(runtimeClass);
				
				
				//Try all the implemented interfaces!
				{
					for (Class implementedInterfaceClass : allInterfaces)
					{
						MethodHandle h = AccumulatedCacheOfSuccessfulMethodHandlesOfDiscoveredSingleFunctionInterfaces.get(implementedInterfaceClass);
						
						if (h != null)
						{
							//Register the runtime class as well, so it will have a quicker discovery of it in the future   //Todo is this a good idea?? :\
							AccumulatedCacheOfSuccessfulMethodHandlesOfDiscoveredSingleFunctionInterfaces.put(runtimeClass, h);
							
							return h;
						}
					}
				}
				
				
				
				//Find it from scratch!
				{
					Method theOneOneMethod = null;
					MethodHandle theOneOneMethodHandle = null;
					{
						for (Class implementedInterfaceClass : allInterfaces)
						{
							Method m = findSingleFunctionInSingleFunctionInterface(implementedInterfaceClass);
							
							if (m == null)
								continue;
							
							MethodHandle h = mh(m);
							
							//Register it one way or another, since we've discovered a legit single-function interface!  Whether or not it turns out to be the right one XD'
							AccumulatedCacheOfSuccessfulMethodHandlesOfDiscoveredSingleFunctionInterfaces.put(implementedInterfaceClass, h);
							
							if (theOneOneMethod == null)
							{
								theOneOneMethod = m;
								theOneOneMethodHandle = h;
							}
							else if (m.getName().equals(theOneOneMethod.getName()) && Arrays.equals(m.getParameterTypes(), theOneOneMethod.getParameterTypes()))
								continue;  //don't worry about non-conflicting methods from single-function interfaces! :>!
							else
								throw new NoSuchMemberRuntimeException(runtimeClass+" implements more than one incompatible single-function interface! ;_;");
						}
						
						if (theOneOneMethod == null)
							throw new NoSuchMemberRuntimeException(runtimeClass+" implements no single-function interface ;_;");
					}
					
					
					//Register the runtime class as well, so it will have a quicker discovery of it in the future   //Todo is this a good idea?? :\
					AccumulatedCacheOfSuccessfulMethodHandlesOfDiscoveredSingleFunctionInterfaces.put(runtimeClass, theOneOneMethodHandle);
					
					
					return theOneOneMethodHandle;
				}
			}
		}
	}
	
	
	
	
	public static MethodHandle getBoundMethodHandleForSingleFunctionInterface(Object instanceOfSingleFunctionInterface) throws NoSuchMemberRuntimeException
	{
		if (instanceOfSingleFunctionInterface == null)
			throw new NullPointerException();
		
		
		if (instanceOfSingleFunctionInterface instanceof ProvidesPrimaryMethodHandle)
			return ((ProvidesPrimaryMethodHandle)instanceOfSingleFunctionInterface).getBoundMethodHandle();
		
		//Default impl
		MethodHandle h = getUnboundMethodHandleForSingleFunctionInterface(instanceOfSingleFunctionInterface);
		return h.bindTo(instanceOfSingleFunctionInterface);
	}
	
	
	
	
	
	
	
	protected static final MethodHandle Runnable_run = lookupInstanceNonoverloadedMethod(Runnable.class, "run");
}
