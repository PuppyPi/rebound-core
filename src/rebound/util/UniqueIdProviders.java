/*
 * Created on Mar 13, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util;

import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.exceptions.DoubleFreeException;
import rebound.exceptions.ImpossibleException;
import rebound.util.collections.prim.PrimitiveCollections.SortedLongSetBackedByList;
import rebound.util.objectutil.JavaNamespace;

public class UniqueIdProviders
implements JavaNamespace
{
	public static class UniqueIdsExhaustedException
	extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		
		public UniqueIdsExhaustedException()
		{
		}
		
		public UniqueIdsExhaustedException(String message)
		{
			super(message);
		}
		
		public UniqueIdsExhaustedException(Throwable cause)
		{
			super(cause);
		}
		
		public UniqueIdsExhaustedException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
	
	
	
	public static interface UniqueIdProvider<T>
	{
		/**
		 * @return *never* null unless that is actually a valid unique id XD   Throw {@link UniqueIdsExhaustedException} if you run out!! :>
		 */
		public T acquire() throws UniqueIdsExhaustedException;
		public void release(T id) throws DoubleFreeException;
		
		//Todo public static interface ExternallyResettableUniqueIdProvider
	}
	
	
	/*
	public static interface Unique_$$Primitive$$_IdProvider
	{
		public _$$prim$$_ acquire() throws UniqueIdsExhaustedException;
		public void release(_$$prim$$_ id) throws DoubleFreeException;
	}
	
	 */
	
	
	
	
	public static interface UniqueBooleanIdProvider
	{
		public boolean acquire() throws UniqueIdsExhaustedException;
		public void release(boolean id) throws DoubleFreeException;
	}
	
	public static interface UniqueByteIdProvider
	{
		public byte acquire() throws UniqueIdsExhaustedException;
		public void release(byte id) throws DoubleFreeException;
	}
	
	public static interface UniqueCharacterIdProvider
	{
		public char acquire() throws UniqueIdsExhaustedException;
		public void release(char id) throws DoubleFreeException;
	}
	
	public static interface UniqueShortIdProvider
	{
		public short acquire() throws UniqueIdsExhaustedException;
		public void release(short id) throws DoubleFreeException;
	}
	
	public static interface UniqueFloatIdProvider
	{
		public float acquire() throws UniqueIdsExhaustedException;
		public void release(float id) throws DoubleFreeException;
	}
	
	public static interface UniqueIntegerIdProvider
	{
		public int acquire() throws UniqueIdsExhaustedException;
		public void release(int id) throws DoubleFreeException;
	}
	
	public static interface UniqueDoubleIdProvider
	{
		public double acquire() throws UniqueIdsExhaustedException;
		public void release(double id) throws DoubleFreeException;
	}
	
	public static interface UniqueLongIdProvider
	{
		public long acquire() throws UniqueIdsExhaustedException;
		public void release(long id) throws DoubleFreeException;
	}
	
	
	
	
	
	
	
	
	
	//Note: we're not going to worry too much about this right now..because
	
	/**
	 * Super-lazy implementation which doesn't keep track of things, but is SUPER fast
	 * and doesn't use up more than O(1) memory! :D
	 * 
	 * ..but it only has so many id's to give out *OVER ALL TIME*
	 * 
	 * But then again, for 64-bits (java long), this is 18.45 quintillion id's XD'!
	 * Which, if they were distributed non-stop, each nanosecond, would take 584.554049254 years..
	 * ..soooo prolly not a problem!! ^^'
	 * XD''!!
	 * 
	 * Note: uses longs as unsigned integers :>
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static class SimpleSinglePassUniqueLongIdProvider
	implements UniqueLongIdProvider
	{
		//protected final long firstId;
		protected long nextId;
		
		//Todo Allowing unused-regions of IDs should be done more generally (maybe they want no 0's, no negatives, nothing below 1024!  I doesn't know! ;; )
		/**
		 * Note: it will be exhausted at (unsigned) overflow to 0!
		 * (so take care to understand that 'negatives' count as really large values (after which there aren't so many remaining ids!)  )
		 */
		public SimpleSinglePassUniqueLongIdProvider(@ActuallyUnsigned long nextId)
		{
			this.nextId = nextId;
		}
		
		public SimpleSinglePassUniqueLongIdProvider()
		{
			this(0);
		}
		
		
		
		@Override
		public long acquire() throws UniqueIdsExhaustedException
		{
			long id = this.nextId;
			this.nextId++;
			if (this.nextId == 0) //overflow! (possibly unsigned overflow! B) XD )
				throw new UniqueIdsExhaustedException("we're lazy and didn't keep track of them for the second pass and beyond X>'");
			
			return id;
		}
		
		
		@Override
		public void release(long id) throws DoubleFreeException
		{
			//Nothingggg!  Because we're lazy here and don't bother keeping track of thingsss! XD'''
		}
	}
	
	
	
	/**
	 * Two-pass in that it starts out fast by merely incrementing the IDs, but once the mathematical limit is exhausted, it goes back through and finds unused ones slightly more slowly :>
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static class SimpleTwoPassUniqueLongIdProvider
	implements UniqueLongIdProvider
	{
		//PERF(MEM) Create and use CompactingSortedSet<Prim>!!
		
		protected long nextId = 0;
		protected boolean exhaustedFirstPass = false;
		protected SortedLongSetBackedByList idsInUse = new SortedLongSetBackedByList();
		
		
		
		@Override
		public long acquire() throws UniqueIdsExhaustedException
		{
			if (!this.exhaustedFirstPass)
			{
				long id = this.nextId;
				this.nextId++;
				if (this.nextId == Long.MIN_VALUE) //meh; let's not use negatives for now XP
					this.exhaustedFirstPass = true;
				
				this.idsInUse.addLong(id);
				
				return id;
			}
			else
			{
				int l = this.idsInUse.size();
				
				if (l == 0)
				{
					throw new ImpossibleException();
				}
				
				if (l == Integer.MAX_VALUE)
					throw new UniqueIdsExhaustedException(); //and here is reason we're not using negative ids for now XD'  (or even full spectrum of longs!)
				
				
				while (this.idsInUse.containsLong(this.nextId))
				{
					this.nextId++;
					if (this.nextId == Long.MIN_VALUE) //overflow! (and not using negatives; so flip back around!)
						this.nextId = 0;
				}
				
				long id = this.nextId;
				
				this.nextId++;
				if (this.nextId == Long.MIN_VALUE) //overflow! (and not using negatives; so flip back around!)
					this.nextId = 0;
				
				this.idsInUse.addLong(id);
				
				return id;
			}
		}
		
		
		@Override
		public void release(long id) throws DoubleFreeException
		{
			int index = this.idsInUse.indexOfLong(id);
			
			if (index == -1)
				throw new DoubleFreeException();
			
			this.idsInUse.removeLongByIndex(index);
			
			if (this.idsInUse.isEmpty())
			{
				//Flip back innn! :D!
				this.nextId = 0;
				this.exhaustedFirstPass = false;
			}
		}
	}
}
