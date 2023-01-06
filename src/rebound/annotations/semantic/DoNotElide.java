package rebound.annotations.semantic;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This tells compilers not to elide code cleverly!
 * Namely because the time taken is important, not just in benchmarks or unit testing, but also in comparing strings with constant time for avoiding timing attacks on servers, or with embedded systems waiting a certain number of CPU cycles (although Uncooperative Multitasking Operating Systems or Managed Runtime Environments' green threads may introduce *more* latency, but at least this is a guarantee there won't be any *less* than the given loop!)
 * 
 * Note that none of these rebound code annotations are actually used in anything as of 2023-01-06 00:38:13 z  X'D
 * (But somedayyyyyyy! :> )
 * (Or maybe someday Java will get official ones and we can just search for uses of ours! :> )
 * (What counts is that The Information Is There :33 )
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface DoNotElide
{
}
