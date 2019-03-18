/*
 * Created on Feb 3, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.container;

import rebound.util.objectutil.JavaNamespace;

public class ContainerInterfaces
implements JavaNamespace
{
	public static interface ObjectContainer<T>
	{
		public T get();
		public void set(T value);
	}
	
	/*
	public static interface _$$Primitive$$_Container
	{
		public _$$prim$$_ get();
		public void set(_$$prim$$_ value);
	}
	
	 */
	
	public static interface BooleanContainer
	{
		public boolean get();
		public void set(boolean value);
	}
	
	public static interface ByteContainer
	{
		public byte get();
		public void set(byte value);
	}
	
	public static interface CharacterContainer
	{
		public char get();
		public void set(char value);
	}
	
	public static interface ShortContainer
	{
		public short get();
		public void set(short value);
	}
	
	public static interface FloatContainer
	{
		public float get();
		public void set(float value);
	}
	
	public static interface IntegerContainer
	{
		public int get();
		public void set(int value);
	}
	
	public static interface DoubleContainer
	{
		public double get();
		public void set(double value);
	}
	
	public static interface LongContainer
	{
		public long get();
		public void set(long value);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	public static ObjectContainer<<<Primitive>>> toNonCachingObjectContainer(final <<Primitive>>Container primitiveContainer)
	{
		return new ObjectContainer<<<Primitive>>>
		()
		{
			public <<Primitive>> get()
			{
				return primitiveContainer.get(); //boxing ^_^
			}
			
			public void set(<<Primitive>> value)
			{
				primitiveContainer.set(value); //unboxing ^_^
			}
		};
	}
	
	 */
	
	/**
	 * Note: there is no performance penalty for [un]boxing with booleans :D, so no need for caching!
	 */
	public static ObjectContainer<Boolean> toNonCachingObjectContainer(final BooleanContainer primitiveContainer)
	{
		return new ObjectContainer<Boolean>
		()
		{
			@Override
			public Boolean get()
			{
				return primitiveContainer.get(); //boxing ^_^
			}
			
			@Override
			public void set(Boolean value)
			{
				primitiveContainer.set(value); //unboxing ^_^
			}
		};
	}
	
	/**
	 * Note: there is no performance penalty for [un]boxing with bytes :D, so no need for caching!
	 */
	public static ObjectContainer<Byte> toNonCachingObjectContainer(final ByteContainer primitiveContainer)
	{
		return new ObjectContainer<Byte>
		()
		{
			@Override
			public Byte get()
			{
				return primitiveContainer.get(); //boxing ^_^
			}
			
			@Override
			public void set(Byte value)
			{
				primitiveContainer.set(value); //unboxing ^_^
			}
		};
	}
	
	public static ObjectContainer<Character> toNonCachingObjectContainer(final CharacterContainer primitiveContainer)
	{
		return new ObjectContainer<Character>
		()
		{
			@Override
			public Character get()
			{
				return primitiveContainer.get(); //boxing ^_^
			}
			
			@Override
			public void set(Character value)
			{
				primitiveContainer.set(value); //unboxing ^_^
			}
		};
	}
	
	public static ObjectContainer<Short> toNonCachingObjectContainer(final ShortContainer primitiveContainer)
	{
		return new ObjectContainer<Short>
		()
		{
			@Override
			public Short get()
			{
				return primitiveContainer.get(); //boxing ^_^
			}
			
			@Override
			public void set(Short value)
			{
				primitiveContainer.set(value); //unboxing ^_^
			}
		};
	}
	
	public static ObjectContainer<Float> toNonCachingObjectContainer(final FloatContainer primitiveContainer)
	{
		return new ObjectContainer<Float>
		()
		{
			@Override
			public Float get()
			{
				return primitiveContainer.get(); //boxing ^_^
			}
			
			@Override
			public void set(Float value)
			{
				primitiveContainer.set(value); //unboxing ^_^
			}
		};
	}
	
	public static ObjectContainer<Integer> toNonCachingObjectContainer(final IntegerContainer primitiveContainer)
	{
		return new ObjectContainer<Integer>
		()
		{
			@Override
			public Integer get()
			{
				return primitiveContainer.get(); //boxing ^_^
			}
			
			@Override
			public void set(Integer value)
			{
				primitiveContainer.set(value); //unboxing ^_^
			}
		};
	}
	
	public static ObjectContainer<Double> toNonCachingObjectContainer(final DoubleContainer primitiveContainer)
	{
		return new ObjectContainer<Double>
		()
		{
			@Override
			public Double get()
			{
				return primitiveContainer.get(); //boxing ^_^
			}
			
			@Override
			public void set(Double value)
			{
				primitiveContainer.set(value); //unboxing ^_^
			}
		};
	}
	
	public static ObjectContainer<Long> toNonCachingObjectContainer(final LongContainer primitiveContainer)
	{
		return new ObjectContainer<Long>
		()
		{
			@Override
			public Long get()
			{
				return primitiveContainer.get(); //boxing ^_^
			}
			
			@Override
			public void set(Long value)
			{
				primitiveContainer.set(value); //unboxing ^_^
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	public static ObjectContainer<<<Primitive>>> toCachingObjectContainer(final <<Primitive>>Container primitiveContainer)
	{
		return new ObjectContainer<<<Primitive>>>
		()
		{
			protected transient <<Primitive>> wrapperValueCache;
			
			public <<Primitive>> get()
			{
				<<Primitive>> wrappedValue = this.wrapperValueCache;
				
				if (wrappedValue == null)
				{
					wrappedValue = primitiveContainer.get(); //boxing ^_^
					this.wrapperValueCache = wrappedValue;
					return wrappedValue;
				}
				else
				{
					return wrappedValue;
				}
			}
			
			public void set(<<Primitive>> value)
			{
				if ((<<prim>>)value != (<<prim>>)this.wrapperValueCache)
				{
					this.wrapperValueCache = value;
					primitiveContainer.set(value); //unboxing ^_^
				}
			}
		};
	}
	
	 */
	
	/**
	 * Note: there is no performance penalty for [un]boxing with booleans :D, so no need for caching!
	 */
	public static ObjectContainer<Boolean> toCachingObjectContainer(final BooleanContainer primitiveContainer)
	{
		return new ObjectContainer<Boolean>
		()
		{
			protected transient Boolean wrapperValueCache;
			
			@Override
			public Boolean get()
			{
				Boolean wrappedValue = this.wrapperValueCache;
				
				if (wrappedValue == null)
				{
					wrappedValue = primitiveContainer.get(); //boxing ^_^
					this.wrapperValueCache = wrappedValue;
					return wrappedValue;
				}
				else
				{
					return wrappedValue;
				}
			}
			
			@Override
			public void set(Boolean value)
			{
				if (value != this.wrapperValueCache)
				{
					this.wrapperValueCache = value;
					primitiveContainer.set(value); //unboxing ^_^
				}
			}
		};
	}
	
	/**
	 * Note: there is no performance penalty for [un]boxing with bytes :D, so no need for caching!
	 */
	public static ObjectContainer<Byte> toCachingObjectContainer(final ByteContainer primitiveContainer)
	{
		return new ObjectContainer<Byte>
		()
		{
			protected transient Byte wrapperValueCache;
			
			@Override
			public Byte get()
			{
				Byte wrappedValue = this.wrapperValueCache;
				
				if (wrappedValue == null)
				{
					wrappedValue = primitiveContainer.get(); //boxing ^_^
					this.wrapperValueCache = wrappedValue;
					return wrappedValue;
				}
				else
				{
					return wrappedValue;
				}
			}
			
			@Override
			public void set(Byte value)
			{
				if (value != this.wrapperValueCache)
				{
					this.wrapperValueCache = value;
					primitiveContainer.set(value); //unboxing ^_^
				}
			}
		};
	}
	
	public static ObjectContainer<Character> toCachingObjectContainer(final CharacterContainer primitiveContainer)
	{
		return new ObjectContainer<Character>
		()
		{
			protected transient Character wrapperValueCache;
			
			@Override
			public Character get()
			{
				Character wrappedValue = this.wrapperValueCache;
				
				if (wrappedValue == null)
				{
					wrappedValue = primitiveContainer.get(); //boxing ^_^
					this.wrapperValueCache = wrappedValue;
					return wrappedValue;
				}
				else
				{
					return wrappedValue;
				}
			}
			
			@Override
			public void set(Character value)
			{
				if (value != this.wrapperValueCache)
				{
					this.wrapperValueCache = value;
					primitiveContainer.set(value); //unboxing ^_^
				}
			}
		};
	}
	
	public static ObjectContainer<Short> toCachingObjectContainer(final ShortContainer primitiveContainer)
	{
		return new ObjectContainer<Short>
		()
		{
			protected transient Short wrapperValueCache;
			
			@Override
			public Short get()
			{
				Short wrappedValue = this.wrapperValueCache;
				
				if (wrappedValue == null)
				{
					wrappedValue = primitiveContainer.get(); //boxing ^_^
					this.wrapperValueCache = wrappedValue;
					return wrappedValue;
				}
				else
				{
					return wrappedValue;
				}
			}
			
			@Override
			public void set(Short value)
			{
				if (value != this.wrapperValueCache)
				{
					this.wrapperValueCache = value;
					primitiveContainer.set(value); //unboxing ^_^
				}
			}
		};
	}
	
	public static ObjectContainer<Float> toCachingObjectContainer(final FloatContainer primitiveContainer)
	{
		return new ObjectContainer<Float>
		()
		{
			protected transient Float wrapperValueCache;
			
			@Override
			public Float get()
			{
				Float wrappedValue = this.wrapperValueCache;
				
				if (wrappedValue == null)
				{
					wrappedValue = primitiveContainer.get(); //boxing ^_^
					this.wrapperValueCache = wrappedValue;
					return wrappedValue;
				}
				else
				{
					return wrappedValue;
				}
			}
			
			@Override
			public void set(Float value)
			{
				if (value != this.wrapperValueCache)
				{
					this.wrapperValueCache = value;
					primitiveContainer.set(value); //unboxing ^_^
				}
			}
		};
	}
	
	public static ObjectContainer<Integer> toCachingObjectContainer(final IntegerContainer primitiveContainer)
	{
		return new ObjectContainer<Integer>
		()
		{
			protected transient Integer wrapperValueCache;
			
			@Override
			public Integer get()
			{
				Integer wrappedValue = this.wrapperValueCache;
				
				if (wrappedValue == null)
				{
					wrappedValue = primitiveContainer.get(); //boxing ^_^
					this.wrapperValueCache = wrappedValue;
					return wrappedValue;
				}
				else
				{
					return wrappedValue;
				}
			}
			
			@Override
			public void set(Integer value)
			{
				if (value != this.wrapperValueCache)
				{
					this.wrapperValueCache = value;
					primitiveContainer.set(value); //unboxing ^_^
				}
			}
		};
	}
	
	public static ObjectContainer<Double> toCachingObjectContainer(final DoubleContainer primitiveContainer)
	{
		return new ObjectContainer<Double>
		()
		{
			protected transient Double wrapperValueCache;
			
			@Override
			public Double get()
			{
				Double wrappedValue = this.wrapperValueCache;
				
				if (wrappedValue == null)
				{
					wrappedValue = primitiveContainer.get(); //boxing ^_^
					this.wrapperValueCache = wrappedValue;
					return wrappedValue;
				}
				else
				{
					return wrappedValue;
				}
			}
			
			@Override
			public void set(Double value)
			{
				if (value != this.wrapperValueCache)
				{
					this.wrapperValueCache = value;
					primitiveContainer.set(value); //unboxing ^_^
				}
			}
		};
	}
	
	public static ObjectContainer<Long> toCachingObjectContainer(final LongContainer primitiveContainer)
	{
		return new ObjectContainer<Long>
		()
		{
			protected transient Long wrapperValueCache;
			
			@Override
			public Long get()
			{
				Long wrappedValue = this.wrapperValueCache;
				
				if (wrappedValue == null)
				{
					wrappedValue = primitiveContainer.get(); //boxing ^_^
					this.wrapperValueCache = wrappedValue;
					return wrappedValue;
				}
				else
				{
					return wrappedValue;
				}
			}
			
			@Override
			public void set(Long value)
			{
				if (value != this.wrapperValueCache)
				{
					this.wrapperValueCache = value;
					primitiveContainer.set(value); //unboxing ^_^
				}
			}
		};
	}
}
