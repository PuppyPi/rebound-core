package rebound.util;

import static java.util.Objects.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.SignalType;
import rebound.annotations.semantic.temporal.concurrencyprimitives.NotThreadSafe2;
import rebound.util.container.ContainerInterfaces.ObjectContainer;
import rebound.util.functional.FunctionInterfaces.NullaryFunction;

/**
 * This is a system for turning an object that only supports a single listener field (ie, set()/get() not add()/remove()) into one that supports add()/remove() by
 * virtue of using the {@link #addToTarget(Object, NullaryFunction, ObjectContainer)} / {@link #removeFromTarget(Object, ObjectContainer)} methods given here instead
 * of the actual ones on the listenable object!  (which, in turn, call {@link #add(Object)} / {@link #remove(Object)} but only if necessary)
 * 
 * This may seem like expensive in terms of memory, but if you use an {@link ArrayList} or {@link HashSet} or anything similar, you're actually using the same number
 * of Java objects as this system uses in its *worst* case!  Also fyi, this type of system is what AWT's add()/remove() listener methods use internally!
 * 
 * The invariant of this system is as follows:
 * 		• If there are zero listeners on an object, its single listener field is null.
 * 		• If there is one listener on an object, its single listener field is that listener (which must not be an instanceof this class)
 * 		• If there is more than one listener on an object, its single listener field is an instance of this class and {@link #add(Object)} can be called to add more listeners.
 * 
 * So if the listenable object has an {@link ArrayList} or god forbid a {@link HashSet} XD, then it always has that object *and the internal array* inside that object!
 * This system never takes more than two objects, the {@link EventListenerMulticaster} and the array!  But in almost all cases of event listeners in almost all code, there's
 * only ever zero or one listeners, in both cases of which, this system takes zero extra objects, using that listener directly!!  And for implementations of this class
 * that effectively inline the functionality of {@link ArrayList} and have some extra fields that function like elements in the array directly, to avoid needing the
 * array for some small number of listeners, there is no extra array object, and there is only one extra object allocated—this one!
 * 
 * The only way to be more efficient than this system and make an add()/remove() based API allow greater efficiency would be to inline into the listenable object's
 * class, the functionality of some efficient implementation of this class which uses an array field and/or some other listener fields for performance, and thus,
 * in turn inlines the functionality of {@link ArrayList} or similar!
 * 
 * Note however, that this system is less efficient for frequently adding and removing listeners if there is a small number of them causing this object to be
 * frequently recreated/destroyed.  In that case, the remove() shouldn't let this become unreachable, and then it would be efficient.  But really there's no way to
 * know such usage patterns ahead of time!  The best implementation is whichever one works best for your context.
 * 
 * Also note that, just like AWT, an add()/remove() based API can be implemented internally *using* this system!  Because a single Java field effectively has a
 * get()/set() interface!  And for that reason, instances of this class should not ever be passed as listeners to add()/remove() listener methods!!
 * Note that in that case, the listenable object decides the implementation of this class instead of the code registering the listener.
 * 
 * Really, the best situation would be to be able to directly inline Java classes into other Java classes and ensure references to them aren't escapable (since they
 * aren't real, like value types in the long-fabled Project Valhalla X3 ).  But that would make the language much more complex.
 * 
 * 
 * Also also, just FYI, whether it's a get()/set() or an add()/remove() API, listenables and multicasters should ALWAYS use normal Strong References, since the
 * listener is usually only reachable from its listenable.  If someone really wants a listener that goes away on its own, they can wrap it in a decorator that holds a
 * {@link WeakReference} to it internally.  (Though unfortunately that won't be purged from multicasters and listenables without some standard API to recognize that
 * it needs to be).
 */
@SignalType
public interface EventListenerMulticaster<Listener>
{
	public int getSize();
	
	/**
	 * if {@link #getSize()} != 1 this returns null!
	 */
	public @Nonnull Listener getSingleListenerIfSingletonOrNullOtherwise();
	
	/**
	 * @param l  This must not, itself, be a {@link EventListenerMulticaster}!
	 */
	public void add(@Nonnull Listener l);
	
	/**
	 * @param l  This must not, itself, be a {@link EventListenerMulticaster}!
	 */
	public void remove(@Nonnull Listener l);
	
	
	
	
	/**
	 * This method may not actually be used very often, but it serves to illustrate the purpose of this interface!
	 * @param <Multicaster> This should extend both <code>{@link EventListenerMulticaster}&lt;Listener&gt;</code> and <code>Listener</code> but Java doesn't support that at the time of writing this (Java 8)
	 */
	@NotThreadSafe2
	public static <Listener, Multicaster extends EventListenerMulticaster<Listener>> void addToTarget(Listener listener, NullaryFunction<Multicaster> newMulticaster, ObjectContainer<Listener> targetListenable)
	{
		requireNonNull(listener);
		requireNonNull(newMulticaster);
		requireNonNull(targetListenable);
		
		if (listener instanceof EventListenerMulticaster)
			throw new IllegalArgumentException("This would cause ambiguous functionality!!");
		
		Listener l = targetListenable.get();
		
		if (l == null)
			targetListenable.set(listener);  // 0 → 1
		
		else if (l == listener)
			return;  //Aha!  It's already what it is!
		
		else if (l instanceof EventListenerMulticaster)  // N → N + 1
			((EventListenerMulticaster<Listener>)l).add(listener);  //Ah it's already a multicaster!  We can just ask it to take care of adding for us :3
		
		else// if (l != listener)
		{
			// 1 → 2
			//We need to *make* a multicaster!
			
			EventListenerMulticaster<Listener> multicaster = newMulticaster.f();
			
			//In order in case that matters!
			multicaster.add(l);
			multicaster.add(listener);
			
			targetListenable.set((Listener)multicaster);
		}
	}
	
	
	
	
	/**
	 * This method may not actually be used very often, but it serves to illustrate the purpose of this interface!
	 * @param <Multicaster> This should extend both <code>{@link EventListenerMulticaster}&lt;Listener&gt;</code> and <code>Listener</code> but Java doesn't support that at the time of writing this (Java 8)
	 */
	@NotThreadSafe2
	public static <Listener, Multicaster extends EventListenerMulticaster<Listener>> void removeFromTarget(Listener listener, ObjectContainer<Listener> targetListenable)
	{
		requireNonNull(targetListenable);
		requireNonNull(listener);
		
		if (listener instanceof EventListenerMulticaster)
			throw new IllegalArgumentException("This would cause ambiguous functionality!!");
		
		Listener l = targetListenable.get();
		
		if (l == null)
		{
			//No worries then :3
		}
		else if (l == listener)
		{
			//Got it! ;D
			targetListenable.set(null);
		}
		else if (l instanceof EventListenerMulticaster)
		{
			EventListenerMulticaster<Listener> multicaster = (EventListenerMulticaster<Listener>)l;
			
			multicaster.remove(listener);
			
			//Now shrink it from a Multicaster to an Individual listener (or nothing!) if possible!
			int newSize = multicaster.getSize();
			if (newSize == 0)
				targetListenable.set(null);  //this would never happen if this method was used exclusively to remove listeners, but you never know!
			else if (newSize == 1)
				targetListenable.set(requireNonNull(multicaster.getSingleListenerIfSingletonOrNullOtherwise()));
			//else, no worries; it just stays big :3
		}
		else
		{
			//No worries then, it's a single listener and not the one we're removing! :3
		}
	}
}
