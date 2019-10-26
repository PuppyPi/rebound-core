package rebound.util.objectutil;

public class CompilerSignals
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
}
