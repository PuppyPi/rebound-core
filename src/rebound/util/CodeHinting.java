package rebound.util;

import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;

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
	 * Hints for keeping a block/body/scope from getting removed by the compiler! :D
	 *  (Currently this isn't explicitly respected by any JIT compilers XD )
	 */
	public static void dontElideThisBasicBlock()
	{
		PointlessByte++;  //who knows, some other code with the same package name might be loaded later on and read/write it concurrently, even after our basic block has been compiled!   ( ͡° ͜ʖ ͡°)
		//eh, it's a good enough shot I suppose x'D
		//Todo check that this actually prevents elision with the stock Sun JIT compiler and also Graal
	}
	
	protected static volatile byte PointlessByte = 0;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
}
