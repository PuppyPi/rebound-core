package rebound.util;

import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import javax.annotation.Nonnegative;

/**
 * Arbitrary here means inconsistently-arbitrary; ie, the values could be from any possible random number generator (or incrementing counter) and everything still work!
 * (In reality they're not for performance; they're just constants; but it could be different!  And that can be useful for code porting or code prooving or optimization!)
 */
public class CodeHinting
{
	/**
	 * Hints for Branch/Path Taken and Branch/Path Not Taken! :D
	 *  (Currently this isn't used by any JIT compilers XD )
	 *  (But it's good to keep this information in code for someday [soon]! :D )
	 *  
	 * See https://en.wikipedia.org/wiki/Branch_predictor
	 * See https://stackoverflow.com/questions/14332848/intel-x86-0x2e-0x3e-prefix-branch-prediction-actually-used
	 * See https://kernelnewbies.org/FAQ/LikelyUnlikely
	 * See https://stackoverflow.com/questions/109710/how-do-the-likely-unlikely-macros-in-the-linux-kernel-work-and-what-is-their-ben
	 * 
	 * @see #unlikely(boolean)
	 */
	public static boolean likely(boolean x)
	{
		return x;
	}
	
	/**
	 * @see #likely(boolean)
	 */
	public static boolean unlikely(boolean x)
	{
		return x;
	}
	
	
	/**
	 * In reality this is Java Reference Identity/Equality, but what using this means is that you don't actually need them to be perfectly reference-identical!
	 * You're just using it as a quick-and-dirty check for equality when it's fine to have false negatives (returns false/unequal when they're really equal)
	 * but not false positives (returns true/equal when they're really different!).
	 * 
	 * For example, if they're equal you can skip some things, but you don't have to, it's just a performance benefit.  But if testing if they're equal takes
	 * a long time..then that negates the whole point of doing the optimization!! XD   So you might just do this quick check, and if this returns true, they're
	 * *definitely* equal!  But if it returns false you don't know, but it's right a lot of the time and it's *blazing* fast XD so fast there's almost never
	 * any reason not to at least try it XD
	 */
	public static boolean quickEq(Object a, Object b)
	{
		return a == b;
	}
	
	
	
	
	
	/**
	 * Hints for keeping a block/body/scope from getting removed by the compiler! :D
	 *  (Currently this isn't explicitly respected by any JIT compilers XD )
	 */
	public static void dontElideThisBasicBlock()
	{
		PointlessByte++;  //who knows, some other code with the same package name might be loaded later on and read/write it concurrently, even after our basic block has been compiled!   ( ͡° ͜ʖ ͡°)
		//eh, it's a good enough shot I suppose x'D
		//Todo check that this actually prevents elision with the stock Sun JIT compiler and also Graal
	}
	
	protected static byte PointlessByte = 0;
	
	
	
	
	
	
	
	
	@Nonnegative
	public static int arbitraryIntNonnegative()
	{
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static <T> T arbitrary(T a, T b)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitrary(T a, T b, T c)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitrary(T a, T b, T c, T d)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitrary(T a, T b, T c, T d, T e)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitrary(T... x)
	{
		return x[0];
	}
	
	/**
	 * This indicates that truly any java.lang.Object could be used (eg, for synchronized/wait/notify, or as an opaque map key, etc.)
	 * and the one given is just for performance.
	 */
	public static Object arbitraryObject(Object x)
	{
		return x;
	}
	
	/**
	 * This indicates that truly any java.lang.Object could be used (eg, for synchronized/wait/notify, or as an opaque map key, etc.)
	 */
	public static Object arbitraryObject()
	{
		return new Object();
	}
	
	
	
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static <T> T arbitraryCheckingId(T a, T b)
	{
		if (a != b)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingId(T a, T b, T c)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingId(T a, T b, T c, T d)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingId(T a, T b, T c, T d, T e)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		if (a != e)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingId(T... x)
	{
		if (x.length > 1)
		{
			T first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (x[i] != first)
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static <T> T arbitraryCheckingEq(T a, T b)
	{
		if (!eq(a, b))  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingEq(T a, T b, T c)
	{
		if (!eq(a, b))  throw new AssertionError();
		if (!eq(a, c))  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingEq(T a, T b, T c, T d)
	{
		if (!eq(a, b))  throw new AssertionError();
		if (!eq(a, c))  throw new AssertionError();
		if (!eq(a, d))  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingEq(T a, T b, T c, T d, T e)
	{
		if (!eq(a, b))  throw new AssertionError();
		if (!eq(a, c))  throw new AssertionError();
		if (!eq(a, d))  throw new AssertionError();
		if (!eq(a, e))  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingEq(T... x)
	{
		if (x.length > 1)
		{
			T first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (!eq(x[i], first))
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	
	
	
	/**
	 * This indicates the two values are eqvuivalent for the purposes of the code; that either one could be used :3
	 */
	public static <T> T arbitraryCheckingEqv(T a, T b)
	{
		if (!eqv(a, b))  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are eqvuivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingEqv(T a, T b, T c)
	{
		if (!eqv(a, b))  throw new AssertionError();
		if (!eqv(a, c))  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are eqvuivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingEqv(T a, T b, T c, T d)
	{
		if (!eqv(a, b))  throw new AssertionError();
		if (!eqv(a, c))  throw new AssertionError();
		if (!eqv(a, d))  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are eqvuivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingEqv(T a, T b, T c, T d, T e)
	{
		if (!eqv(a, b))  throw new AssertionError();
		if (!eqv(a, c))  throw new AssertionError();
		if (!eqv(a, d))  throw new AssertionError();
		if (!eqv(a, e))  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are eqvuivalent for the purposes of the code; that any one could be used :3
	 */
	public static <T> T arbitraryCheckingEqv(T... x)
	{
		if (x.length > 1)
		{
			T first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (!eqv(x[i], first))
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	/**
	 * This indicates that any value could be given and it should work the same!
	 * (eg, if used in a condition or as a value)
	 ⎋a/
	public static _$$prim$$_ arbitrary_$$Prim$$_()
	{
		return _$$primdef$$_;
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 ⎋a/
	public static _$$prim$$_ arbitrary(_$$prim$$_ a, _$$prim$$_ b)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 ⎋a/
	public static _$$prim$$_ arbitrary(_$$prim$$_ a, _$$prim$$_ b, _$$prim$$_ c)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 ⎋a/
	public static _$$prim$$_ arbitrary(_$$prim$$_ a, _$$prim$$_ b, _$$prim$$_ c, _$$prim$$_ d)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 ⎋a/
	public static _$$prim$$_ arbitrary(_$$prim$$_ a, _$$prim$$_ b, _$$prim$$_ c, _$$prim$$_ d, _$$prim$$_ e)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 ⎋a/
	public static _$$prim$$_ arbitrary(_$$prim$$_... x)
	{
		return x[0];
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 ⎋a/
	public static _$$prim$$_ arbitraryCheckingEq(_$$prim$$_ a, _$$prim$$_ b)
	{
		if (a != b)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 ⎋a/
	public static _$$prim$$_ arbitraryCheckingEq(_$$prim$$_ a, _$$prim$$_ b, _$$prim$$_ c)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 ⎋a/
	public static _$$prim$$_ arbitraryCheckingEq(_$$prim$$_ a, _$$prim$$_ b, _$$prim$$_ c, _$$prim$$_ d)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 ⎋a/
	public static _$$prim$$_ arbitraryCheckingEq(_$$prim$$_ a, _$$prim$$_ b, _$$prim$$_ c, _$$prim$$_ d, _$$prim$$_ e)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		if (a != e)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 ⎋a/
	public static _$$prim$$_ arbitraryCheckingEq(_$$prim$$_... x)
	{
		if (x.length > 1)
		{
			_$$prim$$_ first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (x[i] != first)
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	 */
	
	/**
	 * This indicates that any value could be given and it should work the same!
	 * (eg, if used in a condition or as a value)
	 */
	public static boolean arbitraryBoolean()
	{
		return false;
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static boolean arbitrary(boolean a, boolean b)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static boolean arbitrary(boolean a, boolean b, boolean c)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static boolean arbitrary(boolean a, boolean b, boolean c, boolean d)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static boolean arbitrary(boolean a, boolean b, boolean c, boolean d, boolean e)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static boolean arbitrary(boolean... x)
	{
		return x[0];
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static boolean arbitraryCheckingEq(boolean a, boolean b)
	{
		if (a != b)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static boolean arbitraryCheckingEq(boolean a, boolean b, boolean c)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static boolean arbitraryCheckingEq(boolean a, boolean b, boolean c, boolean d)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static boolean arbitraryCheckingEq(boolean a, boolean b, boolean c, boolean d, boolean e)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		if (a != e)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static boolean arbitraryCheckingEq(boolean... x)
	{
		if (x.length > 1)
		{
			boolean first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (x[i] != first)
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	
	/**
	 * This indicates that any value could be given and it should work the same!
	 * (eg, if used in a condition or as a value)
	 */
	public static byte arbitraryByte()
	{
		return ((byte)0);
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static byte arbitrary(byte a, byte b)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static byte arbitrary(byte a, byte b, byte c)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static byte arbitrary(byte a, byte b, byte c, byte d)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static byte arbitrary(byte a, byte b, byte c, byte d, byte e)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static byte arbitrary(byte... x)
	{
		return x[0];
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static byte arbitraryCheckingEq(byte a, byte b)
	{
		if (a != b)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static byte arbitraryCheckingEq(byte a, byte b, byte c)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static byte arbitraryCheckingEq(byte a, byte b, byte c, byte d)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static byte arbitraryCheckingEq(byte a, byte b, byte c, byte d, byte e)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		if (a != e)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static byte arbitraryCheckingEq(byte... x)
	{
		if (x.length > 1)
		{
			byte first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (x[i] != first)
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	
	/**
	 * This indicates that any value could be given and it should work the same!
	 * (eg, if used in a condition or as a value)
	 */
	public static char arbitraryChar()
	{
		return ((char)0);
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static char arbitrary(char a, char b)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static char arbitrary(char a, char b, char c)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static char arbitrary(char a, char b, char c, char d)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static char arbitrary(char a, char b, char c, char d, char e)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static char arbitrary(char... x)
	{
		return x[0];
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static char arbitraryCheckingEq(char a, char b)
	{
		if (a != b)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static char arbitraryCheckingEq(char a, char b, char c)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static char arbitraryCheckingEq(char a, char b, char c, char d)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static char arbitraryCheckingEq(char a, char b, char c, char d, char e)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		if (a != e)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static char arbitraryCheckingEq(char... x)
	{
		if (x.length > 1)
		{
			char first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (x[i] != first)
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	
	/**
	 * This indicates that any value could be given and it should work the same!
	 * (eg, if used in a condition or as a value)
	 */
	public static short arbitraryShort()
	{
		return ((short)0);
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static short arbitrary(short a, short b)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static short arbitrary(short a, short b, short c)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static short arbitrary(short a, short b, short c, short d)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static short arbitrary(short a, short b, short c, short d, short e)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static short arbitrary(short... x)
	{
		return x[0];
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static short arbitraryCheckingEq(short a, short b)
	{
		if (a != b)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static short arbitraryCheckingEq(short a, short b, short c)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static short arbitraryCheckingEq(short a, short b, short c, short d)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static short arbitraryCheckingEq(short a, short b, short c, short d, short e)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		if (a != e)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static short arbitraryCheckingEq(short... x)
	{
		if (x.length > 1)
		{
			short first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (x[i] != first)
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	
	/**
	 * This indicates that any value could be given and it should work the same!
	 * (eg, if used in a condition or as a value)
	 */
	public static float arbitraryFloat()
	{
		return 0.0f;
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static float arbitrary(float a, float b)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static float arbitrary(float a, float b, float c)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static float arbitrary(float a, float b, float c, float d)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static float arbitrary(float a, float b, float c, float d, float e)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static float arbitrary(float... x)
	{
		return x[0];
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static float arbitraryCheckingEq(float a, float b)
	{
		if (a != b)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static float arbitraryCheckingEq(float a, float b, float c)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static float arbitraryCheckingEq(float a, float b, float c, float d)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static float arbitraryCheckingEq(float a, float b, float c, float d, float e)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		if (a != e)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static float arbitraryCheckingEq(float... x)
	{
		if (x.length > 1)
		{
			float first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (x[i] != first)
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	
	/**
	 * This indicates that any value could be given and it should work the same!
	 * (eg, if used in a condition or as a value)
	 */
	public static int arbitraryInt()
	{
		return 0;
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static int arbitrary(int a, int b)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static int arbitrary(int a, int b, int c)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static int arbitrary(int a, int b, int c, int d)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static int arbitrary(int a, int b, int c, int d, int e)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static int arbitrary(int... x)
	{
		return x[0];
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static int arbitraryCheckingEq(int a, int b)
	{
		if (a != b)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static int arbitraryCheckingEq(int a, int b, int c)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static int arbitraryCheckingEq(int a, int b, int c, int d)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static int arbitraryCheckingEq(int a, int b, int c, int d, int e)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		if (a != e)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static int arbitraryCheckingEq(int... x)
	{
		if (x.length > 1)
		{
			int first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (x[i] != first)
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	
	/**
	 * This indicates that any value could be given and it should work the same!
	 * (eg, if used in a condition or as a value)
	 */
	public static double arbitraryDouble()
	{
		return 0.0d;
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static double arbitrary(double a, double b)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static double arbitrary(double a, double b, double c)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static double arbitrary(double a, double b, double c, double d)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static double arbitrary(double a, double b, double c, double d, double e)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static double arbitrary(double... x)
	{
		return x[0];
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static double arbitraryCheckingEq(double a, double b)
	{
		if (a != b)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static double arbitraryCheckingEq(double a, double b, double c)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static double arbitraryCheckingEq(double a, double b, double c, double d)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static double arbitraryCheckingEq(double a, double b, double c, double d, double e)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		if (a != e)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static double arbitraryCheckingEq(double... x)
	{
		if (x.length > 1)
		{
			double first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (x[i] != first)
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	
	/**
	 * This indicates that any value could be given and it should work the same!
	 * (eg, if used in a condition or as a value)
	 */
	public static long arbitraryLong()
	{
		return 0l;
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static long arbitrary(long a, long b)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static long arbitrary(long a, long b, long c)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static long arbitrary(long a, long b, long c, long d)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static long arbitrary(long a, long b, long c, long d, long e)
	{
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static long arbitrary(long... x)
	{
		return x[0];
	}
	
	
	
	/**
	 * This indicates the two values are equivalent for the purposes of the code; that either one could be used :3
	 */
	public static long arbitraryCheckingEq(long a, long b)
	{
		if (a != b)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static long arbitraryCheckingEq(long a, long b, long c)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static long arbitraryCheckingEq(long a, long b, long c, long d)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static long arbitraryCheckingEq(long a, long b, long c, long d, long e)
	{
		if (a != b)  throw new AssertionError();
		if (a != c)  throw new AssertionError();
		if (a != d)  throw new AssertionError();
		if (a != e)  throw new AssertionError();
		
		return a;
	}
	
	/**
	 * This indicates the values are equivalent for the purposes of the code; that any one could be used :3
	 */
	public static long arbitraryCheckingEq(long... x)
	{
		if (x.length > 1)
		{
			long first = x[0];
			int n = x.length;
			for (int i = 1; i < n; i++)
				if (x[i] != first)
					throw new AssertionError();
		}
		
		return x[0];
	}
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO Is undefinedXyz() different from arbitraryXyz() ??
	
	public static <E> E undefined()
	{
		return null;
	}
	
	
	
	
	
	/* <<<
	primxp
	
	public static _$$prim$$_ undefined_$$Prim$$_()
	{
		return _$$primdef$$_;
	}
	 */
	
	public static boolean undefinedBoolean()
	{
		return false;
	}
	
	public static byte undefinedByte()
	{
		return ((byte)0);
	}
	
	public static char undefinedChar()
	{
		return ((char)0);
	}
	
	public static short undefinedShort()
	{
		return ((short)0);
	}
	
	public static float undefinedFloat()
	{
		return 0.0f;
	}
	
	public static int undefinedInt()
	{
		return 0;
	}
	
	public static double undefinedDouble()
	{
		return 0.0d;
	}
	
	public static long undefinedLong()
	{
		return 0l;
	}
	// >>>
}
