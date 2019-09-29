package rebound.util.objectutil;

/**
 * Hints for Branch/Path Taken and Branch/Path Not Taken! :D
 *  (Currently this isn't used by any JIT compilers XD )
 *  (But it's good to keep this information in code for someday [soon]! :D )
 *  
 * See https://en.wikipedia.org/wiki/Branch_predictor
 * See https://stackoverflow.com/questions/14332848/intel-x86-0x2e-0x3e-prefix-branch-prediction-actually-used
 * See https://kernelnewbies.org/FAQ/LikelyUnlikely
 * See https://stackoverflow.com/questions/109710/how-do-the-likely-unlikely-macros-in-the-linux-kernel-work-and-what-is-their-ben
 */
public class BranchHinting
{
	public static boolean unlikely(boolean x)
	{
		return x;
	}
	
	public static boolean likely(boolean x)
	{
		return x;
	}
}
