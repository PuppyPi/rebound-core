/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.concurrencyprimitives;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.concurrent.GuardedBy;

//Todo oh perhaps we should have something that says the *contents* of a thing are guarded XD''
// (since a final field doesn't need to be guarded *itself*, it's the fields *inside the object* that it points to that we're talking about being guarded!!)
// (oof that's a lot of scanning code to figure out which I meant, (with @GuardedBy as well as @GuardedBy2!) x'D )
// (and also, it would never be enough, because whether things inside *that* are guarded by it as well and so on and so on can't easily be specified!! X'D )

/**
 * Same as {@link GuardedBy}, but without the {@link Target} restrictions :P
 * (*cough* constructors and local variables *cough* XD'' )
 * 
 * (edit: oh, it's also {@link Documented}..why isn't {@link GuardedBy} {@link Documented}? ;-;? )
 * 
 * @see GuardedBy
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface GuardedBy2
{
	String value() default "";
	
	int[] depth() default {0};
}
